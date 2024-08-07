package org.peakaboo.ui.swing.plotting.toolbar;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.controller.plotter.view.mode.ChannelViewMode;
import org.peakaboo.controller.plotter.view.mode.ChannelViewModeRegistry;
import org.peakaboo.controller.plotter.view.mode.MaximumViewMode;
import org.peakaboo.controller.plotter.view.mode.SingleViewMode;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.components.panels.ClearPanel;
import org.peakaboo.framework.stratus.components.ui.options.OptionBlock;
import org.peakaboo.framework.stratus.components.ui.options.OptionBlocksPanel;
import org.peakaboo.framework.stratus.components.ui.options.OptionCheckBox;
import org.peakaboo.framework.stratus.components.ui.options.OptionRadioButton;
import org.peakaboo.ui.swing.plotting.PlotPanel;

public class PlotMenuView extends JPopupMenu {

	private OptionRadioButton oInd, oAvg, oMax;
	private Map<ChannelViewMode, OptionRadioButton> scanmodes = new LinkedHashMap<>();
	private OptionCheckBox oLog, oConsist, oFit, oMarks, oIntens, oMono, oRaw, oTitle;
	
	private PlotController controller;
	
	public PlotMenuView(PlotPanel plot, PlotController controller) {
		this.controller = controller;
		
		
		OptionBlock compositeBlock = new OptionBlock().withDividers(false);
		ButtonGroup compositeGroup = new ButtonGroup();
		
		ChannelViewMode viewSingle = new SingleViewMode();
		oInd = new OptionRadioButton(compositeBlock, compositeGroup)
				.withTitle(viewSingle.name())
				.withDescription(viewSingle.description())
				.withTooltip(viewSingle.tooltip())
				.withKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK), plot)
				.withSelection(controller.view().getChannelViewMode().equals(viewSingle))
				.withListener(() -> controller.view().setChannelViewMode(viewSingle));
		compositeBlock.add(oInd);
		compositeGroup.add(oInd.getButton());
		scanmodes.put(viewSingle, oInd);
		
		ChannelViewMode viewAverage = ChannelViewModeRegistry.system().getPresetInstance();
		oAvg = new OptionRadioButton(compositeBlock, compositeGroup)
				.withTitle(viewAverage.name())
				.withDescription(viewAverage.description())
				.withTooltip(viewAverage.tooltip())
				.withKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.CTRL_MASK), plot)
				.withSelection(controller.view().getChannelViewMode().equals(viewAverage))
				.withListener(() -> controller.view().setChannelViewMode(viewAverage));
		compositeBlock.add(oAvg);
		compositeGroup.add(oAvg.getButton());
		scanmodes.put(viewAverage, oAvg);
		
		ChannelViewMode viewMax = new MaximumViewMode();
		oMax = new OptionRadioButton(compositeBlock, compositeGroup)
				.withTitle(viewMax.name())
				.withDescription(viewMax.description())
				.withTooltip(viewMax.tooltip())
				.withKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK), plot)
				.withSelection(controller.view().getChannelViewMode().equals(viewMax))
				.withListener(() -> controller.view().setChannelViewMode(viewMax));
		compositeBlock.add(oMax);
		compositeGroup.add(oMax.getButton());
		scanmodes.put(viewMax, oMax);
		
		
		int customViews = 0;
		for (var proto : ChannelViewModeRegistry.system().getPlugins()) {
			var created = proto.create();
			if (created.isEmpty()) {
				continue;
			}
			var viewmode = created.get();
			if (scanmodes.keySet().contains(viewmode)) { continue; }
			
			var oMode = new OptionRadioButton(compositeBlock, compositeGroup)
					.withTitle(viewmode.name())
					.withDescription(viewmode.description())
					.withTooltip(viewmode.tooltip())
					.withSelection(controller.view().getChannelViewMode().equals(viewmode))
					.withListener(() -> controller.view().setChannelViewMode(viewmode));
			compositeBlock.add(oMode);
			compositeGroup.add(oMode.getButton());
			scanmodes.put(viewmode, oMode);
			customViews++;
			
		}
		
		
		
		
		OptionBlock scaleBlock = new OptionBlock().withDividers(false).withBorder(false);
		OptionBlock viewBlock = new OptionBlock().withDividers(false).withBorder(false);
		
		
		boolean splitColumn = customViews < 1;
		OptionBlock block = splitColumn ? scaleBlock : viewBlock;
		
		oLog = new OptionCheckBox(block)
				.withTitle("Logarithmic Scale")
				.withKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK), plot)
				.withSelection(controller.view().getViewLog())
				.withTooltip("Toggles the plot between a linear and logarithmic scale")
				.withListener(controller.view()::setViewLog);
		block.add(oLog);
		
		
		oConsist = new OptionCheckBox(block)
				.withTitle("Consistent Scale")
				.withTooltip("All spectra in a dataset will be displayed with a consisntent scale")
				.withSelection(controller.view().getConsistentScale())
				.withListener(controller.view()::setConsistentScale);
		block.add(oConsist);
		

				
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


		
		OptionBlocksPanel leftPanel = splitColumn ? new OptionBlocksPanel(compositeBlock, scaleBlock) : new OptionBlocksPanel(compositeBlock);
		OptionBlocksPanel rightPanel = new OptionBlocksPanel(viewBlock);
		ClearPanel twoPaneMenu = new ClearPanel(new BorderLayout());
		twoPaneMenu.add(leftPanel, BorderLayout.WEST);
		twoPaneMenu.add(rightPanel, BorderLayout.EAST);
		
		JLabel energyTitle = new JLabel("Plot Display Options");
		energyTitle.setHorizontalAlignment(SwingConstants.CENTER);
		energyTitle.setFont(energyTitle.getFont().deriveFont(Font.BOLD));
		energyTitle.setBorder(new EmptyBorder(Spacing.huge, Spacing.huge, Spacing.small, Spacing.huge));
		twoPaneMenu.add(energyTitle, BorderLayout.NORTH);
		
		
		this.add(twoPaneMenu);
		
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

		scanmodes.get(controller.view().getChannelViewMode()).setSelected(true);

	}
	
	public void setWidgetState(boolean hasData) {
		//View controls don't get disabled when there's no dataset present
	}
	
}
