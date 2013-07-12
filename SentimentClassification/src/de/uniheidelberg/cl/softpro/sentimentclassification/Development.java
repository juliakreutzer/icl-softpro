package de.uniheidelberg.cl.softpro.sentimentclassification;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class for testing on development set for the optimization of parameters.
 * 
 * Here, the single task training, multi task testing and multi task random testing are performed.
 * Multi task (random) training needs to be done beforehand with hadoop.
 * 
 * Parameters that are included in test series are set in class variables.
 * 
 * The train-dev pairs have to be set directly in the test methods.
 * 
 * Results (measure: error rate) are saved into one file per epoch-setting in directory results.
 */
public class Development {
	static String[] epochs = {"1", "10", "100"}; 
	static String[] epochsMulti = {"1","10", "20", "30"}; //range of epochs for multi task testing
	static String[] learningRates = {"exp", "dec", "1divt", "-6", "-5", "-4", "-3", "-2", "-1", "0", "1"}; //constants are exponents to power of 10 -> e.g. "0" => 1; "1" => 10
	static String[] setNames = {"all", "small.all", "books", "electronics", "dvd", "kitchen"};
	static String[] topKs = {"10", "100", "1000", "2000", "5000", "10000", "50000"}; //only needed in multi task perceptron
	
	/**
	 * Reads a weight vector from a given file to a HashMap<String, Double>. 
	 * Weight vector format: feature1:count1 feature2:count2 feature3:count3 ...
	 * @param f File where weight vector is read from. Format: feature1:count1 feature2:count2 feature3:count3 ...
	 * @return a HashMap<String, Double> of feature-count-pairs representing the weight vector. If given file is empty or cannot be found, the HashMap is null.
	 */
	public static HashMap<String, Double> weightVectorFromFile(File f) {
		String line = new String();
		HashMap<String, Double> weightVector = new HashMap<String, Double>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			line = br.readLine();
			if (line==null){
				System.err.println("Weight vector file is empty: "+f.toString());
				return null;
			}
			br.close();
			String[] arrayWithFeatures = line.split(" ");
			for (String feature : arrayWithFeatures) {
				String[] featureAndValue = feature.split(":");
				String key = featureAndValue[0];
				Double value = Double.parseDouble(featureAndValue[1]);
				weightVector.put(key, value);
			}
		} catch (FileNotFoundException e) {
			System.err.println("Weight vector file not found: "+f.toString());
			weightVector = null;
		} catch (IOException e) {
			e.printStackTrace();
			weightVector = null;
		}
		
