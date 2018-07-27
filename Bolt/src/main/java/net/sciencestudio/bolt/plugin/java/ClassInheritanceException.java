package net.sciencestudio.bolt.plugin.java;

import net.sciencestudio.bolt.plugin.core.exceptions.BoltException;

public class ClassInheritanceException extends BoltException {

	public ClassInheritanceException(String message) {
		super(message);
	}
	
	public ClassInheritanceException(String message, Throwable cause) {
		super(message, cause);
	}

}
