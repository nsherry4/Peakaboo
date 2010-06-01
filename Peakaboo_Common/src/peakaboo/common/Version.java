package peakaboo.common;

public class Version {

	public final static boolean release = false;
	
	public final static boolean inJar = false;
	
	public final static int versionNo = 3;
	public final static String longVersionNo = ( (release) ? "3.0.0" : "2.99.1");
	public final static String logo = (release) ? "logo" : "logo-dev";
	public final static String title = "Peakaboo" + ((release) ? "" : " [Development Release]");
	
}
