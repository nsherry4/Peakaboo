package org.peakaboo.framework.autodialog.view.javafx.layouts;

import org.peakaboo.framework.autodialog.model.Group;
import org.peakaboo.framework.autodialog.view.editors.Editor.LabelStyle;


public abstract class AbstractFXLayout implements FXLayout {

	protected Group group;

	public void initialize(Group group) {
		this.group = group;
		layout();
	}
	

	@Override
	public String getTitle() {
		return group.getName();
	}
	
	@Override
	public Group getValue() {
		return group;
	}

	
	@Override
	public LabelStyle getLabelStyle() {
		return LabelStyle.LABEL_HIDDEN;
	}
	
	
	@Override
	public boolean expandVertical() {
		return false;
	}

	@Override
	public boolean expandHorizontal() {
		return true;
	}
	
	
//	@Override
//	public void addEditors(List<Editor<?>> editors) {
//		this.children = editors;
//		layout();
//	}
//	
//	protected List<Editor<?>> getChildren() {
//		return children;
//	}
//	
//	protected List<Node> getChildNodes() {
//		List<Node> nodes = getChildren().stream().map(child -> ((FXEditor<?>)child).getComponent()).collect(Collectors.toList());
//		return nodes;
//	}

	public abstract void layout();

}
