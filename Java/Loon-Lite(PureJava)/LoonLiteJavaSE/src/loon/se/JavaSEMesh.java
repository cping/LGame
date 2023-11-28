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
package loon.se;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

import loon.LSysException;
import loon.LSystem;
import loon.LTexture;
import loon.canvas.Canvas;
import loon.canvas.Image;
import loon.geom.Affine2f;
import loon.opengl.BlendMethod;
import loon.opengl.Mesh;
import loon.opengl.MeshData;
import loon.utils.MathUtils;

public class JavaSEMesh implements Mesh {

	private AffineTransform newTransform = new AffineTransform();

	private AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);

	private MeshData mesh;

	/**
	 * 矩阵
	 */
	private Affine2f transform;

	/**
	 * 绘图环境
	 */
	private JavaSECanvas _canvas;

	private Composite _tempComposite;

	private int mode = 0;

	public JavaSEMesh(Canvas canvas) {
		if (canvas == null) {
			throw new LSysException("Canvas is null !");
		}
		_canvas = (JavaSECanvas) canvas;
	}

	/**
	 * 将mesh数据渲染到Canvas上面
	 * 
	 * 
	 */
	@Override
	public void paint() {
		if (mesh != null) {
			if (mode == 0) {
				renderWithIndexes(mesh);
			} else {
				renderNoIndexes(mesh);
			}
		}
	}

	/**
	 * 无顶点索引的模式
	 * 
	 * @param mesh
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
	 * @param mesh
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

		final Graphics2D context = _canvas.context;

		Composite oldComposite = context.getComposite();

		if (transform != null) {
			newTransform.setTransform(transform.m00, transform.m01, transform.m10, transform.m11, transform.tx,
					transform.ty);
			context.transform(newTransform);
		}

		// 创建三角形裁剪区域 context.beginPath();

		Path2D path = new GeneralPath();

		path.moveTo(x0, y0);
		path.lineTo(x1, y1);
		path.lineTo(x2, y2);
		path.closePath();

		context.setClip(path);

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

		newTransform.setTransform(deltaA * dDelta, deltaD * dDelta, deltaB * dDelta, deltaE * dDelta, deltaC * dDelta,
				deltaF * dDelta);
		context.transform(newTransform);

		context.drawImage(((JavaSEImage) source).buffer, MathUtils.ifloor(texture.widthRatio() * sourceWidth),
				MathUtils.ifloor(texture.heightRatio() * sourceHeight), MathUtils.ifloor(textureWidth),
				MathUtils.ifloor(textureHeight), MathUtils.ifloor(texture.widthRatio() * sourceWidth),
				MathUtils.ifloor(texture.heightRatio() * sourceHeight), MathUtils.ifloor(textureWidth),
				MathUtils.ifloor(textureHeight), null);

		context.setComposite(oldComposite);
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
		_tempComposite = _canvas.context.getComposite();
	}

	@Override
	public void restore() {
		_canvas.context.setComposite(_tempComposite);
	}

	@Override
	public void transform(float m00, float m01, float m10, float m11, float tx, float ty) {
		newTransform.setTransform(m00, m01, m10, m11, tx, ty);
		_canvas.context.transform(newTransform);
	}

	@Override
	public void transform(Affine2f aff) {
		newTransform.setTransform(aff.m00, aff.m01, aff.m10, aff.m11, aff.tx, aff.ty);
		_canvas.context.transform(newTransform);
	}

	@Override
	public void paint(int tint, Affine2f aff, float left, float top, float right, float bottom, float sl, float st,
			float sr, float sb) {
		paint(tint, aff.m00, aff.m01, aff.m10, aff.m11, aff.tx, aff.ty, left, top, right, bottom, sl, st, sr, sb);
	}

	@Override
	public void paint(int tint, float m00, float m01, float m10, float m11, float tx, float ty, float left, float top,
			float right, float bottom, float sl, float st, float sr, float sb) {

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

		LTexture texture = mesh.texture;
		Image img = texture.getSourceImage();

		if (img != null) {

			BufferedImage display = ((JavaSEImage) img).buffer;

			Graphics2D context = _canvas.context;
			final float canvasAlpha = (float) ((AlphaComposite) context.getComposite()).getAlpha();
			final float alpha = ((float) a / 255f) * canvasAlpha;

			AffineTransform oldTransform = context.getTransform();
			Composite oldComposite = context.getComposite();

			newTransform.setTransform(m00, m01, m10, m11, tx, ty);
			context.setTransform(newTransform);
			if (mesh.blend == BlendMethod.MODE_ADD) {
				if (alpha == 1f) {
					context.setComposite(JavaSEBlendComposite.getMultiply().setColor(r, g, b));
				} else if (alpha != 1f) {
					context.setComposite(JavaSEBlendComposite.getMultiply().derive(alpha).setColor(r, g, b));
				}
			} else {
				if (alpha != 1f) {
					context.setComposite(alphaComposite.derive(alpha));
				}
				if (!isWhiteColor) {
					JavaSECacheImageColor imageColor = ((JavaSEImage) img).getImageColor();
					// 如果默认缓存数许可
					if (imageColor.count() < LSystem.DEFAULT_MAX_CACHE_SIZE
							&& (img.getWidth() <= 512 && img.getHeight() <= 512)) {
						// 如果图像颜色需要混色,产生一个指定色彩的缓存图
						display = imageColor.get(r, g, b);
					} else {
						// 无硬件加速,无缓存渲染混色太慢(AWT环境下是像素渲染),替换自定义Composite算法上合理,但是没有操作性
						// 所以缓存优先.
						if (alpha == 1f) {
							context.setComposite(JavaSEBlendComposite.getMultiply().setColor(r, g, b));
						} else if (alpha != 1f) {
							context.setComposite(JavaSEBlendComposite.getMultiply().derive(alpha).setColor(r, g, b));
						}
					}
				}
			}
			if (!texture.isChild() && sl == 0f && st == 0f && sr == 1f && sb == 1f) {
				context.drawImage(display, MathUtils.ifloor(left), MathUtils.ifloor(top),
						MathUtils.ifloor(right - left), MathUtils.ifloor(bottom - top), null);
			} else {
				float textureWidth = texture.getDisplayWidth();
				float textureHeight = texture.getDisplayHeight();
				float dstX = textureWidth * (sl);
				float dstY = textureHeight * (st);
				float dstWidth = textureWidth * (sr);
				float dstHeight = textureHeight * (sb);
				if (dstWidth > textureWidth) {
					dstWidth = textureWidth;
				}
				if (dstHeight > textureHeight) {
					dstHeight = textureHeight;
				}
				context.drawImage(display, MathUtils.ifloor(left), MathUtils.ifloor(top), MathUtils.iceil(right),
						MathUtils.iceil(bottom), MathUtils.ifloor(dstX), MathUtils.ifloor(dstY),
						MathUtils.iceil(dstWidth), MathUtils.iceil(dstHeight), null);
			}
			context.setTransform(oldTransform);
			context.setComposite(oldComposite);
		}
	}
}
