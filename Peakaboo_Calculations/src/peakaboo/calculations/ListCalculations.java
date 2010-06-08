package peakaboo.calculations;



import java.util.Collections;
import java.util.List;

import peakaboo.calculations.functional.Function1;
import peakaboo.calculations.functional.Function2;
import peakaboo.calculations.functional.Functional;
import peakaboo.datatypes.DataTypeFactory;
import peakaboo.datatypes.tasks.Task;
import peakaboo.datatypes.tasks.executor.TaskExecutor;
import peakaboo.datatypes.tasks.executor.implementations.SplittingTicketedTaskExecutor;



/**
 * This class contains methods used to transform lists (eg log), or to find a specific piece of information about the
 * list (eg max) Not everything that operates on or reads a list needs to go here.
 * 
 * @author Nathaniel Sherry, 2009
 */

public class ListCalculations
{

	public static final int	MIN_SIZE_FOR_THREADING	= 1024;


	/**
	 * returns the maximum value in the list
	 * 
	 * @param list
	 * @return max(list)
	 */
	public static double max(List<Double> list)
	{
		return Collections.max(list);
	}


	/**
	 * returns the absolute version of the list
	 * 
	 * @param list
	 * @return max(list)
	 */
	public static List<Double> abs(List<Double> list)
	{
		return Functional.map(list, new Function1<Double, Double>() {

			@Override
			public Double f(Double element)
			{
				return Math.abs(element);
			}
		});
	}


	/**
	 * returns the maximum value in the list
	 * 
	 * @param list
	 * @return min(list)
	 */
	public static double min(List<Double> list)
	{
		return Collections.min(list);
	}


	/**
	 * returns the maximum value in the list
	 * 
	 * @param list
	 * @param allowzero
	 *            should values of exactly 0 be considered in the search for a minimum value?
	 * @return min(list)
	 */

	public static double min(List<Double> list, final boolean allowzero)
	{

		return Functional.foldr(list, Double.MAX_VALUE, new Function2<Double, Double, Double>() {

			@Override
			public Double f(Double newval, Double currentMin)
			{
				return (newval == 0.0 && !allowzero) ? currentMin : Math.min(currentMin, newval);
			}
		});

	}


