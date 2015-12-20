package org.loon.framework.javase.game.utils;

import java.awt.DisplayMode;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.image.PixelGrabber;

import org.loon.framework.javase.game.core.LHandler;
import org.loon.framework.javase.game.core.LSystem;
import org.loon.framework.javase.game.core.graphics.LImage;

/**
 * Copyright 2008 - 2010
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
 * @project loonframework
 * @author chenpeng
 * @email��ceponline ceponline@yahoo.com.cn
 * @version 0.1
 */
public class ScreenUtils {

	final public static GraphicsEnvironment environment = GraphicsEnvironment
			.getLocalGraphicsEnvironment();

	final public static GraphicsDevice graphicsDevice = environment
			.getDefaultScreenDevice();

	final public static GraphicsConfiguration graphicsConfiguration = graphicsDevice
			.getDefaultConfiguration();

	/**
	 * 查询可用的屏幕设备
	 * 
	 * @param width
	 * @param height
	 * @return
	 */
	public final static DisplayMode searchFullScreenModeDisplay(int width,
			int height) {
		return searchFullScreenModeDisplay(graphicsDevice, width, height);
	}

	/**
	 * 查询可用的屏幕设备
	 * 
	 * @param device
	 * @param width
	 * @param height
	 * @return
	 */
	public final static DisplayMode searchFullScreenModeDisplay(
			GraphicsDevice device, int width, int height) {
		DisplayMode displayModes[] = device.getDisplayModes();
		int currentDisplayPoint = 0;
		DisplayMode fullScreenMode = null;
		DisplayMode normalMode = device.getDisplayMode();
		DisplayMode adisplaymode[] = displayModes;
		int i = 0, length = adisplaymode.length;
		for (int j = length; i < j; i++) {
			DisplayMode mode = adisplaymode[i];
			if (mode.getWidth() == width && mode.getHeight() == height) {
				int point = 0;
				if (normalMode.getBitDepth() == mode.getBitDepth()) {
					point += 40;
				} else {
					point += mode.getBitDepth();
				}
				if (normalMode.getRefreshRate() == mode.getRefreshRate()) {
					point += 5;
				}
				if (currentDisplayPoint < point) {
					fullScreenMode = mode;
					currentDisplayPoint = point;
				}
			}
		}
		return fullScreenMode;
	}

	public static LImage toScreenCaptureImage() {
		LHandler handler = LSystem.getSystemHandler();
		if (handler != null) {
			Image tmp = handler.getImage();
			int width = tmp.getWidth(null);
			int height = tmp.getHeight(null);
			LImage image = new LImage(width, height);
			PixelGrabber pg = new PixelGrabber(tmp, 0, 0, width, height, true);
			try {
				pg.grabPixels();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			int pixels[] = (int[]) pg.getPixels();
			image.setPixels(pixels, width, height);
			if (width != LSystem.screenRect.width
					|| height != LSystem.screenRect.height) {
				LImage temp = image.scaledInstance(LSystem.screenRect.width,
						LSystem.screenRect.height);
				if (image != null) {
					image.dispose();
					image = null;
				}
				return temp;
			}
			return image;
		}
		return null;
	}

}
