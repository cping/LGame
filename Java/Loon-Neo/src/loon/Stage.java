package loon;

import loon.event.GameTouch;
import loon.opengl.GLEx;
import loon.utils.timer.LTimerContext;

/**
 * 一个Screen的衍生抽象类,除了create默认都不必实现,纯组件构建游戏时可以使用此类派生画面
 */
public abstract class Stage extends Screen {
	
	public LTransition onTransition() {
		return LTransition.newEmpty();
	}

	public abstract void create();

	@Override
	public void draw(GLEx g) {

	}

	@Override
	public void onLoad() {
		create();
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
