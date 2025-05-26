package ru.rayyxd.aetpreparation.exceptions;

import org.springframework.validation.BindingResult;

public class FieldValidationException extends RuntimeException{
	private final BindingResult bindingResult;
	
	public FieldValidationException(BindingResult bindingResult) {
		this.bindingResult=bindingResult;
	}
	
	public BindingResult getBindingResult() {
		return bindingResult;
	}

}
