package peakaboo.ui.swing.plotting.fitting.guidedfitting;



import static java.util.stream.Collectors.toList;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import peakaboo.controller.plotter.fitting.FittingController;
import peakaboo.curvefit.peak.transition.LegacyTransitionSeries;
import peakaboo.ui.swing.plotting.fitting.TSSelector;
import peakaboo.ui.swing.plotting.fitting.TSSelectorGroup;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.ClearPanel;
import swidget.widgets.buttons.ImageButton;
import swidget.widgets.buttons.ImageButtonLayout;



class GuidedFittingWidget extends TSSelectorGroup
{

	private int	activeIndex;
	
	private List<ImageButton> editButtons;


	public GuidedFittingWidget(FittingController controller)
	{
		super(controller, 1);
		
		editButtons = new ArrayList<ImageButton>();
		
		resetSelectors(true);
		activeIndex = 0;

		refreshGUI();
		
	}
	

	@Override
	public List<LegacyTransitionSeries> getTransitionSeries()
	{
		return selectors.stream().map(s -> s.getTransitionSeries()).collect(toList());
	}

	public LegacyTransitionSeries getActiveTransitionSeries()
	{
		return selectors.get(activeIndex).getTransitionSeries();		
	}
	

	@Override
	public void setTransitionSeriesOptions(final List<LegacyTransitionSeries> tss)
	{
		selectors.get(activeIndex).setTransitionSeries(tss);
	}
	
	
	@Override
	protected void refreshGUI()
	{

		removeAll();

		editButtons.clear();
		
		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		c.weighty = 0.0;

		TSSelector selector;
		for (int i = 0; i < selectors.size(); i++)
		{
			
			selector = selectors.get(i);
			
			c.gridy += 1;

			c.gridx = 0;
			c.weightx = 1.0;
			selector.setEnabled(i == activeIndex);
			add(selector, c);

			c.gridx = 1;
			c.weightx = 0.0;
			ImageButton edit = createEditButton(selector, i);
			editButtons.add(edit);
			edit.setEnabled(! (i == activeIndex));
			add(edit, c);
			
			c.gridx = 2;
			c.weightx = 0.0;
			add(createRemoveButton(selector), c);

		}

		c.gridy++;
		c.gridx = 2;
		add(addButton, c);
		
		

		c.gridy++;
		c.gridx = 0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		add(new ClearPanel(), c);

		
		revalidate();
		
		
		TSSelectorUpdated(true);


	}

	@Override
	protected TSSelector addTSSelector(boolean active)
	{	
		activeIndex = selectors.size();
		return super.addTSSelector(active);
	}
	
	@Override
	protected void removeTSSelector(TSSelector tssel)
	{
		if (selectors.get(activeIndex) == tssel || activeIndex == selectors.size()-1)
		{
			activeIndex = Math.max(0, selectors.size() - 2);
		}
		
		super.removeTSSelector(tssel);
	}

	private ImageButton createEditButton(final TSSelector selector, final int index)
	{

		final ImageButton edit = new ImageButton(StockIcon.EDIT_EDIT, IconSize.BUTTON)
				.withTooltip("Edit this fitting")
				.withLayout(ImageButtonLayout.IMAGE)
				.withBordered(false);

		edit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				activeIndex = index;
				disableAllSelectors();
				selector.setEnabled(true);
				edit.setEnabled(false);
			}
		});

		return edit;

	}


	private void disableAllSelectors()
	{
		for (TSSelector selector : selectors)
		{
			selector.setEnabled(false);
		}
		
		for (ImageButton edit : editButtons)
		{
			edit.setEnabled(true);
		}
		
	}



}
