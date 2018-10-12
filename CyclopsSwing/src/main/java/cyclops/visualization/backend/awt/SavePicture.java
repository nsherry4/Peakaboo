package cyclops.visualization.backend.awt;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
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
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;

import cyclops.Coord;
import cyclops.log.SciLog;
import cyclops.visualization.Surface;
import swidget.Swidget;
import swidget.dialogues.fileio.SimpleFileExtension;
import swidget.dialogues.fileio.SwidgetFilePanels;
import swidget.icons.StockIcon;
import swidget.widgets.ClearPanel;
import swidget.widgets.Spacing;
import swidget.widgets.buttons.ImageButton;
import swidget.widgets.layerpanel.LayerPanel;
import swidget.widgets.layerpanel.ModalLayer;
import swidget.widgets.layout.ButtonBox;
import swidget.widgets.layout.HeaderBox;
import swidget.widgets.toggle.ItemToggleButton;
import swidget.widgets.toggle.ToggleGroup;


public class SavePicture extends JPanel
{

	private GraphicsPanel			controller;
	private File					startingFolder;
	private ToggleGroup				group;
	private Component				owner;
	private JDialog					dialog;
	Consumer<Optional<File>> 		onComplete;
	
	private JSpinner spnWidth, spnHeight;
	
	
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
			((LayerPanel) owner).pushLayer(new ModalLayer((LayerPanel) owner, this));
			this.requestFocus();
		} else {
			makeGUI(false);
			showDialog();
		}
	}
	
	public void hide() {
		if (owner instanceof LayerPanel) {
			((LayerPanel) owner).popLayer();
		} else {
			if (dialog != null) {
				dialog.setVisible(false);
				dialog = null;
			}
		}
	}
	
	private void showDialog() {
		
		if (owner instanceof Window) {
			dialog = new JDialog((Window)owner);
		} else {
			dialog = new JDialog();
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



	
	private ImageButton saveButton() {
		ImageButton ok = new ImageButton("Save");
		ok.addActionListener(e -> {
			int selection = group.getToggledIndex();
			Cursor oldCursor = getCursor();
			setCursor(new Cursor(Cursor.WAIT_CURSOR));
			if (selection == 0) savePNG();
			if (selection == 1) saveSVG();
			if (selection == 2) savePDF();
			setCursor(oldCursor);
		});
		return ok;
	}

	private ImageButton cancelButton() {
		ImageButton cancel = new ImageButton("Cancel");
		cancel.addActionListener(e -> {
			onComplete.accept(Optional.empty());
			hide();
		});
		return cancel;
	}
	
	public JPanel createDimensionsPane() {
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		spnWidth = new JSpinner(new SpinnerNumberModel((int)Math.ceil(controller.getUsedWidth()), 100, 10000, 1));
		spnHeight = new JSpinner(new SpinnerNumberModel((int)Math.ceil(controller.getUsedHeight()), 100, 10000, 1));
		
		c.weightx = 0.0;
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
		panel.add(Box.createHorizontalGlue(), c);
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
		
		panel.setBorder(Spacing.bHuge());
		
		return panel;
		
	}

	public JPanel createOptionsPane()
	{

		JPanel panel = new ClearPanel();		
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.PAGE_START;
		c.weighty = 0;
		c.weightx = 0;
		
		
		group = new ToggleGroup();

		ItemToggleButton png, svg, pdf;

		png = new ItemToggleButton(StockIcon.MIME_RASTER, "Pixel Image (PNG)",
				"Pixel based images are a grid of coloured dots. They have a fixed size and level of detail.");
		
		svg = new ItemToggleButton(StockIcon.MIME_SVG, "Vector Image (SVG)",
				"Vector images use points, lines, and curves to define an image. They can be scaled to any size.");

		pdf = new ItemToggleButton(StockIcon.MIME_PDF, "PDF File", "PDF files are a more print-oriented vector image format.");

		group.registerButton(png);
		group.registerButton(svg);
		group.registerButton(pdf);
		

		
		c.gridx = 0;
		c.anchor = GridBagConstraints.PAGE_START;
		c.weighty = 0;
		c.weightx = 0;
		
		
		
		
		
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(createDimensionsPane(), c);
		c.fill = GridBagConstraints.HORIZONTAL;	
		c.gridy++;

		
		panel.add(png, c);
		c.gridy++;
		
		panel.add(svg, c);
		c.gridy++;
		
		panel.add(pdf, c);
		c.gridy++;


		
		group.setToggled(0);

		panel.setBorder(Spacing.bHuge());

		
		return panel;

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
					int width = ((Number)spnWidth.getValue()).intValue();
					int height = ((Number)spnHeight.getValue()).intValue();
					controller.writePNG(os, new Coord<Integer>(width, height));
					os.close();
	
					startingFolder = result.get().getParentFile();
					hide();
					
					setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					setEnabled(true);
					
					onComplete.accept(result);
					
				}
				catch (IOException e)
				{
					SciLog.get().log(Level.SEVERE, "Failed to save PNG", e);
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
					int width = ((Number)spnWidth.getValue()).intValue();
					int height = ((Number)spnHeight.getValue()).intValue();
					controller.writeSVG(os, new Coord<Integer>(width, height));
					os.close();

					startingFolder = result.get().getParentFile();
					hide();
					
					setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					setEnabled(true);
					onComplete.accept(result);
				}
				catch (IOException e)
				{
					SciLog.get().log(Level.SEVERE, "Failed to save SVG", e);
				}

			});
						

	}


	private void savePDF()
	{

		setEnabled(false);
		setCursor(new Cursor(Cursor.WAIT_CURSOR));
		
		SimpleFileExtension pdf = new SimpleFileExtension("Portable Document Format", "pdf");
		SwidgetFilePanels.saveFile(owner, "Save Picture As...", startingFolder, pdf, result -> {
			if (!result.isPresent() ) {
				return;
			}
			try {
				OutputStream os = new FileOutputStream(result.get());				
				int width = ((Number)spnWidth.getValue()).intValue();
				int height = ((Number)spnHeight.getValue()).intValue();
				controller.writePDF(os, new Coord<Integer>(width, height));
				os.close();

				startingFolder = result.get().getParentFile();
				hide();
				
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				setEnabled(true);
				onComplete.accept(result);
				
			}
			catch (IOException e)
			{
				SciLog.get().log(Level.SEVERE, "Failed to save PDF", e);
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
