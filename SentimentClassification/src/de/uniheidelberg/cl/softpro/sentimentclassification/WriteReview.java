package src.de.uniheidelberg.cl.softpro.sentimentclassification;
import java.io.File;
import java.util.HashMap;
import java.util.Scanner;


public class WriteReview {

	public static HashMap<String, Integer> convertReviewToHashMap() {
		Scanner s = new Scanner(System.in);
		System.out.println("Type your review: ");
		String review = s.nextLine();
		review = review.replaceAll("[^a-zA-Z 0-9]", "").toLowerCase();
		String[] reviewArray = review.split(" ");
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
	
	public static int evaluateReview() {
		HashMap<String, Double> weightVector = Development.weightVectorFromFile(new File("SentimentClassification/weightVectors/ST_small.all_10_-2.wv"));
		HashMap<String, Integer> review = convertReviewToHashMap();
		int label = 0;
		if (Perceptron.dotProduct(review, weightVector) > 0) {
			label = 1;
		} else {
			label = -1;
		}
		return label;
	}
	
	public static void main(String[] args) {
	//	System.out.println(convertReviewToHashMap());
		System.out.println(evaluateReview());
	}
}
