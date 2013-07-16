package de.uniheidelberg.cl.softpro.sentimentclassification;
import java.io.File;
import java.util.HashMap;

/**
 * This class offers the possibility to write your own reviews and tests if they are classified positive or negative. 
 */
public class WriteReview {
	
	/**
	 * reads review from standard input and converts it into a HashMap<String, Integer>
	 * @return review (HashMap<String, Integer>)
	 */
	public static HashMap<String, Integer> convertReviewToHashMap(String review) {
		review = review.replaceAll("[^a-zA-Z 0-9]", "").toLowerCase(); //convert to lower case and remove all non alphabetic or numeric characters
		String[] reviewArray = review.split(" "); //tokenize into unigrams by simply splitting at " "
		HashMap<String, Integer> reviewAsHashMap = new HashMap<String, Integer>();
		for (String word : reviewArray) {
			if (reviewAsHashMap.containsKey(word)) {
				reviewAsHashMap.put(word, reviewAsHashMap.get(word)+1);
			} else {
				reviewAsHashMap.put(word, 1);
			}
		}
		return reviewAsHashMap;
	}
	
	/**
	 * calculates dot product of review as HashMap and weight vector and evaluates if review is positive or negative
	 * @return label (int)
	 */
	public static int evaluateReview(String text) {
		HashMap<String, Double> weightVector = Development.weightVectorFromFile(new File("SentimentClassification/weightVectors/MTR_all_10_-2_5000.wv"));
		HashMap<String, Integer> review = convertReviewToHashMap(text);
		int label = 0;
		if (Perceptron.dotProduct(review, weightVector) > 0) {
			label = 1;
		} else if (Perceptron.dotProduct(review, weightVector) < 0) {
			label = -1;
		} else {
			label = 0;
		}
		return label;
	}
	
	/**
	 * Call this main if you want to try writing reviews.
	 * A trained perceptron (with weight vector MTR_all_10_-2_5000.wv) analyzes your sentiment.
	 * If the decision is unclear, "don't know" will be the feedback.
	 * This runs in an infinite loop, so you need to interrupt the process for terminating.
	 * @param args not needed here
	 */
	public static void main(String[] args) {
		String text = args[0];
		int label = evaluateReview(text);
		if (label ==1){
			System.out.println("positive!");
		}
		else if (label == -1){
			System.out.println("negative!");
		}
		else {
			System.out.println("don't know ...");
		}
	}
}
