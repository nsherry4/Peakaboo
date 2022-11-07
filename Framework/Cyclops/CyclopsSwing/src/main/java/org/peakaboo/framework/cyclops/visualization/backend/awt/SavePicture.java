package org.peakaboo.framework.cyclops.visualization.backend.awt;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;

import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.log.CyclopsLog;
import org.peakaboo.framework.cyclops.visualization.Surface;
import org.peakaboo.framework.cyclops.visualization.SurfaceType;
import org.peakaboo.framework.swidget.Swidget;
import org.peakaboo.framework.swidget.dialogues.fileio.SimpleFileExtension;
import org.peakaboo.framework.swidget.dialogues.fileio.SwidgetFilePanels;
import org.peakaboo.framework.swidget.live.LiveDialog;
import org.peakaboo.framework.swidget.widgets.Spacing;
import org.peakaboo.framework.swidget.widgets.fluent.button.FluentButton;
import org.peakaboo.framework.swidget.widgets.layerpanel.LayerPanel;
import org.peakaboo.framework.swidget.widgets.layerpanel.ModalLayer;
import org.peakaboo.framework.swidget.widgets.layout.ButtonBox;
import org.peakaboo.framework.swidget.widgets.layout.HeaderBox;
import org.peakaboo.framework.swidget.widgets.options.OptionBlock;
import org.peakaboo.framework.swidget.widgets.options.OptionBlocksPanel;
import org.peakaboo.framework.swidget.widgets.options.OptionRadioButton;


public class SavePicture extends JPanel
{

	private GraphicsPanel			controller;
	private File					startingFolder;
	private Component				owner;
	private JDialog					dialog;
	Consumer<Optional<File>> 		onComplete;
	private FormatPicker			formatPicker;
	private DimensionPicker			dimensionPicker;
	
	
	private ModalLayer				layer = null;
	
	
	public static class FormatPicker extends JPanel {

		private SurfaceType type = SurfaceType.RASTER;
		
		public FormatPicker() {
		
			OptionBlock formats = new OptionBlock();
			ButtonGroup group = new ButtonGroup();
			
			OptionRadioButton raster = new OptionRadioButton(
					formats, 
					group, 
					"Pixel Image (PNG)", 
					"A grid of coloured dots with a fixed size and level of detail",
					type == SurfaceType.RASTER,
					() -> type = SurfaceType.RASTER
				);
			formats.add(raster);
			
			OptionRadioButton vector = new OptionRadioButton(
					formats, 
					group,
					"Scalable Vector Graphic (SVG)", 
					"Defined by points, lines, and curves, they are scalable to any size",
					type == SurfaceType.VECTOR,
					() -> type = SurfaceType.VECTOR
				);
			formats.add(vector);
			
			OptionBlocksPanel panel = new OptionBlocksPanel(formats);
			this.setLayout(new BorderLayout());
			this.add(panel, BorderLayout.CENTER);
			
		}

		public SurfaceType getSelectedSurfaceType() {
			return type;
		}

		
	}

	
	
	
	public static class DimensionPicker extends JPanel {
		
		private JSpinner spnWidth, spnHeight;	
		
