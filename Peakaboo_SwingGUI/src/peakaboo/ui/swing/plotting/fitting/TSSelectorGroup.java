package peakaboo.ui.swing.plotting.fitting;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import peakaboo.controller.plotter.FittingController;
import peakaboo.datatypes.DataTypeFactory;
import peakaboo.datatypes.peaktable.TransitionSeries;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.ClearPanel;
import swidget.widgets.ImageButton;
import swidget.widgets.ImageButton.Layout;
import eventful.EventfulListener;
import fava.Fn;
import fava.signatures.FunctionEach;


public abstract class TSSelectorGroup extends ClearPanel
{

	protected FittingController controller;

	protected List<TSSelector>	selectors;
	protected ImageButton		addButton;
	
	private int					minSelectors;
	
	public TSSelectorGroup(FittingController controller, int minimumSelectors)
	{
		this.controller = controller;
		this.minSelectors = minimumSelectors;
		
		setLayout(new GridBagLayout());
		
		addButton = new ImageButton(StockIcon.EDIT_ADD, "Add", Layout.IMAGE, IconSize.BUTTON);
		addButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				addTSSelector();
			}
		});
				
		selectors = DataTypeFactory.<TSSelector> list();
		
		
	}
	
	
	protected abstract void refreshGUI();
	public abstract void setTransitionSeriesOptions(final List<TransitionSeries> tss);
	public abstract List<TransitionSeries> getTransitionSeries();
	
	protected void removeTSSelector(TSSelector tssel)
	{
		selectors.remove(tssel);
		if (selectors.size() < minSelectors) addTSSelector();
		refreshGUI();
	}

	
	protected void addTSSelector()
	{
		
		TSSelector sel = new TSSelector(controller);
		
		sel.addListener(new EventfulListener() {
			
			public void change()
			{
				TSSelectorUpdated();
			}
		});
		
		selectors.add(sel);
		
		refreshGUI();
	}
	
	
	
	protected ImageButton createRemoveButton(final TSSelector tss)
	{
		ImageButton remove = new ImageButton(StockIcon.EDIT_DELETE, "Remove", Layout.IMAGE, IconSize.BUTTON);

		remove.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				removeTSSelector(tss);
			}
		});

		return remove;
	}
	
	
	public final void resetSelectors()
	{

		selectors.clear();

		System.out.println("reseted");
		
		for (int i = 0; i < minSelectors; i++)
		{
			addTSSelector();	
		}

	}
	
	protected final void TSSelectorUpdated()
	{
		controller.clearProposedTransitionSeries();
		List<TransitionSeries> tss = getTransitionSeries();
		if (tss == null) return;
		
		//add all of the transition series that come back from the summation widget
		Fn.each(tss, new FunctionEach<TransitionSeries>() {

			public void f(TransitionSeries ts)
			{
				if (ts != null) controller.addProposedTransitionSeries(ts);
			}
		});
		
	}

	
	
}
