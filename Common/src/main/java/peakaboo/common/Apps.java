package peakaboo.common;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;



public class Apps
{

	public static void browser(String _url)
	{
		//Check for http instead of http:// so that we don't add it to https:// urls
		String url = _url.toLowerCase().startsWith("http") ? _url : "http://" + _url;
		
		try {
			Desktop desktop = Desktop.getDesktop();
			desktop.browse(new URI(_url));
		} catch (UnsupportedOperationException | IOException | URISyntaxException e1) {
			openDocument(url);
		}

		
	}
	

	private static void openDocument(final String location)
	{
		switch (Env.getOS()) 
		{
			case WINDOWS:
				try
				{
					//proper way of launching a webpage viewer
					Runtime.getRuntime().exec("start " + location);
				}
				catch (IOException e){}
				break;
				
			case MAC:
				
				try
				{
					//proper way of launching a webpage viewer
					Runtime.getRuntime().exec("open " + location);
				}
				catch (IOException e){}
				break;
			
			case UNIX:
			case OTHER:
			default:
				
				try
				{
					//proper way of launching a webpage viewer
					Runtime.getRuntime().exec("xdg-open " + location);
				}
				catch (IOException e){}
				break;

			
		}

	}
	
}

