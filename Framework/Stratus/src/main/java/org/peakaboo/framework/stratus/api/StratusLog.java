package org.peakaboo.framework.stratus.api;

import java.util.logging.Logger;

public class StratusLog {

	
	public static Logger get() {
		StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
		Logger logger = Logger.getLogger( stElements[0].getClassName() );
		return logger;
	}

	
}
