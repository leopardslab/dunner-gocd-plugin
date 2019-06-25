package com.leopardslab.dunner.commands;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.leopardslab.dunner.Context;
import com.leopardslab.dunner.Config;

import java.util.ArrayList;
import java.util.List;

public class DunnerDoCommand implements Command {
    protected List<String> command = new ArrayList<>();
    protected static JobConsoleLogger logger = JobConsoleLogger.getConsoleLogger();

    public DunnerDoCommand(Context taskContext, Config taskConfig, String taskFilePath) {
        command.add("dunner");
        command.add("do");
        command.add(taskConfig.getName());
        command.add("-t");
        command.add(taskFilePath);
    }

    public void run() throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();
        logger.readErrorOf(process.getErrorStream());
        logger.readOutputOf(process.getInputStream());

        int exitCode = process.waitFor();
        process.destroy();

        if (exitCode != 0) {
            throw new Exception("Failed while running task");
        }
    }
}