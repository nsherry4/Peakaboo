package peakaboo.ui.swing.fileio;



import java.awt.Window;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.jnlp.UnavailableServiceException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import fava.*;
import static fava.Fn.*;
import static fava.Functions.*;

import peakaboo.common.Env;
import peakaboo.fileio.AbstractFile;
import peakaboo.fileio.IOCommon;
import peakaboo.ui.swing.icons.IconFactory;
import peakaboo.ui.swing.icons.IconSize;
import peakaboo.ui.swing.widgets.dialogues.SimpleFileFilter;
import peakaboo.ui.swing.widgets.dialogues.SimpleIODialogues;



public class SwingIO
{

	public static List<AbstractFile> openFiles(Window parent, String title, String[] exts, String extDesc,
			String startDir)
	{

		if (Env.isWebStart())
		{

			try
			{
				return IOCommon.wsOpenFiles("~/", exts);
			}
			catch (UnavailableServiceException e)
			{
				JOptionPane.showMessageDialog(
						parent,
						"The Web Start File-Read Service is not Available.",
						"Read Failed.",
						JOptionPane.ERROR_MESSAGE,
						IconFactory.getImageIcon("warn", IconSize.ICON));
				return null;
			}

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
				return map(chooser.getSelectedFiles(), new FunctionMap<File, AbstractFile>() {

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

		if (Env.isWebStart())
		{

			try
			{
				return IOCommon.wsOpenFile("~/", exts);
			}
			catch (UnavailableServiceException e)
			{
				JOptionPane.showMessageDialog(
						parent,
						"The Web Start File-Read Service is not Available.",
						"Read Failed.",
						JOptionPane.ERROR_MESSAGE,
						IconFactory.getImageIcon("warn", IconSize.ICON));
				return null;
			}

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



		if (Env.isWebStart())
		{

			try
			{
				IOCommon.wsSaveFile("~/", "", new String[] { ext }, bais);
			}
			catch (UnavailableServiceException e)
			{
				JOptionPane.showMessageDialog(
						parent,
						"The Web Start File-Write Service is not Available.",
						"Write Failed.",
						JOptionPane.ERROR_MESSAGE,
						IconFactory.getImageIcon("warn", IconSize.ICON));
				return null;
			}
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
