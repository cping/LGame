package org.test;

import loon.Screen;
import loon.action.sprite.SpriteLabel;
import loon.event.GameTouch;
import loon.font.LFont;
import loon.opengl.GLEx;
import loon.stage.ImagePlayer;
import loon.stage.Player.Pointer;
import loon.utils.timer.LTimerContext;

public class PlayerClickTest extends Screen {

	@Override
	public void draw(GLEx g) {

	}

	@Override
	public void onLoad() {

		//设置默认字体大小为20号字
		LFont.setDefaultFont(LFont.getFont(20));
		final SpriteLabel label = new SpriteLabel("testing", 155, 25);
		add(label);
		
		ImagePlayer player = new ImagePlayer("ccc.png");
		getRootPlayer().addAt(player.setScaleX(2).setScaleY(2), 50, 50);
		player.events().add(new Pointer() {

			@Override
			public void onStart(float x, float y) {
				label.setLabel("start");
				System.out.println("start");
			}

			@Override
			public void onEnd(float x, float y) {
				label.setLabel("end");
				System.out.println("end");
			}

			@Override
			public void onDrag(float x, float y) {
				label.setLabel("drag");
				System.out.println("drag");
			}
		});

		add(player);

		add(MultiScreenTest.getBackButton(this));
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
