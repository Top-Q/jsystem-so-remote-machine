package com.topq.remotemachine;

import java.io.File;

import com.aqua.sysobj.conn.CliCommand;

public class MappedDriver {
	private final char driveLetter;
	private final File remoteMappedDir;
	private final RemoteMachine remoteMachine;

	private MappedDriver(char driveLetter, RemoteMachine remoteMachine, File dir) {
		this.driveLetter = driveLetter;
		remoteMappedDir = remoteMachine.getAsRemoteFile(dir);
		this.remoteMachine = remoteMachine;
	}

	public final File getFileAsLocal(File remoteFile) {
		if (remoteFile.getAbsolutePath().startsWith(remoteMappedDir.getAbsolutePath())) {
			String uncPath = remoteFile.getAbsolutePath();
			String localPath = uncPath.replace(remoteMappedDir.getAbsolutePath(), driveLetter
					+ ":/");
			return new File(localPath);
		}

		return remoteFile;
	}

	public void enableMapping() throws Exception {
		String enableMappingCommand = "net use " + driveLetter + ": "
				+ remoteMappedDir.getAbsolutePath() + " /user:" + remoteMachine.user
				+ " " + remoteMachine.password;
		remoteMachine.executeCommand("Map "+remoteMappedDir.getAbsolutePath()+" to " +driveLetter
				+":", new CliCommand(enableMappingCommand));
	}

	public void cancelMapping() throws Exception {
		String disableMappingCommand = "net use " + driveLetter + ": /delete";
		remoteMachine.executeCommand("Unmap "+remoteMappedDir.getAbsolutePath()+" to " +driveLetter
				+":", new CliCommand(disableMappingCommand));
	}
}
