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
package loon.action.sprite.effect;

import loon.LTexture;
import loon.canvas.LColor;
import loon.canvas.Pixmap;
import loon.opengl.GLEx;
import loon.utils.MathUtils;

/**
 * 华夏风象素特效,可以显示太极图和八卦图(这个不太好定义显示成什么样(因为无法预判用户想怎么显示),所以本身没有特效,就是可以显示八卦与太极,用户自行拼接效果吧……)
 */
public class PixelGossipEffect extends BaseAbstractEffect {

	private static final int[][] trigramList = { { 0, 0, 0 }, { 1, 1, 1 }, { 0, 0, 1 }, { 1, 1, 0 }, { 0, 1, 1 },
			{ 1, 0, 0 }, { 0, 1, 0 }, { 1, 0, 1 } };

	public static void drawTaichi(Pixmap g, int centerX, int centerY, int circle, int angle) {
		g.setColor(LColor.white);
		g.fillArc(centerX - circle / 2, centerY - circle / 2, circle, circle, angle, 180);
		g.setColor(LColor.black);
		g.fillArc(centerX - circle / 2, centerY - circle / 2, circle, circle, angle + 180, 180);
		g.fillArc((int) (centerX + (circle / 2 / 2 * (MathUtils.cos(angle * MathUtils.PI / 180))) - circle / 2 / 2),
				(int) (centerY - (circle / 2 / 2 * (MathUtils.sin(angle * MathUtils.PI / 180))) - circle / 2 / 2),
				circle / 2, circle / 2, 0, 360);
		g.setColor(LColor.white);
		g.fillArc(
				(int) (centerX + (circle / 2 / 2 * (MathUtils.cos((angle + 180) * MathUtils.PI / 180)))
						- circle / 2 / 2),
				(int) (centerY - (circle / 2 / 2 * (MathUtils.sin((angle + 180) * MathUtils.PI / 180)))
						- circle / 2 / 2),
				circle / 2, circle / 2, 0, 360);
		g.setColor(LColor.black);
		g.fillArc(
				(int) (centerX + (circle / 2 / 2 * (MathUtils.cos((angle + 180) * MathUtils.PI / 180)))
						- circle / 2 / 2 / 2),
				(int) (centerY - (circle / 2 / 2 * (MathUtils.sin((angle + 180) * MathUtils.PI / 180)))
						- circle / 2 / 2 / 2),
				circle / 2 / 2, circle / 2 / 2, 0, 360);
		g.setColor(LColor.white);
		g.fillArc((int) (centerX + (circle / 2 / 2 * (MathUtils.cos(angle * MathUtils.PI / 180))) - circle / 2 / 2 / 2),
				(int) (centerY - (circle / 2 / 2 * (MathUtils.sin(angle * MathUtils.PI / 180))) - circle / 2 / 2 / 2),
				circle / 2 / 2, circle / 2 / 2, 0, 360);
	}

	/**
	 * 绘制太极图
	 * 
	 * @param g
	 * @param x
	 * @param y
	 * @param circle
	 */
	public static void drawTaichi(GLEx g, float x, float y, float circle) {
		drawTaichi(g, x + circle / 2, y + circle / 2, circle, 0f);
	}

	/**
	 * 非有非无,是为混一,非黑非白,是为同流,非阴非阳,几乎道也
	 * 
	 * @param g
	 * @param centerX
	 * @param centerY
	 * @param circle
	 * @param angle
	 */
	public static void drawTaichi(GLEx g, float centerX, float centerY, float circle, float angle) {
		if (angle > 90) {
			angle -= 90;
		}
		g.setColor(LColor.white);
		g.fillArc(centerX - circle / 2, centerY - circle / 2, circle, circle, angle, 180);
		g.setColor(LColor.black);
		g.fillArc(centerX - circle / 2, centerY - circle / 2, circle, circle, angle + 180, 180);
		g.fillArc(centerX + (circle / 2 / 2 * (MathUtils.cos(angle * MathUtils.PI / 180))) - circle / 2 / 2,
				centerY - (circle / 2 / 2 * (MathUtils.sin(angle * MathUtils.PI / 180))) - circle / 2 / 2, circle / 2,
				circle / 2, 0, 360);
		g.setColor(LColor.white);
		g.fillArc(centerX + (circle / 2 / 2 * (MathUtils.cos((angle + 180) * MathUtils.PI / 180))) - circle / 2 / 2,
				centerY - (circle / 2 / 2 * (MathUtils.sin((angle + 180) * MathUtils.PI / 180))) - circle / 2 / 2,
				circle / 2, circle / 2, 0, 360);
		g.setColor(LColor.black);
		g.fillArc(centerX + (circle / 2 / 2 * (MathUtils.cos((angle + 180) * MathUtils.PI / 180))) - circle / 2 / 2 / 2,
				centerY - (circle / 2 / 2 * (MathUtils.sin((angle + 180) * MathUtils.PI / 180))) - circle / 2 / 2 / 2,
				circle / 2 / 2, circle / 2 / 2, 0, 360);
		g.setColor(LColor.white);
		g.fillArc(centerX + (circle / 2 / 2 * (MathUtils.cos(angle * MathUtils.PI / 180))) - circle / 2 / 2 / 2,
				centerY - (circle / 2 / 2 * (MathUtils.sin(angle * MathUtils.PI / 180))) - circle / 2 / 2 / 2,
				circle / 2 / 2, circle / 2 / 2, 0, 360);
	}

