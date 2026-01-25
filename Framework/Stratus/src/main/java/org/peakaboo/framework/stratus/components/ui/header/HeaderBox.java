package org.peakaboo.framework.stratus.components.ui.header;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.api.hookins.WindowDragger;
import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.panels.ClearPanel;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButtonSize;
import org.peakaboo.framework.stratus.laf.theme.Theme;

/**
 * A themed header bar component with left, centre, and right sections.
 * <p>
 * HeaderBox provides a consistent header UI with three configurable sections arranged
 * horizontally. It supports an optional close button and can enable window dragging for
 * undecorated windows.
 * </p>
 * <p>
 * <strong>Related Components:</strong>
 * </p>
 * <ul>
 * <li>{@link HeaderPanel} - Combines a HeaderBox with a body component</li>
 * <li>{@link HeaderDialog} - A modal dialog with a HeaderPanel</li>
 * <li>{@link HeaderFrame} - A top-level window with a HeaderPanel</li>
 * <li>{@link HeaderLayer} - An in-window overlay with a HeaderPanel</li>
 * </ul>
 */
public class HeaderBox extends JPanel {
	
	private Component left, centre, right;
	private Component close;
	private JPanel rightWrap, closePanel;
	private Runnable onClose = () -> {};
	boolean showClose;

	private Theme theme = null;
	private WindowDragger dragger;

	private boolean needsRebuild = false;


	/**
	 * Creates a HeaderBox with the specified components.
	 *
	 * @param left the component for the left section, or null
	 * @param title the text for the centre section
	 * @param right the component for the right section, or null
	 */
	public HeaderBox(Component left, String title, Component right) {
		this.left = left;
		this.right = right;
		this.centre = makeHeaderTitle(title);

		init();
		rebuild();
	}

	/**
	 * Creates a HeaderBox with the specified components.
	 *
	 * @param left the component for the left section, or null
	 * @param centre the component for the centre section, or null
	 * @param right the component for the right section, or null
	 */
	public HeaderBox(Component left, Component centre, Component right) {
		this.left = left;
		this.centre = centre;
		this.right = right;

		init();
		rebuild();
	}

	/**
	 * Creates an empty HeaderBox with no components.
	 */
	public HeaderBox() {
		this(null, "", null);
	}
	
