package io.github.open.easykafka.client.support.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.*;

/**
 * 线程池参数
 *
 * @author studeyang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThreadPoolProperties {

    /**
     * 核心线程数
     */
    private int corePoolSize;

    /**
     * 最大线程数
     */
    private int maxPoolSize;

    /**
     * 超时时间(秒)
     */
    private int keepAliveSeconds;

    /**
     * 队列容量
     */
    private int queueCapacity;

    /**
     * 拒绝策略(全限定名)
     */
    private Class<? extends RejectedExecutionHandler> rejectedHandler;

    /**
     * 线程名前缀
     */
    private String threadNamePrefix;

    public ExecutorService create() {
        return new ThreadPoolExecutor(corePoolSize,
                maxPoolSize,
                keepAliveSeconds, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(queueCapacity),
                new CustomizableThreadFactory(threadNamePrefix),
                instanceHandler());
    }

    @SneakyThrows(Exception.class)
    protected RejectedExecutionHandler instanceHandler() {
        return (RejectedExecutionHandler) rejectedHandler.getConstructors()[0].newInstance();
    }
}
