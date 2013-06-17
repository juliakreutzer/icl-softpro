package de.uniheidelberg.cl.softpro.sentimentclassification;

/**
 * Class for testing on development set -> optimization of parameters
 * @author jasmin & julia
 */
public class Development {
	String[] epochs = {"1", "10", "100"};
	String[] learningRates = {"exp", "dec", "1divt", "-3", "-2", "-1", "0", "1"}; //constants are exponents to power of 10 -> e.g. "0" => 1; "1" => 10
	
	public static void train(){
		//trains all relevant parameter combinations and saves resulting weight vectors to files
	}
	
	public static void test(){
		//tests all relevant parameter combinations and saves resulting error rates to files
		
		//reads devset from file
		//reads weight vector from file
		//prints error rates into files
	}
	
	public static void main(String[] args){
		train();
		test();
	}
	
}
