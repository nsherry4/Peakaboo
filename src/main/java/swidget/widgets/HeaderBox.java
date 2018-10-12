package swidget.widgets;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.LayoutManager2;
import java.awt.LinearGradientPaint;

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

import swidget.icons.StockIcon;
import swidget.widgets.buttons.ImageButton;
import swidget.widgets.buttons.ImageButtonSize;
import swidget.widgets.gradientpanel.PaintedPanel;

public class HeaderBox extends PaintedPanel {

	private Color base;
	
	public HeaderBox(Component left, String title, Component right) {
		super(true);
		JLabel label = new JLabel(title);
		if (left != null || right != null) {
			label.setBorder(new EmptyBorder(0, Spacing.huge, 0, Spacing.huge));
		}
		Font font = label.getFont();
		font = font.deriveFont(Font.BOLD);
		font = font.deriveFont(font.getSize() + 1f);
		label.setFont(font);
		label.setHorizontalAlignment(JLabel.CENTER);
		
		init(left, label, right);
		
	}
	
	public HeaderBox(Component left, Component centre, Component right) {
		super(true);
		init(left, centre, right);
	}
	
	private void init(Component left, Component centre, Component right) {
		
		base = getBackground();
		
		setBorder(Spacing.bMedium());
		setLayout(new BorderLayout());
		JPanel inner = new ClearPanel();
		add(inner, BorderLayout.CENTER);
				
		HeaderLayout layout = new HeaderLayout(left, centre, right);
		inner.setLayout(layout);
		if (left != null) inner.add(left);
		if (centre != null) inner.add(centre);
		if (right != null) inner.add(right);
		
		setBackgroundPaint(new LinearGradientPaint(0, 0, 0, this.getPreferredSize().height, new float[] {0, 1f}, new Color[] {
			lighten(base, 0.05f),
			base
		}));
		
		Border b = Spacing.bMedium();
		b = new CompoundBorder(new MatteBorder(0, 0, 1, 0, new Color(0x20000000, true)), b);
		b = new CompoundBorder(new MatteBorder(0, 0, 1, 0, new Color(0x60000000, true)), b);
		setBorder(b);
		
	}
	
	private static Color lighten(Color src, float amount) {
    	float[] hsb = new float[3];
    	Color.RGBtoHSB(src.getRed(), src.getGreen(), src.getBlue(), hsb);
    	hsb[2] = Math.min(1f, hsb[2] + amount);
    	return new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
    }

	public static ImageButton closeButton() {
		ImageButton close = new ImageButton()
				.withTooltip("Close")
				.withIcon(StockIcon.WINDOW_CLOSE)
				.withButtonSize(ImageButtonSize.LARGE)
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
		
		HeaderBox box = new HeaderBox(null, "Title Text", new ImageButton("Right Side of the Window"));
		frame.add(box, BorderLayout.NORTH);
		
		frame.setVisible(true);
		
	}
	
}
