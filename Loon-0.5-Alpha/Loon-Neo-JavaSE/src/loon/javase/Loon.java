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
package loon.javase;

import org.lwjgl.opengl.Display;

import loon.LSetting;
import loon.Screen;
import loon.jni.NativeSupport;

public class Loon extends JavaSEGame {

	public Loon(LSetting config) {
		super(config);
	}

	@Override
	public void setTitle(String title) {
		Display.setTitle(title);
	}

	public static void register(LSetting setting,
			Class<? extends Screen> clazz, Object... args) {
		Loon game = new Loon(setting);
		game.registerDisplay(clazz);
	}

	public loon.Display registerDisplay(Class<? extends Screen> clazz,
			Object... args) {
		loon.Display display = super.register(clazz, args);
		this.reset();
		return display;
	}

	public void reset() {
		boolean wasActive = Display.isActive();
		while (!Display.isCloseRequested()) {
			boolean newActive = Display.isActive();
			if (wasActive != newActive) {
				status.emit(wasActive ? Status.PAUSE : Status.RESUME);
				wasActive = newActive;
			}
			((JavaSELwjglGraphics) graphics()).checkScaleFactor();
			if (newActive || !setting.truePause) {
				processFrame();
			}
			Display.update();
			Display.sync(setting.fps);
		}
		shutdown();
	}

	@Override
	protected void preInit() {
		try {
			NativeSupport.loadJNI("lwjgl");
			NativeSupport.loadJNI("lplus");
		} catch (Throwable exc) {
			exc.printStackTrace();
		}
	}

	@Override
	protected JavaSEGraphics createGraphics() {
		return new JavaSELwjglGraphics(this);
	}

	@Override
	protected JavaSEInputMake createInput() {
		return new JavaSELwjglInputMake(this);
	}

}
