package io.aftersound.weave.library;

import org.junit.Test;

import static io.aftersound.weave.library.Target.LocalDevelopment;

public class LibraryManagement_dev {

    @Test
    public void manage() throws Exception {
        LibraryManagement libraryManagement = new LibraryManagement("_dev");
        libraryManagement.executeFor(LocalDevelopment);
    }

}