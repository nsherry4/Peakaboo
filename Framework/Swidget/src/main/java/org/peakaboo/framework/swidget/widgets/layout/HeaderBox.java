package org.peakaboo.framework.swidget.widgets.layout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager2;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import org.peakaboo.framework.stratus.Stratus;
import org.peakaboo.framework.stratus.theme.Theme;
import org.peakaboo.framework.swidget.Swidget;
import org.peakaboo.framework.swidget.hookins.WindowDragger;
import org.peakaboo.framework.swidget.icons.StockIcon;
import org.peakaboo.framework.swidget.widgets.ClearPanel;
import org.peakaboo.framework.swidget.widgets.Spacing;
import org.peakaboo.framework.swidget.widgets.fluent.button.FluentButton;
import org.peakaboo.framework.swidget.widgets.fluent.button.FluentButtonSize;

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
		
		if (Swidget.isStratusLaF()) {
			theme = Stratus.getTheme();
		}
		
		
		dragger = new WindowDragger(HeaderBox.this);
		
	}
	
	
	protected Paint getBackgroundPaint() {
		return theme.getNegative();
	}
	

	@Override
	public void paintComponent(Graphics g)
	{
		
		super.paintComponent(g);
		
		g = g.create();
		Graphics2D g2 = (Graphics2D) g;
		
		Paint paint = getBackgroundPaint();
		if (paint != null) {
			g2.setPaint(paint);
		} else {
			g2.setColor(UIManager.getColor("control"));

		}
		g2.fillRect(0, 0, getWidth(), getHeight());

	}
	
	private float getCurve() {
		if (theme != null) {
			return theme.widgetCurve();
		} else {
			return 0.05f;
		}
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
					new MatteBorder(0, 1, 0, 0, Swidget.dividerColor()), 
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
		b = new CompoundBorder(new MatteBorder(0, 0, 1, 0, Swidget.dividerColor()), b);
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
	
	
	
	private static Color lighten(Color src, float amount) {
    	float[] hsb = new float[3];
    	Color.RGBtoHSB(src.getRed(), src.getGreen(), src.getBlue(), hsb);
    	hsb[2] = Math.min(1f, hsb[2] + amount);
    	return new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
    }
	
	private static Color darken(Color src, float amount) {
    	float[] hsb = new float[3];
    	Color.RGBtoHSB(src.getRed(), src.getGreen(), src.getBlue(), hsb);
    	hsb[2] = Math.max(0f, hsb[2] - amount);
    	return new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
    }

	public static FluentButton closeButton() {
		FluentButton close = new FluentButton()
				.withTooltip("Close")
				.withIcon(StockIcon.WINDOW_CLOSE, true)
				.withButtonSize(FluentButtonSize.LARGE)
				.withBordered(false);
		return close;
	}
	
	private class HeaderLayout implements LayoutManager2 {

		Component left, centre, right;

		public HeaderLayout(Component left, Component centre, Component right) {
			this.left = left;
			this.centre = centre;
			this.right = right;
		}

		@Override
		public void addLayoutComponent(String name, Component comp) {
			// TODO Auto-generated method stub
		}

		@Override
		public void removeLayoutComponent(Component comp) {
			// TODO Auto-generated method stub
		}

		@Override
		public Dimension preferredLayoutSize(Container parent) {
			Dimension d = new Dimension();
			
			d.width += sideSize();
			d.width += centre.getPreferredSize().width;
			d.width += sideSize();
			
			d.height = left == null ? 0 : left.getPreferredSize().height;
			d.height = Math.max(d.height, centre == null ? 0 : centre.getPreferredSize().height);
			d.height = Math.max(d.height, right == null ? 0 : right.getPreferredSize().height);
			
			return d;
		}

		@Override
		public Dimension minimumLayoutSize(Container parent) {
			Dimension d = new Dimension();
			
			d.width += sideSize();
			d.width += sideSize();
			
			d.height = left == null ? 0 : left.getPreferredSize().height;
			d.height = Math.max(d.height, centre == null ? 0 : centre.getPreferredSize().height);
			d.height = Math.max(d.height, right == null ? 0 : right.getPreferredSize().height);
			
			return d;
		}

		@Override
		public void layoutContainer(Container parent) {
			Dimension leftSize = left == null ? new Dimension(0, 0) : new Dimension(left.getPreferredSize());
			Dimension rightSize = right == null ? new Dimension(0, 0) : new Dimension(right.getPreferredSize());
			
			float lHalfDelta = Math.max(0, parent.getHeight() - leftSize.height) / 2f;
			float rHalfDelta = Math.max(0, parent.getHeight() - rightSize.height) / 2f;
			
			if (left != null)   left.setBounds(0, (int)lHalfDelta, leftSize.width, leftSize.height);
			if (centre != null) centre.setBounds(sideSize(), 0, parent.getWidth() - sideSize() - sideSize(), parent.getHeight());
			if (right != null)  right.setBounds(parent.getWidth() - rightSize.width, (int)rHalfDelta, rightSize.width, rightSize.height);

		}

		@Override
		public void addLayoutComponent(Component comp, Object constraints) {
			// TODO Auto-generated method stub
		}

		@Override
		public Dimension maximumLayoutSize(Container target) {
			return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
		}

		@Override
		public float getLayoutAlignmentX(Container target) {
			return 0;
		}

		@Override
		public float getLayoutAlignmentY(Container target) {
			return 0;
		}

		@Override
		public void invalidateLayout(Container target) {

		}
		
				
		private int sideSize() {
			int leftWidth = left == null ? 0 : left.getPreferredSize().width;
			int rightWidth = right == null ? 0 : right.getPreferredSize().width;
			return Math.max(leftWidth, rightWidth);
		}

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
