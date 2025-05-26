package ru.rayyxd.aetpreparation.responses;

public class AppBaseResponse {
	private Boolean success;
	private String message;
	
	public String getMessage() {
		return message;
	}
	
	public Boolean getSuccess() {
		return success;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public void setStatusCode(Boolean success) {
		this.success = success;
	}
	
	public AppBaseResponse() {}

	public AppBaseResponse(Boolean success, String message) {
		this.success=success;
		this.message=message;
	}
}
