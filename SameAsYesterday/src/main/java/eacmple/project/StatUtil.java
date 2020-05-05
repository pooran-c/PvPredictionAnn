package eacmple.project;

import java.util.ArrayList;

public class StatUtil {

	public static double rmse(ArrayList<Float> truth, ArrayList<Float> prediction) {
		if (truth.size() != prediction.size()) {
			throw new IllegalArgumentException(
					String.format("The vector sizes don't match: %d != %d.", truth.size(), prediction.size()));
		}

		int n = truth.size();
		double rss = 0.0;
		for (int i = 0; i < n; i++) {
			rss += Math.pow(truth.get(i) - prediction.get(i), 2);
		}

		return Math.sqrt(rss / n);
	}
}