# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Test Commands

```bash
# Build all modules
mvn clean install

# Run all tests
mvn clean test

# Run tests for a specific module
mvn clean test -f easykafka-client/pom.xml

# Run a specific test class
mvn test -Dtest=JsonUtilsTest

# Run a specific test method
mvn test -Dtest=JsonUtilsTest#someMethod

# Package (used in CI)
mvn -B package --file pom.xml
```

## Module Structure

```
easykafka/
├── easykafka-client/            # Core: all producer/consumer logic and annotations
├── easykafka-spring-boot-starter/ # Spring Boot auto-configuration entry point
├── example-sdk/                 # Shared message definitions (simulates a real SDK jar)
├── example-producer/            # Example producer application
└── example-consumer/            # Example consumer application
```

## Architecture

### Producer Flow

`EventPublisher.publish(event)` (static facade)
→ `DefaultMessagePublisher` (async, TTL thread pool)
→ `SendMessageConverter` (converts `Event` → `SendMessage`)
→ **Sender chain** (decorator pattern):
  - `ReportableSender` (logs outcome)
  - `RetryableSender` (3 retries, exponential backoff starting at 100ms)
  - `ProducerSender` (validates, builds `ProducerRecord`, calls Kafka)
→ `ProducerContainer.getProducer(cluster, tag)` (routes to correct `StringKafkaProducer`)
→ Kafka broker

Producers are registered at startup by `ProducerBeanRegistrar` (a `BeanDefinitionRegistryPostProcessor`). For each configured cluster it registers three beans: `{cluster}BaseProducer`, `{cluster}GrayProducer`, and an adaptive `{cluster}Producer` that selects BASE or GRAY depending on `SpringContext.isGrayEnvironment()`.

### Consumer Flow

`@EventHandler(cluster, topics)` on a class (meta-annotated with `@KafkaListener`)
→ `EventHandlerAnnotationBeanPostProcessor` (`BeanPostProcessor`) discovers annotated beans, resolves `groupId` and registers metadata into `ListenerContainer`
→ `KafkaListenerContainerFactoryRegistrar` creates one `ConcurrentKafkaListenerContainerFactory` per cluster (named `{cluster}KafkaListenerContainerFactory`) with `AckMode.RECORD` and the custom `MessageDeserializer`
→ `@KafkaHandler` method on the handler class receives the deserialized event

### Multi-Cluster & Gray Routing

Each Kafka cluster entry in config has a `cluster` name and an optional `tag` (BASE or GRAY, default BASE). The framework:

1. Registers separate producers per cluster×tag combination.
2. At send time, `ProducerContainer` selects the producer using the `messageTag` field embedded in the `SendMessage`.
3. At consume time, only factories for the current environment's tag are registered.
4. If GRAY config is missing, `EasyKafkaProperties.setUpGray()` auto-fills it by copying the BASE brokers.

### Key Classes

| Class | Role |
|---|---|
| `EventPublisher` | Static publish API |
| `DefaultMessagePublisher` | Async executor + callback invocation |
| `ProducerBeanRegistrar` | Registers `StringKafkaProducer` beans per cluster/tag |
| `ProducerContainer` | Resolves the correct producer at runtime |
| `ProducerSender` | Core Kafka send; builds `ProducerRecord` with key, headers, timestamp |
| `RetryableSender` | Retry decorator (3 attempts, 100ms→5000ms backoff) |
| `EventHandlerAnnotationBeanPostProcessor` | Discovers `@EventHandler` and `@KafkaHandler`, populates `ListenerContainer` |
| `KafkaListenerContainerFactoryRegistrar` | Dynamically registers listener factories per cluster |
| `ListenerContainer` | Runtime registry of listener metadata; validates incoming messages |
| `EasyKafkaAutoConfiguration` | Auto-config entry point; wires everything in correct order |
| `EasyKafkaProperties` | Binds `easykafka.*` YAML config; auto-fills gray cluster configs |
| `MessageDeserializer` | Custom Kafka deserializer; routes to correct event type using `ListenerContainer` |
| `ListenedRecordInterceptor` | Pre-processes consumer records before dispatch |

### Message Base Classes

`AbstractMessage` is the root. Concrete subtypes used in production code:
- `Event` — standard domain event
- `Broadcast` — broadcast message
- `DataSync` — data sync message

Fields auto-populated by the framework before send: `messageId`, `messageKey` (`@MessageKey`-annotated field), `messageHeader` (`@MessageHeader`-annotated fields), `messageTag`, `messageService`, `messageTopic`, `messageCreateTime`.

### Callbacks

```java
// Default (no-op)
EventPublisher.publish(event);

// Custom callback
EventPublisher.publish(event, new MessageCallback() {
    public void onSuccess() { ... }
    public void onFail(Exception e) { ... }
});
```

Callbacks are invoked asynchronously inside the executor thread pool.

## Configuration Reference

Minimum YAML configuration:

```yaml
easykafka:
  init:
    kafkaCluster:
      - cluster: send          # logical cluster name
        brokers: host:9092     # baseline brokers
      - cluster: send
        brokers: gray-host:9092
        tag: gray              # gray environment brokers
```

Runtime properties (`easykafka.runtime.*`) allow overriding producer/consumer settings (batch size, thread pool size, group ID prefix, etc.).

## CI/CD

GitHub Actions (`.github/workflows/maven-publish.yml`) triggers on GitHub Release creation. It builds with `mvn -B package` on JDK 8 (Temurin) and publishes to GitHub Packages. The `release` Maven profile additionally signs artifacts with GPG and deploys to Sonatype/Maven Central.
