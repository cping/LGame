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
package loon.robovm;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import loon.Asyn;
import loon.LGame;
import loon.LSetting;
import loon.Support;
import loon.utils.reply.Act;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.glkit.GLKViewDrawableColorFormat;
import org.robovm.apple.glkit.GLKViewDrawableDepthFormat;
import org.robovm.apple.glkit.GLKViewDrawableMultisample;
import org.robovm.apple.glkit.GLKViewDrawableStencilFormat;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIDevice;

public class RoboVMGame extends LGame {

	public Act<RoboVMOrientEvent> orient = Act.create();

	final int osVersion = getOSVersion();
	final IOSSetting config;

	private boolean paused = false;
	private final long gameStart = System.nanoTime();
	private final ExecutorService pool = Executors.newFixedThreadPool(3);

	private final RoboVMLog log = new RoboVMLog();

	public static class IOSSetting extends LSetting {

		public float displayScaleLargeScreenIfRetina = 1.0f;

		public float displayScaleSmallScreenIfRetina = 1.0f;

		public float displayScaleLargeScreenIfNonRetina = 1.0f;

		public float displayScaleSmallScreenIfNonRetina = 1.0f;

		public boolean orientationPortrait = true;

		public boolean orientationLandscape = true;

		public GLKViewDrawableColorFormat colorFormat = GLKViewDrawableColorFormat.RGB565;

		public GLKViewDrawableDepthFormat depthFormat = GLKViewDrawableDepthFormat._16;

		public GLKViewDrawableStencilFormat stencilFormat = GLKViewDrawableStencilFormat.None;

		public GLKViewDrawableMultisample multisample = GLKViewDrawableMultisample.None;

		public boolean iPadLikePhone = false;

		public int frameInterval = 1;

		public boolean interpolateCanvasDrawing = true;

		public int openALSources = 24;

		public float timeForTermination = 0.5f;

		public GLKViewDrawableColorFormat glBufferFormat = GLKViewDrawableColorFormat.RGBA8888;

		public String storageFileName = "loon.db";
	}

	private final Asyn syn = new Asyn.Default(log, frame) {
		@Override
		public boolean isAsyncSupported() {
			return true;
		}

		@Override
		public void invokeAsync(Runnable action) {
			pool.execute(action);
		}
	};

	private final RoboVMAssets assets;
	private final RoboVMGraphics graphics;
	private final RoboVMInputMake input;
	private final RoboVMSave save;
	private final RoboVMAccelerometer accelerometer;
	private final RoboVMClipboard clipboard;

	protected RoboVMGame(Loon game, IOSSetting config, CGRect initBounds) {
		super(config, game);
		this.config = config;
		this.assets = new RoboVMAssets(this);
		this.graphics = new RoboVMGraphics(this, initBounds);
		this.input = new RoboVMInputMake(this);
		this.accelerometer = new RoboVMAccelerometer(this);
		this.save = new RoboVMSave(this);
		this.clipboard = new RoboVMClipboard();
		this.initProcess();
	}

	@Override
	public Type type() {
		return Type.IOS;
	}

	@Override
	public double time() {
		return System.currentTimeMillis();
	}

	@Override
	public int tick() {
		return (int) ((System.nanoTime() - gameStart) / 1000000);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void openURL(String url) {
		if (!UIApplication.getSharedApplication().openURL(new NSURL(url))) {
			log().warn("Failed to open URL: " + url);
		}
	}

	@Override
	public Asyn asyn() {
		return syn;
	}

	@Override
	public RoboVMAssets assets() {
		return assets;
	}

	@Override
	public RoboVMLog log() {
		return log;
	}

	@Override
	public RoboVMGraphics graphics() {
		return graphics;
	}

	@Override
	public RoboVMInputMake input() {
		return input;
	}

	@Override
	public RoboVMSave save() {
		return save;
	}

	@Override
	public RoboVMAccelerometer accel() {
		return accelerometer;
	}

	@Override
	public RoboVMClipboard clipboard() {
		return clipboard;
	}

	@Override
	public Support support() {
		return support();
	}

	private int getOSVersion() {
		String systemVersion = UIDevice.getCurrentDevice().getSystemVersion();
		int version = Integer.parseInt(systemVersion.split("\\.")[0]);
		return version;
	}

	void processFrame() {
		emitFrame();
	}

	void willEnterForeground() {
		if (!paused) {
			return;
		}
		paused = false;
		syn.invokeLater(new Runnable() {
			public void run() {
				status.emit(Status.RESUME);
			}
		});
	}

	void doEnterBackground() {
		if (paused) {
			return;
		}
		paused = true;
		status.emit(Status.PAUSE);
	}

	void willTerminate() {
		pool.shutdown();
		dispatchEvent(status, Status.EXIT);
	}

}
