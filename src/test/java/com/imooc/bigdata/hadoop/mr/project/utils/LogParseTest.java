package com.imooc.bigdata.hadoop.mr.project.utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

public class LogParseTest {
    LogParser logParser;

    @Before
    public void setUp(){
        logParser = new LogParser();
    }
    @Test
    public void test01() {
        Map<String, String> map = logParser.parse("");
        for(Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
    }

    public void test02() {
        String pageId = ContentUtils.getPageId("");
        System.out.println(pageId);
    }

    @After
    public void tearDown() {
        logParser = null;
    }
}
