package com.leopardslab.dunner;

import java.util.Map;

public class Config {
    private final String image;
    private final String name;
    private final String commands;
    private final String mounts;
    private final String envs;

    public Config(Map config) {
        image = getValue(config, DunnerTask.DOCKER_IMAGE);
        name = getValue(config, DunnerTask.DOCKER_TASK_NAME);
        commands = getValue(config, DunnerTask.DOCKER_COMMANDS);
        mounts = getValue(config, DunnerTask.DOCKER_MOUNTS);
        envs = getValue(config, DunnerTask.DOCKER_ENVS);
    }

    private String getValue(Map config, String property) {
        return (String) ((Map) config.get(property)).get("value");
    }

    public String getImage() {
    	return image;
    }

    public String getName() {
    	return name;
    }

    public String getCommands() {
    	return commands;
    }

    public String getMounts() {
    	return mounts;
    }

    public String getEnvs() {
    	return envs;
    }
}