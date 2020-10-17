package loon.test;

import loon.LTexture;
import loon.LTransition;
import loon.Screen;
import loon.action.avg.AVGDialog;
import loon.action.sprite.effect.NaturalEffect;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.events.GameTouch;
import loon.font.LFont;
import loon.opengl.GLEx;
import loon.utils.Scale;
import loon.utils.timer.LTimerContext;

public class ScreenTest extends Screen {

	public LTransition onTransition() {
		return LTransition.newEmpty();
	}

	Image img = Image.createImage(200, 200);
	LFont font;
	@Override
	public void draw(GLEx g) {
g.setFont(font);
g.drawString("大笔写大字", 166, 166);
g.draw(img.texture(), 125,125);
	}

	@Override
	public void onLoad() {
	font=LFont.getFont(12);

	img.getCanvas().setColor(LColor.yellow);
	img.getCanvas().fillRect(0, 0, img.getWidth(), img.getHeight());

	img.setPixel(LColor.red, 66, 66);
	img.setPixel(LColor.red, 68, 68);
	img.setPixel(LColor.red, 67, 67);
	img.setPixel(LColor.red, 65, 65);
	}

	@Override
	public void alter(LTimerContext timer) {

	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void touchDown(GameTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void touchUp(GameTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void touchMove(GameTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void touchDrag(GameTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

}
