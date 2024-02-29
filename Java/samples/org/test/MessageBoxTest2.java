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

import loon.LTexture;
import loon.Stage;
import loon.component.LMessageBox;
import loon.utils.MathUtils;

public class MessageBoxTest2 extends Stage {

	@Override
	public void create() {
		// 按照screen比例获得信息框大小
		int messageBoxWidth = MathUtils.iceil(getWidth() - 2f);
		int messageBoxHeight = MathUtils.iceil(getHeight() / 2.7f - 2f);
		// 构建一个默认的游戏窗体图
		LTexture texture = getGameWinFrame(messageBoxWidth, messageBoxHeight);
		// 构建信息框
		final LMessageBox box = new LMessageBox(texture, 0, 0, messageBoxWidth, messageBoxHeight);
		// 偏移显示位置
		// box.setBoxOffsetX(5);
		// box.setBoxOffsetY(5);
		// 自动按信息框大小设定头像位置
		box.setAutoFaceImage();
		// box.flagHide();
		// 添加角色对话
		box.addMessage("test1", "妖溺天:人间,又污秽了。人间的小神,你尽力了。三招不能败你，妖溺天自刎当场。");
		box.addMessage("test2", "秦假仙:逆天，尚有例外，逆吾，绝无生机！");

		// 绑定test1和test2的对应头像图片
		box.bindFaceImage("test1", "ccc.png");
		box.bindFaceImage("test2", "ball.png");
		// 额外设定对话索引和头像关系
		// box.setMessageFace(0, "test1");
		// box.setMessageFace(1, "test2");

		box.up((x, y) -> {
			box.loop();
		});
		bottomOn(box);
		add(box);
	}

}
