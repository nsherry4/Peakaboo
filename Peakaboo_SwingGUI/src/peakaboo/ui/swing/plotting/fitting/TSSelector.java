package peakaboo.ui.swing.plotting.fitting;

import static fava.Fn.filter;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import peakaboo.controller.plotter.FittingController;
import peakaboo.curvefit.peaktable.TransitionSeries;
import peakaboo.curvefit.peaktable.TransitionSeriesMode;
import eventful.swing.EventfulPanel;
import fava.signatures.FunctionMap;


public class TSSelector extends EventfulPanel
{
	
	JComboBox	tsCombo;
	JLabel		tsLabel;
	
	ActionListener tsComboListener;
	
	public TSSelector(FittingController controller)
	{

		setLayout(new BorderLayout());

		tsCombo = new JComboBox(
				filter(controller.getFittedTransitionSeries(), new FunctionMap<TransitionSeries, Boolean>() {

					public Boolean f(TransitionSeries element)
					{
						return element.mode == TransitionSeriesMode.PRIMARY;
					}
				}).toArray()
				);
		
		tsLabel = new JLabel();
		tsLabel.setPreferredSize(tsCombo.getPreferredSize());
		tsLabel.setHorizontalAlignment(JLabel.CENTER);

		
		tsComboListener = new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				updateLabelText();
				updateListeners();
			}
		};
		
		tsCombo.addActionListener(tsComboListener);

		add(tsCombo, BorderLayout.CENTER);


	}


	public void setTransitionSeries(List<TransitionSeries> tss)
	{
		
		tsCombo.removeActionListener(tsComboListener);
		
		tsCombo.removeAllItems();
		
		
		if (tss == null) return;
		
		for (TransitionSeries ts : tss)
		{
			tsCombo.addItem(ts);
		}
		tsCombo.setSelectedIndex(0);
		
		updateLabelText();
		
		tsCombo.addActionListener(tsComboListener);
		
		updateListeners();
		
	}
	
	public TransitionSeries getTransitionSeries()
	{
		return (TransitionSeries) tsCombo.getSelectedItem();
	}
	
	
	@Override
	public void setEnabled(boolean enabled)
	{
		
		this.removeAll();
		
		if (enabled)
		{
			add(tsCombo, BorderLayout.CENTER);
		} else {
			add(tsLabel, BorderLayout.CENTER);
		}
		
		super.setEnabled(enabled);

	}
	
	private void updateLabelText()
	{
		tsLabel.setText(  tsCombo.getSelectedItem().toString()  );
	}

}
