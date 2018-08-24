package swidget.widgets.layerpanel;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;

import javax.swing.JLayer;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import org.jdesktop.swingx.border.DropShadowBorder;

import swidget.widgets.ClearPanel;

public class ModalLayer implements Layer {
	private JLayer<JPanel> layer;
	private JPanel component;
	private LayerPanel owner;
	
	private ComponentAdapter listener;
	
	public ModalLayer(LayerPanel owner, JPanel component) {
		this.owner = owner;
		this.component = component;
		this.layer = makeModalLayer();
	}
	

	/* (non-Javadoc)
	 * @see swidget.widgets.tabbedinterface.layer.Layer#getLayer()
	 */
	@Override
	public JLayer<JPanel> getJLayer() {
		return layer;
	}


	/* (non-Javadoc)
	 * @see swidget.widgets.tabbedinterface.layer.Layer#getComponent()
	 */
	@Override
	public JPanel getComponent() {
		return component;
	}

	//clean up after we're done with this modal layer.
	/* (non-Javadoc)
	 * @see swidget.widgets.tabbedinterface.layer.Layer#discard()
	 */
	@Override
	public void discard() {
		if (listener == null) {
			return;
		}
		owner.removeComponentListener(listener);
	}

	private JLayer<JPanel> makeModalLayer() {
		
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
		LayerBlurUI<JPanel> blurUI = new LayerBlurUI<JPanel>(this.owner, this.component) {
			@Override
			public void eventDispatched(AWTEvent e, JLayer<? extends JPanel> l) {
				((InputEvent) e).consume();
			}
		};
		JLayer<JPanel> layer = new JLayer<JPanel>(modalPanel, blurUI);
		
		layer.setVisible(true);
		layer.setOpaque(false);
		layer.setBackground(new Color(0, 0, 0, 0f));
		
		return layer;
				
		
	}
	

	
	private void setModalPanelComponent(JPanel modalPanel, Component component) {
		
		JPanel wrap = new JPanel(new BorderLayout());
		wrap.setOpaque(false);
		JScrollPane modalScroller = new JScrollPane();
		modalScroller.setViewportView(component);
		modalScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		modalScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		modalScroller.setBorder(new EmptyBorder(0, 0, 0, 0));

		
		wrap.add(modalScroller, BorderLayout.CENTER);
		DropShadowBorder border = new DropShadowBorder(Color.BLACK, 12, 0.3f, 20, true, true, true, true);
		wrap.setBorder(border);
		
		modalPanel.removeAll();
		modalPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.gridheight = 3;
		c.gridwidth = 3;
		c.weightx = 0f;
		c.weighty = 0f;
		c.gridx = 1;
		c.gridy = 1;
		
		modalPanel.add(wrap, c);
		updateModalContentDimensions(modalScroller);
		listener = new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				updateModalContentDimensions(modalScroller);
			}
		};
		owner.addComponentListener(listener);

		
	}
	
	private void updateModalContentDimensions(JScrollPane modalScroller) {
		if (modalScroller == null) { 
			return; 
		}
		Component modal = modalScroller.getViewport().getView();
		if (modal == null) {
			return;
		}
		Dimension ownerSize = owner.getSize();
		int newWidth = (int)Math.max(50, Math.min(ownerSize.getWidth()-40, modal.getPreferredSize().getWidth()));
		int newHeight = (int)Math.max(50, Math.min(ownerSize.getHeight()-40, modal.getPreferredSize().getHeight()));
		
		modalScroller.getViewport().setPreferredSize(new Dimension(newWidth, newHeight));
		modalScroller.revalidate();
		
	}
	
	@Override
	public boolean modal() {
		return true;
	}


}