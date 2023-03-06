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

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

import loon.*;
import loon.canvas.Canvas;
import loon.font.Font;
import loon.font.TextFormat;
import loon.font.TextLayout;
import loon.font.TextWrap;
import loon.utils.Scale;

public abstract class JavaSEGraphics extends JavaSEImplGraphics {

	protected static final int[] STYLE_TO_JAVA = { java.awt.Font.PLAIN, java.awt.Font.BOLD, java.awt.Font.ITALIC,
			java.awt.Font.BOLD | java.awt.Font.ITALIC };

	private ByteBuffer imgBuffer = createImageBuffer(1024);
	private Map<String, java.awt.Font> fonts = new HashMap<String, java.awt.Font>();

	protected final JavaSEGame game;

	final FontRenderContext aaFontContext, aFontContext;

	protected JavaSEGraphics(JavaSEGame game, Scale scale) {
		super(game, scale);
		this.game = game;

		Graphics2D aaGfx = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).createGraphics();
		aaGfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		aaFontContext = aaGfx.getFontRenderContext();
		Graphics2D aGfx = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).createGraphics();
		aGfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		aFontContext = aGfx.getFontRenderContext();
	}

	@Override
	public void registerFont(String name, String path) {
		try {
			fonts.put(name, game.assets().requireResource(path).createFont());
		} catch (Exception e) {
			game.reportError("Failed to load font [name=" + name + ", path=" + path + "]", e);
		}
	}

	@Override
	public abstract void setSize(int width, int height, boolean fullscreen);

	@Override
	public TextLayout layoutText(String text, TextFormat format) {
		return JavaSETextLayout.layoutText(this, text, format);
	}

	@Override
	public TextLayout[] layoutText(String text, TextFormat format, TextWrap wrap) {
		return JavaSETextLayout.layoutText(this, text, format, wrap);
	}

	@Override
	protected Canvas createCanvasImpl(Scale scale, int pixelWidth, int pixelHeight) {
		BufferedImage bitmap = new BufferedImage(pixelWidth, pixelHeight, BufferedImage.TYPE_INT_ARGB_PRE);
		return new JavaSECanvas(this, new JavaSEImage(this, scale, bitmap, "<canvas>"));
	}

	@Override
	protected abstract void init();

	@Override
	protected abstract void upload(BufferedImage img, LTexture tex);

	@Override
	protected void updateViewport(Scale scale, float displayWidth, float displayHeight) {
		int viewWidth = scale.scaledCeil(displayWidth);
		int viewHeight = scale.scaledCeil(displayHeight);
		if (!isAllowResize(viewWidth, viewHeight)) {
			return;
		}
		viewportChanged(scale, viewWidth, viewHeight);
	}

	@Override
	java.awt.Font resolveFont(Font font) {
		java.awt.Font jfont = fonts.get(font.name);
		if (jfont == null) {
			fonts.put(font.name, jfont = new java.awt.Font(font.name, java.awt.Font.PLAIN, 12));
		}
		return jfont.deriveFont(STYLE_TO_JAVA[font.style.ordinal()], font.size);
	}

	static BufferedImage convertImage(BufferedImage image) {
		switch (image.getType()) {
		case BufferedImage.TYPE_INT_ARGB_PRE:
			return image;
		case BufferedImage.TYPE_4BYTE_ABGR:
			image.coerceData(true);
			return image;
		}
		BufferedImage convertedImage = new BufferedImage(image.getWidth(), image.getHeight(),
				BufferedImage.TYPE_INT_ARGB_PRE);
		Graphics2D g = convertedImage.createGraphics();
		g.setColor(new java.awt.Color(0f, 0f, 0f, 0f));
		g.fillRect(0, 0, image.getWidth(), image.getHeight());
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return convertedImage;
	}

	@Override
	ByteBuffer checkGetImageBuffer(int byteSize) {
		if (imgBuffer.capacity() >= byteSize) {
			imgBuffer.clear();
		} else {
			imgBuffer = createImageBuffer(byteSize);
		}
		return imgBuffer;
	}

	private static ByteBuffer createImageBuffer(int byteSize) {
		return ByteBuffer.allocateDirect(byteSize).order(ByteOrder.nativeOrder());
	}

}
