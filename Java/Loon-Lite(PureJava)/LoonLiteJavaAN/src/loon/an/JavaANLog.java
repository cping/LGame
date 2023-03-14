package loon.an;

import android.util.Log;

public class JavaANLog extends loon.Log {
    private final String logMes;

    public JavaANLog(String log) {
        this.logMes = log;
    }
    @Override
    protected void callNativeLog(final Level level,final String msg,final Throwable e) {
        if (e == null) {
            if (level.id == Level.ALL.id || level.id <= Level.DEBUG.id) {
                Log.d(logMes + "-" + level, msg);
            } else if (level.id == Level.ALL.id || level.id <= Level.WARN.id) {
                Log.w(logMes + "-" + level, msg);
            } else if (level.id == Level.ALL.id || level.id <= Level.ERROR.id) {
                Log.e(logMes + "-" + level, msg);
            } else {
                Log.i(logMes + "-" + level, msg);
            }
        } else {
            if (level.id == Level.ALL.id || level.id <= Level.DEBUG.id) {
                Log.d(logMes + "-" + level, msg, e);
            } else if (level.id == Level.ALL.id || level.id <= Level.WARN.id) {
                Log.w(logMes + "-" + level, msg, e);
            } else if (level.id == Level.ALL.id || level.id <= Level.ERROR.id) {
                Log.e(logMes + "-" + level, msg, e);
            } else {
                Log.i(logMes + "-" + level, msg, e);
            }
            if (e != null) {
                e.printStackTrace(System.out);
            }
        }
    }

    @Override
    public void onError(Throwable e) {
    }
}
