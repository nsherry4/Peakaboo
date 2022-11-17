package org.peakaboo.framework.swidget.widgets.options;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

public class OptionCustomComponent extends OptionBox implements OptionFluentAPI {

	private OptionLabel label;
	private JComponent component;
	
	public OptionCustomComponent(OptionBlock block, JComponent component, boolean leftAlign) {
		super(block);
		
		this.component = component;
		
		label = new OptionLabel("", "");
		
		if (leftAlign) {
			this.add(component);
			this.addSpacer();
			this.add(label);
			this.addExpander();
		} else {
			this.add(label);
			this.addSpacer();
			this.addExpander();
			this.add(component);
		}
		
		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				onClick();
			}
		});
		
	}
	
	protected void onClick() {
		//noop
	}
	
	public JComponent getComponent() {
		return this.component;
	}
	
	@Override
	public OptionCustomComponent withDescription(String description) {
		this.label.withDescription(description);
		return this;
	}


	@Override
	public OptionCustomComponent withTooltip(String tooltip) {
		this.setToolTipText(tooltip);
		this.component.setToolTipText(tooltip);
		return this;
	}

	@Override
	public OptionCustomComponent withTitle(String title) {
		label.withTitle(title);
		return this;
	}

	@Override
	public OptionCustomComponent withSize(OptionSize size) {
		label.withSize(size);
		this.setPadding(size.getPaddingSize());
		return this;
	}

	@Override
	public OptionCustomComponent withText(String title, String description) {
		label.withText(title, description);
		return this;
	}


}
