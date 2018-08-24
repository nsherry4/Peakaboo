package swidget.widgets.widgetlist;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.ClearPanel;
import swidget.widgets.ImageButton;
import swidget.widgets.ImageButton.Layout;

public class WidgetListPanel<T extends Component> extends JPanel {

	private List<T> components = new ArrayList<>();
	private Map<T, ImageButton> editButtons = new HashMap<>();
	private Map<T, ImageButton> removeButtons = new HashMap<>();
	
	
	private IComponentListController<T> controller;
	private ImageButton addButton;
	private JPanel bottomSpacer = new ClearPanel();
	private GridBagConstraints c;
	
	private T activeComponent;
	
	public WidgetListPanel(IComponentListController<T> controller) {
		this.controller = controller;
		setupUI();
	}
	
	private void setupUI() {
	
		c = new GridBagConstraints();
		setLayout(new GridBagLayout());
		
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		c.weighty = 0.0;

		addButton = createAddButton();
		
		for (T component : components) {
			
			addComponent(component);

		}

		addAddButton();
		addBottomSpacer();
		
		revalidate();
	}
	
	

	private ImageButton createRemoveButton(final T component)
	{
		ImageButton remove = new ImageButton().withIcon(StockIcon.EDIT_REMOVE, IconSize.BUTTON).withTooltip("Remove").withLayout(Layout.IMAGE);

		remove.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				removeComponent(component);
			}
		});

		return remove;
	}
	
	

	private ImageButton createEditButton(final T component)
	{

		final ImageButton edit = new ImageButton(StockIcon.EDIT_EDIT, IconSize.BUTTON).withTooltip("Edit this " + controller.getComponentName()).withLayout(Layout.IMAGE);

		edit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				enableComponent(component);
			}
		});

		return edit;

	}
	
	private ImageButton createAddButton()
	{
		ImageButton add = new ImageButton().withIcon(StockIcon.EDIT_ADD, IconSize.BUTTON).withTooltip("Add").withLayout(Layout.IMAGE);
		
		add.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				addNewComponent();
			}
		});
		
		return add;
	}
	
	
	private void addAddButton()
	{
		c.gridy++;
		c.gridx = 2;
		add(addButton, c);
	}

	private void addBottomSpacer()
	{
		c.gridy++;
		c.gridx = 0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		add(bottomSpacer, c);
	}
	
	private void addComponent(T component)
	{
		
		components.add(component);
		
		c.gridy += 1;
		c.weighty = 0.0;
		
		c.gridx = 0;
		c.weightx = 1.0;
		component.setEnabled(isActive(component));
		add(component, c);

		c.gridx = 1;
		c.weightx = 0.0;
		ImageButton edit = createEditButton(component);
		editButtons.put(component, edit);
		edit.setEnabled(!isActive(component));
		add(edit, c);
		
		c.gridx = 2;
		c.weightx = 0.0;
		ImageButton remove = createRemoveButton(component);
		removeButtons.put(component, remove);
		add(remove, c);
		
	}
	
	private void addNewComponent()
	{	
		remove(bottomSpacer);
		c.gridy--;
		
		remove(addButton);
		c.gridy--;
		
		T component = controller.generateComponent();
		addComponent(component);

		addAddButton();
		addBottomSpacer();
		
		enableComponent(component);
		revalidate();
		
		controller.add(component);
		
		
	}
	
	private void removeComponent(T component)
	{
		components.remove(component);
		
		remove(editButtons.get(component));
		editButtons.remove(component);
				
		remove(removeButtons.get(component));
		removeButtons.remove(component);

		remove(component);
		if (isActive(component) && components.size() > 0) enableComponent(components.get(components.size() - 1)); 
		revalidate();
		
		controller.remove(component);
		
	}
	
	private boolean isActive(T component)
	{
		return activeComponent == component;
	}
	
	private void enableComponent(T component)
	{
		disableAllComponents();
		
		activeComponent = component;
		component.setEnabled(true);
		component.requestFocusInWindow();
		editButtons.get(component).setEnabled(false);
		controller.edit(component);
	}
	
	private void disableComponent(T component)
	{
		component.setEnabled(false);
		editButtons.get(component).setEnabled(true);
	}
	
	private void disableAllComponents()
	{
		for (T component : components) {
			disableComponent(component);
		}
	}
	
}
