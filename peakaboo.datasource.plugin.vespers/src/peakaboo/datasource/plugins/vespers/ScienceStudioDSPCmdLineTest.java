package peakaboo.datasource.plugins.vespers;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import peakaboo.datasource.DataSource;
import peakaboo.datasource.components.fileformat.FileFormatCompatibility;
import scitypes.Spectrum;

/**
 * @author maxweld
 *
 */
public class ScienceStudioDSPCmdLineTest {

	public static void main(String[] args) throws Exception {

		if(args.length == 0) {
			System.out.println("Usage: java -jar ScienceStudioDSP.jar dataFile.dat [ dataFile_spectra.dat ]");
			return;
		}
		
		DataSource dataSource = new ScienceStudio();
		
		List<String> filenames = Arrays.asList(args);
		List<File> files = filenames.stream().map(File::new).collect(Collectors.toList());
		
		if(dataSource.getFileFormat().compatibility(files) != FileFormatCompatibility.NO) {
			System.out.println("Reading: " + filenames);
			dataSource.read(files);
			System.out.println("DONE!");
		}		
		else {
			System.out.println("Cannot Read: " + filenames);
		}
		
		System.out.println("DatasetName: " +  dataSource.getScanData().datasetName());
		System.out.println("Creator: " + dataSource.getMetadata().getCreator());
		System.out.println("CreationTime: " + dataSource.getMetadata().getCreationTime());
		System.out.println("StartTime: " + dataSource.getMetadata().getStartTime());
		System.out.println("EndTime: " + dataSource.getMetadata().getEndTime());
		
		System.out.println("Scan Count: " + dataSource.getScanData().scanCount());
		System.out.println("Data Dimensions: " + dataSource.getDataSize().getDataDimensions());
		System.out.println("Real Dimensions: " + dataSource.getPhysicalSize().getPhysicalDimensions());
	
		for(int idx=0; (idx<dataSource.getScanData().scanCount()) && (idx<100); idx++) {
			System.out.print(dataSource.getScanData().scanName(idx) + ": ");
			System.out.print(dataSource.getDataSize().getDataCoordinatesAtIndex(idx) + ": ");
			System.out.print(dataSource.getPhysicalSize().getPhysicalCoordinatesAtIndex(idx) + ": ");
			System.out.print(spectrumToString(dataSource.getScanData().get(idx), 20));
			System.out.println();
		}
	}

	protected static String spectrumToString(Spectrum spectrum, int limit) {
		boolean first = true;
		StringBuffer buffer = new StringBuffer("[");
		for(int i=0; i<spectrum.size(); i++) {
			if((limit >= 0) && (limit <= i)) {
				buffer.append(",...");
				break;
			}
			if(first) {
				first = false;
				buffer.append(spectrum.get(i));
			} else {
				buffer.append(", ");
				buffer.append(spectrum.get(i));
			}
		}
		buffer.append("]");
		return buffer.toString();
	}
}
