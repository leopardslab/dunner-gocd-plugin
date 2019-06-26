package com.leopardslab.dunner;

import java.io.FileOutputStream;  
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import com.thoughtworks.go.plugin.api.logging.Logger;

public class DunnerTaskFileCreator {
	Logger logger = Logger.getLoggerFor(DunnerTask.class);

	public static final String taskFileDir = "gocd_dunner";
	private String taskFilePath;
	private DunnerTaskFile task;

	public DunnerTaskFileCreator(Config taskConfig) {
		String[] taskCommands = clean(taskConfig.getCommands());
		String[] taskMounts = clean(taskConfig.getMounts());
		String[] taskEnvs = clean(taskConfig.getEnvs());
		this.task = new DunnerTaskFile(taskConfig.getName(), taskConfig.getImage(), taskCommands, taskMounts, taskEnvs);
	}

	public String saveToTempFile() throws IOException {
        String workingDir = System.getProperty("user.dir");
        String taskFileDirPath = String.format("%s/%s", workingDir, taskFileDir);
        File taskDir = new File(taskFileDirPath);
        if (!taskDir.exists()) {
        	boolean created = taskDir.mkdirs();
        	if (!created) {
        		throw new IOException("Failed to create directory for task file");
        	}
        }

        String fileContents = getTaskFileContents();
        File file = new File(String.format("%s/.dunner_%s.yaml", taskFileDirPath, new Date().getTime()));
        taskFilePath = file.getAbsolutePath();
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(fileContents.getBytes());
        return taskFilePath;
	}

	public DunnerTaskFile getTask() {
		return this.task;
	}

	private String[] clean(String value) {
		if (value == null || value == "") return new String[0];
		return value.split("\\r?\\n");
	}

	private String getTaskFileContents() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%s:\n", task.name));
		sb.append(String.format("  - image: %s\n", task.image));
		sb.append(String.format("    name: %s\n", task.name));

		sb.append("    commands:\n");
		for (String c : task.commands) {
			sb.append(String.format("      - [%s]\n", getStringCommand(c)));
		}
		sb.append("    mounts:\n");
		for (String m : task.mounts) {
			sb.append(String.format("      - \"%s\"\n", m));
		}
		sb.append("    envs:\n");
		for (String e : task.envs) {
			sb.append(String.format("      - \"%s\"\n", e));
		}
		return sb.toString();
	}

	public void deleteTaskFile() {
		try {
			FileUtils.deleteDirectory(new File(taskFilePath).getParentFile());
		} catch(IOException e) {
			logger.info("Failed to delete task file");
		}
	}

	private String getStringCommand(String c) {
		String[] words = c.split("\\s+");
		List<String> list = new ArrayList<>();
		for (String w : words) {
			list.add(String.format("\"%s\"", w));
		}
		return String.join(",", list);
	}

}