package peakaboo.ui.javafx.plot.window;


import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.CommandLinksDialog;
import org.controlsfx.dialog.CommandLinksDialog.CommandLinksButtonType;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import peakaboo.controller.mapper.MappingController;
import peakaboo.controller.mapper.mapset.MapSetController;
import peakaboo.controller.mapper.mapview.MapSettings;
import peakaboo.controller.plotter.IPlotController;
import peakaboo.controller.plotter.PlotController;
import peakaboo.controller.plotter.settings.ChannelCompositeMode;
import peakaboo.dataset.DatasetReadResult;
import peakaboo.dataset.DatasetReadResult.ReadStatus;
import peakaboo.datasource.model.DataSource;
import peakaboo.datasource.plugin.DataSourceLoader;
import peakaboo.datasource.plugin.DataSourceLookup;
import peakaboo.datasource.plugin.DataSourcePlugin;
import peakaboo.mapping.FittingTransform;
import peakaboo.mapping.results.MapResultSet;
import peakaboo.ui.javafx.change.IChangeController;
import peakaboo.ui.javafx.map.window.MapWindowController;
import peakaboo.ui.javafx.plot.filter.FilterUIController;
import peakaboo.ui.javafx.plot.fitting.FittingUIController;
import peakaboo.ui.javafx.plot.spectrum.SpectrumUIController;
import peakaboo.ui.javafx.plot.window.changes.DisplayOptionsChange;
import peakaboo.ui.javafx.plot.window.changes.EnergyLevelChange;
import peakaboo.ui.javafx.plot.zoom.ZoomUIController;
import peakaboo.ui.javafx.util.FXUtil;
import peakaboo.ui.javafx.util.IActofUIController;
import peakaboo.ui.javafx.widgets.NumberSpinner;
import plural.executor.ExecutorSet;


public class PlotWindowController extends IActofUIController {

    private ZoomUIController zoomUI;
    private SpectrumUIController spectrumUI;
    private FilterUIController filterUI;
    private FittingUIController fittingUI;


    private IPlotController plotController;
    private String savedSessionFileName;

    @FXML
    private Tab filterTab, fittingTab;

    @FXML
    private CheckMenuItem menuLogScale, menuAxes, menuTitle, menuMono, menuRawData, menuIndividual;

    @FXML
    private RadioMenuItem menuSignalSingle, menuSignalAverage, menuSignalMax;

    @FXML
    private BorderPane plotbox;

    @FXML
    private HBox statusbar;
    
    @FXML private HBox toolbar;
    
    private NumberSpinner kev;

    @Override
    public void ready() throws IOException {
        plotController = new PlotController();

        spectrumUI = SpectrumUIController.load(getChangeBus());
        spectrumUI.setPlotController(plotController);
        plotbox.setCenter(spectrumUI.getNode());

        zoomUI = ZoomUIController.load(getChangeBus());
        zoomUI.setId("plot");
        statusbar.getChildren().add(zoomUI.getNode());

        filterUI = FilterUIController.load(getChangeBus());
        filterUI.setFilteringController(plotController.filtering());
        filterTab.setContent(filterUI.getNode());

        fittingUI = FittingUIController.load(getChangeBus());
        fittingUI.setFittingController(plotController.fitting());
        fittingTab.setContent(fittingUI.getNode());

        kev = new NumberSpinner(new BigDecimal(20.48d), new BigDecimal(0.01d));
        kev.numberProperty().addListener((obs, o, n) -> {
        	plotController.settings().setMaxEnergy(n.floatValue());
        	getChangeBus().broadcast(new EnergyLevelChange(this));
        });
        kev.setPrefWidth(100);
        toolbar.getChildren().add(toolbar.getChildren().size() - 1, kev);
        kev.alignmentProperty().set(Pos.CENTER_RIGHT);
        
        

    }

    @Override
    protected void initialize() throws Exception {
        // TODO Auto-generated method stub

    }

