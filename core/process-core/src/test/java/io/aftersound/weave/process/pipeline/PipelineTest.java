package io.aftersound.weave.process.pipeline;

import io.aftersound.common.Context;
import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.component.ComponentRepository;
import io.aftersound.weave.process.Processor;
import io.aftersound.weave.utils.MapBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static io.aftersound.weave.process.pipeline.Status.NOT_RUNNING;
import static io.aftersound.weave.process.pipeline.Status.RUNNING;
import static org.junit.Assert.assertTrue;

public class PipelineTest {

    private static Pipeline pipeline;
    private static ExecutorService executorService;
    private static int runCount = 0;

    @BeforeClass
    public static void setup() {
        pipeline = new Pipeline(
                new Processor() {
                    @Override
                    public void process(Context context) {
                        runCount++;
                    }
                },
                MapBuilder.linkedHashMap().put("id", "test.pipeline").put("idleTime", 500L).build(),
                new ComponentRepository() {
                    @Override
                    public <COMPONENT> COMPONENT getComponent(String id) {
                        return null;
                    }

                    @Override
                    public <COMPONENT> COMPONENT getComponent(String id, Class<COMPONENT> type) {
                        return null;
                    }

                    @Override
                    public <C> C getComponent(NamedType<C> namedType) {
                        return null;
                    }

                    @Override
                    public Collection<String> componentIds() {
                        return null;
                    }
                }
        );

        executorService = Executors.newSingleThreadExecutor();
    }

    @AfterClass
    public static void teardown() {
        pipeline.getStatus().compareAndSet(RUNNING, NOT_RUNNING);
        executorService.shutdown();
    }

    @Test
    public void run() throws Exception {
        pipeline.getStatus().compareAndSet(NOT_RUNNING, RUNNING);
        Future<?> ignored = executorService.submit(pipeline);
        Thread.sleep(3000L);
        assertTrue(runCount == 6);
    }

}