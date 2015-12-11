package org.test;

import loon.LSetting;
import loon.LTransition;
import loon.LazyLoading;
import loon.Screen;
import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatchScreen;
import loon.action.sprite.node.LNEase;
import loon.action.sprite.node.LNEnd;
import loon.action.sprite.node.LNLabel;
import loon.action.sprite.node.LNMoveBy;
import loon.action.sprite.node.LNRotateBy;
import loon.action.sprite.node.LNSequence;
import loon.action.sprite.node.LNSprite;
import loon.event.GameKey;
import loon.event.GameTouch;
import loon.javase.Loon;
import loon.utils.Easing;

public class NodeTest extends SpriteBatchScreen {

	public LTransition onTransition() {
		return LTransition.newFadeIn();
	}

	LNSprite sprite;

	public void create() {

		// 直接载入图片到节点(直接加载大图)
		sprite = LNSprite.GInitWithFilename("assets/ccc.png");
		// 支持拖拽
		sprite.setLocked(false);
		sprite.setLimitMove(false);
		sprite.Tag = "Test";

		add(sprite);

		LNLabel label = new LNLabel();
		label.setString("测试中");
		label.setRotation(60);
		label.setLocation(120, 120);
		add(label);

		// 执行node动画
		sprite.runAction(LNSequence.Action(LNEase.Action(Easing.BACK_IN_OUT,
				LNMoveBy.Action(1f, 225, 125)), LNRotateBy.Action(0.5f, 360),
				LNEnd.Action()));
		
		add(MultiScreenTest.getBackButton(this));

	}

	public void after(SpriteBatch batch) {

	}

	public void before(SpriteBatch batch) {

	}

	public void press(GameKey e) {

	}

	public void release(GameKey e) {

	}

	public void update(long elapsedTime) {

	}

	public void touchDown(GameTouch e) {

	}

	public void touchUp(GameTouch e) {

	}

	public void touchMove(GameTouch e) {

	}

	public void touchDrag(GameTouch e) {

	}

	public static void main(String[] args) {
		LSetting setting = new LSetting();
		setting.isFPS = true;
		setting.isLogo = false;
		setting.logoPath = "loon_logo.png";
		setting.width_zoom = 640;
		setting.height_zoom = 480;
		setting.fps = 60;
		setting.fontName = "黑体";
		setting.appName = "test";
		setting.emulateTouch = false;
		Loon.register(setting, new LazyLoading.Data() {

			@Override
			public Screen onScreen() {
				return new NodeTest();
			}
		});
	}

	@Override
	public void onResume() {

	}

	@Override
	public void onPause() {

	}

	@Override
	public void dispose() {

	}

	@Override
	public void resize(int width, int height) {

	}

}
