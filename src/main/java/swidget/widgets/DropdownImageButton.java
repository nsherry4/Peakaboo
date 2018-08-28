package swidget.widgets;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JPopupMenu;

import eventful.EventfulEnum;
import eventful.swing.EventfulEnumPanel;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.ImageButton.ButtonSize;
import swidget.widgets.ImageButton.Layout;



public class DropdownImageButton extends EventfulEnumPanel<DropdownImageButton.Actions>
{
	
	private JButton button, dropdown;
	private JPopupMenu menu;
	
	public enum Actions
	{
		MAIN, MENU
	}
	
	EventfulEnum<Actions> eventful;
	
	
	public DropdownImageButton(String filename, String text, String tooltip, JPopupMenu menu)
	{
		init(filename, text, tooltip, IconSize.TOOLBAR_SMALL, ToolbarImageButton.defaultLayout, menu, true);
	}
	
	public DropdownImageButton(String filename, String text, String tooltip, IconSize size, JPopupMenu menu)
	{
		init(filename, text, tooltip, size, ToolbarImageButton.defaultLayout, menu, true);
	}

	public DropdownImageButton(String filename, String text, String tooltip, IconSize size,  Layout layout, JPopupMenu menu)
	{
		init(filename, text, tooltip, size, layout, menu, true);
	}
	
	public DropdownImageButton(StockIcon stock, String text, String tooltip, JPopupMenu menu)
	{
		init(stock.toIconName(), text, tooltip, IconSize.TOOLBAR_SMALL, ToolbarImageButton.defaultLayout, menu, true);
	}
	
	public DropdownImageButton(StockIcon stock, String text, String tooltip, IconSize size, JPopupMenu menu)
	{
		init(stock.toIconName(), text, tooltip, size, ToolbarImageButton.defaultLayout, menu, true);
	}

	public DropdownImageButton(StockIcon stock, String text, String tooltip, IconSize size,  Layout layout, JPopupMenu menu)
	{
		init(stock.toIconName(), text, tooltip, size, layout, menu, true);
	}
	
	public DropdownImageButton(StockIcon stock, String text, String tooltip, IconSize size,  Layout layout, JPopupMenu menu, boolean showArrow)
	{
		init(stock.toIconName(), text, tooltip, size, layout, menu, showArrow);
	}
	
	private void init(String filename, String text, String tooltip, IconSize size, Layout layout, final JPopupMenu menu, boolean showArrow)
	{
		this.menu = menu;
		eventful = new EventfulEnum<Actions>(); 
		
		this.setFocusable(true);
		this.setOpaque(false);
		setLayout(new BorderLayout());
	
		button = new ImageButton(text)
				.withIcon(filename, size)
				.withTooltip(tooltip)
				.withLayout(layout)
				.withBordered(false);
		button.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e)
			{
				updateListeners(Actions.MAIN);
			}
		});
		button.addMouseListener(new MouseAdapter() {
		
			public void mouseClicked(MouseEvent e)
			{
				if (e.getButton() == MouseEvent.BUTTON3 && menu != null) {
					menu.show(
							DropdownImageButton.this, 
							button.getLocation().x, 
							button.getLocation().y + button.getHeight()
						);
				}
			}
		});
		
		
		add(button, BorderLayout.CENTER);


		if (menu != null && showArrow) {
			String dropdownText = "▾";
			String imageName = "";
			
			dropdown = new ImageButton(dropdownText.toString(), imageName)
					.withTooltip("Show additional options")
					//.withLayout(isTextArrow ? Layout.TEXT : Layout.IMAGE)
					.withLayout(Layout.TEXT)
					.withBordered(false)
					.withBorder(Spacing.bMedium())
					.withButtonSize(ButtonSize.COMPACT);
	
			dropdown.addActionListener(e -> showMenu());
			
			add(dropdown, BorderLayout.EAST);
		}
		
		setBorder(Spacing.bNone());
		
	}
	
	public void showMenu() {
		menu.show(DropdownImageButton.this, button.getLocation().x, button.getLocation().y + button.getHeight());
		updateListeners(Actions.MENU);
	}
	
	
	@Override
	public void setEnabled(boolean enabled)
	{
		if (dropdown != null) {
			dropdown.setEnabled(enabled);
		}
		button.setEnabled(enabled);
	}
	

}
