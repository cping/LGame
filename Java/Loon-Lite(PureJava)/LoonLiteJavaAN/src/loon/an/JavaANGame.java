package loon.an;

import android.graphics.Bitmap;
import android.os.Build;

import loon.LGame;
import loon.LSetting;

import loon.canvas.Canvas;
import loon.canvas.Image;


public class JavaANGame extends LGame {

    final private static String BULID_BRAND, BULID_MODEL, BULIDM_PRODUCT, BULIDM_RELEASE, BULIDM_DEVICE;

    private static int BULIDM_SDK;

    public static boolean USE_BITMAP_MEMORY_HACK;

    private static boolean EMULATOR;

    public static final boolean DEBUG_LOGS = false;

    public final static int ICE_CREAM_SANDWICH = 14;

    static {
        BULID_BRAND = Build.BRAND.toLowerCase();
        BULID_MODEL = Build.MODEL.toLowerCase();
        BULIDM_PRODUCT = Build.PRODUCT.toLowerCase();
        BULIDM_RELEASE = Build.VERSION.RELEASE;
        try {
            BULIDM_SDK = Build.VERSION.SDK_INT;
        } catch (Throwable cause) {
            try {
                BULIDM_SDK = Build.VERSION.class.getDeclaredField("SDK_INT").getInt(null);
            } catch (Throwable ex) {
                try {
                    BULIDM_SDK = Integer
                            .parseInt(String.valueOf(Build.VERSION.class.getDeclaredField("SDK").get(null)));
                } catch (Throwable e) {
                    BULIDM_SDK = 15;
                }
            }
        }
        BULIDM_DEVICE = Build.DEVICE;
        EMULATOR = BULID_BRAND.indexOf("generic") != -1 && BULID_MODEL.indexOf("sdk") != -1;
        USE_BITMAP_MEMORY_HACK = BULIDM_SDK < ICE_CREAM_SANDWICH;
    }

    protected final long start = System.nanoTime();
    protected JavaANPlatform mainPlatform;

    protected final JavaANAccelerometer accelerometer;

    protected final JavaANSave save;
    protected final JavaANGraphics graphics;
    protected final JavaANAssets assets;
    protected final JavaANLog log;
    protected final JavaANAsyn asyn;
    protected final JavaANInputMake input;
    protected final JavaANClipboard clipboard;

    private boolean active = true;

    public JavaANGame(JavaANPlatform plat, LSetting config) {
        super(config, plat);
        this.mainPlatform = plat;
        this.log = new JavaANLog(config.appName);
        this.asyn = new JavaANAsyn(log, frame, this);
        this.graphics = new JavaANGraphics(this, true);
        this.input = new JavaANInputMake(this);
        this.assets = new JavaANAssets(this);
        this.save = new JavaANSave(this);
        this.accelerometer = new JavaANAccelerometer(this);
        this.clipboard = new JavaANClipboard(this);
        this.initProcess();
    }

    protected void toggleActivation() {
        active = !active;
    }

    protected void start() {
        active = true;
    }

    @Override
    public LGame resume() {
        super.resume();
        active = true;
        return this;
    }

    @Override
    public LGame pause() {
        super.pause();
        active = false;
        return this;
    }

    @Override
    public void stop() {
        super.stop();
        active = false;
    }

    public void process(boolean wasActive) {
        if (wasActive != active) {
            status.emit(wasActive ? Status.PAUSE : Status.RESUME);
        }
        if (active) {
            emitFrame();
        }
    }

    public boolean isActive() {
        return true;
    }

    public static boolean checkAndroid() {
        String jvm = getProperty("java.runtime.name").toLowerCase();
        if (jvm.indexOf("android runtime") != -1) {
            return true;
        }
        try {
            Class.forName("android.Manifest");
            return true;
        } catch (Throwable cause) {
        }
        return false;
    }

    protected static String getProperty(String value) {
        return getProperty(value, "");
    }

