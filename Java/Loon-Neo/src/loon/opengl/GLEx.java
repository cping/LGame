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
package loon.opengl;

import loon.Graphics;
import loon.LRelease;
import loon.LSystem;
import loon.LTexture;
import loon.LTrans;
import loon.action.camera.BaseCamera;
import loon.canvas.LColor;
import loon.canvas.Paint;
import loon.canvas.PixmapFImpl;
import loon.canvas.Paint.Style;
import loon.font.IFont;
import loon.geom.Affine2f;
import loon.geom.Ellipse;
import loon.geom.Matrix3;
import loon.geom.Matrix4;
import loon.geom.Polygon;
import loon.geom.RectBox;
import loon.geom.RectF;
import loon.geom.RectI;
import loon.geom.Shape;
import loon.geom.Triangle2f;
import loon.geom.Vector2f;
import loon.geom.XY;
import loon.opengl.GLRenderer.GLType;
import loon.utils.Array;
import loon.utils.GLUtils;
import loon.utils.MathUtils;
import loon.utils.TArray;

public class GLEx extends PixmapFImpl implements LRelease {

	/*
	 * 内部类，用来保存与复位GLEx的基本渲染参数
	 */
	private class BrushSave {
		int baseColor = LColor.DEF_COLOR;
		int fillColor = LColor.DEF_COLOR;
		int pixSkip = def_skip;
		float lineWidth = 1f;
		float baseAlpha = 1f;
		int blend = LSystem.MODE_NORMAL;

		boolean alltextures = false;
		IFont font = null;
		LTexture patternTex = null;

		BrushSave cpy() {
			BrushSave save = new BrushSave();
			save.baseColor = this.baseColor;
			save.fillColor = this.fillColor;
			save.pixSkip = this.pixSkip;
			save.lineWidth = this.lineWidth;
			save.alltextures = this.alltextures;
			save.font = this.font;
			save.patternTex = this.patternTex;
			save.blend = this.blend;
			return save;
		}
	}

	public static enum Direction {
		TRANS_NONE, TRANS_MIRROR, TRANS_FLIP, TRANS_MF;
	}

	private LColor tmpColor = new LColor();

	private final Array<Affine2f> affineStack = new Array<Affine2f>();

	private final Array<BrushSave> brushStack = new Array<BrushSave>();

	private final TArray<RectBox> scissors = new TArray<RectBox>();

	private final LTexture colorTex;

	protected final RenderTarget target;

	private int scissorDepth;

	private boolean isClosed = false;

	private Graphics gfx;

	private BaseBatch batch;

	private Affine2f lastTrans;

	private BrushSave lastBrush;

	private float scaleX = 1f, scaleY = 1f;

	private float offsetStringX = 0, offsetStringY = 0;

	/**
	 * 创建一个默认的GL渲染封装，将其作为默认的渲染器来使用。与0.5以前版本不同的是,此GLEX将不再唯一，允许复数构建.
	 * 如果使用HTML5，则禁止非纹理的渲染方式（因为部分浏览器不支持，会自动用纹理方式替代，但是glBegin到glEnd的
	 * 直接像素渲染方式则会禁用）.
	 * 
	 * PS:只有在LGame中注入的，可以影响全局渲染.
	 * 
	 * @param gfx
	 * @param target
	 * @param def
	 * @param alltex
	 */
	public GLEx(Graphics gfx, RenderTarget target, BaseBatch def, boolean alltex) {
		super(0f, 0f, LSystem.viewSize.getRect(), LSystem.viewSize.width, LSystem.viewSize.height, def_skip);
		this.gfx = gfx;
		this.target = target;
		this.batch = def;
		this.affineStack.add(lastTrans = new Affine2f());
		this.colorTex = gfx.finalColorTex();
		this.scale(scaleX = target.xscale(), scaleY = target.yscale());
		this.lastBrush = new BrushSave();
		this.lastBrush.font = LSystem.getSystemGameFont();
		this.lastBrush.alltextures = alltex;
		this.lastBrush.pixSkip = LSystem.isHTML5() ? def_skip_html5 : def_skip;
		this.lastBrush.blend = LSystem.MODE_NORMAL;
		this.brushStack.add(lastBrush);
		this.update();
	}

	public GLEx(Graphics gfx, RenderTarget target, GL20 gl) {
		this(gfx, target, createDefaultBatch(gl), false);
	}

	public int getWidth() {
		if (target != null) {
			return (int) (target.width() / target.xscale());
		}
		return LSystem.viewSize.getWidth();
	}

	public int getHeight() {
		if (target != null) {
			return (int) (target.height() / target.yscale());
		}
		return LSystem.viewSize.getHeight();
	}

	public Graphics gfx() {
		return this.gfx;
	}

	/**
	 * 启动一系列渲染命令
	 * 
	 * @return
	 */
	public GLEx begin() {
		if (isClosed) {
			return this;
		}
		if (batch == null) {
			return this;
		}
		target.bind();
		beginBatch(batch);
		return this;
	}

	/**
	 * 结束当前的渲染，并且一次性提交渲染结果到系统。与0.5以前旧版不同的是，此命令提交的LTexture渲染，如果LTexture对象不改变的话，
	 * 将是连续的. 也就是同一纹理中素材使用的越频繁，渲染效率也就越高，反之，大量使用不同纹理，则会导致渲染速度下降.
	 * 
	 * @return
	 */
	public GLEx end() {
		if (isClosed) {
			return this;
		}
		if (batch == null) {
			return this;
		}
		batch.end();
		return this;
	}

	public BaseBatch batch() {
		return batch;
	}

	public boolean running() {
		if (isClosed) {
			return false;
		}
		if (batch == null) {
			return false;
		}
		return batch.running();
	}

	public GLEx resetFont() {
		this.lastBrush.font = LSystem.getSystemGameFont();
		return this;
	}

	public final GLEx resetColor() {
		return setColor(LColor.DEF_COLOR);
	}

	public GLEx setFont(IFont font) {
		this.lastBrush.font = font;
		return this;
	}

	public IFont getFont() {
		return this.lastBrush.font;
	}

	public BaseBatch pushBatch(BaseBatch b) {
		if (isClosed) {
			return null;
		}
		if (b == null) {
			return null;
		}
		BaseBatch oldBatch = batch;
		save();
		batch.end();
		batch = beginBatch(b);
		return oldBatch;
	}

	public BaseBatch popBatch(BaseBatch oldBatch) {
		if (isClosed) {
			return null;
		}
		if (oldBatch != null) {
			batch.end();
			batch = beginBatch(oldBatch);
			restore();
		}
		return batch;
	}

	/**
	 * 变更画布基础设置
	 * 
	 */
	public final GLEx update() {
		if (isClosed) {
			return this;
		}
		GL20 gl = batch.gl;
		// 刷新原始设置
		GLUtils.reset(gl);
		// 清空背景为黑色
		GLUtils.setClearColor(gl, LColor.black);
		if (!LSystem.isHTML5()) {
			// 禁用色彩抖动
			GLUtils.disableDither(gl);
			// 禁用深度测试
			GLUtils.disableDepthTest(gl);
			// 禁用双面剪切
			GLUtils.disableCulling(gl);
		}
		// 设定画布渲染模式为默认
		this.setBlendMode(LSystem.MODE_NORMAL);
		return this;
	}

	public static BaseBatch createDefaultBatch(GL20 gl) {
		return new TrilateralBatch(gl);
	}

	/**
	 * 设定当前使用的色彩混合模式
	 * 
	 * @param mode
	 */
	public GLEx setBlendMode(int mode) {
		if (isClosed) {
			return this;
		}
		lastBrush.blend = mode;
		GLUtils.setBlendMode(batch.gl, mode);
		return this;
	}

	public int getBlendMode() {
		return GLUtils.getBlendMode();
	}

	public Affine2f tx() {
		return lastTrans;
	}

	public GLEx saveBrush() {
		if (isClosed) {
			return this;
		}
		if (lastBrush != null) {
			brushStack.add(lastBrush = lastBrush.cpy());
		}
		return this;
	}

	public GLEx clearBrushs() {
		if (isClosed) {
			return this;
		}
		brushStack.clear();
		return this;
	}

	public GLEx restoreBrush() {
		if (isClosed) {
			return this;
		}
		lastBrush = brushStack.previousPop();
		if (lastBrush != null) {
			this.setFont(lastBrush.font);
			this.setLineWidth(lastBrush.lineWidth);
			this.setBlendMode(lastBrush.blend);
		}
		return this;
	}

	public GLEx restoreBrushDef() {
		this.lastBrush.baseAlpha = 1f;
		this.lastBrush.baseColor = LColor.DEF_COLOR;
		this.lastBrush.fillColor = LColor.DEF_COLOR;
		this.lastBrush.patternTex = null;
		this.lastBrush.alltextures = LSystem.isHTML5();
		this.setPixSkip(lastBrush.alltextures ? def_skip_html5 : def_skip);
		this.setFont(LSystem.getSystemGameFont());
		this.setLineWidth(1f);
		this.setBlendMode(LSystem.MODE_NORMAL);
		brushStack.pop();
		return this;
	}

	public GLEx save() {
		this.saveTx();
		this.saveBrush();
		return this;
	}

	public GLEx restore() {
		this.restoreTx();
		this.restoreBrush();
		return this;
	}

	public GLEx saveTx() {
		if (isClosed) {
			return this;
		}
		if (lastTrans != null) {
			affineStack.add(lastTrans = lastTrans.cpy());
		}
		return this;
	}

	public GLEx clearTxs() {
		if (isClosed) {
			return this;
		}
		affineStack.clear();
		return this;
	}

	public GLEx restoreTx() {
		if (isClosed) {
			return this;
		}
		lastTrans = affineStack.previousPop();
		return this;
	}

	public GLEx restoreTxDef() {
		if (isClosed) {
			return this;
		}
		lastTrans = new Affine2f();
		scale(scaleX, scaleY);
		affineStack.pop();
		return this;
	}

