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
package loon.teavm;

import loon.Screen;
import loon.Stage;
import loon.LazyLoading.Data;

import loon.teavm.TeaGame.TeaSetting;


public class LauncherMain {
	
	public static class TestScreen extends Stage{

		@Override
		public void create() {
			// TODO Auto-generated method stub
			
		}
		
	}

	public static void main(String[] args) {
	/*	var document = HTMLDocument.current();
		var div = document.createElement("div");
		div.appendChild(document.createTextNode("Loon-TeaVM"));
		document.getBody().appendChild(div);*/

		TeaSetting setting = new TeaSetting();
		setting.fps = 60;
		setting.isDebug = true;
		setting.isDisplayLog = false;
		// source size
		setting.width = 480;
		setting.height = 320;
		// target size
		setting.width_zoom = 800;
		setting.height_zoom = 600;

		setting.isFPS = true;
		//setting.fontSize = 16;
		setting.fontName = "黑体";
		setting.isConsoleLog = true;
		// 按屏幕缩放比例缩放
		// setting.useRatioScaleFactor = true;
		// 当此项开启，并且gwt.xml中设置了loon.addtojs为true,会默认从js中加载资源


		// 设置一个需要的初始化进度条样式（不填则默认）
		// setting.progress = GWTProgressDef.newSimpleLogoProcess(setting);

		Loon.register(setting, new Data() {

			@Override
			public Screen onScreen() {
				return new TestScreen();
			}
		});
	}
}
