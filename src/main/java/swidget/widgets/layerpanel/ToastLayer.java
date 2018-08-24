package swidget.widgets.layerpanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JLabel;
import javax.swing.JLayer;
import javax.swing.JPanel;


import swidget.widgets.ClearPanel;
import swidget.widgets.Spacing;

public class ToastLayer implements Layer {

	private JPanel toast = new ClearPanel();
	private final JLayer<JPanel> toastJLayer;
	private JLabel label;
	private float alpha = 0f, delta = 0.1f;
	private int stage = 0;
	
	private LayerPanel parent;
	
	public ToastLayer(LayerPanel parent, String message) {
		this(parent, message, () -> {});
	}
	
	public ToastLayer(LayerPanel parent, String message, Runnable onClick) {
		this.parent = parent;
		
		toast.setLayout(new FlowLayout());


		label = new JLabel(message) {
			
			@Override
			protected void paintComponent(Graphics g) {
							
				((Graphics2D)g).setRenderingHint(
					    RenderingHints.KEY_ANTIALIASING,
					    RenderingHints.VALUE_ANTIALIAS_ON);
				
				
				Color bg = new Color(0f, 0f, 0f, alpha * 0.67f);
				Color fg = new Color(1f, 1f, 1f, alpha);
				this.setForeground(fg);
				g.setColor(bg);
				g.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), 10, 10);
				super.paintComponent(g);
			}
			

		};
		label.setBorder(Spacing.bHuge());
		label.setFocusable(false);
		toast.setFocusable(false);
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				onClick.run();
			}
		});
				
		toast.add(label, BorderLayout.CENTER);
		
		
		toastJLayer = new JLayer<JPanel>(toast);
		toastJLayer.setFocusable(false);
		
	}
	
	@Override
	public JLayer<JPanel> getJLayer() {
		
		fadeIn();		
		return toastJLayer;
	}

	
	private void fadeIn() {
		Timer timer = new Timer(true);
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				if (stage != 0) { return; }
				alpha += delta;
				alpha = Math.min(1f, Math.max(0, alpha));
				if (alpha == 1.0f) {
					timer.cancel();
					timer.purge();
					stage=1;
					delay();
				}
				ToastLayer.this.toastJLayer.repaint(30);
			}}, 0, 30);
	}
	
	private void fadeOut() {
		Timer timer = new Timer(true);
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				if (stage != 2) { return; }
				alpha -= delta;
				alpha = Math.min(1f, Math.max(0, alpha));
				if (alpha == 0.0f) {
					parent.removeLayer(ToastLayer.this);
					timer.cancel();
					timer.purge();
					stage=3;
				}
				ToastLayer.this.toastJLayer.repaint(30);
			}}, 0, 30);
	}
	
	private void delay() {
		Timer timer = new Timer(true);
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				if (stage != 1) { return; }
				timer.cancel();
				timer.purge();
				stage=2;
				fadeOut();
			}
		}, 5000);
	}
	
	@Override
	public JPanel getComponent() {
		return toast;
	}

	@Override
	public void discard() {
		
	}
	
	@Override
	public boolean modal() {
		return false;
	}


}

class TranslucentLabel extends JLabel {


    public TranslucentLabel(String text) {
        super(text);
    }

    @Override
    public boolean isOpaque() {
        return false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g2d);
        g2d.dispose();
    }

}
