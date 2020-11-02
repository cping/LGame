package org.loon.framework.android.game.utils;

import org.loon.framework.android.game.core.LHandler;
import org.loon.framework.android.game.core.LSystem;
import org.loon.framework.android.game.core.graphics.LImage;

import android.graphics.Bitmap;

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
 * @email ceponline ceponline@yahoo.com.cn
 * @version 0.1
 */
public class ScreenUtils {

	public static LImage toScreenCaptureImage() {
		LHandler handler = LSystem.getSystemHandler();
		if (handler != null) {
			Bitmap tmp = handler.getImage();
			if (tmp == null) {
				return null;
			}
			int width = tmp.getWidth();
			int height = tmp.getHeight();
			LImage image = new LImage(width, height, tmp.getConfig());
			int pixels[] = GraphicsUtils.getPixels(tmp);
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
