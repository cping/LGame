package loon.an;

import android.view.KeyEvent;

public interface JavaANListener {

    void onCreated();

    void onPause();

    void onResume();

    void onExit();

    boolean onKeyDown(int keyCode, KeyEvent event);

    boolean onKeyUp(int keyCode, KeyEvent event);

}