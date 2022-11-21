package org.peakaboo.framework.stratus.components.ui.filechooser.places;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.TableModel;

import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.api.models.ListTableModel;
import org.peakaboo.framework.stratus.components.listwidget.ListWidget;
import org.peakaboo.framework.stratus.components.listwidget.ListWidgetTableCellRenderer;

public class PlacesPanel extends JPanel {

	private Places places;
	private JTable items;
	private TableModel model;
	
	public PlacesPanel(JFileChooser chooser) {
		this(chooser, Places.forPlatform());
	}
	
	public PlacesPanel(JFileChooser chooser, Places places) {
		this.places = places;
		if (places == null) {
			return;
		}
		items = new JTable();
		model = new ListTableModel<>(places.getAll());
		items.setModel(model);
			
		//items.setBackground(new Color(this.getBackground().getRGB()));
		this.setBackground(new Color(items.getBackground().getRGB()));
		this.setBorder(new MatteBorder(0, 0, 0, 1, Stratus.getTheme().getWidgetBorder()));
		
		this.setPreferredSize(new Dimension(160, 160));
		
		
		items.getColumnModel().getColumn(0).setCellRenderer(new ListWidgetTableCellRenderer<>(new DirWidget(), items));
		items.setShowGrid(false);
		items.setTableHeader(null);
		items.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		//By default, <TAB> will cycle between rows in the table and never escape to the next component, which is silly
		items.getActionMap().put("selectNextColumnCell", new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		        manager.focusNextComponent();
			}
		});
		items.getActionMap().put("selectPreviousColumnCell", new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		        manager.focusPreviousComponent();
			}
		});
		
		items.getSelectionModel().addListSelectionListener(l -> {
			int row = items.getSelectedRow();
			if (row == -1) { return; }
			Place dir = places.getAll().get(row);
			if (!dir.getFile().equals(chooser.getCurrentDirectory())) {
				chooser.setCurrentDirectory(dir.getFile());
			}
		});
		
		chooser.addPropertyChangeListener(l -> {
			File dir = chooser.getCurrentDirectory();
			int row = places.index(dir);
			if (row == items.getSelectedRow()) { return; }
			if (row == -1) {
				items.getSelectionModel().clearSelection();
			} else {
				items.getSelectionModel().setSelectionInterval(row, row);
			}
		});

		
		JScrollPane scroller = new JScrollPane(items);
		scroller.setBorder(Spacing.bNone());
		scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroller.setBackground(new Color(items.getBackground().getRGB()));
		scroller.getViewport().setBackground(new Color(items.getBackground().getRGB()));
		setLayout(new BorderLayout());
		add(scroller, BorderLayout.CENTER);
	}
	
	public boolean supported() {
		return places != null;
	}
	

}

class DirWidget extends ListWidget<Place> {

	JLabel l;
	
	public DirWidget() {
		setLayout(new BorderLayout());
		l = new JLabel();
		l.setBorder(new EmptyBorder(Spacing.large, Spacing.huge, Spacing.large, Spacing.huge));
		l.setIconTextGap(Spacing.medium);
		add(l, BorderLayout.CENTER);
	}
	
	@Override
	protected void onSetValue(Place value, boolean selected) {
		l.setText(value.getName());
		l.setIcon(value.getIcon());
	}
	
	@Override
	public void setForeground(Color c) {
		super.setForeground(c);
		if (l == null) { return; }
		l.setForeground(c);
	}
	
	
}

