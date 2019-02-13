package org.peakaboo.ui.swing.mapping.components;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.controller.mapper.settings.MapSettingsController;
import org.peakaboo.ui.swing.Peakaboo;

public class MapMenuView extends JPopupMenu {

	private JMenuItem title, spectrum, coords, dstitle, scalebar;
	private JCheckBoxMenuItem monochrome;
	
	public MapMenuView(MappingController controller) {
		
		title = new JCheckBoxMenuItem("Show Elements List");
		dstitle = new JCheckBoxMenuItem("Show Dataset Title");
		spectrum = new JCheckBoxMenuItem("Show Spectrum");
		coords = new JCheckBoxMenuItem("Show Coordinates");
		scalebar = new JCheckBoxMenuItem("Show Scale Bar");
		monochrome = new JCheckBoxMenuItem("Monochrome");
		

		MapSettingsController viewSettings = controller.getSettings();
		title.setSelected(viewSettings.getShowTitle());
		spectrum.setSelected(viewSettings.getShowSpectrum());
		coords.setSelected(viewSettings.getShowCoords());
		dstitle.setSelected(viewSettings.getShowDatasetTitle());
		scalebar.setSelected(viewSettings.getShowScaleBar());

		spectrum.addActionListener(e -> viewSettings.setShowSpectrum(spectrum.isSelected()));
		coords.addActionListener(e -> viewSettings.setShowCoords(coords.isSelected()));
		title.addActionListener(e -> viewSettings.setShowTitle(title.isSelected()));
		dstitle.addActionListener(e -> viewSettings.setShowDatasetTitle(dstitle.isSelected()));
		scalebar.addActionListener(e -> viewSettings.setShowScaleBar(scalebar.isSelected()));
		monochrome.addActionListener(e -> viewSettings.setMonochrome(monochrome.isSelected()));
		
		
		this.add(title);
		this.add(dstitle);
		this.add(spectrum);
		this.add(coords);
		this.add(scalebar);
		this.addSeparator();
		this.add(monochrome);
		
		controller.addListener(s -> {
			monochrome.setSelected(controller.getSettings().getMonochrome());
			spectrum.setSelected(controller.getSettings().getShowSpectrum());
			coords.setSelected(controller.getSettings().getShowCoords());
		});
		
	}
	
}
