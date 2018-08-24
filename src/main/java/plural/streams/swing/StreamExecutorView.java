package plural.streams.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;

import plural.streams.StreamExecutor;
import plural.streams.StreamExecutor.Event;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.Spacing;

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
	
}
