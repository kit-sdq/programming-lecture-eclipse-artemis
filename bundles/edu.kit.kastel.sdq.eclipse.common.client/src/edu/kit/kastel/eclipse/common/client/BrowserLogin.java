/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.client;

import java.util.concurrent.Semaphore;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class BrowserLogin {

	private static final ILog log = Platform.getLog(BrowserLogin.class);

	private final String hostname;
	private String token;
	private Browser browser;

	private Semaphore waitForBrowser = new Semaphore(0);
	private Semaphore tokenWait = new Semaphore(0);

	public BrowserLogin(String hostname) {
		this.hostname = hostname;
	}

	private Browser createBrowser(String hostname) {
		Display display = new Display();

		Shell shell = new Shell(display, SWT.SHELL_TRIM & ~SWT.CLOSE);
		shell.setLayout(new GridLayout());

		final Browser browser = new Browser(shell, SWT.EDGE);
		browser.setJavascriptEnabled(true);
		browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		browser.setUrl(hostname);

		new BrowserFunction(browser, "tokenCallback") {
			@Override
			public Object function(Object[] objects) {
				if (objects == null || objects.length != 1 || !(objects[0]instanceof String token))
					return null;

				// Crop " at beginning and end
				BrowserLogin.this.token = token.substring(1, token.length() - 1);
				tokenWait.release();
				return null;
			}
		};

		return browser;
	}

	public String getToken() {
		if (this.token != null)
			return token;

		Thread daemon = new Thread(() -> swtThread());
		daemon.setDaemon(true);
		daemon.start();

		log.info("Opened Browser. Waiting for token");
		tokenWait.acquireUninterruptibly();
		log.info("Got Token");

		return this.token;
	}

	private void swtThread() {
		browser = createBrowser(hostname);

		Thread pollingDaemon = new Thread(() -> pollingThread(browser.getDisplay()));
		pollingDaemon.setDaemon(true);
		pollingDaemon.start();

		var shell = browser.getShell();
		var display = browser.getDisplay();

		shell.setSize(1024, 512);
		shell.open();

		waitForBrowser.release();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
			if (token != null)
				shell.close();
		}
		browser = null;
		display.dispose();
	}

	private void pollingThread(Display display) {
		waitForBrowser.acquireUninterruptibly();

		var localBrowser = browser;
		if (localBrowser == null)
			return;

		try {
			while (localBrowser == browser) {
				Thread.sleep(1000);
				display.asyncExec(() -> {
					try {
						if (localBrowser == browser)
							localBrowser.execute("tokenCallback(localStorage.getItem(\"jhi-authenticationtoken\"));");
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
				});
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

	}
}
