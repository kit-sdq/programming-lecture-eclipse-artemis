package edu.kit.kastel.sdq.eclipse.grading.api.alerts;

public interface IAlertObserver {

	void error(String errorMsg, Throwable cause);

	void info(String infoMsg);

	void warn(String warningMsg);
}
