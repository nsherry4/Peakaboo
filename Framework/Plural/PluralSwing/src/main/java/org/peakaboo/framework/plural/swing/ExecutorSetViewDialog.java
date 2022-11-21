package org.peakaboo.framework.plural.swing;

import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.WindowConstants;

import org.peakaboo.framework.plural.executor.ExecutorSet;
import org.peakaboo.framework.stratus.components.ui.live.LiveDialog;

public class ExecutorSetViewDialog extends LiveDialog {

	ExecutorSet<?> executors;
	ExecutorSetView panel;
	
	public ExecutorSetViewDialog(Window owner, ExecutorSet<?> _tasks){
		
		super(owner, "Working...", ModalityType.DOCUMENT_MODAL);
		this.executors = _tasks;
		init(owner);
	}
	
	public ExecutorSetViewDialog(JDialog owner, ExecutorSet<?> _tasks){
		
		super(owner, "Working...", true);
		this.executors = _tasks;
		init(owner);
	}
		
	private void init(Window owner)
	{
	
		setResizable(false);
		
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		
		
		panel = new ExecutorSetView(executors);
		getContentPane().add(panel);

		
		executors.addListener(() -> {
			javax.swing.SwingUtilities.invokeLater(() -> {

				if (executors.isAborted() || executors.getCompleted()){
					setVisible(false);
					dispose();
				}
				
			});
		});
		
		pack();
		setLocationRelativeTo(owner);
		
		addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowOpened(WindowEvent e) {
				executors.startWorking();
			}

		});
		setVisible(true);
        
        
	}
		
	protected void updateProgressBar(){
		panel.updateProgressBar();
	}
		
}
