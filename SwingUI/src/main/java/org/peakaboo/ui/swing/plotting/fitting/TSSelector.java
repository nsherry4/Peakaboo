package org.peakaboo.ui.swing.plotting.fitting;


import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.framework.eventful.EventfulBeacon;
import org.peakaboo.framework.eventful.EventfulListener;
import org.peakaboo.framework.eventful.IEventfulBeacon;


public class TSSelector extends JPanel implements IEventfulBeacon {
	
	//EVENTFUL
	private EventfulBeacon listenee;
	
	public void addListener(EventfulListener l) {
		listenee.addListener(l);
	}

	public void removeAllListeners() {
		listenee.removeAllListeners();
	}

	public void removeListener(EventfulListener l) {
		listenee.removeListener(l);
	}

	public void updateListeners() {
		listenee.updateListeners();
	}
	
	
	
	
	
	private JComboBox<ITransitionSeries> tsCombo;
	private JLabel tsLabel;
	
	private ActionListener tsComboListener;
	
	public TSSelector() {
		listenee = new EventfulBeacon();
		setLayout(new BorderLayout());

		tsCombo = new JComboBox<>();
		
		tsLabel = new JLabel();
		tsLabel.setPreferredSize(tsCombo.getPreferredSize());
		tsLabel.setHorizontalAlignment(JLabel.CENTER);

		
		tsComboListener = e -> {
			updateLabelText();
			updateListeners();
		};
		
		tsCombo.addActionListener(tsComboListener);

		add(tsCombo, BorderLayout.CENTER);


	}


	public void setTransitionSeries(List<ITransitionSeries> tss) {
		
		tsCombo.removeActionListener(tsComboListener);
		tsCombo.removeAllItems();
				
		if (tss == null) return;
		
		for (ITransitionSeries ts : tss) { tsCombo.addItem(ts); }
		if (!tss.isEmpty()) tsCombo.setSelectedIndex(0);
		
		updateLabelText();
		
		tsCombo.addActionListener(tsComboListener);
		updateListeners();
		
	}
	
	public ITransitionSeries getTransitionSeries() {
		return (ITransitionSeries) tsCombo.getSelectedItem();
	}
	
	
	@Override
	public void setEnabled(boolean enabled) {
		
		this.removeAll();
		
		if (enabled) {
			add(tsCombo, BorderLayout.CENTER);
		} else {
			add(tsLabel, BorderLayout.CENTER);
		}
		
		super.setEnabled(enabled);

	}
	
	private void updateLabelText() {
		if (tsCombo.getSelectedItem() == null) {
			tsLabel.setText("");
		} else {
			tsLabel.setText(  tsCombo.getSelectedItem().toString()  );
		}
	}

}
