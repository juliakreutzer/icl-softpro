package de.uniheidelberg.cl.softpro.sentimentclassification;

import java.util.*;
import java.util.Map.Entry;

/**
 * Provides top k feature selection
 */

public class FeatureSelector {
	HashMap <String, Double> mapWeights;
	HashMap <String, Double> mapL2;
	TreeMap <Double, ArrayList<String>> sortedL2;
	
	/**
	 * 
	 * @param weights Hashmap <String, Double> containing the trained perceptron
	 * @param l2 Hashmap <String, Double> containing the calculated l2 values
	 */
	public FeatureSelector (HashMap <String, Double> weights, HashMap <String, Double> l2) {
		if (weights == null) {
			weights = new HashMap <String, Double>();
			// Used for debugging:
			// System.out.println ("!!! WARNING: weights hashmap not initialized");
		}
		if (l2 == null) {
			l2 = new HashMap <String, Double>();
			// Used for debugging:
			// System.out.println ("!!! WARNING: l2 hashmap not initialized");
		}
		// Used for debugging:
		// System.out.println ("Got a hashmap with " + Integer.toString (weights.size()) + "/" + Integer.toString (l2.size()) +  " features");
		this.mapWeights = weights;
		this.mapL2 = l2;
		this.sortedL2 = this.getSortedMap (this.mapL2);
	}
	
	/**
	 * 
	 * @param input HashMap that is to be sorted
	 * @return returns a sorted TreeMap with the content of the HashMap
	 */
	private TreeMap <Double, ArrayList<String>> getSortedMap (HashMap <String, Double> input) {
		TreeMap <Double, ArrayList<String>> newMap = new TreeMap <Double, ArrayList<String>>();
		// A TreeMap can be sorted by its keys easily. Therefore, we use the l2 (saved in HashMap input) value as key and the names of the features as values in the TreeMap value's ArrayList.
		for (String key : input.keySet()) {									// iterate through HashMap's keys
			Double value = input.get (key);	
			if (newMap.containsKey (value)) {								// TreeMap already contains
				ArrayList <String> currentList = newMap.get (value);		// fetch the ArrayList containing feature names
				currentList.add (key);										// add the current feature
				newMap.put (value,  currentList);							// rewrite the old value's ArrayList
			}
			else {
				ArrayList <String> currentList = new ArrayList <String>();	// create a new ArrayList -> it will contain all features with the same l2 norm
				currentList.add (key);										// add feature to ArrayList
				newMap.put (value,  currentList);							// put the (l2-norm, ArrayList) to the sorted TreeMap
			}
		}
		return newMap;
	}
	
	/**
	 * 
	 * @param k int number of features to select
	 * @return	HashMap <String, Double> that contains k features
	 */
	public HashMap <String, Double> getTopKFeatures (int k) {
		HashMap <String, Double> topFeatures = new HashMap<String, Double>();
		int counter = 0;

		while ( counter < k ) {
			for (Entry<Double, ArrayList<String>> set : this.sortedL2.descendingMap().entrySet()) {		// iterate through l2-sorted TreeMap
				ArrayList <String> currentList = set.getValue();										// ArrayList containing all features with the same l2 norm
				for (String feature : currentList) {													// iterate through features
					if (counter < k) {																	// do we still need more features?
						topFeatures.put (feature, this.mapWeights.get (feature));						// add them!
						counter = counter + 1;															// increase counter
					}
				}
			}
		}
		// Used for debugging:
		// System.out.println (" Returning " + Integer.toString (topFeatures.size()) + " of " + Integer.toString (k));
		return topFeatures;
	}
}

