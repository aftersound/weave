package io.aftersound.weave.library;

import org.junit.Test;

import static io.aftersound.weave.library.Target.Docker;

public class LibraryManagement_plain {

    @Test
    public void manage() throws Exception {
        new LibraryManagement("images/plain").executeFor(Docker);
    }

}