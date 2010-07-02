package peakaboo.controller.settings;



import java.io.Serializable;
import java.util.List;

import org.ho.yaml.Yaml;

import fava.*;
import fava.datatypes.Pair;

import peakaboo.controller.plotter.PlotViewOptions;
import peakaboo.datatypes.peaktable.Element;
import peakaboo.datatypes.peaktable.TransitionSeries;
import peakaboo.datatypes.peaktable.TransitionSeriesType;
import peakaboo.drawing.DrawingRequest;
import peakaboo.filters.AbstractFilter;



/**
 * This class acts as a struct for serialization and allows us to (de)serialize a single object and hava a single
 * serialVersionUID
 * 
 * @author Nathaniel Sherry, 2009
 */

public class SerializedData implements Serializable
{

	/**
	 * Version 1 of the SerializedData class
	 */
	private static final long				serialVersionUID	= 1L;

	public DrawingRequest					drawingRequest;
	public PlotViewOptions					viewOptions;
	public List<AbstractFilter>				filters;
	public List<List<Pair<String, String>>>	fittings;


	public String toYaml()
	{
		return Yaml.dump(this, true);
	}

}
