package org.peakaboo.framework.swidget.dialogues.fileio;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.accessibility.AccessibleContext;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.FileChooserUI;
import javax.swing.plaf.basic.BasicFileChooserUI;

import org.peakaboo.framework.swidget.Swidget;
import org.peakaboo.framework.swidget.icons.StockIcon;
import org.peakaboo.framework.swidget.widgets.Spacing;
import org.peakaboo.framework.swidget.widgets.buttons.ImageButton;
import org.peakaboo.framework.swidget.widgets.filechooser.breadcrumb.FileBreadCrumb;
import org.peakaboo.framework.swidget.widgets.filechooser.places.Places;
import org.peakaboo.framework.swidget.widgets.filechooser.places.PlacesWidget;
import org.peakaboo.framework.swidget.widgets.layout.HeaderBox;


public class SwidgetFileChooser extends JFileChooser {

	//scraped widgets
	private JPanel details;
	private JPanel filepane;
	private JButton bmkdir;
	private JList filelist;

	//replacement widgets
	private ImageButton makeDirButton;
	private PlacesWidget placesWidget;
	private HeaderBox headerWidget;
	private JPanel chooserPanel;
	private ImageButton affirmative, negative;
	
	//Callbacks
	private Runnable onAccept, onCancel;
	
	
	private Places places = Places.forPlatform();

	public SwidgetFileChooser() {
		this(null);
	}
	
	public SwidgetFileChooser(File directory) {
		super(directory);
		dosetup();
	}
	
	private void dosetup() {
		try {
			
			setControlButtonsAreShown(false);
			scrapeWidgets();
			
			chooserPanel = makeChooserPanel();
			placesWidget = makePlacesWidget();
			headerWidget = makeHeaderWidget();
			
			this.setLayout(new BorderLayout());
			this.add(chooserPanel, BorderLayout.CENTER);
			this.add(placesWidget, BorderLayout.WEST);
			this.add(headerWidget, BorderLayout.NORTH);
			
			this.setPreferredSize(new Dimension(800, 350));
						
		} catch (ClassCastException e) {
			return;
		}
	}

	private void scrapeWidgets() {
		//component 0 is the button bar with the look-in drop down and the up/home/new-dir buttons
		JPanel header = (JPanel) getComponent(0);
		filepane = (JPanel) getComponent(2);

		//first entry in the button bar is the panel containing the buttons
		JPanel buttons = (JPanel) header.getComponent(0);

		bmkdir = (JButton) buttons.getComponent(4);
		JToggleButton biconview = (JToggleButton) buttons.getComponent(6);
		JToggleButton blistview = (JToggleButton) buttons.getComponent(7);
				
		JPanel filepanechild = (JPanel) filepane.getComponent(0);
		JScrollPane filepanescroller = (JScrollPane) filepanechild.getComponent(0);
		filepanescroller.setBorder(Spacing.bNone());
		
		filelist = (JList) filepanescroller.getViewport().getView();
				
		details = (JPanel) getComponent(3);
		details.setBorder(Spacing.bMedium());

		JPanel filenamePane = (JPanel) details.getComponent(0);
		JPanel filetypepane = (JPanel) details.getComponent(2);
		
		JTextField filename = (JTextField) filenamePane.getComponent(1);
		JComboBox filetype = (JComboBox) filetypepane.getComponent(1);

		this.removeAll();
		
		
		addActionListener(action -> {
			String command = action.getActionCommand();
			//something like double-clicking a file may trigger this
			if (command.equals(JFileChooser.APPROVE_SELECTION)) {
				if (onAccept != null) onAccept.run();
			}
			if (command.equals(JFileChooser.CANCEL_SELECTION)) {
				if (onCancel != null) onCancel.run();
			}
		});
		
	}
	
	private HeaderBox makeHeaderWidget() {
		
		JPanel breadcrumb = this.getBreadcrumb();
		affirmative = new ImageButton(getApproveButtonText()).withStateDefault();
		negative = new ImageButton("Cancel");
		
		addPropertyChangeListener(JFileChooser.APPROVE_BUTTON_TEXT_CHANGED_PROPERTY, evt -> {
			
			String text = getApproveButtonText();
			if (text == null && getDialogType() == JFileChooser.OPEN_DIALOG) {
				text = "Open";
			}
			if (text == null && getDialogType() == JFileChooser.SAVE_DIALOG) {
				text = "Save";
			}
			affirmative.setText(text);	
		});
		addPropertyChangeListener(
				JFileChooser.APPROVE_BUTTON_TOOL_TIP_TEXT_CHANGED_PROPERTY, 
				evt -> affirmative.setToolTipText(getApproveButtonToolTipText()));
		
		/*
		 * Can this really be the only way to get the file chooser to perform the same action as 
		 * the standard control buttons? Calling chooser.approveSelection() just returns the files
		 * selected in the list control without considering text typed into the filename box.
		 */
		BasicFileChooserUI ui = (BasicFileChooserUI) getUI();
		affirmative.addActionListener(ui.getApproveSelectionAction());
		negative.addActionListener(ui.getCancelSelectionAction());
		
		HeaderBox header = new HeaderBox(negative, breadcrumb, affirmative);
		return header;		
	}
		
	private JPanel makeChooserPanel() {
	
		JPanel chooserPanel = new JPanel();
				
		setBorder(new EmptyBorder(Spacing.iNone()));
		filepane.setBorder(new MatteBorder(0, 0, 1, 0, Swidget.dividerColor()));
		filelist.setBorder(Spacing.bSmall());
		
		
		chooserPanel.setLayout(new BorderLayout());
		chooserPanel.add(details, BorderLayout.SOUTH);
		chooserPanel.add(filepane, BorderLayout.CENTER);
		
		return chooserPanel;

	}
	
	private PlacesWidget makePlacesWidget() {
		PlacesWidget placesWidget = null;
		if (places != null) {
			placesWidget = new PlacesWidget(this, places);
		}
		return placesWidget;
	}

	private JPanel getBreadcrumb() {
		
		makeDirButton = new ImageButton(StockIcon.PLACE_FOLDER_NEW);
		makeDirButton.setAction(bmkdir.getAction());
		makeDirButton.withText("").withIcon(StockIcon.PLACE_FOLDER_NEW);
		
		FileBreadCrumb breadcrumb = new FileBreadCrumb(this, places);
		JPanel box = new JPanel(new BorderLayout(Spacing.small, Spacing.small));
		box.setBorder(new EmptyBorder(0, Spacing.medium, 0, Spacing.medium));
		box.add(breadcrumb, BorderLayout.CENTER);
		box.add(makeDirButton, BorderLayout.LINE_END);
		box.setOpaque(false);
		return box;
	}
	
	@Override
	public int showSaveDialog(Component parent) {
		setApproveButtonText("Save");
		return super.showSaveDialog(parent);
	}

	@Override
	public int showOpenDialog(Component parent) {
		setApproveButtonText("Open");
		return super.showOpenDialog(parent);
	}

}
