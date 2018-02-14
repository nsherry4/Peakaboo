package net.sciencestudio.autodialog.view.swing.editors;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import net.sciencestudio.autodialog.model.Parameter;
import net.sciencestudio.autodialog.model.SelectionParameter;


public class ListEditor<T> extends AbstractSwingEditor<T>
{

	private SelectionParameter<T> selparam;
	private JComboBox<T> control;
	

	public ListEditor() {
		control = new JComboBox<>();
	}
	
	
	@Override
	public void initialize(Parameter<T> p)
	{
		this.param = p;
		this.selparam = (SelectionParameter<T>) p;
		
		
		
		for (T t : selparam.getPossibleValues()) control.addItem(t);
		control.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		setFromParameter();
		param.getValueHook().addListener(v -> this.setFromParameter());
		param.getEnabledHook().addListener(e -> setEnabled(e));
		
		
		control.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				getEditorValueHook().updateListeners(getEditorValue());
				if (!param.setValue(getEditorValue())) {
					validateFailed();
				}
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
	public void setEditorValue(T value)
	{
		control.setSelectedItem(value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getEditorValue()
	{
		return (T)control.getSelectedItem();
	}
	

	public void validateFailed() {
		setFromParameter();
	}
	
	
	@Override
	protected void setEnabled(boolean enabled) {
		control.setEnabled(enabled);
	}
	
}
