/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.view.utilities;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public final class UIUtilities {
	private UIUtilities() {
		throw new IllegalAccessError();
	}

	public static ScrolledComposite createTabWithScrolledComposite(TabFolder tabFolder, String title) {
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText(title);
		ScrolledComposite scrolledComposite = new ScrolledComposite(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tabItem.setControl(scrolledComposite);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		return scrolledComposite;
	}

	public static void initializeTabAfterFilling(Composite scrolledComposite, Composite content) {
		if (!(scrolledComposite instanceof ScrolledComposite casted)) {
			return;
		}
		casted.setContent(content);
		casted.setMinSize(content.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		content.layout();
	}
}
