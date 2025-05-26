package ru.rayyxd.aetpreparation.sqlEntities;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class StudentDetailsImpl implements UserDetails{
	
	private Long id;
	private String name;
	private String email;
	private String password;
	
	public static StudentDetailsImpl build(Student student) {
		return new StudentDetailsImpl(
				student.getId(),
				student.getName(),
				student.getEmail(),
				student.getPassword()
				
				);
	}
	
	public StudentDetailsImpl(Long id, String name, String email, String password) {
		this.id=id;
		this.name=name;
		this.email=email;
		this.password=password;
	}
	
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getPassword() {
		// TODO Auto-generated method stub
		return password;
	}
	
	public String getUsername() {
		// TODO Auto-generated method stub
		return email;
	}
	
	public Long getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public StudentDetailsImpl() {
		// TODO Auto-generated constructor stub
	}

	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}
	
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}
	
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}
	
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

}