	/**
	 * 乾卦指天,纯阳纯刚,极阳
	 * 
	 * @param g
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param color
	 * @param fill
	 */
	public static void drawSkyTrigram(GLEx g, float x, float y, float width, float height, LColor color, boolean fill) {
		drawTrigram(g, 0, x, y, width, height, color, fill);
	}

	public static void drawQianTrigram(GLEx g, float x, float y, float width, float height, LColor color,
			boolean fill) {
		drawSkyTrigram(g, x, y, width, height, color, fill);
	}

	/**
	 * 坤卦指地,纯阴纯柔,极阴
	 * 
	 * @param g
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param color
	 * @param fill
	 */
	public static void drawLandTrigram(GLEx g, float x, float y, float width, float height, LColor color,
			boolean fill) {
		drawTrigram(g, 1, x, y, width, height, color, fill);
	}

	public static void drawKunTrigram(GLEx g, float x, float y, float width, float height, LColor color, boolean fill) {
		drawLandTrigram(g, x, y, width, height, color, fill);
	}

	/**
	 * 巽卦指风
	 * 
	 * @param g
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param color
	 * @param fill
	 */
	public static void drawWindTrigram(GLEx g, float x, float y, float width, float height, LColor color,
			boolean fill) {
		drawTrigram(g, 2, x, y, width, height, color, fill);
	}

	public static void drawXunTrigram(GLEx g, float x, float y, float width, float height, LColor color, boolean fill) {
		drawWindTrigram(g, x, y, width, height, color, fill);
	}

	/**
	 * 震卦指雷
	 * 
	 * @param g
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param color
	 * @param fill
	 */
	public static void drawThunderTrigram(GLEx g, float x, float y, float width, float height, LColor color,
			boolean fill) {
		drawTrigram(g, 3, x, y, width, height, color, fill);
	}

	public static void drawZhenTrigram(GLEx g, float x, float y, float width, float height, LColor color,
			boolean fill) {
		drawThunderTrigram(g, x, y, width, height, color, fill);
	}

	/**
	 * 艮卦指山
	 * 
	 * @param g
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param color
	 * @param fill
	 */
	public static void drawMountainTrigram(GLEx g, float x, float y, float width, float height, LColor color,
			boolean fill) {
		drawTrigram(g, 4, x, y, width, height, color, fill);
	}

	public static void drawGenTrigram(GLEx g, float x, float y, float width, float height, LColor color, boolean fill) {
		drawMountainTrigram(g, x, y, width, height, color, fill);
	}

	/**
	 * 兑卦指泽
	 * 
	 * @param g
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param color
	 * @param fill
	 */
	public static void drawSwampTrigram(GLEx g, float x, float y, float width, float height, LColor color,
			boolean fill) {
		drawTrigram(g, 5, x, y, width, height, color, fill);
	}

	public static void drawDuiTrigram(GLEx g, float x, float y, float width, float height, LColor color, boolean fill) {
		drawSwampTrigram(g, x, y, width, height, color, fill);
	}

	/**
	 * 离卦指火
	 * 
	 * @param g
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param color
	 * @param fill
	 */
	public static void drawFireTrigram(GLEx g, float x, float y, float width, float height, LColor color,
			boolean fill) {
		drawTrigram(g, 6, x, y, width, height, color, fill);
	}

	public static void drawLiTrigram(GLEx g, float x, float y, float width, float height, LColor color, boolean fill) {
		drawFireTrigram(g, x, y, width, height, color, fill);
	}

	/**
	 * 坎卦指水
	 * 
	 * @param g
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param color
	 * @param fill
	 */
	public static void drawWaterTrigram(GLEx g, float x, float y, float width, float height, LColor color,
			boolean fill) {
		drawTrigram(g, 7, x, y, width, height, color, fill);
	}

	public static void drawKanTrigram(GLEx g, float x, float y, float width, float height, LColor color, boolean fill) {
		drawWaterTrigram(g, x, y, width, height, color, fill);
	}

	public static void drawTrigram(GLEx g, int idx, float x, float y, float width, float height, LColor color,
			boolean fill) {
		drawTrigram(g, idx, x, y, width, height, height / 5f, color, color, color, fill);
	}

