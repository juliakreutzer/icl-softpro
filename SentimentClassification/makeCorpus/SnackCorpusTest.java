package makeCorpus;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import src.de.uniheidelberg.cl.softpro.sentimentclassification.CreateInstances;
import src.de.uniheidelberg.cl.softpro.sentimentclassification.Development;
import src.de.uniheidelberg.cl.softpro.sentimentclassification.Instance;
import src.de.uniheidelberg.cl.softpro.sentimentclassification.Perceptron;

public class SnackCorpusTest {

	public static void main(String[] args) {
		
		HashMap<String, Double> weightVector1 = Development.weightVectorFromFile(new File("SentimentClassification/weightVectors/ST_small.all_10_-2.wv"));
		HashMap<String, Double> weightVector2 = Development.weightVectorFromFile(new File("SentimentClassification/weightVectors/ST_books_10_-2.wv"));
		HashMap<String, Double> weightVector3 = Development.weightVectorFromFile(new File("SentimentClassification/weightVectors/ST_all_10_-2.wv"));
		HashMap<String, Double> weightVector4 = Development.weightVectorFromFile(new File("SentimentClassification/weightVectors/ST_small.all_10_1divt.wv"));
		HashMap<String, Double> weightVector5 = Development.weightVectorFromFile(new File("SentimentClassification/weightVectors/ST_books_10_1divt.wv"));
		HashMap<String, Double> weightVector6 = Development.weightVectorFromFile(new File("SentimentClassification/weightVectors/ST_all_10_1divt.wv"));
		
		HashMap<String, Double> weightVector7 = Development.weightVectorFromFile(new File("SentimentClassification/weightVectors/MT_small.all_10_1divt_1000.wv"));
		HashMap<String, Double> weightVector8 = Development.weightVectorFromFile(new File("SentimentClassification/weightVectors/MT_all_10_1divt_1000.wv"));
		
		ArrayList<Instance> corpusSnack = CreateInstances.createInstancesFromFileNewFormat(new File("SentimentClassification/makeCorpus/4snacks.test.formatted"));
		
		Perceptron p1 = new Perceptron(weightVector1);
		double erg1 = p1.test(corpusSnack);
		System.out.println("ST_small.all_10_-2.wv"+erg1);
		
		Perceptron p2 = new Perceptron(weightVector2);
		double erg2 = p2.test(corpusSnack);
		System.out.println("ST_books_10_-2.wv"+erg2);
		
		Perceptron p3 = new Perceptron(weightVector3);
		double erg3 = p3.test(corpusSnack);
		System.out.println("ST_all_10_-2.wv"+erg3);
		
		Perceptron p4 = new Perceptron(weightVector4);
		double erg4 = p4.test(corpusSnack);
		System.out.println("ST_small.all_10_1divt.wv"+erg4);
		
		Perceptron p5 = new Perceptron(weightVector5);
		double erg5 = p5.test(corpusSnack);
		System.out.println("ST_books_10_1divt.wv"+erg5);
		
		Perceptron p6 = new Perceptron(weightVector6);
		double erg6 = p6.test(corpusSnack);
		System.out.println("ST_all_10_1divt.wv"+erg6);
		
		Perceptron p7 = new Perceptron(weightVector7);
		double erg7 = p7.test(corpusSnack);
		System.out.println("MT_small.all_10_1divt_1000.wv"+erg7);
		
		Perceptron p8 = new Perceptron(weightVector8);
		double erg8 = p8.test(corpusSnack);
		System.out.println("MT_all_10_1divt_1000.wv"+erg8);
	}
}