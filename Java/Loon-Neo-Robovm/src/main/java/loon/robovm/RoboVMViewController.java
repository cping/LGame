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

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.NSSet;
import org.robovm.apple.glkit.GLKView;
import org.robovm.apple.glkit.GLKViewController;
import org.robovm.apple.glkit.GLKViewControllerDelegate;
import org.robovm.apple.opengles.EAGLContext;
import org.robovm.apple.opengles.EAGLRenderingAPI;
import org.robovm.apple.uikit.UIEvent;
import org.robovm.apple.uikit.UIInterfaceOrientation;
import org.robovm.apple.uikit.UIInterfaceOrientationMask;
import org.robovm.apple.uikit.UITouch;
import org.robovm.objc.Selector;
import org.robovm.objc.annotation.BindSelector;
import org.robovm.objc.annotation.Method;
import org.robovm.rt.bro.annotation.Callback;

public class RoboVMViewController extends GLKViewController implements
		GLKViewControllerDelegate {

	private final GLKView view;

	public final RoboVMGame game;

	private final Loon plat;

	public RoboVMViewController(Loon base, CGRect bounds,
			RoboVMGame.IOSSetting config) {
		EAGLContext ctx = new EAGLContext(EAGLRenderingAPI.OpenGLES2);
		EAGLContext.setCurrentContext(ctx);
		plat = base;
		game = new RoboVMGame(base, config, bounds);
		view = new GLKView(bounds, ctx) {
			@Method(selector = "touchesBegan:withEvent:")
			public void touchesBegan(NSSet<UITouch> touches, UIEvent event) {
				game.input().onTouchesBegan(touches, event);
			}

			@Method(selector = "touchesCancelled:withEvent:")
			public void touchesCancelled(NSSet<UITouch> touches, UIEvent event) {
				game.input().onTouchesCancelled(touches, event);
			}

			@Method(selector = "touchesEnded:withEvent:")
			public void touchesEnded(NSSet<UITouch> touches, UIEvent event) {
				game.input().onTouchesEnded(touches, event);
			}

			@Method(selector = "touchesMoved:withEvent:")
			public void touchesMoved(NSSet<UITouch> touches, UIEvent event) {
				game.input().onTouchesMoved(touches, event);
			}
		};
		view.setDelegate(this);
		view.setDrawableColorFormat(game.config.glBufferFormat);
		view.setDrawableDepthFormat(config.depthFormat);
		view.setDrawableStencilFormat(config.stencilFormat);
		view.setDrawableMultisample(config.multisample);
		view.setMultipleTouchEnabled(true);

		setView(view);
		setDelegate(this);
		setPreferredFramesPerSecond(config.fps);
		addStrongRef(game);
	}

	@Override
	public void update(GLKViewController self) {
		game.processFrame();
	}

	@Override
	public void willPause(GLKViewController self, boolean paused) {
		if (paused) {
			game.doEnterBackground();
		} else {
			view.bindDrawable();
			game.willEnterForeground();
		}
	}

	@Override
	public void viewDidAppear(boolean animated) {
		super.viewDidAppear(animated);
		view.bindDrawable();
		game.graphics().viewDidInit(getView().getBounds());
	}

	@Override
	public void viewDidDisappear(boolean animated) {
		super.viewDidDisappear(animated);
		EAGLContext.setCurrentContext(null);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void willRotate(UIInterfaceOrientation toOrient, double duration) {
		super.willRotate(toOrient, duration);
		game.orient.emit(new RoboVMOrientEvent.WillRotate(toOrient, duration));
	}

	@SuppressWarnings("deprecation")
	@Override
	// from ViewController
	public void didRotate(UIInterfaceOrientation fromOrient) {
		super.didRotate(fromOrient);
		CGRect bounds = plat.getBounds(this);
		game.graphics().setSize(bounds);
		game.orient.emit(new RoboVMOrientEvent.DidRotate(fromOrient));
	}

	@Override
	public UIInterfaceOrientationMask getSupportedInterfaceOrientations() {
		long mask = 0;
		if (game.config.orientationLandscape) {
			mask |= ((1 << UIInterfaceOrientation.LandscapeLeft.value()) | (1 << UIInterfaceOrientation.LandscapeRight
					.value()));
		}
		if (game.config.orientationPortrait) {
			mask |= ((1 << UIInterfaceOrientation.Portrait.value()) | (1 << UIInterfaceOrientation.PortraitUpsideDown
					.value()));
		}
		return new UIInterfaceOrientationMask(mask);
	}

	@Override
	public boolean shouldAutorotate() {
		return true;
	}

	public boolean shouldAutorotateToInterfaceOrientation(
			UIInterfaceOrientation orientation) {
		switch (orientation) {
		case LandscapeLeft:
		case LandscapeRight:
			return game.config.orientationLandscape;
		default:
			return game.config.orientationPortrait;
		}
	}

	@Override
	protected void doDispose() {
		game.willTerminate();
		removeStrongRef(game);
		super.doDispose();
	}

	@Callback
	@BindSelector("shouldAutorotateToInterfaceOrientation:")
	private static boolean shouldAutorotateToInterfaceOrientation(
			RoboVMViewController self, Selector sel,
			UIInterfaceOrientation orientation) {
		return self.shouldAutorotateToInterfaceOrientation(orientation);
	}
}