	/**
	 * 八卦符号绘制
	 * 
	 * @param g
	 * @param idx
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param space
	 * @param topColor
	 * @param centerColor
	 * @param bottomColor
	 * @param fill
	 */
	public static void drawTrigram(GLEx g, int idx, float x, float y, float width, float height, float space,
			LColor topColor, LColor centerColor, LColor bottomColor, boolean fill) {
		float left = x;
		float top = y;
		float top1 = top + height + space;
		float top2 = top1 + height + space;
		if (idx < 0) {
			idx = 0;
		}
		if (idx > 7) {
			idx = 7;
		}
		int[] trigrams = trigramList[idx];
		for (int i = 0; i < trigrams.length; i++) {
			// 0通1断,0阳1阴
			boolean tong = trigrams[i] == 0;
			switch (i) {
			case 0:
				drawYao(g, tong, left, top, width, height, topColor, fill);
				break;
			case 1:
				drawYao(g, tong, left, top1, width, height, centerColor, fill);
				break;
			case 2:
				drawYao(g, tong, left, top2, width, height, bottomColor, fill);
				break;
			}
		}
	}

	public static void drawYao(GLEx g, boolean tong, float x1, float y1, float width, float height, LColor color,
			boolean fill) {
		if (tong) {
			drawYang(g, x1, y1, width, height, color, fill);
		} else {
			drawYin(g, x1, y1, width, height, color, fill);
		}
	}

	public static void drawYang(GLEx g, float x, float y, float width, float height, LColor color, boolean fill) {
		if (!fill) {
			g.drawRect(x, y, width, height, color);
		} else {
			g.fillRect(x, y, width, height, color);
		}
	}

	public static void drawYin(GLEx g, float x1, float y1, float width, float height, LColor color, boolean fill) {
		float space = 2f;
		float widthSpace = MathUtils.max((width / 2f) - space, 1f);
		if (!fill) {
			g.drawRect(x1, y1, widthSpace, height, color);
			g.drawRect(x1 + widthSpace + space + space, y1, widthSpace, height, color);
		} else {
			g.fillRect(x1, y1, widthSpace, height, color);
			g.fillRect(x1 + widthSpace + space + space, y1, widthSpace, height, color);
		}
	}

	private int trigramIndex;

	private boolean fillTrigram;

	private LTexture tjtexture;

	public PixelGossipEffect(int x, int y, int width, int height) {
		this(8, x, y, width, height, LColor.darkGray);
	}

	public PixelGossipEffect(int trigram, int x, int y, int width, int height) {
		this(trigram, x, y, width, height, LColor.darkGray);
	}

	public PixelGossipEffect(int trigram, int x, int y, int width, int height, LColor color) {
		this(trigram, x, y, width, height, color, true);
	}

	public PixelGossipEffect(int trigram, int x, int y, int width, int height, LColor color, boolean fill) {
		setLocation(x, y);
		setSize(width, height);
		setColor(color);
		setRepaint(true);
		this.trigramIndex = trigram;
		this.fillTrigram = fill;
	}

	public Pixmap getTaichi(int circle) {
		Pixmap pix = new Pixmap(circle, circle);
		drawTaichi(pix, circle / 2, circle / 2, circle - 4, 0);
		return pix;
	}

	@Override
	public void onUpdate(final long elapsedTime) {
		checkAutoRemove();
	}

	@Override
	public void repaint(GLEx g, float offsetX, float offsetY) {
		if (completedAfterBlackScreen(g, offsetX, offsetY)) {
			return;
		}
		float x = drawX(offsetX);
		float y = drawY(offsetY);
		int w = width();
		int h = height();
		if (trigramIndex >= 8) {
			if (this.tjtexture == null) {
				int circle = 0;
				if (isScaled()) {
					circle = MathUtils.max((int) (w / getScaleX()), (int) (h / getScaleY()));
				} else {
					circle = MathUtils.max(w, h);
				}
				this.tjtexture = getTaichi(MathUtils.clamp(circle, 4, 150)).texture();
			}
			g.draw(tjtexture, x, y, w, h);
		} else {
			drawTrigram(g, trigramIndex, x, y, w, h / 3, getColor(), fillTrigram);
		}
	}

	public int getTrigramIndex() {
		return trigramIndex;
	}

	public PixelGossipEffect setTrigramIndex(int trigramIndex) {
		this.trigramIndex = trigramIndex;
		return this;
	}

	public boolean isFillTrigram() {
		return fillTrigram;
	}

	public PixelGossipEffect setFillTrigram(boolean fillTrigram) {
		this.fillTrigram = fillTrigram;
		return this;
	}

	@Override
	public PixelGossipEffect setAutoRemoved(boolean autoRemoved) {
		super.setAutoRemoved(autoRemoved);
		return this;
	}

	@Override
	protected void _onDestroy() {
		super._onDestroy();
		if (tjtexture != null) {
			tjtexture.close();
			tjtexture = null;
		}
	}
}
