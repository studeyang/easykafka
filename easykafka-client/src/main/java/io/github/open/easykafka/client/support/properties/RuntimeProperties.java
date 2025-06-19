package io.github.open.easykafka.client.support.properties;

import lombok.Data;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author studeyang
 */
@Data
public class RuntimeProperties {
    private Producer producer = new Producer();
    private Consumer consumer = new Consumer();

    @Data
    public static class Producer {
        private int partitionSize = 500;
        private ThreadPoolProperties async = new ThreadPoolProperties(
                3,
                5,
                60,
                100,
                ThreadPoolExecutor.CallerRunsPolicy.class,
                "kafka-async-producer-");
    }

    @Data
    public static class Consumer {
        private String groupIdPrefix;
    }

}