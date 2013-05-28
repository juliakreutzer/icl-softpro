package de.uniheidelberg.cl.softpro.sentimentclassification;


import java.util.ArrayList;
import java.util.HashMap;

/**
 * Stolen from:
 * @author julia
 * (need some playground for hadoop... O:-)
 */
//class for SingleTaskPerceptron

public class Perceptron{
	private HashMap<String,Double> weights = new HashMap<String,Double>();
	private int epochs;
	private double learningRate;
	//private double p = 0.0; //not needed for modified space
	
	/**
	 * Constructor: creates new SingleTaskPerceptron instance 
	 */
	public Perceptron(int epochs, double learningRate){
		this.epochs = epochs;
		this.learningRate = learningRate;
	}
	
	/**
	 * calculates the dot product of two sparse vectors (HashMap<String,Integer>)
	 * @param m1 training instance vector
	 * @param m2 perceptron weight vector
	 * @return dot product 
	 */
	public static double dotProduct(HashMap<String,Integer> m1, HashMap<String,Double> m2){
		double dotproduct = 0.0; 
		//iterates over keys in 1st feature vector
		for (String key1 : m1.keySet()){
			double value2 = 0.0;
			//if key is already in perceptron weight vector
			if (m2.containsKey(key1)){
				value2 = m2.get(key1);
			}	
			//else is not necessary as value2 is instantiated to 0
			dotproduct += m1.get(key1) * value2;
		}		
		return dotproduct;
	}
	
	public boolean misclassified(Instance i){
		//if ((dotProduct(i.getFeatureVector(),this.weights)+this.p)*i.getLabel()<=0){
		if ((dotProduct(i.getFeatureVector(),this.weights))*i.getLabel()<=0){ //p not needed for modified space
			//System.out.println("misclassified");
			return true;
		}
		else{
			return false;
		}
	}
	
	
	/**
	 * trains the SingleTaskPerceptron instance on a given training set
	 * @param trainset must be an Array of Instances
	 * @return the trained weight vector in HashMap<String,Integer> format
	 */
	public HashMap<String,Double> train(ArrayList<Instance> trainset){
		//for each epoch
		for (int t=1; t<=this.epochs; t++){
			//System.out.println("training in epoch "+t);
			trainset.
			//for input instance
			for (Instance i : trainset){
				//System.out.println("instance "+i.toString());
				//System.out.println("weight vector "+this.weights.toString());
				
				//if misclassified, update with gradient
				if (this.misclassified(i)){
					//update weights
					//HashMap<String,Double> newWeights = new HashMap<String,Double>();
					for (String feature : i.getFeatures()){
						Double featureValue = new Double("0.0");
						//if feature can be found in current weights
						if (this.weights.containsKey(feature)){
							featureValue = this.weights.get(feature);
						}
						//System.out.println(featureValue);
						//update weight
						this.weights.put(feature, featureValue+(this.learningRate*i.getFeatureVector().get(feature)*i.getLabel()));
						//update p
						//this.p = this.p + this.learningRate * i.getLabel(); //not needed for modified space
					}
					//System.out.println("weight vector updated: "+this.weights.toString());
					//System.out.println(this.weights.size());
				}
			}
		}
		return this.weights;
	}
	
	
	public void setWeights(HashMap<String, Double> newWeights) {
		this.weights = newWeights;
	}

	/**
	 * tests the SingleTaskPerceptron instance on a given test set
	 * @param testset must be an Array of Instances
	 * @return
	 */
	public double test(ArrayList<Instance> testset){
		int errors = 0;
		double errorRate = 0.0;
		for (Instance i : testset){
			if (this.misclassified(i)){
				errors ++;
			}
		}
		errorRate = errors/(double) testset.size();
		return errorRate;
	}
	
	/**
	 * getter method for weights of a specific instance
	 * @return HashMap for weights 
	 */
	public HashMap<String,Double> getWeights(){
		return this.weights;
	}
	
	
	/**
	 * prints the parameters for one SingleTaskPerceptron instance
	 */
	public void printParameters(){
		System.out.println("weights: "+this.weights.toString());
		System.out.println("epochs: "+this.epochs);
		System.out.println("learning rate: "+this.learningRate);
		//System.out.println("p "+this.p); //not needed for modified space
	}
	
	
}
