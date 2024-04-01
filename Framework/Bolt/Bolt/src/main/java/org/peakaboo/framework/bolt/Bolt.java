package org.peakaboo.framework.bolt;

import java.util.logging.Logger;

public class Bolt {

	private Bolt() {
		
	}
	
	public static Logger logger() {
		StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
		Logger logger = Logger.getLogger( stElements[0].getClassName() );
		return logger;
	}
	
}
