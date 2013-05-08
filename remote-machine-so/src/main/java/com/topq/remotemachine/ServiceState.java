package com.topq.remotemachine;

import jsystem.framework.report.Reporter;

public enum ServiceState {
	NotStarted("service is not started", "Service '' was not running", Reporter.PASS),
	NotResponding("The service is not responding", "The service '' is not responding!", Reporter.FAIL),
	AlreadyRunning("The requested service has already been started", "The service '' is already running", Reporter.PASS),
	Started("service was started successfully", "Service '' was started successfully", Reporter.PASS),
	Stopped("service was stopped successfully", "Service '' was stopped successfully", Reporter.PASS),
	NotFound("The service name is invalid", "The service '' is invalid", Reporter.FAIL),
	NotExist("service does not exist", "The service '' does not exist", 1060, Reporter.FAIL),
	InProgress("The service is starting or stopping", "The service '' is starting or stopping", Reporter.WARNING),
	Failed("The service cannot be started", "The service '' cannot be started - Failed", Reporter.FAIL),
	ExistsYetFailed("service could not be started", "Service '' could not be started", Reporter.FAIL);

	private final String expectedOutput;
	private final String reportText;
	private int errorValue;
	private final int resultValue;

	private ServiceState(String expectedOutput, String reportText, int resultValue) {
		this(expectedOutput, reportText, -1, resultValue);
	}

	private ServiceState(String expectedOutput, String reportText, int errorValue, int resultValue) {
		this.expectedOutput = expectedOutput;
		this.resultValue = resultValue;
		this.reportText = reportText;
		this.errorValue = errorValue;
	}

	public String getReportText(String serviceName) {
		return reportText.replace("''", "'" + serviceName + "'");
	}

	public static ServiceState determine(String output) {
		ServiceState[] states = values();
		for (ServiceState state : states)
			if (state.checkState(output))
				return state;
		throw new IllegalStateException("No service state was found");
	}

	private boolean checkState(String output) {
		return output.contains(expectedOutput)
				&& (errorValue == -1 || output.contains(errorValue + ""));
	}

	public int getResultValue() {
		return resultValue;
	}

}
