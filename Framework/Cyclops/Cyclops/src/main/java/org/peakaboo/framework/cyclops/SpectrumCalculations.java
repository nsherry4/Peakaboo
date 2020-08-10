package org.peakaboo.framework.cyclops;



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * This class contains methods used to transform Spectrum (eg log), or to find a specific piece of information about the
 * Spectrum (eg max) Not everything that operates on or reads a Spectrum needs to go here.
 * 
 * @author Nathaniel Sherry, 2009
 */

public class SpectrumCalculations
{

	public static final int	MIN_SIZE_FOR_THREADING	= 512;


	public static Spectrum maxLists(ReadOnlySpectrum l1, ReadOnlySpectrum l2)
	{

		Spectrum result = new ISpectrum(l1.size());
		int maxInd = Math.min(l1.size(), l2.size());
		for (int i = 0; i < maxInd; i++)
		{
			result.set(i, Math.max(l1.get(i), l2.get(i)));
		}

		return result;
	}

	
	public static Spectrum maxLists_inplace(final Spectrum s1, final ReadOnlySpectrum s2)
	{
		int size = Math.min(s1.size(), s2.size());
		for (int i = 0; i < size; i++)
		{
			s1.set(i, Math.max(s1.get(i), s2.get(i)));
		}
		

		return s1;
	}
	
	/**
	 * returns the absolute version of the list
	 * 
	 * @param list
	 * @return max(list)
	 */
	public static Spectrum abs(ReadOnlySpectrum source)
	{
		Spectrum result = new ISpectrum(source.size());
		float newvalue;
		for (int i = 0; i < source.size(); i++)
		{
			newvalue = Math.abs(source.get(i));
			result.set(i, newvalue);
		}

		return result;
	}


	/**
	 * returns the maximum value in the list
	 * 
	 * @param list
	 * @param allowzero
	 *            should values of exactly 0 be considered in the search for a minimum value?
	 * @return min(list)
	 */

	public static float min(Spectrum list, final boolean allowzero)
	{

		float min = Float.MAX_VALUE;
		for (int i = 0; i < list.size(); i++) 
		{
			min = (list.get(i) == 0.0 && !allowzero) ? min : Math.min(min, list.get(i));
			min = Math.min(min, list.get(i));
		}
		return min;

	}


	/**
	 * Calculates the maximum value of a list of lists of values
	 * 
	 * @param dataset
	 * @return max(dataset)
	 */
	public static float maxDataset(List<Spectrum> dataset)
	{
		return dataset.stream().map(Spectrum::max).reduce(0f, Math::max);
	}


	/**
	 * returns a copy of the given list with all values in the list expressed as a fraction of the maximum value
	 * 
	 * @param data
	 * @return normalized data
	 */
	public static Spectrum normalize(Spectrum data)
	{
		float max = data.max();
		if (max != 0.0)
		{
			return divideBy(data, max);
		}
		else
		{
			return data;
		}

	}


	/**
	 * replaces the values in the given list with their equivalences as expressed as a fraction of the maximum value
	 * 
	 * @param data
	 */
	public static void normalize_inplace(Spectrum data)
	{
		float max = data.max();
		if (max != 0.0)
		{
			divideBy_inplace(data, max);
		}
	}


	/**
	 * returns a copy of the given list with all values in the list multiplied by the given value
	 * 
	 * @param data
	 * @param value
	 * @return a copy of data multiplied value
	 */
	public static Spectrum multiplyBy(ReadOnlySpectrum source, final float value)
	{

		Spectrum result = new ISpectrum(source.size());
		float newvalue;
		for (int i = 0; i < source.size(); i++)
		{
			newvalue = source.get(i) * value;
			result.set(i, newvalue);
		}

		return result;
	}
	
	

