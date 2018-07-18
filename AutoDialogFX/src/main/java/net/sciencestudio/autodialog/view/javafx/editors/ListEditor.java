package net.sciencestudio.autodialog.view.javafx.editors;


import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import net.sciencestudio.autodialog.model.Parameter;
import net.sciencestudio.autodialog.model.SelectionParameter;

public class ListEditor<T> extends AbstractEditor<T>{

	private ChoiceBox<T> node = new ChoiceBox<>();
	
	public ListEditor() {}
	
	@Override
	public void init(Parameter<T> parameter) {
		this.parameter = (SelectionParameter<T>) parameter;
		SelectionParameter<T> selectionParameter = (SelectionParameter<T>) parameter;
		node.getItems().addAll(selectionParameter.getPossibleValues());
		
		node.getSelectionModel().selectedItemProperty().addListener(change -> {
			getEditorValueHook().updateListeners(getEditorValue());
			if (!parameter.setValue(getEditorValue())) {
				validateFailed();
			}
		});
	}
	

	@Override
	public Node getComponent() {
		return node;
	}

	@Override
	public void setEditorValue(T value) {
		node.getSelectionModel().select(value);
	}

	@Override
	public T getEditorValue() {
		return node.getSelectionModel().getSelectedItem();
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

	public void validateFailed() {
		setFromParameter();
	}

}
