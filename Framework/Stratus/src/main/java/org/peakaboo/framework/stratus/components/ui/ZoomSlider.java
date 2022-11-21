package org.peakaboo.framework.stratus.components.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.function.Consumer;

import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;

import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButtonLayout;


public class ZoomSlider extends JPanel {

	private JSlider zoomSlider;
	private FluentButton in, out;
	private ChangeListener zoomSliderListener;
	
	public ZoomSlider(int start, int end, final int step, Consumer<Integer> onChange) {

		setLayout(new BorderLayout());

		zoomSlider = new JSlider(start, end);
		zoomSlider.setPaintLabels(false);
		zoomSlider.setPaintTicks(false);
		zoomSlider.setValue(start);
		Dimension prefSize = zoomSlider.getPreferredSize();
		prefSize.width /= 2;
		zoomSlider.setPreferredSize(prefSize);
		zoomSliderListener = e -> onChange.accept(getValue());
		zoomSlider.addChangeListener(zoomSliderListener);
		
		
		out = new FluentButton()
				.withIcon(StockIcon.ZOOM_OUT)
				.withTooltip("Zoom Out")
				.withLayout(FluentButtonLayout.IMAGE)
				.withBordered(false)
				.withAction(() -> zoomSlider.setValue(zoomSlider.getValue() - step));
		in = new FluentButton()
				.withIcon(StockIcon.ZOOM_IN)
				.withTooltip("Zoom In")
				.withLayout(FluentButtonLayout.IMAGE)
				.withBordered(false)
				.withAction(() -> zoomSlider.setValue(zoomSlider.getValue() + step));

		add(out, BorderLayout.WEST);
		add(zoomSlider, BorderLayout.CENTER);
		add(in, BorderLayout.EAST);

	}

	public void setValue(int value) {
		zoomSlider.setValue(value);
	}
	
	public void setValueEventless(int value) {
		zoomSlider.removeChangeListener(zoomSliderListener);
		zoomSlider.setValue(value);
		zoomSlider.addChangeListener(zoomSliderListener);
	}
	
	public int getValue() {
		return zoomSlider.getValue();
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		zoomSlider.setEnabled(enabled);
		in.setEnabled(enabled);
		out.setEnabled(enabled);
	}
	
}
