package de.uniheidelberg.cl.softpro.sentimentclassification;
/**
 * Uses perceptron algorithm inside Hadoop MapReduce framework
 * @author hering
 *
 */

import java.io.*;
import java.net.URI;
import java.util.*;

import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;



public class HadoopTrainPerceptron {
	
	/**
	 * Sets configuration parameters for the Hadoop-Job
	 * Starts the Hadoop-Job
	 * Applies l2-Regularization on each job's output-data 
	 * @param args Commandline arguments
	 * @throws Exception
	 */
	
	public static FileSystem fs;
	public static int numberOfEpochs = 10;
	public static int topKFeatures = 10;
	public static String weightVectorFile = "weightVector.txt";

	public static void main(String[] args) throws Exception {
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
	
	public static String readWVFromHDFS( String filename ) {
		Path fileToOpen = new Path (filename);
		BufferedReader br;
		try {
			br = new BufferedReader (new InputStreamReader (fs.open (fileToOpen)));
			return Toolbox.getWeightVector (br);
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}		
	}
	
	public static void writeInitializedVector (Path outFolder) {
		String vectorContent = readWVFromHDFS (outFolder.toString() + "/part-00000");
//		System.out.println( "#####################################");
//		System.out.println( "Write vector:");
//		System.out.println( vectorContent );
		FeatureSelector shrinkThings = new FeatureSelector (Toolbox.convertStringToHashmap (vectorContent));
		
		BufferedWriter out;
		try {
			out = new BufferedWriter (new FileWriter ("initializedVector"));
			out.write( Toolbox.convertHashMapToString (shrinkThings.getTopKFeatures (topKFeatures)));
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void runHadoopJob( Integer currentEpoch, String pathIn, String pathOut ) throws Exception {
		
//		Integer lastEpoch = currentEpoch - 1;
		System.out.println( "#####################################");
		System.out.println( "Initializing Epoch " + currentEpoch.toString());
		System.out.println( "#####################################");
		JobConf conf = new JobConf(HadoopTrainPerceptron.class);
				
		conf.setJobName("Hadoop Perceptron Training - SoftPro Grp 1 - Epoch " + currentEpoch.toString());
	    conf.setOutputKeyClass(Text.class);		// data type for map's output key
	    conf.setOutputValueClass(DoubleWritable.class);	// data type for map's output value
	    
	    conf.setMapperClass(Map.class);			// which class implements the Mapper?
//	    conf.setCombinerClass(Reduce.class);	// not used, runs after Map-Procedure
	    conf.setReducerClass(Reduce.class);		// which class implements the Reducer?

		conf.setInputFormat(KeyValueTextInputFormat.class);	// files read by line; each line = one map instance; line split in "key	value"
	    conf.setOutputFormat(TextOutputFormat.class);		// save result of reducers as text

	    Path inFile = new Path (pathIn);
	    Path outFile = new Path (pathOut + "_e" + currentEpoch.toString());
	    
	    FileInputFormat.setInputPaths(conf, inFile);		// first command line argument used as input path
	    FileOutputFormat.setOutputPath(conf, outFile);	// second command line argument used as output path
	    
	    
	    //conf.set("test","hallo");	// data to be accessed by map and reducer instances, "test" => key; "hallo" => value
	    conf.set("learningRate","0.0001");	// data to be accessed by map and reducer instances, "test" => key; "hallo" => value
	    
	    
	    
	    Path hdfsPath = new Path (weightVectorFile);
	    String localFile;
	    
	    if (fs == null) {
	    	fs = FileSystem.get (conf);
	    	localFile = "emptyVector";
	    }
	    else {
	    	localFile = "initializedVector";
	    }
	    
//	    System.out.println( "#####################################");
//		System.out.println( "Creating weightVectorFile from " + localFile );
//		System.out.println( "#####################################");
	    
	    fs.copyFromLocalFile (new Path (localFile), hdfsPath);
	    DistributedCache.addCacheFile(hdfsPath.toUri(), conf);
	    for( URI cacheFile : DistributedCache.getCacheFiles(conf) ) {
	    	System.out.println( "  Cache file: " + cacheFile.toString() );
	    }
//	    System.out.println( "#####################################");
//		System.out.println( "I'm about to start the job!" );
//		System.out.println( "#####################################");
		
	    RunningJob hadoopTask = JobClient.runJob(conf);		// start Hadoop-Job
	    hadoopTask.waitForCompletion();
	    writeInitializedVector (outFile);
	}
	
	
	
	
	public static class Map extends MapReduceBase implements Mapper<Text, Text, Text, DoubleWritable> {
		/*
		 * Implementation of the Hadoop Mapper-Class
		 * Trains an initialized perceptron on one shard of data
		 */
			 
	    private static double learningRate;	// value set in configure(), used to store passed data
	    private static OutputCollector<Text, DoubleWritable> thisMapsOutputCollector;	// value set in map(), used in close()
	    private static Perceptron p;
	    private static HashMap <String, Double> initializedWeightVector;
	    private static HashMap <String, Double> trainedPerceptron;
	    
	    public void configure (JobConf job) {
	    	/*
	    	 * used to read parameters from JobConf
	    	 * important to get data from previously trained perceptrons
	    	 */ 
	        learningRate = Double.parseDouble (job.get ("learningRate"));							// get learning rate specified in JobConf
//	        System.out.println( "#####################################");
//    		System.out.println( "Configure..." );
//    		System.out.println( "#####################################");
    		try {
				for( Path cacheFile : DistributedCache.getLocalCacheFiles(job) ) {
					System.out.println( "  Cache file: " + cacheFile.getName().toString() );
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        try {
	            String wvCacheName = new Path (weightVectorFile).getName();
		        Path [] cacheFiles = DistributedCache.getLocalCacheFiles (job);
		        if (null != cacheFiles && cacheFiles.length > 0) {
//		        	System.out.println( "#####################################");
//		    		System.out.println( "Search in DC..." );
//		    		System.out.println( "#####################################");
		        	for (Path cachePath : cacheFiles) {
		        		if (cachePath.getName().endsWith(wvCacheName)) {
//		        			System.out.println( "#####################################");
//		    	    		System.out.println( "Found in DC!" );
//		    	    		System.out.println( "#####################################");
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
	    
	    void readWeightVector (Path cachePath) throws IOException {
	    	BufferedReader reader = new BufferedReader (new FileReader (cachePath.toString()));
	    	try {
//	    		System.out.println( "#####################################");
//	    		System.out.println( "Getting weight vector from Distributed Cache..." );
//	    		System.out.println( "#####################################");
	    		initializedWeightVector = Toolbox.convertStringToHashmap (reader.readLine());
	    	} 
	    	finally {
	    		reader.close();
	    	}
	    	
	    }

	    
	    public void map(Text category, Text rawInput, OutputCollector<Text, DoubleWritable> output, Reporter reporter) throws IOException {
	    	/*
	    	 * Implements the mapping method;
	    	 */
	    	thisMapsOutputCollector = output;
	    	p = new Perceptron (1, learningRate);
	    	p.setWeights (initializedWeightVector);
	    	
	    	ArrayList<Instance> trainInstances = Toolbox.convertStringToInstances( rawInput.toString() );	// converts raw input to a datatype that can be used by the perceptron
	    	
	    	System.out.println( "#####################################");
			System.out.println( "Doing some mapping" );
			System.out.println( "#####################################");
			
	    	trainedPerceptron = p.train (trainInstances);
	    	
	    	for( String key : trainedPerceptron.keySet()) {
	    		Double value = trainedPerceptron.get (key);

	    		Text hadoopKey = new Text();
	    		DoubleWritable hadoopValue = new DoubleWritable(); 
	    		
	    		hadoopKey.set (key);
/*	    		if (value == null) {
	    			value = 123.456789;
	    		}*/
	    		hadoopValue.set (value);
	    		
	    		thisMapsOutputCollector.collect (hadoopKey, hadoopValue);
	    	}
	    	
	    	/* NICE TO REMEMBER:
	    	 *
	    	 * Get filename of source currently processing:	    	
	    	 * String fileName = "filename: " + fileSplit.getPath().getName() + " key: " + einSchluessel + "";
	    	 */
	    }
	    
	    public void close()  throws IOException {
	    	/*
	    	 * Called when Mapper-Class has finished running and is about to be closed
	    	 * time to throw collected data back to Hadoop framework / reducers
	    	 */
//	    	System.out.println( "#####################################");
//			System.out.println( "Closed a mapper!" );
//			System.out.println( "#####################################");
	    	//thisMapsOutputCollector.collect(word, aFileName);
	    }
	}
	
	
	
	
	public static class Reduce extends MapReduceBase implements Reducer<Text, DoubleWritable, Text, DoubleWritable> {
		public void reduce(Text key, Iterator<DoubleWritable> values, OutputCollector<Text, DoubleWritable> output, Reporter reporter) throws IOException {
			Double sum = 0.0;
			System.out.println( "#####################################");
			System.out.println( "Let's keep things small..." );
			System.out.println( "#####################################");
			
	        while (values.hasNext()) {
	        	sum += Math.pow (values.next().get(), 2);
	      	}
	        
	        DoubleWritable squareRoot = new DoubleWritable();
	        squareRoot.set (Math.sqrt (sum));
	        output.collect (key, squareRoot);
		}
	}
}
