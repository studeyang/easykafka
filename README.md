# easykafka

EasyKafka 是基于 Spring Kafka 的增强，原 Spring Kafka 所有功能完全适配。

EasyKafka 的建设目标：使消息的发送和接收更简单。

主要解决以下问题：

- 简化消息发送和接收流程
- 封装基线与灰度环境兼容的复杂度
- 统一消息重试

## 一、开发环境

- Java 8
- Maven 3.6.3

## 二、快速开始

### 3.1 发送消息

示例：[EventPublisherTest](example-producer/src/test/java/io/github/open/easykafka/example/EventPublisherTest.java) `sendExampleEvent` 方法

**1、引入依赖**

```xml
<dependency>
    <groupId>io.github.studeyang</groupId>
    <artifactId>easykafka-spring-boot-starter</artifactId>
</dependency>
```

**2、配置**

最少启动配置

```yaml
easykafka:
  init: #初始化
    kafkaCluster: #kafka集群容器
    - cluster: send
      brokers: send-kafka.domain.com:9092
    - cluster: send
      brokers: send-gray-kafka.domain.com:9092
      tag: gray
    - cluster: edms
      brokers: edms-kafka.domain.com:9092
```

**3、定义一条消息**

```java
import io.github.open.easykafka.client.annotation.Topic;
import io.github.open.easykafka.client.message.Event;

@Topic(cluster = "send", name = "easykafka-example-topic")
public class ExampleEvent extends Event {
    private String name;
}
```

**4、发送消息**

```java
import io.github.open.easykafka.client.EventPublisher;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EventPublisherTest {
    @Test
    public void sendExampleEvent() {
        ExampleEvent event = new ExampleEvent();
        event.setName("test");
        EventPublisher.publish(event);
    }
}
```

### 3.2 消费消息

示例：[SingleEventHandler](example-consumer/src/main/java/io/github/open/easykafka/example/handler/SingleEventHandler.java)

**1、引入依赖**

引入 EasyKafka 和 消息定义包的依赖。

```xml
<!-- easykafka -->
<dependency>
    <groupId>io.github.studeyang</groupId>
    <artifactId>easykafka-spring-boot-starter</artifactId>
</dependency>
 
<!-- 消息定义 -->
<dependency>
    <groupId>io.github.studeyang</groupId>
    <artifactId>es-send-basic-api</artifactId>
</dependency>
```

**2、实现消费逻辑**

```java
import io.github.open.easykafka.client.annotation.EventHandler;
import org.springframework.stereotype.Service;

@Service
public class SingleEventHandler {
    @EventHandler
    public void handle(ExampleEvent event) {
        System.out.println("收到了一条消息: " + event);
    }
}
```

