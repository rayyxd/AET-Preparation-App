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
import ru.rayyxd.aetpreparation.dto.VerificationRequestDTO;
import ru.rayyxd.aetpreparation.dto.VerificationCodeRequestDTO;
import ru.rayyxd.aetpreparation.dto.ResetPasswordRequestDTO;
import ru.rayyxd.aetpreparation.exceptions.FieldValidationException;
import ru.rayyxd.aetpreparation.exceptions.ForbiddenEmailException;
import ru.rayyxd.aetpreparation.security.JwtCore;
import ru.rayyxd.aetpreparation.services.StudentService;
import ru.rayyxd.aetpreparation.sqlEntities.Student;
import ru.rayyxd.aetpreparation.sqlRepositories.StudentRepository;
import java.util.Collections;
import java.util.Optional;

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
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(studentLoginRequestDTO.getEmail(), studentLoginRequestDTO.getPassword()));
		Student student = studentRepository.findByEmail(studentLoginRequestDTO.getEmail()).orElseThrow();
		if (!student.isVerified()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
				.body(Collections.singletonMap("message", "Account not verified. Please check your email."));
		}
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
		Optional<Student> existingOpt = studentRepository.findByEmail(studentRegisterRequestDTO.getEmail());
		if(existingOpt.isPresent()) {
			Student existing = existingOpt.get();
			if(existing.isVerified()) {
				throw new ForbiddenEmailException("This email already exists and is verified");
			} else {
				// Update unverified user and send new code
				studentService.updateUnverifiedStudent(existing, studentRegisterRequestDTO);
				return ResponseEntity.ok(Collections.singletonMap("message", "Account updated. Please check your email for the verification code."));
			}
		}
		Student student = new Student();
		String hashed = passwordEncoder.encode(studentRegisterRequestDTO.getPassword());
		student.setName(studentRegisterRequestDTO.getName());
		student.setEmail(studentRegisterRequestDTO.getEmail());
		student.setPassword(hashed);
		studentService.registerNewStudent(student);
		return ResponseEntity.ok(Collections.singletonMap("message", "Registration successful. Please check your email for the verification code."));
	}
	
	@PostMapping("/request-verification-code")
	public ResponseEntity<?> requestVerificationCode(@RequestBody VerificationRequestDTO request) {
		Optional<Student> studentOpt = studentRepository.findByEmail(request.getEmail());
		if (studentOpt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(Collections.singletonMap("message", "Account with this email does not exist"));
		}
		studentService.sendVerificationCode(request.getEmail(), "reset");
		return ResponseEntity.ok(Collections.singletonMap("message", "Verification code sent to email"));
	}

	@PostMapping("/verify-code")
	public ResponseEntity<?> verifyCode(@RequestBody VerificationCodeRequestDTO request) {
		boolean result = studentService.verifyCode(request.getEmail(), request.getCode());
		if (result) {
			return ResponseEntity.ok(Collections.singletonMap("message", "Verification successful"));
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(Collections.singletonMap("message", "Invalid or expired code"));
		}
	}
	
	@PostMapping("/reset-password")
	public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequestDTO request) {
		boolean result = studentService.resetPassword(request.getEmail(), request.getCode(), request.getNewPassword());
		if (result) {
			return ResponseEntity.ok(Collections.singletonMap("message", "Password reset successful"));
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(Collections.singletonMap("message", "Invalid or expired code"));
		}
	}
	
}
