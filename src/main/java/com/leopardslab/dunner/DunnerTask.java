package com.leopardslab.dunner;

import com.thoughtworks.go.plugin.api.GoApplicationAccessor;
import com.thoughtworks.go.plugin.api.GoPlugin;
import com.thoughtworks.go.plugin.api.GoPluginIdentifier;
import com.thoughtworks.go.plugin.api.annotation.Extension;
import com.thoughtworks.go.plugin.api.exceptions.UnhandledRequestTypeException;
import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoApiResponse;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.apache.commons.io.IOUtils;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Extension
public class DunnerTask implements GoPlugin {
    Logger logger = Logger.getLoggerFor(DunnerTask.class);
    public static final String DOCKER_TASK_NAME = "NAME";
    public static final String DOCKER_IMAGE = "IMAGE";
    public static final String DOCKER_COMMANDS = "COMMANDS";
    public static final String DOCKER_MOUNTS = "MOUNTS";
    public static final String DOCKER_ENVS = "ENVS";

    @Override
    public void initializeGoApplicationAccessor(GoApplicationAccessor goApplicationAccessor) {

    }

    @Override
    public GoPluginIdentifier pluginIdentifier() {
        return new GoPluginIdentifier("task", Arrays.asList("1.0"));
    }

    @Override
    public GoPluginApiResponse handle(GoPluginApiRequest request) throws UnhandledRequestTypeException {
        if ("configuration".equals(request.requestName())) {
            return handleGetConfigRequest();
        } else if ("validate".equals(request.requestName())) {
            return handleValidation(request);
        } else if ("execute".equals(request.requestName())) {
            return handleTaskExecution(request);
        } else if ("view".equals(request.requestName())) {
            return handleTaskView();
        }
        throw new UnhandledRequestTypeException(request.requestName());
    }

    private GoPluginApiResponse handleValidation(GoPluginApiRequest request) {
        HashMap validationResult = new HashMap();
        int responseCode = DefaultGoPluginApiResponse.SUCCESS_RESPONSE_CODE;
        HashMap errorMap = new HashMap();

        Map configMap = (Map) new GsonBuilder().create().fromJson(request.requestBody(), Object.class);
        if (((String) ((Map) configMap.get(DOCKER_IMAGE)).get("value")).trim().isEmpty()) {
            errorMap.put(DOCKER_IMAGE, "Image cannot be empty");
        }
        if (((String) ((Map) configMap.get(DOCKER_TASK_NAME)).get("value")).trim().isEmpty()) {
            errorMap.put(DOCKER_TASK_NAME, "Task name cannot be empty");
        }

        validationResult.put("errors", errorMap);
        return createResponse(responseCode, validationResult);
    }

    private GoPluginApiResponse handleTaskExecution(GoPluginApiRequest request) {
        DunnerTaskExecutor executor = new DunnerTaskExecutor();
        Map executionRequest = (Map) new GsonBuilder().create().fromJson(request.requestBody(), Object.class);
        Map config = (Map) executionRequest.get("config");
        Map context = (Map) executionRequest.get("context");

        Result result = executor.execute(new Config(config), new Context(context));
        return createResponse(result.responseCode(), result.toMap());
    }

    private GoPluginApiResponse handleGetConfigRequest() {
        HashMap config = new HashMap();
        addDunnerConfig(config);
        return createResponse(DefaultGoPluginApiResponse.SUCCESS_RESPONSE_CODE, config);
    }

    private void addDunnerConfig(HashMap config) {
        HashMap dockerTaskName = new HashMap();
        dockerTaskName.put("required", true);
        config.put(DOCKER_TASK_NAME, dockerTaskName);

        HashMap dockerImage = new HashMap();
        dockerImage.put("required", true);
        config.put(DOCKER_IMAGE, dockerImage);

        HashMap dockerCommands = new HashMap();
        dockerCommands.put("required", true);
        config.put(DOCKER_COMMANDS, dockerCommands);

        HashMap dockerMounts = new HashMap();
        dockerMounts.put("required", false);
        config.put(DOCKER_MOUNTS, dockerMounts);

        HashMap dockerEnvs = new HashMap();
        dockerEnvs.put("required", false);
        config.put(DOCKER_ENVS, dockerEnvs);
    }

    private GoPluginApiResponse handleTaskView() {
        int responseCode = DefaultGoApiResponse.SUCCESS_RESPONSE_CODE;
        Map view = new HashMap();
        view.put("displayValue", "Dunner Task");
        try {
            view.put("template", IOUtils.toString(getClass().getResourceAsStream("/views/task.template.html"), "UTF-8"));
        } catch (Exception e) {
            responseCode = DefaultGoApiResponse.INTERNAL_ERROR;
            String errorMessage = "Failed to find template: " + e.getMessage();
            view.put("exception", errorMessage);
            logger.error(errorMessage, e);
        }
        return createResponse(responseCode, view);
    }

    private GoPluginApiResponse createResponse(int responseCode, Map body) {
        final DefaultGoPluginApiResponse response = new DefaultGoPluginApiResponse(responseCode);
        GsonBuilder gb = new GsonBuilder();
        response.setResponseBody(gb.serializeNulls().create().toJson(body));
        return response;
    }
}
