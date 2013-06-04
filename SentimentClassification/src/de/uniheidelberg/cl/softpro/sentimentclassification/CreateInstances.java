package de.uniheidelberg.cl.softpro.sentimentclassification;

import java.io.*;
import java.util.*;

public class CreateInstances {
	
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
