package loon.html5.gwt;

import com.google.gwt.user.client.ui.Panel;

import loon.html5.gwt.preloader.Preloader.PreloaderCallback;

public interface GWTProgress {

	 PreloaderCallback getPreloaderCallback(Loon loon, Panel root);
	
}
