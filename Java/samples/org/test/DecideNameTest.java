package org.test;

import loon.LTransition;
import loon.Screen;
import loon.component.LDecideName;
import loon.event.GameTouch;
import loon.font.LFont;
import loon.opengl.GLEx;
import loon.utils.TArray;
import loon.utils.timer.LTimerContext;

public class DecideNameTest extends Screen {

	@Override
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
		TArray<String> list = new TArray<String>();
		list.add("赵钱孙李周吴郑王");
		list.add("冯陈褚卫蒋沈韩杨");
		list.add("朱秦尤许何吕施张");
		list.add("孔曹严华金魏陶姜");
		list.add("龙虎狮豹鹰鹏麒麟");
		list.add("<>");
		LDecideName decideName = new LDecideName(list, 0, 0);
		decideName.setLabelOffsetY(10);
		decideName.setLabelName("角色名:");
		decideName.setLeftOffset(20);
		decideName.setTopOffset(50);
		centerOn(decideName);
		add(decideName);

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
