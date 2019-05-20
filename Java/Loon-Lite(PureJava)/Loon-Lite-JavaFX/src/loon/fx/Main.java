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
package loon.fx;

import loon.LSetting;
import loon.LTexture;
import loon.LTextures;
import loon.LazyLoading;
import loon.Screen;
import loon.event.GameTouch;
import loon.fx.Loon.GameApp;
import loon.opengl.GLEx;
import loon.utils.timer.LTimerContext;

public class Main {

	public static class ScreenTest extends Screen{

		LTexture texture = LTextures.loadTexture("ccc.png");
		@Override
		public void draw(GLEx g) {
			g.draw(texture, 77, 77);
		}

		@Override
		public void onLoad() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void alter(LTimerContext timer) {
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

	public static void main(String[] args) {
		
		LSetting setting = new LSetting();
		// 原始大小
		setting.width = 480;
		setting.height = 320;
		setting.isDebug = false;
		setting.isLogo = false;
		setting.isDisplayLog = false;

		// 要求显示的大小
		setting.width_zoom = 640;
		setting.height_zoom = 480;
		setting.logoPath = "loon_logo.png";
		setting.isFPS = false;
		setting.isMemory = false;

		// 默认字体
		setting.fontName = "黑体";
		// setting.emulateTouch = true;

		GameApp.register(setting, new LazyLoading.Data() {

			@Override
			public Screen onScreen() {
				return new ScreenTest();
			}
		});
	}

}
