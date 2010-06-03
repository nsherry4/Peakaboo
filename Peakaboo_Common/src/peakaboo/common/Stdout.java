package peakaboo.common;


public class Stdout
{

	public static void write(Object message)
	{
		System.out.println(message);
	}
	
	public static void debug(Object message)
	{
		if (! Version.release) System.out.println(message);
	}

}
