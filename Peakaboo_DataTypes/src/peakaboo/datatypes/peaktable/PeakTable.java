package peakaboo.datatypes.peaktable;


import java.util.Comparator;
import java.util.List;
import java.util.Collections;

import peakaboo.datatypes.DataTypeFactory;
import peakaboo.datatypes.Pair;

/**
 * 
 * This class stores the information from the peak table in the form of a list of transition objects. It also
 * provides methods for looking up nearby transitions based on information such as by energy
 * 
 * @author Nathaniel Sherry, 2009
 */

public class PeakTable
{

	private List<TransitionSeries>	elementTransitions;


	public PeakTable()
	{
		elementTransitions = DataTypeFactory.<TransitionSeries> list();
	}


	/**
	 * Adds a {@link TransitionSeries} to the PeakTable
	 * 
	 * @param ts
	 *            the {@link TransitionSeries} to add
	 */
	public void addSeries(TransitionSeries ts)
	{
		if (ts.getTransitionCount() == 0) return;
		elementTransitions.add(ts);
	}


	/**
	 * Generates a list of pairs of {@link TransitionSeries} and doubles, indicating the TransitionSeries
	 * nearest to the given energy, and its distance in energy from the ideal.
	 * 
	 * @param energy the energy level to match against
	 * @return a list of pairs of {@link TransitionSeries} and Doubles indicating how far away each TransitionSeries is
	 */
	public List<Pair<TransitionSeries, Double>> getNearestMatchesToEnergy(double energy)
	{

		List<Pair<TransitionSeries, Double>> matches = DataTypeFactory.<Pair<TransitionSeries, Double>> list();

		for (TransitionSeries ts : elementTransitions) {
			matches.add(new Pair<TransitionSeries, Double>(ts, ts.getProximityToEnergy(energy)));
		}


		Collections.sort(matches, new Comparator<Pair<TransitionSeries, Double>>() {

			public int compare(Pair<TransitionSeries, Double> o1, Pair<TransitionSeries, Double> o2)
			{
				return (int) Math.round(o1.second - o2.second);
			}
		});

		return matches;
	}


	/**
	 * Generates a list of all {@link TransitionSeries} for a given {@link Element} e
	 * @param e the {@link Element} to retrieve the {@link TransitionSeries} for
	 * @return a list of {@link TransitionSeries} for the given {@link Element}
	 */
	public List<TransitionSeries> getTransitionSeriesForElement(Element e)
	{

		List<TransitionSeries> matches = DataTypeFactory.<TransitionSeries> list();

		for (TransitionSeries ts : elementTransitions) {
			if (ts.element == e) matches.add(ts);
		}

		return matches;
	}
}
