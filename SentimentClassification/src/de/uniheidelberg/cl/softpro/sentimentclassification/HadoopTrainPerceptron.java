package de.uniheidelberg.cl.softpro.sentimentclassification;
/**
 * Uses perceptron algorithm inside Hadoop MapReduce framework
 * @author hering
 *
 */

import java.io.*;
import java.util.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;



public class HadoopTrainPerceptron {
	
	/**
	 * Sets configuration parameters for the Hadoop-Job
	 * Starts the Hadoop-Job
	 * Applies l2-Regularization on each job's output-data 
	 * @param args Commandline arguments
	 * @throws Exception
	 */
	
	private static FileSystem fs;
	private static int numberOfEpochs = 10;
	private static int topKFeatures = 10;
	private static String weightVectorFile = "weightVector.txt";
	private static Integer numberOfShards; 

	
	
	
	public static void main(String[] args) throws Exception {
		System.out.println ( "    de.uni-heidelberg.cl.softpro.sentimentclassification   ");
		System.out.println ( "-----------------------------------------------------------");
		System.out.println ( "Copyright (C) Jasmin Schröck, Julia Kreutzer, Mirko Hering");
		System.out.println ( "");
		
		if (args.length == 0) {
			System.out.println ( "hadoop jar hadoopmulti.jar [input folder] [output folder] [number of epochs] [number of top features]");
			System.exit(0);
		}
		if (args.length > 2) {
			if ( args[2] != null) {
				numberOfEpochs = Integer.parseInt (args[2]);
			}
		if (args.length > 3) {
		}
			if ( args[3] != null) {
				topKFeatures = Integer.parseInt (args[2]);
			}
		}
		System.out.println( "Started Hadoop MapReduce Perceptron Training");
		System.out.println( "--------------------------------------------");
		System.out.println( "Parameters:");
		System.out.println( "  number of epochs:    " + new Integer(numberOfEpochs).toString());
		System.out.println( "  top k features:      " + new Integer(topKFeatures).toString());
		System.out.println( "  name of vector file: " + weightVectorFile);
		System.out.println( "" );
		System.out.println( "Sit back and relax!");
		
		for ( int e = 0; e < numberOfEpochs; e++ ) {
			runHadoopJob( e, args[0], args[1]);			
		}
	}
	
	/**
	 * Reads a file from HDFS and converts it to a weight vector hashmap 
	 * @param filename File in HDFS that contains a weight vector
	 * @return HashMap containing the weight vector
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static HashMap <String, HashMap<String, Double>> readWVFileFromHDFS (String filename, FileSystem fileSys ) throws FileNotFoundException, IOException {
		List <Path> paths = new ArrayList <Path>();
		HashMap <String, HashMap <String, Double>> returnMap = new HashMap <String, HashMap <String, Double>>();

		RemoteIterator<LocatedFileStatus> fileList = fileSys.listFiles (new Path (filename), false);

		while (fileList.hasNext()) {
			Path currentFile = fileList.next().getPath();
			String currentFilename = currentFile.getName();
			if (currentFilename.startsWith ("part")) {
				paths.add (currentFile);
			}
		}
		for (Path aFile : paths) {
			BufferedReader br = new BufferedReader (new InputStreamReader (fileSys.open (aFile)));
			returnMap.putAll (getHashMap (br));
		}
		return returnMap;
	}
	
	public static HashMap <String, HashMap<String, Double>> readWVFileFromHDFS( String filename ) throws FileNotFoundException, IOException {
		return readWVFileFromHDFS (filename, fs);		
	}
	
	/**
	 *
	 * @param dataSource BufferedReader
	 * @return Map with 2 keys: l2, weights
	 * @throws IOException
	 */
	
