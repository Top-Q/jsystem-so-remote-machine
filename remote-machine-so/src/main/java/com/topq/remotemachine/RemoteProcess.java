package com.topq.remotemachine;

import java.io.File;

import jsystem.extensions.analyzers.text.FindRegex;
import jsystem.utils.StringUtils;

import com.aqua.sysobj.conn.CliCommand;

public class RemoteProcess {

	protected final String name;
	private final String pathToProcess;
	private final String executableName;
	private final File executableFile;
	private int pid;
	protected final RemoteMachine rum;
	private String parameters = "";
	private int timeout = -1;
	private boolean singleInstance = false;

	public RemoteProcess(String name, RemoteMachine rum, File executableFile) {
		this.rum = rum;
		this.name = name;
		this.executableFile = executableFile;
		pathToProcess = executableFile.getParentFile().getAbsolutePath();
		executableName = executableFile.getName();
	}

	/**
	 * launch the process.
	 * 
	 * @throws Exception
	 */
	public final void launchProcess() throws Exception {
		if (singleInstance)
			terminatePreviousExisingProcess(rum.getTasklist());
		String output = launchProcessImpl();
		pid = derivePID_FromLaunchConsole(output);
		rum.getReport().report("ProcessID found: " + pid);
	}

	private void terminatePreviousExisingProcess(String tasklist) throws Exception {
		rum.getReport().report(
				"Killing all matching processes to '" + executableName + "' on remote machine");
		try {
			rum.setTestAgainstObject(tasklist);
			FindRegex processDetails = new FindRegex(executableName
					+ "\\s+(\\d+)\\s+\\P{Space}+\\s+(\\d)\\s+(\\P{Space}+)\\sK", false);
			int instances = processDetails.instanceCount(tasklist);
			if (instances == 0)
				rum.getReport().report("No insances of '" + executableName + "' were found!!");
			for (int i = 0; i < instances; i++) {
				String pidString = processDetails.findRegex(1, 2, tasklist);
				if (pidString == null)
					throw new NullPointerException(
							"Should not happend! process instace count is wong");
				try {
					killProcessImpl(Integer.parseInt(pidString));
				} catch (Exception e) {
					rum.getReport().report("Error killing process '" + executableName + "'",
							StringUtils.getStackTrace(e), false);
				}
			}
			if (instances > 0) {
				rum.getReport().report("Delay 20 sec after terminating process");
				try {
					Thread.sleep(20000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * The launching implementation.
	 * 
	 * @return The output string from the launch command.
	 * @throws Exception
	 */
	protected String launchProcessImpl() throws Exception {
		CliCommand command = new CliCommand(getFullLaunchCommand());
		if (timeout > 0)
			command.setTimeout(timeout);

		rum.launchCommand("Launching process '" + name + "'", command);
		return command.getResult();
	}

	public final void killProcess() throws Exception {
		String output = killProcessImpl(pid);
		pid = derivePID_FromKillConsole(output);
	}

	protected String killProcessImpl(int pid) throws Exception {
		CliCommand command = new CliCommand(getFullKillCommand(pid));
		String description = "Terminating process '" + name + "'";
		if (pid != -1)
			description += " pID=" + pid;
		rum.launchCommand(description, command);
		return command.getResult();
	}

	private String getFullLaunchCommand() {
		String command = new File(pathToProcess, executableName).getAbsolutePath();
		if (parameters.length() > 0)
			command += " " + parameters;
		return command;
	}

	private String getFullKillCommand(int pid) {
		if (pid == -1)
			return "Taskkill /f /t /im " + executableName;
		return "Taskkill /f /t /PID " + pid;
	}

	protected int derivePID_FromLaunchConsole(String launchingOutput) {
		return -1;
	}

	protected int derivePID_FromKillConsole(String killOutput) {
		return -1;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	public String getParameters() {
		return parameters;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public String getPathToProcess() {
		return pathToProcess;
	}

	public String getExecutableName() {
		return executableName;
	}

	public String getFullPathToExecutable() {
		return executableFile.getAbsolutePath();
	}

	public boolean isSingleInstance() {
		return singleInstance;
	}

	public void setSingleInstance(boolean singleInstance) {
		this.singleInstance = singleInstance;
	}

	public void launchProcess(int delayDuration) throws Exception {
		launchProcess();
		rum.getReport().report("Delaying for " + delayDuration/1000 + " sec");
		Thread.sleep(delayDuration);
	}
}