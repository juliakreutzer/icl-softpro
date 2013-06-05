package de.uniheidelberg.cl.softpro.sentimentclassification.scalable;

import java.io.*;
import java.util.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.ChainMapper;
import org.apache.hadoop.mapred.lib.ChainReducer;
import org.apache.hadoop.mapreduce.Job;


import de.uniheidelberg.cl.softpro.sentimentclassification.*;
import de.uniheidelberg.cl.softpro.sentimentclassification.HadoopTrainPerceptron.Map;
import de.uniheidelberg.cl.softpro.sentimentclassification.HadoopTrainPerceptron.Reduce;

public class HadoopScalableTest {	
	
	public static void main(String[] args) throws Exception {
		runHadoopJob( args[0], args[1]);			
	}
	
	public static void runHadoopJob( String pathIn, String pathOut ) throws Exception {
		
		JobConf conf = new JobConf();
		conf.setJobName ("Hadoop Scalable Perceptron Testing");
		conf.setInputFormat (KeyValueTextInputFormat.class);
		conf.setOutputFormat (TextOutputFormat.class);
		
		
		JobConf mapAConf = new JobConf (false);
		ChainMapper.addMapper (conf, MapA.class, Text.class, Text.class, Text.class, Text.class, true, mapAConf);
		
		JobConf mapBConf = new JobConf (false);
		ChainMapper.addMapper (conf, MapB.class, Text.class, Text.class, Text.class, IntWritable.class, true, mapBConf);
		
		JobConf reducerConf = new JobConf (false);
		ChainReducer.setReducer(conf, Reduce.class, Text.class, IntWritable.class, Text.class, MapWritable.class, true, reducerConf);
		
		Path inFile = new Path (pathIn);
	    Path outFile = new Path (pathOut);
	    
	    FileInputFormat.setInputPaths (conf, inFile);
	    FileOutputFormat.setOutputPath (conf, outFile);
	    
	    RunningJob hadoopTask = JobClient.runJob(conf);
	    hadoopTask.waitForCompletion();
	    
	}	
		
	public static class MapA extends MapReduceBase implements Mapper<Text, Text, Text, Text> {
		// Splits into sentences
		public void map(Text key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
			Text sentence = new Text();
			StringTokenizer st = new StringTokenizer (value.toString(), ".");
			while (st.hasMoreTokens()) {
				sentence.set (st.nextToken());
				output.collect (key, sentence);
			}
		}
	}
	
	
	public static class MapB extends MapReduceBase implements Mapper<Text, Text, Text, IntWritable> {
		// Splits into words
		IntWritable one = new IntWritable(1);
		public void map(Text key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
			System.out.println ("Splitting sentence of " + key.toString() + ": ");
			System.out.println (value.toString());
			Text word = new Text();
			StringTokenizer st = new StringTokenizer (value.toString());
			while (st.hasMoreTokens()) {
				word.set (st.nextToken());
				output.collect (word, one);
			}
		}
	}
	
	public static class Reduce extends MapReduceBase implements Reducer<Text, IntWritable, Text, MapWritable> {
		// Counts words
		public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, MapWritable> output, Reporter reporter) throws IOException {
			int sum = 0;
			while (values.hasNext()) {
				sum += values.next().get();
			}
			MapWritable myMap = new MapWritable();
			myMap.put (new Text("sum"), new IntWritable (sum));
			myMap.put (new Text("key"), key);
			output.collect(key, myMap);
		}
	}
	
}
