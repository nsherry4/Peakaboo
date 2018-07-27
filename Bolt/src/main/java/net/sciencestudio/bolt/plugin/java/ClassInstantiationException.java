package net.sciencestudio.bolt.plugin.java;

import net.sciencestudio.bolt.plugin.core.exceptions.BoltException;

public class ClassInstantiationException extends BoltException {

	public ClassInstantiationException(String message) {
		super(message);
	}
	
	public ClassInstantiationException(String message, Throwable cause) {
		super(message, cause);
	}

	
}
