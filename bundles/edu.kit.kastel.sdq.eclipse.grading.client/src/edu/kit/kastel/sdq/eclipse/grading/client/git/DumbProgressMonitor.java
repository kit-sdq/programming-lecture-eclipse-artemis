package edu.kit.kastel.sdq.eclipse.grading.client.git;

import org.eclipse.core.runtime.IProgressMonitor;

public class DumbProgressMonitor implements IProgressMonitor {

	private boolean isCancelled = false;
	
	private void printLn(String line) {
		System.out.println("[DumbProgressMonitor] " + line);
	}
	
	@Override
	public void beginTask(String name, int totalWork) {
		printLn("beginTask With name=" + name + ",totalWork=" + totalWork);
	}

	@Override
	public void done() {
		// TODO Auto-generated method stub
		printLn("done");
		
	}

	@Override
	public void internalWorked(double work) {
		printLn("internalWorked with work=" + work);
		
	}

	@Override
	public boolean isCanceled() {
		printLn("isCanceled called");
		// TODO Auto-generated method stub
		return this.isCancelled;
	}

	@Override
	public void setCanceled(boolean value) {
		printLn("setCanceled called");
		// TODO Auto-generated method stub
		this.isCancelled = value;
		
	}

	@Override
	public void setTaskName(String name) {
		printLn("setTaskName called with name=" + name);
		// TODO Auto-generated method stub
		
	}

	@Override
	public void subTask(String name) {
		printLn("subTask called with name=" + name);
		// TODO Auto-generated method stub
		
	}

	@Override
	public void worked(int work) {
		printLn("worked called with work=" + work);
		// TODO Auto-generated method stub
		
	}

}
