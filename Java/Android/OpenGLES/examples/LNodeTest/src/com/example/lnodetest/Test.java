package com.example.lnodetest;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatchScreen;
import loon.action.sprite.node.LNEase;
import loon.action.sprite.node.Easing;
import loon.action.sprite.node.LNDelay;
import loon.action.sprite.node.LNEnd;
import loon.action.sprite.node.LNFadeIn;
import loon.action.sprite.node.LNFadeOut;
import loon.action.sprite.node.LNLabel;
import loon.action.sprite.node.LNMoveBy;
import loon.action.sprite.node.LNRotateTo;
import loon.action.sprite.node.LNScaleTo;
import loon.action.sprite.node.LNSequence;
import loon.action.sprite.node.LNSprite;
import loon.core.input.LKey;
import loon.core.input.LTouch;
import loon.core.input.LTransition;

/**
 * Copyright 2008 - 2012
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.3.3
 */

public class Test extends SpriteBatchScreen {

	public LTransition onTransition() {
		return LTransition.newFadeIn();
	}

	public void create() {

		// 直接载入图片到节点(直接加载大图)
		LNSprite sprite = LNSprite.InitWithFilename("assets/ccc.png");
		add(sprite);

		LNLabel label = new LNLabel();
		label.setString("测试中");
		label.setRotation(60);
		add(label);

		sprite.runAction(LNSequence.Action(LNEase.Action(Easing.BACK_IN_OUT,
				LNMoveBy.Action(1f, 125, 225)), LNEnd.Action()));

		// 载入节点配置文件（大图切分为精灵）
		loadNodeDef("assets/thunder.Image.txt");

		LNSprite t1 = new LNSprite("thunder_02");
		t1.setLocation(145, 180);
		add(t1);

		LNSprite t3 = new LNSprite("thunder_03");
		t3.setLocation(199, 99);
		add(t3);

		LNSprite t2 = new LNSprite("thunder_04");
		t2.setLocation(99, 99);
		add(t2);

		t1.setAlpha(0f);
		// 动作执行
		t1.runAction(LNSequence.Action(LNDelay.Action(2f), LNFadeIn.Action(1f),
				LNFadeOut.Action(1f), LNFadeIn.Action(1f), LNFadeOut.Action(1f)));

		t2.setAlpha(0f);
		t2.runAction(LNSequence.Action(LNFadeIn.Action(1f),
				LNFadeOut.Action(1f), LNRotateTo.Action(3f, 90),
				LNFadeIn.Action(1f), LNFadeOut.Action(1f)));

		t3.setAlpha(0f);
		t3.runAction(LNSequence.Action(LNDelay.Action(1f), LNFadeIn.Action(1f),
				LNScaleTo.Action(3f, 2f), LNFadeOut.Action(1f),
				LNScaleTo.Action(3f, 1f), LNFadeIn.Action(1f),
				LNFadeOut.Action(1f)));

	}

	public void after(SpriteBatch batch) {

	}

	public void before(SpriteBatch batch) {

	}

	public void press(LKey e) {

	}

	public void release(LKey e) {

	}

	public void update(long elapsedTime) {

	}

	public void close() {

	}

	public void touchDown(LTouch e) {

	}

	public void touchUp(LTouch e) {

	}

	public void touchMove(LTouch e) {

	}

	public void touchDrag(LTouch e) {

	}

}
