package peakaboo.ui.swing.plotting.fitting;



import java.awt.CardLayout;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import eventful.EventfulTypeListener;
import peakaboo.controller.plotter.IPlotController;
import peakaboo.controller.plotter.fitting.IFittingController;
import peakaboo.ui.swing.plotting.PlotCanvas;
import peakaboo.ui.swing.plotting.fitting.fitted.FittingPanel;
import peakaboo.ui.swing.plotting.fitting.guidedfitting.GuidedFittingPanel;
import peakaboo.ui.swing.plotting.fitting.lookup.LookupPanel;
import peakaboo.ui.swing.plotting.fitting.summation.SummationPanel;
import swidget.widgets.ClearPanel;




public class CurveFittingView extends ClearPanel implements Changeable
{

	protected IFittingController	controller;
	private IPlotController 		plotController;

	private final String			FITTED		= "Fitted";
	private final String			LOOKUP		= "Lookup";
	private final String			SUMMATION	= "Summation";
	private final String			SMART		= "Smart";


	protected FittingPanel			fittedPanel;
	protected LookupPanel			proposalPanel;
	protected SummationPanel		summationPanel;
	protected GuidedFittingPanel		smartPanel;
	
	
	protected JPanel				cardPanel;
	protected CardLayout			card;

	
	

	public CurveFittingView(IFittingController _controller, IPlotController plotController, PlotCanvas canvas)
	{
		super();

		this.controller = _controller;
		this.plotController = plotController;

		setPreferredSize(new Dimension(200, getPreferredSize().height));

		fittedPanel = new FittingPanel(controller, this);
		proposalPanel = new LookupPanel(controller, this);
		summationPanel = new SummationPanel(controller, this);
		smartPanel = new GuidedFittingPanel(controller, this, canvas);

		cardPanel = createCardPanel();

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(cardPanel);

		
		controller.addListener(new EventfulTypeListener<Boolean>() {

			public void change(Boolean b)
			{
				//b will be true if the fitting model has been changed in some way
				//other than through the FittingController (ie load session, undo, etc)
				if (b) {
					changed();
				}
			}
		});

	}


	@Override
	public String getName()
	{
		return "Peak Fitting";
	}

	
	


	public void changed()
	{
		fittedPanel.changed();
		proposalPanel.changed();
	}

	public void elementalAdd()
	{
		card.show(cardPanel, LOOKUP);
		changed();
	}
	
	public void summationAdd()
	{
		summationPanel.setActive(true);
		summationPanel.resetSelectors();
		card.show(cardPanel, SUMMATION);
		changed();
	}
	
	public void guidedAdd()
	{
		smartPanel.resetSelectors();
		smartPanel.setSelectionMode(true);
		card.show(cardPanel, SMART);
		changed();
	}
	
	public void smartAdd() {
		if (plotController.data().hasDataSet() && plotController.settings().getMaxEnergy() > 0f) {
			guidedAdd();
		} else {
			elementalAdd();
		}
	}

	
	
	public void dialogClose()
	{
		card.show(cardPanel, FITTED);
		smartPanel.setSelectionMode(false);
		summationPanel.setActive(false);
		changed();
	}
	


	private JPanel createCardPanel()
	{
		JPanel panel = new ClearPanel();
		card = new CardLayout();
		panel.setLayout(card);

		panel.add(fittedPanel, FITTED);
		panel.add(proposalPanel, LOOKUP);
		panel.add(summationPanel, SUMMATION);
		panel.add(smartPanel, SMART);

		return panel;
	}
	
	
	
	
}

