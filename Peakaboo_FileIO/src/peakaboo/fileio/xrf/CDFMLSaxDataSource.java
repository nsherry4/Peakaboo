package peakaboo.fileio.xrf;



import java.io.IOException;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DefaultHandler2;
import org.xml.sax.helpers.XMLReaderFactory;

import peakaboo.fileio.AbstractFile;



public class CDFMLSaxDataSource extends DefaultHandler2
{

	XMLReader	xr;


	public CDFMLSaxDataSource(AbstractFile file)
	{
		super();

		try
		{

			xr = XMLReaderFactory.createXMLReader();
			xr.setContentHandler(this);
			xr.setErrorHandler(this);

			xr.parse(new InputSource(file.getInputStream()));

		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SAXException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public void startDocument()
	{
		System.out.println("Start document");
	}


	public void endDocument()
	{
		System.out.println("End document");
	}


	public void startElement(String uri, String name, String qName, Attributes atts)
	{
		if ("".equals(uri)) System.out.println("Start element: " + qName);
		else System.out.println("Start element: {" + uri + "}" + name);

		for (int i = 0; i < atts.getLength(); i++)
		{
			System.out.println("\t" + atts.getValue(i));
		}

	}


	public void characters(char ch[], int start, int length)
	{

		System.out.print("Characters:    \"");
		for (int i = start; i < start + length; i++)
		{
			switch (ch[i])
			{
				case '\\':
					System.out.print("\\\\");
					break;
				case '"':
					System.out.print("\\\"");
					break;
				case '\n':
					System.out.print("\\n");
					break;
				case '\r':
					System.out.print("\\r");
					break;
				case '\t':
					System.out.print("\\t");
					break;
				default:
					System.out.print(ch[i]);
					break;
			}
		}
		System.out.print("\"\n");
	}


	public void endElement(String uri, String name, String qName)
	{

		if ("".equals(uri)) System.out.println("End element: " + qName);
		else System.out.println("End element:   {" + uri + "}" + name);

	}


	public static void main(String args[]) throws Exception
	{

		new CDFMLSaxDataSource(new AbstractFile("/home/nathaniel/Desktop/June.035.xml"));

	}

}
