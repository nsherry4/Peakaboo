package org.peakaboo.framework.swidget.dialogues.fileio;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import org.peakaboo.framework.swidget.Swidget;
import org.peakaboo.framework.swidget.icons.StockIcon;
import org.peakaboo.framework.swidget.widgets.Spacing;
import org.peakaboo.framework.swidget.widgets.buttons.ImageButton;
import org.peakaboo.framework.swidget.widgets.filechooser.breadcrumb.FileBreadCrumb;
import org.peakaboo.framework.swidget.widgets.filechooser.places.Places;


class SwidgetFileChooser extends JFileChooser {

	private ImageButton makeDirButton = null;
	
	public SwidgetFileChooser(File directory) {
		super(directory);
		setup();
	}
	
	private void setup() {
		try {
			
			
			//component 0 is the button bar with the look-in drop down and the up/home/new-dir buttons
			JPanel header = (JPanel) getComponent(0);
			JPanel filepane = (JPanel) getComponent(2);
	
			//first entry in the button bar is the panel containing the buttons
			JPanel buttons = (JPanel) header.getComponent(0);
	
			JButton bmkdir = (JButton) buttons.getComponent(4);
			JToggleButton biconview = (JToggleButton) buttons.getComponent(6);
			JToggleButton blistview = (JToggleButton) buttons.getComponent(7);
					
			JPanel filepanechild = (JPanel) filepane.getComponent(0);
			JScrollPane filepanescroller = (JScrollPane) filepanechild.getComponent(0);
			filepanescroller.setBorder(Spacing.bNone());
			
			JList filelist = (JList) filepanescroller.getViewport().getView();
					
			JPanel details = (JPanel) getComponent(3);
			details.setBorder(Spacing.bMedium());
	
			JPanel filenamePane = (JPanel) details.getComponent(0);
			JPanel filetypepane = (JPanel) details.getComponent(2);
			
			JTextField filename = (JTextField) filenamePane.getComponent(1);
			JComboBox filetype = (JComboBox) filetypepane.getComponent(1);
	
			
			
			makeDirButton = new ImageButton(StockIcon.PLACE_FOLDER_NEW);
			makeDirButton.setAction(bmkdir.getAction());
			makeDirButton.withText("").withIcon(StockIcon.PLACE_FOLDER_NEW);
					
			setBorder(new EmptyBorder(Spacing.iNone()));
			filepane.setBorder(new MatteBorder(0, 0, 1, 0, Swidget.dividerColor()));
			filelist.setBorder(Spacing.bSmall());
			
			this.removeAll();
			this.setLayout(new BorderLayout());
			this.add(details, BorderLayout.SOUTH);
			this.add(filepane, BorderLayout.CENTER);
			
		} catch (ClassCastException e) {
			return;
		}
		
		
	}

	public JPanel getHeader(Places places) {
		FileBreadCrumb breadcrumb = new FileBreadCrumb(this, places);
		JPanel box = new JPanel(new BorderLayout(Spacing.small, Spacing.small));
		box.setBorder(new EmptyBorder(0, Spacing.medium, 0, Spacing.medium));
		box.add(breadcrumb, BorderLayout.CENTER);
		box.add(makeDirButton, BorderLayout.LINE_END);
		box.setOpaque(false);
		return box;
	}
	
}
