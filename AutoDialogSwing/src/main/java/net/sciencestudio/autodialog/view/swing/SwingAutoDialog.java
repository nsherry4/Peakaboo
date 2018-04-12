package net.sciencestudio.autodialog.view.swing;


import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import net.sciencestudio.autodialog.model.Group;
import net.sciencestudio.autodialog.view.editors.AutoDialogButtons;
import net.sciencestudio.autodialog.view.swing.layouts.SwingLayoutFactory;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.ButtonBox;
import swidget.widgets.ImageButton;
import swidget.widgets.ImageButton.Layout;
import swidget.widgets.Spacing;
import swidget.widgets.TextWrapping;


public class SwingAutoDialog extends JDialog
{

	//private IADController controller;
	private Container parent;

	private String helpTitle;
	private String helpMessage;
	
	private Group group;
	private AutoDialogButtons buttons;
	private boolean selected_ok = false;
	
	
	private ImageButton info;

	
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
		scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		scroller.setBorder(Spacing.bMedium());
		c.add(scroller, BorderLayout.CENTER);


		c.add(createButtonBox(), BorderLayout.SOUTH);
		
				
		pack();
		setLocationRelativeTo(parent);
		setTitle(group.getName());
		setVisible(true);
	}
	
	
	
	
	
	
	private JPanel createButtonBox()
	{
		
		ButtonBox bbox = new ButtonBox();
		
		if (buttons == AutoDialogButtons.OK_CANCEL) {
			
			ImageButton ok = new ImageButton(StockIcon.CHOOSE_OK, "OK", true);
			ok.addActionListener(e -> {
				this.selected_ok = true;
				System.err.println("Set OK");
				SwingAutoDialog.this.setVisible(false);
			});
			
			ImageButton cancel = new ImageButton(StockIcon.CHOOSE_CANCEL, "Cancel", true);
			cancel.addActionListener(e -> {
				SwingAutoDialog.this.setVisible(false);
			});
			
			bbox.addRight(0, cancel);
			bbox.addRight(0, ok);
			
		} else if (buttons == AutoDialogButtons.CLOSE) {
			
			ImageButton close = new ImageButton(StockIcon.WINDOW_CLOSE, "Close", true);
			close.addActionListener(e -> {
				SwingAutoDialog.this.setVisible(false);
			});
			
			bbox.addRight(0, close);
			
		}
		
		
		info = new ImageButton(StockIcon.BADGE_HELP, "Filter Information", Layout.IMAGE, true);
		info.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e)
			{	
				JOptionPane.showMessageDialog(
						SwingAutoDialog.this, 
						TextWrapping.wrapTextForMultiline(helpMessage),
						helpTitle, 
						JOptionPane.INFORMATION_MESSAGE, 
						StockIcon.BADGE_HELP.toImageIcon(IconSize.ICON)
					);

			}
		});
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
	
	
	
	public Container getParent() {
		return parent;
	}

	public void setParent(Container parent) {
		this.parent = parent;
	}

	public boolean okSelected() {
		return selected_ok;
	}

	public Group getGroup() {
		return group;
	}
	
	
	

}
