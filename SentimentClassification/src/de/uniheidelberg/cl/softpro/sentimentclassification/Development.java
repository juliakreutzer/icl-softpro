package de.uniheidelberg.cl.softpro.sentimentclassification;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;

/**
 * Class for testing on development set -> optimization of parameters
 * @author jasmin & julia
 */
public class Development {
	static String[] epochs = {"1", "10", "100"};
	static String[] learningRates = {"exp", "dec", "1divt", "-6", "-5", "-4", "-3", "-2", "-1", "0", "1"}; //constants are exponents to power of 10 -> e.g. "0" => 1; "1" => 10
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
	
	//tests all relevant parameter combinations and saves resulting error rates to files
	public static void singleTest(){
		
		for (String ep : epochs){
			
			//set System.out to file for epoch
			File file = new File("SentimentClassification/results/singleTaskDevResults/"+ep);
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				System.setOut(new PrintStream(new FileOutputStream(file)));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			System.out.println(ep+" epochs\n");
			
			for (String learningRate : learningRates){
				System.out.println("-----------------------------------");
				System.out.println("learning rate: "+learningRate+"\n");
				
				//these are the train-dev pairs we want to inspect:
				//all on all, small on small, books on books, dvd on dvd, electronics on electronics, kitchen on kitchen, cat on cat, all on cat, small on cat
				
				//read trained weight vectors from files
				HashMap<String, Double> weightVectorAll = weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/ST_%s_%s_%s.wv","all", ep, learningRate)));
				HashMap<String, Double> weightVectorSmall = weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/ST_%s_%s_%s.wv","small.all", ep, learningRate)));
				HashMap<String, Double> weightVectorBooks = weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/ST_%s_%s_%s.wv","books", ep, learningRate)));
				HashMap<String, Double> weightVectorDvd = weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/ST_%s_%s_%s.wv","dvd", ep, learningRate)));
				HashMap<String, Double> weightVectorElectronics = weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/ST_%s_%s_%s.wv","electronics", ep, learningRate)));
				HashMap<String, Double> weightVectorKitchen = weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/ST_%s_%s_%s.wv","kitchen", ep, learningRate)));

				//read dev instances
				ArrayList<Instance> devSetAll = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.dev.corpus.final.formatted","all")));
				ArrayList<Instance> devSetSmall = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.dev.corpus.final.formatted","small.all")));
				ArrayList<Instance> devSetBooks = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.dev.corpus.final.formatted","books")));
				ArrayList<Instance> devSetDvd = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.dev.corpus.final.formatted","dvd")));
				ArrayList<Instance> devSetElectronics = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.dev.corpus.final.formatted","electronics")));
				ArrayList<Instance> devSetKitchen = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.dev.corpus.final.formatted","kitchen")));

				//create and test perceptrons for train and dev pairs -> print these results
				System.out.println("all on all:\t"+new Perceptron(weightVectorAll).test(devSetAll));
				System.out.println("small on small:\t"+new Perceptron(weightVectorSmall).test(devSetSmall));
				double e1 = new Perceptron(weightVectorBooks).test(devSetBooks);
				System.out.println("books on books:\t"+e1);
				double e2 = new Perceptron(weightVectorDvd).test(devSetDvd);
				System.out.println("dvd on dvd:\t"+e2);
				double e3 = new Perceptron(weightVectorElectronics).test(devSetElectronics);
				System.out.println("electronics on electronics:\t"+e3);
				double e4 = new Perceptron(weightVectorKitchen).test(devSetKitchen);
				System.out.println("kitchen on kitchen:\t"+e4);
				double avgCatOnCat = (e1+e2+e3+e4)/4;
				System.out.println("cat on cat:\t"+avgCatOnCat);
				Perceptron p = new Perceptron(weightVectorAll);
				double f1 = p.test(devSetBooks);
				double f2 = p.test(devSetDvd);
				double f3 = p.test(devSetElectronics);
				double f4 = p.test(devSetKitchen);
				double avgAllOnCat = (f1+f2+f3+f4)/4;
				System.out.println("all on cat:\t"+avgAllOnCat);
				Perceptron q = new Perceptron(weightVectorSmall);
				double g1 = q.test(devSetBooks);
				double g2 = q.test(devSetDvd);
				double g3 = q.test(devSetElectronics);
				double g4 = q.test(devSetKitchen);
				double avgSmallOnCat = (g1+g2+g3+g4)/4;
				System.out.println("small on cat:\t"+avgSmallOnCat+"\n");
			}
		}
	}
	
	public static void main(String[] args){
	 	singleTrain();
	 	System.out.println("training completed, now start testing...");
		singleTest();
	}
	
}
