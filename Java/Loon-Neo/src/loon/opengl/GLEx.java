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
import loon.LSysException;
import loon.LSystem;
import loon.LTexture;
import loon.LTrans;
import loon.action.camera.BaseCamera;
import loon.canvas.LColor;
import loon.canvas.Paint;
import loon.canvas.Paint.Style;
import loon.font.IFont;
import loon.geom.Affine2f;
import loon.geom.BoxSize;
import loon.geom.Ellipse;
import loon.geom.Matrix3;
import loon.geom.Matrix4;
import loon.geom.PointF;
import loon.geom.Polygon;
import loon.geom.RectBox;
import loon.geom.RectF;
import loon.geom.RectI;
import loon.geom.Shape;
import loon.geom.Triangle2f;
import loon.geom.Vector2f;
import loon.geom.XY;
import loon.geom.XYZW;
import loon.opengl.GLRenderer.GLType;
import loon.utils.Array;
import loon.utils.GLUtils;
import loon.utils.IntMap;
import loon.utils.MathUtils;
import loon.utils.TArray;

/**
 * 一个全局使用的渲染器,内部为OpenGL封装,混合有JavaSE(JavaME)的Graphics和Android的Canvas主要API功能
 */
public class GLEx extends BatchEx<GLEx> implements LRelease {

	/*
	 * 内部类，用来保存与复位GLEx的基本渲染参数
	 */
	private static class BrushSave {
		int baseColor = LColor.DEF_COLOR;
		int fillColor = LColor.DEF_COLOR;
		int pixSkip = def_skip;
		float lineWidth = 1f;
		float baseAlpha = 1f;
		int blend = BlendMethod.MODE_NORMAL;

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

	// 如果此项为true,则GLEx循环中每次begin会启动一个FrameBuffer保存,每次end会关闭一个FrameBuffer并将对应纹理保存至Texture
	// 以此完整保存全局FrameBuffer内容(否则GLEx中每次begin时FrameBuffer绑定都会刷新，如果多次begin和end，将无法保存全部内容)
	private boolean saveToFrameBufferTexture;

	private final Vector2f tempLocation = new Vector2f();

	private final Affine2f tempAffine = new Affine2f();

	private final IntMap<PointF> rhombusArray = new IntMap<PointF>();

	private final Array<LTextureImage> frameBuffers = new Array<LTextureImage>();

	private final Array<Affine2f> affineStack = new Array<Affine2f>();

	private final Array<BrushSave> brushStack = new Array<BrushSave>();

	private final TArray<RectBox> scissors = new TArray<RectBox>();

	private final TArray<LTexture> bufferTextures = new TArray<LTexture>();

	private final LColor currentColorUpdate = new LColor();

	private final LColor currentColorTemp = new LColor();

	private final LTexture colorTex;

	protected RenderTarget target;

	private boolean useBegin;

	private GLRenderer glRenderer;

	private boolean rendererDrawLocked = false;

	private int scissorDepth;

	private boolean isClosed = false;

	private Graphics gfx;

	private BaseBatch batch;

	private Affine2f lastTrans;

	private BrushSave lastBrush;

	private LTextureImage lastFrameBuffer;

	private float triangleValue = 0.5235988f;

	private float scaleX = 1f, scaleY = 1f;

	private float offsetStringX = 0, offsetStringY = 0;

	private int _oldFill = -1;

	private int _oldBase = -1;

	private int _oldResult = -1;

	private final Polygon _currentPolys = new Polygon();

	public GLEx(Graphics gfx, RenderTarget target, GL20 gl, boolean alltex, boolean saveFrameBuffer) {
		this(gfx, target, createDefaultBatch(gl), alltex, saveFrameBuffer);
	}

	public GLEx(Graphics gfx, RenderTarget target, GL20 gl) {
		this(gfx, target, createDefaultBatch(gl), false, false);
	}

	public RenderTarget createTarget(LTexture tex) {
		return RenderTarget.create(gfx, tex);
	}

	/**
	 * 创建一个默认的GL渲染封装，将其作为默认的渲染器来使用。与0.5以前版本不同的是,此GLEX将不再唯一，允许复数构建.
	 * 如果使用HTML5，则禁止非纹理的渲染方式（因为部分浏览器不支持，会自动用纹理方式替代，但是glBegin到glEnd的 直接像素渲染方式则会禁用）.
	 * 
	 * PS:只有在LGame中注入的，可以影响全局渲染.
	 * 
	 * @param gfx
	 * @param target
	 * @param def
	 * @param alltex
	 */
	public GLEx(Graphics gfx, RenderTarget target, BaseBatch def, boolean alltex, boolean saveFrameBuffer) {
		super(0f, 0f, LSystem.viewSize.getRect(), LSystem.viewSize.width, LSystem.viewSize.height, def_skip);
		this.gfx = gfx;
		this.target = target;
		this.batch = def;
		this.affineStack.add(lastTrans = new Affine2f());
		this.colorTex = gfx.finalColorTex();
		if (target != null) {
			this.scale(scaleX = gfx.onDPI(target.xscale()), scaleY = gfx.onDPI(target.yscale()));
		} else {
			this.scale(scaleX = gfx.onDPI(LSystem.getScaleWidth()), scaleY = gfx.onDPI(LSystem.getScaleHeight()));
		}
		this.lastBrush = new BrushSave();
		this.lastBrush.font = LSystem.getSystemGameFont();
		this.lastBrush.alltextures = alltex;
		this.lastBrush.pixSkip = LSystem.isHTML5() ? def_skip_html5 : def_skip;
		this.lastBrush.blend = BlendMethod.MODE_NORMAL;
		this.brushStack.add(lastBrush);
		this.saveToFrameBufferTexture = saveFrameBuffer;
		this.triangleValue = 0.5235988f;
		this.scaleX = 1f;
		this.scaleY = 1f;
		this.offsetStringX = 0;
		this.offsetStringY = 0;
		this._oldFill = -1;
		this._oldBase = -1;
		this._oldResult = -1;
		this.update();
	}

	public boolean isSaveFrameBuffer() {
		return this.saveToFrameBufferTexture;
	}

	public GLEx setRenderTarget(RenderTarget tar) {
		if (tar != null && this.target != null && this.target != tar && this.target.id() != tar.id()) {
			this.target.close();
			this.target = tar;
		}
		return this;
	}

	public RenderTarget getRenderTarget() {
		return this.target;
	}

	public void bindRenderTarget() {
		this.target.bind();
	}

	public void unbindRenderTarget() {
		this.target.unbind();
	}

	public GLEx updateSize(float sx, float sy) {
		Affine2f tx = affineStack.first();
		if (tx != null) {
			this.scaleX = gfx.onDPI(sx);
			this.scaleY = gfx.onDPI(sy);
			tx.setScale(scaleX, scaleY);
		}
		return this;
	}

	public GLEx resize() {
		if (target != null) {
			updateSize(target.xscale(), target.yscale());
		} else {
			updateSize(LSystem.getScaleWidth(), LSystem.getScaleHeight());
		}
		return this;
	}

	public int getWidth() {
		if (target != null) {
			return MathUtils.iceil(target.width() / target.xscale());
		}
		return LSystem.viewSize.getWidth();
	}

