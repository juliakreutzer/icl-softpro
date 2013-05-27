package de.uniheidelberg.cl.softpro.sentimentclassification;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * 
 */

/**
 * @author mirko
 *
 */
public class Toolbox {
	public static Instance[] convertStringToInstances (String input) {
		/*
		 * Code geklaut aus Julia's SingleTaskTry.java
		 */
		int c = 0;
		String[] splitInput = input.split("<>");	//<> separates reviews -> for each review
		
		Instance[] instances = new Instance [splitInput.length];
		for (String review : splitInput) {
			HashMap<String, Integer> m = new HashMap<String, Integer>();
			int l = 0;
			String[] entries = review.split(" "); 	// ["is_such:1", "feel:1"]
			for (String pair : entries){			//pair: "is_such:1"
				if (pair.startsWith("#")){ 			//if #label#:...
					String label = pair.split(":")[1];
					if (label.equals("positive")){
						l = 1;
					}
					else {
						l = -1;
					}
				}
				else { 								//if feature:count
					if (pair.split(":").length==2){ //to filter out category at the beginning of each corpus
						String feature = pair.split(":")[0];
						int count = Integer.parseInt(pair.split(":")[1]);
						m.put(feature, count);
					}
				}
			}
			Instance i = new Instance(m,l);
			instances[c] = i;
			c = c+1;
		}
		return instances;
	}

	public static HashMap<String, Double> convertStringToHashmap (String input) {
		HashMap<String, Double> map = new HashMap<String, Double>();
		try {
			for (String pair : input.split ("<>")) {
				String[] splitPair = pair.split (" *= *", 2); 
				map.put (splitPair[0], splitPair.length == 1 ? 0.0 : Double.parseDouble (splitPair[1]));
			}
		}
		catch (NullPointerException e) {
			System.err.println ("NullPointerException: convertStringToHashmap");
		}
		return map;
	}

	public static String convertHashMapToString (HashMap<String, Double> map) {
		return "";
	}
	
	public static String getWeightVector( BufferedReader dataSource ) throws IOException {
		StringBuilder weightVector = new StringBuilder();
        String line;
        line = dataSource.readLine();
        while (line != null) {
        	StringBuilder singleWeight = new StringBuilder();
        	singleWeight.append ("<>");
        	singleWeight.append (line);
        	weightVector.append (singleWeight.toString());
        	line = dataSource.readLine();
        }
        dataSource.close();
		return weightVector.toString();
	}
}
