package org.peakaboo.framework.eventful;

import java.util.logging.Logger;

public class Eventful {

	public static Logger logger() {
		StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
		return Logger.getLogger( stElements[0].getClassName() );
	}

}
