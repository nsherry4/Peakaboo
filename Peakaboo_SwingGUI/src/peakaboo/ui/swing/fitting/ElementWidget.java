package peakaboo.ui.swing.fitting;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import peakaboo.ui.swing.widgets.Spacing;


public class ElementWidget extends JPanel
{
	private JPanel elementContents;
	private JLabel elementName;
	private JLabel elementDetail;
	private JCheckBox elementCheck;
	
	public ElementWidget()
	{
		super();
		
		setLayout(new BorderLayout());
		
		elementContents = new JPanel(new BorderLayout()); elementContents.setOpaque(false);
		
		elementName = new JLabel("");
		elementDetail = new JLabel("");
		
		elementContents.add(elementName, BorderLayout.CENTER);
		elementContents.add(elementDetail, BorderLayout.SOUTH);
		elementContents.setBorder(Spacing.bSmall());
		add(elementContents, BorderLayout.CENTER);
		//elementContents.setBorder(Spacing.bSmall());
		elementCheck = new JCheckBox(); elementCheck.setOpaque(false);
		elementCheck.setBorder(Spacing.bLarge());
		add(elementCheck, BorderLayout.WEST);
		
		elementName.setOpaque(false);
		elementDetail.setOpaque(false);
		elementName.setFont(elementName.getFont().deriveFont(elementName.getFont().getSize() * 1.4f));
		elementDetail.setFont(elementDetail.getFont().deriveFont(Font.PLAIN));
	}
	
	public boolean isSelected()
	{
		return elementCheck.isSelected();
	}
	public void setSelected(boolean selected)
	{
		elementCheck.setSelected(selected);
	}

	public void setName(String title)
	{
		elementName.setText(title);
	}
	public void setDescription(String description)
	{
		elementDetail.setText(description);
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
	
}
