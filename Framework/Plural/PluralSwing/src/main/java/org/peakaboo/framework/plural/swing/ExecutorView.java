package org.peakaboo.framework.plural.swing;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.peakaboo.framework.plural.executor.PluralExecutor;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.Stratus;


public class ExecutorView extends JPanel{

	private JLabel label;
	private Font originalFont;
	private PluralExecutor executor;
	
	public ExecutorView(PluralExecutor task){
		this.executor = task;
		
		this.setLayout(new BorderLayout());
		
		label = new JLabel(task.getName());
		label.setFont(label.getFont().deriveFont(label.getFont().getSize() + 1f));
		originalFont = label.getFont();
		label.setHorizontalAlignment(SwingConstants.CENTER);
		this.add(label, BorderLayout.CENTER);
		
		this.setBorder(Spacing.bSmall());
		
		task.addListener(this::setState);
				
	}
	
	protected void setState(){
		
		var green = Stratus.getTheme().emphasize(
				Stratus.getTheme().getPalette().getColour("Green", "5"), 0.1f
		);
		var yellow = Stratus.getTheme().emphasize(
				Stratus.getTheme().getPalette().getColour("Yellow", "5"), 0.1f
		);
		
		switch (executor.getState()){
		
		case COMPLETED:
			label.setFont(originalFont);
			label.setForeground(green);
			break;
		case WORKING:
			label.setFont(originalFont.deriveFont(Font.BOLD));
			label.setForeground(green);
			break;
		case STALLED:
			label.setFont(originalFont.deriveFont(Font.BOLD));
			label.setForeground(yellow);
			break;
		case UNSTARTED:
			label.setFont(originalFont);
			label.setForeground(Stratus.getTheme().getControlText());
			break;
		case SKIPPED:
			label.setFont(originalFont);
			label.setForeground(Stratus.getTheme().getControlTextDisabled());
			break;
		}
		
	}
		
}
