package eacmple.project;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.statistics.HistogramDataset;

public class Plotter {

	public Plotter() {
	}

	public void makeChart(ArrayList<Float> x, String name) throws IOException {
		double[] vals = new double[x.size()];
		for (int i = 0; i < x.size() - 1; i++) {
			vals[i] = (double) x.get(i);
		}
		
		HistogramDataset dataset = new HistogramDataset();
		dataset.addSeries("key", vals, 50);

		JFreeChart histogram = ChartFactory.createHistogram("Pv output", "Time", "Energy",  dataset);

		ChartUtils.saveChartAsPNG(new File(name + ".png"), histogram, 450, 400);

	}
}
