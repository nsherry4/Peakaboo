package org.peakaboo.framework.stratus.components.ui.layers;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;

import javax.swing.JComponent;
import javax.swing.JLayer;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;

import org.jdesktop.swingx.border.DropShadowBorder;
import org.peakaboo.framework.stratus.components.layouts.CenteringLayout;
import org.peakaboo.framework.stratus.components.panels.ClearPanel;

public class ModalLayer implements Layer {
	private JLayer<JComponent> layer;
	private JComponent component;
	protected LayerPanel owner;
	
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
	public JComponent getComponent() {
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
		
		JPanel wrap = new JPanel(new BorderLayout());
		wrap.setOpaque(false);
		wrap.add(component, BorderLayout.CENTER);
		
		Border border;
		if (LayerPanel.blurLowerLayers) {
			border = new DropShadowBorder(Color.BLACK, 10, 0.3f, 30, true, true, true, true);
		} else {
			border = new MatteBorder(1, 1, 1, 1, Color.BLACK);
		}
		wrap.setBorder(border);
		
		modalPanel.removeAll();
		
		if (sizeWithParent) {
			modalPanel.setLayout(new BorderLayout());
			modalPanel.add(wrap, BorderLayout.CENTER);
		} else {
			modalPanel.setLayout(new CenteringLayout());
			modalPanel.add(wrap);			
		}
		
	}

	@Override
	public boolean modal() {
		return true;
	}
	
	protected void remove() {
		owner.removeLayer(this);
	}


}