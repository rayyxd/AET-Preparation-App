package ru.rayyxd.aetpreparation.sqlEntities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "final_tests")
public class FinalTest {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private int testId;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "student_id")
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	private Student user;
	
	@Column(nullable = false)
	private double score;
	
	@Column(nullable = false)
	private LocalDateTime createdAt;
	
	public FinalTest() {}
	
	public FinalTest(int testId, Student user, double score, LocalDateTime createdAt) {
		this.testId = testId;
		this.user = user;
		this.score = score;
		this.createdAt = createdAt;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public int getTestId() {
		return testId;
	}
	
	public void setTestId(int testId) {
		this.testId = testId;
	}
	
	public Student getUser() {
		return user;
	}
	
	public void setUser(Student user) {
		this.user = user;
	}
	
	public double getScore() {
		return score;
	}
	
	public void setScore(double score) {
		this.score = score;
	}
	
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
