package org.peakaboo.ui.swing.plugins;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.apache.batik.ext.swing.GridBagConstants;
import org.peakaboo.framework.bolt.plugin.core.issue.BoltIssue;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.StratusText;
import org.peakaboo.framework.stratus.api.icons.IconSize;
import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.panels.PropertyPanel;
import org.peakaboo.framework.stratus.components.panels.TitledPanel;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;

class IssueView extends JPanel {

	public IssueView(BoltIssue<?> issue, PluginManager parent) {
		super(new GridBagLayout());
		this.setBorder(Spacing.bHuge());
		
		
		//components
		Map<String, String> properties = new HashMap<>();
		properties.put("Source", issue.longSource());	
		TitledPanel titlePanel = new TitledPanel(new PropertyPanel(properties), issue.title(), true);
		titlePanel.setBadge(StockIcon.BADGE_ERROR.toImageIcon(IconSize.ICON));
		
		JLabel desc = new JLabel();
		desc.setVerticalAlignment(SwingConstants.TOP);
		desc.setBorder(Spacing.bHuge());
		desc.setText(StratusText.lineWrap(desc, issue.description(), 500));

		JPanel actionPanel = new JPanel(new GridBagLayout());
		FluentButton action = null;
		if (issue.hasFix()) {
			action = new FluentButton(issue.fixName());
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
		this.add(desc, c);
				
	}
	
}
