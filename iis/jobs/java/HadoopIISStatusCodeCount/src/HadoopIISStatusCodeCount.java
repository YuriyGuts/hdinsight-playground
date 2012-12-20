import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * This is an example Hadoop Map/Reduce application.
 * It reads the IIS 6.0 WWW log files, parses the HTTP status codes and counts them.
 * The output is a list of HTTP codes with their occurrence counts.
 *
 * To run: bin/hadoop jar HadoopIISStatusCodeCount.jar HadoopIISStatusCodeCount
 *            [-m <i>map-tasks</i>] [-r <i>reduce-tasks</i>] <i>in-dir</i> <i>out-dir</i>
 */
public class HadoopIISStatusCodeCount extends Configured implements Tool
{
    /**
     * A mapper class that parses an IIS log entry and emits the ["HTTP %STATUSCODE%", 1] pair.
     */
    public static class HadoopIISStatusCodeCountMap extends MapReduceBase
        implements Mapper<LongWritable, Text, Text, IntWritable>
    {
        private final static int expectedEntryComponentCount = 15;
        private final static int httpStatusCodeComponentIndex = 9;
        
        private final static IntWritable one = new IntWritable(1);
        private Text intermediateKey = new Text();

        public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter)
            throws IOException
        {
            String logEntry = value.toString();
            String[] logEntryComponents = logEntry.split("\\s+");
            
            if (logEntryComponents.length != expectedEntryComponentCount)
            {
                return;
            }

            intermediateKey.set("HTTP " + logEntryComponents[httpStatusCodeComponentIndex].toString());
            output.collect(intermediateKey, one);
        }
    }

    /**
     * A reducer class that just emits the sum of the input values.
     */
    public static class HadoopIISStatusCodeCountReduce extends MapReduceBase
        implements Reducer<Text, IntWritable, Text, IntWritable>
    {
        public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output, Reporter reporter)
            throws IOException
        {
            int sum = 0;
            while (values.hasNext())
            {
                sum += values.next().get();
            }
            output.collect(key, new IntWritable(sum));
        }
    }

    private static int printUsage()
    {
        System.out.println("HadoopIISStatusCodeCount [-m <map-tasks>] [-r <reduce-tasks>] <input> <output>");
        ToolRunner.printGenericCommandUsage(System.out);
        return -1;
    }

    /**
     * The main driver for IIS Status Code Count map/reduce program.
     * Invoke this method to submit the map/reduce job.
     * @throws IOException When there are communication problems with the job tracker.
     */
    public int run(String[] args) throws Exception
    {
        JobConf conf = new JobConf(getConf(), HadoopIISStatusCodeCount.class);
        conf.setJobName("HadoopIISStatusCodeCount");

        // The keys are strings.
        conf.setOutputKeyClass(Text.class);
        // The values are counts (integers).
        conf.setOutputValueClass(IntWritable.class);

        // Assigning the classes responsible for Map and Reduce tasks.
        conf.setMapperClass(HadoopIISStatusCodeCountMap.class);
        conf.setCombinerClass(HadoopIISStatusCodeCountReduce.class);
        conf.setReducerClass(HadoopIISStatusCodeCountReduce.class);

        List<String> otherArgs = new ArrayList<String>();
        
        for (int i = 0; i < args.length; i++)
        {
            try
            {
                if ("-m".equals(args[i]))
                {
                    conf.setNumMapTasks(Integer.parseInt(args[++i]));
                }
                else if ("-r".equals(args[i]))
                {
                    conf.setNumReduceTasks(Integer.parseInt(args[++i]));
                }
                else
                {
                    otherArgs.add(args[i]);
                }
            }
            catch (NumberFormatException e)
            {
                System.out.println("ERROR: Integer expected instead of " + args[i]);
                return printUsage();
            }
            catch (ArrayIndexOutOfBoundsException e)
            {
                System.out.println("ERROR: Required parameter missing from " + args[i - 1]);
                return printUsage();
            }
        }

        // Make sure there are exactly 2 parameters left.
        if (otherArgs.size() != 2)
        {
            System.out.println("ERROR: Wrong number of parameters: " + otherArgs.size() + " instead of 2.");
            return printUsage();
        }

        FileInputFormat.setInputPaths(conf, otherArgs.get(0));
        FileOutputFormat.setOutputPath(conf, new Path(otherArgs.get(1)));

        JobClient.runJob(conf);
        return 0;
    }

    public static void main(String[] args)
        throws Exception
    {
        int result = ToolRunner.run(new Configuration(), new HadoopIISStatusCodeCount(), args);
        System.exit(result);
    }
}