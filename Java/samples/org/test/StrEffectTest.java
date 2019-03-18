package org.test;

import loon.Stage;
import loon.action.sprite.effect.StringEffect;
import loon.canvas.LColor;
import loon.event.Touched;
import loon.geom.Vector2f;
import loon.utils.Calculator;

public class StrEffectTest extends Stage{

	@Override
	public void create() {
		//构建一个简单的计算用类
		final Calculator cal = new Calculator();
		//监听触屏
		down(new Touched() {
			
			@Override
			public void on(float x, float y) {
				String mes = cal.add(1).toString();
				//添加一个字符效果,上移,显示计算后数字,除此显示位置为触屏位置,颜色lightYellow
				add(StringEffect.up(mes, Vector2f.at(x, y), LColor.lightYellow));
				//添加一个字符效果,下移,显示数字,除此显示位置为触屏位置,颜色green
				add(StringEffect.down(mes, Vector2f.at(x, y), LColor.green));
				//添加一个字符效果,左移,显示数字,除此显示位置为触屏位置,颜色yellow
				add(StringEffect.left(mes, Vector2f.at(x, y), LColor.yellow));
				//添加一个字符效果,右移,显示数字,除此显示位置为触屏位置,颜色lightSkyBlue
				add(StringEffect.right(mes, Vector2f.at(x, y), LColor.lightSkyBlue));
				//添加一个字符效果,45度视角的左方(西北),显示数字,除此显示位置为触屏位置,颜色lightPink
				add(StringEffect.m45Dleft(mes, Vector2f.at(x, y), LColor.lightPink));
				//添加一个字符效果,45度视角的右方(东南),显示数字,除此显示位置为触屏位置,颜色lightSlateGray
				add(StringEffect.m45Dright(mes, Vector2f.at(x, y), LColor.lightSlateGray));
				//添加一个字符效果,45度视角的上方(东北),显示数字,除此显示位置为触屏位置,颜色lightCyan
				add(StringEffect.m45Dup(mes, Vector2f.at(x, y), LColor.lightCyan));
				//添加一个字符效果,45度视角的下方(西南),显示数字,除此显示位置为触屏位置,颜色lightSalmon
				add(StringEffect.m45Ddown(mes, Vector2f.at(x, y), LColor.lightSalmon));
				//添加一个字符效果,不移动,显示数字,除此显示位置为触屏位置,颜色red
				add(StringEffect.notMove(mes, Vector2f.at(x, y), LColor.red));
			}
		});
		add(MultiScreenTest.getBackButton(this, 1));
	}

}
