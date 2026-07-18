package com.leohao.android.alistlite;

import com.jayway.jsonpath.JsonPath;
import com.leohao.android.alistlite.util.AppUtil;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void configReadTest() throws IOException {
        System.out.println(getConfigValue("scheme.http_port"));
    }

    @Test
    public void versionCompare() throws IOException {
        System.out.println(AppUtil.compareVersion("2.0.1","2.1.2"));
        System.out.println(AppUtil.compareVersion("2.1.10","2.1.9"));
        System.out.println(AppUtil.compareVersion("2.0.10-beta1","2.0.10-beta20"));
    }

    public Object getConfigValue(String jsonPath) throws IOException {
        File configFile = new File("C:\\Users\\LeoHao\\Desktop\\config.json");
        String configString = FileUtils.readFileToString(configFile, StandardCharsets.UTF_8);
        return JsonPath.read(configString, jsonPath);
    }
}
