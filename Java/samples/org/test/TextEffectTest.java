/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
 * @version 0.5
 */
package org.test;

import loon.Screen;
import loon.Stage;
import loon.action.sprite.effect.TextEffect;
import loon.canvas.LColor;
import loon.event.FrameLoopEvent;
import loon.utils.MathUtils;

public class TextEffectTest extends Stage {

	@Override
	public void create() {

		// 构建一个文字效果
		final TextEffect text = new TextEffect();

		String[] messages = { "学英语", "好大的邪恶", "这屏幕又大又圆", "坚强", "我变色了", "我是什么颜色？", "绿了,绿了", "兄弟挺住", "有人看着看着就开了浏览器",
				"敬你是条汉子", "爱是一道绿光", "力量", "一回生,二回熟,慢慢就习惯了", "头上长着青青草原", "人才", "是个狼灭", "这谁顶的住啊?", "楼歪了", "我整个人都歪了",
				"要想生活过得去,就得头上……", "看热闹不嫌事大", "哈哈哈哈", "赐予你力量", "你们还有没有人性", "爱的力量", "共同开发", "舔狗没出路" };
		for (int i = 0; i < messages.length; i++) {
			String message = messages[i];
			LColor color = i % 2 == 0 ? LColor.white : LColor.red;
			if (message.indexOf("变色") != -1 || message.indexOf("力量") != -1 || message.indexOf('绿') != -1
					|| message.indexOf("头上") != -1) {
				color = LColor.green;
			}
			// 注入文字
			text.addText(messages[i], color, MathUtils.random(getHalfWidth(), getWidth() - 20),
					MathUtils.random(20, getHeight() - 40), 60f,
					message.indexOf('歪') != -1 ? MathUtils.random(0, 45) : 0, message.indexOf('大') != -1 ? 1.2f : 1f,
					MathUtils.random(-1f, -5f), 0f);
		}
		// 注入Effect到Screen
		add(text);
		// 让Screen内置一个间隔0的循环事件
		loop(0, new FrameLoopEvent() {

			@Override
			public void invoke(long elapsedTime, Screen e) {
				if (text.isCompleted()) {
					text.reset();
				}
			}

			@Override
			public void completed() {

			}
		});
		add(MultiScreenTest.getBackButton(this, 2));
	}

}
