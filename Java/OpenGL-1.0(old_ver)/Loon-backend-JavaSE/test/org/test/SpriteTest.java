package org.test;

import loon.LGame;
import loon.LKey;
import loon.LSetting;
import loon.LSystem;
import loon.LTouch;
import loon.LTransition;
import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatchScreen;
import loon.action.sprite.SpriteFont;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.core.geom.Vector2f;
import loon.core.graphics.device.LColor;

public class SpriteTest extends SpriteBatchScreen {

	SpriteFont font;

	@Override
	public void create() {
		// TODO Auto-generated method stub
		font = SpriteFont.read("assets/ScoreFont.pak");
	}
	
	public LTransition onTransition() {
		return LTransition.newEmpty();
	}
	
	@Override
	public void after(SpriteBatch batch) {
		batch.drawString(String.format("%s %s", getTouchX(),getTouchY()), getHalfWidth(), getHalfHeight());
		batch.draw(font, "Test",99,99);
		batch.draw(font, "Test", new Vector2f(0, 0), LColor.red, 0,
				new Vector2f(), new Vector2f(1f, 1f), SpriteEffects.None);

	}

	@Override
	public void before(SpriteBatch batch) {

	}

	@Override
	public void press(LKey e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void release(LKey e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(long elapsedTime) {

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public void touchDown(LTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void touchUp(LTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void touchMove(LTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void touchDrag(LTouch e) {
		// TODO Auto-generated method stub

	}

	public static void main(String[] args) {
		LSetting setting = new LSetting();
		setting.width=800;
		setting.height=600;
	
		LGame.register(setting, SpriteTest.class);
	}

}
