package org.peakaboo.framework.swidget.dialogues.fileio;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.basic.BasicFileChooserUI;

import org.peakaboo.framework.stratus.Stratus;
import org.peakaboo.framework.stratus.theme.Theme;
import org.peakaboo.framework.swidget.Swidget;
import org.peakaboo.framework.swidget.icons.IconSize;
import org.peakaboo.framework.swidget.icons.StockIcon;
import org.peakaboo.framework.swidget.widgets.Spacing;
import org.peakaboo.framework.swidget.widgets.filechooser.breadcrumb.FileBreadCrumb;
import org.peakaboo.framework.swidget.widgets.filechooser.places.Places;
import org.peakaboo.framework.swidget.widgets.filechooser.places.PlacesPanel;
import org.peakaboo.framework.swidget.widgets.fluent.button.FluentButton;
import org.peakaboo.framework.swidget.widgets.layout.HeaderBox;
import org.peakaboo.framework.swidget.widgets.listwidget.ListWidget;
import org.peakaboo.framework.swidget.widgets.listwidget.ListWidgetListCellRenderer;


public class SwidgetFileChooser extends JFileChooser {

	//scraped widgets
	private JPanel details;
	private JPanel filepane;
	private JButton bmkdir;
	private JList<File> filelist;

	//replacement widgets
	private FluentButton makeDirButton;
	PlacesPanel placesWidget;
	HeaderBox headerWidget;
	JScrollPane scroller, placesScroller;
	private JPanel chooserPanel;
	private FluentButton affirmative, negative;
	
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
	
	@Override
	public ImageIcon getIcon(File file) {
		return StockIcon.fromMimeType(file).toImageIcon(IconSize.TOOLBAR_SMALL);
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
			this.add(headerWidget, BorderLayout.NORTH);
			if (placesWidget.supported()) {
				this.add(placesWidget, BorderLayout.WEST);
			}
			
			this.setPreferredSize(new Dimension(850, 400));
						
		} catch (ClassCastException e) {
			return;
		}
	}


	private <T> T getFilesWidget(Container container, Class<? extends Component> clazz) {
		for (Component component : container.getComponents()) {
			if (clazz.isInstance(component)) {
				return (T) component;
			}
			if (component instanceof Container) {
				Component fromChild = getFilesWidget((Container) component, clazz);
				if (fromChild != null) {
					return (T) fromChild;
				}
			}

		}
		return null;
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
		scroller = (JScrollPane) filepanechild.getComponent(0);
		scroller.setBorder(Spacing.bNone());
		
		
		
		
		
		filepane.addPropertyChangeListener("viewType", e -> {
			int type = (int) e.getNewValue();
			switch (type) {
				case 0: //list
					break;
				case 1: //table
					JTable table = getFilesWidget(this, JTable.class);
					
					//No wasted space on detail view border
					JScrollPane detailScroller = (JScrollPane) table.getParent().getParent();
					detailScroller.setBorder(Spacing.bNone());
					
					//Row height to match larger icons
					table.setRowHeight(Math.max(table.getFont().getSize() + 4, 24 + 1));
					
					break;
			}
		});
		
		
		
		filelist = getFilesWidget(this, JList.class);
		filelist.setSelectionBackground(getBackground());
		ListWidgetListCellRenderer<File> r = new ListWidgetListCellRenderer<>(new ListFileWidget(filelist));
		filelist.setCellRenderer(r);
		
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
		affirmative = new FluentButton(getApproveButtonText()).withStateDefault();
		negative = new FluentButton("Cancel");
		
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
	
	private PlacesPanel makePlacesWidget() {
		PlacesPanel placesWidget = null;
		if (places != null) {
			placesWidget = new PlacesPanel(this, places);
		}
		return placesWidget;
	}

	private JPanel getBreadcrumb() {
		
		makeDirButton = new FluentButton(StockIcon.PLACE_FOLDER_NEW_SYMBOLIC);
		makeDirButton.setAction(bmkdir.getAction());
		makeDirButton.withText("").withIcon(StockIcon.PLACE_FOLDER_NEW_SYMBOLIC);
		
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

	

	abstract class BaseFileWidget extends ListWidget<File> {
		JLabel label;
		Color selBg;
		Color selFg;
		Color bg;
		Color fg;
		
		@Override
		protected void onSetValue(File file, boolean selected) {
			SwidgetFileChooser chooser = SwidgetFileChooser.this;
			
			label.setText(chooser.getName(file));
			label.setIcon(chooser.getIcon(file));
			label.setForeground(this.getForeground());
							
			this.setOpaque(selected);
			this.setBackground(selected ? selBg : bg);
			this.setForeground(selected ? selFg : fg);
			label.setForeground(selected ? selFg : fg);
			
		}
		
	}
	
	class ListFileWidget extends BaseFileWidget {


		public ListFileWidget(JList<File> list) {
			label = new JLabel();
			
			label.setBorder(Spacing.bTiny());
			setLayout(new BorderLayout());
			this.add(label, BorderLayout.CENTER);
			label.setOpaque(false);
			
			if (Swidget.isStratusLaF()) {
				Theme theme = Stratus.getTheme();
				selBg = theme.getHighlight();
				selFg = theme.getHighlightText();
				bg = theme.getRecessedControl();
				fg = theme.getRecessedText();
			} else {
				selBg = list.getSelectionBackground();
				selFg = list.getSelectionForeground();
				bg = list.getBackground();
				fg = list.getForeground();
			}
			
		}

		@Override
		protected void paintComponent(Graphics g) {
			if (isOpaque()) {
				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		    	
				g2.setColor(getBackground());
				g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 6, 6));
			}
		}


		
	}
	
	class DetailsFileWidget extends BaseFileWidget {



		public DetailsFileWidget(JTable owner) {
			label = new JLabel();
			
			label.setBorder(Spacing.bTiny());
			setLayout(new BorderLayout());
			this.add(label, BorderLayout.CENTER);
			label.setOpaque(false);
			
			if (Swidget.isStratusLaF()) {
				Theme theme = Stratus.getTheme();
				selBg = theme.getHighlight();
				selFg = theme.getHighlightText();
				bg = theme.getRecessedControl();
				fg = theme.getRecessedText();
			} else {
				selBg = owner.getSelectionBackground();
				selFg = owner.getSelectionForeground();
				bg = owner.getBackground();
				fg = owner.getForeground();
			}
			
		}

		
	};
	
	
}
