package loon.an;

import android.graphics.*;
import loon.LSysException;
import loon.LTexture;
import loon.canvas.Canvas;
import loon.canvas.Image;
import loon.geom.Affine2f;
import loon.opengl.BlendMethod;
import loon.opengl.Mesh;
import loon.opengl.MeshData;
import loon.utils.MathUtils;

public class JavaANMesh implements Mesh {

	private final float[] _transform = new float[9];

	private final Matrix matrix = new Matrix();

	private MeshData mesh;

	/**
	 * 矩阵
	 */
	private Affine2f _currentTransform;

	private final Paint _currentPaint = new Paint();
	/**
	 * 绘图环境
	 */
	private final JavaANCanvas _canvas;
	private final int _mode;
	private final Rect srcR = new Rect();
	private final RectF dstR = new RectF();

	public JavaANMesh(Canvas canvas) {
		this(0, canvas);
	}

	public JavaANMesh(int mode, Canvas canvas) {
		if (canvas == null) {
			throw new LSysException("Canvas is null !");
		}
		this._mode = mode;
		this._canvas = (JavaANCanvas) canvas;
	}

	/**
	 * 将mesh数据渲染到Canvas上面
	 */
	@Override
	public void paint() {
		if (mesh != null) {
			if (_mode == 0) {
				renderWithIndexes(mesh);
			} else {
				renderNoIndexes(mesh);
			}
		}
	}

	/**
	 * 无顶点索引的模式
	 *
	 */
	public void renderNoIndexes(MeshData mesh) {
		int i, len = mesh.amount == -1 ? mesh.vertices.length / 2 : mesh.amount;
		int index;
		for (i = 0; i < len - 2; i++) {
			index = i * 2;
			this.renderDrawTriangle(mesh, index, (index + 2), (index + 4));
		}
	}

	/**
	 * 使用顶点索引模式绘制
	 *
	 */
	public void renderWithIndexes(MeshData mesh) {
		int[] indexes = mesh.indexes;
		int i, len = mesh.amount == -1 ? indexes.length : mesh.amount;
		for (i = 0; i < len; i += 3) {
			int index0 = indexes[i] * 2;
			int index1 = indexes[i + 1] * 2;
			int index2 = indexes[i + 2] * 2;
			this.renderDrawTriangle(mesh, index0, index1, index2);
		}
	}

	public Mesh setAffine(Affine2f aff) {
		_currentTransform = aff;
		return this;
	}

	private float[] setArrayTransform(float m11, float m12, float m21, float m22, float dx, float dy) {
		_transform[0] = m11;
		_transform[1] = m21;
		_transform[2] = dx;
		_transform[3] = m12;
		_transform[4] = m22;
		_transform[5] = dy;
		_transform[6] = 0f;
		_transform[7] = 0f;
		_transform[8] = 1f;
		return _transform;
	}

