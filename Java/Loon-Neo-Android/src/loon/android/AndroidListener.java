package loon.android;

import android.view.KeyEvent;

public interface AndroidListener {

	void onPause();

	void onResume();

	void onExit();

	boolean onKeyDown(int keyCode, KeyEvent event);

	boolean onKeyUp(int keyCode, KeyEvent event);

}