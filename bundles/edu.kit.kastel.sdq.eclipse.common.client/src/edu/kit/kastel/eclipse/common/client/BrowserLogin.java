/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.client;

import java.util.Objects;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.browser.Browser;
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

	private static final ILog log = Platform.getLog(BrowserLogin.class);

	private final String fullUrl;
	private String token;
	private Browser browser;

	private boolean closed = false;

	private long lastSuccessWasAlreadyLoggedIn;

	public BrowserLogin(String fullUrl) {
		super((Shell) null);
		this.fullUrl = fullUrl;
		this.setShellStyle((SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MODELESS | SWT.ON_TOP) & ~SWT.CLOSE);
	}

	@Override
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

	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite comp = (Composite) super.createDialogArea(parent);
		final GridLayout layout = (GridLayout) comp.getLayout();
		layout.numColumns = 1;

		if (this.isWindows10orAbove()) {
			this.browser = new Browser(comp, SWT.EDGE);
		} else {
			this.browser = new Browser(comp, SWT.NONE);
		}
		this.browser.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.browser.setJavascriptEnabled(true);
		this.browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		this.browser.setUrl(this.fullUrl);
		return comp;
	}

	@Override
	public int open() {
		this.closed = false;
		this.lastSuccessWasAlreadyLoggedIn = System.currentTimeMillis();
		int result = super.open();
		this.closed = true;
		return result;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		this.createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CLOSE_LABEL, true);
	}

	@Override
	protected void cancelPressed() {
		this.closed = true;
		super.cancelPressed();
	}

	/**
	 * Gets the JWT Token from Artemis
	 *
	 * @return the JWT Token or {@code null} if no token provided.
	 */
	public String getToken() {
		if (this.token != null) {
			return this.token;
		}

		this.closed = false;

		Thread pollingDaemon = new Thread(this::pollingThread);
		pollingDaemon.setDaemon(true);
		pollingDaemon.start();

		log.info("Opened Browser. Waiting for token");
		Display.getDefault().syncExec(this::open);
		log.info("Got Token: " + (this.token != null));
		return this.token;
	}

	private void pollingThread() {
		try {
			var display = Display.getDefault();
			while (!this.closed) {
				Thread.sleep(POLL_INTERVAL);
				display.asyncExec(this::readCookieAndSetToken);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

	}

	private void readCookieAndSetToken() {
		try {
			String jwtToken = Browser.getCookie("jwt", this.fullUrl);

			if (jwtToken == null && this.token != null) {
				log.info("Logout occured");
				this.token = null;
				return;
			}

			if (jwtToken == null || Objects.equals(this.token, jwtToken)) {
				return;
			}

			log.info("Got a new Token: " + jwtToken);
			this.token = jwtToken;

			// Close Dialog
			if (!this.wasAlreadyLoggedIn()) {
				Display.getDefault().asyncExec(BrowserLogin.this::okPressed);
			}
		} catch (SWTException | SWTError e) {
			if (e.getMessage().equals("Widget is disposed") || e.getMessage().contains("cookie access requires a Browser instance")) {
				return;
			}
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	private boolean wasAlreadyLoggedIn() {
		long currentTime = System.currentTimeMillis();
		long diff = currentTime - this.lastSuccessWasAlreadyLoggedIn;
		if (diff > MIN_TIME_TO_LOGIN_IN_MS) {
			return false;
		}
		this.lastSuccessWasAlreadyLoggedIn = currentTime;
		return true;
	}

	private boolean isWindows10orAbove() {
		String os = System.getProperty("os.name");
		if (os == null || !os.startsWith("Windows")) {
			return false;
		}
		return os.contains("Windows 10") || os.contains("Windows 11");
	}
}
