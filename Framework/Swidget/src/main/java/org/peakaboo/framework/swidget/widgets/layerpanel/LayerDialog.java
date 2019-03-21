package org.peakaboo.framework.swidget.widgets.layerpanel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.peakaboo.framework.swidget.icons.IconSize;
import org.peakaboo.framework.swidget.icons.StockIcon;
import org.peakaboo.framework.swidget.widgets.Spacing;
import org.peakaboo.framework.swidget.widgets.buttons.ImageButton;
import org.peakaboo.framework.swidget.widgets.layout.ButtonBox;
import org.peakaboo.framework.swidget.widgets.layout.HeaderBox;


public class LayerDialog {

	public enum MessageType {
		ERROR, WARNING, INFO, QUESTION; 
	}
	
	private String title;
	private JComponent body;
	private MessageType messageType;
	private List<JButton> leftButtons = new ArrayList<>(), rightButtons = new ArrayList<>();
	private Runnable hider = () -> {};
	private boolean header = false;
	private ImageButton defaultButton;
	
	public LayerDialog(String title, String body, MessageType messageType) {
		this(title, buildBodyComponent(body), messageType);
	}
	
	public LayerDialog(String title, JComponent body, MessageType messageType) {
		this.title = title;
		this.body = body;
		this.messageType = messageType;
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
		JDialog dialog = new JDialog(frame);
		hider = () -> dialog.setVisible(false);
		dialog.setTitle(this.title);
		dialog.setModal(true);
		dialog.setContentPane(buildPanel(false));
		
		dialog.pack();
		dialog.setLocationRelativeTo(frame);
		dialog.setAlwaysOnTop(alwaysOnTop);
		dialog.setVisible(true);
		
	}
	
	private void showInLayer(LayerPanel owner) {
		JPanel panel = buildPanel(header);
		Layer layer = new ModalLayer(owner, panel);
		owner.pushLayer(layer);
		if (defaultButton != null) { defaultButton.grabFocus(); }
		hider = () -> owner.removeLayer(layer);
	}
	
	private JPanel buildPanel(boolean selfcontained) {
		JPanel panel = new JPanel(new BorderLayout());
		int pad = Spacing.huge * 2;

		JPanel center = new JPanel(new BorderLayout(pad, pad));
		center.setBorder(new EmptyBorder(pad, pad, pad, pad));
		JLabel lblTitle = new JLabel("<html><span style='font-size: 175%; font-weight: bold;'>" + title + "</span></html>");
		lblTitle.setVerticalAlignment(JLabel.TOP);
		center.add(lblTitle, BorderLayout.NORTH);
		center.add(this.body, BorderLayout.CENTER);
		
		panel.add(center, BorderLayout.CENTER);
		
		JLabel icon = new JLabel(getBadge());
		icon.setVerticalAlignment(JLabel.TOP);
		icon.setHorizontalAlignment(JLabel.CENTER);
		icon.setBorder(new EmptyBorder(pad, pad, pad, 0));
		panel.add(icon, BorderLayout.WEST);

		
		if (!selfcontained) {
			panel.add(buildButtonBox(), BorderLayout.SOUTH);
		} else {			
			panel.add(buildHeaderBox(), BorderLayout.NORTH);
		}
		
		
		return panel;
	}
	
	
	private static JComponent buildBodyComponent(String body) {
		int pad = Spacing.huge * 2;
		JLabel lblBody = new JLabel("<html>" + body.replace("\n",	"<br/>") + "</html>");
		lblBody.setVerticalAlignment(JLabel.TOP);
		return lblBody;
	}
	
	private ButtonBox buildButtonBox() {
		ButtonBox box = new ButtonBox();
		for (JButton b : leftButtons) {
			box.addLeft(b);
		}
		for (JButton b : rightButtons) {
			box.addRight(b);
		}
		
		if (leftButtons.size() == 0 && rightButtons.size() == 0) {
			defaultButton = new ImageButton("OK").withAction(() -> hide());
			box.addRight(defaultButton);
		}
		
		return box;
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
			defaultButton = new ImageButton("OK").withAction(() -> hide());
			right = defaultButton;
		}
		
		HeaderBox box = new HeaderBox(left, this.title, right);
		return box;
	}
	
	private ImageIcon getBadge() {
		switch (this.messageType) {
		case ERROR: 	return StockIcon.BADGE_ERROR.toImageIcon(IconSize.ICON);
		case WARNING:	return StockIcon.BADGE_WARNING.toImageIcon(IconSize.ICON);
		case INFO:		return StockIcon.BADGE_INFO.toImageIcon(IconSize.ICON);
		case QUESTION:	return StockIcon.BADGE_HELP.toImageIcon(IconSize.ICON);
		default:		return StockIcon.BADGE_HELP.toImageIcon(IconSize.ICON);
		}
	}
	
	public void hide() {
		hider.run();
	}
		
}
