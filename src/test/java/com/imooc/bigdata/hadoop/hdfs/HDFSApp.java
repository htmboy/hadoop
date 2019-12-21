package com.imooc.bigdata.hadoop.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.util.Progressable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * 使用Java API操作HDFS文件系统
 *
 * 关键点
 * 1） 创建Configuration
 * 2) 获取FileSystem
 * 3) ...就是你的HDFS API的操作
 */
public class HDFSApp {

    // 单元测试模式

    public static final String HDFS_PATH = "hdfs://192.168.124.54:8020";
    FileSystem fileSystem = null;
    Configuration configuration = null;

    @Before
    public void setUp() throws Exception {

        System.out.println("-----------------setUp--------------");
        configuration = new Configuration();
        configuration.set("dfs.replication", "1");

        /**
         * 构建一个访问指定HDFS系统的客户端对象
         * 第一个参数： HDFS的URI
         * 第二个参数： 客户端指定的配置参数
         * 第三个参数： 客户端的身份，说白了就是用户名
         */
        fileSystem = FileSystem.get(new URI(HDFS_PATH), configuration, "root");
    }

    /**
     * 创建HDFS文件夹
     *
     */
    @Test
    public void mkdir() throws IOException {
        fileSystem.mkdirs(new Path("/hdfsapi/test"));
    }

    /**
     * 查看HDFS内容
     *
     */
    @Test
    public void text() throws IOException {
        FSDataInputStream in = fileSystem.open(new Path("/hdfsapi/output/wc.out"));
        IOUtils.copyBytes(in, System.out, 1024);
    }

    /**
     * 创建文件
     */
    @Test
    public void create() throws IOException {
        FSDataOutputStream out = fileSystem.create(new Path("/hdfsapi/test/c.txt"));
        out.writeUTF("hello\tword\thello\tmap\tmapt\thello");
        out.flush();
        out.close();
    }

    /**
     * 重命名
     * @throws IOException
     */
    @Test
    public void rename() throws IOException {
        Path oldPath = new Path("/hdfsapi/test/a.txt");
        Path newPath = new Path("/hdfsapi/test/b.txt");
        boolean result = fileSystem.rename(oldPath, newPath);
        System.out.println(result);
    }

    /**
     * 拷贝本地文件到HDFS文件系统
     * @throws IOException
     */
    @Test
    public void copyFromLocalFile() throws IOException {
        Path src = new Path("d:\\package-lock.json");
        Path dst = new Path("/hdfsapi/test/");
        fileSystem.copyFromLocalFile(src, dst);
    }

    /**
     * 拷贝本地大文件到HDFS文件系统
     * @throws IOException
     */
    @Test
    public void copyFromLocalBigFile() throws IOException {
        InputStream in = new BufferedInputStream(new FileInputStream(new File("d:\\xinmai\\XWSI3702.MOV")));
        FSDataOutputStream out = fileSystem.create(new Path("/hdfsapi/test/XWSI3702.MOV"), new Progressable() {
            @Override
            public void progress() {
                System.out.print(".");
            }
        });
        IOUtils.copyBytes(in, out, 4096);
    }

    /**
     * 拷贝HDFS文件到本地： 下载
     * @throws IOException
     */
    @Test
    public void copyToLocalFile() throws IOException {
        Path src = new Path("/hdfsapi/test/XWSI3702.MOV");
        Path dst = new Path("d:\\xftp");
        fileSystem.copyToLocalFile(false, src, dst, true);
    }

    /**
     * 查看目标文件夹下的所有文件
     * @throws IOException
     */
    @Test
    public void listFiles() throws IOException {
        FileStatus[] statuses = fileSystem.listStatus(new Path("/hdfsapi/test"));
        for(FileStatus file : statuses) {
            String isDir = file.isDirectory() ? "文件夹" : "文件";
            String permission = file.getPermission().toString();
            short replication = file.getReplication();
            long length = file.getLen();
            String path = file.getPath().toString();

            System.out.println(isDir + "\t" + permission + "\t" + replication + "\t" + length + "\t" + path);
        }
    }

    /**
     * 递归查看目标文件夹下的所有文件
     * @throws IOException
     */
    @Test
    public void listFilesRecursive() throws IOException {
        RemoteIterator<LocatedFileStatus> files = fileSystem.listFiles(new Path("/"), true);
        while(files.hasNext()){
            LocatedFileStatus file = files.next();
            String isDir = file.isDirectory() ? "文件夹" : "文件";
            String permission = file.getPermission().toString();
            short replication = file.getReplication();
            long length = file.getLen();
            String path = file.getPath().toString();

            System.out.println(isDir + "\t" + permission + "\t" + replication + "\t" + length + "\t" + path);
        }
    }
    @Test
    public void getFileBlockLocations() throws IOException {
        FileStatus fileStatus = fileSystem.getFileStatus(new Path("/hdfsapi/test/XWSI3702.MOV"));
        BlockLocation[] blocks = fileSystem.getFileBlockLocations(fileStatus, 0, fileStatus.getLen());
        for (BlockLocation block : blocks) {
            for (String name : block.getNames())
            System.out.println(name + " : " + block.getOffset() + " : " + block.getLength() + " : " + block.getHosts()
            );
        }
    }

    @Test
    public void delete() throws IOException {
        boolean result = fileSystem.delete(new Path("/hdfsapi/test/a.txt"), true);
        System.out.println(result);
    }

    @After
    public void tearDown() {
        configuration = null;
        fileSystem = null;
        System.out.println("-----------------tearDown--------------");
    }


//    public static void main(String[] args) throws Exception {
//
//        // hadoop00:8020
//        Configuration configuration = new Configuration();
//        FileSystem fileSystem = FileSystem.get(new URI("hdfs://192.168.124.54:8020"), configuration, "root");
//        Path path = new Path("/hdfsapi/test");
//        boolean result = fileSystem.mkdirs(path);
//        System.out.println(result);
//    }
}
