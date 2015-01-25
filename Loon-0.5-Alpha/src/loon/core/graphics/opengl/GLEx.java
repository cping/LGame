/**
 * 
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
package loon.core.graphics.opengl;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;


import loon.Files;
import loon.core.LSystem;
import loon.core.geom.Matrix;
import loon.core.geom.Polygon;
import loon.core.geom.RectBox;
import loon.core.geom.Shape;
import loon.core.geom.Triangle;
import loon.core.geom.Triangle2f;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.graphics.LImage;
import loon.core.graphics.device.LGraphics;
import loon.core.graphics.device.LTrans;
import loon.jni.NativeSupport;
import loon.utils.MathUtils;

public final class GLEx implements LTrans {

	private static boolean enableBlend = false;
	
	public static void enableBlend(final GL20 gl10) {
		try {
			if (!GLEx.enableBlend) {
				gl10.glEnable(GL20.GL_BLEND);
				GLEx.enableBlend = true;
			}
		} catch (Exception e) {
		}
	}

	public static void disableBlend(final GL20 gl10) {
		try {
			if (GLEx.enableBlend) {
				gl10.glDisable(GL20.GL_BLEND);
				GLEx.enableBlend = false;
			}
		} catch (Exception e) {
		}
	}

	public final void reload(){
		enableBlend = false;
	}
	
	public static int width() {
		return LSystem.screenRect.width;
	}

	public static int height() {
		return LSystem.screenRect.height;
	}


	public static class Clip {

		public int x;

		public int y;

		public int width;

		public int height;

		public Clip(Clip clip) {
			this(clip.x, clip.y, clip.width, clip.height);
		}

		public Clip(int x, int y, int w, int h) {
			this.setBounds(x, y, w, h);
		}

		public void setBounds(int x, int y, int w, int h) {
			this.x = x;
			this.y = y;
			this.width = w;
			this.height = h;
		}

		public int getBottom() {
			return height;
		}

		public int getLeft() {
			return x;
		}

		public int getRight() {
			return width;
		}

		public int getTop() {
			return y;
		}

	}

	public static enum Direction {
		TRANS_NONE, TRANS_MIRROR, TRANS_FILP, TRANS_MF;
	}

	public static GLEx self;

	public static GL20 gl;

	private int currentBlendMode;

	private float _lastAlpha = 1F, lineWidth, sx = 1, sy = 1;

	private boolean isClose, isTex2DEnabled, isARRAYEnable, isAntialias,
			isScissorTest, isPushed;

	private final Clip clip;

	private float translateX, translateY;

	private final RectBox viewPort;

	private boolean preTex2dMode;

	public static int lazyTextureID;

	private static final int[] delBufferID = new int[1];

	private static final int[] delTextureID = new int[1];

	boolean onAlpha;

	static int verMajor, verMinor;

	private LColor color = new LColor(LColor.white);

	private static final float[] rectDataCords = new float[16];

	private static final FloatBuffer rectData = NativeSupport
			.newFloatBuffer(rectDataCords.length);

	private static int glDataBufferID;

	private LFont font = LFont.getDefaultFont();

	private boolean onSaveFlag;

	public GLEx(int width, int height) {
		GLEx.gl = new LWJGLGL20();
		GLEx.self = this;
		this.viewPort = new RectBox(0, 0, width, height);
		this.clip = new Clip(0, 0, viewPort.width, viewPort.height);
		this.isTex2DEnabled = false;
		this.isClose = false;
	}

	public int getWidth() {
		return (int) viewPort.getWidth();
	}

	public int getHeight() {
		return (int) viewPort.getHeight();
	}

	/**
	 * 变更画布基础设置
	 * 
	 */
	public final void update() {

	}

	private boolean useBegin;

	private GLBatch glBatch;

	/**
	 * 模拟标准OpenGL的glBegin(实际为重新初始化顶点集合)
	 * 
	 * @param mode
	 */
	public final void glBegin(int mode) {
		if (isClose) {
			return;
		}
		this.glTex2DDisable();
		/*if (glBatch == null) {
			glBatch = new GLBatch(9000);
		}
		glBatch.begin(mode);*/
		this.useBegin = true;
	}

	/**
	 * 在模拟标准OpenGL的环境中传入指定像素点
	 * 
	 * @param x
	 * @param y
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 */
	public void putPixel4ES(float x, float y, float r, float g, float b, float a) {
		if (isClose || !useBegin) {
			return;
		}
		if (a <= 0 || (r == 0 && g == 0 && b == 0 && a == 0)) {
			return;
		}
		if ((x < 0 || y < 0) || (x > viewPort.width || y > viewPort.height)) {
			return;
		}
		this.glVertex2f(x, y);
		this.glColor(r, g, b, a);
	}

	/**
	 * 在模拟标准OpenGL的环境中传入指定像素点
	 * 
	 * @param x
	 * @param y
	 * @param c
	 */
	public void putPixel4ES(float x, float y, LColor c) {
		putPixel4ES(x, y, c.r, c.g, c.b, c.a);
	}

	/**
	 * 在模拟标准OpenGL的环境中传入指定像素点
	 * 
	 * @param x
	 * @param y
	 * @param r
	 * @param g
	 * @param b
	 */
	public void putPixel3ES(float x, float y, float r, float g, float b) {
		putPixel4ES(x, y, r, g, b, 1);
	}

	/**
	 * 设置纹理坐标
	 * 
	 * @param fcol
	 * @param frow
	 */
	public final void glTexCoord2f(float fcol, float frow) {
		if (isClose || !useBegin) {
			return;
		}
		glBatch.texCoord(fcol, frow);
	}

	/**
	 * 添加二维纹理
	 * 
	 * @param x
	 * @param y
	 */
	public final void glVertex2f(float x, float y) {
		if (isClose || !useBegin) {
			return;
		}
	//	glVertex3f(x, y, 0);
	}

	/**
	 * 添加三维纹理
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public final void glVertex3f(float x, float y, float z) {
		if (isClose || !useBegin) {
			return;
		}
		//glBatch.vertex(x, y, z);
	}

	/**
	 * 定义色彩
	 * 
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 */
	public void glColor(float r, float g, float b, float a) {
		if (isClose || !useBegin) {
			return;
		}
		glBatch.color(r, g, b, a);
	}

	/**
	 * 定义色彩
	 * 
	 * @param c
	 */
	public void glColor(LColor c) {
		if (isClose) {
			return;
		}
		glBatch.color(c);
	}

	/**
	 * 定义色彩
	 * 
	 * @param r
	 * @param g
	 * @param b
	 */
	public void glColor(float r, float g, float b) {
		glColor(r, g, b, 1);
	}

	/**
	 * 绘制线段(模式应为GL.GL_LINES)
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	public void glLine(float x1, float y1, float x2, float y2) {
		$drawLine(x1, y1, x2, y2, false);
	}

	/**
	 * 绘制矩形(模式应为GL.GL_POLYGON)
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public void glDrawRect(float x, float y, float width, float height) {
		glRect(x, y, width, height, false);
	}

	/**
	 * 绘制矩形(模式应为GL.GL_POLYGON)
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public void glFillRect(float x, float y, float width, float height) {
		glRect(x, y, width, height, true);
	}

	/**
	 * 绘制矩形(模式应为GL.GL_POLYGON)
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param fill
	 */
	private final void glRect(float x, float y, float width, float height,
			boolean fill) {
		float[] xs = new float[4];
		float[] ys = new float[4];
		xs[0] = x;
		xs[1] = x + width;
		xs[2] = x + width;
		xs[3] = x;
		ys[0] = y;
		ys[1] = y;
		ys[2] = y + height;
		ys[3] = y + height;
		if (fill) {
			glFillPoly(xs, ys, 4);
		} else {
			glDrawPoly(xs, ys, 4);
		}
	}

	/**
	 * 绘制多边形(模式应为GL.GL_LINE_LOOP)
	 * 
	 * @param xPoints
	 * @param yPoints
	 * @param nPoints
	 */
	public void glDrawPoly(float[] xPoints, float[] yPoints, int nPoints) {
		$drawPolygon(xPoints, yPoints, nPoints, false);
	}

	/**
	 * 填充多边形(模式应为GL.GL_LINE_LOOP)
	 * 
	 * @param xPoints
	 * @param yPoints
	 * @param nPoints
	 */
	public void glFillPoly(float[] xPoints, float[] yPoints, int nPoints) {
		$fillPolygon(xPoints, yPoints, nPoints, false);
	}

	/**
	 * 检查是否开启了glbegin函数
	 * 
	 * @return
	 */
	public boolean useGLBegin() {
		return useBegin;
	}

	/**
	 * 模拟标准OpenGL的glEnd(实际为提交顶点坐标给OpenGL)
	 * 
	 */
	public final void glEnd() {
		if (isClose || !useBegin) {
			useBegin = false;
			return;
		}
		//glBatch.end();
		useBegin = false;
	}

	/**
	 * 开启2D纹理设置(禁用此前的纹理操作)
	 * 
	 */
	public final void glTex2DDisable() {
		if (isClose) {
			return;
		}
		if (isTex2DEnabled) {
			gl.glDisable(GL.GL_TEXTURE_2D);
			isTex2DEnabled = false;
		}
	}

	/**
	 * 关闭2D纹理设置(允许新的纹理操作)
	 * 
	 */
	public final void glTex2DEnable() {
		if (isClose) {
			return;
		}
		if (!isTex2DEnabled) {
			gl.glEnable(GL.GL_TEXTURE_2D);
			isTex2DEnabled = true;
		}
	}

	/**
	 * 允许顶点数组操作
	 * 
	 */
	public final void glTex2DARRAYEnable() {

	}

	/**
	 * 禁用定点数组操作
	 * 
	 */
	public final void glTex2DARRAYDisable() {

	}

	/**
	 * 设定当前使用的色彩混合模式
	 * 
	 * @param mode
	 */
	public final void setBlendMode(int mode) {
		if (isClose) {
			return;
		}
		if (currentBlendMode == mode) {
			return;
		}
		this.currentBlendMode = mode;
		if (currentBlendMode == GL.MODE_NORMAL) {
			GLEx.enableBlend(gl);
			gl.glColorMask(true, true, true, true);
			gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			return;
		} else if (currentBlendMode == GL.MODE_ALPHA_MAP) {
			GLEx.disableBlend(gl);
			gl.glColorMask(false, false, false, true);
			return;
		} else if (currentBlendMode == GL.MODE_ALPHA_BLEND) {
			GLEx.enableBlend(gl);
			gl.glColorMask(true, true, true, false);
			gl.glBlendFunc(GL20.GL_DST_ALPHA, GL20.GL_ONE_MINUS_DST_ALPHA);
			return;
		} else if (currentBlendMode == GL.MODE_COLOR_MULTIPLY) {
			GLEx.enableBlend(gl);
			gl.glColorMask(true, true, true, true);
			gl.glBlendFunc(GL20.GL_ONE_MINUS_SRC_COLOR, GL20.GL_SRC_COLOR);
			return;
		} else if (currentBlendMode == GL.MODE_ADD) {
			GLEx.enableBlend(gl);
			gl.glColorMask(true, true, true, true);
			gl.glBlendFunc(GL20.GL_ONE, GL20.GL_ONE);
			return;
		} else if (currentBlendMode == GL.MODE_SPEED) {
			GLEx.enableBlend(gl);
			gl.glColorMask(true, true, true, false);
			gl.glBlendFunc(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
			return;
		} else if (currentBlendMode == GL.MODE_SCREEN) {
			GLEx.enableBlend(gl);
			gl.glColorMask(true, true, true, true);
			gl.glBlendFunc(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_COLOR);
			return;
		} else if (currentBlendMode == GL.MODE_ALPHA_ONE) {
			GLEx.enableBlend(gl);
			gl.glColorMask(true, true, true, true);
			gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
			return;
		} else if (currentBlendMode == GL.MODE_ALPHA) {
			GLEx.enableBlend(gl);
			gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			return;
		} else if (currentBlendMode == GL.MODE_NONE) {
			GLEx.disableBlend(gl);
			gl.glColorMask(true, true, true, false);
			return;
		}
	}


	/**
	 * 保存当前的矩阵设置
	 * 
	 */
	public final void glPushMatrix() {
		if (isClose) {
			return;
		}

	}

	/**
	 * 还原上次保存的矩阵设置
	 * 
	 */
	public final void glPopMatrix() {

	}

	/**
	 * 清除当前帧色彩
	 * 
	 * @param clear
	 */
	public void reset(boolean clear) {
		if (isClose) {
			return;
		}
		_isReplace = true;
		bind(0);
		if (isTex2DEnabled) {
			gl.glDisable(GL.GL_TEXTURE_2D);
			isTex2DEnabled = false;
		}
		if (clear) {
			gl.glClearColor(0, 0, 0, 1f);
			gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		}
	}

	/**
	 * 清空屏幕
	 * 
	 */
	public final void drawClear() {
		if (isClose) {
			return;
		}
		drawClear(LColor.black);
	}

	/**
	 * 以指定色彩清空屏幕
	 * 
	 * @param color
	 */
	public final void drawClear(LColor color) {
		if (isClose) {
			return;
		}
		gl.glClearColor(color.r, color.g, color.b, color.a);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}

	/**
	 * 设定色彩透明度
	 * 
	 * @param alpha
	 */
	public void setAlphaValue(int alpha) {
		if (isClose) {
			return;
		}
		setAlpha((float) alpha / 255);
	}

	public boolean isAlpha() {
		return onAlpha;
	}

	/**
	 * 设定色彩透明度
	 * 
	 * @param alpha
	 */
	public void setAlpha(float alpha) {
		if (alpha == _lastAlpha) {
			return;
		}
		_lastAlpha = alpha < 0 ? 0 : alpha > 1 ? 1 : alpha;

	}

	/**
	 * 返回当前的色彩透明度
	 * 
	 * @return
	 */
	public float getAlpha() {
		return color.a;
	}

	/**
	 * 设定画布颜色
	 * 
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 */
	public void setColorValue(int r, int g, int b, int a) {
		float red = (float) r / 255.0f;
		float green = (float) g / 255.0f;
		float blue = (float) b / 255.0f;
		float alpha = (float) a / 255.0f;
		setColor(red, green, blue, alpha);
	}

	/**
	 * 释放颜色设定
	 * 
	 */
	public final void resetColor() {
		if (isClose) {
			return;
		}
		if (!color.equals(LColor.white)) {
			color.setColor(1f, 1f, 1f, 1f);

		}
	}

	/**
	 * 设定画布颜色
	 * 
	 * @param color
	 */
	public final void setColorRGB(LColor c) {
		if (isClose) {
			return;
		}
		if (!c.equals(color)) {
			GL_MODULATE();
			color.setColor(c.r, c.g, c.b, _lastAlpha);

		}
	}

	/**
	 * 设定画布颜色
	 * 
	 * @param color
	 */
	public final void setColorARGB(LColor c) {
		if (isClose) {
			return;
		}
		if (!c.equals(color)) {
			GL_MODULATE();
			float alpha = _lastAlpha == 1 ? c.a : _lastAlpha;
			color.setColor(c.r, c.g, c.b, alpha);

		}
	}

	/**
	 * 设定画布颜色
	 * 
	 * @param pixel
	 */
	public final void setColor(int pixel) {
		int[] rgbs = LColor.getRGBs(pixel);
		setColorValue(rgbs[0], rgbs[1], rgbs[2], (int) (_lastAlpha * 255));
	}

	/**
	 * 设定画布颜色
	 * 
	 * @param c
	 */
	public final void setColor(LColor c) {
		setColorARGB(c);
	}

	/**
	 * 设定画布颜色
	 * 
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 */
	public final void setColor(final float r, final float g, final float b,
			final float a) {
		if (isClose) {
			return;
		}
		if (!color.equals(r, g, b, a)) {
			GL_MODULATE();
			color.setFloatColor(r, g, b, a);

		}
	}

	public final void setColor(final int r, final int g, final int b,
			final int a) {
		if (isClose) {
			return;
		}
		float red = r / 255f;
		float green = g / 255f;
		float blue = b / 255f;
		float alpha = a / 255f;
		if (!color.equals(red, green, blue, alpha)) {
			GL_MODULATE();
			color.setFloatColor(red, green, blue, alpha);

		}
	}

	/**
	 * 设定画布颜色
	 * 
	 * @param r
	 * @param g
	 * @param b
	 */
	public final void setColor(final float r, final float g, final float b) {
		setColor(r, g, b, _lastAlpha);
	}

	public final void setColor(final int r, final int g, final int b) {
		setColor(r, g, b, (int) (_lastAlpha * 255));
	}

	/**
	 * 获得当前画布颜色
	 * 
	 * @return
	 */
	public final LColor getColor() {
		return new LColor(color);
	}

	public final int getColorRGB() {
		return color.getRGB();
	}

	public final int getColorARGB() {
		return color.getARGB();
	}

	/**
	 * 是否采用清晰的绘图模式
	 * 
	 * @param flag
	 */
	public void setAntiAlias(boolean flag) {
		if (isClose) {
			return;
		}
		if (flag) {
			gl.glEnable(GL.GL_LINE_SMOOTH);
		} else {
			gl.glDisable(GL.GL_LINE_SMOOTH);
		}
		this.isAntialias = flag;
	}

	public boolean isAntialias() {
		return isAntialias;
	}

	/**
	 * 绘制五角星
	 * 
	 * @param color
	 * @param x
	 * @param y
	 * @param r
	 */
	public void drawSixStart(LColor color, float x, float y, float r) {
		if (isClose) {
			return;
		}
		setColor(color);
		drawTriangle(color, x, y, r);
		drawRTriangle(color, x, y, r);
	}

	/**
	 * 绘制正三角
	 * 
	 * @param color
	 * @param x
	 * @param y
	 * @param r
	 */
	public void drawTriangle(LColor color, float x, float y, float r) {
		if (isClose) {
			return;
		}
		float x1 = x;
		float y1 = y - r;
		float x2 = x - (r * MathUtils.cos(MathUtils.PI / 6));
		float y2 = y + (r * MathUtils.sin(MathUtils.PI / 6));
		float x3 = x + (r * MathUtils.cos(MathUtils.PI / 6));
		float y3 = y + (r * MathUtils.sin(MathUtils.PI / 6));
		float[] xpos = new float[3];
		xpos[0] = x1;
		xpos[1] = x2;
		xpos[2] = x3;
		float[] ypos = new float[3];
		ypos[0] = y1;
		ypos[1] = y2;
		ypos[2] = y3;
		setColor(color);
		fillPolygon(xpos, ypos, 3);
	}

	/**
	 * 绘制倒三角
	 * 
	 * @param color
	 * @param x
	 * @param y
	 * @param r
	 */
	public void drawRTriangle(LColor color, float x, float y, float r) {
		if (isClose) {
			return;
		}
		float x1 = x;
		float y1 = y + r;
		float x2 = x - (r * MathUtils.cos(MathUtils.PI / 6.0f));
		float y2 = y - (r * MathUtils.sin(MathUtils.PI / 6.0f));
		float x3 = x + (r * MathUtils.cos(MathUtils.PI / 6.0f));
		float y3 = y - (r * MathUtils.sin(MathUtils.PI / 6.0f));
		float[] xpos = new float[3];
		xpos[0] = x1;
		xpos[1] = x2;
		xpos[2] = x3;
		float[] ypos = new float[3];
		ypos[0] = y1;
		ypos[1] = y2;
		ypos[2] = y3;
		setColor(color);
		fillPolygon(xpos, ypos, 3);
	}

	/**
	 * 绘制三角形
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param x3
	 * @param y3
	 */
	public void drawTriangle(final float x1, final float y1, final float x2,
			final float y2, final float x3, final float y3) {
		if (isClose) {
			return;
		}
		glBegin(GL.GL_LINE_LOOP);
		glVertex2f(x1, y1);
		glVertex2f(x2, y2);
		glVertex2f(x3, y3);
		glEnd();
	}

	/**
	 * 填充三角形
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param x3
	 * @param y3
	 */
	public void fillTriangle(final float x1, final float y1, final float x2,
			final float y2, final float x3, final float y3) {
		if (isClose) {
			return;
		}
		glBegin(GL.GL_TRIANGLES);
		glVertex2f(x1, y1);
		glVertex2f(x2, y2);
		glVertex2f(x3, y3);
		glEnd();
	}

	/**
	 * 绘制并填充一组三角
	 * 
	 * @param ts
	 */
	public void fillTriangle(Triangle2f[] ts) {
		fillTriangle(ts, 0, 0);
	}

	/**
	 * 绘制并填充一组三角
	 * 
	 * @param ts
	 * @param x
	 * @param y
	 */
	public void fillTriangle(Triangle2f[] ts, int x, int y) {
		if (isClose) {
			return;
		}
		if (ts == null) {
			return;
		}
		int size = ts.length;
		for (int i = 0; i < size; i++) {
			fillTriangle(ts[i], x, y);
		}
	}

	/**
	 * 绘制并填充一组三角
	 * 
	 * @param t
	 */
	public void fillTriangle(Triangle2f t) {
		fillTriangle(t, 0, 0);
	}

	/**
	 * 绘制并填充一组三角
	 * 
	 * @param t
	 * @param x
	 * @param y
	 */
	public void fillTriangle(Triangle2f t, float x, float y) {
		if (isClose) {
			return;
		}
		if (t == null) {
			return;
		}
		float[] xpos = new float[3];
		float[] ypos = new float[3];
		xpos[0] = x + t.xpoints[0];
		xpos[1] = x + t.xpoints[1];
		xpos[2] = x + t.xpoints[2];
		ypos[0] = y + t.ypoints[0];
		ypos[1] = y + t.ypoints[1];
		ypos[2] = y + t.ypoints[2];
		fillPolygon(xpos, ypos, 3);
	}

	/**
	 * 绘制一组三角
	 * 
	 * @param ts
	 */
	public void drawTriangle(Triangle2f[] ts) {
		drawTriangle(ts, 0, 0);
	}

	/**
	 * 绘制一组三角
	 * 
	 * @param ts
	 * @param x
	 * @param y
	 */
	public void drawTriangle(Triangle2f[] ts, int x, int y) {
		if (isClose) {
			return;
		}
		if (ts == null) {
			return;
		}
		int size = ts.length;
		for (int i = 0; i < size; i++) {
			drawTriangle(ts[i], x, y);
		}
	}

	/**
	 * 绘制三角
	 * 
	 * @param t
	 */
	public void drawTriangle(Triangle2f t) {
		drawTriangle(t, 0, 0);
	}

	/**
	 * 绘制三角
	 * 
	 * @param t
	 * @param x
	 * @param y
	 */
	public void drawTriangle(Triangle2f t, float x, float y) {
		if (isClose) {
			return;
		}
		if (t == null) {
			return;
		}
		float[] xpos = new float[3];
		float[] ypos = new float[3];
		xpos[0] = x + t.xpoints[0];
		xpos[1] = x + t.xpoints[1];
		xpos[2] = x + t.xpoints[2];
		ypos[0] = y + t.ypoints[0];
		ypos[1] = y + t.ypoints[1];
		ypos[2] = y + t.ypoints[2];
		drawPolygon(xpos, ypos, 3);
	}

	/**
	 * 绘制椭圆
	 * 
	 * @param centerX
	 * @param centerY
	 * @param r
	 * @param a
	 */
	public void drawOval(float x1, float y1, float width, float height) {
		this.drawArc(x1, y1, width, height, 32, 0, 360);
	}

	/**
	 * 填充椭圆
	 * 
	 * @param centerX
	 * @param centerY
	 * @param r
	 * @param a
	 */
	public void fillOval(float x1, float y1, float width, float height) {
		this.fillArc(x1, y1, width, height, 32, 0, 360);
	}

	/**
	 * 画线
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	public void drawLine(float x1, float y1, float x2, float y2) {
		if (isClose) {
			return;
		}
		$drawLine(x1, y1, x2, y2, true);
	}

	private void $drawLine(float x1, float y1, float x2, float y2, boolean use) {
		if (x1 > x2) {
			x1++;
		} else {
			x2++;
		}
		if (y1 > y2) {
			y1++;
		} else {
			y2++;
		}
		if (use) {
			glBegin(GL.GL_LINES);
		}
		{
			glVertex2f(x1, y1);
			glVertex2f(x2, y2);
		}
		if (use) {
			glEnd();
		}
	}

	/**
	 * 绘制色彩点
	 * 
	 * @param x
	 * @param y
	 */
	public void drawPoint(float x, float y) {
		if (isClose) {
			return;
		}
		glBegin(GL.GL_POINTS);
		glVertex2f(x, y);
		glEnd();
	}

	/**
	 * 绘制一组色彩点
	 * 
	 * @param x
	 * @param y
	 * @param size
	 */
	public void drawPoints(float[] x, float[] y, int size) {
		if (isClose) {
			return;
		}
		glBegin(GL.GL_POINTS);
		for (int i = 0; i < size; i++) {
			glVertex2f(x[i], y[i]);
		}
		glEnd();
	}

	/**
	 * 绘制指定图形
	 * 
	 * @param shape
	 */
	public final void draw(Shape shape) {
		if (isClose) {
			return;
		}
		float[] points = shape.getPoints();
		if (points.length == 0) {
			return;
		}
		glBegin(GL.GL_LINE_STRIP);
		for (int i = 0; i < points.length; i += 2) {
			glVertex2f(points[i], points[i + 1]);
		}
		if (shape.closed()) {
			glVertex2f(points[0], points[1]);
		}
		glEnd();
	}

	/**
	 * 填充指定图形
	 * 
	 * @param shape
	 */
	public final void fill(Shape shape) {
		if (isClose) {
			return;
		}
		if (shape == null) {
			return;
		}
		Triangle tris = shape.getTriangles();
		if (tris.getTriangleCount() == 0) {
			return;
		}
		glBegin(GL.GL_TRIANGLES);
		for (int i = 0; i < tris.getTriangleCount(); i++) {
			for (int p = 0; p < 3; p++) {
				float[] pt = tris.getTrianglePoint(i, p);
				glVertex2f(pt[0], pt[1]);
			}
		}
		glEnd();
	}

	/**
	 * 结合纹理绘制指定形状
	 * 
	 * @param shape
	 * @param image
	 * @param scaleX
	 * @param scaleY
	 */
	public void draw(Shape shape, final LTexture image, final float scaleX,
			final float scaleY) {
		if (shape == null) {
			return;
		}
		Triangle tris = shape.getTriangles();
		if (tris.getTriangleCount() == 0) {
			return;
		}

	}

	/**
	 * 结合纹理绘制指定形状，且自动修正大小
	 * 
	 * @param shape
	 * @param image
	 */
	public void drawFit(Shape shape, LTexture image) {
		drawFit(shape, image, 1f, 1f);
	}

	/**
	 * 结合纹理绘制指定形状，且自动修正大小
	 * 
	 * @param shape
	 * @param image
	 * @param scaleX
	 * @param scaleY
	 */
	public void drawFit(Shape shape, final LTexture image, final float scaleX,
			final float scaleY) {
		if (shape == null) {
			return;
		}
		Triangle tris = shape.getTriangles();
		if (tris.getTriangleCount() == 0) {
			return;
		}

	}

	/**
	 * 结合纹理绘制指定形状
	 * 
	 * @param shape
	 * @param image
	 */
	public void draw(Shape shape, LTexture image) {
		draw(shape, image, 0.01f, 0.01f);
	}

	/**
	 * 绘制多边形到指定位置
	 * 
	 * @param p
	 * @param x
	 * @param y
	 */
	public void draw(final Shape p, final float x, final float y) {
		if (isClose) {
			return;
		}

	}

	/**
	 * 绘制多边形为指定旋转方向
	 * 
	 * @param p
	 * @param rotation
	 */
	public void draw(final Shape p, final float rotation) {
		if (isClose) {
			return;
		}

	}

	/**
	 * 填充多边形到指定位置
	 * 
	 * @param p
	 * @param x
	 * @param y
	 */
	public void fill(final Shape p, final float x, final float y) {
		if (isClose) {
			return;
		}

	}

	/**
	 * 填充多边形为指定旋转方向
	 * 
	 * @param p
	 * @param rotation
	 */
	public void fill(final Shape p, final float rotation) {
		if (isClose) {
			return;
		}

	}

	/**
	 * 填充多边形
	 * 
	 * @param p
	 */
	public void fillPolygon(Polygon p) {
		fill(p);
	}

	/**
	 * 填充多边形
	 * 
	 * @param xPoints
	 * @param yPoints
	 * @param nPoints
	 */
	public void fillPolygon(float xPoints[], float yPoints[], int nPoints) {
		if (isClose) {
			return;
		}
		$fillPolygon(xPoints, yPoints, nPoints, true);
	}

	private final void $fillPolygon(float xPoints[], float yPoints[],
			int nPoints, boolean use) {
		if (use) {
			glBegin(GL.GL_POLYGON);
		}
		{
			for (int i = 0; i < nPoints; i++) {
				glVertex2f(xPoints[i], yPoints[i]);
			}
		}
		if (use) {
			glEnd();
		}
	}

	/**
	 * 绘制多边形轮廓
	 * 
	 * @param p
	 */
	public void drawPolygon(Polygon p) {
		draw(p);
	}

	/**
	 * 绘制多边形轮廓
	 * 
	 * @param xPoints
	 * @param yPoints
	 * @param nPoints
	 */
	public void drawPolygon(float[] xPoints, float[] yPoints, int nPoints) {
		if (isClose) {
			return;
		}
		$drawPolygon(xPoints, yPoints, nPoints, true);

	}

	private void $drawPolygon(float[] xPoints, float[] yPoints, int nPoints,
			boolean use) {
		if (use) {
			glBegin(GL.GL_LINE_LOOP);
		}
		for (int i = 0; i < nPoints; i++) {
			glVertex2f(xPoints[i], yPoints[i]);
		}
		if (use) {
			glEnd();
		}
	}

	/**
	 * 绘制一个矩形
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	public final void drawRect(final float x1, final float y1, final float x2,
			final float y2) {
		setRect(x1, y1, x2, y2, false);
	}

	/**
	 * 绘制一个矩形
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param color
	 */
	public final void drawRect(final float x1, final float y1, final float x2,
			final float y2, LColor color) {
		int argb = getColorARGB();
		setColor(color);
		setRect(x1, y1, x2, y2, false);
		setColor(argb);
	}

	/**
	 * 填充一个矩形
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	public final void fillRect(final float x1, final float y1, final float x2,
			final float y2) {
		setRect(x1, y1, x2, y2, true);
	}

	/**
	 * 填充一个矩形
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	public final void fillRect(final float x1, final float y1, final float x2,
			final float y2, LColor color) {
		int argb = getColorARGB();
		setColor(color);
		setRect(x1, y1, x2, y2, true);
		setColor(argb);
	}

	private float[] temp_xs = new float[4];

	private float[] temp_ys = new float[4];

	/**
	 * 设置矩形图案
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param fill
	 */
	public final void setRect(float x, float y, float width, float height,
			boolean fill) {
		if (isClose) {
			return;
		}

		temp_xs[0] = x;
		temp_xs[1] = x + width;
		temp_xs[2] = x + width;
		temp_xs[3] = x;

		temp_ys[0] = y;
		temp_ys[1] = y;
		temp_ys[2] = y + height;
		temp_ys[3] = y + height;

		if (fill) {
			fillPolygon(temp_xs, temp_ys, 4);
		} else {
			drawPolygon(temp_xs, temp_ys, 4);
		}
	}

	/**
	 * 绘制指定大小的弧度
	 * 
	 * @param rect
	 * @param segments
	 * @param start
	 * @param end
	 */
	public final void drawArc(RectBox rect, int segments, float start, float end) {
		drawArc(rect.x, rect.y, rect.width, rect.height, segments, start, end);
	}

	/**
	 * 绘制指定大小的弧度
	 * 
	 * @param x1
	 * @param y1
	 * @param width
	 * @param height
	 * @param segments
	 * @param start
	 * @param end
	 */
	public final void drawArc(float x1, float y1, float width, float height,
			int segments, float start, float end) {
		if (isClose) {
			return;
		}

		while (end < start) {
			end += 360;
		}
		float cx = x1 + (width / 2.0f);
		float cy = y1 + (height / 2.0f);
		glBegin(GL.GL_LINE_STRIP);
		int step = 360 / segments;
		for (float a = start; a < (end + step); a += step) {
			float ang = a;
			if (ang > end) {
				ang = end;
			}
			float x = (cx + (MathUtils.cos(MathUtils.toRadians(ang)) * width / 2.0f));
			float y = (cy + (MathUtils.sin(MathUtils.toRadians(ang)) * height / 2.0f));
			glVertex2f(x, y);
		}
		glEnd();
	}

	/**
	 * 填充指定大小的弧度
	 * 
	 * @param x1
	 * @param y1
	 * @param width
	 * @param height
	 * @param start
	 * @param end
	 */
	public final void fillArc(float x1, float y1, float width, float height,
			float start, float end) {
		fillArc(x1, y1, width, height, 40, start, end);
	}

	/**
	 * 填充指定大小的弧度
	 * 
	 * @param x1
	 * @param y1
	 * @param width
	 * @param height
	 * @param segments
	 * @param start
	 * @param end
	 */
	public final void fillArc(float x1, float y1, float width, float height,
			int segments, float start, float end) {
		if (isClose) {
			return;
		}

		while (end < start) {
			end += 360;
		}
		float cx = x1 + (width / 2.0f);
		float cy = y1 + (height / 2.0f);
		glBegin(GL.GL_TRIANGLE_FAN);
		int step = 360 / segments;
		glVertex2f(cx, cy);
		for (float a = start; a < (end + step); a += step) {
			float ang = a;
			if (ang > end) {
				ang = end;
			}

			float x = (cx + (MathUtils.cos(MathUtils.toRadians(ang)) * width / 2.0f));
			float y = (cy + (MathUtils.sin(MathUtils.toRadians(ang)) * height / 2.0f));

			glVertex2f(x, y);
		}
		glEnd();
		if (isAntialias) {
			glBegin(GL.GL_TRIANGLE_FAN);
			glVertex2f(cx, cy);
			if (end != 360) {
				end -= 10;
			}
			for (float j = start; j < (end + step); j += step) {
				float ang = j;
				if (ang > end) {
					ang = end;
				}

				float x = (cx + (MathUtils.cos(MathUtils.toRadians(ang + 10))
						* width / 2.0f));
				float y = (cy + (MathUtils.sin(MathUtils.toRadians(ang + 10))
						* height / 2.0f));

				glVertex2f(x, y);
			}
			glEnd();
		}
	}

	/**
	 * 绘制圆形边框
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param radius
	 */
	public final void drawRoundRect(float x, float y, float width,
			float height, int radius) {
		drawRoundRect(x, y, width, height, radius, 40);
	}

	/**
	 * 绘制圆形边框
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param radius
	 * @param segs
	 */
	public final void drawRoundRect(float x, float y, float width,
			float height, int radius, int segs) {
		if (isClose) {
			return;
		}
		if (radius < 0) {
			throw new IllegalArgumentException("radius > 0");
		}
		if (radius == 0) {
			drawRect(x, y, width, height);
			return;
		}
		int mr = (int) MathUtils.min(width, height) / 2;
		if (radius > mr) {
			radius = mr;
		}
		drawLine(x + radius, y, x + width - radius, y);
		drawLine(x, y + radius, x, y + height - radius);
		drawLine(x + width, y + radius, x + width, y + height - radius);
		drawLine(x + radius, y + height, x + width - radius, y + height);
		float d = radius * 2;
		drawArc(x + width - d, y + height - d, d, d, segs, 0, 90);
		drawArc(x, y + height - d, d, d, segs, 90, 180);
		drawArc(x + width - d, y, d, d, segs, 270, 360);
		drawArc(x, y, d, d, segs, 180, 270);
	}

	/**
	 * 填充圆形边框
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param cornerRadius
	 */
	public final void fillRoundRect(float x, float y, float width,
			float height, int cornerRadius) {
		fillRoundRect(x, y, width, height, cornerRadius, 40);
	}

	/**
	 * 填充圆形边框
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param radius
	 * @param segs
	 */
	public final void fillRoundRect(float x, float y, float width,
			float height, int radius, int segs) {
		if (isClose) {
			return;
		}
		if (radius < 0) {
			throw new IllegalArgumentException("radius > 0");
		}
		if (radius == 0) {
			fillRect(x, y, width, height);
			return;
		}
		int mr = (int) MathUtils.min(width, height) / 2;
		if (radius > mr) {
			radius = mr;
		}
		float d = radius * 2;
		fillRect(x + radius, y, width - d, radius);
		fillRect(x, y + radius, radius, height - d);
		fillRect(x + width - radius, y + radius, radius, height - d);
		fillRect(x + radius, y + height - radius, width - d, radius);
		fillRect(x + radius, y + radius, width - d, height - d);
		fillArc(x + width - d, y + height - d, d, d, segs, 0, 90);
		fillArc(x, y + height - d, d, d, segs, 90, 180);
		fillArc(x + width - d, y, d, d, segs, 270, 360);
		fillArc(x, y, d, d, segs, 180, 270);
	}

	public final void setLineWidth(float width) {
		if (isClose) {
			return;
		}

	}

	public void resetLineWidth() {
		if (isClose) {
			return;
		}

	}

	public final float getLineWidth() {
		return lineWidth;
	}

	final static void updateHardwareBuff(LTexture texture) {

	}

	final static void bufferDataARR(int bufferID, FloatBuffer data, int usage) {

	}

	final static void bufferSubDataARR(int bufferID, int offset,
			FloatBuffer data) {

	}

	public static void checkError() {
		try {
			tryError();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void tryError() throws Exception {
		int id = gl.glGetError();
		if (id != GL20.GL_NO_ERROR) {
			String method = Thread.currentThread().getStackTrace()[3]
					.getMethodName();
			throw new Exception(id + ":" + method);
		}
	}

	/**
	 * 验证是否为2的N次幂
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isPowerOfTwo(int value) {
		return value != 0 && (value & value - 1) == 0;
	}

	/**
	 * 转换数值为2的N次幂
	 * 
	 * @param value
	 * @return
	 */
	public static int toPowerOfTwo(int value) {
		if (value == 0) {
			return 1;
		}
		if ((value & value - 1) == 0) {
			return value;
		}
		value |= value >> 1;
		value |= value >> 2;
		value |= value >> 4;
		value |= value >> 8;
		value |= value >> 16;
		return value + 1;
	}

	/**
	 * 清空剪切后的显示区域
	 * 
	 */
	public void clearClip() {
		if (isClose) {
			return;
		}
		try {
			if (isScissorTest) {
				gl.glDisable(GL20.GL_SCISSOR_TEST);
				isScissorTest = false;
			}
			clip.setBounds(0, 0, viewPort.width, viewPort.height);
			gl.glScissor(0, 0, (int) (viewPort.width * LSystem.scaleWidth),
					(int) (viewPort.height * LSystem.scaleHeight));
		} catch (Exception e) {
		}
	}

	/**
	 * 设定指定剪切区域显示图像
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public final void setClip(int x, int y, int width, int height) {
		if (isClose) {
			return;
		}
		if (!isScissorTest) {
			gl.glEnable(GL20.GL_SCISSOR_TEST);
			isScissorTest = true;
		}
		clip.setBounds(x, y, width, height);
		gl.glScissor(
				(int) (x * LSystem.scaleWidth),
				(int) ((LSystem.screenRect.height - y - height) * LSystem.scaleHeight),
				(int) (width * LSystem.scaleWidth),
				(int) (height * LSystem.scaleHeight));
	}

	/**
	 * 设定指定剪切区域显示图像
	 * 
	 * @param c
	 */
	public final void setClip(Clip c) {
		if (isClose) {
			return;
		}
		if (c == null) {
			clearClip();
			return;
		}
		setClip(c.x, c.y, c.width, c.height);
	}

	/**
	 * 返回当前的剪切区域
	 * 
	 * @return
	 */
	public Clip getClip() {
		return new Clip(clip);
	}

	public int getClipHeight() {
		return clip.height;
	}

	public int getClipWidth() {
		return clip.width;
	}

	public int getClipX() {
		return clip.x;
	}

	public int getClipY() {
		return clip.y;
	}

	public final void setViewPort(int x, int y, int width, int height) {
		if (isClose) {
			return;
		}

	}

	public final void setViewPort(RectBox port) {
		setViewPort((int) port.x, (int) port.y, port.width, port.height);
	}

	public final RectBox getViewPort() {
		return viewPort;
	}

	public void save() {
		if (isClose) {
			return;
		}
		if (!isPushed) {

		}
	}

	public void restore() {
		if (isClose) {
			return;
		}
		this._lastAlpha = 1;
		this.sx = 1;
		this.sy = 1;
		if (isPushed) {

			isPushed = false;
		}
		resetFont();
	}

	public void scale(float sx, float sy) {
		if (isClose) {
			return;
		}
		save();
		this.sx = this.sx * sx;
		this.sy = this.sy * sy;

	}

	public void rotate(float rx, float ry, float angle) {
		if (isClose) {
			return;
		}
		save();
		translate(rx, ry);

		translate(-rx, -ry);
	}

	public final void rotate(float angle) {
		float centerX = viewPort.width / 2;
		float centerY = viewPort.height / 2;
		rotate(angle, centerX, centerY);
	}

	public void translate(float x, float y) {
		if (isClose) {
			return;
		}
		save();
		translateX = x;
		translateY = y;

		clip.x -= x;
		clip.width -= x;
		clip.y -= y;
		clip.height -= y;
	}

	public void glTranslatef(float x, float y, float z) {
		if (isClose) {
			return;
		}

	}

	public void glRotatef(float angle, float x, float y, float z) {
		if (isClose) {
			return;
		}

	}

	/**
	 * 设定背景颜色
	 * 
	 * @param color
	 */
	public final void setBackground(LColor color) {
		if (isClose) {
			return;
		}
		gl.glClearColor(color.r, color.g, color.b, color.a);
	}

	/**
	 * 渲染纹理到指定位置
	 * 
	 * @param texture
	 * @param x
	 * @param y
	 */
	public final void drawTexture(LTexture texture, float x, float y) {
		if (isClose || texture == null || texture.isClose) {
			return;
		}
		if (!texture.isLoaded) {
			texture.loadTexture();
		}
		drawTexture(texture, x, y, texture.width, texture.height, 0, 0,
				texture.width, texture.height, color, 0, null, null);
	}

	/**
	 * 渲染纹理到指定位置
	 * 
	 * @param texture
	 * @param x
	 * @param y
	 * @param rotation
	 * @param d
	 */
	public final void drawTexture(LTexture texture, float x, float y,
			float rotation, Direction d) {
		if (isClose || texture == null || texture.isClose) {
			return;
		}
		if (!texture.isLoaded) {
			texture.loadTexture();
		}
		drawTexture(texture, x, y, texture.width, texture.height, 0, 0,
				texture.width, texture.height, color, rotation, null, d);
	}

	/**
	 * 渲染纹理到指定位置并修正为指定色彩
	 * 
	 * @param texture
	 * @param x
	 * @param y
	 * @param color
	 */
	public final void drawTexture(LTexture texture, float x, float y,
			LColor color) {
		if (isClose || texture == null || texture.isClose) {
			return;
		}
		if (!texture.isLoaded) {
			texture.loadTexture();
		}
		drawTexture(texture, x, y, texture.width, texture.height, 0, 0,
				texture.width, texture.height, color, 0, null, null);
	}

	/**
	 * 渲染纹理到指定位置并修正为指定角度
	 * 
	 * @param texture
	 * @param x
	 * @param y
	 * @param rotation
	 */
	public final void drawTexture(LTexture texture, float x, float y,
			float rotation) {
		if (isClose || texture == null || texture.isClose) {
			return;
		}
		drawTexture(texture, x, y, texture.width, texture.height, 0, 0,
				texture.width, texture.height, color, rotation, null, null);
	}

	/**
	 * 渲染纹理为指定状态
	 * 
	 * @param texture
	 * @param x
	 * @param y
	 * @param dir
	 */
	public final void drawTexture(LTexture texture, float x, float y,
			Direction dir) {
		if (isClose || texture == null || texture.isClose) {
			return;
		}
		if (!texture.isLoaded) {
			texture.loadTexture();
		}
		drawTexture(texture, x, y, texture.width, texture.height, 0, 0,
				texture.width, texture.height, color, 0, null, dir);
	}

	/**
	 * 渲染纹理为指定状态
	 * 
	 * @param texture
	 * @param x
	 * @param y
	 * @param color
	 * @param rotation
	 */
	public final void drawTexture(LTexture texture, float x, float y,
			LColor color, float rotation) {
		if (isClose || texture == null || texture.isClose) {
			return;
		}
		if (!texture.isLoaded) {
			texture.loadTexture();
		}
		drawTexture(texture, x, y, texture.width, texture.height, 0, 0,
				texture.width, texture.height, color, rotation, null, null);
	}

	public final void drawFlipTexture(LTexture texture, float x, float y,
			LColor color) {
		if (isClose || texture == null || texture.isClose) {
			return;
		}
		drawTexture(texture, x, y, texture.width, texture.height, 0, 0,
				texture.width, texture.height, color, 0, null,
				Direction.TRANS_FILP);
	}

	public final void drawMirrorTexture(LTexture texture, float x, float y,
			LColor color) {
		if (isClose || texture == null || texture.isClose) {
			return;
		}
		drawTexture(texture, x, y, texture.width, texture.height, 0, 0,
				texture.width, texture.height, color, 0, null,
				Direction.TRANS_MIRROR);
	}

	/**
	 * 渲染纹理为指定状态
	 * 
	 * @param texture
	 * @param x
	 * @param y
	 * @param color
	 * @param dir
	 */
	public final void drawTexture(LTexture texture, float x, float y,
			LColor color, Direction dir) {
		if (isClose || texture == null || texture.isClose) {
			return;
		}
		if (!texture.isLoaded) {
			texture.loadTexture();
		}
		drawTexture(texture, x, y, texture.width, texture.height, 0, 0,
				texture.width, texture.height, color, 0, null, dir);
	}

	/**
	 * 渲染纹理为指定状态
	 * 
	 * @param texture
	 * @param x
	 * @param y
	 * @param color
	 * @param rotation
	 * @param origin
	 * @param dir
	 */
	public final void drawTexture(LTexture texture, float x, float y,
			LColor color, float rotation, Vector2f origin, Direction dir) {
		if (isClose || texture == null || texture.isClose) {
			return;
		}
		if (!texture.isLoaded) {
			texture.loadTexture();
		}
		drawTexture(texture, x, y, texture.width, texture.height, 0, 0,
				texture.width, texture.height, color, rotation, origin, dir);
	}

	/**
	 * 渲染纹理为指定状态
	 * 
	 * @param texture
	 * @param x
	 * @param y
	 * @param color
	 * @param rotation
	 * @param origin
	 * @param scale
	 * @param dir
	 */
	public final void drawTexture(LTexture texture, float x, float y,
			LColor color, float rotation, Vector2f origin, float scale,
			Direction dir) {
		if (isClose || texture == null || texture.isClose) {
			return;
		}
		if (!texture.isLoaded) {
			texture.loadTexture();
		}
		drawTexture(texture, x, y, texture.width * scale, texture.height
				* scale, 0, 0, texture.width, texture.height, color, rotation,
				origin, dir);
	}

	/**
	 * 渲染纹理为指定状态
	 * 
	 * @param texture
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public final void drawTexture(LTexture texture, float x, float y,
			float width, float height) {
		if (isClose || texture == null || texture.isClose) {
			return;
		}
		if (!texture.isLoaded) {
			texture.loadTexture();
		}
		drawTexture(texture, x, y, width, height, 0, 0, texture.width,
				texture.height, color, 0, null, null);
	}

	/**
	 * 渲染纹理为指定状态
	 * 
	 * @param texture
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param color
	 */
	public final void drawTexture(LTexture texture, float x, float y,
			float width, float height, LColor color) {
		if (isClose || texture == null || texture.isClose) {
			return;
		}
		if (!texture.isLoaded) {
			texture.loadTexture();
		}
		drawTexture(texture, x, y, width, height, 0, 0, texture.width,
				texture.height, color, 0, null, null);
	}

	/**
	 * 渲染纹理为指定状态
	 * 
	 * @param texture
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param rotation
	 */
	public final void drawTexture(LTexture texture, float x, float y,
			float width, float height, float rotation) {
		if (isClose || texture == null || texture.isClose) {
			return;
		}
		if (!texture.isLoaded) {
			texture.loadTexture();
		}
		drawTexture(texture, x, y, width, height, 0, 0, texture.width,
				texture.height, color, rotation, null, null);
	}

	/**
	 * 渲染纹理为指定状态
	 * 
	 * @param texture
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param dir
	 */
	public final void drawTexture(LTexture texture, float x, float y,
			float width, float height, Direction dir) {
		if (isClose || texture == null || texture.isClose) {
			return;
		}
		if (!texture.isLoaded) {
			texture.loadTexture();
		}
		drawTexture(texture, x, y, width, height, 0, 0, texture.width,
				texture.height, color, 0, null, dir);
	}

	/**
	 * 渲染纹理为指定状态
	 * 
	 * @param texture
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param color
	 * @param rotation
	 */
	public final void drawTexture(LTexture texture, float x, float y,
			float width, float height, float rotation, LColor color) {
		if (isClose || texture == null || texture.isClose) {
			return;
		}
		if (!texture.isLoaded) {
			texture.loadTexture();
		}
		drawTexture(texture, x, y, width, height, 0, 0, texture.width,
				texture.height, color, rotation, null, null);
	}

	/**
	 * 渲染纹理为指定状态
	 * 
	 * @param texture
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param color
	 * @param dir
	 */
	public final void drawTexture(LTexture texture, float x, float y,
			float width, float height, LColor color, Direction dir) {
		if (isClose || texture == null || texture.isClose) {
			return;
		}
		if (!texture.isLoaded) {
			texture.loadTexture();
		}
		drawTexture(texture, x, y, width, height, 0, 0, texture.width,
				texture.height, color, 0, null, dir);
	}

	/**
	 * 渲染纹理为指定状态
	 * 
	 * @param texture
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param color
	 * @param rotation
	 * @param origin
	 * @param dir
	 */
	public final void drawTexture(LTexture texture, float x, float y,
			float width, float height, LColor color, float rotation,
			Vector2f origin, Direction dir) {
		if (isClose || texture == null || texture.isClose) {
			return;
		}
		if (!texture.isLoaded) {
			texture.loadTexture();
		}
		drawTexture(texture, x, y, width, height, 0, 0, texture.width,
				texture.height, color, rotation, origin, dir);
	}

	/**
	 * 渲染纹理为指定状态
	 * 
	 * @param texture
	 * @param dx1
	 * @param dy1
	 * @param dx2
	 * @param dy2
	 * @param sx1
	 * @param sy1
	 * @param sx2
	 * @param sy2
	 */
	public final void drawTexture(LTexture texture, float dx1, float dy1,
			float dx2, float dy2, float sx1, float sy1, float sx2, float sy2) {
		if (isClose || texture == null || texture.isClose) {
			return;
		}
		drawTexture(texture, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, color, 0,
				null, null);
	}

	/**
	 * 按照Java中近似作用函数切分纹理
	 * 
	 * @param texture
	 * @param dx1
	 * @param dy1
	 * @param dx2
	 * @param dy2
	 * @param sx1
	 * @param sy1
	 * @param sx2
	 * @param sy2
	 */
	public final void drawJavaTexture(LTexture texture, float dx1, float dy1,
			float dx2, float dy2, float sx1, float sy1, float sx2, float sy2) {
		if (isClose || texture == null || texture.isClose) {
			return;
		}
		drawTexture(texture, dx1, dy1, dx2 - dx1, dy2 - dy1, sx1, sy1, sx2,
				sy2, color, 0, null, null);
	}

	/**
	 * 渲染纹理为指定状态
	 * 
	 * @param texture
	 * @param dx1
	 * @param dy1
	 * @param dx2
	 * @param dy2
	 * @param sx1
	 * @param sy1
	 * @param sx2
	 * @param sy2
	 * @param color
	 */
	public final void drawTexture(LTexture texture, float dx1, float dy1,
			float dx2, float dy2, float sx1, float sy1, float sx2, float sy2,
			LColor color) {
		if (isClose || texture == null || texture.isClose) {
			return;
		}
		drawTexture(texture, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, color, 0,
				null, null);
	}

	/**
	 * 渲染纹理为指定状态
	 * 
	 * @param texture
	 * @param dx1
	 * @param dy1
	 * @param dx2
	 * @param dy2
	 * @param sx1
	 * @param sy1
	 * @param sx2
	 * @param sy2
	 * @param rotation
	 * @param color
	 */
	public final void drawTexture(LTexture texture, float dx1, float dy1,
			float dx2, float dy2, float sx1, float sy1, float sx2, float sy2,
			float rotation, LColor color) {
		if (isClose || texture == null || texture.isClose) {
			return;
		}
		drawTexture(texture, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, color,
				rotation, null, null);
	}

	/**
	 * 渲染纹理为指定状态
	 * 
	 * @param image
	 * @param x_src
	 * @param y_src
	 * @param width
	 * @param height
	 * @param transform
	 * @param x_dst
	 * @param y_dst
	 * @param anchor
	 */
	public void drawRegion(LImage image, int x_src, int y_src, int width,
			int height, int transform, int x_dst, int y_dst, int anchor) {
		if (isClose || image == null || image.isClose()) {
			return;
		}
		drawRegion(image.getTexture(), x_src, y_src, width, height, transform,
				x_dst, y_dst, anchor, color);
	}

	/**
	 * 渲染纹理为指定状态
	 * 
	 * @param image
	 * @param x_src
	 * @param y_src
	 * @param width
	 * @param height
	 * @param transform
	 * @param x_dst
	 * @param y_dst
	 * @param anchor
	 * @param c
	 */
	public void drawRegion(LImage image, int x_src, int y_src, int width,
			int height, int transform, int x_dst, int y_dst, int anchor,
			LColor c) {
		if (isClose || image == null || image.isClose()) {
			return;
		}
		drawRegion(image.getTexture(), x_src, y_src, width, height, transform,
				x_dst, y_dst, anchor, c);
	}

	/**
	 * 渲染纹理为指定状态
	 * 
	 * @param texture
	 * @param x_src
	 * @param y_src
	 * @param width
	 * @param height
	 * @param transform
	 * @param x_dst
	 * @param y_dst
	 * @param anchor
	 */
	public void drawRegion(LTexture texture, int x_src, int y_src, int width,
			int height, int transform, int x_dst, int y_dst, int anchor) {
		drawRegion(texture, x_src, y_src, width, height, transform, x_dst,
				y_dst, anchor, color);
	}

	/**
	 * 渲染纹理为指定状态
	 * 
	 * @param texture
	 * @param x_src
	 * @param y_src
	 * @param width
	 * @param height
	 * @param transform
	 * @param x_dst
	 * @param y_dst
	 * @param anchor
	 */
	public void drawJavaRegion(LTexture texture, int x_src, int y_src,
			int width, int height, int transform, int x_dst, int y_dst,
			int anchor) {
		drawRegion(texture, x_src, y_src, width - x_src, height - y_src,
				transform, x_dst, y_dst, anchor, color);
	}

	/**
	 * 渲染纹理为指定状态
	 * 
	 * @param texture
	 * @param x_src
	 * @param y_src
	 * @param width
	 * @param height
	 * @param transform
	 * @param x_dst
	 * @param y_dst
	 * @param anchor
	 * @param c
	 */
	public void drawRegion(LTexture texture, int x_src, int y_src, int width,
			int height, int transform, int x_dst, int y_dst, int anchor,
			LColor c) {
		if (isClose || texture == null || texture.isClose) {
			return;
		}
		if (x_src + width > texture.getWidth()
				|| y_src + height > texture.getHeight() || width < 0
				|| height < 0 || x_src < 0 || y_src < 0) {
			throw new IllegalArgumentException("Area out of texture");
		}
		int dW = width, dH = height;

		float rotate = 0;
		Direction dir = Direction.TRANS_NONE;

		switch (transform) {
		case TRANS_NONE: {
			break;
		}
		case TRANS_ROT90: {
			rotate = 90;
			dW = height;
			dH = width;
			break;
		}
		case TRANS_ROT180: {
			rotate = 180;
			break;
		}
		case TRANS_ROT270: {
			rotate = 270;
			dW = height;
			dH = width;
			break;
		}
		case TRANS_MIRROR: {
			dir = Direction.TRANS_MIRROR;
			break;
		}
		case TRANS_MIRROR_ROT90: {
			dir = Direction.TRANS_MIRROR;
			rotate = -90;
			dW = height;
			dH = width;
			break;
		}
		case TRANS_MIRROR_ROT180: {
			dir = Direction.TRANS_MIRROR;
			rotate = -180;
			break;
		}
		case TRANS_MIRROR_ROT270: {
			dir = Direction.TRANS_MIRROR;
			rotate = -270;
			dW = height;
			dH = width;
			break;
		}
		default:
			throw new IllegalArgumentException("Bad transform");
		}

		boolean badAnchor = false;

		if (anchor == 0) {
			anchor = LGraphics.TOP | LGraphics.LEFT;
		}

		if ((anchor & 0x7f) != anchor || (anchor & LGraphics.BASELINE) != 0) {
			badAnchor = true;
		}

		if ((anchor & LGraphics.TOP) != 0) {
			if ((anchor & (LGraphics.VCENTER | LGraphics.BOTTOM)) != 0) {
				badAnchor = true;
			}
		} else if ((anchor & LGraphics.BOTTOM) != 0) {
			if ((anchor & LGraphics.VCENTER) != 0) {
				badAnchor = true;
			} else {
				y_dst -= dH - 1;
			}
		} else if ((anchor & LGraphics.VCENTER) != 0) {
			y_dst -= (dH - 1) >>> 1;
		} else {
			badAnchor = true;
		}

		if ((anchor & LGraphics.LEFT) != 0) {
			if ((anchor & (LGraphics.HCENTER | LGraphics.RIGHT)) != 0) {
				badAnchor = true;
			}
		} else if ((anchor & LGraphics.RIGHT) != 0) {
			if ((anchor & LGraphics.HCENTER) != 0) {
				badAnchor = true;
			} else {
				x_dst -= dW - 1;
			}
		} else if ((anchor & LGraphics.HCENTER) != 0) {
			x_dst -= (dW - 1) >>> 1;
		} else {
			badAnchor = true;
		}
		if (badAnchor) {
			throw new IllegalArgumentException("Bad Anchor");
		}

		drawTexture(texture, x_dst, y_dst, width, height, x_src, y_src, x_src
				+ width, y_src + height, c, rotate, null, dir);
	}

	/**
	 * 渲染纹理为指定状态
	 * 
	 * @param texture
	 * @param destRect
	 */
	public final void drawTexture(LTexture texture, RectBox destRect) {
		if (isClose || texture == null || texture.isClose) {
			return;
		}
		if (!texture.isLoaded) {
			texture.loadTexture();
		}
		drawTexture(texture, destRect.x, destRect.y, destRect.width,
				destRect.height, 0, 0, texture.width, texture.height, color, 0,
				null, null);
	}

	/**
	 * 渲染纹理为指定状态
	 * 
	 * @param texture
	 * @param destRect
	 * @param color
	 */
	public final void drawTexture(LTexture texture, RectBox destRect,
			LColor color) {
		if (isClose || texture == null || texture.isClose) {
			return;
		}
		if (!texture.isLoaded) {
			texture.loadTexture();
		}
		drawTexture(texture, destRect.x, destRect.y, destRect.width,
				destRect.height, 0, 0, texture.width, texture.height, color, 0,
				null, null);
	}

	/**
	 * 渲染纹理为指定状态
	 * 
	 * @param texture
	 * @param destRect
	 * @param srcRect
	 */
	public final void drawTexture(LTexture texture, RectBox destRect,
			RectBox srcRect) {
		if (isClose || texture == null || texture.isClose) {
			return;
		}
		if (!texture.isLoaded) {
			texture.loadTexture();
		}
		drawTexture(texture, destRect.x, destRect.y, destRect.width,
				destRect.height, srcRect.x, srcRect.y, srcRect.width,
				srcRect.height, color, 0, null, null);
	}

	/**
	 * 渲染纹理为指定状态
	 * 
	 * @param texture
	 * @param destRect
	 * @param srcRect
	 * @param color
	 */
	public final void drawTexture(LTexture texture, RectBox destRect,
			RectBox srcRect, LColor color) {
		if (isClose || texture == null || texture.isClose) {
			return;
		}
		if (!texture.isLoaded) {
			texture.loadTexture();
		}
		drawTexture(texture, destRect.x, destRect.y, destRect.width,
				destRect.height, srcRect.x, srcRect.y, srcRect.width,
				srcRect.height, color, 0, null, null);
	}

	/**
	 * 渲染纹理为指定状态
	 * 
	 * @param texture
	 * @param destRect
	 * @param srcRect
	 * @param rotation
	 */
	public final void drawTexture(LTexture texture, RectBox destRect,
			RectBox srcRect, float rotation) {
		if (isClose || texture == null || texture.isClose) {
			return;
		}
		if (!texture.isLoaded) {
			texture.loadTexture();
		}
		drawTexture(texture, destRect.x, destRect.y, destRect.width,
				destRect.height, srcRect.x, srcRect.y, srcRect.width,
				srcRect.height, color, rotation, null, null);
	}

	/**
	 * 渲染纹理为指定状态
	 * 
	 * @param texture
	 * @param position
	 */
	public final void drawTexture(LTexture texture, Vector2f position) {
		if (isClose || texture == null || texture.isClose) {
			return;
		}
		if (!texture.isLoaded) {
			texture.loadTexture();
		}
		drawTexture(texture, position.x, position.y, texture.width,
				texture.height, 0, 0, texture.width, texture.height, color, 0,
				null, null);
	}

	/**
	 * 渲染纹理为指定状态
	 * 
	 * @param texture
	 * @param position
	 * @param color
	 */
	public final void drawTexture(LTexture texture, Vector2f position,
			LColor color) {
		if (isClose || texture == null || texture.isClose) {
			return;
		}
		if (!texture.isLoaded) {
			texture.loadTexture();
		}
		drawTexture(texture, position.x, position.y, texture.width,
				texture.height, 0, 0, texture.width, texture.height, color, 0,
				null, null);
	}

	/**
	 * 渲染纹理为指定设置
	 * 
	 * @param texture
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param srcX
	 * @param srcY
	 * @param srcWidth
	 * @param srcHeight
	 * @param c
	 * @param rotation
	 */
	public final void drawTexture(LTexture texture, float x, float y,
			float width, float height, float srcX, float srcY, float srcWidth,
			float srcHeight, LColor c, float rotation) {
		drawTexture(texture, x, y, width, height, srcX, srcY, srcWidth,
				srcHeight, c, rotation, null, null);
	}

	private boolean updateColor;

	/**
	 * 渲染纹理为指定设置
	 * 
	 * @param texture
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param srcX
	 * @param srcY
	 * @param srcWidth
	 * @param srcHeight
	 * @param color
	 * @param rotation
	 * @param origin
	 * @param dir
	 */
	private final void drawTexture(LTexture texture, float x, float y,
			float width, float height, float srcX, float srcY, float srcWidth,
			float srcHeight, LColor c, float rotation, Vector2f origin,
			Direction dir) {

		if (isClose) {
			return;
		}

		if (!texture.isVisible) {
			return;
		}
		if (!texture.isLoaded) {
			texture.loadTexture();
		}

		if (checkAlpha(c)) {
			return;
		}

		glTex2DEnable();
		{

			bind(texture.textureID);

			if (!texture.isStatic) {
				updateColor = (c != null && !color.equals(c));
				if (updateColor) {
					GL_MODULATE();

				}
			}

		}
	}

	private LTexture lastTextre;

	public LTexture getLastTexture() {
		return lastTextre;
	}

	private float lastX, lastY, lastWidth, LastHeight;

	/**
	 * 将指定纹理文件作为矩形区域注入画布
	 * 
	 * @param texture
	 * @param srcX
	 * @param srcY
	 * @param srcWidth
	 * @param srcHeight
	 */
	private final void put(LTexture texture, float srcX, float srcY,
			float srcWidth, float srcHeight) {

		if (lastTextre != texture || lastX != srcX || lastY != srcY
				|| lastWidth != srcWidth || LastHeight != srcHeight) {
			final float invTexWidth = (1f / texture.width) * texture.widthRatio;
			final float invTexHeight = (1f / texture.height)
					* texture.heightRatio;

			final float xOff = srcX * invTexWidth + texture.xOff;
			final float yOff = srcY * invTexHeight + texture.yOff;
			final float widthRatio = srcWidth * invTexWidth;
			final float heightRatio = srcHeight * invTexHeight;

			rectDataCords[8] = xOff;
			rectDataCords[9] = yOff;
			rectDataCords[10] = widthRatio;
			rectDataCords[11] = yOff;
			rectDataCords[12] = xOff;
			rectDataCords[13] = heightRatio;
			rectDataCords[14] = widthRatio;
			rectDataCords[15] = heightRatio;

			lastTextre = texture;
			lastX = srcX;
			lastY = srcY;
			lastWidth = srcWidth;
			LastHeight = srcHeight;

			rectData.put(rectDataCords, 8, 8);
			rectData.position(8);
		}

	}

	/**
	 * 清空画布为指定色彩
	 * 
	 * @param r
	 * @param g
	 * @param b
	 */
	public void clear(float r, float g, float b) {

	}

	/**
	 * 输出字符串
	 * 
	 * @param string
	 * @param position
	 */
	public final void drawString(String string, Vector2f position) {
		drawString(string, position.x, position.y, color);
	}

	/**
	 * 输出字符串
	 * 
	 * @param string
	 * @param position
	 * @param color
	 */
	public final void drawString(String string, Vector2f position, LColor color) {
		drawString(string, position.x, position.y, color);
	}

	/**
	 * 输出字符串
	 * 
	 * @param string
	 * @param x
	 * @param y
	 */
	public final void drawString(String string, float x, float y) {
		drawString(string, x, y, color);
	}

	/**
	 * 输出字符串
	 * 
	 * @param string
	 * @param x
	 * @param y
	 * @param color
	 */
	public final void drawString(String string, float x, float y, LColor color) {
		if (isClose) {
			return;
		}
		drawString(string, x, y, 0, color);
	}

	/**
	 * 输出字符串
	 * 
	 * @param string
	 * @param x
	 * @param y
	 * @param rotation
	 */
	public final void drawString(String string, float x, float y, float rotation) {
		if (isClose) {
			return;
		}
		drawString(string, x, y, rotation, color);
	}

	/**
	 * 输出字符串
	 * 
	 * @param string
	 * @param x
	 * @param y
	 * @param rotation
	 * @param c
	 * @param check
	 */
	public void drawString(String string, float x, float y, float rotation,
			LColor c) {
		if (isClose || c == null || checkAlpha(c)) {
			return;
		}
		if (string == null || string.length() == 0) {
			return;
		}
		y = y - font.getAscent();

	}

	/**
	 * 输出字符
	 * 
	 * @param chars
	 * @param x
	 * @param y
	 */
	public void drawChar(char chars, float x, float y) {
		drawChar(chars, x, y, 0);
	}

	/**
	 * 输出字符
	 * 
	 * @param chars
	 * @param x
	 * @param y
	 * @param rotation
	 */
	public void drawChar(char chars, float x, float y, float rotation) {
		drawChar(chars, x, y, rotation, color);
	}

	/**
	 * 输出字符
	 * 
	 * @param chars
	 * @param x
	 * @param y
	 * @param rotation
	 * @param c
	 */
	public void drawChar(char chars, float x, float y, float rotation, LColor c) {
		drawString(String.valueOf(chars), x, y, rotation, c);
	}

	/**
	 * 输出字符
	 * 
	 * @param message
	 * @param offset
	 * @param length
	 * @param x
	 * @param y
	 */
	public void drawBytes(byte[] message, int offset, int length, int x, int y) {
		if (isClose) {
			return;
		}
		drawString(new String(message, offset, length), x, y);
	}

	/**
	 * 输出字符
	 * 
	 * @param message
	 * @param offset
	 * @param length
	 * @param x
	 * @param y
	 */
	public void drawChars(char[] message, int offset, int length, int x, int y) {
		if (isClose) {
			return;
		}
		drawString(new String(message, offset, length), x, y);
	}

	/**
	 * 输出字符
	 * 
	 * @param message
	 * @param x
	 * @param y
	 * @param anchor
	 */
	public void drawString(String message, int x, int y, int anchor) {
		if (isClose) {
			return;
		}
		int newx = x;
		int newy = y;
		if (anchor == 0) {
			anchor = LGraphics.TOP | LGraphics.LEFT;
		}
		if ((anchor & LGraphics.TOP) != 0) {
			newy -= font.getAscent();
		} else if ((anchor & LGraphics.BOTTOM) != 0) {
			newy -= font.getAscent();
		}
		if ((anchor & LGraphics.HCENTER) != 0) {
			newx -= font.stringWidth(message) / 2;
		} else if ((anchor & LGraphics.RIGHT) != 0) {
			newx -= font.stringWidth(message);
		}
		drawString(message, newx, newy);
	}

	public void drawStyleString(String message, int x, int y, int color,
			int color1) {
		if (isClose) {
			return;
		}
		setColor(color);
		drawString(message, x + 1, y);
		drawString(message, x - 1, y);
		drawString(message, x, y + 1);
		drawString(message, x, y - 1);
		setColor(color1);
		drawString(message, x, y);
	}

	public void drawStyleString(String message, int x, int y, LColor c1,
			LColor c2) {
		if (isClose) {
			return;
		}
		setColorRGB(c1);
		drawString(message, x + 1, y);
		drawString(message, x - 1, y);
		drawString(message, x, y + 1);
		drawString(message, x, y - 1);
		setColorRGB(c2);
		drawString(message, x, y);
	}

	/**
	 * 绑定指定纹理ID
	 * 
	 * @param id
	 */
	public void bind(int id) {
		if (lazyTextureID != id) {
			gl.glBindTexture(GL20.GL_TEXTURE_2D, id);
			lazyTextureID = id;
		}
	}

	public void bind(LTexture tex2d) {
		if (!tex2d.isVisible) {
			return;
		}
		if (!tex2d.isLoaded) {
			tex2d.loadTexture();
		}
		bind(tex2d.textureID);
	}

	public final int getBlendMode() {
		return this.currentBlendMode;
	}

	private boolean _isReplace = false;

	public final void GL_REPLACE() {

	}

	public final void GL_MODULATE() {

	}

	public void resetFont() {
		this.font = LFont.getDefaultFont();
		this.resetColor();
	}

	public void setFont(LFont font) {
		this.font = font;
	}

	public LFont getFont() {
		return this.font;
	}

	public final void set2DStateOn() {

	}

	public final void set2DStateOff() {

	}

	public void savePrj() {

	}

	public void restorePrj() {

	}

	public void saveMatrices() {

	}

	public void restoreMatrices() {

	}

	public void setMatrixMode(Matrix m) {
		if (isClose) {
			return;
		}

	}

	private final boolean checkAlpha(LColor c) {
		return _lastAlpha < 0.1f;
	}

	private final boolean checkSave(LTexture texture, float x, float y,
			float width, float height, float rotation, Direction dir) {
		return x != 0 || y != 0 || width != texture.width
				|| height != texture.height || dir != Direction.TRANS_NONE;
	}

	public final float getTranslateX() {
		return translateX;
	}

	public final float getTranslateY() {
		return translateY;
	}

	public final boolean isClose() {
		return isClose;
	}

	public final void dispose() {
		isClose = true;
		if (glBatch != null) {
			glBatch.dispose();
		}
	}

}
