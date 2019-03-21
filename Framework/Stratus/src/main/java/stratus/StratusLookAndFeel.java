package stratus;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.ToolTipManager;
import javax.swing.UIDefaults;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import stratus.Stratus.ButtonState;
import stratus.components.StratusComboBoxUI;
import stratus.painters.BorderPainter;
import stratus.painters.ButtonPainter;
import stratus.painters.ComboBoxArrowPainter;
import stratus.painters.CompositePainter;
import stratus.painters.FillPainter;
import stratus.painters.RadioButtonPainter;
import stratus.painters.SplitPaneDividerPainter;
import stratus.painters.TableHeaderPainter;
import stratus.painters.TitledBorderBorder;
import stratus.painters.ToolTipPainter;
import stratus.painters.TreeArrowPainter;
import stratus.painters.checkbutton.CheckButtonPainter;
import stratus.painters.checkbutton.CheckPainter;
import stratus.painters.linkedbutton.LinkedButtonPainter;
import stratus.painters.progressbar.ProgressBarBackgroundPainter;
import stratus.painters.progressbar.ProgressBarForegroundPainter;
import stratus.painters.progressbar.ProgressBarForegroundPainter.Mode;
import stratus.painters.scrollbar.ScrollBarThumbPainter;
import stratus.painters.scrollbar.ScrollBarTrackPainter;
import stratus.painters.slider.SliderThumbPainter;
import stratus.painters.slider.SliderTrackPainter;
import stratus.painters.spinner.NextButtonPainter;
import stratus.painters.spinner.PreviousButtonPainter;
import stratus.painters.spinner.SpinnerArrowPainter;
import stratus.painters.tabs.TabPainter;
import stratus.painters.tabs.TabbedAreaPainter;
import stratus.painters.textfield.TextFieldBackgroundPainter;
import stratus.painters.textfield.TextFieldBorderPainter;
import stratus.painters.toolbar.ToolbarBackgroundPainter;
import stratus.painters.toolbar.ToolbarBorderPainter;
import stratus.painters.toolbar.ToolbarBorderPainter.Side;
import stratus.theme.LightTheme;
import stratus.theme.Theme;

public class StratusLookAndFeel extends NimbusLookAndFeel {

	public static boolean DISABLE_FONT_HINTING = true;
	public static boolean REPLACE_PAINTERS = true;
	public static boolean REPLACE_ICONS = true;
	public static boolean HEAVYWEIGHT_POPUPS = true;
	
	private Theme theme;
	
	
	public StratusLookAndFeel() {
		this(new LightTheme());
	}
	
	public StratusLookAndFeel(Theme theme) {
		this.theme = theme;
		
		System.setProperty("swing.aatext", "true");
		
		if (DISABLE_FONT_HINTING) {
			//Force the system to use the font's built-in hinting.
			//We later load a modified font with NO hinting information.
			//This seems to need to be set very early (before AWT starts 
			//up?) on oracle jvms.
			System.setProperty("awt.useSystemAAFontSettings", "gasp");
		}
		
		if (HEAVYWEIGHT_POPUPS) {
			//Force all menus to be heavyweight components to get that nice OS-composited drop shadow.
			JPopupMenu.setDefaultLightWeightPopupEnabled(false);
			ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
		}
		

	}
	
