package org.peakaboo.framework.swidget.dialogues.fileio.specialdirectories;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.TableModel;

import org.peakaboo.framework.stratus.Stratus;
import org.peakaboo.framework.stratus.StratusLookAndFeel;
import org.peakaboo.framework.swidget.Swidget;
import org.peakaboo.framework.swidget.icons.IconSize;
import org.peakaboo.framework.swidget.icons.StockIcon;
import org.peakaboo.framework.swidget.models.ListTableModel;
import org.peakaboo.framework.swidget.widgets.Spacing;
import org.peakaboo.framework.swidget.widgets.listwidget.ListWidget;
import org.peakaboo.framework.swidget.widgets.listwidget.ListWidgetTableCellRenderer;

public class SpecialDirectoriesWidget extends JPanel {

	private SpecialDirectories specials;
	private List<File> dirs;
	private JTable items;
	private TableModel model;
	
	public SpecialDirectoriesWidget(JFileChooser chooser) {
		specials = SpecialDirectories.get();
		items = new JTable();
		dirs = new ArrayList<File>(specials.getRoots().keySet());
		model = new ListTableModel<File>(dirs);
		items.setModel(model);
			
		//items.setBackground(new Color(this.getBackground().getRGB()));
		this.setBackground(new Color(items.getBackground().getRGB()));
		
		Color dividerColour = UIManager.getColor("stratus-widget-border");
		if (dividerColour == null) {
			dividerColour = Color.LIGHT_GRAY;
		}
		this.setBorder(new MatteBorder(0, 0, 0, 1, dividerColour));
		
		this.setPreferredSize(new Dimension(140, 140));
		
		
		items.getColumnModel().getColumn(0).setCellRenderer(new ListWidgetTableCellRenderer<>(new DirWidget(specials)));
		items.setShowGrid(false);
		items.setTableHeader(null);
		items.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		items.getSelectionModel().addListSelectionListener(l -> {
			int row = items.getSelectedRow();
			if (row == -1) { return; }
			File dir = dirs.get(row);
			if (!dir.equals(chooser.getCurrentDirectory())) {
				chooser.setCurrentDirectory(dir);
			}
		});
		
		chooser.addPropertyChangeListener(l -> {
			File dir = chooser.getCurrentDirectory();
			if (dirs.indexOf(dir) == items.getSelectedRow()) { return; }
			int row = dirs.indexOf(dir);
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

}

class DirWidget extends ListWidget<File> {

	private SpecialDirectories xdg;
	JLabel l;
	
	public DirWidget(SpecialDirectories xdg) {
		this.xdg = xdg;
		setLayout(new BorderLayout());
		l = new JLabel();
		l.setBorder(new EmptyBorder(Spacing.large, Spacing.huge, Spacing.large, Spacing.huge));
		l.setIconTextGap(Spacing.medium);
		add(l, BorderLayout.CENTER);
	}
	
	@Override
	protected void onSetValue(File value) {
		l.setText(xdg.getName(value));
		l.setIcon(xdg.getIcon(value));
	}
	
	@Override
	public void setForeground(Color c) {
		super.setForeground(c);
		if (l == null) { return; }
		l.setForeground(c);
	}
	
	
}

