package loon.template.mygame;

import loon.Screen;
import loon.Stage;
import loon.android.AndroidGame.AndroidSetting;
import loon.android.Loon;

public class MainActivity extends Loon {

    public static class TestScreen extends Stage{

        @Override
        public void create(){

        }

    }


    @Override
    public void onMain() {
        AndroidSetting setting = new AndroidSetting();
        setting.isFPS = true;
        setting.isMemory  = true;
        setting.isLogo = false;
        setting.fullscreen = true;
        setting.width = 320;
        setting.height = 480;
        //若启动此模式，则画面等比压缩，不会失真
        setting.useRatioScaleFactor = false;
        //强制一个显示大小(在android模式下，不填则默认全屏，此模式可能会造成画面失真)
        //setting.width_zoom = getContainerWidth();
        //setting.height_zoom = getContainerHeight();
        //屏幕显示模式
        //setting.showMode = LMode.FitFill;
        setting.logoPath = "loon_logo.png";
        setting.fps = 60;
        setting.fontName = "Dialog";
        setting.appName = "test";
        setting.emulateTouch = false;
        setting.lockBackDestroy = false;
        register(setting, new Data() {

            @Override
            public Screen onScreen() {
                return new TestScreen();
            }
        });
    }
}
