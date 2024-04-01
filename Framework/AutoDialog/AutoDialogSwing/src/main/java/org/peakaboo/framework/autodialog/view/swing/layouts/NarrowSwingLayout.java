package org.peakaboo.framework.autodialog.view.swing.layouts;

import java.awt.BorderLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.peakaboo.framework.autodialog.model.Value;
import org.peakaboo.framework.autodialog.view.editors.Editor.LabelStyle;
import org.peakaboo.framework.autodialog.view.swing.SwingView;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.components.panels.ClearPanel;

public class NarrowSwingLayout extends SimpleSwingLayout {

	private int maxwidth;
	
	public NarrowSwingLayout(int maxwidth) {
		this.maxwidth = maxwidth;
	}
	

	@Override
	public void layout() {
		root.setOpaque(false);
		root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
		root.removeAll();
		
		for (Value<?> param : group.getValue()) {
			layoutValue(param);
		}

		root.doLayout();
	}
	
	//Lays out a single parameter as part of the process of laying out a set of parameters
	@Override
	protected void layoutValue(Value<?> param) {
		SwingView editor = fromValue(param);
		if (editor == null) { return; }
		
		int editorsWidth = 0;
		for (Value<?> v : group.getValue()) {
			editorsWidth = Math.max(editorsWidth, fromValue(param).getComponent().getPreferredSize().width);
		}
		
		JLabel paramLabel = makeLabel(editor);

		var editorComponent = editor.getComponent();
		
		int fullwidth = paramLabel.getPreferredSize().width + editorComponent.getPreferredSize().width;
		boolean toowide = fullwidth > this.maxwidth;
		
		
		int spacing = Spacing.small;
		JPanel rowpanel = new ClearPanel();
		if (editor.getLabelStyle() == LabelStyle.LABEL_ON_SIDE && !toowide) {
			rowpanel.setLayout(new BorderLayout(spacing, spacing));
			rowpanel.add(paramLabel, BorderLayout.WEST);
			rowpanel.add(editorComponent, BorderLayout.EAST);
		} else if (
				(editor.getLabelStyle() == LabelStyle.LABEL_ON_SIDE && toowide) ||
				(editor.getLabelStyle() == LabelStyle.LABEL_ON_TOP)
			) {
			rowpanel.setLayout(new BorderLayout(spacing, spacing));
			rowpanel.add(paramLabel, BorderLayout.NORTH);
			rowpanel.add(editorComponent, BorderLayout.SOUTH);
		} else if (editor.getLabelStyle() == LabelStyle.LABEL_HIDDEN) {
			rowpanel.setLayout(new BorderLayout(spacing, spacing));
			rowpanel.add(editorComponent, BorderLayout.CENTER);
		}
		this.root.add(rowpanel);
		this.root.add(Box.createVerticalStrut(Spacing.medium));
		
	}

	
}
