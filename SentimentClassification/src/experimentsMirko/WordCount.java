package experimentsMirko;

import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;

public class WordCount {

  public static class Map extends MapReduceBase implements Mapper<Text, Text, Text, Text> {
//    private final static IntWritable one = new IntWritable(1);
    private Text word = new Text();
    private Text aFileName = new Text();

    public void map(Text key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
      String line = value.toString();

      FileSplit fileSplit = (FileSplit)reporter.getInputSplit();
      String einSchluessel =  key.toString();
      String fileName = "filename: " + fileSplit.getPath().getName() + " key: " + einSchluessel;
      aFileName.set(fileName);
      
      StringTokenizer tokenizer = new StringTokenizer(line);
      while (tokenizer.hasMoreTokens()) {
    	String sauberesWort = tokenizer.nextToken().toString();
    	sauberesWort = sauberesWort.replaceAll("\\W", "");
        word.set(sauberesWort);
        output.collect(word, aFileName);
      }
    }
  }

  public static class Reduce extends MapReduceBase implements Reducer<Text, Text, Text, Text> {
    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
      String answer = new String();
      ArrayList<String> fileNames = new ArrayList<String>();
      
      while (values.hasNext()) {
    	  String valueAsString = values.next().toString();

    	  if ( !fileNames.contains( valueAsString ) ) {
    		  answer = answer + " " + valueAsString;
    		  fileNames.add( valueAsString );
    	  }
      }
      output.collect(key, new Text(answer));
    }
  }

  public static void main(String[] args) throws Exception {
    JobConf conf = new JobConf(WordCount.class);
    conf.setJobName("wordcount v2");

    conf.setOutputKeyClass(Text.class);
    conf.setOutputValueClass(Text.class);

    conf.setMapperClass(Map.class);
//    conf.setCombinerClass(Reduce.class);
    conf.setReducerClass(Reduce.class);

    conf.setInputFormat(KeyValueTextInputFormat.class);
    conf.setOutputFormat(TextOutputFormat.class);

    FileInputFormat.setInputPaths(conf, new Path(args[0]));
    FileOutputFormat.setOutputPath(conf, new Path(args[1]));

    JobClient.runJob(conf);
  }
}
