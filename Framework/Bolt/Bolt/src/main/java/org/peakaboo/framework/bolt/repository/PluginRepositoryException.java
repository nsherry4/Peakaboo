package org.peakaboo.framework.bolt.repository;

public class PluginRepositoryException extends RuntimeException {
    public PluginRepositoryException(String message) {
        super(message);
    }
    
    public PluginRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
