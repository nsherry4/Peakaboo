package org.peakaboo.framework.druthers.serialize;

import java.io.IOException;

public class DruthersLoadException extends IOException {
	public DruthersLoadException(Throwable cause) {
		super(cause);
	}
	public DruthersLoadException(String msg) {
		super(msg);
	}
}
