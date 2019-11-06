package org.peakaboo.ui.swing.mapping.sidebar;

import java.util.Optional;
import java.util.function.Consumer;

import org.peakaboo.controller.mapper.MapUpdateType;
import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.controller.mapper.selection.MapSelectionController.SelectionType;
import org.peakaboo.framework.autodialog.model.Group;
import org.peakaboo.framework.autodialog.view.swing.SwingAutoPanel;
import org.peakaboo.framework.stratus.controls.ButtonLinker;
import org.peakaboo.framework.swidget.icons.IconSize;
import org.peakaboo.framework.swidget.widgets.Spacing;
import org.peakaboo.framework.swidget.widgets.buttons.ImageButtonSize;
import org.peakaboo.framework.swidget.widgets.buttons.ToggleImageButton;
import org.peakaboo.framework.swidget.widgets.layout.SettingsPanel;
import org.peakaboo.framework.swidget.widgets.toggle.ToggleGroup;

public class MapSelectionPanel extends SettingsPanel {

	private MappingController controller;
	private SelectionType currentSelectionType;
	private ButtonLinker linker;
	
	public MapSelectionPanel(MappingController controller) {
		this.controller = controller;
		setName("Selection");
		
		
		
		ToggleImageButton selRect = new ToggleImageButton()
				.withIcon("select-rectangular", IconSize.BUTTON)
				.withTooltip("Select Rectangle")
				.withButtonSize(ImageButtonSize.COMPACT)
				.withAction(() -> controller.getSelection().setSelectionType(SelectionType.RECTANGLE));
		ToggleImageButton selEllipse = new ToggleImageButton()
				.withIcon("select-ellipse", IconSize.BUTTON)
				.withTooltip("Select Ellipse")
				.withButtonSize(ImageButtonSize.COMPACT)
				.withAction(() -> controller.getSelection().setSelectionType(SelectionType.ELLIPSE));
		ToggleImageButton selSimilar = new ToggleImageButton()
				.withIcon("select-continuous-area", IconSize.BUTTON)
				.withTooltip("Select By Similarity")
				.withButtonSize(ImageButtonSize.COMPACT)
				.withAction(() -> controller.getSelection().setSelectionType(SelectionType.SIMILAR));
		ToggleImageButton selShape = new ToggleImageButton()
				.withIcon("select-lasso", IconSize.BUTTON)
				.withTooltip("Select Drawn Shape")
				.withButtonSize(ImageButtonSize.COMPACT)
				.withAction(() -> controller.getSelection().setSelectionType(SelectionType.SHAPE));
		ToggleGroup selGroup = new ToggleGroup();
		selGroup.registerButton(selRect);
		selGroup.registerButton(selEllipse);
		selGroup.registerButton(selSimilar);
		selGroup.registerButton(selShape);
		Consumer<SelectionType> onSelChange = s -> {
			if (currentSelectionType == s) {
				return;
			}
			currentSelectionType = s;
			
			switch (s) {
			case ELLIPSE:
				selGroup.setToggled(selEllipse);
				break;
			case RECTANGLE:
				selGroup.setToggled(selRect);
				break;
			case SIMILAR:
				selGroup.setToggled(selSimilar);
				break;			
			case SHAPE:
				selGroup.setToggled(selShape);
				break;
			}
			
			make();
		};
		linker = new ButtonLinker(selRect, selEllipse, selSimilar, selShape);
				
		controller.addListener(t -> {
			boolean enabled = controller.getFiltering().isReplottable();
			this.setEnabled(enabled);
			//threshold.setEnabled(enabled);
			//padding.setEnabled(enabled);
			//thresholdLabel.setEnabled(enabled);
			//paddingLabel.setEnabled(enabled);
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
