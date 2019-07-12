package com.leopardslab.dunner;
import java.util.List;

public class DunnerTaskFile {
	public String name;
	public String image;
	public List<String> commands;
	public List<String> mounts;
	public List<String> envs;

	public DunnerTaskFile(String name, String image, List<String> commands, List<String> mounts, List<String> envs) {
		this.name = name;
		this.image = image;
		this.commands = commands;
		this.mounts = mounts;
		this.envs = envs;
	}
}