package org.peakaboo.framework.stratus.components.ui.layers;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayer;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.icons.IconSize;
import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.panels.ClearPanel;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;

public class ToastLayer implements Layer {

	private JPanel toast = new ClearPanel();
	private FluentButton close;
	private final JLayer<JComponent> toastJLayer;
	private JComponent label;
	private float alpha = 0f, delta = 0.1f;
	private int stage = 0;
	
	private LayerPanel parent;
	private int duration = 5000;
	
	public ToastLayer(LayerPanel parent, String message) {
		this(parent, message, () -> {});
	}
	
	public ToastLayer(LayerPanel parent, String message, Runnable onClick) {
		this.parent = parent;
		
		
		toast.setLayout(new FlowLayout());

		JPanel messagePanel = new ClearPanel(new BorderLayout()){
			
			@Override
			protected void paintComponent(Graphics g) {
							
				((Graphics2D)g).setRenderingHint(
					    RenderingHints.KEY_ANTIALIASING,
					    RenderingHints.VALUE_ANTIALIAS_ON);
				
				
				
				g.setColor(new Color(0xB0000000, true));
				g.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), 20, 20);

				
				super.paintComponent(g);
			}
			

		};
		messagePanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				onClick.run();
			}
		});
		
		if (message.contains("\n")) {
			label = new ClearPanel();
			BoxLayout layout = new BoxLayout(label, BoxLayout.Y_AXIS);
			label.setLayout(layout);
			String[] lines = message.split("\n");
			for (String line : lines) {
				JLabel lineLabel = makeLabel(line);
				label.add(lineLabel);
			}
		} else {
			label = makeLabel(message);
		}
		label.setBorder(Spacing.bMedium());
		label.setAlignmentY(0.5f);
		
		close = new FluentButton()
				.withIcon(StockIcon.WINDOW_CLOSE, IconSize.BUTTON, Color.WHITE)
				.withAction(this::fadeOut)
				.withBordered(false)
				.withBorder(Spacing.bMedium());
		close.setVerticalAlignment(SwingConstants.CENTER);
		close.setFocusable(false);
		close.setToolTipText(null);
		
		
		toast.setFocusable(false);
		messagePanel.add(label, BorderLayout.CENTER);
		messagePanel.add(close, BorderLayout.WEST);
		messagePanel.setBorder(Spacing.bMedium());
		toast.add(messagePanel);
		
		
		toastJLayer = new JLayer<JComponent>(toast);
		toastJLayer.setFocusable(false);
		
	}
	
	private JLabel makeLabel(String message) {
		JLabel label = new JLabel(message);
		label.setBorder(Spacing.bSmall());
		label.setFocusable(false);
		label.setOpaque(false);
		label.setForeground(Color.WHITE);
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		return label;
	}
	
	@Override
	public JLayer<JComponent> getJLayer() {
		
		fadeIn();		
		return toastJLayer;
	}

	
	private void fadeIn() {
		Timer timer = new Timer(true);
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				if (stage != 0) { return; }
				alpha += delta;
				alpha = Math.min(1f, Math.max(0, alpha));
				if (alpha == 1.0f) {
					timer.cancel();
					timer.purge();
					delay();
				}
				ToastLayer.this.toastJLayer.repaint(30);
			}}, 0, 30);
	}
	
	private void fadeOut() {
		stage=2;
		
		Timer timer = new Timer(true);
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				if (stage != 2) { return; }
				alpha -= delta;
				alpha = Math.min(1f, Math.max(0, alpha));
				if (alpha == 0.0f) {
					parent.removeLayer(ToastLayer.this);
					timer.cancel();
					timer.purge();
					stage=3;
				}
				ToastLayer.this.toastJLayer.repaint(30);
			}}, 0, 30);
	}
	
	private void delay() {
		stage=1;
		
		Timer timer = new Timer(true);
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				if (stage != 1) { return; }
				timer.cancel();
				timer.purge();
				fadeOut();
			}
		}, duration);
	}
	
	@Override
	public JPanel getComponent() {
		return toast;
	}

	@Override
	public void discard() {
		
	}
	
	@Override
	public boolean modal() {
		return false;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}
	
	


}