	@Override
	public UIDefaults getDefaults() {
		UIDefaults ret = super.getDefaults();

		

		// UI/Theme Overrides
		reg(ret, "nimbusSelection", theme.getHighlight());
		reg(ret, "nimbusSelectionBackground", theme.getHighlight());
		reg(ret, "nimbusFocus", theme.getHighlight());
		reg(ret, "nimbusBlueGrey", theme.getWidgetBorder());
		reg(ret, "nimbusInfoBlue", theme.getHighlight());
		reg(ret, "nimbusBase", theme.getHighlight());
		reg(ret, "nimbusOrange", theme.getHighlight());
		reg(ret, "nimbusLightBackground", theme.getControl());
		
		reg(ret, "textInactiveText", theme.getControlTextDisabled());
		reg(ret, "textHighlight", theme.getHighlight());
		reg(ret, "textBackground", theme.getHighlight());

		reg(ret, "text", theme.getControlText());
		reg(ret, "menu", theme.getMenu());
		reg(ret, "control", theme.getControl());
		
		
		//Stratus specific values
		reg(ret, "stratus-highlight", theme.getHighlight());
		reg(ret, "stratus-highlight-text", theme.getHighlightText());
		
		reg(ret, "stratus-control", theme.getControl());
		reg(ret, "stratus-control-text", theme.getControlText());
		reg(ret, "stratus-control-text-disabled", theme.getControlTextDisabled());
		
		reg(ret, "stratus-menu", theme.getMenu());
		reg(ret, "stratus-menu-text", theme.getMenuText());
		
		reg(ret, "stratus-widget", theme.getWidget());
		reg(ret, "stratus-widget-bevel", theme.getWidgetBevel());
		reg(ret, "stratus-widget-border", theme.getWidgetBorder());
		
		reg(ret, "stratus-recessed-control", theme.getRecessedControl());
		reg(ret, "stratus-recessed-text", theme.getRecessedText());
		
		reg(ret, "stratus-table-header", theme.getTableHeader());
		reg(ret, "stratus-table-header-text", theme.getTableHeaderText());
		
		reg(ret, "stratus-scroll-handle", theme.getScrollHandle());
		
		
		
		
		
//		//Fonts
		if (DISABLE_FONT_HINTING) {
			
			try {	
				GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
				Font f;
				f = Font.createFont(Font.TRUETYPE_FONT, StratusLookAndFeel.class.getResourceAsStream("/stratus/fonts/dejavu/DejaVuSansUnhinted.ttf"));
				ge.registerFont(f);
				
				f = Font.createFont(Font.TRUETYPE_FONT, StratusLookAndFeel.class.getResourceAsStream("/stratus/fonts/dejavu/DejaVuSansBoldUnhinted.ttf"));
				ge.registerFont(f);
				
				f = Font.createFont(Font.TRUETYPE_FONT, StratusLookAndFeel.class.getResourceAsStream("/stratus/fonts/dejavu/DejaVuSansObliqueUnhinted.ttf"));
				ge.registerFont(f);
				
				f = Font.createFont(Font.TRUETYPE_FONT, StratusLookAndFeel.class.getResourceAsStream("/stratus/fonts/dejavu/DejaVuSansBoldObliqueUnhinted.ttf"));
				ge.registerFont(f);
			} catch (FontFormatException | IOException e) {
				Logger.getLogger("Stratus").log(Level.WARNING, "Failed to configure font", e);
			}
			
			for (Object key : ret.keySet()) {
				Object value = ret.get(key);
				if (value instanceof javax.swing.plaf.FontUIResource || value instanceof Font) {
					
					Font oldFont = (Font) ret.get(key);
					int fontSize = oldFont.getSize();
					Font newFont = new Font("DejaVu Sans Unhinted", oldFont.getStyle(), fontSize);
					reg(ret, key, newFont);

				}
			}
		}
		
		if (REPLACE_PAINTERS) {
		
			//MENUS
			reg(ret, "MenuBar[Enabled].backgroundPainter", new FillPainter(theme.getControl()));
			reg(ret, "MenuBar[Enabled].borderPainter", new FillPainter(theme.getControl()));
			reg(ret, "MenuBar:Menu[Selected].backgroundPainter", new FillPainter(theme.getHighlight()));
			reg(ret, "MenuBar.contentMargins", new Insets(0, 0, 0, 0));
			reg(ret, "MenuBar:Menu.contentMargins", new Insets(4, 8, 5, 8));
			reg(ret, "MenuBar:Menu[Enabled].textForeground", theme.getMenuText());
			reg(ret, "MenuBar:Menu[MouseOver].textForeground", theme.getHighlightText());
			reg(ret, "MenuBar:Menu[Disabled].textForeground", theme.getControlTextDisabled());
			
			
			reg(ret, "MenuItem[Enabled].textForeground", theme.getMenuText());
			reg(ret, "MenuItem[Disabled].textForeground", theme.getControlTextDisabled());
			reg(ret, "MenuItem[MouseOver].textForeground", theme.getHighlightText());
			reg(ret, "MenuItem.contentMargins", new Insets(4, 12, 4, 13));
			reg(ret, "MenuItem:MenuItemAccelerator[Enabled].textForeground", theme.getControlTextDisabled());
			reg(ret, "MenuItem:MenuItemAccelerator[Disabled].textForeground", theme.getControlTextDisabled());
			reg(ret, "MenuItem:MenuItemAccelerator[MouseOver].textForeground", theme.getHighlightText());
			

			reg(ret, "CheckBoxMenuItem.contentMargins", new Insets(4, 12, 4, 13));
			reg(ret, "CheckBoxMenuItem[Enabled].textForeground", theme.getMenuText());
			reg(ret, "CheckBoxMenuItem[MouseOver].textForeground", theme.getHighlightText());
			reg(ret, "CheckBoxMenuItem[Disabled].textForeground", theme.getControlTextDisabled());
			
			reg(ret, "RadioButtonMenuItem.contentMargins", new Insets(4, 12, 4, 13));
			reg(ret, "RadioButtonMenuItem[Enabled].textForeground", theme.getMenuText());
			reg(ret, "RadioButtonMenuItem[MouseOver].textForeground", theme.getHighlightText());
			reg(ret, "RadioButtonMenuItem[Disabled].textForeground", theme.getControlTextDisabled());
			
			reg(ret, "Menu.contentMargins", new Insets(4, 12, 4, 5));
			reg(ret, "Menu[Enabled].textForeground", theme.getMenuText());
			reg(ret, "Menu[MouseOver].textForeground", theme.getHighlightText());
			reg(ret, "Menu[Disabled].textForeground", theme.getControlTextDisabled());
			
			reg(ret, "PopupMenu[Enabled].backgroundPainter", new CompositePainter(new FillPainter(theme.getMenu()), new BorderPainter(theme.getWidgetBorder(), 1, 0)));
			
			
			//TOOLBAR
			reg(ret, "ToolBar.backgroundPainter", new ToolbarBackgroundPainter(theme));
			reg(ret, "ToolBar[North].borderPainter", new ToolbarBorderPainter(theme, Side.NORTH));
			reg(ret, "ToolBar[South].borderPainter", new ToolbarBorderPainter(theme, Side.SOUTH));
			reg(ret, "ToolBar[East].borderPainter", new ToolbarBorderPainter(theme, Side.EAST));
			reg(ret, "ToolBar[West].borderPainter", new ToolbarBorderPainter(theme, Side.WEST));
			
			
			
			
			//BUTTON
			reg(ret, "Button.foreground", 						theme.getControlText());
			reg(ret, "Button.textForeground",					theme.getControlText());
			reg(ret, "Button.disabledText", 					theme.getControlTextDisabled());
			reg(ret, "Button[Disabled].textForeground", 		theme.getControlTextDisabled());
			reg(ret, "Button[Default+Pressed].textForeground", 	theme.getControlText());
	
			reg(ret, "Button[Default+Focused+MouseOver].backgroundPainter", 	new LinkedButtonPainter(theme, ButtonState.DEFAULT, ButtonState.FOCUSED, ButtonState.MOUSEOVER));
			reg(ret, "Button[Default+Focused+Pressed].backgroundPainter", 	new LinkedButtonPainter(theme, ButtonState.DEFAULT, ButtonState.FOCUSED, ButtonState.PRESSED));
			reg(ret, "Button[Default+Focused].backgroundPainter", 			new LinkedButtonPainter(theme, ButtonState.DEFAULT, ButtonState.FOCUSED));
			reg(ret, "Button[Default+MouseOver].backgroundPainter", 			new LinkedButtonPainter(theme, ButtonState.DEFAULT, ButtonState.MOUSEOVER));
			reg(ret, "Button[Default+Pressed].backgroundPainter", 			new LinkedButtonPainter(theme, ButtonState.DEFAULT, ButtonState.PRESSED));
			reg(ret, "Button[Default].backgroundPainter", 					new LinkedButtonPainter(theme, ButtonState.DEFAULT));
			reg(ret, "Button[Disabled].backgroundPainter", 					new LinkedButtonPainter(theme, ButtonState.DISABLED));
			reg(ret, "Button[Enabled].backgroundPainter", 					new LinkedButtonPainter(theme, ButtonState.ENABLED));
			reg(ret, "Button[Focused+MouseOver].backgroundPainter", 			new LinkedButtonPainter(theme, ButtonState.FOCUSED, ButtonState.MOUSEOVER));
			reg(ret, "Button[Focused+Pressed].backgroundPainter", 			new LinkedButtonPainter(theme, ButtonState.FOCUSED, ButtonState.PRESSED));
			reg(ret, "Button[Focused].backgroundPainter", 					new LinkedButtonPainter(theme, ButtonState.FOCUSED));
			reg(ret, "Button[MouseOver].backgroundPainter", 					new LinkedButtonPainter(theme, ButtonState.MOUSEOVER));
			reg(ret, "Button[Pressed].backgroundPainter", 					new LinkedButtonPainter(theme, ButtonState.PRESSED));
					
	
			//BUTTON on TOOLBAR
			reg(ret, "ToolBar:Button.foreground", 						theme.getControlText());
			reg(ret, "ToolBar:Button.textForeground",					theme.getControlText());
			reg(ret, "ToolBar:Button.disabledText", 					theme.getControlTextDisabled());
			reg(ret, "ToolBar:Button[Disabled].textForeground", 		theme.getControlTextDisabled());
			reg(ret, "ToolBar:Button[Default+Pressed].textForeground", 	theme.getControlText());
			
			reg(ret, "ToolBar:Button[Focused+MouseOver].backgroundPainter", 	new LinkedButtonPainter(theme, ButtonState.FOCUSED, ButtonState.MOUSEOVER));
			reg(ret, "ToolBar:Button[Focused+Pressed].backgroundPainter", 	new LinkedButtonPainter(theme, ButtonState.FOCUSED, ButtonState.PRESSED));
			reg(ret, "ToolBar:Button[Focused].backgroundPainter", 			new LinkedButtonPainter(theme, ButtonState.FOCUSED));
			reg(ret, "ToolBar:Button[MouseOver].backgroundPainter", 			new LinkedButtonPainter(theme, ButtonState.MOUSEOVER));
			reg(ret, "ToolBar:Button[Pressed].backgroundPainter", 			new LinkedButtonPainter(theme, ButtonState.PRESSED));
			
			reg(ret, "ToolBar:Button.contentMargins", new Insets(6, 6, 6, 6));
			
			
			//TOGGLE BUTTON
			reg(ret, "ToggleButton.foreground", 						theme.getControlText());
			reg(ret, "ToggleButton.textForeground",						theme.getControlText());
			reg(ret, "ToggleButton.disabledText", 						theme.getControlTextDisabled());
			reg(ret, "ToggleButton[Disabled].textForeground", 			theme.getControlTextDisabled());
			reg(ret, "ToggleButton[Default+Pressed].textForeground", 	theme.getControlText());
	
			reg(ret, "ToggleButton[Default+Focused+MouseOver].backgroundPainter", 		new LinkedButtonPainter(theme, ButtonState.DEFAULT, ButtonState.FOCUSED, ButtonState.MOUSEOVER));
			reg(ret, "ToggleButton[Default+Focused+Pressed].backgroundPainter", 		new LinkedButtonPainter(theme, ButtonState.DEFAULT, ButtonState.FOCUSED, ButtonState.PRESSED));
			reg(ret, "ToggleButton[Default+Focused].backgroundPainter", 				new LinkedButtonPainter(theme, ButtonState.DEFAULT, ButtonState.FOCUSED));
			reg(ret, "ToggleButton[Default+MouseOver].backgroundPainter", 				new LinkedButtonPainter(theme, ButtonState.DEFAULT, ButtonState.MOUSEOVER));
			reg(ret, "ToggleButton[Default+Pressed].backgroundPainter", 				new LinkedButtonPainter(theme, ButtonState.DEFAULT, ButtonState.PRESSED));
			reg(ret, "ToggleButton[Default].backgroundPainter", 						new LinkedButtonPainter(theme, ButtonState.DEFAULT));
			reg(ret, "ToggleButton[Disabled].backgroundPainter", 						new LinkedButtonPainter(theme, ButtonState.DISABLED));
			reg(ret, "ToggleButton[Disabled+Selected].backgroundPainter", 				new LinkedButtonPainter(theme, ButtonState.DISABLED, ButtonState.SELECTED));
			reg(ret, "ToggleButton[Enabled].backgroundPainter", 						new LinkedButtonPainter(theme, ButtonState.ENABLED));
			reg(ret, "ToggleButton[Focused+MouseOver].backgroundPainter", 				new LinkedButtonPainter(theme, ButtonState.FOCUSED, ButtonState.MOUSEOVER));
			reg(ret, "ToggleButton[Focused+MouseOver+Selected].backgroundPainter", 		new LinkedButtonPainter(theme, ButtonState.FOCUSED, ButtonState.MOUSEOVER, ButtonState.SELECTED));
			reg(ret, "ToggleButton[Focused+Pressed].backgroundPainter", 				new LinkedButtonPainter(theme, ButtonState.FOCUSED, ButtonState.PRESSED));
			reg(ret, "ToggleButton[Focused+Pressed+Selected].backgroundPainter", 		new LinkedButtonPainter(theme, ButtonState.FOCUSED, ButtonState.PRESSED, ButtonState.SELECTED));
			reg(ret, "ToggleButton[Focused].backgroundPainter", 						new LinkedButtonPainter(theme, ButtonState.FOCUSED));
			reg(ret, "ToggleButton[Focused+Selected].backgroundPainter", 				new LinkedButtonPainter(theme, ButtonState.FOCUSED, ButtonState.SELECTED));
			reg(ret, "ToggleButton[MouseOver].backgroundPainter", 						new LinkedButtonPainter(theme, ButtonState.MOUSEOVER));
			reg(ret, "ToggleButton[MouseOver+Selected].backgroundPainter", 				new LinkedButtonPainter(theme, ButtonState.MOUSEOVER, ButtonState.SELECTED));
			reg(ret, "ToggleButton[Pressed].backgroundPainter", 						new LinkedButtonPainter(theme, ButtonState.PRESSED));
			reg(ret, "ToggleButton[Pressed+Selected].backgroundPainter", 				new LinkedButtonPainter(theme, ButtonState.PRESSED, ButtonState.SELECTED));
			reg(ret, "ToggleButton[Selected].backgroundPainter", 						new LinkedButtonPainter(theme, ButtonState.SELECTED));
			
			
			
			//TOGGLE BUTTON on TOOLBAR
			reg(ret, "ToolBar:ToggleButton[Disabled+Selected].backgroundPainter", 			new LinkedButtonPainter(theme, 3, ButtonState.DISABLED, ButtonState.SELECTED));
			reg(ret, "ToolBar:ToggleButton[Focused+MouseOver].backgroundPainter", 			new LinkedButtonPainter(theme, 3, ButtonState.FOCUSED, ButtonState.MOUSEOVER));
			reg(ret, "ToolBar:ToggleButton[Focused+MouseOver+Selected].backgroundPainter", 	new LinkedButtonPainter(theme, 3, ButtonState.FOCUSED, ButtonState.MOUSEOVER, ButtonState.SELECTED));
			reg(ret, "ToolBar:ToggleButton[Focused+Pressed].backgroundPainter", 				new LinkedButtonPainter(theme, 3, ButtonState.FOCUSED, ButtonState.PRESSED));
			reg(ret, "ToolBar:ToggleButton[Focused+Pressed+Selected].backgroundPainter", 	new LinkedButtonPainter(theme, 3, ButtonState.FOCUSED, ButtonState.PRESSED, ButtonState.SELECTED));
			reg(ret, "ToolBar:ToggleButton[Focused].backgroundPainter", 						new LinkedButtonPainter(theme, 3, ButtonState.FOCUSED));
			reg(ret, "ToolBar:ToggleButton[Focused+Selected].backgroundPainter", 			new LinkedButtonPainter(theme, 3, ButtonState.FOCUSED, ButtonState.SELECTED));
			reg(ret, "ToolBar:ToggleButton[MouseOver].backgroundPainter", 					new LinkedButtonPainter(theme, 3, ButtonState.MOUSEOVER));
			reg(ret, "ToolBar:ToggleButton[MouseOver+Selected].backgroundPainter", 			new LinkedButtonPainter(theme, 3, ButtonState.MOUSEOVER, ButtonState.SELECTED));
			reg(ret, "ToolBar:ToggleButton[Pressed].backgroundPainter", 						new LinkedButtonPainter(theme, 3, ButtonState.PRESSED));
			reg(ret, "ToolBar:ToggleButton[Pressed+Selected].backgroundPainter", 			new LinkedButtonPainter(theme, 3, ButtonState.PRESSED, ButtonState.SELECTED));
			reg(ret, "ToolBar:ToggleButton[Selected].backgroundPainter", 					new LinkedButtonPainter(theme, 3, ButtonState.SELECTED));
			
			reg(ret, "ToolBar:ToggleButton.contentMargins", new Insets(6, 6, 6, 6));
			
			//SCROLL BARS
			reg(ret, "ScrollBar:ScrollBarTrack[Enabled].backgroundPainter", new ScrollBarTrackPainter(theme));
			reg(ret, "ScrollBar:ScrollBarTrack[Enabled].backgroundPainter", new ScrollBarTrackPainter(theme));
			
			reg(ret, "ScrollBar.incrementButtonGap", 0);
			reg(ret, "ScrollBar.decrementButtonGap", 0);
			reg(ret, "ScrollBar.thumbHeight", 14);
			reg(ret, "ScrollBar:\"ScrollBar.button\".size", 0);
	
			reg(ret, "ScrollBar:ScrollBarThumb[Enabled].backgroundPainter", new ScrollBarThumbPainter(theme, ButtonState.ENABLED));
			reg(ret, "ScrollBar:ScrollBarThumb[Pressed].backgroundPainter", new ScrollBarThumbPainter(theme, ButtonState.PRESSED));
			reg(ret, "ScrollBar:ScrollBarThumb[MouseOver].backgroundPainter", new ScrollBarThumbPainter(theme, ButtonState.MOUSEOVER));
			
			
			//COMBOBOX
			reg(ret, "ComboBox[Enabled].backgroundPainter", new ButtonPainter(theme, ButtonState.ENABLED));
			reg(ret, "ComboBox[Enabled+Selected].backgroundPainter", new ButtonPainter(theme, ButtonState.ENABLED));
			reg(ret, "ComboBox[Disabled].backgroundPainter", new ButtonPainter(theme, ButtonState.DISABLED));
			reg(ret, "ComboBox[Disabled+Pressed].backgroundPainter", new ButtonPainter(theme, ButtonState.DISABLED, ButtonState.PRESSED));
			reg(ret, "ComboBox[Disabled+Selected].backgroundPainter", new ButtonPainter(theme, ButtonState.DISABLED));
			reg(ret, "ComboBox[MouseOver].backgroundPainter", new ButtonPainter(theme, ButtonState.MOUSEOVER));
			reg(ret, "ComboBox[Pressed].backgroundPainter", new ButtonPainter(theme, ButtonState.PRESSED));
			reg(ret, "ComboBox[Focused].backgroundPainter", new ButtonPainter(theme, ButtonState.FOCUSED));
			reg(ret, "ComboBox[Focused+Pressed].backgroundPainter", new ButtonPainter(theme, ButtonState.PRESSED, ButtonState.FOCUSED));
			reg(ret, "ComboBox[Focused+MouseOver].backgroundPainter", new ButtonPainter(theme, ButtonState.MOUSEOVER, ButtonState.FOCUSED));
			
			reg(ret, "ComboBox.contentMargins", new Insets(0, 4, 0, 4));
			
			
			//For exitable comboboxes, don't draw anything, let the two components draw themselves
			reg(ret, "ComboBox[Editable+Focused].backgroundPainter", new FillPainter(new Color(0, 0, 0, 0)));
			
	//		reg(ret, "ComboBox[Disabled+Editable].backgroundPainter", new ButtonPainter(ButtonState.DISABLED));
	//		reg(ret, "ComboBox[Editable+Enabled].backgroundPainter", new ButtonPainter(ButtonState.ENABLED));
	//		reg(ret, "ComboBox[Editable+Focused].backgroundPainter", new ButtonPainter(ButtonState.FOCUSED));
	//		reg(ret, "ComboBox[Editable+MouseOver].backgroundPainter", new ButtonPainter(ButtonState.MOUSEOVER));
	//		reg(ret, "ComboBox[Editable+Pressed].backgroundPainter", new ButtonPainter(ButtonState.PRESSED));
	
			reg(ret, "ComboBox:\"ComboBox.textField\"[Disabled].backgroundPainter", new TextFieldBackgroundPainter(theme, ButtonState.DISABLED));
			reg(ret, "ComboBox:\"ComboBox.textField\"[Enabled].backgroundPainter", new TextFieldBackgroundPainter(theme, ButtonState.ENABLED));
			reg(ret, "ComboBox:\"ComboBox.textField\"[Selected].backgroundPainter", new TextFieldBackgroundPainter(theme, ButtonState.SELECTED));
			reg(ret, "ComboBox:\"ComboBox.textField\"[Focused].backgroundPainter", new TextFieldBackgroundPainter(theme, ButtonState.FOCUSED));
			
			
			
			reg(ret, "ComboBox:\"ComboBox.arrowButton\"[Disabled+Editable].backgroundPainter", new ButtonPainter(theme, ButtonState.DISABLED));
			reg(ret, "ComboBox:\"ComboBox.arrowButton\"[Editable+Enabled].backgroundPainter", new ButtonPainter(theme, ButtonState.ENABLED));
			reg(ret, "ComboBox:\"ComboBox.arrowButton\"[Editable+MouseOver].backgroundPainter", new ButtonPainter(theme, ButtonState.MOUSEOVER));
			reg(ret, "ComboBox:\"ComboBox.arrowButton\"[Editable+Pressed].backgroundPainter", new ButtonPainter(theme, ButtonState.PRESSED));
			reg(ret, "ComboBox:\"ComboBox.arrowButton\"[Editable+Selected].backgroundPainter", new ButtonPainter(theme, ButtonState.ENABLED));
			reg(ret, "ComboBox:\"ComboBox.arrowButton\"[Editable+Selected].backgroundPainter", new ButtonPainter(theme, ButtonState.ENABLED));
			
			//combobox arrow painters
			reg(ret, "ComboBox:\"ComboBox.arrowButton\"[Enabled].foregroundPainter", new ComboBoxArrowPainter(theme.getControlText()));
			reg(ret, "ComboBox:\"ComboBox.arrowButton\"[Selected].foregroundPainter", new ComboBoxArrowPainter(theme.getControlText()));
			reg(ret, "ComboBox:\"ComboBox.arrowButton\"[Pressed].foregroundPainter", new ComboBoxArrowPainter(theme.getControlText()));
			reg(ret, "ComboBox:\"ComboBox.arrowButton\"[MouseOver].foregroundPainter", new ComboBoxArrowPainter(theme.getControlText()));
			reg(ret, "ComboBox:\"ComboBox.arrowButton\"[Disabled].foregroundPainter", new ComboBoxArrowPainter(theme.getControlTextDisabled()));

			//combobox list renderer colours
			reg(ret, "ComboBox:\"ComboBox.listRenderer\".background", theme.getRecessedControl());
			reg(ret, "ComboBox:\"ComboBox.listRenderer\"[Disabled].textForeground", theme.getControlTextDisabled());
			reg(ret, "ComboBox:\"ComboBox.listRenderer\"[Selected].background", theme.getHighlight());
			reg(ret, "ComboBox:\"ComboBox.listRenderer\"[Selected].textForeground", theme.getHighlightText());
			
			reg(ret, "ComboBox:\"ComboBox.listRenderer\".contentMargins", new Insets(4, 8, 4, 8));
			
			reg(ret, "ComboBox.foreground", theme.getControlText());
			
			reg(ret, "ComboBoxUI", StratusComboBoxUI.class.getName());
			
			
			
			
			//CHECKBOX
			reg(ret, "CheckBox[Disabled].iconPainter", new CheckButtonPainter(theme, ButtonState.DISABLED));
			reg(ret, "CheckBox[Disabled+Selected].iconPainter", new CompositePainter(new CheckButtonPainter(theme, ButtonState.DISABLED), new CheckPainter(theme, 5, -1, false)));
			reg(ret, "CheckBox[Enabled].iconPainter", new CheckButtonPainter(theme, ButtonState.ENABLED));
			reg(ret, "CheckBox[Enabled+Selected].iconPainter", new CompositePainter(new CheckButtonPainter(theme, ButtonState.ENABLED), new CheckPainter(theme, 5, -1)));
			reg(ret, "CheckBox[Focused+MouseOver].iconPainter", new CheckButtonPainter(theme, ButtonState.FOCUSED, ButtonState.MOUSEOVER));
			reg(ret, "CheckBox[Focused+MouseOver+Selected].iconPainter", new CompositePainter(new CheckButtonPainter(theme, ButtonState.FOCUSED, ButtonState.MOUSEOVER), new CheckPainter(theme, 5, -1)));
			reg(ret, "CheckBox[Focused+Pressed].iconPainter", new CheckButtonPainter(theme, ButtonState.FOCUSED, ButtonState.PRESSED));
			reg(ret, "CheckBox[Focused+Pressed+Selected].iconPainter", new CompositePainter(new CheckButtonPainter(theme, ButtonState.FOCUSED, ButtonState.PRESSED), new CheckPainter(theme, 5, -1)));
			reg(ret, "CheckBox[Focused].iconPainter", new CheckButtonPainter(theme, ButtonState.FOCUSED));
			reg(ret, "CheckBox[Focused+Selected].iconPainter", new CompositePainter(new CheckButtonPainter(theme, ButtonState.FOCUSED), new CheckPainter(theme, 5, -1)));
			reg(ret, "CheckBox[MouseOver].iconPainter", new CheckButtonPainter(theme, ButtonState.MOUSEOVER));
			reg(ret, "CheckBox[MouseOver+Selected].iconPainter", new CompositePainter(new CheckButtonPainter(theme, ButtonState.MOUSEOVER), new CheckPainter(theme, 5, -1)));
			reg(ret, "CheckBox[Pressed].iconPainter", new CheckButtonPainter(theme, ButtonState.PRESSED));
			reg(ret, "CheckBox[Pressed+Selected].iconPainter", new CompositePainter(new CheckButtonPainter(theme, ButtonState.PRESSED), new CheckPainter(theme, 5, -1)));
			reg(ret, "CheckBox[Selected].iconPainter", new CompositePainter(new CheckButtonPainter(theme, ButtonState.ENABLED), new CheckPainter(theme, 5, -1)));
			
			reg(ret, "CheckBox[Enabled].textForeground", theme.getControlText());
			
	
			
			
			//RADIO BUTTONS
			reg(ret, "RadioButton[Disabled+Selected].iconPainter", new RadioButtonPainter(theme, true, ButtonState.DISABLED));
			reg(ret, "RadioButton[Disabled].iconPainter", new RadioButtonPainter(theme, false, ButtonState.DISABLED));
			reg(ret, "RadioButton[Enabled].iconPainter", new RadioButtonPainter(theme, false, ButtonState.ENABLED));
			reg(ret, "RadioButton[Focused+MouseOver+Selected].iconPainter", new RadioButtonPainter(theme, true, ButtonState.FOCUSED, ButtonState.MOUSEOVER));
			reg(ret, "RadioButton[Focused+MouseOver].iconPainter", new RadioButtonPainter(theme, false, ButtonState.FOCUSED, ButtonState.MOUSEOVER));
			reg(ret, "RadioButton[Focused+Pressed+Selected].iconPainter", new RadioButtonPainter(theme, true, ButtonState.FOCUSED, ButtonState.PRESSED));
			reg(ret, "RadioButton[Focused+Pressed].iconPainter", new RadioButtonPainter(theme, false, ButtonState.FOCUSED, ButtonState.PRESSED));
			reg(ret, "RadioButton[Focused+Selected].iconPainter", new RadioButtonPainter(theme, true, ButtonState.FOCUSED));
			reg(ret, "RadioButton[Focused].iconPainter", new RadioButtonPainter(theme, false, ButtonState.FOCUSED));
			reg(ret, "RadioButton[MouseOver+Selected].iconPainter", new RadioButtonPainter(theme, true, ButtonState.MOUSEOVER));
			reg(ret, "RadioButton[MouseOver].iconPainter", new RadioButtonPainter(theme, false, ButtonState.MOUSEOVER));
			reg(ret, "RadioButton[Pressed+Selected].iconPainter", new RadioButtonPainter(theme, true, ButtonState.PRESSED));
			reg(ret, "RadioButton[Pressed].iconPainter", new RadioButtonPainter(theme, false, ButtonState.PRESSED));
			reg(ret, "RadioButton[Selected].iconPainter", new RadioButtonPainter(theme, true, ButtonState.ENABLED));
			
			reg(ret, "RadioButton[Enabled].textForeground", theme.getControlText());
			
			
			
			//SPINNERS TODO: Merged buttons? 
			reg(ret, "Spinner:\"Spinner.nextButton\"[Disabled].backgroundPainter", new NextButtonPainter(theme, ButtonState.DISABLED));
			reg(ret, "Spinner:\"Spinner.nextButton\"[Enabled].backgroundPainter", new NextButtonPainter(theme, ButtonState.ENABLED));
			reg(ret, "Spinner:\"Spinner.nextButton\"[Focused+MouseOver].backgroundPainter", new NextButtonPainter(theme, ButtonState.FOCUSED, ButtonState.MOUSEOVER));
			reg(ret, "Spinner:\"Spinner.nextButton\"[Focused+Pressed].backgroundPainter", new NextButtonPainter(theme, ButtonState.FOCUSED, ButtonState.PRESSED));
			reg(ret, "Spinner:\"Spinner.nextButton\"[Focused].backgroundPainter", new NextButtonPainter(theme, ButtonState.FOCUSED));
			reg(ret, "Spinner:\"Spinner.nextButton\"[MouseOver].backgroundPainter", new NextButtonPainter(theme, ButtonState.MOUSEOVER));
			reg(ret, "Spinner:\"Spinner.nextButton\"[Pressed].backgroundPainter", new NextButtonPainter(theme, ButtonState.PRESSED));
			reg(ret, "Spinner:\"Spinner.previousButton\"[Disabled].backgroundPainter", new PreviousButtonPainter(theme, ButtonState.DISABLED));
			reg(ret, "Spinner:\"Spinner.previousButton\"[Enabled].backgroundPainter", new PreviousButtonPainter(theme, ButtonState.ENABLED));
			reg(ret, "Spinner:\"Spinner.previousButton\"[Focused+MouseOver].backgroundPainter", new PreviousButtonPainter(theme, ButtonState.FOCUSED, ButtonState.MOUSEOVER));
			reg(ret, "Spinner:\"Spinner.previousButton\"[Focused+Pressed].backgroundPainter", new PreviousButtonPainter(theme, ButtonState.FOCUSED, ButtonState.PRESSED));
			reg(ret, "Spinner:\"Spinner.previousButton\"[Focused].backgroundPainter", new PreviousButtonPainter(theme, ButtonState.FOCUSED));
			reg(ret, "Spinner:\"Spinner.previousButton\"[MouseOver].backgroundPainter", new PreviousButtonPainter(theme, ButtonState.MOUSEOVER));
			reg(ret, "Spinner:\"Spinner.previousButton\"[Pressed].backgroundPainter", new PreviousButtonPainter(theme, ButtonState.PRESSED));
			
			reg(ret, "Spinner:\"Spinner.nextButton\"[Disabled].foregroundPainter", new SpinnerArrowPainter(theme.getControlTextDisabled(), true));
			reg(ret, "Spinner:\"Spinner.nextButton\"[Enabled].foregroundPainter", new SpinnerArrowPainter(theme.getControlText(), true));
			reg(ret, "Spinner:\"Spinner.nextButton\"[Focused+MouseOver].foregroundPainter", new SpinnerArrowPainter(theme.getControlText(), true));
			reg(ret, "Spinner:\"Spinner.nextButton\"[Focused+Pressed].foregroundPainter", new SpinnerArrowPainter(theme.getControlText(), true));
			reg(ret, "Spinner:\"Spinner.nextButton\"[Focused].foregroundPainter", new SpinnerArrowPainter(theme.getControlText(), true));
			reg(ret, "Spinner:\"Spinner.nextButton\"[MouseOver].foregroundPainter", new SpinnerArrowPainter(theme.getControlText(), true));
			reg(ret, "Spinner:\"Spinner.nextButton\"[Pressed].foregroundPainter", new SpinnerArrowPainter(theme.getControlText(), true));
			reg(ret, "Spinner:\"Spinner.previousButton\"[Disabled].foregroundPainter", new SpinnerArrowPainter(theme.getControlTextDisabled(), false));
			reg(ret, "Spinner:\"Spinner.previousButton\"[Enabled].foregroundPainter", new SpinnerArrowPainter(theme.getControlText(), false));
			reg(ret, "Spinner:\"Spinner.previousButton\"[Focused+MouseOver].foregroundPainter", new SpinnerArrowPainter(theme.getControlText(), false));
			reg(ret, "Spinner:\"Spinner.previousButton\"[Focused+Pressed].foregroundPainter", new SpinnerArrowPainter(theme.getControlText(), false));
			reg(ret, "Spinner:\"Spinner.previousButton\"[Focused].foregroundPainter", new SpinnerArrowPainter(theme.getControlText(), false));
			reg(ret, "Spinner:\"Spinner.previousButton\"[MouseOver].foregroundPainter", new SpinnerArrowPainter(theme.getControlText(), false));
			reg(ret, "Spinner:\"Spinner.previousButton\"[Pressed].foregroundPainter", new SpinnerArrowPainter(theme.getControlText(), false));
			
	
			
			reg(ret, "Spinner:Panel:\"Spinner.formattedTextField\"[Disabled].backgroundPainter", new TextFieldBackgroundPainter(theme, ButtonState.DISABLED));
			reg(ret, "Spinner:Panel:\"Spinner.formattedTextField\"[Enabled].backgroundPainter", new TextFieldBackgroundPainter(theme, ButtonState.ENABLED));
			reg(ret, "Spinner:Panel:\"Spinner.formattedTextField\"[Focused+Selected].backgroundPainter", new TextFieldBackgroundPainter(theme, ButtonState.FOCUSED, ButtonState.SELECTED));
			reg(ret, "Spinner:Panel:\"Spinner.formattedTextField\"[Focused].backgroundPainter", new TextFieldBackgroundPainter(theme, ButtonState.FOCUSED));
			reg(ret, "Spinner:Panel:\"Spinner.formattedTextField\"[Selected].backgroundPainter", new TextFieldBackgroundPainter(theme, ButtonState.SELECTED));
			
			reg(ret, "Spinner:Panel:\"Spinner.formattedTextField\"[Enabled].textForeground", theme.getRecessedText());
			
			
			
	
			//TEXTFIELD
			
			reg(ret, "TextField[Disabled].borderPainter", new TextFieldBorderPainter(theme, ButtonState.DISABLED));
			reg(ret, "TextField[Enabled].borderPainter", new TextFieldBorderPainter(theme, ButtonState.ENABLED));
			reg(ret, "TextField[Focused].borderPainter", new TextFieldBorderPainter(theme, ButtonState.ENABLED, ButtonState.SELECTED));
	
			reg(ret, "TextField[Disabled].backgroundPainter", new TextFieldBackgroundPainter(theme, ButtonState.DISABLED));
			reg(ret, "TextField[Enabled].backgroundPainter", new TextFieldBackgroundPainter(theme, ButtonState.ENABLED));
			reg(ret, "TextField[Selected].backgroundPainter", new TextFieldBackgroundPainter(theme, ButtonState.ENABLED));
			reg(ret, "TextField[Focused].backgroundPainter", new TextFieldBackgroundPainter(theme, ButtonState.ENABLED, ButtonState.SELECTED));
	
			reg(ret, "TextField.background", theme.getRecessedControl());
			reg(ret, "TextField.foreground", theme.getRecessedText());
			
			
			//TEXTAREA
			reg(ret, "TextArea.background", theme.getRecessedControl());
			reg(ret, "TextArea.foreground", theme.getRecessedText());
			
			reg(ret, "TextArea[Enabled].borderPainter", new TextFieldBorderPainter(theme, ButtonState.ENABLED));
			reg(ret, "TextArea[Enabled+NotInScrollPane].borderPainter", new TextFieldBorderPainter(theme, ButtonState.ENABLED));
			reg(ret, "TextArea[Selected].borderPainter", new TextFieldBorderPainter(theme, ButtonState.ENABLED));
			
			reg(ret, "TextArea[Enabled].backgroundPainter", new TextFieldBackgroundPainter(theme, ButtonState.ENABLED));
			reg(ret, "TextArea[Enabled+NotInScrollPane].backgroundPainter", new TextFieldBackgroundPainter(theme, ButtonState.ENABLED));
			reg(ret, "TextArea[Selected].backgroundPainter", new TextFieldBackgroundPainter(theme, ButtonState.ENABLED));
			
			//TEXTPANE
			reg(ret, "TextPane.background", theme.getRecessedControl());
			reg(ret, "TextPane.foreground", theme.getRecessedText());
			
			reg(ret, "TextPane[Enabled].backgroundPainter", new FillPainter(theme.getRecessedControl()));
			reg(ret, "TextPane[Selected].backgroundPainter", new FillPainter(theme.getRecessedControl()));
			
			
			
			
			//PASSWORD FIELD
			reg(ret, "PasswordField[Disabled].borderPainter", new TextFieldBorderPainter(theme, ButtonState.DISABLED));
			reg(ret, "PasswordField[Enabled].borderPainter", new TextFieldBorderPainter(theme, ButtonState.ENABLED));
			reg(ret, "PasswordField[Focused].borderPainter", new TextFieldBorderPainter(theme, ButtonState.ENABLED, ButtonState.SELECTED));
	
			reg(ret, "PasswordField[Disabled].backgroundPainter", new TextFieldBackgroundPainter(theme, ButtonState.DISABLED));
			reg(ret, "PasswordField[Enabled].backgroundPainter", new TextFieldBackgroundPainter(theme, ButtonState.ENABLED));
			reg(ret, "PasswordField[Focused].backgroundPainter", new TextFieldBackgroundPainter(theme, ButtonState.ENABLED, ButtonState.SELECTED));
			
			//FORMATTED TEXT FIELD (Used in ComboBoxes?)
			reg(ret, "FormattedTextField[Disabled].borderPainter", new TextFieldBorderPainter(theme, ButtonState.DISABLED));
			reg(ret, "FormattedTextField[Enabled].borderPainter", new TextFieldBorderPainter(theme, ButtonState.ENABLED));
			reg(ret, "FormattedTextField[Focused].borderPainter", new TextFieldBorderPainter(theme, ButtonState.ENABLED, ButtonState.SELECTED));
	
			reg(ret, "FormattedTextField[Disabled].backgroundPainter", new TextFieldBackgroundPainter(theme, ButtonState.DISABLED));
			reg(ret, "FormattedTextField[Enabled].backgroundPainter", new TextFieldBackgroundPainter(theme, ButtonState.ENABLED));
			reg(ret, "FormattedTextField[Selected].backgroundPainter", new TextFieldBackgroundPainter(theme, ButtonState.ENABLED, ButtonState.SELECTED));
			
			
			//TEXT AREA
			reg(ret, "TextArea[Enabled+NotInScrollPane].borderPainter", new TextFieldBorderPainter(theme, ButtonState.ENABLED));
			reg(ret, "TextArea[Focused+NotInScrollPane].borderPainter", new TextFieldBorderPainter(theme, ButtonState.ENABLED, ButtonState.SELECTED));
			
			reg(ret, "TextArea[Enabled+NotInScrollPane].backgroundPainter", new TextFieldBackgroundPainter(theme, ButtonState.ENABLED));
			reg(ret, "TextArea[Focused+NotInScrollPane].backgroundPainter", new TextFieldBackgroundPainter(theme, ButtonState.ENABLED, ButtonState.SELECTED));
			
			
			//EDITOR PANE
//			reg(ret, "EditorPane[Selected].backgroundPainter", new TextFieldBackgroundPainter(theme, ButtonState.ENABLED, ButtonState.SELECTED));
//			reg(ret, "EditorPane[Enabled].backgroundPainter", new TextFieldBackgroundPainter(theme, ButtonState.ENABLED));
//			reg(ret, "EditorPane[Disabled].backgroundPainter", new TextFieldBackgroundPainter(theme, ButtonState.DISABLED));
			
			//TITLEDBORDER
			reg(ret, "TitledBorder.border", new CompoundBorder(new EmptyBorder(2, 4, 4, 4), new CompoundBorder(new TitledBorderBorder(theme), new EmptyBorder(6, 6, 6, 6))));
			reg(ret, "TitledBorder.titleColor", theme.getControlText());
			
			//LABEL
			reg(ret, "Label.foreground", theme.getControlText());
			
			//TOOLTIP
			reg(ret, "ToolTip[Enabled].backgroundPainter", new ToolTipPainter(theme));
			reg(ret, "ToolTip[Disabled].backgroundPainter", new ToolTipPainter(theme));
			reg(ret, "ToolTip.textForeground", Color.white);
			reg(ret, "ToolTip.contentMargins", new Insets(8, 8, 8, 8));
			
			
			
			
			//TABBEDPANE
			reg(ret, "TabbedPane:TabbedPaneTabArea[Disabled].backgroundPainter", new TabbedAreaPainter(theme, false));
			reg(ret, "TabbedPane:TabbedPaneTabArea[Enabled+MouseOver].backgroundPainter", new TabbedAreaPainter(theme, true));
			reg(ret, "TabbedPane:TabbedPaneTabArea[Enabled+Pressed].backgroundPainter", new TabbedAreaPainter(theme, true));
			reg(ret, "TabbedPane:TabbedPaneTabArea[Enabled].backgroundPainter", new TabbedAreaPainter(theme, true));
			reg(ret, "TabbedPane:TabbedPaneTabArea.contentMargins", new Insets(0, 8, 0, 8));
			
			reg(ret, "TabbedPane:TabbedPaneTab.contentMargins", new Insets(8, 12, 8, 12));
			reg(ret, "TabbedPane:TabbedPaneTab[Disabled+Selected].backgroundPainter", new TabPainter(theme, ButtonState.DISABLED, ButtonState.SELECTED));
			reg(ret, "TabbedPane:TabbedPaneTab[Disabled].backgroundPainter", new TabPainter(theme, ButtonState.DISABLED));
			reg(ret, "TabbedPane:TabbedPaneTab[Enabled+MouseOver].backgroundPainter", new TabPainter(theme, ButtonState.MOUSEOVER));
			reg(ret, "TabbedPane:TabbedPaneTab[Enabled+Pressed].backgroundPainter", new TabPainter(theme, ButtonState.PRESSED));
			reg(ret, "TabbedPane:TabbedPaneTab[Enabled].backgroundPainter", new TabPainter(theme));
			reg(ret, "TabbedPane:TabbedPaneTab[Focused+MouseOver+Selected].backgroundPainter", new TabPainter(theme, ButtonState.FOCUSED, ButtonState.MOUSEOVER, ButtonState.SELECTED));
			reg(ret, "TabbedPane:TabbedPaneTab[Focused+Pressed+Selected].backgroundPainter", new TabPainter(theme, ButtonState.FOCUSED, ButtonState.PRESSED, ButtonState.SELECTED));
			reg(ret, "TabbedPane:TabbedPaneTab[Focused+Selected].backgroundPainter", new TabPainter(theme, ButtonState.FOCUSED, ButtonState.SELECTED));
			reg(ret, "TabbedPane:TabbedPaneTab[MouseOver+Selected].backgroundPainter", new TabPainter(theme, ButtonState.MOUSEOVER, ButtonState.SELECTED));
			reg(ret, "TabbedPane:TabbedPaneTab[Pressed+Selected].backgroundPainter", new TabPainter(theme, ButtonState.PRESSED, ButtonState.SELECTED));
			reg(ret, "TabbedPane:TabbedPaneTab[Selected].backgroundPainter", new TabPainter(theme, ButtonState.SELECTED));
			
			reg(ret, "TabbedPane:TabbedPaneTab[Focused+Pressed+Selected].textForeground", theme.getControlText());
			reg(ret, "TabbedPane:TabbedPaneTab[Pressed+Selected].textForeground", theme.getControlText());
			reg(ret, "TabbedPane:TabbedPaneTab[Disabled].textForeground", theme.getControlTextDisabled());
			reg(ret, "TabbedPane:TabbedPaneTab.textForeground", theme.getControlText());
			
			reg(ret, "TabbedPane.extendTabsToBase", false);
			
			
			
			//SLIDERS - don't use pressed state, it looks funny with a track behind it, instead keep the mouseover look when pressed
			reg(ret, "Slider:SliderThumb[ArrowShape+Disabled].backgroundPainter", new SliderThumbPainter(theme, ButtonState.DISABLED));
			reg(ret, "Slider:SliderThumb[ArrowShape+Enabled].backgroundPainter", new SliderThumbPainter(theme, ButtonState.ENABLED));
			reg(ret, "Slider:SliderThumb[ArrowShape+Focused+MouseOver].backgroundPainter", new SliderThumbPainter(theme, ButtonState.FOCUSED, ButtonState.MOUSEOVER));
			reg(ret, "Slider:SliderThumb[ArrowShape+Focused+Pressed].backgroundPainter", new SliderThumbPainter(theme, ButtonState.FOCUSED, ButtonState.MOUSEOVER));
			reg(ret, "Slider:SliderThumb[ArrowShape+Focused].backgroundPainter", new SliderThumbPainter(theme, ButtonState.FOCUSED));
			reg(ret, "Slider:SliderThumb[ArrowShape+MouseOver].backgroundPainter", new SliderThumbPainter(theme, ButtonState.MOUSEOVER));
			reg(ret, "Slider:SliderThumb[ArrowShape+Pressed].backgroundPainter", new SliderThumbPainter(theme, ButtonState.MOUSEOVER));
			
			reg(ret, "Slider:SliderThumb[Disabled].backgroundPainter", new SliderThumbPainter(theme, ButtonState.DISABLED));
			reg(ret, "Slider:SliderThumb[Enabled].backgroundPainter", new SliderThumbPainter(theme, ButtonState.ENABLED));
			reg(ret, "Slider:SliderThumb[Focused+MouseOver].backgroundPainter", new SliderThumbPainter(theme, ButtonState.FOCUSED, ButtonState.MOUSEOVER));
			reg(ret, "Slider:SliderThumb[Focused+Pressed].backgroundPainter", new SliderThumbPainter(theme, ButtonState.FOCUSED, ButtonState.MOUSEOVER));
			reg(ret, "Slider:SliderThumb[Focused].backgroundPainter", new SliderThumbPainter(theme, ButtonState.FOCUSED));
			reg(ret, "Slider:SliderThumb[MouseOver].backgroundPainter", new SliderThumbPainter(theme, ButtonState.MOUSEOVER));
			reg(ret, "Slider:SliderThumb[Pressed].backgroundPainter", new SliderThumbPainter(theme, ButtonState.MOUSEOVER));
			
			reg(ret, "Slider:SliderTrack[Enabled].backgroundPainter", new SliderTrackPainter(theme, true));
			reg(ret, "Slider:SliderTrack[Disabled].backgroundPainter", new SliderTrackPainter(theme, false));
			
			reg(ret, "Slider.disabledText", theme.getControlTextDisabled());
			reg(ret, "Slider.foreground", theme.getControlText());
			reg(ret, "Slider.textForeground", theme.getControlText());
			reg(ret, "Slider.tickColor", theme.getControlText());
			
			
			
			//PROGRESS BAR
			reg(ret, "ProgressBar[Enabled].backgroundPainter", new ProgressBarBackgroundPainter(theme, true));
			reg(ret, "ProgressBar[Disabled].backgroundPainter", new ProgressBarBackgroundPainter(theme, false));
			
			reg(ret, "ProgressBar[Disabled].foregroundPainter", new ProgressBarForegroundPainter(theme, false, Mode.EMPTY));
			reg(ret, "ProgressBar[Disabled+Finished].foregroundPainter", new ProgressBarForegroundPainter(theme, false, Mode.FULL));
			reg(ret, "ProgressBar[Disabled+Indeterminate].foregroundPainter", new ProgressBarForegroundPainter(theme, false, Mode.FULL));
			
			reg(ret, "ProgressBar[Enabled].foregroundPainter", new ProgressBarForegroundPainter(theme, true, Mode.EMPTY));
			reg(ret, "ProgressBar[Enabled+Finished].foregroundPainter", new ProgressBarForegroundPainter(theme, true, Mode.FULL));
			reg(ret, "ProgressBar[Enabled+Indeterminate].foregroundPainter", new ProgressBarForegroundPainter(theme, true, Mode.INDETERMINATE));
			
			reg(ret, "ProgressBar.foreground", theme.getControlText());
			
	
			//TABLE
			reg(ret, "Table.background", theme.getRecessedControl());
			reg(ret, "Table.alternateRowColor", Stratus.darken(theme.getRecessedControl()));
			reg(ret, "Table:\"Table.cellRenderer\".background", theme.getRecessedControl());
			reg(ret, "Table[Disabled].textForeground", theme.getControlTextDisabled());
			reg(ret, "Table[Enabled].textForeground", theme.getControlText());
			reg(ret, "Table[Selected].textForeground", theme.getHighlightText());
			
			//TABLEHEADER
			reg(ret, "TableHeader:\"TableHeader.renderer\"[Disabled+Sorted].backgroundPainter", new TableHeaderPainter(theme));
			reg(ret, "TableHeader:\"TableHeader.renderer\"[Disabled].backgroundPainter", new TableHeaderPainter(theme));
			reg(ret, "TableHeader:\"TableHeader.renderer\"[Enabled+Focused+Sorted].backgroundPainter", new TableHeaderPainter(theme));
			reg(ret, "TableHeader:\"TableHeader.renderer\"[Enabled+Focused].backgroundPainter", new TableHeaderPainter(theme));
			reg(ret, "TableHeader:\"TableHeader.renderer\"[Enabled+Sorted].backgroundPainter", new TableHeaderPainter(theme));
			reg(ret, "TableHeader:\"TableHeader.renderer\"[Enabled].backgroundPainter", new TableHeaderPainter(theme));
			reg(ret, "TableHeader:\"TableHeader.renderer\"[MouseOver].backgroundPainter", new TableHeaderPainter(theme));
			reg(ret, "TableHeader:\"TableHeader.renderer\"[Pressed].backgroundPainter", new TableHeaderPainter(theme));
			
			reg(ret, "TableHeader.foreground", theme.getTableHeaderText());
			//For some reason, using the reg method, which wraps colors in a UIResource, causes this one property to break
			ret.put("TableHeader.textForeground", theme.getTableHeaderText());
			reg(ret, "TableHeader.disabledText", theme.getControlTextDisabled());
			reg(ret, "TableHeader:\"TableHeader.renderer\".contentMargins", new Insets(3, 5, 3, 5));
			reg(ret, "TableHeader.font", ((Font)ret.get("TableHeader.font")).deriveFont(Font.BOLD));
			
			
			
			//TREE
			reg(ret, "Tree.background", theme.getRecessedControl());
			reg(ret, "Tree[Enabled].expandedIconPainter", new TreeArrowPainter(theme.getControlText(), true));
			reg(ret, "Tree[Enabled].collapsedIconPainter", new TreeArrowPainter(theme.getControlText(), false));
			reg(ret, "Tree[Enabled+Selected].expandedIconPainter", new TreeArrowPainter(theme.getControlText(), true));
			reg(ret, "Tree[Enabled+Selected].collapsedIconPainter", new TreeArrowPainter(theme.getControlText(), false));
			
			//LIST
			reg(ret, "List.background", theme.getRecessedControl());			
			
			//SPLITPANE
			reg(ret, "SplitPane:SplitPaneDivider[Enabled].backgroundPainter", new SplitPaneDividerPainter(theme));
			reg(ret, "SplitPane:SplitPaneDivider[Focused].backgroundPainter", new SplitPaneDividerPainter(theme));
			reg(ret, "SplitPane.size", 8);
			reg(ret, "SplitPane.dividerSize", 8);
			
			//SEPARATOR
			reg(ret, "Separator.foreground", Stratus.darken(theme.getControl()));
			
		}

		
		if (REPLACE_ICONS) {
		
			// ICONS
			reg(ret, "Tree.closedIcon", getImageIcon("place-folder", 16));
			reg(ret, "Tree.openIcon", getImageIcon("place-folder-open", 16));
			
			reg(ret, "FileView.directoryIcon", getImageIcon("place-folder", 16));
			
			reg(ret, "FileChooser.directoryIcon", getImageIcon("place-folder", 16));
			reg(ret, "FileChooser.fileIcon", getImageIcon("mime-text", 16));
			reg(ret, "FileChooser.homeFolderIcon", getImageIcon("place-home", 16));
			reg(ret, "FileChooser.newFolderIcon", getImageIcon("place-folder-new", 16));
			reg(ret, "FileChooser.upFolderIcon", getImageIcon("go-up", 16));

			reg(ret, "OptionPane.errorIcon", getImageIcon("badge-error", 48));
			reg(ret, "OptionPane.informationIcon", getImageIcon("badge-info", 48));
			reg(ret, "OptionPane.questionIcon", getImageIcon("badge-help", 48));
			reg(ret, "OptionPane.warningIcon", getImageIcon("badge-warning", 48));

		}

		
		return ret;
	}
	
