package edu.kit.kastel.sdq.eclipse.grading.client.git;

import org.eclipse.core.runtime.IProgressMonitor;

public class DumbProgressMonitor implements IProgressMonitor {

	private boolean isCancelled = false;

	@Override
	public void beginTask(String name, int totalWork) {
		this.printLn("beginTask With name=" + name + ",totalWork=" + totalWork);
	}

	@Override
	public void done() {
		this.printLn("done");
	}

	@Override
	public void internalWorked(double work) {
		this.printLn("internalWorked with work=" + work);

	}

	@Override
	public boolean isCanceled() {
		this.printLn("isCanceled called");
		return this.isCancelled;
	}

	private void printLn(String line) {
		System.out.println("[DumbProgressMonitor] " + line);
	}

	@Override
	public void setCanceled(boolean value) {
		this.printLn("setCanceled called");
		this.isCancelled = value;
	}

	@Override
	public void setTaskName(String name) {
		this.printLn("setTaskName called with name=" + name);
	}

	@Override
	public void subTask(String name) {
		this.printLn("subTask called with name=" + name);
	}

	@Override
	public void worked(int work) {
		this.printLn("worked called with work=" + work);
	}

}
