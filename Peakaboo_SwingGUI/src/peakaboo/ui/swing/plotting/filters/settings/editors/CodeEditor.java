package peakaboo.ui.swing.plotting.filters.settings.editors;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import jsyntaxpane.DefaultSyntaxKit;

import org.jdesktop.swingx.JXEditorPane;

import peakaboo.controller.plotter.filtering.IFilteringController;
import peakaboo.filter.AbstractFilter;
import peakaboo.filter.Parameter;
import peakaboo.ui.swing.plotting.filters.settings.ParamListener;
import peakaboo.ui.swing.plotting.filters.settings.SingleFilterView;

import swidget.icons.StockIcon;
import swidget.widgets.ImageButton;

public class CodeEditor extends JPanel implements Editor
{

	public JXEditorPane codeEditor;
	private Parameter param;
	
	public CodeEditor(Parameter param, AbstractFilter filter, IFilteringController controller, SingleFilterView view)
	{
		
		this.param = param;
		
		DefaultSyntaxKit.initKit();
		
		codeEditor = new JXEditorPane();
        JScrollPane scrPane = new JScrollPane(codeEditor);
        
        if (param.getProperty("Language") != null) codeEditor.setContentType("text/" + param.getProperty("Language"));
        
        codeEditor.setText(param.codeValue());
        
        
		final ParamListener pl = new ParamListener(param, filter, controller, view);
		
		ImageButton okbutton = new ImageButton(StockIcon.CHOOSE_OK, "Apply");
		okbutton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				pl.update(CodeEditor.this);
			}
		});
		
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		add(scrPane, c);
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridy = 1;
		c.fill = GridBagConstraints.NONE;
		add(okbutton, c);
	}
	
	public float getVerticalWeight()
	{
		try
		{
			return Float.parseFloat(param.getProperty("EditorVWeight"));
		}
		catch (NumberFormatException e)
		{
			return 1f;
		}
	}

	@Override
	public boolean expandHorizontal()
	{
		return true;
	}

	@Override
	public Style getStyle()
	{
		return Style.LABEL_ON_TOP;
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
