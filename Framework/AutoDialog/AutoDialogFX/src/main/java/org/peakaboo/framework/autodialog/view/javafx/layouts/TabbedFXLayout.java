package org.peakaboo.framework.autodialog.view.javafx.layouts;

import java.util.ArrayList;
import java.util.List;

import org.peakaboo.framework.autodialog.view.javafx.FXView;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import org.peakaboo.framework.autodialog.model.Group;
import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.framework.autodialog.model.Value;

public class TabbedFXLayout extends AbstractFXLayout {

	TabPane node;
	
	public TabbedFXLayout() {
		node = new TabPane();
	}
	
	@Override
	public void layout() {
		node.getTabs().clear();
		
		List<Value<?>> generalList = new ArrayList<>();		
		for (Value<?> value : group.getValue()) {
			if (value instanceof Parameter) {
				generalList.add(value);
			}
		}
		Group general = new Group("General", generalList);
		
		if (general.getValue().size() > 0) {
			node.getTabs().add(node(general));
		}
		
		
		for (Value<?> value : group.getValue()) {
			if (value instanceof Group) {
				node.getTabs().add(node((Group)value));
			}
		}
		
	}

	
	protected Tab node(Group group) {
		Tab tab = new Tab();
		FXView view = FXLayoutFactory.forGroup(group);
		tab.setText(view.getTitle());
		tab.setClosable(false);
		
		BorderPane content = new BorderPane();
		content.setCenter(view.getComponent());
		content.setPadding(new Insets(6));
		tab.setContent(content);
		
		return tab;
	}

	@Override
	public Node getComponent() {
		return node;
	}

	
}
