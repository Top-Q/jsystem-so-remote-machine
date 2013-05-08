package com.topq.remotemachine;

/**
 * Flow execution mode.
 */
public enum RmExceptionType {

	MACHINE_NOT_FOUND("The network path was not found"),
	BAD_USER_NAME_OR_PASSWORD("unknown user name or bad password"),
	UNKNOWN("Unknown exception");

	public final String exceptionMessage;

	private RmExceptionType(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}

	@Override
	public String toString() {
		return exceptionMessage;
	}
	
	public static RmExceptionType string2enum(String string) {
		for (RmExceptionType enumString : RmExceptionType.values()) {
			if (string.contains(enumString.toString())) {
				return enumString;
			}
		}
		return UNKNOWN;
	}
	
}
