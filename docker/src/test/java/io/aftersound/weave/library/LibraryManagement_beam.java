package io.aftersound.weave.library;

import org.junit.Test;

import static io.aftersound.weave.library.Target.Docker;

public class LibraryManagement_beam {

    @Test
    public void b2380_f1116() throws Exception {
        // prepare libraries needed for docker image which supports
        // submitting flink job to Apache Flink cluster on 1.11.6 via Apache Beam
        //   1.Apache Beam 2.38.0 core and its required dependencies
        //   2.beam-runners-flink-1.11 and its required dependencies
        //   3.Apache Flink client sdk for 1.11.6 and its required dependencies
        new LibraryManagement("b2380_f1116").executeFor(Docker);
    }

    @Test
    public void b2380_f1127() throws Exception {
        // prepare libraries needed for docker image which supports
        // submitting flink job to Apache Flink cluster on 1.12.7 via Apache Beam
        //   1.Apache Beam 2.38.0 core and its required dependencies
        //   2.beam-runners-flink-1.12 and its required dependencies
        //   3.Apache Flink client sdk for 1.12.7 and its required dependencies
        new LibraryManagement("b2380_f1127").executeFor(Docker);
    }

    @Test
    public void b2380_f1136() throws Exception {
        // prepare libraries needed for docker image which supports
        // submitting flink job to Apache Flink cluster on 1.13.6 via Apache Beam
        //   1.Apache Beam 2.38.0 core and its required dependencies
        //   2.beam-runners-flink-1.13 and its required dependencies
        //   3.Apache Flink client sdk for 1.13.6 and its required dependencies
        new LibraryManagement("b2380_f1136").executeFor(Docker);
    }

    @Test
    public void b2380_f1145() throws Exception {
        // prepare libraries needed for docker image which supports
        // submitting flink job to Apache Flink cluster on 1.14.5 via Apache Beam
        //   1.Apache Beam 2.38.0 core and its required dependencies
        //   2.beam-runners-flink-1.14 and its required dependencies
        //   3.Apache Flink client sdk for 1.14.5 and its required dependencies
        new LibraryManagement("b2380_f1145").executeFor(Docker);
    }

}