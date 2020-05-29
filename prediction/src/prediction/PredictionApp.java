package prediction;

import java.io.File;
import java.util.Arrays;

import org.encog.ConsoleStatusReportable;
import org.encog.Encog;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.versatile.NormalizationHelper;
import org.encog.ml.data.versatile.VersatileMLDataSet;
import org.encog.ml.data.versatile.columns.ColumnType;
import org.encog.ml.data.versatile.sources.CSVDataSource;
import org.encog.ml.data.versatile.sources.VersatileDataSource;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.ml.model.EncogModel;
import org.encog.util.csv.CSVFormat;

public class PredictionApp {

	static VersatileMLDataSet data;

	public static void main(String[] args) {
		CSVFormat format = new CSVFormat(',', ',');
		VersatileDataSource source = new CSVDataSource(new File("nov18.csv"), true, format);

		MLRegression bestMethod = read(source, format);

		source.rewind();

		NormalizationHelper helper = data.getNormHelper();
		String[] line = source.readLine();
		MLData input = helper.allocateInputVector();
		while (line != null) {
			helper.normalizeInputVector(line, input.getData(), false);
			MLData output = bestMethod.compute(input);
			String[] irisChosen = helper.denormalizeOutputVectorToString(output);

			StringBuilder result = new StringBuilder();
			for (String s : irisChosen) {
				result.append(String.format("%.4f", Double.parseDouble(s)) + " ");
			}
			System.out.println(result.toString());

			line = source.readLine();
		}
		Encog.getInstance().shutdown();
	}

	static MLRegression read(VersatileDataSource source, CSVFormat format) {
		data = new VersatileMLDataSet(source);
		data.getNormHelper().setFormat(format);

		for (int i = 0; i < 24; i++) {
			data.defineInput(data.defineSourceColumn("IN" + i, i, ColumnType.continuous));
		}

		for (int i = 0; i < 24; i++) {
			data.defineOutput(data.defineSourceColumn("OUT" + i, i + 24, ColumnType.continuous));
		}

		data.getNormHelper().defineUnknownValue("?");
		data.analyze();

		EncogModel model = new EncogModel(data);
		model.selectMethod(data, MLMethodFactory.TYPE_FEEDFORWARD);
		model.setReport(new ConsoleStatusReportable());

		data.normalize();

//		for (double[] d : data.getData()) {
//			for (double e : d) {
//				System.out.print(e + ", ");
//			}
//			System.out.println();
//		}

		model.holdBackValidation(0.1, false, 1001);
		model.selectTrainingType(data);
		MLRegression bestMethod = (MLRegression) model.crossvalidate(5, false);

		System.out.println("Training  error :  " + model.calculateError(bestMethod, model.getTrainingDataset()));
		System.out.println("Validation  error :  " + model.calculateError(bestMethod, model.getValidationDataset()));

		NormalizationHelper helper = data.getNormHelper();
		System.out.println(helper.toString());// Display the f i n a l model
		System.out.println("Final model: " + bestMethod);

		return bestMethod;
	}

}
