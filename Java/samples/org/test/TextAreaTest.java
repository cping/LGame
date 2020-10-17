package org.test;

import loon.LTexture;
import loon.Stage;
import loon.canvas.LColor;
import loon.component.LTextArea;
import loon.events.Touched;

public class TextAreaTest extends Stage {

	@Override
	public void create() {
		// 构建一个300x240的游戏窗体背景图,颜色黑蓝相间,横向渐变
		LTexture texture = getGameWinFrame(300, 240, LColor.black, LColor.blue, false);
		// 允许显示行数默认(若显示行数设置可以写成(10,66,36,300,240)这类),位置66,36,大小300x240,文字闪烁
		final LTextArea area = new LTextArea(66, 36, 300, 240, true);
		// 替换默认文字颜色并禁止文字闪烁
		// area.setDefaultColor(255, 255, 255, false);
		area.setBackground(texture);
		area.put("你惊扰了【撒旦】的安眠", LColor.red);
		area.put("2333333333333", LColor.yellow);
		area.put("6666666666");
		area.put("点击我增加数据");
		// 从下向上刷数据
		// area.setShowType(LTextArea.TYPE_UP);
		// 清空数据
		// area.clear();
		area.up(new Touched() {

			@Override
			public void on(float x, float y) {
				area.put("数据增加", LColor.red);

			}
		});
		// 偏移文字显示位置
		area.setLeftOffset(5);
		area.setTopOffset(5);
		// addString为在前一行追加数据
		area.addString("1", LColor.red);
		add(area);
		add(MultiScreenTest.getBackButton(this, 0));
	}

}
