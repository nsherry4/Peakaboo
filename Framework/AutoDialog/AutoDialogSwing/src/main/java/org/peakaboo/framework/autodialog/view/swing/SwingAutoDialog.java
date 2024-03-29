package org.peakaboo.framework.autodialog.view.swing;


import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Window;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.peakaboo.framework.autodialog.model.Group;
import org.peakaboo.framework.autodialog.view.editors.AutoDialogButtons;
import org.peakaboo.framework.autodialog.view.swing.layouts.SwingLayoutFactory;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.StratusText;
import org.peakaboo.framework.stratus.api.icons.IconSize;
import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.ButtonBox;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButtonLayout;
import org.peakaboo.framework.stratus.components.ui.live.LiveDialog;


public class SwingAutoDialog extends LiveDialog
{

	private Container parentContainer;

	private String helpTitle;
	private String helpMessage;
	
	private Group group;
	private AutoDialogButtons buttons;
	private boolean selectedOk = false;
	
	
	private FluentButton info;

	
	public SwingAutoDialog(Window owner, Group group) {
		this(owner, group, AutoDialogButtons.OK_CANCEL);
	}

	public SwingAutoDialog(Window owner, Group group, AutoDialogButtons buttons) {
		super(owner);
		this.buttons = buttons;
		this.group = group;
	}

	public SwingAutoDialog(Group group) {
		this(group, AutoDialogButtons.OK_CANCEL);
	}

	public SwingAutoDialog(Group group, AutoDialogButtons buttons) {
		this.buttons = buttons;
		this.group = group;
	}


	public void initialize(){
		
		Container c = this.getContentPane();
		c.setLayout(new BorderLayout());
			
		JScrollPane scroller = new JScrollPane(SwingLayoutFactory.forGroup(group).getComponent());
		scroller.setBorder(Spacing.bNone());
		scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		scroller.setBorder(Spacing.bMedium());
		c.add(scroller, BorderLayout.CENTER);


		c.add(createButtonBox(), BorderLayout.SOUTH);
		
				
		pack();
		setLocationRelativeTo(parentContainer);
		setTitle(group.getName());
		setVisible(true);
	}
	
	
	
	
	
	
	private JPanel createButtonBox() {
		
		ButtonBox bbox = new ButtonBox();
		
		if (buttons == AutoDialogButtons.OK_CANCEL) {
			
			FluentButton ok = new FluentButton("OK")
					.withStateDefault()
					.withAction(() -> {
						this.selectedOk = true;
						SwingAutoDialog.this.setVisible(false);
					});
			
			FluentButton cancel = new FluentButton("Cancel")
					.withAction(() -> SwingAutoDialog.this.setVisible(false));
			
			bbox.addRight(0, ok);
			bbox.addRight(0, cancel);
			
		} else if (buttons == AutoDialogButtons.CLOSE) {
			
			FluentButton close = new FluentButton("Close")
					.withAction(() -> SwingAutoDialog.this.setVisible(false));
			
			bbox.addRight(0, close);
			
		}
		
		
		info = new FluentButton().withIcon(StockIcon.BADGE_QUESTION)
				.withTooltip("More Information")
				.withLayout(FluentButtonLayout.IMAGE)
				.withBordered(true)
				.withAction(() -> 
					JOptionPane.showMessageDialog(
						SwingAutoDialog.this, 
						StratusText.lineWrapHTML(SwingAutoDialog.this, helpMessage),
						helpTitle, 
						JOptionPane.INFORMATION_MESSAGE, 
						StockIcon.BADGE_QUESTION.toImageIcon(IconSize.ICON)
					)
				);
		info.setFocusable(false);
		if (helpMessage == null) info.setVisible(false);
		
		
		bbox.addLeft(0, info);

		
				
		return bbox;
		
	}

	
	
	
	
	public String getHelpTitle() {
		return helpTitle;
	}

	public void setHelpTitle(String helpTitle) {
		this.helpTitle = helpTitle;
		
	}

	public String getHelpMessage() {
		return helpMessage;
	}

	public void setHelpMessage(String helpMessage) {
		this.helpMessage = helpMessage;
		if (info != null) info.setVisible((helpMessage != null && helpMessage.length() > 0));
	}
	
	
	@Override
	public Container getParent() {
		return parentContainer;
	}

	public void setParent(Container parent) {
		this.parentContainer = parent;
	}

	public boolean okSelected() {
		return selectedOk;
	}

	public Group getGroup() {
		return group;
	}
	
	
	

}
