package peakaboo.ui.swing.widgets.tasks;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import peakaboo.datatypes.eventful.PeakabooSimpleListener;
import peakaboo.datatypes.tasks.Task;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.Spacing;

public class TaskView extends JPanel{

	private JLabel label;
	private JLabel icon;
	private Task task;
	
	public TaskView(Task task){
		this.task = task;
		
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
		
		task.addListener(new PeakabooSimpleListener() {
		
			public void change() {
				// TODO Auto-generated method stub
				setState();
			}
		});
				
	}
	
	protected void setState(){
		
		switch (task.getState()){
		
		case COMPLETED:
			icon.setIcon(StockIcon.CHOOSE_OK.toImageIcon(IconSize.BUTTON));
			break;
		case WORKING:
		case STALLED:
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
