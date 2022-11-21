package org.peakaboo.ui.swing.plotting.toolbar;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.controller.plotter.view.ChannelCompositeMode;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.menuitem.FluentCheckMenuItem;
import org.peakaboo.framework.stratus.components.ui.options.OptionBlock;
import org.peakaboo.framework.stratus.components.ui.options.OptionBlocksPanel;
import org.peakaboo.framework.stratus.components.ui.options.OptionCheckBox;
import org.peakaboo.framework.stratus.components.ui.options.OptionRadioButton;
import org.peakaboo.ui.swing.plotting.PlotPanel;

public class PlotMenuView extends JPopupMenu {

	private OptionRadioButton oInd, oAvg, oMax;
	private OptionCheckBox oLog, oConsist, oFit, oMarks, oIntens, oMono, oRaw, oTitle;
	
	private PlotController controller;
	
	public PlotMenuView(PlotPanel plot, PlotController controller) {
		this.controller = controller;
		
		
		OptionBlock compositeBlock = new OptionBlock().withDividers(false);
		ButtonGroup compositeGroup = new ButtonGroup();
		
		oInd = new OptionRadioButton(compositeBlock, compositeGroup)
				.withTitle(ChannelCompositeMode.NONE.show())
				.withTooltip("Shows one spectrum at a time")
				.withKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK), plot)
				.withSelection(controller.view().getChannelCompositeMode() == ChannelCompositeMode.NONE)
				.withListener(() -> controller.view().setChannelCompositeMode(ChannelCompositeMode.NONE));
		compositeBlock.add(oInd);
		compositeGroup.add(oInd.getButton());
		
		oAvg = new OptionRadioButton(compositeBlock, compositeGroup)
				.withTitle(ChannelCompositeMode.AVERAGE.show())
				.withTooltip("Shows an average of all spectra")
				.withKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.CTRL_MASK), plot)
				.withSelection(controller.view().getChannelCompositeMode() == ChannelCompositeMode.AVERAGE)
				.withListener(() -> controller.view().setChannelCompositeMode(ChannelCompositeMode.AVERAGE));
		compositeBlock.add(oAvg);
		compositeGroup.add(oAvg.getButton());
		
		oMax = new OptionRadioButton(compositeBlock, compositeGroup)
				.withTitle(ChannelCompositeMode.MAXIMUM.show())
				.withTooltip("Shows the maximum counts per channel across all spectra")
				.withKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK), plot)
				.withSelection(controller.view().getChannelCompositeMode() == ChannelCompositeMode.MAXIMUM)
				.withListener(() -> controller.view().setChannelCompositeMode(ChannelCompositeMode.MAXIMUM));
		compositeBlock.add(oMax);
		compositeGroup.add(oMax.getButton());
		

		
		
		OptionBlock scaleBlock = new OptionBlock().withDividers(false).withBorder(false);
		
		oLog = new OptionCheckBox(scaleBlock)
				.withTitle("Logarithmic Scale")
				.withKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK), plot)
				.withSelection(controller.view().getViewLog())
				.withTooltip("Toggles the plot between a linear and logarithmic scale")
				.withListener(controller.view()::setViewLog);
		scaleBlock.add(oLog);
		
		
		oConsist = new OptionCheckBox(scaleBlock)
				.withTitle("Consistent Scale")
				.withTooltip("All spectra in a dataset will be displayed with a consisntent scale")
				.withSelection(controller.view().getConsistentScale())
				.withListener(controller.view()::setConsistentScale);
		scaleBlock.add(oConsist);
		
		
		
		OptionBlock viewBlock = new OptionBlock().withDividers(false).withBorder(false);
		
		

		
		
				
		oFit = new OptionCheckBox(viewBlock)
				.withTitle("Individual Fittings")
				.withTooltip("Switches between showing all fittings as a single curve and showing all fittings individually")
				.withSelection(controller.view().getShowIndividualSelections())
				.withListener(controller.view()::setShowIndividualSelections);
		viewBlock.add(oFit);
		

		oMarks = new OptionCheckBox(viewBlock)
				.withTitle("Transition Lines")
				.withTooltip("Label fittings with lines denoting their transition energies")
				.withSelection(controller.view().getShowElementMarkers())
				.withListener(controller.view()::setShowElementMarkers);
		viewBlock.add(oMarks);

		
		oIntens = new OptionCheckBox(viewBlock)
				.withTitle("Fitting Intensities")
				.withTooltip("Label fittings with their intensities")
				.withSelection(controller.view().getShowElementIntensities())
				.withListener(controller.view()::setShowElementIntensities);
		viewBlock.add(oIntens);
	
		
		oRaw = new OptionCheckBox(viewBlock)
				.withTitle("Raw Data Outline")
				.withTooltip("Toggles an outline of the original raw data")
				.withSelection(controller.view().getShowRawData())
				.withListener(controller.view()::setShowRawData);
		viewBlock.add(oRaw);

		
		oTitle = new OptionCheckBox(viewBlock)
				.withTitle("Show Dataset Title")
				.withTooltip("Toggles showing the dataset title in the plot")
				.withSelection(controller.view().getShowTitle())
				.withListener(controller.view()::setShowTitle);
		viewBlock.add(oTitle);
		
		
		oMono = new OptionCheckBox(viewBlock)
				.withTitle("Monochrome")
				.withTooltip("Toggles the monochrome colour palette")
				.withSelection(controller.view().getMonochrome())
				.withListener(controller.view()::setMonochrome);
		viewBlock.add(oMono);


		
		OptionBlocksPanel compositePanel = new OptionBlocksPanel(compositeBlock, scaleBlock, viewBlock);
		this.add(compositePanel);
		
		updateWidgetValues();
		controller.addListener(s -> updateWidgetValues());
		
	}

	private void updateWidgetValues() {
		
		oLog.setSelected(controller.view().getViewLog());
		oMono.setSelected(controller.view().getMonochrome());
		oMarks.setSelected(controller.view().getShowElementMarkers());
		oIntens.setSelected(controller.view().getShowElementIntensities());
		oRaw.setSelected(controller.view().getShowRawData());
		oFit.setSelected(controller.view().getShowIndividualSelections());
		oConsist.setSelected(controller.view().getConsistentScale());

		switch (controller.view().getChannelCompositeMode())
		{

			case NONE:
				oInd.setSelected(true);
				break;
			case AVERAGE:
				oAvg.setSelected(true);
				break;
			case MAXIMUM:
				oMax.setSelected(true);
				break;
		}
		
	}
	
	public void setWidgetState(boolean hasData) {
		//View controls don't get disabled when there's no dataset present
	}
	
}
