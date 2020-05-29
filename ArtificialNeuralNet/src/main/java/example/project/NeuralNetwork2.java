package example.project;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.opencsv.CSVReader;

/**
 * Simple Artificial Neural Network
 * 
 * Data Preprocessed to predict the Pv output using the sliding window
 * technique, Data used in the Pv output for one year.
 * 
 * Sliding window is created with "24 input data points" and "one output data
 * point"
 * 
 * The neural net has 24 input neurons and one hidden layer with 10 neurons, 1
 * output neurons.
 *
 */
public class NeuralNetwork2 {

	// Variable Declaration

	/**
	 * List of neural net layers
	 */
	static Layer[] layers;

	/**
	 * The Window size for the sliding window
	 */
	static int window = 24;

	/**
	 * The dataset containing the PV production
	 */
	static Data2[] dataSet;
	
	/**
	 * The training set
	 */
	static Data2[] trainSet;
	
	/**
	 * The test set
	 */
	static Data2[] testSet;
	
	/**
	 * The predicted data set
	 */
	static PredictedData2[] predictedSet;
	
	/**
	 * The Testing Data
	 */
	static TestingData[] testDataSet;

	public static void main(String[] args) throws IOException {

		// Set the Min and Max weight value for all Neurons
		Neuron.setRangeWeight(-1, 1);

		// The neural net contains 3 layers
		layers = new Layer[3];

		// First layer is input layer with 24 neurons
		layers[0] = null;

		// First hidden layer with, 10 neurons and 24 connections from the input layer
		layers[1] = new Layer(24, 10);

		// Output layer with 1 neurons and 10 connections from the hidden layer
		layers[2] = new Layer(10, 1);

		// ==========================================================
		// creating the Data, Splitting the test and train

		CreateTrainingData();
		int trainRatio = 80;
		trainTestSplit(dataSet, trainRatio);

		// ==========================================================
		// Training the Neural network
		train(10, 0.05f);
		
		// ==========================================================

		System.out.println("============");
		System.out.println("Predicting for test data after training");
		System.out.println("============");

		int totalTestSize = testSet.length;
		predictedSet = new PredictedData2[totalTestSize];
		
		/*
		 * Every predict (Forward propogation) gives one output data points
		 * example : if forward propogation invoked 24 times, we get 24 predicted data points
		 *  
		 */
		ArrayList<Float> eOutput = new ArrayList<Float>();
		for (int i = 0; i < totalTestSize; i++) {
			forward(testSet[i].actualInput);
			// value on the last neuron will give the predicted data 
			eOutput.add(testSet[i].actualInput.get(0));
			predictedSet[i] = new PredictedData2(layers[2].neurons[0].value);
		}

		
		double rmse = 0.0;
		for (int i = 1; i < totalTestSize; i++) {			
			rmse += StatUtil.rmse(testSet[i].getExpectedOutput() , predictedSet[i].predicted );
		}
		double finalRmse = rmse / totalTestSize;

		System.out.println("RMSE values of nural net is " + finalRmse);


		Plotter p = new Plotter();
		Plotter p1 = new Plotter();
		

		//ArrayList<Float> eOutput = new ArrayList<Float>();
		ArrayList<Float> pOutput = new ArrayList<Float>();
		for (int i = testSet.length-23; i < testSet.length-1; i ++) {
			
			pOutput.add(predictedSet[i].predicted);			
		}
		


		System.out.println(eOutput.size());
		System.out.println(pOutput.size());
		p.makeChart(eOutput, "Test3");
		p1.makeChart(pOutput, "Predicted3");
		
		

		
//		p.makeChart(testSet[2].expectedOutput, "Test2");
//		p1.makeChart(predictedSet[2].predicted, "Predicted2");

	}

	private static void trainTestSplit(Data2[] dataSet, float trainRatio) {
		float totalRecords = dataSet.length;

		float splitIndex = (float) Math.floor(totalRecords * (trainRatio / 100));
		System.out.println("total records " + totalRecords + " is split into 0 index to " + (int) splitIndex
				+ " index as train dataset , and  " + (int) (splitIndex + 1) + " index to " + (int) (totalRecords - 1)
				+ " index as test dataset");

		float nextIndex = totalRecords - splitIndex;

		trainSet = new Data2[(int) (splitIndex)];
		testSet = new Data2[(int) (nextIndex)];

		trainSet = Arrays.copyOfRange(dataSet, 0, (int) splitIndex);
		testSet = Arrays.copyOfRange(dataSet, (int) (splitIndex + 1), (int) (totalRecords - 1));

	}

//	/**
//	 * This method creates the data for testing the neural net.
//	 * 
//	 * @return arrayList of arrayList of the test data
//	 */
//	private static ArrayList<ArrayList<Float>> getTestData() {
//		String baseDirTest = "src/main/resources/superTest.csv";
//
//		ArrayList<String> pvOutput = new ArrayList<String>();
//
//		ArrayList<ArrayList<Float>> X_test = new ArrayList<ArrayList<Float>>();
//
//		try (Reader reader = Files.newBufferedReader(Paths.get(baseDirTest)); @SuppressWarnings("deprecation")
//		CSVReader csvReader = new CSVReader(reader, ',', '"', 1);) {
//			// Reading Records One by One in a String array
//			String[] nextRecord;
//
//			while ((nextRecord = csvReader.readNext()) != null) {
//				pvOutput.add(nextRecord[1]);
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		ArrayList<Float> intPvOut = parsingStringTofloat(pvOutput);
//		X_test.add(intPvOut);
//		return X_test;
//	}

