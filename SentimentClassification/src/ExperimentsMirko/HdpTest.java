package ExperimentsMirko;

public class HdpTest {
	public void run(String inputPath, String outputPath) throws Exception {
	    JobConf conf = new JobConf(WordCount.class);
	    conf.setJobName("wordcount");

	    // the keys are words (strings)
	    conf.setOutputKeyClass(Text.class);
	    // the values are counts (ints)
	    conf.setOutputValueClass(IntWritable.class);

	    conf.setMapperClass(MapClass.class);
	    conf.setReducerClass(Reduce.class);

	    FileInputFormat.addInputPath(conf, new Path(inputPath));
	    FileOutputFormat.setOutputPath(conf, new Path(outputPath));

	    JobClient.runJob(conf);
	  }
}
