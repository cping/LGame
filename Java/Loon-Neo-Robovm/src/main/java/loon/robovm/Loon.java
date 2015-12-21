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

import loon.LGame;
import loon.LSetting;
import loon.LazyLoading;
import loon.Platform;
import loon.event.KeyMake;
import loon.event.SysInput;
import loon.robovm.RoboVMGame.IOSSetting;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.NSThread;
import org.robovm.apple.uikit.UIAlertView;
import org.robovm.apple.uikit.UIAlertViewDelegateAdapter;
import org.robovm.apple.uikit.UIAlertViewStyle;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIApplicationDelegateAdapter;
import org.robovm.apple.uikit.UIApplicationLaunchOptions;
import org.robovm.apple.uikit.UIDevice;
import org.robovm.apple.uikit.UIInterfaceOrientation;
import org.robovm.apple.uikit.UIKeyboardType;
import org.robovm.apple.uikit.UIReturnKeyType;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UITextAutocapitalizationType;
import org.robovm.apple.uikit.UITextAutocorrectionType;
import org.robovm.apple.uikit.UITextField;
import org.robovm.apple.uikit.UIUserInterfaceIdiom;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.apple.uikit.UIWindow;

@SuppressWarnings("deprecation")
public abstract class Loon extends UIApplicationDelegateAdapter implements
		Platform, LazyLoading {

	private RoboVMGame game = null;
	private LSetting setting;
	private LazyLoading.Data mainData;
	private float displayScaleFactor;
	private UIWindow uiWindow;
	private UIApplication uiApp;

	@Override
	public boolean didFinishLaunching(UIApplication app,
			UIApplicationLaunchOptions launchOpts) {
		CGRect bounds = UIScreen.getMainScreen().getBounds();
		uiApp = app;
		uiWindow = new UIWindow(bounds);
		onMain();
		if (setting instanceof RoboVMGame.IOSSetting) {
			IOSSetting config = (IOSSetting) setting;
			float scale = (float) (getIosVersion() >= 8 ? UIScreen
					.getMainScreen().getNativeScale() : UIScreen
					.getMainScreen().getScale());
			if (scale >= 2.0f) {
				if (UIDevice.getCurrentDevice().getUserInterfaceIdiom() == UIUserInterfaceIdiom.Pad) {
					displayScaleFactor = config.displayScaleLargeScreenIfRetina
							* scale;
				} else {
					displayScaleFactor = config.displayScaleSmallScreenIfRetina
							* scale;
				}
			} else {
				if (UIDevice.getCurrentDevice().getUserInterfaceIdiom() == UIUserInterfaceIdiom.Pad) {
					displayScaleFactor = config.displayScaleLargeScreenIfNonRetina;
				} else {
					displayScaleFactor = config.displayScaleSmallScreenIfNonRetina;
				}
			}
			RoboVMViewController ctrl = new RoboVMViewController(this,
					uiWindow.getBounds(), (RoboVMGame.IOSSetting) setting);
			uiWindow.setRootViewController(ctrl);
			game = ctrl.game;
		} else {
			RoboVMGame.IOSSetting config = new IOSSetting();
			config.copy(setting);
			RoboVMViewController ctrl = new RoboVMViewController(this,
					uiWindow.getBounds(), config);
			uiWindow.setRootViewController(ctrl);
			game = ctrl.game;
			setting = config;
		}
		initialize();
		uiWindow.makeKeyAndVisible();
		addStrongRef(uiWindow);
		return true;
	}

	CGRect getBounds(UIViewController viewController) {
		CGRect bounds = UIScreen.getMainScreen().getBounds();
		UIInterfaceOrientation orientation = null;
		if (setting != null && setting instanceof RoboVMGame.IOSSetting) {
			IOSSetting config = (IOSSetting) setting;
			if (viewController != null) {
				orientation = viewController.getInterfaceOrientation();
			} else if (config.orientationLandscape == config.orientationPortrait) {
				orientation = uiApp.getStatusBarOrientation();
			} else if (config.orientationLandscape) {
				orientation = UIInterfaceOrientation.LandscapeRight;
			} else {
				orientation = UIInterfaceOrientation.Portrait;
			}
		} else {
			if (viewController != null) {
				orientation = viewController.getInterfaceOrientation();
			} else {
				orientation = UIInterfaceOrientation.LandscapeLeft;
			}
		}
		int width;
		int height;
		switch (orientation) {
		case LandscapeLeft:
		case LandscapeRight:
			height = (int) bounds.getWidth();
			width = (int) bounds.getHeight();
			if (width < height) {
				width = (int) bounds.getWidth();
				height = (int) bounds.getHeight();
			}
			break;
		default:
			width = (int) bounds.getWidth();
			height = (int) bounds.getHeight();
		}
		width *= displayScaleFactor;
		height *= displayScaleFactor;
		return new CGRect(0, 0, width, height);
	}

	public abstract void onMain();

	public LGame getGame() {
		return game;
	}

	protected RoboVMGame initialize() {
		if (game != null) {
			game.register(mainData.onScreen());
		}
		return game;
	}

	public void register(LSetting s, LazyLoading.Data data) {
		this.setting = s;
		this.mainData = data;
	}

	public void close() {
		NSThread.exit();
	}

	@Override
	public int getContainerWidth() {
		return (int) UIScreen.getMainScreen().getBounds().getWidth();
	}

	@Override
	public int getContainerHeight() {
		return (int) UIScreen.getMainScreen().getBounds().getHeight();
	}

	@Override
	public Orientation getOrientation() {
		if (getContainerHeight() > getContainerWidth()) {
			return Orientation.Portrait;
		} else {
			return Orientation.Landscape;
		}
	}

	private int getIosVersion() {
		String systemVersion = UIDevice.getCurrentDevice().getSystemVersion();
		int version = Integer.parseInt(systemVersion.split("\\.")[0]);
		return version;
	}

	public UIWindow getUIWindow() {
		return uiWindow;
	}

	public UIApplication getUIApp() {
		return uiApp;
	}

	@Override
	public void sysText(final SysInput.TextEvent event,
			KeyMake.TextType textType, String label, String initVal) {
		if (game == null) {
			event.cancel();
			return;
		}
		UIAlertView view = new UIAlertView();
		if (label != null) {
			view.setTitle(label);
		}
		view.addButton("Cancel");
		view.addButton("OK");
		view.setAlertViewStyle(UIAlertViewStyle.PlainTextInput);

		final UITextField field = view.getTextField(0);
		field.setReturnKeyType(UIReturnKeyType.Done);
		if (initVal != null) {
			field.setText(initVal);
		}

		switch (textType) {
		case NUMBER:
			field.setKeyboardType(UIKeyboardType.NumberPad);
			break;
		case EMAIL:
			field.setKeyboardType(UIKeyboardType.EmailAddress);
			break;
		case URL:
			field.setKeyboardType(UIKeyboardType.URL);
			break;
		case DEFAULT:
			field.setKeyboardType(UIKeyboardType.Default);
			break;
		}
		field.setAutocorrectionType(UITextAutocorrectionType.Yes);
		field.setAutocapitalizationType(UITextAutocapitalizationType.Sentences);
		field.setSecureTextEntry(false);
		view.setDelegate(new UIAlertViewDelegateAdapter() {
			public void clicked(UIAlertView view, long buttonIndex) {
				if (buttonIndex == 0) {
					event.cancel();
				} else {
					event.input(field.getText());
				}
			}
		});
		view.show();
	}

	@Override
	public void sysDialog(final SysInput.ClickEvent event,String title, String text, String ok, String cancel) {
		if (game == null) {
			event.cancel();
			return;
		}
		UIAlertView view = new UIAlertView();
		view.setTitle(title);
		view.setMessage(text);
		if (cancel != null) {
			view.addButton(cancel);
		}
		view.addButton(ok);
		view.setAlertViewStyle(UIAlertViewStyle.Default);
		view.setDelegate(new UIAlertViewDelegateAdapter() {
			public void clicked(UIAlertView view, long buttonIndex) {
				if(buttonIndex == 1){
					event.clicked();
				}else{
					event.cancel();
				}
			}
		});
		view.show();
	}

	/**
	 * 
	 * public static void main (String[] args) { NSAutoreleasePool pool = new
	 * NSAutoreleasePool(); UIApplication.main(args, null,
	 * YourRoboVMGame.class); pool.close(); }
	 */

}