	public static HashMap<String, HashMap<String, Double>> getHashMap( BufferedReader dataSource ) throws IOException {
		HashMap <String, HashMap<String, Double>> map = new HashMap <String, HashMap<String, Double>>();
		HashMap <String, Double> weightVector = new HashMap <String, Double>();
		HashMap <String, Double> l2Values = new HashMap <String, Double>();
		String line;
        line = dataSource.readLine();
        while (line != null) {
        	String[] splitLine = line.split("\t");
        	weightVector.put (splitLine[0], Double.parseDouble (splitLine[2]));
        	l2Values.put (splitLine[0], Double.parseDouble (splitLine[1]));
        	line = dataSource.readLine();
        }
        dataSource.close();
        map.put( "l2", l2Values);
        map.put ( "weights", weightVector);
		return map;
	}
	
	
	/**
	 * Converts a hashmap to a string that can be saved in a text file
	 * @param map A HashMap to be converted 
	 * @return String in the format "key:value key:value key:value ..."
	 */
	public static String convertHashMapToString (HashMap<String, Double> map) {
		StringBuilder returnString = new StringBuilder();
		Boolean first = true;
		for (String key : map.keySet()) {
			if (first) {
				first = false;
			}
			else {
				returnString.append (" ");
			}
			returnString.append (key);
			returnString.append (":");
			returnString.append (map.get (key));
		}
		return returnString.toString();
	}
	
	/**
	 * 
	 * @param HDFSPath Path in HDFS that contains text files
	 * @return Number of lines in all files
	 * @throws FileNotFoundException 
	 * @throws IOException
	 */
	
	public static Integer getNumberOfLinesInFolder (Path HDFSPath, FileSystem fileSys) throws FileNotFoundException, IOException {
		Integer count = 0;
		String line = "";
		RemoteIterator<LocatedFileStatus> folderContent = fileSys.listFiles (HDFSPath, false);
		
		while (folderContent.hasNext()) {
			FSDataInputStream HDFSFileStream = fileSys.open (folderContent.next().getPath());	// opens file stream from HDFS
			BufferedReader br = new BufferedReader (new InputStreamReader (HDFSFileStream));	// does the actual reading

			while ( (line = br.readLine()) != null) {
			    count++;
			}
			
			br.close();
		}
			
		return count;
	}
	
	public static Integer getNumberOfLinesInFolder( Path HDFSPath ) throws IOException {
		return getNumberOfLinesInFolder (HDFSPath, fs);
	}
	
	/**
	 * Fetches the latest weight vector from HDFS and saves it to the local file system 
	 * @param outFolder output folder of the JobConf.OutputPath, must contain part-00000 file with reducers' results
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void writeInitializedVector (Path outFolder, FileSystem fileSys, int NumberOfFeatures) throws FileNotFoundException, IOException {
		HashMap <String, HashMap<String, Double>> epocheData = readWVFileFromHDFS (outFolder.toString(), fileSys);
		
		FeatureSelector shrinkThings = new FeatureSelector (epocheData.get ("weights"), epocheData.get ("l2"));	// Saves only top k features
		
		BufferedWriter out;
		try {
			out = new BufferedWriter (new FileWriter ("initializedVector"));
			out.write( convertHashMapToString (shrinkThings.getTopKFeatures (NumberOfFeatures)));
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void writeInitializedVector (Path outFolder) throws FileNotFoundException, IOException {
		writeInitializedVector (outFolder, fs, topKFeatures);
	}
	
	/**
	 * 
	 * @param currentEpoch current epoch of the perceptron
	 * @param pathIn path to input files/corpus
	 * @param pathOut prefix of the output path (will be [pathOut]_e[currentEpoch])
	 * @throws Exception
	 */
	
