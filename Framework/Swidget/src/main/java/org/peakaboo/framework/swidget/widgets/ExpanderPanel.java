package org.peakaboo.framework.swidget.widgets;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;

import org.peakaboo.framework.swidget.icons.IconSize;
import org.peakaboo.framework.swidget.icons.StockIcon;

public class ExpanderPanel extends ClearPanel {
	private boolean expanded = false;
	private Component header, body;
	private JLabel expander;

	public ExpanderPanel(Component header, Component body) {
		this.header = header;
		this.body = body;
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				toggleSelection();
			}
		});

		this.expanded = false;
		this.setRequestFocusEnabled(true);

		expander = new JLabel(StockIcon.GO_NEXT.toImageIcon(IconSize.BUTTON));

		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0f;
		c.weighty = 0f;
		c.anchor = GridBagConstraints.NORTHWEST;
		this.add(expander, c);

		c.gridx++;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1f;
		this.add(header, c);

		c.gridy = 1;
		c.gridx = 1;
		this.add(body, c);

		this.body.setVisible(this.expanded);

	}

	public void toggleSelection() {
		expanded = !expanded;
		this.body.setVisible(expanded);
		StockIcon icon = expanded ? StockIcon.GO_DOWN : StockIcon.GO_NEXT;
		this.expander.setIcon(icon.toImageIcon(IconSize.BUTTON));
	}
}