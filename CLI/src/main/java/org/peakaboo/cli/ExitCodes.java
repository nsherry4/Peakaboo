package org.peakaboo.cli;

/**
 * Process exit codes for the Peakaboo CLI. 0-2 are picocli's own conventions,
 * the rest name the failure so a script can tell a bad session from bad data.
 */
public final class ExitCodes {

	private ExitCodes() {}

	public static final int OK = 0;
	public static final int ERROR = 1;
	public static final int USAGE = 2;
	public static final int BAD_SESSION = 3;
	public static final int BAD_DATA = 4;
	public static final int EXPORT_FAILED = 5;

}
