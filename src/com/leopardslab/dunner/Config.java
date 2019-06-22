package com.leopardslab.dunner;

import java.util.Map;

public class Config {
    public final String image;

    public Config(Map config) {
        image = getValue(config, DunnerTask.DOCKER_IMAGE);
    }

    private String getValue(Map config, String property) {
        return (String) ((Map) config.get(property)).get("value");
    }
}