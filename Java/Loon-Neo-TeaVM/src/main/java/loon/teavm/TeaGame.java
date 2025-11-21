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

import loon.Accelerometer;
import loon.Assets;
import loon.Asyn;
import loon.Clipboard;
import loon.Graphics;
import loon.LGame;
import loon.LSetting;
import loon.Log;
import loon.Platform;
import loon.Save;
import loon.Support;
import loon.events.InputMake;

public class TeaGame extends LGame {
	public TeaGame(LSetting config, Platform plat) {
		super(config, plat);
		// TODO Auto-generated constructor stub
	}

	private static final int MIN_DELAY = 5;

	/**
	 * 由于手机版的浏览器对webgl支持实在各种奇葩，不同手机环境差异实在惊人，干脆把常用的刷新方式都写出来，用户自己选……
	 */
	public static enum Repaint {
		// RequestAnimationFrame效率最高
		// Schedule在某些情况下更适用（有间断时）
		// AnimationScheduler本质是前两者的api混合，会等待canvas渲染后刷新，虽然效率最低，但是最稳，不容易造成webgl卡死现象
		// 为了稳定考虑，所以默认用这个.
		RequestAnimationFrame, Schedule, AnimationScheduler;
	}

	public static class TeaSetting extends LSetting {

		public String imageName = "img";

		public String canvasName = "canvas";

		public String canvasMethod = "2d";

		public Repaint repaint = Repaint.AnimationScheduler;

		// 是否支持使用flash加载资源（如果要做成静态文件包，涉及跨域问题(也就是非服务器端运行时)，所以需要禁止此项）
		public boolean preferFlash = false;

		// 当前浏览器的渲染模式
		public Mode mode = Mode.AUTODETECT;

		// 当此项存在时，会尝试加载内部资源
		// public LocalAssetResources internalRes = null;

		// 当此项存在时，同样会尝试加载内部资源
		public boolean jsloadRes = false;

		public boolean transparentCanvas = false;

		public boolean antiAliasing = true;

		public boolean stencil = false;

		public boolean premultipliedAlpha = false;

		public boolean preserveDrawingBuffer = false;

		// 如果此项开启，按照屏幕大小等比缩放
		public boolean useRatioScaleFactor = false;

		// 需要绑定的层id
		public String rootId = "loon-root";

		// 初始化时的进度条样式（不实现则默认加载）
		// public TeaProgress progress = null;

		// 如果此项为true,则仅以异步加载资源
		public boolean asynResource = false;
		public TeaWindowListener windowListener;
	}

	public static enum Mode {
		WEBGL, CANVAS, AUTODETECT;
	}

	public TeaSetting getSetting() {
		return null;
	}

	@Override
	public Type type() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double time() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int tick() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void openURL(String url) {
		// TODO Auto-generated method stub

	}

	@Override
	public Assets assets() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Asyn asyn() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Graphics graphics() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputMake input() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Clipboard clipboard() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Log log() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Save save() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Accelerometer accel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Support support() {
		// TODO Auto-generated method stub
		return null;
	}

}
