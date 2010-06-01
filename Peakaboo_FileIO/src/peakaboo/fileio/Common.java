package peakaboo.fileio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import peakaboo.calculations.functional.Function2;
import peakaboo.calculations.functional.Functional;

/**
 * 
 *  This class provides convenience methods for dealing with fileIO
 *
 * @author Nathaniel Sherry, 2009
 */

public class Common {

	/**
	 * The types of files that Peakaboo is capable of reading	
	 * @author Nathaniel Sherry, 2009
	 *
	 */
	public enum FileType {CLSXML, ZIP, CDFML}
	
	/**
	 * Given a file name, it will return a String representation of the contents of the file
	 * @param filename
	 * @return the contents of filename
	 */
	public static String fileToString(String filename)
	{

		try {
	        BufferedReader in = new BufferedReader(new FileReader(filename));
	        
	        return readerToString(in);
	        
	    } catch (IOException e) {
	    	e.printStackTrace();
	    	return null;
	    }

	    
	}
	
	public static <T1> void listToFile(String filename, List<T1> list)
	{
		StringBuilder stringbuilder = new StringBuilder();
		
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(filename));
			
			out.write(
					Functional.foldr(list, stringbuilder, new Function2<T1, StringBuilder, StringBuilder>() {
						@Override
						public StringBuilder run(T1 varT1, StringBuilder builder) { return builder.append(varT1.toString() + "\n"); }
					}).toString()
			);
			
			out.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Given a BufferedReader, it will return a String representation of the contents of the Reader
	 * @param reader
	 * @return the contents of the BufferedReader
	 */
	public static String readerToString(BufferedReader reader){
		
		StringBuffer file = new StringBuffer();
		String line;
		
		try {
		
			while (true){
				
				line = reader.readLine();
				if (line == null) break;
				file.append(line + System.getProperty("line.separator"));
				
			}
		
			reader.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		
		return file.toString();
		
	}
	
	/**
	 * Sorts a list of filenames in alphanumeric order; "A2" will come before "A10" 
	 * @param filenames
	 */
	public static void sortFilenames(List<String> filenames)
	{
		
		Collections.sort(filenames, new AlphaNumericComparitor());
		
	}
	
	/**
	 * A standard way or retrieving the file path.
	 * @param filename
	 * @return the path of filename
	 */
	public static String getFilePath(String filename){
		
		File f = new File(filename);
		return f.getAbsolutePath();
		
	}
	
	/**
	 * Examines the list of filenames, and determines the largest substring (starting at 0) which all filenames share.
	 * @param filenames
	 * @return the common portion of the given filenames
	 */
	public static String getCommonFileName(List<String> filenames)
	{
		
		String name = getFileNameLessExt(new File(filenames.get(0)).getName());
		String fileStart;
				
		for (String filename : filenames)
		{
			while (true)
			{
				
				fileStart = getFileNameLessExt(new File(filename).getName()).substring(0, name.length());
				if (name.equals(fileStart)) break;
				name = name.substring(0, name.length() - 1);
				
			}
			
		}
		
		return name;
	}

	/**
	 * Returns the file name with the file extension removed
	 * @param name
	 * @return file name without extension
	 */
	private static String getFileNameLessExt(String name)
	{
		String[] parts = name.split("\\.", 2);
		return parts[0];
	}
	
	/**
	 * Checks to see if a file's extension is the same as the given one
	 * @param filename
	 * @param extension
	 * @return true if the extensions match, false otherwise
	 */
	public static boolean checkFileExtension(String filename, String extension){
		
		return filename.toLowerCase().endsWith(extension.toLowerCase());
		
	}
	
}


