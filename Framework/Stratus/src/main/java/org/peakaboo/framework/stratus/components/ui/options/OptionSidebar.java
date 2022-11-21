package org.peakaboo.framework.stratus.components.ui.options;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.StratusColour;
import org.peakaboo.framework.stratus.api.icons.IconFactory;
import org.peakaboo.framework.stratus.components.listwidget.ListWidget;
import org.peakaboo.framework.stratus.components.listwidget.ListWidgetListCellRenderer;

public class OptionSidebar extends OptionComponent {
	
	private JList<OptionSidebar.Entry> list;
	
	public static class Entry {
		String name;
		ImageIcon icon;
		public boolean trailingSeparator = false;
		public Entry(String name, ImageIcon icon) {
			this.name = name;
			this.icon = icon;
		}
		public String getName() {
			return name;
		}
		
	}
	
	class Widget extends ListWidget<Entry> {

		private JLabel label;
		private Border separatorBorder;
		
		public Widget() {
			setLayout(new BorderLayout());
			label = new JLabel();
			label.setBorder(Spacing.bHuge());
			label.setFont(label.getFont().deriveFont(14f));
			this.add(label, BorderLayout.CENTER);
			this.setPreferredSize(new Dimension(200, 40));
			label.setIconTextGap(Spacing.huge);

			separatorBorder = new MatteBorder(0, 0, 1, 0, StratusColour.moreTransparent(borderAlpha, 0.1f));
		}
		
		@Override
		protected void onSetValue(Entry entry, boolean selected) {
			label.setText(entry.name);
			label.setForeground(this.getForeground());
			//label.setIcon(value.icon.toSymbolicIcon(IconSize.BUTTON, this.getForeground()));
			label.setIcon(IconFactory.recolour(entry.icon, this.getForeground()));
			
			if (entry.trailingSeparator) {
				this.setBorder(separatorBorder);
			} else {
				this.setBorder(null);
			}
			
		}
		
	}
	
	public OptionSidebar(List<Entry> entries, Consumer<Entry> selectionCallback) {
		super();
		
		DefaultListModel<Entry> model = new DefaultListModel<>();
		for (Entry entry : entries) {
			model.addElement(entry);
		}
		
		list = new JList<>(model);
		list.setSelectionBackground(selectionBg);
		list.setSelectionForeground(selectionFg);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		ListWidgetListCellRenderer<Entry> cellRenderer = new ListWidgetListCellRenderer<>(new Widget());
		list.setCellRenderer(cellRenderer);
		list.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent event) {
				if (event.getValueIsAdjusting()) {
					return;
				}
				selectionCallback.accept(entries.get(list.getSelectedIndex()));
			}
		});
		
		list.setBorder(new MatteBorder(0, 0, 0, 1, border));
		
		this.setLayout(new BorderLayout());
		this.add(list);
		
	}

	@Override
	protected void paintBackground(Graphics2D g) {
		g.setColor(bg);
		g.fillRect(0, 0, getWidth(), getHeight());
	}

	public void select(Entry entry) {
		list.setSelectedValue(entry, true);
	}
	
}
