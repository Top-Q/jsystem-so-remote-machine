package com.topq.remotemachine.tests;

import java.io.File;

import junit.framework.SystemTestCase4;

import org.junit.Before;
import org.junit.Test;

import com.topq.remotemachine.PSExecRemoteProcess;
import com.topq.remotemachine.RemoteMachine;

public class RemoteMachineOperations extends SystemTestCase4 {

	private RemoteMachine local;
	private RemoteMachine remote;
	
	@Before
	public void loadSystemObject() throws Exception {
		remote = (RemoteMachine)system.getSystemObject("remoteMachine");
		local = new RemoteMachine("127.0.0.1", "qaqa", "nice2011!");
	}
	
	@Test
	public void copyFileToRemoteMachine() throws Exception {		
		local.copyFilesTo(new File[]{new File("c:\\RemoteMachineCopy.txt")}, remote, new File("c:\\"));
	}
	
	@Test
	public void copyDirToRemoteMachine() throws Exception {		
		local.copyFilesTo(new File[]{new File("C:\\Automation\\work\\Situator")}, remote, new File("c:\\Automation"));
	}
	
	@Test
	public void startPsExec() throws Exception {
		PSExecRemoteProcess process = new PSExecRemoteProcess("TestComplete", remote, new File("c:\\Windows\\System32\\calc.exe"), "c:\\Program Files\\PsTools\\PsExec.exe");
		process.launchProcess();
		sleep(100000);
	}
	
}
