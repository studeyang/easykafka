package io.github.open.easykafka.event;

import lombok.Data;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/5/8
 */
@Data
public class SubExampleEvent extends ExampleEvent {
    private String sub;
}
