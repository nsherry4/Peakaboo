package org.peakaboo.framework.plural.swing;

import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JDialog;
import javax.swing.WindowConstants;

import org.peakaboo.framework.plural.executor.ExecutorSet;

public class ExecutorSetViewDialog extends JDialog {

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

				if (executors.isAborted()){
					//executors.finished();
					setVisible(false);
					dispose();
				}
				else if (executors.getCompleted()){
					//executors.finished();
					setVisible(false);
					dispose();
				} else {
					//updateProgressBar();
				}
				
			});
		});
		
		pack();
		setLocationRelativeTo(owner);
		
		addWindowListener(new WindowListener() {
			
			public void windowOpened(WindowEvent e)
			{
				executors.startWorking();
			}
		
			public void windowIconified(WindowEvent e){}
		
			public void windowDeiconified(WindowEvent e){}
		
			public void windowDeactivated(WindowEvent e){}
		
			public void windowClosing(WindowEvent e){}
			
			public void windowClosed(WindowEvent e){}

			public void windowActivated(WindowEvent e){}
		});
		setVisible(true);
        
        
	}
	
	
	@Override
	public synchronized void setVisible(boolean b)
	{
		super.setVisible(b);
	}
	
	protected void updateProgressBar(){
		panel.updateProgressBar();
	}
	
	public static void main(String[] args)
	{
		
	}
		
}