		return weightVector;
	}
		
	/**
	 * Trains all relevant parameter combinations and saves resulting weight vectors to files.
	 * Iterates for training over all given parameters (class variables).
	 * Corpus is read from directory "data/processed_acl/corpus_final_formatted"
	 * Weight vectors are saved in "weightVectors". Mind the naming conventions! (see readme)
	 */
	public static void singleTrain(){
		
		for (String name : setNames) {
			//read training set
			ArrayList<Instance> trainset = CreateInstances.createInstancesFromFileNewFormat(new File("SentimentClassification/data/processed_acl/corpus_final_formatted/"+name+".train.corpus.final.formatted"));
			if (trainset == null){
				System.err.println("Corpus file could not be read. Training continues with next given file.");
				continue;
			}
			//for each parameter setting, train a perceptron and save the trained weight vector
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
	
	/**
	 * Tests all relevant parameter combinations for single task perceptron and saves resulting error rates to files (to "results/singleTaskDevResults_ALL/Baselines/", one per epoch setting).
	 * dev-test pairs have to be defined within method.
	 * Weight vectors are read from directory "weightVectors". Mind the naming conventions! (see readme)
	 */
	public static void singleTest(){
		
		for (String ep : epochs){
			
			//set System.out to file for epoch
			File file = new File("SentimentClassification/results/singleTaskDevResults_ALL/Baselines/"+ep);
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("Out path could not be set.");
			}
			try {
				System.setOut(new PrintStream(new FileOutputStream(file)));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.err.println("Out path could not be set.");
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

				//if weight vector file could not be found or is empty
				if (weightVectorSmall == null || weightVectorAll == null || weightVectorBooks == null || weightVectorDvd == null || weightVectorElectronics == null || weightVectorKitchen == null ){
					continue;
				}
				
				
				//read dev instances
				//ArrayList<Instance> devSetAll = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.dev.corpus.final.formatted","all")));
				//ArrayList<Instance> devSetSmall = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.dev.corpus.final.formatted","small.all")));
				ArrayList<Instance> devSetBooks = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.dev.corpus.final.formatted","books")));
				ArrayList<Instance> devSetDvd = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.dev.corpus.final.formatted","dvd")));
				ArrayList<Instance> devSetElectronics = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.dev.corpus.final.formatted","electronics")));
				ArrayList<Instance> devSetKitchen = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.dev.corpus.final.formatted","kitchen")));

				//create and test perceptrons for train and dev pairs -> print these results
				//System.out.println("all on all:\t"+new Perceptron(weightVectorAll).test(devSetAll));
				//System.out.println("small on small:\t"+new Perceptron(weightVectorSmall).test(devSetSmall));
				double d1 = new Perceptron(weightVectorBooks).test(devSetBooks);
				System.out.println("books on books:\t"+d1);
				double d2 = new Perceptron(weightVectorDvd).test(devSetDvd);
				System.out.println("dvd on dvd:\t"+d2);
				double d3 = new Perceptron(weightVectorElectronics).test(devSetElectronics);
				System.out.println("electronics on electronics:\t"+d3);
				double d4 = new Perceptron(weightVectorKitchen).test(devSetKitchen);
				System.out.println("kitchen on kitchen:\t"+d4);
				double avgCatOnCat = (d1+d2+d3+d4)/4;
				System.out.println("cat on cat:\t"+avgCatOnCat+"\n");

				Perceptron q = new Perceptron(weightVectorSmall);
				Perceptron p = new Perceptron(weightVectorAll);
				
				double e1 = q.test(devSetBooks);
				double e2 = q.test(devSetDvd);
				double e3 = q.test(devSetElectronics);
				double e4 = q.test(devSetKitchen);
				
				double f1 = p.test(devSetBooks);
				double f2 = p.test(devSetDvd);
				double f3 = p.test(devSetElectronics);
				double f4 = p.test(devSetKitchen);
			
				double avgAllOnCat = (f1+f2+f3+f4)/4;
				double avgSmallOnCat = (e1+e2+e3+e4)/4;
				
				System.out.println("small on books:\t"+e1);
				System.out.println("small on dvd:\t"+e2);
				System.out.println("small on electronics:\t"+e3);
				System.out.println("small on kitchen:\t"+e4);
				System.out.println("all on books:\t"+f1);
				System.out.println("all on dvd:\t"+f2);
				System.out.println("all on electronics:\t"+f3);
				System.out.println("all on kitchen:\t"+f4);
				System.out.println("small on cat:\t"+avgSmallOnCat);
				System.out.println("all on cat:\t"+avgAllOnCat);
			}
		}
	}
	
	/**
	 * Tests all relevant parameter combinations for multi task perceptron with categories as shards and saves resulting error rates to files (to "/results/multiTaskDevResults_ALL/", one per epoch setting).
	 * dev-test pairs have to be defined within method.
	 * Weight vectors are read from directory "weightVectors". Mind the naming conventions! (see readme) 
	 */
		public static void multiTest(){
			
			for (String ep : epochsMulti){
				
				//set System.out to file for epoch
				File file = new File("SentimentClassification/results/multiTaskDevResults_ALL/"+ep);
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
					System.err.println("Out path could not be set.");
				}
				try {
					System.setOut(new PrintStream(new FileOutputStream(file)));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					System.err.println("Out path could not be set.");
				}
				
				System.out.println(ep+" epochs\n");
				
				for (String topK : topKs){
					System.out.println("-----------------------------------");
					System.out.println("top k: "+topK+"\n");
									
					for (String learningRate : learningRates){
						System.out.println("-----------------------------------");
						System.out.println("learning rate: "+learningRate+"\n");
						
						//these are the train-dev pairs we want to inspect:
						//small on books, small on dvd, small on electronics, small on kitchen, small on cat
						
						//read trained weight vectors from files
						HashMap<String, Double> weightVectorAll = weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/MT_%s_%s_%s_%s.wv","all", ep, learningRate, topK)));
						HashMap<String, Double> weightVectorSmall = weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/MT_%s_%s_%s_%s.wv","small.all", ep, learningRate, topK)));
						
						//if weight vector file could not be found or is empty
						if (weightVectorSmall == null || weightVectorAll == null){
							continue;
						}
						
						//read dev instances
						//ArrayList<Instance> devSetAll = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.dev.corpus.final.formatted","all")));
						//ArrayList<Instance> devSetSmall = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.dev.corpus.final.formatted","small.all")));
						ArrayList<Instance> devSetBooks = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.dev.corpus.final.formatted","books")));
						ArrayList<Instance> devSetDvd = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.dev.corpus.final.formatted","dvd")));
						ArrayList<Instance> devSetElectronics = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.dev.corpus.final.formatted","electronics")));
						ArrayList<Instance> devSetKitchen = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.dev.corpus.final.formatted","kitchen")));
	
						//create and test perceptrons for train and dev pairs -> print these results
						//System.out.println("all on all:\t"+new Perceptron(weightVectorAll).test(devSetAll));
						//System.out.println("small on small:\t"+new Perceptron(weightVectorSmall).test(devSetSmall));
						
						//all + small
						Perceptron q = new Perceptron(weightVectorSmall);
						Perceptron p = new Perceptron(weightVectorAll);
						
						double e1 = q.test(devSetBooks);
						double e2 = q.test(devSetDvd);
						double e3 = q.test(devSetElectronics);
						double e4 = q.test(devSetKitchen);
						
						double f1 = p.test(devSetBooks);
						double f2 = p.test(devSetDvd);
						double f3 = p.test(devSetElectronics);
						double f4 = p.test(devSetKitchen);
					
						double avgAllOnCat = (f1+f2+f3+f4)/4;
						double avgSmallOnCat = (e1+e2+e3+e4)/4;
						
						System.out.println("small on books:\t"+e1);
						System.out.println("small on dvd:\t"+e2);
						System.out.println("small on electronics:\t"+e3);
						System.out.println("small on kitchen:\t"+e4);
						System.out.println("all on books:\t"+f1);
						System.out.println("all on dvd:\t"+f2);
						System.out.println("all on electronics:\t"+f3);
						System.out.println("all on kitchen:\t"+f4);
						System.out.println("small on cat:\t"+avgSmallOnCat);
						System.out.println("all on cat:\t"+avgAllOnCat);
					}
				}
			}
		}
		
		/**
	    * Tests all relevant parameter combinations for multi task random sharded perceptron and saves resulting error rates to files (to "/results/multiTaskRandomDevResults_ALL/", one per epoch setting).
		* dev-test pairs have to be defined within method.
		* Weight vectors are read from directory "weightVectors". Mind the naming conventions! (see readme) 
		*/
				public static void multiRandomTest(){
					
					for (String ep : epochsMulti){
						
						//set System.out to file for epoch
						File file = new File("SentimentClassification/results/multiTaskRandomDevResults_ALL/"+ep);
						try {
							file.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
							System.err.println("Out path could not be set.");
						}
						try {
							System.setOut(new PrintStream(new FileOutputStream(file)));
						} catch (FileNotFoundException e) {
							e.printStackTrace();
							System.err.println("Out path could not be set.");
						}
						
						System.out.println(ep+" epochs\n");
						
						for (String topK : topKs){
							System.out.println("-----------------------------------");
							System.out.println("top k: "+topK+"\n");
											
							for (String learningRate : learningRates){
								System.out.println("-----------------------------------");
								System.out.println("learning rate: "+learningRate+"\n");
								
								//these are the train-dev pairs we want to inspect:
								//small on books, small on dvd, small on electronics, small on kitchen, small on cat
								
								//read trained weight vectors from files
								HashMap<String, Double> weightVectorAll = weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/MTR_%s_%s_%s_%s.wv","all", ep, learningRate, topK)));
								HashMap<String, Double> weightVectorSmall = weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/MTR_%s_%s_%s_%s.wv","small.all", ep, learningRate, topK)));
									
								//if weight vector file could not be found or is empty
								if (weightVectorSmall == null || weightVectorAll == null){
									continue;
								}
								
								//read dev instances
								//ArrayList<Instance> devSetAll = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.dev.corpus.final.formatted","all")));
								//ArrayList<Instance> devSetSmall = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.dev.corpus.final.formatted","small.all")));
								ArrayList<Instance> devSetBooks = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.dev.corpus.final.formatted","books")));
								ArrayList<Instance> devSetDvd = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.dev.corpus.final.formatted","dvd")));
								ArrayList<Instance> devSetElectronics = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.dev.corpus.final.formatted","electronics")));
								ArrayList<Instance> devSetKitchen = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.dev.corpus.final.formatted","kitchen")));
			
								
								//create and test perceptrons for train and dev pairs -> print these results
						
								//all + small
								Perceptron q = new Perceptron(weightVectorSmall);
								Perceptron p = new Perceptron(weightVectorAll);
								
								double e1 = q.test(devSetBooks);
								double e2 = q.test(devSetDvd);
								double e3 = q.test(devSetElectronics);
								double e4 = q.test(devSetKitchen);
								
								double f1 = p.test(devSetBooks);
								double f2 = p.test(devSetDvd);
								double f3 = p.test(devSetElectronics);
								double f4 = p.test(devSetKitchen);
							
								double avgAllOnCat = (f1+f2+f3+f4)/4;
								double avgSmallOnCat = (e1+e2+e3+e4)/4;
								
								System.out.println("small on books:\t"+e1);
								System.out.println("small on dvd:\t"+e2);
								System.out.println("small on electronics:\t"+e3);
								System.out.println("small on kitchen:\t"+e4);
								System.out.println("all on books:\t"+f1);
								System.out.println("all on dvd:\t"+f2);
								System.out.println("all on electronics:\t"+f3);
								System.out.println("all on kitchen:\t"+f4);
								System.out.println("small on cat:\t"+avgSmallOnCat);
								System.out.println("all on cat:\t"+avgAllOnCat);
							}
						}
					}
				}
	
	/**
	 * Here you call training and dev-testing methods.
	 * Please change output paths within methods if you want to avoid overwriting existing files when training or testing again.
	 * @param args not needed here.
	 */
	public static void main(String[] args){
		System.out.println("Start single task training.");
	 	singleTrain();
	 	System.out.println("Single task training completed, now start testing.");
	 	
		System.out.println("Start single task testing.");
	 	singleTest();
	 	
	 	System.out.println("Start multi task testing.");
		multiTest();
		
		System.out.println("Start multi task (random sharded) testing.");
		multiRandomTest();
		
		System.out.println("Training and all tests are completed.");
	}
	
}
