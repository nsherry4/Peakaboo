package peakaboo.ui.swing.widgets.testing;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import scitypes.Coord;


public class Lines extends JPanel{


	public Coord<Integer> p1, p2;
	
	public Lines()
	{

		setOpaque(false);
		
		setPreferredSize(new Dimension(500, 500));
		
		addMouseListener(new MouseListener() {
			
			public void mouseReleased(MouseEvent e) {
				
				int x, y;
				x = e.getX();
				y = e.getY();
				
				if (p1 == null) 
					p1 = new Coord<Integer>(x, y);
				else
					p2 = new Coord<Integer>(x, y);
				
				repaint();
				
			}
			
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
	
		
		
	}
	
	@Override
	public void paint(Graphics g)
	{
		
		super.paint(g);
		
		if (p1 != null && p2 != null)
		{
			g.drawLine(p1.x, p1.y, p2.x, p2.y);
		}
		
	}
	
	
}
