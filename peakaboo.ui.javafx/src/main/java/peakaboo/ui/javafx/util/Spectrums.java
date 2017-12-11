package peakaboo.ui.javafx.util;

import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import scitypes.ReadOnlySpectrum;
import scitypes.Spectrum;


public class Spectrums {

	public static Series<Number, Number> asSeries(ReadOnlySpectrum spectrum) {
		Series<Number, Number> series = new Series<>();
		for (int x = 0; x < spectrum.size(); x++) {
			Data<Number, Number> data = new Data<>(x, spectrum.get(x));
			series.getData().add(data);
		}
		return series;
	}
	
	public static Series<Number, Number> asSeries2D(ReadOnlySpectrum spectrum, int width, int height) {
		Series<Number, Number> series = new Series<>();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int i = y*height+x;
				Data<Number, Number> data = new Data<>(x, y, spectrum.get(i));
				series.getData().add(data);
			}
		}
		return series;
	}
	
}
