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

import loon.Graphics;
import loon.LTexture;
import loon.opengl.TextureSource;
import loon.utils.Scale;

import org.robovm.apple.coregraphics.CGBitmapContext;
import org.robovm.apple.coregraphics.CGImage;
import org.robovm.apple.coregraphics.CGInterpolationQuality;

public class RoboVMCanvasImage extends RoboVMImage {

	CGBitmapContext bctx;

	public RoboVMCanvasImage(Graphics gfx, Scale scale, int pixelWidth,
			int pixelHeight, boolean interpolate) {
		super(gfx, scale, pixelWidth, pixelHeight, TextureSource.RenderCanvas);
		bctx = RoboVMGraphics.createCGBitmap(pixelWidth, pixelHeight);
		if (!interpolate) {
			bctx.setInterpolationQuality(CGInterpolationQuality.None);
		}
	}

	@Override
	public CGImage cgImage() {
		return bctx.toImage();
	}

	@Override
	public void upload(Graphics gfx, LTexture tex) {
		upload(gfx, tex.getID(), pixelWidth, pixelHeight, bctx.getData());
	}

	@Override
	public void dispose() {
		super.dispose();
		if (bctx != null) {
			bctx.dispose();
			bctx = null;
		}
	}
}