	public boolean setClip(float x, float y, float width, float height) {
		return startClipped(x, y, width, height);
	}

	public boolean startClipped(float x1, float y1, float w1, float h1) {
		if (isClosed) {
			return false;
		}
		int x = (int) (x1 * LSystem.getScaleWidth());
		int y = (int) (y1 * LSystem.getScaleHeight());
		int width = (int) (w1 * LSystem.getScaleWidth());
		int height = (int) (h1 * LSystem.getScaleHeight());
		batch.flush();
		RectBox r = pushScissorState(x, target.flip() ? target.height() - y - height : y, width, height);
		batch.gl.glScissor(r.x(), r.y(), r.width(), r.height());
		if (scissorDepth == 1) {
			GLUtils.enablecissorTest(batch.gl);
		}
		return !r.isEmpty();
	}

	public GLEx clearClip() {
		return endClipped();
	}

	public GLEx endClipped() {
		if (isClosed) {
			return this;
		}
		batch.flush();
		RectBox r = popScissorState();
		if (r == null) {
			GLUtils.disablecissorTest(batch.gl);
		} else {
			batch.gl.glScissor(r.x(), r.y(), r.width(), r.height());
		}
		return this;
	}

	public GLEx translate(float x, float y) {
		lastTrans.translate(x, y);
		return this;
	}

	public GLEx scale(float s) {
		lastTrans.scale(s, s);
		return this;
	}

	public GLEx scale(float sx, float sy) {
		lastTrans.scale(sx, sy);
		return this;
	}

	public GLEx scale(float sx, float sy, float rx, float ry) {
		Affine2f aff = tx();
		if (aff != null) {
			aff.translate(rx, ry);
			aff.preScale(sx, sy);
			aff.translate(-rx, -ry);
		}
		return this;
	}

	public GLEx rotate(float rx, float ry, float angle) {
		Affine2f aff = tx();
		if (aff != null) {
			aff.translate(rx, ry);
			aff.preRotate(angle);
			aff.translate(-rx, -ry);

		}
		return this;
	}

	public GLEx viewport(int x, int y, int width, int height) {
		if (isClosed) {
			return this;
		}
		batch.gl.glViewport(x, y, width, height);
		return this;
	}

	public GLEx rotate(float angle) {
		if (isClosed) {
			return this;
		}
		tx().preRotate(angle);
		return this;
	}

	public GLEx flip(float x, float y, float width, float height, boolean flipX, boolean flipY) {
		if (isClosed) {
			return this;
		}
		if (flipX && flipY) {
			Affine2f.transform(tx(), x, y, Affine2f.TRANS_ROT180, width, height);
		} else if (flipX) {
			Affine2f.transform(tx(), x, y, Affine2f.TRANS_MIRROR, width, height);
		} else if (flipY) {
			Affine2f.transform(tx(), x, y, Affine2f.TRANS_MIRROR_ROT180, width, height);
		}
		return this;
	}

	public GLEx transform(float m00, float m01, float m10, float m11, float tx, float ty) {
		if (isClosed) {
			return this;
		}
		Affine2f top = tx();
		Affine2f.multiply(top, m00, m01, m10, m11, tx, ty, top);
		return this;
	}

	public GLEx concatenate(Affine2f xf) {
		if (isClosed) {
			return this;
		}
		Affine2f.multiply(tx(), xf, xf);
		return this;
	}

	public GLEx concatenate(Affine2f xf, float originX, float originY) {
		if (isClosed) {
			return this;
		}
		Affine2f txf = tx();
		Affine2f.multiply(txf, xf.m00, xf.m01, xf.m10, xf.m11, xf.tx, xf.ty, txf);
		if (originX != 0 || originY != 0) {
			txf.translate(-originX, -originY);
		}
		return this;
	}

	public Affine2f getAffine() {
		return lastTrans;
	}

	public GLEx setAffine(Affine2f aff) {
		if (isClosed) {
			return this;
		}
		lastTrans = aff;
		return this;
	}

	public GLEx set(Affine2f aff) {
		if (isClosed) {
			return this;
		}
		saveTx();
		Affine2f txf = tx();
		txf.set(aff);
		return this;
	}

	public GLEx set(Matrix3 mat3) {
		if (isClosed) {
			return this;
		}
		saveTx();
		Affine2f txf = tx();
		txf.set(mat3);
		return this;
	}

	public GLEx set(Matrix4 mat4) {
		if (isClosed) {
			return this;
		}
		saveTx();
		Affine2f txf = tx();
		txf.set(mat4);
		return this;
	}

	public GLEx setCamera(BaseCamera came) {
		Affine2f txf = tx();
		if (txf != null) {
			came.setup();
			this.set(came.getView().cpy().thisCombine(tx()));
		}
		return this;
	}

	public GLEx preConcatenate(Affine2f xf) {
		if (isClosed) {
			return this;
		}
		Affine2f txf = tx();
		Affine2f.multiply(xf.m00, xf.m01, xf.m10, xf.m11, xf.tx, xf.ty, txf, txf);
		return this;
	}

	public float getAlpha() {
		return alpha();
	}

	// 以实际渲染颜色的alpha为优先返回
	public float alpha() {
		return ((this.lastBrush.baseColor >> 24) & 0xFF) / 255f;
	}

	public GLEx setAlpha(float alpha) {
		// fix alpha
		if (alpha < 0.01f) {
			alpha = 0.01f;
			this.lastBrush.baseAlpha = 0;
		} else if (alpha > 1f) {
			alpha = 1f;
			this.lastBrush.baseAlpha = 1f;
		} else {
			this.lastBrush.baseAlpha = alpha;
		}
		int ialpha = (int) (0xFF * MathUtils.clamp(alpha, 0, 1));
		this.lastBrush.baseColor = (ialpha << 24) | (this.lastBrush.baseColor & 0xFFFFFF);
		return this;
	}

	public void reset(float red, float green, float blue, float alpha) {
		if (isClosed) {
			return;
		}
		GLUtils.setClearColor(batch.gl, red, green, blue, alpha);
		this.setFont(LSystem.getSystemGameFont());
		this.lastBrush.baseColor = LColor.DEF_COLOR;
		this.lastBrush.fillColor = LColor.DEF_COLOR;
		this.lastBrush.baseAlpha = 1f;
		this.lastBrush.patternTex = null;
		this.setBlendMode(LSystem.MODE_NORMAL);
		this.resetLineWidth();
	}

	public void reset() {
		if (isClosed) {
			return;
		}
		tmpColor.setColor(this.lastBrush.baseColor);
		reset(tmpColor.r, tmpColor.g, tmpColor.b, tmpColor.a);
	}

	public int color() {
		return this.lastBrush.baseColor;
	}

	public int getTint() {
		return color();
	}

	public LColor getColor() {
		return new LColor(this.lastBrush.baseColor);
	}

	public GLEx setColor(LColor color) {
		int argb = color.getARGB();
		setColor(argb);
		return this;
	}

	public GLEx setColor(int r, int g, int b) {
		return setColor(LColor.getRGB(r, g, b));
	}

	public GLEx setColor(int r, int g, int b, int a) {
		return setColor(LColor.getARGB(r, g, b, a));
	}

	public GLEx setColor(float r, float g, float b, float a) {
		return setColor(LColor.getARGB((int) (r > 1 ? r : r * 255), (int) (g > 1 ? g : r * 255),
				(int) (b > 1 ? b : b * 255), (int) (a > 1 ? a : a * 255)));
	}

	public GLEx setColor(int c) {
		this.setTint(c);
		this.setAlpha(LColor.getAlpha(this.lastBrush.baseColor));
		this.lastBrush.fillColor = c;
		this.lastBrush.patternTex = null;
		return this;
	}

	public GLEx setTint(int r, int g, int b) {
		return setColor(LColor.getRGB(r, g, b));
	}

	public GLEx setTint(int r, int g, int b, int a) {
		return setColor(LColor.getARGB(r, g, b, a));
	}

	public GLEx setTint(float r, float g, float b, float a) {
		return setColor(LColor.getARGB((int) (r > 1 ? r : r * 255), (int) (g > 1 ? g : r * 255),
				(int) (b > 1 ? b : b * 255), (int) (a > 1 ? a : a * 255)));
	}

	public GLEx setTint(LColor color) {
		return this.setTint(color.getARGB());
	}

	public GLEx setTint(int c) {
		if (this.lastBrush.baseColor != c) {
			end();
		}
		if (this.lastBrush.baseAlpha != 1f) {
			this.lastBrush.baseColor = c;
			int ialpha = (int) (0xFF * MathUtils.clamp(this.lastBrush.baseAlpha, 0, 1));
			this.lastBrush.baseColor = (ialpha << 24) | (this.lastBrush.baseColor & 0xFFFFFF);
		} else {
			this.lastBrush.baseColor = c;
		}
		this.lastBrush.baseAlpha = this.lastBrush.baseAlpha;
		this.lastBrush.baseColor = this.lastBrush.baseColor;
		return this;
	}

	public int combineColor(int c) {
		int otint = this.lastBrush.baseColor;
		if (c != LColor.DEF_COLOR) {
			this.lastBrush.baseColor = LColor.combine(c, otint);
		}
		return otint;
	}

	public GLEx setFillColor(int color) {
		this.lastBrush.fillColor = color;
		this.lastBrush.patternTex = null;
		return this;
	}

	public GLEx setFillPattern(LTexture texture) {
		this.lastBrush.patternTex = texture;
		return this;
	}

	public GLEx clear() {
		return clear(0, 0, 0, 0);
	}

	public GLEx clear(float red, float green, float blue, float alpha) {
		GLUtils.setClearColor(batch.gl, red, green, blue, alpha);
		return this;
	}

	public final GLEx clear(LColor color) {
		GLUtils.setClearColor(batch.gl, color);
		return this;
	}

	public GLEx rect(RectF.Range rect, float x, float y, Paint paint) {
		return rect(rect.x(), rect.y(), rect.width(), rect.height(), x, y, paint);
	}