	/**
	 * returns a copy of the given list with all values in the list multiplied by the given value
	 * 
	 * @param data
	 * @param value
	 * @return the given spectrum, now with altered values
	 */
	public static Spectrum multiplyBy_inplace(final Spectrum source, final float value)
	{	
		
		float newvalue;
		for (int i = 0; i < source.size(); i++)
		{
			newvalue = source.get(i) * value;
			source.set(i, newvalue);
		}
		
		return source;
	}

	
	/**
	 * returns the target Spectrum containing the results of multiplying source by value
	 * 
	 * @param source
	 * @param target
	 * @param value
	 * @return the given target spectrum, now with altered values
	 */
	public static Spectrum multiplyBy_target(final ReadOnlySpectrum source, final Spectrum target, final float value)
	{	
		//optimization to get rid of get/set call overhead
		final float[] sourceArray = ((Spectrum)source).backingArray();
		final float[] targetArray = target.backingArray();
		
		final int size = source.size();
		for (int i = 0; i < size; i++)
		{
			targetArray[i] = sourceArray[i] * value;
		}
		
		return target;
	}




	/**
	 * Returns a copy of the given list with all values in the list expressed as a fraction of the given value
	 * 
	 * @param data
	 * @param value
	 * @return a copy of data divided by value
	 */
	public static Spectrum divideBy(ReadOnlySpectrum source, final float value)
	{

		Spectrum result = new ISpectrum(source.size());
		float newvalue;
		for (int i = 0; i < source.size(); i++)
		{
			newvalue = source.get(i) / value;
			result.set(i, newvalue);
		}

		return result;

	}


	/**
	 * Replaces the values in the given list with their equivalences as expressed as divided by value
	 * 
	 * @param data
	 * @param value
	 */
	public static void divideBy_inplace(Spectrum data, final float value)
	{

		float newvalue;
		for (int i = 0; i < data.size(); i++)
		{
			newvalue = data.get(i) / value;
			data.set(i, newvalue);
		}

	}


	/**
	 * Subtracts value from each element in data
	 * 
	 * @param data
	 * @param value
	 * @return a copy of data, with value subtracted from each element
	 */
	public static Spectrum subtractFromList(ReadOnlySpectrum data, float value)
	{
		Spectrum target = new ISpectrum(data.size());
		return subtractFromList(data, target, value, Float.NaN);
	}


	/**
	 * Subtracts value from each element in data
	 * 
	 * @param data
	 * @param value
	 * @return a copy of data, with value subtracted from each element
	 */
	public static Spectrum subtractFromList_target(Spectrum source, Spectrum target, float value)
	{
		return subtractFromList(source, target, value, Float.NaN);
	}
	

	
	/**
	 * Subtracts value from each element in data, while keeping all values no lower than minimum
	 * 
	 * @param data
	 * @param value
	 * @param minimum
	 * @return a copy of data, with value subtracted from each element
	 */
	public static Spectrum subtractFromList(ReadOnlySpectrum source, final float value, final float minimum)
	{

		Spectrum result = new ISpectrum(source.size());
		float newvalue;
		for (int i = 0; i < source.size(); i++)
		{
			newvalue = source.get(i) - value;
			if (!Float.isNaN(minimum) && value < minimum) newvalue = minimum;
			result.set(i, newvalue);
		}

		return result;
	}

	
	public static Spectrum subtractFromList_inplace(Spectrum source, final float value) {
		return subtractFromList_inplace(source, value, Float.NaN);
	}
	
	public static Spectrum subtractFromList_inplace(Spectrum source, final float value, final float minimum) {
		return subtractFromList(source, source, value, minimum);
	}
	
	public static Spectrum subtractFromList(ReadOnlySpectrum source, Spectrum target, final float value) {
		return subtractFromList(source, target, value, Float.NaN);
	}
	
	/**
	 * Subtracts value from each element in data, while keeping all values no lower than minimum
	 * 
	 * @param data
	 * @param value
	 * @param minimum
	 * @return a copy of data, with value subtracted from each element
	 */
	public static Spectrum subtractFromList(ReadOnlySpectrum source, Spectrum target, final float value, final float minimum)
	{

		float newvalue;
		for (int i = 0; i < source.size(); i++)
		{
			newvalue = source.get(i) - value;
			if (!Float.isNaN(minimum) && value < minimum) newvalue = minimum;
			target.set(i, newvalue);
		}

		return target;
	}

	
	public static Spectrum subtractListFrom_inplace(Spectrum source, final float value) {
		return subtractListFrom_inplace(source, value, Float.NaN);
	}
	
