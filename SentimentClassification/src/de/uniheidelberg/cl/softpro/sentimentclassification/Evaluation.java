package src.de.uniheidelberg.cl.softpro.sentimentclassification;
import java.io.*;
import java.util.*;

public class Evaluation {
	static String ep = "10";
	static String learningRate = "-2";
	static String topK = "5000";
		
	/**
	 * tests all relevant parameter combinations for single task perceptron and saves resulting error rates to files
	 */
	public static void singleTest(){
				
		//set System.out to file for epoch
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
		
		
		//these are the train-test pairs we want to inspect:
		//books on books, dvd on dvd, electronics on electronics, kitchen on kitchen, cat on cat (avg), small on books, small on dvd, small on electronics, small on kitchen, small on cat (avg)
		
		//read trained weight vectors from files
		//HashMap<String, Double> weightVectorAll = weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/ST_%s_%s_%s.wv","all", ep, learningRate)));
		HashMap<String, Double> weightVectorSmall = Development.weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/ST_%s_%s_%s.wv","small.all", ep, learningRate)));
		HashMap<String, Double> weightVectorBooks =  Development.weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/ST_%s_%s_%s.wv","books", ep, learningRate)));
		HashMap<String, Double> weightVectorDvd =  Development.weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/ST_%s_%s_%s.wv","dvd", ep, learningRate)));
		HashMap<String, Double> weightVectorElectronics =  Development.weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/ST_%s_%s_%s.wv","electronics", ep, learningRate)));
		HashMap<String, Double> weightVectorKitchen =  Development.weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/ST_%s_%s_%s.wv","kitchen", ep, learningRate)));
		HashMap<String, Double> weightVectorAll =  Development.weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/ST_%s_%s_%s.wv","all", ep, learningRate)));
		
		//read test instances
		//ArrayList<Instance> devSetAll = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.dev.corpus.final.formatted","all")));
		//ArrayList<Instance> devSetSmall = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.dev.corpus.final.formatted","small.all")));
		ArrayList<Instance> testSetBooks = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.test.corpus.final.formatted","books")));
		ArrayList<Instance> testSetDvd = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.test.corpus.final.formatted","dvd")));
		ArrayList<Instance> testSetElectronics = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.test.corpus.final.formatted","electronics")));
		ArrayList<Instance> testSetKitchen = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.test.corpus.final.formatted","kitchen")));

		//create and test perceptrons for train and dev pairs -> print these results
		//System.out.println("all on all:\t"+new Perceptron(weightVectorAll).test(devSetAll));
		//System.out.println("small on small:\t"+new Perceptron(weightVectorSmall).test(devSetSmall));
		double e1 = new Perceptron(weightVectorBooks).test(testSetBooks);
		System.out.println("books on books:\t"+e1);
		double e2 = new Perceptron(weightVectorDvd).test(testSetDvd);
		System.out.println("dvd on dvd:\t"+e2);
		double e3 = new Perceptron(weightVectorElectronics).test(testSetElectronics);
		System.out.println("electronics on electronics:\t"+e3);
		double e4 = new Perceptron(weightVectorKitchen).test(testSetKitchen);
		System.out.println("kitchen on kitchen:\t"+e4);
		double avgCatOnCat = (e1+e2+e3+e4)/4;
		System.out.println("cat on cat:\t"+avgCatOnCat);
		//Perceptron p = new Perceptron(weightVectorAll);
		//double f1 = p.test(devSetBooks);
		//double f2 = p.test(devSetDvd);
		//double f3 = p.test(devSetElectronics);
		//double f4 = p.test(devSetKitchen);
		//double avgAllOnCat = (f1+f2+f3+f4)/4;
		//System.out.println("all on cat:\t"+avgAllOnCat);
		Perceptron q = new Perceptron(weightVectorSmall);
		double g1 = q.test(testSetBooks);
		System.out.println("small on books:\t"+g1);
		double g2 = q.test(testSetDvd);
		System.out.println("small on dvd:\t"+g2);
		double g3 = q.test(testSetElectronics);
		System.out.println("small on electronics:\t"+g3);
		double g4 = q.test(testSetKitchen);
		System.out.println("small on kitchen:\t"+g4);
		double avgSmallOnCat = (g1+g2+g3+g4)/4;
		System.out.println("small on cat:\t"+avgSmallOnCat+"\n");
		
		Perceptron r = new Perceptron(weightVectorAll);
		double h1 = r.test(testSetBooks);
		System.out.println("all on books:\t"+h1);
		double h2 = r.test(testSetDvd);
		System.out.println("all on dvd:\t"+h2);
		double h3 = r.test(testSetElectronics);
		System.out.println("all on electronics:\t"+h3);
		double h4 = r.test(testSetKitchen);
		System.out.println("all on kitchen:\t"+h4);
		double avgAllOnCat = (h1+h2+h3+h4)/4;
		System.out.println("all on cat:\t"+avgAllOnCat+"\n");
	}

	
	/**
	 * tests all relevant parameter combinations for multi task perceptron with categories as shards and saves resulting error rates to files
	 */
	public static void multiTest(){
	
		
		//set System.out to file for epoch
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
				
		//these are the train-dev pairs we want to inspect:
		//small on books, small on dvd, small on electronics, small on kitchen, small on cat
		
		//read trained weight vectors from files
		
		HashMap<String, Double> weightVectorSmall = Development.weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/MT_%s_%s_%s_%s.wv","small.all", ep, learningRate, topK)));
		
		if (weightVectorSmall == null){
			System.err.println(String.format("weight vector file could not be found: SentimentClassification/weightVectors/MT_%s_%s_%s_%s.wv","small.all", ep, learningRate, topK));
		}
		HashMap<String, Double> weightVectorAll = Development.weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/MT_%s_%s_%s_%s.wv","all", ep, learningRate, topK)));
		
		if (weightVectorAll == null){
			System.err.println(String.format("weight vector file could not be found: SentimentClassification/weightVectors/MT_%s_%s_%s_%s.wv","all", ep, learningRate, topK));
		}
		
		//read dev instances
		//ArrayList<Instance> devSetAll = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.dev.corpus.final.formatted","all")));
		//ArrayList<Instance> devSetSmall = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.dev.corpus.final.formatted","small.all")));
		ArrayList<Instance> testSetBooks = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.test.corpus.final.formatted","books")));
		ArrayList<Instance> testSetDvd = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.test.corpus.final.formatted","dvd")));
		ArrayList<Instance> testSetElectronics = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.test.corpus.final.formatted","electronics")));
		ArrayList<Instance> testSetKitchen = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.test.corpus.final.formatted","kitchen")));

		//create and test perceptrons for train and dev pairs -> print these results
		//System.out.println("all on all:\t"+new Perceptron(weightVectorAll).test(devSetAll));
		//System.out.println("small on small:\t"+new Perceptron(weightVectorSmall).test(devSetSmall));
		double e1 = new Perceptron(weightVectorSmall).test(testSetBooks);
		System.out.println("small on books:\t"+e1);
		double e2 = new Perceptron(weightVectorSmall).test(testSetDvd);
		System.out.println("small on dvd:\t"+e2);
		double e3 = new Perceptron(weightVectorSmall).test(testSetElectronics);
		System.out.println("small on electronics:\t"+e3);
		double e4 = new Perceptron(weightVectorSmall).test(testSetKitchen);
		System.out.println("small on kitchen:\t"+e4);
		double avgSmallOnCat = (e1+e2+e3+e4)/4;
		System.out.println("small on cat:\t"+avgSmallOnCat);
		
		double f1 = new Perceptron(weightVectorAll).test(testSetBooks);
		System.out.println("all on books:\t"+f1);
		double f2 = new Perceptron(weightVectorAll).test(testSetDvd);
		System.out.println("all on dvd:\t"+f2);
		double f3 = new Perceptron(weightVectorAll).test(testSetElectronics);
		System.out.println("all on electronics:\t"+f3);
		double f4 = new Perceptron(weightVectorAll).test(testSetKitchen);
		System.out.println("all on kitchen:\t"+f4);
		double avgAllOnCat = (f1+f2+f3+f4)/4;
		System.out.println("all on cat:\t"+avgAllOnCat);
	}

		
		/**
		 * tests all relevant parameter combinations for multi task random perceptron and saves resulting error rates to files
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
				
		//these are the train-dev pairs we want to inspect:
		//small on books, small on dvd, small on electronics, small on kitchen, small on cat and everything with all
		
		//read trained weight vectors from files
		//HashMap<String, Double> weightVectorAll = weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/MT_%s_%s_%s_%s.wv","all", ep, learningRate, topK)));
		HashMap<String, Double> weightVectorSmall = Development.weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/MTR_%s_%s_%s_%s.wv","small.all", ep, learningRate, topK)));
		
		if (weightVectorSmall == null){
			System.err.println(String.format("weight vector file could not be found: SentimentClassification/weightVectors/MTR_%s_%s_%s_%s.wv","small.all", ep, learningRate, topK));
		}
		HashMap<String, Double> weightVectorAll = Development.weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/MTR_%s_%s_%s_%s.wv","all", ep, learningRate, topK)));
		
		if (weightVectorAll == null){
			System.err.println(String.format("weight vector file could not be found: SentimentClassification/weightVectors/MTR_%s_%s_%s_%s.wv","all", ep, learningRate, topK));
		}
		//read dev instances
		//ArrayList<Instance> devSetAll = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.dev.corpus.final.formatted","all")));
		//ArrayList<Instance> devSetSmall = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.dev.corpus.final.formatted","small.all")));
		ArrayList<Instance> testSetBooks = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.test.corpus.final.formatted","books")));
		ArrayList<Instance> testSetDvd = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.test.corpus.final.formatted","dvd")));
		ArrayList<Instance> testSetElectronics = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.test.corpus.final.formatted","electronics")));
		ArrayList<Instance> testSetKitchen = CreateInstances.createInstancesFromFileNewFormat(new File(String.format("SentimentClassification/data/processed_acl/corpus_final_formatted/%s.test.corpus.final.formatted","kitchen")));

		//create and test perceptrons for train and dev pairs -> print these results
		//System.out.println("all on all:\t"+new Perceptron(weightVectorAll).test(devSetAll));
		//System.out.println("small on small:\t"+new Perceptron(weightVectorSmall).test(devSetSmall));
		double e1 = new Perceptron(weightVectorSmall).test(testSetBooks);
		System.out.println("small on books:\t"+e1);
		double e2 = new Perceptron(weightVectorSmall).test(testSetDvd);
		System.out.println("small on dvd:\t"+e2);
		double e3 = new Perceptron(weightVectorSmall).test(testSetElectronics);
		System.out.println("small on electronics:\t"+e3);
		double e4 = new Perceptron(weightVectorSmall).test(testSetKitchen);
		System.out.println("small on kitchen:\t"+e4);
		double avgSmallOnCat = (e1+e2+e3+e4)/4;
		System.out.println("small on cat:\t"+avgSmallOnCat);
		
		double f1 = new Perceptron(weightVectorAll).test(testSetBooks);
		System.out.println("all on books:\t"+f1);
		double f2 = new Perceptron(weightVectorAll).test(testSetDvd);
		System.out.println("all on dvd:\t"+f2);
		double f3 = new Perceptron(weightVectorAll).test(testSetElectronics);
		System.out.println("all on electronics:\t"+f3);
		double f4 = new Perceptron(weightVectorAll).test(testSetKitchen);
		System.out.println("all on kitchen:\t"+f4);
		double avgAllOnCat = (f1+f2+f3+f4)/4;
		System.out.println("all on cat:\t"+avgAllOnCat);
	}

	public static void testSnacks(){
		File file = new File("SentimentClassification/results/testResults/Snacks");
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
		
		ArrayList<Instance> testSetSnacks = CreateInstances.createInstancesFromFileNewFormat(new File("SentimentClassification/makeCorpus/4snacks.test.formatted"));
		
		HashMap<String, Double> weightVectorSmallST = Development.weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/ST_%s_%s_%s.wv","small.all", "10", "-2")));
		double e2 = new Perceptron(weightVectorSmallST).test(testSetSnacks);
		System.out.println("ST, small (-2) on snacks: "+e2);

		HashMap<String, Double> weightVectorSmallMTR = Development.weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/MTR_%s_%s_%s_%s.wv","small.all", "10", "-2", "5000")));
		double e3 = new Perceptron(weightVectorSmallMTR).test(testSetSnacks);
		System.out.println("MTR, small (-2 top 5000) on snacks: "+e3);
		
		HashMap<String, Double> weightVectorSmallMT = Development.weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/MT_%s_%s_%s_%s.wv","small.all", "10", "-2", "5000")));
		double e1 = new Perceptron(weightVectorSmallMT).test(testSetSnacks);
		System.out.println("MT, small (-2 top 5000) on snacks: "+e1);
		
		
		HashMap<String, Double> weightVectorAllST = Development.weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/ST_%s_%s_%s.wv","all", "10", "-2")));
		double f2 = new Perceptron(weightVectorAllST).test(testSetSnacks);
		System.out.println("ST, all (-2) on snacks: "+f2);

		HashMap<String, Double> weightVectorAllMTR = Development.weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/MTR_%s_%s_%s_%s.wv","all", "10", "-2", "5000")));
		double f3 = new Perceptron(weightVectorAllMTR).test(testSetSnacks);
		System.out.println("MTR, all (-2 top 5000) on snacks: "+f3);
		
		HashMap<String, Double> weightVectorAllMT = Development.weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/MT_%s_%s_%s_%s.wv","all", "10", "-2", "5000")));
		double f1 = new Perceptron(weightVectorAllMT).test(testSetSnacks);
		System.out.println("MT, all (-2 top 5000) on snacks: "+f1);
		
	}
	

	public static void testGardening(){
		File file = new File("SentimentClassification/results/testResults/Gardening");
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
		
		ArrayList<Instance> testSetSnacks = CreateInstances.createInstancesFromFileNewFormat(new File("SentimentClassification/makeCorpus/gardening.test.formatted"));
		
		HashMap<String, Double> weightVectorSmallST = Development.weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/ST_%s_%s_%s.wv","small.all", "10", "-2")));
		double e2 = new Perceptron(weightVectorSmallST).test(testSetSnacks);
		System.out.println("ST, small (-2) on gardening: "+e2);

		HashMap<String, Double> weightVectorSmallMTR = Development.weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/MTR_%s_%s_%s_%s.wv","small.all", "10", "-2", "5000")));
		double e3 = new Perceptron(weightVectorSmallMTR).test(testSetSnacks);
		System.out.println("MTR, small (-2 top 5000) on gardening: "+e3);
		
		HashMap<String, Double> weightVectorSmallMT = Development.weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/MT_%s_%s_%s_%s.wv","small.all", "10", "-2", "5000")));
		double e1 = new Perceptron(weightVectorSmallMT).test(testSetSnacks);
		System.out.println("MT, small (-2 top 5000) on gardening: "+e1);
		
		
		HashMap<String, Double> weightVectorAllST = Development.weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/ST_%s_%s_%s.wv","all", "10", "-2")));
		double f2 = new Perceptron(weightVectorAllST).test(testSetSnacks);
		System.out.println("ST, all (-2) on gardening: "+f2);

		HashMap<String, Double> weightVectorAllMTR = Development.weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/MTR_%s_%s_%s_%s.wv","all", "10", "-2", "5000")));
		double f3 = new Perceptron(weightVectorAllMTR).test(testSetSnacks);
		System.out.println("MTR, all (-2 top 5000) on gardening: "+f3);
		
		HashMap<String, Double> weightVectorAllMT = Development.weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/MT_%s_%s_%s_%s.wv","all", "10", "-2", "5000")));
		double f1 = new Perceptron(weightVectorAllMT).test(testSetSnacks);
		System.out.println("MT, all (-2 top 5000) on gardening: "+f1);
		
	}
	
	public static void testOutdoor(){
		File file = new File("SentimentClassification/results/testResults/Outdoor");
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
		
		ArrayList<Instance> testSetOutdoor = CreateInstances.createInstancesFromFileNewFormat(new File("SentimentClassification/makeCorpus/outdoor.test.formatted"));
		
		HashMap<String, Double> weightVectorSmallST = Development.weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/ST_%s_%s_%s.wv","small.all", "10", "-2")));
		double e2 = new Perceptron(weightVectorSmallST).test(testSetOutdoor);
		System.out.println("ST, small (-2) on outdoor: "+e2);

		HashMap<String, Double> weightVectorSmallMTR = Development.weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/MTR_%s_%s_%s_%s.wv","small.all", "10", "-2", "5000")));
		double e3 = new Perceptron(weightVectorSmallMTR).test(testSetOutdoor);
		System.out.println("MTR, small (-2 top 5000) on outdoor: "+e3);
		
		HashMap<String, Double> weightVectorSmallMT = Development.weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/MT_%s_%s_%s_%s.wv","small.all", "10", "-2", "5000")));
		double e1 = new Perceptron(weightVectorSmallMT).test(testSetOutdoor);
		System.out.println("MT, small (-2 top 5000) on outdoor: "+e1);
		
		
		HashMap<String, Double> weightVectorAllST = Development.weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/ST_%s_%s_%s.wv","all", "10", "-2")));
		double f2 = new Perceptron(weightVectorAllST).test(testSetOutdoor);
		System.out.println("ST, all (-2) on outdoor: "+f2);

		HashMap<String, Double> weightVectorAllMTR = Development.weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/MTR_%s_%s_%s_%s.wv","all", "10", "-2", "5000")));
		double f3 = new Perceptron(weightVectorAllMTR).test(testSetOutdoor);
		System.out.println("MTR, all (-2 top 5000) on outdoor: "+f3);
		
		HashMap<String, Double> weightVectorAllMT = Development.weightVectorFromFile(new File(String.format("SentimentClassification/weightVectors/MT_%s_%s_%s_%s.wv","all", "10", "-2", "5000")));
		double f1 = new Perceptron(weightVectorAllMT).test(testSetOutdoor);
		System.out.println("MT, all (-2 top 5000) on outdoor: "+f1);
		
	}
	
	public static void main(String[] args){
		//singleTest();
		//multiTest();
		//multiRandomTest();
		//testSnacks();
		//testGardening();
		//testOutdoor();
	}	
}
