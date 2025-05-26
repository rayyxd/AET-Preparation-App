package ru.rayyxd.aetpreparation.sqlEntities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class FinalTest {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "testId")
	private Long id;
	
	
	@OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "moduleId")
    private Module module;
	
	@Column
	private Double passignScore;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Module getModule() {
		return module;
	}
	
	public void setModule(Module module) {
		this.module = module;
	}
	
	public Double getPassignScore() {
		return passignScore;
	}
	
	public void setPassignScore(Double passignScore) {
		this.passignScore = passignScore;
	}
	
	FinalTest(Module module, Double passingScore){
		this.module=module;
		this.passignScore=passingScore;
	}
	
	
}
