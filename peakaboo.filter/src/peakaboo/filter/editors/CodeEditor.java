package peakaboo.filter.editors;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import org.jdesktop.swingx.JXEditorPane;

import com.ezware.dialog.task.TaskDialogs;

import autodialog.model.Parameter;
import autodialog.view.editors.IEditor;
import commonenvironment.AbstractFile;
import eventful.Eventful;
import fava.functionable.FStringInput;
import jsyntaxpane.DefaultSyntaxKit;
import swidget.dialogues.fileio.SwidgetIO;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.Spacing;
import swidget.widgets.ToolbarImageButton;

public class CodeEditor extends Eventful implements IEditor<String>
{

	public JXEditorPane codeEditor;
	private Parameter<String> param;
	
	private String language;
	public String errorMessage;
	
	private JPanel panel;
	
	public CodeEditor(String language)
	{
		this.language = language;
	}
	
	@Override
	public void initialize(final Parameter<String> param)
	{
	
		this.param = param;
		panel = new JPanel();
		
		DefaultSyntaxKit.initKit();
		
		codeEditor = new JXEditorPane();
		codeEditor.setMinimumSize(new Dimension(400, 200));
        JScrollPane scrPane = new JScrollPane(codeEditor);
        
        if (language != null) codeEditor.setContentType("text/" + language);
        
        codeEditor.setText((String)param.getValue());
        
        
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setOpaque(false);
        toolbar.setBorder(Spacing.bNone());
        
        ToolbarImageButton open = new ToolbarImageButton(StockIcon.DOCUMENT_OPEN, "Open");
        ToolbarImageButton save = new ToolbarImageButton(StockIcon.DOCUMENT_SAVE_AS, "Save");
        ToolbarImageButton apply = new ToolbarImageButton(StockIcon.CHOOSE_OK, "Apply", "Apply any code changes to the filter", true);
        
        toolbar.add(open);
        toolbar.add(save);
        toolbar.add(Box.createHorizontalGlue());
        toolbar.add(apply);
        
        open.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent event)
			{
				AbstractFile file = SwidgetIO.openFile(
						null, 
						"Open Java Source File", 
						new String[][]{{".java"}}, 
						new String[]{"Java Source Files"}, 
						"~"
					);
				try
				{
					String code = FStringInput.contents(file.getInputStream());
					codeEditor.setText(code);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		});
        
        save.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				ByteArrayOutputStream baos = new ByteArrayOutputStream();

				try
				{
					baos.write(codeEditor.getText().getBytes());
					baos.close();
					SwidgetIO.saveFile(null, "Save Java Source File", "java", "Java Source File", "~", baos);
				}
				catch (IOException e1)
				{
					e1.printStackTrace();
					TaskDialogs.showException(e1);
				}
				
			}
		});
        
        
		apply.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				updateListeners();
			}
		});
		
        
		
		
		

		
		
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridy=0;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(toolbar, c);
		
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridy++;
		c.fill = GridBagConstraints.BOTH;
		panel.add(scrPane, c);

	}
	
	public boolean expandVertical()
	{
		return true;
	}

	@Override
	public boolean expandHorizontal()
	{
		return true;
	}

	@Override
	public LabelStyle getLabelStyle()
	{
		return LabelStyle.LABEL_HIDDEN;
	}

	@Override
	public JComponent getComponent()
	{
		return panel;
	}

	@Override
	public void setFromParameter()
	{
		codeEditor.setText((String)param.getValue());
	}

	@Override
	public String getEditorValue()
	{
		return codeEditor.getText();
	}

	@Override
	public void validateFailed() {
		JOptionPane.showMessageDialog(
				panel, 
				errorMessage, 
				"Code Error", 
				JOptionPane.ERROR_MESSAGE,
				StockIcon.BADGE_WARNING.toImageIcon(IconSize.ICON)
			);
		
		errorMessage = "";
	}
	
}
