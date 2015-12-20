package org.loon.framework.android.game.action.sprite;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import org.loon.framework.android.game.core.LObject;
import org.loon.framework.android.game.core.geom.AffineTransform;
import org.loon.framework.android.game.core.geom.Area;
import org.loon.framework.android.game.core.geom.PathIterator;
import org.loon.framework.android.game.core.geom.Point;
import org.loon.framework.android.game.core.geom.Polygon;
import org.loon.framework.android.game.core.geom.RectBox;
import org.loon.framework.android.game.core.graphics.LColor;
import org.loon.framework.android.game.core.graphics.LImage;
import org.loon.framework.android.game.core.graphics.device.LGraphics;
import org.loon.framework.android.game.utils.CollectionUtils;
import org.loon.framework.android.game.utils.GraphicsUtils;

import android.graphics.Bitmap;

/**
 * Copyright 2008 - 2010
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * 
 * of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * 
 * under the License.
 * 
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1.2
 */
public class SpriteImage extends LObject implements ISprite {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1982900453464314946L;

	public boolean visible = true;

	private boolean isOpaque = true;

	private Polygon newPy;

	private Mask mask;

	// 此为0.2.9版新增类，用以处理像素级旋转
	private SpriteRotate sRotate;

	// 将像素转化为Polygon时的取点间隔值(越小精确度越高，代价为取点速度越慢)
	private int makePolygonInterval = 3;

	private CollisionMask collisionMask;

	private Polygon[] polygons;

	protected int[] pixels;

	protected int transform;

	protected int width, height;

	private Bitmap bitmap;

	private LImage img;

	public SpriteImage(String fileName) {
		this(fileName, 0, 0);
	}

	public SpriteImage(Bitmap bit) {
		this(bit, 0, 0);
	}

	public SpriteImage(LImage img) {
		this(img.getBitmap(), 0, 0);
	}

	public SpriteImage(Bitmap bit, int x, int y) {
		this(bit, x, y, bit.getWidth(), bit.getHeight());
	}

	public SpriteImage(LImage img, int x, int y) {
		this(img.getBitmap(), x, y);
	}

	public SpriteImage(LImage img, int x, int y, int width, int height) {
		this(img.getBitmap(), x, y, width, height);
	}

	public SpriteImage(Bitmap bit, int x, int y, int width, int height) {
		this.setLocation(x, y);
		this.width = width;
		this.height = height;
		this.bind(bit);
	}

	public SpriteImage(SpriteImage image) {
		this(image, 0, 0);
	}

	public SpriteImage(String fileName, int x, int y) {
		this(GraphicsUtils.loadBitmap(fileName, true), x, y);
	}

	public SpriteImage(SpriteImage image, int x, int y) {
		this.setLocation(x, y);
		this.width = image.width;
		this.height = image.height;
		this.bind(image.getBitmap());
	}

	public SpriteImage(int x, int y, int width, int height) {
		this.setLocation(x, y);
		this.width = width;
		this.height = height;
		this.bind(null, LColor.red);
	}

	private void bind(Bitmap img) {
		bind(img, null);
	}

	private void bind(Bitmap bit, LColor color) {
		int size = width * height;
		pixels = new int[width * height];
		transform = Sprite.TRANS_NONE;
		bit.getPixels(pixels, 0, width, 0, 0, width, height);
		int pixel;
		for (int i = 0; i < size; i++) {
			pixels[i] = LColor.premultiply(pixel = pixels[i]);
			if (isOpaque && (pixel >>> 24) < 255) {
				isOpaque = false;
			}
		}
		bitmap = bit;
	}

	/**
	 * 变更掩码中数据为指定角度
	 * 
	 * @param transform
	 */
	public Mask updateMask(int t) {
		this.transform = t;
		if (transform == Sprite.TRANS_NONE) {
			return createMask(pixels, width, height);
		}
		int[] trans = new int[pixels.length];
		if (transform != Sprite.TRANS_NONE) {
			sRotate = makeRotate(transform);
			trans = sRotate.makeSpritePixels();
		}
		return createMask(trans, sRotate.getWidth(), sRotate.getHeight());
	}

	private SpriteRotate makeRotate(int t) {
		int angle;
		switch (t) {
		case LGraphics.TRANS_ROT90:
			angle = 90;
			break;
		case LGraphics.TRANS_ROT180:
			angle = 180;
			break;
		case LGraphics.TRANS_ROT270:
			angle = 270;
			break;
		case LGraphics.TRANS_MIRROR:
			angle = -360;
			break;
		case LGraphics.TRANS_MIRROR_ROT90:
			angle = -90;
			break;
		case LGraphics.TRANS_MIRROR_ROT180:
			angle = -180;
			break;
		case LGraphics.TRANS_MIRROR_ROT270:
			angle = -270;
			break;
		default:
			throw new RuntimeException("Illegal transformation: " + transform);
		}
		return rotate(angle);
	}

