package peakaboo.ui.javafx.map.chart;


/*
 * Copyright (c) 2010, 2014, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sun.javafx.charts.Legend;
import com.sun.javafx.charts.Legend.LegendItem;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.beans.NamedArg;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * Chart type that plots bubbles for the data points in a series. The extra value property of Data is used to represent
 * the radius of the bubble it should be a java.lang.Number.
 * @since JavaFX 2.0
 */
public class HeatChart<X,Y> extends XYChart<X,Y> {

    // -------------- PRIVATE FIELDS ------------------------------------------

    private Legend legend = new Legend();

    // -------------- CONSTRUCTORS ----------------------------------------------

    /**
     * Construct a new BubbleChart with the given axis. BubbleChart does not use a Category Axis. 
     * Both X and Y axes should be of type NumberAxis.
     *
     * @param xAxis The x axis to use
     * @param yAxis The y axis to use
     */
    public HeatChart(@NamedArg("xAxis") Axis<X> xAxis, @NamedArg("yAxis") Axis<Y> yAxis) {
        this(xAxis, yAxis, FXCollections.<Series<X, Y>>observableArrayList());
    }

    public HeatChart(@NamedArg("xAxis") Axis<X> xAxis, @NamedArg("yAxis") Axis<Y> yAxis, Series<X, Y> data) {
        this(xAxis, yAxis, FXCollections.<Series<X, Y>>observableArrayList(data));
    }
    
    /**
     * Construct a new BubbleChart with the given axis and data. BubbleChart does not 
     * use a Category Axis. Both X and Y axes should be of type NumberAxis.
     *
     * @param xAxis The x axis to use
     * @param yAxis The y axis to use
     * @param data The data to use, this is the actual list used so any changes to it will be reflected in the chart
     */
    public HeatChart(@NamedArg("xAxis") Axis<X> xAxis, @NamedArg("yAxis") Axis<Y> yAxis, @NamedArg("data") ObservableList<Series<X,Y>> data) {
        super(xAxis, yAxis);
        setLegend(legend);
        if (!(xAxis instanceof ValueAxis && yAxis instanceof ValueAxis)) {
            throw new IllegalArgumentException("Axis type incorrect, X and Y should both be NumberAxis");
        }
        setData(data);
    }

    // -------------- METHODS ------------------------------------------------------------------------------------------

    /**
     * Used to get a double value from a object that can be a Number object or null
     *
     * @param number Object possibly a instance of Number
     * @param nullDefault What value to return if the number object is null or not a Number
     * @return number converted to double or nullDefault
     */
    private static double getDoubleValue(Object number, double nullDefault) {
        return !(number instanceof Number) ? nullDefault : ((Number)number).doubleValue();
    }

    /** @inheritDoc */
    @Override protected void layoutPlotChildren() {
    	
    	
        // update bubble positions
      for (int seriesIndex=0; seriesIndex < _getDataSize(); seriesIndex++) {
            Series<X,Y> series = getData().get(seriesIndex);
//            for (Data<X,Y> item = series.begin; item != null; item = item.next) {
            Iterator<Data<X,Y>> iter = getDisplayedDataIterator(series);
            while(iter.hasNext()) {
                Data<X,Y> item = iter.next();
                double x = getXAxis().getDisplayPosition(_getCurrentX(item));
                double y = getYAxis().getDisplayPosition(_getCurrentY(item));
                if (Double.isNaN(x) || Double.isNaN(y)) {
                    continue;
                }
                Node bubble = item.getNode();
                Rectangle rect;
                if (bubble != null) {
                    if (bubble instanceof StackPane) {
                        StackPane region = (StackPane)item.getNode();
                        if (region.getShape() == null) {
                            rect = new Rectangle(getDoubleValue(item.getExtraValue(), 1), getDoubleValue(item.getExtraValue(), 1));
                        } else if (region.getShape() instanceof Ellipse) {
                            rect = (Rectangle)region.getShape();
                        } else {
                            return;
                        }
                        rect.setWidth(1 * ((getXAxis() instanceof NumberAxis) ? Math.abs(((NumberAxis)getXAxis()).getScale()) : 1));
                        rect.setHeight(1 * ((getYAxis() instanceof NumberAxis) ? Math.abs(((NumberAxis)getYAxis()).getScale()) : 1));
                        
                        double val = getDoubleValue(item.getExtraValue(), 1);
                        val /= 50000d;
                        Paint paint = Color.color(val, val, val);
                        region.setBackground(new Background(new BackgroundFill(paint, null, null)));
                        // Note: workaround for RT-7689 - saw this in ProgressControlSkin
                        // The region doesn't update itself when the shape is mutated in place, so we
                        // null out and then restore the shape in order to force invalidation.
                        region.setShape(null);
                        region.setShape(rect);
                        region.setScaleShape(false);
                        region.setCenterShape(false);
                        region.setCacheShape(false);
                        // position the bubble
                        bubble.setLayoutX(x);
                        bubble.setLayoutY(y);
                    }
                }
            }
        }
    }

