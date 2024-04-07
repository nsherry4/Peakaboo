package org.peakaboo.ui.swing.mapping.components;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;

import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.controller.mapper.settings.MapSettingsController;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.menu.FluentCheckMenuItem;

public class MapMenuView extends JPopupMenu {

	private FluentCheckMenuItem title, spectrum, coords, dstitle, scalebar, monochrome;
	
	public MapMenuView(MappingController controller) {
		
		MapSettingsController settings = controller.getSettings();
		
		title = new FluentCheckMenuItem("Show Elements List")
				.withSelected(settings.getShowTitle())
				.withAction(settings::setShowTitle);
		
		dstitle = new FluentCheckMenuItem("Show Dataset Title")
				.withSelected(settings.getShowDatasetTitle())
				.withAction(settings::setShowDatasetTitle);
		
		spectrum = new FluentCheckMenuItem("Show Spectrum")
				.withSelected(settings.getShowSpectrum())
				.withAction(settings::setShowSpectrum);
				
		coords = new FluentCheckMenuItem("Show Coordinates")
				.withSelected(settings.getShowCoords())
				.withAction(settings::setShowCoords);
		
		scalebar = new FluentCheckMenuItem("Show Scale Bar")
				.withSelected(settings.getShowScaleBar())
				.withAction(settings::setShowScaleBar);
		
		monochrome = new FluentCheckMenuItem("Monochrome")
				.withSelected(settings.getMonochrome())
				.withAction(settings::setMonochrome);

		
		this.add(title);
		this.add(dstitle);
		this.add(spectrum);
		this.add(coords);
		this.add(scalebar);
		this.addSeparator();
		this.add(monochrome);
		
		controller.addListener(t -> {
			monochrome.setSelected(controller.getSettings().getMonochrome());
			spectrum.setSelected(controller.getSettings().getShowSpectrum());
			coords.setSelected(controller.getSettings().getShowCoords());
		});
		
	}
	
}
