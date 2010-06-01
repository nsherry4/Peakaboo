package peakaboo.controller.settings;


import java.io.Serializable;
import java.util.List;

import peakaboo.controller.plotter.PlotViewOptions;
import peakaboo.datatypes.peaktable.Element;
import peakaboo.drawing.DrawingRequest;
import peakaboo.filters.AbstractFilter;

/**
 * This class acts as a struct for serialization and allows us to (de)serialize a single object and hava a
 * single serialVersionUID
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */


public class SerializedData implements Serializable
{

	/**
	 * Version 1 of the SerializedData class
	 */
	private static final long	serialVersionUID	= 1L;

	public DrawingRequest		dr;
	public PlotViewOptions		viewOptions;
	public List<AbstractFilter>	filters;
	public List<Element>		elements;


}
