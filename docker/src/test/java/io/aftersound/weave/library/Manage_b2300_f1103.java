package io.aftersound.weave.library;

import org.junit.Test;

import static io.aftersound.weave.library.Target.Docker;

public class Manage_b2300_f1103 {

    @Test
    public void execute() throws Exception {
        // prepare libraries needed for docker image which supports
        // submitting flink job to Apache Flink cluster on 1.10.3 via Apache Beam
        //   1.Apache Beam 2.30.0 core and its required dependencies
        //   2.beam-runners-flink-1.10 and its required dependencies
        //   3.Apache Flink client sdk for 1.10.3 and its required dependencies
        new LibraryManagement("images/b2300_f1103").executeFor(Docker);
    }

}