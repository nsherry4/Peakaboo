package swidget.widgets;

import javax.swing.ImageIcon;
import javax.swing.border.Border;

import swidget.icons.IconFactory;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;

public class ToolbarImageButton extends ImageButton {

	public static final Layout significantLayout = Layout.IMAGE_ON_SIDE;
	
	boolean isSignificant;
	
	public ToolbarImageButton() {
		config.size = IconSize.TOOLBAR_SMALL;
		config.bordered = false;
	}
	
	

	public ToolbarImageButton(StockIcon icon) {
		this();
		config.imagename = icon.toIconName();

		makeButton();
	}

	public ToolbarImageButton(StockIcon icon, IconSize size) {
		this();
		config.imagename = icon.toIconName();
		config.size = size;

		makeButton();
	}

	
	public ToolbarImageButton(String text, StockIcon icon) {
		this();
		config.text = text;
		config.imagename = icon.toIconName();

		makeButton();
	}
	
	public ToolbarImageButton(String text, String icon) {
		this();
		config.text = text;
		config.imagename = icon;

		makeButton();
	}
	
	

	public ToolbarImageButton withBordered(boolean bordered) {
		config.bordered = bordered;
		makeButton();
		return this;
	}
	
	public ToolbarImageButton withIcon(StockIcon stock) {
		return withIcon(stock, IconSize.TOOLBAR_SMALL);
	}
	
	public ToolbarImageButton withIcon(StockIcon stock, IconSize size) {
		return withIcon(stock.toIconName(), size);
	}
	
	public ToolbarImageButton withIcon(String filename) {
		return withIcon(filename, IconSize.TOOLBAR_SMALL);
	}
	
	public ToolbarImageButton withIcon(String filename, IconSize size) {
		super.withIcon(filename, size);
		return this;
	}
	
	public ToolbarImageButton withBorder(Border border) {
		super.withBorder(border);
		return this;
	}
	
	public ToolbarImageButton withText(String text) {
		super.withText(text);
		return this;
	}
	
	public ToolbarImageButton withTooltip(String tooltip) {
		super.withTooltip(tooltip);
		return this;
	}
	
	public ToolbarImageButton withLayout(Layout layout) {
		super.withLayout(layout);
		return this;
	}

	public ToolbarImageButton withAction(Runnable action) {
		super.withAction(action);
		return this;
	}
	
	
	
	public ToolbarImageButton withSignificance(boolean significant) {
		this.isSignificant = significant;
		
		makeButton();
		return this;
	}
	
	protected Layout guessLayout() {
		Layout mode = this.isSignificant ? Layout.IMAGE_ON_SIDE : Layout.IMAGE;
		ImageIcon image = IconFactory.getImageIcon(config.imagename, config.size);
		if (config.imagename == null || image.getIconHeight() == -1) {
			mode = Layout.TEXT;
		} else if (config.text == null || "".equals(config.text)) {
			mode = Layout.IMAGE;
		}
		return mode;
	}
	
	
}
