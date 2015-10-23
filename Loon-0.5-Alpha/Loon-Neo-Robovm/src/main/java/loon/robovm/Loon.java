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

import loon.LSetting;
import loon.Screen;
import loon.robovm.RoboVMGame.IOSSetting;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIApplicationDelegateAdapter;
import org.robovm.apple.uikit.UIApplicationLaunchOptions;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UIWindow;

public abstract class Loon extends UIApplicationDelegateAdapter {

	private RoboVMGame game = null;
	private LSetting setting;
	private Class<? extends Screen> mainClass;
	private Object[] parameters;

	@Override
	public boolean didFinishLaunching(UIApplication app,
			UIApplicationLaunchOptions launchOpts) {
		CGRect bounds = UIScreen.getMainScreen().getBounds();
		UIWindow window = new UIWindow(bounds);
		onMain();
		if (setting instanceof RoboVMGame.IOSSetting) {
			RoboVMViewController ctrl = new RoboVMViewController(
					window.getBounds(), (RoboVMGame.IOSSetting) setting);
			window.setRootViewController(ctrl);
			game = ctrl.game;
		} else {
			RoboVMGame.IOSSetting config = new IOSSetting();
			config.copy(setting);
			RoboVMViewController ctrl = new RoboVMViewController(
					window.getBounds(), config);
			window.setRootViewController(ctrl);
			game = ctrl.game;
			setting = config;
		}
		initialize();
		window.makeKeyAndVisible();
		addStrongRef(window);
		return true;
	}

	public abstract void onMain();

	protected RoboVMGame getGame() {
		return game;
	}

	protected RoboVMGame initialize() {
		game.register(mainClass, parameters);
		return game;
	}

	public void register(LSetting s, Class<? extends Screen> clazz,
			Object... pars) {
		this.setting = s;
		this.mainClass = clazz;
		this.parameters = pars;
	}

	/**
	 * 
	 * public static void main (String[] args) { NSAutoreleasePool pool = new
	 * NSAutoreleasePool(); UIApplication.main(args, null,
	 * YourRoboVMGame.class); pool.close(); }
	 */

}
