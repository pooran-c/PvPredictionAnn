package eacmple.project;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import com.opencsv.CSVReader;






public class SameAsYesterdayModel {
	
	/**
	 * The Window size for the sliding window
	 */
	static int window = 24;
	
	/**
	 * The Training data
	 */
	static Data[] dataSet;

	static Data[] trainSet;
	static Data[] testSet;
	
	static PredictedData[] predictedSet;
	
	public static void main(String[] args) throws IOException {
		
		CreateTrainingData();
		int trainRatio = 70;
		trainTestSplit(dataSet, trainRatio);
		
	}

	private static void CreateTrainingData() {
		String baseDir = "src/main/resources/Data_test/november2018Train.csv";
		ArrayList<String> pvOutput = new ArrayList<String>();

		ArrayList<ArrayList<Float>> X = new ArrayList<ArrayList<Float>>();
		ArrayList<ArrayList<Float>> y = new ArrayList<ArrayList<Float>>();

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

		splitSequence(intPvOut, window, X, y);

		int size = X.size();
		dataSet = new Data[size];

		for (int i = 0; i < size - 1; i++) {
			dataSet[i] = new Data(X.get(i), y.get(i));
		}
	}

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
			ArrayList<ArrayList<Float>> y) {
		for (int i = 0; i < pvOutput.size() - 1; i++) {
			int endIndex = i + window;
			int endIndexY = endIndex + window;
			if (endIndex > pvOutput.size() - window) {
				break;
			}
			y.add(new ArrayList<Float>(pvOutput.subList(endIndex, endIndexY)));
			X.add(new ArrayList<Float>(pvOutput.subList(i, endIndex)));
		}
	}
	
	private static void trainTestSplit(Data[] dataSet, float trainRatio) {
		float totalRecords = dataSet.length;

		float splitIndex =  (float) Math.floor(totalRecords * (trainRatio / 100));
		System.out.println("total records " + totalRecords + " is split into 0 index to " + (int) splitIndex +  
				" index as train dataset , and  " +(int) (splitIndex + 1 ) + " index to " + (int)(totalRecords - 1)  + " index as test dataset");

		float nextIndex = totalRecords - splitIndex;

		trainSet = new Data[(int)(splitIndex)];
		testSet = new Data[(int)(nextIndex)];

		trainSet = Arrays.copyOfRange(dataSet, 0, (int) splitIndex);
		testSet = Arrays.copyOfRange(dataSet, (int) (splitIndex + 1),(int) (totalRecords - 1));

	}
}
