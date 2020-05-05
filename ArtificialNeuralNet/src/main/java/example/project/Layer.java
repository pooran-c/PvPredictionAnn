package example.project;

import java.util.ArrayList;

public class Layer {
	public Neuron[] neurons;

	// Constructor for the hidden and output layer
	public Layer(int inNeurons, int numberNeurons) {
		this.neurons = new Neuron[numberNeurons];

		for (int i = 0; i < numberNeurons; i++) {
			float[] weights = new float[inNeurons];
			for (int j = 0; j < inNeurons; j++) {
				weights[j] = StatUtil.RandomFloat(Neuron.minWeightValue, Neuron.maxWeightValue);
			}
			neurons[i] = new Neuron(weights, StatUtil.RandomFloat(0, 1));
		}
	}

	// Constructor for the input layer
	public Layer(Float[] inputs) {
		this.neurons = new Neuron[inputs.length];
		for (int i = 0; i < inputs.length; i++) {
			this.neurons[i] = new Neuron(inputs[i]);
		}
	}

	// Constructor for the input layer
	public Layer(ArrayList<Float> inputs) {
		this.neurons = new Neuron[inputs.size()];
		for (int i = 0; i < inputs.size(); i++) {
			this.neurons[i] = new Neuron(inputs.get(i));
		}
	}

}