package loon.an;

import loon.LSetting;

public class JavaANSetting extends LSetting {

    // 屏幕显示模式
    public JavaANMode showMode = JavaANMode.Fill;

    //显示大小时修正显示栏高度
    public boolean fixStatusHeight = true;

    // 是否允许改变屏幕方向
    public boolean useOrientation = true;

    // 是否使用唤醒锁
    public boolean useWakelock = false;

    // 请求的屏幕方向(-1时以xml设置为准)
    public int orientation = -1;

    // 若此项为true,则会检查configChanges是否已经设置
    public boolean checkConfig = false;

    // 是否隐藏状态栏
    public boolean hideStatusBar = true;

    // 是否隐藏虚拟按钮
    public boolean useImmersiveMode = false;

    // 是否使用等比屏幕缩放（使用此项，可以保证游戏画面不是失真，而不使用此项，则默认全屏拉伸满屏幕，不管画面是否变形）
    public boolean useRatioScaleFactor = false;

    // android事件专用监听器
    public JavaANListener listener = null;

    // 是否锁定注销功能(若为true，则默认back按键不许退出)
    public boolean lockBackDestroy = false;

    // 是否启动back注销功能(若为true，则默认back按键即注销游戏)
    public boolean isBackDestroy = false;

    public boolean doubleBuffer = false;

}