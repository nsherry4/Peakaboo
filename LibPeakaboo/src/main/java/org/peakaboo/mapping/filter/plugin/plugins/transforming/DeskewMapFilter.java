package org.peakaboo.mapping.filter.plugin.plugins.transforming;

import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.framework.autodialog.model.style.editors.IntegerSpinnerStyle;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.GridPerspective;
import org.peakaboo.framework.cyclops.ISpectrum;
import org.peakaboo.framework.cyclops.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.Spectrum;
import org.peakaboo.mapping.filter.model.AreaMap;
import org.peakaboo.mapping.filter.plugin.MapFilterDescriptor;
import org.peakaboo.mapping.filter.plugin.plugins.AbstractMapFilter;

public class DeskewMapFilter extends AbstractMapFilter {

	Parameter<Integer> angle;
	
	@Override
	public String getFilterName() {
		return "Deskew";
	}

	@Override
	public String getFilterDescription() {
		return "The Deskew plugin corrects for problems with an experimental setup which results in data being horizontally skewed. Vertical skew can be corrected through combination with the rotation filters.";
	}

	@Override
	public MapFilterDescriptor getFilterDescriptor() {
		return new MapFilterDescriptor(MapFilterDescriptor.GROUP_TRANSFORMING, "Deskewed");
	}

	@Override
	public void initialize() {
		angle = new Parameter<Integer>("Angle", new IntegerSpinnerStyle(), 0, a -> Math.abs(a.getValue()) <= 45);
		super.addParameter(angle);
	}

	@Override
	public AreaMap filter(AreaMap source) {
		float skewWidth = (float) (Math.tan(angle.getValue()/57.29578f) * source.getSize().y);
		int newWidth = (int) Math.ceil(Math.abs(skewWidth) + source.getSize().x); 

		GridPerspective<Float> ingrid  = new GridPerspective<Float>(source.getSize().x, source.getSize().y, 0f);
		GridPerspective<Float> outgrid = new GridPerspective<Float>(newWidth, source.getSize().y, 0f);
		ReadOnlySpectrum input = source.getData();
		Spectrum output = new ISpectrum(source.getSize().y * newWidth);
		
		outgrid.visit(output, (pi, px, py, value) -> {
			//how far along is this row
			float ypercent = ((float)py)/((float)source.getSize().y);
			float skewOffset = skewWidth * ypercent;
			float skewX = px-skewOffset;
			if (skewWidth < 0) {
				skewX -= Math.abs(skewWidth);
			}
			int skewXFloor = (int) Math.floor(skewX);
			//how to sample from floor and ceil pixels, how much to take from each
			//eg x=10.2, ceilPercent = 0.2 or 20%
			float ceilPercent = skewX - ((float)skewXFloor);
			
			float floorValue = ingrid.get(input, skewXFloor, py);
			float ceilValue = ingrid.get(input, skewXFloor+1, py);
			
			value = floorValue*(1f-ceilPercent) + ceilValue*ceilPercent;
			outgrid.set(output, px, py, value);
		});
		
		return new AreaMap(output, source.getElements(), new Coord<Integer>(newWidth, source.getSize().y), null);		
		
	}

	@Override
	public boolean isReplottable() {
		return false;
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
		return "c7772c3d-f06d-4d71-b940-aebf93bd7814";
	}

}
