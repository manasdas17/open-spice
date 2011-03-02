package org.cloudqucs.client.Dialogs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;

public class PropsDialog extends DialogBox {

	private static final Binder binder = GWT.create(Binder.class);

	interface Binder extends UiBinder<Widget, PropsDialog> {
	}

	public PropsDialog() {
		setWidget(binder.createAndBindUi(this));
	}

}
