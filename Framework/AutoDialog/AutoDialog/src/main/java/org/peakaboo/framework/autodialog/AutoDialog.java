package org.peakaboo.framework.autodialog;

import java.util.logging.Logger;

public class AutoDialog {

	public static Logger logger() {
		StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
		Logger logger = Logger.getLogger( stElements[0].getClassName() );
		return logger;
	}
	
}
