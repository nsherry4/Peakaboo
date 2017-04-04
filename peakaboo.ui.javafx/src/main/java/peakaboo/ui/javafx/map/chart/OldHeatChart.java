package peakaboo.ui.javafx.map.chart;

import java.util.Iterator;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;


public class OldHeatChart<X, Y> extends XYChart<X, Y> {

	private double maxValue;
	private int datawidth, dataheight;
	
	public OldHeatChart(Axis<X> xAxis, Axis<Y> yAxis, Series<X,Y> data, int datawidth, int dataheight) {
		this(xAxis, yAxis, FXCollections.observableArrayList(data), datawidth, dataheight);
	}
	
	public OldHeatChart(Axis<X> xAxis, Axis<Y> yAxis, ObservableList<Series<X,Y>> data, int datawidth, int dataheight) {
		super(xAxis, yAxis);
		this.datawidth = datawidth;
		this.dataheight = dataheight;
		setData(data);
	}

	public void setMaximumValue(double max) {
		maxValue = max;
	}
	
	@Override
	protected void dataItemAdded(javafx.scene.chart.XYChart.Series<X, Y> series, int itemIndex,
			javafx.scene.chart.XYChart.Data<X, Y> item) {
        Node bubble = createBubble(series, getData().indexOf(series), item, itemIndex);
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

	@Override
	protected void dataItemRemoved(javafx.scene.chart.XYChart.Data<X, Y> item,
			javafx.scene.chart.XYChart.Series<X, Y> series) {
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

	@Override
	protected void dataItemChanged(javafx.scene.chart.XYChart.Data<X, Y> item) {		
	}

	@Override
	protected void seriesAdded(javafx.scene.chart.XYChart.Series<X, Y> series, int seriesIndex) {
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

	@Override
	protected void seriesRemoved(javafx.scene.chart.XYChart.Series<X, Y> series) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void layoutPlotChildren() {
		for (Series<X, Y> series : getData()) {
			
			Iterator<Data<X,Y>> iter = getDisplayedDataIterator(series);
			while (iter.hasNext()) {
				Data<X,Y> item = iter.next();
				X currentX = ReflectionUtils.forceMethodCall(Data.class, "getCurrentX", item);
				Y currentY = ReflectionUtils.forceMethodCall(Data.class, "getCurrentY", item);
				
                double x = getXAxis().getDisplayPosition(currentX);
                double y = getYAxis().getDisplayPosition(currentY);
                if (Double.isNaN(x) || Double.isNaN(y)) {
                    continue;
                }
                Node bubble = item.getNode();
                
                Rectangle rect;
                if (bubble != null) {
                    if (bubble instanceof StackPane) {
                        StackPane region = (StackPane)item.getNode();
                        if (region.getShape() == null) {
                            rect = new Rectangle(1d, 1d);
                            //getDoubleValue(item.getExtraValue(), 1), getDoubleValue(item.getExtraValue(), 1)
                        } else if (region.getShape() instanceof Ellipse) {
                            rect = (Rectangle)region.getShape();
                        } else {
                            return;
                        }
                        
                        //rect.setWidth(getDoubleValue(item.getExtraValue(), 1) * ((getXAxis() instanceof NumberAxis) ? Math.abs(((NumberAxis)getXAxis()).getScale()) : 1));
                        //rect.setHeight(getDoubleValue(item.getExtraValue(), 1) * ((getYAxis() instanceof NumberAxis) ? Math.abs(((NumberAxis)getYAxis()).getScale()) : 1));
                        rect.setWidth(1 * ((getXAxis() instanceof NumberAxis) ? Math.abs(((NumberAxis)getXAxis()).getScale()) : 1));
                        rect.setHeight(1 * ((getYAxis() instanceof NumberAxis) ? Math.abs(((NumberAxis)getYAxis()).getScale()) : 1));
                        //TODO: set background colour
                        
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
    private Node createBubble(Series<X, Y> series, int seriesIndex, final Data<X,Y> item, int itemIndex) {
        Node bubble = item.getNode();
        // check if bubble has already been created
        if (bubble == null) {
            bubble = new StackPane();
            item.setNode(bubble);
        }
        // set bubble styles
        
        //TODO: Fix This
        //bubble.getStyleClass().setAll("chart-bubble", "series" + seriesIndex, "data" + itemIndex, series.defaultColorStyleClass);
        bubble.getStyleClass().setAll("chart-bubble", "series" + seriesIndex, "data" + itemIndex);
        return bubble;
    }
	
    private static double getDoubleValue(Object number, double nullDefault) {
        return !(number instanceof Number) ? nullDefault : ((Number)number).doubleValue();
    }

	
	
}