	public GLEx rect(RectI.Range rect, float x, float y, Paint paint) {
		return rect(rect.x(), rect.y(), rect.width(), rect.height(), x, y, paint);
	}

	public GLEx rect(float sx, float sy, float sw, float sh, float x, float y, Paint paint) {
		int tmp = this.lastBrush.baseColor;
		float line = getLineWidth();
		Style style = Style.FILL;
		if (paint != null) {
			setLineWidth(paint.strokeWidth);
			style = paint.style;
			setColor(paint.color);
		}
		switch (style) {
		case FILL:
			fillRect(sx + x / 2, sy + y / 2, sw, sh);
			break;
		case STROKE:
			drawRect(sx + x / 2, sy + y / 2, sw, sh);
			break;
		case FILL_AND_STROKE:
			fillRect(sx + x / 2, sy + y / 2, sw, sh);
			drawRect(sx + x / 2, sy + y / 2, sw, sh);
			break;
		}
		setColor(tmp);
		setLineWidth(line);
		return this;
	}

	public GLEx drawBitmap(Painter texture, RectF.Range src, RectF.Range des, Paint paint) {
		int tmp = this.lastBrush.baseColor;
		if (paint != null) {
			if (paint.style == Style.FILL) {
				setColor(paint.color);
			}
		}
		draw(texture, des.x(), des.y(), des.width(), des.height(), src.x(), src.y(), src.width(), src.height());
		setColor(tmp);
		return this;
	}

	public GLEx drawBitmap(Painter texture, RectF.Range des, Paint paint) {
		int tmp = this.lastBrush.baseColor;
		if (paint != null) {
			if (paint.style == Style.FILL) {
				setColor(paint.color);
			}
		}
		draw(texture, des.x(), des.y(), des.width(), des.height());
		setColor(tmp);
		return this;
	}

	public GLEx drawBitmap(Painter texture, RectI.Range src, RectI.Range des, Paint paint) {
		int tmp = this.lastBrush.baseColor;
		if (paint != null) {
			if (paint.style == Style.FILL) {
				setColor(paint.color);
			}
		}
		draw(texture, des.x(), des.y(), des.width(), des.height(), src.x(), src.y(), src.width(), src.height());
		setColor(tmp);
		return this;
	}

	public GLEx drawBitmap(Painter texture, RectI.Range des, Paint paint) {
		int tmp = this.lastBrush.baseColor;
		if (paint != null) {
			if (paint.style == Style.FILL) {
				setColor(paint.color);
			}
		}
		draw(texture, des.x(), des.y(), des.width(), des.height());
		setColor(tmp);
		return this;
	}

	public GLEx drawBitmap(Painter texture, float x, float y, Paint paint) {
		int tmp = this.lastBrush.baseColor;
		if (paint != null) {
			if (paint.style == Style.FILL) {
				setColor(paint.color);
			}
		}
		draw(texture, x, y);
		setColor(tmp);
		return this;
	}

	/**
	 * 渲染指定字符串到屏幕（此函数显示，与旧版drawString渲染位置相同）
	 * 
	 * @param message
	 * @param x
	 * @param y
	 * @return
	 */
	public GLEx drawText(String message, float x, float y) {
		return drawText(message, x, y, 0, null);
	}

	/**
	 * 渲染指定字符串到屏幕（此函数显示，与旧版drawString渲染位置相同）
	 * 
	 * @param message
	 * @param x
	 * @param y
	 * @param paint
	 * @return
	 */
	public GLEx drawText(String message, float x, float y, Paint paint) {
		return drawText(message, x, y, 0, paint);
	}

	/**
	 * 渲染指定字符串到屏幕（此函数显示，与旧版drawString渲染位置相同）
	 * 
	 * @param message
	 * @param x
	 * @param y
	 * @param rotation
	 * @param paint
	 * @return
	 */
	public GLEx drawText(String message, float x, float y, float rotation, Paint paint) {
		if (paint != null && paint.getFont() != null) {
			IFont tmpFont = paint.getFont();
			tmpFont.drawString(this, message, x + offsetStringX, y + offsetStringY - tmpFont.getAscent() - 1, rotation,
					tmpColor.setColor(paint.color));
		} else if (this.lastBrush.font != null) {
			this.lastBrush.font.drawString(this, message, x + offsetStringX,
					y + offsetStringY - this.lastBrush.font.getAscent() - 1, rotation,
					tmpColor.setColor(this.lastBrush.baseColor));
		}
		return this;
	}

	public GLEx drawText(String message, float x, float y, LColor color, float rotation) {
		return drawText(message, x, y, color.getARGB(), rotation);
	}

	public GLEx drawText(String message, float x, float y, int color, float rotation) {
		int tmp = this.lastBrush.baseColor;
		setColor(color);
		this.lastBrush.font.drawString(this, message, x + offsetStringX,
				y + offsetStringY - this.lastBrush.font.getAscent() - 1, rotation,
				tmpColor.setColor(this.lastBrush.baseColor));
		setColor(tmp);
		return this;
	}

	public GLEx drawScale(Painter texture, float x, float y, float w, float h, LColor color, float scaleX, float scaleY,
			float rotation) {
		if (isClosed) {
			return this;
		}
		if (texture == null) {
			return this;
		}
		return draw(texture, x, y, texture.width(), texture.height(), 0, 0, texture.width(), texture.height(), color,
				rotation, scaleX, scaleY, null, Direction.TRANS_NONE);
	}

	public GLEx drawScale(Painter texture, float x, float y, float w, float h, LColor color, float scaleX,
			float scaleY) {
		if (isClosed) {
			return this;
		}
		if (texture == null) {
			return this;
		}
		return draw(texture, x, y, texture.width(), texture.height(), 0, 0, texture.width(), texture.height(), color, 0,
				null, Direction.TRANS_NONE);
	}

	public GLEx drawMirrorScale(Painter texture, float x, float y, float w, float h, LColor color, float scaleX,
			float scaleY, float rotation) {
		if (isClosed) {
			return this;
		}
		if (texture == null) {
			return this;
		}
		return draw(texture, x, y, w, h, 0, 0, texture.width(), texture.height(), color, rotation, scaleX, scaleY, null,
				Direction.TRANS_MIRROR);
	}

	public GLEx drawMirrorScale(Painter texture, float x, float y, float w, float h, LColor color, float scaleX,
			float scaleY) {
		if (isClosed) {
			return this;
		}
		if (texture == null) {
			return this;
		}
		return draw(texture, x, y, w, h, 0, 0, texture.width(), texture.height(), color, 0, scaleX, scaleY, null,
				Direction.TRANS_MIRROR);
	}

	public GLEx drawFlipScale(Painter texture, float x, float y, float w, float h, LColor color, float scaleX,
			float scaleY, float rotation) {
		if (isClosed) {
			return this;
		}
		if (texture == null) {
			return this;
		}
		return draw(texture, x, y, w, h, 0, 0, texture.width(), texture.height(), color, rotation, scaleX, scaleY, null,
				Direction.TRANS_FLIP);
	}

	public GLEx drawFlipScale(Painter texture, float x, float y, float w, float h, LColor color, float scaleX,
			float scaleY) {
		if (isClosed) {
			return this;
		}
		if (texture == null) {
			return this;
		}
		return draw(texture, x, y, w, h, 0, 0, texture.width(), texture.height(), color, 0, scaleX, scaleY, null,
				Direction.TRANS_FLIP);
	}

	public final GLEx draw(Painter texture, float x, float y, Direction dir) {
		if (isClosed) {
			return this;
		}
		if (texture == null) {
			return this;
		}
		return draw(texture, x, y, texture.width(), texture.height(), 0, 0, texture.width(), texture.height(), null, 0,
				null, dir);
	}

	public final GLEx draw(Painter texture, float x, float y, LColor color, float rotation) {
		if (isClosed) {
			return this;
		}
		if (texture == null) {
			return this;
		}
		return draw(texture, x, y, texture.width(), texture.height(), 0, 0, texture.width(), texture.height(), color,
				rotation, null, null);
	}

	public GLEx draw(Painter texture, float x, float y) {
		if (isClosed) {
			return this;
		}
		if (texture == null) {
			return this;
		}
		return draw(texture, x, y, texture.width(), texture.height());
	}

	public GLEx draw(Painter texture, float x, float y, LColor color) {
		if (isClosed) {
			return this;
		}
		if (texture == null) {
			return this;
		}
		return draw(texture, x, y, texture.width(), texture.height(), color);
	}

	public GLEx draw(Painter texture, float x, float y, float w, float h, LColor color) {
		if (isClosed) {
			return this;
		}
		if (texture == null) {
			return this;
		}
		int argb = this.lastBrush.baseColor;
		if (color != null) {
			argb = color.getARGB(alpha());
		}
		texture.addToBatch(batch, argb, tx(), x, y, w, h);
		return this;
	}

	public GLEx draw(Painter texture, float x, float y, float rotation) {
		if (isClosed) {
			return this;
		}
		if (texture == null) {
			return this;
		}
		return draw(texture, x, y, texture.width(), texture.height(), rotation);
	}

	public GLEx draw(Painter texture, float x, float y, LColor color, float rotation, Vector2f pivot) {
		if (isClosed) {
			return this;
		}
		if (texture == null) {
			return this;
		}
		return draw(texture, x, y, texture.width(), texture.height(), color, rotation, pivot);
	}

	public GLEx draw(Painter texture, float x, float y, float w, float h, float rotation) {
		if (isClosed) {
			return this;
		}
		if (texture == null) {
			return this;
		}
		Affine2f xf = tx();
		if (rotation != 0) {
			xf = new Affine2f();
			float w1 = x + w / 2;
			float h1 = y + h / 2;
			xf.translate(w1, h1);
			xf.preRotate(rotation);
			xf.translate(-w1, -h1);
			Affine2f.multiply(tx(), xf, xf);
		}
		texture.addToBatch(batch, this.lastBrush.baseColor, xf, x, y, w, h);
		return this;
	}

