package peakaboo.calculations;



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fava.*;
import static fava.Fn.*;
import static fava.Functions.*;

import peakaboo.datatypes.Spectrum;
import peakaboo.datatypes.tasks.Task;
import peakaboo.datatypes.tasks.executor.TaskExecutor;
import peakaboo.datatypes.tasks.executor.implementations.SplittingTicketedTaskExecutor;



/**
 * This class contains methods used to transform lists (eg log), or to find a specific piece of information about the
 * list (eg max) Not everything that operates on or reads a list needs to go here.
 * 
 * @author Nathaniel Sherry, 2009
 */

public class SpectrumCalculations
{

	public static final int	MIN_SIZE_FOR_THREADING	= 1024;


	/**
	 * returns the maximum value in the list
	 * 
	 * @param list
	 * @return max(list)
	 */
	public static float max(Spectrum list)
	{
		float max = Float.MIN_VALUE;
		for (int i = 0; i < list.size(); i++) max = Math.max(max, list.get(i));
		return max;
	}


	/**
	 * returns the absolute version of the list
	 * 
	 * @param list
	 * @return max(list)
	 */
	public static Spectrum abs(Spectrum source)
	{
		Spectrum result = new Spectrum(source.size());
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
	 * @return min(list)
	 */
	public static float min(Spectrum list)
	{
		float min = Float.MAX_VALUE;
		for (int i = 0; i < list.size(); i++) min = Math.min(min, list.get(i));
		return min;
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
			//min = Math.min(min, list.get(i));
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

		return foldr(dataset, max(dataset.get(0)), new FunctionCombine<Spectrum, Float, Float>() {

			@Override
			public Float f(Spectrum list, Float currentMax)
			{
				return Math.max(currentMax, max(list));
			}
		});

	}


	/**
	 * returns a copy of the given list with all values in the list expressed as a fraction of the maximum value
	 * 
	 * @param data
	 * @return normalized data
	 */
	public static Spectrum normalize(Spectrum data)
	{
		float max = max(data);
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
		float max = max(data);
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
	public static Spectrum multiplyBy(Spectrum source, final float value)
	{

		Spectrum result = new Spectrum(source.size());
		float newvalue;
		for (int i = 0; i < source.size(); i++)
		{
			newvalue = source.get(i) * value;
			result.set(i, newvalue);
		}

		return result;
	}


	/**
	 * A threaded version of {@link SpectrumCalculations#multiplyBy(List, float)}
	 * 
	 * @param data
	 * @param value
	 * @return a copy of data multiplied value
	 */
	public static Spectrum multiplyBy_threaded(final Spectrum data, final float value)
	{

		if (data.size() < MIN_SIZE_FOR_THREADING) return multiplyBy(data, value);

		final Spectrum result = new Spectrum(data.size());

		Task task = new Task() {

			@Override
			public boolean work(int ordinal)
			{
				result.set(ordinal, data.get(ordinal) * value);
				return true;
			}
		};

		TaskExecutor te = new SplittingTicketedTaskExecutor(data.size(), task);
		te.executeBlocking();

		return result;
	}


	/**
	 * Returns a copy of the given list with all values in the list expressed as a fraction of the given value
	 * 
	 * @param data
	 * @param value
	 * @return a copy of data divided by value
	 */
	public static Spectrum divideBy(Spectrum source, final float value)
	{

		Spectrum result = new Spectrum(source.size());
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
	 * A threaded version of {@link #divideBy(List, float)}
	 * 
	 * @param data
	 * @param value
	 * @return a copy of data divided by value
	 */
	public static Spectrum divideBy_threaded(final Spectrum data, final float value)
	{

		if (data.size() < MIN_SIZE_FOR_THREADING) return divideBy(data, value);

		final Spectrum result = new Spectrum(data.size());

		Task task = new Task() {

			@Override
			public boolean work(int ordinal)
			{
				result.set(ordinal, data.get(ordinal) / value);
				return true;
			}
		};

		TaskExecutor te = new SplittingTicketedTaskExecutor(data.size(), task);
		te.executeBlocking();

		return result;
	}


	/**
	 * Subtracts value from each element in data
	 * 
	 * @param data
	 * @param value
	 * @return a copy of data, with value subtracted from each element
	 */
	public static Spectrum subtractFromList(Spectrum data, float value)
	{
		Spectrum target = new Spectrum(data.size());
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
	public static Spectrum subtractFromList(Spectrum source, final float value, final float minimum)
	{

		Spectrum result = new Spectrum(source.size());
		float newvalue;
		for (int i = 0; i < source.size(); i++)
		{
			newvalue = source.get(i) - value;
			if (value < minimum && minimum != Float.NaN) newvalue = minimum;
			result.set(i, newvalue);
		}

		return result;
	}

	
	/**
	 * Subtracts value from each element in data, while keeping all values no lower than minimum
	 * 
	 * @param data
	 * @param value
	 * @param minimum
	 * @return a copy of data, with value subtracted from each element
	 */
	public static Spectrum subtractFromList(Spectrum source, Spectrum target, final float value, final float minimum)
	{

		float newvalue;
		for (int i = 0; i < source.size(); i++)
		{
			newvalue = source.get(i) - value;
			if (value < minimum && minimum != Float.NaN) newvalue = minimum;
			target.set(i, newvalue);
		}

		return target;
	}


	/**
	 * A threaded version of {@link #subtractFromList(List, float, float)}
	 * 
	 * @param data
	 * @param value
	 * @param minimum
	 * @return a copy of data, with value subtracted from each element
	 */
	public static Spectrum subtractFromList_threaded(final Spectrum data, final float value,
			final float minimum)
	{

		if (data.size() < MIN_SIZE_FOR_THREADING) return subtractFromList(data, value, minimum);

		final Spectrum result = new Spectrum(data.size());

		Task task = new Task() {

			@Override
			public boolean work(int ordinal)
			{
				float newvalue = data.get(ordinal) - value;
				if (newvalue < minimum && minimum != Float.NaN) newvalue = minimum;
				result.set(ordinal, newvalue);
				return true;
			}
		};

		TaskExecutor te = new SplittingTicketedTaskExecutor(data.size(), task);
		te.executeBlocking();

		return result;

	}


	/**
	 * adds the elements of the two lists together
	 * 
	 * @param l1
	 * @param l2
	 * @return a list which is the sum of the two lists given
	 */
	public static Spectrum addLists(Spectrum l1, Spectrum l2)
	{

		Spectrum result = new Spectrum(l1.size());
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
	public static void addLists_inplace(Spectrum l1, Spectrum l2)
	{

		int maxInd = Math.min(l1.size(), l2.size());
		float value;
		for (int i = 0; i < maxInd; i++)
		{
			value = l1.get(i) + l2.get(i);
			l1.set(i, value);
		}
	}


	/**
	 * A threaded version of {@link #addLists(List, List)}
	 * 
	 * @param l1
	 * @param l2
	 * @return a list which is the sum of the two lists given
	 */
	public static Spectrum addLists_threaded(final Spectrum l1, final Spectrum l2)
	{

		if (l1 == null && l2 == null) return null;
		if (l2 == null) return l1;
		if (l1 == null) return l2;
		if (l1.size() != l2.size()) return null;

		if (l1.size() < MIN_SIZE_FOR_THREADING) return addLists(l1, l2);

		final Spectrum result = new Spectrum(l1.size());

		Task task = new Task() {

			@Override
			public boolean work(int ordinal)
			{
				result.set(ordinal, l1.get(ordinal) + l2.get(ordinal));
				return true;
			}
		};

		TaskExecutor te = new SplittingTicketedTaskExecutor(l1.size(), task);
		te.executeBlocking();

		return result;

	}


	/**
	 * Subtracts l2 from l1
	 * 
	 * @param l1
	 * @param l2
	 * @return a list which is the result of l1 - l2
	 */
	public static Spectrum subtractLists(Spectrum l1, Spectrum l2)
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
	public static Spectrum subtractLists(Spectrum l1, Spectrum l2, final float minimum)
	{

		Spectrum result = new Spectrum(l1.size());
		int maxInd = Math.min(l1.size(), l2.size());
		float value;
		for (int i = 0; i < maxInd; i++)
		{
			value = l1.get(i) - l2.get(i);
			if (value < minimum && minimum != Float.NaN) value = minimum;
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
		subtractLists_inplace(l1, l2, Float.NaN);
	}


	/**
	 * Subtracts l2 from l1, placing the results in l1, while keeping all values no lower than minimum
	 * 
	 * @param l1
	 * @param l2
	 * @param minimum
	 */
	public static void subtractLists_inplace(Spectrum l1, Spectrum l2, final float minimum)
	{

		int maxInd = Math.min(l1.size(), l2.size());
		float value;
		for (int i = 0; i < maxInd; i++)
		{
			value = l1.get(i) - l2.get(i);
			if (value < minimum && minimum != Float.NaN) value = minimum;
			l1.set(i, value);
		}
		
	}


	/**
	 * A threaded version of {@link #subtractLists(List, List, float)}
	 * 
	 * @param l1
	 * @param l2
	 * @param minimum
	 * @return a list which is the result of l1 - l2
	 */
	public static Spectrum subtractLists_threaded(final Spectrum l1, final Spectrum l2, final float minimum)
	{

		if (l1 == null && l2 == null) return null;
		if (l2 == null) return l1;
		if (l1 == null) return l2;
		if (l1.size() != l2.size()) return null;

		if (l1.size() < MIN_SIZE_FOR_THREADING) return subtractLists(l1, l2, minimum);

		final Spectrum result = new Spectrum(l1.size());

		Task task = new Task() {

			@Override
			public boolean work(int ordinal)
			{
				float newValue = l1.get(ordinal) - l2.get(ordinal);
				if (newValue < minimum && minimum != Float.NaN) newValue = minimum;
				result.set(ordinal, newValue);
				return true;
			}
		};

		TaskExecutor te = new SplittingTicketedTaskExecutor(l1.size(), task);
		te.executeBlocking();

		return result;

	}


	/**
	 * Multiplies two lists together
	 * 
	 * @param l1
	 * @param l2
	 * @return a list which is the result of l1*l2
	 */
	public static Spectrum multiplyLists(Spectrum l1, Spectrum l2)
	{

		int maxInd = Math.min(l1.size(), l2.size());
		Spectrum result = new Spectrum(maxInd);
		for (int i = 0; i < maxInd; i++)
		{
			result.set(i, l1.get(i) * l2.get(i));
		}
		
		return result;
	}


	/**
	 * A threaded version of {@link #multiplyBy(List, float)}
	 * 
	 * @param l1
	 * @param l2
	 * @return a list which is the result of l1*l2
	 */
	public static Spectrum multiplyLists_threaded(final Spectrum l1, final Spectrum l2)
	{

		if (l1 == null && l2 == null) return null;
		if (l2 == null) return l1;
		if (l1 == null) return l2;
		if (l1.size() != l2.size()) return null;

		if (l1.size() < MIN_SIZE_FOR_THREADING) return multiplyLists(l1, l2);

		final Spectrum result = new Spectrum(l1.size());

		Task task = new Task() {

			@Override
			public boolean work(int ordinal)
			{
				result.set(ordinal, l1.get(ordinal) * l2.get(ordinal));
				return true;
			}
		};

		TaskExecutor te = new SplittingTicketedTaskExecutor(l1.size(), task);
		te.executeBlocking();

		return result;

	}


	/**
	 * takes a dataset and returns a single scan/list containing the average value for each channel
	 * 
	 * @param dataset
	 * @return the per-channel-averaged scan
	 */
	public static Spectrum getDatasetAverage(List<Spectrum> dataset)
	{

		Spectrum average = new Spectrum(dataset.get(0).size());
		
		for (Spectrum s : dataset)
		{
			for (int i = 0; i < s.size(); i++)
			{
				average.set(i, average.get(i) + s.get(i));
			}
		}
		
		for (int i = 0; i < average.size(); i++)
		{
			average.set(i, average.get(i) / dataset.size());
		}
		
		/*
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
		*/
		
		return average;

	}


	/**
	 * Takes a dataset and returns a single scan/list containing the most intense value for each
	 * channel
	 * 
	 * @param dataset
	 * @return the top signal per-channel scan
	 */
	public static Spectrum getDatasetMaximums(List<Spectrum> dataset)
	{

		/*// a list for eventual maximums, and a list for all values for a particular channel
		Spectrum maximums = new Spectrum(dataset.get(0).size());
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
*/
		
		
		Spectrum maximums = new Spectrum(dataset.get(0).size());
		float currentVal, maxVal;
		
		for (Spectrum s : dataset)
		{
			for (int i = 0; i < s.size(); i++)
			{
				currentVal = s.get(i);
				maxVal = maximums.get(i);
				if (currentVal > maxVal) maximums.set(i, currentVal);
			}
		}
				
		return maximums;
	}


	/**
	 * Logs the given list of values
	 * 
	 * @param list
	 * @return a copy of list, with the values logged
	 */
	public static Spectrum logList(Spectrum list)
	{

		Spectrum result = new Spectrum(list.size());
		logList_target(list, result);
		return result;

	}


	/**
	 * Logs the given list of values, with the results stored in list
	 * 
	 * @param list
	 */
	public static void logList_target(Spectrum source, Spectrum target)
	{

		float logValue;
		for (int i = 0; i < source.size(); i++)
		{
			logValue = (float)Math.log1p(source.get(i));
			logValue = logValue < 0 ? 0 : logValue;
			logValue = Float.isNaN(logValue) ? 0 : logValue;
			target.set(i, (float)logValue);
		}

	}


	/**
	 * A threaded version of {@link #logList(List)}
	 * 
	 * @param data
	 * @return a copy of list, with the values logged
	 */
	public static Spectrum logList_threaded(final Spectrum data)
	{

		if (data.size() < MIN_SIZE_FOR_THREADING) return logList(data);

		final Spectrum result = new Spectrum(data.size(), 0.0f);

		Task task = new Task() {

			@Override
			public boolean work(int ordinal)
			{
				float logValue = (float)Math.log(data.get(ordinal) + 1.0);

				logValue = logValue < 0 ? 0 : logValue;
				logValue = Float.isNaN(logValue) ? 0 : logValue;

				result.set(ordinal, (float)logValue);
				return true;
			}
		};

		TaskExecutor te = new SplittingTicketedTaskExecutor(data.size(), task);
		te.executeBlocking();

		return result;

	}


	/**
	 * Sums the values in the given list
	 * 
	 * @param list
	 * @return the sum of the values in the list
	 */
	public static float sumValuesInList(Spectrum list)
	{

		return fold(list, 0f, addf());

	}
	
	/**
	 * Sums the values in the given list
	 * 
	 * @param list
	 * @return the sum of the values in the list
	 */
	public static float sumValuesInList(Spectrum list, int start, int stop)
	{
		float sum = 0;
		for (int i = start; i < stop; i++)
		{
			sum += list.get(i);
		}
		return sum;
	}

}
