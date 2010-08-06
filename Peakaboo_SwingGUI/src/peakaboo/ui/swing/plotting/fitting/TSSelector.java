package peakaboo.ui.swing.plotting.fitting;

import static fava.Fn.filter;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import peakaboo.controller.plotter.fitting.IFittingController;
import peakaboo.curvefit.peaktable.TransitionSeries;
import peakaboo.curvefit.peaktable.TransitionSeriesMode;
import eventful.swing.EventfulPanel;
import fava.signatures.FunctionMap;


public class TSSelector extends EventfulPanel
{
	
	JComboBox	tsCombo;
	JLabel		tsLabel;
	
	ActionListener tsComboListener;
	
	public TSSelector(IFittingController controller)
	{

		setLayout(new BorderLayout());

		tsCombo = new JComboBox();
		
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
		
		for (TransitionSeries ts : tss) { tsCombo.addItem(ts); }
		if (tss.size() > 0) tsCombo.setSelectedIndex(0);
		
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
		if (tsCombo.getSelectedItem() == null)
		{
			tsLabel.setText("");
		} else {
			tsLabel.setText(  tsCombo.getSelectedItem().toString()  );
		}
	}

}
