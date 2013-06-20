package de.uniheidelberg.cl.softpro.sentimentclassification;

//import Instance;

import java.io.*;
import java.util.*;

public class CreateInstances {
	
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
	
	//liest Datei ein in Format: kat (tab) feature:count feature:count #label#:positive (neue Zeile) kat ....
	// gibt eine ArrayList mit je einer Instance pro review aus
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
			System.err.println("File not found");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return instanceArray;
	}
	//liest String ein in Format: feature:count feature:count #label#:positive<>feature:count ....
	// gibt eine ArrayList mit je einer Instance pro review aus
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
