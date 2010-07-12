package peakaboo.common;

import commonenvironment.Env;

public class Version {

	public final static boolean release = false;
	
	public final static boolean inJar = Env.inJar();
	
	public final static int versionNo = 3;
	public final static String buildDate = "2010-06-23";
	public final static String longVersionNo = ( (release) ? "3.0.0" : "2.99.8b");
	public final static String logo = (release) ? "logo" : "devlogo";
	public final static String icon = (release) ? "icon" : "devicon";
	public final static String title = "Peakaboo" + ((release) ? "" : " [Development Release]");
	
}
