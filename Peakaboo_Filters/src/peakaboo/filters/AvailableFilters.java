package peakaboo.filters;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.List;

import peakaboo.common.Version;
import peakaboo.datatypes.DataTypeFactory;
import peakaboo.filters.filters.AgressiveWaveletNoiseFilter;
import peakaboo.filters.filters.BruknerRemoval;
import peakaboo.filters.filters.FourierLowPass;
import peakaboo.filters.filters.MovingAverage;
import peakaboo.filters.filters.PolynomialRemoval;
import peakaboo.filters.filters.SavitskyGolaySmoothing;
import peakaboo.filters.filters.WaveletNoiseFilter;


public class AvailableFilters
{
	
	public static List<AbstractFilter> generateFilterList()
	{
		if (Version.inJar)
		{
			try {
				return generateFilterListStatic();
			} catch (InstantiationException e) {
				e.printStackTrace();
				return DataTypeFactory.<AbstractFilter>list();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return DataTypeFactory.<AbstractFilter>list();
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

		List<AbstractFilter> list = DataTypeFactory.<AbstractFilter> list();

		Class<?> c;
		Class<AbstractFilter> afc;
		AbstractFilter f;

		c = FourierLowPass.class;
		afc = (Class<AbstractFilter>) c;
		f = afc.newInstance();
		list.add(f);

		c = MovingAverage.class;
		afc = (Class<AbstractFilter>) c;
		f = afc.newInstance();
		list.add(f);

		c = PolynomialRemoval.class;
		afc = (Class<AbstractFilter>) c;
		f = afc.newInstance();
		list.add(f);
		
		c = BruknerRemoval.class;
		afc = (Class<AbstractFilter>) c;
		f = afc.newInstance();
		list.add(f);

		c = SavitskyGolaySmoothing.class;
		afc = (Class<AbstractFilter>) c;
		f = afc.newInstance();
		list.add(f);

		c = AgressiveWaveletNoiseFilter.class;
		afc = (Class<AbstractFilter>) c;
		f = afc.newInstance();
		list.add(f);

		c = WaveletNoiseFilter.class;
		afc = (Class<AbstractFilter>) c;
		f = afc.newInstance();
		list.add(f);
		
		return list;


	}


	// messy logic for getting a list of all available filters
	public static List<AbstractFilter> generateFilterListDynamic()
	{

		List<AbstractFilter> list = DataTypeFactory.<AbstractFilter> list();

		Package p = AbstractFilter.class.getPackage();
		List<Class<?>> classes = null;
		
		try {
			classes = getClasses(p.getName() + ".filters");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		AbstractFilter f;
		
		if (classes != null) {
			for (int i = 0; i < classes.size(); i++) {
				if (classes.get(i).getSuperclass() == AbstractFilter.class) {

					// this will warn of an unchecked cast, but since we're checking
					// for ourselves on the line above, this can be ignored.
					try {
						f = (AbstractFilter)classes.get(i).newInstance();
						if (f.showFilter()) list.add(f);
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
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
		try {
			ClassLoader cld = Thread.currentThread().getContextClassLoader();
			if (cld == null) {
				throw new ClassNotFoundException("Can't get class loader.");
			}
			String path = pckgname.replace('.', '/');
			URL resource = cld.getResource(path);
			if (resource == null) {
				throw new ClassNotFoundException("No resource for " + path);
			}
			URI resourceURI = resource.toURI();
			String location = resourceURI.getPath();
			System.err.println(location);
			directory = new File(location);
			System.err.println(directory.getAbsoluteFile());
		} catch (Exception x) {
			throw new ClassNotFoundException(pckgname + " (" + directory + ") does not appear to be a valid package");
		}
		
		
		
		if (directory.exists()) {
			// Get the list of the files contained in the package
			String[] files = directory.list();
			for (int i = 0; i < files.length; i++) {
				// we are only interested in .class files
				if (files[i].endsWith(".class")) {
					// removes the .class extension
					String classname = files[i].substring(0, files[i].length() - 6);
					classes.add(Class.forName(pckgname + '.' + classname));
				}
			}
		} else {
			throw new ClassNotFoundException(pckgname + " does not appear to be a valid package");
		}

		return classes;
	}
}
