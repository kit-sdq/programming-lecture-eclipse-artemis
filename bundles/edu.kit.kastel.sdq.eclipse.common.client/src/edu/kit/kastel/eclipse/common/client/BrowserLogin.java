/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.client;

import java.util.Objects;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

public class BrowserLogin extends Dialog {
	private static final int WIDTH = 1024;
	private static final int HEIGHT = 1024;

	private static final long MIN_TIME_TO_LOGIN_IN_MS = 5000;
	private static final long POLL_INTERVAL = 1000;

	private static final String CALLBACK_NAME = "tokenCallback";

	private static final ILog log = Platform.getLog(BrowserLogin.class);

	private final String hostname;
	private String token;
	private Browser browser;

	private boolean closed = false;

	private long lastSuccessWasAlreadyLoggedIn;

	public BrowserLogin(String hostname) {
		super((Shell) null);
		this.hostname = hostname;
		this.setShellStyle((SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL | SWT.ON_TOP) & ~SWT.CLOSE);
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Artemis Login");
		newShell.setSize(WIDTH, HEIGHT);

		// Set to mid
		Monitor mon = Display.getDefault().getMonitors()[0];
		int newLeftPos = (mon.getBounds().width - WIDTH) / 2;
		int newTopPos = (mon.getBounds().height - HEIGHT) / 2;
		newShell.setLocation(newLeftPos, newTopPos);
	}

	protected Control createDialogArea(Composite parent) {
		final Composite comp = (Composite) super.createDialogArea(parent);
		final GridLayout layout = (GridLayout) comp.getLayout();
		layout.numColumns = 1;

		browser = new Browser(comp, SWT.EDGE);
		browser.setLayoutData(new GridData(GridData.FILL_BOTH));
		browser.setJavascriptEnabled(true);
		browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		browser.setUrl(hostname);

		new BrowserFunction(browser, CALLBACK_NAME) {
			@Override
			public Object function(Object[] parameters) {
				handleTokenCallback(parameters);
				return null;
			}
		};

		return comp;
	}

	@Override
	public int open() {
		closed = false;
		lastSuccessWasAlreadyLoggedIn = System.currentTimeMillis();
		int result = super.open();
		closed = true;
		return result;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CLOSE_LABEL, true);
	}

	@Override
	protected void cancelPressed() {
		closed = true;
		super.cancelPressed();
	}

	/**
	 * Gets the JWT Token from Artemis
	 * 
	 * @return the JWT Token or {@code null} if no token provided.
	 */
	public String getToken() {
		if (this.token != null)
			return token;

		closed = false;

		Thread pollingDaemon = new Thread(() -> pollingThread());
		pollingDaemon.setDaemon(true);
		pollingDaemon.start();

		log.info("Opened Browser. Waiting for token");
		Display.getDefault().syncExec(() -> this.open());
		log.info("Got Token");
		return this.token;
	}

	private void pollingThread() {
		try {
			var display = Display.getDefault();
			while (!closed) {
				Thread.sleep(POLL_INTERVAL);
				display.asyncExec(() -> callBrowserFunction());
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

	}

	private void handleTokenCallback(Object[] parameters) {
		if (parameters == null || parameters.length != 1 || !(parameters[0]instanceof String newToken)) {
			if (token != null) {
				log.info("Logout occured");
				token = null;
			}
			return;
		}

		// Crop '"' at beginning and end
		newToken = newToken.substring(1, newToken.length() - 1);

		if (!Objects.equals(token, newToken)) {
			log.info("Got a new Token");
		}

		token = newToken;

		// Close Dialog
		if (!wasAlreadyLoggedIn()) {
			Display.getDefault().asyncExec(() -> BrowserLogin.this.okPressed());
		}
	}

	private void callBrowserFunction() {
		try {
			browser.execute(CALLBACK_NAME + "(localStorage.getItem(\"jhi-authenticationtoken\"));");
		} catch (SWTException e) {
			if (e.getMessage().equals("Widget is disposed")) {
				return;
			}
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	private boolean wasAlreadyLoggedIn() {
		long currentTime = System.currentTimeMillis();
		long diff = currentTime - lastSuccessWasAlreadyLoggedIn;
		if (diff > MIN_TIME_TO_LOGIN_IN_MS) {
			return false;
		}
		lastSuccessWasAlreadyLoggedIn = currentTime;
		return true;
	}
}