	public static Spectrum subtractListFrom_inplace(Spectrum source, final float value, final float minimum) {
		return subtractListFrom(source, source, value, minimum);
	}
	
	public static Spectrum subtractListFrom(ReadOnlySpectrum source, Spectrum target, final float value) {
		return subtractListFrom(source, target, value, Float.NaN);
	}
	
	/**
	 * Subtracts each element in data from value, while keeping all values no lower than minimum
	 * 
	 * @param data
	 * @param value
	 * @param minimum
	 * @return a copy of data, with value subtracted from each element
	 */
	public static Spectrum subtractListFrom(ReadOnlySpectrum source, Spectrum target, final float value, final float minimum)
	{

		float newvalue;
		for (int i = 0; i < source.size(); i++)
		{
			newvalue = value - source.get(i);
			if (!Float.isNaN(minimum) && value < minimum) newvalue = minimum;
			target.set(i, newvalue);
		}

		return target;
	}
	
	
	public static Spectrum addToList(ReadOnlySpectrum data, float value) {
		Spectrum copy = new ISpectrum(data.size());
		for (int i = 0; i < data.size(); i++) {
			copy.set(i, data.get(i) + value);
		}
		return copy;
	}
	
	public static void addToList_inplace(Spectrum data, float value) {
		for (int i = 0; i < data.size(); i++) {
			data.set(i, data.get(i) + value);
		}
	}
	

	/**
	 * adds the elements of the two lists together
	 * 
	 * @param l1
	 * @param l2
	 * @return a list which is the sum of the two lists given
	 */
	public static Spectrum addLists(ReadOnlySpectrum l1, ReadOnlySpectrum l2)
	{

		Spectrum result = new ISpectrum(l1.size());
		int maxInd = Math.min(l1.size(), l2.size());
		float value;
		for (int i = 0; i < maxInd; i++)
		{
			value = l1.get(i) + l2.get(i);
			result.set(i, value);
		}

		return result;
	}


	/**
	 * adds the elements of the two lists together, placing the results in l1
	 * 
	 * @param l1
	 * @param l2
	 */
	public static void addLists_inplace(final Spectrum l1, final ReadOnlySpectrum l2)
	{

		//optimization to get rid of get/set call overhead
		final float[] l1Array = l1.backingArray();
		final float[] l2Array = ((Spectrum)l2).backingArray();
		
		final int maxInd = Math.min(l1.size(), l2.size());
		
		for (int i = 0; i < maxInd; i++)
		{
			l1Array[i] = l1Array[i] + l2Array[i];
		}
		
	}


	/**
	 * Subtracts l2 from l1
	 * 
	 * @param l1
	 * @param l2
	 * @return a list which is the result of l1 - l2
	 */
	public static Spectrum subtractLists(ReadOnlySpectrum l1, ReadOnlySpectrum l2)
	{

		return subtractLists(l1, l2, Float.NaN);
	}


	/**
	 * Subtracts l2 from l1, while keeping all values no lower than minimum
	 * 
	 * @param l1
	 * @param l2
	 * @param minimum
	 * @return a list which is the result of l1 - l2
	 */
	public static Spectrum subtractLists(ReadOnlySpectrum l1, ReadOnlySpectrum l2, final float minimum)
	{

		Spectrum result = new ISpectrum(l1.size());
		int maxInd = Math.min(l1.size(), l2.size());
		float value;
		for (int i = 0; i < maxInd; i++)
		{
			value = l1.get(i) - l2.get(i);
			if (!Float.isNaN(minimum) && value < minimum) value = minimum;
			result.set(i, value);
		}

		return result;
	}


