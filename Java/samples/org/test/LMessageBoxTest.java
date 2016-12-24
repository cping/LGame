package org.test;

import loon.LTexture;
import loon.LTransition;
import loon.Screen;
import loon.component.DefUI;
import loon.component.LComponent;
import loon.component.LMessageBox;
import loon.event.ClickListener;
import loon.event.GameTouch;
import loon.font.LFont;
import loon.opengl.GLEx;
import loon.utils.timer.LTimerContext;

public class LMessageBoxTest extends Screen {

	public LTransition onTransition() {
		return LTransition.newEmpty();
	}

	@Override
	public void draw(GLEx g) {

	}

	@Override
	public void onLoad() {

		//设置默认字体大小为20号字
		LFont.setDefaultFont(LFont.getFont(20));
		LTexture texture = DefUI.getGameWinFrame(200, 200);
		LMessageBox box = new LMessageBox(new String[] {
				"人间谁能看尽山色，千里孤行终归寂寞。翻天覆地炙手可热，百年之后有谁记得。",
				"明月西斜遗珠何落，金乌归海乾坤并合。世事如棋造化难说，能解其中非你非我。" }, texture, 66, 66, 180,
				180);
		//行间距3
		box.setLeading(3);
		//偏移10,10
		box.setOffset(10, 10);
		add(box);
		centerOn(box);
		box.SetClick(new ClickListener() {

			@Override
			public void UpClick(LComponent comp, float x, float y) {

			}

			@Override
			public void DragClick(LComponent comp, float x, float y) {

			}

			@Override
			public void DownClick(LComponent comp, float x, float y) {
				LMessageBox box = (LMessageBox) comp;
				box.loop();
			}

			@Override
			public void DoClick(LComponent comp) {

			}
		});
		add(MultiScreenTest.getBackButton(this,0));
		

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
