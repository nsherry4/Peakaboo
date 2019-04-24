package org.peakaboo.ui.swing.plotting.fitting.fitted;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.peakaboo.framework.swidget.widgets.ClearPanel;
import org.peakaboo.framework.swidget.widgets.Spacing;


public class FittedWidget extends ClearPanel
{
	private JPanel elementContents;
	private JLabel elementName;
	private JLabel elementNumber, elementIntensity;
	
	private boolean flagged = false;
	
	public FittedWidget()
	{
		super();
		
		setLayout(new BorderLayout());
		
		elementContents = new ClearPanel(new BorderLayout());
		
		elementName = new JLabel("");
		elementNumber = new JLabel("");
		elementIntensity = new JLabel("");
		//elementNumber.setHorizontalAlignment(JLabel.RIGHT);
		//elementIntensity.setHorizontalAlignment(JLabel.RIGHT);
		
		elementContents.add(elementName, BorderLayout.CENTER);
		elementContents.setBorder(Spacing.bLarge());

		add(elementContents, BorderLayout.CENTER);
				
		elementName.setOpaque(false);
		elementNumber.setOpaque(false);
		elementIntensity.setOpaque(false);
		
		elementName.setFont(elementName.getFont().deriveFont(elementName.getFont().getSize() * 1.4f).deriveFont(Font.BOLD));
		elementNumber.setFont(elementNumber.getFont().deriveFont(elementNumber.getFont().getSize() * 0.9f));
		elementIntensity.setFont(elementIntensity.getFont().deriveFont(elementIntensity.getFont().getSize() * 0.9f));
		
		JPanel details = new ClearPanel(new BorderLayout(8, 0));
		details.add(elementIntensity, BorderLayout.WEST);
		details.add(elementNumber, BorderLayout.CENTER);
		elementContents.add(details, BorderLayout.SOUTH);

		
		
	}
	
	@Override
	public void setName(String title)
	{
		elementName.setText(title);
	}

	
	public void setFlag(boolean flag) {
		flagged = flag;
	}

	
	@Override
	public void setForeground(Color c)
	{
		
		super.setForeground(c);
		if (elementName == null) return;
		Color cDetail = new Color(c.getRed(), c.getGreen(), c.getBlue(), 192);
		elementName.setForeground(c);
		elementNumber.setForeground(cDetail);
		elementIntensity.setForeground(cDetail);
	}
	
	private void setForeground() {
		setForeground(super.getForeground());
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

	public void setIntensity(String intensity) {
		elementIntensity.setText("Intensity: " + intensity);
	}

	public void setAtomicNumber(int atomicNumber) {
		elementNumber.setText("Z: " + atomicNumber);
	}

	public void setAtomicNumbers(List<Integer> atomicNumbers) {
		String zstring = atomicNumbers.stream().map(i -> i.toString()).reduce((a, b) -> a + ", " + b).get();
		elementNumber.setText("Z: " + zstring);
	}
	
	
}