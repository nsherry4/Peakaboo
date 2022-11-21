package org.peakaboo.framework.plural.swing;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

import org.peakaboo.framework.plural.executor.ExecutorSet;
import org.peakaboo.framework.plural.executor.ExecutorState;
import org.peakaboo.framework.plural.executor.PluralExecutor;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;
import org.peakaboo.framework.stratus.components.ui.header.HeaderBox;

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

		FluentButton cancel = new FluentButton("Cancel").withStateCritical().withAction(() -> {
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
