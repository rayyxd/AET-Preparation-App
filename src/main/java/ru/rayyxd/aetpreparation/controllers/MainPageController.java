package ru.rayyxd.aetpreparation.controllers;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import ru.rayyxd.aetpreparation.dto.MainPageResponseDTO;
import ru.rayyxd.aetpreparation.dto.SaveModuleProgressRequest;
import ru.rayyxd.aetpreparation.exceptions.NoAuthTokenException;
import ru.rayyxd.aetpreparation.noSqlEntities.ModuleNoSQL;
import ru.rayyxd.aetpreparation.noSqlRepositories.FinalTestNoSqlRepository;
import ru.rayyxd.aetpreparation.noSqlRepositories.ModulesNoSQLRepository;
import ru.rayyxd.aetpreparation.security.JwtCore;
import ru.rayyxd.aetpreparation.sqlEntities.FinalTest;
import ru.rayyxd.aetpreparation.sqlEntities.Module;
import ru.rayyxd.aetpreparation.sqlEntities.UserProgress;
import ru.rayyxd.aetpreparation.sqlRepositories.FinalTestRepository;
import ru.rayyxd.aetpreparation.sqlRepositories.ModulesRepository;
import ru.rayyxd.aetpreparation.sqlRepositories.UserProgressRepository;



@RestController
public class MainPageController {
	
	@Autowired
	private ModulesRepository modulesRepository;
	
	@Autowired
	private ModulesNoSQLRepository modulesNoSQLRepository;
	
	@Autowired
	private UserProgressRepository progressRepository;
	
	@Autowired
	private FinalTestRepository finalTestRepository;
	
	@Autowired 
	private FinalTestNoSqlRepository finalTestNoSqlRepository;
	
	@Autowired
	private UserProgressRepository userProgressRepository;
	
	@Autowired
	private JwtCore jwtCore;
	
	@GetMapping("/main")
	public ResponseEntity<?> getModules(@RequestHeader("Authorization") String authHeader){
		
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new NoAuthTokenException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        
        Long studentId = jwtCore.getUserIdFromJwt(token);
        
        List<MainPageResponseDTO> dtos = modulesRepository.findAll().stream()
            .map(module -> {
                double progress = progressRepository
                    .findByStudentIdAndModuleId(studentId, module.getId())
                    .map(UserProgress::getProgress)
                    .orElse(0.0);

                return new MainPageResponseDTO(
                    module.getId().intValue(),
                    module.getTitle(),
                    module.getDescription(),
                    progress
                );
            })
            .collect(Collectors.toList());

        return new ResponseEntity<>(dtos, HttpStatus.OK);
	}
	
	
	
	
	@GetMapping("/main/{id}")
	public ResponseEntity<?> getModuleById(@PathVariable int id) {
		
		ModuleNoSQL mnsql = modulesNoSQLRepository.findByModuleId(id).orElseThrow(NoSuchElementException::new);
		
		return new ResponseEntity<> (modulesNoSQLRepository.findByModuleId(id).orElseThrow(NoSuchElementException::new), HttpStatus.OK);
	}
	
	@PostMapping("/main/saveprogress")
	public ResponseEntity<?> saveUserProgress(@RequestHeader("Authorization") String authHeader, @RequestBody SaveModuleProgressRequest request){
		
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new NoAuthTokenException("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
		
		Long moduleId = Long.valueOf(request.getModuleId());
		Long studentId = jwtCore.getUserIdFromJwt(token);
		Double progress = request.getProgress();
		
		UserProgress currentUserProgress = userProgressRepository.findByStudentIdAndModuleId(studentId, moduleId).orElseThrow(NoSuchElementException::new);
		
		if (progress <= currentUserProgress.getProgress()) {
			return ResponseEntity.ok().body(java.util.Collections.singletonMap("message", "Progress is lower than before"));
		}else {
			currentUserProgress.setProgress(progress);
			userProgressRepository.save(currentUserProgress);
			return ResponseEntity.ok().body(java.util.Collections.singletonMap("message", "Progress updated successfully"));
		}
	}
	
	@GetMapping("/main/{id}/test")
	public ResponseEntity<?> getModuleTest(@PathVariable int id){
		
		// TODO: получать из JWT
        Long studentId = 1L;
    	
        double prog = progressRepository.findByStudentIdAndModuleId(studentId, Long.valueOf(id)).orElseThrow(NoSuchElementException::new).getProgress();  
		if(prog >=70.0) {

			int finalTestId = finalTestRepository.findById((long) id).orElseThrow(NoSuchElementException::new).getId().intValue();
			
			System.out.println("received test");
			return new ResponseEntity<>(finalTestNoSqlRepository.findByTestId(finalTestId),HttpStatus.OK);
		
		}
        return new ResponseEntity<>("Not enought points bozo",HttpStatus.DESTINATION_LOCKED );
	}
	
	
	
	
}
