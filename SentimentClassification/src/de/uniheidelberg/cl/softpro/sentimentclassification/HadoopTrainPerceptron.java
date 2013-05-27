package de.uniheidelberg.cl.softpro.sentimentclassification;
/**
 * Uses perceptron algorithm inside Hadoop MapReduce framework
 * @author hering
 *
 */

import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;



public class HadoopTrainPerceptron {
	
	/**
	 * Sets configuration parameters for the Hadoop-Job
	 * Starts the Hadoop-Job
	 * Applies l2-Regularization on each job's output-data 
	 * @param args Commandline arguments
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
		JobConf conf = new JobConf(HadoopTrainPerceptron.class);
		conf.setJobName("Hadoop Perceptron Training - SoftPro Grp 1");
	    conf.setOutputKeyClass(Text.class);		// data type for map's output key
	    conf.setOutputValueClass(DoubleWritable.class);	// data type for map's output value
	    
	    conf.setMapperClass(Map.class);			// which class implements the Mapper?
//	    conf.setCombinerClass(Reduce.class);	// not used, runs after Map-Procedure
	    conf.setReducerClass(Reduce.class);		// which class implements the Reducer?

		conf.setInputFormat(KeyValueTextInputFormat.class);	// files read by line; each line = one map instance; line split in "key	value"
	    conf.setOutputFormat(TextOutputFormat.class);		// save result of reducers as text

	    FileInputFormat.setInputPaths(conf, new Path(args[0]));		// first command line argument used as input path
	    FileOutputFormat.setOutputPath(conf, new Path(args[1]));	// second command line argument used as output path

	    //conf.set("test","hallo");	// data to be accessed by map and reducer instances, "test" => key; "hallo" => value
	    conf.set("learningRate","0.0001");	// data to be accessed by map and reducer instances, "test" => key; "hallo" => value
	    conf.set("initializedWeightVector", "");	// data to be accessed by map and reducer instances, "test" => key; "hallo" => value
	    
	    JobClient.runJob(conf);		// start Hadoop-Job
	}
	
	
	
	
	public static class Map extends MapReduceBase implements Mapper<Text, Text, Text, Text> {
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
	        initializedWeightVector = Toolbox.convertStringToHashmap(job.get("initializedWeightVector"));	// get either an initialized weight vector from previously ran trainings or an empty WV 
	    }

	    
	    public void map(Text category, Text rawInput, OutputCollector<Text, DoubleWritable> output, Reporter reporter) throws IOException {
	    	/*
	    	 * Implements the mapping method;
	    	 */
	    	thisMapsOutputCollector = output;
	    	p = new Perceptron (1, learningRate);
	    	p.setWeights (initializedWeightVector);
	    	
	    	Instance[] trainInstances = Toolbox.convertStringToInstances( rawInput.toString() );	// converts raw input to a datatype that can be used by the perceptron
	    	
	    	trainedPerceptron = p.train (trainInstances);
	    	
	    	
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
	    	Iterator results = trainedPerceptron.entrySet().iterator();
	    	while (results.hasNext()) {
	    		String key = results.next().toString();
	    		Double value = trainedPerceptron.get (key);

	    		Text hadoopKey = new Text();
	    		DoubleWritable hadoopValue = new DoubleWritable(); 
	    		
	    		hadoopKey.set (key);
	    		hadoopValue.set (value); 		
	    		
	    		thisMapsOutputCollector.collect(key, value);
	    	}
	    	
	    	//thisMapsOutputCollector.collect(word, aFileName);
	    }
	}
	
	
	
	
	public static class Reduce extends MapReduceBase implements Reducer<Text, Text, Text, Text> {
		public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
	        while (values.hasNext()) {
	        	output.collect(key, new Text(values.next().toString()));
	      	}
		}
	}
}
