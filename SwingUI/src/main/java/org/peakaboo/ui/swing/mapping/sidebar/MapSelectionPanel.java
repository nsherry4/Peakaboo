package org.peakaboo.ui.swing.mapping.sidebar;

import java.util.function.Consumer;

import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.peakaboo.controller.mapper.MapUpdateType;
import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.controller.mapper.selection.MapSelectionController.SelectionType;
import org.peakaboo.framework.stratus.controls.ButtonLinker;
import org.peakaboo.framework.swidget.icons.IconSize;
import org.peakaboo.framework.swidget.widgets.buttons.ImageButton;
import org.peakaboo.framework.swidget.widgets.buttons.ImageButtonSize;
import org.peakaboo.framework.swidget.widgets.buttons.ToggleImageButton;
import org.peakaboo.framework.swidget.widgets.layout.SettingsPanel;
import org.peakaboo.framework.swidget.widgets.toggle.ToggleGroup;

public class MapSelectionPanel extends SettingsPanel {

	private JSpinner threshold;
	private JSpinner padding;
	
	
	
	public MapSelectionPanel(MappingController controller) {
		
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
		};
		onSelChange.accept(controller.getSelection().getSelectionType());
		ButtonLinker linker = new ButtonLinker(selRect, selEllipse, selSimilar, selShape);
		
		
		addSetting(linker);
		
		setName("Selection");
		
		String thresholdTip = "<html>Controls the selection threshold.<br/>Points selected will be between (v/threshold, v*threshold),<br/>where v is the value of the clicked point.</html>";
		SpinnerNumberModel thresholdModel = new SpinnerNumberModel(controller.getSelection().getNeighbourThreshold(), 1.0d, 100.0d, 0.1d);
		threshold = new JSpinner(thresholdModel);
		JLabel thresholdLabel = new JLabel("Threshold");
		threshold.addChangeListener(l -> {
			Double val = (Double) thresholdModel.getValue();
			controller.getSelection().setNeighbourThreshold(val.floatValue());
		});
		threshold.setToolTipText(thresholdTip);
		thresholdLabel.setToolTipText(thresholdTip);
		
		padding = new JSpinner(new SpinnerNumberModel(controller.getSelection().getNeighbourPadding(), 0, 10, 1));
		padding.addChangeListener(l -> {
			controller.getSelection().setNeighbourPadding((Integer) padding.getValue());
		});
		
		JLabel paddingLabel = new JLabel("Padding");
		addSetting(threshold, thresholdLabel, LabelPosition.BESIDE, false, false);
		addSetting(padding, paddingLabel, LabelPosition.BESIDE, false, false);
		
		controller.addListener(t -> {
			boolean enabled = controller.getFiltering().isReplottable();
			this.setEnabled(enabled);
			threshold.setEnabled(enabled);
			padding.setEnabled(enabled);
			thresholdLabel.setEnabled(enabled);
			paddingLabel.setEnabled(enabled);
			if (t == MapUpdateType.UI_OPTIONS) {
				onSelChange.accept(controller.getSelection().getSelectionType());
			}
		});
		
	}
	
}