	@Override
	public void renderDrawTriangle(MeshData mesh, int index0, int index1, int index2) {

		float[] uvs = mesh.uvs;
		float[] vertices = mesh.vertices;
		LTexture texture = mesh.texture;

		Image source = texture.getImage();

		float textureWidth = texture.pixelWidth();
		float textureHeight = texture.pixelHeight();
		float sourceWidth = source.getWidth();
		float sourceHeight = source.getHeight();

		// uv数据
		float u0 = 1f;
		float u1 = 1f;
		float u2 = 1f;
		float v0 = 1f;
		float v1 = 1f;
		float v2 = 1f;

		if (mesh.useUvTransform) {
			Affine2f ut = mesh.uvTransform;

			u0 = ((uvs[index0] * ut.m00) + (uvs[index0 + 1] * ut.m10) + ut.tx) * sourceWidth;
			u1 = ((uvs[index1] * ut.m00) + (uvs[index1 + 1] * ut.m10) + ut.tx) * sourceWidth;
			u2 = ((uvs[index2] * ut.m00) + (uvs[index2 + 1] * ut.m10) + ut.tx) * sourceWidth;
			v0 = ((uvs[index0] * ut.m01) + (uvs[index0 + 1] * ut.m11) + ut.ty) * sourceHeight;
			v1 = ((uvs[index1] * ut.m01) + (uvs[index1 + 1] * ut.m11) + ut.ty) * sourceHeight;
			v2 = ((uvs[index2] * ut.m01) + (uvs[index2 + 1] * ut.m11) + ut.ty) * sourceHeight;
		} else {
			u0 = uvs[index0] * sourceWidth;
			u1 = uvs[index1] * sourceWidth;
			u2 = uvs[index2] * sourceWidth;
			v0 = uvs[index0 + 1] * sourceHeight;
			v1 = uvs[index1 + 1] * sourceHeight;
			v2 = uvs[index2 + 1] * sourceHeight;
		}

		// 绘制顶点数据
		float x0 = vertices[index0];
		float x1 = vertices[index1];
		float x2 = vertices[index2];
		float y0 = vertices[index0 + 1];
		float y1 = vertices[index1 + 1];
		float y2 = vertices[index2 + 1];

		if (mesh.canvasPadding > 0) {// 扩展区域，解决黑边问题
			float paddingX = mesh.canvasPadding;
			float paddingY = mesh.canvasPadding;
			float centerX = (x0 + x1 + x2) / 3;
			float centerY = (y0 + y1 + y2) / 3;

			float normX = x0 - centerX;
			float normY = y0 - centerY;

			float dist = MathUtils.sqrt((normX * normX) + (normY * normY));

			x0 = centerX + ((normX / dist) * (dist + paddingX));
			y0 = centerY + ((normY / dist) * (dist + paddingY));

			normX = x1 - centerX;
			normY = y1 - centerY;

			dist = MathUtils.sqrt((normX * normX) + (normY * normY));
			x1 = centerX + ((normX / dist) * (dist + paddingX));
			y1 = centerY + ((normY / dist) * (dist + paddingY));

			normX = x2 - centerX;
			normY = y2 - centerY;

			dist = MathUtils.sqrt((normX * normX) + (normY * normY));
			x2 = centerX + ((normX / dist) * (dist + paddingX));
			y2 = centerY + ((normY / dist) * (dist + paddingY));
		}

		final android.graphics.Canvas context = _canvas.context;

		context.save();
		if (_currentTransform != null) {
			matrix.setValues(setArrayTransform(_currentTransform.m00, _currentTransform.m01, _currentTransform.m10,
					_currentTransform.m11, _currentTransform.tx, _currentTransform.ty));
			matrix.setValues(_transform);
			context.concat(matrix);
		}

		// 创建三角形裁剪区域
		Path path = new Path();
		path.moveTo(x0, y0);
		path.lineTo(x1, y1);
		path.lineTo(x2, y2);
		path.close();

		JavaANCanvasState.setPaintState(_currentPaint);
		_currentPaint.setStyle(Paint.Style.FILL);

		context.drawPath(path, _currentPaint);

		// 计算矩阵，将图片变形到合适的位置
		float delta = (u0 * v1) + (v0 * u2) + (u1 * v2) - (v1 * u2) - (v0 * u1) - (u0 * v2);
		float dDelta = 1 / delta;
		float deltaA = (x0 * v1) + (v0 * x2) + (x1 * v2) - (v1 * x2) - (v0 * x1) - (x0 * v2);
		float deltaB = (u0 * x1) + (x0 * u2) + (u1 * x2) - (x1 * u2) - (x0 * u1) - (u0 * x2);
		float deltaC = (u0 * v1 * x2) + (v0 * x1 * u2) + (x0 * u1 * v2) - (x0 * v1 * u2) - (v0 * u1 * x2)
				- (u0 * x1 * v2);
		float deltaD = (y0 * v1) + (v0 * y2) + (y1 * v2) - (v1 * y2) - (v0 * y1) - (y0 * v2);
		float deltaE = (u0 * y1) + (y0 * u2) + (u1 * y2) - (y1 * u2) - (y0 * u1) - (u0 * y2);
		float deltaF = (u0 * v1 * y2) + (v0 * y1 * u2) + (y0 * u1 * v2) - (y0 * v1 * u2) - (v0 * u1 * y2)
				- (u0 * y1 * v2);

		matrix.setValues(setArrayTransform(deltaA * dDelta, deltaD * dDelta, deltaB * dDelta, deltaE * dDelta,
				deltaC * dDelta, deltaF * dDelta));
		matrix.setValues(_transform);

		context.concat(matrix);

		draw(context, ((JavaANImage) source).buffer, texture.widthRatio() * sourceWidth,
				texture.heightRatio() * sourceHeight, textureWidth, textureHeight, texture.widthRatio() * sourceWidth,
				texture.heightRatio() * sourceHeight, textureWidth, textureHeight, _currentPaint);

		context.restore();

	}

