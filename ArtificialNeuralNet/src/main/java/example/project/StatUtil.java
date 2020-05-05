package example.project;

import java.util.ArrayList;

public class StatUtil {
	
	// Get a random numbers between min and max
    public static float RandomFloat(float min, float max) {
        float a = (float) Math.random();
        float num = min + (float) Math.random() * (max - min);
        if(a < 0.5)
            return num;
        else
            return -num;
    }
    
    // Sigmoid function
    public static float Sigmoid(float x) {
        return (float) (1/(1+Math.pow(Math.E, -x)));
    }
    
    // Derivative of the sigmoid function
    public static float SigmoidDerivative(float x) {
        return Sigmoid(x)*(1-Sigmoid(x));
    }
    
    // Used for the backpropagation
    public static float squaredError(float output,float target) {
    	return (float) (0.5*Math.pow(2,(target-output)));
    }
    
    // Used to calculate the overall error rate (not yet used)
    public static float sumSquaredError(float[] outputs,float[] targets) {
    	float sum = 0;
    	for(int i=0;i<outputs.length;i++) {
    		sum += squaredError(outputs[i],targets[i]);
    	}
    	return sum;
    }
    
    public static double rmse(ArrayList<Float> truth, ArrayList<Float> prediction) {
    	if (truth.size() != prediction.size()) {
            throw new IllegalArgumentException(String.format("The vector sizes don't match: %d != %d.", truth.size(), prediction.size()));
        }
    	
    	int n = truth.size();
    	double rss = 0.0;
    	for (int i = 0; i < n; i++) {
    		rss += Math.pow(truth.get(i) - prediction.get(i), 2);
    	}
    	
    	return Math.sqrt(rss/n);
    }
}