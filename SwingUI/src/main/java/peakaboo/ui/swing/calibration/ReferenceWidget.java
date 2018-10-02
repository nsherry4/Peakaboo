package peakaboo.ui.swing.calibration;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import peakaboo.mapping.calibration.CalibrationReference;
import swidget.widgets.Spacing;

public class ReferenceWidget extends JPanel {

	private CalibrationReference reference;
	private JLabel name, desc, info;
	
	public ReferenceWidget() {
		setLayout(new BorderLayout(Spacing.medium, Spacing.medium));
		
		name = new JLabel();
		name.setFont(name.getFont().deriveFont(name.getFont().getSize()*1.5f));
		name.setFont(name.getFont().deriveFont(Font.BOLD));
		add(name, BorderLayout.CENTER);
		
		desc = new JLabel();
		add(desc, BorderLayout.SOUTH);
		
		info = new JLabel();
		info.setVerticalAlignment(SwingConstants.TOP);
		add(info, BorderLayout.EAST);
		
		setBorder(Spacing.bLarge());
		
	}
	
	public void setReference(CalibrationReference reference) {
		this.reference = reference;
			
		name.setText(reference.getName());
		desc.setText(reference.getDescription());
		int count = reference.getConcentrations().size();
		info.setText(count + " Fittings");
		
	}
	
	@Override
	public void setForeground(Color c) {
		if (name != null) {
			name.setForeground(c);
			Color cDetail = new Color(c.getRed(), c.getGreen(), c.getBlue(), 192);
			desc.setForeground(cDetail);
			info.setForeground(cDetail);
		}
	}
	
}
