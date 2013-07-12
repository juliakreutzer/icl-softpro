package de.uniheidelberg.cl.softpro.sentimentclassification;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.lang.Math;

/**
 * Class that represents a Perceptron with a weight vector, and a number of epochs and a learning rate for training
 */
public class Perceptron{
	
	 /*
	 * instance variables are:
	 * HashMap<String,Double> - representing the weight vector
	 * int epochs - number of epochs for training
	 * String learningRate - representing the learning rate for training
	 * 
	 * Perceptrons can be initiated by given ...
	 * 1)epochs and learningRate
	 * 2)only learningRate (epochs default:1)
	 * 3)weight vector
	 * 4)default parameters (epochs:10, learningRate -2)
	 */
	
	private HashMap<String,Double> weights = new HashMap<String,Double>();
	private int epochs;
	private String learningRate; //global learning rate is string	
	/**
	 * Constructor: creates new Perceptron instance 
	 * @param epochs number of training epochs
	 * @param learningRate learningRate for training
	 */
	public Perceptron(int epochs, String learningRate){
		this.epochs = epochs;
		this.learningRate = learningRate;
	}
	
	/**
	 * constructor with learningRate parameter
	 * epochs are set to 1 in order to use this constructor for parallel multi task learning
	 * @param learningRate must be a String
	 */
	public Perceptron(String learningRate){
		this(1,learningRate);
	}
	
	/**
	 * constructor for perceptron with given weights
	 * used for testing and evaluation
	 * @param weights, HashMap of Strings (features) and Doubles (counts)
	 */
	public Perceptron(HashMap<String, Double> weights){
		this.weights = weights;
	}
	
	/**
	 * default constructor
	 * epochs are set to 10, learningRate to 0.01
	 */
	public Perceptron(){
		this(10, "-2");
	}
	
	/**
	 * calculates the dot product of two sparse vectors (HashMap<String,Integer>)
	 * @param m1 training instance vector
	 * @param m2 perceptron weight vector
	 * @return dot product (double)
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
	
	/**
	 * checks whether an instance is misclassified by trained Perceptron or not
	 * @param i test instance
	 * @return true or false
	 */
	public boolean misclassified(Instance i){
		//	if ((dotProduct(i.getFeatureVector(),this.weights))*i.getLabel()<=1){ //with margin
		if ((dotProduct(i.getFeatureVector(),this.weights))*i.getLabel()<=0){
			return true;
		}
		else{
			return false;
		}
	}

	
	/**
	 * trains the SingleTaskPerceptron instance on a given training set for all epochs
	 * @param trainset must be an Array of Instances
	 * @return the trained weight vector in HashMap<String,Integer> format
	 */
	public HashMap<String,Double> trainSingle(ArrayList<Instance> trainset){
		//for each epoch
		for (int t=1; t<=this.epochs; t++){			
			
			//various learning rates
			double currentLearningRate = 0; //local learning rate is double
			
			if (this.learningRate.equals("exp")){
				currentLearningRate = 1*Math.pow(0.85,-1/new Double(trainset.size()));
			}
			else if (this.learningRate.equals("dec")){
				currentLearningRate = 1/(1+new Double(t)/new Double(trainset.size()));
			}
			else if (this.learningRate.equals("1divt")){
				currentLearningRate = 1/new Double(t);
			}
			else if (Double.parseDouble(this.learningRate)>=-10 || Double.parseDouble(this.learningRate)<=10   ){
				currentLearningRate = Math.pow(10,Double.parseDouble(this.learningRate));
			}
						
			for (Instance i : trainset){

				//if misclassified, update with gradient
				if (this.misclassified(i)){
					//update weights
					for (String feature : i.getFeatures()){
						Double featureValue = new Double("0.0");
						//if feature can be found in current weights
						if (this.weights.containsKey(feature)){
							featureValue = this.weights.get(feature);
						}
						//update weight
						this.weights.put(feature, featureValue+(currentLearningRate*i.getFeatureVector().get(feature)*i.getLabel()));
					}
				}
			}
		}
		return this.weights;
	}
	
	public String getLearningRate() {
		return learningRate;
	}

	public void setLearningRate(double newLearningRate){ 
		this.learningRate = new Double(newLearningRate).toString();
	}

	/**
	 * trains the Perceptron on a given set of instances, but only for one epoch
	 * @param trainset ArrayList of instances which contain training samples
	 * @return the trained weight vector in HashMap<String,Integer> format
	 */
	public HashMap<String,Double> trainMulti(ArrayList<Instance> trainset, int currentEpoch){
		for (Instance i : trainset){
			
			//various learning rates
			double currentLearningRate = 0; //local learning rate is double
			
			//learning has to start with epoch no. 1, not 0!
			if (currentEpoch==0){
				System.err.print("Counting of epochs should start at 1. Never start at 0!");
				System.exit(1);
			}
			
			if (this.learningRate.equals("exp")){
				currentLearningRate = 1*Math.pow(0.85,-1/new Double(trainset.size()));
			}
			else if (this.learningRate.equals("dec")){
				currentLearningRate = 1/(1+new Double(currentEpoch)/new Double(trainset.size()));
			}
			else if (this.learningRate.equals("1divt")){
				currentLearningRate = 1/new Double(currentEpoch);
			}
			else if (Double.parseDouble(this.learningRate)>=-10 || Double.parseDouble(this.learningRate)<=10   ){
				currentLearningRate = Math.pow(10,Double.parseDouble(this.learningRate));
			}
						
			//if misclassified, update with gradient
			if (this.misclassified(i)){
				//update weights
				for (String feature : i.getFeatures()){
					Double featureValue = new Double("0.0");
					//if feature can be found in current weights
					if (this.weights.containsKey(feature)){
						featureValue = this.weights.get(feature);
					}
					//update weight
					this.weights.put(feature, featureValue+(currentLearningRate*i.getFeatureVector().get(feature)*i.getLabel()));
				}
			}
		}
		return this.weights;
	}
	
	/**
	 * sets the weights of a Perceptron
	 * @param newWeights must be a HashMap<String, Double>
	 */
	public void setWeights(HashMap<String, Double> newWeights) {
		this.weights = newWeights;
	}
	

	/**
	 * tests the SingleTaskPerceptron instance on a given test set
	 * @param testset must be an Array of Instances
	 * @return error rate (double), i.e. #correctly_classified_samples / #all_samples
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
	 * prints parameters for one SingleTaskPerceptron instance:
	 * epochs and learning rate
	 */
	public void printParameters(){
		System.out.println("epochs: "+this.epochs);
		System.out.println("learning rate: "+this.learningRate);
		//System.out.println("p "+this.p); //not needed for modified space
	}
	
	/**
	 * prints the weightVector in a readable way:
	 * one feature-weight pair each line
	 */
	public void printWeights(){
		for (String key : this.weights.keySet()){
			System.out.append(key+" : "+this.weights.get(key).toString()+"\n");
		}
		System.out.flush();
	}
	
	/**
	 * stores the weight vector in the given file
	 * note the naming conventions: (see readme)
	 * <ST|MT|MTR>_<training set>_<number of epochs in training>_<learning rate>_<top k features selected>.wv 
	 * @param outFile is the output file where the weight vector is saved
	 */
	public void writeWeightsToFile(File outFile){
		
		BufferedWriter out;
		try {
			out = new BufferedWriter (new FileWriter (outFile));
			for (String key : this.weights.keySet()){
				out.append(key+":"+this.weights.get(key).toString()+" ");
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
