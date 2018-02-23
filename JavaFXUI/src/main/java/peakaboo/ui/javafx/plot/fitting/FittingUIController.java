package peakaboo.ui.javafx.plot.fitting;


import java.io.IOException;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.BorderPane;
import peakaboo.controller.plotter.fitting.FittingController;
import peakaboo.curvefit.model.transitionseries.TransitionSeries;
import peakaboo.curvefit.model.transitionseries.TransitionSeriesType;
import peakaboo.ui.javafx.change.ChangeController;
import peakaboo.ui.javafx.util.FXUtil;
import peakaboo.ui.javafx.util.IActofUIController;
import peakaboo.ui.javafx.widgets.dialogheader.DialogHeader;


public class FittingUIController extends IActofUIController {

	private FittingController fittings;

	@FXML private BorderPane fittingsPane, lookupPane, autofitPane, summationPane;
	@FXML private TableView<TransitionSeries> fittingsTable;
	@FXML private TableColumn<TransitionSeries, Boolean>  fittingsFit;
	@FXML private TableColumn<TransitionSeries, String> fittingsTS;
	@FXML private ListView<TransitionSeries> lookupListK, lookupListL, lookupListM;
	@FXML private Accordion lookupAccordion;
	
	@Override
	public void ready() throws IOException {
		
		DialogHeader lookupHeader = DialogHeader.load(this::lookupAccept, this::lookupCancel);
		lookupPane.setTop(lookupHeader.getNode());
		
		
		fittingsTS.setCellValueFactory(features -> {
			return new SimpleStringProperty(features.getValue().toElementString());
		});
		
		new CheckBoxTableCell<TransitionSeries, Boolean>();
		
		new CheckBoxTableCell<TransitionSeries, TransitionSeries>(row -> {
			return Bindings.selectBoolean(fittings.getFittedTransitionSeries().get(row), "visible");
		});
		

		
		
		fittingsFit.setCellValueFactory(features -> {
			SimpleBooleanProperty prop = new SimpleBooleanProperty(features.getValue().isVisible());
			prop.addListener((obs, o, n) -> {
				features.getValue().setVisible(n);
				fittings.fittingDataInvalidated();
				getChangeBus().broadcast(new FittingChange(this));
			});
			return prop;
		});
		fittingsFit.setCellFactory(column -> new CheckBoxTableCell<>());
		
		showFittings();
		
	}

	@Override
	protected void initialize() throws Exception {
		// TODO Auto-generated method stub

	}
	
	
	public void setFittingController(FittingController fittings) {
		this.fittings = fittings;
		populateFittings();
		populateLookups();
	}
	
	private void populateFittings() {
		fittingsTable.getItems().setAll(fittings.getFittedTransitionSeries());
		
	}
	
	private void populateLookups() {
		lookupListK.getItems().setAll(fittings.getUnfittedTransitionSeries(TransitionSeriesType.K));
		lookupListL.getItems().setAll(fittings.getUnfittedTransitionSeries(TransitionSeriesType.L));
		lookupListM.getItems().setAll(fittings.getUnfittedTransitionSeries(TransitionSeriesType.M));
	}
	
	
	
	

	public void fittingsAddGuided() {
		showAutofit();
	}
	
	public void fittingsAddElement() {
		showLookup();
	}
	
	public void fittingsAddSummation() {
		showSummation();
	}
	
	public void fittingsRemove() {
		
	}
	
	public void fittingsClear() {
		
	}
	
	public void fittingsSort() {
		
	}
	
	public void fittingsUp() {
		
	}
	
	public void fittingsDown() {
		
	}
	
	
	
	public void lookupAccept() {
		TitledPane pane = lookupAccordion.getExpandedPane();
		if (pane != null) {
			ListView<TransitionSeries> list = (ListView<TransitionSeries>) pane.getContent();
			TransitionSeries ts = list.getSelectionModel().getSelectedItem();
			fittings.addTransitionSeries(ts);
		}
		
		populateFittings();
		populateLookups();
		showFittings();
		getChangeBus().broadcast(new FittingChange(this));
	}

	public void lookupCancel() {
		showFittings();
	}
	
	
	
	
	private void showNone() {
		fittingsPane.setVisible(false);
		lookupPane.setVisible(false);
		autofitPane.setVisible(false);
		summationPane.setVisible(false);
	}
	
	private void showFittings() {
		showNone();
		fittingsPane.setVisible(true);
	}
	
	private void showLookup() {
		showNone();
		lookupPane.setVisible(true);
	}
	
	private void showAutofit() {
		showNone();
		autofitPane.setVisible(true);
	}
	
	private void showSummation() {
		showNone();
		summationPane.setVisible(true);
	}
	

	public static FittingUIController load(ChangeController changes) throws IOException {
		return FXUtil.load(FittingUIController.class, "Fitting.fxml", changes);
	}


}
