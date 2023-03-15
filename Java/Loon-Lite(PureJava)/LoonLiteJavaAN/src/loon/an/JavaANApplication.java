package loon.an;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import java.io.File;

import loon.LGame;
import loon.LSysException;
import loon.LSystem;
import loon.LazyLoading;
import loon.Platform;
import loon.an.window.JavaANAppSurfaceView;
import loon.canvas.LColor;
import loon.events.KeyMake;
import loon.events.SysInput;
import loon.geom.RectBox;
import loon.geom.RectI;
import loon.utils.MathUtils;

public abstract class JavaANApplication extends Activity implements JavaANPlatform {
    private String btnOKText = "OK";
    private String btnCancelText = "Cancel";

    private Handler handler;

    protected JavaANGame game;

    protected JavaANAppSurfaceView gameView;

    private JavaANScreenReceiver screenReceiver;

    protected FrameLayout frameLayout;

    protected JavaANSetting setting;

    protected int zoomWidth, zoomHeight;
    protected int maxDisplayWidth, maxDisplayHeight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_USER_PRESENT);

        this.screenReceiver = new JavaANScreenReceiver(this);
        this.registerReceiver(screenReceiver, filter);

        this.setting = new JavaANSetting();
        final LazyLoading.Data mainData = this.main(setting);

        if (setting.mainClass == null) {
            setting.mainClass = getClass();
        }

        JavaANMode mode = setting.showMode;
        if (mode == null) {
            mode = JavaANMode.Fill;
        }

        if (setting != null && setting.fullscreen) {
            this.setFullScreen(setting.fullscreen);
        } else {
            int windowFlags = makeWindowFlags();
            getWindow().setFlags(windowFlags, windowFlags);
        }

        this.handler = new Handler();

        int width = setting.width;
        int height = setting.height;

        // 是否按比例缩放屏幕
        if (setting.useRatioScaleFactor) {
            float scale = scaleFactor();
            width *= scale;
            height *= scale;
            setting.width_zoom = width;
            setting.height_zoom = height;
            setting.updateScale();
            mode = JavaANMode.MaxRatio;
            // 若缩放值为无法实现的数值，则默认操作
        } else if (setting.width_zoom <= 0 || setting.height_zoom <= 0) {
            updateViewSize(setting.landscape(), setting.width, setting.height, mode);
            width = this.maxDisplayWidth;
            height = this.maxDisplayHeight;
            setting.width_zoom = this.maxDisplayWidth;
            setting.height_zoom = this.maxDisplayHeight;
            setting.updateScale();
            mode = JavaANMode.Fill;
        } else {
            this.maxDisplayWidth = setting.width;
            this.maxDisplayHeight = setting.height;
            this.zoomWidth = setting.width_zoom;
            this.zoomHeight = setting.height_zoom;
            updateViewSizeData(mode);
            setting.updateScale();
        }

        this.game = new JavaANGame(this, setting);

        if (mainData != null) {
            try {
                game.register(mainData.onScreen());
            } catch (Exception e) {
                e.printStackTrace();
                throw new LSysException(e.getMessage());
            }
        }

        this.gameView = new JavaANAppSurfaceView(getApplicationContext(), game);

        setContentView(mode, gameView, width, height);
        setRequestedOrientation(configOrientation());
        createWakeLock(setting.useWakelock);
        hideStatusBar(setting.hideStatusBar);

        final boolean hideButtons = setting.useImmersiveMode || setting.fullscreen;
        setImmersiveMode(hideButtons);
        if (hideButtons && JavaANGame.getSDKVersion() >= 19) {
            try {
                Class<?> vlistener = Class.forName("loon.an.JavaANVisibilityListener");
                Object o = vlistener.newInstance();
                java.lang.reflect.Method method = vlistener.getDeclaredMethod("createListener", JavaANPlatform.class);
                method.invoke(o, this);
            } catch (Exception e) {
            }
        }
        if (setting.checkConfig) {
            try {
                final int REQUIRED_CONFIG_CHANGES = android.content.pm.ActivityInfo.CONFIG_ORIENTATION
                        | android.content.pm.ActivityInfo.CONFIG_KEYBOARD_HIDDEN;
                android.content.pm.ActivityInfo info = this.getPackageManager()
                        .getActivityInfo(new android.content.ComponentName(getApplicationContext(), this.getClass()), 0);
                if ((info.configChanges & REQUIRED_CONFIG_CHANGES) != REQUIRED_CONFIG_CHANGES) {
                    new android.app.AlertDialog.Builder(this).setMessage(
                                    "Loon Tip : Please add the following line to the Activity manifest .\n[configChanges=\"keyboardHidden|orientation\"]")
                            .show();
                }
            } catch (Exception e) {
                Log.w(setting.appName, "Cannot access game AndroidManifest.xml file !");
            }
        }
    }

    public JavaANAppSurfaceView gameView() {
        return gameView;
    }

    protected void createWakeLock(boolean use) {
        if (use) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    protected void hideStatusBar(boolean hide) {
        if (!hide || JavaANGame.getSDKVersion() < 11) {
            return;
        }
        getWindow().getDecorView().setSystemUiVisibility(0x1);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        Log.w(setting.appName, "onWindowFocusChanged(" + hasFocus + ")");
        super.onWindowFocusChanged(hasFocus);
        if (setting != null) {
            setImmersiveMode(setting.useImmersiveMode || setting.fullscreen);
            hideStatusBar(setting.hideStatusBar);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (setting != null && setting.isBackDestroy) {
            LSystem.exit();
            return super.onKeyDown(keyCode, event);
        }
        if (setting != null && setting.lockBackDestroy && keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        boolean result = super.onKeyDown(keyCode, event);
        if (setting != null && setting.listener != null) {
            return setting.listener.onKeyDown(keyCode, event);
        }
        return result;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (setting != null && setting.lockBackDestroy && keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        boolean result = super.onKeyUp(keyCode, event);
        if (setting != null && setting.listener != null) {
            return setting.listener.onKeyUp(keyCode, event);
        }
        return result;
    }

    @Override
    public Handler getHandler() {
        return handler;
    }

    @Override
    public Window getApplicationWindow() {
        return this.getWindow();
    }

    protected void syncPlatform() {
        if (game != null) {
            game.mainPlatform = this;
            game.setPlatform(this);
        }
    }

    @Override
    protected void onDestroy() {
        syncPlatform();
        Log.w(setting.appName, "Call onDestroy");
        if (screenReceiver != null) {
            unregisterReceiver(screenReceiver);
            screenReceiver.screenLocked = false;
        }
        if (setting != null && setting.listener != null) {
            setting.listener.onExit();
        }
        for (File file : getDir().listFiles()) {
            file.delete();
        }
        if (gameView != null) {
            gameView.close();
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        syncPlatform();
        Log.w(setting.appName, "Call onPause");
        if (setting != null && setting.listener != null) {
            setting.listener.onPause();
        }
        if (gameView != null) {
            gameView.onPause();
        }
        if (game != null) {
            game.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        syncPlatform();
        Log.w(setting.appName, "Call onResume");
        if (setting != null && setting.listener != null) {
            setting.listener.onResume();
        }
        if (game != null) {
            game.onResume();
        }
        if (gameView != null) {
            gameView.onResume();
        }
        super.onResume();
    }

    public Object getSystemService(String name) {
        return super.getSystemService(name);
    }

    public File getDir() {
        return super.getCacheDir();
    }

    public AssetManager getResAssets() {
        return super.getResources().getAssets();
    }

    public void runOnUI(Runnable runnable) {
        runOnUiThread(runnable);
    }

    @Override
    public SharedPreferences getSharedPreferences(String name) {
        return getSharedPreferences(name, 0);
    }

    @Override
    public Context getContext() {
        return super.getApplicationContext();
    }

    protected void updateViewSize(final boolean landscape, int width, int height, JavaANMode mode) {

        RectBox d = getScreenDimension();

        this.maxDisplayWidth = MathUtils.max(d.width(), 1);
        this.maxDisplayHeight = MathUtils.max(d.height(), 1);

        if (landscape && (d.getWidth() > d.getHeight())) {
            maxDisplayWidth = d.width();
            maxDisplayHeight = d.height();
        } else if (landscape && (d.getWidth() < d.getHeight())) {
            maxDisplayHeight = d.width();
            maxDisplayWidth = d.height();
        } else if (!landscape && (d.getWidth() < d.getHeight())) {
            maxDisplayWidth = d.width();
            maxDisplayHeight = d.height();
        } else if (!landscape && (d.getWidth() > d.getHeight())) {
            maxDisplayHeight = d.width();
            maxDisplayWidth = d.height();
        }

        if (mode != JavaANMode.Max) {
            if (landscape) {
                this.zoomWidth = width;
                this.zoomHeight = height;
            } else {
                this.zoomWidth = height;
                this.zoomHeight = width;
            }
        } else {
            if (landscape) {
                this.zoomWidth = maxDisplayWidth >= width ? width : maxDisplayWidth;
                this.zoomHeight = maxDisplayHeight >= height ? height : maxDisplayHeight;
            } else {
                this.zoomWidth = maxDisplayWidth >= height ? height : maxDisplayWidth;
                this.zoomHeight = maxDisplayHeight >= width ? width : maxDisplayHeight;
            }
        }

        if (mode == JavaANMode.Fill) {

            LSystem.setScaleWidth(((float) maxDisplayWidth) / zoomWidth);
            LSystem.setScaleHeight(((float) maxDisplayHeight) / zoomHeight);

        } else if (mode == JavaANMode.FitFill) {

            RectBox res = JavaANGraphicsUtils.fitLimitSize(zoomWidth, zoomHeight, maxDisplayWidth, maxDisplayHeight);
            maxDisplayWidth = res.width;
            maxDisplayHeight = res.height;
            LSystem.setScaleWidth(((float) maxDisplayWidth) / zoomWidth);
            LSystem.setScaleHeight(((float) maxDisplayHeight) / zoomHeight);

        } else if (mode == JavaANMode.Ratio) {

            maxDisplayWidth = View.MeasureSpec.getSize(maxDisplayWidth);
            maxDisplayHeight = View.MeasureSpec.getSize(maxDisplayHeight);

            float userAspect = (float) zoomWidth / (float) zoomHeight;
            float realAspect = (float) maxDisplayWidth / (float) maxDisplayHeight;

            if (realAspect < userAspect) {
                maxDisplayHeight = Math.round(maxDisplayWidth / userAspect);
            } else {
                maxDisplayWidth = Math.round(maxDisplayHeight * userAspect);
            }

            LSystem.setScaleWidth(((float) maxDisplayWidth) / zoomWidth);
            LSystem.setScaleHeight(((float) maxDisplayHeight) / zoomHeight);

        } else if (mode == JavaANMode.MaxRatio) {

            maxDisplayWidth = View.MeasureSpec.getSize(maxDisplayWidth);
            maxDisplayHeight = View.MeasureSpec.getSize(maxDisplayHeight);

            float userAspect = (float) zoomWidth / (float) zoomHeight;
            float realAspect = (float) maxDisplayWidth / (float) maxDisplayHeight;

            if ((realAspect < 1 && userAspect > 1) || (realAspect > 1 && userAspect < 1)) {
                userAspect = (float) zoomHeight / (float) zoomWidth;
            }

            if (realAspect < userAspect) {
                maxDisplayHeight = Math.round(maxDisplayWidth / userAspect);
            } else {
                maxDisplayWidth = Math.round(maxDisplayHeight * userAspect);
            }

            LSystem.setScaleWidth(((float) maxDisplayWidth) / zoomWidth);
            LSystem.setScaleHeight(((float) maxDisplayHeight) / zoomHeight);

        } else {

            LSystem.setScaleWidth(1f);
            LSystem.setScaleHeight(1f);

        }
        updateViewSizeData(mode);
    }

    private void updateViewSizeData(JavaANMode mode) {
        if (zoomWidth <= 0) {
            zoomWidth = maxDisplayWidth;
        }
        if (zoomHeight <= 0) {
            zoomHeight = maxDisplayHeight;
        }
        LSystem.setScaleWidth(((float) maxDisplayWidth) / zoomWidth);
        LSystem.setScaleHeight(((float) maxDisplayHeight) / zoomHeight);
        LSystem.viewSize.setSize(zoomWidth, zoomHeight);

        StringBuffer sbr = new StringBuffer();
        sbr.append("Mode:").append(mode);
        sbr.append("\nWidth:").append(zoomWidth).append(",Height:" + zoomHeight);
        sbr.append("\nmaxDisplayWidth:").append(maxDisplayWidth).append(",maxDisplayHeight:" + maxDisplayHeight);
        Log.d("JavaAN Update Size", sbr.toString());
    }


    public JavaANScreenReceiver getScreenReceiver() {
        return screenReceiver;
    }

    public RectBox getScreenDimension() {
        DisplayMetrics dm = getSysDisplayMetrices();
        return new RectBox(dm.xdpi, dm.ydpi, dm.widthPixels, dm.heightPixels);
    }

    protected float scaleFactor() {
        return getResources().getDisplayMetrics().density;
    }

    public void setFullScreen(boolean fullScreen) {
        Window win = getWindow();
        if (JavaANGame.isAndroidVersionHigher(11)) {
            int flagHardwareAccelerated = 0x1000000;
            win.setFlags(flagHardwareAccelerated, flagHardwareAccelerated);
        }

        if (fullScreen) {
            try {
                requestWindowFeature(Window.FEATURE_NO_TITLE);
            } catch (Exception ex) {
            }
            win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            win.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        } else {
            win.setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }
    }


    protected int makeWindowFlags() {
        return (WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
    }

    protected boolean useOrientation() {
        if (game == null) {
            return true;
        }
        return useOrientation(setting);
    }

    protected boolean useOrientation(JavaANSetting setting) {
        if (setting == null) {
            return true;
        }
        return setting.useOrientation;
    }

    protected int configOrientation() {
        if (game == null) {
            return 0;
        }

        boolean use = useOrientation(setting);

        int orientation = -1;

        if (use) {
            if (!JavaANGame.isAndroidVersionHigher(23)) {
                orientation = this.getRequestedOrientation();
            } else {
                try {
                    orientation = this.getResources().getConfiguration().orientation;
                } catch (Throwable cause) {
                }
                if (orientation == ActivityInfo.SCREEN_ORIENTATION_USER) {
                    orientation = this.getRequestedOrientation();
                }
            }
            if (setting.orientation != -1) {
                orientation = setting.orientation;
            }

        }
        if (orientation <= -1 || !use) {
            if (setting.landscape()) {
                orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            } else {
                orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            }
        } else {
            if (setting.landscape() && orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            } else if (!setting.landscape() && orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            }
        }
        return orientation;
    }


    private void setContentView(JavaANMode mode, JavaANAppSurfaceView view, int w, int h) {
        this.frameLayout = new FrameLayout(this);
        this.frameLayout.setBackgroundColor(LColor.black.getRGB());
        if (mode == JavaANMode.Defalut) {
            // 添加游戏View，显示为指定大小，并居中
            this.addView(view, view.getWidth(), view.getHeight(), JavaANLocation.CENTER);
        } else if (mode == JavaANMode.Ratio) {
            // 添加游戏View，显示为屏幕许可范围，并居中
            this.addView(view, w, h, JavaANLocation.CENTER);
        } else if (mode == JavaANMode.MaxRatio) {
            // 添加游戏View，显示为屏幕许可的最大范围(可能比单纯的Ratio失真)，并居中
            this.addView(view, w, h, JavaANLocation.CENTER);
        } else if (mode == JavaANMode.Max) {
            // 添加游戏View，显示为最大范围值，并居中
            this.addView(view, w, h, JavaANLocation.CENTER);
        } else if (mode == JavaANMode.Fill) {
            // 添加游戏View，显示为全屏，并居中
            this.addView(view, android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT, JavaANLocation.CENTER);
        } else if (mode == JavaANMode.FitFill) {
            // 添加游戏View，显示为按比例缩放情况下的最大值，并居中
            this.addView(view, w, h, JavaANLocation.CENTER);
        }
        getWindow().setContentView(frameLayout);
    }

    public View inflate(final int layoutID) {
        final android.view.LayoutInflater inflater = android.view.LayoutInflater.from(this);
        return inflater.inflate(layoutID, null);
    }

    /**
     * 生成一个对应指定位置的RelativeLayout
     *
     * @param location
     * @return
     */
    public static RelativeLayout.LayoutParams createRelativeLayout(JavaANLocation location) {
        return createRelativeLayout(location, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    }

    /**
     * 生成一个对应指定位置的RelativeLayout
     *
     * @param location
     * @return
     */
    public static RelativeLayout.LayoutParams createRelativeLayout(JavaANLocation location, int w, int h) {
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(w, h);
        if (location == JavaANLocation.LEFT) {
            relativeParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            relativeParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        } else if (location == JavaANLocation.RIGHT) {
            relativeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            relativeParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        } else if (location == JavaANLocation.TOP) {
            relativeParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        } else if (location == JavaANLocation.BOTTOM) {
            relativeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        } else if (location == JavaANLocation.BOTTOM_LEFT) {
            relativeParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            relativeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        } else if (location == JavaANLocation.BOTTOM_RIGHT) {
            relativeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            relativeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        } else if (location == JavaANLocation.CENTER) {
            relativeParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
            relativeParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        } else if (location == JavaANLocation.ALIGN_BASELINE) {
            relativeParams.addRule(RelativeLayout.ALIGN_BASELINE, RelativeLayout.TRUE);
        } else if (location == JavaANLocation.ALIGN_LEFT) {
            relativeParams.addRule(RelativeLayout.ALIGN_LEFT, RelativeLayout.TRUE);
        } else if (location == JavaANLocation.ALIGN_TOP) {
            relativeParams.addRule(RelativeLayout.ALIGN_TOP, RelativeLayout.TRUE);
        } else if (location == JavaANLocation.ALIGN_RIGHT) {
            relativeParams.addRule(RelativeLayout.ALIGN_RIGHT, RelativeLayout.TRUE);
        } else if (location == JavaANLocation.ALIGN_BOTTOM) {
            relativeParams.addRule(RelativeLayout.ALIGN_BOTTOM, RelativeLayout.TRUE);
        } else if (location == JavaANLocation.ALIGN_PARENT_LEFT) {
            relativeParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        } else if (location == JavaANLocation.ALIGN_PARENT_TOP) {
            relativeParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        } else if (location == JavaANLocation.ALIGN_PARENT_RIGHT) {
            relativeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        } else if (location == JavaANLocation.ALIGN_PARENT_BOTTOM) {
            relativeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        } else if (location == JavaANLocation.CENTER_IN_PARENT) {
            relativeParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        } else if (location == JavaANLocation.CENTER_HORIZONTAL) {
            relativeParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        } else if (location == JavaANLocation.CENTER_VERTICAL) {
            relativeParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        }
        return relativeParams;
    }

    protected FrameLayout.LayoutParams createLayoutParams() {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;
        return layoutParams;
    }

    public void addView(final View view, JavaANLocation location) {
        if (view == null) {
            return;
        }
        addView(view, android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT, location);
    }

    public void addView(final View view, int w, int h, JavaANLocation location) {
        if (view == null) {
            return;
        }
        android.widget.RelativeLayout viewLayout = new android.widget.RelativeLayout(this);
        android.widget.RelativeLayout.LayoutParams relativeParams = createRelativeLayout(location, w, h);
        viewLayout.addView(view, relativeParams);
        addView(viewLayout);
    }

    public void addView(final View view) {
        if (view == null) {
            return;
        }
        frameLayout.addView(view, createLayoutParams());
        try {
            if (view.getVisibility() != View.VISIBLE) {
                view.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
        }
    }

    public void removeView(final View view) {
        if (view == null) {
            return;
        }
        frameLayout.removeView(view);
        try {
            if (view.getVisibility() != View.GONE) {
                view.setVisibility(View.GONE);
            }
        } catch (Exception e) {
        }
    }

    public void setImmersiveMode(boolean use) {
        if (use && JavaANGame.getSDKVersion() > 11 && JavaANGame.getSDKVersion() < 19) {
            try {
                View view = getWindow().getDecorView();
                view.setSystemUiVisibility(View.GONE);
            } catch (Exception e) {
            }
            return;
        }
        if (!use || JavaANGame.getSDKVersion() < 19) {
            return;
        }
        try {
            View view = getWindow().getDecorView();
            int code = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            view.setSystemUiVisibility(code);
        } catch (Exception e) {
        }
    }

    public FrameLayout getLayout() {
        return frameLayout;
    }

    /**
     * 获得状态栏的高度
     *
     * @return
     */
    public int getStatusHeight() {
        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
            statusHeight = this.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
        }
        return statusHeight;
    }

    protected DisplayMetrics getSysDisplayMetrices() {
        DisplayMetrics dm = new DisplayMetrics();
        try {
            getWindowManager().getDefaultDisplay().getMetrics(dm);
        } catch (Throwable cause) {
            cause.printStackTrace();
        }
        return dm;
    }

    @Override
    public int getContainerWidth() {
        DisplayMetrics dm = getSysDisplayMetrices();
        return dm.widthPixels;
    }

    @Override
    public int getContainerHeight() {
        DisplayMetrics dm = getSysDisplayMetrices();
        return dm.heightPixels;
    }

    @Override
    public Orientation getOrientation() {
        return null;
    }

    @Override
    public LGame getGame() {
        return null;
    }

    public String getBtnOKText() {
        return btnOKText;
    }

    public JavaANApplication setBtnOKText(String btnOKText) {
        this.btnOKText = btnOKText;
        return this;
    }

    public String getBtnCancelText() {
        return btnCancelText;
    }

    public JavaANApplication setBtnCancelText(String btnCancelText) {
        this.btnCancelText = btnCancelText;
        return this;
    }

    public JavaANApplication setBtn(String ok, String cancel) {
        setBtnOKText(ok);
        setBtnCancelText(cancel);
        return this;
    }

    @Override
    public void sysText(SysInput.TextEvent event, KeyMake.TextType textType, String label, String initialValue) {
        if (game == null) {
            event.cancel();
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final AlertDialog.Builder alert = new AlertDialog.Builder(JavaANApplication.this);
                alert.setMessage(label);
                final EditText input = new EditText(JavaANApplication.this);
                final int inputType;
                switch (textType) {
                    case NUMBER:
                        inputType = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED;
                        break;
                    case EMAIL:
                        inputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
                        break;
                    case URL:
                        inputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI;
                        break;
                    case DEFAULT:
                    default:
                        inputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL;
                        break;
                }
                input.setInputType(inputType);
                input.setText(initialValue);
                alert.setView(input);

                alert.setPositiveButton(btnOKText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        event.input(input.getText().toString());
                    }
                });

                alert.setNegativeButton(btnCancelText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        event.cancel();
                    }
                });
                alert.show();
            }
        });
    }

    @Override
    public void sysDialog(SysInput.ClickEvent event, String title, String text, String ok, String cancel) {
        if (game == null) {
            event.cancel();
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder alert = new AlertDialog.Builder(JavaANApplication.this).setTitle(title).setMessage(text);
                alert.setPositiveButton(ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        event.clicked();
                    }
                });
                if (cancel != null) {
                    alert.setNegativeButton(cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                            event.cancel();
                        }
                    });
                }
                alert.show();
            }
        });
    }

    public void showSystemButtonUI() {
        if (JavaANGame.getSDKVersion() > 11 && JavaANGame.getSDKVersion() < 19) {
            try {
                View view = getWindow().getDecorView();
                view.setSystemUiVisibility(View.VISIBLE);
            } catch (Exception e) {
            }
            return;
        }
        if (JavaANGame.getSDKVersion() < 19) {
            return;
        }
        try {
            View view = getWindow().getDecorView();
            int code = View.SYSTEM_UI_FLAG_FULLSCREEN;
            view.setSystemUiVisibility(code);
        } catch (Exception e) {
        }
    }

    public void hideSystemButtonUI() {
        if (JavaANGame.getSDKVersion() > 11 && JavaANGame.getSDKVersion() < 19) {
            try {
                View view = getWindow().getDecorView();
                view.setSystemUiVisibility(View.GONE);
            } catch (Exception e) {
            }
            return;
        }
        if (JavaANGame.getSDKVersion() < 19) {
            return;
        }
        try {
            View view = getWindow().getDecorView();
            int code = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            view.setSystemUiVisibility(code);
        } catch (Exception e) {
        }
    }

    /**
     * 获取当前屏幕截图，包含状态栏
     *
     * @return
     */
    public Bitmap snapShotWithStatusBar() {
        return snap();
    }

    /**
     * 获取当前屏幕截图，不包含状态栏
     *
     * @return
     */
    public Bitmap snapShotWithoutStatusBar() {
        final View view = getWindow().getDecorView();
        final int width = view.getWidth();
        final int height = view.getHeight();
        Bitmap bmp = snap(view);
        Rect frame = new Rect();
        view.getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height - statusBarHeight);
        bmp.recycle();
        bmp = null;
        return bp;
    }

    public Bitmap snap(int width, int height) {
        View view = getWindow().getDecorView();
        return getBitmapFromView(view, width, height);
    }

    public Bitmap snap() {
        return snap(getWindow().getDecorView());
    }

    public Bitmap snap(View view) {
        return getBitmapFromView(view, view.getWidth(), view.getHeight());
    }

    public static Bitmap getBitmapFromView(View view, int width, int height) {
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        android.view.ViewGroup.LayoutParams layouts = view.getLayoutParams();
        view.layout(0, 0, layouts.width, layouts.height);
        view.draw(canvas);
        return bmp;
    }

    public RectI getDeviceScreenSize(boolean useDeviceSize) {
        return getDeviceScreenSize(this, useDeviceSize);
    }

    public static RectI getDeviceScreenSize(Context context, boolean useDeviceSize) {
        RectI rect = new RectI();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        android.view.Display display = windowManager.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;
        if (!useDeviceSize) {
            rect.width = widthPixels;
            rect.height = heightPixels;
            return rect;
        }
        int buildInt = JavaANGame.getSDKVersion();
        if (buildInt >= 14 && buildInt < 17)
            try {
                widthPixels = (Integer) android.view.Display.class.getMethod("getRawWidth").invoke(display);
                heightPixels = (Integer) android.view.Display.class.getMethod("getRawHeight").invoke(display);
            } catch (Exception ignored) {
            }
        if (buildInt >= 17)
            try {
                android.graphics.Point realSize = new android.graphics.Point();
                android.view.Display.class.getMethod("getRealSize", android.graphics.Point.class).invoke(display,
                        realSize);
                widthPixels = realSize.x;
                heightPixels = realSize.y;
            } catch (Exception ignored) {
            }
        rect.width = widthPixels;
        rect.height = heightPixels;
        return rect;
    }

    @Override
    public void close() {
        if (game != null && game.setting.isCloseOnAppExit) {
            if (gameView != null) {
                gameView.close();
            }
        }
    }
}