    @Override protected void dataItemAdded(Series<X,Y> series, int itemIndex, Data<X,Y> item) {
        Node bubble = createRectangle(series, getData().indexOf(series), item, itemIndex);
        if (shouldAnimate()) {
            // fade in new bubble
            bubble.setOpacity(0);
            getPlotChildren().add(bubble);
            FadeTransition ft = new FadeTransition(Duration.millis(500),bubble);
            ft.setToValue(1);
            ft.play();
        } else {
            getPlotChildren().add(bubble);
        }
    }

    @Override protected  void dataItemRemoved(final Data<X,Y> item, final Series<X,Y> series) {
        final Node bubble = item.getNode();
        if (shouldAnimate()) {
            // fade out old bubble
            FadeTransition ft = new FadeTransition(Duration.millis(500),bubble);
            ft.setToValue(0);
            ft.setOnFinished(actionEvent -> {
                getPlotChildren().remove(bubble);
                removeDataItemFromDisplay(series, item);
                bubble.setOpacity(1.0);
            });
            ft.play();
        } else {
            getPlotChildren().remove(bubble);
            removeDataItemFromDisplay(series, item);
        }
    }

    /** @inheritDoc */
    @Override protected void dataItemChanged(Data<X, Y> item) {
    }
    
    @Override protected  void seriesAdded(Series<X,Y> series, int seriesIndex) {
        // handle any data already in series
        for (int j=0; j<series.getData().size(); j++) {
            Data<X,Y> item = series.getData().get(j);
            Node bubble = createRectangle(series, seriesIndex, item, j);
            if (shouldAnimate()) {
                bubble.setOpacity(0);
                getPlotChildren().add(bubble);
                // fade in new bubble
                FadeTransition ft = new FadeTransition(Duration.millis(500),bubble);
                ft.setToValue(1);
                ft.play();
            } else {
                getPlotChildren().add(bubble);
            }
        }
    }

    @Override protected  void seriesRemoved(final Series<X,Y> series) {
        // remove all bubble nodes
        if (shouldAnimate()) {
            ParallelTransition pt = new ParallelTransition();
            pt.setOnFinished(event -> {
                removeSeriesFromDisplay(series);
            });
            for (XYChart.Data<X,Y> d : series.getData()) {
                final Node bubble = d.getNode();
                // fade out old bubble
                FadeTransition ft = new FadeTransition(Duration.millis(500),bubble);
                ft.setToValue(0);
                ft.setOnFinished(actionEvent -> {
                    getPlotChildren().remove(bubble);
                    bubble.setOpacity(1.0);
                });
                pt.getChildren().add(ft);
            }
            pt.play();
        } else {
            for (XYChart.Data<X,Y> d : series.getData()) {
                final Node bubble = d.getNode();
                getPlotChildren().remove(bubble);
            }
            removeSeriesFromDisplay(series);
        }

    }