	private void init() {	
		rightWrap = new ClearPanel(new BorderLayout(Spacing.medium, Spacing.medium));
		close = HeaderBox.makeCloseButton().withAction(() -> onClose.run());
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

	@Override
	public void doLayout() {
		if (needsRebuild) {
			rebuild();
			needsRebuild = false;
		}
		super.doLayout();
	}

	private void scheduleRebuild() {
		needsRebuild = true;
		revalidate();
		repaint();
	}

	/**
	 * Ensures any pending rebuild is executed immediately.
	 * This is called automatically by HeaderDialog and HeaderFrame before packing.
	 */
	void ensureBuilt() {
		if (needsRebuild) {
			rebuild();
			needsRebuild = false;
		}
	}


	private void rebuild() {
		
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



	/**
	 * Sets all three sections of the header at once.
	 *
	 * @param left the component for the left section, or null
	 * @param centre the component for the centre section, or null
	 * @param right the component for the right section, or null
	 */
	public void setComponents(Component left, Component centre, Component right) {
		this.left = left;
		this.centre = centre;
		this.right = right;
		scheduleRebuild();
	}

	/**
	 * Sets all three sections of the header at once, with text for the centre.
	 *
	 * @param left the component for the left section, or null
	 * @param centre the text for the centre section
	 * @param right the component for the right section, or null
	 */
	public void setComponents(Component left, String centre, Component right) {
		this.left = left;
		this.centre = makeHeaderTitle(centre);
		this.right = right;
		scheduleRebuild();
	}

	/**
	 * Gets the component in the left section.
	 *
	 * @return the left component, or null if not set
	 */
	public Component getLeft() {
		return left;
	}

	/**
	 * Sets the component for the left section.
	 *
	 * @param left the component to display, or null to clear
	 */
	public void setLeft(Component left) {
		this.left = left;
		scheduleRebuild();
	}

	/**
	 * Gets the component in the centre section.
	 *
	 * @return the centre component, or null if not set
	 */
	public Component getCentre() {
		return centre;
	}

	/**
	 * Sets the centre section to display the specified text.
	 *
	 * @param title the text to display
	 */
	public void setCentre(String title) {
		setCentre(makeHeaderTitle(title));
	}

	/**
	 * Sets the component for the centre section.
	 *
	 * @param centre the component to display, or null to clear
	 */
	public void setCentre(Component centre) {
		this.centre = centre;
		scheduleRebuild();
	}

	/**
	 * Gets the component in the right section.
	 *
	 * @return the right component, or null if not set
	 */
	public Component getRight() {
		return right;
	}

	/**
	 * Sets the component for the right section.
	 *
	 * @param right the component to display, or null to clear
	 */
	public void setRight(Component right) {
		this.right = right;
		scheduleRebuild();
	}

	/**
	 * Sets whether the close button should be displayed.
	 *
	 * @param show true to show the close button, false to hide it
	 */
	public void setShowClose(boolean show) {
		this.showClose = show;
		scheduleRebuild();
	}

	/**
	 * Gets whether the close button is displayed.
	 *
	 * @return true if the close button is shown, false otherwise
	 */
	public boolean getShowClose() {
		return showClose;
	}

	/**
	 * Gets whether window dragging is enabled for this header.
	 *
	 * @return true if dragging is enabled, false otherwise
	 */
	public boolean isDraggable() {
		return dragger.isDraggable();
	}

	/**
	 * Sets whether window dragging should be enabled for this header.
	 * When enabled, clicking and dragging the header will move its containing window.
	 *
	 * @param draggable true to enable dragging, false to disable
	 */
	public void setDraggable(boolean draggable) {
		this.dragger.setDraggable(draggable);
	}

	/**
	 * Creates a styled label suitable for use as a header title.
	 *
	 * @param title the text to display
	 * @return a JLabel with header title styling
	 */
	private static JLabel makeHeaderTitle(String title) {
		JLabel label = new JLabel(title);
		Font font = label.getFont();
		font = font.deriveFont(Font.BOLD);
		font = font.deriveFont(font.getSize() + 1f);
		label.setFont(font);
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setBorder(new EmptyBorder(0, Spacing.huge, 0, Spacing.huge));
		return label;
	}

	/**
	 * Creates a HeaderBox configured for yes/no confirmation dialogs.
	 *
	 * @param title the text to display in the centre
	 * @param yesString the text for the affirmative button
	 * @param yesAction the action to run when the affirmative button is clicked
	 * @param noString the text for the negative button
	 * @param noAction the action to run when the negative button is clicked
	 * @return a configured HeaderBox with the specified buttons
	 */
	public static HeaderBox createYesNo(String title, String yesString, Runnable yesAction, String noString, Runnable noAction) {
		FluentButton yes = new FluentButton(yesString).withStateDefault().withAction(yesAction);
		FluentButton no = new FluentButton(noString).withAction(noAction);
		return new HeaderBox(no, title, yes);
	}

	/**
	 * Creates a styled close button suitable for use in headers.
	 *
	 * @return a FluentButton configured as a close button
	 */
	private static FluentButton makeCloseButton() {
		FluentButton close = new FluentButton()
				.withTooltip("Close")
				.withIcon(StockIcon.WINDOW_CLOSE)
				.withButtonSize(FluentButtonSize.LARGE)
				.withBordered(false);
		return close;
	}
	
	/**
	 * Sets the action to run when the close button is clicked.
	 *
	 * @param onClose the action to run, or null (or an empty Runner) to do nothing
	 */
	public void setOnClose(Runnable onClose) {
		if (onClose == null) {
			onClose = () -> {};
		}
		this.onClose = onClose;
	}

}
