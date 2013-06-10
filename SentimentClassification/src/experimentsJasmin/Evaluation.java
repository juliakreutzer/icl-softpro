package src.experimentsJasmin;
import java.io.*;
import java.util.*;

public class Evaluation {

	// wandelt Datei mit Gewichtsvektor in eine HashMap<String, Double> um
	public static HashMap<String, Double> weightVectorFromFile(File f) {
		String line = new String();
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			line = br.readLine();
			br.close();
		} catch (FileNotFoundException e) {
			System.err.println("File not found");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		HashMap<String, Double> weightVector = new HashMap<String, Double>();
		String[] arrayWithFeatures = line.split(" ");
		for (String feature : arrayWithFeatures) {
			String[] featureAndValue = feature.split(":");
			String key = featureAndValue[0];
			Double value = Double.parseDouble(featureAndValue[1]);
			weightVector.put(key, value);
		}
		return weightVector;
	}
	
	public static void main(String[] args) {
		//System.out.println(weightVectorFromFile(new File("/home/jasmin/workspace/Test/src/testDatei.txt")));
		/*HashMap<String, Double> weightVector = weightVectorFromFile(new File ("/home/jasmin/workspace/Test/src/testDatei.txt"));
		Perceptron perceptronTest = new Perceptron(weightVector);
		ArrayList<Instance> testset = //.....
		perceptronTest.test(testset);*/
	}
}