    protected static String getProperty(String value, String def) {
        String result = null;
        try {
            result = System.getProperty(value, def).trim();
        } catch (Throwable cause) {
            result = "";
        }
        return result;
    }

    @Override
    public Image snapshot() {
        Bitmap image = null;
        if (mainPlatform != null) {
            image = mainPlatform.snap();
        }
        return new JavaANImage(graphics, image);

    }

    public void onPause() {
        status.emit(Status.PAUSE);
    }

    public void onResume() {
        status.emit(Status.RESUME);
    }

    public void onExit() {
        status.emit(Status.EXIT);
    }

    public JavaANPlatform getMainPlatform() {
        return mainPlatform;
    }

    public JavaANCanvas getCanvas() {
        return graphics.getCanvas();
    }

    @Override
    public Environment env() {
        return Environment.ANDROID;
    }

    @Override
    public double time() {
        return System.currentTimeMillis();
    }

    @Override
    public int tick() {
        return (int) ((System.nanoTime() - start) / 1000000L);
    }
    @Override
    public JavaANMesh makeMesh(Canvas canvas) {
        return new JavaANMesh(canvas);
    }
    @Override
    public JavaANAssets assets() {
        return this.assets;
    }

    @Override
    public JavaANAsyn asyn() {
        return this.asyn;
    }

    @Override
    public JavaANGraphics graphics() {
        return this.graphics;
    }

    @Override
    public JavaANInputMake input() {
        return this.input;
    }

    @Override
    public JavaANClipboard clipboard() {
        return this.clipboard;
    }

    @Override
    public JavaANLog log() {
        return this.log;
    }

    @Override
    public JavaANSave save() {
        return this.save;
    }

    @Override
    public JavaANAccelerometer accel() {
        return this.accelerometer;
    }

    @Override
    public Sys getPlatform() {
        return Sys.ANDROID;
    }

    @Override
    public boolean isMobile() {
        return true;
    }

    @Override
    public boolean isDesktop() {
        return false;
    }

    @Override
    public boolean isBrowser() {
        return false;
    }

    public static boolean isDevice(String d) {
        return BULIDM_DEVICE.equalsIgnoreCase(d);
    }

    /**
     * 判定当前Android系统版本是否高于指定的版本
     *
     * @param ver
     * @return
     */
    public static boolean isAndroidVersionHigher(final int ver) {
        return BULIDM_SDK >= ver;
    }

    public static String getModel() {
        return BULID_MODEL;
    }

    public static String getProductName() {
        return BULIDM_PRODUCT;
    }

    public static String getOSVersion() {
        return BULIDM_RELEASE;
    }

    public static int getSDKVersion() {
        return BULIDM_SDK;
    }

    public static String getBRANDName() {
        return BULID_BRAND;
    }

    public static boolean isEmulator() {
        return EMULATOR;
    }

    public static boolean isHTC() {
        return BULID_BRAND.indexOf("htc") != -1;
    }

    public static boolean isSamsung() {
        final boolean isSamsung = BULID_BRAND.indexOf("samsung") != -1;
        return isSamsung;
    }

    public static boolean isSamsungGalaxy() {
        final boolean isGalaxy = BULID_MODEL.indexOf("galaxy") != -1;
        return isSamsung() && isGalaxy;
    }

    public static boolean isDroidOrMilestone() {
        final boolean isMotorola = BULID_BRAND.indexOf("moto") != -1;
        final boolean isDroid = BULID_MODEL.indexOf("droid") != -1;
        final boolean isMilestone = BULID_MODEL.indexOf("milestone") != -1;
        return isMotorola && (isDroid || isMilestone);
    }

    public static boolean isHuawei() {
        return BULID_BRAND.indexOf("emui") != -1;
    }

    public static boolean isXiaomi() {
        return BULID_BRAND.indexOf("miui") != -1;
    }
}
