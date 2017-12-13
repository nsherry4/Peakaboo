package peakaboo.ui.javafx.plot.zoom;


import peakaboo.ui.javafx.change.Change;
import peakaboo.ui.javafx.util.ActofUIController;


public class ZoomChange extends Change {

    private double value;

    public ZoomChange(ActofUIController source, double value) {
        super(source);
        this.value = value;
    }

    public double getValue() {
        return value;
    }

}
