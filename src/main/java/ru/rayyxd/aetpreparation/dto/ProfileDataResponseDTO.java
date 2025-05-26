package ru.rayyxd.aetpreparation.dto;

public class ProfileDataResponseDTO {
	
	private String name;
	private String email;
	
	public String getEmail() {
		return email;
	}
	
	public String getName() {
		return name;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}

	public void setName(String name) {
		this.name = name;
	}
}
