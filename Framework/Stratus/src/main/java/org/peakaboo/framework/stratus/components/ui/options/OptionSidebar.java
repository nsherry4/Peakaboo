package org.peakaboo.framework.stratus.components.ui.options;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
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
import org.peakaboo.framework.stratus.components.stencil.Stencil;
import org.peakaboo.framework.stratus.components.stencil.StencilListCellRenderer;

public class OptionSidebar extends OptionComponent {
	
	private JList<OptionSidebar.Entry> list;
	
	public static class Entry {
		String name;
		Optional<ImageIcon> icon;
		public boolean trailingSeparator = false;
		public boolean isHeading = false;
		public Entry(String name) {
			this.name = name;
			this.icon = Optional.empty();
		}
		public Entry(String name, ImageIcon icon) {
			this.name = name;
			this.icon = Optional.ofNullable(icon);
		}
		public String getName() {
			return name;
		}
		public Entry setHeading(boolean isHeading) {
			this.isHeading = isHeading;
			return this;
		}
		
	}
	
	class Widget extends Stencil<Entry> {

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
			
			if (entry.isHeading) {
				selected = false;
				this.setOpaque(false);
				label.setForeground(StratusColour.moreTransparent(this.getForeground(), 0.4f));
				label.setFont(label.getFont().deriveFont(11f));
				label.setBorder(new javax.swing.border.EmptyBorder(Spacing.huge, Spacing.huge, Spacing.small, Spacing.huge));
				label.setIconTextGap(0);
				label.setIcon(null);
			} else {
				label.setForeground(this.getForeground());
				label.setFont(label.getFont().deriveFont(14f));
				label.setBorder(Spacing.bHuge());
				label.setIconTextGap(Spacing.huge);
				if (entry.icon.isPresent()) {
					label.setIcon(IconFactory.recolour(entry.icon.get(), this.getForeground()));
				} else {
					label.setIcon(null);
				}
			}
			
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
		list.setSelectionModel(new DefaultListSelectionModel() {
			@Override
			public void setSelectionInterval(int index0, int index1) {
				if (index0 == index1 && index0 >= 0 && index0 < entries.size() && entries.get(index0).isHeading) {
					return; // Disallow selecting this item
				}
				super.setSelectionInterval(index0, index1);
			}
		});
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		StencilListCellRenderer<Entry> cellRenderer = new StencilListCellRenderer<>(new Widget());
		list.setCellRenderer(cellRenderer);
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent event) {
				if (event.getValueIsAdjusting()) {
					return;
				}
				int idx = list.getSelectedIndex();
				if (idx >= 0) {
					selectionCallback.accept(entries.get(idx));
				}
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
