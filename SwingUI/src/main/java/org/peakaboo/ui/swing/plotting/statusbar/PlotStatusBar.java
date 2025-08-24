package org.peakaboo.ui.swing.plotting.statusbar;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.MatteBorder;

import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.controller.plotter.view.mode.ChannelViewMode;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.panels.ClearPanel;
import org.peakaboo.framework.stratus.components.ui.KeyValuePill;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButtonConfig;
import org.peakaboo.framework.stratus.components.ui.header.HeaderLayout;
import org.peakaboo.framework.stratus.components.ui.layers.ToastLayer;
import org.peakaboo.ui.swing.app.widgets.StatusBarPillStrip;
import org.peakaboo.ui.swing.plotting.PlotPanel;

public class PlotStatusBar extends ClearPanel {


	private JLabel channelLabel;
	private PlotZoomControls zoom;
	private PlotScanNumber scanSelector;
	private FluentButton clipboardButton;
	
	private KeyValuePill pView, pChannel, pEnergy, pValue, pRaw;

	private PlotController controller;
	private PlotPanel plotPanel;
	private Timer toastDebounceTimer;
	
	public PlotStatusBar(PlotController controller, PlotPanel plotPanel) {
		this.controller = controller;
		this.plotPanel = plotPanel;
		
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

		zoom = new PlotZoomControls(controller);
		
		
		Timer[] debounceBox = new Timer[1];
		clipboardButton = new FluentButton()
				.withIcon(StockIcon.EDIT_COPY)
				.withTooltip("Copy single spectrum as CSV")
				.withBordered(FluentButtonConfig.BorderStyle.ACTIVE)
				.withAction(() -> {
					SpectrumView spectrum = this.controller.getPlotDataSpectra().filtered();
					float[] data = spectrum.backingArrayCopy();

					// Convert to CSV format - single row of values
					StringBuilder csv = new StringBuilder();
					for (int i = 0; i < data.length; i++) {
						if (i > 0) {
							csv.append(",");
						}
						csv.append(data[i]);
					}

					// Copy to clipboard
					plotPanel.copyInteraction(csv.toString(), debounceBox);
					
				}
		);

		ClearPanel eastPanel = new ClearPanel(new BorderLayout());
		eastPanel.add(clipboardButton, BorderLayout.WEST);
		eastPanel.add(zoom, BorderLayout.EAST);

		scanSelector = new PlotScanNumber(controller);

		this.add(scanSelector);
		this.add(pillstrip);
		this.add(eastPanel);
		this.setLayout(new HeaderLayout(scanSelector, pillstrip, eastPanel));
		this.setBorder(new MatteBorder(1, 0, 0, 0, Stratus.getTheme().getWidgetBorder()));
		
		
		this.setData(controller.view().getChannelViewMode());

	}
	
	public void setWidgetState(boolean hasData) {
		this.setEnabled(hasData);
		
		scanSelector.setWidgetState(hasData);
		zoom.setWidgetState(hasData);
		clipboardButton.setEnabled(hasData);
		
	}


	public void setData(ChannelViewMode viewmode, int channel, float energy, float value, float rawvalue) {
		pView.setValue(viewmode.name());
		
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
		
	public void setData(ChannelViewMode viewmode) {
		pView.setValue(viewmode.name());
		
		if (pChannel.isVisible()) pChannel.setVisible(false);
		if (pEnergy.isVisible()) pEnergy.setVisible(false);
		if (pValue.isVisible()) pValue.setVisible(false);
		if (pRaw.isVisible()) pRaw.setVisible(false);
	}
	

}
