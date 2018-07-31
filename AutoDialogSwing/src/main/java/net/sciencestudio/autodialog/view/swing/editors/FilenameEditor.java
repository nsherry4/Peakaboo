package net.sciencestudio.autodialog.view.swing.editors;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sciencestudio.autodialog.model.Parameter;
import swidget.icons.StockIcon;
import swidget.widgets.ImageButton;
import swidget.widgets.ImageButton.Layout;

public class FilenameEditor extends AbstractSwingEditor<String> {

	private FileSelector control = new FileSelector(this);
	

	public FilenameEditor() {
		
	}
	
	public FilenameEditor(JFileChooser chooser) {
		setFileChooser(chooser);
	}
	
	@Override
	public void initialize(Parameter<String> param) {
		this.param = param;
		
		setFromParameter();
		param.getValueHook().addListener(v -> this.setFromParameter());
		param.getEnabledHook().addListener(e -> setEnabled(e));
		
	}
	
	public void setFileChooser(JFileChooser chooser) {
		control.chooser = chooser;
	}
	
	public JFileChooser getFileChooser() {
		return control.chooser;
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
	JFileChooser chooser;
	
	public FileSelector(final FilenameEditor parent) {
		super(new BorderLayout());
		open = new ImageButton().withIcon(StockIcon.DOCUMENT_OPEN).withTooltip("Browse for Files").withLayout(Layout.IMAGE);
		
		filenameField = new JTextField(10);
		filenameField.setEditable(false);
		
		chooser = new JFileChooser();
		
		open.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				//display dialog
				chooser.showOpenDialog(FileSelector.this);
				
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
