package org.peakaboo.framework.swidget.widgets.layerpanel.widgets;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.MatteBorder;

import org.peakaboo.framework.swidget.Swidget;
import org.peakaboo.framework.swidget.widgets.Spacing;
import org.peakaboo.framework.swidget.widgets.fluent.button.FluentButton;
import org.peakaboo.framework.swidget.widgets.layerpanel.HeaderLayer;
import org.peakaboo.framework.swidget.widgets.layerpanel.LayerPanel;

public abstract class ListPickerLayer<T> extends HeaderLayer {
	
	private List<T> items;
	private Consumer<T> onAccept;
	private FluentButton accept, reject;
	private PickerList<T> reflist;
	
	static class PickerList<T> extends JPanel {
		
		private ListPickerLayer<T> parent;
		private JTable table;
		
		public PickerList(ListPickerLayer<T> parent, JTable table) {
			this.parent = parent;
			this.table = table;
			
			table.setShowGrid(false);
			table.setGridColor(Swidget.dividerColor());
			table.setTableHeader(null);
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			
			table.addMouseListener(new MouseAdapter() {
		
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						parent.onAccept.accept(getSelectedReference());
					}
				}
			});
			
			table.addKeyListener(new KeyAdapter() {
				
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						parent.onAccept.accept(getSelectedReference());
					}
				}
			});
			
			table.setRowSelectionInterval(0, 0);
			
			table.setFillsViewportHeight(true);
			JScrollPane scroller = new JScrollPane(table);
			scroller.setPreferredSize(new Dimension(scroller.getPreferredSize().width, 250));
			scroller.setBorder(new MatteBorder(1, 1, 1, 1, Swidget.dividerColor()));
					
			setLayout(new BorderLayout());
			setBorder(Spacing.bHuge());
			add(scroller, BorderLayout.CENTER);
			
			focusTable();
			
		}
		
		public T getSelectedReference() {
			if (table.getSelectedRow() == -1) {
				return null;
			}
			return parent.items.get(table.getSelectedRow());
		}
		
		void focusTable() {
			SwingUtilities.invokeLater(() -> {
				table.requestFocus();
				table.grabFocus();
			});
		}
	}
	
	public ListPickerLayer(LayerPanel owner, String title, List<T> items, Consumer<T> onAccept) {
		super(owner, false);
		this.items = items;
		this.onAccept = t -> {
			remove();
			onAccept.accept(t);
		};
		
		reflist = new PickerList<>(this, getTable(items));
		
		accept = new FluentButton("OK").withStateDefault().withAction(() -> this.onAccept.accept(reflist.getSelectedReference()));
		reject = new FluentButton("Cancel").withAction(this::remove);
		
		getHeader().setComponents(reject, title, accept);
		setBody(reflist);
		
		reflist.focusTable();
		
	}

	protected abstract JTable getTable(List<T> items);

	
}


