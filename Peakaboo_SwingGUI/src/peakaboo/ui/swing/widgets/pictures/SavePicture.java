package peakaboo.ui.swing.widgets.pictures;


import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import peakaboo.controller.CanvasController;
import swidget.containers.SwidgetContainer;
import swidget.containers.SwidgetDialog;
import swidget.dialogues.fileio.SwidgetIO;
import swidget.icons.StockIcon;
import swidget.widgets.ClearPanel;
import swidget.widgets.ImageButton;
import swidget.widgets.Spacing;
import swidget.widgets.toggle.ComplexToggle;
import swidget.widgets.toggle.ComplexToggleGroup;


public class SavePicture extends SwidgetDialog
{

	private CanvasController	controller;
	private String				startingFolder;
	swidget.widgets.toggle.ComplexToggleGroup			group;


	JPanel				controlsPanel;
	
	public SavePicture(SwidgetContainer owner, CanvasController controller, String startingFolder)
	{

		super(owner, "Save as Image");

		this.controller = controller;
		this.startingFolder = startingFolder;

		init();

	}
	
	private void init()
	{

		controlsPanel = new ClearPanel();

		controlsPanel.setLayout(new BorderLayout());
		controlsPanel.add(createOptionsPane(), BorderLayout.CENTER);
		controlsPanel.add(createControlPanel(), BorderLayout.SOUTH);

		add(controlsPanel);

		//int height = getPreferredSize().height;
		//setPreferredSize(new Dimension(500, height));

		

		pack();
		setResizable(false);
		centreOnParent();
		setModal(true);
		setVisible(true);
	}


	public JPanel createControlPanel()
	{

		JPanel buttonBox = new ClearPanel();
		buttonBox.setLayout(new BoxLayout(buttonBox, BoxLayout.LINE_AXIS));


		ImageButton ok = new ImageButton("document-save", "Save", true);
		ImageButton cancel = new ImageButton(StockIcon.CHOOSE_CANCEL, "Cancel", true);

		ok.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{

				int selection = group.getToggledIndex();
				Cursor oldCursor = getCursor();
				setCursor(new Cursor(Cursor.WAIT_CURSOR));
				if (selection == 0) savePNG();
				if (selection == 1) saveSVG();
				if (selection == 2) savePDF();
				setCursor(oldCursor);

			}
		});

		cancel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				setVisible(false);
			}
		});

		buttonBox.add(Box.createHorizontalGlue());
		buttonBox.add(ok);
		buttonBox.add(Box.createHorizontalStrut(5));
		buttonBox.add(cancel);

		buttonBox.setBorder(Spacing.bHuge());

		return buttonBox;

	}


	public JPanel createOptionsPane()
	{

		JPanel panel = new ClearPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
	
		group = new ComplexToggleGroup();

		ComplexToggle png, svg, pdf;

		png = new ComplexToggle(StockIcon.MIME_RASTER, "Pixel Image (PNG)",
				"Pixel based images are a grid of coloured dots. They have a fixed size and level of detail.", group);
		
		svg = new ComplexToggle(StockIcon.MIME_SVG, "Vector Image (SVG)",
				"Vector images use points, lines, and curves to define an image. They can be scaled to any size.",
				group);

		pdf = new ComplexToggle(StockIcon.MIME_PDF, "PDF File", "PDF files are a more print-oriented vector image format.",
				group);

		png.setPreferredSize(new Dimension(500, png.getPreferredSize().height));
		svg.setPreferredSize(new Dimension(500, svg.getPreferredSize().height));
		pdf.setPreferredSize(new Dimension(500, pdf.getPreferredSize().height));

		panel.add(png);
		panel.add(svg);
		panel.add(pdf);

		group.setToggled(0);

		panel.setBorder(Spacing.bHuge());

		return panel;

	}


	private void savePNG()
	{

		try
		{
			
			controlsPanel.setEnabled(false);
			controlsPanel.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			
			ByteArrayOutputStream baos = SwidgetIO.getSaveFileBuffer();				
			controller.writePNG(baos);

			String result = SwidgetIO.saveFile(this, "Save Picture As...", "png", "Portable Network Graphic", startingFolder, baos);

			if (result != null)
			{
				setVisible(false);
				startingFolder = result;
			}
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			setEnabled(true);
			
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	private void saveSVG()
	{


		try
		{
			
			controlsPanel.setEnabled(false);
			controlsPanel.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			
			ByteArrayOutputStream baos = SwidgetIO.getSaveFileBuffer();				
			controller.writeSVG(baos);

			String result = SwidgetIO.saveFile(this, "Save Picture As...", "svg", "Scalable Vector Graphic", startingFolder, baos);

			if (result != null)
			{
				setVisible(false);
				startingFolder = result;
			}
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			setEnabled(true);
			
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	}


	private void savePDF()
	{

		try
		{
			
			controlsPanel.setEnabled(false);
			controlsPanel.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			
			ByteArrayOutputStream baos = SwidgetIO.getSaveFileBuffer();				
			controller.writePDF(baos);

			String result = SwidgetIO.saveFile(this, "Save Picture As...", "pdf", "Portable Document Format", startingFolder, baos);

			if (result != null)
			{
				setVisible(false);
				startingFolder = result;
			}
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			setEnabled(true);
			
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	
	public String getStartingFolder()
	{
		return startingFolder;
	}

	

}
