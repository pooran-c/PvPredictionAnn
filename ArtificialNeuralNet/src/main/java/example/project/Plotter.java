package example.project;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class Plotter {

	public Plotter() {
	}

	public void makeChart(ArrayList<Float> x, String name) throws IOException {
		double[] vals = new double[x.size()];
		for (int i = 0; i < x.size() - 1; i++) {
			vals[i] = (double) x.get(i);
		}

		JFreeChart lineChart = ChartFactory.createLineChart("Pv output", "Time", "Energy", createDataset(vals),
				PlotOrientation.VERTICAL, true, true, false);

		ChartUtils.saveChartAsPNG(new File(name + ".png"), lineChart, 950, 400);

	}
	
	

	private DefaultCategoryDataset createDataset(double[] vals) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (int i = 0; i < vals.length; i++) {
			dataset.addValue(vals[i], "Energy", String.valueOf(i));
		}

		return dataset;
	}
}
