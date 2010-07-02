package peakaboo.ui.swing.widgets.tasks;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;

import peakaboo.datatypes.eventful.PeakabooSimpleListener;
import peakaboo.datatypes.tasks.Task;
import peakaboo.datatypes.tasks.TaskList;
import swidget.containers.SwidgetContainer;
import swidget.containers.SwidgetDialog;
import swidget.widgets.ImageButton;
import swidget.widgets.Spacing;

public class TaskListView extends SwidgetDialog {

	TaskList<?> tasks;
	private JProgressBar progress;
	
	public TaskListView(SwidgetContainer frame, TaskList<?> _tasks){
		
		super(frame, "Working...", true);
		setResizable(false);
		
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		this.tasks = _tasks;
		
        JPanel panel = new JPanel();
        getContentPane().add(panel);

		
        LayoutManager layout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        panel.setLayout(layout);
		
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 1.0;
        c.weighty = 0.0;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        
        JLabel title = new JLabel(tasks.getDescription());
        title.setFont(title.getFont().deriveFont(Font.BOLD).deriveFont(title.getFont().getSize() + 2f));
        title.setBorder(Spacing.bMedium());
        panel.add(title, c);
        

		TaskView view;
		for (Task task : tasks){
			
			c.gridy += 1;
			
			view = new TaskView(task);
			panel.add(view, c);
			
		}

        
		c.gridy += 1;
		c.weighty = 1.0;
		
		progress = new JProgressBar();
		progress.setMaximum(100);
		progress.setMinimum(0);
		progress.setValue(0);
		JPanel progressPanel = new JPanel();
		progressPanel.add(progress);
		progressPanel.setBorder(Spacing.bLarge());
		panel.add(progressPanel, c);
        
		c.weighty = 0.0;
		c.gridy += 1;
		c.anchor = GridBagConstraints.LAST_LINE_END;
		ImageButton cancel = new ImageButton("cancel", "Cancel", true);
		cancel.setMargin(Spacing.iSmall());
		cancel.addActionListener(new ActionListener() {
		
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				tasks.requestAbortWorking();
			}
		});
		panel.add(cancel, c);
		
		
		panel.setBorder(Spacing.bHuge());
		
		tasks.addListener(new PeakabooSimpleListener() {
		
			public void change() {
								
				// TODO Auto-generated method stub
				if (tasks.isAborted()){
					tasks.finished();
					setVisible(false);
				}
				else if (tasks.getCompleted()){
					tasks.finished();
					setVisible(false);
				} else {
					updateProgressBar();
				}
			}
		});
		
		pack();
        
		centreOnParent();
		
        tasks.startWorking();
        setVisible(true);
        
        
        
	}
	
	
	protected void updateProgressBar(){
				
		for (Task t : tasks){
			if (t.getState() == Task.State.WORKING){
				progress.setValue((int)(t.getProgress() * 100));
				progress.setIndeterminate(false);
				break;
			} else if (t.getState() == Task.State.STALLED){
				progress.setIndeterminate(true);
			}
		}
		
	}
		
}
