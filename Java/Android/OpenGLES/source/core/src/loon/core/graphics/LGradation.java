package loon.core.graphics;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import loon.core.LRelease;
import loon.core.LSystem;
import loon.core.graphics.device.LGraphics;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.GLLoader;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTexture.Format;


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
public class LGradation implements LRelease {

	private static HashMap<String, LGradation> gradations;

	private LColor start;

	private LColor end;

	private int width, height, alpha;

	private LTexture drawTexWidth, drawTexHeight;

	private LImage drawImgWidth, drawImgHeight;


	public static LGradation getInstance(LColor s, LColor e, int w, int h) {
		return getInstance(s, e, w, h, 125);
	}

	public static LGradation getInstance(LColor s, LColor e, int w, int h,
			int alpha) {
		if (gradations == null) {
			gradations = new HashMap<String, LGradation>(10);
		}
		int hashCode = 1;
		hashCode = LSystem.unite(hashCode, s.getRGB());
		hashCode = LSystem.unite(hashCode, e.getRGB());
		hashCode = LSystem.unite(hashCode, w);
		hashCode = LSystem.unite(hashCode, h);
		hashCode = LSystem.unite(hashCode, alpha);
		String key = String.valueOf(hashCode);
		LGradation o = gradations.get(key);
		if (o == null) {
			gradations.put(key, o = new LGradation(s, e, w, h, alpha));
		}
		return o;
	}

	private LGradation() {

	}

	private LGradation(LColor s, LColor e, int w, int h, int alpha) {
		this.start = s;
		this.end = e;
		this.width = w;
		this.height = h;
		this.alpha = alpha;
	}

	public synchronized void drawWidth(GLEx g, int x, int y) {
		try {
			if (drawTexWidth == null) {
				LImage img = new LImage(width, height, true);
				LGraphics gl = img.getLGraphics();
				for (int i = 0; i < width; i++) {
					gl.setColor(
							(start.getRed() * (width - i)) / width
									+ (end.getRed() * i) / width,
							(start.getGreen() * (width - i)) / width
									+ (end.getGreen() * i) / width,
							(start.getBlue() * (width - i)) / width
									+ (end.getBlue() * i) / width, alpha);
					gl.drawLine(i, 0, i, height);
				}
				drawTexWidth = new LTexture(GLLoader.getTextureData(img),
						Format.SPEED);
				gl.dispose();
				gl = null;
			}
			g.drawTexture(drawTexWidth, x, y);
		} catch (Exception ex) {
			for (int i = 0; i < width; i++) {
				g.setColorValue(
						(start.getRed() * (width - i)) / width
								+ (end.getRed() * i) / width,
						(start.getGreen() * (width - i)) / width
								+ (end.getGreen() * i) / width,
						(start.getBlue() * (width - i)) / width
								+ (end.getBlue() * i) / width, alpha);
				g.drawLine(i + x, y, i + x, y + height);
			}
		}
	}

	public synchronized void drawHeight(GLEx g, int x, int y) {
		try {
			if (drawTexHeight == null) {
				LImage img = new LImage(width, height, true);
				LGraphics gl = img.getLGraphics();
				for (int i = 0; i < height; i++) {
					gl.setColor(
							(start.getRed() * (height - i)) / height
									+ (end.getRed() * i) / height,
							(start.getGreen() * (height - i)) / height
									+ (end.getGreen() * i) / height,
							(start.getBlue() * (height - i)) / height
									+ (end.getBlue() * i) / height, alpha);
					gl.drawLine(0, i, width, i);
				}
				drawTexHeight = new LTexture(GLLoader.getTextureData(img),
						Format.SPEED);
				gl.dispose();
				gl = null;
			}
			g.drawTexture(drawTexHeight, x, y);
		} catch (Exception ex) {
			for (int i = 0; i < height; i++) {
				g.setColorValue(
						(start.getRed() * (height - i)) / height
								+ (end.getRed() * i) / height,
						(start.getGreen() * (height - i)) / height
								+ (end.getGreen() * i) / height,
						(start.getBlue() * (height - i)) / height
								+ (end.getBlue() * i) / height, alpha);
				g.drawLine(x, i + y, x + width, i + y);
			}
		}
	}

	public synchronized void drawWidth(LGraphics g, int x, int y) {
		try {
			if (drawImgWidth == null) {
				drawImgWidth = new LImage(width, height, true);
				LGraphics gl = drawImgWidth.getLGraphics();
				for (int i = 0; i < width; i++) {
					gl.setColor(
							(start.getRed() * (width - i)) / width
									+ (end.getRed() * i) / width,
							(start.getGreen() * (width - i)) / width
									+ (end.getGreen() * i) / width,
							(start.getBlue() * (width - i)) / width
									+ (end.getBlue() * i) / width, alpha);
					gl.drawLine(i, 0, i, height);
				}
				gl.dispose();
				gl = null;
			}
			g.drawImage(drawImgWidth, x, y);
		} catch (Exception e) {
			for (int i = 0; i < width; i++) {
				g.setColor(
						(start.getRed() * (width - i)) / width
								+ (end.getRed() * i) / width,
						(start.getGreen() * (width - i)) / width
								+ (end.getGreen() * i) / width,
						(start.getBlue() * (width - i)) / width
								+ (end.getBlue() * i) / width, alpha);
				g.drawLine(i + x, y, i + x, y + height);
			}
		}
	}

	public synchronized void drawHeight(LGraphics g, int x, int y) {
		try {
			if (drawImgHeight == null) {
				drawImgHeight = new LImage(width, height, true);
				LGraphics gl = drawImgHeight.getLGraphics();
				for (int i = 0; i < height; i++) {
					gl.setColor(
							(start.getRed() * (height - i)) / height
									+ (end.getRed() * i) / height,
							(start.getGreen() * (height - i)) / height
									+ (end.getGreen() * i) / height,
							(start.getBlue() * (height - i)) / height
									+ (end.getBlue() * i) / height, alpha);
					gl.drawLine(0, i, width, i);
				}
				gl.dispose();
				gl = null;
			}
			g.drawImage(drawImgHeight, x, y);
		} catch (Exception e) {
			for (int i = 0; i < height; i++) {
				g.setColor(
						(start.getRed() * (height - i)) / height
								+ (end.getRed() * i) / height,
						(start.getGreen() * (height - i)) / height
								+ (end.getGreen() * i) / height,
						(start.getBlue() * (height - i)) / height
								+ (end.getBlue() * i) / height, alpha);
				g.drawLine(x, i + y, x + width, i + y);
			}
		}
	}

	public static void close() {
		if (gradations == null) {
			return;
		}
		Set<?> entrys = gradations.entrySet();
		for (Iterator<?> it = entrys.iterator(); it.hasNext();) {
			Entry<?, ?> e = (Entry<?, ?>) it.next();
			LGradation g = (LGradation) e.getValue();
			if (g != null) {
				g.dispose();
				g = null;
			}
		}
		gradations.clear();
	}

	@Override
	public void dispose() {
		if (drawTexWidth != null) {
			drawTexWidth.destroy();
		}
		if (drawTexHeight != null) {
			drawTexHeight.destroy();
		}
		if (drawImgWidth != null) {
			drawImgWidth.dispose();
		}
		if (drawImgWidth != null) {
			drawImgWidth.dispose();
		}
	}

}
