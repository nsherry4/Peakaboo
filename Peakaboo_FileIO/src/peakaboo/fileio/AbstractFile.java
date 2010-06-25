package peakaboo.fileio;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.jnlp.FileContents;



public class AbstractFile
{

	public enum ReadType
	{
		STRING, FILE_CONTENTS, URL
	}

	private ReadType	type;
	private Object		contents;


	public AbstractFile(String filename)
	{
		type = ReadType.STRING;
		contents = filename;
	}


	public AbstractFile(FileContents filecontents)
	{
		type = ReadType.FILE_CONTENTS;
		contents = filecontents;
	}

	public AbstractFile(URL url)
	{
		type = ReadType.URL;
		contents = url;
	}
	

	public BufferedReader getReader()
	{
		if (type == ReadType.STRING)
		{
			try
			{
				return new BufferedReader(new FileReader((String) contents));
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
				return null;
			}
		}
		else if (type == ReadType.FILE_CONTENTS)
		{
			try
			{
				return new BufferedReader(new InputStreamReader(((FileContents) contents).getInputStream()));
			}
			catch (IOException e)
			{
				return null;
			}
		} else if (type == ReadType.URL)
		{
			try
			{
				return new BufferedReader(new InputStreamReader(  ((URL)contents).openStream()  ));
			}
			catch (IOException e)
			{
				return null;
			}
		}

		return null;
	}


	public InputStream getInputStream() throws IOException
	{
				
		if (type == ReadType.STRING)
		{
			try
			{
				return new FileInputStream((String) contents);
			}
			catch (FileNotFoundException e)
			{
				throw new IOException();
			}
		}
		else if (type == ReadType.FILE_CONTENTS)
		{
			return ((FileContents) contents).getInputStream();
			
		} else if (type == ReadType.URL)
		{
			return ((URL)contents).openStream();
		}
		
		return null;
	}


	public String getFileName()
	{
		if (type == ReadType.STRING)
		{
			return (String) contents;
		}
		else if (type == ReadType.FILE_CONTENTS)
		{
			try
			{
				return ((FileContents) contents).getName();
			}
			catch (IOException e)
			{
				return "";
			}
		}
		else if (type == ReadType.URL)
		{
			return ((URL)contents).getPath();
		}

		return "";

		
	}
	
	public long getFileSize()
	{
		if (type == ReadType.STRING)
		{
			return new File( (String) contents ).length();
		}
		else if (type == ReadType.FILE_CONTENTS)
		{
			try
			{
				return ((FileContents) contents).getLength();
			}
			catch (IOException e)
			{
				return 0;
			}
		}

		return 0;
	}
	

}
