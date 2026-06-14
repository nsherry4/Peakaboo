package org.peakaboo.framework.stratus.components.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.components.ui.header.HeaderPanel;

/**
 * Hosts a body component and paints a soft drop shadow across the top edge
 * of the body. The shadow is drawn * <i>over</i> the body content (after its
 * children) to emphasize the header and reinforce the structure of the UI.
 * <p>
 * The effect is a single, small gradient fill, so it is cheap to paint, and it is
 * skipped entirely under {@link Stratus#lowGraphicsMode}.
 */
public class BodyShadowPanel extends ClearPanel {

	/** Height, in pixels, of the soft shadow cast onto the body. */
	private static final int SHADOW_HEIGHT = 6;

	/** Gentle multiplier applied to the theme's shadow alpha at the top edge. */
	private static final float SHADOW_STRENGTH = 0.3f;

	private boolean shadowEnabled = true;

	public BodyShadowPanel() {
		setLayout(new BorderLayout());
	}
	
	public BodyShadowPanel(Component c) {
		this();
		setContent(c);
	}
	
	/**
	 * Replaces the hosted body with the given component (or clears it when null).
	 */
	public void setContent(Component c) {
		BorderLayout layout = (BorderLayout) getLayout();
		Component existing = layout.getLayoutComponent(BorderLayout.CENTER);
		if (existing != null) {
			remove(existing);
		}
		if (c != null) {
			add(c, BorderLayout.CENTER);
		}
		revalidate();
		repaint();
	}

	public Component getContent() {
		return ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.CENTER);
	}
	
	private boolean hasContent() {
		return getContent() != null;
	}

	public void setShadowEnabled(boolean shadowEnabled) {
		this.shadowEnabled = shadowEnabled;
		repaint();
	}

	public boolean isShadowEnabled() {
		return shadowEnabled;
	}

	@Override
	protected void paintChildren(Graphics g) {
		super.paintChildren(g);

		if (!shadowEnabled || Stratus.lowGraphicsMode || !hasContent()) {
			return;
		}

		Graphics2D g2 = Stratus.modernGraphicsCopy(g);
		Color base = Stratus.getTheme().getShadow();
		Color top = new Color(base.getRed(), base.getGreen(), base.getBlue(), (int)(base.getAlpha() * SHADOW_STRENGTH));
		Color bottom = new Color(base.getRed(), base.getGreen(), base.getBlue(), 0);
		g2.setPaint(new GradientPaint(0, 0, top, 0, SHADOW_HEIGHT, bottom));
		g2.fillRect(0, 0, getWidth(), SHADOW_HEIGHT);
		g2.dispose();
	}

}
