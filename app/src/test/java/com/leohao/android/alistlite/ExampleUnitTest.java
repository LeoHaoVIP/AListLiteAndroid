package com.leohao.android.alistlite;

import com.jayway.jsonpath.JsonPath;
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

    public Object getConfigValue(String jsonPath) throws IOException {
        File configFile = new File("C:\\Users\\LeoHao\\Desktop\\config.json");
        String configString = FileUtils.readFileToString(configFile, StandardCharsets.UTF_8);
        return JsonPath.read(configString, jsonPath);
    }
}