package org.peakaboo.ui.swing.mapping.sidebar;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.peakaboo.controller.mapper.fitting.MapFittingController;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.ComponentStrip;
import org.peakaboo.framework.stratus.components.panels.ClearPanel;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButtonSize;
import org.peakaboo.ui.swing.mapping.sidebar.modes.MapUIRegistry;


public class MapFittingPanel extends ClearPanel
{

	private CardLayout	card;

	private JPanel		cardPanel;
	
	private Map<String, JPanel> mapModePanels = new LinkedHashMap<>();
	
	
	public MapFittingPanel(final MapFittingController controller)
	{
		//create the card panel
		cardPanel = new ClearPanel();
		card = new CardLayout();
		cardPanel.setLayout(card);
		
		final JComboBox<String> modeSelectBox = new JComboBox<String>();
		
		//create each of the view mode panels
		for (String key : MapUIRegistry.get().typeNames()) {
			var modePanel = MapUIRegistry.get().create(key, controller);
			mapModePanels.put(key, modePanel);
			cardPanel.add(modePanel, key);
			modeSelectBox.addItem(key);
		}	
		
		//create combobox
		
		modeSelectBox.setRenderer(new AlignedListCellRenderer(SwingConstants.CENTER));
		modeSelectBox.addActionListener(e -> {
			String mode = (String) modeSelectBox.getSelectedItem();
			controller.setMapDisplayMode(mode);
			card.show(cardPanel, mode.toString());
		});
		JPanel modeSelectPanel = new ClearPanel(new BorderLayout());
		modeSelectPanel.add(modeSelectBox, BorderLayout.CENTER);
		modeSelectPanel.setBorder(Spacing.bMedium());
		
		
		
		
		
		FluentButton selectAll = new FluentButton(StockIcon.SELECTION_ALL)
				.withButtonSize(FluentButtonSize.COMPACT)
				.withTooltip("Select All")
				.withBordered(false)
				.withAction(() -> controller.getActiveMode().setAllVisible(true));
		FluentButton selectNone = new FluentButton(StockIcon.SELECTION_NONE)
				.withButtonSize(FluentButtonSize.COMPACT)
				.withTooltip("Select None")
				.withBordered(false)
				.withAction(() -> controller.getActiveMode().setAllVisible(false));
		ComponentStrip linker = new ComponentStrip(selectNone, selectAll);
		linker.setBorder(new EmptyBorder(0, Spacing.small, 0, 0));
		modeSelectPanel.add(linker, BorderLayout.EAST);
		
		//add the two components to this panel
		setLayout(new BorderLayout());
		add(cardPanel, BorderLayout.CENTER);
		add(modeSelectPanel, BorderLayout.NORTH);
		
		
		
	}
	
}

class AlignedListCellRenderer extends DefaultListCellRenderer {
    
    private int align;
    
    public AlignedListCellRenderer(int align) {
        this.align = align;
    }

    @Override
    public Component getListCellRendererComponent(JList list, 
                                                  Object value, 
                                                  int index, 
                                                  boolean isSelected, 
                                                  boolean cellHasFocus) {
        // DefaultListCellRenderer uses a JLabel as the rendering component:
        JLabel lbl = (JLabel)super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);
        lbl.setHorizontalAlignment(align);
        return lbl;
    }
}
