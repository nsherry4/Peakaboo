package org.peakaboo.framework.plural.streams.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.peakaboo.framework.eventful.EventfulConfig;
import org.peakaboo.framework.plural.streams.StreamExecutor;
import org.peakaboo.framework.plural.streams.StreamExecutor.Event;
import org.peakaboo.framework.swidget.Swidget;
import org.peakaboo.framework.swidget.icons.IconSize;
import org.peakaboo.framework.swidget.icons.StockIcon;
import org.peakaboo.framework.swidget.widgets.Spacing;

public class StreamExecutorView extends JPanel {

	
	private StreamExecutor<?> exec;
	
	public StreamExecutorView(StreamExecutor<?> exec) {
		super();
		
		this.exec = exec;
		setLayout(new BorderLayout(8, 8));
		setBorder(Spacing.bSmall());
		
		JLabel icon = new JLabel();
		Dimension d = new Dimension(16, 16);
		icon.setMinimumSize(d);
		icon.setMaximumSize(d);
		icon.setPreferredSize(d);
		this.add(icon, BorderLayout.WEST);
		
		JLabel text = new JLabel(exec.getName());
		this.add(text, BorderLayout.CENTER);
		
		exec.addListener(event -> {
			if (event == Event.COMPLETED) {
				icon.setIcon(StockIcon.CHOOSE_OK.toImageIcon(IconSize.BUTTON));
			}
			if (event == Event.PROGRESS && exec.getCount() > 0) {
				icon.setIcon(StockIcon.GO_NEXT.toImageIcon(IconSize.BUTTON));
			}
		});
		
		
	}
	
	public StreamExecutor<?> getExecutor() {
		return exec;
	}
	


	public static void main(String[] args) throws InterruptedException {
		
		EventfulConfig.uiThreadRunner = SwingUtilities::invokeLater;
				
		Swidget.initialize(() -> {
			int size = 10000;
			
			List<Integer> ints = new ArrayList<>();
			for (int i = 0; i < size; i++) {
				ints.add(i);
			}
			System.out.println("Starting...");
			
			
			StreamExecutor<List<Integer>> e1 = new StreamExecutor<>("s1");
			e1.setSize(size);
			
			e1.addListener((event) -> {
				if (event == Event.PROGRESS) {
					System.out.println("E1 Processed: " + e1.getCount());
				}
				
				if (event == Event.COMPLETED) {
					System.out.println("E1 Done!");
				}
			});
			
			e1.setTask(() -> {
				return e1.observe(ints.stream()).map(v -> {
					float f = v;
					for (int i = 0; i < 100000; i++) {
						f = (int)Math.pow(f, 1.0001);
					}
					return (int)f;
				}).collect(Collectors.toList());
			});
			
	
			
			
			StreamExecutor<List<Integer>> e2 = new StreamExecutor<>("s2");
			e2.setSize(size);
			
			e2.addListener((event) -> {
				if (event == Event.PROGRESS) {
					System.out.println("E2 Processed: " + e2.getCount());
				}
				
				if (event == Event.COMPLETED) {
					System.out.println("E2 Done!");
				}
			});
			
			e2.setTask(() -> {
				return e2.observe(e1.getResult().get().stream()).map(v -> {
					float f = v;
					for (int i = 0; i < 100000; i++) {
						f = (int)Math.pow(f, 1.0001);
					}
					return (int)f;
				}).collect(Collectors.toList());
			});
			
			e1.then(e2);
			
			
			StreamExecutorView v1 = new StreamExecutorView(e1);
			StreamExecutorView v2 = new StreamExecutorView(e2);
			
			JFrame frame = new JFrame();
			StreamExecutorPanel panel = new StreamExecutorPanel("Two Tasks", v1, v2);
			frame.getContentPane().setLayout(new BorderLayout());
			frame.getContentPane().add(panel, BorderLayout.CENTER);
	
			e2.addListener((event) -> {
				if (event != Event.PROGRESS) {
					frame.setVisible(false);
				}
			});
			
		
			frame.pack();
			frame.setVisible(true);
			
			e1.start();
		}, "Test");

		

		
		
		Thread.currentThread().sleep(5000);
		
	}
	
}
