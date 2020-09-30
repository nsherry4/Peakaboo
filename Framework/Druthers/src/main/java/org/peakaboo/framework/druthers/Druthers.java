package org.peakaboo.framework.druthers;

import java.util.logging.Logger;

public class Druthers {

	public static Logger logger() {
		StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
		return Logger.getLogger( stElements[0].getClassName() );
	}
}
