package org.peakaboo.framework.cyclops.spectrum;



import java.util.List;

import org.peakaboo.framework.cyclops.GridPerspective;


/**
 * This class contains methods used to transform Spectrum (eg log), or to find a specific piece of information about the
 * Spectrum (eg max) Not everything that operates on or reads a Spectrum needs to go here.
 * 
 * @author Nathaniel Sherry, 2009
 */

public class SpectrumCalculations
{

	public static final int	MIN_SIZE_FOR_THREADING	= 512;


	public static Spectrum maxLists(SpectrumView l1, SpectrumView l2) {
		float[] a1 = ((Spectrum)l1).backingArray();
		float[] a2 = ((Spectrum)l2).backingArray();
		
		Spectrum result = new ArraySpectrum(l1.size());
		float[] r = result.backingArray();
		
		int maxInd = Math.min(l1.size(), l2.size());
		for (int i = 0; i < maxInd; i++) {
			r[i] = Math.max(a1[i], a2[i]);
		}

		return result;
	}

	
	public static Spectrum maxLists_inplace(final Spectrum s1, final SpectrumView s2) {
		float[] a1 = s1.backingArray();
		float[] a2 = ((Spectrum)s2).backingArray();
		
		int size = Math.min(a1.length, a2.length);
		for (int i = 0; i < size; i++) {
			a1[i] = Math.max(a1[i], a2[i]);
		}
		

		return s1;
	}
	
