package loon.live2d;

import loon.live2d.id.*;

public class Live2D
{
    private static final Boolean c;
    private static final Boolean d;
    public static final String __L2D_VERSION_STR__ = "2.0.05";
    public static final int __L2D_VERSION_NO__ = 200000000;
    public static boolean L2D_SAMPLE;
    public static boolean L2D_VERBOSE;
    public static boolean L2D_DEBUG_IMPORT;
    public static boolean L2D_DEBUG;
    public static boolean L2D_TEMPORARY_DEBUG;
    public static boolean L2D_RANGE_CHECK;
    public static boolean L2D_RANGE_CHECK_POINT;
    public static boolean L2D_DEFORMER_EXTEND;
    public static boolean L2D_FORCE_UPDATE;
    public static boolean L2D_INVERT_TEXTURE;
    public static boolean L2D_OUTSIDE_PARAM_AVAILABLE;
    public static int L2D_NO_ERROR;
    public static int L2D_ERROR_LIVE2D_INIT_FAILED;
    public static int L2D_ERROR_FILE_LOAD_FAILED;
    public static int L2D_ERROR_MEMORY_ERROR;
    public static int L2D_ERROR_MODEL_DATA_VERSION_MISMATCH;
    public static int L2D_ERROR_MODEL_DATA_EOF_ERROR;
    public static int L2D_ERROR_MODEL_DATA_UNKNOWN_FORMAT;
    public static int L2D_ERROR_DDTEXTURE_SETUP_TRANSFORM_FAILED;
    static boolean a;
    static int b;
    
    static {
        c = false;
        d = false;
        Live2D.L2D_SAMPLE = true;
        Live2D.L2D_VERBOSE = true;
        Live2D.L2D_DEBUG_IMPORT = false;
        Live2D.L2D_DEBUG = true;
        Live2D.L2D_TEMPORARY_DEBUG = true;
        Live2D.L2D_RANGE_CHECK = true;
        Live2D.L2D_RANGE_CHECK_POINT = true;
        Live2D.L2D_DEFORMER_EXTEND = true;
        Live2D.L2D_FORCE_UPDATE = false;
        Live2D.L2D_INVERT_TEXTURE = false;
        Live2D.L2D_OUTSIDE_PARAM_AVAILABLE = false;
        Live2D.L2D_NO_ERROR = 0;
        Live2D.L2D_ERROR_LIVE2D_INIT_FAILED = 1000;
        Live2D.L2D_ERROR_FILE_LOAD_FAILED = 1001;
        Live2D.L2D_ERROR_MEMORY_ERROR = 1100;
        Live2D.L2D_ERROR_MODEL_DATA_VERSION_MISMATCH = 2000;
        Live2D.L2D_ERROR_MODEL_DATA_EOF_ERROR = 2001;
        Live2D.L2D_ERROR_MODEL_DATA_UNKNOWN_FORMAT = 2002;
        Live2D.L2D_ERROR_DDTEXTURE_SETUP_TRANSFORM_FAILED = 4000;
        Live2D.a = true;
    }
    
    public static void init() {
        if (Live2D.a) {
            Live2D.a = false;
            if (Live2D.c) {}
            else if (Live2D.d) {}
            else {}
        }
    }
    
    public void dispose() {
        ID.releaseStored_notForClientCall();
    }
    
    public static String getVersionStr() {
        return "2.0.05";
    }
    
    public static int getVersionNo() {
        return 200000000;
    }
    
    public static void setError(final int errorNo) {
        Live2D.b = errorNo;
    }
    
    public static int getError() {
        final int b = Live2D.b;
        Live2D.b = 0;
        return b;
    }
}