	/**
	 * 生成像素掩码
	 * 
	 * @param pixels
	 * @param w
	 * @param h
	 * @return
	 */
	private Mask createMask(int[] pixels, int w, int h) {
		int width = w;
		int height = h;
		Mask data = new Mask(width, height);
		boolean[][] mask = new boolean[height][width];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				mask[y][x] = (pixels[x + w * y] & LColor.transparent) == LColor.transparent;
			}
		}
		data.setData(mask);
		return data;
	}

	/**
	 * 判定指定像素点是否透明
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isTransparent(int x, int y) {
		if (x < 0 || y < 0 || x >= width || y >= height) {
			return true;
		} else if (isOpaque) {
			return false;
		} else {
			return (pixels[x + width * y] & LColor.transparent) == LColor.transparent;
		}
	}

	/**
	 * 绘制图像到当前画布
	 */
	public void createUI(LGraphics g) {
		if (visible) {
			g.drawBitmap(bitmap, x(), y());
		}
	}

	/**
	 * 旋转当前精灵像素为指定角度并返回
	 * 
	 * @param angle
	 * @return
	 */
	public int[] rotatePixels(int angle) {
		return rotate(angle).makePixels();
	}

	/**
	 * 旋转当前精灵像素为指定角度并返回精灵像素旋转用类
	 * 
	 * @param angle
	 * @return
	 */
	public SpriteRotate rotate(int angle) {
		if (sRotate == null) {
			sRotate = new SpriteRotate(this, width, height, angle);
		} else {
			sRotate.setAngle(angle);
		}
		return sRotate;
	}

	/**
	 * 以当前图像为基础，创建一个指定旋转角度的Polygon
	 * 
	 * @param angle
	 * @return
	 */
	public Polygon rotatePolygon(int x, int y, int angle) {
		SpriteRotate sr = rotate(angle);
		int[] pixels = sr.makePixels();
		return makePolygon(pixels, x, y, 0, 0, sr.getWidth(), sr.getHeight());
	}

	/**
	 * 返回一个对应指定旋转角度，指定坐标的Polygon
	 * 
	 * @param x
	 * @param y
	 * @param t
	 * @return
	 */
	protected Polygon getPolygon(int x, int y, int t) {
		if (polygons == null) {
			polygons = new Polygon[Sprite.TRANS_MIRROR_ROT90 + 1];
		}
		Polygon py = polygons[t];
		if (py == null) {
			if (t != Sprite.TRANS_NONE) {
				sRotate = makeRotate(t);
				int[] trans = sRotate.makeSpritePixels();
				py = makePolygon(trans, 0, 0, 0, 0, sRotate.getWidth(), sRotate
						.getHeight());
			} else {
				py = makePolygon(0, 0);
			}
			polygons[t] = py;
		}
		if (newPy == null) {
			newPy = new Polygon(py.xpoints, py.ypoints, py.npoints);
		} else {
			int npoints = py.npoints;
			newPy.npoints = npoints;
			newPy.xpoints = CollectionUtils.copyOf(py.xpoints, npoints);
			newPy.ypoints = CollectionUtils.copyOf(py.ypoints, npoints);
		}
		newPy.translate(x, y);
		return newPy;
	}

	public Polygon makePolygon(int x, int y) {
		return makePolygon(pixels, x, y, 0, 0, width, height);
	}

	public Polygon makePolygon(int startX, int startY, int limitX, int limitY) {
		return makePolygon(pixels, 0, 0, startX, startY, limitX, limitY);
	}

	public Polygon makePolygon(int[] pixels, int offsetX, int offsetY,
			int startX, int startY, int limitX, int limitY) {
		Polygon split = null;
		Polygon result = null;
		ArrayList<Point[]> points = new ArrayList<Point[]>();
		Point[] tmpPoint;
		int x1, y1, x2, y2;
		boolean secondPoint;
		for (int y = startY; y < limitY - makePolygonInterval; y += makePolygonInterval) {
			secondPoint = false;
			x1 = y1 = -1;
			x2 = y2 = -1;
			for (int x = startX; x < limitX; x++) {
				if (!secondPoint) {
					if ((pixels[x + limitX * y] & LColor.transparent) == LColor.transparent) {
						x1 = x;
						y1 = y;
						secondPoint = true;
					}
				} else {
					if ((pixels[x + limitX * y] & LColor.transparent) == LColor.transparent) {
						x2 = x;
						y2 = y;
					}
				}
			}
			if (secondPoint && (x2 > -1) && (y2 > -1)) {
				tmpPoint = new Point[2];
				tmpPoint[0] = new Point(offsetX + x1, offsetY + y1);
				tmpPoint[1] = new Point(offsetX + x2, offsetY + y2);
				points.add(tmpPoint);
			}
		}
		split = makePolygon(points);
		if (split != null) {
			points = new ArrayList<Point[]>();
			for (int x = startX; x < limitX - makePolygonInterval; x += makePolygonInterval) {
				secondPoint = false;
				x1 = y1 = -1;
				x2 = y2 = -1;
				for (int y = startY; y < limitY; y++) {
					if (!secondPoint) {
						if ((pixels[x + limitX * y] & LColor.transparent) == LColor.transparent) {
							x1 = x;
							y1 = y;
							secondPoint = true;
						}
					} else {
						if ((pixels[x + limitX * y] & LColor.transparent) == LColor.transparent) {
							x2 = x;
							y2 = y;
						}
					}
				}
				if (secondPoint && (x2 > -1) && (y2 > -1)) {
					tmpPoint = new Point[2];
					tmpPoint[0] = new Point(offsetX + x1, offsetY + y1);
					tmpPoint[1] = new Point(offsetX + x2, offsetY + y2);
					points.add(tmpPoint);
				}
			}
			result = makePolygon(points);

		}
		return filterPolygon(result);
	}

	/**
	 * 将指定的Point集合注入Polygon当中
	 * 
	 * @param points
	 * @return
	 */
	private static Polygon makePolygon(ArrayList<Point[]> points) {
		Polygon polygon = null;
		if (!points.isEmpty()) {
			int size = points.size();
			polygon = new Polygon();
			for (int i = 0; i < size; i++) {
				Point p = ((Point[]) points.get(i))[0];
				polygon.addPoint(p.x, p.y);
			}
			for (int i = size - 1; i >= 0; i--) {
				Point p = ((Point[]) points.get(i))[1];
				polygon.addPoint(p.x, p.y);
			}
		}
		return polygon;
	}

	/**
	 * 过滤Polygon，减少交叉图形
	 * 
	 * @param polygon
	 * @return
	 */
	private static Polygon filterPolygon(Polygon polygon) {
		Area area = new Area(polygon);
		Polygon newPoly = new Polygon();
		PathIterator it = area.getPathIterator(AffineTransform
				.getTranslateInstance(0, 0), 0);
		float[] coords = new float[6];
		LinkedHashSet<String> set = new LinkedHashSet<String>();
		while (!it.isDone()) {
			it.currentSegment(coords);
			Point v = new Point((int) coords[0], (int) coords[1]);
			if (!set.contains(v.toString())) {
				newPoly.addPoint(v.x, v.y);
				set.add(v.toString());
			}
			it.next();
		}
		return newPoly;
	}

	public LImage getImage() {
		if (img == null) {
			img = new LImage(bitmap);
		}
		return img;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void update(long timer) {

	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public void move(int x, int y) {
		this.move(x, y);
	}

	public void setLocation(int x, int y) {
		this.setX(x);
		this.setY(y);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int[] getData() {
		return pixels;
	}

	public SpriteImage copy() {
		return new SpriteImage(this);
	}

	public CollisionMask getMask(int trans, int x, int y) {
		if (mask == null || transform != trans) {
			mask = updateMask(trans);
		}
		if (collisionMask == null) {
			collisionMask = new CollisionMask(mask);
		} else {
			collisionMask.set(mask, x, y, mask.getWidth(), mask.getHeight());
		}
		return collisionMask;
	}

	public RectBox getCollisionBox() {
		return getRect(x(), y(), width, height);
	}

	public float getAlpha() {
		return 0;
	}

	public boolean isOpaque() {
		return isOpaque;
	}

	public int getMakePolygonInterval() {
		return makePolygonInterval;
	}

	public void setMakePolygonInterval(int makePolygonInterval) {
		this.makePolygonInterval = makePolygonInterval;
	}

	public void dispose() {
		if (mask != null) {
			mask = null;
		}
		if (sRotate != null) {
			sRotate = null;
		}
		if (bitmap != null) {
			bitmap.recycle();
			bitmap = null;
		}
		if (img != null) {
			img.dispose();
			img = null;
		}
	}

}
