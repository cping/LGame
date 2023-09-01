package loon.an;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class JavaANScreenReceiver extends BroadcastReceiver {

    protected boolean screenLocked;

    protected JavaANApplication app;

    public JavaANScreenReceiver(JavaANApplication a){
        this.app = a;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == Intent.ACTION_SCREEN_OFF) {
            onLocked();
        } else if (intent.getAction() == Intent.ACTION_SCREEN_ON) {
            android.app.KeyguardManager keyguard = (android.app.KeyguardManager) context
                    .getSystemService(Context.KEYGUARD_SERVICE);
            if (!keyguard.isKeyguardLocked()) {
                onUnlocked();
            }
        } else if (intent.getAction() == Intent.ACTION_USER_PRESENT) {
            onUnlocked();
        }
    }

    private void onLocked() {
        screenLocked = true;
    }

    private void onUnlocked() {
        screenLocked = false;
        if (app != null) {
            app.game.pause();
        }
    }

    public boolean isScreenLocked() {
        return screenLocked;
    }

}