	public static void runHadoopJob( Integer currentEpoch, String pathIn, String pathOut ) throws Exception {
		
		System.out.println( "#####################################");
		System.out.println( "Initializing Epoch " + currentEpoch.toString());
		System.out.println( "#####################################");
	
		Configuration conf = new Configuration();
				
	    Path inFile = new Path (pathIn);
	    Path outFile = new Path (pathOut + "_e" + currentEpoch.toString());

	    Path hdfsPath = new Path (weightVectorFile);
	    String localFile;
	    
	    if (fs == null) {	// if fs == null it's the first epoch
	    	fs = FileSystem.get (conf);	// creates fs-object
	    	localFile = "emptyVector";	// first epoch: weight vector has to be initialized with zeros
	    }
	    else {
	    	localFile = "initializedVector";	// not-first epoch: use already initialized vector from local file system
	    }
	    
	    
	    if (numberOfShards == null) {	// it's enough to count the number of shards once
	    	numberOfShards = getNumberOfLinesInFolder (inFile);	// Must be run AFTER fs was instanciated!
	    }
	    
	    
	    conf.set ("learningRate","1.0E-4");	// data to be accessed by map and reducer instances, "test" => key; "hallo" => value
	    conf.set ("numberOfShards", numberOfShards.toString());
	    

	    Job hdpJob = new Job (conf);
	    
	    hdpJob.setJobName("Hadoop Perceptron Training - SoftPro Grp 1 - Epoch " + currentEpoch.toString());
	    hdpJob.setOutputKeyClass(Text.class);		// data type for map's output key
	    hdpJob.setOutputValueClass(DoubleWritable.class);	// data type for map's output value
	    	    
	    hdpJob.setJarByClass(HadoopTrainPerceptron.class);
	    
	    hdpJob.setMapperClass(Map.class);			// which class implements the Mapper?
	    hdpJob.setReducerClass(Reduce.class);		// which class implements the Reducer?

	    hdpJob.setInputFormatClass(KeyValueTextInputFormat.class);	// files read by line; each line = one map instance; line split in "key	value"
	    hdpJob.setOutputFormatClass(TextOutputFormat.class);		// save result of reducers as text

	    
	    FileInputFormat.addInputPath (hdpJob, inFile);		// first command line argument used as input path
	    FileOutputFormat.setOutputPath (hdpJob, outFile);	// second command line argument used as output path
	    	    
	    
	    fs.copyFromLocalFile (new Path (localFile), hdfsPath);	// copy existing weight vector file from local filesystem to hdfs
	    DistributedCache.addCacheFile(hdfsPath.toUri(), conf);	// add file to distributed cache
		
	    hdpJob.waitForCompletion (true);	// wait until all jobs completed
	    writeInitializedVector (outFile);	// save the new weight vector to a file in the local file system
	}
	
	
	
	/**
	 * 
	 * @author mirko
	 *
	 */
	public static class Map extends Mapper<Text, Text, Text, DoubleWritable> {
		/*
		 * Implementation of the Hadoop Mapper-Class
		 * Trains an initialized perceptron on one shard of data
		 */
			 
	    private static double learningRate;	// value set in configure(), used to store passed data
	    private static Perceptron p;
	    private static HashMap <String, Double> initializedWeightVector;
	    private static HashMap <String, Double> trainedPerceptron;
	    
