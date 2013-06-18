package src.de.uniheidelberg.cl.softpro.sentimentclassification;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class for testing on development set -> optimization of parameters
 * @author jasmin & julia
 */
public class Development {
	static String[] epochs = {"1", "10", "100"};
	static String[] learningRates = {"exp", "dec", "1divt", "-3", "-2", "-1", "0", "1"}; //constants are exponents to power of 10 -> e.g. "0" => 1; "1" => 10
	static String[] setNames = {"all", "small.all", "books", "electronics", "dvd", "kitchen"};
	
	// wandelt Datei mit Gewichtsvektor (Format: feature:count feature:count ...) in eine HashMap<String, Double> um
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
		
	//trains all relevant parameter combinations and saves resulting weight vectors to files
	public static void singleTrain(){
		
		//Training und weightVectorFiles erstellen
		for (String name : setNames) {
			ArrayList<Instance> trainset = CreateInstances.createInstancesFromFileNewFormat(new File("SentimentClassification/data/processed_acl/corpus_final_formatted/"+name+".train.corpus.final.formatted"));
			for (String epoch : epochs) {
				int epochInt = Integer.parseInt(epoch);
				for (String learningRate : learningRates) {
					
					Perceptron p = new Perceptron(epochInt, learningRate);
					p.trainSingle(trainset);
					p.writeWeightsToFile(new File("SentimentClassification/weightVectors/ST_"+name+"_"+epoch+"_"+learningRate+".wv"));
				}
			}
		}
	}
	
	public static void singleTest(){
		//tests all relevant parameter combinations and saves resulting error rates to files

		for (String e : epochs){
			for (String learningRate : learningRates){
				for (String trainSetName : setNames){
					
					//File vectorInputFile = new File(String.format("SentimentClassification/weightVectors/ST_%s_%s_%s.wv",trainSetName, e, learningRate));
					File vectorInputFile = new File("SentimentClassification/results/ST_All_10_0.0001.wv");
					//reads weight vector from file 
					HashMap<String, Double> weightVector = null;
					try{
					weightVector = weightVectorFromFile(vectorInputFile);
					}
					catch (Exception ex){
					    System.out.println("Caught Exception: " + ex.getMessage());
					    break;
					}
					//creates new Perceptron for given weight vector
					Perceptron p = new Perceptron(weightVector);
					
					for (String devSetName : setNames) {
						//reads devset from file				
						File devSetFile = new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.dev.corpus.final.formatted",devSetName));
						
						//loads reviews of development set
						ArrayList<Instance> devSet = CreateInstances.createInstancesFromFile(devSetFile);
					
						//tests Perceptron with trained weights on devset
						double errorRate = p.test(devSet);
						
						//prints errorRate for current setting
						System.out.println(String.format("%s on %s:\t%f", trainSetName, devSetName, errorRate));
					}
				}
			}	
		File resultsFile = new File("SentimentClassification/results/singleTaskResults/"+e);
		}	
		//reads devset from file
	//	String outFileName = String.format("SentimentClassification/results/singleTaskResults/ST_%s_%s_%s.wv",trainSetName, e, learningRate);	

		//reads weight vector from file
		//prints error rates into files
	}
	
	public static void main(String[] args){
		singleTrain();
	//	singleTest();
	}
	
}
