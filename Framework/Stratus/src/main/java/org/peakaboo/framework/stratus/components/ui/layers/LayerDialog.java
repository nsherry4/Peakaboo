package org.peakaboo.framework.stratus.components.ui.layers;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.icons.IconSize;
import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.ButtonBox;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;
import org.peakaboo.framework.stratus.components.ui.header.HeaderBox;
import org.peakaboo.framework.stratus.components.ui.live.LiveDialog;


public class LayerDialog {

	private String title;
	private JComponent body;
	private List<JButton> leftButtons = new ArrayList<>(), rightButtons = new ArrayList<>();
	private Runnable hider = () -> {};
	private FluentButton defaultButton;
	private ImageIcon icon;

	
	public LayerDialog(String title, String body) {
		this(title, body, (ImageIcon)null);
	}
	
	public LayerDialog(String title, String body, StockIcon icon) {
		this(title, body, icon.toImageIcon(IconSize.ICON));
	}
	
	public LayerDialog(String title, String body, ImageIcon icon) {
		this(title, buildBodyComponent(body), icon);
	}

	
	public LayerDialog(String title, JComponent body) {
		this(title, body, (ImageIcon)null);
	}
	
	public LayerDialog(String title, JComponent body, StockIcon icon) {
		this(title, body, icon.toImageIcon(IconSize.ICON));
	}
	
	public LayerDialog(String title, JComponent body, ImageIcon icon) {
		this.title = title;
		this.body = body;
		this.icon = icon;
	}
	
	
	public LayerDialog addLeft(JButton button) {
		leftButtons.add(button);
		button.addActionListener(e -> hide());
		return this;
	}

	public LayerDialog addRight(JButton button) {
		rightButtons.add(button);
		button.addActionListener(e -> hide());
		return this;
	}
	

	public void showIn(LayerPanel owner) {
		if (owner == null) {
			showInWindow(null);
		} else {
			showInLayer(owner);
		}
	}
		
	public void showInWindow(Window frame) {
		showInWindow(frame, false);
	}
	
	public void showInWindow(Window frame, boolean alwaysOnTop) {
		JDialog dialog = new LiveDialog(frame);
		hider = () -> dialog.setVisible(false);
		dialog.setTitle(this.title);
		dialog.setModal(true);
		dialog.setUndecorated(true);
		dialog.setContentPane(buildPanel());
		
		dialog.pack();
		dialog.setLocationRelativeTo(frame);
		dialog.setAlwaysOnTop(alwaysOnTop);
		dialog.setVisible(true);
		
	}
	
	private void showInLayer(LayerPanel owner) {
		JPanel panel = buildPanel();
		Layer layer = new ModalLayer(owner, panel);
		owner.pushLayer(layer);
		if (defaultButton != null) { defaultButton.grabFocus(); }
		hider = () -> owner.removeLayer(layer);
	}
	
	private JPanel buildPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		int pad = Spacing.huge * 2;

		JPanel center = new JPanel(new BorderLayout(pad, pad));
		center.setBorder(new EmptyBorder(pad, pad, pad, pad));
		center.add(this.body, BorderLayout.CENTER);
		panel.add(center, BorderLayout.CENTER);
		
		JLabel lblIcon = new JLabel();
		lblIcon.setHorizontalAlignment(JLabel.CENTER);
		lblIcon.setBorder(new EmptyBorder(pad, pad, pad, 0));
		if (icon != null) {
			lblIcon.setIcon(icon);
			panel.add(lblIcon, BorderLayout.WEST);
		}
			

		if (icon != null) {
			center.add(Box.createVerticalGlue(), BorderLayout.NORTH);
			center.add(Box.createVerticalGlue(), BorderLayout.SOUTH);
		}
		lblIcon.setVerticalAlignment(JLabel.CENTER);
		
		panel.add(buildHeaderBox(), BorderLayout.NORTH);
	
		
		
		return panel;
	}
	
	
	private static JComponent buildBodyComponent(String body) {
		int pad = Spacing.huge * 2;
		JLabel lblBody = new JLabel("<html>" + body.replace("\n",	"<br/>") + "</html>");
		lblBody.setVerticalAlignment(JLabel.CENTER);
		return lblBody;
	}
	
	
	private HeaderBox buildHeaderBox() {
		Component left, right;
		
		if (leftButtons.size() == 0) {
			left = null;
		} else if (leftButtons.size() == 1) {
			left = leftButtons.get(0);
		} else {
			ButtonBox leftBox = new ButtonBox(Spacing.small, false);
			for (JButton b : leftButtons) {
				leftBox.addLeft(b);
			}
			left = leftBox;
		}
		
		if (rightButtons.size() == 0) {
			right = null;
		} else if (rightButtons.size() == 1) {
			right = rightButtons.get(0);
		} else {
			ButtonBox rightBox = new ButtonBox(Spacing.small, false);
			for (JButton b : rightButtons) {
				rightBox.addRight(b);
			}
			right = rightBox;
		}
		
		if (left == null && right == null) {
			defaultButton = new FluentButton("OK").withStateDefault().withAction(() -> hide());
			right = defaultButton;
		}
		
		HeaderBox box = new HeaderBox(left, this.title, right);
		return box;
	}

	
	public void hide() {
		hider.run();
	}
		
}
