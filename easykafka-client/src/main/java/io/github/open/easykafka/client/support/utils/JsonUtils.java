package io.github.open.easykafka.client.support.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * 消息json转换器
 * @author studeyang
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("all")
public final class JsonUtils {

    private static final SerializerFeature[] JSON_CONFIG = new SerializerFeature[]{
            SerializerFeature.WriteClassName
    };

    /**
     * Object转换json
     */
    public static <T> String toJson(T obj) {

        if (null == obj) {
            return null;
        }

        try {
            return JSON.toJSONString(obj, JSON_CONFIG);
        } catch (Exception e) {
            log.error("toJson error, obj:{}", obj, e);
            return null;
        }
    }

    /**
     * json转Object
     */
    public static <T> T toObject(String json, Class<? extends T> objClass) {

        if (!StringUtils.hasText(json)) {
            return null;
        }

        try {
            return JSON.parseObject(json, objClass);
        } catch (Exception e) {
            log.error("toObject error, json:{}", json, e);
            return null;
        }
    }

    public static Object parse(String json) {

        if (!StringUtils.hasText(json)) {
            return null;
        }

        try {
            return JSON.parse(json, Feature.SupportAutoType);
        } catch (Exception e) {
            log.error("toObject error, json:{}", json, e);
            return null;
        }
    }

    /**
     * json转JsonNode
     */
    public static JSONObject toNode(String json) {

        if (!StringUtils.hasText(json)) {
            return null;
        }

        try {
            return JSON.parseObject(json);
        } catch (Exception e) {
            log.error("toNode error, json:{}", json, e);
            return null;
        }
    }

    /**
     * json转Object
     */
    public static <T> T toObject(String json, TypeReference<T> typeReference) {

        if (!StringUtils.hasText(json)) {
            return null;
        }

        try {
            return JSON.parseObject(json, typeReference);
        } catch (Exception e) {
            log.error("toObject error, json:{}", json, e);
        }

        return null;
    }
}
