import java.io.*;
import java.util.*;

public class CreateInstances {
	//1) übergabe: string (ohne kategorienamen am anfang)-> arraylist<instances>
	//2) übergabe: filename -> arraylist<instance>
	
	public static ArrayList<Instance> createInstancesFromFile(File f) {
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
		String[] reviews = line.split("<>");
		reviews[0] = reviews[0].replaceFirst(".*\t", "");
		ArrayList<String> al = new ArrayList<String>();
		for (String review : reviews) {
			al.add(review);
		}
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
			//System.out.println(inst.getFeatureVector());	//test
			//System.out.println(inst.getLabel());			//test
			instanceArray.add(inst);
		}
		return instanceArray;
	}
	
	public static ArrayList<Instance> createInstancesFromString(String s) {
		String[] reviews = s.split("<>");
		reviews[0] = reviews[0].replaceFirst(".*\t", "");
		ArrayList<String> al = new ArrayList<String>();
		for (String review : reviews) {
			al.add(review);
		}
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
			//System.out.println(inst.getFeatureVector());	//test
			//System.out.println(inst.getLabel());			//test
			instanceArray.add(inst);
		}
		return instanceArray;
	}
	
	public static void main(String[] args) {
		ArrayList<Instance> erg1 = createInstancesFromFile(new File("/home/jasmin/workspace/Test/src/testDatei.txt"));
		System.out.println(erg1);
		
		ArrayList<Instance> erg2 = createInstancesFromString("das:2 ist:1 ein:1 test:1 #label#:negative<>zum:1 ausprobieren:1 und:1 testen:1 #label#:positive<>wort:2 wert:3 #label#:positive");
		System.out.println(erg2);
	}
}