	public GLEx draw(Painter texture, float x, float y, float w, float h, LColor color, float rotation,
			Vector2f pivot) {
		return draw(texture, x, y, w, h, color, rotation, pivot, 1f, 1f);
	}

	public GLEx draw(Painter texture, float x, float y, float w, float h, LColor color, float rotation, Vector2f pivot,
			float sx, float sy) {
		return draw(texture, x, y, w, h, color, rotation, pivot, sx, sy, false, false);
	}

	public GLEx draw(Painter texture, float x, float y, float w, float h, LColor color, float rotation, float sx,
			float sy, boolean flipX, boolean flipY) {
		return draw(texture, x, y, w, h, color, rotation, null, sx, sy, flipX, flipY);
	}

	public GLEx draw(Painter texture, float x, float y, float w, float h, LColor color, float rotation, Vector2f pivot,
			float sx, float sy, boolean flipX, boolean flipY) {
		if (isClosed) {
			return this;
		}
		if (texture == null) {
			return this;
		}
		int argb = this.lastBrush.baseColor;
		if (color != null) {
			argb = color.getARGB(alpha());
		}
		Affine2f xf = tx();
		if (rotation != 0 || sx != 1f || sy != 1f || flipX || flipY) {
			xf = new Affine2f();
			float centerX = x + w / 2;
			float centerY = y + h / 2;
			if (pivot != null && (pivot.x != -1 && pivot.y != -1)) {
				centerX = x + pivot.x;
				centerX = y + pivot.y;
			}
			if (rotation != 0) {
				xf.translate(centerX, centerY);
				xf.preRotate(rotation);
				xf.translate(-centerX, -centerY);
			}
			if (flipX || flipY) {
				if (flipX && flipY) {
					Affine2f.transform(xf, x, y, Affine2f.TRANS_ROT180, w, h);
				} else if (flipX) {
					Affine2f.transform(xf, x, y, Affine2f.TRANS_MIRROR, w, h);
				} else if (flipY) {
					Affine2f.transform(xf, x, y, Affine2f.TRANS_MIRROR_ROT180, w, h);
				}
			}
			if (sx != 1f || sy != 1f) {
				xf.translate(centerX, centerY);
				xf.preScale(sx, sy);
				xf.translate(-centerX, -centerY);
			}
			Affine2f.multiply(tx(), xf, xf);
		}
		texture.addToBatch(batch, argb, xf, x, y, w, h);
		return this;
	}

	public GLEx draw(Painter texture, float x, float y, float w, float h, LColor color, float rotation) {
		if (isClosed) {
			return this;
		}
		if (texture == null) {
			return this;
		}
		int argb = this.lastBrush.baseColor;
		if (color != null) {
			argb = color.getARGB(alpha());
		}
		Affine2f xf = tx();
		if (rotation != 0) {
			xf = new Affine2f();
			float w1 = x + w / 2;
			float h1 = y + h / 2;
			xf.translate(w1, h1);
			xf.preRotate(rotation);
			xf.translate(-w1, -h1);
			Affine2f.multiply(tx(), xf, xf);
		}
		texture.addToBatch(batch, argb, xf, x, y, w, h);
		return this;
	}

	public GLEx draw(Painter texture, float x, float y, float w, float h) {
		if (isClosed) {
			return this;
		}
		if (texture == null) {
			return this;
		}
		texture.addToBatch(batch, this.lastBrush.baseColor, tx(), x, y, w, h);
		return this;
	}

	public GLEx draw(Painter texture, float dx, float dy, float sx, float sy, float sw, float sh) {
		if (isClosed) {
			return this;
		}
		if (texture == null) {
			return this;
		}
		texture.addToBatch(batch, this.lastBrush.baseColor, tx(), dx, dy, sw, sh, sx, sy, sw, sh);
		return this;
	}

	public GLEx draw(Painter texture, float dx, float dy, float dw, float dh, float sx, float sy, float sw, float sh) {
		if (isClosed) {
			return this;
		}
		if (texture == null) {
			return this;
		}
		texture.addToBatch(batch, this.lastBrush.baseColor, tx(), dx, dy, dw, dh, sx, sy, sw, sh);
		return this;
	}

	public GLEx draw(Painter texture, float dx, float dy, float dw, float dh, float sx, float sy, float sw, float sh,
			LColor color) {
		if (isClosed) {
			return this;
		}
		if (texture == null) {
			return this;
		}
		if (LColor.white.equals(color)) {
			texture.addToBatch(batch, this.lastBrush.baseColor, tx(), dx, dy, dw, dh, sx, sy, sw, sh);
			return this;
		}
		int argb = this.lastBrush.baseColor;
		if (color != null) {
			argb = color.getARGB(alpha());
		}
		texture.addToBatch(batch, argb, tx(), dx, dy, dw, dh, sx, sy, sw, sh);
		return this;
	}

	public GLEx draw(Painter texture, float dx, float dy, float dw, float dh, float sx, float sy, float sw, float sh,
			float rotation) {
		if (isClosed) {
			return this;
		}
		if (texture == null) {
			return this;
		}
		if (rotation == 0) {
			texture.addToBatch(batch, this.lastBrush.baseColor, tx(), dx, dy, dw, dh, sx, sy, sw, sh);
			return this;
		}
		Affine2f xf = tx();
		if (rotation != 0) {
			xf = new Affine2f();
			float w1 = dx + dw / 2;
			float h1 = dy + dh / 2;
			xf.translate(w1, h1);
			xf.preRotate(rotation);
			xf.translate(-w1, -h1);
			Affine2f.multiply(tx(), xf, xf);
		}
		texture.addToBatch(batch, this.lastBrush.baseColor, xf, dx, dy, dw, dh, sx, sy, sw, sh);
		return this;
	}

	public GLEx drawFlip(Painter texture, float x, float y, LColor color) {
		if (isClosed) {
			return this;
		}
		if (texture == null) {
			return this;
		}
		return draw(texture, x, y, texture.width(), texture.height(), 0, 0, texture.width(), texture.height(), color, 0,
				null, Direction.TRANS_FLIP);
	}

	public GLEx drawFlip(Painter texture, float x, float y, float w, float h, LColor color, float rotation) {
		if (isClosed) {
			return this;
		}
		if (texture == null) {
			return this;
		}
		return draw(texture, x, y, w, h, 0, 0, texture.width(), texture.height(), color, rotation, null,
				Direction.TRANS_FLIP);
	}

	public GLEx drawFlip(Painter texture, float x, float y, LColor color, float rotation) {
		if (isClosed) {
			return this;
		}
		if (texture == null) {
			return this;
		}
		return draw(texture, x, y, texture.width(), texture.height(), 0, 0, texture.width(), texture.height(), color,
				rotation, null, Direction.TRANS_FLIP);
	}

	public GLEx drawMirror(Painter texture, float x, float y, LColor color) {
		if (isClosed) {
			return this;
		}
		if (texture == null) {
			return this;
		}
		return draw(texture, x, y, texture.width(), texture.height(), 0, 0, texture.width(), texture.height(), color, 0,
				null, Direction.TRANS_MIRROR);
	}

	public GLEx drawMirror(Painter texture, float x, float y, LColor color, float rotation) {
		if (isClosed) {
			return this;
		}
		if (texture == null) {
			return this;
		}
		return draw(texture, x, y, texture.width(), texture.height(), 0, 0, texture.width(), texture.height(), color,
				rotation, null, Direction.TRANS_MIRROR);
	}

	public GLEx drawMirror(Painter texture, float x, float y, float w, float h, float rotation) {
		if (isClosed) {
			return this;
		}
		if (texture == null) {
			return this;
		}
		return draw(texture, x, y, w, h, 0, 0, texture.width(), texture.height(), null, rotation, null,
				Direction.TRANS_MIRROR);
	}

	public GLEx drawMirror(Painter texture, float x, float y, float w, float h, LColor color, float rotation) {
		if (isClosed) {
			return this;
		}
		if (texture == null) {
			return this;
		}
		return draw(texture, x, y, w, h, 0, 0, texture.width(), texture.height(), color, rotation, null,
				Direction.TRANS_MIRROR);
	}

	public GLEx draw(Painter texture, float x, float y, LColor color, Direction dir) {
		if (isClosed) {
			return this;
		}
		if (texture == null) {
			return this;
		}
		return draw(texture, x, y, texture.width(), texture.height(), 0, 0, texture.width(), texture.height(), color, 0,
				null, dir);
	}

	public GLEx draw(Painter texture, float x, float y, LColor color, float rotation, Vector2f origin, Direction dir) {
		if (isClosed) {
			return this;
		}
		if (texture == null) {
			return this;
		}
		return draw(texture, x, y, texture.width(), texture.height(), 0, 0, texture.width(), texture.height(), color,
				rotation, origin, dir);
	}

	public GLEx draw(Painter texture, RectBox destRect, RectBox srcRect, LColor color, float rotation) {
		if (rotation == 0) {
			return draw(texture, destRect.x, destRect.y, destRect.width, destRect.height, srcRect.x, srcRect.y,
					srcRect.width, srcRect.height, color);
		}
		return draw(texture, destRect.x, destRect.y, destRect.width, destRect.height, srcRect.x, srcRect.y,
				srcRect.width, srcRect.height, color, rotation, null, null);
	}

	public GLEx draw(Painter texture, float x, float y, float width, float height, float srcX, float srcY,
			float srcWidth, float srcHeight, LColor c, float rotation) {
		if (rotation == 0) {
			return draw(texture, x, y, width, height, srcX, srcY, srcWidth, srcHeight, c);
		}
		return draw(texture, x, y, width, height, srcX, srcY, srcWidth, srcHeight, c, rotation, null, null);
	}

