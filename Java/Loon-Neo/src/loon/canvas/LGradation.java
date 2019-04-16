/**
 * Copyright 2008 - 2011
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
 * @version 0.1
 */
package loon.canvas;

import loon.LRelease;
import loon.LSystem;
import loon.LTexture;
import loon.opengl.GLEx;
import loon.utils.IntMap;

/**
 * Color渲染Image的简单渐变实现
 */
public class LGradation implements LRelease {

	private static IntMap<LGradation> COLOR_GRADATIONS;

	private LColor startColor;

	private LColor endColor;

	private int width, height, alpha;

	private LTexture drawTexWidth, drawTexHeight;

	private Image drawImgWidth, drawImgHeight;

	public static LGradation getInstance(LColor s, LColor e, int w, int h) {
		return getInstance(s, e, w, h, 125);
	}

	public static LGradation getInstance(LColor s, LColor e, int w, int h, int alpha) {
		if (COLOR_GRADATIONS == null) {
			COLOR_GRADATIONS = new IntMap<LGradation>(10);
		}
		int hashCode = 1;
		hashCode = LSystem.unite(hashCode, s.getRGB());
		hashCode = LSystem.unite(hashCode, e.getRGB());
		hashCode = LSystem.unite(hashCode, w);
		hashCode = LSystem.unite(hashCode, h);
		hashCode = LSystem.unite(hashCode, alpha);
		LGradation o = COLOR_GRADATIONS.get(hashCode);
		if (o == null) {
			COLOR_GRADATIONS.put(hashCode, o = new LGradation(s, e, w, h, alpha));
		}
		return o;
	}

	private LGradation() {

	}

	private LGradation(LColor s, LColor e, int w, int h, int alpha) {
		this.startColor = s;
		this.endColor = e;
		this.width = w;
		this.height = h;
		this.alpha = alpha;
	}

	public void drawWidth(GLEx g, int x, int y) {
		try {
			if (drawTexWidth == null && !drawTexWidth.isClosed()) {
				Canvas gl = LSystem.base().graphics().createCanvas(width, height);
				for (int i = 0; i < width; i++) {
					gl.setColor((startColor.getRed() * (width - i)) / width + (endColor.getRed() * i) / width,
							(startColor.getGreen() * (width - i)) / width + (endColor.getGreen() * i) / width,
							(startColor.getBlue() * (width - i)) / width + (endColor.getBlue() * i) / width, alpha);
					gl.drawLine(i, 0, i, height);
				}
				drawTexWidth = gl.toTexture();
				if (gl.image != null) {
					gl.image.close();
				}
			}
			g.draw(drawTexWidth, x, y);
		} catch (Throwable ex) {
			for (int i = 0; i < width; i++) {
				g.setColor((startColor.getRed() * (width - i)) / width + (endColor.getRed() * i) / width,
						(startColor.getGreen() * (width - i)) / width + (endColor.getGreen() * i) / width,
						(startColor.getBlue() * (width - i)) / width + (endColor.getBlue() * i) / width, alpha);
				g.drawLine(i + x, y, i + x, y + height);
			}
		}
	}

	public void drawHeight(GLEx g, int x, int y) {
		try {
			if (drawTexHeight == null && !drawTexHeight.isClosed()) {
				Canvas gl = LSystem.base().graphics().createCanvas(width, height);
				for (int i = 0; i < height; i++) {
					gl.setColor((startColor.getRed() * (height - i)) / height + (endColor.getRed() * i) / height,
							(startColor.getGreen() * (height - i)) / height + (endColor.getGreen() * i) / height,
							(startColor.getBlue() * (height - i)) / height + (endColor.getBlue() * i) / height, alpha);
					gl.drawLine(0, i, width, i);
				}
				drawTexHeight = gl.toTexture();
				if (gl.image != null) {
					gl.image.close();
				}
			}
			g.draw(drawTexHeight, x, y);
		} catch (Throwable ex) {
			for (int i = 0; i < height; i++) {
				g.setColor((startColor.getRed() * (height - i)) / height + (endColor.getRed() * i) / height,
						(startColor.getGreen() * (height - i)) / height + (endColor.getGreen() * i) / height,
						(startColor.getBlue() * (height - i)) / height + (endColor.getBlue() * i) / height, alpha);
				g.drawLine(x, i + y, x + width, i + y);
			}
		}
	}

