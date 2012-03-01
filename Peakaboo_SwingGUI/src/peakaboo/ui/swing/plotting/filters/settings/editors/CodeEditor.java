package peakaboo.ui.swing.plotting.filters.settings.editors;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import jsyntaxpane.DefaultSyntaxKit;

import org.jdesktop.swingx.JXEditorPane;

import com.ezware.dialog.task.TaskDialogs;

import commonenvironment.AbstractFile;
import fava.functionable.FStringInput;

import peakaboo.controller.plotter.filtering.IFilteringController;
import peakaboo.filter.AbstractFilter;
import peakaboo.filter.Parameter;
import peakaboo.ui.swing.plotting.filters.settings.ParamListener;
import peakaboo.ui.swing.plotting.filters.settings.SingleFilterView;

import swidget.dialogues.fileio.SwidgetIO;
import swidget.icons.StockIcon;
import swidget.widgets.Spacing;
import swidget.widgets.ToolbarImageButton;

public class CodeEditor extends JPanel implements Editor
{

	public JXEditorPane codeEditor;
	private Parameter param;
	
	public CodeEditor(final Parameter param, AbstractFilter filter, IFilteringController controller, final SingleFilterView view)
	{
		
		this.param = param;
		
		DefaultSyntaxKit.initKit();
		
		codeEditor = new JXEditorPane();
		codeEditor.setMinimumSize(new Dimension(400, 200));
        JScrollPane scrPane = new JScrollPane(codeEditor);
        
        if (param.getProperty("Language") != null) codeEditor.setContentType("text/" + param.getProperty("Language"));
        
        codeEditor.setText(param.codeValue());
        
        
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
        
        final ParamListener pl = new ParamListener(param, filter, controller, view);
        
		apply.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				pl.update(CodeEditor.this);
			}
		});
		
        
		
		
		

		
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridy=0;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(toolbar, c);
		
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridy++;
		c.fill = GridBagConstraints.BOTH;
		add(scrPane, c);

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
	public Style getStyle()
	{
		return Style.LABEL_HIDDEN;
	}

	@Override
	public JComponent getComponent()
	{
		return this;
	}

	@Override
	public void setFromParameter()
	{
		codeEditor.setText(param.codeValue());
	}

	@Override
	public Object getEditorValue()
	{
		return codeEditor.getText();
	}
	
}
