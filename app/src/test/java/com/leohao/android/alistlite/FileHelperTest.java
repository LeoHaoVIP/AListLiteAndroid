package com.leohao.android.alistlite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.leohao.android.alistlite.util.FileHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * FileHelper 单元测试（替代 commons-io 后的核心逻辑回归）。
 *
 * @author LeoHao
 */
public class FileHelperTest {

    private File root;

    @Before
    public void setUp() throws IOException {
        root = File.createTempFile("filehelper-test", "");
        if (!root.delete() || !root.mkdirs()) {
            fail("无法创建临时目录: " + root);
        }
    }

    @After
    public void tearDown() {
        deleteQuietly(root);
    }

    @Test
    public void writeThenRead_keepsUtf8Content() throws IOException {
        File target = new File(root, "config.json");
        String content = "{\"name\":\"酷酷\",\"emoji\":\"🔥\"}";

        FileHelper.writeText(target, content);

        assertEquals(content, FileHelper.readText(target));
    }

    @Test
    public void writeText_createsMissingParentDirs() throws IOException {
        File target = new File(root, "a/b/c/note.txt");

        FileHelper.writeText(target, "hello");

        assertTrue(target.exists());
        assertEquals("hello", FileHelper.readText(target));
    }

    @Test
    public void copyDirectory_copiesNestedFiles() throws IOException {
        File src = new File(root, "src");
        File nested = new File(src, "nested");
        assertTrue(nested.mkdirs());
        FileHelper.writeText(new File(src, "config.json"), "{\"a\":1}");
        FileHelper.writeText(new File(nested, "data.db"), "bin");

        File dst = new File(root, "dst");
        FileHelper.copyDirectory(src, dst);

        assertEquals("{\"a\":1}", FileHelper.readText(new File(dst, "config.json")));
        assertEquals("bin", FileHelper.readText(new File(dst, "nested/data.db")));
    }

    @Test(expected = IOException.class)
    public void copyDirectory_failsWhenSourceMissing() throws IOException {
        FileHelper.copyDirectory(new File(root, "not-exist"), new File(root, "dst"));
    }

    private static void deleteQuietly(File file) {
        if (file == null || !file.exists()) {
            return;
        }
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                deleteQuietly(child);
            }
        }
        //noinspection ResultOfMethodCallIgnored
        file.delete();
    }
}
