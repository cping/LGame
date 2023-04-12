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
package loon.se;

import java.awt.image.BufferedImage;

import loon.*;
import loon.canvas.Canvas;
import loon.geom.Dimension;
import loon.utils.Scale;

public class JavaSEGraphics extends JavaSEImplGraphics {

	private Dimension screenSize = new Dimension();

	protected JavaSECanvas canvas;

	protected JavaSEGraphics(JavaSEGame game) {
		this(game, true);
	}

	protected JavaSEGraphics(JavaSEGame game, boolean resized) {
		this(game, Scale.ONE, resized);
	}

	protected JavaSEGraphics(JavaSEGame game, Scale scale, boolean resized) {
		super(game, scale);
		if (game.setting instanceof JavaSESetting) {
			JavaSESetting setting = (JavaSESetting) game.setting;
			if (!setting.doubleBuffer) {
				setDPIScale(JavaSEApplication.dpiScale());
			}
		}
		this.createCanvas(game.setting, scale, resized);
	}

	protected Canvas createCanvas(LSetting setting, Scale scale, boolean resized) {
		if (canvas == null) {
			BufferedImage image = null;
			if (resized) {
				image = JavaSEImplGraphics.createBufferedImage(scale.scaledFloor(setting.getShowWidth()),
						scale.scaledFloor(setting.getShowHeight()), BufferedImage.TYPE_INT_ARGB_PRE);
			} else {
				image = JavaSEImplGraphics.createBufferedImage(scale.scaledFloor(setting.width),
						scale.scaledFloor(setting.height), BufferedImage.TYPE_INT_ARGB_PRE);
			}
			canvas = new JavaSECanvas(this, new JavaSEImage(this, image), true);
		}
		return canvas;
	}

	@Override
	public JavaSECanvas getCanvas() {
		return canvas;
	}

	public void onSizeChanged(int viewWidth, int viewHeight) {
		if (!isAllowResize(viewWidth, viewHeight)) {
			return;
		}
		screenSize.width = viewWidth / scale.factor;
		screenSize.height = viewHeight / scale.factor;
		viewportChanged(scale, viewWidth, viewHeight);
	}

	@Override
	public Dimension screenSize() {
		return this.screenSize;
	}

}
