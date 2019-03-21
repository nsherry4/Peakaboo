package net.sciencestudio.autodialog.view.swing.editors;


import java.util.function.Consumer;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.sciencestudio.autodialog.model.Parameter;
import net.sciencestudio.autodialog.model.style.Style;
import net.sciencestudio.autodialog.model.style.editors.TextBoxStyle;

public class TextBoxEditor extends AbstractSwingEditor<String> {


	private JTextField control;
	
	public TextBoxEditor() {
		control = new JTextField(15);
	}
	
	@Override
	public void initialize(Parameter<String> param)
	{	
		this.param = param;

		setFromParameter();
		param.getValueHook().addListener(v -> this.setFromParameter());
		param.getEnabledHook().addListener(e -> setEnabled(e));
		
		//control.setPreferredSize(new Dimension(70, control.getPreferredSize().height));
		control.setText(param.getValue());

		
		
		Consumer<DocumentEvent> changeListener = e -> {
			getEditorValueHook().updateListeners(getEditorValue());
			if (!param.setValue(getEditorValue())) {
				validateFailed();
			}
		};
		
		control.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				changeListener.accept(e);
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				changeListener.accept(e);
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				changeListener.accept(e);
			}
		});

	}
	
	@Override
	public boolean expandVertical()
	{
		return false;
	}

	@Override
	public boolean expandHorizontal()
	{
		return false;
	}

	@Override
	public LabelStyle getLabelStyle()
	{
		return LabelStyle.LABEL_ON_SIDE;
	}

	@Override
	public JComponent getComponent()
	{
		return control;
	}

	@Override
	public void setEditorValue(String value)
	{
		control.setText(value);
	}

	@Override
	public String getEditorValue()
	{
		return (String)control.getText();
	}
	

	public void validateFailed() {
		SwingUtilities.invokeLater(this::setFromParameter);
	}
	
	@Override
	protected void setEnabled(boolean enabled) {
		control.setEnabled(enabled);
	}
	

}
