package io.aftersound.weave.library;

import org.junit.Test;

import static io.aftersound.weave.library.Target.Docker;

public class LibraryManagement_beam {

    @Test
    public void b2380_f1136() throws Exception {
        // prepare libraries needed for docker image
        //   Apache Beam 2.38.0 with beam-runner-flink-1.13
        //   Apache Flink client sdk for 1.13.6 and required dependencies
        new LibraryManagement("b2380_f1136").executeFor(Docker);
    }

}