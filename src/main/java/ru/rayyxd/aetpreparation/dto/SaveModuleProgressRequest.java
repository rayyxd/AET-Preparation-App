package ru.rayyxd.aetpreparation.dto;

public class SaveModuleProgressRequest {

	private int moduleId;
	
	private double progress;
	
	public void setModuleId(int moduleId) {
		this.moduleId = moduleId;
	}
	
	public int getModuleId() {
		return moduleId;
	}
	
	public void setProgress(double progress) {
		this.progress = progress;
	}
	
	public double getProgress() {
		return progress;
	}
	
	public SaveModuleProgressRequest() {}
	
}
