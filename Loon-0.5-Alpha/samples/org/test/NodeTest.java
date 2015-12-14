package org.test;

import loon.LTransition;
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
import loon.font.LFont;
import loon.utils.Easing;

public class NodeTest extends SpriteBatchScreen {

	public LTransition onTransition() {
		return LTransition.newFadeIn();
	}

	LNSprite sprite;

	public void create() {

		//设置默认字体大小为20号字
		LFont.setDefaultFont(LFont.getFont(20));
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
