package peakaboo.filter;



import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.List;

import commonenvironment.Env;

import fava.Fn;
import fava.lists.FList;
import fava.signatures.FunctionMap;
import static fava.Fn.*;

import peakaboo.datatypes.DataTypeFactory;
import peakaboo.filter.AbstractFilter.FilterType;
import peakaboo.filter.filters.advanced.Derivitive;
import peakaboo.filter.filters.advanced.Integrate;
import peakaboo.filter.filters.arithmetic.Addition;
import peakaboo.filter.filters.arithmetic.Multiply;
import peakaboo.filter.filters.arithmetic.Subtraction;
import peakaboo.filter.filters.background.BruknerRemoval;
import peakaboo.filter.filters.background.PolynomialRemoval;
import peakaboo.filter.filters.noise.AgressiveWaveletNoiseFilter;
import peakaboo.filter.filters.noise.FourierLowPass;
import peakaboo.filter.filters.noise.MovingAverage;
import peakaboo.filter.filters.noise.SavitskyGolaySmoothing;
import peakaboo.filter.filters.noise.WaveletNoiseFilter;



public class AvailableFilters
{

	public static List<Class<AbstractFilter>> availableFilters;
	
	public static List<Class<AbstractFilter>> generateFilterList()
	{
		if (availableFilters == null) {
		
			if (Env.inJar())
			{
				availableFilters = generateFilterListStatic();
			}
			else
			{
				availableFilters = generateFilterListDynamic();
			}
		}
		return availableFilters;
	}

	
	
	public static List<AbstractFilter> getNewInstancesForAllFilters()
	{
		return Fn.map(
				
				AvailableFilters.generateFilterList(),
				
				new FunctionMap<Class<AbstractFilter>, AbstractFilter>() {

					public AbstractFilter f(Class<AbstractFilter> f)
					{
						return AvailableFilters.createNewInstance(f);
					}
				}
			);
	}
	

	public static List<AbstractFilter> getNewInstancesForAllFilters(final FilterType type)
	{
		return Fn.map(
				
				Fn.filter(
						AvailableFilters.generateFilterList(),
						
						new FunctionMap<Class<AbstractFilter>, Boolean>() {

							public Boolean f(Class<AbstractFilter> f)
							{
								return createNewInstance(f).getFilterType() == type;
							}}
				),
				
				new FunctionMap<Class<AbstractFilter>, AbstractFilter>() {

					public AbstractFilter f(Class<AbstractFilter> f)
					{
						return AvailableFilters.createNewInstance(f);
					}
				}
			);
	}

	@SuppressWarnings("unchecked")
	public static AbstractFilter createNewInstance(AbstractFilter f)
	{
		return createNewInstance((Class<AbstractFilter>)f.getClass());
	}
	
	public static AbstractFilter createNewInstance(Class<AbstractFilter> f)
	{
		try
		{
			return f.newInstance();
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
			return null;
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	

	// for jar files -- how do we load these dynamically when inside a jar file?
	private static List<Class<AbstractFilter>> generateFilterListStatic()
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
				Multiply.class 
		};

		return filter(

		map(classes, new FunctionMap<Class<?>, Class<AbstractFilter>>() {

			@SuppressWarnings("unchecked")
			
			public Class<AbstractFilter> f(Class<?> element)
			{
				try
				{
					return (Class<AbstractFilter>) element;
				}
				catch (Exception e)
				{
					return null;
				}

			}
		}), new FunctionMap<Class<AbstractFilter>, Boolean>(){

			
			public Boolean f(Class<AbstractFilter> c)
			{
				if (createNewInstance(c) != null && createNewInstance(c).showFilter()) return true;
				return false;
			}
		});

	}


	// messy logic for getting a list of all available filters
	@SuppressWarnings("unchecked")
	private static List<Class<AbstractFilter>> generateFilterListDynamic()
	{

		List<Class<AbstractFilter>> list = DataTypeFactory.<Class<AbstractFilter>> list();

		Package p = AbstractFilter.class.getPackage();
		List<Class<?>> classes = new FList<Class<?>>();

		try
		{
			for (FilterType ft : FilterType.values())
			{
				classes.addAll( getClasses(p.getName() + ".filters." + ft.name().toLowerCase()) );
			}
		}
		catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Class<AbstractFilter> f;

		if (classes != null)
		{
			for (int i = 0; i < classes.size(); i++)
			{
				if (classes.get(i).getSuperclass() == AbstractFilter.class || classes.get(i).getSuperclass().getSuperclass() == AbstractFilter.class)
				{

					f = (Class<AbstractFilter>) classes.get(i);
					if (createNewInstance(f) != null && createNewInstance(f).showFilter()) list.add(f);

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
