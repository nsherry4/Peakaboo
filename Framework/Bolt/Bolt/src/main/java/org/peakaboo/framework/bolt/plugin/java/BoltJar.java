package org.peakaboo.framework.bolt.plugin.java;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;


public class BoltJar {

	public static boolean isClassInJar(Class<?> classInJar)
	{
		String className = classInJar.getName().replace('.', '/');
		URL jar = BoltJar.class.getResource("/" + className + ".class");
		if (jar == null) { return false; }
		String classJar = jar.toString();
		return classJar.startsWith("jar:");
	}
	

	public static File getJarForClass(Class<?> classInJar)
	{
		if (!isClassInJar(classInJar)) return null;
		try {
			return new File(classInJar.getProtectionDomain().getCodeSource().getLocation().toURI());
		} catch (URISyntaxException e) {
			return null;
		}
	}
	
	public static boolean checkImplementsInterface(Class<?> c, Class<?> targetInterface)
	{
		if (c == null) return false;
		
		while (c != Object.class) {
		
			Class<?> ifaces[] = c.getInterfaces();
			for (Class<?> iface : ifaces)
			{
				if (iface.equals(targetInterface)) return true;
			}
						
			c = c.getSuperclass();
			
		}
		return false;
		
	}
	
}
