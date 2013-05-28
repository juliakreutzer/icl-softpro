package de.uniheidelberg.cl.softpro.sentimentclassification;

import java.io.*;
import java.util.*;

public class CreateInstances {
	
	public static String readFile(File f) {
		String line = new String();
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			line = br.readLine();
			br.close();
		} catch (FileNotFoundException e) {
			System.err.println("File not found");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return line;
	}
	
	public static ArrayList<String> formatting(String kategorie) {		//mit ArrayList
		String[] reviews = kategorie.split("<>");
		reviews[0] = reviews[0].replace("dvd\t", "");
		reviews[0] = reviews[0].replace("books\t", "");
		reviews[0] = reviews[0].replace("kitchen\t", "");
		reviews[0] = reviews[0].replace("electronics\t", "");
		ArrayList<String> al = new ArrayList<String>();
		for (String review : reviews) {
			al.add(review);
		}
		return  al;
	}
	
	public static ArrayList<Instance> makingInstances(ArrayList<String> al) {
		ArrayList<Instance> instanceArray = new ArrayList<Instance>();
		for (String review : al) {
			int label = 0;
			HashMap<String, Integer> hm = new HashMap<String, Integer>();
			String[] reviewArray = review.split(" ");
			if (reviewArray[reviewArray.length-1].equals("#label#:negative")) {
				label = -1;
			} else if (reviewArray[reviewArray.length-1].equals("#label#:positive")){
				label = 1;
			}
			for (int i = 0; i <= reviewArray.length-2; i++) {
				String[] keyvalue = reviewArray[i].split(":");
				String key = keyvalue[0];
				int value = Integer.parseInt(keyvalue[1]);
				hm.put(key, value);
			}
			Instance inst = new Instance(hm, label);
			inst.foldInP(); //folds in p, i.e. adding new dimension !p with count -1
			inst.foldInLabel(); //fold in label, i.e. new dimension !p is multiplied with label
			
			//System.out.println(inst.getFeatureVector());	//test
			//System.out.println(inst.getLabel());			//test
			instanceArray.add(inst);
		}
		return instanceArray;
	}
}
