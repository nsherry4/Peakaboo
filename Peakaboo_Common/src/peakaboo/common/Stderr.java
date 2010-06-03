package peakaboo.common;


public class Stderr
{

	public static void write(Object message)
	{
		System.err.println(message);
	}
	
	public static void debug(Object message)
	{
		if (! Version.release) System.err.println(message);
	}

}
