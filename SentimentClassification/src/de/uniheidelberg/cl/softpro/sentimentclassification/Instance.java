package de.uniheidelberg.cl.softpro.sentimentclassification;

import java.util.HashMap;
import java.util.Set;

/**
 * Objects of this class each represent one instance (-> review) with feature vector and label.
 */
public class Instance {
	private HashMap<String,Integer> featureVector; //contains of features and their counts
	private int label;

	/**
	 * Constructor with feature vector and label
	 * @param featureVector must be a HashMap<String,Integer> for values and counts
	 * @param label must be either -1 (neg) or +1 (pos)
	 */
	public Instance(HashMap<String,Integer> featureVector,int label){
		this.featureVector = featureVector;
		this.label = label;
	}
	/**
	 * String representation for an Instance object
	 * @return String including feature vector and label
	 */
	public String toString(){
		return "features: "+this.featureVector.toString()+", label: "+this.label;
	}

	/**
	 * Sets the feature vector of an Instance object. 
	 * @param newVector must be a HashMap of String and Integer values
	 */
	public void setFeatureVector(HashMap<String,Integer> newVector){
		this.featureVector = newVector;
	}
	
	/**
	 * Gets an instance's feature vector.
	 * @return HashMap of features and their counts <String, Integer>
	 */
	public HashMap<String, Integer> getFeatureVector() {
		return featureVector;
	}

	/**
	 * Gets an instance's label.
	 * @return integer value [-1,1] representing either negative or positive sentiment
	 */
	public int getLabel() {
		return label;
	}
	
	/**
	 * Gets an instance's features.
	 * @return a set of features (Strings)
	 */
	public Set<String> getFeatures(){
		return this.featureVector.keySet();
	}

	/**
	 * For learning in modified space, an additional feature named "!p" with count -1 is added to the feature vector.
	 */
	public void foldInP(){
		this.featureVector.put("!p", -1);
	}
	
	/**
	 * The instance's label is folded into its feature vector. This is needed in modified space.
	 */
	public void foldInLabel(){
		this.featureVector.put("!p",this.featureVector.get("!p")*this.label);
	}
}
