package com.topq.remotemachine;

import java.util.ArrayList;

import jsystem.framework.report.Reporter;

import com.aqua.sysobj.conn.CliCommand;

public class RemoteService {
	private static final int ServiceActionTimeout = 120000;
	private final String serviceName;
	private final String serviceProcessName;
	private final RemoteMachine rum;

	public RemoteService(String serviceName, String serviceProcessName, RemoteMachine rum) {
		this.serviceName = serviceName;
		this.rum = rum;
		this.serviceProcessName = serviceProcessName;
	}

	public void start() throws Exception {
		if (serviceProcessName != null)
			rum.killAllMatchingProcess(serviceProcessName);
		rum.getReport().report("Starting Service '" + serviceName + "'", true);

		ServiceState state = performServiceAction("Start '" + serviceName + "' Service",
				"net START \"" + serviceName + "\"");
		rum.getReport().report(state.getReportText(serviceName), true);
	}

	public void stop() throws Exception {
		rum.getReport().report("Stoping Service '" + serviceName + "'", true);
		ServiceState state = performServiceAction("Stop '" + serviceName + "' Service",
				"net STOP \"" + serviceName + "\"");
		rum.getReport().report(state.getReportText(serviceName), true);
	}

	private ServiceState performServiceAction(String cmdLabel, String cmdText) throws Exception {
		ServiceState state;
		CliCommand cmd = new CliCommand();
		ArrayList<String> commands = new ArrayList<String>();
		commands.add(cmdText);
		cmd.setCommands(commands.toArray(new String[commands.size()]));
		cmd.setTimeout(ServiceActionTimeout);
		rum.executeCommand(cmdLabel, cmd);
		state = ServiceState.determine(cmd.getResult());
		if (state.getResultValue() == Reporter.FAIL)
			throw new Exception(state.getReportText(serviceName));
		return state;
	}

	public String getServiceName() {
		return serviceName;
	}

	@Override
	public String toString() {
		return serviceName + "@" + rum.toString();
	}

}