    public void toggleLogScale() {
        plotController.settings().setViewLog(menuLogScale.isSelected());
        getChangeBus().broadcast(new DisplayOptionsChange(this));
    }

    public void toggleAxes() {
        plotController.settings().setShowAxes(menuAxes.isSelected());
        getChangeBus().broadcast(new DisplayOptionsChange(this));
    }

    public void toggleTitle() {
        plotController.settings().setShowTitle(menuTitle.isSelected());
        getChangeBus().broadcast(new DisplayOptionsChange(this));
    }

    public void toggleMono() {
        plotController.settings().setMonochrome(menuMono.isSelected());
        getChangeBus().broadcast(new DisplayOptionsChange(this));
    }

    public void toggleRawData() {
        plotController.settings().setShowRawData(menuRawData.isSelected());
        getChangeBus().broadcast(new DisplayOptionsChange(this));
    }

    public void toggleIndividual() {
        plotController.settings().setShowIndividualSelections(menuIndividual.isSelected());
        getChangeBus().broadcast(new DisplayOptionsChange(this));
    }

    public void selectSignalSingle() {
        plotController.settings().setShowChannelMode(ChannelCompositeMode.NONE);
        getChangeBus().broadcast(new DisplayOptionsChange(this));
    }

    public void selectSignalAverage() {
        plotController.settings().setShowChannelMode(ChannelCompositeMode.AVERAGE);
        getChangeBus().broadcast(new DisplayOptionsChange(this));
    }

    public void selectSignalMax() {
        plotController.settings().setShowChannelMode(ChannelCompositeMode.MAXIMUM);
        getChangeBus().broadcast(new DisplayOptionsChange(this));
    }

    public void onMapFittings() throws IOException {
    	//TODO: Show progress
    	MapResultSet results = plotController.getMapCreationTask(FittingTransform.AREA).startWorkingBlocking();
    	
    	MapSettings mapSettings = plotController.getLastMapSettings();
    	
    	MapSetController mapData = new MapSetController();
		if (plotController.data().getDataSet().hasPhysicalSize()) {
			mapData.setMapData(
					results,
					plotController.data().getDataSet().getScanData().datasetName(),
					plotController.data().getDataSet().getDataSize().getDataDimensions(),
					plotController.data().getDataSet().getPhysicalSize().getPhysicalDimensions(),
					plotController.data().getDataSet().getPhysicalSize().getPhysicalUnit(),
					plotController.data().getDiscards().list()
				);
		} else {
			mapData.setMapData(
					results,
					plotController.data().getDataSet().getScanData().datasetName(),
					plotController.data().getDiscards().list()
				);
		}
		
		MappingController mapController = new MappingController(mapData, mapSettings, plotController);
		    	
		
		//create a new change bus for the mapping window, it's results should be isolated from changes elsewhere
		MapWindowController mapWindow = MapWindowController.load(new IChangeController());
		mapWindow.newTab(mapController);
		mapWindow.show();
		
		
    	
    }
    
    public void onMapFittingsHeight() throws IOException {
    	//TODO:
    	Alert alert = new Alert(AlertType.INFORMATION);
    	alert.setContentText("This feature not yet implemented");
    	alert.setTitle("TODO");
    	alert.showAndWait();
    }
    

    public void onDatasetOpen() {

        List<File> files;
        List<DataSource> formats = new ArrayList<DataSource>(DataSourceLoader.getPluginSet().newInstances());

        String[][] exts = new String[formats.size()][];
        String[] descs = new String[formats.size()];
        for (int i = 0; i < formats.size(); i++) {
            exts[i] = formats.get(i).getFileFormat().getFileExtensions().toArray(new String[] {});
            descs[i] = formats.get(i).getFileFormat().getFormatName();
        }

        files = openNewDataset(exts, descs);
        if (files == null) return;

        loadFiles(files);

    }


