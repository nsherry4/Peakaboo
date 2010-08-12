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

import peakaboo.common.DataTypeFactory;
import peakaboo.filter.AbstractFilter.FilterType;
import peakaboo.filter.filters.advanced.DataToWavelet;
import peakaboo.filter.filters.advanced.Derivitive;
import peakaboo.filter.filters.advanced.Integrate;
import peakaboo.filter.filters.advanced.SegmentFilter;
import peakaboo.filter.filters.advanced.SpectrumNormalization;
import peakaboo.filter.filters.advanced.WaveletToData;
import peakaboo.filter.filters.arithmetic.Addition;
import peakaboo.filter.filters.arithmetic.Multiply;
import peakaboo.filter.filters.arithmetic.Subtraction;
import peakaboo.filter.filters.background.BruknerRemoval;
import peakaboo.filter.filters.background.LinearTrimRemoval;
import peakaboo.filter.filters.background.PolynomialRemoval;
import peakaboo.filter.filters.noise.AgressiveWaveletNoiseFilter;
import peakaboo.filter.filters.noise.FourierLowPass;
import peakaboo.filter.filters.noise.MovingAverage;
import peakaboo.filter.filters.noise.SavitskyGolaySmoothing;
import peakaboo.filter.filters.noise.SpringSmoothing;
import peakaboo.filter.filters.noise.WaveletNoiseFilter;



public class AvailableFilters
{

	public static List<Class<? extends AbstractFilter>>	availableFilters;


	public static List<Class<? extends AbstractFilter>> generateFilterList()
	{
		if (availableFilters == null)
		{

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

		new FunctionMap<Class<? extends AbstractFilter>, AbstractFilter>() {

			public AbstractFilter f(Class<? extends AbstractFilter> f)
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

						new FunctionMap<Class<? extends AbstractFilter>, Boolean>() {

							public Boolean f(Class<? extends AbstractFilter> f)
							{
								return createNewInstance(f).getFilterType() == type;
							}
						}
				),

		new FunctionMap<Class<? extends AbstractFilter>, AbstractFilter>() {

			public AbstractFilter f(Class<? extends AbstractFilter> f)
					{
						return AvailableFilters.createNewInstance(f);
					}
		}
			);
	}


	public static AbstractFilter createNewInstance(AbstractFilter f)
	{
		return createNewInstance(f.getClass());
	}


	public static AbstractFilter createNewInstance(Class<? extends AbstractFilter> f)
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
	private static List<Class<? extends AbstractFilter>> generateFilterListStatic()
	{

		List<Class<? extends AbstractFilter>> l = DataTypeFactory.<Class<? extends AbstractFilter>> list();
		
		//noise
		l.add(FourierLowPass.class);
		l.add(MovingAverage.class);
		l.add(SavitskyGolaySmoothing.class);
		l.add(AgressiveWaveletNoiseFilter.class);
		l.add(WaveletNoiseFilter.class);
		l.add(SpringSmoothing.class);

		//background
		l.add(PolynomialRemoval.class);
		l.add(BruknerRemoval.class);
		l.add(LinearTrimRemoval.class);

		//advanced
		l.add(Integrate.class);
		l.add(Derivitive.class);
		l.add(DataToWavelet.class);
		l.add(WaveletToData.class);
		l.add(SegmentFilter.class);
		l.add(SpectrumNormalization.class);
		
		//arithmetic
		l.add(Addition.class);
		l.add(Subtraction.class);
		l.add(Multiply.class);
		

		return filter(

		l,

		new FunctionMap<Class<? extends AbstractFilter>, Boolean>() {


			public Boolean f(Class<? extends AbstractFilter> c)
				{
					if (createNewInstance(c) != null && createNewInstance(c).showFilter()) return true;
					return false;
				}
		});

	}


	// messy logic for getting a list of all available filters
	private static List<Class<? extends AbstractFilter>> generateFilterListDynamic()
	{

		List<Class<? extends AbstractFilter>> list = DataTypeFactory.<Class<? extends AbstractFilter>> list();

		Package p = AbstractFilter.class.getPackage();
		List<Class<?>> classes = new FList<Class<?>>();

		try
		{
			for (FilterType ft : FilterType.values())
			{
				classes.addAll(getClasses(p.getName() + ".filters." + ft.name().toLowerCase()));
			}
		}
		catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Class<? extends AbstractFilter> f;

		if (classes != null)
		{
			for (int i = 0; i < classes.size(); i++)
			{
				if (classes.get(i).getSuperclass() == AbstractFilter.class
						|| classes.get(i).getSuperclass().getSuperclass() == AbstractFilter.class)
				{

					f = (Class<? extends AbstractFilter>) classes.get(i);
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
