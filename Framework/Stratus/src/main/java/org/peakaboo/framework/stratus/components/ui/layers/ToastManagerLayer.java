package org.peakaboo.framework.stratus.components.ui.layers;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayer;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.icons.IconSize;
import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.panels.ClearPanel;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;

/**
 * A layer that manages multiple toast notifications, displaying them in a
 * vertical stack centered from the top of the window.
 */
public class ToastManagerLayer implements Layer {

	private final LayerPanel parent;
	private final JPanel container;
	private final JPanel toastStack;
	private final JLayer<JComponent> layer;
	private final List<ToastMessage> messages = new ArrayList<>();

	private static final int DEFAULT_DURATION = 5000;
	private static final float DELTA_ATTACK = 0.15f;
	private static final float DELTA_DECAY = 0.05f;

	public ToastManagerLayer(LayerPanel parent) {
		this.parent = parent;

		// Create vertical stack for toasts
		toastStack = new ClearPanel();
		toastStack.setLayout(new BoxLayout(toastStack, BoxLayout.Y_AXIS));

		// Use GridBagLayout for precise centering without expansion
		container = new ClearPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.weighty = 1.0; // Push to top
		gbc.weightx = 0.0; // Don't expand horizontally
		container.add(toastStack, gbc);

		layer = new JLayer<>(container);
		layer.setFocusable(false);
	}

	/**
	 * Add a new toast message
	 */
	public void addToast(String message) {
		addToast(message, () -> {});
	}

	/**
	 * Add a new toast message with a click action
	 */
	public void addToast(String message, Runnable onClick) {
		ToastMessage toast = new ToastMessage(message, onClick);
		messages.add(toast);
		toastStack.add(toast.panel);
		if (messages.size() > 1) {
			toastStack.add(Box.createVerticalStrut(Spacing.medium));
		}
		toastStack.revalidate();
		toastStack.repaint();
		toast.fadeIn();
	}

	private void removeToast(ToastMessage toast) {
		messages.remove(toast);

		// Remove the panel and its spacing
		int index = -1;
		for (int i = 0; i < toastStack.getComponentCount(); i++) {
			if (toastStack.getComponent(i) == toast.panel) {
				index = i;
				break;
			}
		}

		if (index >= 0) {
			toastStack.remove(index);
			// Remove spacing after this toast if it exists
			if (index < toastStack.getComponentCount()) {
				Component next = toastStack.getComponent(index);
				if (next instanceof Box.Filler) {
					toastStack.remove(index);
				}
			}
		}

		toastStack.revalidate();
		toastStack.repaint();

		// If no more toasts, remove this layer from parent
		if (messages.isEmpty()) {
			parent.removeLayer(this);
		}
	}

	@Override
	public JLayer<JComponent> getJLayer() {
		return layer;
	}

	@Override
	public JComponent getContent() {
		return container;
	}

	@Override
	public JComponent getOuterComponent() {
		return container;
	}

	@Override
	public void discard() {
		// Clean up any running timers
		for (ToastMessage message : new ArrayList<>(messages)) {
			message.cleanup();
		}
		messages.clear();
	}

	@Override
	public boolean modal() {
		return false;
	}

	/**
	 * Inner class representing a single toast message with its own lifecycle
	 */
	private class ToastMessage {
		private final String message;
		private final Runnable onClick;
		private final JPanel panel;
		private final JComponent label;
		private final FluentButton closeButton;

		private float alpha = 0f;
		private int stage = 0; // 0=fading in, 1=waiting, 2=fading out, 3=done
		private Timer fadeInTimer;
		private Timer delayTimer;
		private Timer fadeOutTimer;

		ToastMessage(String message, Runnable onClick) {
			this.message = message;
			this.onClick = onClick;

			// Create the message panel with rounded background
			JPanel messagePanel = new ClearPanel(new BorderLayout()) {
				@Override
				protected void paintComponent(Graphics g) {
					Graphics2D g2d = (Graphics2D) g;
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

					int paintAlpha = (int)(0xB0 * alpha);
					g.setColor(new Color(paintAlpha << 24, true));
					g.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

					super.paintComponent(g);
				}
			};

			messagePanel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					onClick.run();
				}
			});

			// Create label
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

			// Create close button
			closeButton = new FluentButton()
					.withIcon(StockIcon.WINDOW_CLOSE, IconSize.BUTTON, Color.WHITE)
					.withAction(this::fadeOut)
					.withBordered(false)
					.withBorder(Spacing.bMedium());
			closeButton.setVerticalAlignment(SwingConstants.CENTER);
			closeButton.setFocusable(false);
			closeButton.setToolTipText(null);

			messagePanel.add(label, BorderLayout.CENTER);
			messagePanel.add(closeButton, BorderLayout.WEST);
			messagePanel.setBorder(Spacing.bMedium());

			panel = new ClearPanel(new java.awt.FlowLayout());
			panel.add(messagePanel);
			panel.setFocusable(false);
		}

		private JLabel makeLabel(String text) {
			JLabel label = new JLabel(text);
			label.setBorder(Spacing.bSmall());
			label.setFocusable(false);
			label.setOpaque(false);
			label.setForeground(Color.WHITE);
			label.setHorizontalAlignment(JLabel.CENTER);
			label.setAlignmentX(Component.CENTER_ALIGNMENT);
			return label;
		}

		private void fadeIn() {
			stage = 0;
			fadeInTimer = new Timer(30, e -> {
				if (stage != 0) {
					return;
				}
				alpha += DELTA_ATTACK;
				alpha = Math.min(1f, Math.max(0, alpha));
				if (alpha >= 1.0f) {
					fadeInTimer.stop();
					delay();
				}
				panel.repaint();
			});
			fadeInTimer.start();
		}

		private void delay() {
			stage = 1;
			delayTimer = new Timer(DEFAULT_DURATION, e -> {
				if (stage != 1) {
					return;
				}
				fadeOut();
			});
			delayTimer.setRepeats(false);
			delayTimer.start();
		}

		private void fadeOut() {
			stage = 2;
			if (delayTimer != null && delayTimer.isRunning()) {
				delayTimer.stop();
			}

			fadeOutTimer = new Timer(30, e -> {
				if (stage != 2) {
					return;
				}
				alpha -= DELTA_DECAY;
				alpha = Math.min(1f, Math.max(0, alpha));
				if (alpha <= 0.0f) {
					fadeOutTimer.stop();
					stage = 3;
					removeToast(ToastMessage.this);
				}
				panel.repaint();
			});
			fadeOutTimer.start();
		}

		private void cleanup() {
			if (fadeInTimer != null) {
				fadeInTimer.stop();
			}
			if (delayTimer != null) {
				delayTimer.stop();
			}
			if (fadeOutTimer != null) {
				fadeOutTimer.stop();
			}
		}
	}
}