package peakaboo.ui.swing.dialogues;

import java.awt.Component;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.Window;

import javax.swing.JDialog;

import peakaboo.ui.swing.plotting.PeakabooContainer;


public class PeakabooDialog extends JDialog implements PeakabooContainer
{
	
	private PeakabooContainer container;

	public PeakabooDialog(PeakabooContainer container, String title)
	{
		this(container, title, true);
	}
	
	public PeakabooDialog(PeakabooContainer container, String title, boolean modal)
	{
		super();
		
		this.container = container;
		setModal(  modal  );
				
		setTitle(title);
		
	}
	
	public void centreOnParent()
	{
		if (! container.isApplet()) 
			setLocationRelativeTo(container.getWindow());
		else
			setLocationRelativeTo(container.getPanel());
	}

	public Component getComponent()
	{
		return this;
	}

	public Window getWindow()
	{
		return this;
	}

	public Panel getPanel()
	{
		return null;
	}

	public boolean isApplet()
	{
		return false;
	}

	public void close()
	{
		setVisible(false);
	}
	
}
