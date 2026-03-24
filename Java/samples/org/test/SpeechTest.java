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

import loon.Stage;
import loon.canvas.LColor;
import loon.component.LSpeechDialog;
import loon.component.LSpeechDialog.BubbleType;
import loon.component.LSpeechDialog.TextSegment;
import loon.utils.TArray;

public class SpeechTest extends Stage {

	@Override
	public void create() {
		LSpeechDialog dialog = new LSpeechDialog(25, 25, 330, 200);
		// 添加一个说话角色, 对话框伴随的小尾巴在左侧
		dialog.putCharacter("角色A", LColor.white, LColor.blue, null, "left");
		// 添加一组对话
		TArray<TextSegment> mes1 = new TArray<TextSegment>();
		// 前半部分单纯蓝色文字
		mes1.add(new LSpeechDialog.TextSegment("Hello Java\n", LColor.blue));
		// 中间部分单纯黑色文字
		mes1.add(new LSpeechDialog.TextSegment("你…你真的要走吗？\n不能等到下午再说吗？", LColor.black));
		// 后半部分红色，震颤，幅度3
		mes1.add(new LSpeechDialog.TextSegment("\n你总说我是你的翅膀\n难道就不能让我陪你翱翔？", LColor.red, true, false, false, false,
				false, 3, 0, 3f));
		// 添加对话到角色, 语速0.1秒每字，使用椭圆形对话框
		dialog.putDialogue("角色A", mes1, 0.1f, BubbleType.ELLIPSE, "normal");

		// 添加角色B, 小尾巴位于对话框中间
		dialog.putCharacter("角色B", LColor.white, LColor.blue, null, "center");
		// 添加对话
		TArray<TextSegment> mes2 = new TArray<TextSegment>();
		mes2.add(new LSpeechDialog.TextSegment("不可以的，你要知道，\n男人的归途是星辰大海，\n不能因为树木而放弃森林。", LColor.black, false, false,
				false, false, false, 3, 0, 3f));
		dialog.putDialogue("角色B", mes2, 0.1f, BubbleType.ROUND, "normal");

		// 添加第三组对话
		TArray<TextSegment> mes3 = new TArray<TextSegment>();
		// 所有特效一起开
		mes3.add(new LSpeechDialog.TextSegment("\n汝可知伊藤诚，\n恨不逢旧事乎？", LColor.red, true, true, true, true, true, 9, 9,
				6f));
		// 添加对话到角色A, 语速0.2秒每字，使用圆形对话框, 偏移显示位置-20,20
		dialog.putDialogue("角色A", mes3, 0.2f, BubbleType.CIRCLE, "normal", -20f, 20f);

		centerOn(dialog);
		add(dialog);
		// 点击对话框时直接进入下一个对话
		dialog.up((x, y) -> {
			dialog.nextDialogue();
		});

	}

}
