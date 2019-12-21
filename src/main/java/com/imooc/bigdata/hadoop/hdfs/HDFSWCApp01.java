package com.imooc.bigdata.hadoop.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 使用HDFS API完成wordcount统计
 * 需求：统计HDFS上的文件的wc，然后将统计结果输出到HDFS
 *
 * 功能拆解：
 * 1） 读取HDFS上的文件 -==> HDFS API
 * 2） 业务处理（词频统计）： 对文件中的每一行数据都要进行业务处理（按照分割符分割） ==> Mapper
 * 3） 将处理结果缓存起来  ==> Context
 * 4） 将结果输出到HDFS ==> HDFS API
 */
public class HDFSWCApp01 {
    public static void main(String[] args) throws Exception {
        // 1)  读取HDFS上的文件 ==> HDFS API
        Path input = new Path("/hdfsapi/test/c.txt");

        // 获取要操作的HDFS文件系统
        FileSystem fs = FileSystem.get(new URI("hdfs://192.168.124.54:8020"), new Configuration(), "root");

        RemoteIterator<LocatedFileStatus> iterator = fs.listFiles(input, false);

        ImoocContext context = new ImoocContext();
        ImoocMapper mapper = new WordCountMapper();

        while(iterator.hasNext()){
            LocatedFileStatus file = iterator.next();
            FSDataInputStream in = fs.open(file.getPath());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null){

                // 2) 业务处理（词频统计）
                // 再业务逻辑完成之后将结果写到Cache中去

                mapper.map(line, context);
            }
            reader.close();
            in.close();
        }

        // 3) 将处理的结果缓存起来 用Map

        Map<Object, Object> contextMap = context.getCacheMap();

        // 4) 将结果输出到HDFS ==> HDFS API
        Path output = new Path("/hdfsapi/output");
        FSDataOutputStream out = fs.create(new Path(output, new Path("wc.out")));

        // 将第3步缓存中的内容输出到out中去
        Set<Map.Entry<Object, Object>> entries = contextMap.entrySet();
        for (Map.Entry<Object, Object> entry : entries) {
            out.write((entry.getKey().toString() + "\t" + entry.getValue() + "\n").getBytes());
        }
        out.close();
        fs.close();
        System.out.println("HDFS API 统计词频运行成功......");

        // 获取要操作
    }
}
