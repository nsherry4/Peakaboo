package peakaboo.common;

public class Version {

	public final static boolean release = true;
	public final static boolean rc = true;
	public final static boolean beta = true;
	
	public final static String devReleaseDescription = "Development Version";
	public final static String rcReleaseDescription = "Release Candidate";
	public final static String releaseDescription = release ? "" : rc ? rcReleaseDescription : devReleaseDescription;
	
	public final static String titleReleaseDescription = (release ? "" : " [") + releaseDescription + (release ? "" : "]");
	
	
	public final static int versionNo = 4;
	public final static int rcNo = 3;
	public final static int betaNo = 1;
	
	public final static String buildDate = "2012-03-09";
	public final static String longVersionNo =  
			release ? "4.0.0" : 
			rc 		? "4.0.0 RC" + rcNo : 
			(versionNo - 1) + "." + (beta ? "99." + betaNo : "98");
	public final static String logo = (release) ? "logo" : rc ? "rclogo" : "devlogo";
	public final static String icon = (release) ? "icon" : rc ? "rcicon" : "devicon";
	public final static String program_name = "Peakaboo";
	public final static String title = program_name + " " + (release ? versionNo : longVersionNo) + titleReleaseDescription;
	
	
}
