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

public class MessageBoxTest3 extends Stage {
	/**
	 *  //对话剧本样式,本质就是一个loon默认的配置文件格式,中英文或其它语言皆可,除了英文关键字不能改以外,其他随便修改
	 *  [秦假仙]
		name = "秦假仙"
		face = "ball.png"
		[妖溺天]
		name = "妖逆天"
		face = "ccc.png"
		[对话]
		begin name = "剧本1"
		妖溺天:人间,又污秽了。人间的小神,你尽力了。三招不能败你，妖溺天自刎当场。
		秦假仙:逆天，尚有例外，逆吾，绝无生机!
		妖溺天:大胆，狂妄，纵横无界为主，问天，可敢为敌？!
		秦假仙:试我诛神之招！
		秦假仙:魔武究竟，十二神天龙逆道，八部儒释离火极！
		end
		begin name = "剧本2"
		妖溺天:五浊恶世，吞没菩提。
		秦假仙:中原不败大侠秦厉害。
		end
	 */
	@Override
	public void create() {

		// 按照screen比例获得信息框大小
		int messageBoxWidth = MathUtils.iceil(getWidth() - 2f);
		int messageBoxHeight = MathUtils.iceil(getHeight() / 2.7f - 2f);
		// 构建一个默认的游戏窗体图
		LTexture texture = getGameWinFrame(messageBoxWidth, messageBoxHeight);
		// 构建信息框
		final LMessageBox box = new LMessageBox(texture, 0, 0, messageBoxWidth, messageBoxHeight);

		// 加载对话项的剧本1
		box.load("messageboxtest.txt", "对话", "剧本1");
		box.up((x, y) -> {
			box.next();
			if (box.isCompleted()) {
				// 加载对话项的剧本2
				box.load("messageboxtest.txt", "对话", "剧本2");
			}
			// .loop();
		});
		bottomOn(box);
		add(box);

	}

}
