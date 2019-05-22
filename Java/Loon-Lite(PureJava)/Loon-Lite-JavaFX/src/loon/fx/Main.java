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
import loon.LTransition;
import loon.LazyLoading;
import loon.Screen;
import loon.canvas.LColor;
import loon.event.GameTouch;
import loon.fx.JavaFXGame.JavaFXSetting;
import loon.fx.Loon.GameApp;
import loon.opengl.GLEx;
import loon.utils.timer.LTimerContext;

public class Main extends GameApp {

	public static class ScreenTest extends Screen {

		LTexture texture = loadTexture("ccc.png");

		@Override
		public void draw(GLEx g) {
			// g.fillRect(66, 66, 388, 388,LColor.red);
			g.draw(texture, 77, 77, LColor.red);
			g.drawString("数据测试avddf", 77, 77);
		}

		@Override
		public void onLoad() {
			// setBackground(LColor.yellow);
		}

		@Override
		public void alter(LTimerContext timer) {

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

	public static void main(String[] args) {

		JavaFXSetting setting = new JavaFXSetting();
		// 原始大小
		setting.width = 480;
		setting.height = 320;
		setting.isDebug = true;
		setting.isLogo = false;
		setting.isDisplayLog = false;

		// 要求显示的大小
		setting.width_zoom = 640;
		setting.height_zoom = 480;
		setting.logoPath = "loon_logo.png";
		setting.isFPS = false;
		setting.isMemory = false;
		setting.iconPaths = new String[] { "l.png" };
		setting.fullscreen = true;
		// 默认字体
		setting.fontName = "黑体";

		register(setting, new LazyLoading.Data() {

			@Override
			public Screen onScreen() {
				return new ScreenTest();
			}
		});
	}

}
