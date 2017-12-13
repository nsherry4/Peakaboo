package peakaboo.ui.javafx.plot.zoom;


import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import peakaboo.ui.javafx.change.ChangeController;
import peakaboo.ui.javafx.util.FXUtil;
import peakaboo.ui.javafx.util.IActofUIController;


public class ZoomUIController extends IActofUIController {

    @FXML
    private Button zoomin, zoomout;

    @FXML
    private Slider slider;



    @Override
    public void ready() {
        slider.setMin(0);
        slider.setMax(1);
        slider.setValue(0);
        slider.setMajorTickUnit(0.1);
        slider.setMinorTickCount(10);
        slider.setBlockIncrement(0.1);

        slider.valueProperty().addListener((obs, o, n) -> {
            getChangeBus().broadcast(new ZoomChange(this, slider.getValue()));
        });

    }

    public void onZoomIn() {
        slider.increment();
    }

    public void onZoomOut() {
        slider.decrement();
    }

    @Override
    protected void initialize() throws Exception {
        // TODO Auto-generated method stub

    }



    public final void setMax(double value) {
        slider.setMax(value);
    }

    public final double getMax() {
        return slider.getMax();
    }

    public final void setMin(double value) {
        slider.setMin(value);
    }

    public final double getMin() {
        return slider.getMin();
    }

    public final void setValue(double value) {
        slider.setValue(value);
    }

    public final double getValue() {
        return slider.getValue();
    }

    public final void setShowTickLabels(boolean value) {
        slider.setShowTickLabels(value);
    }

    public final boolean isShowTickLabels() {
        return slider.isShowTickLabels();
    }

    public final void setShowTickMarks(boolean value) {
        slider.setShowTickMarks(value);
    }

    public final boolean isShowTickMarks() {
        return slider.isShowTickMarks();
    }

    public final void setMajorTickUnit(double value) {
        slider.setMajorTickUnit(value);
    }

    public final double getMajorTickUnit() {
        return slider.getMajorTickUnit();
    }

    public final void setMinorTickCount(int value) {
        slider.setMinorTickCount(value);
    }

    public final int getMinorTickCount() {
        return slider.getMinorTickCount();
    }

    public final void setBlockIncrement(double value) {
        slider.setBlockIncrement(value);
    }

    public final double getBlockIncrement() {
        return slider.getBlockIncrement();
    }

    public static ZoomUIController load(ChangeController changes) throws IOException {
        return FXUtil.load(ZoomUIController.class, "Zoom.fxml", changes);
    }

}
