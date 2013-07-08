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
	
	private static FileSystem fs;					// used to access the hadoop file system, to get the trained weight vector
	private static int numberOfEpochs = 10;			// number of epochs for perceptron training
	private static int topKFeatures = 10;			// number of top k features to select after training
	private static String newLearningRate = "-4";	// learning rate passed as string
	private static String weightVectorFile = "weightVector.txt";	// file to save the trained weight vector in hdfs after each epoch
	private static Integer numberOfShards; 			// number of shards that the corpus is split into; = number of categories
	private static String[] categoryNames;			// names of the categories
	
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
				categoryNames =  args[5].split(";");
			}
		}
		if (args.length > 6) {				// if args[6] is set to anything, random shards should be created; no perceptron training, only corpus creation!
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
		System.out.println ( "		categories: " + args[5] + " (" + categoryNames.length + ")");
		System.out.println ( "" );
		System.out.println ( "	Sit back and relax!");
		
		for ( int e = 1; e <= numberOfEpochs; e++ ) {		// start with epoch=1, rerun hadoop job for each epoch
			runHadoopJob (e, args[0], args[1]);			
		}
	}

	/**
	 * 
	 * @param pathIn Path containing the corpus files with "old"/natural category names
	 * @param pathOut Path to save the new corpus files to
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws InterruptedException
	 */
	public static void createRandomShards (String pathIn, String pathOut) throws IOException, ClassNotFoundException, InterruptedException {
		System.out.println (" Creating random shards...");
		Path inFile = new Path (pathIn);				// Input path for hadoop job
		Path outFile = new Path (pathOut);				// Output path for hadoop job
		
		long startTimeRS = System.currentTimeMillis();	// just some log information ;) how much time does the job take?
		
		Configuration confRS = new Configuration();		// sometimes used to pass parameters to mapper and reducer (e.g. confRS.set (String, String))
		
		Job jobRS = new Job (confRS);					// Instanciate hadoop job, tell it to use the Configuration object
		
		jobRS.setJarByClass (HadoopTrainPerceptronScalable.class);	// Tell hadoop where to find the mapper.class and reducer.class
		jobRS.setJobName ("SWP Grp 1 - RandomShards");				// title of the hadoop job, to be found in jobtracker
		jobRS.setOutputKeyClass (Text.class);						// data type for map's output key
		jobRS.setOutputValueClass (Text.class);						// data type for map's output value
	    	    
		jobRS.setInputFormatClass (KeyValueTextInputFormat.class);	// files read by line; each line = one map instance; line split in "key	value"
		jobRS.setOutputFormatClass (TextOutputFormat.class);		// save result of reducers as text

		jobRS.setMapperClass (RandomShardsMapper.class);				// which class to use as mapper? 
		jobRS.setReducerClass (RandomShardsReducer.class);				// which class to use as reducer?
		
		FileInputFormat.setInputPaths (jobRS, inFile);					// set the input path
		FileOutputFormat.setOutputPath (jobRS, outFile);				// set the output path, where to save the NamedOutput files
		
		
		// adds an output file for each category name passed in args[]
		for (String category : categoryNames) {
			MultipleOutputs.addNamedOutput (jobRS, category, TextOutputFormat.class, Text.class, Text.class);
		}
		
		System.out.println ("Starting hadoop job NOW");
		jobRS.waitForCompletion (true);									// wait until hadoop job has completed
		long stopTimeRS = System.currentTimeMillis();
		long totalTimeRS = stopTimeRS - startTimeRS;
		System.out.println ("Took " + totalTimeRS + "ms");
	}
	
	/**
	 * 
	 * @param currentEpoch passes the current epoch to hadoop job; important for perceptron training
	 * @param pathIn path containing the corpus files
	 * @param pathOut path to save intermediate results in (must be unique!), epoch and phase information will be appended automatically
	 * @throws Exception
	 * 
	 * This class runs two hadoop jobs. The first job trains an average perceptron for each category. The second job calculates the l2 norm and the average value of each of the weight vector's features.
	 * After each epoch, the weight vector is saved into the local file system and then is subject of top-k-feature selection.  
	 */

	public static void runHadoopJob (Integer currentEpoch, String pathIn, String pathOut ) throws Exception {
		
		System.out.println( "#####################################");
		System.out.println( "    Phase 1 (Epoch " + currentEpoch.toString() + ")");
		System.out.println( "#####################################");
		Configuration confOne = new Configuration();			// Configuration for the first phase
		
	    Path inFile = new Path (pathIn);						// path containing the corpus files
	    Path outFile = new Path (pathOut + "_e" + currentEpoch.toString());		// output path for phase 1, will be used as input path for phase 2
	    Path out2File = new Path (pathOut + "2_e" + currentEpoch.toString());	// output path for phase 2
	    
	    Path hdfsPath = new Path (weightVectorFile);		// used for DistributedCache: in which file is the trained weight vector saved?
	    String localFile;									// path of the weight vector file in local filesystem
	    
	    if (fs == null) {					// if fs == null it's the first epoch
	    	fs = FileSystem.get (confOne);	// creates filesystem-object
	    	localFile = "emptyVector";		// first epoch: weight vector has to be initialized with zeros
	    }
	    else {
	    	localFile = "initializedVector";	// not-first epoch: use already initialized vector from local file system
	    }
	    
	    
	    if (numberOfShards == null) {				// it's enough to count the number of shards once
	    	numberOfShards = categoryNames.length;	// set the number of shards
	    }
	    
	    // Strings that can be accessed by mapper and reducers
	    confOne.set ("learningRate", newLearningRate);				// learning rate for perceptron training
	    confOne.set ("numberOfShards", numberOfShards.toString());	// number of shards; important for l2 regularization
	    confOne.set ("currentEpoch", currentEpoch.toString());   	// current epoch for perceptron training
	    
	    fs.copyFromLocalFile (new Path (localFile), hdfsPath);		// copy existing weight vector file from local filesystem to hdfs
	    DistributedCache.addCacheFile (hdfsPath.toUri(), confOne);	// add file to distributed cache
		
	    Job jobOne = new Job (confOne);								// job for phase 1
		
	    jobOne.setJarByClass (HadoopTrainPerceptronScalable.class);
		jobOne.setJobName ("SWP Grp 1 - E" + currentEpoch.toString() + " P1");
		jobOne.setOutputKeyClass (Text.class);		// data type for map's output key
		jobOne.setOutputValueClass (MapWritable.class);	// data type for map's output value
	    
		jobOne.setInputFormatClass (KeyValueTextInputFormat.class);	// files read by line; each line = one map instance; line split in "key	value"
		jobOne.setOutputFormatClass (TextOutputFormat.class);		// save result of reducers as text

		jobOne.setMapperClass(mapTrainSmallPerceptrons.class);		// map class of the first hadoop job, trains several small average perceptrons		
		jobOne.setReducerClass(reduceUnitePerceptrons.class);		// creates one average perceptron of all "small" perceptrons per category 
		
		FileInputFormat.setInputPaths (jobOne, inFile);				// folder that contains our corpus
		FileOutputFormat.setOutputPath (jobOne, outFile);			// folder that our weight vectors will be written to

		// create one weight vector file per category:
		for (String category : categoryNames) {
			MultipleOutputs.addNamedOutput(jobOne, category, TextOutputFormat.class, Text.class, DoubleWritable.class);	
		}
		
		long startTime = System.currentTimeMillis();
		jobOne.waitForCompletion (true);							// wait for job one to complete
		long stopTime = System.currentTimeMillis();
		long totalTime = stopTime - startTime;
		System.out.println ("Took " + totalTime + "ms");
		
		Configuration confTwo = new Configuration();				// Configuration for job two
		confTwo.set ("learningRate", confOne.get ("learningRate"));	// learning rate [currently unused!]
		confTwo.set ("numberOfShards", confOne.get ("numberOfShards"));	// number of shards/categories that were processed -> important for l2 and average calculation

		
		Job jobTwo = new Job (confTwo);								// Job for phase 2
		
		jobTwo.setJarByClass (HadoopTrainPerceptronScalable.class);
		jobTwo.setJobName ("SWP Grp 1 - E" + currentEpoch.toString() + " P2");
		jobTwo.setOutputKeyClass (Text.class);		// data type for map's output key
		jobTwo.setOutputValueClass (MapWritable.class);	// data type for map's output value
	    	    
		jobTwo.setInputFormatClass (KeyValueTextInputFormat.class);	// files read by line; each line = one map instance; line split in "key	value"
		jobTwo.setOutputFormatClass (TextOutputFormat.class);		// save result of reducers as text

		jobTwo.setMapperClass(mapOrderThings.class);				// map class of second hadoop job, gets the features and combines them with the category [assigning the category actually is not necessary for further processing; the main purpose of the mapper is to pass the features to the reducer.]
		jobTwo.setReducerClass(reduceCalculateThings.class);		// calculates l2 norm and the average value of each feature
		
		RemoteIterator<LocatedFileStatus> fileList = fs.listFiles(outFile, false);	// get a list of files inside phase one's output directory 
		
		// only add those files to input, that are ment to be processed. On some systems, hadoop saves log files inside the output directory, so we have to tell our system, not to use the logs.
		while (fileList.hasNext()) {
			Path currentFile = fileList.next().getPath();
			String currentFilename = currentFile.getName();
			if (!currentFilename.startsWith("_") && !currentFilename.startsWith(".") && !currentFilename.startsWith("part")) {	// log files start with "_", temporary files start with ".", uncategorized (empty) files start with "part"
				FileInputFormat.addInputPath(jobTwo, currentFile);	// add file to input queue
			}
		}
		
		FileOutputFormat.setOutputPath (jobTwo, out2File);			// set output path of phase 2
		
		System.out.println( "#####################################");
		System.out.println( "    Phase 2 (Epoch " + currentEpoch.toString() + ")");
		System.out.println( "#####################################");
		long startTime2 = System.currentTimeMillis();
		jobTwo.waitForCompletion (true);							// wait until phase 2 completed
		long stopTime2 = System.currentTimeMillis();
		long totalTime2 = stopTime2 - startTime2;
		System.out.println ("Took " + totalTime2 + "ms");
		
	    HadoopTrainPerceptron.writeInitializedVector (out2File, fs, topKFeatures);	// save the new weight vector to a file in the local file system and do top k feature extraction
	}

	/**
	 * Mapper class of phase 1
	 * See description of {@link runHadoopJob}
	 * @author mirko
	 *
	 */
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
			
	        this.learningRate = config.get ("learningRate");				// get learning rate specified in JobConf
	        this.currentEpoch = config.get ("currentEpoch");				// get the number of the current epoch as passed in JobConf

	        try {
	            String wvCacheName = new Path (weightVectorFile).getName();			// get the specified name of the file in the distributed cache that contains the already trained weight vector  
		        Path [] cacheFiles = DistributedCache.getLocalCacheFiles (config);	// get an array of files that are stored in distributed cache 
		        if (null != cacheFiles && cacheFiles.length > 0) {
		        	for (Path cachePath : cacheFiles) {						// iterate through files in distributed cache
		        		if (cachePath.getName().endsWith (wvCacheName)) {	// filenames in distributed cache look like "/tmp/hadoop-[user]/namenode01.lan[actual filename]". Therefore, we can only compare the d.c. filename's ending with the weight vector filename
		        			readWeightVector (cachePath);					// save weight vector into hashmap	
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
	    
	    /**
	     * @param key 	Key of the split, usually a category name
	     * @param value	Value of the split, should be in the format  "feature:value feature:value [...] #label#:[positive|negative]"
	     */
	    protected void map(Text key, Text value, Context context) throws IOException {

			if (!this.perceptrons.containsKey (key.toString())) {
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
	
	public static class RandomShardsMapper extends Mapper <Text, Text, Text, Text> {
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
	
	public static class RandomShardsReducer extends Reducer <Text, Text, Text, Text> {
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
