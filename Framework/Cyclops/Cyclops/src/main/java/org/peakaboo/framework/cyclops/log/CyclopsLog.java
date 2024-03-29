package org.peakaboo.framework.cyclops.log;

import java.util.logging.Logger;

public class CyclopsLog {

	private CyclopsLog() {}
	
	public static Logger get() {
		StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
		Logger logger = Logger.getLogger( stElements[0].getClassName() );
		return logger;
	}
	
}
