package peakaboo.common;

public class Version {

	public final static boolean release = false;
	public final static boolean rc = false;
	
	
	public final static int versionNo = 4;
	public final static String buildDate = "2011-04-11";
	public final static String longVersionNo = ( (release) ? "4.0.0" : rc ? "4.0.0 RC1" : "3.97.0");
	public final static String logo = (release) ? "logo" : rc ? "rclogo" : "devlogo";
	public final static String icon = (release) ? "icon" : rc ? "rcicon" : "devicon";
	public final static String program_name = "Peakaboo";
	public final static String title = program_name + " " + (release ? versionNo : longVersionNo) + ((release) ? "" : rc ? " [Release Candidate]" : " [Development Release]");
	
	
}
