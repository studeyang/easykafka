package io.github.open.easykafka.client.support.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * @author 005964
 */
@UtilityClass
@Slf4j
public class AnnotationModifyUtils {

    /**
     * 修改方法级别的注解属性值
     *
     * @param annotation    原始注解
     * @param attributeName 属性名
     * @param value         新值
     */
    @SuppressWarnings("unchecked")
    public static <T extends Annotation> void modifyMethodAnnotation(T annotation, String attributeName, Object value) {
        try {
            Object annotationProxy = Proxy.getInvocationHandler(annotation);

            Field field = ReflectionUtils.findField(annotationProxy.getClass(), "memberValues");
            if (field == null) {
                log.error("'memberValues' Field Not Found.");
                return;
            }

            ReflectionUtils.makeAccessible(field);

            Map<String, Object> attributeMap = (Map<String, Object>) field.get(annotationProxy);

            attributeMap.put(attributeName, value);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to modify annotation attribute value", e);
        }
    }

}