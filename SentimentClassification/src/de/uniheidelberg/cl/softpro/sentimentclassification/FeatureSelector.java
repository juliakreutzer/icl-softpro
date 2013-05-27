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
		
		List <Double> orderableValues = new ArrayList <Double> (this.unorderedMap.values());
		Collections.sort (orderableValues, Collections.reverseOrder());
		Double minimum = orderableValues.get (k);
		
		for ( String key : this.unorderedMap.keySet()) {
			if (this.unorderedMap.get (key) >= minimum) {
				topFeatures.put(key, this.unorderedMap.get (key));
			}
		}
		return topFeatures;
	}
}
