package org.peakaboo.ui.swing.mapping.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.controller.mapper.settings.MapSettingsController;
import org.peakaboo.framework.cyclops.visualization.palette.ColourStopPalette;
import org.peakaboo.framework.cyclops.visualization.palette.Gradient;
import org.peakaboo.framework.cyclops.visualization.palette.Gradients;
import org.peakaboo.framework.cyclops.visualization.palette.PaletteColour;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.components.panels.ClearPanel;
import org.peakaboo.framework.stratus.components.ui.options.OptionBlock;
import org.peakaboo.framework.stratus.components.ui.options.OptionBlocksPanel;
import org.peakaboo.framework.stratus.components.ui.options.OptionCheckBox;
import org.peakaboo.framework.stratus.components.ui.options.OptionRadioButton;
import org.peakaboo.framework.stratus.components.ui.options.OptionSize;

public class MapMenuView extends JPopupMenu {

	private MappingController controller;
	
	public MapMenuView(MappingController controller) {
		this.controller = controller;
		
		MapSettingsController settings = controller.getSettings();
		
		ClearPanel panel = new ClearPanel(new BorderLayout());
		panel.setBorder(Spacing.bHuge());		
		
		ButtonGroup paletteGroup = new ButtonGroup();
		OptionBlock paletteBlock = new OptionBlock();
		
		paletteBlock.add(makeOpt(Gradients.SPECTRUM, paletteBlock, paletteGroup));
		paletteBlock.add(makeOpt(Gradients.GEORGIA, paletteBlock, paletteGroup));
		paletteBlock.add(makeOpt(Gradients.IRON, paletteBlock, paletteGroup));
		paletteBlock.add(makeOpt(Gradients.NAVIA, paletteBlock, paletteGroup));
		paletteBlock.add(makeOpt(Gradients.LAJOLLA, paletteBlock, paletteGroup));
		paletteBlock.add(makeOpt(Gradients.OSLO, paletteBlock, paletteGroup));
		paletteBlock.add(makeOpt(Gradients.MONOCHROME, paletteBlock, paletteGroup));
		paletteBlock.add(makeOpt(Gradients.INV_MONOCHROME, paletteBlock, paletteGroup));
				
		OptionBlocksPanel blockPanel = new OptionBlocksPanel(OptionSize.SMALL, paletteBlock);	
		panel.add(blockPanel, BorderLayout.WEST);
		
		
		OptionBlock viewBlock = new OptionBlock().withDividers(false).withBorder(false);
		
		OptionCheckBox opt;
		opt = new OptionCheckBox(viewBlock)
				.withTitle("Show Elements List")
				.withSelection(settings.getShowTitle())
				.withListener(settings::setShowTitle);
		viewBlock.add(opt);
		
		opt = new OptionCheckBox(viewBlock)
				.withTitle("Show Dataset Title")
				.withSelection(settings.getShowDatasetTitle())
				.withListener(settings::setShowDatasetTitle);
		viewBlock.add(opt);
		
		opt = new OptionCheckBox(viewBlock)
				.withTitle("Show Spectrum")
				.withSelection(settings.getShowSpectrum())
				.withListener(settings::setShowSpectrum);
		viewBlock.add(opt);
		
		opt = new OptionCheckBox(viewBlock)
				.withTitle("Show Coordinates")
				.withSelection(settings.getShowCoords())
				.withListener(settings::setShowCoords);
		viewBlock.add(opt);
		
		opt = new OptionCheckBox(viewBlock)
				.withTitle("Show Scale Bar")
				.withSelection(settings.getShowScaleBar())
				.withListener(settings::setShowScaleBar);
		viewBlock.add(opt);
		
		blockPanel = new OptionBlocksPanel(OptionSize.SMALL, viewBlock);
		blockPanel.setBorder(new EmptyBorder(Spacing.none, Spacing.huge, Spacing.none, Spacing.none));
		panel.add(blockPanel, BorderLayout.EAST);
		
		
		JLabel energyTitle = new JLabel("Map Display Options");
		energyTitle.setHorizontalAlignment(SwingConstants.CENTER);
		energyTitle.setFont(energyTitle.getFont().deriveFont(Font.BOLD));
		energyTitle.setBorder(new EmptyBorder(Spacing.none, Spacing.huge, Spacing.huge, Spacing.huge));
		panel.add(energyTitle, BorderLayout.NORTH);
		
		this.add(panel);

	}
	
	private OptionRadioButton<Gradient> makeOpt(Gradient g, OptionBlock block, ButtonGroup group) {
		var opt = new OptionRadioButton<>(block, group, g)
				.withListener(this::set)
				.withTitle(g.getName());
		opt.getButton().setIcon(gradientToIcon(g));
		return opt;
	}
	
	private void set(Gradient g) {
		this.controller.getSettings().setColourGradient(g);
	}

	private ImageIcon gradientToIcon(Gradient g) {
		int width = 64;
		int height = 16;
		
		// Generate the raw gradient
		ColourStopPalette palette = new ColourStopPalette(g);
		BufferedImage i = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		WritableRaster raster = i.getRaster();
		for (int x = 0; x < width; x++) {
			PaletteColour col = palette.getFillColour(x, width);
			int[] argb = new int[] {col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha()};
			for (int y = 0; y < height; y++) {
				raster.setPixel(x, y, argb);;
			}
		}
		
		// Paint the gradient image onto another image within a rounded rectangle shape
		BufferedImage c = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = Stratus.g2d(c.createGraphics());
		var clip = new RoundRectangle2D.Float(0, 0, width, height, 10, 10);
		var bounds = new Rectangle2D.Float(0, 0, width, height);
		var texture = new TexturePaint(i, bounds);
		g2d.setPaint(texture);
		g2d.fill(clip);
		
		return new ImageIcon(c);
		
	}
	
}
