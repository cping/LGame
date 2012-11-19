package org.loon.framework.android.game.core.graphics;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import org.loon.framework.android.game.core.LRelease;
import org.loon.framework.android.game.core.LSystem;
import org.loon.framework.android.game.core.graphics.device.LGraphics;

import android.graphics.Bitmap.Config;

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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
// 默认的色彩渐变器(标准Java中有同作用类，此为统一Android与JavaSE组件而添加)
public class LGradation implements LRelease {

	private LColor start;

	private LColor end;

	private int width, height, alpha;

	private LImage drawWidth, drawHeight;

	private static HashMap<String, LGradation> lazyGradation;

	public static LGradation getInstance(LColor s, LColor e, int w, int h) {
		return getInstance(s, e, w, h, 125);
	}

	public static LGradation getInstance(LColor s, LColor e, int w, int h,
			int alpha) {
		if (lazyGradation == null) {
			lazyGradation = new HashMap<String, LGradation>(10);
		}
		int hashCode = 1;
		hashCode = LSystem.unite(hashCode, s.getRGB());
		hashCode = LSystem.unite(hashCode, e.getRGB());
		hashCode = LSystem.unite(hashCode, w);
		hashCode = LSystem.unite(hashCode, h);
		hashCode = LSystem.unite(hashCode, alpha);
		String key = String.valueOf(hashCode);
		LGradation o = (LGradation) lazyGradation.get(key);
		if (o == null) {
			lazyGradation.put(key, o = new LGradation(s, e, w, h, alpha));
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

	public synchronized void drawWidth(LGraphics g, int x, int y) {
		try {
			if (drawWidth == null) {
				drawWidth = new LImage(width, height, Config.ARGB_8888);
				LGraphics gl = drawWidth.getLGraphics();
				for (int i = 0; i < width; i++) {
					gl.setColor((start.getRed() * (width - i))
							/ width + (end.getRed() * i) / width, (start
							.getGreen() * (width - i))
							/ width + (end.getGreen() * i) / width, (start
							.getBlue() * (width - i))
							/ width + (end.getBlue() * i) / width, alpha);
					gl.drawLine(i, 0, i, height);
				}
				gl.dispose();
				gl = null;
			}
			g.drawImage(drawWidth, x, y);
		} catch (Exception e) {
			for (int i = 0; i < width; i++) {
				g.setColor((start.getRed() * (width - i)) / width
						+ (end.getRed() * i) / width,
						(start.getGreen() * (width - i)) / width
								+ (end.getGreen() * i) / width, (start
								.getBlue() * (width - i))
								/ width + (end.getBlue() * i) / width, alpha);
				g.drawLine(i + x, y, i + x, y + height);
			}
		}
	}

	public synchronized void drawHeight(LGraphics g, int x, int y) {
		try {
			if (drawHeight == null) {
				drawHeight = new LImage(width, height, Config.ARGB_8888);
				LGraphics gl = drawHeight.getLGraphics();
				for (int i = 0; i < height; i++) {
					gl.setColor((start.getRed() * (height - i))
							/ height + (end.getRed() * i) / height, (start
							.getGreen() * (height - i))
							/ height + (end.getGreen() * i) / height, (start
							.getBlue() * (height - i))
							/ height + (end.getBlue() * i) / height, alpha);
					gl.drawLine(0, i, width, i);
				}
				gl.dispose();
				gl = null;
			}
			g.drawImage(drawHeight, x, y);
		} catch (Exception e) {
			for (int i = 0; i < height; i++) {
				g.setColor((start.getRed() * (height - i)) / height
						+ (end.getRed() * i) / height,
						(start.getGreen() * (height - i)) / height
								+ (end.getGreen() * i) / height, (start
								.getBlue() * (height - i))
								/ height + (end.getBlue() * i) / height, alpha);
				g.drawLine(x, i + y, x + width, i + y);
			}
		}
	}

	public static void close() {
		if (lazyGradation == null) {
			return;
		}
		Set<?> entrys = lazyGradation.entrySet();
		for (Iterator<?> it = entrys.iterator(); it.hasNext();) {
			Entry<?, ?> e = (Entry<?, ?>) it.next();
			LGradation g = (LGradation) e.getValue();
			if (g != null) {
				g.dispose();
				g = null;
			}
		}
	}

	public void dispose() {
		if (drawWidth != null) {
			drawWidth.dispose();
		}
		if (drawHeight != null) {
			drawHeight.dispose();
		}
	}


}
