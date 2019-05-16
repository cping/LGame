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

import javafx.animation.AnimationTimer;
import loon.LSetting;
import loon.LazyLoading;
import loon.Screen;

public class Main  {


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

		Loon.register(setting, new LazyLoading.Data() {

			@Override
			public Screen onScreen() {
				return null;//new CollisionWorldTest();
			}
		});
	}

}
