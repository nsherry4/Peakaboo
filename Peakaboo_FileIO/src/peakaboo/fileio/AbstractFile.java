package peakaboo.fileio;



import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.jnlp.FileContents;



public class AbstractFile
{

	public enum ReadType
	{
		STRING, FILE_CONTENTS
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

		return "";
	}

}
