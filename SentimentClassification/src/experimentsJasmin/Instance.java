package src.experimentsJasmin;
/*
 * @author jasmin
 *
 */


import java.util.*;

public class Instance {

	private HashMap<String, Integer> featureVector;
	private int label;
	
	public Instance (HashMap<String, Integer> fv, int l) {
		featureVector = fv;
		label = l;
	}
	
	public HashMap<String, Integer> getFeatureVector() {
		return featureVector;
	}
	
	public int getLabel() {
		return label;
	}
	
	public void setLabel(int x) {
		if (x == -1 | x == 1) {
			label = x;
		} else {
			System.out.println("Falscher Wert");
		}
	}
}
