import java.util.*;

/**
 * 
 */

/**
 * @author jasmin
 *
 */
public class InstanceTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HashMap<String, Integer> hm = new HashMap<String, Integer>();
		hm.put("string1", 1);
		hm.put("string2", 2);
		Instance i = new Instance(hm, 1);
		int a = hm.get("string1");
		System.out.println(a);
		HashMap<String, Integer> f = i.getFeatureVector();
		int l = i.getLabel();
		System.out.println(f);
		System.out.println(l);
		i.setLabel(-1);
		System.out.println(i.getLabel());
	}

}
