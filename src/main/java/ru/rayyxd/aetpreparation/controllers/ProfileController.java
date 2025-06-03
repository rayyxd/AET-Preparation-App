package ru.rayyxd.aetpreparation.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ru.rayyxd.aetpreparation.dto.ProfileDataResponseDTO;
import ru.rayyxd.aetpreparation.exceptions.NoAuthTokenException;
import ru.rayyxd.aetpreparation.security.JwtCore;
import ru.rayyxd.aetpreparation.dto.UpdateProfileRequestDTO;
import ru.rayyxd.aetpreparation.services.StudentService;


@RestController
public class ProfileController {
	
	@Autowired
	private JwtCore jwtCore;
	
	@Autowired
	private StudentService studentService;
	
	
	@GetMapping("/user")
	public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String authHeader) {
        System.out.println("entered /user");
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new NoAuthTokenException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        
        ProfileDataResponseDTO dataResponse = new ProfileDataResponseDTO();
     
        dataResponse.setEmail(jwtCore.getEmailFromJwt(token));
        dataResponse.setName(jwtCore.getNameFromJwt(token));   
    
        return new ResponseEntity<>(dataResponse, HttpStatus.OK);
            
    }

    @PostMapping("/user/update-name")
    public ResponseEntity<?> updateName(@RequestHeader("Authorization") String authHeader, @RequestBody UpdateProfileRequestDTO request) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new NoAuthTokenException("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        boolean result = studentService.updateName(token, request.getName());
        if (result) {
            return ResponseEntity.ok().body(java.util.Collections.singletonMap("message", "Name updated successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(java.util.Collections.singletonMap("message", "Failed to update name"));
        }
    }

    @PostMapping("/user/request-email-change")
    public ResponseEntity<?> requestEmailChange(@RequestHeader("Authorization") String authHeader, @RequestBody UpdateProfileRequestDTO request) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new NoAuthTokenException("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        boolean result = studentService.requestEmailChange(token, request.getEmail(), request.getPassword());
        if (result) {
            return ResponseEntity.ok().body(java.util.Collections.singletonMap("message", "Verification code sent to new email"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(java.util.Collections.singletonMap("message", "Failed to request email change (wrong password or email in use)"));
        }
    }

    @PostMapping("/user/confirm-email-change")
    public ResponseEntity<?> confirmEmailChange(@RequestHeader("Authorization") String authHeader, @RequestBody UpdateProfileRequestDTO request) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new NoAuthTokenException("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        boolean result = studentService.confirmEmailChange(token, request.getEmail(), request.getVerificationCode());
        if (result) {
            return ResponseEntity.ok().body(java.util.Collections.singletonMap("message", "Email updated successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(java.util.Collections.singletonMap("message", "Invalid code or expired"));
        }
    }
}