	/**
	 * simple output to parse the float to string and adding into arraylist
	 * 
	 * @param pvOutput2 ArrayList of pv-ouput-value
	 * @return
	 */
	private static ArrayList<Float> parsingStringTofloat(ArrayList<String> pvOutput2) {
		ArrayList<Float> res = new ArrayList<Float>();
		int size = pvOutput2.size();
		for (int i = 0; i < size; i++) {
			res.add(Float.parseFloat(pvOutput2.get(i)));
		}
		return res;
	}

	/**
	 * This method implements the sliding window technique.
	 * 
	 * @param pvOutput arrayList input of pv-output-values from which the data is
	 *                 split to make it ready for neural net
	 * @param window   The window size
	 * @param X        Train X arrayList
	 * @param y        Train y arrayList
	 */
	public static void splitSequence(ArrayList<Float> pvOutput, //
			int window, //
			ArrayList<ArrayList<Float>> X, //
			ArrayList<Float> y) {
		for (int i = 0; i < pvOutput.size() - 1; i++) {
			int endIndex = i + window;
			int endIndexY = endIndex + 1;
			//System.out.println(i + " to " + endIndex + " is x and y is  " + endIndexY);
			if (endIndex > pvOutput.size() - window) {
				break;
			}
			y.add(pvOutput.get(endIndexY));
			X.add(new ArrayList<Float>(pvOutput.subList(i, endIndex)));
		}
	}

