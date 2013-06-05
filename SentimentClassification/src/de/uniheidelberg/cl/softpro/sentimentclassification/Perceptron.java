package de.uniheidelberg.cl.softpro.sentimentclassification;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * class that represents an Perceptron for testing and training
 * @author julia
 */
public class Perceptron{
	private HashMap<String,Double> weights = new HashMap<String,Double>();
	private int epochs;
	private double learningRate;
	//private double p = 0.0; //not needed for modified space
	
	/**
	 * Constructor: creates new SingleTaskPerceptron instance 
	 * @param epochs number of training epochs
	 * @param learningRate learningRate for training
	 */
	public Perceptron(int epochs, double learningRate){
		this.epochs = epochs;
		this.learningRate = learningRate;
	}
	
	/**
	 * constructor with learningRate parameter
	 * epochs are set to 1
	 * @param learningRate
	 */
	public Perceptron(double learningRate){
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
	 * epochs are set to 1, learningRate to 0.0001
	 */
	public Perceptron(){
		this(1, 0.0001);
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
	 * trains the SingleTaskPerceptron instance on a given training set for all epochs
	 * @param trainset must be an Array of Instances
	 * @return the trained weight vector in HashMap<String,Integer> format
	 */
	public HashMap<String,Double> trainSingle(ArrayList<Instance> trainset){
		//for each epoch
		for (int t=1; t<=this.epochs; t++){
			//System.out.println("training in epoch "+t);
			//for input instance
			
			//here you can test various learning rates
		//	double newLearningRate = 1/(1+t/trainset.size()); //"decreasing" from Paper 3.3 (5)
			//double newLearningRate = 1/t;
			//this.setLearningRate(newLearningRate);
			
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
	
	public double getLearningRate() {
		return learningRate;
	}

	public void setLearningRate(double newlearningRate) {
		this.learningRate = newlearningRate;
	}

	/**
	 * trains the Perceptron on a given set of instances, but only for one epoch
	 * @param trainset ArrayList of instances which contain training samples
	 * @return error rate (double), i.e. #correctly_classified_samples / #all_samples
	 */
	public HashMap<String,Double> trainMulti(ArrayList<Instance> trainset){
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
	 * note the name conventions:     
	 * ST (SingleTask?): (Kategoriename) | All
	 * MT (MultiTask?): Kat | Random
	 * Followed by: number of epochs, learning rate, for MT: Number of Top Features
	 * file ending: .wv
	 * e.g. ST_books_10_0.0001.wv
	 * e.g. MT_Random_100_0.0001_100.wv 
	 * @param outFile
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
