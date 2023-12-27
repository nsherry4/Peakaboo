package org.peakaboo.mapping.filter.plugin.plugins.mathematical;

import java.util.List;

import org.peakaboo.curvefit.peak.table.Element;
import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.framework.autodialog.model.SelectionParameter;
import org.peakaboo.framework.autodialog.model.classinfo.EnumClassInfo;
import org.peakaboo.framework.autodialog.model.style.editors.DropDownStyle;
import org.peakaboo.framework.autodialog.model.style.editors.RealSpinnerStyle;
import org.peakaboo.framework.cyclops.spectrum.SpectrumCalculations;
import org.peakaboo.mapping.filter.model.AreaMap;
import org.peakaboo.mapping.filter.plugin.MapFilterDescriptor;
import org.peakaboo.mapping.filter.plugin.plugins.AbstractMapFilter;

public class ElementMultiplyFilter extends AbstractMapFilter {

	Parameter<Float> multiplier;
	SelectionParameter<Element> chosenElement;
	
	@Override
	public String getFilterName() {
		return "Multiply Element";
	}

	@Override
	public String getFilterDescription() {
		return "Multiplies each map by the given multiplier";
	}

	@Override
	public MapFilterDescriptor getFilterDescriptor() {
		return MapFilterDescriptor.MATH;
	}

	@Override
	public void initialize() {
		chosenElement = new SelectionParameter<Element>(
				"Element", 
				new DropDownStyle<>(), 
				Element.Fe, 
				new EnumClassInfo<>(Element.class),
				this::validate
			);
		chosenElement.setPossibleValues(Element.values());
		addParameter(chosenElement);
		multiplier = new Parameter<>("Multiplier", new RealSpinnerStyle(), 1f, this::validate);
		addParameter(multiplier);
	}
	
	private boolean validate(Parameter<?> param) {
		if (multiplier.getValue() <= 0f) { return false; }
		if (multiplier.getValue() > 1000f) { return false; }
		return true;
	}

	@Override
	public AreaMap filter(AreaMap source) {
		List<Element> elements = source.getElements();
		if (elements.size() != 1) { return source; }
		
		Element element = elements.get(0);	
		if (element != chosenElement.getValue()) { return source; }
		
		return new AreaMap(SpectrumCalculations.multiplyBy(source.getData(), multiplier.getValue()), source);
	}

	@Override
	public boolean isReplottable() {
		return true;
	}

	@Override
	public String pluginVersion() {
		return "0.1";
	}

	@Override
	public String pluginUUID() {
		return "76850b8a-31e9-4759-a65f-70dc89f18492";
	}

}
