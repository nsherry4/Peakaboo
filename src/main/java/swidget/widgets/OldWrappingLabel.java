package swidget.widgets;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;


public class OldWrappingLabel extends ClearPanel
{

	private String text;
	private Integer width;
	private JPanel innerPanel;
	

	public OldWrappingLabel(String text, int width)
	{
		this.text = text;
		this.width = width;
		
		init();		
		
	}
	
	
	private void init()
	{
		innerPanel = new ClearPanel(){
			

			@Override
			public void paint(Graphics graphics)
			{
				super.paint(graphics);
				int realWidth, origWidth;
				origWidth = width;
				realWidth = width;
				
				
				Graphics2D g = (Graphics2D)graphics;
				
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
				
				String[] words = text.split(" ");
				String line = "";
				FontMetrics m = g.getFontMetrics();
				int currentHeight = m.getAscent();
				
				for (String word : words)
				{
					
					//always have at least one word per line
					if (line.equals(""))
					{
						line += word;
						continue;
					}
					
					//if appending this word would make the line too long
					if (m.stringWidth(line + " " + word) > origWidth)
					{
						realWidth = Math.max(realWidth, m.stringWidth(line));
						g.drawString(line, 0, currentHeight);
						currentHeight += m.getHeight();
						line = word;
					} else {
						line += " " + word;
					}
					
				}
				
				g.drawString(line, 0, currentHeight);

				
				this.setPreferredSize(new Dimension(realWidth, currentHeight));
				
			}
			
		};
		
		setLayout(new BorderLayout());
		add(innerPanel, BorderLayout.CENTER);
		
		getInitialSize();
	}
	
	
	
	private void getInitialSize()
	{
		FontMetrics m = this.getFontMetrics(this.getFont());
		
		if (text == null) return;
		
		int realWidth, origWidth;
		origWidth = width;
		realWidth = width;

		
		String[] words = text.split(" ");
		String line = "";
		int currentHeight = m.getHeight();
		
		for (String word : words)
		{
			
			//always have at least one word per line
			if (line.equals(""))
			{
				line += word;
				continue;
			}
			
			//if appending this word would make the line too long
			if (m.stringWidth(line + " " + word) > origWidth)
			{
				realWidth = Math.max(realWidth, m.stringWidth(line));
				currentHeight += m.getHeight();
				line = word;
			} else {
				line += " " + word;
			}
			
		}
		
		
		innerPanel.setPreferredSize(new Dimension(realWidth, currentHeight));
		
	}
	
	public void setText(String text)
	{
		this.text = text;
		getInitialSize();
		repaint();
	}
	
	public void setWrapWidth(int width)
	{
		this.width = width;
	}
	
	
	
	
}