	private static ImageIcon getImageIcon(String imageName, int size){
		
		URL url = getImageIconURL(imageName, size);
		
		if (url == null){
			if (!  (imageName == null || "".equals(imageName))  )
			{
				Logger.getLogger("Swidget").log(Level.WARNING, "Image not found " + imageName);
			}
			url = getImageIconURL("notfound", 0);
		}
		
		ImageIcon image;
		image = new ImageIcon(url);
		return image;
		
	}
	
	private static URL getImageIconURL(String imageName, int size)
	{
		String iconDir = "";
	
		if (size > 0) {
			iconDir = "/stratus/icons/" + size + "/";
		}
		String path = iconDir + imageName + ".png";
				
		return StratusLookAndFeel.class.getResource(path);
		
	}
	
	
	@Override
	public String getID() {
		return "Stratus";
	}
	
	@Override
	public String getName() {
		return "Stratus";
	}
	
	@Override
	public String getDescription() {
		return "Stratus Look and Feel";
	}
	
	

    /**
     * Creates the Synth look and feel <code>ComponentUI</code> for
     * the passed in <code>JComponent</code>.
     *
     * @param c JComponent to create the <code>ComponentUI</code> for
     * @return ComponentUI to use for <code>c</code>
     */
    public static ComponentUI createUI(JComponent c) {
        String key = c.getUIClassID().intern();

        
        
		if (key == "ComboBoxUI") {
			return StratusComboBoxUI.createUI(c);
		} else {
			NimbusLookAndFeel.createUI(c);
		}
        
//        if (key == "ButtonUI") {
//            return SynthButtonUI.createUI(c);
//        }
//        else if (key == "CheckBoxUI") {
//            return SynthCheckBoxUI.createUI(c);
//        }
//        else if (key == "CheckBoxMenuItemUI") {
//            return SynthCheckBoxMenuItemUI.createUI(c);
//        }
//        else if (key == "ColorChooserUI") {
//            return SynthColorChooserUI.createUI(c);
//        }
//        else if (key == "ComboBoxUI") {
//            return SynthComboBoxUI.createUI(c);
//        }
//        else if (key == "DesktopPaneUI") {
//            return SynthDesktopPaneUI.createUI(c);
//        }
//        else if (key == "DesktopIconUI") {
//            return SynthDesktopIconUI.createUI(c);
//        }
//        else if (key == "EditorPaneUI") {
//            return SynthEditorPaneUI.createUI(c);
//        }
//        else if (key == "FileChooserUI") {
//            return SynthFileChooserUI.createUI(c);
//        }
//        else if (key == "FormattedTextFieldUI") {
//            return SynthFormattedTextFieldUI.createUI(c);
//        }
//        else if (key == "InternalFrameUI") {
//            return SynthInternalFrameUI.createUI(c);
//        }
//        else if (key == "LabelUI") {
//            return SynthLabelUI.createUI(c);
//        }
//        else if (key == "ListUI") {
//            return SynthListUI.createUI(c);
//        }
//        else if (key == "MenuBarUI") {
//            return SynthMenuBarUI.createUI(c);
//        }
//        else if (key == "MenuUI") {
//            return SynthMenuUI.createUI(c);
//        }
//        else if (key == "MenuItemUI") {
//            return SynthMenuItemUI.createUI(c);
//        }
//        else if (key == "OptionPaneUI") {
//            return SynthOptionPaneUI.createUI(c);
//        }
//        else if (key == "PanelUI") {
//            return SynthPanelUI.createUI(c);
//        }
//        else if (key == "PasswordFieldUI") {
//            return SynthPasswordFieldUI.createUI(c);
//        }
//        else if (key == "PopupMenuSeparatorUI") {
//            return SynthSeparatorUI.createUI(c);
//        }
//        else if (key == "PopupMenuUI") {
//            return SynthPopupMenuUI.createUI(c);
//        }
//        else if (key == "ProgressBarUI") {
//            return SynthProgressBarUI.createUI(c);
//        }
//        else if (key == "RadioButtonUI") {
//            return SynthRadioButtonUI.createUI(c);
//        }
//        else if (key == "RadioButtonMenuItemUI") {
//            return SynthRadioButtonMenuItemUI.createUI(c);
//        }
//        else if (key == "RootPaneUI") {
//            return SynthRootPaneUI.createUI(c);
//        }
//        else if (key == "ScrollBarUI") {
//            return SynthScrollBarUI.createUI(c);
//        }
//        else if (key == "ScrollPaneUI") {
//            return SynthScrollPaneUI.createUI(c);
//        }
//        else if (key == "SeparatorUI") {
//            return SynthSeparatorUI.createUI(c);
//        }
//        else if (key == "SliderUI") {
//            return SynthSliderUI.createUI(c);
//        }
//        else if (key == "SpinnerUI") {
//            return SynthSpinnerUI.createUI(c);
//        }
//        else if (key == "SplitPaneUI") {
//            return SynthSplitPaneUI.createUI(c);
//        }
//        else if (key == "TabbedPaneUI") {
//            return SynthTabbedPaneUI.createUI(c);
//        }
//        else if (key == "TableUI") {
//            return SynthTableUI.createUI(c);
//        }
//        else if (key == "TableHeaderUI") {
//            return SynthTableHeaderUI.createUI(c);
//        }
//        else if (key == "TextAreaUI") {
//            return SynthTextAreaUI.createUI(c);
//        }
//        else if (key == "TextFieldUI") {
//            return SynthTextFieldUI.createUI(c);
//        }
//        else if (key == "TextPaneUI") {
//            return SynthTextPaneUI.createUI(c);
//        }
//        else if (key == "ToggleButtonUI") {
//            return SynthToggleButtonUI.createUI(c);
//        }
//        else if (key == "ToolBarSeparatorUI") {
//            return SynthSeparatorUI.createUI(c);
//        }
//        else if (key == "ToolBarUI") {
//            return SynthToolBarUI.createUI(c);
//        }
//        else if (key == "ToolTipUI") {
//            return SynthToolTipUI.createUI(c);
//        }
//        else if (key == "TreeUI") {
//            return SynthTreeUI.createUI(c);
//        }
//        else if (key == "ViewportUI") {
//            return SynthViewportUI.createUI(c);
//        }
		return null;
    }

    
    
    private Object reg(UIDefaults reg, Object key, Object value) {
    	
    	if (value instanceof Color) {
    		value = new ColorUIResource((Color) value);
    	}
    	
    	return reg.put(key, value);
    }
	
	
}


class ColorUIResource extends Color implements UIResource {
	ColorUIResource(Color c) {
		super(c.getRGB());
	}
}


