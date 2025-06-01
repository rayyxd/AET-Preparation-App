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

import jakarta.transaction.Transactional;
import ru.rayyxd.aetpreparation.sqlEntities.Module;
import ru.rayyxd.aetpreparation.sqlEntities.Student;
import ru.rayyxd.aetpreparation.sqlEntities.StudentDetailsImpl;
import ru.rayyxd.aetpreparation.sqlEntities.UserProgress;
import ru.rayyxd.aetpreparation.sqlRepositories.ModulesRepository;
import ru.rayyxd.aetpreparation.sqlRepositories.StudentRepository;
import ru.rayyxd.aetpreparation.sqlRepositories.UserProgressRepository;

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
	
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
		Student student = studentRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Email not found"));
		return StudentDetailsImpl.build(student);
	}
	
	
	@Transactional
    public Student registerNewStudent(Student student) {
     // 1) Сохраняем студента
        Student saved = studentRepository.save(student);

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

    public void sendVerificationCode(String email) {
        Optional<Student> studentOpt = studentRepository.findByEmail(email);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            String code = generateVerificationCode();
            student.setVerificationCode(code);
            student.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(10));
            studentRepository.save(student);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Verification Code");
            message.setText("Your verification code is: " + code);
            mailSender.send(message);
        }
    }

    public boolean verifyCode(String email, String code) {
        Optional<Student> studentOpt = studentRepository.findByEmailAndVerificationCode(email, code);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            if (student.getVerificationCodeExpiresAt() != null && student.getVerificationCodeExpiresAt().isAfter(LocalDateTime.now())) {
                student.setVerificationCode(null);
                student.setVerificationCodeExpiresAt(null);
                studentRepository.save(student);
                return true;
            }
        }
        return false;
    }
	
}
