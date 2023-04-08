package org.peakaboo.framework.stratus.components.ui.header;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.api.hookins.WindowDragger;
import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.panels.ClearPanel;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButtonSize;
import org.peakaboo.framework.stratus.laf.theme.Theme;

public class HeaderBox extends JPanel {
	
	private Component left, centre, right;
	private Component close;
	private JPanel rightWrap, closePanel;
	private Runnable onClose;
	boolean showClose;
	
	private Theme theme = null;
	private WindowDragger dragger;
	
	
	public HeaderBox(Component left, String title, Component right) {
		this.left = left;
		this.right = right;
		this.centre = makeHeaderTitle(title);
		
		init();
		make();
	}
	
	public HeaderBox(Component left, Component centre, Component right) {
		this.left = left;
		this.centre = centre;
		this.right = right;
		
		init();
		make();
	}
	
	public HeaderBox() {
		this(null, "", null);
	}
	
	private void init() {	
		rightWrap = new ClearPanel(new BorderLayout(Spacing.medium, Spacing.medium));
		close = HeaderBox.closeButton().withAction(() -> onClose.run());
		closePanel = new ClearPanel(new BorderLayout());
		closePanel.add(close, BorderLayout.CENTER);
		
		theme = Stratus.getTheme();
						
		dragger = new WindowDragger(HeaderBox.this);
		
	}
	

	@Override
	public void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		
		g = g.create();
		Graphics2D g2 = (Graphics2D) g;
		
		Paint paint = theme.getNegative();
		if (paint != null) {
			g2.setPaint(paint);
		} else {
			g2.setColor(UIManager.getColor("control"));

		}
		g2.fillRect(0, 0, getWidth(), getHeight());

	}
	
	
	private void make() {
		
		removeAll();	
		
		setBorder(Spacing.bMedium());
		setLayout(new BorderLayout());
		JPanel inner = new ClearPanel();
		add(inner, BorderLayout.CENTER);
		
		rightWrap.removeAll();
		if (right != null) {
			rightWrap.add(right, BorderLayout.CENTER);
			closePanel.setBorder(new CompoundBorder(
					new MatteBorder(0, 1, 0, 0, Stratus.getTheme().getWidgetBorder()), 
					new EmptyBorder(0, Spacing.medium, 0, 0)));

			
		} else {
			closePanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		}
		if (showClose) {
			rightWrap.add(closePanel, BorderLayout.EAST);
		}
		
		HeaderLayout layout = new HeaderLayout(left, centre, rightWrap);
		inner.setLayout(layout);
		if (left != null) inner.add(left);
		if (centre != null) inner.add(centre);
		if (right != null || showClose) { 
			inner.add(rightWrap);
		}
		
		Border b = Spacing.bMedium();
		b = new CompoundBorder(new MatteBorder(0, 0, 1, 0, Stratus.getTheme().getWidgetBorder()), b);
		setBorder(b);
		
	}

	

	public void setComponents(Component left, Component centre, Component right) {
		this.left = left;
		this.centre = centre;
		this.right = right;
		make();
	}
	
	public void setComponents(Component left, String centre, Component right) {
		this.left = left;
		this.centre = makeHeaderTitle(centre);
		this.right = right;
		make();
	}
	
	public Component getLeft() {
		return left;
	}

	public void setLeft(Component left) {
		this.left = left;
		make();
	}

	public Component getCentre() {
		return centre;
	}

	public void setCentre(String title) {
		setCentre(makeHeaderTitle(title));
	}
	
	public void setCentre(Component centre) {
		this.centre = centre;
		make();
	}

	public Component getRight() {
		return right;
	}

	public void setRight(Component right) {
		this.right = right;
		make();
	}

	public void setShowClose(boolean show) {
		this.showClose = show;
		make();
	}
	
	public boolean getShowClose() {
		return showClose;
	}

	
	
	
	public boolean isDraggable() {
		return dragger.isDraggable();
	}

	public void setDragable(boolean draggable) {
		this.dragger.setDraggable(draggable);
	}

	public static JLabel makeHeaderTitle(String title) {
		JLabel label = new JLabel(title);
		Font font = label.getFont();
		font = font.deriveFont(Font.BOLD);
		font = font.deriveFont(font.getSize() + 1f);
		label.setFont(font);
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setBorder(new EmptyBorder(0, Spacing.huge, 0, Spacing.huge));
		return label;
	}
	

	
	public static HeaderBox createYesNo(String title, String yesString, Runnable yesAction, String noString, Runnable noAction) {
		FluentButton yes = new FluentButton(yesString).withStateDefault().withAction(yesAction);
		FluentButton no = new FluentButton(noString).withAction(noAction);
		return new HeaderBox(no, title, yes);
	}
	
	
	public static FluentButton closeButton() {
		FluentButton close = new FluentButton()
				.withTooltip("Close")
				.withIcon(StockIcon.WINDOW_CLOSE)
				.withButtonSize(FluentButtonSize.LARGE)
				.withBordered(false);
		return close;
	}
	
	public static void main(String[] args) throws UnsupportedLookAndFeelException {
		
		UIManager.setLookAndFeel(new NimbusLookAndFeel());
		
		JFrame frame = new JFrame("test");
		frame.setPreferredSize(new Dimension(640, 480));
		frame.pack();
		frame.getContentPane().setLayout(new BorderLayout());
		
		HeaderBox box = new HeaderBox(null, "Title Text", new FluentButton("Right Side of the Window"));
		frame.add(box, BorderLayout.NORTH);
		
		frame.setVisible(true);
		
	}

	public void setOnClose(Runnable onClose) {
		this.onClose = onClose;
	}

}
