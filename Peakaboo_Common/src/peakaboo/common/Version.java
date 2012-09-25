package peakaboo.common;

public class Version {

	public final static boolean release = false;
	public final static boolean rc = false;
	public final static boolean beta = false;
	
	public final static String devReleaseDescription = "Development Version";
	public final static String rcReleaseDescription = "Release Candidate";
	public final static String releaseDescription = release ? "" : rc ? rcReleaseDescription : devReleaseDescription;
	
	public final static String titleReleaseDescription = (release ? "" : " [") + releaseDescription + (release ? "" : "]");
	
	
	public final static int versionNo = 5;
	public final static int rcNo = 0;
	public final static int betaNo = 0;
	
	public final static String buildDate = "2012-06-06";
	public final static String longVersionNo =  
			release ? versionNo + ".0.0" : 
			rc 		? versionNo + ".0.0 RC" + rcNo : 
			(versionNo - 1) + "." + (beta ? "99." + betaNo : "98");
	public final static String logo = (release) ? "logo" : rc ? "rclogo" : "devlogo";
	public final static String icon = (release) ? "icon" : rc ? "rcicon" : "devicon";
	public final static String program_name = "Peakaboo";
	public final static String title = program_name + " " + (release ? versionNo : longVersionNo) + titleReleaseDescription;
	
	
}
