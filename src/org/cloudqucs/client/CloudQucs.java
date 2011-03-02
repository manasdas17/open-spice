package org.cloudqucs.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootLayoutPanel;

class CloudQucs implements EntryPoint{

	@Override
	public void onModuleLoad() {
		MainScreen outer = new MainScreen();
		RootLayoutPanel root = RootLayoutPanel.get();
	    root.add(outer);
		outer.setScrollBars();
	}
}
