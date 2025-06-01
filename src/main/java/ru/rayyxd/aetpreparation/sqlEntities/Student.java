package ru.rayyxd.aetpreparation.sqlEntities;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Students")
public class Student{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "studentId")
	private Long id;
	
	@Column
	private String name;
	
	@Column
	private String email;
	
	@Column
	private String password;
	
	@Column(name = "verification_code")
	private String verificationCode;
	
	@Column(name = "verification_code_expires_at")
	private java.time.LocalDateTime verificationCodeExpiresAt;
	
	public Long getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getVerificationCode() {
		return verificationCode;
	}
	
	public java.time.LocalDateTime getVerificationCodeExpiresAt() {
		return verificationCodeExpiresAt;
	}
	
	public void setId(Long id) {
		this.id=id;
	}
	public void setName(String name) {
		this.name=name;
	}
	
	public void setEmail(String email) {
		this.email=email;
	}
	
	public void setPassword(String password) {
		this.password=password;
	}
	
	public void setVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
	}
	
	public void setVerificationCodeExpiresAt(java.time.LocalDateTime verificationCodeExpiresAt) {
		this.verificationCodeExpiresAt = verificationCodeExpiresAt;
	}
	
	public String toString() {
		return "user: " + getId() + ", " + getName() + ", " + getEmail() +", " + getPassword() + ";";
	}
}