	public GLEx draw(Painter texture, float x, float y, float width, float height, float srcX, float srcY,
			float srcWidth, float srcHeight, LColor color, float rotation, Vector2f origin, Direction dir) {
		return draw(texture, x, y, width, height, srcX, srcY, srcWidth, srcHeight, color, rotation, 1f, 1f, origin,
				dir);
	}

	public GLEx draw(Painter texture, float x, float y, Vector2f origin, float width, float height, float scaleX,
			float scaleY, float rotation, float srcX, float srcY, float srcWidth, float srcHeight, boolean flipX,
			boolean flipY, LColor color) {
		if (!flipX && !flipY) {
			return draw(texture, x, y, width, height, srcX, srcY, srcWidth, srcHeight, color, rotation, scaleX, scaleY,
					origin, Direction.TRANS_NONE);
		} else if (flipX && !flipY) {
			return draw(texture, x, y, width, height, srcX, srcY, srcWidth, srcHeight, color, rotation, scaleX, scaleY,
					origin, Direction.TRANS_FLIP);
		} else if (!flipX && flipY) {
			return draw(texture, x, y, width, height, srcX, srcY, srcWidth, srcHeight, color, rotation, scaleX, scaleY,
					origin, Direction.TRANS_MIRROR);
		} else {
			return draw(texture, x, y, width, height, srcX, srcY, srcWidth, srcHeight, color, rotation, scaleX, scaleY,
					origin, Direction.TRANS_MF);
		}
	}

	public GLEx draw(Painter texture, float x, float y, float width, float height, float srcX, float srcY,
			float srcWidth, float srcHeight, LColor color, float rotation, float scaleX, float scaleY, Vector2f origin,
			Direction dir) {
		return draw(texture, x, y, width, height, srcX, srcY, srcWidth, srcHeight, color, rotation, scaleX, scaleY,
				origin, null, dir);
	}

	public GLEx draw(Painter texture, float x, float y, float width, float height, float srcX, float srcY,
			float srcWidth, float srcHeight, LColor color, float rotation, float scaleX, float scaleY, Vector2f origin,
			Vector2f pivot, Direction dir) {
		if (isClosed) {
			return this;
		}
		if (texture == null) {
			return this;
		}

		Affine2f xf = tx();

		boolean dirDirty = (dir != null && dir != Direction.TRANS_NONE);

		boolean rotDirty = (rotation != 0 || pivot != null);

		boolean oriDirty = (origin != null && (origin.x != 0 || origin.y != 0));

		boolean scaleDirty = !(scaleX == 1 && scaleY == 1);

		if (dirDirty || rotDirty || oriDirty || scaleDirty) {
			xf = new Affine2f();
			if (oriDirty) {
				xf.translate(origin.x, origin.y);
			}
			if (rotDirty) {
				float centerX = x + width / 2;
				float centerY = y + height / 2;
				if (pivot != null && (pivot.x != -1 && pivot.y != -1)) {
					centerX = x + pivot.x;
					centerX = y + pivot.y;
				}
				xf.translate(centerX, centerY);
				xf.preRotate(rotation);
				xf.translate(-centerX, -centerY);
			}
			if (scaleDirty) {
				float centerX = x + width / 2;
				float centerY = y + height / 2;
				if (pivot != null && (pivot.x != -1 && pivot.y != -1)) {
					centerX = x + pivot.x;
					centerX = y + pivot.y;
				}
				xf.translate(centerX, centerY);
				xf.preScale(scaleX, scaleY);
				xf.translate(-centerX, -centerY);
			}
			if (dirDirty) {
				switch (dir) {
				case TRANS_MIRROR:
					Affine2f.transform(xf, x, y, Affine2f.TRANS_MIRROR, width, height);
					break;
				case TRANS_FLIP:
					Affine2f.transform(xf, x, y, Affine2f.TRANS_MIRROR_ROT180, width, height);
					break;
				case TRANS_MF:
					Affine2f.transform(xf, x, y, Affine2f.TRANS_ROT180, width, height);
					break;
				default:
					break;
				}
			}
			Affine2f.multiply(tx(), xf, xf);
		}

		int argb = this.lastBrush.baseColor;
		if (color != null) {
			argb = color.getARGB(alpha());
		}
		texture.addToBatch(batch, argb, xf, x, y, width, height, srcX, srcY, srcWidth, srcHeight);
		return this;
	}

	public GLEx drawCentered(Painter texture, float x, float y) {
		if (isClosed) {
			return this;
		}
		if (texture == null) {
			return this;
		}
		return draw(texture, x - texture.width() / 2, y - texture.height() / 2);
	}

	public GLEx drawLine(XY a, XY b) {
		return drawLine(a.getX(), a.getY(), b.getX(), b.getY(), this.lastBrush.lineWidth);
	}

	public GLEx drawLine(XY a, XY b, float width) {
		return drawLine(a.getX(), a.getY(), b.getX(), b.getY(), width);
	}

	public GLEx drawLine(float x0, float y0, float x1, float y1, LColor color) {
		int tmp = this.lastBrush.baseColor;
		setColor(color);
		drawLine(x0, y0, x1, y1);
		setColor(tmp);
		return this;
	}

	public GLEx drawLine(float x0, float y0, float x1, float y1, float width) {
		if (isClosed) {
			return this;
		}
		if (x1 < x0) {
			float temp = x0;
			x0 = x1;
			x1 = temp;
			temp = y0;
			y0 = y1;
			y1 = temp;
		}

		float dx = x1 - x0, dy = y1 - y0;
		float length = MathUtils.sqrt(dx * dx + dy * dy);
		float wx = dx * (width / 2) / length;
		float wy = dy * (width / 2) / length;

		Affine2f xf = new Affine2f();
		xf.setRotation(MathUtils.atan2(dy, dx));
		xf.setTranslation(x0 + wy, y0 - wx);
		Affine2f.multiply(tx(), xf, xf);
		if (this.lastBrush.patternTex != null) {
			batch.addQuad(this.lastBrush.patternTex, this.lastBrush.baseColor, xf, 0, 0, length, width);
		} else {
			batch.addQuad(colorTex, LColor.combine(this.lastBrush.fillColor, this.lastBrush.baseColor), xf, 0, 0,
					length, width);
		}
		return this;
	}

	public GLEx fillRect(float x, float y, float width, float height) {
		if (isClosed) {
			return this;
		}
		fillRectNative(x, y, width, height);
		return this;
	}

	public GLEx closeBatch() {
		if (batch != null) {
			batch.close();
		}
		return this;
	}

	public GLEx initBatch() {
		if (batch != null) {
			batch.init();
		}
		return this;
	}

	private BaseBatch beginBatch(BaseBatch batch) {
		batch.begin(target.width(), target.height(), target.flip());
		return batch;
	}

	public int getClipX() {
		if (scissors.size == 0) {
			return 0;
		}
		return scissors.get(scissorDepth).x();
	}

	public int getClipY() {
		if (scissors.size == 0) {
			return 0;
		}
		return scissors.get(scissorDepth).y();
	}

	public int getClipWidth() {
		if (scissors.size == 0) {
			return 0;
		}
		return scissors.get(scissorDepth).width;
	}

	public int getClipHeight() {
		if (scissors.size == 0) {
			return 0;
		}
		return scissors.get(scissorDepth).height;
	}

	private RectBox pushScissorState(int x, int y, int width, int height) {
		if (scissorDepth == scissors.size) {
			scissors.add(new RectBox());
		}
		RectBox r = scissors.get(scissorDepth);
		if (scissorDepth == 0) {
			r.setBounds(x, y, width, height);
		} else {
			RectBox pr = scissors.get(scissorDepth - 1);
			r.setLocation(MathUtils.max(pr.x, x), MathUtils.max(pr.y, y));
			r.setSize(MathUtils.max(MathUtils.min(pr.maxX(), x + width - 1) - r.x, 0),
					MathUtils.max(MathUtils.min(pr.maxY(), y + height - 1) - r.y, 0));
		}
		if (this.lastBrush.alltextures) {
			setClipImpl(0, 0, r, getWidth(), getHeight());
		}
		scissorDepth++;
		return r;
	}

	private RectBox popScissorState() {
		scissorDepth--;
		RectBox r = scissorDepth == 0 ? null : scissors.get(scissorDepth - 1);
		if (this.lastBrush.alltextures) {
			if (r == null) {
				setClipImpl(0, 0, LSystem.viewSize.getRect(), getWidth(), getHeight());
			} else {
				setClipImpl(0, 0, r, getWidth(), getHeight());
			}
		}
		return r;
	}

	private boolean useBegin;

	private GLRenderer glRenderer;

	/**
	 * 图形形状渲染开始
	 * 
	 * @param mode
	 * @return
	 */
	private GLEx beginRenderer(GLType mode) {
		end();
		GLUtils.disableTextures(batch.gl);
		if (glRenderer == null) {
			glRenderer = new GLRenderer(this);
		}
		saveTx();
		this.glRenderer.begin(lastTrans, mode);
		this.useBegin = true;
		return this;
	}

	/**
	 * 图形形状渲染结束
	 * 
	 * @return
	 */
	private GLEx endRenderer() {
		if (!useBegin) {
			useBegin = false;
			return this;
		}
		try {
			glRenderer.end();
		} finally {
			restoreTx();
			useBegin = false;
		}
		return this;
	}

	public GLEx drawLine(float x1, float y1, float x2, float y2) {
		return $drawLine(x1, y1, x2, y2, true);
	}

