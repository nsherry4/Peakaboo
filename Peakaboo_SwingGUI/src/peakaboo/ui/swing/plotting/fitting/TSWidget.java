package peakaboo.ui.swing.plotting.fitting;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

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
		if (large) elementName.setFont(elementName.getFont().deriveFont(elementName.getFont().getSize() * 1.4f));
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
