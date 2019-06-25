package com.leopardslab.dunner;

import com.leopardslab.dunner.commands.DunnerDoCommand;
import com.thoughtworks.go.plugin.api.logging.Logger;


public class DunnerTaskExecutor {
    Logger logger = Logger.getLoggerFor(DunnerTaskExecutor.class);

    public Result execute(Config config, Context context) {
        DunnerTaskFileCreator tfc = new DunnerTaskFileCreator(config);
        try {
            String taskFilePath = tfc.saveToTempFile();
            return runCommand(context, config, taskFilePath);
        } catch (Exception ex) {
            logger.error("Error running the command", ex);
            return new Result(false, "Failed while running the task", ex);
        } finally {
            tfc.deleteTaskFile();
        }
    }

    private Result runCommand(Context taskContext, Config taskConfig, String taskFilePath) throws Exception {
        try {
            new DunnerDoCommand(taskContext, taskConfig, taskFilePath).run();
            return new Result(true, "Finished");
        } catch(Exception ex) {
            logger.error("Error running the command", ex);
            return new Result(false, "Task execution failed");
        }
    }
}