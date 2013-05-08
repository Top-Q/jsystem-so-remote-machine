package com.topq.remotemachine;

import java.io.File;

import jsystem.extensions.analyzers.text.FindText;

import com.aqua.sysobj.conn.CliCommand;
import com.aqua.sysobj.conn.CmdConnection;

public class PSExecRemoteProcess extends RemoteProcess {
	
	private String pathToPSExec = "d:\\Programs\\PS Tools\\PsExec.exe";
	private int sessionID = 0;

	public PSExecRemoteProcess(String name, RemoteMachine rum, File executableFile, String pathToPSExec) {
		super(name, rum, executableFile);
		this.pathToPSExec = pathToPSExec;
		File psexecFile = new File(pathToPSExec);
		if (!psexecFile.exists())
			throw new IllegalArgumentException("File Does not exist on the running machine: " + pathToPSExec);
	}

	@Override
	protected String launchProcessImpl() throws Exception {
		CmdConnection cmdCon = new CmdConnection();
		cmdCon.setCloneOnEveryOperation(true);
		CliCommand cliCommand = new CliCommand("\"" + pathToPSExec + "\" \\\\" + rum.getHost() + " -u \"" + rum.getUser() + "\" -p \""
				+ rum.getPassword() + "\" -d -i " + getSessionID() + " -w \"" + getPathToProcess() + "\" \"" + getFullPathToExecutable() + "\" "
				+ getParameters());
		cliCommand.addMusts(new String[]{"process ID"});
		cmdCon.handleCliCommand("", cliCommand);		
		return cliCommand.getResult();
	}

	@Override
	protected int derivePID_FromKillConsole(String killOutput) {
		return super.derivePID_FromKillConsole(killOutput);
	}

	@Override
	protected int derivePID_FromLaunchConsole(String launchingOutput) {
		rum.setTestAgainstObject(launchingOutput);
		FindText ft = new FindText("process ID (\\d+)", true, true, 2);
		rum.isAnalyzeSuccess(ft);
		String pid = ft.getCounter();
		if (pid.length() == 0)
			return -1;
		try {
			return Integer.parseInt(pid);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public int getSessionID() {
		return sessionID;
	}

	public void setSessionID(int sessionID) {
		this.sessionID = sessionID;
	}
}
