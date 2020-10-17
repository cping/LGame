package org.doudizhu.test;

import loon.LSystem;
import loon.LTexture;
import loon.LTextures;
import loon.Screen;
import loon.component.LToast;
import loon.events.GameTouch;
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
			case 2:
				LSystem.exit();
				break;
			case 3:
				handle.add(LToast.makeText("你的牌太小！"));
				break;
			case 4:
				handle.add(LToast.makeText("出牌不符合规则！"));
				break;
			case 5:
				handle.add(LToast.makeText("请出牌！"));
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
		//把用户渲染置于画布顶层
		fristUserDraw();
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
}