/*
 * TabCloseIcon.java
 */

package peakaboo.ui.swing.mapping;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Icon;
import javax.swing.JTabbedPane;

import eventful.Eventful;

import swidget.icons.IconFactory;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;

/**
 * Taken from http://weblogs.java.net/blog/herkules/archive/2005/10/close_icons_on_1.html
 * @author Herkules
 */
public class TabIconButton extends Eventful implements Icon
{
	private final Icon mIcon;
	private JTabbedPane mTabbedPane = null;
	private transient Rectangle mPosition = null;
	
	private MouseListener listener;
	
	/**
	 * Creates a new instance of TabCloseIcon.
	 */
	public TabIconButton( StockIcon icon )
	{
		mIcon = icon.toImageIcon(IconSize.BUTTON);
	}
	public TabIconButton( String filename )
	{
		mIcon = IconFactory.getImageIcon(filename, IconSize.BUTTON);
	}
	
	
	
	/**
	 * when painting, remember last position painted.
	 */
	public void paintIcon(Component c, Graphics g, int x, int y)
	{
		if( null==mTabbedPane && listener == null )
		{
			mTabbedPane = (JTabbedPane)c;
			
			listener = new MouseAdapter()
			{
				@Override public void mouseClicked(MouseEvent e)
				{
					// asking for isConsumed is *very* important, otherwise more than one tab might get closed!
					if ( mPosition.contains( e.getX(), e.getY() ) )
					{
						if (! e.isConsumed()) {
							//final int index = mTabbedPane.getSelectedIndex();
							//mTabbedPane.remove( index );
							e.consume();
							updateListeners();
						}
					}
				}
				
				@Override public void mouseReleased( MouseEvent e )
				{

				}
			};
			
			mTabbedPane.addMouseListener( listener );
		}
		
		mPosition = new Rectangle( x,y, getIconWidth(), getIconHeight() );
		mIcon.paintIcon(c, g, x, y );
	}
	
	
	public void detatchListener()
	{
		if (listener != null && mTabbedPane != null)
		{
			mTabbedPane.removeMouseListener(listener);
		}
	}
	
	/**
	 * just delegate
	 */
	public int getIconWidth()
	{
		return mIcon.getIconWidth();
	}
	
	/**
	 * just delegate
	 */
	public int getIconHeight()
	{
		return mIcon.getIconHeight();
	}
	
}