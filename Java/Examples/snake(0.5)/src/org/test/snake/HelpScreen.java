package org.test.snake;

import loon.Screen;
import loon.event.GameTouch;
import loon.opengl.GLEx;
import loon.utils.timer.LTimerContext;

public class HelpScreen extends Screen {

	@Override
	public void draw(GLEx g) {
		g.draw(Assets.background, 0, 0);
		g.draw(Assets.help1, 64, 100);
		g.draw(Assets.buttons, 256, 416, 0, 64, 64, 64);
	}

	@Override
	public void onLoad() {

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
		  if(e.x() > 256 && e.y() > 416 ) {
              setScreen(new HelpScreen2());
              if(Settings.soundEnabled){
                  Assets.click.play();
              }
              return;
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