	    /*
	     * (non-Javadoc)
	     * @see org.apache.hadoop.mapred.MapReduceBase#configure(org.apache.hadoop.mapred.JobConf)
	     */
	    public void setup (Context context) {
	    	/*
	    	 * used to read parameters from JobConf
	    	 * important to get data from previously trained perceptrons
	    	 */ 
			Configuration config = context.getConfiguration();
	        learningRate = Double.parseDouble (config.get ("learningRate"));							// get learning rate specified in JobConf
    		try {
				for( Path cacheFile : DistributedCache.getLocalCacheFiles (config) ) {
					System.out.println( "  Cache file: " + cacheFile.getName().toString() );
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
	        try {
	            String wvCacheName = new Path (weightVectorFile).getName();
		        Path [] cacheFiles = DistributedCache.getLocalCacheFiles (config);
		        if (null != cacheFiles && cacheFiles.length > 0) {
		        	for (Path cachePath : cacheFiles) {
		        		if (cachePath.getName().endsWith(wvCacheName)) {
		        			readWeightVector (cachePath);
		        			break;
		        		}
		        	}
		        }
	    	} 
	    	catch (IOException ioe) {
	    		System.err.println("IOException reading from distributed cache");
	    		System.err.println(ioe.toString());
	    	}
	    }
	    /**
	     * 
	     * @param cachePath path and filename of the weight vector file in local filesystem (fetched via distributed cache)
	     * @throws IOException
	     */
	    void readWeightVector (Path cachePath) throws IOException {
	    	BufferedReader reader = new BufferedReader (new FileReader (cachePath.toString()));
	    	try {
	    		initializedWeightVector = convertStringToHashmap (reader.readLine());
	    	} 
	    	finally {
	    		reader.close(); 
	    	}
	    	
	    }

	    public void map(Text category, Text rawInput, Context context) throws IOException, InterruptedException {
	    	/*
	    	 * Implements the mapping method;
	    	 */
	    	p = new Perceptron (Double.toString (learningRate));
	    	p.setWeights (initializedWeightVector);
	    	
	    	ArrayList<Instance> trainInstances = CreateInstances.createInstancesFromString( rawInput.toString() );	// converts raw input to a datatype that can be used by the perceptron
	    				
	    	trainedPerceptron = p.trainMulti (trainInstances, 1);	// trains perceptron
	    	
	    	for( String key : trainedPerceptron.keySet()) {	
	    		Double value = trainedPerceptron.get (key);

	    		// Convert to hadoop data types
	    		Text hadoopKey = new Text();
	    		DoubleWritable hadoopValue = new DoubleWritable(); 
	    		
	    		hadoopKey.set (key);
	    		hadoopValue.set (value);
	    		
	    		context.write (hadoopKey, hadoopValue);	// return a key-value pair for each feature
	    	}
	    	
	    	/* NICE TO REMEMBER:
	    	 *
	    	 * Get filename of source currently processing:	    	
	    	 * String fileName = "filename: " + fileSplit.getPath().getName() + " key: " + einSchluessel + "";
	    	 */
	    }
	    
		/**
		 * Converts a string to a hash map; used to parse the weight vector from the distributed cache to a hashmap  
		 * @param input String in the format "key:value key:value key:value ..."
		 * @return HashMap containing the features and their values to be used by Perceptron.setWeights()
		 */
		public static HashMap<String, Double> convertStringToHashmap (String input) {
			HashMap<String, Double> map = new HashMap<String, Double>();
			try {
				for (String pair : input.split (" ")) {
					String[] splitPair = pair.split (":", 2);
					map.put (splitPair[0], splitPair.length == 1 ? 1.234 : Double.parseDouble (splitPair[1]));

				}
			}
			catch (NullPointerException e) {
				System.err.println ("NullPointerException: convertStringToHashmap");
				System.err.println ("input = '" + input + "'");
				return map;
			}
			return map;
		}
	}
	
	
	/**
	 * 
	 * @author mirko
	 *
	 */
	public static class Reduce extends Reducer<Text, DoubleWritable, Text, Text> {
		
		public static Integer numberOfShards;
		
		public void setup (Context context) {
	    	/*
	    	 * used to read parameters from JobConf
	    	 * important to get the number of shards
	    	 */ 
			Configuration conf = context.getConfiguration();
			numberOfShards = Integer.parseInt (conf.get ("numberOfShards"));	// get the number of shards
		}
	       
		public void reduce(Text key, Iterator<DoubleWritable> values, Context context) throws IOException, InterruptedException {
			// l2-norm = sqrt (sum ((feature's value)^2)) 
			Double sum_pow = 0.0; 
			Double sum = 0.0;
			
	        while (values.hasNext()) {
	        	Double aValue = values.next().get();
	        	sum_pow += Math.pow (aValue, 2); // sum ((feature's value)^2)
	        	sum += aValue;
	      	}
	        

	        Double l2 = Math.sqrt (sum_pow);	// calculate square root
	        Double mean = sum / numberOfShards;			// calculate mean
	        
	        // convert to hadoop data type
	        Text returnValue = new Text( new String( l2.toString() + "	" + mean.toString()));
	        context.write (key, returnValue);
		}
	}
}
