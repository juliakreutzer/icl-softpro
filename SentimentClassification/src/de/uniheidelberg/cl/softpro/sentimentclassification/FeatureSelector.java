package de.uniheidelberg.cl.softpro.sentimentclassification;

import java.util.*;

/**
 * Selects top k features of a weight vector
 * @author hering
 *
 */

public class FeatureSelector {
	HashMap <String, Double> mapWeights;
	HashMap <String, Double> mapL2;
	
	/**
	 * 
	 * @param weights Hashmap <String, Double> containing the trained perceptron
	 * @param l2 Hashmap <String, Double> containing the calculated l2 values
	 */
	public FeatureSelector (HashMap <String, Double> weights, HashMap <String, Double> l2) {
		this.mapWeights = weights;
		this.mapL2 = l2;
	}
	
	/**
	 * 
	 * @param k int
	 * @return
	 */
	public HashMap <String, Double> getTopKFeatures (int k) {
		HashMap <String, Double> topFeatures = new HashMap<String, Double>();
		Integer i = 0;
		
		for (Map.Entry <String, Double> TopKEntry : entriesSortedByValues(this.mapL2)) {
			if (i <= k) {
				topFeatures.put (TopKEntry.getKey(), mapWeights.get (TopKEntry.getKey()));
				i++;
			}
			else {
				break;
			}
		}
		
		return topFeatures;
	}
	
	/**
	 * 
	 * @param map
	 * @return
	 */
	static <K,V extends Comparable<? super V>> SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
		SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>> (new Comparator<Map.Entry<K,V>>() {
	            @Override public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
	                return e1.getValue().compareTo(e2.getValue());
	            }
	        }
	    );
	    sortedEntries.addAll(map.entrySet());
	    return sortedEntries;
	}
	
}

