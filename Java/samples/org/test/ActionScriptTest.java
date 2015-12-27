package org.test;

import loon.LTransition;
import loon.Screen;
import loon.action.ActionScript;
import loon.action.sprite.Sprite;
import loon.event.GameTouch;
import loon.opengl.GLEx;
import loon.utils.timer.LTimerContext;

public class ActionScriptTest extends Screen {

	public LTransition onTransition() {
		return LTransition.newEmpty();
	}

	@Override
	public void draw(GLEx g) {

	}

	@Override
	public void onLoad() {

		Sprite sprite = new Sprite("assets/ball.png");
		ActionScript script = act(sprite,
				"move(127,127,true)->rotate(360)->delay(2f)->fadein(60)->fadeout(90)");
		add(sprite);
		script.start();

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
