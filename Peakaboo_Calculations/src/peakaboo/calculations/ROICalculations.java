package peakaboo.calculations;


import java.util.List;

import peakaboo.datatypes.DataTypeFactory;
import peakaboo.datatypes.ranges.ROI;

/**
 * 
 * This class contains static methods for dealing with Regions of Interest and the data sets they apply to
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */

public class ROICalculations
{


	/**
	 * Sums the values in a region of interest for a single scan
	 * 
	 * @param list
	 *            list of values to sum
	 * @param region
	 *            a region of the data in which to sum the values
	 * @return the sum of all data points inside the given region
	 */
	public static double getSumInRegion(List<Double> list, ROI region)
	{
		double result = 0.0;
		for (int i = region.getStart(); i < region.getStop(); i++) {
			result += list.get(i);
		}
		return result;
	}


	/**
	 * Generates a map for a dataset and a given {@link ROI}
	 * 
	 * @param dataset
	 *            the dataset from which to generate the map
	 * @param region
	 *            a region of the data from which to generate the map
	 * @return a map based on the given region
	 */
	public static List<Double> getMapOverRegion(List<List<Double>> dataset, ROI region)
	{
		List<Double> map = DataTypeFactory.<Double> list();

		for (int i = 0; i < dataset.size(); i++) {
			map.add(getSumInRegion(dataset.get(i), region));
		}

		return map;
	}


	/**
	 * Generates a map for a dataset and a given list of {@link ROI}s
	 * 
	 * @param dataset
	 *            the dataset from which to generate the map
	 * @param regions 
	 *            a list of regions of the data from which to generate the map
	 * @return a map based on the given regions
	 */
	public static List<Double> getMapOverRegionSet(List<List<Double>> dataset, List<ROI> regions)
	{
		List<Double> map = DataTypeFactory.<Double> list();

		double sum;

		for (int i = 0; i < dataset.size(); i++) {

			sum = 0.0;
			for (ROI region : regions) {
				sum += getSumInRegion(dataset.get(i), region);
			}

			map.add(sum);

		}

		return map;
	}

}
