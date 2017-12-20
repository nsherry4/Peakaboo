package peakaboo.ui.javafx.plot.filter;

import autodialog.model.Group;
import autodialog.view.javafx.FXAutoDialog;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import peakaboo.filter.model.Filter;
import peakaboo.ui.javafx.PeakabooFX;

public class FilterCellView extends ListCell<Filter> {

	FilterUIController uiController;
	
	HBox hbox = new HBox();
    Label label = new Label("(empty)");
    Button button = new Button("", new ImageView(PeakabooFX.class.getResource("icons/24/misc-preferences.png").toString()));
    CheckBox enabled = new CheckBox();
    Filter lastItem;

    public FilterCellView(FilterUIController uiController) {
        super();
        this.uiController = uiController;
        
        label.setPadding(new Insets(6d));
        
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.getChildren().addAll(enabled, button, label);
        
        button.setOnAction(e -> {
        	Group group = super.getItem().getParameterGroup();
        	FXAutoDialog dialog = new FXAutoDialog(group);
    		group.getValueHook().addListener(o -> {
    			System.out.println("EVENT");
    			uiController.filters.filteredDataInvalidated();
    			uiController.getChangeBus().broadcast(new FilterChange(uiController));
    		});
        	dialog.initialize();
        });
        enabled.setOnAction(e -> {
        	Filter filter = super.getItem();
        	filter.setEnabled(enabled.isSelected());
        	uiController.filters.filteredDataInvalidated();
        	uiController.getChangeBus().broadcast(new FilterChange(uiController));
        });
    }

    @Override
    protected void updateItem(Filter item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);  // No text in label of super class
        if (empty) {
            lastItem = null;
            setGraphic(null);
        } else {
        	enabled.setSelected(super.getItem().isEnabled());
            lastItem = item;
            label.setText(item!=null ? item.getFilterName() : "<No Filter>");
            setGraphic(hbox);
        }
    }
	
}
