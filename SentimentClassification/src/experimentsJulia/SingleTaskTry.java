package experimentsJulia;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author julia
 *
 */
public class SingleTaskTry {
	
	/**reads feature vectors and labels of a file to an array of instances
	 * one instance at one line 
	 * @param args
	 */
	public static Instance[] readFromFile(String InputFileName){
		List<String> lines = new ArrayList<String>();
		try {
			BufferedReader in = new BufferedReader(new FileReader(InputFileName));
			String line = null;
			while ((line = in.readLine()) != null) {
				lines.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int c = 0;
		Instance[] instances = new Instance[lines.size()];
		for (String line : lines){
			//System.out.println("line: "+line);
			HashMap<String, Integer> m = new HashMap<String, Integer>();
			int l = 0;
			String[] entries = line.split(" "); // ["is_such:1", "feel:1"]
			for (String pair : entries){ //pair: "is_such:1"
				if (pair.startsWith("#")){ //if label
					String label = pair.split(":")[1];
					if (label.equals("positive")){
						l = 1;
					}
					else {
						l = -1;
					}
				}
				else { //if feature:count
					String feature = pair.split(":")[0];
					int count = Integer.parseInt(pair.split(":")[1]);
					m.put(feature, count);
				}
			}
			//System.out.println(m.toString());
			//System.out.println(l);
			Instance i = new Instance(m,l);
			//System.out.println(i.toString());
			
			instances[c]=i;
			//System.out.println(instances.toString());
			c = c+1;
		}
	//	System.out.println(instances.length);
		return instances;
	}

	/*
	 * reads instances from a file which is in the following corpus format:
	 * review1 <> review2 <> ...
	 * in one line is the whole corpus
	 * review looks like that: feature1:count feature2:count ... #label:positive
	 */
	public static ArrayList<Instance> readFromCorpusFile(String InputFileName){
		String line = "";
		try {
			BufferedReader in = new BufferedReader(new FileReader(InputFileName));
			line = in.readLine(); //only necessary to read one line as whole corpus is saved in this line
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int c = 0;
		ArrayList<Instance> instances = new ArrayList<Instance>();
		for (String review : line.split("<>")){ //<> separates reviews -> for each review
			//System.out.println("review: "+review);
			HashMap<String, Integer> m = new HashMap<String, Integer>();
			int l = 0;
			String[] entries = review.split(" "); // ["is_such:1", "feel:1"]
			for (String pair : entries){ //pair: "is_such:1"
				if (pair.startsWith("#")){ //if #label#:...
					String label = pair.split(":")[1];
					if (label.equals("positive")){
						l = 1;
					}
					else {
						l = -1;
					}
				}
				else { //if feature:count
					if (pair.split(":").length==2){ //to filter out category at the beginning of each corpus
						String feature = pair.split(":")[0];
						int count = Integer.parseInt(pair.split(":")[1]);
						m.put(feature, count);
					}
				}
			}
			//System.out.println(m.toString());
			//System.out.println(l);
			Instance i = new Instance(m,l);
			//System.out.println(i.toString());
			
			instances.add(c, i);
			//System.out.println(instances.toString());
			c = c+1;
		}
		System.out.println(instances.size());
		return instances;
	}
	
	public static void main(String[] args) {
		double learningrate = 5;
		int epochs = 10;
		
		System.out.println(epochs+", "+learningrate);
		
		//creates new SingleTaskPerceptron instances
		
		SingleTaskPerceptron p = new SingleTaskPerceptron(epochs, learningrate);
		SingleTaskPerceptron q = new SingleTaskPerceptron(epochs, learningrate);
		SingleTaskPerceptron r = new SingleTaskPerceptron(epochs, learningrate);
		SingleTaskPerceptron s = new SingleTaskPerceptron(epochs, learningrate);
		SingleTaskPerceptron t = new SingleTaskPerceptron(epochs, learningrate);
		
		//reads from file to ArrayList of instances
		//for training
		ArrayList<Instance> train_instances_all = readFromCorpusFile("SentimentClassification/data/processed_acl/corpus/all.train.corpus");
		ArrayList<Instance> train_instances_dvd = readFromCorpusFile("SentimentClassification/data/processed_acl/corpus/dvd.train.corpus");
		ArrayList<Instance> train_instances_electronics = readFromCorpusFile("SentimentClassification/data/processed_acl/corpus/electronics.train.corpus");
		ArrayList<Instance> train_instances_kitchen = readFromCorpusFile("SentimentClassification/data/processed_acl/corpus/kitchen.train.corpus");
		ArrayList<Instance> train_instances_books = readFromCorpusFile("SentimentClassification/data/processed_acl/corpus/books.train.corpus");
		
		//for testing
		ArrayList<Instance>  test_instances_all = readFromCorpusFile("SentimentClassification/data/processed_acl/corpus/all.test.corpus");
		ArrayList<Instance>  test_instances_books = readFromCorpusFile("SentimentClassification/data/processed_acl/corpus/books.test.corpus");
		ArrayList<Instance>  test_instances_dvd = readFromCorpusFile("SentimentClassification/data/processed_acl/corpus/dvd.test.corpus");
		ArrayList<Instance>  test_instances_electronics = readFromCorpusFile("SentimentClassification/data/processed_acl/corpus/electronics.test.corpus");
		ArrayList<Instance>  test_instances_kitchen = readFromCorpusFile("SentimentClassification/data/processed_acl/corpus/kitchen.test.corpus");
		
		
		//train perceptrons
		p.train(train_instances_all);
		q.train(train_instances_books);
		r.train(train_instances_dvd);
		s.train(train_instances_electronics);
		t.train(train_instances_kitchen);

	
		//test perceptrons	
		System.out.println("all on all");
		System.out.println(p.test(test_instances_all));
		
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
		
	}
}
