import java.io.*;
import java.util.*;

public class CreateInstances {
	//1) übergabe: string (ohne kategorienamen am anfang)-> arraylist<instances>
	//2) übergabe: filename -> arraylist<instance>
	
	/*public static ArrayList<String> readFile2(File f) {
		ArrayList<String> kategorienInArrayList = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line = br.readLine();
			while (line != null) {
				kategorienInArrayList.add(line);
				line = br.readLine();
			}
			br.close();
		} catch (FileNotFoundException e) {
			System.err.println("File not found");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return kategorienInArrayList;
	}*/
	
	/*public static void main(String[] args) {
		ArrayList<String> erg = readFile(new File("/home/jasmin/workspace/Test/src/testDatei.txt"));
		System.out.println(erg);
	}*/
	
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
	
	/*public static String[] formatting(String kategorie) {			//mit Array
		String[] reviews = kategorie.split(" NEWLINE ");
		reviews[0] = reviews[0].replace("dvd\t", "");		//dvd, books... ???
		return  reviews;
	}*/
	
	public static ArrayList<String> formatting(String kategorie) {		//mit ArrayList
		String[] reviews = kategorie.split(" NEWLINE ");
		reviews[0] = reviews[0].replace("dvd\t", "");		//dvd, books... ???
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
			System.out.println(inst.getFeatureVector());	//test
			System.out.println(inst.getLabel());			//test
			instanceArray.add(inst);
		}
		return instanceArray;
	}
	
	public static void main(String[] args) {
		String erg = readFile(new File("/home/jasmin/workspace/Test/src/testDatei.txt"));
		ArrayList<String> al = formatting(erg);
		System.out.println(al);
		ArrayList<Instance> instanceArrayList = makingInstances(al);
		System.out.println(instanceArrayList);
	}
}