	private GLEx $drawLine(float x1, float y1, float x2, float y2, boolean use) {
		if (isClosed) {
			return this;
		}
		if (this.lastBrush.alltextures) {
			return drawLine(x1, y1, x2, y2, this.lastBrush.lineWidth);
		} else {
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
				beginRenderer(GLType.Line);
			}
			int argb = LColor.combine(this.lastBrush.fillColor, this.lastBrush.baseColor);
			glRenderer.setColor(argb);
			glRenderer.line(x1, y1, x2, y2);
			if (use) {
				endRenderer();
			}
			return this;
		}
	}

	/**
	 * 绘制不特定Shape
	 * 
	 * @param shape
	 * @return
	 */
	public GLEx draw(Shape shape) {
		return draw(shape, 0f, 0f);
	}

	/**
	 * 绘制不特定Shape
	 * 
	 * @param shape
	 * @param x
	 * @param y
	 * @return
	 */
	public GLEx draw(Shape shape, float x, float y) {
		if (shape == null) {
			return this;
		}
		if (this.lastBrush.alltextures) {
			drawShapeImpl(shape, x, y);
		} else {
			float[] points = shape.getPoints();
			if (points.length == 0) {
				return this;
			}
			int argb = LColor.combine(this.lastBrush.fillColor, this.lastBrush.baseColor);
			if (points.length == 2) {
				beginRenderer(GLType.Point);
				glRenderer.setColor(argb);
				glRenderer.point(points[0], points[1]);
				endRenderer();
			} else if (points.length == 4) {
				beginRenderer(GLType.Line);
				glRenderer.setColor(argb);
				glRenderer.line(points[0], points[1], points[2], points[3]);
				endRenderer();
			} else {
				if (points.length < 6 || points.length % 2 != 0) {
					return drawPolyline(shape, x, y);
				}
				beginRenderer(GLType.Line);
				glRenderer.setColor(argb);
				glRenderer.polygon(points);
				endRenderer();
			}
		}
		return this;
	}

	/**
	 * 绘制不连线的Shape
	 * 
	 * @param shape
	 * @return
	 */
	public GLEx drawPolyline(Shape shape) {
		return drawPolyline(shape, 0, 0);
	}

	/**
	 * 绘制不连线的Shape
	 * 
	 * @param shape
	 * @param x
	 * @param y
	 * @return
	 */
	public GLEx drawPolyline(Shape shape, float x, float y) {
		if (shape == null) {
			return this;
		}
		if (this.lastBrush.alltextures) {
			final float[] points = shape.getPoints();
			int size = points.length;
			int len = size / 2;
			final float[] xps = new float[len];
			final float[] yps = new float[len];
			for (int i = 0, j = 0; i < size; i += 2, j++) {
				xps[j] = points[i] + x;
				yps[j] = points[i + 1] + y;
			}
			drawPolylineImpl(xps, yps, len);
		} else {
			float[] points = shape.getPoints();
			if (points.length == 0) {
				return this;
			}
			int argb = LColor.combine(this.lastBrush.fillColor, this.lastBrush.baseColor);
			beginRenderer(GLType.Line);
			glRenderer.setColor(argb);
			glRenderer.polyline(points);
			endRenderer();
		}
		return this;
	}

	/**
	 * 绘制不特定Shape
	 * 
	 * @param shape
	 * @return
	 */
	public GLEx fill(Shape shape) {
		return fill(shape, 0f, 0f);
	}

	/**
	 * 绘制不特定Shape
	 * 
	 * @param shape
	 * @param x
	 * @param y
	 * @return
	 */
	public GLEx fill(Shape shape, float x, float y) {
		if (shape == null) {
			return this;
		}
		if (this.lastBrush.alltextures) {
			fillShapeImpl(shape, x, y);
		} else {
			int argb = LColor.combine(this.lastBrush.fillColor, this.lastBrush.baseColor);
			beginRenderer(GLType.Filled);
			glRenderer.setColor(argb);
			glRenderer.drawShape(shape, x, y);
			endRenderer();
		}
		return this;
	}

	/**
	 * 绘制五角星
	 * 
	 * @param color
	 * @param x
	 * @param y
	 * @param r
	 */
	public GLEx drawSixStart(LColor color, float x, float y, float r) {
		setColor(color);
		drawTriangle(color, x, y, r);
		drawRTriangle(color, x, y, r);
		return this;
	}

	/**
	 * 绘制正三角
	 * 
	 * @param color
	 * @param x
	 * @param y
	 * @param r
	 */
	public GLEx drawTriangle(LColor color, float x, float y, float r) {
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
		return this;
	}

	/**
	 * 绘制倒三角
	 * 
	 * @param color
	 * @param x
	 * @param y
	 * @param r
	 */
	public GLEx drawRTriangle(LColor color, float x, float y, float r) {
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
		return this;
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
	public GLEx drawTriangle(final float x1, final float y1, final float x2, final float y2, final float x3,
			final float y3) {
		beginRenderer(GLType.Line);
		int argb = LColor.combine(this.lastBrush.fillColor, this.lastBrush.baseColor);
		glRenderer.setColor(argb);
		glRenderer.triangle(x1, y1, x2, y2, x3, y3);
		endRenderer();
		return this;
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
	public GLEx fillTriangle(final float x1, final float y1, final float x2, final float y2, final float x3,
			final float y3) {
		beginRenderer(GLType.Filled);
		int argb = LColor.combine(this.lastBrush.fillColor, this.lastBrush.baseColor);
		glRenderer.setColor(argb);
		glRenderer.triangle(x1, y1, x2, y2, x3, y3);
		endRenderer();
		return this;
	}

	/**
	 * 绘制并填充一组三角
	 * 
	 * @param ts
	 */
	public GLEx fillTriangle(Triangle2f[] ts) {
		return fillTriangle(ts, 0, 0);
	}

	/**
	 * 绘制并填充一组三角
	 * 
	 * @param ts
	 * @param x
	 * @param y
	 */
	public GLEx fillTriangle(Triangle2f[] ts, int x, int y) {
		if (ts == null) {
			return this;
		}
		int size = ts.length;
		for (int i = 0; i < size; i++) {
			fillTriangle(ts[i], x, y);
		}
		return this;
	}

	/**
	 * 绘制并填充一组三角
	 * 
	 * @param t
	 */
	public GLEx fillTriangle(Triangle2f t) {
		return fillTriangle(t, 0, 0);
	}

	/**
	 * 绘制并填充一组三角
	 * 
	 * @param t
	 * @param x
	 * @param y
	 */
	public GLEx fillTriangle(Triangle2f t, float x, float y) {
		if (t == null) {
			return this;
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
		return this;
	}

	/**
	 * 绘制一组三角
	 * 
	 * @param ts
	 */
	public GLEx drawTriangle(Triangle2f[] ts) {
		return drawTriangle(ts, 0, 0);
	}

	/**
	 * 绘制一组三角
	 * 
	 * @param ts
	 * @param x
	 * @param y
	 */
	public GLEx drawTriangle(Triangle2f[] ts, int x, int y) {
		if (ts == null) {
			return this;
		}
		int size = ts.length;
		for (int i = 0; i < size; i++) {
			drawTriangle(ts[i], x, y);
		}
		return this;
	}

	/**
	 * 绘制三角
	 * 
	 * @param t
	 */
	public GLEx drawTriangle(Triangle2f t) {
		return drawTriangle(t, 0, 0);
	}

	/**
	 * 绘制三角
	 * 
	 * @param t
	 * @param x
	 * @param y
	 */
	public GLEx drawTriangle(Triangle2f t, float x, float y) {
		if (t == null) {
			return this;
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
		return this;
	}

	/**
	 * 绘制椭圆
	 * 
	 * @param centerX
	 * @param centerY
	 * @param r
	 * @param Aa
	 */
	public GLEx drawOval(float x1, float y1, float width, float height) {
		if (this.lastBrush.alltextures) {
			drawOvalImpl(x1, y1, width, height);
			return this;
		} else {
			return this.drawArc(x1, y1, width, height, 32, 0, 360);
		}
	}

	/**
	 * 填充椭圆
	 * 
	 * @param centerX
	 * @param centerY
	 * @param r
	 * @param Aa
	 */
	public GLEx fillOval(float x1, float y1, float width, float height) {
		if (this.lastBrush.alltextures) {
			fillOvalImpl(x1, y1, width, height);
			return this;
		} else {
			return this.fillArc(x1, y1, width, height, 32, 0, 360);
		}
	}

	/**
	 * 绘制色彩点
	 * 
	 * @param x
	 * @param y
	 */
	public GLEx drawPoint(float x, float y) {
		if (this.lastBrush.alltextures) {
			drawPointImpl(x, y);
		} else {
			beginRenderer(GLType.Point);
			int argb = LColor.combine(this.lastBrush.fillColor, this.lastBrush.baseColor);
			glRenderer.setColor(argb);
			glRenderer.point(x, y);
			endRenderer();
		}
		return this;
	}

	/**
	 * 绘制色彩点
	 * 
	 * @param x
	 * @param y
	 */
	public GLEx drawPoint(float x, float y, int color) {
		if (this.lastBrush.alltextures) {
			int tmp = this.lastBrush.baseColor;
			setColor(color);
			drawPointImpl(x, y);
			setColor(tmp);
		} else {
			beginRenderer(GLType.Point);
			int argb = LColor.combine(this.lastBrush.baseColor, color);
			glRenderer.setColor(argb);
			glRenderer.point(x, y);
			endRenderer();
		}
		return this;
	}

	/**
	 * 绘制一组色彩点
	 * 
	 * @param x
	 * @param y
	 * @param size
	 */
	public GLEx drawPoints(float[] x, float[] y, int size) {
		if (this.lastBrush.alltextures) {
			for (int i = 0; i < size; i++) {
				drawPointImpl(x[i], y[i]);
			}
		} else {
			beginRenderer(GLType.Point);
			int argb = LColor.combine(this.lastBrush.fillColor, this.lastBrush.baseColor);
			glRenderer.setColor(argb);
			for (int i = 0; i < size; i++) {
				glRenderer.point(x[i], y[i]);
			}
			endRenderer();
		}
		return this;
	}

	/**
	 * 填充多边形
	 * 
	 * @param xPoints
	 * @param yPoints
	 * @param nPoints
	 */
	public final GLEx fillPolygon(float[] xPoints, float[] yPoints, int nPoints) {
		if (isClosed) {
			return this;
		}
		if (this.lastBrush.alltextures) {
			fillPolygonImpl(xPoints, yPoints, nPoints);
		} else {
			fill(new Polygon(xPoints, yPoints, nPoints));
		}
		return this;
	}

	/**
	 * 绘制多边形轮廓
	 * 
	 * @param xPoints
	 * @param yPoints
	 * @param nPoints
	 */
	public GLEx drawPolygon(float[] xPoints, float[] yPoints, int nPoints) {
		if (isClosed) {
			return this;
		}
		if (this.lastBrush.alltextures) {
			drawPolygonImpl(xPoints, yPoints, nPoints);
		} else {
			draw(new Polygon(xPoints, yPoints, nPoints));
		}
		return this;
	}

	/**
	 * 绘制一个矩形
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	public final GLEx drawRect(final float x1, final float y1, final float x2, final float y2) {
		setRect(x1, y1, x2, y2, false);
		return this;
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
	public final GLEx drawRect(final float x1, final float y1, final float x2, final float y2, LColor color) {
		int argb = this.lastBrush.baseColor;
		setColor(color);
		setRect(x1, y1, x2, y2, false);
		setColor(argb);
		return this;
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
	public final GLEx drawRect(final float x1, final float y1, final float x2, final float y2, int color) {
		int argb = this.lastBrush.baseColor;
		setColor(color);
		setRect(x1, y1, x2, y2, false);
		setColor(argb);
		return this;
	}

	/**
	 * 填充一个矩形
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	public final GLEx fillRect(final float x1, final float y1, final float x2, final float y2, LColor color) {
		int argb = this.lastBrush.baseColor;
		setColor(color);
		setRect(x1, y1, x2, y2, true);
		setColor(argb);
		return this;
	}

	/**
	 * 填充一个矩形
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	public final GLEx fillRect(final float x1, final float y1, final float x2, final float y2, int color) {
		int argb = this.lastBrush.baseColor;
		setColor(color);
		setRect(x1, y1, x2, y2, true);
		setColor(argb);
		return this;
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
	public final GLEx setRect(float x, float y, float width, float height, boolean fill) {
		if (isClosed) {
			return this;
		}
		if (this.lastBrush.alltextures) {
			if (fill) {
				fillRectNative(x, y, width, height);
			} else {
				float tempX = x;
				float tempY = y;
				float tempWidth = x + width;
				float tempHeight = y + height;
				if (tempX > tempWidth) {
					x = tempX;
					tempX = tempWidth;
					tempWidth = x;
				}
				if (tempY > tempHeight) {
					y = tempY;
					tempY = tempHeight;
					tempHeight = y;
				}
				drawLine(tempX, tempY, tempHeight, tempY, this.lastBrush.lineWidth);
				drawLine(tempX, tempY + 1, tempX, tempHeight, this.lastBrush.lineWidth);
				drawLine(tempHeight, tempHeight, tempX + 1, tempHeight, this.lastBrush.lineWidth);
				drawLine(tempHeight, tempHeight - 1, tempHeight, tempY + 1, this.lastBrush.lineWidth);
			}
			return this;
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
		return this;
	}

	/**
	 * 绘制指定大小的弧度
	 * 
	 * @param rect
	 * @param segments
	 * @param start
	 * @param end
	 */
	public final GLEx drawArc(RectBox rect, int segments, float start, float end) {
		return drawArc(rect.x, rect.y, rect.width, rect.height, segments, start, end);
	}

	/**
	 * 绘制指定大小的弧度
	 * 
	 * @param x1
	 * @param y1
	 * @param width
	 * @param height
	 * @param start
	 * @param end
	 * @return
	 */
	public final GLEx drawArc(float x1, float y1, float width, float height, float start, float end) {
		return drawArc(x1, y1, width, height, 40, start, end);
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
	public final GLEx drawArc(float x1, float y1, float width, float height, int segments, float start, float end) {
		if (isClosed) {
			return this;
		}
		if (this.lastBrush.alltextures) {
			drawArcImpl(x1, y1, width, height, start, end);
		} else {
			while (end < start) {
				end += 360;
			}
			float radiusW = width / 2.0f;
			float radiusH = height / 2.0f;
			float cx = x1 + radiusW;
			float cy = y1 + radiusH;
			if ((int) radiusW == (int) radiusH) {
				beginRenderer(GLType.Line);
				int argb = LColor.combine(this.lastBrush.fillColor, this.lastBrush.baseColor);
				glRenderer.setColor(argb);
				if (end - start == 360) {
					glRenderer.oval(cx, cy, MathUtils.min(radiusW, radiusH));
				} else {
					glRenderer.arc(cx, cy, MathUtils.min(radiusW, radiusH), start, end, segments);
				}
				endRenderer();
			} else {
				draw(new Ellipse(cx, cy, radiusW, radiusH, start, end, segments));
			}
		}
		return this;
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
	public final GLEx fillArc(float x1, float y1, float width, float height, float start, float end) {
		return fillArc(x1, y1, width, height, 40, start, end);
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
	public final GLEx fillArc(float x1, float y1, float width, float height, int segments, float start, float end) {
		if (isClosed) {
			return this;
		}
		if (this.lastBrush.alltextures) {
			fillArcImpl(x1, y1, width, height, start, end);
		} else {
			while (end < start) {
				end += 360;
			}
			float radiusW = width / 2.0f;
			float radiusH = height / 2.0f;
			float cx = x1 + radiusW;
			float cy = y1 + radiusH;
			beginRenderer(GLType.Filled);
			int argb = LColor.combine(this.lastBrush.fillColor, this.lastBrush.baseColor);
			glRenderer.setColor(argb);
			if (end - start == 360) {
				glRenderer.oval(cx, cy, MathUtils.min(radiusW, radiusH));
			} else {
				glRenderer.arc(cx, cy, MathUtils.min(radiusW, radiusH), start, end, segments);
			}
			endRenderer();
		}
		return this;
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
	public final GLEx drawRoundRect(float x, float y, float width, float height, int radius) {
		return drawRoundRect(x, y, width, height, radius, 40);
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
	public final GLEx drawRoundRect(float x, float y, float width, float height, int radius, int segs) {
		if (isClosed) {
			return this;
		}
		if (this.lastBrush.alltextures) {
			drawRoundRectImpl(x, y, width, height, radius);
		} else {
			if (radius < 0) {
				throw new IllegalArgumentException("radius > 0");
			}
			if (radius == 0) {
				drawRect(x, y, width, height);
				return this;
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
		return this;
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
	public final GLEx fillRoundRect(float x, float y, float width, float height, int cornerRadius) {
		return fillRoundRect(x, y, width, height, cornerRadius, 40);
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
	public final GLEx fillRoundRect(float x, float y, float width, float height, int radius, int segs) {
		if (isClosed) {
			return this;
		}
		if (this.lastBrush.alltextures) {
			fillRoundRectImpl(x, y, width, height, radius);
		} else {
			if (radius < 0) {
				throw new IllegalArgumentException("radius > 0");
			}
			if (radius == 0) {
				fillRect(x, y, width, height);
				return this;
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
		return this;
	}

	/**
	 * 统一偏移drawString的X轴
	 * 
	 * @return
	 */
	public float getOffsetStringX() {
		return offsetStringX;
	}

	public void setOffsetStringX(float offsetStringX) {
		this.offsetStringX = offsetStringX;
	}

	/**
	 * 统一偏移drawString的Y轴
	 * 
	 * @return
	 */
	public float getOffsetStringY() {
		return offsetStringY;
	}

	public void setOffsetStringY(float offsetStringY) {
		this.offsetStringY = offsetStringY;
	}

	/**
	 * PS:此处drawString，相比旧版做了一些改变，旧版是按照java标注的drawstring函数绘制字符串，
	 * 减去ascent值后再进行显示，而目前版本则按照xna的字符模式，不再减去该值，所以默认显示位置有所变化。如果要实现
	 * loon-0.5以前版本的drawString功能，请使用drawText函数.(最关键的是，loon文字显示默认使用本机字体，
	 * 而非强制导入ttf或者图片字体，因此ascent这个值，随着运行环境不同，会有细微变化，所以使用旧版drawString
	 * 显示字符位置，会随着系统出现微妙变化，而新版中则希望样式更为统一(如果减去size值，可以相对固定位置，但不一定能和
	 * 当前系统字体的实际大小配合，显示位置同样可能存在细微差异，始终无位移始终最稳妥)).
	 */
	/**
	 * 输出字符串
	 * 
	 * @param string
	 * @param position
	 */
	public GLEx drawString(String string, Vector2f position) {
		return drawString(string, position.x, position.y, tmpColor.setColor(this.lastBrush.baseColor));
	}

	/**
	 * 输出字符串
	 * 
	 * @param string
	 * @param position
	 * @param color
	 */
	public GLEx drawString(String string, Vector2f position, LColor color) {
		return drawString(string, position.x, position.y, color);
	}

	/**
	 * 输出字符串
	 * 
	 * @param string
	 * @param x
	 * @param y
	 */
	public GLEx drawString(String string, float x, float y) {
		return drawString(string, x, y, tmpColor.setColor(this.lastBrush.baseColor));
	}

	/**
	 * 输出字符串
	 * 
	 * @param string
	 * @param x
	 * @param y
	 * @param color
	 */
	public GLEx drawString(String string, float x, float y, LColor color) {
		return drawString(string, x, y, 0, color);
	}

	/**
	 * 输出字符串
	 * 
	 * @param string
	 * @param x
	 * @param y
	 * @param rotation
	 */
	public GLEx drawString(String string, float x, float y, float rotation) {
		return drawString(string, x, y, rotation, tmpColor.setColor(this.lastBrush.baseColor));
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
	public GLEx drawString(String string, float x, float y, float rotation, LColor c) {
		if (isClosed) {
			return this;
		}
		if (this.lastBrush.font != null) {
			this.lastBrush.font.drawString(this, string, x + offsetStringX, y + offsetStringY, rotation, c);
		}
		return this;
	}

	/**
	 * 输出字符串
	 * 
	 * @param mes
	 * @param x
	 * @param y
	 * @param scaleX
	 * @param scaleY
	 * @param ax
	 * @param ay
	 * @param rotation
	 * @param c
	 * @return
	 */
	public GLEx drawString(String mes, float x, float y, float scaleX, float scaleY, float ax, float ay, float rotation,
			LColor c) {
		if (isClosed) {
			return this;
		}
		if (c == null) {
			return this;
		}
		if (mes == null || mes.length() == 0) {
			return this;
		}
		this.lastBrush.font.drawString(this, mes, x + offsetStringX, y + offsetStringY, scaleX, scaleY, ax, ay,
				rotation, c);
		return this;
	}

	/**
	 * 输出字符串
	 * 
	 * @param message
	 * @param x
	 * @param y
	 * @param c1
	 * @param c2
	 * @return
	 */
	public GLEx drawString(String message, float x, float y, int c1, int c2) {
		if (isClosed) {
			return this;
		}
		int tmp = this.lastBrush.baseColor;
		setColor(c1);
		drawString(message, x + 1, y);
		drawString(message, x - 1, y);
		drawString(message, x, y + 1);
		drawString(message, x, y - 1);
		setColor(c2);
		drawString(message, x, y);
		setColor(tmp);
		return this;
	}

	/**
	 * 输出字符串
	 * 
	 * @param message
	 * @param x
	 * @param y
	 * @param c1
	 * @param c2
	 * @return
	 */
	public GLEx drawString(String message, float x, float y, LColor c1, LColor c2) {
		if (isClosed) {
			return this;
		}
		int tmp = this.lastBrush.baseColor;
		setColor(c1);
		drawString(message, x + 1, y);
		drawString(message, x - 1, y);
		drawString(message, x, y + 1);
		drawString(message, x, y - 1);
		setColor(c2);
		drawString(message, x, y);
		setColor(tmp);
		return this;
	}

	/**
	 * 输出字符
	 * 
	 * @param chars
	 * @param x
	 * @param y
	 */
	public GLEx drawChar(char chars, float x, float y) {
		return drawChar(chars, x, y, 0);
	}

	/**
	 * 输出字符
	 * 
	 * @param chars
	 * @param x
	 * @param y
	 * @param rotation
	 */
	public GLEx drawChar(char chars, float x, float y, float rotation) {
		return drawChar(chars, x, y, rotation, tmpColor.setColor(this.lastBrush.baseColor));
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
	public GLEx drawChar(char chars, float x, float y, float rotation, LColor c) {
		return drawString(String.valueOf(chars), x, y, rotation, c);
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
	public GLEx drawRegion(Painter texture, int x_src, int y_src, int width, int height, int transform, int x_dst,
			int y_dst, int anchor) {
		return drawRegion(texture, x_src, y_src, width, height, transform, x_dst, y_dst, anchor, null);
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
	 * @return
	 */
	public GLEx drawRegion(Painter texture, int x_src, int y_src, int width, int height, int transform, int x_dst,
			int y_dst, int anchor, LColor c) {
		return drawRegion(texture, x_src, y_src, width, height, transform, x_dst, y_dst, anchor, c, 0);
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
	public GLEx drawRegion(Painter texture, int x_src, int y_src, int width, int height, int transform, int x_dst,
			int y_dst, int anchor, LColor c, float radius) {
		return drawRegion(texture, x_src, y_src, width, height, transform, x_dst, y_dst, anchor, c, null, radius);
	}

	public GLEx drawRegion(Painter texture, int x_src, int y_src, int width, int height, int transform, int x_dst,
			int y_dst, int anchor, LColor c, Vector2f pivot, float radius) {
		return drawRegion(texture, x_src, y_src, width, height, transform, x_dst, y_dst, anchor, c, pivot, 1f, 1f,
				radius);
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
	 * @param pivot
	 * @param radius
	 * @return
	 */
	public GLEx drawRegion(Painter texture, int x_src, int y_src, int width, int height, int transform, int x_dst,
			int y_dst, int anchor, LColor c, Vector2f pivot, float sx, float sy, float radius) {
		if (isClosed) {
			return this;
		}
		if (x_src + width > texture.width() || y_src + height > texture.height() || width < 0 || height < 0 || x_src < 0
				|| y_src < 0) {
			throw new IllegalArgumentException("Area out of texture");
		}
		int dW = width, dH = height;

		float rotate = radius;
		Direction dir = Direction.TRANS_NONE;

		switch (transform) {
		case LTrans.TRANS_NONE: {
			break;
		}
		case LTrans.TRANS_ROT90: {
			rotate = 90 + radius;
			dW = height;
			dH = width;
			break;
		}
		case LTrans.TRANS_ROT180: {
			rotate = 180 + radius;
			break;
		}
		case LTrans.TRANS_ROT270: {
			rotate = 270 + radius;
			dW = height;
			dH = width;
			break;
		}
		case LTrans.TRANS_MIRROR: {
			dir = Direction.TRANS_MIRROR;
			break;
		}
		case LTrans.TRANS_MIRROR_ROT90: {
			dir = Direction.TRANS_MIRROR;
			rotate = -90 + radius;
			dW = height;
			dH = width;
			break;
		}
		case LTrans.TRANS_MIRROR_ROT180: {
			dir = Direction.TRANS_MIRROR;
			rotate = -180 + radius;
			break;
		}
		case LTrans.TRANS_MIRROR_ROT270: {
			dir = Direction.TRANS_MIRROR;
			rotate = -270 + radius;
			dW = height;
			dH = width;
			break;
		}
		default:
			throw new IllegalArgumentException("Bad transform");
		}

		boolean badAnchor = false;

		if (anchor == 0) {
			anchor = LTrans.TOP | LTrans.LEFT;
		}

		if ((anchor & 0x7f) != anchor || (anchor & LTrans.BASELINE) != 0) {
			badAnchor = true;
		}

		if ((anchor & LTrans.TOP) != 0) {
			if ((anchor & (LTrans.VCENTER | LTrans.BOTTOM)) != 0) {
				badAnchor = true;
			}
		} else if ((anchor & LTrans.BOTTOM) != 0) {
			if ((anchor & LTrans.VCENTER) != 0) {
				badAnchor = true;
			} else {
				y_dst -= dH - 1;
			}
		} else if ((anchor & LTrans.VCENTER) != 0) {
			y_dst -= (dH - 1) >>> 1;
		} else {
			badAnchor = true;
		}

		if ((anchor & LTrans.LEFT) != 0) {
			if ((anchor & (LTrans.HCENTER | LTrans.RIGHT)) != 0) {
				badAnchor = true;
			}
		} else if ((anchor & LTrans.RIGHT) != 0) {
			if ((anchor & LTrans.HCENTER) != 0) {
				badAnchor = true;
			} else {
				x_dst -= dW - 1;
			}
		} else if ((anchor & LTrans.HCENTER) != 0) {
			x_dst -= (dW - 1) >>> 1;
		} else {
			badAnchor = true;
		}
		if (badAnchor) {
			throw new IllegalArgumentException("Bad Anchor");
		}

		return draw(texture, x_dst, y_dst, width, height, x_src, y_src, x_src + width, y_src + height, c, rotate, sx,
				sy, null, pivot, dir);
	}

	public GLEx setLineWidth(float width) {
		if (isClosed) {
			return this;
		}
		if (width != this.lastBrush.lineWidth) {
			this.lastBrush.lineWidth = width;
			batch.gl.glLineWidth(width);
		}
		return this;
	}

	public GLEx resetLineWidth() {
		if (isClosed) {
			return this;
		}
		if (this.lastBrush.lineWidth != 1f) {
			batch.gl.glLineWidth(1f);
			this.lastBrush.lineWidth = 1f;
		}
		return this;
	}

	public float getLineWidth() {
		return this.lastBrush.lineWidth;
	}

	public boolean disposed() {
		return this.isClosed;
	}

	/**
	 * GLEx是否全部渲染都使用贴图进行
	 * 
	 * @return
	 */
	public boolean isAlltextures() {
		return this.lastBrush.alltextures;
	}

	/**
	 * 此项数值为true时,则GLEx全部Shape渲染都为贴图模拟
	 * 
	 * @param all
	 * @return
	 */
	public GLEx setAlltextures(boolean all) {
		this.lastBrush.alltextures = all;
		return this;
	}

	@Override
	protected void fillRectNative(float x, float y, float width, float height) {
		if (this.lastBrush.patternTex != null) {
			batch.addQuad(this.lastBrush.patternTex, this.lastBrush.baseColor, tx(), x, y, width, height);
		} else {
			batch.addQuad(colorTex, LColor.combine(this.lastBrush.fillColor, this.lastBrush.baseColor), tx(), x, y,
					width, height);
		}
	}

	@Override
	protected void drawLineImpl(float x1, float y1, float x2, float y2) {
		drawLine(x1, y1, x2, y2, getPixSkip());
	}

	@Override
	protected void drawPointNative(float x, float y, int skip) {
		if (!inside(x, y)) {
			if (this.lastBrush.patternTex != null) {
				batch.addQuad(this.lastBrush.patternTex, this.lastBrush.baseColor, lastTrans, x, y,
						skip + this.lastBrush.lineWidth, skip + this.lastBrush.lineWidth);
			} else {
				batch.addQuad(colorTex, LColor.combine(this.lastBrush.fillColor, this.lastBrush.baseColor), lastTrans,
						x, y, skip + this.lastBrush.lineWidth, skip + this.lastBrush.lineWidth);
			}
		}
	}

	/**
	 * width的缩放比例
	 * 
	 * @return
	 */
	public float getScaleX() {
		return scaleX;
	}

	/**
	 * height的缩放比率
	 * 
	 * @return
	 */
	public float getScaleY() {
		return scaleY;
	}

	@Override
	public void close() {
		this.isClosed = true;
		this.useBegin = false;
		if (glRenderer != null) {
			glRenderer.close();
		}
	}

}
