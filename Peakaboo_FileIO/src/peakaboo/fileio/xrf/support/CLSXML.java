package peakaboo.fileio.xrf.support;

import java.util.List;

import peakaboo.datatypes.DataTypeFactory;
import scitypes.Spectrum;
import swidget.dialogues.fileio.AbstractFile;
import swidget.dialogues.fileio.IOCommon;

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
	public static Spectrum readScanFromFile(AbstractFile file)
	{
		
		if (IOCommon.checkFileExtension(file.getFileName(), ".xml"))
		{	
			//get the whole file
			String contents = IOCommon.readerToString(file.getReader());
			return readScanFromString(contents);
			
		}
		return null;
		
	}
	
	/**
	 * Reads a scan from the given string
	 * @param scan
	 * @return a single scan
	 */
	public static Spectrum readScanFromString(String scan){
		
		try {
		
			String[] numbers = scan.split("<IOC1607-004.mca1>")[1].split("</IOC1607-004.mca1>")[0].split(" ");
			
			Spectrum data = new Spectrum(numbers.length);
			for (int i = 0; i < numbers.length; i++){
				data.set(i, Float.parseFloat(numbers[i]));
			}
			return data;
		} catch (ArrayIndexOutOfBoundsException e){
			return null;
		}
		
		
	}
	
	
	
	public static void filterNonXMLFilesFromFileList(List<AbstractFile> files){
		
		List<AbstractFile> badFiles = DataTypeFactory.<AbstractFile>list();
		
		for (AbstractFile file : files){
			
			if (!  IOCommon.checkFileExtension(file.getFileName(), ".xml")  ) badFiles.add(file);
			
		}
		
		files.removeAll(badFiles);
		
	}

	
	/**
	 * Reads the maximum energy from a given String representation of a file
	 * @param contents the String representation of a file
	 * @return the maximum energy as specified in the given file
	 */
	public static float readMaxEnergy(String contents){ 
	
		String energyHeader = "<IOC1607-004_dxp1.EMAX_RBV>";
		String energyFooter = "</IOC1607-004_dxp1.EMAX_RBV>";
		float energy = 20.48f;
		
		try {
			int start = contents.indexOf(energyHeader) + energyHeader.length();
			int stop = contents.indexOf(energyFooter);
			if (start < 0 || stop < 0) return energy;
				
			return Float.parseFloat(contents.substring(start, stop));
		} catch (Exception e) {
			return energy;
		}
		
	}
	
}
