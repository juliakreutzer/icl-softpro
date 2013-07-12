package de.uniheidelberg.cl.softpro.sentimentclassification;

//import Instance;

import java.io.*;
import java.util.*;

/**
 * @author jasmin
 * reads text files and converts to different formats which are needed for further work
 */
public class CreateInstances {
	
	
	//ONLY USED FOR "OLD" HADOOP PERCEPTRON -> REMOVE IF NOT NEEDED ANYMORE
	//liest Datei ein in Format: kat (tab) review<>review .... (alles in einer Zeile)
	// gibt eine ArrayList mit je einer Instance pro review aus
	public static ArrayList<Instance> createInstancesFromFile(File f) {
		String line = new String();
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			line = br.readLine();
			br.close();
		} catch (FileNotFoundException e) {
			System.err.println("File not found");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		String[] reviews = line.split("<>");
		reviews[0] = reviews[0].replaceFirst(".*\t", "");
		
		ArrayList<Instance> instanceArray = new ArrayList<Instance>();
		for (String review : reviews) {
			int label = 0;
			HashMap<String, Integer> hm = new HashMap<String, Integer>();
			String[] reviewArray = review.split(" ");
			if (reviewArray[reviewArray.length-1].equals("#label#:negative")) {
				label = -1;
			} else if (reviewArray[reviewArray.length-1].equals("#label#:positive")){
				label = 1;
			}
			for (int i = 0; i <= reviewArray.length-2; i++) {
				String[] keyvalue = reviewArray[i].split(":");
				if (keyvalue.length == 2) {
					String key = keyvalue[0];
					int value = Integer.parseInt(keyvalue[1]);
					hm.put(key, value);
				}
			}
			Instance inst = new Instance(hm, label);
			//System.out.println(inst.getFeatureVector());	//test
			//System.out.println(inst.getLabel());			//test
			instanceArray.add(inst);
		}
		return instanceArray;
	}
	
	/**
	 * reads file in following format: category (tab) feature:count feature:count #label#:positive (new line) category ....
	 * gibt eine ArrayList mit je einer Instance pro review aus
	 * @param f
	 * @return 
	 */
	public static ArrayList<Instance> createInstancesFromFileNewFormat(File f) {
		String line = new String();
		ArrayList<Instance> instanceArray = new ArrayList<Instance>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			line = br.readLine();
			while (line != null) {
				HashMap<String, Integer> hm = new HashMap<String, Integer>();
				int label = 0;
				String[] review = line.split(" ");
				review[0] = review[0].replaceFirst(".*\t", "");
				if (review[review.length-1].equals("#label#:negative")) {
					label = -1;
				} else if (review[review.length-1].equals("#label#:positive")){
					label = 1;
				}
				for (int i = 0; i <= review.length-2; i++) {
					String[] keyvalue = review[i].split(":");
					if (keyvalue.length == 2) {
						String key = keyvalue[0];
						int value = Integer.parseInt(keyvalue[1]);
						hm.put(key, value);
					}
				}
				Instance inst = new Instance(hm, label);
				//System.out.println(inst.getFeatureVector());	//test
				//System.out.println(inst.getLabel());			//test
				instanceArray.add(inst);
				line = br.readLine();
			}
			br.close();
		} catch (FileNotFoundException e) {
			System.err.println("Corpus file "+f+" not found.");
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return instanceArray;
	}
	//liest String ein in Format: feature:count feature:count #label#:positive<>feature:count ....
	// gibt eine ArrayList mit je einer Instance pro review aus
	//used in HadoopTrainPerceptronScalable and HadoopTrainPerceptron
	public static ArrayList<Instance> createInstancesFromString(String s) {
		String[] reviews = s.split("<>");
		
		ArrayList<Instance> instanceArray = new ArrayList<Instance>();
		for (String review : reviews) {
			int label = 0;
			HashMap<String, Integer> hm = new HashMap<String, Integer>();
			String[] reviewArray = review.split(" ");
			if (reviewArray[reviewArray.length-1].equals("#label#:negative")) {
				label = -1;
			} else if (reviewArray[reviewArray.length-1].equals("#label#:positive")){
				label = 1;
			}
			for (int i = 0; i <= reviewArray.length-2; i++) {
				String[] keyvalue = reviewArray[i].split(":");
				if (keyvalue.length == 2) {
					String key = keyvalue[0];
					int value = Integer.parseInt(keyvalue[1]);
					hm.put(key, value);
				}
			}
			Instance inst = new Instance(hm, label);
			//System.out.println(inst.getFeatureVector());	//test
			//System.out.println(inst.getLabel());			//test
			instanceArray.add(inst);
		}
		return instanceArray;
	}
}
