/**
 * 
 */
package de.uniheidelberg.cl.softpro.sentimentclassification;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
/**
 * Trains a single task perceptron on training data
 * @author julia
 */

public class TrainSinglePerceptron {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		double learningrate = 1.0E-4;
		int epochs = 100;
		
		System.out.println(epochs+", "+learningrate);
		
		//creates new SingleTaskPerceptron instances
		
		Perceptron p = new Perceptron(epochs, learningrate);
//		Perceptron q = new Perceptron(epochs, learningrate);
//		Perceptron r = new Perceptron(epochs, learningrate);
//		Perceptron s = new Perceptron(epochs, learningrate);
//		Perceptron t = new Perceptron(epochs, learningrate);
		
		//reads from file to ArrayList of instances
		//for training
		ArrayList<Instance> train_instances_all = CreateInstances.createInstancesFromFile(new File("/home/mirko/workspace/SentimentClassification/data/processed_acl/corpus/all.train.corpus"));
//		ArrayList<Instance> train_instances_dvd = CreateInstances.createInstancesFromFile(new File("~/workspace/SentimentClassification/data/processed_acl/corpus/dvd.train.corpus"));
//		ArrayList<Instance> train_instances_electronics = CreateInstances.createInstancesFromFile(new File("~/workspace/SentimentClassification/data/processed_acl/corpus/electronics.train.corpus"));
//		ArrayList<Instance> train_instances_kitchen = CreateInstances.createInstancesFromFile(new File("~/workspace/SentimentClassification/data/processed_acl/corpus/kitchen.train.corpus"));
//		ArrayList<Instance> train_instances_books = CreateInstances.createInstancesFromFile(new File("SentimentClassification/data/processed_acl/corpus/books.train.corpus"));
		
		//for testing
//		ArrayList<Instance>  test_instances_all = CreateInstances.createInstancesFromFile(new File("/home/mirko/workspace/SentimentClassification/data/processed_acl/corpus/all.test.corpus"));
//		ArrayList<Instance>  test_instances_books = CreateInstances.createInstancesFromFile(new File("SentimentClassification/data/processed_acl/corpus/books.test.corpus"));
//		ArrayList<Instance>  test_instances_dvd = CreateInstances.createInstancesFromFile(new File("SentimentClassification/data/processed_acl/corpus/dvd.test.corpus"));
//		ArrayList<Instance>  test_instances_electronics = CreateInstances.createInstancesFromFile(new File("SentimentClassification/data/processed_acl/corpus/electronics.test.corpus"));
//		ArrayList<Instance>  test_instances_kitchen = CreateInstances.createInstancesFromFile(new File("SentimentClassification/data/processed_acl/corpus/kitchen.test.corpus"));
//		
		//train perceptrons
		System.out.println("Training...");
		p.trainSingle(train_instances_all);
//		q.trainSingle(train_instances_books);
//		r.trainSingle(train_instances_dvd);
//		s.trainSingle(train_instances_electronics);
//		t.trainSingle(train_instances_kitchen);
	
		//test perceptrons	
		System.out.println("Writing...");
		BufferedWriter out = new BufferedWriter (new FileWriter ("output.txt"));
		out.write( HadoopTrainPerceptron.convertHashMapToString (p.getWeights()));
		out.close();
		System.out.println("Done!");
//		System.out.println("all on all: "+p.test(test_instances_all));
//		System.out.println("all on books: "+p.test(test_instances_books));
//		System.out.println("all on dvd: "+p.test(test_instances_dvd));
//		System.out.println("all on electronics: "+p.test(test_instances_electronics));
//		System.out.println("all on kitchen: "+p.test(test_instances_kitchen));
//		
//		System.out.println("books on books: "+q.test(test_instances_books));
//		System.out.println("books on all: "+q.test(test_instances_all));
//		
//		System.out.println("dvd on dvd: "+r.test(test_instances_dvd));
//		System.out.println("dvd on all: "+r.test(test_instances_all));
//		
//		System.out.println("electronics on electronics: "+s.test(test_instances_electronics));
//		System.out.println("electronics on all "+s.test(test_instances_all));
//		
//		System.out.println("kitchen on kitchen: "+t.test(test_instances_kitchen));
//		System.out.println("kitchen on all: "+t.test(test_instances_all));

	}
}