	/**
	 * Subtracts l2 from l1, placing the results in l1
	 * 
	 * @param l1
	 * @param l2
	 */
	public static void subtractLists_inplace(Spectrum l1, Spectrum l2)
	{
		float[] l1a = l1.backingArray();
		float[] l2a = ((Spectrum)l2).backingArray();
		
		int maxInd = Math.min(l1.size(), l2.size());
		for (int i = 0; i < maxInd; i++)
		{
			l1a[i] = l1a[i] - l2a[i];
		}
		
	}


	/**
	 * Subtracts l2 from l1, placing the results in l1, while keeping all values no lower than minimum
	 * 
	 * @param l1
	 * @param l2
	 * @param minimum
	 */
	public static void subtractLists_inplace(Spectrum l1, ReadOnlySpectrum l2, final float minimum)
	{

		float[] l1a = l1.backingArray();
		float[] l2a = ((Spectrum)l2).backingArray();
		
		int maxInd = Math.min(l1.size(), l2.size());
		float value;
		for (int i = 0; i < maxInd; i++)
		{
			value = l1a[i] - l2a[i];
			if (!Float.isNaN(minimum) && value < minimum) value = minimum;
			l1a[i] = value;
		}
		
	}

	
	public static void subtractLists_target(ReadOnlySpectrum l1, ReadOnlySpectrum l2, Spectrum target) {
		float[] l1a = ((Spectrum)l1).backingArray();
		float[] l2a = ((Spectrum)l2).backingArray();
		float[] ta = target.backingArray();
		
		int maxInd = Math.min(l1.size(), l2.size());
		for (int i = 0; i < maxInd; i++)
		{
			ta[i] = l1a[i] - l2a[i];
		}
	}
	
	public static void subtractLists_target(ReadOnlySpectrum l1, ReadOnlySpectrum l2, Spectrum target, final float minimum) {
		
		float[] l1a = ((Spectrum)l1).backingArray();
		float[] l2a = ((Spectrum)l2).backingArray();
		float[] ta = target.backingArray();
		
		int maxInd = Math.min(l1.size(), l2.size());
		float value;
		for (int i = 0; i < maxInd; i++)
		{
			value = l1a[i] - l2a[i];
			if (!Float.isNaN(minimum) && value < minimum) value = minimum;
			ta[i] = value;
		}
		
	}




	/**
	 * Multiplies two lists together
	 * 
	 * @param l1
	 * @param l2
	 * @return a list which is the result of l1*l2
	 */
	public static Spectrum multiplyLists(ReadOnlySpectrum l1, ReadOnlySpectrum l2)
	{

		int maxInd = Math.min(l1.size(), l2.size());
		Spectrum result = new ISpectrum(maxInd);
		for (int i = 0; i < maxInd; i++)
		{
			result.set(i, l1.get(i) * l2.get(i));
		}
		
		return result;
	}
	
	/**
	 * Multiplies two lists together, storing the result in l1
	 * 
	 * @param l1
	 * @param l2
	 * @return a list which is the result of l1*l2
	 */
	public static Spectrum multiplyLists_inplace(Spectrum l1, ReadOnlySpectrum l2)
	{

		int maxInd = Math.min(l1.size(), l2.size());
		for (int i = 0; i < maxInd; i++)
		{
			l1.set(i, l1.get(i) * l2.get(i));
		}
		
		return l1;
	}




	/**
	 * takes a dataset and returns a single scan/list containing the average value for each channel
	 * 
	 * @param dataset
	 * @return the per-channel-averaged scan
	 */
	public static Spectrum getDatasetAverage(List<ReadOnlySpectrum> dataset)
	{

		Spectrum average = new ISpectrum(dataset.get(0).size());

		float channelSum;
		for (int channel = 0; channel < dataset.get(0).size(); channel++)
		{
			channelSum = 0;
			for (int point = 0; point < dataset.size(); point++)
			{
				channelSum += dataset.get(point).get(channel);
			}
			average.set(channel, channelSum / dataset.size());
		}

		return average;

	}


