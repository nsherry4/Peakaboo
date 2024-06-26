package org.peakaboo.framework.plural.swing;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.peakaboo.framework.plural.executor.PluralExecutor;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.icons.IconSize;
import org.peakaboo.framework.stratus.api.icons.StockIcon;


public class ExecutorView extends JPanel{

	private JLabel label;
	private JLabel icon;
	private PluralExecutor executor;
	
	public ExecutorView(PluralExecutor task){
		this.executor = task;
		
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		label = new JLabel(task.getName());
		icon = new JLabel();
		Dimension d = new Dimension(16, 16);
		icon.setMinimumSize(d);
		icon.setMaximumSize(d);
		icon.setPreferredSize(d);
		
		this.add(icon);
		this.add(Box.createHorizontalStrut(8));
		this.add(label);
		this.add(Box.createHorizontalGlue());
		
		this.setBorder(Spacing.bSmall());
		
		task.addListener(this::setState);
				
	}
	
	protected void setState(){
		
		switch (executor.getState()){
		
		case COMPLETED:
			icon.setIcon(StockIcon.PROCESS_COMPLETED.toImageIcon(IconSize.BUTTON));
			break;
		case WORKING, STALLED:
			icon.setIcon(StockIcon.GO_NEXT.toImageIcon(IconSize.BUTTON));
			break;
		case UNSTARTED:
			icon.setIcon(null);
			break;
		case SKIPPED:
			icon.setIcon(StockIcon.GO_DOWN.toImageIcon(IconSize.BUTTON));
			break;
		}
		
	}
		
}
