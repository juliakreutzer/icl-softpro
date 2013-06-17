package de.uniheidelberg.cl.softpro.sentimentclassification;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class for testing on development set -> optimization of parameters
 * @author jasmin & julia
 */
public class Development {
	static String[] epochs = {"1", "10", "100"};
	static String[] learningRates = {"exp", "dec", "1divt", "-3", "-2", "-1", "0", "1"}; //constants are exponents to power of 10 -> e.g. "0" => 1; "1" => 10
	static String[] setNames = {"all", "small", "books", "electronics", "dvd", "kitchen"};
	
	public static void singleTrain(){
		//trains all relevant parameter combinations and saves resulting weight vectors to files
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
					weightVector = Evaluation.weightVectorFromFile(vectorInputFile);
					}
					catch (Exception ex){
					    System.out.println("Caught Exception: " + ex.getMessage());
					    break;
					}
					//creates new Perceptron for given weight vector
					Perceptron p = new Perceptron(weightVector);
					
					for (String devSetName : setNames) {
										
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
	//	singleTrain();
		singleTest();
	}
	
}
