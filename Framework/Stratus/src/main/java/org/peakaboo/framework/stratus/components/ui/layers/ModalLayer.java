package org.peakaboo.framework.stratus.components.ui.layers;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.event.ComponentAdapter;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;
import javax.swing.JLayer;
import javax.swing.JPanel;

import org.peakaboo.framework.stratus.api.ManagedImageBuffer;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.components.layouts.CenteringLayout;
import org.peakaboo.framework.stratus.components.panels.ClearPanel;

public class ModalLayer implements Layer {
	
	private JLayer<JComponent> layer;
	private JComponent component;
	protected LayerPanel owner;
	protected JPanel wrap;
	private ComponentAdapter listener;
	private boolean sizeWithParent = false;
	
	
	public ModalLayer(LayerPanel owner, JComponent component) {
		this(owner, component, false);
	}
	
	public ModalLayer(LayerPanel owner, JComponent component, boolean sizeWithParent) {
		this.owner = owner;
		this.component = component;
		this.sizeWithParent = sizeWithParent;
		
		this.layer = makeModalLayer();
		
	}
	


	@Override
	public JLayer<JComponent> getJLayer() {
		return layer;
	}



	@Override
	public JComponent getContent() {
		return component;
	}

	//clean up after we're done with this modal layer.
	@Override
	public void discard() {
		if (listener == null) {
			return;
		}
		owner.removeComponentListener(listener);
	}

	private JLayer<JComponent> makeModalLayer() {
		
		JPanel modalPanel = new ClearPanel();
		
		modalPanel.addMouseListener(new MouseAdapter() {});
		modalPanel.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				e.consume();
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				e.consume();
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				e.consume();
			}
		});
		
		setModalPanelComponent(modalPanel, this.component);
		
		//set blur in case another layer is placed on top of this one
		LayerBlurUI<JComponent> blurUI = new LayerBlurUI<JComponent>(this.owner, this.component) {
			@Override
			public void eventDispatched(AWTEvent e, JLayer<? extends JComponent> l) {
				((InputEvent) e).consume();
			}
		};
		JLayer<JComponent> layer = new JLayer<JComponent>(modalPanel, blurUI);
		
		layer.setVisible(true);
		layer.setOpaque(false);
		layer.setBackground(new Color(0, 0, 0, 0f));
		
		return layer;
				
		
	}
	

	
	private void setModalPanelComponent(JPanel modalPanel, Component component) {
		
		
		
		wrap = new JPanel(new BorderLayout()) {
		
			ManagedImageBuffer buffered = new ManagedImageBuffer();
			
			@Override
			public void paint(Graphics g) {

				if (LayerPanel.lowGraphicsMode) {
					super.paint(g);
				} else {
					//Paint into a buffer
					buffered.resize(this.getWidth(), this.getHeight());
					buffered.clear();
					var buffer = buffered.get();
					var bufg = buffer.createGraphics();

					Graphics2D b2d = (Graphics2D) bufg;
					b2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					b2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);	
					super.paint(b2d);
					buffered.markPainted();
					
					//Set up our graphics context for antialiasing
					Graphics2D g2d = (Graphics2D) g;
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);			
					
					// Define a clip shape, then take the rendered ui in the buffer, set it as a texture 
					// and fill that shape. This is the only way to get antialiased corners
					var clip = new RoundRectangle2D.Float(component.getX(), component.getY(), component.getWidth(), component.getHeight(), getCornerRadius(), getCornerRadius());
					var bounds = new Rectangle2D.Float(0, 0, buffer.getWidth(), buffer.getHeight());
					var texture = new TexturePaint(buffer, bounds);
					g2d.setPaint(texture);
					g2d.fill(clip);					
				}

				
			}
			
		};
		wrap.setOpaque(false);
		wrap.add(component, BorderLayout.CENTER);
		
		
		modalPanel.removeAll();
		
		if (sizeWithParent) {
			modalPanel.setLayout(new BorderLayout());
			modalPanel.add(wrap, BorderLayout.CENTER);
			modalPanel.setBorder(Spacing.bLarge());
		} else {
			modalPanel.setLayout(new CenteringLayout());
			modalPanel.add(wrap);
			modalPanel.setBorder(Spacing.bNone());
		}
		
	}

	@Override
	public boolean modal() {
		return true;
	}
	
	@Override
	public int getCornerRadius() {
		return Spacing.huge;
	}
	
	protected void remove() {
		owner.removeLayer(this);
	}

	@Override
	public JComponent getOuterComponent() {
		return wrap;
	}


}