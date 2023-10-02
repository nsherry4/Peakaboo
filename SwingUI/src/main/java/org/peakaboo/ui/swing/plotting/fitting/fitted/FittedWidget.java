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
import javax.swing.border.EmptyBorder;

import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.components.panels.ClearPanel;


public class FittedWidget extends ClearPanel
{
	private JPanel elementContents;
	private JLabel elementName;
	private JLabel elementNumber, elementIntensity;
	
	private boolean flagged = false;
	
	
	public static FittedWidget large() {
		return new FittedWidget(16, 11, Spacing.large, true);
	}

	public static FittedWidget medium() {
		return new FittedWidget(14, 11, Spacing.medium, false);
	}
	
	
	private FittedWidget(float titleSize, float detailSize, int borderSize, boolean bold)	{
		super();
		
		setLayout(new BorderLayout());
		
		elementContents = new ClearPanel(new BorderLayout());
		
		elementName = new JLabel("");
		elementNumber = new JLabel("");
		elementIntensity = new JLabel("");
		
		elementContents.add(elementName, BorderLayout.CENTER);
		elementContents.setBorder(new EmptyBorder(borderSize, borderSize, borderSize, borderSize));

		add(elementContents, BorderLayout.CENTER);
				
		elementName.setOpaque(false);
		elementNumber.setOpaque(false);
		elementIntensity.setOpaque(false);
		
		elementName.setFont(elementName.getFont().deriveFont(titleSize).deriveFont(bold ? Font.BOLD : 0));
		elementNumber.setFont(elementNumber.getFont().deriveFont(detailSize));
		elementIntensity.setFont(elementIntensity.getFont().deriveFont(detailSize));
		
		JPanel details = new ClearPanel(new BorderLayout(8, 0));
		details.add(elementIntensity, BorderLayout.WEST);
		details.add(elementNumber, BorderLayout.CENTER);
		elementContents.add(details, BorderLayout.SOUTH);

		
		
	}
	
	@Override
	public void setName(String title) {
		elementName.setText(title);
	}

	
	public void setFlag(boolean flag) {
		flagged = flag;
	}

	
	@Override
	public void setForeground(Color c) {
		
		super.setForeground(c);
		if (elementName == null) return;
		Color cDetail = new Color(c.getRed(), c.getGreen(), c.getBlue(), 192);
		elementName.setForeground(c);
		elementNumber.setForeground(cDetail);
		elementIntensity.setForeground(cDetail);
	}
	
	@Override
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
		String zstring = atomicNumbers.stream().map(i -> i.toString()).reduce((a, b) -> a + ", " + b).orElse("?");
		elementNumber.setText("Z: " + zstring);
	}
	
	
}