package edu.kit.kastel.eclipse.grading.view.listeners;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;

public class KeyboardAwareMouseListener implements MouseListener {

	private Runnable onShiftClick;
	private Runnable onCtrlClick;
	private Runnable onAltClick;
	private Runnable onLeftClick;
	private Runnable onMiddleClick;
	private Runnable onRightClick;
	private Runnable onClick;
	
	
	@Override
	public void mouseUp(MouseEvent e) {
		if ((e.stateMask & SWT.SHIFT) == SWT.SHIFT) {
			this.invokeClickHandler(onShiftClick);
		}
		if ((e.stateMask & SWT.CTRL) == SWT.CTRL) {
			this.invokeClickHandler(onCtrlClick);
		}
		if ((e.stateMask & SWT.ALT) == SWT.ALT) {
			this.invokeClickHandler(onAltClick);
		}
		if (e.stateMask == SWT.BUTTON1) {	// Special handling, hence SWT.BUTTON1 is always set iff mouse is pressed
			this.invokeClickHandler(onLeftClick);
		}
		if ((e.stateMask & SWT.BUTTON2) == SWT.BUTTON2) {
			this.invokeClickHandler(onMiddleClick);
		}
		if ((e.stateMask & SWT.BUTTON3) == SWT.BUTTON3) {
			this.invokeClickHandler(onRightClick);
		}
		this.invokeClickHandler(onClick);
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
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		
		private final KeyboardAwareMouseListener listener;
		
		public Builder() {
			this.listener = new KeyboardAwareMouseListener();
		}
		
		public Builder onShiftClick(Runnable runnable) {
			this.listener.onShiftClick = runnable;
			return this;
		}
		
		public Builder onCtrlClick(Runnable runnable) {
			this.listener.onCtrlClick = runnable;
			return this;
		}
		
		public Builder onAltClick(Runnable runnable) {
			this.listener.onAltClick = runnable;
			return this;
		}
		
		public Builder onLeftClick(Runnable runnable) {
			this.listener.onLeftClick = runnable;
			return this;
		}
		
		public Builder onMiddleClick(Runnable runnable) {
			this.listener.onMiddleClick = runnable;
			return this;
		}
		
		public Builder onRightClick(Runnable runnable) {
			this.listener.onRightClick = runnable;
			return this;
		}
		
		public Builder onClick(Runnable runnable) {
			this.listener.onClick = runnable;
			return this;
		}
		
		public KeyboardAwareMouseListener build() {
			return this.listener;
		}
	}

}
