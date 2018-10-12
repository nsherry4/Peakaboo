package plural.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

import plural.executor.ExecutorSet;
import plural.executor.ExecutorState;
import plural.executor.PluralExecutor;
import swidget.icons.StockIcon;
import swidget.widgets.ButtonBox;
import swidget.widgets.HeaderBox;
import swidget.widgets.Spacing;
import swidget.widgets.buttons.ImageButton;

public class ExecutorSetView extends JPanel
{

	ExecutorSet<?> executors;
	private JProgressBar progress;
	
	public ExecutorSetView(ExecutorSet<?> _tasks){
		
		this.executors = _tasks;
		init();
	}
	
		
	private void init()
	{
		this.setLayout(new BorderLayout());

		ImageButton cancel = new ImageButton("Cancel").withStateCritical().withAction(() -> {
			executors.requestAbortWorking();
		});
		
        HeaderBox header = new HeaderBox(null, executors.getDescription(), cancel);
        this.add(header, BorderLayout.NORTH);
		
		JPanel center = new JPanel(new BorderLayout());
		center.setBorder(Spacing.bHuge());
		
        JPanel lineItems = new JPanel();
        lineItems.setBorder(new EmptyBorder(0, Spacing.huge, Spacing.huge, Spacing.huge));
		LayoutManager layout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        lineItems.setLayout(layout);

        
        
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 0.0;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
               
		ExecutorView view;
		for (PluralExecutor pl : executors){
			view = new ExecutorView(pl);
			lineItems.add(view, c);
			
			c.gridy += 1;
		}	
		center.add(lineItems, BorderLayout.CENTER);
		
		
		
		progress = new JProgressBar();
		progress.setMaximum(100);
		progress.setMinimum(0);
		progress.setValue(0);
		center.add(progress, BorderLayout.SOUTH);

		
		
		this.add(center, BorderLayout.CENTER);
		

		

		

		
		executors.addListener(() -> {
			javax.swing.SwingUtilities.invokeLater(() -> {

				if (executors.isAborted() && executors.isResultSet()){
					executors.discard();
				}
				else if (executors.getCompleted()){
					executors.discard();
				} else {
					updateProgressBar();
				}
				
			});
		});
		
		
	}
	
	
	@Override
	public synchronized void setVisible(boolean b)
	{
		super.setVisible(b);
	}
	
	protected void updateProgressBar(){
				
		for (PluralExecutor e : executors){
			if (e.getState() == ExecutorState.WORKING){
				progress.setValue((int)(e.getProgress() * 100));
				progress.setIndeterminate(false);
				break;
			} else if (e.getState() == ExecutorState.STALLED){
				progress.setIndeterminate(true);
			}
		}
		
	}
	

	
}
