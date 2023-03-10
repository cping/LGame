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

import java.awt.Dimension;
import java.awt.Toolkit;

import loon.EmulatorListener;
import loon.LSystem;
import loon.LTexture;
import loon.Screen;
import loon.canvas.LColor;
import loon.canvas.Pixmap;
import loon.events.GameTouch;
import loon.opengl.GLEx;
import loon.se.JavaSESetting;
import loon.se.Loon;
import loon.utils.Resolution;
import loon.utils.timer.LTimerContext;

public class Main extends Loon {

	public static class ScreenTest extends Screen implements EmulatorListener {

		LTexture texture;

		@Override
		public void draw(GLEx g) {
			g.draw(texture, 228, 128);
			g.setAlpha(1f);
			g.drawString("656565", 434, 432);

		}

		@Override
		public void onLoad() {
			// TODO Auto-generated method stub
			setBackground("back1.png");
			texture = loadTexture("ccc.png");
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

		@Override
		public void onUpClick() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onLeftClick() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRightClick() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onDownClick() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTriangleClick() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSquareClick() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCircleClick() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCancelClick() {
			// TODO Auto-generated method stub

		}

		@Override
		public void unUpClick() {
			// TODO Auto-generated method stub

		}

		@Override
		public void unLeftClick() {
			// TODO Auto-generated method stub

		}

		@Override
		public void unRightClick() {
			// TODO Auto-generated method stub

		}

		@Override
		public void unDownClick() {
			// TODO Auto-generated method stub

		}

		@Override
		public void unTriangleClick() {
			// TODO Auto-generated method stub

		}

		@Override
		public void unSquareClick() {
			// TODO Auto-generated method stub

		}

		@Override
		public void unCircleClick() {
			// TODO Auto-generated method stub

		}

		@Override
		public void unCancelClick() {
			// TODO Auto-generated method stub

		}

	}

	public static void main(String[] args) {
		JavaSESetting setting = new JavaSESetting();
		setting.width = 640;
		setting.height = 480;
		setting.width_zoom = 800;
		setting.height_zoom = 600;
		setting.fps = 60;
		setting.isDebug = true;
		setting.isDisplayLog = true;
		setting.fontName = "黑体";
		// setting.fullscreen = true;
		// setting.doubleBuffer = true;
		setting.graphicsMode = SPEED;
		setting.iconPaths = new String[] { "l.png" };
		register(Main.class, setting, () -> {
			return new ScreenTest();
		});
	}

}
