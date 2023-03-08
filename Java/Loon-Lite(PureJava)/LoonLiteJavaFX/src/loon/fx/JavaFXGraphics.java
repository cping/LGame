/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
package loon.fx;

import javafx.scene.image.WritableImage;
import loon.Graphics;
import loon.LSetting;
import loon.canvas.Canvas;
import loon.font.TextFormat;
import loon.font.TextLayout;
import loon.font.TextWrap;
import loon.geom.Dimension;
import loon.utils.Scale;

public class JavaFXGraphics extends Graphics {

	private final JavaFXGame game;

	private Dimension screenSize = new Dimension();

	protected JavaFXCanvas canvas;

	protected JavaFXGraphics(JavaFXGame game) {
		this(game, true);
	}
	
	protected JavaFXGraphics(JavaFXGame game, boolean resized) {
		this(game, Scale.ONE, resized);
	}

	protected JavaFXGraphics(JavaFXGame game, Scale scale, boolean resized) {
		super(game, scale);
		this.game = game;
		this.createCanvas(game.setting, scale, resized);
	}

	protected Canvas createCanvas(LSetting setting, Scale scale, boolean resized) {
		if (canvas == null) {
			JavaFXImage image = null;
			if (resized) {
				image = new JavaFXImage(this.game, scale.scaledFloor(setting.getShowWidth()),
						scale.scaledFloor(setting.getShowHeight()));
			} else {
				image = new JavaFXImage(this.game, scale.scaledFloor(setting.width), scale.scaledFloor(setting.height));
			}
			canvas = new JavaFXCanvas(this, image);
		}
		return canvas;
	}

	@Override
	public Canvas getCanvas() {
		return canvas;
	}

	void onSizeChanged(int viewWidth, int viewHeight) {
		if (!isAllowResize(viewWidth, viewHeight)) {
			return;
		}
		screenSize.width = viewWidth / scale.factor;
		screenSize.height = viewHeight / scale.factor;
		game.log().info("Updating size " + viewWidth + "x" + viewHeight + " / " + scale.factor + " -> " + screenSize);
		viewportChanged(scale, viewWidth, viewHeight);
	}

	@Override
	public Dimension screenSize() {
		return this.screenSize;
	}

	@Override
	public TextLayout layoutText(String text, TextFormat format) {
		return JavaFXTextLayout.layoutText(this, text, format);
	}

	@Override
	public TextLayout[] layoutText(String text, TextFormat format, TextWrap wrap) {
		return JavaFXTextLayout.layoutText(this, text, format, wrap);
	}

	@Override
	protected Canvas createCanvasImpl(Scale scale, int pixelWidth, int pixelHeight) {
		WritableImage bitmap = new WritableImage(pixelWidth, pixelHeight);
		JavaFXImage image = new JavaFXImage(this, scale, bitmap, "<canvas>");
		return new JavaFXCanvas(this, image);
	}

}
