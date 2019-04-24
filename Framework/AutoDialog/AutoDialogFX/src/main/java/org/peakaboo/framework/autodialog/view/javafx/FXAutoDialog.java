package org.peakaboo.framework.autodialog.view.javafx;

import org.peakaboo.framework.autodialog.view.javafx.layouts.FXLayoutFactory;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.peakaboo.framework.autodialog.model.Group;


public class FXAutoDialog {

	private BorderPane node;
	private Group group;
	
		
	public FXAutoDialog(Group group) {
		this.group = group;
		node = new BorderPane();
		node.setPadding(new Insets(6));

		node.setCenter(FXLayoutFactory.forGroup(group).getComponent());
		
	}
	

	
	public void initialize() {
		
		Stage dialog = new Stage();
		//dialog.initStyle(StageStyle.UNIFIED);
		dialog.setResizable(false);
		
		Scene scene = new Scene(node);
		dialog.setScene(scene);
		dialog.setTitle(group.getName());
		dialog.sizeToScene();
		dialog.show();
	}
	

	
}

