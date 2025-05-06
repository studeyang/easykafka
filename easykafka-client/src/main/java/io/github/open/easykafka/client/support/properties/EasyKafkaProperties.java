package io.github.open.easykafka.client.support.properties;

import io.github.open.easykafka.client.model.Tag;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/16
 */
@Data
public class EasyKafkaProperties implements InitializingBean {

    private static final int TAG_SUM = Tag.values().length;
    private InitProperties init;
    private RuntimeProperties runtime = new RuntimeProperties();

    @Override
    public void afterPropertiesSet() {
        setUpGray();
    }

    private void setUpGray() {
        Map<String, List<InitProperties.KafkaCluster>> clusterMap = init.getKafkaCluster().stream()
                .collect(Collectors.groupingBy(InitProperties.KafkaCluster::getCluster));

        clusterMap.forEach((clusterName, clusterList) -> {
            // 当前cluster未配置灰度
            if (clusterMap.get(clusterName).size() < TAG_SUM) {
                InitProperties.KafkaCluster baseKafkaCluster = clusterList.get(0);

                InitProperties.KafkaCluster grayKafkaCluster = new InitProperties.KafkaCluster();
                grayKafkaCluster.setTag(Tag.GRAY);
                grayKafkaCluster.setCluster(baseKafkaCluster.getCluster());
                grayKafkaCluster.setBrokers(baseKafkaCluster.getBrokers());

                init.getKafkaCluster().add(grayKafkaCluster);
            }
        });
    }

}
