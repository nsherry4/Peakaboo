package org.peakaboo.ui.swing.mapping.sidebar;

import java.util.Optional;
import java.util.function.Consumer;

import javax.swing.ButtonGroup;

import org.peakaboo.controller.mapper.MapUpdateType;
import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.controller.mapper.selection.MapSelectionController.SelectionType;
import org.peakaboo.framework.autodialog.model.Group;
import org.peakaboo.framework.autodialog.view.swing.SwingAutoPanel;
import org.peakaboo.framework.stratus.controls.ButtonLinker;
import org.peakaboo.framework.swidget.icons.IconSize;
import org.peakaboo.framework.swidget.widgets.Spacing;
import org.peakaboo.framework.swidget.widgets.fluent.button.FluentButtonSize;
import org.peakaboo.framework.swidget.widgets.fluent.button.FluentToggleButton;
import org.peakaboo.framework.swidget.widgets.layout.SettingsPanel;

public class MapSelectionPanel extends SettingsPanel {

	private MappingController controller;
	private SelectionType currentSelectionType;
	private ButtonLinker linker;
	
	public MapSelectionPanel(MappingController controller) {
		this.controller = controller;
		setName("Selection");
		
		
		
		FluentToggleButton selRect = new FluentToggleButton()
				.withIcon("select-rectangular", IconSize.BUTTON)
				.withTooltip("Select Rectangle")
				.withButtonSize(FluentButtonSize.COMPACT)
				.withAction(() -> controller.getSelection().setSelectionType(SelectionType.RECTANGLE));
		FluentToggleButton selEllipse = new FluentToggleButton()
				.withIcon("select-ellipse", IconSize.BUTTON)
				.withTooltip("Select Ellipse")
				.withButtonSize(FluentButtonSize.COMPACT)
				.withAction(() -> controller.getSelection().setSelectionType(SelectionType.ELLIPSE));
		FluentToggleButton selSimilar = new FluentToggleButton()
				.withIcon("select-continuous-area", IconSize.BUTTON)
				.withTooltip("Select By Similarity")
				.withButtonSize(FluentButtonSize.COMPACT)
				.withAction(() -> controller.getSelection().setSelectionType(SelectionType.SIMILAR));
		FluentToggleButton selShape = new FluentToggleButton()
				.withIcon("select-lasso", IconSize.BUTTON)
				.withTooltip("Select Hand-Drawn Shape")
				.withButtonSize(FluentButtonSize.COMPACT)
				.withAction(() -> controller.getSelection().setSelectionType(SelectionType.SHAPE));
		ButtonGroup selGroup = new ButtonGroup();
		selGroup.add(selRect);
		selGroup.add(selEllipse);
		selGroup.add(selSimilar);
		selGroup.add(selShape);
		Consumer<SelectionType> onSelChange = s -> {
			if (currentSelectionType == s) {
				return;
			}
			currentSelectionType = s;
			
			switch (s) {
			case ELLIPSE:
				selGroup.setSelected(selEllipse.getModel(), true);
				break;
			case RECTANGLE:
				selGroup.setSelected(selRect.getModel(), true);
				break;
			case SIMILAR:
				selGroup.setSelected(selSimilar.getModel(), true);
				break;			
			case SHAPE:
				selGroup.setSelected(selShape.getModel(), true);
				break;
			}
			
			make();
		};
		linker = new ButtonLinker(selRect, selEllipse, selSimilar, selShape);
				
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
		addSetting(linker);
		Optional<Group> parameters = controller.getSelection().getParameters();
		if (parameters.isPresent()) {
			SwingAutoPanel panel = new SwingAutoPanel(parameters.get());
			panel.setBorder(Spacing.bMedium());
			addSetting(panel);	
		}
		
	}
	
}
