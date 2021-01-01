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
import loon.font.LFont;
import loon.opengl.GLEx;
import loon.opengl.LSTRFont;
import loon.utils.timer.LTimerContext;

public class LFontTest extends Screen{

	
	
	@Override
	public void draw(GLEx g) {
		if(font!=null) {
			font.drawString(g,"悠然半步,平山河萬里.\n偷闲一子,定紅塵千秋.\nabcdD", 55, 55);
			//实际产生的纹理
			g.draw(font.getTexture(), 0,128);
		}
	}
	
	LSTRFont font;

	@Override
	public void onLoad() {
		//setBackground(LColor.red);
		//加载一个默认的本地字体(和运行环境有关,不同操作系统效果不一样,需要一致请使用BMFont或者BDFont)，并且注入需要初始化到纹理的文字(重复字符会自动去除)
		font = new LSTRFont(LFont.getDefaultFont(),",.，。abcdefgABCD1234悠然半步平山河萬里偷闲一子定紅塵千秋半是率性半是癫一念超然红尘元");
		//font.setPixelColor(LColor.yellow);
	    //font.setFontSize(25);
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