	public int getHeight() {
		if (target != null) {
			return MathUtils.iceil(target.height() / target.yscale());
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
		startFrameBuffer();
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
		stopFrameBuffer();
		batch.end();
		return this;
	}

	/**
	 * 提交纹理渲染结果到GPU
	 * 
	 * @return
	 */
	public GLEx flush() {
		if (isClosed) {
			return this;
		}
		if (batch == null) {
			return this;
		}
		batch.flush();
		return this;
	}

	public BaseBatch batch() {
		return batch;
	}

	/**
	 * 将当然GLEx缓存到一个单独的LTextureImage中去(用于获得指定范围内的texture)
	 * 
	 * @return
	 */
	public LTextureImage toFrameBufferTexture(boolean saveBuffer) {
		return new LTextureImage(this.gfx, this.batch, getWidth() * getScaleX(), getHeight() * getScaleY(),
				this.isAlltextures(), saveBuffer);
	}

	public LTextureImage toFrameBufferTexture() {
		return toFrameBufferTexture(false);
	}

	/**
	 * 替换当前Batch的着色器设置
	 * 
	 * @param source
	 * @return
	 */
	public GLEx setShaderSource(ShaderSource source) {
		if (batch != null) {
			batch.setShaderSource(source);
		}
		return this;
	}

	/**
	 * 获得当前Batch的着色器设置
	 * 
	 * @return
	 */
	public ShaderSource getShaderSource() {
		if (batch != null) {
			return batch.getShaderSource();
		}
		return null;
	}

	/**
	 * 判断当前GLEx的Batch是否正在运行
	 * 
	 * @return
	 */
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
		if (font == null) {
			return this;
		}
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
		if (batch != null) {
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
			this.setBlendMode(BlendMethod.MODE_NORMAL);
		}
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
		return lastBrush.blend;
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

	public GLEx restoreBrush(int idx) {
		if (isClosed) {
			return this;
		}
		lastBrush = brushStack.get(idx);
		if (lastBrush != null) {
			this.setFont(lastBrush.font);
			this.setLineWidth(lastBrush.lineWidth);
			this.setBlendMode(lastBrush.blend);
		}
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
		this.setBlendMode(BlendMethod.MODE_NORMAL);
		brushStack.pop();
		return this;
	}

	public GLEx save() {
		this.saveTx();
		this.saveBrush();
		return this;
	}

	public int saveCount() {
		save();
		int size = affineStack.size();
		int idx = size > 0 ? size - 1 : 0;
		return idx;
	}

	public GLEx restore() {
		this.restoreTx();
		this.restoreBrush();
		return this;
	}

	public GLEx restoreToCount(int idx) {
		idx = MathUtils.clamp(idx, 0, affineStack.size() - 1);
		this.restoreTx(idx);
		this.restoreBrush(idx);
		return this;
	}

	public GLEx enableFrameBuffer() {
		this.saveToFrameBufferTexture = true;
		return this;
	}

	public GLEx disableFrameBuffer() {
		this.saveToFrameBufferTexture = false;
		return this;
	}

	public TArray<LTexture> getFrameBufferTextures() {
		return bufferTextures;
	}

	public GLEx saveFrameBuffer() {
		enableFrameBuffer();
		startFrameBuffer();
		return this;
	}

	public LTextureImage getFrameBuffers(int idx) {
		if (idx > -1 && idx < frameBuffers.size()) {
			return frameBuffers.get(idx);
		}
		return null;
	}

	public LTexture freeFrameBuffer(int idx) {
		stopFrameBuffer();
		LTexture texture = null;
		TArray<LTexture> temps = getFrameBufferTextures();
		if (temps.size - 1 >= idx) {
			texture = temps.get(idx);
		}
		return texture;
	}

	public GLEx startFrameBuffer() {
		if (isClosed) {
			return this;
		}
		if (this.saveToFrameBufferTexture) {
			if (frameBuffers != null) {
				frameBuffers.add(lastFrameBuffer = toFrameBufferTexture(false));
				bufferTextures.add(lastFrameBuffer.texture);
				lastFrameBuffer.begin();
			}
		}
		return this;
	}

	public GLEx stopFrameBuffer() {
		if (isClosed) {
			return this;
		}
		if (this.saveToFrameBufferTexture) {
			lastFrameBuffer = frameBuffers.pop();
			if (lastFrameBuffer != null) {
				lastFrameBuffer.end().close();
			}
		}
		return this;
	}

	public GLEx clearFrame() {
		if (saveToFrameBufferTexture) {
			clearFrameBuffer();
			clearFrameTextures();
		}
		return this;
	}

	public GLEx clearFrameBuffer() {
		if (isClosed) {
			return this;
		}
		if (frameBuffers != null) {
			for (; frameBuffers.hashNext();) {
				LTextureImage buffer = frameBuffers.next();
				if (buffer != null) {
					buffer.close();
				}
			}
			frameBuffers.stopNext();
			frameBuffers.clear();
		}
		return this;
	}

	public GLEx clearFrameTextures() {
		if (isClosed) {
			return this;
		}
		if (bufferTextures != null) {
			for (int i = bufferTextures.size() - 1; i > -1; i--) {
				LTexture texture = bufferTextures.get(i);
				if (texture != null) {
					texture.close(true);
				}
			}
			bufferTextures.clear();
		}
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

	public GLEx restoreTx(int idx) {
		if (isClosed) {
			return this;
		}
		lastTrans = affineStack.get(idx);
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

	public final LTexture getPatternTex() {
		return lastBrush.patternTex == null ? colorTex : lastBrush.patternTex;
	}

	public boolean setClip(BoxSize rect) {
		if (rect == null) {
			return false;
		}
		return startClipped(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
	}

	public boolean setClip(float x, float y, float width, float height) {
		return startClipped(x, y, width, height);
	}

	public boolean startClipped(float x1, float y1, float w1, float h1) {
		if (isClosed) {
			return false;
		}
		int x = MathUtils.floor(x1 * LSystem.getScaleWidth());
		int y = MathUtils.floor(y1 * LSystem.getScaleHeight());
		int width = MathUtils.floor(w1 * LSystem.getScaleWidth());
		int height = MathUtils.floor(h1 * LSystem.getScaleHeight());
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

	public GLEx resetClip() {
		GLUtils.disablecissorTest(batch.gl);
		return this;
	}

	public GLEx endClipped() {
		if (isClosed) {
			return this;
		}
		batch.flush();
		RectBox r = popScissorState();
		if (r == null) {
			resetClip();
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

	public GLEx concat(Affine2f aff) {
		if (isClosed) {
			return this;
		}
		saveTx();
		Affine2f txf = tx();
		txf.concat(aff);
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
		if (alpha == this.lastBrush.baseAlpha) {
			return this;
		}
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

	public GLEx reset(float red, float green, float blue, float alpha) {
		if (isClosed) {
			return this;
		}
		GLUtils.setClearColor(batch.gl, red, green, blue, alpha);
		this.setFont(LSystem.getSystemGameFont());
		this.lastBrush.baseColor = LColor.DEF_COLOR;
		this.lastBrush.fillColor = LColor.DEF_COLOR;
		this.lastBrush.baseAlpha = 1f;
		this.lastBrush.patternTex = null;
		this.setBlendMode(BlendMethod.MODE_NORMAL);
		this.resetLineWidth();
		return this;
	}

	public GLEx reset() {
		if (isClosed) {
			return this;
		}
		currentColorUpdate.setColor(this.lastBrush.baseColor);
		return reset(currentColorUpdate.r, currentColorUpdate.g, currentColorUpdate.b, currentColorUpdate.a);
	}

	protected int syncBrushColorInt() {
		if (this.lastBrush.fillColor == _oldFill && this.lastBrush.baseColor == _oldBase) {
			return _oldResult;
		}
		_oldFill = lastBrush.fillColor;
		_oldBase = lastBrush.baseColor;
		return _oldResult = LColor.combine(this.lastBrush.fillColor, this.lastBrush.baseColor);
	}

	protected int syncBrushColorInt(int color) {
		if (this.lastBrush.fillColor == color && this.lastBrush.baseColor == _oldBase) {
			return _oldResult;
		}
		_oldFill = color;
		_oldBase = lastBrush.baseColor;
		return _oldResult = LColor.combine(color, this.lastBrush.baseColor);
	}

	protected LColor syncBrushColor() {
		return currentColorUpdate.setColor(syncBrushColorInt());
	}

	protected LColor syncBrushColor(int color) {
		return currentColorUpdate.setColor(syncBrushColorInt(color));
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
		if (color == null) {
			return this;
		}
		setColor(color.getARGB());
		return this;
	}

	public GLEx setColor(int r, int g, int b) {
		currentColorTemp.setColor(r, g, b);
		return setColor(currentColorTemp.getRGB());
	}

	public GLEx setColor(int r, int g, int b, int a) {
		currentColorTemp.setColor(r, g, b, a);
		return setColor(currentColorTemp.getARGB());
	}

	public GLEx setColor(float r, float g, float b) {
		currentColorTemp.setFloatColor(r, g, b);
		return setColor(currentColorTemp.getRGB());
	}

	public GLEx setColor(float r, float g, float b, float a) {
		currentColorTemp.setFloatColor(r, g, b, a);
		return setColor(currentColorTemp.getARGB());
	}

	public GLEx setColor(int c) {
		if (this.lastBrush.baseColor == c && this.lastBrush.baseAlpha == 1f) {
			return this;
		}
		this.setTint(c);
		this.setAlpha(LColor.getAlpha(this.lastBrush.baseColor));
		this.lastBrush.fillColor = c;
		this.lastBrush.patternTex = null;
		return this;
	}

	public GLEx setTint(int r, int g, int b) {
		currentColorTemp.setColor(r, g, b);
		return setTint(currentColorTemp.getRGB());
	}

	public GLEx setTint(int r, int g, int b, int a) {
		currentColorTemp.setColor(r, g, b, a);
		return setTint(currentColorTemp.getARGB());
	}

	public GLEx setTint(float r, float g, float b) {
		currentColorTemp.setFloatColor(r, g, b);
		return setTint(currentColorTemp.getRGB());
	}

	public GLEx setTint(float r, float g, float b, float a) {
		currentColorTemp.setFloatColor(r, g, b, a);
		return setTint(currentColorTemp.getARGB());
	}

	public GLEx setTint(LColor color) {
		if (color == null) {
			return this;
		}
		return this.setTint(color.getARGB());
	}

	public GLEx setTint(int c) {
		if (this.lastBrush.baseColor == c && this.lastBrush.baseAlpha == 1f) {
			return this;
		}
		if (this.lastBrush.baseAlpha != 1f) {
			this.lastBrush.baseColor = c;
			int ialpha = (int) (0xFF * MathUtils.clamp(this.lastBrush.baseAlpha, 0, 1));
			this.lastBrush.baseColor = (ialpha << 24) | (this.lastBrush.baseColor & 0xFFFFFF);
		} else {
			this.lastBrush.baseColor = c;
		}
		return this;
	}

	private final Affine2f getTempAffine() {
		tempAffine.idt();
		return tempAffine;
	}

	public RectBox getViewport() {
		return GLUtils.getGLViewport(this.gfx.gl);
	}

	public int combineColor(int c) {
		int otint = this.lastBrush.baseColor;
		if (c != LColor.DEF_COLOR) {
			this.lastBrush.baseColor = otint = LColor.combine(c, otint);
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
					syncBrushColor(paint.color));
		} else if (this.lastBrush.font != null) {
			this.lastBrush.font.drawString(this, message, x + offsetStringX,
					y + offsetStringY - this.lastBrush.font.getAscent() - 1, rotation, syncBrushColor());
		}
		return this;
	}

	public GLEx drawText(String message, float x, float y, LColor color) {
		return drawText(message, x, y, color.getARGB(), 0);
	}

	public GLEx drawText(String message, float x, float y, LColor color, float rotation) {
		return drawText(message, x, y, color.getARGB(), rotation);
	}

	public GLEx drawText(String message, float x, float y, int color, float rotation) {
		int tmp = this.lastBrush.baseColor;
		if (this.lastBrush.font != null) {
			setColor(color);
			this.lastBrush.font.drawString(this, message, x + offsetStringX,
					y + offsetStringY - this.lastBrush.font.getAscent() - 1, rotation, syncBrushColor());
			setColor(tmp);
		}
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
		if (rotation == 0f) {
			return draw(texture, x, y, w, h);
		}
		return draw(texture, x, y, w, h, w / 2, h / 2, rotation);
	}

	public GLEx draw(Painter texture, float x, float y, float w, float h, Vector2f origin, float rotation) {
		if (origin == null) {
			return draw(texture, x, y, w, h, w / 2, h / 2, rotation);
		} else {
			return draw(texture, x, y, w, h, origin.x, origin.y, rotation);
		}
	}

	public GLEx draw(Painter texture, float x, float y, float w, float h, float originX, float originY,
			float rotation) {
		if (isClosed) {
			return this;
		}
		if (texture == null) {
			return this;
		}
		Affine2f xf = tx();
		if (rotation != 0) {
			xf = getTempAffine();
			float w1 = x + originX;
			float h1 = y + originY;
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
			xf = getTempAffine();
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
			xf = getTempAffine();
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

	public GLEx triangle(Painter texture, float x1, float y1, float x2, float y2, float x3, float y3) {
		if (isClosed) {
			return this;
		}
		if (texture == null) {
			return this;
		}
		texture.addToBatch(batch, this.lastBrush.baseColor, tx(), x1, y1, x2, y2, x3, y3, x3, y3);
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
			xf = getTempAffine();
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

	public GLEx drawFlip(Painter texture, float x, float y) {
		if (isClosed) {
			return this;
		}
		if (texture == null) {
			return this;
		}
		return drawFlip(texture, x, y, LColor.white);
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

	public GLEx drawFlip(Painter texture, float x, float y, float w, float h) {
		if (isClosed) {
			return this;
		}
		if (texture == null) {
			return this;
		}
		return draw(texture, x, y, w, h, 0, 0, texture.width(), texture.height(), LColor.white, 0f, null,
				Direction.TRANS_FLIP);
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

	public GLEx draw(Painter texture, float x, float y, LColor color, float rotation, Vector2f origin, float scale,
			Direction dir) {
		return draw(texture, x, y, color, rotation, origin, Vector2f.at(scale, scale), dir);
	}

	public GLEx draw(Painter texture, float x, float y, LColor color, float rotation, Vector2f origin, Vector2f scale,
			Direction dir) {
		return draw(texture, x, y, color, rotation, origin, scale, dir, false);
	}

	public GLEx draw(Painter texture, float x, float y, LColor color, float rotation, Vector2f origin, float scale,
			Direction dir, boolean offset) {
		return draw(texture, x, y, color, rotation, origin, Vector2f.at(scale, scale), dir, offset);
	}

	public GLEx draw(Painter texture, float x, float y, LColor color, float rotation, Vector2f origin, Vector2f scale,
			Direction dir, boolean offset) {
		if (isClosed) {
			return this;
		}
		if (texture == null) {
			return this;
		}
		return draw(texture, x, y, texture.width(), texture.height(), 0f, 0f, texture.width(), texture.height(), color,
				rotation, scale.x, scale.y, origin, null, dir, offset);
	}

	public GLEx draw(Painter texture, float x, float y, float width, float height, float srcX, float srcY,
			float srcWidth, float srcHeight, LColor color, float rotation, Vector2f origin, Vector2f scale,
			Direction dir) {
		return draw(texture, x, y, width, height, srcX, srcY, srcWidth, srcHeight, color, rotation, scale.x, scale.y,
				origin, dir);
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
				origin, null, dir, false);
	}

	public GLEx draw(Painter texture, float x, float y, float width, float height, float srcX, float srcY,
			float srcWidth, float srcHeight, LColor color, float rotation, float scaleX, float scaleY, Vector2f origin,
			Vector2f pivot, Direction dir) {
		return draw(texture, x, y, width, height, srcX, srcY, srcWidth, srcHeight, color, rotation, scaleX, scaleY,
				origin, pivot, dir, false);
	}

	public GLEx draw(Painter texture, float x, float y, float width, float height, float srcX, float srcY,
			float srcWidth, float srcHeight, LColor color, float rotation, float scaleX, float scaleY, Vector2f origin,
			Vector2f pivot, Direction dir, boolean offset) {
		if (isClosed) {
			return this;
		}
		if (texture == null) {
			return this;
		}

		Affine2f xf = tx();

		final boolean dirDirty = (dir != null && dir != Direction.TRANS_NONE);
		final boolean rotDirty = (rotation != 0 || pivot != null);
		final boolean scaleDirty = !(scaleX == 1 && scaleY == 1);

		if (dirDirty || rotDirty || scaleDirty) {
			xf = getTempAffine();

			float originX = width / 2;
			float originY = height / 2;

			if (origin != null) {
				if (origin.x == 0 && origin.y == 0) {
					originX = origin.x == 0 ? width / 2 : origin.x;
					originY = origin.y == 0 ? height / 2 : origin.y;
				} else {
					originX = origin.x;
					originY = origin.y;
				}
			}

			if (offset) {
				xf.translate(-originX, -originY);
			}

			float centerX = x + originX;
			float centerY = y + originY;
			if (rotDirty) {
				if (pivot != null && (pivot.x != -1 && pivot.y != -1)) {
					centerX = x + pivot.x;
					centerX = y + pivot.y;
				}
				xf.translate(centerX, centerY);
				xf.preRotate(rotation);
				xf.translate(-centerX, -centerY);
			}
			if (scaleDirty) {
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
					Affine2f.transformOrigin(xf, x, y, Affine2f.TRANS_MIRROR, originX, originY);
					break;
				case TRANS_FLIP:
					Affine2f.transformOrigin(xf, x, y, Affine2f.TRANS_MIRROR_ROT180, originX, originY);
					break;
				case TRANS_MF:
					Affine2f.transformOrigin(xf, x, y, Affine2f.TRANS_ROT180, originX, originY);
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

	public GLEx drawLine(XY a, XY b, float width, LColor color) {
		return drawLine(a.getX(), a.getY(), b.getX(), b.getY(), width, color);
	}

	public GLEx drawLine(float x0, float y0, float x1, float y1, LColor color) {
		return drawLine(x0, y0, x1, y1, this.lastBrush.lineWidth, color);
	}

	public GLEx drawLine(float x0, float y0, float x1, float y1, float width, LColor color) {
		int tmp = this.lastBrush.baseColor;
		setColor(color);
		drawLine(x0, y0, x1, y1, width);
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
		line(x0, y0, x1, y1, width);
		return this;
	}

	public GLEx drawLineGlow(float x0, float y0, float x1, float y1, LColor startColor, LColor endColor) {
		return drawLineGlow(x0, y0, x1, y1, lastBrush.lineWidth, startColor, endColor, 10);
	}

	public GLEx drawLineGlow(float x0, float y0, float x1, float y1, float width, LColor startColor, LColor endColor,
			int steps) {
		return drawLineGlow(x0, y0, x1, y1, width, width * 2f, startColor, endColor, steps);
	}

	public GLEx drawLineGlow(float x0, float y0, float x1, float y1, float width, float endWidth, LColor startColor,
			LColor endColor, int steps) {
		if (isClosed) {
			return this;
		}
		float step = (endWidth - width) / steps;
		float r = (endColor.r - startColor.r) / steps;
		float g = (endColor.g - startColor.g) / steps;
		float b = (endColor.b - startColor.b) / steps;
		float a = (endColor.a - startColor.a) / steps;

		for (int i = steps; i >= 0; i--) {
			drawLine(x0, y0, x1, y1, width + step * i, currentColorUpdate.setColor((startColor.r + r * i),
					(startColor.g + g * i), (startColor.b + b * i), (startColor.a + a * i)));
		}
		return this;
	}

	public GLEx drawLines(LColor color, XY... points) {
		return drawLines(color, this.lastBrush.lineWidth, points, false);
	}

	public GLEx drawLines(LColor color, float width, XY... points) {
		return drawLines(color, width, points, false);
	}

	public GLEx drawLines(LColor color, float width, XY[] points, boolean smooth) {
		if (isClosed) {
			return this;
		}
		if (points.length < 2) {
			return this;
		}
		if (smooth) {
			fillCircle(points[0].getX(), points[0].getY(), width / 2, color);
		}
		for (int i = 0; i < points.length - 1; i++) {
			XY cur = points[i];
			XY next = points[i + 1];
			if (next != null) {
				fillCircle(next.getX(), next.getY(), width / 2, color);
			}
			if (cur != null && next != null) {
				drawLine(cur, next, width, color);
			}
		}
		return this;
	}

	public GLEx drawDashLine(XY start, XY end, int gaps, float width, LColor color, boolean edges) {
		return drawDashLine(start.getX(), start.getY(), end.getX(), end.getY(), gaps, width, color, edges);
	}

	public GLEx drawDashLine(float x1, float y1, float x2, float y2, int gaps, LColor color, boolean edges) {
		return drawDashLine(x1, y1, x2, y2, gaps, this.lastBrush.lineWidth, color, edges);
	}

	public GLEx drawDashLine(float x1, float y1, float x2, float y2, int gaps, float width, LColor color,
			boolean edges) {
		if (isClosed) {
			return this;
		}
		if (gaps <= 0) {
			drawLine(x1, y1, x2, y2, width, color);
		} else {
			Vector2f w = new Vector2f(x2 - x1, y2 - y1);
			float l = w.length();
			Vector2f dir = w.div(l);
			int totalGaps = gaps * 2 + 1;
			float size = l / totalGaps;
			Vector2f offset = dir.mul(size);
			Vector2f cur = new Vector2f(x1, y1);
			for (int i = 0; i < totalGaps; i++) {
				if (i % 2 == 0) {
					Vector2f next = cur.add(offset);
					if (edges) {
						fillCircle(cur.x, cur.y, width * 0.5f, color);
						fillCircle(next.x, next.y, width * 0.5f, color);
					}
					drawLine(cur, next, width, color);
					cur = next;

				} else {
					cur = cur.add(offset);
				}
			}
		}
		return this;
	}

	public GLEx drawDashLine(XY start, XY end, int gaps, float gapSizeF, LColor color) {
		return drawDashLine(start.getX(), start.getY(), end.getX(), end.getY(), gaps, this.lastBrush.lineWidth, color,
				false);
	}

	public GLEx drawDashLine(XY start, XY end, int gaps, float gapSizeF, float width, LColor color, boolean edges) {
		return drawDashLine(start.getX(), start.getY(), end.getX(), end.getY(), gaps, width, color, edges);
	}

	public GLEx drawDashLine(XY start, XY end, int gaps, float gapSizeF, float width, LColor color) {
		return drawDashLine(start.getX(), start.getY(), end.getX(), end.getY(), gaps, gapSizeF, width, color, false);
	}

	public GLEx drawDashLine(float x1, float y1, float x2, float y2, int gaps, float gapSizeF, float width,
			LColor color) {
		return drawDashLine(x1, y1, x2, y2, gaps, gapSizeF, width, color, false);
	}

	public GLEx drawDashLine(float x1, float y1, float x2, float y2, int gaps, float gapSizeF, float width,
			LColor color, boolean edges) {
		if (isClosed) {
			return this;
		}
		if (gaps <= 0) {
			drawLine(x1, y1, x2, y2, width, color);
		} else {
			Vector2f cur = new Vector2f(x1, y1);
			Vector2f w = new Vector2f(x2, y2).sub(cur);
			float l = w.length();
			Vector2f dir = w.div(l);

			float totalGapSize = l * gapSizeF;
			float remaining = l - totalGapSize;
			float gapSize = totalGapSize / gaps;
			float size = remaining / (gaps + 1);

			Vector2f gapOffset = dir.mul(gapSize);
			Vector2f offset = dir.mul(size);

			int totalGaps = gaps * 2 + 1;

			for (int i = 0; i < totalGaps; i++) {
				if (i % 2 == 0) {
					Vector2f next = cur.add(offset);
					if (edges) {
						fillCircle(cur.x, cur.y, width * 0.5f, color);
						fillCircle(next.x, next.y, width * 0.5f, color);
					}
					drawLine(cur, next, width, color);
					cur = next;
				} else {
					cur = cur.add(gapOffset);
				}
			}
		}
		return this;
	}

	public GLEx drawGrid(XYZW rect, int lines, float size, LColor color) {
		return drawGrid(rect.getX(), rect.getY(), rect.getZ(), rect.getW(), lines, size, color);
	}

	public GLEx drawGrid(float x, float y, float w, float h, int lines, float size, LColor color) {
		if (isClosed) {
			return this;
		}
		float width = w;
		float height = h;
		float hGap = width / lines;
		float vGap = height / lines;
		Vector2f tl = new Vector2f(x, y);
		Vector2f tr = tl.add(width - hGap, 0f);
		Vector2f bl = tl.add(0f, height - vGap);
		for (int l = 0; l < lines; l++) {
			Vector2f xOffset = new Vector2f(hGap, 0f).mul(l);
			Vector2f yOffset = new Vector2f(0f, vGap).mul(l);
			drawLine(tl.add(xOffset), bl.add(xOffset), size, color);
			drawLine(tl.add(yOffset), tr.add(yOffset), size, color);
		}
		return this;
	}

	/**
	 * 虚线绘制
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param divisions
	 * @param width
	 * @return
	 */
	public GLEx drawDashLine(float x1, float y1, float x2, float y2, int divisions, float width, LColor color) {
		float dx = x2 - x1, dy = y2 - y1;
		for (int i = 0; i < divisions; i++) {
			if (i % 2 == 0) {
				drawLine(x1 + ((float) i / divisions) * dx, y1 + ((float) i / divisions) * dy,
						x1 + ((i + 1f) / divisions) * dx, y1 + ((i + 1f) / divisions) * dy, width, color);
			}
		}
		return this;
	}

	public GLEx drawDashLine(float x1, float y1, float x2, float y2, int divisions, LColor color) {
		return drawDashLine(x1, y1, x2, y2, divisions, this.lastBrush.lineWidth, color);
	}

	public GLEx drawDashLine(float x1, float y1, float x2, float y2, int divisions) {
		return drawDashLine(x1, y1, x2, y2, divisions, this.lastBrush.lineWidth, syncBrushColor());
	}

	public GLEx drawAngleLine(float x, float y, float angle, float length) {
		tempLocation.set(1f).setLength(length).setAngle(angle);
		return drawLine(x, y, x + tempLocation.x(), y + tempLocation.y(), this.lastBrush.lineWidth);
	}

	public GLEx drawAngleLine(float x, float y, float angle, float length, float width) {
		tempLocation.set(1f).setLength(length).setAngle(angle);
		return drawLine(x, y, x + tempLocation.x(), y + tempLocation.y(), width);
	}

	/**
	 * 从指定开始角度到终止角度绘制不合口圆形
	 * 
	 * @param x
	 * @param y
	 * @param startAngle
	 * @param endAngle
	 * @param radius
	 * @param color
	 * @return
	 */
	public GLEx drawStrokeCircle(float x, float y, float startAngle, float endAngle, float radius, LColor color) {
		return drawStrokeCircle(x, y, startAngle, endAngle, radius, 2, true, color);
	}

	/**
	 * 从指定开始角度到终止角度绘制不合口圆形
	 * 
	 * @param x
	 * @param y
	 * @param startAngle
	 * @param endAngle
	 * @param radius
	 * @param width
	 * @param width
	 * @param color
	 * @return
	 */
	public GLEx drawStrokeCircle(float x, float y, float startAngle, float endAngle, float radius, float width,
			LColor color) {
		return drawStrokeCircle(x, y, startAngle, endAngle, radius, width, true, color);
	}

	/**
	 * 从指定开始角度到终止角度绘制不合口圆形
	 * 
	 * @param x
	 * @param y
	 * @param startAngle
	 * @param endAngle
	 * @param radius
	 * @param width
	 * @param clockwise
	 * @param color
	 * @return
	 */
	public GLEx drawStrokeCircle(float x, float y, float startAngle, float endAngle, float radius, float width,
			boolean clockwise, LColor color) {
		int argb = this.lastBrush.baseColor;
		setColor(color);
		drawStrokeCircle(x, y, startAngle, endAngle, radius, width, clockwise);
		setColor(argb);
		return this;
	}

	/**
	 * 从指定开始角度到终止角度绘制不合口圆形
	 * 
	 * @param x
	 * @param y
	 * @param startAngle
	 * @param endAngle
	 * @param radius
	 * @param width
	 * @param clockwise
	 * @param color
	 * @return
	 */
	public GLEx drawStrokeCircle(float x, float y, float startAngle, float endAngle, float radius, float width,
			boolean clockwise, int color) {
		int argb = this.lastBrush.baseColor;
		setColor(color);
		drawStrokeCircle(x, y, startAngle, endAngle, radius, width, clockwise);
		setColor(argb);
		return this;
	}

	/**
	 * 从指定开始角度到终止角度绘制不合口圆形
	 * 
	 * @param x
	 * @param y
	 * @param startAngle
	 * @param endAngle
	 * @param radius
	 * @param width
	 * @param clockwise
	 * @return
	 */
	public GLEx drawStrokeCircle(float x, float y, float startAngle, float endAngle, float radius, float width,
			boolean clockwise) {
		if (startAngle > endAngle) {
			float newAngle = startAngle - endAngle;
			endAngle = startAngle;
			startAngle = newAngle;
		}

		final float newRadius = radius;
		final float newX = x + newRadius;
		final float newY = y + newRadius;

		final float fixV = clockwise ? -90 : +90;

		float scaleFactor = 0.6f;
		int sides = 10 + MathUtils.floor(newRadius * scaleFactor);

		final int startSide = (int) (sides / 360f * startAngle);

		tempLocation.set(0f);

		for (int i = startSide; i < sides; i++) {

			float v = endAngle / sides * i;

			tempLocation.set(newRadius, 0).setAngle(v + fixV);
			float x1 = tempLocation.x;
			float y1 = tempLocation.y;

			tempLocation.set(newRadius, 0).setAngle(endAngle / sides * (i + 1) + fixV);

			drawLine(x1 + newX, y1 + newY, tempLocation.x + newX, tempLocation.y + newY, width);

		}
		return this;
	}

	public GLEx drawStrokeGradientCircle(float x, float y, float startAngle, float endAngle, float radius, float width,
			int startColor, int endColor, float angle) {
		return drawStrokeGradientCircle(x, y, startAngle, endAngle, radius, width, startColor, endColor, true, 1.15f,
				angle);
	}

	public GLEx drawStrokeGradientCircle(float x, float y, float startAngle, float endAngle, float radius, float width,
			LColor startColor, LColor endColor, float angle) {
		return drawStrokeGradientCircle(x, y, startAngle, endAngle, radius, width,
				startColor == null ? -1 : startColor.getARGB(), endColor == null ? -1 : endColor.getARGB(), true, 1.15f,
				angle);
	}

	public GLEx drawStrokeGradientCircle(float x, float y, float startAngle, float endAngle, float radius, float width,
			int startColor, int endColor) {
		return drawStrokeGradientCircle(x, y, startAngle, endAngle, radius, width, startColor, endColor, 0f);
	}

	public GLEx drawStrokeGradientCircle(float x, float y, float startAngle, float endAngle, float radius, float width,
			LColor startColor, LColor endColor) {
		return drawStrokeGradientCircle(x, y, startAngle, endAngle, radius, width,
				startColor == null ? -1 : startColor.getARGB(), endColor == null ? -1 : endColor.getARGB(), 0f);
	}

	public GLEx drawStrokeGradientCircle(float x, float y, float startAngle, float endAngle, float width, float height,
			float size, int startColor, int endColor, float angle) {
		return drawStrokeGradientCircle(x, y, startAngle, endAngle, width, height, size, startColor, endColor, true,
				1.15f, angle);
	}

	public GLEx drawStrokeGradientCircle(float x, float y, float startAngle, float endAngle, float width, float height,
			float size, LColor startColor, LColor endColor, float angle) {
		return drawStrokeGradientCircle(x, y, startAngle, endAngle, width, height, size,
				startColor == null ? -1 : startColor.getARGB(), endColor == null ? -1 : endColor.getARGB(), true, 1.15f,
				angle);
	}

	public GLEx drawStrokeGradientCircle(float x, float y, float startAngle, float endAngle, float width, float height,
			float size, int startColor, int endColor) {
		return drawStrokeGradientCircle(x, y, startAngle, endAngle, width, height, size, startColor, endColor, 0f);
	}

	public GLEx drawStrokeGradientCircle(float x, float y, float startAngle, float endAngle, float width, float height,
			float size, LColor startColor, LColor endColor) {
		return drawStrokeGradientCircle(x, y, startAngle, endAngle, width, height, size,
				startColor == null ? -1 : startColor.getARGB(), endColor == null ? -1 : endColor.getARGB(), 0f);
	}

	public GLEx drawStrokeGradientCircle(float x, float y, float startAngle, float endAngle, float width, float height,
			float size, int startColor, int endColor, boolean clockwise, float space, float angle) {
		final float radiusW = width / 2f;
		final float radiusH = height / 2f;
		final float radius = (radiusW > radiusH ? (radiusH) : (radiusW));
		float centerX = 0f;
		float centerY = 0f;
		if (radiusH > radiusW) {
			centerX = x - (radiusW - radius);
			centerY = y + (radiusH - radius);
		} else if (radiusH < radiusW) {
			centerX = x + (radiusW - radius);
			centerY = y - (radiusH - radius);
		} else {
			centerX = x - (radiusW - radius);
			centerY = y - (radiusH - radius);
		}
		return drawStrokeGradientCircle(centerX, centerY, startAngle, endAngle, radius, size, startColor, endColor,
				angle);
	}

	/**
	 * 从指定开始角度到终止角度绘制不合口圆形并进行渐变
	 * 
	 * @param x
	 * @param y
	 * @param startAngle
	 * @param endAngle
	 * @param radius
	 * @param width
	 * @param startColor
	 * @param endColor
	 * @param clockwise
	 * @param space
	 * @return
	 */
	public GLEx drawStrokeGradientCircle(float x, float y, float startAngle, float endAngle, float radius, float width,
			int startColor, int endColor, boolean clockwise, float space, float angle) {
		if (startAngle == 0f && endAngle == 0f) {
			return this;
		}
		if ((startColor == -1 && endColor == -1) || (startColor == endColor)) {
			return drawStrokeCircle(x, y, startAngle, endAngle, radius, width, clockwise, endColor);
		}
		if (startAngle > endAngle) {
			float newAngle = startAngle - endAngle;
			endAngle = startAngle;
			startAngle = newAngle;
		}

		final float newRadius = radius;
		final float newX = x + newRadius;
		final float newY = y + newRadius;

		final float fixV = clockwise ? -90 : +90;

		int sides = MathUtils.floor(newRadius);

		final int startSide = (int) (sides / 360f * startAngle);

		tempLocation.set(0f);

		final int argb = this.lastBrush.baseColor;

		for (int i = startSide; i < sides; i++) {

			tempLocation.set(newRadius, 0).setAngle((endAngle / sides * i + fixV) + angle);
			float x1 = tempLocation.x;
			float y1 = tempLocation.y;

			tempLocation.set(newRadius, 0).setAngle((endAngle / sides * (i + space) + fixV) + angle);

			int color = LColor.getGradient(startColor, endColor, i / (float) sides);

			setColor(color);
			drawLine(x1 + newX, y1 + newY, tempLocation.x + newX, tempLocation.y + newY, width);
		}
		setColor(argb);

		return this;
	}

	public GLEx drawSpikes(float x, float y, float radius, float length, int spikes, float rot, float width) {
		tempLocation.set(0f, 1f);
		float step = 360f / spikes;

		for (int i = 0; i < spikes; i++) {
			tempLocation.setAngle(i * step + rot);
			tempLocation.setLength(radius);
			float x1 = tempLocation.x, y1 = tempLocation.y;
			tempLocation.setLength(radius + length);

			drawLine(x + x1, y + y1, x + tempLocation.x, y + tempLocation.y, width);
		}
		return this;
	}

	public GLEx drawSpikes(float x, float y, float rad, float length, int spikes) {
		return drawSpikes(x, y, rad, length, spikes, 0, this.lastBrush.lineWidth);
	}

	public GLEx drawSpikes(float x, float y, float rad, float length, int spikes, float width) {
		return drawSpikes(x, y, rad, length, spikes, 0, width);
	}

	public GLEx drawCurve(float x1, float y1, float cx1, float cy1, float cx2, float cy2, float x2, float y2,
			int segments) {
		return drawCurve(x1, y1, cx1, cy1, cx2, cy2, x2, y2, segments, this.lastBrush.lineWidth);
	}

	/**
	 * 绘制弧线
	 * 
	 * @param x1
	 * @param y1
	 * @param cx1
	 * @param cy1
	 * @param cx2
	 * @param cy2
	 * @param x2
	 * @param y2
	 * @param segments
	 * @param width
	 * @return
	 */
	public GLEx drawCurve(float x1, float y1, float cx1, float cy1, float cx2, float cy2, float x2, float y2,
			int segments, float width) {

		final float subdivstep = 1f / segments;
		final float subdiv_stepa = subdivstep * subdivstep;
		final float subdiv_stepb = subdivstep * subdivstep * subdivstep;

		final float pre1 = 3 * subdivstep;
		final float pre2 = 3 * subdiv_stepa;
		final float pre4 = 6 * subdiv_stepa;
		final float pre5 = 6 * subdiv_stepb;

		final float tmp1x = x1 - cx1 * 2 + cx2;
		final float tmp1y = y1 - cy1 * 2 + cy2;

		final float tmp2x = (cx1 - cx2) * 3 - x1 + x2;
		final float tmp2y = (cy1 - cy2) * 3 - y1 + y2;

		float fx = x1;
		float fy = y1;

		float dfx = (cx1 - x1) * pre1 + tmp1x * pre2 + tmp2x * subdiv_stepb;
		float dfy = (cy1 - y1) * pre1 + tmp1y * pre2 + tmp2y * subdiv_stepb;

		float ddfx = tmp1x * pre4 + tmp2x * pre5;
		float ddfy = tmp1y * pre4 + tmp2y * pre5;

		float dddfx = tmp2x * pre5;
		float dddfy = tmp2y * pre5;

		for (; segments-- > 0;) {
			float fxold = fx, fyold = fy;
			fx += dfx;
			fy += dfy;
			dfx += ddfx;
			dfy += ddfy;
			ddfx += dddfx;
			ddfy += dddfy;
			drawLine(fxold, fyold, fx, fy, width);
		}

		drawLine(fx, fy, x2, y2, width);
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

	public boolean isClip() {
		return scissorDepth != 0;
	}

	public int getClipX() {
		if (scissorDepth == 0) {
			return 0;
		}
		return scissors.get(scissorDepth - 1).x();
	}

	public int getClipY() {
		if (scissorDepth == 0) {
			return 0;
		}
		return scissors.get(scissorDepth - 1).y();
	}

	public int getClipWidth() {
		if (scissorDepth == 0) {
			return getWidth();
		}
		return scissors.get(scissorDepth - 1).width;
	}

	public int getClipHeight() {
		if (scissorDepth == 0) {
			return getWidth();
		}
		return scissors.get(scissorDepth - 1).height;
	}

	public RectBox getClip() {
		if (scissorDepth == 0) {
			return LSystem.viewSize.getRect();
		}
		return scissors.get(scissorDepth - 1);
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
			setClipImpl(r.x, r.y, r, r.width, r.height);
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

	public GLEx clearRect(int x, int y, int width, int height) {
		return clearRect(x, y, width, height, null);
	}

	public GLEx clearRect(int x1, int y1, int w1, int h1, LColor rgba) {
		LColor clear = LColor.black;
		if (rgba != null) {
			clear = rgba;
		}
		if (scissorDepth < 1) {
			final GL20 gl = batch.gl;
			int x = MathUtils.floor(x1 * LSystem.getScaleWidth());
			int y = MathUtils.floor(y1 * LSystem.getScaleHeight());
			int w = MathUtils.floor(w1 * LSystem.getScaleWidth());
			int h = MathUtils.floor(h1 * LSystem.getScaleHeight());
			batch.flush();
			GLUtils.enablecissorTest(gl);
			gl.glScissor(x, target.flip() ? target.height() - y - h : y, w, h);
			GLUtils.setClearColor(gl, clear.r, clear.g, clear.b, clear.a);
			GLUtils.disablecissorTest(gl);
		} else {
			startClipped(x1, y1, w1, h1);
			GLUtils.setClearColor(batch.gl, clear.r, clear.g, clear.b, clear.a);
			endClipped();
		}
		return this;
	}

	protected float getScaleTransX() {
		return lastTrans.tx / scaleX;
	}

	protected float getScaleTransY() {
		return lastTrans.ty / scaleY;
	}

	public boolean isRendererLocked() {
		return rendererDrawLocked;
	}

	public GLEx rendererLock() {
		this.rendererDrawLocked = true;
		return this;
	}

	public GLEx freeRendererLock() {
		this.rendererDrawLocked = false;
		return this;
	}

	public GLRenderer beginBatchRenderer(GLType mode) {
		GLRenderer renderer = beginRenderer(mode);
		rendererLock();
		return renderer;
	}

	public GLRenderer endBatchRenderer() {
		freeRendererLock();
		return endRenderer();
	}

	/**
	 * 图形形状渲染开始
	 * 
	 * @param mode
	 * @return
	 */
	public GLRenderer beginRenderer(GLType mode) {
		if (!rendererDrawLocked) {
			end();
			GLUtils.disableTextures(batch.gl);
			if (glRenderer == null) {
				glRenderer = new GLRenderer(this);
			}
			saveTx();
			this.glRenderer.begin(lastTrans, mode);
			this.useBegin = true;
		}
		return glRenderer;
	}

	/**
	 * 图形形状渲染结束
	 * 
	 * @return
	 */
	public GLRenderer endRenderer() {
		if (!rendererDrawLocked) {
			if (!useBegin) {
				useBegin = false;
				return null;
			}
			try {
				glRenderer.end();
			} finally {
				restoreTx();
				useBegin = false;
			}
		}
		return glRenderer;
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
			} else if (x1 != x2) {
				x2++;
			}
			if (y1 > y2) {
				y1++;
			} else if (y1 != y2) {
				y2++;
			}
			if (use) {
				beginRenderer(GLType.Line);
			}
			int argb = syncBrushColorInt();
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
	 * @param color
	 * @return
	 */
	public GLEx draw(Shape shape, LColor color) {
		int tmp = getTint();
		setTint(color);
		draw(shape, 0f, 0f);
		setTint(tmp);
		return this;
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
			int argb = syncBrushColorInt();
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
		return drawPolyline(shape.getPoints(), x, y);
	}

	/**
	 * 绘制不连线的Shape
	 * 
	 * @param points
	 * @param x
	 * @param y
	 * @return
	 */
	public GLEx drawPolyline(float[] points, float x, float y) {
		if (points == null || points.length == 0) {
			return this;
		}
		int size = points.length;
		if (size == 2) {
			drawPoint(points[0], points[1]);
			return this;
		}
		if (size == 4) {
			drawLine(points[0], points[1], points[2], points[3]);
			return this;
		}
		if (this.lastBrush.alltextures) {
			int len = size / 2;
			final float[] xps = new float[len];
			final float[] yps = new float[len];
			for (int i = 0, j = 0; i < size; i += 2, j++) {
				xps[j] = points[i] + x;
				yps[j] = points[i + 1] + y;
			}
			drawPolylineImpl(xps, yps, len);
		} else {
			int argb = syncBrushColorInt();
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
	 * @param color
	 * @return
	 */
	public GLEx fill(Shape shape, LColor color) {
		int tmp = getTint();
		setTint(color);
		fill(shape, 0f, 0f);
		setTint(tmp);
		return this;
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
			int argb = syncBrushColorInt();
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
		float x2 = x - (r * MathUtils.cos(triangleValue));
		float y2 = y + (r * MathUtils.sin(triangleValue));
		float x3 = x + (r * MathUtils.cos(triangleValue));
		float y3 = y + (r * MathUtils.sin(triangleValue));
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
		float x2 = x - (r * MathUtils.cos(triangleValue));
		float y2 = y - (r * MathUtils.sin(triangleValue));
		float x3 = x + (r * MathUtils.cos(triangleValue));
		float y3 = y - (r * MathUtils.sin(triangleValue));
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
		int argb = syncBrushColorInt();
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
		int argb = syncBrushColorInt();
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
	 * 绘制菱形区域
	 *
	 * @param amount
	 * @param x
	 * @param y
	 * @param radius
	 * @param color
	 * @return
	 */
	public GLEx drawRhombus(int amount, float x, float y, float radius, LColor color) {
		return drawRhombus(x, y, amount, 1, radius, 0, color);
	}

	public GLEx drawDashCircle(XY pos, float radius, int sidesPerGap, LColor color) {
		return drawDashCircle(pos.getX(), pos.getY(), radius, sidesPerGap, color);
	}

	public GLEx drawDashCircle(float centerX, float centerY, float radius, int sidesPerGap, LColor color) {
		return drawDashCircle(centerX, centerY, radius, sidesPerGap, lastBrush.lineWidth, color, 10f, false);
	}

	public GLEx drawDashCircle(float centerX, float centerY, float radius, int sidesPerGap, float width, LColor color) {
		return drawDashCircle(centerX, centerY, radius, sidesPerGap, width, color, 10f, false);
	}

	public GLEx drawDashCircle(float centerX, float centerY, float radius, int sidesPerGap, float width, LColor color,
			float sideLength, boolean edges) {
		if (isClosed) {
			return this;
		}
		float anglePieceRad = MathUtils.TWO_PI;
		int sides = MathUtils.getCircleArcSideCount(radius, MathUtils.abs(anglePieceRad * MathUtils.RAD_TO_DEG),
				sideLength);
		float angleStep = MathUtils.toDegrees(anglePieceRad / sides);
		float size = sideLength * sidesPerGap;
		float remainingSize = size;
		boolean gap = false;
		tempLocation.set(centerX, centerY);
		for (int i = 0; i < sides; i++) {
			if (!gap) {
				Vector2f start = tempLocation.add(Vector2f.RIGHT().mul(radius).rotate(angleStep * i));
				Vector2f end = tempLocation.add(Vector2f.RIGHT().mul(radius).rotate(angleStep * (i + 1)));
				if (edges) {
					fillCircle(start, width * 0.5f, color);
					fillCircle(end, width * 0.5f, color);
				}
				drawLine(start, end, width, color);
			}
			remainingSize -= sideLength;
			if (remainingSize <= 0f) {
				gap = !gap;
				remainingSize = size;
			}
		}
		return this;
	}

	public GLEx drawDashCircle(float x, float y, float radius) {
		return drawDashCircle(x, y, radius, this.lastBrush.lineWidth);
	}

	public GLEx drawDashCircle(float x, float y, float radius, float width, LColor color) {
		int argb = this.lastBrush.baseColor;
		setColor(color);
		drawDashCircle(x, y, radius, width);
		setColor(argb);
		return this;
	}

	public GLEx drawDashCircle(float x, float y, float radius, float width) {
		return drawDashCircle(x, y, 2, radius, width);
	}

	public GLEx drawDashCircle(float x, float y, int side, float radius, float width) {
		final float newRadius = radius / side;
		final float newX = x + newRadius;
		final float newY = y + newRadius;
		float scaleFactor = 0.6f;
		int sides = 10 + MathUtils.floor(newRadius * scaleFactor);
		if (sides % side == 1) {
			sides++;
		}
		tempLocation.set(0f);

		for (int i = 0; i < sides; i++) {
			if (i % side == 0) {
				continue;
			}
			tempLocation.set(newRadius, 0).setAngle(360f / sides * i + 90);
			float x1 = tempLocation.x;
			float y1 = tempLocation.y;

			tempLocation.set(newRadius, 0).setAngle(360f / sides * (i + 1) + 90);

			drawLine(x1 + newX, y1 + newY, tempLocation.x + newX, tempLocation.y + newY, width);
		}
		return this;
	}

	/**
	 * 以虚线绘制菱形区域
	 * 
	 * @param amount
	 * @param x
	 * @param y
	 * @param radius
	 * @param divisions
	 * @param color
	 * @return
	 */
	public GLEx drawDashRhombus(int amount, float x, float y, float radius, int divisions, LColor color) {
		return drawDashRhombus(x, y, amount, 1, radius, 0, divisions, color);
	}

	/**
	 * 以虚线绘制菱形区域
	 * 
	 * @param amount
	 * @param x
	 * @param y
	 * @param radius
	 * @param color
	 * @return
	 */
	public GLEx drawDashRhombus(int amount, float x, float y, float radius, LColor color) {
		return drawDashRhombus(x, y, amount, 1, radius, 0, 5, color);
	}

	/**
	 * 绘制菱形区域
	 * 
	 * @param amount
	 * @param x
	 * @param y
	 * @param radius
	 * @return
	 */
	public GLEx drawRhombus(int amount, float x, float y, float radius) {
		return drawRhombus(amount, x, y, radius, syncBrushColor());
	}

	/**
	 * 以虚线绘制菱形区域
	 * 
	 * @param amount
	 * @param x
	 * @param y
	 * @param radius
	 * @param divisions
	 * @return
	 */
	public GLEx drawDashRhombus(int amount, float x, float y, float radius, int divisions) {
		return drawDashRhombus(amount, x, y, radius, divisions, syncBrushColor());
	}

	/**
	 * 绘制菱形区域
	 * 
	 * @param x
	 * @param y
	 * @param pointAmount
	 * @param pointStep
	 * @param radius
	 * @param beginAngle
	 * @param color
	 * @return
	 */
	public GLEx drawRhombus(float x, float y, int pointAmount, int pointStep, float radius, float beginAngle,
			LColor color) {
		return drawRhombus(x, y, pointAmount, pointStep, radius, beginAngle, false, 1, color);
	}

	/**
	 * 以虚线绘制菱形区域
	 * 
	 * @param x
	 * @param y
	 * @param pointAmount
	 * @param pointStep
	 * @param radius
	 * @param beginAngle
	 * @param divisions
	 * @param color
	 * @return
	 */
	public GLEx drawDashRhombus(float x, float y, int pointAmount, int pointStep, float radius, float beginAngle,
			int divisions, LColor color) {
		return drawRhombus(x, y, pointAmount, pointStep, radius, beginAngle, true, divisions, color);
	}

	/**
	 * 绘制菱形区域
	 * 
	 * @param x
	 * @param y
	 * @param pointAmount
	 * @param pointStep
	 * @param radius
	 * @param beginAngle
	 * @param dashLine
	 * @param divisions
	 * @param color
	 * @return
	 */
	public GLEx drawRhombus(float x, float y, int pointAmount, int pointStep, float radius, float beginAngle,
			boolean dashLine, int divisions, LColor color) {
		final int steps = pointStep * pointAmount;
		boolean update = false;
		if (steps != rhombusArray.size) {
			rhombusArray.clear();
			update = true;
		}
		final float newRadius = radius / 2;
		final float amount = 360f / pointAmount;
		final float step = newRadius / pointStep;
		int count = 0;
		for (int j = 0; j < pointStep; j++) {
			for (int i = 0; i < pointAmount; i++) {
				float len = amount * i;
				float newX = MathUtils.cos(MathUtils.toRadians(len - beginAngle)) * (j + 1) * step;
				float newY = MathUtils.sin(MathUtils.toRadians(len - beginAngle)) * (j + 1) * step;
				float rx = newX + x + newRadius;
				float ry = -newY + y + newRadius;
				if (update) {
					rhombusArray.put(count++, new PointF(rx, ry));
				} else {
					PointF result = rhombusArray.get(count++);
					if (result != null) {
						result.set(rx, ry);
					}
				}
			}
		}
		int size = rhombusArray.size();
		for (int i = 1; i <= size; i++) {
			PointF p1 = rhombusArray.get(i - 1);
			if (p1 != null) {
				if ((i) % (pointAmount) != 0) {
					PointF opend = rhombusArray.get(i);
					if (opend != null) {
						if (dashLine) {
							drawDashLine(p1.x, p1.y, opend.x, opend.y, divisions, color);
						} else {
							drawLine(p1.x, p1.y, opend.x, opend.y, color);
						}
					}
				} else {
					PointF closed = rhombusArray.get(i - pointAmount);
					if (closed != null) {
						if (dashLine) {
							drawDashLine(p1.x, p1.y, closed.x, closed.y, divisions, color);
						} else {
							drawLine(p1.x, p1.y, closed.x, closed.y, color);
						}
					}
				}
			}
		}
		return this;
	}

	/**
	 * 绘制椭圆
	 * 
	 * @param x1
	 * @param y1
	 * @param width
	 * @param height
	 */
	public GLEx drawOval(float x1, float y1, float width, float height) {
		return drawOval(x1, y1, width, height, LSystem.LAYER_TILE_SIZE);
	}

	/**
	 * 绘制椭圆
	 * 
	 * @param x1
	 * @param y1
	 * @param width
	 * @param height
	 * @param seg
	 * @return
	 */
	public GLEx drawOval(float x1, float y1, float width, float height, int seg) {
		final float lineWidth = this.lastBrush.lineWidth;
		if (this.lastBrush.alltextures || lineWidth != 1f) {
			oval(x1, y1, width, height, lineWidth);
			return this;
		} else {
			return this.drawArc(x1, y1, width, height, seg, 0, MathUtils.DEG_FULL);
		}
	}

	/**
	 * 绘制椭圆
	 * 
	 * @param x1
	 * @param y1
	 * @param width
	 * @param height
	 * @param c
	 * @return
	 */
	public GLEx drawOval(float x1, float y1, float width, float height, LColor c) {
		int tint = color();
		setTint(c);
		drawOval(x1, y1, width, height);
		setTint(tint);
		return this;
	}

	/**
	 * 填充椭圆
	 * 
	 * @param x1
	 * @param y1
	 * @param width
	 * @param height
	 */
	public GLEx fillOval(float x1, float y1, float width, float height) {
		if (this.lastBrush.alltextures) {
			fillOvalImpl(x1, y1, width, height);
			return this;
		} else {
			return this.fillArc(x1, y1, width, height, LSystem.LAYER_TILE_SIZE, 0, MathUtils.DEG_FULL);
		}
	}

	/**
	 * 填充椭圆
	 * 
	 * @param x1
	 * @param y1
	 * @param width
	 * @param height
	 * @param c
	 * @return
	 */
	public GLEx fillOval(float x1, float y1, float width, float height, LColor c) {
		int tint = color();
		setTint(c);
		if (this.lastBrush.alltextures) {
			fillOvalImpl(x1, y1, width, height);
		} else {
			this.fillArc(x1, y1, width, height, LSystem.LAYER_TILE_SIZE, 0, MathUtils.DEG_FULL);
		}
		setTint(tint);
		return this;
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
			int argb = syncBrushColorInt();
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
			int argb = syncBrushColorInt(color);
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
			int argb = syncBrushColorInt();
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
			_currentPolys.setPolygon(xPoints, yPoints, nPoints);
			fill(_currentPolys);
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
			_currentPolys.setPolygon(xPoints, yPoints, nPoints);
			draw(_currentPolys);
		}
		return this;
	}

	/**
	 * 直接以本地(图片)绘制多边形轮廓
	 * 
	 * @param xPoints
	 * @param yPoints
	 * @param nPoints
	 * @return
	 */
	public GLEx drawPolygonNative(float[] xPoints, float[] yPoints, int nPoints) {
		if (isClosed) {
			return this;
		}
		drawPolygonImpl(xPoints, yPoints, nPoints);
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

	public GLEx drawDashRect(float x, float y, float w, float h, int gapsPerSide, float width, LColor color) {
		return drawDashRect(x, y, w, h, gapsPerSide, width, color, false, false);
	}

	public GLEx drawDashRect(XYZW rect, int gapsPerSide, float width, LColor color) {
		return drawDashRect(rect.getX(), rect.getY(), rect.getZ(), rect.getW(), gapsPerSide, width, color, false,
				false);
	}

	public GLEx drawDashRect(float x, float y, float w, float h, int gapsPerSide, float width, LColor color,
			boolean round, boolean edges) {
		if (isClosed) {
			return this;
		}
		if (round) {
			PointF[] rect = RectBox.getRectCorners(x, y, w, h);
			float r = width * 0.5f;
			fillCircle(rect[0], r, color);
			fillCircle(rect[1], r, color);
			fillCircle(rect[2], r, color);
			fillCircle(rect[3], r, color);
		}

		PointF[] segments = RectBox.getRectSegments(x, y, w, h);

		for (int i = 0; i < segments.length; i += 2) {
			PointF start = segments[i];
			PointF end = segments[i + 1];
			drawDashLine(start, end, gapsPerSide, width, color, edges);
		}
		return this;
	}

	public GLEx drawDashRect(float x, float y, float w, float h, int gapsPerSide, float gapSizeF, float width,
			LColor color) {
		return drawDashRect(x, y, w, h, gapsPerSide, gapSizeF, width, color, false, false);
	}

	public GLEx drawDashRect(XYZW rect, int gapsPerSide, float gapSizeF, float width, LColor color) {
		return drawDashRect(rect.getX(), rect.getY(), rect.getZ(), rect.getW(), gapsPerSide, gapSizeF, width, color,
				false, false);
	}

	public GLEx drawDashRect(float x, float y, float w, float h, int gapsPerSide, float gapSizeF, float width,
			LColor color, boolean round, boolean edges) {
		if (isClosed) {
			return this;
		}
		if (round) {
			PointF[] rect = RectBox.getRectCorners(x, y, w, h);
			float r = width * 0.5f;
			fillCircle(rect[0], r, color);
			fillCircle(rect[1], r, color);
			fillCircle(rect[2], r, color);
			fillCircle(rect[3], r, color);
		}

		PointF[] segments = RectBox.getRectSegments(x, y, w, h);

		for (int i = 0; i < segments.length; i += 2) {
			PointF start = segments[i];
			PointF end = segments[i + 1];
			drawDashLine(start, end, gapsPerSide, gapSizeF, width, color, edges);
		}
		return this;
	}

	/**
	 * 绘制一个由虚线组成的矩形
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param color
	 * @param divisions
	 * @return
	 */
	public final GLEx drawDashRect(float x, float y, float width, float height, LColor color, int divisions) {
		if (divisions <= 1) {
			return drawRect(x, y, width, height, color);
		}
		int argb = this.lastBrush.baseColor;
		setColor(color);
		float tempX = x;
		float tempY = y;
		float tempWidth = tempX + width;
		float tempHeight = tempY + height;
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
		drawDashLine(tempX, tempY, tempWidth, tempY, divisions);
		drawDashLine(tempX, tempY + 1, tempX, tempHeight, divisions);
		drawDashLine(tempWidth, tempHeight, tempX + 1, tempHeight, divisions);
		drawDashLine(tempWidth, tempHeight - 1, tempWidth, tempY + 1, divisions);
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
		if (fill) {
			fillRectNative(x, y, width, height);
		} else {
			float tempX = x;
			float tempY = y;
			float tempWidth = tempX + width;
			float tempHeight = tempY + height;
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
			drawLine(tempX, tempY, tempWidth, tempY, this.lastBrush.lineWidth);
			drawLine(tempX, tempY + 1, tempX, tempHeight, this.lastBrush.lineWidth);
			drawLine(tempWidth, tempHeight, tempX + 1, tempHeight, this.lastBrush.lineWidth);
			drawLine(tempWidth, tempHeight - 1, tempWidth, tempY + 1, this.lastBrush.lineWidth);
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
		return drawArc(x1, y1, width, height, LSystem.LAYER_TILE_SIZE, start, end);
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
	 * @return
	 */
	public final GLEx drawArc(float x1, float y1, float width, float height, int segments, float start, float end) {
		return drawArc(x1, y1, width, height, segments, start, end, end < 0f);
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
	public final GLEx drawArc(float x1, float y1, float width, float height, int segments, float start, float end,
			boolean reverse) {
		if (isClosed) {
			return this;
		}
		final float rotation = (end - start);
		if (this.lastBrush.alltextures || (this.lastBrush.lineWidth != 1f && rotation == MathUtils.DEG_FULL)) {
			if (rotation != MathUtils.DEG_FULL) {
				int skip = getPixSkip();
				setPixSkip(MathUtils.floor(MathUtils.max(this.lastBrush.lineWidth, skip)));
				drawArcImpl(x1, y1, width, height, start, end);
				setPixSkip(skip);
			} else {
				oval(x1, y1, width, height, this.lastBrush.lineWidth);
			}
		} else {
			final float radiusW = width / 2f;
			final float radiusH = height / 2f;
			final float cx = x1 + radiusW;
			final float cy = y1 + radiusH;
			if (MathUtils.equal(radiusW, radiusH)) {
				beginRenderer(GLType.Line);
				final int argb = syncBrushColorInt();
				glRenderer.setColor(argb);
				if (rotation == MathUtils.DEG_FULL) {
					glRenderer.oval(cx, cy, MathUtils.min(radiusW, radiusH));
				} else {
					glRenderer.arc(cx, cy, MathUtils.min(radiusW, radiusH), start, end, segments, reverse);
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
		return fillArc(x1, y1, width, height, LSystem.LAYER_TILE_SIZE, start, end);
	}

	/**
	 * 填充指定大小的弧度
	 * 
	 * @param rect
	 * @param start
	 * @param segments
	 * @param end
	 * @return
	 */
	public final GLEx fillArc(RectBox rect, int segments, float start, float end) {
		return fillArc(rect.x, rect.y, rect.width, rect.height, segments, start, end);
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
	 * @return
	 */
	public final GLEx fillArc(float x1, float y1, float width, float height, int segments, float start, float end) {
		return fillArc(x1, y1, width, height, segments, start, end, end < 0f);
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
	 * @param reverse
	 * @return
	 */
	public final GLEx fillArc(float x1, float y1, float width, float height, float start, float end, boolean reverse) {
		return fillArc(x1, y1, width, height, LSystem.LAYER_TILE_SIZE, start, end, reverse);
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
	 * @param reverse
	 * @return
	 */
	public final GLEx fillArc(float x1, float y1, float width, float height, int segments, float start, float end,
			boolean reverse) {
		if (isClosed) {
			return this;
		}
		if (this.lastBrush.alltextures) {
			fillArcImpl(x1, y1, width, height, start, end);
		} else {
			final float radiusW = width / 2f;
			final float radiusH = height / 2f;
			final float cx = x1 + radiusW;
			final float cy = y1 + radiusH;
			if (MathUtils.equal(radiusW, radiusH)) {
				beginRenderer(GLType.Filled);
				final int argb = syncBrushColorInt();
				glRenderer.setColor(argb);
				if (end - start == MathUtils.DEG_FULL) {
					glRenderer.oval(cx, cy, MathUtils.min(radiusW, radiusH));
				} else {
					glRenderer.arc(cx, cy, MathUtils.min(radiusW, radiusH), start, end, segments, reverse);
				}
				endRenderer();
			} else {
				fill(new Ellipse(cx, cy, radiusW, radiusH, start, end, segments));
			}
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
		return drawRoundRect(x, y, width, height, radius, LSystem.LAYER_TILE_SIZE);
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
				throw new LSysException("radius > 0");
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
		return fillRoundRect(x, y, width, height, cornerRadius, LSystem.LAYER_TILE_SIZE);
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
				throw new LSysException("radius > 0");
			}
			if (radius == 0) {
				fillRect(x, y, width, height);
				return this;
			}
			if (MathUtils.isOdd(radius)) {
				radius += 1;
			}
			int mr = MathUtils.ifloor((MathUtils.min(width, height) / 2));
			if (radius > mr) {
				radius = mr;
			}
			float d = radius * 2;
			fillRect(x + radius, y, width - d, radius);
			fillRect(x, y + radius, radius, height - d);
			fillRect(x + width - radius, y + radius, radius, height - d);
			fillRect(x + radius, y + height - radius, width - d, radius);
			fillRect(x + radius, y + radius, width - d, height - d);
			fillArc(x, y + height - d, d, d, segs, 90, 90);
			fillArc(x, y, d, d, segs, 180, 90);
			fillArc(x + width - d, y, d, d, segs, 269, 90);
			fillArc(x + width - d, y + height - d, d, d, segs, 0, 90);
		}
		return this;
	}

	public GLEx fillCircle(float x, float y, float radius) {
		return fillOval(x, y, radius, radius);
	}

	public GLEx fillCircle(XY pos, float radius) {
		return fillOval(pos.getX(), pos.getY(), radius, radius);
	}

	public GLEx fillCircle(XY pos, float radius, LColor color) {
		return fillOval(pos.getX(), pos.getY(), radius, radius, color);
	}

	public GLEx fillCircle(float x, float y, float radius, LColor color) {
		int argb = this.lastBrush.baseColor;
		setColor(color);
		fillCircle(x, y, radius);
		setColor(argb);
		return this;
	}

	public GLEx drawCircle(float x, float y, float radius) {
		return drawOval(x, y, radius, radius);
	}

	public GLEx drawCircle(float x, float y, float radius, LColor color) {
		int argb = this.lastBrush.baseColor;
		setColor(color);
		drawCircle(x, y, radius);
		setColor(argb);
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
	 * PS:此处drawString，不减去ascent再进行显示，和减去该值的drawText渲染后上下位置不一样的
	 */
	/**
	 * 输出字符串
	 * 
	 * @param text
	 * @param position
	 */
	public GLEx drawString(String text, Vector2f position) {
		return drawString(text, position.x, position.y, syncBrushColor());
	}

	/**
	 * 输出字符串
	 * 
	 * @param text
	 * @param position
	 * @param color
	 */
	public GLEx drawString(String text, Vector2f position, LColor color) {
		return drawString(text, position.x, position.y, color);
	}

	/**
	 * 输出字符串
	 * 
	 * @param text
	 * @param x
	 * @param y
	 */
	public GLEx drawString(String text, float x, float y) {
		return drawString(text, x, y, syncBrushColor());
	}

	/**
	 * 输出字符串
	 * 
	 * @param text
	 * @param x
	 * @param y
	 * @param color
	 */
	public GLEx drawString(String text, float x, float y, LColor color) {
		return drawString(text, x, y, 0, color);
	}

	/**
	 * 输出字符串
	 * 
	 * @param text
	 * @param x
	 * @param y
	 * @param rotation
	 */
	public GLEx drawString(String text, float x, float y, float rotation) {
		return drawString(text, x, y, rotation, syncBrushColor());
	}

	/**
	 * 输出字符串
	 * 
	 * @param text
	 * @param x
	 * @param y
	 * @param rotation
	 * @param c
	 */
	public GLEx drawString(String text, float x, float y, float rotation, LColor c) {
		if (isClosed) {
			return this;
		}
		if (this.lastBrush.font != null) {
			this.lastBrush.font.drawString(this, text, x + offsetStringX, y + offsetStringY, rotation, c);
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
		if (this.lastBrush.font != null) {
			this.lastBrush.font.drawString(this, mes, x + offsetStringX, y + offsetStringY, scaleX, scaleY, ax, ay,
					rotation, c);
		}
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
		return drawChar(chars, x, y, rotation, syncBrushColor());
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
			throw new LSysException("Area out of texture");
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
			throw new LSysException("Bad transform");
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
			throw new LSysException("Bad Anchor");
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
			batch.addQuad(colorTex, syncBrushColorInt(), tx(), x, y, width, height);
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
				batch.addQuad(colorTex, syncBrushColorInt(), lastTrans, x, y, skip + this.lastBrush.lineWidth,
						skip + this.lastBrush.lineWidth);
			}
		}
	}

	@Override
	public BatchEx<GLEx> quad(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
		if (this.lastBrush.patternTex != null) {
			batch.quad(this.lastBrush.patternTex, this.lastBrush.baseColor, tx(), x1, y1, x2, y2, x3, y3, x4, y4);
		} else {
			batch.quad(colorTex, syncBrushColorInt(), tx(), x1, y1, x2, y2, x3, y3, x4, y4);
		}
		return this;
	}

	@Override
	public BatchEx<GLEx> quad(float x1, float y1, float c1, float x2, float y2, float c2, float x3, float y3, float c3,
			float x4, float y4, float c4) {
		if (this.lastBrush.patternTex != null) {
			batch.quad(this.lastBrush.patternTex, tx(), x1, y1, c1, x2, y2, c2, x3, y3, c3, x4, y4, c4);
		} else {
			batch.quad(colorTex, tx(), x1, y1, c1, x2, y2, c2, x3, y3, c3, x4, y4, c4);
		}
		return this;
	}

	/**
	 * 获得当前画布旋转角度
	 * 
	 * @return
	 */
	public float getAngle() {
		return lastTrans == null ? 0f : lastTrans.getAngle();
	}

	/**
	 * 获得当前画布偏移的X坐标
	 * 
	 * @return
	 */
	public float getTranslationX() {
		return lastTrans == null ? 0f : lastTrans.tx();
	}

	/**
	 * 获得当前画布偏移的Y坐标
	 * 
	 * @return
	 */
	public float getTranslationY() {
		return lastTrans == null ? 0f : lastTrans.ty();
	}

	/**
	 * width的缩放比例
	 * 
	 * @return
	 */
	public float getScaleX() {
		return lastTrans == null ? scaleX : lastTrans.scaleX();
	}

	/**
	 * height的缩放比率
	 * 
	 * @return
	 */
	public float getScaleY() {
		return lastTrans == null ? scaleY : lastTrans.scaleY();
	}

	@Override
	public void close() {
		this.isClosed = true;
		this.useBegin = false;
		if (rhombusArray != null) {
			rhombusArray.clear();
		}
		if (glRenderer != null) {
			glRenderer.close();
		}
	}

}
