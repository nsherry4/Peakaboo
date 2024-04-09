package org.peakaboo.ui.swing.plotting.fitting;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.peakaboo.controller.plotter.fitting.FittingController;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.components.ui.itemlist.SelectionListControls;

/**
 * Abstract fitting panel wraps a custom fitting widget with header controls to
 * accept/reject and displays a title
 */
public abstract class AbstractFittingPanel extends JPanel {

	protected FittingController controller;
	protected CurveFittingView owner;
	
	private SelectionListControls controls;
	
	protected AbstractFittingPanel(
			FittingController controller, 
			CurveFittingView owner,
			String name,
			String title) {
		
		this.controller = controller;
		this.owner = owner;

		// Build selection controls
		this.controls = new SelectionListControls(name, title) {
			
			@Override
			protected void cancel() {
				onCancel();
			}
			
			@Override
			protected void approve() {
				onAccept();
			}
		};
		controls.setOpaque(false);
		
		// Layout
		this.setLayout(new BorderLayout());
		this.add(controls, BorderLayout.NORTH);

		// Leave the CENTER space for impl-specific widget, set with setBody
		
	}
	
	public void setBody(JComponent c) {
		JScrollPane scroll = new JScrollPane(c);
		scroll.setBorder(Spacing.bNone());
		scroll.setPreferredSize(new Dimension(200, 0));
		scroll.setBackground(getBackground());
		scroll.getViewport().setBackground(getBackground());
		this.add(scroll, BorderLayout.CENTER);
	}
	
	protected abstract void onAccept();
	protected abstract void onCancel();
	
	/**
	 * Sets if this panel is currently active and being used by the user. When true,
	 * a component should register any listeners or change any controller settings
	 * it may need to in order to work properly
	 * 
	 * @param isActive
	 */
	public abstract void setActive(boolean isActive);
}