		public DimensionPicker(int startWidth, int startHeight) {
		
			this.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();

			final int SIZE_MIN = 100;
			final int SIZE_MAX = 10000;
			
			//if one (or both) of the dimensions are larger than the max 
			//size, we have to calculate the correct scaling factor to keep
			//the aspect ratio
			if (startWidth > SIZE_MAX || startHeight > SIZE_MAX) {
				float scaleRatio = Math.max(((float)startWidth)/SIZE_MAX, ((float)startHeight)/SIZE_MAX);
				startWidth /= scaleRatio;
				startHeight /= scaleRatio;
			}
			
			spnWidth = new JSpinner(new SpinnerNumberModel(Math.min(startWidth, SIZE_MAX), SIZE_MIN, SIZE_MAX, 1));
			spnHeight = new JSpinner(new SpinnerNumberModel(Math.min(startHeight, SIZE_MAX), SIZE_MIN, SIZE_MAX, 1));
			
			c.weightx = 0.0;
			c.fill = GridBagConstraints.NONE;
			c.gridx = 0;
			
			
			c.fill = GridBagConstraints.BOTH;
			c.weightx = 1.0;
			this.add(Box.createHorizontalGlue(), c);
			c.gridx++;
			c.fill = GridBagConstraints.NONE;
			c.weightx = 0.0;
			
			this.add(new JLabel("Width"), c);
			c.gridx++;
			this.add(spnWidth, c);
			c.gridx++;
			
			
			c.fill = GridBagConstraints.BOTH;
			c.weightx = 1.0;
			this.add(Box.createHorizontalGlue(), c);
			c.gridx++;
			c.fill = GridBagConstraints.NONE;
			c.weightx = 0.0;
			
			
			this.add(new JLabel("Height"), c);
			c.gridx++;
			this.add(spnHeight, c);
			c.gridx++;
			
			c.fill = GridBagConstraints.BOTH;
			c.weightx = 1.0;
			this.add(Box.createHorizontalGlue(), c);
			c.gridx++;
			c.fill = GridBagConstraints.NONE;
			c.weightx = 0.0;

		}
		
		public int getDimensionWidth() {
			return ((Number)spnWidth.getValue()).intValue();
		}
		
		public int getDimensionHeight() {
			return ((Number)spnHeight.getValue()).intValue();
		}
		
	}
	
	
	
