package org.peakaboo.ui.swing.plugins;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.batik.ext.swing.GridBagConstants;

import net.sciencestudio.bolt.plugin.core.issue.BoltIssue;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.JTextLabel;
import swidget.widgets.Spacing;
import swidget.widgets.buttons.ImageButton;
import swidget.widgets.layout.PropertyPanel;
import swidget.widgets.layout.TitledPanel;

public class IssueView extends JPanel {

	public IssueView(BoltIssue<?> issue, PluginsOverview parent) {
		super(new GridBagLayout());
		this.setBorder(Spacing.bHuge());
		
		
		//components
		Map<String, String> properties = new HashMap<>();
		properties.put("Source", issue.longSource());	
		TitledPanel titlePanel = new TitledPanel(new PropertyPanel(properties), issue.title(), true);
		titlePanel.setBadge(StockIcon.BADGE_ERROR.toImageIcon(IconSize.ICON));
		
		JTextLabel desc = new JTextLabel(issue.description());
		JScrollPane scroller = new JScrollPane(desc);
		scroller.setBorder(Spacing.bLarge());
		scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		JPanel actionPanel = new JPanel(new GridBagLayout());
		ImageButton action = null;
		if (issue.hasFix()) {
			action = new ImageButton(issue.fixName());
			if (issue.isFixDestructuve()) {
				action.withStateCritical();
			} else {
				action.withStateDefault();
			}
			action.withAction(() -> {
				issue.fix();
				parent.reload();
			});
			GridBagConstraints c = new GridBagConstraints();
			c.anchor = GridBagConstraints.CENTER;
			c.fill = GridBagConstraints.NONE;
			actionPanel.add(action, c);
			titlePanel.addControls(actionPanel);
		}
		
	
		
		
		//layout
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstants.HORIZONTAL;
		c.anchor = GridBagConstants.NORTHWEST;
		c.weightx = 1f;
		c.weighty = 0f;
		c.gridx = 0;
		c.gridy = 0;
		c.ipadx = Spacing.large;
		c.ipady = Spacing.large;
				
		this.add(titlePanel, c);

		c.gridy++;
		c.weighty=1f;
		c.fill = GridBagConstraints.BOTH;
		this.add(scroller, c);
				
	}
	
}
