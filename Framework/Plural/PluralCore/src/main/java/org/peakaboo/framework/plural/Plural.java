package org.peakaboo.framework.plural;

import java.util.logging.Logger;

public class Plural {

	private Plural() {}
	
	public static Logger logger() {
		StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
		return Logger.getLogger( stElements[0].getClassName() );
	}
		
	/**
	 * Convenience method for {@link Runtime#availableProcessors()}
	 * @return
	 */
	public static int cores()
	{
		return Runtime.getRuntime().availableProcessors();
	}
	
}
