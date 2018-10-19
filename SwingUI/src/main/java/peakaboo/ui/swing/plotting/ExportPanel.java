package peakaboo.ui.swing.plotting;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.ListCellRenderer;
import javax.swing.SpinnerNumberModel;

import cyclops.visualization.backend.awt.GraphicsPanel;
import peakaboo.controller.plotter.PlotController;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.Spacing;
import swidget.widgets.buttons.ImageButton;
import swidget.widgets.layerpanel.LayerPanel;
import swidget.widgets.layerpanel.ModalLayer;
import swidget.widgets.layout.HeaderBox;
import swidget.widgets.listwidget.ListWidget;
import swidget.widgets.listwidget.ListWidgetListCellRenderer;
import swidget.widgets.toggle.ItemToggleButton;

public class ExportPanel extends JPanel {
	
	private PlotController plot;
	private GraphicsPanel canvas;
	
	private JSpinner spnWidth, spnHeight;
	
	private enum PlotFormat {
		PNG, SVG, PDF;
	}
	private JComboBox<PlotFormat> formats;
	
	public ExportPanel(LayerPanel parent, GraphicsPanel canvas, PlotController plot) {
		this.plot = plot;
		this.canvas = canvas;
		//this.setPreferredSize(new Dimension(500, 350));
		this.setLayout(new BorderLayout());
		ModalLayer layer = new ModalLayer(parent, this);
		
		
		HeaderBox header = HeaderBox.createYesNo("Export Data", 
				"OK", () -> {
					parent.removeLayer(layer);
				}, 
				"Cancel", () -> {
					parent.removeLayer(layer);
				}
			);
		this.add(header, BorderLayout.NORTH);
		
		
		JPanel body = new JPanel();
		body.setBorder(Spacing.bHuge());
		body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
		body.add(createDimensionsPane());
		body.add(createFormatPicker());
		this.add(body, BorderLayout.CENTER);
		
		parent.pushLayer(layer);
		
		
	}
	
	
	


	
	public JComboBox<PlotFormat> createFormatPicker() {
		formats = new JComboBox<>(new PlotFormat[] {PlotFormat.PNG, PlotFormat.SVG, PlotFormat.PDF});
		formats.setRenderer(new ListWidgetListCellRenderer<>(new ListWidget<PlotFormat>() {

			private JLabel label;
			{
				setLayout(new BorderLayout());
				setBorder(Spacing.bSmall());
				label = new JLabel();
				add(label, BorderLayout.CENTER);
			}
			
			@Override
			protected void onSetValue(PlotFormat value) {
				switch (value) {
				case PNG:
					label.setIcon(StockIcon.MIME_RASTER.toImageIcon(IconSize.BUTTON));
					label.setText("Pixel Image (PNG)");
					setToolTipText("Pixel based images are a grid of coloured dots. They have a fixed size and level of detail.");
					break;
				case PDF:
					label.setIcon(StockIcon.MIME_PDF.toImageIcon(IconSize.BUTTON));
					label.setText("PDF File");
					setToolTipText("PDF files are a more print-oriented vector image format.");
					break;
				case SVG:
					label.setIcon(StockIcon.MIME_SVG.toImageIcon(IconSize.BUTTON));
					label.setText("Vector Image (SVG)");
					setToolTipText("Vector images use points, lines, and curves to define an image. They can be scaled to any size.");
					break;
				
				}
			}
			
		}));
		
		
		
		return formats;
	}
	
	public JPanel createDimensionsPane() {
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		spnWidth = new JSpinner(new SpinnerNumberModel((int)Math.ceil(canvas.getUsedWidth()), 100, 10000, 1));
		spnHeight = new JSpinner(new SpinnerNumberModel((int)Math.ceil(canvas.getUsedHeight()), 100, 10000, 1));
		
		c.weightx = 0.0;
		c.insets = Spacing.iSmall();
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		panel.add(Box.createHorizontalGlue(), c);
		c.gridx++;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		
		panel.add(new JLabel("Width"), c);
		c.gridx++;
		panel.add(spnWidth, c);
		c.gridx++;
		
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		panel.add(Box.createHorizontalStrut(Spacing.huge), c);
		c.gridx++;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		
		
		panel.add(new JLabel("Height"), c);
		c.gridx++;
		panel.add(spnHeight, c);
		c.gridx++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		panel.add(Box.createHorizontalGlue(), c);
		c.gridx++;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		
		panel.setBorder(Spacing.bLarge());
		
		return panel;
		
	}

	
}


