package org.peakaboo.mapping.filter.plugin.plugins.smoothing;

import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.framework.autodialog.model.style.editors.RealSpinnerStyle;
import org.peakaboo.framework.cyclops.GridPerspective;
import org.peakaboo.framework.cyclops.ISpectrum;
import org.peakaboo.framework.cyclops.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.Spectrum;
import org.peakaboo.mapping.filter.model.AreaMap;
import org.peakaboo.mapping.filter.plugin.MapFilterDescriptor;
import org.peakaboo.mapping.filter.plugin.plugins.AbstractMapFilter;

public class DenoiseMapFilter extends AbstractMapFilter {

	Parameter<Float> paramThreshold;
	
	@Override
	public String getFilterName() {
		return "Denoise";
	}

	@Override
	public String getFilterDescription() {
		return "The Denoise filter finds aberrantly outlying pixels and replaces them with local averages";
	}

	@Override
	public MapFilterDescriptor getFilterDescriptor() {
		return new MapFilterDescriptor(MapFilterDescriptor.GROUP_SMOOTHING, "Denoised");
	}

	@Override
	public void initialize() {
		paramThreshold = new Parameter<Float>("Threshold", new RealSpinnerStyle(), 1.5f, this::validate);
		super.addParameter(paramThreshold);
	}

	private boolean validate(Parameter<?> p) {
		return paramThreshold.getValue() >= 1.1f;
	}
	
	@Override
	public AreaMap filter(AreaMap source) {
		ReadOnlySpectrum data = source.getData();
		Spectrum filtered = new ISpectrum(data);
		GridPerspective<Float> grid = new GridPerspective<Float>(source.getSize().x, source.getSize().y, 0f);
		
		float threshold = paramThreshold.getValue();
		
		for (int y = 0; y < source.getSize().y; y++) {
			for (int x = 0; x < source.getSize().x; x++) {

				float sum = 0f;
				int count = 0;
				float value = grid.get(data, x, y);
				
				int delta[] = {-1, 0, 1};
				boolean replace = true;
				
				average: 
				for (int dx : delta) {
					for (int dy : delta) {
						if (dx == 0 && dy == 0) {
							continue;
						}
						
						int px = x+dx;
						int py = y+dy;
						float pvalue;
						
						if (grid.boundsCheck(px, py)) {
							pvalue = grid.get(data, px, py);
							sum += pvalue; 
							count ++;
							
							if (value >= pvalue/threshold && value <= pvalue*threshold) {
								replace = false;
								break average;
							}
						}
						
					}
				}

				if (replace) {
					float average = sum/count;
					if (value < average/threshold || value > average*threshold) {
						grid.set(filtered, x, y, average);
					}	
				}
				
				
			}
		}

		
		return new AreaMap(filtered, source);
	}

	@Override
	public boolean isReplottable() {
		return true;
	}

	@Override
	public boolean pluginEnabled() {
		return true;
	}

	@Override
	public String pluginVersion() {
		return "1.0";
	}

	@Override
	public String pluginUUID() {
		return "964d7707-d8ed-48fd-a2fa-38116d1d6a85";
	}

}
