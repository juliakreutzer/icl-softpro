package de.uniheidelberg.cl.softpro.sentimentclassification.scalable;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.jobcontrol.ControlledJob;
import org.apache.hadoop.mapreduce.lib.jobcontrol.JobControl;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import de.uniheidelberg.cl.softpro.sentimentclassification.CreateInstances;
import de.uniheidelberg.cl.softpro.sentimentclassification.HadoopTrainPerceptron;
import de.uniheidelberg.cl.softpro.sentimentclassification.Perceptron;
//import de.uniheidelberg.cl.softpro.sentimentclassification.HadoopTrainPerceptron.Map;
//import de.uniheidelberg.cl.softpro.sentimentclassification.HadoopTrainPerceptron.Reduce;
//import de.uniheidelberg.cl.softpro.sentimentclassification.scalable.HadoopScalableTest.MapA;
//import de.uniheidelberg.cl.softpro.sentimentclassification.scalable.HadoopScalableTest.MapB;
import de.uniheidelberg.cl.softpro.sentimentclassification.Instance;

public class HadoopTrainPerceptronScalable {
	
	private static FileSystem fs;
	private static int numberOfEpochs = 10;
	private static int topKFeatures = 10;
	private static String weightVectorFile = "weightVector.txt";
	private static Integer numberOfShards; 

	
	public static void main(String[] args) throws Exception {
		System.out.println ( "de.uni-heidelberg.cl.softpro.sentimentclassification.scalable");
		System.out.println ( "-------------------------------------------------------------");
		System.out.println ( " Copyright (C) Jasmin Schröck, Julia Kreutzer, Mirko Hering");
		System.out.println ( "");
		
		if (args.length == 0) {
			System.out.println ( "hadoop jar scalable.jar [input folder] [output folder] [number of epochs] [number of top features]");
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
		System.out.println ( "    Started Hadoop MapReduce Scalable Perceptron Training    ");
		System.out.println ( "-------------------------------------------------------------");
		System.out.println ( "	Parameters:");
		System.out.println ( "		number of epochs:    " + new Integer(numberOfEpochs).toString());
		System.out.println ( "		top k features:      " + new Integer(topKFeatures).toString());
		System.out.println ( "		name of vector file: " + weightVectorFile);
		System.out.println ( "" );
		System.out.println ( "	Sit back and relax!");
		
		for ( int e = 0; e < numberOfEpochs; e++ ) {
			runHadoopJob( e, args[0], args[1]);			
		}
	}

	
	public static void runHadoopJob( Integer currentEpoch, String pathIn, String pathOut ) throws Exception {
		
		System.out.println( "#####################################");
		System.out.println( "Initializing Epoch " + currentEpoch.toString());
		System.out.println( "#####################################");

		Configuration confOne = new Configuration();
		
	    Path inFile = new Path (pathIn);
	    Path outFile = new Path (pathOut + "_e" + currentEpoch.toString());
	    Path out2File = new Path (pathOut + "2_e" + currentEpoch.toString());
	    
	    Path hdfsPath = new Path (weightVectorFile);
	    String localFile;
	    
	    if (fs == null) {	// if fs == null it's the first epoch
	    	fs = FileSystem.get (confOne);	// creates fs-object
	    	localFile = "emptyVector";	// first epoch: weight vector has to be initialized with zeros
	    }
	    else {
	    	localFile = "initializedVector";	// not-first epoch: use already initialized vector from local file system
	    }
	    
	    
	    if (numberOfShards == null) {	// it's enough to count the number of shards once
	    	//numberOfShards = HadoopTrainPerceptron.getNumberOfLinesInFolder (inFile, fs);	// Must be run AFTER fs was instanciated!
	    	numberOfShards = 4;
	    }
	    
	    confOne.set ("learningRate","1.0E-4");	// data to be accessed by map and reducer instances, "test" => key; "hallo" => value
	    confOne.set ("numberOfShards", numberOfShards.toString());
	      	    
	    
	    fs.copyFromLocalFile (new Path (localFile), hdfsPath);	// copy existing weight vector file from local filesystem to hdfs
	    DistributedCache.addCacheFile (hdfsPath.toUri(), confOne);	// add file to distributed cache
		
	    Job jobOne = new Job (confOne);
		
		jobOne.setJobName ("Hadoop Perceptron Training - SoftPro Grp 1 - Epoch " + currentEpoch.toString());
		jobOne.setOutputKeyClass (Text.class);		// data type for map's output key
		jobOne.setOutputValueClass (MapWritable.class);	// data type for map's output value
	    	    
		jobOne.setInputFormatClass (KeyValueTextInputFormat.class);	// files read by line; each line = one map instance; line split in "key	value"
		jobOne.setOutputFormatClass (TextOutputFormat.class);		// save result of reducers as text

		jobOne.setMapperClass(mapTrainSmallPerceptrons.class);
		jobOne.setReducerClass(reduceUnitePerceptrons.class);
		
		FileInputFormat.setInputPaths (jobOne, inFile);
		FileOutputFormat.setOutputPath (jobOne, outFile);
		
		MultipleOutputs.addNamedOutput(jobOne, "dvd", TextOutputFormat.class, Text.class, DoubleWritable.class);
		MultipleOutputs.addNamedOutput(jobOne, "books", TextOutputFormat.class, Text.class, DoubleWritable.class);
		MultipleOutputs.addNamedOutput(jobOne, "electronics", TextOutputFormat.class, Text.class, DoubleWritable.class);
		MultipleOutputs.addNamedOutput(jobOne, "kitchen", TextOutputFormat.class, Text.class, DoubleWritable.class);
		
		
		Configuration confTwo = new Configuration();
		confTwo.set ("learningRate", confOne.get ("learningRate"));	// data to be accessed by map and reducer instances, "test" => key; "hallo" => value
		confTwo.set ("numberOfShards", confOne.get ("numberOfShards"));

		Job jobTwo = new Job (confTwo);
		
		jobTwo.setJobName ("Hadoop Perceptron Training Phase 2 - SoftPro Grp 1 - Epoch " + currentEpoch.toString());
		jobTwo.setOutputKeyClass (Text.class);		// data type for map's output key
		jobTwo.setOutputValueClass (MapWritable.class);	// data type for map's output value
	    	    
		jobTwo.setInputFormatClass (KeyValueTextInputFormat.class);	// files read by line; each line = one map instance; line split in "key	value"
		jobTwo.setOutputFormatClass (TextOutputFormat.class);		// save result of reducers as text

		jobTwo.setMapperClass(mapOrderThings.class);
		jobTwo.setReducerClass(reduceCalculateThings.class);
		
		FileInputFormat.setInputPaths (jobTwo, outFile);
		FileOutputFormat.setOutputPath (jobTwo, out2File);
		
		jobOne.waitForCompletion (true);
		jobTwo.waitForCompletion (true);
	    
	    HadoopTrainPerceptron.writeInitializedVector (out2File, fs);	// save the new weight vector to a file in the local file system
	}

	public static class mapTrainSmallPerceptrons extends Mapper<Text, Text, Text, MapWritable> {
		private HashMap <String, Perceptron> perceptrons = new HashMap <String, Perceptron> ();
		private static double learningRate;	// value set in configure(), used to store passed data
		
		private static HashMap <String, Double> initializedWeightVector;
		
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
		        		if (cachePath.getName().endsWith (wvCacheName)) {
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
	    
		public void map(Text key, Text value, Context context) throws IOException {

			if (!this.perceptrons.containsKey(key.toString())) {
				Perceptron newPerceptron = new Perceptron (learningRate);
				newPerceptron.setWeights (initializedWeightVector);
				this.perceptrons.put (key.toString(), newPerceptron);
			}
			Perceptron currentPerceptron = this.perceptrons.get (key.toString());
			ArrayList<Instance> trainInstance = CreateInstances.createInstancesFromString( value.toString() );	// converts raw input to a datatype that can be used by the perceptron
			
			currentPerceptron.setWeights(currentPerceptron.trainMulti (trainInstance));
			this.perceptrons.put (key.toString(), currentPerceptron);
		}
		
		public void cleanup (Context context) throws IOException, InterruptedException {
			for (String key : this.perceptrons.keySet()) {
				MapWritable map = new MapWritable();
				for ( Map.Entry <String, Double> eintrag : this.perceptrons.get(key).getWeights().entrySet()) {
					map.put (new Text (eintrag.getKey()), new DoubleWritable (eintrag.getValue()));
				}
				context.write (new Text (key), map);
			}
		}
		
		/**
		 * Converts a string to a hash map; used to parse the weight vector from the distributed cache to a hashmap  
		 * @param input String in the format "key:value key:value key:value ..."
		 * @return HashMap containing the features and their values to be used by Perceptron.setWeights()
		 */
		private static HashMap<String, Double> convertStringToHashmap (String input) {
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
	
	public static class reduceUnitePerceptrons extends Reducer <Text, MapWritable, Text, DoubleWritable> {
		public void reduce(Text key, Iterable<MapWritable> values, Context context) throws IOException, InterruptedException {
			MultipleOutputs <Text, DoubleWritable> mos = new MultipleOutputs (context);
			HashMap <Text, DoubleWritable> unitedWeightVector = new HashMap <Text, DoubleWritable>();
			Integer numberOfPerceptrons = 0;
			for ( MapWritable partResult : values) {
				++numberOfPerceptrons;
				
				// Adds up all perceptrons' values:
				for ( Map.Entry <Writable, Writable> entry : partResult.entrySet()) {
					Writable currentKey = entry.getKey();
					Writable currentValue = entry.getValue();
					
					if (unitedWeightVector.keySet().contains (currentKey)) {
						DoubleWritable newValue = new DoubleWritable (Double.parseDouble (unitedWeightVector.get (currentKey).toString()) + Double.parseDouble (currentValue.toString()));
						unitedWeightVector.put ( (Text)currentKey, newValue);
					}
					else {
						DoubleWritable newValue = new DoubleWritable (Double.parseDouble (currentValue.toString()));
						unitedWeightVector.put ( (Text)currentKey, newValue);
					}
				}
			}
			
			// Calculates the mean of the perceptrons by dividing the added values by the number of perceptrons
			for ( Entry<Text, DoubleWritable> entry : unitedWeightVector.entrySet()) {
				Text currentKey = entry.getKey();
				DoubleWritable currentValue = entry.getValue();
				DoubleWritable newValue = new DoubleWritable (Double.parseDouble (currentValue.toString()) / numberOfPerceptrons);
				unitedWeightVector.put (currentKey, newValue);
			}
						
			for ( Entry<Text, DoubleWritable> entry : unitedWeightVector.entrySet()) {
				Text currentKey = (Text)entry.getKey();
				DoubleWritable currentValue = (DoubleWritable)entry.getValue();
				mos.write (currentKey, currentValue, key.toString());
			}
			mos.close();
		}
	}
	
	
	public static Text categoryLabel = new Text ("cat");
	public static Text valueLabel = new Text ("val");
	
	public static class mapOrderThings extends Mapper <Text, DoubleWritable, Text, MapWritable> {
		public void map (Text key, DoubleWritable value, Context context) throws IOException, InterruptedException {
			MapWritable output = new MapWritable();
			FileSplit fileSplit = (FileSplit) context.getInputSplit();
			String fileName = fileSplit.getPath().getName().toString();
			Text category = new Text (fileName.split ("-")[0]);
			
			output.put (categoryLabel, category);
			output.put (valueLabel, new Text (value.toString()));
			
			context.write (key, output);
		}
	}
	
	public static class reduceCalculateThings extends Reducer <Text, MapWritable, Text, Text> {
		public void reduce(Text key, Iterable<MapWritable> values, Context context) throws IOException, InterruptedException {
			Configuration conf = context.getConfiguration();
			Double numberOfShards = Double.parseDouble (conf.get ("numberOfShards"));
			
			// l2-norm = sqrt (sum ((feature's value)^2)) 
			Double sum_pow = 0.0; 
			Double sum = 0.0;
			
	        for ( MapWritable value : values ) {
	        	Double aValue = Double.parseDouble (value.get (valueLabel).toString());
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
