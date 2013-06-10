package src.de.uniheidelberg.cl.softpro.sentimentclassification;
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
		//weigthVector einlesen
		HashMap<String, Double> weightVector = weightVectorFromFile(new File (//Pfad));
		//Perceptron erstellen mit eingelesenem weigthVector
		Perceptron perceptronTest = new Perceptron(weightVector);
		//devset einlesen
		ArrayList<Instance> devset = CreateInstances.createInstancesFromFile(//Pfad);
		// errorRate berechnen mit Perceptron-Methode test
		double errorRate = perceptronTest.test(devset);
	}
}
