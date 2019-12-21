package com.imooc.bigdata.hadoop.mr.project.mrv2;

import com.imooc.bigdata.hadoop.mr.project.utils.ContentUtils;
import com.imooc.bigdata.hadoop.mr.project.utils.LogParser;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Map;

public class ETLApp {

    public static void main(String[] args) {

    }

    static class MyMapper extends Mapper<LongWritable, Text, NullWritable, Text> {
        private LogParser logParser;

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            logParser = new LogParser();
        }

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String log = value.toString();
            Map<String, String> info = logParser.parse(log);
            String ip = info.get("ip");
            String country = info.get("counry");
            String province = info.get("province");
            String city = info.get("city");
            String url = info.get("url");
            String time = info.get("time");
            String pageId = ContentUtils.getPageId(url);

            StringBuilder builder = new StringBuilder();
            builder.append(ip).append("\t");
            builder.append(country).append("\t");
            builder.append(province).append("\t");
            builder.append(city).append("\t");
            builder.append(url).append("\t");
            builder.append(time).append("\t");
            builder.append(pageId).append("\t");

            context.write(NullWritable.get(), new Text(builder.toString()));
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
