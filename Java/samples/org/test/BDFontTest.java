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

import loon.Screen;
import loon.events.GameTouch;
import loon.font.BDFont;
import loon.opengl.GLEx;
import loon.utils.timer.LTimerContext;

public class BDFontTest extends Screen {

	@Override
	public void draw(GLEx g) {
		if (font != null) {
			font.drawString(g,"悠然半步,平山河萬里.\n偷闲一子,定紅塵千秋.\nabcdD", 55, 55);
			//实际产生的纹理
			g.draw(font.getTexture(), 0,128);
		}
	}

	BDFont font;

	@Override
	public void onLoad() {
		//setBackground(LColor.red);
		//加载一个bdf字体文件,并且注入需要初始化到纹理的文字(重复字符会自动去除)
		font = new BDFont("assets/pixfont.bdf", ",.，。abcdefgABCD1234悠然半步平山河萬里偷闲一子定紅塵千秋半是率性半是癫一念超然红尘元");
		//改变生成纹理字体时的纹理像素大小(默认按照bdf文件的pixelsize或者size项渲染)
		//font.setPixelFontSize(20);
		//像素字体颜色设置
		//font.setPixelColor(LColor.yellow);
		//改变字体显示时大小(直接按比例缩放，放大看有可能剪切不齐或者模糊)
		font.setSize(20);
		//变更渲染到纹理的字体内容
		//font.updateTexture("新的紋理字体内容");

		add(MultiScreenTest.getBackButton(this,0));
	}

	@Override
	public void alter(LTimerContext context) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void touchDown(GameTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void touchUp(GameTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void touchMove(GameTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void touchDrag(GameTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

}
