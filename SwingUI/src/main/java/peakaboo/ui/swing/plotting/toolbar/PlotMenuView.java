package peakaboo.ui.swing.plotting.toolbar;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

import eventful.EventfulTypeListener;
import peakaboo.controller.plotter.PlotController;
import peakaboo.controller.plotter.view.ChannelCompositeMode;
import peakaboo.ui.swing.plotting.PlotPanel;

public class PlotMenuView extends JPopupMenu {

	private JCheckBoxMenuItem logPlot, consistentScale, monochrome, raw, fittings;
	private JCheckBoxMenuItem markings, intensities;
	private JRadioButtonMenuItem individual, average, maximum;
	
	private PlotController controller;
	
	public PlotMenuView(PlotPanel plot, PlotController controller) {
		this.controller = controller;

		ButtonGroup viewGroup = new ButtonGroup();

		individual = PlotMenuUtils.createMenuRadioItem(plot,
				ChannelCompositeMode.NONE.show(), 
				null, null, 
				o -> controller.view().setChannelCompositeMode(ChannelCompositeMode.NONE), 
				KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK), 
				KeyEvent.VK_I
			);
		individual.setSelected(controller.view().getChannelCompositeMode() == ChannelCompositeMode.NONE);
		viewGroup.add(individual);
		this.add(individual);
		

		average = PlotMenuUtils.createMenuRadioItem(plot,
				ChannelCompositeMode.AVERAGE.show(), 
				null, null, 
				o -> controller.view().setChannelCompositeMode(ChannelCompositeMode.AVERAGE), 
				KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.CTRL_MASK), 
				KeyEvent.VK_M
			);
		average.setSelected(controller.view().getChannelCompositeMode() == ChannelCompositeMode.AVERAGE);
		viewGroup.add(average);
		this.add(average);
		

		maximum = PlotMenuUtils.createMenuRadioItem(plot,
				ChannelCompositeMode.MAXIMUM.show(), 
				null, null, 
				o -> controller.view().setChannelCompositeMode(ChannelCompositeMode.MAXIMUM),
				KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK), 
				KeyEvent.VK_T
			);
		maximum.setSelected(controller.view().getChannelCompositeMode() == ChannelCompositeMode.MAXIMUM);
		viewGroup.add(maximum);
		this.add(maximum);
		
		
		
		
		
		
		
		
		this.addSeparator();
		
		
		




		
		logPlot = PlotMenuUtils.createMenuCheckItem(plot,
				"Logarithmic Scale", 
				null, 
				"Toggles the plot between a linear and logarithmic scale",
				controller.view()::setViewLog,
				KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK), 
				KeyEvent.VK_L
		);

		consistentScale = PlotMenuUtils.createMenuCheckItem(plot,
				"Consistent Scale", 
				null, 
				"All spectra in a dataset will be displayed with a consisntent scale",
				controller.view()::setConsistentScale,
				null, 
				null
		);
				
		fittings = PlotMenuUtils.createMenuCheckItem(plot,
				"Individual Fittings", 
				null, 
				"Switches between showing all fittings as a single curve and showing all fittings individually",
				controller.view()::setShowIndividualSelections,
				null, 
				KeyEvent.VK_O
		);

		markings = PlotMenuUtils.createMenuCheckItem(plot,
				"Transition Lines", 
				null, 
				"Label fittings with lines denoting their transition energies",
				controller.view()::setShowElementMarkers,
				null, null
		);

		intensities = PlotMenuUtils.createMenuCheckItem(plot,
				"Fitting Intensities", 
				null, 
				"Label fittings with their intensities",
				controller.view()::setShowElementIntensities,
				null, 
				null
		);
	
		raw = PlotMenuUtils.createMenuCheckItem(plot,
				"Raw Data Outline", 
				null, 
				"Toggles an outline of the original raw data",
				controller.view()::setShowRawData,
				null, 
				KeyEvent.VK_O
		);		

		monochrome = PlotMenuUtils.createMenuCheckItem(plot,
				"Monochrome", 
				null, 
				"Toggles the monochrome colour palette",
				controller.view()::setMonochrome,
				null, 
				KeyEvent.VK_M
		);
		


		this.add(logPlot);
		this.add(consistentScale);
		this.add(fittings);
		this.add(markings);
		this.add(intensities);
		this.add(raw);
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
