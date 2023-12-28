package org.peakaboo.mapping.filter.plugin.plugins;

import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.framework.autodialog.model.style.editors.IntegerSpinnerStyle;
import org.peakaboo.framework.autodialog.model.style.editors.RealSpinnerStyle;
import org.peakaboo.framework.cyclops.GridPerspective;
import org.peakaboo.framework.cyclops.spectrum.ISpectrum;
import org.peakaboo.framework.cyclops.spectrum.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.mapping.filter.model.AreaMap;

public abstract class AbstractConvolvingMapFilter extends AbstractMapFilter {


	/**
	 * kernel should be a 3x3 convolution kernel
	 */
	protected Parameter<Integer> reps;
	protected Parameter<Float> intensity;
	private int reach = 1;

	public AbstractConvolvingMapFilter(int reach) {
		super();
		this.reach = reach;
	}
	
	@Override
	public void initialize() {
		reps = new Parameter<>("Repetitions", new IntegerSpinnerStyle(), 1, this::validateReps);
		addParameter(reps);
		intensity = new Parameter<>("Intensity", new RealSpinnerStyle(), 1f, this::validateIntensity);
		addParameter(intensity);
	}

	protected boolean validateReps(Parameter<?> param) {
		if (reps.getValue() <= 0) { return false; }
		if (reps.getValue() > 10) { return false; }
		return true;
	}
	
	protected boolean validateIntensity(Parameter<?> param) {
		if (intensity.getValue() <= 0.5) { return false; }
		if (intensity.getValue() > 2) { return false; }
		return true;
	}
	
	protected abstract float[][] getKernel(float intensity);
	
	@Override
	public AreaMap filter(MapFilterContext ctx) {
		AreaMap source = ctx.map();
		ReadOnlySpectrum data = source.getData();
		Spectrum filtered = new ISpectrum(data.size());
		GridPerspective<Float> grid = new GridPerspective<Float>(source.getSize().x, source.getSize().y, 0f);

		//apply the intenisty value to 
		float[][] kernel = getKernel(intensity.getValue());
				
		for (int rep = 0; rep < reps.getValue(); rep++) {
		
			for (int y = 0; y < source.getSize().y; y++) {
				for (int x = 0; x < source.getSize().x; x++) {
	
					float sum = 0f;
					float count = 0;
					
					for (int dy = -reach; dy <= reach; dy++) {
						int py = y+dy;
						for (int dx = -reach; dx <= reach; dx++) {
							int px = x+dx;
							if (!grid.boundsCheck(px, py)) {
								continue;
							}
							float kval = kernel[reach+dy][reach+dx];
							sum += grid.get(data, px, py)*kval;
							count += kval;
						}
					}
					
					if (count == 0) {
						count = 0.01f;
					}
					float newValue = Math.max(0, sum/count);
					grid.set(filtered, x, y, newValue);
					
				}
			}
			
			data = filtered;
			
		}
		
		return new AreaMap(filtered, source);
	}

	@Override
	public boolean isReplottable() {
		return true;
	}

	


}
