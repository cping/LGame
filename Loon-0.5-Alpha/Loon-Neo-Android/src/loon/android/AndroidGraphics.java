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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import loon.Graphics;
import loon.canvas.Canvas;
import loon.font.Font;
import loon.font.TextFormat;
import loon.font.TextLayout;
import loon.font.TextWrap;
import loon.geom.Dimension;
import loon.geom.Vector2f;
import loon.utils.Scale;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Pair;

public class AndroidGraphics extends Graphics {

	public interface Refreshable {

		void onSurfaceLost();

		void onSurfaceCreated();
	}

	private final AndroidGame game;
	private final Vector2f touchTemp = new Vector2f();

	private Map<Refreshable, Void> refreshables = Collections
			.synchronizedMap(new WeakHashMap<Refreshable, Void>());

	private final Map<Pair<String, Font.Style>, Typeface> fonts = new HashMap<Pair<String, Font.Style>, Typeface>();
	private final Map<Pair<String, Font.Style>, String[]> ligatureHacks = new HashMap<Pair<String, Font.Style>, String[]>();

	private Dimension screenSize = new Dimension();
	private ScaleFunc canvasScaleFunc = new ScaleFunc() {
		public Scale computeScale(float width, float height, Scale gfxScale) {
			return gfxScale;
		}
	};

	final Bitmap.Config preferredBitmapConfig;

	public AndroidGraphics(AndroidGame game, Bitmap.Config bitmapConfig) {
		super(game, new AndroidGL20(), new Scale(game.activity.scaleFactor()));
		this.game = game;
		this.preferredBitmapConfig = bitmapConfig;
	}

	void onSizeChanged(int viewWidth, int viewHeight) {
		screenSize.width = viewWidth / scale.factor;
		screenSize.height = viewHeight / scale.factor;
		game.log().info(
				"Updating size " + viewWidth + "x" + viewHeight + " / "
						+ scale.factor + " -> " + screenSize);
		viewportChanged(scale, viewWidth, viewHeight);
	}

	public void registerFont(String path, String name, Font.Style style,
			String... ligatureGlyphs) {
		try {
			registerFont(game.assets().getTypeface(path), name, style,
					ligatureGlyphs);
		} catch (Exception e) {
			game.reportError("Failed to load font [name=" + name + ", path="
					+ path + "]", e);
		}
	}

	public void registerFont(Typeface face, String name, Font.Style style,
			String... ligatureGlyphs) {
		Pair<String, Font.Style> key = Pair.create(name, style);
		fonts.put(key, face);
		ligatureHacks.put(key, ligatureGlyphs);
	}

	public void setCanvasFilterBitmaps(boolean filterBitmaps) {
		if (filterBitmaps){
			AndroidCanvasState.PAINT_FLAGS |= Paint.FILTER_BITMAP_FLAG;
		}
		else{
			AndroidCanvasState.PAINT_FLAGS &= ~Paint.FILTER_BITMAP_FLAG;
		}
	}

	public interface ScaleFunc {

		Scale computeScale(float width, float height, Scale gfxScale);
	}

	public void setCanvasScaleFunc(ScaleFunc scaleFunc) {
		if (scaleFunc == null){
			throw new NullPointerException("Scale func have not null");
		}
		canvasScaleFunc = scaleFunc;
	}

	@Override
	public Dimension screenSize() {
		return screenSize;
	}

	@Override
	public Canvas createCanvas(float width, float height) {
		Scale scale = canvasScaleFunc.computeScale(width, height, this.scale);
		return createCanvasImpl(scale, scale.scaledCeil(width),
				scale.scaledCeil(height));
	}

	@Override
	public TextLayout layoutText(String text, TextFormat format) {
		return AndroidTextLayout.layoutText(this, text, format);
	}

	@Override
	public TextLayout[] layoutText(String text, TextFormat format, TextWrap wrap) {
		return AndroidTextLayout.layoutText(this, text, format, wrap);
	}

	@Override
	protected Canvas createCanvasImpl(Scale scale, int pixelWidth,
			int pixelHeight) {
		Bitmap bitmap = Bitmap.createBitmap(pixelWidth, pixelHeight,
				preferredBitmapConfig);
		return new AndroidCanvas(this, new AndroidImage(this, scale, bitmap,
				"<canvas>"));
	}

	AndroidFont resolveFont(Font font) {
		if (font == null) {
			return AndroidFont.DEFAULT;
		}
		Pair<String, Font.Style> key = Pair.create(font.name, font.style);
		Typeface face = fonts.get(key);
		if (face == null) {
			fonts.put(key, face = AndroidFont.create(font));
		}
		return new AndroidFont(face, font.size, ligatureHacks.get(key));
	}

	void onSurfaceCreated() {
		for (Refreshable ref : refreshables.keySet()) {
			ref.onSurfaceCreated();
		}
	}

	void onSurfaceLost() {
		for (Refreshable ref : refreshables.keySet()) {
			ref.onSurfaceLost();
		}
	}

	void addRefreshable(Refreshable ref) {
		assert ref != null;
		refreshables.put(ref, null);
	}

	void removeRefreshable(Refreshable ref) {
		assert ref != null;
		refreshables.remove(ref);
	}

	Vector2f transformTouch(float x, float y) {
		return touchTemp.set(x / scale.factor, y / scale.factor);
	}
}
