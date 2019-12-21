package com.imooc.bigdata.hadoop.mr.project.utils;

import org.junit.Test;

public class IPTest {

    @Test
    public void testIP() {
        IPParser.RegionInfo regionInfo = IPParser.getInstance().analyseIp("139.199.77.144");
        System.out.println(regionInfo.getCountry());
        System.out.println(regionInfo.getProvince());
        System.out.println(regionInfo.getCity());
    }
}
