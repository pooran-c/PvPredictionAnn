package example.project;

import java.util.ArrayList;

public class TrainingData {
 
    ArrayList<Float> data;
    ArrayList<Float> expectedOutput;
   
    public TrainingData(ArrayList<Float> arrayList, ArrayList<Float> expectedOutput) {
        this.data = arrayList;
        this.expectedOutput = expectedOutput;
    }
   
}