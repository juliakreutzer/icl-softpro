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
	public static Instance[] readFromCorpusFile(String InputFileName){
		String line = "";
		try {
			BufferedReader in = new BufferedReader(new FileReader(InputFileName));
			line = in.readLine(); //only neccessary to read one line as whole corpus is saved in this line
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int c = 0;
		Instance[] instances = new Instance[line.split("<>").length];
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
			
			instances[c]=i;
			//System.out.println(instances.toString());
			c = c+1;
		}
		System.out.println(instances.length);
		return instances;
	}
	
	public static void main(String[] args) {
		double learningrate = 0.0001;
		int epochs = 1000;
		
		System.out.println(epochs+", "+learningrate);
		
		//creates new SingleTaskPerceptron instance
		SingleTaskPerceptron p = new SingleTaskPerceptron(epochs, learningrate);
		
		//reads from file to Array of instances
		//Instance[] train_instances = readFromFile("test5.review");
		//System.out.println(train_instances.length+" train_instances read");
		
		//Instance[] test_instances2 = readFromFile("test_test5.review");
		//System.out.println(test_instances2.length+" test_instances read");
		
		//readFromCorpusFile("corpus/books.test.corpus");
		//readFromCorpusFile("corpus/books.train.corpus");
		Instance[] train_instances_all = readFromCorpusFile("data/processed_acl/corpus/all.train.corpus");
		Instance[] test_instances_all = readFromCorpusFile("data/processed_acl/corpus/all.test.corpus");

		Instance[] test_instances_books = readFromCorpusFile("data/processed_acl/corpus/books.test.corpus");
		Instance[] test_instances_dvd = readFromCorpusFile("data/processed_acl/corpus/dvd.test.corpus");
		Instance[] test_instances_electronics = readFromCorpusFile("data/processed_acl/corpus/electronics.test.corpus");
		Instance[] test_instances_kitchen = readFromCorpusFile("data/processed_acl/corpus/kitchen.test.corpus");
		
		
		//Instance[] train_instances_all = readFromFile("singleTask_all.reviews");
		//System.out.println("train_instances_all: "+train_instances_all.length+" train_instances read");
		
		//Instance[] test_instances_dvd = readFromFile("singleTask_dvd.reviews");
		//System.out.println("test_instances_dvd: "+test_instances_dvd.length+" train_instances read");
		
		//Instance[] test_instances_electronics = readFromFile("singleTask_electronics.reviews");
		//System.out.println("test_instances_electronics: "+test_instances_electronics.length+" train_instances read");
		
		//Instance[] test_instances_kitchen = readFromFile("singleTask_kitchen.reviews");
		//System.out.println("test_instances_kitchen: "+test_instances_kitchen.length+" train_instances read");
		
		//Instance[] test_instances_books = readFromFile("singleTask_books.reviews");
		//System.out.println("test_instances_books: "+test_instances_books.length+" train_instances read");
		
		//trains Perceptron on Array of instances
		/* testing the dotproduct method
		HashMap<String,Integer> test1 = new HashMap<String,Integer>();
		test1.put("testword",1);
		test1.put("notin2",2);
		test1.put("both", 9);
		HashMap<String,Double> test2 = new HashMap<String,Double>();
		test2.put("testword",5.0);
		test2.put("notin1", 2.3);
		test2.put("both", 3.0);
		System.out.println(SingleTaskPerceptron.dotProduct(test1, test2));
		*/
		HashMap<String,Double> t = p.train(train_instances_all);
		//p.printParameters();
		//System.out.println(t);
		double r = p.test(test_instances_dvd);
		System.out.println("dvd :"+r);
		double s = p.test(test_instances_electronics);
		System.out.println("electronics: "+s);
		double o = p.test(test_instances_kitchen);
		System.out.println("kitchen: "+o);
		double q = p.test(test_instances_books);
		System.out.println("books: "+q);
		double w = p.test(test_instances_all);
		System.out.println("all: "+w);
		
		
	}
}
