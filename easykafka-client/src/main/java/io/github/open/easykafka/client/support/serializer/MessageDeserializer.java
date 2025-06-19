package io.github.open.easykafka.client.support.serializer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import io.github.open.easykafka.client.message.AbstractMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Arrays;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/23
 */
@Slf4j
public class MessageDeserializer implements Deserializer<AbstractMessage> {

    @Override
    public AbstractMessage deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }
        try {
            String jsonData = new String(data);
            Object object = JSON.parse(jsonData, Feature.SupportAutoType);
            if (object instanceof AbstractMessage) {
                return (AbstractMessage) object;
            } else {
                log.error("Can't deserialize data [{}] from topic [{}]", jsonData, topic);
            }
        } catch (Exception e) {
            log.error("Can't deserialize data [{}] from topic [{}]", Arrays.toString(data), topic, e);
        }
        return null;
    }

}
