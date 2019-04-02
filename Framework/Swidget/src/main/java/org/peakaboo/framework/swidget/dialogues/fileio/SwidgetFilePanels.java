package org.peakaboo.framework.swidget.dialogues.fileio;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.basic.BasicFileChooserUI;

import org.peakaboo.framework.swidget.dialogues.fileio.specialdirectories.SpecialDirectories;
import org.peakaboo.framework.swidget.dialogues.fileio.specialdirectories.SpecialDirectoriesWidget;
import org.peakaboo.framework.swidget.widgets.buttons.ImageButton;
import org.peakaboo.framework.swidget.widgets.layerpanel.HeaderLayer;
import org.peakaboo.framework.swidget.widgets.layerpanel.LayerDialog;
import org.peakaboo.framework.swidget.widgets.layerpanel.LayerPanel;
import org.peakaboo.framework.swidget.widgets.layerpanel.ModalLayer;
import org.peakaboo.framework.swidget.widgets.layerpanel.LayerDialog.MessageType;
import org.peakaboo.framework.swidget.widgets.layout.HeaderBox;
import org.peakaboo.framework.swidget.widgets.layout.HeaderPanel;

public class SwidgetFilePanels {

	private static void showChooser(Component parent, JFileChooser chooser, Runnable onAccept, Runnable onCancel, String title) {
		if (LayerPanel.parentOf(parent)) {
			LayerPanel tabPanel = LayerPanel.parentFor(parent);
			chooser.setControlButtonsAreShown(false);
			ImageButton affirmative = new ImageButton(chooser.getApproveButtonText()).withStateDefault();
			ImageButton negative = new ImageButton("Cancel");
			
			HeaderLayer layer = new HeaderLayer(tabPanel, false);
			JPanel chooserPanel = new JPanel(new BorderLayout());
			chooserPanel.add(chooser, BorderLayout.CENTER);
			if (SpecialDirectories.supported()) {
				chooserPanel.add(new SpecialDirectoriesWidget(chooser), BorderLayout.WEST);
			}
			layer.getHeader().setComponents(negative, title, affirmative);
			layer.setBody(chooserPanel);
			
			chooser.addActionListener(action -> {
				String command = action.getActionCommand();
				//something like double-clicking a file may trigger this
				if (command.equals(JFileChooser.APPROVE_SELECTION)) {
					layer.remove();
					onAccept.run();
				}
				if (command.equals(JFileChooser.CANCEL_SELECTION)) {
					layer.remove();
					onCancel.run();
				}
			});
			
			/*
			 * Can this really be the only way to get the file chooser to perform the same action as 
			 * the standard control buttons? Calling chooser.approveSelection() just returns the files
			 * selected in the list control without considering text typed into the filename box.
			 */
			BasicFileChooserUI ui = (BasicFileChooserUI) chooser.getUI();
			affirmative.addActionListener(ui.getApproveSelectionAction());
			negative.addActionListener(ui.getCancelSelectionAction());
			
			tabPanel.pushLayer(layer);
			chooser.requestFocus();

		} else {
			int result = chooser.showSaveDialog(parent);
			if (result == JFileChooser.APPROVE_OPTION) {
				onAccept.run();
			} else if (result == JFileChooser.ERROR_OPTION || result == JFileChooser.CANCEL_OPTION) {
				onCancel.run();
			}
		}	
	}
	
