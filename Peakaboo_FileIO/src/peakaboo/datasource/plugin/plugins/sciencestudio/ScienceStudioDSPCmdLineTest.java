package peakaboo.datasource.plugin.plugins.sciencestudio;

import java.util.Arrays;
import java.util.List;

import peakaboo.datasource.DataSource;
import peakaboo.datasource.plugin.plugins.ScienceStudioDSP;
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
		
		DataSource dataSource = new ScienceStudioDSP();
		
		List<String> filenames = Arrays.asList(args);
		
		if(dataSource.canRead(filenames)) {
			System.out.println("Reading: " + filenames);
			dataSource.read(filenames);
			System.out.println("DONE!");
		}		
		else {
			System.out.println("Cannot Read: " + filenames);
		}
		
		System.out.println("DatasetName: " +  dataSource.datasetName());
		System.out.println("Creator: " + dataSource.getCreator());
		System.out.println("CreationTime: " + dataSource.getCreationTime());
		System.out.println("StartTime: " + dataSource.getStartTime());
		System.out.println("EndTime: " + dataSource.getEndTime());
		
		System.out.println("Scan Count: " + dataSource.scanCount());
		System.out.println("Data Dimensions: " + dataSource.getDataDimensions());
		System.out.println("Real Dimensions: " + dataSource.getRealDimensions());
	
		for(int idx=0; (idx<dataSource.scanCount()) && (idx<100); idx++) {
			System.out.print(dataSource.scanNames().get(idx) + ": ");
			System.out.print(dataSource.getDataCoordinatesAtIndex(idx) + ": ");
			System.out.print(dataSource.getRealCoordinatesAtIndex(idx) + ": ");
			System.out.print(spectrumToString(dataSource.get(idx), 20));
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
