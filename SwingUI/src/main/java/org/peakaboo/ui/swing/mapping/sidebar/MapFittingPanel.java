package org.peakaboo.ui.swing.mapping.sidebar;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.peakaboo.controller.mapper.fitting.MapFittingController;
import org.peakaboo.display.map.modes.MapModes;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.ButtonLinker;
import org.peakaboo.framework.stratus.components.panels.ClearPanel;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButtonSize;
import org.peakaboo.ui.swing.mapping.sidebar.modes.Composite;
import org.peakaboo.ui.swing.mapping.sidebar.modes.Correlation;
import org.peakaboo.ui.swing.mapping.sidebar.modes.Overlay;
import org.peakaboo.ui.swing.mapping.sidebar.modes.Ratio;
import org.peakaboo.ui.swing.mapping.sidebar.modes.Ternary;


public class MapFittingPanel extends ClearPanel
{

	private CardLayout	card;

	private JPanel		cardPanel;
	
	private JPanel		compPanel, overPanel, ratioPanel, correlationPanel, ternaryPanel;
	
	
	
	public MapFittingPanel(final MapFittingController controller)
	{
		//create the card panel
		cardPanel = new ClearPanel();
		card = new CardLayout();
		cardPanel.setLayout(card);
		
		//create each of the view mode panels
		compPanel = new Composite(controller);
		overPanel = new Overlay(controller);
		ratioPanel = new Ratio(controller);
		correlationPanel = new Correlation(controller);
		ternaryPanel = new Ternary(controller);
		
		//add each of the panels
		cardPanel.add(compPanel, MapModes.COMPOSITE.toString());
		cardPanel.add(overPanel, MapModes.OVERLAY.toString());
		cardPanel.add(ratioPanel, MapModes.RATIO.toString());
		cardPanel.add(correlationPanel, MapModes.CORRELATION.toString());
		cardPanel.add(ternaryPanel, MapModes.TERNARYPLOT.toString());
		
		
		//create combobox
		final JComboBox<MapModes> modeSelectBox = new JComboBox<>(MapModes.values());
		modeSelectBox.setRenderer(new AlignedListCellRenderer(SwingConstants.CENTER));
		modeSelectBox.addActionListener(e -> {
			MapModes mode = (MapModes)modeSelectBox.getSelectedItem();
			controller.setMapDisplayMode(mode);
			card.show(cardPanel, mode.toString());
		});
		JPanel modeSelectPanel = new ClearPanel(new BorderLayout());
		modeSelectPanel.add(modeSelectBox, BorderLayout.CENTER);
		modeSelectPanel.setBorder(Spacing.bMedium());
		
		
		
		
		
		FluentButton selectAll = new FluentButton(StockIcon.SELECTION_ALL)
				.withButtonSize(FluentButtonSize.COMPACT)
				.withTooltip("Select All")
				.withAction(() -> controller.getActiveMode().setAllVisible(true));
		FluentButton selectNone = new FluentButton(StockIcon.SELECTION_NONE)
				.withButtonSize(FluentButtonSize.COMPACT)
				.withTooltip("Select None")
				.withAction(() -> controller.getActiveMode().setAllVisible(false));
		ButtonLinker linker = new ButtonLinker(selectNone, selectAll);
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
