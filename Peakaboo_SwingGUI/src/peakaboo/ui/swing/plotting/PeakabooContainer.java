package peakaboo.ui.swing.plotting;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.Window;

import javax.swing.JMenuBar;


public interface PeakabooContainer
{

	public void setTitle(String s);
	public void setJMenuBar(JMenuBar jmb);
	
	public void validate();
	public void repaint();
	
	public boolean isApplet();
	public Window getWindow();
	public Panel getPanel();
	public Component getComponent();
	
	
}
