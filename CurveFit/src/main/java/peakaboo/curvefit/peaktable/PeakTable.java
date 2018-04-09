package peakaboo.curvefit.peaktable;



import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import peakaboo.curvefit.transition.TransitionSeries;
import peakaboo.curvefit.transition.TransitionSeriesType;
import scitypes.Pair;





/**
 * This class stores the information from the peak table in the form of a list of transition objects. It also provides
 * methods for looking up nearby transitions based on information such as by energy
 * 
 * @author Nathaniel Sherry, 2009-2010
 */

public class PeakTable
{

	private static ArrayList<TransitionSeries> elementTransitions = new ArrayList<TransitionSeries>();

	private static ArrayList<TransitionSeries> getAllTransitions() {
		ArrayList<TransitionSeries> newlist = new ArrayList<>();
		//Make sure the canonical TransitionSeries list is initialized
		if (elementTransitions.size() == 0) {
			PeakTableReader.readPeakTable();
		}
		for (TransitionSeries ts : elementTransitions) {
			newlist.add(new TransitionSeries(ts));
		}
		return newlist;
	}

	/**
	 * Adds a {@link TransitionSeries} to the PeakTable
	 * 
	 * @param ts
	 *            the {@link TransitionSeries} to add
	 */
	public static void addSeries(TransitionSeries ts)
	{
		if (ts.getTransitionCount() == 0) return;
		elementTransitions.add(ts);
	}
	
	public static void clearSeries() {
		elementTransitions.clear();
	}




	/**
	 * Generates a list of all {@link TransitionSeries} for a given {@link Element} e
	 * 
	 * @param e
	 *            the {@link Element} to retrieve the {@link TransitionSeries} for
	 * @return a list of {@link TransitionSeries} for the given {@link Element}
	 */
	public static List<TransitionSeries> getTransitionSeriesForElement(final Element e)
	{
		return getAllTransitions().stream().filter(ts -> (ts.element == e)).collect(Collectors.toList());
	}
	
	public static List<TransitionSeries> getAllTransitionSeries()
	{
		return getAllTransitions().stream().map(a -> a).collect(toList());
	}
	
	public static TransitionSeries getTransitionSeries(final Element e, final TransitionSeriesType t)
	{
		List<TransitionSeries> tss = getAllTransitions().stream().filter(ts -> (ts.element == e) && (ts.type == t)).collect(Collectors.toList());
		if (tss.size() == 0) return null;
		return tss.get(0);
	}
	
	public static void main(String[] args) {
		PeakTableReader.readPeakTableXraylib();
		for (TransitionSeries ts : PeakTable.getAllTransitionSeries()) {
			System.out.println(ts);
		}
	}
	
}
