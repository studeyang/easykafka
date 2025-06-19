package io.github.open.easykafka.client.support;

import io.github.open.easykafka.client.model.MessageConstant;
import io.github.open.easykafka.client.support.properties.EasyKafkaProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

/**
 * Spring容器上下文
 *
 * @author studeyang
 */
@Slf4j
public final class SpringContext implements ApplicationContextAware {

    @Getter
    @Setter(value = AccessLevel.PRIVATE)
    private static ApplicationContext context;
    @Getter
    @Setter(value = AccessLevel.PRIVATE)
    private static String service;
    @Getter
    @Setter(value = AccessLevel.PRIVATE)
    private static Integer serverPort;

    @Getter
    @Setter(value = AccessLevel.PRIVATE)
    private static String groupIdPrefix;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        setContext(applicationContext);
        setService(getProperty(MessageConstant.APPLICATION_NAME));
        setServerPort(Integer.valueOf(getProperty("server.port")));
        initGroupIdPrefix();
    }

    private static void initGroupIdPrefix() {
        String groupIdPrefix = context.getBean(EasyKafkaProperties.class).getRuntime().getConsumer().getGroupIdPrefix();
        if (StringUtils.hasText(groupIdPrefix)) {
            setGroupIdPrefix(groupIdPrefix);
        }

        try {
            String serviceName = SpringContext.getService();
            int lastDashIndex = serviceName.lastIndexOf("-");
            String lastPart = serviceName.substring(lastDashIndex + 1);
            // 示例: Send-Post.Biz-Post-RouteStaff
            // 该消费者组表示: 寄件使用Post服务消费Biz-Post-RouteStaff这个Topic
            setGroupIdPrefix("Send-" + Character.toUpperCase(lastPart.charAt(0)) + lastPart.substring(1) + ".");
        } catch (Exception e) {
            throw new IllegalStateException("Can't get default groupId");
        }
    }

    public static <T> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }

    public static <T> T getBean(String name, Class<T> beanClass) {
        return context.getBean(name, beanClass);
    }

    public static String getProperty(String key) {
        return context.getEnvironment().getProperty(key);
    }

    public static boolean isDevEnvironment() {
        String[] profiles = context.getEnvironment().getActiveProfiles();
        return profiles.length > 0 && "default".equals(profiles[0]);
    }

    public static boolean isGrayEnvironment() {
        try {
            String envTag = System.getenv("ENV_TAG");
            return "gray".equals(envTag);
        } catch (Exception e) {
            log.warn("Gray Environment Recognize Error", e);
            return false;
        }
    }

    public static <T> T register(T bean) {
        String clazzName = bean.getClass().getSimpleName();
        String beanName = clazzName.substring(0, 1).toLowerCase().concat(clazzName.substring(1));
        return register(beanName, bean);
    }

    public static <T> T register(String beanName, T bean) {
        AutowireCapableBeanFactory beanFactory = context.getAutowireCapableBeanFactory();
        if (beanFactory instanceof DefaultListableBeanFactory) {
            DefaultListableBeanFactory listableBeanFactory = (DefaultListableBeanFactory) beanFactory;
            listableBeanFactory.registerSingleton(beanName, bean);
            listableBeanFactory.autowireBean(bean);
            return (T) listableBeanFactory.initializeBean(bean, beanName);
        }
        return bean;
    }

}
