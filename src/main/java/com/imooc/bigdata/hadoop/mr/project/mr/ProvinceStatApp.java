package com.imooc.bigdata.hadoop.mr.project.mr;

import com.imooc.bigdata.hadoop.mr.project.utils.IPParser;
import com.imooc.bigdata.hadoop.mr.project.utils.LogParser;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.Map;

/**
 * 身份浏览量统计
 */
public class ProvinceStatApp {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

        Configuration configuration = new Configuration();
        FileSystem fileSystem = FileSystem.get(configuration);
        Path outputPath = new Path("");
        if(fileSystem.exists(outputPath)) {
            fileSystem.delete(outputPath, true);
        }
        Job job = Job.getInstance(configuration);
        job.setJarByClass(ProvinceStatApp.class);

        job.setMapperClass(MyMapper.class);
        job.setReducerClass(MyReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(LongWritable.class);

        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(LongWritable.class);

        FileInputFormat.setInputPaths(job, new Path(""));
        FileOutputFormat.setOutputPath(job, new Path(""));

        job.waitForCompletion(true);

    }

    static class MyMapper extends Mapper<LongWritable, Text, Text, LongWritable> {
        private LongWritable ONE = new LongWritable(1);
        private LogParser logParser;

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            logParser = new LogParser();
        }

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String log = value.toString();

            Map<String, String> info = logParser.parse("");
            String ip = info.get("ip");

            if(StringUtils.isNotBlank(ip)) {
                IPParser.RegionInfo regionInfo = IPParser.getInstance().analyseIp(ip);
                if(regionInfo != null) {
                    String province = regionInfo.getProvince();
                    if(StringUtils.isNotBlank(province)) {
                        context.write(new Text(province), ONE);
                    } else {
                        context.write(new Text("-"), ONE);
                    }
                } else {
                    context.write(new Text("-"), ONE);
                }
            } else {
                context.write(new Text("-"), ONE);
            }
        }
    }

    static class MyReducer extends Reducer<Text, LongWritable, Text, LongWritable> {
        @Override
        protected void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
            long count = 0;
            for(LongWritable value : values) {
                count++;
            }

            context.write(key, new LongWritable(count));
        }
    }
}
