package io.github.open.easykafka.example;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author studeyang
 */
@SpringBootApplication
@Slf4j
public class ExampleProducerApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ExampleProducerApplication.class, args);
        log.info("服务启动成功: http://localhost:{}", context.getEnvironment().getProperty("server.port"));
    }

}
