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

import loon.LSystem;
import loon.Screen;
import loon.event.GameTouch;
import loon.event.Updateable;
import loon.opengl.GLEx;
import loon.utils.timer.LTimer;
import loon.utils.timer.LTimerContext;

public class TimerTest extends Screen{
	
	LTimer timer1 = new LTimer(LSystem.SECOND);

	@Override
	public void draw(GLEx g) {
		
		
	}

	@Override
	public void onLoad() {

		LTimer timer2 = new LTimer(LSystem.SECOND);
		//执行10次
		timer2.setRepeats(10);
		timer2.setUpdateable(new Updateable() {
			
			@Override
			public void action(Object a) {
				i("Timer2 running");
				
			}
		});
		//提交计时器到Loon循环中
		timer2.submit();
		
		//关闭Screen时注销计时器
		putRelease(timer2);

		add(MultiScreenTest.getBackButton(this, 2));
	}

	@Override
	public void alter(LTimerContext context) {
		//满足Timer1延迟条件时
		if(timer1.action(context)){
			//打印info
			i("Timer1 Running");
		}
	}

	@Override
	public void resize(int width, int height) {
		
		
	}

	@Override
	public void touchDown(GameTouch e) {
		
		
	}

	@Override
	public void touchUp(GameTouch e) {
		
		
	}

	@Override
	public void touchMove(GameTouch e) {
		
		
	}

	@Override
	public void touchDrag(GameTouch e) {
		
		
	}

	@Override
	public void resume() {
		
		
	}

	@Override
	public void pause() {
		
		
	}

	@Override
	public void close() {
		
		
	}
	
	

}
