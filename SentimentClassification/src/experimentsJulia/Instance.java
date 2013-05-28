/**
 * 
 */
package experimentsJulia;

import java.util.HashMap;
import java.util.Set;

/**
 * @author julia
 * objects of this class each represent one instance with feature vector and label 
 */
public class Instance {
	private HashMap<String,Integer> featureVector;
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
	
	public String toString(){
		return "features: "+this.featureVector.toString()+", label: "+this.label;
	}

	public void setFeatureVector(HashMap<String,Integer> newVector){
		this.featureVector = newVector;
	}
	
	public HashMap<String, Integer> getFeatureVector() {
		return featureVector;
	}

	
	public int getLabel() {
		return label;
	}

	public void setLabel(int label) {
		if (label == -1 | label == 1){
			this.label = label;
			}
		else{
			System.err.println("Value for label not allowed. Must be either -1 or 1.");
		}
	}
	
	public Set<String> getFeatures(){
		return this.featureVector.keySet();
	}

	//in case we want to use modified space -
	public void foldInP(){
		this.featureVector.put("!p", -1);
	}
	
	public void foldInLabel(){
		this.featureVector.put("!p",this.featureVector.get("!p")*this.label);
	}
}