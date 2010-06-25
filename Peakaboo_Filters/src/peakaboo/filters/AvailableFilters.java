package peakaboo.filters;



import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.List;

import fava.*;
import static fava.Fn.*;
import static fava.Functions.*;

import peakaboo.common.Version;
import peakaboo.datatypes.DataTypeFactory;
import peakaboo.filters.filters.Addition;
import peakaboo.filters.filters.AgressiveWaveletNoiseFilter;
import peakaboo.filters.filters.BruknerRemoval;
import peakaboo.filters.filters.Derivitive;
import peakaboo.filters.filters.FourierLowPass;
import peakaboo.filters.filters.Integrate;
import peakaboo.filters.filters.MovingAverage;
import peakaboo.filters.filters.Multiply;
import peakaboo.filters.filters.PolynomialRemoval;
import peakaboo.filters.filters.SavitskyGolaySmoothing;
import peakaboo.filters.filters.Subtraction;
import peakaboo.filters.filters.WaveletNoiseFilter;



public class AvailableFilters
{

	public static List<AbstractFilter> generateFilterList()
	{
		if (Version.inJar)
		{
			try
			{
				return generateFilterListStatic();
			}
			catch (InstantiationException e)
			{
				e.printStackTrace();
				return DataTypeFactory.<AbstractFilter> list();
			}
			catch (IllegalAccessException e)
			{
				e.printStackTrace();
				return DataTypeFactory.<AbstractFilter> list();
			}
		}
		else
		{
			return generateFilterListDynamic();
		}
	}


	// for jar files -- how do we load these dynamically when inside a jar file?
	public static List<AbstractFilter> generateFilterListStatic() throws InstantiationException, IllegalAccessException
	{

		Class<?>[] classes = {
				FourierLowPass.class,
				MovingAverage.class,
				PolynomialRemoval.class,
				BruknerRemoval.class,
				SavitskyGolaySmoothing.class,
				AgressiveWaveletNoiseFilter.class,
				WaveletNoiseFilter.class,
				Integrate.class,
				Derivitive.class,
				Addition.class,
				Subtraction.class,
				Multiply.class };

		return filter(

		map(classes, new FunctionMap<Class<?>, AbstractFilter>() {

			@SuppressWarnings("unchecked")
			
			public AbstractFilter f(Class<?> element)
			{
				try
				{
					return ((Class<AbstractFilter>) element).newInstance();
				}
				catch (Exception e)
				{
					return null;
				}

			}
		}), new FunctionMap<AbstractFilter, Boolean>(){

			
			public Boolean f(AbstractFilter filter)
			{
				if (filter != null && filter.showFilter()) return true;
				return false;
			}
		});

	}


	// messy logic for getting a list of all available filters
	public static List<AbstractFilter> generateFilterListDynamic()
	{

		List<AbstractFilter> list = DataTypeFactory.<AbstractFilter> list();

		Package p = AbstractFilter.class.getPackage();
		List<Class<?>> classes = null;

		try
		{
			classes = getClasses(p.getName() + ".filters");
		}
		catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		AbstractFilter f;

		if (classes != null)
		{
			for (int i = 0; i < classes.size(); i++)
			{
				if (classes.get(i).getSuperclass() == AbstractFilter.class)
				{

					// this will warn of an unchecked cast, but since we're checking
					// for ourselves on the line above, this can be ignored.
					try
					{
						f = (AbstractFilter) classes.get(i).newInstance();
						if (f.showFilter()) list.add(f);
					}
					catch (InstantiationException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					catch (IllegalAccessException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		}

		return list;

	}


	// really really messy logic for getting all classes which extend AbstractFilter
	private static List<Class<?>> getClasses(String pckgname) throws ClassNotFoundException
	{

		List<Class<?>> classes = DataTypeFactory.<Class<?>> list();
		// Get a File object for the package
		File directory = null;
		try
		{
			ClassLoader cld = Thread.currentThread().getContextClassLoader();
			if (cld == null)
			{
				throw new ClassNotFoundException("Can't get class loader.");
			}
			String path = pckgname.replace('.', '/');
			URL resource = cld.getResource(path);
			if (resource == null)
			{
				throw new ClassNotFoundException("No resource for " + path);
			}
			URI resourceURI = resource.toURI();
			String location = resourceURI.getPath();
			directory = new File(location);
		}
		catch (Exception x)
		{
			throw new ClassNotFoundException(pckgname + " (" + directory + ") does not appear to be a valid package");
		}

		if (directory.exists())
		{
			// Get the list of the files contained in the package
			String[] files = directory.list();
			for (int i = 0; i < files.length; i++)
			{
				// we are only interested in .class files
				if (files[i].endsWith(".class"))
				{
					// removes the .class extension
					String classname = files[i].substring(0, files[i].length() - 6);
					classes.add(Class.forName(pckgname + '.' + classname));
				}
			}
		}
		else
		{
			throw new ClassNotFoundException(pckgname + " does not appear to be a valid package");
		}

		return classes;
	}
}
