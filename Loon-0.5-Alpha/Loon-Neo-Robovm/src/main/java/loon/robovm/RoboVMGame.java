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
import loon.Json;
import loon.LGame;
import loon.LSetting;
import loon.Log;
import loon.Save;
import loon.Support;
import loon.utils.json.JsonImpl;
import loon.utils.reply.Act;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.NSTimer;
import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.glkit.GLKViewDrawableColorFormat;
import org.robovm.apple.opengles.EAGLContext;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIDevice;
import org.robovm.apple.uikit.UIInterfaceOrientationMask;
import org.robovm.objc.block.VoidBlock1;

public class RoboVMGame extends LGame {

	public Act<RoboVMOrientEvent> orient = Act.create();

	final int osVersion = getOSVersion();
	final IOSSetting config;

	private boolean paused = false;
	private final long gameStart = System.nanoTime();
	private final ExecutorService pool = Executors.newFixedThreadPool(3);

	private final RoboVMLog log = new RoboVMLog();
	private final Json json = new JsonImpl();
	
	public static class IOSSetting extends LSetting {

		public UIInterfaceOrientationMask orients = UIInterfaceOrientationMask.Portrait;

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

	protected RoboVMGame(IOSSetting config, CGRect initBounds) {
		super(config);
		this.config = config;
		this.assets = new RoboVMAssets(this);
		this.graphics = new RoboVMGraphics(this, initBounds);
		this.input = new RoboVMInputMake(this);
		this.save = new RoboVMSave(this);
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

	@Override
	public void openURL(String url) {
		if (!UIApplication.getSharedApplication().openURL(new NSURL(url))) {
			log().warn("Failed to open URL: " + url);
		}
	}

	@Override
	public RoboVMAssets assets() {
		return assets;
	}

	@Override
	public Asyn asyn() {
		return syn;
	}

	@Override
	public Json json() {
		return json;
	}

	@Override
	public Log log() {
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
	public Save save() {
		return save;
	}

	void processFrame() {
		emitFrame();
	}

	void willEnterForeground() {
		if (!paused){
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
		if (paused){
			return;
		}
		paused = true;
		status.emit(Status.PAUSE);
	}

	void willTerminate() {
		new NSTimer(config.timeForTermination, new VoidBlock1<NSTimer>() {
			public void invoke(NSTimer timer) {
				EAGLContext.setCurrentContext(null);
				if (assets != null && assets._audio != null) {
					assets._audio.terminate();
				}
			}
		}, null, false);
		dispatchEvent(status, Status.EXIT);
	}

	private int getOSVersion() {
		String systemVersion = UIDevice.getCurrentDevice().getSystemVersion();
		int version = Integer.parseInt(systemVersion.split("\\.")[0]);
		return version;
	}

	@Override
	public Support support() {
		return support();
	}

}
