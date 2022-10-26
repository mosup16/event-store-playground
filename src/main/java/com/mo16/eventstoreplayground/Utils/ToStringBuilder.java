package com.mo16.eventstoreplayground.Utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class ToStringBuilder {
    private final Map<String, String> props = new LinkedHashMap<>();
    private String className;

    public static ToStringBuilder get() {
        return new ToStringBuilder();
    }

    public ToStringBuilder withClassName(String className) {
        this.className = className;
        return this;
    }

    public ToStringBuilder withProperty(String name, String value) {
        props.put(name, value);
        return this;
    }

    public String string() {
        String body = String.join(", ",
                props.entrySet().stream()
                        .map(entry -> {
                            var name = entry.getKey();
                            var value = entry.getValue();
                            return String.join("=", name, value);
                        }).toList()
        );
        return this.className + "[" + body + "]";
    }
}
