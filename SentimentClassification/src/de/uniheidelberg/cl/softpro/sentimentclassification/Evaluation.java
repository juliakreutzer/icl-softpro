package de.uniheidelberg.cl.softpro.sentimentclassification;
import java.io.*;
import java.util.*;

public class Evaluation {
	
	public static void main(String[] args) {
		
		//hier wollen wir nach Parameter-Optimierung einmalig die entsprechenden Gewichtsvektoren laden und sie auf dem TestSet testen
		//das funktioniert für single genauso wie für multi -> Auswahl der Gewichtsvektoren
		//Ergebnisse müssen irgendwie gespeichert werden
		
		
		//steht jetzt alles in Development!!!
		//Trainingsdateien einlesen
		/*ArrayList<Instance> train_instances_all = CreateInstances.createInstancesFromFileNewFormat(new File("SentimentClassification/data/processed_acl/corpus_final_formatted/all.train.corpus.final.formatted"));
		ArrayList<Instance> train_instances_books = CreateInstances.createInstancesFromFileNewFormat(new File("SentimentClassification/data/processed_acl/corpus_final_formatted/books.train.corpus.final.formatted"));
		ArrayList<Instance> train_instances_dvd = CreateInstances.createInstancesFromFileNewFormat(new File("SentimentClassification/data/processed_acl/corpus_final_formatted/dvd.train.corpus.final.formatted"));
		ArrayList<Instance> train_instances_kitchen = CreateInstances.createInstancesFromFileNewFormat(new File("SentimentClassification/data/processed_acl/corpus_final_formatted/kitchen.train.corpus.final.formatted"));
		ArrayList<Instance> train_instances_electronics = CreateInstances.createInstancesFromFileNewFormat(new File("SentimentClassification/data/processed_acl/corpus_final_formatted/electronics.train.corpus.final.formatted"));
		
		//devset einlesen
		ArrayList<Instance> dev_instances_all = CreateInstances.createInstancesFromFileNewFormat(new File("SentimentClassification/data/processed_acl/corpus_final_formatted/all.dev.corpus.final.formatted"));
		ArrayList<Instance> dev_instances_books = CreateInstances.createInstancesFromFileNewFormat(new File("SentimentClassification/data/processed_acl/corpus_final_formatted/books.dev.corpus.final.formatted"));
		ArrayList<Instance> dev_instances_dvd = CreateInstances.createInstancesFromFileNewFormat(new File("SentimentClassification/data/processed_acl/corpus_final_formatted/dvd.dev.corpus.final.formatted"));
		ArrayList<Instance> dev_instances_kitchen = CreateInstances.createInstancesFromFileNewFormat(new File("SentimentClassification/data/processed_acl/corpus_final_formatted/kitchen.dev.corpus.final.formatted"));
		ArrayList<Instance> dev_instances_electronics = CreateInstances.createInstancesFromFileNewFormat(new File("SentimentClassification/data/processed_acl/corpus_final_formatted/electronics.dev.corpus.final.formatted"));
		
		//Training und weigthVectorFile erstellen
		int epochs = 50;				//Epochen
		String learningRate = "1";	//Lernrate
		System.out.println("Epochen: " + epochs + ", Lernrate: " + learningRate);
		
		Perceptron p_all = new Perceptron(epochs, learningRate);
		p_all.trainSingle(train_instances_all);
		p_all.writeWeightsToFile(new File("ST_all"));
		
		Perceptron p_books = new Perceptron(epochs, learningRate);
		p_books.trainSingle(train_instances_books);
		p_books.writeWeightsToFile(new File("ST_books"));
		
		Perceptron p_dvd = new Perceptron(epochs, learningRate);
		p_dvd.trainSingle(train_instances_dvd);
		p_dvd.writeWeightsToFile(new File("ST_dvd"));
		
		Perceptron p_kitchen = new Perceptron(epochs, learningRate);
		p_kitchen.trainSingle(train_instances_kitchen);
		p_kitchen.writeWeightsToFile(new File("ST_kitchen"));
		
		Perceptron p_electronics = new Perceptron(epochs, learningRate);
		p_electronics.trainSingle(train_instances_electronics);
		p_electronics.writeWeightsToFile(new File("ST_electronics"));
		
		//weigthVector einlesen
		HashMap<String, Double> wv_all = weightVectorFromFile(new File ("ST_all"));
		HashMap<String, Double> wv_books = weightVectorFromFile(new File ("ST_books"));
		HashMap<String, Double> wv_dvd = weightVectorFromFile(new File ("ST_dvd"));
		HashMap<String, Double> wv_kitchen = weightVectorFromFile(new File ("ST_kitchen"));
		HashMap<String, Double> wv_electronics = weightVectorFromFile(new File ("ST_electronics"));
		
		//Perceptron erstellen mit eingelesenem weigthVector
		Perceptron perceptronTest_all = new Perceptron(wv_all);
		Perceptron perceptronTest_books = new Perceptron(wv_books);
		Perceptron perceptronTest_dvd = new Perceptron(wv_dvd);
		Perceptron perceptronTest_kitchen = new Perceptron(wv_kitchen);
		Perceptron perceptronTest_electronics = new Perceptron(wv_electronics);
		
		//errorRate berechnen mit Perceptron-Methode test
		double errorRate_all = perceptronTest_all.test(dev_instances_all);
		System.out.println("All on All: " + errorRate_all);
		
		double errorRate_books = perceptronTest_books.test(dev_instances_books);
		System.out.println("Books on Books: " + errorRate_books);
		
		double errorRate_dvd = perceptronTest_dvd.test(dev_instances_dvd);
		System.out.println("Dvd on Dvd: " + errorRate_dvd);
		
		double errorRate_kitchen = perceptronTest_kitchen.test(dev_instances_kitchen);
		System.out.println("Kitchen on Kitchen: " + errorRate_kitchen);
		
		double errorRate_electronics = perceptronTest_electronics.test(dev_instances_electronics);
		System.out.println("Electronics on Electronics: " + errorRate_electronics);
		*/
	}
	
}
