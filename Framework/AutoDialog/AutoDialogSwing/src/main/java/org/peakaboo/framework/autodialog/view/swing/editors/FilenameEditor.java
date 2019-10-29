package org.peakaboo.framework.autodialog.view.swing.editors;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.framework.autodialog.model.style.editors.FileNameStyle;
import org.peakaboo.framework.swidget.dialogues.fileio.SimpleFileExtension;
import org.peakaboo.framework.swidget.dialogues.fileio.SimpleFileFilter;
import org.peakaboo.framework.swidget.dialogues.fileio.SwidgetFileChooser;
import org.peakaboo.framework.swidget.dialogues.fileio.SwidgetFilePanels;
import org.peakaboo.framework.swidget.icons.StockIcon;
import org.peakaboo.framework.swidget.widgets.buttons.ImageButton;
import org.peakaboo.framework.swidget.widgets.buttons.ImageButtonLayout;

public class FilenameEditor extends AbstractSwingEditor<String> {

	private FileSelector control;
	private FileNameStyle style;
	

	public FilenameEditor() {
		
	}
	
	
	@Override
	public void initialize(Parameter<String> param) {
		this.param = param;
		this.style = (FileNameStyle) param.getStyle();
		this.control = new FileSelector(this, style);
		
		setFromParameter();
		param.getValueHook().addListener(v -> this.setFromParameter());
		param.getEnabledHook().addListener(e -> setEnabled(e));
		
	}
	

	@Override
	public boolean expandVertical() {
		return false;
	}

	@Override
	public boolean expandHorizontal() {
		return true;
	}

	@Override
	public LabelStyle getLabelStyle() {
		return LabelStyle.LABEL_ON_SIDE;
	}

	@Override
	public void setEditorValue(String value) {
		control.setFilename(value);
	}

	
	public void validateFailed() {
		setFromParameter();
	}

	@Override
	public String getEditorValue() {
		return control.getFilename();
	}

	@Override
	public JComponent getComponent() {
		return control;
	}

	@Override
	protected void setEnabled(boolean enabled) {
		control.setEnabled(enabled);
	}
}


class FileSelector extends JPanel
{
	JTextField filenameField;
	JButton open;
	String filename;
	SwidgetFileChooser chooser;
	
	public FileSelector(final FilenameEditor parent, FileNameStyle style) {
		super(new BorderLayout());
		open = new ImageButton().withIcon(StockIcon.DOCUMENT_OPEN).withTooltip("Browse for Files").withLayout(ImageButtonLayout.IMAGE);
		
		filenameField = new JTextField(10);
		filenameField.setEditable(false);
		
		chooser = new SwidgetFileChooser();
		if (style.getTypeTitle() != null && style.getTypeExtensions() != null) {
			chooser.setFileFilter(new SimpleFileFilter(new SimpleFileExtension(style.getTypeTitle(), style.getTypeExtensions())));	
		}
		
		
		open.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				//display dialog
				if (chooser.showOpenDialog(FileSelector.this) != JFileChooser.APPROVE_OPTION) {
					return;
				}
				
				//return if no selection
				if (chooser.getSelectedFile() == null) return;
				
				//update with selection
				setFilename(chooser.getSelectedFile().toString());
				parent.getEditorValueHook().updateListeners(parent.getEditorValue());
				if (!parent.param.setValue(parent.getEditorValue())) {
					parent.validateFailed();
				}
			}
		});
		
		add(open, BorderLayout.EAST);
		add(filenameField);
		
	}
	
	public String getFilename()
	{
		return filename;
	}
	
	public void setFilename(String filename)
	{
		this.filename = filename;
		setFilenameField(filename);
	}
	
	private void setFilenameField(String filename)
	{
		
		String name = "";
		if (filename != null) {
			name = new File(filename).getName();
		}
		filenameField.setText(name);
	}
	

}
