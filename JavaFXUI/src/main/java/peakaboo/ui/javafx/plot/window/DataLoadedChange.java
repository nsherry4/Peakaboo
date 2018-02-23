package peakaboo.ui.javafx.plot.window;


import peakaboo.controller.plotter.data.DataController;
import peakaboo.ui.javafx.change.Change;
import peakaboo.ui.javafx.util.ActofUIController;


public class DataLoadedChange extends Change {

    private DataController data;

    public DataLoadedChange(ActofUIController source, DataController data) {
        super(source);
        this.data = data;
    }


    public DataController getData() {
        return data;
    }



}
