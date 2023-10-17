package ru.sergdm.ws.exception;

public class ResourceAlreadyExistsException extends Exception {
	
	public ResourceAlreadyExistsException() {
	}

	public ResourceAlreadyExistsException(String msg) {
		super(msg);
	}
	
}