	public SavePicture(Component owner, GraphicsPanel controller, File startingFolder, Consumer<Optional<File>> onComplete)
	{
		this.onComplete = onComplete;
		this.owner = owner;
		this.controller = controller;
		this.startingFolder = startingFolder;
		
		KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		this.getInputMap(JComponent.WHEN_FOCUSED).put(key, key.toString());
		this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(key, key.toString());
		this.getActionMap().put(key.toString(), new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				hide();
			}
		});
		
	}

	public void show() {
		if (owner instanceof LayerPanel) {
			makeGUI(true);
			layer = new ModalLayer((LayerPanel) owner, this);
			((LayerPanel) owner).pushLayer(layer);
			this.requestFocus();
		} else {
			makeGUI(false);
			showDialog();
		}
	}
	
	public void hide() {
		if (owner instanceof LayerPanel) {
			((LayerPanel) owner).removeLayer(layer);
		} else {
			if (dialog != null) {
				dialog.setVisible(false);
				dialog = null;
			}
		}
	}
	
	private void showDialog() {
		
		if (owner instanceof Window) {
			dialog = new LiveDialog((Window)owner);
		} else {
			dialog = new LiveDialog();
		}

		dialog.setTitle("Save as Image");
		dialog.getContentPane().setLayout(new BorderLayout());
		dialog.getContentPane().add(this, BorderLayout.CENTER);
		
		dialog.pack();
		dialog.setResizable(false);
		dialog.setLocationRelativeTo(owner);
		dialog.setModal(true);
		setVisible(true);
	}

	private void makeGUI(boolean inLayer) {
		if (inLayer) {
			setLayout(new BorderLayout());
			add(createOptionsPane(), BorderLayout.CENTER);
			add(new HeaderBox(cancelButton(), "Save as Image", saveButton().withStateDefault()), BorderLayout.NORTH);
		} else {
			setLayout(new BorderLayout());
			add(createOptionsPane(), BorderLayout.CENTER);
			ButtonBox box = new ButtonBox();
			box.addLeft(cancelButton());
			box.addRight(saveButton());
			add(box, BorderLayout.SOUTH);
		}
	}



	
	private FluentButton saveButton() {
		return new FluentButton("Save").withAction(() -> {
			Cursor oldCursor = getCursor();
			setCursor(new Cursor(Cursor.WAIT_CURSOR));
			saveSurfaceType(formatPicker.getSelectedSurfaceType());
			setCursor(oldCursor);
		});
	}

	private FluentButton cancelButton() {
		return new FluentButton("Cancel").withAction(() -> {
			onComplete.accept(Optional.empty());
			hide();
		});
	}
	



	public JPanel createOptionsPane() {
		
		JPanel panel = new JPanel(new BorderLayout(Spacing.huge, Spacing.huge));
		panel.setBorder(Spacing.bHuge());
		
		dimensionPicker = new DimensionPicker((int)Math.ceil(controller.getUsedWidth()), (int)Math.ceil(controller.getUsedHeight()));
		panel.add(dimensionPicker, BorderLayout.NORTH);
		
		formatPicker = new FormatPicker();
		panel.add(formatPicker, BorderLayout.CENTER);
		
		return panel;

	}


	
	
	private void saveSurfaceType(SurfaceType format) {
		switch (format) {
		case RASTER: 
			savePNG();
			return;
		case VECTOR:
			saveSVG();
			return;
		}
	}
	

	private void savePNG()
	{


			
			setEnabled(false);
			setCursor(new Cursor(Cursor.WAIT_CURSOR));
			

			SimpleFileExtension png = new SimpleFileExtension("Portable Network Graphic", "png");
			SwidgetFilePanels.saveFile(owner, "Save Picture As...", startingFolder, png, result -> {
				if (!result.isPresent()) {
					return;
				}
				try
				{
					OutputStream os = new FileOutputStream(result.get());
					controller.writePNG(os, new Coord<Integer>(dimensionPicker.getDimensionWidth(), dimensionPicker.getDimensionHeight()));
					os.close();
	
					startingFolder = result.get().getParentFile();
					hide();
					
					setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					setEnabled(true);
					
					onComplete.accept(result);
					
				}
				catch (IOException e)
				{
					CyclopsLog.get().log(Level.SEVERE, "Failed to save PNG", e);
				}
			});


	}


	private void saveSVG()
	{

			
			setEnabled(false);
			setCursor(new Cursor(Cursor.WAIT_CURSOR));
			
			SimpleFileExtension svg = new SimpleFileExtension("Scalable Vector Graphic", "svg");
			SwidgetFilePanels.saveFile(owner, "Save Picture As...", startingFolder, svg, result -> {
				if (!result.isPresent()) {
					return;
				}
				try
				{
					OutputStream os = new FileOutputStream(result.get());				
					controller.writeSVG(os, new Coord<Integer>(dimensionPicker.getDimensionWidth(), dimensionPicker.getDimensionHeight()));
					os.close();

					startingFolder = result.get().getParentFile();
					hide();
					
					setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					setEnabled(true);
					onComplete.accept(result);
				}
				catch (IOException e)
				{
					CyclopsLog.get().log(Level.SEVERE, "Failed to save SVG", e);
				}

			});
						

	}

	public File getStartingFolder()
	{
		return startingFolder;
	}

	
	private File tempfile() throws IOException
	{
		final File tempfile = File.createTempFile("Image File - ", " export");
		tempfile.deleteOnExit();
		return tempfile;
	}
	
	
	public static void main(String[] args) throws InterruptedException {
		

		

		
		GraphicsPanel g = new GraphicsPanel() {
			
			@Override
			public float getUsedWidth(float zoom) {
				// TODO Auto-generated method stub
				return 1000;
			}
			
			@Override
			public float getUsedWidth() {
				// TODO Auto-generated method stub
				return 2000;
			}
			
			@Override
			public float getUsedHeight(float zoom) {
				// TODO Auto-generated method stub
				return 500;
			}
			
			@Override
			public float getUsedHeight() {
				// TODO Auto-generated method stub
				return 1000;
			}
			
			@Override
			protected void drawGraphics(Surface backend, Coord<Integer> size) {
				// TODO Auto-generated method stub
				
			}
		};
		
		Swidget.initializeAndWait("Test");
		
		new SavePicture(null, g, null, file -> {});
		
		
		
		
		
	}

}
