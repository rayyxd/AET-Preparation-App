package ru.rayyxd.aetpreparation.dto;

public class AuthResponseDTO {
	private String token;
	
	private Long expiration; 
	
	public String getToken() {
		return token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}	
	
	public Long getExpiration() {
		return expiration;
	}
	
	public void setExpiration(Long expiration) {
		this.expiration = expiration;
	}
	
}
