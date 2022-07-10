package io.aftersound.weave.library;

import org.junit.Test;

import static io.aftersound.weave.library.Target.Docker;

public class Manage_bundle {

    @Test
    public void manage() throws Exception {
        new LibraryManagement("images/bundle").executeFor(Docker);
    }

}