	public void drawWidth(Canvas g, int x, int y) {
		try {
			if (drawImgWidth == null && !drawImgWidth.isClosed()) {
				Canvas gl = LSystem.base().graphics().createCanvas(width, height);
				drawImgWidth = gl.image;
				for (int i = 0; i < width; i++) {
					gl.setColor((startColor.getRed() * (width - i)) / width + (endColor.getRed() * i) / width,
							(startColor.getGreen() * (width - i)) / width + (endColor.getGreen() * i) / width,
							(startColor.getBlue() * (width - i)) / width + (endColor.getBlue() * i) / width, alpha);
					gl.drawLine(i, 0, i, height);
				}
				gl.close();
				gl = null;
			}
			g.draw(drawImgWidth, x, y);
		} catch (Throwable e) {
			for (int i = 0; i < width; i++) {
				g.setColor((startColor.getRed() * (width - i)) / width + (endColor.getRed() * i) / width,
						(startColor.getGreen() * (width - i)) / width + (endColor.getGreen() * i) / width,
						(startColor.getBlue() * (width - i)) / width + (endColor.getBlue() * i) / width, alpha);
				g.drawLine(i + x, y, i + x, y + height);
			}
		}
	}

	public void drawHeight(Canvas g, int x, int y) {
		try {
			if (drawImgHeight == null && !drawImgHeight.isClosed()) {
				Canvas gl = LSystem.base().graphics().createCanvas(width, height);
				drawImgHeight = gl.image;
				for (int i = 0; i < height; i++) {
					gl.setColor((startColor.getRed() * (height - i)) / height + (endColor.getRed() * i) / height,
							(startColor.getGreen() * (height - i)) / height + (endColor.getGreen() * i) / height,
							(startColor.getBlue() * (height - i)) / height + (endColor.getBlue() * i) / height, alpha);
					gl.drawLine(0, i, width, i);
				}
				gl.close();
				gl = null;
			}
			g.draw(drawImgHeight, x, y);
		} catch (Throwable e) {
			for (int i = 0; i < height; i++) {
				g.setColor((startColor.getRed() * (height - i)) / height + (endColor.getRed() * i) / height,
						(startColor.getGreen() * (height - i)) / height + (endColor.getGreen() * i) / height,
						(startColor.getBlue() * (height - i)) / height + (endColor.getBlue() * i) / height, alpha);
				g.drawLine(x, i + y, x + width, i + y);
			}
		}
	}

	public void drawWidth(Pixmap g, int x, int y) {
		try {
			if (drawImgWidth == null && !drawImgWidth.isClosed()) {
				for (int i = 0; i < width; i++) {
					g.setColor((startColor.getRed() * (width - i)) / width + (endColor.getRed() * i) / width,
							(startColor.getGreen() * (width - i)) / width + (endColor.getGreen() * i) / width,
							(startColor.getBlue() * (width - i)) / width + (endColor.getBlue() * i) / width, alpha);
					g.drawLine(i, 0, i, height);
				}
				drawImgWidth = g.getImage();
			}
		} catch (Throwable e) {
			for (int i = 0; i < width; i++) {
				g.setColor((startColor.getRed() * (width - i)) / width + (endColor.getRed() * i) / width,
						(startColor.getGreen() * (width - i)) / width + (endColor.getGreen() * i) / width,
						(startColor.getBlue() * (width - i)) / width + (endColor.getBlue() * i) / width, alpha);
				g.drawLine(i + x, y, i + x, y + height);
			}
		}
	}

	public void drawHeight(Pixmap g, int x, int y) {
		try {
			if (drawImgHeight == null && !drawImgHeight.isClosed()) {
				for (int i = 0; i < height; i++) {
					g.setColor((startColor.getRed() * (height - i)) / height + (endColor.getRed() * i) / height,
							(startColor.getGreen() * (height - i)) / height + (endColor.getGreen() * i) / height,
							(startColor.getBlue() * (height - i)) / height + (endColor.getBlue() * i) / height, alpha);
					g.drawLine(0, i, width, i);
				}
				drawImgHeight = g.getImage();
			}
		} catch (Throwable e) {
			for (int i = 0; i < height; i++) {
				g.setColor((startColor.getRed() * (height - i)) / height + (endColor.getRed() * i) / height,
						(startColor.getGreen() * (height - i)) / height + (endColor.getGreen() * i) / height,
						(startColor.getBlue() * (height - i)) / height + (endColor.getBlue() * i) / height, alpha);
				g.drawLine(x, i + y, x + width, i + y);
			}
		}
	}

	public static void dispose() {
		if (COLOR_GRADATIONS == null) {
			return;
		}
		for (LGradation g : COLOR_GRADATIONS.values()) {
			if (g != null) {
				g.close();
			}
		}
		COLOR_GRADATIONS.clear();
	}

	@Override
	public void close() {
		if (drawTexWidth != null) {
			drawTexWidth.close();
		}
		if (drawTexHeight != null) {
			drawTexHeight.close();
		}
		if (drawImgWidth != null) {
			drawImgWidth.close();
		}
		if (drawImgWidth != null) {
			drawImgWidth.close();
		}
	}

}
