package io.aftersound.weave.library;

import org.junit.Test;

import static io.aftersound.weave.library.Target.Docker;

public class LibraryManagement_b2380_f113 {

    @Test
    public void manage() throws Exception {
        LibraryManagement libraryManagement = new LibraryManagement("b2380_f113");
        libraryManagement.executeFor(Docker);
    }

}