package de.uniheidelberg.cl.softpro.sentimentclassification;

import java.util.*;

/**
 * Selects top k features of a weight vector
 * @author hering
 *
 */

public class FeatureSelector {
	HashMap <String, Double> unorderedMap;
	
	public FeatureSelector (HashMap <String, Double> newMap) {
		this.unorderedMap = newMap;
	}
	
	public HashMap <String, Double> getTopKFeatures (int k) {
		HashMap <String, Double> topFeatures = new HashMap<String, Double>();
		System.out.println("Sorting...");
		Integer i = 0;
		
		for (Map.Entry <String, Double> TopKEntry : entriesSortedByValues(this.unorderedMap)) {
			if (i < k) {
				topFeatures.put(TopKEntry.getKey(), TopKEntry.getValue());
			}
			else {
				break;
			}
		}
		
		return topFeatures;
	}
	
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

