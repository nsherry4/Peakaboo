package org.peakaboo.ui.swing.mapping.sidebar;

import java.util.Optional;
import java.util.function.Consumer;

import javax.swing.ButtonGroup;

import org.peakaboo.controller.mapper.MapUpdateType;
import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.controller.mapper.selection.MapSelectionController.SelectionType;
import org.peakaboo.framework.autodialog.model.Group;
import org.peakaboo.framework.autodialog.view.swing.SwingAutoPanel;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.api.icons.IconSize;
import org.peakaboo.framework.stratus.components.ButtonLinker;
import org.peakaboo.framework.stratus.components.panels.SettingsPanel;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButtonSize;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentToggleButton;
import org.peakaboo.ui.swing.app.PeakabooIcons;

public class MapSelectionPanel extends SettingsPanel {

	private MappingController controller;
	private SelectionType currentSelectionType;
	private MapSelectionComponent selections;
	
	public static class MapSelectionComponent extends ButtonLinker {
		
		private FluentToggleButton selRect, selEllipse, selSimilar, selShape;
		private ButtonGroup selGroup;
		
		public MapSelectionComponent(Consumer<SelectionType> listener) {
		
			var fg = Stratus.getTheme().getControlText();
			
			selRect = new FluentToggleButton()
					.withIcon(PeakabooIcons.SELECT_RECTANGULAR, IconSize.BUTTON, fg)
					.withTooltip("Select Rectangle")
					.withButtonSize(FluentButtonSize.COMPACT)
					.withAction(() -> listener.accept(SelectionType.RECTANGLE));
			
			selEllipse = new FluentToggleButton()
					.withIcon(PeakabooIcons.SELECT_ELLIPSE, IconSize.BUTTON, fg)
					.withTooltip("Select Ellipse")
					.withButtonSize(FluentButtonSize.COMPACT)
					.withAction(() -> listener.accept(SelectionType.ELLIPSE));
			
			selSimilar = new FluentToggleButton()
					.withIcon(PeakabooIcons.SELECT_CONTINUOUS_AREA, IconSize.BUTTON, fg)
					.withTooltip("Select By Similarity")
					.withButtonSize(FluentButtonSize.COMPACT)
					.withAction(() -> listener.accept(SelectionType.SIMILAR));
			
			selShape = new FluentToggleButton()
					.withIcon(PeakabooIcons.SELECT_LASSO, IconSize.BUTTON, fg)
					.withTooltip("Select Hand-Drawn Shape")
					.withButtonSize(FluentButtonSize.COMPACT)
					.withAction(() -> listener.accept(SelectionType.SHAPE));
			
			selGroup = new ButtonGroup();
			selGroup.add(selRect);
			selGroup.add(selEllipse);
			selGroup.add(selSimilar);
			selGroup.add(selShape);
			
			addButton(selRect);
			addButton(selEllipse);
			addButton(selSimilar);
			addButton(selShape);
			
						
		}
		
		public void setSelection(SelectionType selection) {
			
			var button = switch(selection) {
				case ELLIPSE -> selEllipse;
				case RECTANGLE -> selRect;
				case SIMILAR -> selSimilar;
				case SHAPE -> selShape;
			};
					
			selGroup.setSelected(button.getModel(), true);
			
		}
		
	}
	
	public MapSelectionPanel(MappingController controller) {
		this.controller = controller;
		setName("Selection");
		
		selections = new MapSelectionComponent(selected -> controller.getSelection().setSelectionType(selected));
		

		Consumer<SelectionType> onSelChange = s -> {
			if (currentSelectionType == s) {
				return;
			}
			currentSelectionType = s;
			
			selections.setSelection(s);
			
			make();
		};
				
		controller.addListener(t -> {
			boolean enabled = controller.getFiltering().isReplottable();
			this.setEnabled(enabled);
			if (t == MapUpdateType.UI_OPTIONS) {
				onSelChange.accept(controller.getSelection().getSelectionType());
			}
		});
		
		currentSelectionType = null;
		onSelChange.accept(controller.getSelection().getSelectionType());
		
	}
	
	private void make() {
		clearSettings();
		addSetting(selections);
		Optional<Group> parameters = controller.getSelection().getParameters();
		if (parameters.isPresent()) {
			SwingAutoPanel panel = new SwingAutoPanel(parameters.get());
			panel.setBorder(Spacing.bMedium());
			addSetting(panel);	
		}
		
	}
	
}
