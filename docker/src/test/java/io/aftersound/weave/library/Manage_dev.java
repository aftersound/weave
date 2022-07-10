package io.aftersound.weave.library;

import org.junit.Test;

import static io.aftersound.weave.library.Target.LocalDevelopment;

public class Manage_dev {

    @Test
    public void manage() throws Exception {
        new LibraryManagement("dev").executeFor(LocalDevelopment);
    }

}