	/**
	 * This method create the training Data, this method uses the split sequence to
	 * implement sliding window technique
	 */
	public static void CreateTrainingData() {

		String baseDir = "src/main/resources/Data_test/november2018Train.csv";
		ArrayList<String> pvOutput = new ArrayList<String>();

		ArrayList<ArrayList<Float>> X = new ArrayList<ArrayList<Float>>();
		ArrayList<Float> y = new ArrayList<Float>();

		try (Reader reader = Files.newBufferedReader(Paths.get(baseDir)); @SuppressWarnings("deprecation")
		CSVReader csvReader = new CSVReader(reader, ',', '"', 1);) {
			// Reading Records One by One in a String array
			String[] nextRecord;

			while ((nextRecord = csvReader.readNext()) != null) {
				pvOutput.add(nextRecord[1]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		ArrayList<Float> intPvOut = parsingStringTofloat(pvOutput);

		ArrayList<Float> normalised = new ArrayList<Float>();

		// Normalising the Data
		// normalised = GetNormalisedData(intPvOut);

		splitSequence(intPvOut, window, X, y);

		int size = X.size();
		dataSet = new Data2[size];

		for (int i = 0; i < size - 1; i++) {
			dataSet[i] = new Data2(X.get(i), y.get(i));
		}
	}

	private static ArrayList<Float> GetNormalisedData(ArrayList<Float> intPvOut) {
		DescriptiveStatistics stats = new DescriptiveStatistics();
		ArrayList<Float> normalisedPvOutput = new ArrayList<Float>();

		for (int i = 0; i < intPvOut.size(); i++) {
			stats.addValue(intPvOut.get(i));
		}

		// Compute some statistics
		double mean = stats.getMean();
		double std = stats.getStandardDeviation();

		System.out.println("Mean of the Data is : " + mean);
		System.out.println("Standard Deviation is : " + std);

		for (int i = 0; i < intPvOut.size(); i++) {

			normalisedPvOutput.add((float) ((intPvOut.get(i) - mean) / std));
		}
		return normalisedPvOutput;

	}

	/**
	 * Forward propogation of the neural net,
	 * 
	 * @param inputs
	 */
	public static void forward(ArrayList<Float> inputs) {
		// First bring the inputs into the input layer layers[0]
		layers[0] = new Layer(inputs);

		for (int i = 1; i < layers.length; i++) {
			// System.out.println("Ith row" + i );
			for (int j = 0; j < layers[i].neurons.length; j++) {
				// System.out.println("Jth row" + j );
				float sum = 0;
				for (int k = 0; k < layers[i - 1].neurons.length; k++) {
					// System.out.println("Kth row" + k );
					sum += layers[i - 1].neurons[k].value * layers[i].neurons[j].weights[k];
				}
				// sum += layers[i].neurons[j].bias;
				layers[i].neurons[j].value = StatUtil.Sigmoid(sum);
			}
		}
	}

	/**
	 * Backward propogation
	 * 
	 * @param learning_rate
	 * @param tData
	 */
	public static void backward(float learning_rate, Data2 tData) {

		int number_layers = layers.length;
		int out_index = number_layers - 1;

		// Update the output layers
		// For each output
		for (int i = 0; i < layers[out_index].neurons.length; i++) {
			// and for each of their weights
			float output = layers[out_index].neurons[i].value;
			float target = tData.expectedOutput;// .get(i);
			float derivative = output - target;
			float delta = derivative * (output * (1 - output));
			layers[out_index].neurons[i].gradient = delta;
			for (int j = 0; j < layers[out_index].neurons[i].weights.length; j++) {
				float previous_output = layers[out_index - 1].neurons[j].value;
				float error = delta * previous_output;
				layers[out_index].neurons[i].cache_weights[j] = layers[out_index].neurons[i].weights[j]
						- learning_rate * error;
			}
		}

		// Update all the subsequent hidden layers
		for (int i = out_index - 1; i > 0; i--) {
			// For all neurons in that layers
			for (int j = 0; j < layers[i].neurons.length; j++) {
				float output = layers[i].neurons[j].value;
				float gradient_sum = sumGradient(j, i + 1);
				float delta = (gradient_sum) * (output * (1 - output));
				layers[i].neurons[j].gradient = delta;
				// And for all their weights
				for (int k = 0; k < layers[i].neurons[j].weights.length; k++) {
					float previous_output = layers[i - 1].neurons[k].value;
					float error = delta * previous_output;
					layers[i].neurons[j].cache_weights[k] = layers[i].neurons[j].weights[k] - learning_rate * error;
				}
			}
		}

		// Here we do another pass where we update all the weights
		for (int i = 0; i < layers.length; i++) {
			for (int j = 0; j < layers[i].neurons.length; j++) {
				layers[i].neurons[j].update_weight();
			}
		}

	}

	/**
	 * This function sums up all the gradient connecting a given neuron in a given
	 * layer
	 * 
	 * @param n_index
	 * @param l_index
	 * @return
	 */
	public static float sumGradient(int n_index, int l_index) {
		float gradient_sum = 0;
		Layer current_layer = layers[l_index];
		for (int i = 0; i < current_layer.neurons.length; i++) {
			Neuron current_neuron = current_layer.neurons[i];
			gradient_sum += current_neuron.weights[n_index] * current_neuron.gradient;
		}
		return gradient_sum;
	}

	/**
	 * This function is used to train being forward and backward.
	 * 
	 * @param training_iterations
	 * @param learning_rate
	 */
	public static void train(int training_iterations, float learning_rate) {
		for (int i = 0; i < training_iterations; i++) {
			for (int j = 0; j < trainSet.length - 1; j++) {
				forward(trainSet[j].actualInput);
				backward(learning_rate, trainSet[j]);
			}
		}
	}

//	public static void CreateTrainingData() {
//		/*
//		 * float[] input1 = new float[] {0, 0}; //Expect 0 here float[] input2 = new
//		 * float[] {0, 1}; //Expect 1 here float[] input3 = new float[] {1, 0}; //Expect
//		 * 1 here float[] input4 = new float[] {1, 1}; //Expect 0 here
//		 * 
//		 * float[] expectedOutput1 = new float[] {0}; float[] expectedOutput2 = new
//		 * float[] {1}; float[] expectedOutput3 = new float[] {1}; float[]
//		 * expectedOutput4 = new float[] {0};
//		 */
//
//		String baseDir = "src/main/resources/november2018Train.csv";
//		ArrayList<String> pvOutput = new ArrayList<String>();
//
//		ArrayList<ArrayList<Float>> X = new ArrayList<ArrayList<Float>>();
//		ArrayList<ArrayList<Float>> y = new ArrayList<ArrayList<Float>>();
//
//		try (Reader reader = Files.newBufferedReader(Paths.get(baseDir)); @SuppressWarnings("deprecation")
//		CSVReader csvReader = new CSVReader(reader, ',', '"', 1);) {
//			// Reading Records One by One in a String array
//			String[] nextRecord;
//
//			while ((nextRecord = csvReader.readNext()) != null) {
//				pvOutput.add(nextRecord[1]);
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		ArrayList<Float> intPvOut = parsingStringTofloat(pvOutput);
//
//		splitSequence(intPvOut, window, X, y);
//
//		int size = X.size();
//		// My changes (using an array for the data sets)
//		tDataSet = new TrainingData[size];
//
//		for (int i = 0; i < size - 1; i++) {
//			tDataSet[i] = new TrainingData(X.get(i), y.get(i));
//		}
//		/*
//		 * tDataSet[0] = new TrainingData(input1, expectedOutput1); tDataSet[1] = new
//		 * TrainingData(input2, expectedOutput2); tDataSet[2] = new TrainingData(input3,
//		 * expectedOutput3); tDataSet[3] = new TrainingData(input4, expectedOutput4);
//		 */       
//	}
}