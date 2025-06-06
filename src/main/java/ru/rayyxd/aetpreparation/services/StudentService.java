package ru.rayyxd.aetpreparation.services;

import java.util.List;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import ru.rayyxd.aetpreparation.sqlEntities.FinalTest;
import ru.rayyxd.aetpreparation.sqlEntities.Module;
import ru.rayyxd.aetpreparation.sqlEntities.Student;
import ru.rayyxd.aetpreparation.sqlEntities.StudentDetailsImpl;
import ru.rayyxd.aetpreparation.sqlEntities.UserProgress;
import ru.rayyxd.aetpreparation.sqlRepositories.FinalTestRepository;
import ru.rayyxd.aetpreparation.sqlRepositories.ModulesRepository;
import ru.rayyxd.aetpreparation.sqlRepositories.StudentRepository;
import ru.rayyxd.aetpreparation.sqlRepositories.UserProgressRepository;
import ru.rayyxd.aetpreparation.dto.UserGradesResponseDTO;
import ru.rayyxd.aetpreparation.noSqlEntities.FinalTestNoSql;
import ru.rayyxd.aetpreparation.noSqlRepositories.FinalTestNoSqlRepository;
import ru.rayyxd.aetpreparation.security.JwtCore;

@Service
public class StudentService implements UserDetailsService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserProgressRepository progressRepository;

    @Autowired
    private ModulesRepository modulesRepository;

    @Autowired
    private FinalTestNoSqlRepository finalTestNoSqlRepository;
    
    @Autowired
    private FinalTestRepository finalTestRepository;
    
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtCore jwtCore;


    /**
     * При логине Spring Security вызывает именно этот метод, чтобы найти UserDetails по email.
     * Здесь оставляем прежнюю логику: ищем по email, потому что аутентификация на входе идёт "email+пароль".
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email not found"));
        return StudentDetailsImpl.build(student);
    }

    /**
     * Регистрация нового студента:
     * 1) Сохраняем студента (со статусом verified = false).
     * 2) Отправляем ему код подтверждения на почту.
     * 3) Инициализируем записи в user_progress (progress = 0.0 для каждого существующего модуля).
     */
    @Transactional
    public Student registerNewStudent(Student student) {
        student.setVerified(false);
        Student saved = studentRepository.save(student);
        sendVerificationCode(student.getEmail(), "register");

        List<Module> modules = modulesRepository.findAll();
        List<UserProgress> zeroProgress = modules.stream()
            .map(module -> new UserProgress(saved, module, 0.0))
            .toList();
        progressRepository.saveAll(zeroProgress);

        return saved;
    }

    /**
     * Генерация шестизначного кода для email-подтверждения.
     */
    public String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    /**
     * Отправка кода подтверждения на e-mail. reason может быть "register" или "reset".
     */
    public void sendVerificationCode(String email, String reason) {
        Optional<Student> studentOpt = studentRepository.findByEmail(email);
        if (studentOpt.isEmpty()) {
            return;
        }

        Student student = studentOpt.get();
        String code = generateVerificationCode();
        student.setVerificationCode(code);
        student.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(10));
        studentRepository.save(student);

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setTo(email);

            String htmlMsg;
            if ("register".equals(reason)) {
                helper.setSubject("Подтверждение регистрации на AET");
                htmlMsg = """
                    <div style='font-family:sans-serif;'>
                      <h2 style='color:#4280EF;'>AET</h2>
                      <p>Здравствуйте!</p>
                      <p>Вы регистрируетесь на платформе <b>AET</b>.</p>
                      <p style='font-size:18px;'>Ваш код для подтверждения: <b style='color:#4280EF;'>""" + code + "</b></p>" +
                      "<p>Пожалуйста, введите этот код в приложении для завершения регистрации.</p>" +
                      "<p style='color:gray;font-size:13px;'>Если вы не регистрировались, просто проигнорируйте это письмо.</p>" +
                      "<br><p>С уважением,<br>Команда AET</p></div>";
            } else {
                helper.setSubject("Восстановление пароля на AET");
                htmlMsg = """
                    <div style='font-family:sans-serif;'>
                      <h2 style='color:#4280EF;'>AET</h2>
                      <p>Здравствуйте!</p>
                      <p>Вы запросили восстановление пароля для своей учётной записи на платформе <b>AET</b>.</p>
                      <p style='font-size:18px;'>Ваш код для подтверждения: <b style='color:#4280EF;'>""" + code + "</b></p>" +
                      "<p>Пожалуйста, введите этот код в приложении. Код действителен в течение 10 минут.</p>" +
                      "<p style='color:gray;font-size:13px;'>Если вы не запрашивали восстановление пароля, просто проигнорируйте это письмо.</p>" +
                      "<br><p>С уважением,<br>Команда AET</p></div>";
            }

            helper.setText(htmlMsg, true);
            mailSender.send(mimeMessage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Проверка кода верификации (при регистрации или сбросе пароля).
     */
    public boolean verifyCode(String email, String code) {
        Optional<Student> studentOpt = studentRepository.findByEmailAndVerificationCode(email, code);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            if (student.getVerificationCodeExpiresAt() != null
                    && student.getVerificationCodeExpiresAt().isAfter(LocalDateTime.now())) {
                student.setVerified(true);
                studentRepository.save(student);
                return true;
            }
        }
        return false;
    }

    /**
     * Сброс пароля: проверяем код, если валидно → кодируем новый пароль и сохраняем.
     */
    public boolean resetPassword(String email, String code, String newPassword) {
        Optional<Student> studentOpt = studentRepository.findByEmailAndVerificationCode(email, code);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            if (student.getVerificationCodeExpiresAt() != null
                    && student.getVerificationCodeExpiresAt().isAfter(LocalDateTime.now())) {

                student.setPassword(passwordEncoder.encode(newPassword));
                student.setVerificationCode(null);
                student.setVerificationCodeExpiresAt(null);
                studentRepository.save(student);
                return true;
            }
        }
        return false;
    }

    /**
     * Обновление имени текущего пользователя. Пришёл JWT в заголовке, извлекаем userId,
     * ищем студента по ID и меняем имя.
     */
    public boolean updateName(String token, String newName) {
        Long userId = jwtCore.getUserIdFromJwt(token);
        Optional<Student> studentOpt = studentRepository.findById(userId);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            student.setName(newName);
            studentRepository.save(student);
            return true;
        }
        return false;
    }

    /**
     * Запрос на смену email:
     * 1) Из JWT достаём userId
     * 2) Проверяем пароль для безопасности
     * 3) Генерируем временный код, сохраняем его вместе с новым email в поле verificationCode как “EMAILCHANGE:<код>:<новыйEmail>”
     * 4) Отправляем этот код уже на новый email
     */
    public boolean requestEmailChange(String token, String newEmail, String password) {
        Long userId = jwtCore.getUserIdFromJwt(token);
        Optional<Student> studentOpt = studentRepository.findById(userId);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();

            // проверяем, что введённый пароль совпадает
            if (!passwordEncoder.matches(password, student.getPassword())) {
                return false;
            }
            // проверяем, что newEmail свободен
            if (studentRepository.findByEmail(newEmail).isPresent()) {
                return false;
            }

            String code = generateVerificationCode();
            student.setVerificationCode("EMAILCHANGE:" + code + ":" + newEmail);
            student.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(10));
            studentRepository.save(student);

            // отправляем код уже на новый email
            try {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
                helper.setTo(newEmail);
                helper.setSubject("Подтверждение смены email на AET");

                String htmlMsg = """
                    <div style='font-family:sans-serif;'>
                      <h2 style='color:#4280EF;'>AET</h2>
                      <p>Здравствуйте!</p>
                      <p>Вы запросили смену email для своей учётной записи на платформе <b>AET</b>.</p>
                      <p style='font-size:18px;'>Ваш код для подтверждения: <b style='color:#4280EF;'>""" + code + "</b></p>" +
                      "<p>Пожалуйста, введите этот код в приложении. Код действителен в течение 10 минут.</p>" +
                      "<p style='color:gray;font-size:13px;'>Если вы не запрашивали смену email, просто проигнорируйте это письмо.</p>" +
                      "<br><p>С уважением,<br>Команда AET</p></div>";

                helper.setText(htmlMsg, true);
                mailSender.send(mimeMessage);

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Подтверждение смены email:
     * 1) Из JWT достаём userId, потом получаем сам объект Student.
     * 2) В verificationCode должно храниться строка вида "EMAILCHANGE:<код>:<новыйEmail>"
     * 3) Если код совпадает и срок не истёк → обновляем в БД самое поле email на новое и сбрасываем verificationCode.
     */
    public boolean confirmEmailChange(String token, String code) {
        Long userId = jwtCore.getUserIdFromJwt(token);
        Optional<Student> studentOpt = studentRepository.findById(userId);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            String stored = student.getVerificationCode();
            if (stored != null && stored.startsWith("EMAILCHANGE:")) {
                String[] parts = stored.split(":");
                // parts[0] = "EMAILCHANGE", parts[1] = код, parts[2] = новый email
                if (parts.length == 3 && parts[1].equals(code)) {
                    String newEmail = parts[2];
                    if (student.getVerificationCodeExpiresAt() != null
                            && student.getVerificationCodeExpiresAt().isAfter(LocalDateTime.now())) {

                        // Прежде чем сохранить, убедимся, что новый email по-прежнему свободен
                        if (studentRepository.findByEmail(newEmail).isEmpty()) {
                            student.setEmail(newEmail);
                            student.setVerificationCode(null);
                            student.setVerificationCodeExpiresAt(null);
                            studentRepository.save(student);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public List<UserGradesResponseDTO> getUserGrades(String token) {
        // 1) Извлекаем userId из токена
        Long userId = jwtCore.getUserIdFromJwt(token);

        // 2) Находим Student по userId
        Student student = studentRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // 3) Берём все записи FinalTest для этого пользователя
        List<FinalTest> attempts = finalTestRepository.findAllByUser(student);

        // 4) Для каждой попытки ("attempt") собираем DTO
        return attempts.stream().map(attempt -> {
            int testId = attempt.getTestId();
            double scoreDouble = attempt.getScore();
            // Приведём к int (можете изменить по своему усмотрению, например округление)
            int grade = (int) Math.round(scoreDouble);

            LocalDateTime createdAtLdt = attempt.getCreatedAt();         

            // Ищем в Mongo определение самого теста
            Optional<FinalTestNoSql> maybeNoSql = finalTestNoSqlRepository.findByTestId(testId);
            String title = "Unknown Test";
            int maxGrade = 0;

            if (maybeNoSql.isPresent()) {
                FinalTestNoSql testDef = maybeNoSql.get();
                title = testDef.getTitle();
                // Считаем, что максимальный балл = число вопросов (size списка content)
                if (testDef.getContent() != null) {
                    maxGrade = testDef.getContent().size();
                }
            }

            return new UserGradesResponseDTO(title, grade, maxGrade, createdAtLdt);
        }).collect(Collectors.toList());
    }
}
