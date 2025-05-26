package ru.rayyxd.aetpreparation.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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
	
}