    /**
     * Create a Bubble for a given data item if it doesn't already have a node
     *
     *
     * @param series
     * @param seriesIndex The index of the series containing the item
     * @param item        The data item to create node for
     * @param itemIndex   The index of the data item in the series
     * @return Node used for given data item
     */
    private Node createRectangle(Series<X, Y> series, int seriesIndex, final Data<X,Y> item, int itemIndex) {
        Node rect = item.getNode();
        // check if bubble has already been created
        if (rect == null) {
            rect = new StackPane();
            item.setNode(rect);
        }
        // set bubble styles
        rect.getStyleClass().setAll("chart-bubble", "series" + seriesIndex, "data" + itemIndex, _getDefaultColorStyleClass(series));
        return rect;
    }

    /**
     * This is called when the range has been invalidated and we need to update it. If the axis are auto
     * ranging then we compile a list of all data that the given axis has to plot and call invalidateRange() on the
     * axis passing it that data.
     */
    @Override protected void updateAxisRange() {
        // For bubble chart we need to override this method as we need to let the axis know that they need to be able
        // to cover the whole area occupied by the bubble not just its center data value
        final Axis<X> xa = getXAxis();
        final Axis<Y> ya = getYAxis();
        List<X> xData = null;
        List<Y> yData = null;
        if(xa.isAutoRanging()) xData = new ArrayList<X>();
        if(ya.isAutoRanging()) yData = new ArrayList<Y>();
        final boolean xIsCategory = xa instanceof CategoryAxis;
        final boolean yIsCategory = ya instanceof CategoryAxis;
        if(xData != null || yData != null) {
            for(Series<X,Y> series : getData()) {
                for(Data<X,Y> data: series.getData()) {
                    if(xData != null) {
                        if(xIsCategory) {
                            xData.add(data.getXValue());
                        } else {
                            xData.add(xa.toRealValue(xa.toNumericValue(data.getXValue()) + getDoubleValue(data.getExtraValue(), 0)));
                            xData.add(xa.toRealValue(xa.toNumericValue(data.getXValue()) - getDoubleValue(data.getExtraValue(), 0)));
                        }
                    }
                    if(yData != null){
                        if(yIsCategory) {
                            yData.add(data.getYValue());
                        } else {
                            yData.add(ya.toRealValue(ya.toNumericValue(data.getYValue()) + getDoubleValue(data.getExtraValue(), 0)));
                            yData.add(ya.toRealValue(ya.toNumericValue(data.getYValue()) - getDoubleValue(data.getExtraValue(), 0)));
                        }
                    }
                }
            }
            if(xData != null) xa.invalidateRange(xData);
            if(yData != null) ya.invalidateRange(yData);
        }
    }

    /**
     * This is called whenever a series is added or removed and the legend needs to be updated
     */
    @Override protected void updateLegend() {
        legend.getItems().clear();
        if (getData() != null) {
            for (int seriesIndex=0; seriesIndex< getData().size(); seriesIndex++) {
                Series<X,Y> series = getData().get(seriesIndex);
                LegendItem legenditem = new LegendItem(series.getName());
                legenditem.getSymbol().getStyleClass().addAll("series"+seriesIndex,"chart-bubble",
                        "bubble-legend-symbol", _getDefaultColorStyleClass(series));
                legend.getItems().add(legenditem);
            }
        }
        if (legend.getItems().size() > 0) {
            if (getLegend() == null) {
                setLegend(legend);
            }
        } else {
            setLegend(null);
        }
    }
    
    public int _getDataSize() {
    	return ReflectionUtils.forceMethodCall(XYChart.class, "getDataSize", this);
    }
    
    public X _getCurrentX(Data<X, Y> item) {
    	return ReflectionUtils.forceMethodCall(Data.class, "getCurrentX", item);
    }
    
    public Y _getCurrentY(Data<X, Y> item) {
    	return ReflectionUtils.forceMethodCall(Data.class, "getCurrentY", item);
    }
    
    public String _getDefaultColorStyleClass(Series<X, Y> series) {
    	return ReflectionUtils.forceFieldCall(Series.class, "defaultColorStyleClass", series);
    }
    
}
