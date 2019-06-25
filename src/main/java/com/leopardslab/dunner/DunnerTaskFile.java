package com.leopardslab.dunner;

public class DunnerTaskFile {
	public String name;
	public String image;
	public String[] commands;
	public String[] mounts;
	public String[] envs;

	public DunnerTaskFile(String name, String image, String[] commands, String[] mounts, String[] envs) {
		this.name = name;
		this.image = image;
		this.commands = commands;
		this.mounts = mounts;
		this.envs = envs;
	}
}