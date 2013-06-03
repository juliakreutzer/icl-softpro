package de.uniheidelberg.cl.softpro.sentimentclassification;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 */

/**
 * @author mirko
 *
 */
public class Toolbox {
	
	//Julia: diese Methode sollte durch CreateInstances abgedeckt werden
//	public static ArrayList<Instance> convertStringToInstances (String input) {
//		/*
//		 * Code geklaut aus Julia's SingleTaskTry.java
//		 */
//		ArrayList<Instance> instanceArray = new ArrayList<Instance>();
//		
//		String[] reviews = input.split("<>");
//		reviews[0] = reviews[0].replaceFirst(".*\t", "");
//		
//		for (String review : reviews) {
//			int label = 0;
//			HashMap<String, Integer> hm = new HashMap<String, Integer>();
//			String[] reviewArray = review.split(" ");
//			if (reviewArray[reviewArray.length-1].equals("#label#:negative")) {
//				label = -1;
//			} else if (reviewArray[reviewArray.length-1].equals("#label#:positive")){
//				label = 1;
//			}
//			for (int i = 0; i <= reviewArray.length-2; i++) {
//				String[] keyvalue = reviewArray[i].split(":");
//				if (keyvalue.length == 2) {
//					String key = keyvalue[0];
//					int value = Integer.parseInt (keyvalue[1]);
//					hm.put(key, value);
//				}
//			}
//			Instance inst = new Instance(hm, label);
//			instanceArray.add(inst);
//		}
//		return instanceArray;
//	}

	
	public static HashMap<String, Double> convertStringToHashmap (String input) {
		HashMap<String, Double> map = new HashMap<String, Double>();
		try {
			System.out.println ("Initialisiere WV...");
			System.out.println (input);
			for (String pair : input.split ("<>")) {
				System.out.println("string: " + pair);
				String[] splitPair = pair.split (" *= *", 2);
				System.out.println("key: " + splitPair[0]);
				System.out.println("value: " + splitPair[1]);
				map.put (splitPair[0], splitPair.length == 1 ? 1.234 : Double.parseDouble (splitPair[1]));

			}
		}
		catch (NullPointerException e) {
			System.err.println ("NullPointerException: convertStringToHashmap");
			System.err.println ("input = '" + input + "'");
			return map;
		}
		return map;
	}
	
	

	public static String convertHashMapToString (HashMap<String, Double> map) {
		StringBuilder returnString = new StringBuilder();
		Boolean first = true;
		for (String key : map.keySet()) {
			if (first) {
				first = false;
			}
			else {
				returnString.append ("<>");
			}
			returnString.append (key);
			returnString.append ("=");
			returnString.append (map.get (key));
		}
		return returnString.toString();
	}
	
	public static String getWeightVector( BufferedReader dataSource ) throws IOException {
		StringBuilder weightVector = new StringBuilder();
        String line;
        line = dataSource.readLine();
        while (line != null) {
//        	System.out.println ("Read line:");
//        	System.out.println (line);
        	String[] splitLine = line.split("\t");
//        	System.out.println ("1: " + splitLine[0] + " - 2: " + new Double(splitLine[1]).toString());
        	StringBuilder singleWeight = new StringBuilder();
        	singleWeight.append (splitLine [0]);
        	singleWeight.append ("=");
        	singleWeight.append (splitLine [1]);
        	singleWeight.append ("<>");
        	weightVector.append (singleWeight.toString());
        	line = dataSource.readLine();
        }
        dataSource.close();
		return weightVector.toString();
	}
}
