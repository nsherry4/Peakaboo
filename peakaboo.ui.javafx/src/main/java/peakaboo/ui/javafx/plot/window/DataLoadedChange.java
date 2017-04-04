package peakaboo.ui.javafx.plot.window;


import peakaboo.controller.plotter.data.IDataController;
import peakaboo.ui.javafx.change.Change;
import peakaboo.ui.javafx.util.ActofUIController;


public class DataLoadedChange extends Change {

    private IDataController data;

    public DataLoadedChange(ActofUIController source, IDataController data) {
        super(source);
        this.data = data;
    }


    public IDataController getData() {
        return data;
    }



}
