package peakaboo.ui.swing.container;

import java.awt.Container;
import java.awt.Window;

import javax.swing.JFrame;
import javax.swing.JMenuBar;

public class FrameContainer implements PeakabooContainer
{
	JFrame	frame;
	
	public FrameContainer(JFrame container)
	{
		frame = container;
	}
	
	
	public Container getContainer()
	{
		return frame;
	}


	@Override
	public void setTitle(String title)
	{
		frame.setTitle(title);
	}


	@Override
	public void setJMenuBar(JMenuBar menubar)
	{
		frame.setJMenuBar(menubar);
	}


	@Override
	public Window getWindow()
	{
		return frame;
	}
	
	
}
