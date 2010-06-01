package peakaboo.datatypes.eventful;


import java.util.List;

import peakaboo.datatypes.DataTypeFactory;

/**
 * 
 * An abstract controller for a simple Model/View/Controller system. Contains the mechanism for notifying
 * views of a change to the model via the controller.
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */

public abstract class Eventful implements IEventful
{

	protected final List<PeakabooSimpleListener>	listeners;


	public Eventful()
	{
		listeners = DataTypeFactory.<PeakabooSimpleListener> list();
	}


	public void addListener(PeakabooSimpleListener l)
	{
		listeners.add(l);
	}


	public void removeListener(PeakabooSimpleListener l)
	{
		listeners.remove(l);
	}
	
	public void removeAllListeners()
	{
		listeners.clear();
	}


	public void updateListeners(final Object message)
	{

		if (listeners.size() == 0) return;

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run()	{
		
				for (PeakabooSimpleListener l : listeners) {
					
					if (l instanceof PeakabooMessageListener)
						((PeakabooMessageListener)l).change(message);
					else
						l.change();
				}
				
			}
		});

	}
	
	public void updateListeners()
	{

		if (listeners.size() == 0) return;

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run()	{
		
				for (PeakabooSimpleListener l : listeners) {
					l.change();
				}
				
			}
		});

	}


}