	public static void saveFile(Component parent, String title, File startingFolder, SimpleFileExtension extension, Consumer<Optional<File>> callback)
	{

		JFileChooser chooser = new JFileChooser(startingFolder);
		chooser.setMultiSelectionEnabled(false);
		chooser.setDialogTitle(title);
		chooser.setFileFilter(extension.getFilter());
		chooser.setApproveButtonText("Save");
		chooser.setApproveButtonToolTipText("Save Selected File");
		
		
				
		//Run this when the dialog is accepted
		Runnable onAccept = () -> {
			String filename = chooser.getSelectedFile().toString();
			if (!extension.match(filename)) {
				filename += "." + extension.getExtensions().get(0);
			}
			File file = new File(filename);
			
			warnFileExists(parent, file, proceed -> {
				if (proceed) {
					callback.accept(Optional.of(file));
				} else {
					callback.accept(Optional.empty());
				}	
			});
			
		};
		
		Runnable onCancel = () -> {
			callback.accept(Optional.empty());
		};
		
		showChooser(parent, chooser, onAccept, onCancel, title);
		

	}
	
	
	
	
	private static JFileChooser getOpener(String title, File startingFolder, List<SimpleFileExtension> extensions) {
		JFileChooser chooser = new JFileChooser(startingFolder);
		chooser.setDialogTitle(title);
		
		chooser.setApproveButtonText("Open");
		chooser.setApproveButtonToolTipText("Open Selected File(s)");
		
		//First all an All Formats filter
		if (extensions.size() > 1) {
			SimpleFileExtension all = new SimpleFileExtension("All Supported Formats");
			for (SimpleFileExtension extension : extensions) {
				all.exts.addAll(extension.getExtensions());
			}
			chooser.addChoosableFileFilter(all.getFilter());
		}
		//Then add all the extensions one at a time
		for (SimpleFileExtension extension : extensions) {
			chooser.addChoosableFileFilter(extension.getFilter());
		}
		//Then set All Formats (or the only format) option as default.
		//There will be an "All Files" option at [0]
		FileFilter chosenFilter = chooser.getChoosableFileFilters()[1];
		chooser.setFileFilter(chosenFilter);
		
		return chooser;
	}
	
	
	
	public static void openFile(Component parent, String title, File startingFolder, SimpleFileExtension extension, Consumer<Optional<File>> callback) {
		openFile(parent, title, startingFolder, Collections.singletonList(extension), callback);
	}
	
	public static void openFile(Component parent, String title, File startingFolder, List<SimpleFileExtension> extensions, Consumer<Optional<File>> callback)
	{
		JFileChooser chooser = getOpener(title, startingFolder, extensions);
		chooser.setMultiSelectionEnabled(false);


		//Run this when the dialog is accepted
		Runnable onAccept = () -> {
			File file = chooser.getSelectedFile();
			callback.accept(Optional.of(file));
		};
		
		Runnable onCancel = () -> {
			callback.accept(Optional.empty());
		};
		
		
		showChooser(parent, chooser, onAccept, onCancel, title);
		
	}
	
	
	
	public static void openFiles(Component parent, String title, File startingFolder, SimpleFileExtension extension, Consumer<Optional<List<File>>> callback) {
		openFiles(parent, title, startingFolder, Collections.singletonList(extension), callback);
	}
	
	public static void openFiles(Component parent, String title, File startingFolder, List<SimpleFileExtension> extensions, Consumer<Optional<List<File>>> callback)
	{
		JFileChooser chooser = getOpener(title, startingFolder, extensions);
		chooser.setMultiSelectionEnabled(true);

		//Run this when the dialog is accepted
		Runnable onAccept = () -> {
			List<File> files = Arrays.asList(chooser.getSelectedFiles());
			callback.accept(Optional.of(files));
		};
		
		Runnable onCancel = () -> {
			callback.accept(Optional.empty());
		};
		
		showChooser(parent, chooser, onAccept, onCancel, title);
		
	}
	
	
	
	static void warnFileExists(Component parent, File filename, Consumer<Boolean> onResult)
	{
		
		String body = "The file you have selected already exists, are you sure you want to replace it?";
		String title = "File Already Exists";
		
		if (filename.exists()) {
			if (parent instanceof LayerPanel) {
				
				new LayerDialog(
						title, 
						body, 
						MessageType.QUESTION)
					.addLeft(new ImageButton("Cancel").withAction(() -> onResult.accept(false)))
					.addRight(new ImageButton("Replace").withAction(() -> onResult.accept(true)))
					.showIn((LayerPanel) parent);
				
			} else if (parent instanceof Window) {
				
				new LayerDialog(
						title, 
						body, 
						MessageType.QUESTION)
					.addLeft(new ImageButton("Cancel").withAction(() -> onResult.accept(false)))
					.addRight(new ImageButton("Replace").withAction(() -> onResult.accept(true)))
					.showInWindow((Window)parent);
			} else {
				
				//??? fallback
				int response = JOptionPane.showConfirmDialog(parent,
						"The file you have selected already exists, are you sure you want to replace it?",
						"File Already Exists", JOptionPane.YES_NO_OPTION
					);
				onResult.accept(response == JOptionPane.YES_OPTION);
				
			}

		} else {
			onResult.accept(true);
		}

	}

	
}
