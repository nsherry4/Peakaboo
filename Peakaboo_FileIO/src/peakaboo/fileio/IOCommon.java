package peakaboo.fileio;



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.jnlp.FileContents;
import javax.jnlp.FileOpenService;
import javax.jnlp.FileSaveService;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;

import peakaboo.datatypes.functional.Function1;
import peakaboo.datatypes.functional.Function2;
import peakaboo.datatypes.functional.Functional;
import peakaboo.datatypes.functional.stock.Functions;



/**
 * This class provides convenience methods for dealing with fileIO
 * 
 * @author Nathaniel Sherry, 2009
 */

public class IOCommon
{

	/**
	 * The types of files that Peakaboo is capable of reading
	 * 
	 * @author Nathaniel Sherry, 2009
	 */
	public enum FileType
	{
		CLSXML, ZIP, CDFML
	}


	/**
	 * Given a file name, it will return a String representation of the contents of the file
	 * 
	 * @param filename
	 * @return the contents of filename
	 */
	public static String fileToString(String filename)
	{

		try
		{
			BufferedReader in = new BufferedReader(new FileReader(filename));

			String readString = readerToString(in);

			in.close();

			return readString;

		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}

	}


	public static <T1> void listToFile(String filename, List<T1> list)
	{
		StringBuilder stringbuilder = new StringBuilder();

		try
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(filename));

			out.write(Functional.foldr(list, stringbuilder, new Function2<T1, StringBuilder, StringBuilder>() {

				@Override
				public StringBuilder f(T1 varT1, StringBuilder builder)
				{
					return builder.append(varT1.toString() + "\n");
				}
			}).toString());

			out.close();

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}


	/**
	 * Given a BufferedReader, it will return a String representation of the contents of the Reader, and will close the BufferedReader
	 * 
	 * @param reader
	 * @return the contents of the BufferedReader
	 */
	public static String readerToString(BufferedReader reader)
	{

		if (reader == null) return "";
		StringBuffer file = new StringBuffer();
		String line;

		try
		{

			while (true)
			{

				line = reader.readLine();
				if (line == null) break;
				file.append(line + System.getProperty("line.separator"));

			}

		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		try
		{
			reader.close();
		}
		catch (IOException e)
		{

		}
		
		return file.toString();

	}


	/**
	 * Sorts a list of filenames in alphanumeric order; "A2" will come before "A10"
	 * 
	 * @param filenames
	 */
	public static void sortFiles(List<AbstractFile> filenames)
	{

		Functional.sortBy(filenames, new AlphaNumericComparitor(), new Function1<AbstractFile, String>() {

			@Override
			public String f(AbstractFile file)
			{
				return file.getFileName();
			}
		});

	}


	/**
	 * Sorts a list of filenames in alphanumeric order; "A2" will come before "A10"
	 * 
	 * @param filenames
	 */
	public static void sortFilenames(List<String> filenames)
	{

		Functional.sortBy(filenames, new AlphaNumericComparitor(), Functions.<String> id());

	}


	/**
	 * A standard way or retrieving the file path.
	 * 
	 * @param filename
	 * @return the path of filename
	 */
	public static String getFilePath(String filename)
	{
		
		List<String> parts = Functional.map(filename.split(separator()), Functions.<String> id());
		parts.remove(parts.size() - 1);
		return Functional.fold(parts, Functions.concat(separator())) + separator();

	}


	public static String separator()
	{
		String separator = File.separator;
		if ( "\\".equals(separator) ) separator = "\\\\";
		return separator;
	}
	
	/**
	 * A standard way or retrieving the file title.
	 * 
	 * @param filename
	 * @return the path of filename
	 */
	public static String getFileTitle(String filename)
	{
		
		String[] parts = filename.split(separator());
		return parts[parts.length - 1];

		// File f = new File(filename);
		// return f.getAbsolutePath();

	}


	/**
	 * A standard way or retrieving the file path.
	 * 
	 * @param filename
	 * @return the path of filename
	 */
	public static String getParentFolder(String filename)
	{

		
		
		String parts[] = filename.split(separator());
		if (parts.length == 1) return null;
		return parts[parts.length - 2];

		// File f = new File(filename);
		// return f.getAbsolutePath();

	}


	/**
	 * Examines the list of filenames, and determines the largest substring (starting at 0) which all filenames share.
	 * 
	 * @param filenames
	 * @return the common portion of the given filenames
	 */
	public static String getCommonFileName(List<String> filenames)
	{

		
		List<String> titles = Functional.map(filenames, new Function1<String, String>() {

			@Override
			public String f(String element)
			{
				return getFileTitle(element);
			}
		});
		
		

		String name = getFileNameLessExt(titles.get(0));
		String fileStart;

		for (String filename : titles)
		{
			while (true)
			{

				fileStart = getFileNameLessExt(filename).substring(0, name.length());
				if (name.equals(fileStart)) break;
				name = name.substring(0, name.length() - 1);

			}

		}

		return name;
	}


	/**
	 * Returns the file name with the file extension removed
	 * 
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
	 * 
	 * @param filename
	 * @param extension
	 * @return true if the extensions match, false otherwise
	 */
	public static boolean checkFileExtension(String filename, String extension)
	{

		return filename.toLowerCase().endsWith(extension.toLowerCase());

	}
	
	public static List<AbstractFile> wsOpenFiles(String path, String[] extensions) throws UnavailableServiceException
	{
		FileOpenService fos;
		try
		{
			fos = (FileOpenService) ServiceManager.lookup("javax.jnlp.FileOpenService");
			
			return Functional.map(fos.openMultiFileDialog(path, extensions), new Function1<FileContents, AbstractFile>(){

				@Override
				public AbstractFile f(FileContents element)
				{
					return new AbstractFile(element);
				}});
			
		}
		catch (IOException e)
		{
			return null;
		}
	
	}
	
	public static AbstractFile wsOpenFile(String path, String[] extensions) throws UnavailableServiceException
	{
		FileOpenService fos;
		try
		{
			fos = (FileOpenService) ServiceManager.lookup("javax.jnlp.FileOpenService");
			
			return new AbstractFile(  fos.openFileDialog(path, extensions)  );			
		}
		catch (IOException e)
		{
			return null;
		}
	
	}
	
	public static FileContents wsSaveFile(String path, String name, String[] extensions, InputStream inputStream) throws UnavailableServiceException
	{
		FileSaveService fos;

		try
		{
			
			fos = (FileSaveService) ServiceManager.lookup("javax.jnlp.FileSaveService");
			
			return fos.saveFileDialog(  path, extensions, inputStream, name  );
			
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;

	}
	
}
