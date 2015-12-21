package org.test;

import loon.LTransition;
import loon.Screen;
import loon.action.sprite.Sprite;
import loon.event.GameTouch;
import loon.font.LFont;
import loon.opengl.GLEx;
import loon.utils.timer.LTimerContext;

public class SpriteTest extends Screen {

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
		// 添加一个精灵，动画按照45x29每格拆分
		Sprite sprite = new Sprite("dog.png", 45, 29);
		sprite.Tag = "zzzzzzzzzzzzz";
		// 最多允许播放20帧
		sprite.setMaxFrame(20);
		sprite.setLocation(165, 165);
		// 缩放2倍
		sprite.setScale(2f);
		// 镜像垂直显示
		sprite.setTransform(Sprite.TRANS_MIRROR);
	
		
		Sprite sprite2 = new Sprite("dog.png", 45, 29);
		sprite2.Tag = "ccccccccccccccc";
		// 最多允许播放20帧
		sprite2.setMaxFrame(20);
		sprite2.setLocation(65, 165);
		// 缩放2倍
		sprite2.setScale(2f);
		// 镜像垂直显示
		sprite2.setTransform(Sprite.TRANS_MIRROR_ROT90);
		add(sprite2);
		add(sprite);
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