	public void draw(android.graphics.Canvas context, Bitmap bitmap, float x, float y, float w, float h, Paint paint) {
		draw(context, bitmap, x, y, w, h, 0, 0, bitmap.getWidth(), bitmap.getHeight(), paint);
	}

	void draw(android.graphics.Canvas context, Bitmap bitmap, float x, float y, float w, float h, float x1, float y1,
			float w1, float h1, Paint paint) {
		srcR.set(MathUtils.floor(x1), MathUtils.floor(y1), MathUtils.floor(x1 + w1), MathUtils.floor(y1 + h1));
		dstR.set(x, y, x + w, y + h);
		context.drawBitmap(bitmap, srcR, dstR, paint);
	}

	@Override
	public MeshData getMesh() {
		return mesh;
	}

	@Override
	public void setMesh(MeshData mesh) {
		this.mesh = mesh;
	}

	@Override
	public void setIndices(int[] inds) {
		mesh.indexes = inds;
	}

	@Override
	public void setVertices(float[] vers) {
		mesh.vertices = vers;
	}

	protected static double colorMap(double value, double start, double stop, double targetStart, double targetStop) {
		return targetStart + (targetStop - targetStart) * ((value - start) / (stop - start));
	}

	public void save() {
		_canvas.context.save();
	}

	@Override
	public void restore() {
		_canvas.context.restore();
	}

	@Override
	public void transform(float m00, float m01, float m10, float m11, float tx, float ty) {
		matrix.setValues(setArrayTransform(m00, m01, m10, m11, tx, ty));
		_canvas.context.concat(matrix);
	}

	@Override
	public void transform(Affine2f aff) {
		matrix.setValues(setArrayTransform(aff.m00, aff.m01, aff.m10, aff.m11, aff.tx, aff.ty));
		_canvas.context.concat(matrix);
	}

	@Override
	public void paint(int tint, Affine2f aff, float left, float top, float right, float bottom, float sl, float st,
			float sr, float sb) {
		paint(tint, aff.m00, aff.m01, aff.m10, aff.m11, aff.tx, aff.ty, left, top, right, bottom, sl, st, sr, sb);
	}

	@Override
	public void paint(int tint, float m00, float m01, float m10, float m11, float tx, float ty, float left, float top,
			float right, float bottom, float sl, float st, float sr, float sb) {
		LTexture texture = mesh.texture;
		Image img = texture.getSourceImage();

		if (img != null) {

			int r = (tint & 0x00FF0000) >> 16;
			int g = (tint & 0x0000FF00) >> 8;
			int b = (tint & 0x000000FF);
			int a = (tint & 0xFF000000) >> 24;

			if (a < 0) {
				a += 256;
			}
			if (a == 0) {
				a = 255;
			}

			final boolean isWhiteColor = (tint == -1 || (r == 255 && g == 255 && b == 255));
			final android.graphics.Canvas context = _canvas.context;

			JavaANCanvasState.setPaintState(_currentPaint);

			_currentPaint.setColor(tint);
			if (!isWhiteColor) {
				_currentPaint.setColorFilter(new PorterDuffColorFilter(tint, PorterDuff.Mode.SRC_ATOP));
			} else {
				_currentPaint.setColorFilter(null);
			}
			_currentPaint.setAlpha(a);
			if (mesh.blend == BlendMethod.MODE_ADD) {
				_currentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.ADD));
			} else if (mesh.blend == BlendMethod.MODE_NORMAL) {
				_currentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
			}

			matrix.setValues(setArrayTransform(m00, m01, m10, m11, tx, ty));
			context.setMatrix(matrix);

			Bitmap buffer = ((JavaANImage) img).buffer;
			if (!texture.isChild() && sl == 0f && st == 0f && sr == 1f && sb == 1f) {
				draw(context, buffer, left, top, (right - left), (bottom - top), _currentPaint);
			} else {
				final float textureWidth = texture.getDisplayWidth();
				final float textureHeight = texture.getDisplayHeight();
				final float dstX = textureWidth * (sl);
				final float dstY = textureHeight * (st);
				float dstWidth = textureWidth * (sr);
				float dstHeight = textureHeight * (sb);
				if (dstWidth > textureWidth) {
					dstWidth = textureWidth;
				}
				if (dstHeight > textureHeight) {
					dstHeight = textureHeight;
				}
				draw(context, buffer, left, top, (right - left), (bottom - top), dstX, dstY, dstWidth - dstX,
						dstHeight - dstY, _currentPaint);
			}

			_currentPaint.setXfermode(null);
		}
	}

}