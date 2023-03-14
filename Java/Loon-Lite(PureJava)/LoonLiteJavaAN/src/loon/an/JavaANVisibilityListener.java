package loon.an;

import android.view.View;

import loon.LSystem;

public class JavaANVisibilityListener {

    public void createListener(final JavaANPlatform application) {
        try {
            View rootView = application.getApplicationWindow().getDecorView();
            rootView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int arg) {
                    application.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            application.setImmersiveMode(true);
                        }
                    });
                }
            });
        } catch (Throwable t) {
            LSystem.debug("JavaAN Application",
                    "Can't create OnSystemUiVisibilityChangeListener, unable to use immersive mode.", t);
        }
    }
}
