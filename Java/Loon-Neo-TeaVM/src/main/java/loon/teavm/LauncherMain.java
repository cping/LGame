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

import loon.LTexture;
import loon.Screen;
import loon.Stage;
import loon.LazyLoading.Data;
import loon.canvas.LColor;
import loon.component.LClickButton;
import loon.component.LComponent;
import loon.component.LMessageBox;
import loon.component.LSelect;
import loon.component.LToast;
import loon.events.ClickListener;
import loon.teavm.TeaGame.TeaSetting;

public class LauncherMain {

	public static class TestScreen extends Stage {

		@Override
		public void create() {

			setBackground(LColor.red);
			LSelect select = new LSelect(120, 100, 200, 250);

			select.setMessage(new String[] { "ABDFDFD", "B", "C", "D" });
			add(select);

			LTexture texture = getGameWinFrame(200, 200);
			LMessageBox box = new LMessageBox(
					new String[] { "人间谁能看尽山色，千里孤行终归寂寞。翻天覆地炙手可热，百年之后有谁记得。", "明月西斜遗珠何落，金乌归海乾坤并合。世事如棋造化难说，能解其中非你非我。" },
					texture, 66, 66, 180, 180);
			// box.setGradientFontColor(true);
			// 行间距3
			box.setLeading(3);
			// 偏移10,10
			box.setBoxOffset(10, 10);

			// box.setFaceImage("ccc.png");

			add(box);
			centerOn(box);

			LClickButton click = LClickButton.make("DSDSDS");

			click.setLocation(170, 200);
			click.setToolTipText("DSDSDSFF");
			click.up((x, y) -> {
				add(LToast.makeText("AAAAAAAAAAAAAAAA"));
			});
			add(click);

			box.S(new ClickListener() {

				@Override
				public void UpClick(LComponent comp, float x, float y) {

				}

				@Override
				public void DragClick(LComponent comp, float x, float y) {

				}

				@Override
				public void DownClick(LComponent comp, float x, float y) {
					LMessageBox box = (LMessageBox) comp;
					box.loop();
				}

				@Override
				public void DoClick(LComponent comp) {

				}
			});
		}

	}

	public static void main(String[] args) {
		/*
		 * var document = HTMLDocument.current(); var div =
		 * document.createElement("div");
		 * div.appendChild(document.createTextNode("Loon-TeaVM"));
		 * document.getBody().appendChild(div);
		 */

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
		// setting.fontSizeClip = 3;
		setting.fontName = "黑体";
		setting.isConsoleLog = true;
		// setting.fullscreen = true;
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
