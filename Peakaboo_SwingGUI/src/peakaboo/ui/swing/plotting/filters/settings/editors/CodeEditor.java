package peakaboo.ui.swing.plotting.filters.settings.editors;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

public class CodeEditor extends JPanel
{

	public JXEditorPane codeEditor;
	
	public CodeEditor(Parameter param, AbstractFilter filter, IFilteringController controller, SingleFilterView view)
	{
		DefaultSyntaxKit.initKit();
		
		codeEditor = new JXEditorPane();
        JScrollPane scrPane = new JScrollPane(codeEditor);
        
        if (param.getProperty("Language") != null) codeEditor.setContentType("text/" + param.getProperty("Language"));
        
        codeEditor.setText(param.codeValue());
        
        int height = 400;
        if (param.getProperty("CodeHeight") != null) height = Integer.parseInt(param.getProperty("CodeHeight"));
        
        scrPane.setPreferredSize(new Dimension(600, height));
        	        
        
		final ParamListener pl = new ParamListener(param, filter, controller, view);
		
		ImageButton okbutton = new ImageButton(StockIcon.CHOOSE_OK, "Apply");
		okbutton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				pl.update(CodeEditor.this);
			}
		});
		
		
		setLayout(new BorderLayout());
		add(scrPane, BorderLayout.CENTER);
		add(okbutton, BorderLayout.SOUTH);
	}
	
}
