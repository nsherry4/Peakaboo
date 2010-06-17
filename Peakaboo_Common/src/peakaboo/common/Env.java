package peakaboo.common;



import javax.jnlp.ServiceManager;



public class Env
{

	public static boolean inJar()
	{
		Env env = new Env();
		String className = env.getClass().getName().replace('.', '/');
		String classJar = env.getClass().getResource("/" + className + ".class").toString();
		return classJar.startsWith("jar:");
	}


	public static boolean isWindows()
	{

		String os = System.getProperty("os.name").toLowerCase();
		// windows
		return (os.indexOf("win") >= 0);

	}


	public static boolean isMac()
	{

		String os = System.getProperty("os.name").toLowerCase();
		// Mac
		return (os.indexOf("mac") >= 0);

	}


	public static boolean isUnix()
	{

		String os = System.getProperty("os.name").toLowerCase();
		// linux or unix
		return (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0);

	}


	public static boolean isWebStart()
	{
		return (ServiceManager.getServiceNames() != null && ServiceManager.getServiceNames().length != 0);
	}

}
