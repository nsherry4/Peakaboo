package peakaboo.ui.swing.calibration.picker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import peakaboo.mapping.calibration.CalibrationReference;
import swidget.widgets.Spacing;
import swidget.widgets.listwidget.ListWidget;

public class ReferenceWidget extends ListWidget<CalibrationReference> {

	private JLabel name, desc;
	
	public ReferenceWidget() {
		setLayout(new BorderLayout(Spacing.medium, Spacing.medium));
		
		name = new JLabel();
		name.setFont(name.getFont().deriveFont(name.getFont().getSize()*1.5f));
		name.setFont(name.getFont().deriveFont(Font.BOLD));
		add(name, BorderLayout.CENTER);
		
		desc = new JLabel();
		add(desc, BorderLayout.SOUTH);


		setBorder(Spacing.bLarge());
		
	}
		
	@Override
	public void setForeground(Color c) {
		if (name != null) {
			name.setForeground(c);
			Color cDetail = new Color(c.getRed(), c.getGreen(), c.getBlue(), 192);
			desc.setForeground(cDetail);
		}
	}

	@Override
	protected void onSetValue(CalibrationReference reference) {
		name.setText(reference.getName());
		desc.setText(reference.getDescription());
	}
	
}
