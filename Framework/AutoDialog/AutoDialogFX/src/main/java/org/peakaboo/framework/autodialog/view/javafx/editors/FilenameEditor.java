package org.peakaboo.framework.autodialog.view.javafx.editors;

import java.io.File;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import org.peakaboo.framework.autodialog.model.Parameter;

public class FilenameEditor extends AbstractEditor<File> {

	HBox node = new HBox(3);
	TextField filename;

	public FilenameEditor() {
	}

	@Override
	public void init(Parameter<File> parameter) {

		Button browse = new Button("\u2026");
		File starting = parameter.getValue();
		if (starting == null) { starting = new File("/"); }
		filename = new TextField(starting.getAbsolutePath());

		node.getChildren().add(filename);
		node.getChildren().add(browse);

		HBox.setHgrow(filename, Priority.ALWAYS);

		FileChooser chooser = new FileChooser();
		browse.setOnAction(event -> {

			File file;
			chooser.setInitialDirectory(parameter.getValue().getParentFile());
			file = chooser.showOpenDialog(null);

			if (file != null) {
				getEditorValueHook().updateListeners(file);
				if (!parameter.setValue(getEditorValue())) {
					validateFailed();
				}
			}
		});

	}

	@Override
	public Node getComponent() {
		return node;
	}

	@Override
	public File getEditorValue() {
		return new File(filename.getText());
	}

	@Override
	public void setEditorValue(File value) {
		if (value == null) { value = new File("/"); }
		filename.setText(value.getAbsolutePath());
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
