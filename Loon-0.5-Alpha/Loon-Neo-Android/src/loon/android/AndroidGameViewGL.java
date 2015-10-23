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
package loon.android;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import loon.LSystem;
import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

@SuppressLint("ClickableViewAccessibility")
public class AndroidGameViewGL extends GLSurfaceView {

	private final AndroidGame game;
	private AtomicBoolean started = new AtomicBoolean(false);
	private AtomicBoolean paused = new AtomicBoolean(true);

	public AndroidGameViewGL(Context context, AndroidGame g) {
		super(context);
		this.game = g;

		setFocusable(true);
		setEGLContextClientVersion(2);
		setPreserveEGLContextOnPause(true);

		setRenderer(new Renderer() {
			@Override
			public void onSurfaceCreated(GL10 gl, EGLConfig config) {
				game.graphics().onSurfaceCreated();
			}

			@Override
			public void onSurfaceChanged(GL10 gl, int width, int height) {
				game.graphics().onSizeChanged(width, height);
				if (!started.get()) {
					startGame();
				}
			}

			@Override
			public void onDrawFrame(GL10 gl) {
				if (!paused.get()) {
					game.processFrame();
				}
			}
		});
		setRenderMode(RENDERMODE_CONTINUOUSLY);
	}

	@Override
	public void onPause() {
		paused.set(LSystem.PAUSED = true);
		queueEvent(new Runnable() {
			public void run() {
				game.graphics().onSurfaceLost();
			}
		});
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		paused.set(LSystem.PAUSED = false);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return game.input().onTouch(event);
	}

	void startGame() {
		started.set(true);
		queueEvent(new Runnable() {
			@Override
			public void run() {
				game.activity.onMain();
				game.activity.initialize();
				paused.set(LSystem.PAUSED = false);
			}
		});
	}
}
