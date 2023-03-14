package loon.an;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.Window;

import java.io.File;

import loon.LazyLoading;
import loon.Platform;

public interface JavaANPlatform extends Platform {

    public LazyLoading.Data main(JavaANSetting setting);

    void setImmersiveMode(boolean b);

    Handler getHandler();

    Window getApplicationWindow();

    public Object getSystemService(String name);

    public File getDir();

    public AssetManager getResAssets();

    public void runOnUI(Runnable runnable);

    public Bitmap snap();

    public SharedPreferences getSharedPreferences(String name);

    public Context getContext();

}
