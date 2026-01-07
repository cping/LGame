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
package loon.cport;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import loon.Graphics;
import loon.LGame;
import loon.LTexture;
import loon.canvas.Canvas;
import loon.canvas.Pixmap;
import loon.font.TextFormat;
import loon.font.TextLayout;
import loon.font.TextWrap;
import loon.geom.Dimension;
import loon.opengl.GL20;
import loon.utils.GLUtils;
import loon.utils.MathUtils;
import loon.utils.Scale;

public class CGraphics extends Graphics {
	protected CGraphics(LGame game, GL20 gl, Scale scale) {
		super(game, gl, scale);
		// TODO Auto-generated constructor stub
	}

	public void registerFont(String name, String path) {
		try {
			CTextLayout.putSTBFont(name, game.assets().requireResource(path).createFont());
		} catch (Exception e) {
			game.reportError("Failed to load font [name=" + name + ", path=" + path + "]", e);
		}
	}

	@Override
	public Dimension screenSize() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TextLayout layoutText(String text, TextFormat format) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TextLayout[] layoutText(String text, TextFormat format, TextWrap wrap) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Canvas createCanvasImpl(Scale scale, int pixelWidth, int pixelHeight) {
		// TODO Auto-generated method stub
		return null;
	}

	void copyArea(Pixmap image, int x, int y, int width, int height, int dx, int dy) {
		Pixmap tmp = image.copy(x, y, width, height);
		image.drawPixmap(tmp, x + dx, y + dy);
		tmp.close();
		tmp = null;
	}

	protected void upload(Pixmap img, LTexture tex) {
		final int srcWidth = img.getWidth();
		final int srcHeight = img.getHeight();
		int texWidth = GLUtils.powerOfTwo(srcWidth);
		int texHeight = GLUtils.powerOfTwo(srcHeight);
		final int width = srcWidth;
		final int height = srcHeight;
		boolean hasAlpha = img.hasAlpha();
		int srcPixelFormat = hasAlpha ? GL20.GL_RGBA : GL20.GL_RGB;
		if (MathUtils.isPowerOfTwo(srcWidth) && MathUtils.isPowerOfTwo(srcHeight)) {
			texHeight = srcHeight;
			texWidth = srcWidth;
			GLUtils.bindTexture(gl, tex);
			gl.glTexImage2D(GL20.GL_TEXTURE_2D, 0, srcPixelFormat, texWidth, texHeight, 0, srcPixelFormat,
					GL20.GL_UNSIGNED_BYTE, img.convertPixmapToByteBuffer());
			gl.checkError("updateTexture");
			return;
		}
		
		Pixmap texImage = new Pixmap(texWidth, texHeight, hasAlpha);
		texImage.drawPixmap(img, 0, 0);
		if (height < texHeight - 1) {
			copyArea(texImage, 0, 0, width, 1, 0, texHeight - 1);
			copyArea(texImage, 0, height - 1, width, 1, 0, 1);
		}
		if (width < texWidth - 1) {
			copyArea(texImage, 0, 0, 1, height, texWidth - 1, 0);
			copyArea(texImage, width - 1, 0, 1, height, 1, 0);
		}
		ByteBuffer source = texImage.convertPixmapToByteBuffer();
		if (texImage != null) {
			texImage.close();
			texImage = null;
		}
		GLUtils.bindTexture(gl, tex);
		gl.glTexImage2D(GL20.GL_TEXTURE_2D, 0, srcPixelFormat, texWidth, texHeight, 0, srcPixelFormat,
				GL20.GL_UNSIGNED_BYTE, source);
		gl.checkError("updateTexture");
	}

}
