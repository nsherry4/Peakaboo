package peakaboo.ui.javafx.map.tab;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.layout.BorderPane;
import peakaboo.controller.mapper.MappingController;
import peakaboo.ui.javafx.change.ChangeController;
import peakaboo.ui.javafx.map.chart.HeatChart;
import peakaboo.ui.javafx.util.FXUtil;
import peakaboo.ui.javafx.util.IActofUIController;
import peakaboo.ui.javafx.util.Spectrums;
import scitypes.Spectrum;


public class MapTabController extends IActofUIController {

	@FXML private BorderPane chartpane;

	private MappingController controller;
	private HeatChart<Number, Number> chart;
	
	@Override
	public void ready() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initialize() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	private void setMappingController(MappingController controller) {
		this.controller = controller;
		
		controller.settings.setDataWidth(121);
		controller.settings.setDataHeight(41);

		
		
		int minX, minY, maxX, maxY;
		minX = 0;
		minY = 0;
		maxX = controller.settings.getDataWidth();
		maxY = controller.settings.getDataHeight();
		
		Spectrum spectrum = controller.mapsController.getMapResultSet().sumAllTransitionSeriesMaps();
		Series<Number, Number> series  = Spectrums.asSeries2D(spectrum, maxX, maxY);


		ValueAxis<Number> xAxis = new NumberAxis(minX, maxX, (maxX - minX) / 10d);
		ValueAxis<Number> yAxis = new NumberAxis(minY, maxY, (maxY - minY) / 10d);
		
		chart = new HeatChart<>(xAxis, yAxis, series);
		chartpane.setCenter(chart);
		
		
	}
	
	public static MapTabController load(ChangeController changes, MappingController controller) throws IOException {
		MapTabController tab = FXUtil.load(MapTabController.class, "MapTab.fxml", changes);
		tab.setMappingController(controller);
		return tab;
	}
	
	
}
