package io.github.open.easykafka.client.support.converter;

import lombok.experimental.UtilityClass;

import java.io.PrintWriter;
import java.io.Writer;

/**
 * 异常转换器
 * @author studeyang
 */
@UtilityClass
public final class ExceptionConverter {

    /**
     * 获取异常根信息
     */
    public static String getCause(Throwable e) {
        Writer writer = new StringBuilderWriter();
        e.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }

    static class StringBuilderWriter extends Writer {

        private final StringBuilder builder;

        public StringBuilderWriter() {
            this.builder = new StringBuilder(512);
        }

        @Override
        public Writer append(char value) {
            builder.append(value);
            return this;
        }

        @Override
        public Writer append(CharSequence value) {
            builder.append(value);
            return this;
        }

        @Override
        public Writer append(CharSequence value, int start, int end) {
            builder.append(value, start, end);
            return this;
        }

        @Override
        public void close() {
            // no-op
        }

        @Override
        public void flush() {
            // no-op
        }

        @Override
        public void write(String value) {
            if (value != null) {
                builder.append(value);
            }
        }

        @Override
        public void write(char[] value, int offset, int length) {
            if (value != null) {
                builder.append(value, offset, length);
            }
        }

        @Override
        public String toString() {
            return builder.toString();
        }
    }
}
