/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
package loon;

import loon.utils.NumberUtils;

public class LSetting {

	// 是否显示FPS帧率
	public boolean isFPS = false;

	// 是否显示logo（替换logo使用logoPath指定地址）
	public boolean isLogo = false;

	// 帧率
	public int fps = 60;

	// 游戏画面实际宽度
	public int width = 480;

	// 游戏画面实际高度
	public int height = 320;

	// 游戏画面缩放大小（不需要则维持在-1即可）
	public int width_zoom = -1;

	public int height_zoom = -1;

	// 是否全屏
	public boolean fullscreen = false;

	// 是否使用虚拟触屏按钮
	public boolean emulateTouch = false;

	public int activationKey = -1;

	public boolean convertImagesOnLoad = true;

	public String appName = LSystem.APP_NAME;

	public String logoPath = "loon_logo.png";

	public String fontName = LSystem.FONT_NAME;
	
	// 当前游戏类型，默认为"未知"
	public GameType gameType = GameType.UNKOWN;

	public void copy(LSetting setting) {
		this.isFPS = setting.isFPS;
		this.isLogo = setting.isLogo;
		this.fps = setting.fps;
		this.width = setting.width;
		this.height = setting.height;
		this.width_zoom = setting.width_zoom;
		this.height_zoom = setting.height_zoom;
		this.fullscreen = setting.fullscreen;
		this.emulateTouch = setting.emulateTouch;
		this.activationKey = setting.activationKey;
		this.convertImagesOnLoad = setting.convertImagesOnLoad;
		this.appName = setting.appName;
		this.logoPath = setting.logoPath;
		this.fontName = setting.fontName;
	}

	public boolean landscape() {
		return this.height < this.width;
	}

	public void updateScale() {
		if (scaling()) {
			LSystem.setScaleWidth((float) width_zoom / (float) width);
			LSystem.setScaleHeight((float) height_zoom / (float) height);
			LSystem.viewSize.setSize(width, height);
		}
	}

	public boolean scaling() {
		return this.width_zoom > 0
				&& this.height_zoom > 0
				&& (this.width_zoom != this.width || this.height_zoom != this.height);
	}

	public int getShowWidth() {
		return this.width_zoom > 0 ? this.width_zoom : this.width;
	}

	public int getShowHeight() {
		return this.height_zoom > 0 ? this.height_zoom : this.height;
	}

	/**
	 * 判断设备是否宽屏
	 * 
	 * @return
	 */
	public boolean wideScreen() {
		return NumberUtils.compare(getShowWidth() / getShowHeight(), 1.777777f) == 0;
	}

}
