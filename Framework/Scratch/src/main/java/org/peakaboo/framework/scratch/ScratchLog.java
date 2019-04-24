package org.peakaboo.framework.scratch;

import java.util.logging.Logger;

public class ScratchLog {

	public static Logger get() {
		StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
		Logger logger = Logger.getLogger( stElements[0].getClassName() );
		return logger;
	}
	
}
