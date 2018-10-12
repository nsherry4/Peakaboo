package plural.streams.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

import plural.streams.StreamExecutor;
import swidget.icons.StockIcon;
import swidget.widgets.ButtonBox;
import swidget.widgets.HeaderBox;
import swidget.widgets.Spacing;
import swidget.widgets.buttons.ImageButton;

public class StreamExecutorPanel extends JPanel {

	public StreamExecutorPanel(String title, StreamExecutorView... observerViews) {
		this(title, Arrays.asList(observerViews));
	}

	public StreamExecutorPanel(String t, List<StreamExecutorView> observerViews) {

		this.setLayout(new BorderLayout());
		
		
		
		ImageButton cancel = new ImageButton("Cancel").withStateCritical();
		cancel.addActionListener(e -> {
			List<StreamExecutorView> reversed = new ArrayList<>(observerViews);
			Collections.reverse(reversed);
			for (StreamExecutorView v : reversed) {
				v.getExecutor().abort();
			}
		});

		
		HeaderBox header = new HeaderBox(null, t, cancel);
		this.add(header, BorderLayout.NORTH);
		
		
		JPanel center = new JPanel(new BorderLayout());
		center.setBorder(Spacing.bHuge());
		
		JPanel lineItems = new JPanel();
		lineItems.setBorder(new EmptyBorder(0, Spacing.huge, Spacing.huge, Spacing.huge));
		LayoutManager layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		lineItems.setLayout(layout);


		
		
		
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.anchor = GridBagConstraints.FIRST_LINE_START;

		for (StreamExecutorView obsv : observerViews) {
			lineItems.add(obsv, c);
			c.gridy += 1;
		}
		center.add(lineItems, BorderLayout.CENTER);
		
		
		
		JProgressBar progress = new JProgressBar();
		progress.setMaximum(100);
		progress.setMinimum(0);
		progress.setValue(0);
		center.add(progress, BorderLayout.SOUTH);

		
		
		this.add(center, BorderLayout.CENTER);
		
		


		

		for (StreamExecutorView v : observerViews) {
			v.getExecutor().addListener(event -> {
				StreamExecutor<?> exec = v.getExecutor();
				if (exec.getSize() <= 0) {
					progress.setIndeterminate(true);
				} else {
					progress.setIndeterminate(false);
					progress.setValue(exec.getCount() * 100 / exec.getSize());
				}
			});
		}

	}

}
