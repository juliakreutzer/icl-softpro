package de.uniheidelberg.cl.softpro.sentimentclassification;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

/**
 * Selects top k features of a weight vector
 * @author hering
 *
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
			System.out.println ("!!! WARNING: weights hashmap not initialized");
		}
		if (l2 == null) {
			l2 = new HashMap <String, Double>();
			System.out.println ("!!! WARNING: l2 hashmap not initialized");
		}
		System.out.println ("Got a hashmap with " + Integer.toString (weights.size()) + "/" + Integer.toString (l2.size()) +  " features");
		this.mapWeights = weights;
		this.mapL2 = l2;
		this.sortedL2 = this.getSortedMap (this.mapL2);
	}
	
	private TreeMap <Double, ArrayList<String>> getSortedMap (HashMap <String, Double> input) {
		TreeMap <Double, ArrayList<String>> newMap = new TreeMap <Double, ArrayList<String>>();
		for (String key : input.keySet()) {
			Double value = input.get (key);
			if (newMap.containsKey (value)) {
				ArrayList <String> currentList = newMap.get (value);
				currentList.add (key);
				newMap.put (value,  currentList);
			}
			else {
				ArrayList <String> currentList = new ArrayList <String>();
				currentList.add (key);
				newMap.put (value,  currentList);
			}
		}
		return newMap;
	}
	
	/**
	 * 
	 * @param k int
	 * @return
	 */
	public HashMap <String, Double> getTopKFeatures (int k) {
		HashMap <String, Double> topFeatures = new HashMap<String, Double>();
		int counter = 0;

		while ( counter < k ) {
			for (Entry<Double, ArrayList<String>> set : this.sortedL2.descendingMap().entrySet()) {
				ArrayList <String> currentList = set.getValue();
				for (String feature : currentList) {
					if (counter < k) {
						topFeatures.put (feature, this.mapWeights.get (feature));
						counter = counter + 1;
					}
				}
			}
		}
		System.out.println (" Returning " + Integer.toString (topFeatures.size()) + " of " + Integer.toString (k));
		return topFeatures;
	}
}

