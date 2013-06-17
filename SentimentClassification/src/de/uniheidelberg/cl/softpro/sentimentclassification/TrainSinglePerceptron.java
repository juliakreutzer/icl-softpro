package de.uniheidelberg.cl.softpro.sentimentclassification;

import java.io.File;
import java.util.ArrayList;
/**
 * Trains a single task perceptron on training data
 * @author julia
 */

public class TrainSinglePerceptron {

	/**
	 * Main Method for training the single perceptron
	 * @param args command line parameters, not used
	 */
	public static void main(String[] args) {
		String learningrate = "dec";
		int epochs = 10;
		
		System.out.println(epochs+", "+learningrate);
		
		//creates new SingleTaskPerceptron instances
		
		Perceptron p = new Perceptron(epochs, learningrate);
		Perceptron q = new Perceptron(epochs, learningrate);
		Perceptron r = new Perceptron(epochs, learningrate);
		Perceptron s = new Perceptron(epochs, learningrate);
		Perceptron t = new Perceptron(epochs, learningrate);
		
		//reads from file to ArrayList of instances
		//for training
		ArrayList<Instance> train_instances_all = CreateInstances.createInstancesFromFile(new File("SentimentClassification/data/processed_acl/corpus_final/all.train.corpus.final"));
		ArrayList<Instance> train_instances_dvd = CreateInstances.createInstancesFromFile(new File("SentimentClassification/data/processed_acl/corpus_final/dvd.train.corpus.final"));
		ArrayList<Instance> train_instances_electronics = CreateInstances.createInstancesFromFile(new File("SentimentClassification/data/processed_acl/corpus_final/electronics.train.corpus.final"));
		ArrayList<Instance> train_instances_kitchen = CreateInstances.createInstancesFromFile(new File("SentimentClassification/data/processed_acl/corpus_final/kitchen.train.corpus.final"));
		ArrayList<Instance> train_instances_books = CreateInstances.createInstancesFromFile(new File("SentimentClassification/data/processed_acl/corpus_final/books.train.corpus.final"));
		
		//for testing
		ArrayList<Instance>  dev_instances_all = CreateInstances.createInstancesFromFile(new File("SentimentClassification/data/processed_acl/corpus_final/all.dev.corpus.final"));
		ArrayList<Instance>  dev_instances_books = CreateInstances.createInstancesFromFile(new File("SentimentClassification/data/processed_acl/corpus_final/books.dev.corpus.final"));
		ArrayList<Instance>  dev_instances_dvd = CreateInstances.createInstancesFromFile(new File("SentimentClassification/data/processed_acl/corpus_final/dvd.dev.corpus.final"));
		ArrayList<Instance>  dev_instances_electronics = CreateInstances.createInstancesFromFile(new File("SentimentClassification/data/processed_acl/corpus_final/electronics.dev.corpus.final"));
		ArrayList<Instance>  dev_instances_kitchen = CreateInstances.createInstancesFromFile(new File("SentimentClassification/data/processed_acl/corpus_final/kitchen.dev.corpus.final"));
		
		//train perceptrons
		p.trainSingle(train_instances_all);
		//p.printWeights();
		//p.writeWeightsToFile(new File("SentimentClassification/results/ST_All_10_0.0001.wv"));
		q.trainSingle(train_instances_books);
		//q.printWeights();
		r.trainSingle(train_instances_dvd);
		s.trainSingle(train_instances_electronics);
		t.trainSingle(train_instances_kitchen);
	
		//test perceptrons	
		System.out.println("all on all: "+p.test(dev_instances_all));
		System.out.println("all on books: "+p.test(dev_instances_books));
		System.out.println("all on dvd: "+p.test(dev_instances_dvd));
		System.out.println("all on electronics: "+p.test(dev_instances_electronics));
		System.out.println("all on kitchen: "+p.test(dev_instances_kitchen));
		
		System.out.println("books on books: "+q.test(dev_instances_books));
		System.out.println("books on all: "+q.test(dev_instances_all));
		
		System.out.println("dvd on dvd: "+r.test(dev_instances_dvd));
		System.out.println("dvd on all: "+r.test(dev_instances_all));
		
		System.out.println("electronics on electronics: "+s.test(dev_instances_electronics));
		System.out.println("electronics on all "+s.test(dev_instances_all));
		
		System.out.println("kitchen on kitchen: "+t.test(dev_instances_kitchen));
		System.out.println("kitchen on all: "+t.test(dev_instances_all));
		
		
		//test with synsets
	//	Perceptron a = new Perceptron(epochs, learningrate);
		//ArrayList <Instance> trainA = CreateInstances.createInstancesFromFileNewFormat(new File("SentimentClassification/data/processed_acl/corpus_final_formatted_WN/books.train.corpus.final.formatted.WN"));
		//ArrayList <Instance> testA = CreateInstances.createInstancesFromFileNewFormat(new File("SentimentClassification/data/processed_acl/corpus_final_formatted_WN/books.dev.corpus.final.formatted.WN"));
		//a.trainSingle(trainA);
		//System.out.println("books on books:" +a.test(testA));
	}
}

