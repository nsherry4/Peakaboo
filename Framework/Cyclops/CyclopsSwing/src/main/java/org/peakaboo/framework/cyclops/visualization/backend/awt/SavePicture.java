package org.peakaboo.framework.cyclops.visualization.backend.awt;


import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;

import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.log.CyclopsLog;
import org.peakaboo.framework.cyclops.visualization.Surface;
import org.peakaboo.framework.cyclops.visualization.descriptor.SurfaceDescriptor;
import org.peakaboo.framework.cyclops.visualization.descriptor.SurfaceExporterRegistry;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.components.dialogs.fileio.SimpleFileExtension;
import org.peakaboo.framework.stratus.components.dialogs.fileio.StratusFilePanels;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;
import org.peakaboo.framework.stratus.components.ui.header.HeaderBox;
import org.peakaboo.framework.stratus.components.ui.layers.LayerPanel;
import org.peakaboo.framework.stratus.components.ui.layers.ModalLayer;
import org.peakaboo.framework.stratus.components.ui.options.OptionChooserPanel;
import org.peakaboo.framework.stratus.components.ui.options.OptionRadioButton;


public class SavePicture extends JPanel {

	private GraphicsPanel			controller;
	private File					startingFolder;
	private LayerPanel				owner;
	Consumer<Optional<File>> 		onComplete;
	private OptionChooserPanel<SurfaceDescriptor> formatPicker;
	private DimensionPicker			dimensionPicker;
	
	
	private ModalLayer				layer = null;
	
	
	
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
	
	
	
	public SavePicture(LayerPanel owner, GraphicsPanel controller, File startingFolder, Consumer<Optional<File>> onComplete) {
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
		makeGUI();
		layer = new ModalLayer((LayerPanel) owner, this);
		((LayerPanel) owner).pushLayer(layer);
		this.requestFocus();
	}
	
	public void hide() {
		((LayerPanel) owner).removeLayer(layer);
	}
	

	private void makeGUI() {
		setLayout(new BorderLayout());
		add(createOptionsPane(), BorderLayout.CENTER);
		add(new HeaderBox(cancelButton(), "Save as Image", saveButton().withStateDefault()), BorderLayout.NORTH);
	}



	
	private FluentButton saveButton() {
		return new FluentButton("Save").withAction(() -> {
			Cursor oldCursor = getCursor();
			setCursor(new Cursor(Cursor.WAIT_CURSOR));
			saveSurfaceType(formatPicker.getSelected());
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
		
		
		
		formatPicker = new OptionChooserPanel<>(SurfaceExporterRegistry.exporters(), item -> {
			return new OptionRadioButton().withText(item.title(), item.description());
		});
		panel.add(formatPicker, BorderLayout.CENTER);
		
		return panel;

	}

	private void saveSurfaceType(SurfaceDescriptor descriptor) {
		
		setEnabled(false);
		setCursor(new Cursor(Cursor.WAIT_CURSOR));

		var ext = new SimpleFileExtension(descriptor.title() + " (" + descriptor.extension() + ")", descriptor.extension().toLowerCase());
		
		StratusFilePanels.saveFile(owner, "Save Picture As...", startingFolder, ext, result -> {
			if (!result.isPresent()) { return; }
			
			try {
				OutputStream os = new FileOutputStream(result.get());
				controller.write(descriptor, os, new Coord<Integer>(dimensionPicker.getDimensionWidth(), dimensionPicker.getDimensionHeight()));
				os.close();

				startingFolder = result.get().getParentFile();
				hide();
				
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				setEnabled(true);
				
				onComplete.accept(result);
				
			} catch (IOException e) {
				CyclopsLog.get().log(Level.SEVERE, "Failed to save image", e);
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
		
		Stratus.initializeAndWait("Test");
		
		new SavePicture(null, g, null, file -> {});
		
		
		
		
		
	}

}
