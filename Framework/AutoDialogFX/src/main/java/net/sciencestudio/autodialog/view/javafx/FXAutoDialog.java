package net.sciencestudio.autodialog.view.javafx;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import net.sciencestudio.autodialog.model.Group;
import net.sciencestudio.autodialog.view.javafx.layouts.FXLayoutFactory;


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