	/**
	 * Takes a dataset and returns a single scan/list containing the average of the top 10% most intense values for each
	 * channel
	 * 
	 * @param dataset
	 * @return the top-10% per-channel scan
	 */
	public static Spectrum getDatasetMaximums(List<Spectrum> dataset)
	{

		// a list for eventual maximums, and a list for all values for a particular channel
		Spectrum maximums = new ISpectrum(dataset.get(0).size());
		List<Float> valuesAtChannel = new ArrayList<Float>();

		// determine a range for the top 10th of a list
		int section = (int) Math.round((dataset.size() - 1.0) * 0.9);
		if (section < 0) section = 0;

		float channelMax;
		for (int channel = 0; channel < dataset.get(0).size(); channel++)
		{

			valuesAtChannel.clear();
			for (int point = 0; point < dataset.size(); point++)
			{
				// if (dataset.get(point).get(channel) > channelMax) channelMax =
				// dataset.get(point).get(channel);
				valuesAtChannel.add(dataset.get(point).get(channel));
			}

			// sort the values for this channel
			Collections.sort(valuesAtChannel);

			// grab the top 10th of the list
			List<Float> top = valuesAtChannel.subList(section, dataset.size());
			// if there isn't a tenth to grab, just get the top one
			if (top.size() == 0)
			{
				top = valuesAtChannel.subList(dataset.size() - 1, dataset.size());
			}

			// do an averaging
			channelMax = 0.0f;
			for (int i = 0; i < top.size(); i++)
			{
				channelMax += top.get(i);
			}
			channelMax /= top.size();

			maximums.set(channel, channelMax);
		}

		return maximums;
	}


	/**
	 * Logs the given list of values
	 * 
	 * @param list
	 * @return a copy of list, with the values logged
	 */
	public static Spectrum logList(ReadOnlySpectrum list)
	{

		Spectrum result = new ISpectrum(list.size());
		logList_target(list, result);
		return result;

	}


	/**
	 * Logs the given list of values, with the results stored in list
	 * 
	 * @param list
	 */
	public static void logList_target(ReadOnlySpectrum source, Spectrum target)
	{

		float logValue;
		for (int i = 0; i < source.size(); i++)
		{
			logValue = (float)Math.log1p(source.get(i));
			logValue = logValue < 0 ? 0 : logValue;
			logValue = Float.isNaN(logValue) ? 0 : logValue;
			target.set(i, logValue);
		}

	}



	
	/**
	 * Sums the values in the given list
	 * 
	 * @param list
	 * @return the sum of the values in the list
	 */
	public static float sumValuesInList(ReadOnlySpectrum list, int start, int stop)
	{
		float sum = 0;
		for (int i = start; i < stop; i++)
		{
			sum += list.get(i);
		}
		return sum;
	}

	/**
	 * Reverses a data grid along the y axis.
	 * @param data the data to be reversed
	 * @param grid the {@link GridPerspective} defining the dimensions of the data
	 * @return a list of values reversed on the Y axis
	 */
	public static Spectrum gridYReverse(Spectrum data, GridPerspective<Float> grid)
	{

		Spectrum result = new ISpectrum(grid.height * grid.width, 0.0f);
		int y_reverse;

		for (int x = 0; x < grid.width; x++) {
			for (int y = 0; y < grid.height; y++) {

				y_reverse = grid.height - y - 1;

				int index = grid.getIndexFromXY(x, y);
				int index_reverse = grid.getIndexFromXY(x, y_reverse);
				result.set(index_reverse, data.get(index));
				//grid.set(result, x, y_reverse, data.get(grid.getIndexFromXY(x, y)));

			}
		}

		return result;

	}


	public static Spectrum derivative(ReadOnlySpectrum list) {
		Spectrum result = new ISpectrum(list.size());
		
		for (int i = 0; i < list.size()-1; i++)
		{
			result.set(i, list.get(i+1) - list.get(i));
		}
			
		return result;
	}


	public static Spectrum integral(ReadOnlySpectrum list) {
		Spectrum result = new ISpectrum(list.size());
		float val = 0;
		
		
		for (int i = 0; i < list.size(); i++)
		{
			val += list.get(i);
			result.set(i,  val );
		}
		
		return result;
	}



}
