package de.uniheidelberg.cl.softpro.sentimentclassification;
import java.io.*;
import java.util.*;

/**
 * This class provides evaluation methods for testing trained weight vectors on different test sets.
 */
public class Evaluation {
	//the following are the optimal parameters for training (learned in development)
	static String ep = "10";
	static String learningRate = "-2";
	static String topK = "5000";
		
	/**
	 * Tests all relevant parameter combinations for single task perceptron and saves resulting error rates to "SentimentClassification/results/testResults/SingleTrainTested"
	 * Interesting error rates are here:
	 * books on books, dvd on dvd, electronics on electronics, kitchen on kitchen, cat on cat, small on books, small on dvd, small on electronics, small on kitchen, all on books, all on dvd, all on electronics, all on kitchen, small on cat, all on cat
	 */
	public static void singleTest(){
				
		//set System.out to path of output file
		File file = new File("SentimentClassification/results/testResults/SingleTrainTested");
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
		
		//read trained weight vectors from files
		HashMap<String, Double> weightVectorSmall = Development.weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/ST_%s_%s_%s.wv","small.all", ep, learningRate)));
		HashMap<String, Double> weightVectorBooks =  Development.weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/ST_%s_%s_%s.wv","books", ep, learningRate)));
		HashMap<String, Double> weightVectorDvd =  Development.weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/ST_%s_%s_%s.wv","dvd", ep, learningRate)));
		HashMap<String, Double> weightVectorElectronics =  Development.weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/ST_%s_%s_%s.wv","electronics", ep, learningRate)));
		HashMap<String, Double> weightVectorKitchen =  Development.weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/ST_%s_%s_%s.wv","kitchen", ep, learningRate)));
		HashMap<String, Double> weightVectorAll =  Development.weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/ST_%s_%s_%s.wv","all", ep, learningRate)));
		
		//read test instances
		//ArrayList<Instance> testSetAll = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.test.corpus.final.formatted","all")));
		//ArrayList<Instance> testSetSmall = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.test.corpus.final.formatted","small.all")));
		ArrayList<Instance> testSetBooks = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.test.corpus.final.formatted","books")));
		ArrayList<Instance> testSetDvd = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.test.corpus.final.formatted","dvd")));
		ArrayList<Instance> testSetElectronics = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.test.corpus.final.formatted","electronics")));
		ArrayList<Instance> testSetKitchen = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.test.corpus.final.formatted","kitchen")));

		//create and test perceptrons for train and test pairs -> print these results
		//System.out.println("all on all:\t"+new Perceptron(weightVectorAll).test(testSetAll));
		//System.out.println("small on small:\t"+new Perceptron(weightVectorSmall).test(testSetSmall));
		double d1 = new Perceptron(weightVectorBooks).test(testSetBooks);
		System.out.println("books on books:\t"+d1);
		double d2 = new Perceptron(weightVectorDvd).test(testSetDvd);
		System.out.println("dvd on dvd:\t"+d2);
		double d3 = new Perceptron(weightVectorElectronics).test(testSetElectronics);
		System.out.println("electronics on electronics:\t"+d3);
		double d4 = new Perceptron(weightVectorKitchen).test(testSetKitchen);
		System.out.println("kitchen on kitchen:\t"+d4);
		double avgCatOnCat = (d1+d2+d3+d4)/4;
		System.out.println("cat on cat:\t"+avgCatOnCat+"\n");

		Perceptron q = new Perceptron(weightVectorSmall);
		Perceptron p = new Perceptron(weightVectorAll);
		
		double e1 = q.test(testSetBooks);
		double e2 = q.test(testSetDvd);
		double e3 = q.test(testSetElectronics);
		double e4 = q.test(testSetKitchen);
		
		double f1 = p.test(testSetBooks);
		double f2 = p.test(testSetDvd);
		double f3 = p.test(testSetElectronics);
		double f4 = p.test(testSetKitchen);
	
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

	
	/**
	 * Tests all relevant parameter combinations for multi task perceptron (shards = tasks) and saves resulting error rates to file "SentimentClassification/results/testResults/MultiTrainTested"
	 * Interesting error rates are here:
	 * small on books, small on dvd, small on electronics, small on kitchen, all on books, all on dvd, all on electronics, all on kitchen, small on cat, all on cat
	 */
	public static void multiTest(){
		
		//set System.out path to output file
		File file = new File("SentimentClassification/results/testResults/MultiTrainTested");
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
		
		//read trained weight vectors from files
		HashMap<String, Double> weightVectorSmall = Development.weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/MT_%s_%s_%s_%s.wv","small.all", ep, learningRate, topK)));
		
		if (weightVectorSmall == null){
			System.err.println(String.format("weight vector file could not be found: SentimentClassification/weightVectors/MT_%s_%s_%s_%s.wv","small.all", ep, learningRate, topK));
		}
		HashMap<String, Double> weightVectorAll = Development.weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/MT_%s_%s_%s_%s.wv","all", ep, learningRate, topK)));
		
		if (weightVectorAll == null){
			System.err.println(String.format("weight vector file could not be found: SentimentClassification/weightVectors/MT_%s_%s_%s_%s.wv","all", ep, learningRate, topK));
		}
		
		//read test instances
		//ArrayList<Instance> testSetAll = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.test.corpus.final.formatted","all")));
		//ArrayList<Instance> testSetSmall = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.test.corpus.final.formatted","small.all")));
		ArrayList<Instance> testSetBooks = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.test.corpus.final.formatted","books")));
		ArrayList<Instance> testSetDvd = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.test.corpus.final.formatted","dvd")));
		ArrayList<Instance> testSetElectronics = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.test.corpus.final.formatted","electronics")));
		ArrayList<Instance> testSetKitchen = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.test.corpus.final.formatted","kitchen")));

		//create and test perceptrons for train and test pairs -> print these results
		
		//all + small
		Perceptron q = new Perceptron(weightVectorSmall);
		Perceptron p = new Perceptron(weightVectorAll);
		
		double e1 = q.test(testSetBooks);
		double e2 = q.test(testSetDvd);
		double e3 = q.test(testSetElectronics);
		double e4 = q.test(testSetKitchen);
		
		double f1 = p.test(testSetBooks);
		double f2 = p.test(testSetDvd);
		double f3 = p.test(testSetElectronics);
		double f4 = p.test(testSetKitchen);
	
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

		
		/**
		 * Tests all relevant parameter combinations for multi task random perceptron and saves resulting error rates to file "SentimentClassification/results/testResults/MultiTrainRandomTested"
		 * Interesting error rates are here:
		 * small on books, small on dvd, small on electronics, small on kitchen, all on books, all on dvd, all on electronics, all on kitchen, small on cat, all on cat
		 */
	public static void multiRandomTest(){
	
		File file = new File("SentimentClassification/results/testResults/MultiTrainRandomTested");
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
			
		//read trained weight vectors from files
		HashMap<String, Double> weightVectorSmall = Development.weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/MTR_%s_%s_%s_%s.wv","small.all", ep, learningRate, topK)));
		
		if (weightVectorSmall == null){
			System.err.println(String.format("weight vector file could not be found: SentimentClassification/weightVectors/MTR_%s_%s_%s_%s.wv","small.all", ep, learningRate, topK));
		}
		HashMap<String, Double> weightVectorAll = Development.weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/MTR_%s_%s_%s_%s.wv","all", ep, learningRate, topK)));
		
		if (weightVectorAll == null){
			System.err.println(String.format("weight vector file could not be found: SentimentClassification/weightVectors/MTR_%s_%s_%s_%s.wv","all", ep, learningRate, topK));
		}
		//read test instances
		//ArrayList<Instance> testSetAll = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.test.corpus.final.formatted","all")));
		//ArrayList<Instance> testSetSmall = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.test.corpus.final.formatted","small.all")));
		ArrayList<Instance> testSetBooks = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.test.corpus.final.formatted","books")));
		ArrayList<Instance> testSetDvd = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.test.corpus.final.formatted","dvd")));
		ArrayList<Instance> testSetElectronics = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.test.corpus.final.formatted","electronics")));
		ArrayList<Instance> testSetKitchen = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.test.corpus.final.formatted","kitchen")));

		//create and test perceptrons for train and test pairs -> print these results
		
		//all + small
		Perceptron q = new Perceptron(weightVectorSmall);
		Perceptron p = new Perceptron(weightVectorAll);
		
		double e1 = q.test(testSetBooks);
		double e2 = q.test(testSetDvd);
		double e3 = q.test(testSetElectronics);
		double e4 = q.test(testSetKitchen);
		
		double f1 = p.test(testSetBooks);
		double f2 = p.test(testSetDvd);
		double f3 = p.test(testSetElectronics);
		double f4 = p.test(testSetKitchen);
	
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
	
	/**
	 * This methods allows to test trained weight vectors on unseen testsets. 
	 * Results (error rates) can be found in a file in the "testResults" directory.
	 * Train-Test pairs of weight vectors and test sets must be defined within this methods.
	 * @param category is the testset's name. It should be written in the beginning of each line of corpus. This parameter also sets the name of the output file.
	 */
	public static void testUnseen(String category){
		File file = new File("SentimentClassification/results/testResults/"+category);
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
		
		ArrayList<Instance> testSetCat = CreateInstances.createInstancesFromFileNewFormat(new File("SentimentClassification/makeCorpus/"+category+".test.formatted"));
		
		HashMap<String, Double> weightVectorBooks = Development.weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/ST_%s_%s_%s.wv","books", "10", "-2")));
		double e4 = new Perceptron(weightVectorBooks).test(testSetCat);
		System.out.println("ST, books (-2) on "+category+": "+e4);
		
		HashMap<String, Double> weightVectorElectronics = Development.weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/ST_%s_%s_%s.wv","electronics", "10", "-2")));
		double e5 = new Perceptron(weightVectorElectronics).test(testSetCat);
		System.out.println("ST, electronics (-2) on "+category+": "+e5);
		
		HashMap<String, Double> weightVectorDvd = Development.weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/ST_%s_%s_%s.wv","dvd", "10", "-2")));
		double e6 = new Perceptron(weightVectorDvd).test(testSetCat);
		System.out.println("ST, dvd (-2) on "+category+": "+e6);
		
		HashMap<String, Double> weightVectorKitchen = Development.weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/ST_%s_%s_%s.wv","kitchen", "10", "-2")));
		double e7 = new Perceptron(weightVectorKitchen).test(testSetCat);
		System.out.println("ST, kitchen (-2) on "+category+": "+e7);
		
		HashMap<String, Double> weightVectorAllST = Development.weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/ST_%s_%s_%s.wv","all", "10", "-2")));
		double e8 = new Perceptron(weightVectorAllST).test(testSetCat);
		System.out.println("ST, all (-2) on "+category+": "+e8);
		
		HashMap<String, Double> weightVectorSmallST = Development.weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/ST_%s_%s_%s.wv","small.all", "10", "-2")));
		double e2 = new Perceptron(weightVectorSmallST).test(testSetCat);
		System.out.println("ST, small (-2) on "+category+": "+e2);
		
		HashMap<String, Double> weightVectorSmallMT = Development.weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/MT_%s_%s_%s_%s.wv","small.all", "10", "-2", "5000")));
		double e1 = new Perceptron(weightVectorSmallMT).test(testSetCat);
		System.out.println("MT, small (-2 top 5000) on "+category+": "+e1);
		
		HashMap<String, Double> weightVectorAllMT = Development.weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/MT_%s_%s_%s_%s.wv","all", "10", "-2", "5000")));
		double f1 = new Perceptron(weightVectorAllMT).test(testSetCat);
		System.out.println("MT, all (-2 top 5000) on "+category+": "+f1);
		
		HashMap<String, Double> weightVectorSmallMTR = Development.weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/MTR_%s_%s_%s_%s.wv","small.all", "10", "-2", "5000")));
		double e3 = new Perceptron(weightVectorSmallMTR).test(testSetCat);
		System.out.println("MTR, small (-2 top 5000) on "+category+": "+e3);

		HashMap<String, Double> weightVectorAllMTR = Development.weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/MTR_%s_%s_%s_%s.wv","all", "10", "-2", "5000")));
		double f3 = new Perceptron(weightVectorAllMTR).test(testSetCat);
		System.out.println("MTR, all (-2 top 5000) on "+category+": "+f3);
	}
	
	/**
	 * Here you can define which tests will be performed when running main.
	 * @param args
	 */
	public static void main(String[] args){
		singleTest();
		multiTest();
		multiRandomTest();
		testUnseen("snacks");
		testUnseen("outdoor");
		testUnseen("gardening");
	}	
}
