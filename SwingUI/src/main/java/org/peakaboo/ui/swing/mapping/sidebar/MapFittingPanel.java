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

import org.peakaboo.controller.mapper.fitting.MapFittingController;
import org.peakaboo.display.map.modes.MapModes;
import org.peakaboo.framework.stratus.controls.ButtonLinker;
import org.peakaboo.framework.swidget.icons.StockIcon;
import org.peakaboo.framework.swidget.widgets.ClearPanel;
import org.peakaboo.framework.swidget.widgets.Spacing;
import org.peakaboo.framework.swidget.widgets.buttons.ImageButton;
import org.peakaboo.framework.swidget.widgets.buttons.ImageButtonSize;
import org.peakaboo.ui.swing.mapping.sidebar.modes.Composite;
import org.peakaboo.ui.swing.mapping.sidebar.modes.Overlay;
import org.peakaboo.ui.swing.mapping.sidebar.modes.Ratio;
import org.peakaboo.ui.swing.mapping.sidebar.modes.Scatter;


public class MapFittingPanel extends ClearPanel
{

	private CardLayout	card;

	private JPanel		cardPanel;
	
	private JPanel		compPanel, overPanel, ratioPanel, scatterPanel;
	
	
	
	public MapFittingPanel(final MapFittingController controller)
	{
		//create the card panel
		cardPanel = new ClearPanel();
		card = new CardLayout();
		cardPanel.setLayout(card);
		
		//create each of the three panels
		compPanel = new Composite(controller);
		overPanel = new Overlay(controller);
		ratioPanel = new Ratio(controller);
		scatterPanel = new Scatter(controller);
		
		//add each of the panels
		cardPanel.add(compPanel, MapModes.COMPOSITE.toString());
		cardPanel.add(overPanel, MapModes.OVERLAY.toString());
		cardPanel.add(ratioPanel, MapModes.RATIO.toString());
		cardPanel.add(scatterPanel, MapModes.SCATTER.toString());
		
		
		//create combobox
		final JComboBox<MapModes> modeSelectBox = new JComboBox<>(MapModes.values());
		modeSelectBox.setRenderer(new AlignedListCellRenderer(SwingConstants.CENTER));
		modeSelectBox.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e)
			{
				MapModes mode = (MapModes)modeSelectBox.getSelectedItem();
				controller.setMapDisplayMode(mode);
				card.show(cardPanel, mode.toString());
			}
		});
		JPanel modeSelectPanel = new ClearPanel(new BorderLayout());
		modeSelectPanel.add(modeSelectBox, BorderLayout.CENTER);
		modeSelectPanel.setBorder(Spacing.bSmall());
		
		
		ImageButton selectAll = new ImageButton(StockIcon.SELECTION_ALL)
				.withButtonSize(ImageButtonSize.COMPACT)
				.withTooltip("Select All")
				.withAction(() -> controller.setAllTransitionSeriesVisibility(true));
		ImageButton selectNone = new ImageButton(StockIcon.SELECTION_NONE)
				.withButtonSize(ImageButtonSize.COMPACT)
				.withTooltip("Select None")
				.withAction(() -> controller.setAllTransitionSeriesVisibility(false));
		ButtonLinker linker = new ButtonLinker(selectNone, selectAll);
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
