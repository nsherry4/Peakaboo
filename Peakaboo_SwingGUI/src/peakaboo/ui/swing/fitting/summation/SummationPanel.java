package peakaboo.ui.swing.fitting.summation;



import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JComboBox;

import peakaboo.controller.plotter.FittingController;
import peakaboo.datatypes.DataTypeFactory;
import peakaboo.datatypes.functional.Function1;
import peakaboo.datatypes.functional.Functional;
import peakaboo.datatypes.functional.stock.Functions;
import peakaboo.datatypes.peaktable.TransitionSeries;
import peakaboo.datatypes.peaktable.TransitionSeriesMode;
import peakaboo.ui.swing.icons.IconSize;
import peakaboo.ui.swing.widgets.ClearPanel;
import peakaboo.ui.swing.widgets.ImageButton;
import peakaboo.ui.swing.widgets.ImageButton.Layout;



public class SummationPanel extends ClearPanel
{

	private FittingController	controller;
	private List<TSSelector>	selectors;
	private ImageButton			addButton;


	public SummationPanel(FittingController controller)
	{
		this.controller = controller;

		setLayout(new GridBagLayout());

		selectors = DataTypeFactory.<TSSelector> list();

		addButton = new ImageButton("add", "Add", Layout.IMAGE, IconSize.BUTTON);
		addButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				addTSSelector();
			}
		});

		resetSelectors();
	}


	protected void removeTSSelector(TSSelector tssel)
	{
		selectors.remove(tssel);
		if (selectors.size() < 2) addTSSelector();
		refreshGUI();
	}


	protected void addTSSelector()
	{
		selectors.add(new TSSelector(controller, this));
		refreshGUI();
	}


	public void resetSelectors()
	{

		selectors.clear();

		selectors.add(new TSSelector(controller, this));
		selectors.add(new TSSelector(controller, this));

		refreshGUI();

	}


	private void refreshGUI()
	{

		removeAll();

		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		c.weighty = 0.0;

		for (TSSelector tss : selectors)
		{
			c.gridy += 1;

			c.gridx = 0;
			c.weightx = 1.0;
			add(tss, c);

			c.gridx = 1;
			c.weightx = 0.0;
			add(removeButtonWidget(tss), c);


		}

		c.gridy++;
		c.gridx = 1;
		add(addButton, c);

		c.gridy++;
		c.gridx = 0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		add(new ClearPanel(), c);

		revalidate();

		TSSelectorUpdated();


	}


	private ImageButton removeButtonWidget(final TSSelector tss)
	{
		ImageButton remove = new ImageButton("remove", "Remove", Layout.IMAGE, IconSize.BUTTON);

		remove.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				removeTSSelector(tss);
			}
		});

		return remove;
	}


	protected void TSSelectorUpdated()
	{
		controller.clearProposedTransitionSeries();
		TransitionSeries ts = getTransitionSeries();
		if (ts == null) return;
		controller.addProposedTransitionSeries(ts);
	}


	public TransitionSeries getTransitionSeries()
	{

		//get a list of all TransitionSeries to be summed
		List<TransitionSeries> tss = Functional.filter(Functional.map(selectors, new Function1<TSSelector, TransitionSeries>() {

			public TransitionSeries f(TSSelector element)
			{
				return element.getTransitionSeries();
			}
		}), Functions.<TransitionSeries>notNull());
		
		return TransitionSeries.summation(tss);

	}
}



class TSSelector extends ClearPanel
{

	JComboBox	tsCombo;


	public TSSelector(FittingController controller, final SummationPanel owner)
	{

		setLayout(new BorderLayout());



		tsCombo = new JComboBox(
				Functional.filter(controller.getFittedTransitionSeries(), new Function1<TransitionSeries, Boolean>() {

					public Boolean f(TransitionSeries element)
					{
						return element.mode == TransitionSeriesMode.PRIMARY;
					}
				}).toArray()
				);

		tsCombo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				owner.TSSelectorUpdated();
			}
		});

		add(tsCombo, BorderLayout.CENTER);


	}


	public TransitionSeries getTransitionSeries()
	{
		return (TransitionSeries) tsCombo.getSelectedItem();
	}

}
