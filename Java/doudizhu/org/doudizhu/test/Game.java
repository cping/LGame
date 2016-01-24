package org.doudizhu.test;

import loon.LSetting;
import loon.LTexture;
import loon.LTextures;
import loon.LazyLoading;
import loon.Screen;
import loon.event.GameTouch;
import loon.javase.Loon;
import loon.opengl.GLEx;
import loon.utils.timer.LTimerContext;

public class Game extends Screen {
	public final static int MENU = 0;
	public final static int GAME = 1;
	public final static int EXIT = 2;
	public final static int SMALL_CARD = 3;
	public final static int WRONG_CARD = 4;
	public final static int EMPTY_CARD = 5;

	// 额外的纵向缩放比例
	public static float SCALE_VERTICAL = 1f;
	// 额外的横向缩放比例
	public static float SCALE_HORIAONTAL = 1f;
	private static int mode = 0;
	MenuView mv;
	GameView gv;

	static Game handle;

	public static LTexture getImage(String path) {
		return LTextures.loadTexture("assets/" + path + ".png");
	}

	public static void sendEmptyMessage(int idx) {
		if (handle != null) {
			switch (idx) {
			case 0:

				mode = 0;
				break;
			case 1:

				mode = 1;
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void draw(GLEx g) {
		switch (mode) {
		case 0:
			if (mv != null) {
				mv.onDraw(g);
			}
			break;
		case 1:
			if (gv != null) {
				gv.onDraw(g);
			}
			break;
		default:
			break;
		}
		
	}

	@Override
	public void onLoad() {
		handle = this;
		mv = new MenuView();
		gv = new GameView();
	}

	@Override
	public void alter(LTimerContext timer) {

	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void touchDown(GameTouch e) {

	}

	@Override
	public void touchUp(GameTouch e) {

		switch (mode) {
		case 0:
			if (mv != null) {
				mv.onTouch(e.x(), e.y());
			}
			break;
		case 1:
			if (gv != null) {
				gv.onTouch(e.x(), e.y());
			}
			break;
		default:
			break;
		}
		
	
	}

	@Override
	public void touchMove(GameTouch e) {

	}

	@Override
	public void touchDrag(GameTouch e) {

	}

	@Override
	public void resume() {

	}

	@Override
	public void pause() {

	}

	@Override
	public void close() {

	}

	public static void main(String[] args) {

		LSetting setting = new LSetting();
		setting.isFPS = true;
		setting.isLogo = false;
		setting.logoPath = "loon_logo.png";
		setting.width = 480;
		setting.height = 320;
		setting.fps = 60;
		setting.fontName = "黑体";
		setting.appName = "斗地主";
		setting.emulateTouch = false;
		Loon.register(setting, new LazyLoading.Data() {

			@Override
			public Screen onScreen() {
				return new Game();
			}
		});

	}
}