    private List<File> openNewDataset(String[][] exts, String[] desc) {

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open Schema File");

        exts = new String[][]{{"*"}};
        desc = new String[]{"All Files"};
        
        ExtensionFilter filter;
        for (int i = 0; i < desc.length; i++) {
            List<String> extensions = new ArrayList<String>();
            for (String ext : exts[i]) {
                extensions.add("*." + ext);
            }
            filter = new ExtensionFilter(desc[i], extensions);

            chooser.getExtensionFilters().add(filter);
        }

        // chooser.setSelectedExtensionFilter(filter);
        chooser.setTitle("Select Data Files to Open");
        List<File> files = chooser.showOpenMultipleDialog(getNode().getScene().getWindow());
        if (files == null) { return null; }
        //array returned from dialog is unmodifiable
        files = new ArrayList<>(files);
        File lastDir = plotController.data().getDataSet().getDataSourcePath();
        chooser.setInitialDirectory(lastDir);

        return files;

        // return SwidgetIO.openFiles(container.getContainer(),
        // "Select Data Files to Open", exts, desc, controller.data()
        // .getDataSourceFolder());
    }

    private void loadFiles(List<File> files) {

		List<DataSourcePlugin> candidates =  DataSourceLoader.getPluginSet().newInstances();
		List<DataSource> formats = DataSourceLookup.findDataSourcesForFiles(files, candidates);

        if (formats.size() > 1) {
            DataSource dsp = pickDSP(formats, getNode());
            if (dsp != null) loadFiles(files, dsp);
        } else if (formats.size() == 0) {
        	Alert openFailed = new Alert(AlertType.ERROR);
        	openFailed.setTitle("Open Failed");
        	openFailed.setHeaderText("Peakaboo could not open the selected files.");
        	openFailed.setContentText("Could not determine the data format of the selected file(s).");
        	openFailed.showAndWait();
        } else {
            loadFiles(files, formats.get(0));
        }

    }

    private static DataSource pickDSP(List<DataSource> formats, Node owner) {

    	    	
        List<CommandLinksButtonType> links = new ArrayList<>();
    	CommandLinksButtonType link;
        for (DataSource format : formats) {
            link = new CommandLinksButtonType(format.getFileFormat().getFormatName(), format.getFileFormat().getFormatDescription(), false);
            links.add(link);
        }

        CommandLinksDialog dialog = new CommandLinksDialog(links);
        dialog.setTitle("Please Select Data Format");
        dialog.setHeaderText("Peakaboo can't determine the format of this data");
        dialog.setContentText("");
        Optional<ButtonType> optResult = dialog.showAndWait();
        
        if (!optResult.isPresent()) return null;
                
        ButtonType result = optResult.get();
        System.out.println(result.getText());      
        
        for (DataSource format : formats) {
            if (format.getFileFormat().getFormatName().equals(result.getText())) { return format; }
        }

        return null;


    }

    private void loadFiles(List<File> files, DataSource dsp) {
        if (files != null) {

            ExecutorSet<DatasetReadResult> reading = plotController.data().TASK_readFileListAsDataset(files, dsp);

            // ExecutorSetView view = new
            // ExecutorSetView(getNode().getScene().getWindow(), reading);
            //
            // // handle some race condition where the window gets told to close
            // // too early on failure
            // // I don't think its in my code, but I don't know for sure
            // view.setVisible(false);

            // TODO: have GUI for loading
            reading.startWorkingBlocking();

            DatasetReadResult result = reading.getResult();
            if (result.status == ReadStatus.FAILED) {
            	Alert openFailed = new Alert(AlertType.ERROR);
            	openFailed.setTitle("Open Failed");
            	openFailed.setHeaderText("Peakaboo could not open the data set.");
            	openFailed.setContentText("Peakaboo could not open this dataset.\n" + result.message);
            	openFailed.showAndWait();
            }

            getChangeBus().broadcast(new DataLoadedChange(this, plotController.data()));

            // set some controls based on the fact that we have just loaded a
            // new data set
            savedSessionFileName = null;

        }
    }


    public static PlotWindowController load() throws IOException {
        return FXUtil.load(PlotWindowController.class, "PlotWindow.fxml", new IChangeController());
    }
}
