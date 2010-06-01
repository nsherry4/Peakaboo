package peakaboo.fileio.xrf.support;

import java.util.List;

import peakaboo.datatypes.DataTypeFactory;
import peakaboo.fileio.Common;

/**
 * 
 *  This class provides logic for reading XRF data from the deprecated XML data format defined by the Canadian Light Source. 
 *
 * @author Nathaniel Sherry, 2009
 */

public class CLSXML {


	/**
	 * Reads the scan data from the given filename
	 * @param filename
	 * @return a single scan
	 */
	public static List<Double> readScanFromFile(String filename)
	{
		
		if (Common.checkFileExtension(filename, ".xml"))
		{	
			//get the whole file
			String contents = Common.fileToString(filename);
			return readScanFromString(contents);
			
		}
		return null;
		
	}
	
	/**
	 * Reads a scan from the given string
	 * @param scan
	 * @return a single scan
	 */
	public static List<Double> readScanFromString(String scan){
		
		try {
		
			String[] numbers = scan.split("<IOC1607-004.mca1>")[1].split("</IOC1607-004.mca1>")[0].split(" ");
			
			List<Double> data = DataTypeFactory.<Double>list();
			for (int i = 0; i < numbers.length; i++){
				data.add(Double.parseDouble(numbers[i]));
			}
			return data;
		} catch (ArrayIndexOutOfBoundsException e){
			return null;
		}
		
		
	}
	
	
	
	public static void filterNonXMLFilesFromFileList(List<String> filenames){
		
		List<String> badFiles = DataTypeFactory.<String>list();
		
		for (String filename : filenames){
			
			if (!  Common.checkFileExtension(filename, ".xml")  ) badFiles.add(filename);
			
		}
		
		filenames.removeAll(badFiles);
		
	}

	
	/**
	 * Reads the maximum energy from a given String representation of a file
	 * @param contents the String representation of a file
	 * @return the maximum energy as specified in the given file
	 */
	public static Double readMaxEnergy(String contents){ 
	
		String energyHeader = "<IOC1607-004_dxp1.EMAX_RBV>";
		String energyFooter = "</IOC1607-004_dxp1.EMAX_RBV>";
		double energy = 20.48;
		
		try {
			int start = contents.indexOf(energyHeader) + energyHeader.length();
			int stop = contents.indexOf(energyFooter);
			if (start < 0 || stop < 0) return energy;
				
			return Double.parseDouble(contents.substring(start, stop));
		} catch (Exception e) {
			return energy;
		}
		
	}
	
}
