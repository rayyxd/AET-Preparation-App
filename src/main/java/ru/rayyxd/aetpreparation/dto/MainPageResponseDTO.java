package ru.rayyxd.aetpreparation.dto;

public class MainPageResponseDTO {
	int id;
	
	String title;
	
	String description;
	
	double progress;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}	
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public double getProgress() {
		return progress;
	}
	
	public void setProgress(double progress) {
		this.progress = progress;
	}
		
	public MainPageResponseDTO(int id, String title, String description, double progress) {
		this.id=id;
		this.title=title;
		this.description=description;
		this.progress=progress;
				
	}
	
	
	
	
}
