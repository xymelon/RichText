package com.xycoding.richtext;

import java.util.Map;

/**
 * Created by xymelon on 2017/4/28.
 */
public class TagBlock {

    private final String name;
    private Map<String, String> attributes;

    public TagBlock(String name) {
        this.name = name;
    }

    public TagBlock(String name, Map<String, String> attributes) {
        this.name = name;
        this.attributes = attributes;
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

}
