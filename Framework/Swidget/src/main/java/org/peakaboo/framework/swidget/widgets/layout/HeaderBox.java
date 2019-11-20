package org.peakaboo.framework.swidget.widgets.layout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.LayoutManager2;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import org.peakaboo.framework.swidget.icons.StockIcon;
import org.peakaboo.framework.swidget.widgets.ClearPanel;
import org.peakaboo.framework.swidget.widgets.PaintedPanel;
import org.peakaboo.framework.swidget.widgets.Spacing;
import org.peakaboo.framework.swidget.widgets.buttons.ImageButton;
import org.peakaboo.framework.swidget.widgets.buttons.ImageButtonSize;

public class HeaderBox extends PaintedPanel {
	
	
	
	private Component left, centre, right;
	private Component close;
	private JPanel rightWrap, closePanel;
	private Runnable onClose;
	boolean showClose;
	
	private Theme theme = null;
	
	//dragging
	private Point initialClick;
	private boolean dragable = true;
	
	
	public HeaderBox(Component left, String title, Component right) {
		super(true);
		this.left = left;
		this.right = right;
		this.centre = makeHeaderTitle(title);
		
		init();
		make();
	}
	
	public HeaderBox(Component left, Component centre, Component right) {
		super(true);
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
		
		addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				initialClick = null;
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				initialClick = e.getPoint();
			}

		});
		
		addMouseMotionListener(new MouseMotionAdapter() {
			
			@Override
			public void mouseDragged(MouseEvent e) {
				if(!SwingUtilities.isLeftMouseButton(e)) {
					return;
				}
				
				if (initialClick == null || !dragable) {
					return;
				}
								
				Window parent = SwingUtilities.getWindowAncestor(HeaderBox.this);
				
				//dont' move parent window if it's maximized
				if (parent instanceof JFrame) {
					JFrame parentJFrame = (JFrame) parent;
					if ((parentJFrame.getExtendedState() & JFrame.MAXIMIZED_HORIZ) == JFrame.MAXIMIZED_HORIZ ||
						(parentJFrame.getExtendedState() & JFrame.MAXIMIZED_VERT) == JFrame.MAXIMIZED_VERT) {
						return;
					}
				}
				
				//Thanks https://stackoverflow.com/questions/9650874/java-swing-obtain-window-jframe-from-inside-a-jpanel
	            // get location of Window
	            int thisX = parent.getX();
	            int thisY = parent.getY();
	            
	            // Determine how much the mouse moved since the initial click
	            int xMoved = e.getX() - initialClick.x;
	            int yMoved = e.getY() - initialClick.y;

	            // Move window to this position
	            int X = thisX + xMoved;
	            int Y = thisY + yMoved;
	            parent.setLocation(X, Y);
			}
		});
		
		
	}
	
	
	@Override
	public Paint getBackgroundPaint() {
		Color base = getBackground();
		float curve = 0.05f;
		
		if (theme != null) {
			if (Stratus.focusedWindow(this)) {
				base = theme.getNegative();
				curve = theme.widgetCurve();
			} else {
				base = theme.getControl();
				curve = 0f;
			}
		}
		
		return new LinearGradientPaint(0, 0, 0, this.getPreferredSize().height, new float[] {0, 1f}, new Color[] {
				lighten(base, curve/2f),
				darken(base, curve/2f)
			});
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
		if (isDrawBackground()) {
			Color borderColour = new Color(0x20000000, true);
			if (Swidget.isStratusLaF()) {
				borderColour = Stratus.getTheme().getWidgetBorderAlpha();
			}
			b = new CompoundBorder(new MatteBorder(0, 0, 1, 0, borderColour), b);
		} else {
			b = new CompoundBorder(new EmptyBorder(0, 0, 1, 0), b);
		}
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
	
	@Override
	public void setDrawBackground(boolean drawBackground) {
		super.setDrawBackground(drawBackground);
		make();
	}
	
	
	
	
	
	public boolean isDragable() {
		return dragable;
	}

	public void setDragable(boolean dragable) {
		this.dragable = dragable;
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
		ImageButton yes = new ImageButton(yesString).withStateDefault().withAction(yesAction);
		ImageButton no = new ImageButton(noString).withAction(noAction);
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

	public void setOnClose(Runnable onClose) {
		this.onClose = onClose;
	}

}
