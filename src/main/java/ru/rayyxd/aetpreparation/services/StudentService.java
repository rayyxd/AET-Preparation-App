package ru.rayyxd.aetpreparation.services;

import java.util.List;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;

import jakarta.transaction.Transactional;
import ru.rayyxd.aetpreparation.sqlEntities.Module;
import ru.rayyxd.aetpreparation.sqlEntities.Student;
import ru.rayyxd.aetpreparation.sqlEntities.StudentDetailsImpl;
import ru.rayyxd.aetpreparation.sqlEntities.UserProgress;
import ru.rayyxd.aetpreparation.sqlRepositories.ModulesRepository;
import ru.rayyxd.aetpreparation.sqlRepositories.StudentRepository;
import ru.rayyxd.aetpreparation.sqlRepositories.UserProgressRepository;
import ru.rayyxd.aetpreparation.security.JwtCore;

@Service
public class StudentService implements UserDetailsService{

	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired
	private UserProgressRepository progressRepository;
	
	@Autowired
	private ModulesRepository modulesRepository;
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private JwtCore jwtCore;
	
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
		Student student = studentRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Email not found"));
		return StudentDetailsImpl.build(student);
	}
	
	
	@Transactional
    public Student registerNewStudent(Student student) {
     // 1) Сохраняем студента
        student.setVerified(false);
        Student saved = studentRepository.save(student);
        sendVerificationCode(student.getEmail(), "register");

        // 2) Берём все модули
        List<Module> modules = modulesRepository.findAll();

        // 3) Для каждого создаём UserProgress с progress = 0.0
        List<UserProgress> zeroProgress = modules.stream()
            .map(module -> new UserProgress(saved, module, 22.2))
            .toList();

        // 4) Сохраняем их одним батчем
        progressRepository.saveAll(zeroProgress);
        return saved;
	}
	
	public String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    public void sendVerificationCode(String email, String reason) {
        Optional<Student> studentOpt = studentRepository.findByEmail(email);
        if (studentOpt.isPresent()) {
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
                    htmlMsg = "<div style='font-family:sans-serif;'>" +
                        "<h2 style='color:#4280EF;'>AET</h2>" +
                        "<p>Здравствуйте!</p>" +
                        "<p>Вы регистрируетесь на платформе <b>AET</b>.</p>" +
                        "<p style='font-size:18px;'>Ваш код для подтверждения: <b style='color:#4280EF;'>" + code + "</b></p>" +
                        "<p>Пожалуйста, введите этот код в приложении для завершения регистрации.</p>" +
                        "<p style='color:gray;font-size:13px;'>Если вы не регистрировались, просто проигнорируйте это письмо.</p>" +
                        "<br><p>С уважением,<br>Команда AET</p></div>";
                } else {
                    helper.setSubject("Восстановление пароля на AET");
                    htmlMsg = "<div style='font-family:sans-serif;'>" +
                        "<h2 style='color:#4280EF;'>AET</h2>" +
                        "<p>Здравствуйте!</p>" +
                        "<p>Вы запросили восстановление пароля для своей учётной записи на платформе <b>AET</b>.</p>" +
                        "<p style='font-size:18px;'>Ваш код для подтверждения: <b style='color:#4280EF;'>" + code + "</b></p>" +
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
    }

    public boolean verifyCode(String email, String code) {
        Optional<Student> studentOpt = studentRepository.findByEmailAndVerificationCode(email, code);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            if (student.getVerificationCodeExpiresAt() != null && student.getVerificationCodeExpiresAt().isAfter(LocalDateTime.now())) {
                student.setVerified(true);
                studentRepository.save(student);
                return true;
            }
        }
        return false;
    }

    public boolean resetPassword(String email, String code, String newPassword) {
        Optional<Student> studentOpt = studentRepository.findByEmailAndVerificationCode(email, code);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            if (student.getVerificationCodeExpiresAt() != null && student.getVerificationCodeExpiresAt().isAfter(LocalDateTime.now())) {
                student.setPassword(passwordEncoder.encode(newPassword));
                student.setVerificationCode(null);
                student.setVerificationCodeExpiresAt(null);
                studentRepository.save(student);
                return true;
            }
        }
        return false;
    }

    public boolean updateName(String token, String newName) {
        String email = jwtCore.getEmailFromJwt(token);
        Optional<Student> studentOpt = studentRepository.findByEmail(email);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            student.setName(newName);
            studentRepository.save(student);
            return true;
        }
        return false;
    }

    public boolean requestEmailChange(String token, String newEmail, String password) {
        String currentEmail = jwtCore.getEmailFromJwt(token);
        Optional<Student> studentOpt = studentRepository.findByEmail(currentEmail);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            if (!passwordEncoder.matches(password, student.getPassword())) {
                return false;
            }
            if (studentRepository.findByEmail(newEmail).isPresent()) {
                return false;
            }
            // Сохраняем код для нового email
            String code = generateVerificationCode();
            student.setVerificationCode(code);
            student.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(10));
            studentRepository.save(student);
            // Отправляем код на новый email
            try {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
                helper.setTo(newEmail);
                helper.setSubject("Подтверждение смены email на AET");
                String htmlMsg = "<div style='font-family:sans-serif;'>" +
                        "<h2 style='color:#4280EF;'>AET</h2>" +
                        "<p>Здравствуйте!</p>" +
                        "<p>Вы запросили смену email для своей учётной записи на платформе <b>AET</b>.</p>" +
                        "<p style='font-size:18px;'>Ваш код для подтверждения: <b style='color:#4280EF;'>" + code + "</b></p>" +
                        "<p>Пожалуйста, введите этот код в приложении. Код действителен в течение 10 минут.</p>" +
                        "<p style='color:gray;font-size:13px;'>Если вы не запрашивали смену email, просто проигнорируйте это письмо.</p>" +
                        "<br><p>С уважением,<br>Команда AET</p></div>";
                helper.setText(htmlMsg, true);
                mailSender.send(mimeMessage);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            // Временно сохраняем новый email в поле verificationCode (или можно добавить отдельное поле)
            student.setVerificationCode("EMAILCHANGE:" + code + ":" + newEmail);
            studentRepository.save(student);
            return true;
        }
        return false;
    }

    public boolean confirmEmailChange(String token, String newEmail, String code) {
        String currentEmail = jwtCore.getEmailFromJwt(token);
        Optional<Student> studentOpt = studentRepository.findByEmail(currentEmail);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            String stored = student.getVerificationCode();
            if (stored != null && stored.startsWith("EMAILCHANGE:")) {
                String[] parts = stored.split(":");
                if (parts.length == 3 && parts[1].equals(code) && parts[2].equals(newEmail)) {
                    if (student.getVerificationCodeExpiresAt() != null && student.getVerificationCodeExpiresAt().isAfter(LocalDateTime.now())) {
                        student.setEmail(newEmail);
                        student.setVerificationCode(null);
                        student.setVerificationCodeExpiresAt(null);
                        studentRepository.save(student);
                        return true;
                    }
                }
            }
        }
        return false;
    }
	
}
