package org.peakaboo.dataset.source.model;

public class DataSourceReadException extends Exception {
	
	public DataSourceReadException(String message) {
		super(message);
	}
	
	public DataSourceReadException(String message, Throwable cause) {
		super(message, cause);
	}
}