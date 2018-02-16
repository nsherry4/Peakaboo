package peakaboo.controller.settings;



import java.util.ArrayList;
import java.util.List;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;

import peakaboo.controller.plotter.settings.SettingsModel;
import peakaboo.filter.model.Filter;
import peakaboo.filter.model.SerializedFilter;
import peakaboo.filter.plugins.noise.SpringSmoothing;
import scidraw.drawing.DrawingRequest;



/**
 * This class acts as a struct for serialization and allows us to (de)serialize a single object and hava a single
 * serialVersionUID
 * 
 * @author Nathaniel Sherry, 2009
 */

public class SerializedData
{

	public DrawingRequest					drawingRequest;
	public SettingsModel					settings;
	
	public List<Integer>					badScans;

	
	public List<SerializedFilter>			filters = new ArrayList<>();
	public void storeFilters(List<Filter> filters) {
		this.filters.clear();
		for (Filter filter : filters) {
			this.filters.add(new SerializedFilter(filter));
		}
	}
	
	
	
	
	//Outer List - list of fitting
	//Middle List - list of TS in a fitting
	//Inner List - stand-in for a pair, since this YAML library seems to have trouble serializing pairs
	// the pair contains Element and Transition. eg (Ni, K)
	//public List<List<List<String>>>	fittings;
	public List<SerializedTransitionSeries> fittings;

	
	
	public static SerializedData deserialize(String yaml)
	{
		
		Yaml y = new Yaml();
		SerializedData data = (SerializedData)y.load(yaml);
		return data;
		
	}
	
	public String serialize()
	{
		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(FlowStyle.BLOCK);
		
		Yaml y = new Yaml(options);
		
				
		return y.dump(this);
		
	}
	
	public static void main(String[] args) {
		
		Yaml y = new Yaml();
		Filter filter = new SpringSmoothing();
		SerializedFilter serial = new SerializedFilter(filter);
			
		y.dump(serial);
		y.dump(filter);
	}



	
}
