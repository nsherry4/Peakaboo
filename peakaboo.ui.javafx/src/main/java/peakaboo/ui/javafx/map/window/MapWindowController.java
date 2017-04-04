package peakaboo.ui.javafx.map.window;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import peakaboo.controller.mapper.MappingController;
import peakaboo.ui.javafx.change.ChangeController;
import peakaboo.ui.javafx.map.tab.MapTabController;
import peakaboo.ui.javafx.util.FXUtil;
import peakaboo.ui.javafx.util.IActofUIController;


public class MapWindowController extends IActofUIController {

	private Stage stage;
	
	@FXML private TabPane tabs;
	
	@Override
	public void ready() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initialize() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	public void show() {
		stage.show();		
	}
	
	public void newTab(MappingController controller) {
		
		try {
			MapTabController mapTabController = MapTabController.load(getChangeBus(), controller);
			
			Tab tab = new Tab();
			tab.setContent(mapTabController.getNode());
			tab.setText(controller.mapsController.getDatasetTitle());
			
			tabs.getTabs().add(tab);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static MapWindowController load(ChangeController changes) throws IOException {
		MapWindowController window = FXUtil.load(MapWindowController.class, "MapWindow.fxml", changes);
		
        Scene scene = new Scene((Parent) window.getNode());
        window.stage = new Stage(StageStyle.DECORATED);
        window.stage.setScene(scene);
        
		return window;
	}
	
}
