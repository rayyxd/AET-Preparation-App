package ru.rayyxd.aetpreparation.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import ru.rayyxd.aetpreparation.sqlEntities.Module;
import ru.rayyxd.aetpreparation.sqlEntities.Student;
import ru.rayyxd.aetpreparation.sqlEntities.UserProgress;
import ru.rayyxd.aetpreparation.sqlRepositories.ModulesRepository;
import ru.rayyxd.aetpreparation.sqlRepositories.StudentRepository;
import ru.rayyxd.aetpreparation.sqlRepositories.UserProgressRepository;


@Service
public class ModuleService {

	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired
	private UserProgressRepository progressRepository;
	
	@Autowired
	private ModulesRepository modulesRepository;
	
	
	@Transactional
	public Module createModule(Module dto) {
	    Module m = new Module();
	    m.setTitle(dto.getTitle());
	    m.setDescription(dto.getDescription());
	   
	    Module saved = modulesRepository.save(m);

	    // для всех уже существующих студентов:
	    List<Student> allStudents = studentRepository.findAll();

	    // Если студентов нет — просто возвращаем saved и выходим
	    if (allStudents.isEmpty()) {
	        return saved;
	    }	
	    
	    List<UserProgress> progresses = allStudents.stream()
	        .map(student -> new UserProgress(student, saved, 11.1))
	        .toList();
	    progressRepository.saveAll(progresses);

	    return saved;
	}
}
