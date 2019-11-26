package org.peakaboo.ui.swing.plotting.toolbar;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.controller.plotter.view.ChannelCompositeMode;
import org.peakaboo.framework.swidget.widgets.buttons.components.menuitem.SwidgetCheckMenuItem;
import org.peakaboo.framework.swidget.widgets.buttons.components.menuitem.SwidgetRadioMenuItem;
import org.peakaboo.ui.swing.plotting.PlotPanel;

public class PlotMenuView extends JPopupMenu {

	private JCheckBoxMenuItem logPlot, consistentScale, monochrome, raw, title, fittings;
	private JCheckBoxMenuItem markings, intensities;
	private JRadioButtonMenuItem individual, average, maximum;
	
	private PlotController controller;
	
	public PlotMenuView(PlotPanel plot, PlotController controller) {
		this.controller = controller;

		ButtonGroup viewGroup = new ButtonGroup();

		individual = new SwidgetRadioMenuItem()
				.withText(ChannelCompositeMode.NONE.show())
				.withKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK), plot)
				.withMnemonic(KeyEvent.VK_I)
				.withAction(() -> controller.view().setChannelCompositeMode(ChannelCompositeMode.NONE));
		individual.setSelected(controller.view().getChannelCompositeMode() == ChannelCompositeMode.NONE);
		viewGroup.add(individual);
		this.add(individual);
		

		average = new SwidgetRadioMenuItem()
				.withText(ChannelCompositeMode.AVERAGE.show())
				.withKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.CTRL_MASK), plot)
				.withMnemonic(KeyEvent.VK_M)
				.withAction(() -> controller.view().setChannelCompositeMode(ChannelCompositeMode.AVERAGE));
		average.setSelected(controller.view().getChannelCompositeMode() == ChannelCompositeMode.AVERAGE);
		viewGroup.add(average);
		this.add(average);
		

		maximum = new SwidgetRadioMenuItem()
				.withText(ChannelCompositeMode.MAXIMUM.show())
				.withKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK), plot)
				.withMnemonic(KeyEvent.VK_T)
				.withAction(() -> controller.view().setChannelCompositeMode(ChannelCompositeMode.MAXIMUM));
		maximum.setSelected(controller.view().getChannelCompositeMode() == ChannelCompositeMode.MAXIMUM);
		viewGroup.add(maximum);
		this.add(maximum);
		
		
		
		
		
		
		
		
		this.addSeparator();
		
		
		




		
		logPlot = new SwidgetCheckMenuItem()
				.withText("Logarithmic Scale")
				.withTooltip("Toggles the plot between a linear and logarithmic scale")
				.withKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK), plot)
				.withMnemonic(KeyEvent.VK_L)
				.withAction(controller.view()::setViewLog);

		consistentScale = new SwidgetCheckMenuItem()
				.withText("Consistent Scale")
				.withTooltip("All spectra in a dataset will be displayed with a consisntent scale")
				.withAction(controller.view()::setConsistentScale);
				
		fittings = new SwidgetCheckMenuItem()
				.withText("Individual Fittings")
				.withTooltip("Switches between showing all fittings as a single curve and showing all fittings individually")
				.withMnemonic(KeyEvent.VK_F)
				.withAction(controller.view()::setShowIndividualSelections);

		markings = new SwidgetCheckMenuItem()
				.withText("Transition Lines")
				.withTooltip("Label fittings with lines denoting their transition energies")
				.withAction(controller.view()::setShowElementMarkers);

		intensities = new SwidgetCheckMenuItem()
				.withText("Fitting Intensities")
				.withTooltip("Label fittings with their intensities")
				.withAction(controller.view()::setShowElementIntensities);
	
		raw = new SwidgetCheckMenuItem()
				.withText("Raw Data Outline")
				.withTooltip("Toggles an outline of the original raw data")
				.withMnemonic(KeyEvent.VK_R)
				.withAction(controller.view()::setShowRawData);

		title = new SwidgetCheckMenuItem()
				.withText("Show Dataset Title")
				.withTooltip("Toggles showing the dataset title in the plot")
				.withAction(controller.view()::setShowTitle);
		
		monochrome = new SwidgetCheckMenuItem()
				.withText("Monochrome")
				.withTooltip("Toggles the monochrome colour palette")
				.withMnemonic(KeyEvent.VK_M)
				.withAction(controller.view()::setMonochrome);

		this.add(logPlot);
		this.add(consistentScale);
		this.add(fittings);
		this.add(markings);
		this.add(intensities);
		this.add(raw);
		this.add(title);
		this.add(monochrome);

		
		updateWidgetValues();
		controller.addListener(s -> {
			updateWidgetValues();
		});
		
	}

	private void updateWidgetValues() {
		
		logPlot.setSelected(controller.view().getViewLog());
		monochrome.setSelected(controller.view().getMonochrome());
		markings.setSelected(controller.view().getShowElementMarkers());
		intensities.setSelected(controller.view().getShowElementIntensities());
		raw.setSelected(controller.view().getShowRawData());
		fittings.setSelected(controller.view().getShowIndividualSelections());
		consistentScale.setSelected(controller.view().getConsistentScale());

		switch (controller.view().getChannelCompositeMode())
		{

			case NONE:
				individual.setSelected(true);
				break;
			case AVERAGE:
				average.setSelected(true);
				break;
			case MAXIMUM:
				maximum.setSelected(true);
				break;
		}
		
	}
	
	public void setWidgetState(boolean hasData) {
		//View controls don't get disabled when there's no dataset present
	}
	
}
