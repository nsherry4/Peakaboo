package peakaboo.common;

public class Version {

	public final static boolean release = false;
	public final static boolean rc = true;
	
	
	public final static int versionNo = 4;
	public final static int rcNo = 1;
	public final static String buildDate = "2012-03-01";
	public final static String longVersionNo = ( (release) ? "4.0.0" : rc ? "4.0.0 RC" + rcNo : "3.98.1");
	public final static String logo = (release) ? "logo" : rc ? "rclogo" : "devlogo";
	public final static String icon = (release) ? "icon" : rc ? "rcicon" : "devicon";
	public final static String program_name = "Peakaboo";
	public final static String title = program_name + " " + (release ? versionNo : longVersionNo) + ((release) ? "" : rc ? " [Release Candidate]" : " [Development Release]");
	
	
}
