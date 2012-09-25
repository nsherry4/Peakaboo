package peakaboo.ui.swing.container;

import java.awt.Container;
import java.awt.Window;

import javax.swing.JMenuBar;

/** 
 * Interface for abstracting out the differences between an 
 * application and an applet, as much as possible. Depending
 * on the implementation, some methods may be non-functional,
 * or may return null. 
 * @author Nathaniel Sherry
 *
 */

public interface PeakabooContainer
{
	/** 
	 * Returns the base {@link Container}, eg a JFrame, JApplet, etc
	 * @return
	 */
	public Container getContainer();
	
	/**
	 * Returns the Container as a {@link Window}, if it is one. 
	 * Returns null, otherwise. 
	 * @return
	 */
	public Window getWindow();
	
	/**
	 * Sets the title for the base container, if titles are supported
	 * @param title
	 */
	public void setTitle(String title);
	
	/**
	 * Sets the menu bar for the base container
	 * @param menubar
	 */
	public void setJMenuBar(JMenuBar menubar);
}
