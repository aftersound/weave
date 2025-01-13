package io.aftersound.util.map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PathTest {

    @Test
    public void simplePath() {
        Path path = Path.of("p1.p2");
        assertNotNull(path);
        assertEquals(3, path.getSegments().size());
    }

    @Test
    public void pathWithSegmentGroup() {
        Path path = Path.of("p1.p2.{f1,f2}");
        assertNotNull(path);
        assertEquals(3, path.getSegments().size());
    }

}