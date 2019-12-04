package org.peakaboo.framework.autodialog;

import java.util.logging.Logger;

public class AutoDialog {

	public static Logger logger() {
		StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
		return Logger.getLogger( stElements[0].getClassName() );
	}
	
}
