package swidget.widgets.widgetlist;


public interface IComponentListController<T> {

	T generateComponent();
	String getComponentName();
	
	void add(T component);
	void remove(T component);
	void edit(T component);
	
	
	
}
