package org.loon.main;

import java.io.IOException;

import android.app.Activity;
import android.app.ActivityManager;
import android.util.Log;

import loon.LRipple;
import loon.LSystem;
import loon.LTouch;
import loon.action.ScaleTo;
import loon.action.map.Field2D;
import loon.action.map.tmx.TMXTiledMap;
import loon.core.Assets;
import loon.core.EmulatorListener;
import loon.core.event.ActionKey;
import loon.core.event.Updateable;
import loon.core.graphics.LComponent;
import loon.core.graphics.LScrollContainer;
import loon.core.graphics.Screen;
import loon.core.graphics.component.ClickListener;
import loon.core.graphics.component.DefUI;
import loon.core.graphics.component.LCheckBox;
import loon.core.graphics.component.LClickButton;
import loon.core.graphics.component.LDecideName;
import loon.core.graphics.component.LLabel;
import loon.core.graphics.component.LLabel.LabelAlignment;
import loon.core.graphics.component.LMenu.MenuItem;
import loon.core.graphics.component.LHistory;
import loon.core.graphics.component.LLabels;
import loon.core.graphics.component.LLayer;
import loon.core.graphics.component.LMap2D;
import loon.core.graphics.component.LMenu;
import loon.core.graphics.component.LPad;
import loon.core.graphics.component.LPaper;
import loon.core.graphics.component.LProgress;
import loon.core.graphics.component.LSelectorIcon;
import loon.core.graphics.component.LTextArea;
import loon.core.graphics.component.LTextBar;
import loon.core.graphics.component.LTextField;
import loon.core.graphics.component.LTextList;
import loon.core.graphics.component.LToast;
import loon.core.graphics.component.LWindow;
import loon.core.graphics.component.LProgress.ProgressType;
import loon.core.graphics.component.LToast.Style;
import loon.core.graphics.component.table.LTable;
import loon.core.graphics.component.table.ListItem;
import loon.core.graphics.component.table.TableLayout;
import loon.core.graphics.device.LColor;
import loon.core.graphics.device.LFont;
import loon.core.graphics.device.LImage;
import loon.core.graphics.opengl.GL;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.core.graphics.opengl.TextureUtils;
import loon.core.timer.LTimer;
import loon.core.timer.LTimerContext;
import loon.media.Sound;
import loon.utils.StringUtils;
import loon.utils.collection.Array;

public class ScreenTest extends Screen {

	public void onLoad() {
	}

	@Override
	public void draw(GLEx g) {

	}

	LTimer delay = new LTimer(LSystem.SECOND * 3);

	@Override
	public void alter(LTimerContext timer) {
		if (delay.action(timer)) {
			for (int i = 0; i < 10; i++) {
				LTexture texture = new LImage(512,512,true).getTexture();
				texture.destroy();
			}
			showMemoryInfo();
		}
	}

	public void showMemoryInfo() {
		final ActivityManager activityManager = (ActivityManager) LSystem
				.getActivity().getSystemService(Activity.ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
		activityManager.getMemoryInfo(info);
		Log.i("test", "系统剩余内存:" + (info.availMem >> 10) + "k");
		Log.i("test", "系统是否处于低内存运行：" + info.lowMemory);
	}

	@Override
	public void touchDown(LTouch e) {

		// playSound("assets/pussy.wav");
		// stopSound("assets/music/pussy.wav");
	}

	@Override
	public void touchUp(LTouch e) {

	}

	@Override
	public void touchMove(LTouch e) {

	}

	@Override
	public void touchDrag(LTouch e) {

	}

}
