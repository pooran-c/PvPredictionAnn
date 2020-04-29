package example.project;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import com.opencsv.CSVReader;

public class NeuralNetwork {

	// Variable Declaration

	// Layers
	static Layer[] layers; 

	// Training data
	static TrainingData[] tDataSet; 

	// Main Method
	public static void main(String[] args) {

		// Set the Min and Max weight value for all Neurons
		Neuron.setRangeWeight(-1, 1);

		// Create the layers

		layers = new Layer[3];
		layers[0] = null; // Input Layer 0,5
		layers[1] = new Layer(5, 6); // Hidden Layer 6,6
		layers[2] = new Layer(6, 1); // Output Layer 6,1

		// Create the training data
		CreateTrainingData();

		System.out.println("============");
		System.out.println("Output before training");
		System.out.println("============");
		for (int i = 0; i < tDataSet.length - 1; i++) {
			forward(tDataSet[i].data);
			// System.out.println(layers[2].neurons[0].value);
		}

		train(1000000, 0.05f);

		System.out.println("============");
		System.out.println("Output after training");
		System.out.println("============");
		for (int i = 0; i < tDataSet.length - 1; i++) {
			forward(tDataSet[i].data);
			System.out.println(layers[2].neurons[0].value);
		}
	}

	private static ArrayList<Float> parsingStringTofloat(ArrayList<String> pvOutput2) {
		ArrayList<Float> res = new ArrayList<Float>();
		int size = pvOutput2.size();
		for (int i = 0; i < size; i++) {
			res.add(Float.parseFloat(pvOutput2.get(i)));
		}
		return res;
	}

	public static void splitSequence(ArrayList<Float> pvOutput, int window, ArrayList<ArrayList<Float>> X,
			ArrayList<Float> y) {
		for (int i = 0; i < pvOutput.size() - 1; i++) {
			int endIndex = i + window;
			if (endIndex > pvOutput.size() - 1) {
				break;
			}
			y.add(pvOutput.get(endIndex));
			X.add(new ArrayList<Float>(pvOutput.subList(i, endIndex)));
		}
	}

	public static void CreateTrainingData() {
//        float[] input1 = new float[] {0, 0}; //Expect 0 here
//        float[] input2 = new float[] {0, 1}; //Expect 1 here
//        float[] input3 = new float[] {1, 0}; //Expect 1 here
//        float[] input4 = new float[] {1, 1}; //Expect 0 here
//       
//        float[] expectedOutput1 = new float[] {0};
//        float[] expectedOutput2 = new float[] {1};
//        float[] expectedOutput3 = new float[] {1};
//        float[] expectedOutput4 = new float[] {0};

		//String baseDir = "src/main/resources/Book1.csv";
		String baseDir = "src/main/resources/FEMS494_Train_28_10_2018__16_10_2019_1.csv";
		ArrayList<String> pvOutput = new ArrayList<String>();

		ArrayList<ArrayList<Float>> X = new ArrayList<ArrayList<Float>>();
		ArrayList<Float> y = new ArrayList<Float>();

		try (Reader reader = Files.newBufferedReader(Paths.get(baseDir)); @SuppressWarnings("deprecation")
		CSVReader csvReader = new CSVReader(reader, ',', '"', 1);) {
			// Reading Records One by One in a String array
			String[] nextRecord;

			while ((nextRecord = csvReader.readNext()) != null) {
				pvOutput.add(nextRecord[17]);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<Float> intPvOut = parsingStringTofloat(pvOutput);

		int window = 5;
		splitSequence(intPvOut, window, X, y);

		int size = X.size();
		// My changes (using an array for the data sets)
		tDataSet = new TrainingData[size];

		System.out.println("Actual input  " + X);
		System.out.println("Actual output  " + y);

		for (int i = 0; i < size - 1; i++) {
			tDataSet[i] = new TrainingData(X.get(i), new ArrayList<Float>(Arrays.asList(y.get(i))));
		}
//        tDataSet[0] = new TrainingData(input1, expectedOutput1);
//        tDataSet[1] = new TrainingData(input2, expectedOutput2);
//        tDataSet[2] = new TrainingData(input3, expectedOutput3);
//        tDataSet[3] = new TrainingData(input4, expectedOutput4);        
	}

	public static void forward(ArrayList<Float> inputs) {
		// First bring the inputs into the input layer layers[0]
		layers[0] = new Layer(inputs);

		for (int i = 1; i < layers.length; i++) {
			for (int j = 0; j < layers[i].neurons.length; j++) {
				float sum = 0;
				for (int k = 0; k < layers[i - 1].neurons.length; k++) {
					sum += layers[i - 1].neurons[k].value * layers[i].neurons[j].weights[k];
				}
				// sum += layers[i].neurons[j].bias; // TODO add in the bias
				layers[i].neurons[j].value = StatUtil.Sigmoid(sum);
			}
		}
	}

//    public static void forward(Float[] inputs) {
//    	// First bring the inputs into the input layer layers[0]
//    	layers[0] = new Layer(inputs);
//    	
//        for(int i = 1; i < layers.length; i++) {
//        	for(int j = 0; j < layers[i].neurons.length; j++) {
//        		float sum = 0;
//        		for(int k = 0; k < layers[i-1].neurons.length; k++) {
//        			sum += layers[i-1].neurons[k].value*layers[i].neurons[j].weights[k];
//        		}
//        		//sum += layers[i].neurons[j].bias; // TODO add in the bias 
//        		layers[i].neurons[j].value = StatUtil.Sigmoid(sum);
//        	}
//        } 	
//    }

	// backward propogation
	public static void backward(float learning_rate, TrainingData tData) {

		int number_layers = layers.length;
		int out_index = number_layers - 1;

		// Update the output layers
		// For each output
		for (int i = 0; i < layers[out_index].neurons.length; i++) {
			// and for each of their weights
			float output = layers[out_index].neurons[i].value;
			float target = tData.expectedOutput.get(i);
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

	// This function sums up all the gradient connecting a given neuron in a given
	// layer
	public static float sumGradient(int n_index, int l_index) {
		float gradient_sum = 0;
		Layer current_layer = layers[l_index];
		for (int i = 0; i < current_layer.neurons.length; i++) {
			Neuron current_neuron = current_layer.neurons[i];
			gradient_sum += current_neuron.weights[n_index] * current_neuron.gradient;
		}
		return gradient_sum;
	}

	// This function is used to train being forward and backward.
	public static void train(int training_iterations, float learning_rate) {
		for (int i = 0; i < training_iterations; i++) {
			for (int j = 0; j < tDataSet.length - 1; j++) {
				forward(tDataSet[j].data);
				backward(learning_rate, tDataSet[j]);
			}
		}
	}
}