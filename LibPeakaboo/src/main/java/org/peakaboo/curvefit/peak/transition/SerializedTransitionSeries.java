package org.peakaboo.curvefit.peak.transition;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.peakaboo.curvefit.peak.table.PeakTable;


/**
 * Simple structure for serializing a {@link TransitionSeriesMode} and reconsituting it.
 * @author NAS
 *
 */
public class SerializedTransitionSeries
{

	public List<String> components;
	public boolean visible;

	public SerializedTransitionSeries()
	{
		components = new ArrayList<String>();
	}
	
	public SerializedTransitionSeries(ITransitionSeries ts)
	{
		this();
		
		components.clear();
		for (ITransitionSeries bt : ts.getPrimaryTransitionSeries())
		{
			components.add(bt.getElement().name() + ":" + bt.getShell().name());
		}
		
		this.visible = ts.isVisible();
		
	}
	

	public Optional<ITransitionSeries> toTS()
	{
		
		List<ITransitionSeries> tss = new ArrayList<>();
		ITransitionSeries created;
		
		for (String tsd : components)
		{
			created = PeakTable.SYSTEM.get(tsd);
			if (created != null) {
				tss.add(created);
			}
		}
		ITransitionSeries ts = ITransitionSeries.pileup(tss);
		if (ts == null) { return Optional.empty(); }
		ts.setVisible(this.visible);
		
		return Optional.ofNullable(ts);
		
	}
	
}
