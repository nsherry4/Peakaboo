package org.peakaboo.ui.swing.plotting.statusbar;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.LayoutManager;
import java.text.DecimalFormat;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.MatteBorder;

import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.controller.plotter.view.ChannelCompositeMode;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.components.panels.ClearPanel;
import org.peakaboo.framework.stratus.components.ui.KeyValuePill;
import org.peakaboo.framework.stratus.components.ui.header.HeaderLayout;
import org.peakaboo.ui.swing.app.widgets.StatusBarPillStrip;

public class PlotStatusBar extends ClearPanel {


	private JLabel channelLabel;
	private PlotZoomControls zoom;
	private PlotScanNumber scanSelector;
	
	private KeyValuePill pView, pChannel, pEnergy, pValue, pRaw;

	private PlotController controller;
	
	public PlotStatusBar(PlotController controller) {
		this.controller = controller;
		
		channelLabel = new JLabel("");
		channelLabel.setHorizontalAlignment(SwingConstants.CENTER);
		channelLabel.setFont(channelLabel.getFont().deriveFont(Font.PLAIN));

		
		
		pView = new KeyValuePill("View");
		pChannel = new KeyValuePill("Channel", 4);
		pEnergy = new KeyValuePill("Energy", new DecimalFormat("0.00"), 6);
		pValue = new KeyValuePill("Value", new DecimalFormat("0.00"), 7);
		pRaw = new KeyValuePill("Raw", new DecimalFormat("0.00"), 7);
		
		var pillstrip = new StatusBarPillStrip();
		pillstrip.addPills(pView, pChannel, pEnergy, pValue, pRaw);
		
		

		channelLabel.setBorder(Spacing.bSmall());
		this.add(pillstrip, BorderLayout.CENTER);

		zoom = new PlotZoomControls(controller);
		this.add(zoom, BorderLayout.EAST);

		scanSelector = new PlotScanNumber(controller);
		this.add(scanSelector, BorderLayout.WEST);

		this.setLayout(new HeaderLayout(scanSelector, pillstrip, zoom));
		this.setBorder(new MatteBorder(1, 0, 0, 0, Stratus.getTheme().getWidgetBorder()));
		
		
		this.setData(controller.view().getChannelCompositeMode());

	}
	
	public void setWidgetState(boolean hasData) {
		this.setEnabled(hasData);
		
		scanSelector.setWidgetState(hasData);
		zoom.setWidgetState(hasData);
		
	}


	public void setData(ChannelCompositeMode viewmode, int channel, float energy, float value, float rawvalue) {
		pView.setValue(viewmode.shortName());
		
		pChannel.setValue(channel);
		if (!pChannel.isVisible()) pChannel.setVisible(true);
		
		pEnergy.setValue(energy);
		if (!pEnergy.isVisible()) pEnergy.setVisible(true);
		
		pValue.setValue(value);
		if (!pValue.isVisible()) pValue.setVisible(true);
		
		if (controller.filtering().getFilterCount() > 0 || rawvalue != value) {
			pRaw.setValue(rawvalue);
			pRaw.setVisible(true);
		} else {
			pRaw.setVisible(false);
		}
	}
		
	public void setData(ChannelCompositeMode viewmode) {
		pView.setValue(viewmode.shortName());
		
		if (pChannel.isVisible()) pChannel.setVisible(false);
		if (pEnergy.isVisible()) pEnergy.setVisible(false);
		if (pValue.isVisible()) pValue.setVisible(false);
		if (pRaw.isVisible()) pRaw.setVisible(false);
	}
	


}
