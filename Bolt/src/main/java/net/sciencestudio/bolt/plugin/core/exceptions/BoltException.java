package net.sciencestudio.bolt.plugin.core.exceptions;

public class BoltException extends Exception {

	public BoltException(String message) {
		super(message);
	}
	
	public BoltException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
