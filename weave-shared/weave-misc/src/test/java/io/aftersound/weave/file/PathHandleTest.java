package io.aftersound.weave.file;

import org.junit.Test;

import java.nio.file.Path;
import java.util.Date;

import static org.junit.Assert.*;

public class PathHandleTest {

    @Test
    public void testPathWithSystemPropertyPlaceholder() {
        String path = "${_dir1}/${_dir2}/test/1.txt";
        System.setProperty("_dir1", "a");
        assertEquals("a/test/1.txt", PathHandle.of(path).path().toString());

        System.setProperty("_dir1", "/a");
        assertEquals("/a/test/1.txt", PathHandle.of(path).path().toString());

        System.setProperty("_dir2", "b");
        assertEquals("/a/b/test/1.txt", PathHandle.of(path).path().toString());

        assertEquals("/var/tmp/data/1234.txt", PathHandle.of("//var//tmp////data/1234.txt").path().toString());
        assertEquals("/var/tmp/data/1234 .txt", PathHandle.of("//var//tmp////data/1234 .txt").path().toString());

        System.setProperty("_dir1", "a");
        System.setProperty("_dir2", "b");
        assertEquals("/ab/test/1.txt", PathHandle.of("/${_dir1}${_dir2}/test/1.txt").path().toString());
        assertEquals("/a_b/test/1.txt", PathHandle.of("/${_dir1}_${_dir2}/test/1.txt").path().toString());
    }

    @Test
    public void testPathWithDirectivePlaceholder() {
        Path p = PathHandle.of("${_UUID_}").path();
        assertEquals(36, p.toString().length());
        System.out.println(p.toString());

        p = PathHandle.of("${_NOW_IN_MILLIS_}").path();
        assertEquals(13, p.toString().length());
        System.out.println(p.toString());

        p = PathHandle.of("${_NOW_IN_NANOS_}").path();
        assertEquals(15, p.toString().length());
        System.out.println(p.toString());
    }

    @Test
    public void testNullPath() {
        assertNull(PathHandle.of(null).path());
        assertNull(PathHandle.of("").path());
        assertNull(PathHandle.of("  ").path());
    }

}