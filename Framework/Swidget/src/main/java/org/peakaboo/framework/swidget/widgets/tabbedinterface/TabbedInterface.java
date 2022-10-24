package org.peakaboo.framework.swidget.widgets.tabbedinterface;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import org.peakaboo.framework.swidget.icons.IconSize;
import org.peakaboo.framework.swidget.icons.StockIcon;
import org.peakaboo.framework.swidget.widgets.ClearPanel;


public abstract class TabbedInterface<T extends Component> extends JTabbedPane
{

	private JFrame window;
	
	private Function<T, String> titleFunction;
	private boolean working = false;
	protected int tabWidth = 150;
	
	private T lastSelectedTab = null;
	
	public TabbedInterface(JFrame window, Function<T, String> titleFunction) {
		this(window, titleFunction, 150);
	}
	
	public TabbedInterface(JFrame window, Function<T, String> titleFunction, int tabWidth) {
		this.titleFunction = titleFunction;
		this.tabWidth = tabWidth;
		this.window = window;
	}
	
	public void init() {
		makeNewTabButton();
		newTab();
		
		
		this.addChangeListener(e -> {
			int i = this.getSelectedIndex();
			if (i < 0) return;
			
			TabbedInterfaceTitle titleComponent = (TabbedInterfaceTitle) this.getTabComponentAt(i);
			if (titleComponent == null) return;
			
			titleChanged(titleComponent.getTitle());
		});
		

		this.addChangeListener(e -> {
			if (!working) {
				working = true;
				//is the new-tab tab the only tab?
				if (this.getTabCount() == 1)
				{
					newTab();
				}
				
				//does the new-tab tab the focused tab?
				if (this.getSelectedIndex() == this.getTabCount() -1)
				{
					newTab();
				}
				
				
				lastSelectedTab = getActiveTab();
				titleChanged(getActiveTabTitle().getTitle());
				working = false;
			}
			
		});
		
	}
	

	private void makeNewTabButton() {
		Icon newmapButton = StockIcon.WINDOW_TAB_NEW.toSymbolicIcon(IconSize.BUTTON);
		this.addTab("", newmapButton, new ClearPanel());	
	}
	
	
	protected JTabbedPane getJTabbedPane()
	{
		return this;
	}
	
	public void addTab(T component)
	{
		int count = this.getTabCount() - 1;
		
		TabbedInterfaceTitle titleComponent = makeTitleComponent(component);
		this.insertTab("", null, component, "", count);
		this.setTabComponentAt(count, titleComponent);
		setTabTitle(component, titleFunction.apply(component));
	}
	
	public TabbedInterfaceTitle makeTitleComponent(T component) {
		TabbedInterfaceTitle title = new TabbedInterfaceTitle(this, tabWidth);
		title.setOnDoubleClick(() -> titleDoubleClicked(component));
		return title;
	}
	
	public void addActiveTab(T component) {
		addTab(component);
		setActiveTab(component);
	}
	
	public T newTab()
	{
		T component = createComponent();
		addActiveTab(component);
		return component;
	}
	
	@SuppressWarnings("unchecked")
	protected void closeTab(int i)
	{
		//If this is the last tab before the new-tab tab and it's focused, try to focus another tab first
		if (i == this.getTabCount() - 2 && this.getSelectedIndex() == i && i != 0) {
			setActiveTab(i-1);
		}
		destroyComponent((T)this.getComponentAt(i));
		this.remove(i);
		
		if (this.getTabCount() == 0) newTab();
		
	}
	
	public void closeTab(T component)
	{
		destroyComponent(component);
		this.remove(component);
	}
	
	@SuppressWarnings("unchecked")
	public T getActiveTab()
	{
		//if the new-tab tab is focused now, return the last tab to have focus
		if (this.getSelectedIndex() == this.getTabCount()-1) {
			return lastSelectedTab;
		} else {
			return (T) this.getSelectedComponent();
		}
	}
	
	private TabbedInterfaceTitle getActiveTabTitle() {
		return (TabbedInterfaceTitle) this.getTabComponentAt(this.getSelectedIndex());
	}
		
	public void setActiveTab(T component)
	{
		this.setSelectedComponent(component);
		component.requestFocus();
		lastSelectedTab = component;
		titleChanged(getActiveTabTitle().getTitle());
	}
	
	public void setActiveTab(int i)
	{
		this.setSelectedIndex(i);
		titleChanged(getActiveTabTitle().getTitle());
	}
	
	
	public void setTabTitle(T component, String title)
	{
		int i = this.indexOfComponent(component);
		if (i < 0) return;
		TabbedInterfaceTitle titleComponent = (TabbedInterfaceTitle) this.getTabComponentAt(i);
		titleComponent.setTitle(title);
		this.setToolTipTextAt(i, title);
		if (i == this.getSelectedIndex()) {
			titleChanged(title);
		}
	}

	public List<T> getTabs() {
		List<T> tabs = new ArrayList<>();
		for (int i = 0; i < this.getTabCount()-1; i++) {
			tabs.add((T) this.getComponentAt(i));
		}
		return tabs;
	}
	
	public JFrame getWindow() {
		return window;
	}
		
	protected abstract T createComponent();
	protected abstract void destroyComponent(T component);
	protected abstract void titleChanged(String title);	
	protected abstract void titleDoubleClicked(T component);
	
}
