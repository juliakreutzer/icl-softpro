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
import org.apache.hadoop.mapreduce.lib.partition.*;

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
	private static String newLearningRate = "-4";
	private static String weightVectorFile = "weightVector.txt";
	private static Integer numberOfShards; 
	private static String categoryNames;
	
	public static void main(String[] args) throws Exception {
		System.out.println ( "-------------------------------------------------------------");
		System.out.println ( "de.uni-heidelberg.cl.softpro.sentimentclassification.scalable");
		System.out.println ( "-------------------------------------------------------------");
		System.out.println ( " Copyright (C) Jasmin Schröck, Julia Kreutzer, Mirko Hering");
		System.out.println ( "");
		
		if (args.length == 0) {
			System.out.println ( "hadoop jar scalable.jar [input folder] [output folder] [number of epochs] [number of top features] [learningRate] [categoryNames] [random?]");
			System.exit(0);
		}
		if (args.length > 2) {
			if ( args[2] != null) {
				numberOfEpochs = Integer.parseInt (args[2]);
			}
		}
		if (args.length > 3) {
			if ( args[3] != null) {
				topKFeatures = Integer.parseInt (args[3]);
			}
		}
		if (args.length > 4) {
			if ( args[4] != null) {
				newLearningRate =  args[4];
			}
		}
		if (args.length > 5) {
			if ( args[5] != null) {
				categoryNames =  args[5];
			}
		}
		if (args.length > 6) {
			if (args [6] != null) {
				createRandomShards (args[0], args[1]);
				System.exit(0);
			}
		}

		System.out.println ( "    Started Hadoop MapReduce Scalable Perceptron Training    ");
		System.out.println ( "-------------------------------------------------------------");
		System.out.println ( "	Parameters:");
		System.out.println ( "		number of epochs:    " + new Integer(numberOfEpochs).toString());
		System.out.println ( "		top k features:      " + new Integer(topKFeatures).toString());
		System.out.println ( "		learning rate:      " + newLearningRate);
		System.out.println ( "		name of vector file: " + weightVectorFile);
		System.out.println ( "		categories: " + categoryNames);
		System.out.println ( "" );
		System.out.println ( "	Sit back and relax!");
		
		for ( int e = 0; e < numberOfEpochs; e++ ) {
			runHadoopJob (e, args[0], args[1]);			
		}
	}

	public static void createRandomShards (String pathIn, String pathOut) throws IOException, ClassNotFoundException, InterruptedException {
		System.out.println (" Creating random shards...");
		Path inFile = new Path (pathIn);
		Path outFile = new Path (pathOut);
		
		long startTimeRS = System.currentTimeMillis();
		
		Configuration confRS = new Configuration();
//		confRS.set("mapred.job.tracker", "local");
		
		Job jobRS = new Job (confRS);
		
		jobRS.setJarByClass (HadoopTrainPerceptronScalable.class);
		jobRS.setJobName ("SWP Grp 1 - RandomShards");
		jobRS.setOutputKeyClass (Text.class);		// data type for map's output key
		jobRS.setOutputValueClass (Text.class);	// data type for map's output value
	    	    
		jobRS.setInputFormatClass (KeyValueTextInputFormat.class);	// files read by line; each line = one map instance; line split in "key	value"
		jobRS.setOutputFormatClass (TextOutputFormat.class);		// save result of reducers as text

		jobRS.setMapperClass(einMapper.class);
		jobRS.setReducerClass(einReducer.class);
		
		FileInputFormat.setInputPaths (jobRS, inFile);
		FileOutputFormat.setOutputPath (jobRS, outFile);
		
		MultipleOutputs.addNamedOutput (jobRS, "0", TextOutputFormat.class, Text.class, Text.class);
		MultipleOutputs.addNamedOutput (jobRS, "1", TextOutputFormat.class, Text.class, Text.class);
		MultipleOutputs.addNamedOutput (jobRS, "2", TextOutputFormat.class, Text.class, Text.class);
		MultipleOutputs.addNamedOutput (jobRS, "3", TextOutputFormat.class, Text.class, Text.class);
		
		System.out.println ("Starting hadoop job NOW");
		jobRS.waitForCompletion (true);
		long stopTimeRS = System.currentTimeMillis();
		long totalTimeRS = stopTimeRS - startTimeRS;
		System.out.println ("Took " + totalTimeRS + "ms");	
	}
	
	public static void runHadoopJob (Integer currentEpoch, String pathIn, String pathOut ) throws Exception {
		
		System.out.println( "#####################################");
		System.out.println( "    Phase 1 (Epoch " + currentEpoch.toString() + ")");
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
	    
	    confOne.set ("learningRate", newLearningRate);	// data to be accessed by map and reducer instances, "test" => key; "hallo" => value
	    confOne.set ("numberOfShards", numberOfShards.toString());
	    confOne.set ("currentEpoch", currentEpoch.toString());   
	    
	    fs.copyFromLocalFile (new Path (localFile), hdfsPath);	// copy existing weight vector file from local filesystem to hdfs
	    DistributedCache.addCacheFile (hdfsPath.toUri(), confOne);	// add file to distributed cache
		
	    Job jobOne = new Job (confOne);
		
	    jobOne.setJarByClass (HadoopTrainPerceptronScalable.class);
		jobOne.setJobName ("SWP Grp 1 - E" + currentEpoch.toString() + " P1");
		jobOne.setOutputKeyClass (Text.class);		// data type for map's output key
		jobOne.setOutputValueClass (MapWritable.class);	// data type for map's output value
	    
		jobOne.setInputFormatClass (KeyValueTextInputFormat.class);	// files read by line; each line = one map instance; line split in "key	value"
		jobOne.setOutputFormatClass (TextOutputFormat.class);		// save result of reducers as text

		jobOne.setMapperClass(mapTrainSmallPerceptrons.class);
		jobOne.setReducerClass(reduceUnitePerceptrons.class);
		
		FileInputFormat.setInputPaths (jobOne, inFile);
		FileOutputFormat.setOutputPath (jobOne, outFile);
		
		for (String category : categoryNames.split (";")) {
			MultipleOutputs.addNamedOutput(jobOne, category, TextOutputFormat.class, Text.class, DoubleWritable.class);
		}
		
		long startTime = System.currentTimeMillis();
		jobOne.waitForCompletion (true);
		long stopTime = System.currentTimeMillis();
		long totalTime = stopTime - startTime;
		System.out.println ("Took " + totalTime + "ms");
		
		Configuration confTwo = new Configuration();
		confTwo.set ("learningRate", confOne.get ("learningRate"));	// data to be accessed by map and reducer instances, "test" => key; "hallo" => value
		confTwo.set ("numberOfShards", confOne.get ("numberOfShards"));

		
		Job jobTwo = new Job (confTwo);
		
		jobTwo.setJarByClass (HadoopTrainPerceptronScalable.class);
		jobTwo.setJobName ("SWP Grp 1 - E" + currentEpoch.toString() + " P2");
		jobTwo.setOutputKeyClass (Text.class);		// data type for map's output key
		jobTwo.setOutputValueClass (MapWritable.class);	// data type for map's output value
	    	    
		jobTwo.setInputFormatClass (KeyValueTextInputFormat.class);	// files read by line; each line = one map instance; line split in "key	value"
		jobTwo.setOutputFormatClass (TextOutputFormat.class);		// save result of reducers as text

		jobTwo.setMapperClass(mapOrderThings.class);
		jobTwo.setReducerClass(reduceCalculateThings.class);
		
		RemoteIterator<LocatedFileStatus> fileList = fs.listFiles(outFile, false);
		
		while (fileList.hasNext()) {
			Path currentFile = fileList.next().getPath();
			String currentFilename = currentFile.getName();
			if (!currentFilename.startsWith("_") && !currentFilename.startsWith(".") && !currentFilename.startsWith("part")) {
				FileInputFormat.addInputPath(jobTwo, currentFile);
			}
		}
		
		FileOutputFormat.setOutputPath (jobTwo, out2File);
		
		System.out.println( "#####################################");
		System.out.println( "    Phase 2 (Epoch " + currentEpoch.toString() + ")");
		System.out.println( "#####################################");
		long startTime2 = System.currentTimeMillis();
		jobTwo.waitForCompletion (true);
		long stopTime2 = System.currentTimeMillis();
		long totalTime2 = stopTime2 - startTime2;
		System.out.println ("Took " + totalTime2 + "ms");
		
	    HadoopTrainPerceptron.writeInitializedVector (out2File, fs, topKFeatures);	// save the new weight vector to a file in the local file system
	}

	public static class mapTrainSmallPerceptrons extends Mapper<Text, Text, Text, MapWritable> {
		private HashMap <String, Perceptron> perceptrons = new HashMap <String, Perceptron> ();
		private String learningRate;	// value set in configure(), used to store passed data
		private String currentEpoch;
		
		private HashMap <String, Double> initializedWeightVector;
		
		public void run (Context context) throws IOException, InterruptedException {
			setup(context);
			while (context.nextKeyValue()) {
				map (context.getCurrentKey(), context.getCurrentValue(), context);
			}
			cleanup (context);
		}
		
		protected void setup (Context context) {
	    	/*
	    	 * used to read parameters from JobConf
	    	 * important to get data from previously trained perceptrons
	    	 */
			Configuration config = context.getConfiguration();
			
	        this.learningRate = config.get ("learningRate");							// get learning rate specified in JobConf
	        this.currentEpoch = config.get ("currentEpoch");
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
	    		this.initializedWeightVector = convertStringToHashmap (reader.readLine());
	    	} 
	    	finally {
	    		reader.close();
	    	}
	    	
	    }
	    
	    protected void map(Text key, Text value, Context context) throws IOException {

			if (!this.perceptrons.containsKey(key.toString())) {
				Perceptron newPerceptron = new Perceptron (this.learningRate);
				newPerceptron.setWeights (this.initializedWeightVector);
				this.perceptrons.put (key.toString(), newPerceptron);
			}
			Perceptron currentPerceptron = this.perceptrons.get (key.toString());
			ArrayList<Instance> trainInstance = CreateInstances.createInstancesFromString (value.toString());	// converts raw input to a datatype that can be used by the perceptron
			
			currentPerceptron.setWeights (currentPerceptron.trainMulti (trainInstance, Integer.parseInt (currentEpoch)));
			this.perceptrons.put (key.toString(), currentPerceptron);
		}
		
		protected void cleanup (Context context) throws IOException, InterruptedException {
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
		private HashMap<String, Double> convertStringToHashmap (String input) {
			HashMap<String, Double> map = new HashMap<String, Double>();
			try {
				for (String pair : input.split (" ")) {
					String[] splitPair = pair.split (":", 2);
					map.put (splitPair[0], splitPair.length == 1 ? 1.234 : Double.parseDouble (splitPair[1]));

				}
			}
			catch (NullPointerException e) {
				System.err.println ("NullPointerException: convertStringToHashmap (this may happen during first run)");
				System.err.println ("input = '" + input + "'");
				return map;
			}
			return map;
		}
	}
	
	public static class reduceUnitePerceptrons extends Reducer <Text, MapWritable, Text, DoubleWritable> {
		public void reduce(Text key, Iterable<MapWritable> values, Context context) throws IOException, InterruptedException {
			MultipleOutputs <Text, DoubleWritable> mos = new MultipleOutputs<Text, DoubleWritable> (context);
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
	
	public static class mapOrderThings extends Mapper <Text, Text, Text, MapWritable> {
		public void map (Text key, Text value, Context context) throws IOException, InterruptedException {
			MapWritable output = new MapWritable();
			FileSplit fileSplit = (FileSplit) context.getInputSplit();
			String fileName = fileSplit.getPath().getName().toString();
			Text category = new Text (fileName.split ("-")[0]);
			
			output.put (categoryLabel, category);
			output.put (valueLabel, value);
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
	
	public static class einMapper extends Mapper <Text, Text, Text, Text> {
		private int i = 0;
		
		public void map (Text key, Text value, Context context) throws IOException, InterruptedException {
			System.out.println ("[m]  Writing context for " + value.toString() + " as " + Integer.toString (i));
			context.write (new Text (Integer.toString (i)), value);
			i++;
			if (i>3) {
				System.out.println ("[m]  Resetting i to 0");
				i = 0;
			}
		}
	}
	
	public static class einReducer extends Reducer <Text, Text, Text, Text> {
		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			MultipleOutputs <Text, Text> mos = new MultipleOutputs<Text, Text> (context);
			for (Text value : values) {
				System.out.println ("[r]  Writing context");
				mos.write (key, value, key.toString());
			}
			mos.close();
		}
	}
}