	/**
	 * Calculates the maximum value of a list of lists of values
	 * 
	 * @param dataset
	 * @return max(dataset)
	 */
	public static double maxDataset(List<List<Double>> dataset)
	{

		return Functional.foldr(dataset, max(dataset.get(0)), new Function2<List<Double>, Double, Double>() {

			@Override
			public Double f(List<Double> list, Double currentMax)
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
	public static List<Double> normalize(List<Double> data)
	{
		double max = max(data);
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
	public static void normalize_inplace(List<Double> data)
	{
		double max = max(data);
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
	public static List<Double> multiplyBy(List<Double> data, final double value)
	{

		return Functional.map(data, new Function1<Double, Double>() {

			@Override
			public Double f(Double element)
			{
				return element * value;
			}
		});
	}


	/**
	 * A threaded version of {@link ListCalculations#multiplyBy(List, double)}
	 * 
	 * @param data
	 * @param value
	 * @return a copy of data multiplied value
	 */
	public static List<Double> multiplyBy_threaded(final List<Double> data, final double value)
	{

		if (data.size() < MIN_SIZE_FOR_THREADING) return multiplyBy(data, value);

		final List<Double> result = DataTypeFactory.<Double> listInit(data.size());

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
	public static List<Double> divideBy(List<Double> data, final double value)
	{

		return Functional.map(data, new Function1<Double, Double>() {

			@Override
			public Double f(Double element)
			{
				return element / value;
			}
		});

	}


	/**
	 * Replaces the values in the given list with their equivalences as expressed as divided by value
	 * 
	 * @param data
	 * @param value
	 */
	public static void divideBy_inplace(List<Double> data, final double value)
	{

		Functional.map_target(data, data, new Function1<Double, Double>() {

			@Override
			public Double f(Double element)
			{
				return element / value;
			}
		});
	}


	/**
	 * A threaded version of {@link #divideBy(List, double)}
	 * 
	 * @param data
	 * @param value
	 * @return a copy of data divided by value
	 */
	public static List<Double> divideBy_threaded(final List<Double> data, final double value)
	{

		if (data.size() < MIN_SIZE_FOR_THREADING) return divideBy(data, value);

		final List<Double> result = DataTypeFactory.<Double> listInit(data.size());

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
	public static List<Double> subtractFromList(List<Double> data, double value)
	{
		return subtractFromList(data, null, value, Double.NaN);
	}

	/**
	 * Subtracts value from each element in data
	 * 
	 * @param data
	 * @param value
	 * @return a copy of data, with value subtracted from each element
	 */
	public static List<Double> subtractFromList_target(List<Double> source, List<Double> target, double value)
	{
		return subtractFromList(source, target, value, Double.NaN);
	}
	

	
	/**
	 * Subtracts value from each element in data, while keeping all values no lower than minimum
	 * 
	 * @param data
	 * @param value
	 * @param minimum
	 * @return a copy of data, with value subtracted from each element
	 */
	public static List<Double> subtractFromList(List<Double> source, final double value, final double minimum)
	{

		return subtractFromList(source, null, value, minimum);
	}

	
	/**
	 * Subtracts value from each element in data, while keeping all values no lower than minimum
	 * 
	 * @param data
	 * @param value
	 * @param minimum
	 * @return a copy of data, with value subtracted from each element
	 */
	public static List<Double> subtractFromList(List<Double> source, List<Double> target, final double value, final double minimum)
	{

		return Functional.map_target(source, (target == null ? source : target), new Function1<Double, Double>() {

			private double	newval;


			@Override
			public Double f(Double element)
			{

				newval = element - value;

				if (newval < minimum && minimum != Double.NaN) return minimum;
				else return newval;
			}
		});
	}


	/**
	 * A threaded version of {@link #subtractFromList(List, double, double)}
	 * 
	 * @param data
	 * @param value
	 * @param minimum
	 * @return a copy of data, with value subtracted from each element
	 */
	public static List<Double> subtractFromList_threaded(final List<Double> data, final double value,
			final double minimum)
	{

		if (data.size() < MIN_SIZE_FOR_THREADING) return subtractFromList(data, value, minimum);

		final List<Double> result = DataTypeFactory.<Double> listInit(data.size());

		Task task = new Task() {

			@Override
			public boolean work(int ordinal)
			{
				double newvalue = data.get(ordinal) - value;
				if (newvalue < minimum && minimum != Double.NaN) newvalue = minimum;
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
	public static List<Double> addLists(List<Double> l1, List<Double> l2)
	{

		return Functional.zipWith(l1, l2, new Function2<Double, Double, Double>() {

			@Override
			public Double f(Double val1, Double val2)
			{
				return val1 + val2;
			}
		});
	}


	/**
	 * adds the elements of the two lists together, placing the results in l1
	 * 
	 * @param l1
	 * @param l2
	 */
	public static void addLists_inplace(List<Double> l1, List<Double> l2)
	{

		Functional.zipWith_inplace(l1, l2, new Function2<Double, Double, Double>() {

			@Override
			public Double f(Double val1, Double val2)
			{
				return val1 + val2;
			}
		});
	}


	/**
	 * A threaded version of {@link #addLists(List, List)}
	 * 
	 * @param l1
	 * @param l2
	 * @return a list which is the sum of the two lists given
	 */
	public static List<Double> addLists_threaded(final List<Double> l1, final List<Double> l2)
	{

		if (l1 == null && l2 == null) return null;
		if (l2 == null) return l1;
		if (l1 == null) return l2;
		if (l1.size() != l2.size()) return null;

		if (l1.size() < MIN_SIZE_FOR_THREADING) return addLists(l1, l2);

		final List<Double> result = DataTypeFactory.<Double> listInit(l1.size());

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
	public static List<Double> subtractLists(List<Double> l1, List<Double> l2)
	{

		return Functional.zipWith(l1, l2, new Function2<Double, Double, Double>() {

			@Override
			public Double f(Double val1, Double val2)
			{
				return val1 - val2;
			}
		});
	}


	/**
	 * Subtracts l2 from l1, while keeping all values no lower than minimum
	 * 
	 * @param l1
	 * @param l2
	 * @param minimum
	 * @return a list which is the result of l1 - l2
	 */
	public static List<Double> subtractLists(List<Double> l1, List<Double> l2, final double minimum)
	{

		return Functional.zipWith(l1, l2, new Function2<Double, Double, Double>() {

			double	newval;


			@Override
			public Double f(Double val1, Double val2)
			{
				newval = val1 - val2;
				if (newval < minimum) return minimum;
				else return newval;
			}
		});

	}


	/**
	 * Subtracts l2 from l1, placing the results in l1
	 * 
	 * @param l1
	 * @param l2
	 */
	public static void subtractLists_inplace(List<Double> l1, List<Double> l2)
	{
		subtractLists_inplace(l1, l2, Double.NaN);
	}


	/**
	 * Subtracts l2 from l1, placing the results in l1, while keeping all values no lower than minimum
	 * 
	 * @param l1
	 * @param l2
	 * @param minimum
	 */
	public static void subtractLists_inplace(List<Double> l1, List<Double> l2, final double minimum)
	{

		Functional.zipWith_inplace(l1, l2, new Function2<Double, Double, Double>() {

			double	newval;


			@Override
			public Double f(Double val1, Double val2)
			{
				newval = val1 - val2;
				if (newval < minimum && minimum != Double.NaN) return minimum;
				else return newval;
			}
		});

	}


	/**
	 * A threaded version of {@link #subtractLists(List, List, double)}
	 * 
	 * @param l1
	 * @param l2
	 * @param minimum
	 * @return a list which is the result of l1 - l2
	 */
	public static List<Double> subtractLists_threaded(final List<Double> l1, final List<Double> l2, final double minimum)
	{

		if (l1 == null && l2 == null) return null;
		if (l2 == null) return l1;
		if (l1 == null) return l2;
		if (l1.size() != l2.size()) return null;

		if (l1.size() < MIN_SIZE_FOR_THREADING) return subtractLists(l1, l2, minimum);

		final List<Double> result = DataTypeFactory.<Double> listInit(l1.size());

		Task task = new Task() {

			@Override
			public boolean work(int ordinal)
			{
				double newValue = l1.get(ordinal) - l2.get(ordinal);
				if (newValue < minimum && minimum != Double.NaN) newValue = minimum;
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
	public static List<Double> multiplyLists(List<Double> l1, List<Double> l2)
	{

		return Functional.zipWith(l1, l2, new Function2<Double, Double, Double>() {

			@Override
			public Double f(Double val1, Double val2)
			{
				return val1 * val2;
			}
		});

	}


	/**
	 * A threaded version of {@link #multiplyBy(List, double)}
	 * 
	 * @param l1
	 * @param l2
	 * @return a list which is the result of l1*l2
	 */
	public static List<Double> multiplyLists_threaded(final List<Double> l1, final List<Double> l2)
	{

		if (l1 == null && l2 == null) return null;
		if (l2 == null) return l1;
		if (l1 == null) return l2;
		if (l1.size() != l2.size()) return null;

		if (l1.size() < MIN_SIZE_FOR_THREADING) return multiplyLists(l1, l2);

		final List<Double> result = DataTypeFactory.<Double> listInit(l1.size());

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
	public static List<Double> getDatasetAverage(List<List<Double>> dataset)
	{

		List<Double> average = DataTypeFactory.<Double> list();

		double channelSum;
		for (int channel = 0; channel < dataset.get(0).size(); channel++)
		{
			channelSum = 0;
			for (int point = 0; point < dataset.size(); point++)
			{
				channelSum += dataset.get(point).get(channel);
			}
			average.add(channel, channelSum / dataset.size());
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
	public static List<Double> getDatasetMaximums(List<List<Double>> dataset)
	{

		// a list for eventual maximums, and a list for all values for a particular channel
		List<Double> maximums = DataTypeFactory.<Double> list();
		List<Double> valuesAtChannel = DataTypeFactory.<Double> list();

		// determine a range for the top 10th of a list
		int section = (int) Math.round((dataset.size() - 1.0) * 0.9);
		if (section < 0) section = 0;

		double channelMax;
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
			List<Double> top = valuesAtChannel.subList(section, dataset.size());
			// if there isn't a tenth to grab, just get the top one
			if (top.size() == 0)
			{
				top = valuesAtChannel.subList(dataset.size() - 1, dataset.size());
			}

			// do an averaging
			channelMax = 0.0;
			for (int i = 0; i < top.size(); i++)
			{
				channelMax += top.get(i);
			}
			channelMax /= top.size();

			maximums.add(channel, channelMax);
		}

		return maximums;
	}


	/**
	 * Logs the given list of values
	 * 
	 * @param list
	 * @return a copy of list, with the values logged
	 */
	public static List<Double> logList(List<Double> list)
	{

		List<Double> result = DataTypeFactory.<Double> listInit(list);
		logList_inplace(result);
		return result;

	}


	/**
	 * Logs the given list of values, with the results stored in list
	 * 
	 * @param list
	 */
	public static void logList_inplace(List<Double> list)
	{

		Functional.map_target(list, list, new Function1<Double, Double>() {

			private double	logValue;


			@Override
			public Double f(Double value)
			{
				logValue = Math.log(value + 1.0);
				logValue = logValue < 0 ? 0 : logValue;
				logValue = Double.isNaN(logValue) ? 0 : logValue;
				return logValue;
			}
		});

	}


	/**
	 * A threaded version of {@link #logList(List)}
	 * 
	 * @param data
	 * @return a copy of list, with the values logged
	 */
	public static List<Double> logList_threaded(final List<Double> data)
	{

		if (data.size() < MIN_SIZE_FOR_THREADING) return logList(data);

		final List<Double> result = DataTypeFactory.<Double> listInit(data.size());

		Task task = new Task() {

			@Override
			public boolean work(int ordinal)
			{
				double logValue = Math.log(data.get(ordinal) + 1.0);

				logValue = logValue < 0 ? 0 : logValue;
				logValue = Double.isNaN(logValue) ? 0 : logValue;

				result.set(ordinal, logValue);
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
	public static double sumValuesInList(List<Double> list)
	{

		return Functional.foldr(list, 0d, new Function2<Double, Double, Double>() {

			@Override
			public Double f(Double val, Double sum)
			{
				return sum + val;
			}
		});

	}

}
