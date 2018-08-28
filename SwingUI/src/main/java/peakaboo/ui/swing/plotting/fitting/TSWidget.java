package peakaboo.ui.swing.plotting.fitting;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import swidget.widgets.ClearPanel;
import swidget.widgets.Spacing;


public class TSWidget extends ClearPanel
{
	private JPanel elementContents;
	private JLabel elementName;
	private JLabel elementDetail;
	private JCheckBox elementCheck;
	private boolean flagged = false;
	
	public TSWidget(boolean large)
	{
		super();
		
		setLayout(new BorderLayout());
		
		elementContents = new JPanel(new BorderLayout()); elementContents.setOpaque(false);
		
		elementName = new JLabel("");
		elementDetail = new JLabel("");
		
		elementContents.add(elementName, BorderLayout.CENTER);
		if (large) elementContents.add(elementDetail, BorderLayout.SOUTH);
		if (large) {
			elementContents.setBorder(Spacing.bSmall());
		} else {
			elementContents.setBorder(Spacing.bTiny());
		}
		add(elementContents, BorderLayout.CENTER);
		//elementContents.setBorder(Spacing.bSmall());
		elementCheck = new JCheckBox(); elementCheck.setOpaque(false);
		if (!large) add(elementCheck, BorderLayout.WEST);
				
		elementName.setOpaque(false);
		elementDetail.setOpaque(false);
		if (large) elementName.setFont(elementName.getFont().deriveFont(elementName.getFont().getSize() * 1.4f).deriveFont(Font.BOLD));
		if (large) elementDetail.setFont(elementDetail.getFont().deriveFont(Font.PLAIN));
		
		
		
	}
	
	public boolean isSelected()
	{
		return elementCheck.isSelected();
	}
	public void setSelected(boolean selected)
	{
		elementCheck.setSelected(selected);
	}

	@Override
	public void setName(String title)
	{
		elementName.setText(title);
	}
	public void setDescription(String description)
	{
		elementDetail.setText(description);
	}
	
	public void setFlag(boolean flag) {
		flagged = flag;
	}

	
	@Override
	public void setForeground(Color c)
	{
		super.setForeground(c);
		if (elementName != null) elementName.setForeground(c);
		if (elementDetail != null) elementDetail.setForeground(c);
	}
	
	public JCheckBox getCheckBox()
	{
		return elementCheck;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (flagged) {
			drawFlag(g);
		}
	}
	
	private void drawFlag(Graphics g) {
				
		int size = Math.min(10, Math.min(getWidth(), getHeight()));
		int x = getWidth() - size;
		int y = 0;
		
		GeneralPath path = new GeneralPath();
		path.moveTo(x, y);
		path.lineTo(x+size, y);
		path.lineTo(x+size, y+size);
		path.lineTo(x, y);
		
		
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(new Color(0xffF9A825, true));
		g2d.fill(path);
		
	}
	
	
}