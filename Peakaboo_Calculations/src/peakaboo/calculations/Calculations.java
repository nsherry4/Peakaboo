package peakaboo.calculations;


import java.util.List;

import peakaboo.datatypes.DataTypeFactory;
import peakaboo.datatypes.GridPerspective;

/**
 * 
 * This class holds various algorithms which are do not fall into another category of calculation.
 * 
 * @author Nathaniel Sherry, 2009
 */

public class Calculations
{


	/**
	 * Given a bounded region, and a scan, this will determine where the centre of a peak inside this region resides 
	 * @param data the scan to examine
	 * @param start the starting channel for the search
	 * @param stop the stopping channel for the search
	 * @return the centrepoint of a peak as a channel index
	 */
	public static double getPeakCenter(List<Double> data, int start, int stop)
	{
		double weightedSum;
		double sum;

		weightedSum = 0.0;
		sum = 0.0;
		for (int i = start; i <= stop; i++) {
			weightedSum += i * data.get(i);
			sum += data.get(i);
		}

		return weightedSum / sum;
	}


	/**
	 * Reverses a data grid along the y axis.
	 * @param data the data to be reversed
	 * @param grid the {@link GridPerspective} defining the dimensions of the data
	 * @return a list of values reversed on the Y axis
	 */
	public static List<Double> gridYReverse(List<Double> data, GridPerspective<Double> grid)
	{

		List<Double> result = DataTypeFactory.<Double> listInit(grid.height * grid.width);
		int y_reverse;

		for (int x = 0; x < grid.width; x++) {
			for (int y = 0; y < grid.height; y++) {

				y_reverse = grid.height - y - 1;

				grid.set(result, x, y_reverse, grid.get(data, x, y));

			}
		}

		return result;

	}


}
