package ru.sergdm.ws.exception;

public class ResourceNotExpectedException extends Exception {
	public ResourceNotExpectedException() {
	}

	public ResourceNotExpectedException(String msg) {
		super(msg);
	}
}
