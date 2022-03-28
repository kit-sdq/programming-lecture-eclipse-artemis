/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.grading.view.listeners;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;

/**
 * This class provides a {@link MouseListener} which supports different handlers
 * if specific keys on the keyboard are pressed while clicking. However, this
 * listener will only listen to the {@link MouseListener#mouseUp(MouseEvent)}
 * 
 * @author Shirkanesi
 */
public class KeyboardAwareMouseListener implements MouseListener {

	private final Map<Integer, Runnable> listeners;
	private Runnable onEveryClick;

	public KeyboardAwareMouseListener() {
		this.listeners = new HashMap<>();
	}

	@Override
	public void mouseUp(MouseEvent e) {
		this.invokeClickHandler(this.listeners.get(e.stateMask));
		this.invokeClickHandler(onEveryClick);
	}

	/**
	 * Sets the click-handler which will be called on every click this listeners
	 * handles. This handler will be invoked <strong>after</strong> all other
	 * handlers.
	 * 
	 * @param handler the handler to be set
	 */
	public void setClickHandlerForEveryClick(Runnable handler) {
		this.onEveryClick = handler;
	}

	/**
	 * Sets a click-handler for all the button-masks supplied as second parameter.
	 * Those masks are specified by the constant in {@link SWT} (e.g. SWT.SHIFT). By
	 * supplying multiple masks the handler will be bound to multiple events (those
	 * individual masks)
	 * 
	 * @param handler the handler to be set
	 * @param masks   the masks the handler should be invoked on.
	 */
	public void setClickHandler(Runnable handler, int... masks) {
		for (int mask : masks) {
			if ((mask & SWT.BUTTON2) == SWT.BUTTON2 || (mask & SWT.BUTTON3) == SWT.BUTTON3) {
				// the two other buttons won't fire the normal click
				this.listeners.put(mask, handler);
			} else {
				this.listeners.put(mask | SWT.BUTTON1, handler);
			}
		}
	}

	private void invokeClickHandler(Runnable handler) {
		if (handler != null) {
			handler.run();
		}
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {
		// NOP
	}

	@Override
	public void mouseDown(MouseEvent e) {
		// NOP
	}

}
