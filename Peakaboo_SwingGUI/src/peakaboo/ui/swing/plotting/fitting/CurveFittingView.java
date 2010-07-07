package peakaboo.ui.swing.plotting.fitting;



import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import peakaboo.controller.plotter.FittingController;
import peakaboo.datatypes.eventful.PeakabooSimpleListener;
import peakaboo.ui.swing.plotting.fitting.fitted.FittingPanel;
import peakaboo.ui.swing.plotting.fitting.summation.SummationPanel;
import peakaboo.ui.swing.plotting.fitting.unfitted.ProposalPanel;
import swidget.widgets.ClearPanel;
import swidget.widgets.Spacing;




public class CurveFittingView extends ClearPanel implements Changeable
{

	protected FittingController		controller;

	private final String			FITTED		= "Fitted";
	private final String			UNFITTED	= "Unfitted";
	private final String			SUMMATION	= "Summation";


	protected FittingPanel			fittedPanel;
	protected ProposalPanel			proposalPanel;
	protected SummationPanel		summationPanel;
	
	
	protected JPanel				cardPanel;
	protected CardLayout			card;
	

	public CurveFittingView(FittingController _controller)
	{
		super();

		this.controller = _controller;

		setPreferredSize(new Dimension(200, getPreferredSize().height));

		fittedPanel = new FittingPanel(controller, this);
		proposalPanel = new ProposalPanel(controller, this);
		summationPanel = new SummationPanel(controller, this);

		cardPanel = createCardPanel(fittedPanel, proposalPanel, summationPanel);

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(cardPanel);

		controller.addListener(new PeakabooSimpleListener() {

			public void change()
			{
				CurveFittingView.this.changed();
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
		card.show(cardPanel, UNFITTED);
		changed();
	}
	
	public void summationAdd()
	{
		summationPanel.resetSelectors();
		card.show(cardPanel, SUMMATION);
		changed();
	}
	
	
	public void dialogClose()
	{
		card.show(cardPanel, FITTED);
		changed();
	}
	


	private JPanel createCardPanel(JPanel t1, JPanel t2, JPanel t3)
	{
		JPanel panel = new ClearPanel();
		card = new CardLayout();
		panel.setLayout(card);

		panel.add(t1, FITTED);
		panel.add(t2, UNFITTED);
		panel.add(t3, SUMMATION);

		return panel;
	}
	
	
	
	
}

