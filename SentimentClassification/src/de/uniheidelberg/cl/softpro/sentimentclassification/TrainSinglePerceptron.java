/**
 * 
 */
package de.uniheidelberg.cl.softpro.sentimentclassification;

import java.io.File;
import java.util.ArrayList;
/**
 * Trains a single task perceptron on training data
 * TODO: def train/test dataset, write output?
 * @author julia
 */

public class TrainSinglePerceptron {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		double learningrate = 5;
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
		ArrayList<Instance> train_instances_all = CreateInstances.makingInstances(CreateInstances.formatting(CreateInstances.readFile(new File("SentimentClassification/data/processed_acl/corpus/all.train.corpus"))));
		/*TODO: change method calls
		ArrayList<Instance> train_instances_dvd = readFromCorpusFile("SentimentClassification/data/processed_acl/corpus/dvd.train.corpus");
		ArrayList<Instance> train_instances_electronics = readFromCorpusFile("SentimentClassification/data/processed_acl/corpus/electronics.train.corpus");
		ArrayList<Instance> train_instances_kitchen = readFromCorpusFile("SentimentClassification/data/processed_acl/corpus/kitchen.train.corpus");
		ArrayList<Instance> train_instances_books = readFromCorpusFile("SentimentClassification/data/processed_acl/corpus/books.train.corpus");
		*/
		//for testing
		ArrayList<Instance>  test_instances_all = CreateInstances.makingInstances(CreateInstances.formatting(CreateInstances.readFile(new File("SentimentClassification/data/processed_acl/corpus/all.test.corpus"))));
		/*TODO: change method calls
		ArrayList<Instance>  test_instances_books = readFromCorpusFile("SentimentClassification/data/processed_acl/corpus/books.test.corpus");
		ArrayList<Instance>  test_instances_dvd = readFromCorpusFile("SentimentClassification/data/processed_acl/corpus/dvd.test.corpus");
		ArrayList<Instance>  test_instances_electronics = readFromCorpusFile("SentimentClassification/data/processed_acl/corpus/electronics.test.corpus");
		ArrayList<Instance>  test_instances_kitchen = readFromCorpusFile("SentimentClassification/data/processed_acl/corpus/kitchen.test.corpus");
		*/
		
		//train perceptrons
		p.train(train_instances_all);
		/*TODO: comment in as soon as CreateInstances works well and easy
		q.train(train_instances_books);
		r.train(train_instances_dvd);
		s.train(train_instances_electronics);
		t.train(train_instances_kitchen);
		*/
	
		//test perceptrons	
		System.out.println("all on all");
		System.out.println(p.test(test_instances_all));

		
		/*TODO: comment in and change as soon as CreateInstances works well and easy
		System.out.println("books on books");
		System.out.println(q.test(test_instances_books));
		System.out.println("books on all");
		System.out.println(q.test(test_instances_all));
		
		System.out.println("dvd on dvd");
		System.out.println(r.test(test_instances_dvd));
		System.out.println("dvd on all");
		System.out.println(r.test(test_instances_all));
		
		System.out.println("electronics on electronics");
		System.out.println(s.test(test_instances_electronics));
		System.out.println("electronics on all");
		System.out.println(s.test(test_instances_all));
		
		System.out.println("kitchen on kitchen");
		System.out.println(t.test(test_instances_kitchen));
		System.out.println("kitchen on all");
		System.out.println(t.test(test_instances_all));
		*/

	}
}

