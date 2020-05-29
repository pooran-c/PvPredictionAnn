package example.project;

import java.util.ArrayList;

public class Data2 {

	public ArrayList<Float> actualInput;
	public Float expectedOutput;

	public Data2(ArrayList<Float> actualInput, Float expectedOutput) {
		this.actualInput = actualInput;
		this.expectedOutput = expectedOutput;
	}

	public ArrayList<Float> getActualInput() {
		return actualInput;
	}

	public void setActualInput(ArrayList<Float> actualInput) {
		this.actualInput = actualInput;
	}

	public Float getExpectedOutput() {
		return expectedOutput;
	}

	public void setExpectedOutput(Float expectedOutput) {
		this.expectedOutput = expectedOutput;
	}
	
	

}