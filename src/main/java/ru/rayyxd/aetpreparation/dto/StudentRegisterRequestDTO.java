package ru.rayyxd.aetpreparation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class StudentRegisterRequestDTO {
	
	@NotBlank(message = "Your name must not be empty")
	private String name;
	
	@Email(message = "Your email must have valid format")
	@NotBlank(message = "Your email must not be empty")
	private String email;
	
	@Size(min =8, message="Password must be at least 8 characters long")
	@NotBlank(message = "Your password must not be empty")
	private String password;
	
	
	public String getEmail() {
		return email;
	}
	
	public String getName() {
		return name;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
}
