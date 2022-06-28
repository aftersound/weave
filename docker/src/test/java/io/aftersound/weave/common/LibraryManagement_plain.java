package io.aftersound.weave.common;

import org.junit.Test;

public class LibraryManagement_plain {

    @Test
    public void manage() throws Exception {
        LibraryManagement libraryManagement = new LibraryManagement("plain");
        libraryManagement.execute();
    }

}