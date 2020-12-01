/**
 * Copyright 2008 - 2020 The Loon Game Engine Authors
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
import loon.component.LQuestionAnswer;
import loon.component.LToast;
import loon.component.LToast.Style;
import loon.component.LQuestionAnswer.QAObject;
import loon.events.Touched;
import loon.font.BMFont;

public class AnswerTest extends Stage {

	@Override
	public void create() {

		// 构建问答器
		final LQuestionAnswer qaer = new LQuestionAnswer(50, 65, 300, 300);
		
		// 构建问答对象
		QAObject qa1 = new QAObject("“粉身碎骨浑不怕,要留清白在人间”写的是__。", "A.石膏", "B.玉石", "C.石灰", "D.瓷器");
		// 设定正确答案
		qa1.addCorrect(2);

		QAObject qa2 = new QAObject("“大珠小珠落玉盘”形容的乐器是__。", "A.二胡", "B.琵琶", "C.古琴");
		qa2.addCorrect(1);

		QAObject qa3 = new QAObject("“行百里者半九十”出自__。", "A.《离骚》", "B.《吕氏春秋》", "C.《中庸》", "D.《战国策》");
		qa3.addCorrect(3);

		// 添加问答对象
		qaer.addQA(qa1);
		qaer.addQA(qa2);
		qaer.addQA(qa3);
		// 点击答案后自动进入下一题(仅限单选有效)
		// qaer.setAutoNext(true);
		// 改变当前问题索引号
		// qaer.setQuestionIndex(2);
		// 允许多项选择(默认单选)
		// qaer.setAllowMultiChoice(true);
		// 添加用户答案
		// qaer.addUserAnswer(2);
		// 判断当前问答答案是否正确
		// System.out.println(qaer.checkQA());
		qaer.setQAListener(new LQuestionAnswer.ClickEvent() {

			@Override
			public void onSelected(int questionIndex, int index) {
				// 检查当前答案是否正确
				if (qaer.checkQA()) {
					LToast.makeText("回答正确").show();
				} else {
					LToast.makeText("答题错误,索引应为:" + qaer.getCorrects(), Style.ERROR).show();
				}
			}
		});
		add(qaer);

		// 加载图像文字
		BMFont bmfont = new BMFont("assets/info.fnt", "assets/info.png");

		// 添加一个Next按钮,监听按下事件
		addButton(bmfont, "Next", 380, 180, 80, 35).down(new Touched() {

			@Override
			public void on(float x, float y) {

				// 检查当前索引问题是否有回答
				if (qaer.checkChoice()) {
					// 下一题
					qaer.nextQuestion();
					LToast.makeText("进度完成" + qaer.getPercentString() + ",正确:" + qaer.getUserCorrects().size).show();
				} else {
					LToast.makeText("尚未答题").show();
				}
			}
		});
		addButton(bmfont, "Back", 380, 120, 80, 35).down(new Touched() {

			@Override
			public void on(float x, float y) {
				// 上一题
				qaer.backQuestion();
			}
		});

		add(MultiScreenTest.getBackButton(this, 2));
	}

}
