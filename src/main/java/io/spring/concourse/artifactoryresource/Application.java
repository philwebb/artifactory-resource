package io.spring.concourse.artifactoryresource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import io.spring.concourse.artifactoryresource.command.CheckCommand;
import io.spring.concourse.artifactoryresource.command.Command;
import io.spring.concourse.artifactoryresource.command.InCommand;
import io.spring.concourse.artifactoryresource.command.OutCommand;

/**
 * Main application entry point.
 */
public final class Application {

	private final Map<String, Command> commands = new HashMap<>();

	public Application() {
		setupCommands();
	}

	protected void setupCommands() {
		addCommand("check", new CheckCommand());
		addCommand("in", new InCommand());
		addCommand("out", new OutCommand());
	}

	private void addCommand(String name, Command command) {
		this.commands.put(name, command);
	}

	public void run(String[] args) {
		if (args.length == 0) {
			throw new RuntimeException("Missing arguments");
		}
		String command = args[0];
		String[] commandArgs = Arrays.asList(args).subList(1, args.length)
				.toArray(new String[] {});
		getCommand(command).run(commandArgs);
	}

	protected final Command getCommand(String name) {
		Command command = this.commands.get(name);
		if (command == null) {
			throw new RuntimeException("Unknown command " + name);
		}
		return command;
	}

	public static void main(String[] args) {
		try {
			new Application().run(args);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}

}
