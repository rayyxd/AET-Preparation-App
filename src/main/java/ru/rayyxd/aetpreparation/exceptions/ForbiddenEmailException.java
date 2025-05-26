package ru.rayyxd.aetpreparation.exceptions;

public class ForbiddenEmailException extends RuntimeException{
	public ForbiddenEmailException(String message) {
		super(message);
	}

}
