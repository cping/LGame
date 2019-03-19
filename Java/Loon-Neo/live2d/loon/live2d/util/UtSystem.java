package loon.live2d.util;

import loon.utils.TimeUtils;

public class UtSystem
{

    static long timer;
    
    static {
        UtSystem.timer = -1L;
    }
    
    public static boolean isBigEndian() {
        return true;
    }
    
    public static long getUserTimeMSec() {
        return (UtSystem.timer == -1L) ? getSystemTimeMSec() : UtSystem.timer;
    }
    
    public static void setUserTimeMSec(final long userTime) {
        UtSystem.timer = userTime;
    }
    
    public static long updateUserTimeMSec() {
        return UtSystem.timer = getSystemTimeMSec();
    }
    
    public static void resetUserTimeMSec() {
        UtSystem.timer = -1L;
    }
    
    public static long getTimeMSec() {
        return TimeUtils.millis();
    }
    
    public static long getSystemTimeMSec() {
        return TimeUtils.millis();
    }
    
}
