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

	public PlotMenuView(PlotPanel plot, PlotController controller) {
	

		final JMenuItem logPlot, monochrome, raw, fittings;

		
		logPlot = PlotMenuUtils.createMenuCheckItem(plot,
				"Logarithmic Scale", null, "Toggles the plot between a linear and logarithmic scale",
				b -> {
					controller.view().setViewLog(b);
				},
				KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK), KeyEvent.VK_L
		);
		logPlot.setSelected(controller.view().getViewLog());
		

		monochrome = PlotMenuUtils.createMenuCheckItem(plot,
				"Monochrome", null, "Toggles the monochrome colour palette",
				b -> {
					controller.view().setMonochrome(b);
				},
				null, KeyEvent.VK_M
		);
		
		

		raw = PlotMenuUtils.createMenuCheckItem(plot,
				"Raw Data Outline", null, "Toggles an outline of the original raw data",
				b -> {
					controller.view().setShowRawData(b);
				},
				null, KeyEvent.VK_O
		);
		
		fittings = PlotMenuUtils.createMenuCheckItem(plot,
				"Individual Fittings", null, "Switches between showing all fittings as a single curve and showing all fittings individually",
				b -> {
					controller.view().setShowIndividualSelections(b);
				},
				null, KeyEvent.VK_O
		);	
		
		this.add(logPlot);
		this.add(monochrome);
		
		this.add(raw);
		this.add(fittings);


		// Element Drawing submenu
		final JCheckBoxMenuItem emarkings, eintensities;

		
		emarkings = PlotMenuUtils.createMenuCheckItem(plot,
				"Transition Markings", null, "Label fittings with lines denoting their transition energies",
				b -> {
					controller.view().setShowElementMarkers(b);
				},
				null, null
		);
		this.add(emarkings);

		
		eintensities = PlotMenuUtils.createMenuCheckItem(plot,
				"Fitting Intensities", null, "Label fittings with their intensities",
				b -> {
					controller.view().setShowElementIntensities(b);
				},
				null, null
		);
		this.add(eintensities);


		this.addSeparator();
		
		JCheckBoxMenuItem consistentScale = PlotMenuUtils.createMenuCheckItem(plot,
				"Use Consistent Scale", null, "All spectra in a dataset will be displayed with a consisntent scale",
				b -> {
					controller.view().setConsistentScale(b);
				},
				null, null
		);
		consistentScale.setSelected(controller.view().getConsistentScale());
		this.add(consistentScale);
		
		
		this.addSeparator();
		


		final JRadioButtonMenuItem individual, average, maximum;

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
		

		
		controller.addListener(new EventfulTypeListener<String>() {

			public void change(String s)
			{

				logPlot.setSelected(controller.view().getViewLog());
				monochrome.setSelected(controller.view().getMonochrome());

				emarkings.setSelected(controller.view().getShowElementMarkers());
				eintensities.setSelected(controller.view().getShowElementIntensities());

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

				raw.setSelected(controller.view().getShowRawData());
				fittings.setSelected(controller.view().getShowIndividualSelections());

			}
		});
		
	}

	public void setWidgetState(boolean hasData) {
		// TODO Auto-generated method stub
		
	}
	
}
