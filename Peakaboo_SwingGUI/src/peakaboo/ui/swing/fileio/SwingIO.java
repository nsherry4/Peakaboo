package peakaboo.ui.swing.fileio;



import java.awt.Window;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import peakaboo.common.OS;
import peakaboo.datatypes.SigDigits;
import peakaboo.datatypes.functional.Function1;
import peakaboo.datatypes.functional.Functional;
import peakaboo.datatypes.peaktable.TransitionSeries;
import peakaboo.fileio.AbstractFile;
import peakaboo.fileio.IOCommon;
import peakaboo.ui.swing.widgets.dialogues.SimpleFileFilter;
import peakaboo.ui.swing.widgets.dialogues.SimpleIODialogues;



public class SwingIO
{

	public static List<AbstractFile> openFiles(Window parent, String title, String[] exts, String extDesc, String startDir)
	{

		if (OS.isWebStart())
		{

			return IOCommon.openFiles("~/", exts);

		}
		else
		{

			JFileChooser chooser = new JFileChooser(startDir);
			chooser.setMultiSelectionEnabled(true);
			chooser.setDialogTitle(title);

			SimpleFileFilter filter = new SimpleFileFilter();
			for (String ext : exts)
			{
				filter.addExtension(ext);
			}
			filter.setDescription(extDesc);
			chooser.setFileFilter(filter);

			int returnVal = chooser.showOpenDialog(parent);
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				return Functional.map(chooser.getSelectedFiles(), new Function1<File, AbstractFile>() {

					public AbstractFile f(File f)
					{
						return new AbstractFile(f.toString());
					}
				});

			}
			else
			{
				return null;
			}

		}

	}
	
	

	public static AbstractFile openFile(Window parent, String title, String[] exts, String extDesc, String startDir)
	{

		if (OS.isWebStart())
		{

			return IOCommon.openFile("~/", exts);

		}
		else
		{

			JFileChooser chooser = new JFileChooser(startDir);
			chooser.setMultiSelectionEnabled(true);
			chooser.setDialogTitle(title);

			SimpleFileFilter filter = new SimpleFileFilter();
			for (String ext : exts)
			{
				filter.addExtension(ext);
			}
			filter.setDescription(extDesc);
			chooser.setFileFilter(filter);

			int returnVal = chooser.showOpenDialog(parent);
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				return new AbstractFile(chooser.getSelectedFile().toString());
			}
			else
			{
				return null;
			}

		}


	}


	public static ByteArrayOutputStream getSaveFileBuffer()
	{
		return new ByteArrayOutputStream();


	}


	public static String saveFile(Window parent, String title, String ext, String extDesc, String startDir,
			ByteArrayOutputStream outStream) throws IOException
	{

		outStream.close();
		
		ByteArrayInputStream bais = new ByteArrayInputStream(outStream.toByteArray());
		

		
		if (OS.isWebStart())
		{

			IOCommon.saveFile("~/", "", new String[] { ext }, bais);
			return "";

		}
		else
		{

			String saveFilename = SimpleIODialogues.chooseFileSave(parent, title, startDir, ext, extDesc);

			if (saveFilename != null)
			{
				File saveFile = new File(saveFilename);
				String savePictureFolder = saveFile.getParent();
				FileOutputStream fos = new FileOutputStream(saveFile);
				fos.write(outStream.toByteArray());
				fos.flush();
				fos.close();
				
				return savePictureFolder;
			}
			
			return "";

		}


	}

}
