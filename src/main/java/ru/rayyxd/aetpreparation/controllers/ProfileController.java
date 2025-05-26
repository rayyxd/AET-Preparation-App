package ru.rayyxd.aetpreparation.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import ru.rayyxd.aetpreparation.dto.ProfileDataResponseDTO;
import ru.rayyxd.aetpreparation.exceptions.NoAuthTokenException;
import ru.rayyxd.aetpreparation.security.JwtCore;


@RestController
public class ProfileController {
	
	@Autowired
	private JwtCore jwtCore;
	
	
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
}
