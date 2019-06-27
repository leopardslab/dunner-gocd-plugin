package com.leopardslab.dunner;

import com.leopardslab.dunner.commands.DunnerDoCommand;
import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;

public class DunnerTaskExecutor {
    Logger logger = Logger.getLoggerFor(DunnerTaskExecutor.class);
    private static JobConsoleLogger consoleLogger = JobConsoleLogger.getConsoleLogger();

    public Result execute(Config config, Context context) {
        DunnerTaskFileCreator tfc = new DunnerTaskFileCreator(config, context);
        String taskFilePath = "";
        try {
            taskFilePath = tfc.saveToTempFile();
        } catch (Exception ex) {
            logger.error("Failed to create dunner task file", ex);
            return new Result(false, "Failed to create dunner task file");
        }
        Result result = runCommand(context, config, taskFilePath);
        consoleLogger.printLine(String.format("Dunner task file saved at %s", taskFilePath));
        return result;
    }

    private Result runCommand(Context taskContext, Config taskConfig, String taskFilePath) {
        try {
            new DunnerDoCommand(taskContext, taskConfig, taskFilePath).run();
            return new Result(true, "Task execution complete");
        } catch(Exception ex) {
            logger.error("Error running the command", ex);
            return new Result(false, "Task execution failed");
        }
    }
}