	/**
	 * returns the absolute version of the list
	 * 
	 * @param list
	 * @return max(list)
	 */
	public static Spectrum abs(SpectrumView source)
	{
		Spectrum result = new ArraySpectrum(source.size());
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

	public static float min(SpectrumView list, final boolean allowzero)
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
	public static float maxDataset(List<? extends SpectrumView> dataset)
	{
		return dataset.stream().map(SpectrumView::max).reduce(0f, Math::max);
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
			return new ArraySpectrum(data);
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
	public static Spectrum multiplyBy(SpectrumView source, final float value)
	{

		Spectrum result = new ArraySpectrum(source.size());
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
	public static Spectrum multiplyBy_target(final SpectrumView source, final Spectrum target, final float value)
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
	

	
	
	public static Spectrum fma(final SpectrumView source, float mult, final SpectrumView add, final Spectrum target) {
		return fma(source, mult, add, target, 0, source.size()-1);				
	}
	
	public static Spectrum fma(final SpectrumView source, float mult, final SpectrumView add, final Spectrum target, int first, int last) {
		final float[] sourceArray = ((Spectrum)source).backingArray();
		final float[] addArray = ((Spectrum)add).backingArray();
		final float[] targetArray = ((Spectrum)target).backingArray();
		
		for (int i = first; i <= last; i++) {
			targetArray[i] = Math.fma(sourceArray[i], mult, addArray[i]);
		}
		
		return target;
	}

	/*
	 * Version of the fma method which adds from and stores to the target array 
	 */
	public static Spectrum fma_target(final SpectrumView source, float mult, final Spectrum target, int first, int last) {
		final float[] sourceArray = ((Spectrum)source).backingArray();
		final float[] targetArray = ((Spectrum)target).backingArray();
		
		for (int i = first; i <= last; i++) {
			targetArray[i] = Math.fma(sourceArray[i], mult, targetArray[i]);
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
	public static Spectrum divideBy(SpectrumView source, final float value)
	{

		float inverse = 1f/value;
		Spectrum result = new ArraySpectrum(source.size());
		for (int i = 0; i < source.size(); i++)
		{
			result.set(i, source.get(i) * inverse);
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

		float inverse = 1f/value;
		for (int i = 0; i < data.size(); i++)
		{
			data.set(i, data.get(i) * inverse);
		}

	}


	/**
	 * Subtracts value from each element in data
	 * 
	 * @param data
	 * @param value
	 * @return a copy of data, with value subtracted from each element
	 */
	public static Spectrum subtractFromList(SpectrumView data, float value)
	{
		Spectrum target = new ArraySpectrum(data.size());
		return subtractFromList(data, target, value, Float.NaN);
	}


	/**
	 * Subtracts value from each element in data
	 * 
	 * @param data
	 * @param value
	 * @return a copy of data, with value subtracted from each element
	 */
	public static Spectrum subtractFromList_target(SpectrumView source, Spectrum target, float value)
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
	public static Spectrum subtractFromList(SpectrumView source, final float value, final float minimum)
	{

		Spectrum result = new ArraySpectrum(source.size());
		float newvalue;
		boolean minIsNaN = Float.isNaN(minimum);
		for (int i = 0; i < source.size(); i++)
		{
			newvalue = source.get(i) - value;
			if (!minIsNaN && value < minimum) newvalue = minimum;
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
	
	public static Spectrum subtractFromList(SpectrumView source, Spectrum target, final float value) {
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
	public static Spectrum subtractFromList(SpectrumView source, Spectrum target, final float value, final float minimum)
	{

		float newvalue;
		boolean minIsNaN = Float.isNaN(minimum);
		for (int i = 0; i < source.size(); i++)
		{
			newvalue = source.get(i) - value;
			if (!minIsNaN && value < minimum) newvalue = minimum;
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
	
	public static Spectrum subtractListFrom(SpectrumView source, Spectrum target, final float value) {
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
	public static Spectrum subtractListFrom(SpectrumView source, Spectrum target, final float value, final float minimum)
	{

		float newvalue;
		boolean minIsNaN = Float.isNaN(minimum);
		for (int i = 0; i < source.size(); i++)
		{
			newvalue = value - source.get(i);
			if (!minIsNaN && value < minimum) newvalue = minimum;
			target.set(i, newvalue);
		}

		return target;
	}
	
	
	public static Spectrum addToList(SpectrumView data, float value) {
		Spectrum copy = new ArraySpectrum(data.size());
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
	public static Spectrum addLists(SpectrumView l1, SpectrumView l2)
	{

		Spectrum result = new ArraySpectrum(l1.size());
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
	public static void addLists_inplace(final Spectrum l1, final SpectrumView l2)
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
	public static Spectrum subtractLists(SpectrumView l1, SpectrumView l2)
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
	public static Spectrum subtractLists(SpectrumView l1, SpectrumView l2, final float minimum)
	{

		Spectrum result = new ArraySpectrum(l1.size());
		int maxInd = Math.min(l1.size(), l2.size());
		float value;
		boolean minIsNaN = Float.isNaN(minimum);
		for (int i = 0; i < maxInd; i++)
		{
			value = l1.get(i) - l2.get(i);
			if (!minIsNaN && value < minimum) value = minimum;
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
	public static void subtractLists_inplace(Spectrum l1, SpectrumView l2)
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
	public static void subtractLists_inplace(Spectrum l1, SpectrumView l2, final float minimum)
	{

		float[] l1a = l1.backingArray();
		float[] l2a = ((Spectrum)l2).backingArray();
		
		int maxInd = Math.min(l1.size(), l2.size());
		
		if (! Float.isFinite(minimum)) {
			// If min is not finite (not NaN and not +/- Infinity), just ignore it and perform 
			//the subtraction without it
			subtractLists_inplace(l1, l2);
			return;
		} else {
			// Otherwise, perform the subtraction and choose the larger of the minimum or the result
			for (int i = 0; i < maxInd; i++) {
				l1a[i] = Math.max(minimum, l1a[i] - l2a[i]);
			}
		}
		

		
	}

	

	public static void subtractLists_target(SpectrumView l1, SpectrumView l2, Spectrum target) {
		subtractLists_target(l1, l2, target, 0, Math.min(l1.size(), l2.size())-1);
	}
	
	public static void subtractLists_target(SpectrumView l1, SpectrumView l2, Spectrum target, int first, int last) {
		float[] l1a = ((Spectrum)l1).backingArray();
		float[] l2a = ((Spectrum)l2).backingArray();
		float[] ta = target.backingArray();
		
		for (int i = first; i <= last; i++) {
			ta[i] = l1a[i] - l2a[i];
		}
	}
	
	public static void subtractLists_target(SpectrumView l1, SpectrumView l2, Spectrum target, final float minimum) {
		
		float[] l1a = ((Spectrum)l1).backingArray();
		float[] l2a = ((Spectrum)l2).backingArray();
		float[] ta = target.backingArray();
		
		int maxInd = Math.min(l1.size(), l2.size());
		float value;
		boolean minIsNaN = Float.isNaN(minimum);
		for (int i = 0; i < maxInd; i++)
		{
			value = l1a[i] - l2a[i];
			if (!minIsNaN && value < minimum) value = minimum;
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
	public static Spectrum multiplyLists(SpectrumView l1, SpectrumView l2)
	{

		int maxInd = Math.min(l1.size(), l2.size());
		Spectrum result = new ArraySpectrum(maxInd);
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
	public static Spectrum multiplyLists_inplace(Spectrum l1, SpectrumView l2)
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
	public static Spectrum getDatasetAverage(List<SpectrumView> dataset)
	{

		Spectrum average = new ArraySpectrum(dataset.get(0).size());

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
	 * Logs the given list of values
	 * 
	 * @param list
	 * @return a copy of list, with the values logged
	 */
	public static Spectrum logList(SpectrumView list)
	{

		Spectrum result = new ArraySpectrum(list.size());
		logList_target(list, result);
		return result;

	}


	/**
	 * Logs the given list of values, with the results stored in list
	 * 
	 * @param list
	 */
	public static void logList_target(SpectrumView source, Spectrum target)
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
	public static float sumValuesInList(SpectrumView list, int start, int stop)
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
	public static Spectrum gridYReverse(SpectrumView data, GridPerspective<Float> grid)
	{

		Spectrum result = new ArraySpectrum(grid.height * grid.width, 0.0f);
		int y_reverse;

		for (int x = 0; x < grid.width; x++) {
			for (int y = 0; y < grid.height; y++) {

				y_reverse = grid.height - y - 1;

				int index = grid.getIndexFromXY(x, y);
				int index_reverse = grid.getIndexFromXY(x, y_reverse);
				result.set(index_reverse, data.get(index));

			}
		}

		return result;

	}


	public static Spectrum derivative(SpectrumView list) {
		Spectrum result = new ArraySpectrum(list.size());
		
		for (int i = 0; i < list.size()-1; i++)
		{
			result.set(i, list.get(i+1) - list.get(i));
		}
			
		return result;
	}


	public static Spectrum integral(SpectrumView list) {
		Spectrum result = new ArraySpectrum(list.size());
		float val = 0;
		
		
		for (int i = 0; i < list.size(); i++)
		{
			val += list.get(i);
			result.set(i,  val );
		}
		
		return result;
	}



}
