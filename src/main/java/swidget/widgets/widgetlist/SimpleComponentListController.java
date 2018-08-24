package swidget.widgets.widgetlist;

import java.awt.Component;

public abstract class SimpleComponentListController<T extends Component> implements IComponentListController<T> {

	private String name;
	
	public SimpleComponentListController(String name) {
		this.name = name;
	}
	
	@Override
	public String getComponentName() {
		return name;
	}

	@Override
	public void add(Component component) {}

	@Override
	public void remove(Component component) {}

	@Override
	public void edit(Component component) {}

}
