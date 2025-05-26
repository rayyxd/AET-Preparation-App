package ru.rayyxd.aetpreparation.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import ru.rayyxd.aetpreparation.dto.StudentLoginRequestDTO;
import ru.rayyxd.aetpreparation.dto.AuthResponseDTO;
import ru.rayyxd.aetpreparation.dto.StudentRegisterRequestDTO;
import ru.rayyxd.aetpreparation.exceptions.FieldValidationException;
import ru.rayyxd.aetpreparation.exceptions.ForbiddenEmailException;
import ru.rayyxd.aetpreparation.security.JwtCore;
import ru.rayyxd.aetpreparation.services.StudentService;
import ru.rayyxd.aetpreparation.sqlEntities.Student;
import ru.rayyxd.aetpreparation.sqlRepositories.StudentRepository;

@RestController
public class SecurityController {
	
	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private JwtCore jwtCore;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private StudentService studentService;
		
	@PostMapping("/login")
	public ResponseEntity<?> signin(@RequestBody StudentLoginRequestDTO studentLoginRequestDTO){
		Authentication authentication = null;
	
		// Custom Exception handling 
//		try {
		authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(studentLoginRequestDTO.getEmail(), studentLoginRequestDTO.getPassword()));
//		} catch (BadCredentialsException e) {}

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtCore.generateToken(authentication);
		Long expiration = jwtCore.getExpFromJwt(jwt).getTime();
		
		AuthResponseDTO response = new AuthResponseDTO();
		response.setToken(jwt);
		response.setExpiration(expiration);
		
		
		return new ResponseEntity<>(response, HttpStatus.OK);	
	}
	
	
	@PostMapping("/register")
	public ResponseEntity<?> signup(@Valid @RequestBody StudentRegisterRequestDTO studentRegisterRequestDTO, BindingResult bindingResult){
		if(bindingResult.hasErrors()) { 	
			throw new FieldValidationException(bindingResult);
		}
		
		if(studentRepository.existsByEmail(studentRegisterRequestDTO.getEmail())) {
			throw new ForbiddenEmailException("This email already exists");
		}
		
		Student student = new Student();
		
		String hashed = passwordEncoder.encode(studentRegisterRequestDTO.getPassword());
		
		student.setName(studentRegisterRequestDTO.getName());
		student.setEmail(studentRegisterRequestDTO.getEmail());
		student.setPassword(hashed);
		
		studentService.registerNewStudent(student);
		
		Authentication authentication = null;
		
		// Custom Exception handling 
		//try {
		authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(student.getEmail(), studentRegisterRequestDTO.getPassword()));
		//} catch (BadCredentialsException e) {}
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtCore.generateToken(authentication);
		Long expiration = jwtCore.getExpFromJwt(jwt).getTime();
		
		AuthResponseDTO response = new AuthResponseDTO();
		response.setToken(jwt);
		response.setExpiration(expiration);
		
		return new ResponseEntity<>(response, HttpStatus.OK);
		
	}
	
}
