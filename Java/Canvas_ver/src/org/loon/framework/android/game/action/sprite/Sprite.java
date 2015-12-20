package org.loon.framework.android.game.action.sprite;

import org.loon.framework.android.game.core.LObject;
import org.loon.framework.android.game.core.geom.Point2D;
import org.loon.framework.android.game.core.geom.Polygon;
import org.loon.framework.android.game.core.geom.RectBox;
import org.loon.framework.android.game.core.geom.Vector2D;
import org.loon.framework.android.game.core.graphics.LImage;
import org.loon.framework.android.game.core.graphics.device.LGraphics;
import org.loon.framework.android.game.core.graphics.device.LTrans;
import org.loon.framework.android.game.core.graphics.filter.ImageFilterFactory;
import org.loon.framework.android.game.core.graphics.filter.ImageFilterType;
import org.loon.framework.android.game.utils.CollisionUtils;
import org.loon.framework.android.game.utils.GraphicsUtils;

import android.graphics.Bitmap;

/**
 * Copyright 2008 - 2009
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
public class Sprite extends LObject implements ISprite, Collidable, LTrans {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1982110847888726016L;

	// 默认每帧刷新时间
	final static private long defaultTimer = 150;

	// 图像过滤类型
	private int filterType;

	// 图像过滤器
	private ImageFilterFactory factory;

	// 是否可见
	private boolean visible;

	// 精灵名称
	private String spriteName;

	// 精灵图片
	private SpriteImage image;

	// 动画
	private Animation animation = new Animation();

	private Object tag;

	private int transform;

	/**
	 * 默认构造函数
	 * 
	 */
	public Sprite() {
		this(0, 0);
	}

	/**
	 * 以下参数分别为 坐标x,坐标y
	 * 
	 * @param x
	 * @param y
	 */
	public Sprite(double x, double y) {
		this("Sprite" + System.currentTimeMillis(), x, y);
	}

	/**
	 * 以下参数分别为 精灵名,坐标x,坐标y
	 * 
	 * @param spriteName
	 * @param x
	 * @param y
	 */
	private Sprite(String spriteName, double x, double y) {
		this.setLocation(x, y);
		this.spriteName = spriteName;
		this.visible = true;
		this.filterType = ImageFilterFactory.NoneFilter;
		this.factory = ImageFilterFactory.getInstance();
	}

	/**
	 * 以下参数分别为 取材文件,每行取材宽度,每列取材长度
	 * 
	 * @param fileName
	 * @param row
	 * @param col
	 */
	public Sprite(String fileName, int row, int col) {
		this(fileName, -1, 0, 0, row, col, defaultTimer);
	}

	/**
	 * 以下参数分别为 取材文件,每行取材宽度,每列取材长度,平均每桢显示时间
	 * 
	 * @param fileName
	 * @param row
	 * @param col
	 * @param timer
	 */
	public Sprite(String fileName, int row, int col, long timer) {
		this(fileName, -1, 0, 0, row, col, timer);
	}

	/**
	 * 以下参数分别为 取材文件,坐标x,坐标y,每行取材宽度,每列取材长度
	 * 
	 * @param fileName
	 * @param x
	 * @param y
	 * @param row
	 * @param col
	 */
	public Sprite(String fileName, double x, double y, int row, int col) {
		this(fileName, x, y, row, col, defaultTimer);
	}

	/**
	 * 以下参数分别为 取材文件,坐标x,坐标y,每行取材宽度,每列取材长度,平均每桢显示时间
	 * 
	 * @param fileName
	 * @param x
	 * @param y
	 * @param row
	 * @param col
	 * @param timer
	 */
	private Sprite(String fileName, double x, double y, int row, int col,
			long timer) {
		this(fileName, -1, x, y, row, col, timer);
	}

	/**
	 * 以下参数分别为 取材文件,最大分解桢数,坐标x,坐标y,每行取材宽度,每列取材长度
	 * 
	 * @param fileName
	 * @param maxFrame
	 * @param x
	 * @param y
	 * @param row
	 * @param col
	 */
	public Sprite(String fileName, int maxFrame, double x, double y, int row,
			int col) {
		this(fileName, maxFrame, x, y, row, col, defaultTimer);
	}

	/**
	 * 以下参数分别为 取材文件,最大分解桢数,坐标x,坐标y,每行取材宽度,每列取材长度,平均每桢显示时间
	 * 
	 * @param fileName
	 * @param maxFrame
	 * @param x
	 * @param y
	 * @param row
	 * @param col
	 * @param timer
	 */
	public Sprite(String fileName, int maxFrame, double x, double y, int row,
			int col, long timer) {
		this("Sprite" + System.currentTimeMillis(), fileName, maxFrame, x, y,
				row, col, timer);
	}

	/**
	 * 以下参数分别为 精灵名，取材文件，最大分解桢数,坐标x,坐标y,每行取材宽度,每列取材长度,平均每桢显示时间
	 * 
	 * @param spriteName
	 * @param fileName
	 * @param maxFrame
	 * @param x
	 * @param y
	 * @param row
	 * @param col
	 * @param timer
	 */
	public Sprite(String spriteName, String fileName, int maxFrame, double x,
			double y, int row, int col, long timer) {
		this(spriteName,
				GraphicsUtils.getSplitImages(fileName, row, col, true),
				maxFrame, x, y, timer);
	}

	/**
	 * 注入指定图片
	 * 
	 * @param fileName
	 */
	public Sprite(String fileName) {
		this(GraphicsUtils.loadImage(fileName));
	}

	/**
	 * 注入一个单独的图像
	 * 
	 * @param img
	 */
	public Sprite(LImage img) {
		this(new LImage[] { img }, 0, 0);
	}

	/**
	 * 以下参数分别为 图像数组
	 * 
	 * @param images
	 */
	public Sprite(LImage[] images) {
		this(images, 0, 0);
	}

	/**
	 * 以下参数分别为 图像数组,坐标x,坐标y
	 * 
	 * @param images
	 * @param x
	 * @param y
	 */
	public Sprite(LImage[] images, double x, double y) {
		this(images, x, y, defaultTimer);
	}

	/**
	 * 以下参数分别为 图像数组,平均每桢显示时间
	 * 
	 * @param images
	 * @param timer
	 */
	public Sprite(LImage[] images, long timer) {
		this(images, -1, 0, 0, defaultTimer);
	}

	/**
	 * 以下参数分别为 图像数组,坐标x,坐标y,平均每桢显示时间
	 * 
	 * @param images
	 * @param x
	 * @param y
	 * @param timer
	 */
	public Sprite(LImage[] images, double x, double y, long timer) {
		this(images, -1, x, y, timer);
	}

	/**
	 * 以下参数分别为 图像数组,最大分解桢数,坐标x,坐标y,平均每桢显示时间
	 * 
	 * @param spriteName
	 * @param images
	 * @param maxFrame
	 * @param x
	 * @param y
	 * @param timer
	 */
	public Sprite(LImage[] images, int maxFrame, double x, double y, long timer) {
		this("Sprite" + System.currentTimeMillis(), images, maxFrame, x, y,
				timer);
	}

	/**
	 * 以下参数分别为 精灵名，图像数组，最大分解桢数,坐标x,坐标y,平均每桢显示时间
	 * 
	 * @param spriteName
	 * @param images
	 * @param maxFrame
	 * @param x
	 * @param y
	 * @param timer
	 */
	public Sprite(String spriteName, LImage[] images, int maxFrame, double x,
			double y, long timer) {
		this.setLocation(x, y);
		this.spriteName = spriteName;
		this.setAnimation(animation, images, maxFrame, timer);
		this.visible = true;
		this.filterType = ImageFilterFactory.NoneFilter;
		this.factory = ImageFilterFactory.getInstance();
	}

	/**
	 * 设定是否播放动画
	 * 
	 * @param running
	 */
	public void setRunning(boolean runing) {
		animation.setRunning(runing);
	}

	/**
	 * 是否在播放动画
	 * 
	 * @return
	 */
	public boolean isRunning() {
		return animation.isRunning();
	}

	/**
	 * 返回当前总桢数
	 * 
	 * @return
	 */
	public int getTotalFrames() {
		return animation.getTotalFrames();
	}

	/**
	 * 设定当前帧
	 * 
	 * @param index
	 */
	public void setCurrentFrameIndex(int index) {
		animation.setCurrentFrameIndex(index);
	}

	/**
	 * 返回当前桢索引
	 * 
	 * @return
	 */
	public int getCurrentFrameIndex() {
		return animation.getCurrentFrameIndex();
	}

	/**
	 * 插入指定动画
	 * 
	 * @param myAnimation
	 * @param images
	 * @param maxFrame
	 * @param timer
	 */
	private void setAnimation(Animation myAnimation, LImage[] images,
			int maxFrame, long timer) {
		if (maxFrame != -1) {
			for (int i = 0; i < maxFrame; i++) {
				myAnimation.addFrame(new SpriteImage(images[i]), timer);
			}
		} else {
			for (int i = 0; i < images.length; i++) {
				myAnimation.addFrame(new SpriteImage(images[i]), timer);
			}
		}
	}

	/**
	 * 插入指定动画
	 * 
	 * @param fileName
	 * @param maxFrame
	 * @param row
	 * @param col
	 * @param timer
	 */
	public void setAnimation(String fileName, int maxFrame, int row, int col,
			long timer) {
		setAnimation(new Animation(), GraphicsUtils.getSplitImages(fileName,
				row, col, true), maxFrame, timer);
	}

	/**
	 * 插入指定动画
	 * 
	 * @param fileName
	 * @param row
	 * @param col
	 * @param timer
	 */
	public void setAnimation(String fileName, int row, int col, long timer) {
		setAnimation(fileName, -1, row, col, timer);
	}

	/**
	 * 插入指定动画
	 * 
	 * @param images
	 * @param maxFrame
	 * @param timer
	 */
	public void setAnimation(LImage[] images, int maxFrame, long timer) {
		setAnimation(new Animation(), images, maxFrame, timer);
	}

	/**
	 * 插入指定动画
	 * 
	 * @param images
	 * @param timer
	 */
	public void setAnimation(LImage[] images, long timer) {
		setAnimation(new Animation(), images, -1, timer);
	}

	/**
	 * 插入指定动画
	 * 
	 * @param animation
	 */
	public void setAnimation(Animation animation) {
		this.animation = animation;
	}

	public Animation getAnimation() {
		return animation;
	}

	/**
	 * 变更动画
	 */
	public void update(long timer) {
		if (visible) {
			animation.update(timer);
		}
	}

	/**
	 * 变更定位器坐标
	 * 
	 * @param vector
	 */
	public void updateLocation(Vector2D vector) {
		this.setX(Math.round(vector.getX()));
		this.setY(Math.round(vector.getY()));
	}

	public SpriteImage getImage() {
		return animation.getSpriteImage();
	}

	public LImage getLImage() {
		return animation.getSpriteImage().getImage();
	}

	public SpriteImage getImage(int index) {
		return animation.getSpriteImage(index);
	}

	public int getWidth() {
		SpriteImage si = animation.getSpriteImage();
		if (si == null) {
			return -1;
		}
		return si.getWidth();
	}

	public int getHeight() {
		SpriteImage si = animation.getSpriteImage();
		if (si == null) {
			return -1;
		}
		return si.getHeight();
	}

	/**
	 * 获得对应当前精灵的Polygon
	 * 
	 * @return
	 */
	public Polygon getPolygon() {
		SpriteImage si = animation.getSpriteImage();
		if (si == null) {
			return new Polygon();
		}
		return si.getPolygon(x(), y(), transform);
	}

	/**
	 * 设定生成Polygon时像素取点间隔
	 * 
	 * @param i
	 */
	public void setPolygonInterval(int i) {
		SpriteImage si = animation.getSpriteImage();
		if (si == null) {
			return;
		}
		si.setMakePolygonInterval(i);
	}

	/**
	 * 获得精灵的中间位置
	 * 
	 * @return
	 */
	public Point2D.Float getMiddlePoint() {
		return new Point2D.Float(getLocation().x() + getWidth() / 2,
				getLocation().y() + getHeight() / 2);
	}

	/**
	 * 获得两个精灵的中间距离
	 * 
	 * @param second
	 * @return
	 */
	public double getDistance(Sprite second) {
		return this.getMiddlePoint().distance(second.getMiddlePoint());
	}

	/**
	 * 返回碰撞盒
	 * 
	 * @return
	 */
	public RectBox getCollisionBox() {
		return getRect(getLocation().x(), getLocation().y(),
				getWidth(), getHeight());
	}

	/**
	 * 检查是否与指定精灵位置发生了矩形碰撞
	 * 
	 * @param sprite
	 * @return
	 */
	public boolean isRectToRect(Sprite sprite) {
		return CollisionUtils.isRectToRect(this.getCollisionBox(), sprite
				.getCollisionBox());
	}

	/**
	 * 检查是否与指定精灵位置发生了圆形碰撞
	 * 
	 * @param sprite
	 * @return
	 */
	public boolean isCircToCirc(Sprite sprite) {
		return CollisionUtils.isCircToCirc(this.getCollisionBox(), sprite
				.getCollisionBox());
	}

	/**
	 * 检查是否与指定精灵位置发生了方形与圆形碰撞
	 * 
	 * @param sprite
	 * @return
	 */
	public boolean isRectToCirc(Sprite sprite) {
		return CollisionUtils.isRectToCirc(this.getCollisionBox(), sprite
				.getCollisionBox());
	}

	/**
	 * 检查两个精灵的像素点是否发生了碰撞
	 * 
	 * @param sprite
	 * @return
	 */
	public boolean isPixelCollision(Sprite sprite) {
		return CollisionUtils.isPixelHit(this, sprite);
	}

	/**
	 * 获得用于碰撞检测的像素遮挡数据
	 */
	public CollisionMask getMask() {
		SpriteImage si = animation.getSpriteImage();
		if (si == null) {
			return null;
		}
		return si.getMask(transform, x(), y());
	}

	public void createUI(LGraphics g) {
		if (!visible) {
			return;
		}
		image = animation.getSpriteImage();
		if (image == null) {
			return;
		}
		switch (filterType) {
		case ImageFilterType.NoneFilter:
			if (LTrans.TRANS_NONE == transform) {
				g.drawBitmap(image.getBitmap(), x(), y());
			} else {
				g.drawRegion(image.getBitmap(), 0, 0, getWidth(), getHeight(),
						transform, x(), y(), LGraphics.TOP | LGraphics.LEFT);
			}
			return;
		default:
			Bitmap tmp = factory.doFilter(image.getBitmap(), filterType);
			if (LTrans.TRANS_NONE == transform) {
				g.drawBitmap(tmp, x(), y());
			} else {
				g.drawRegion(tmp, 0, 0, getWidth(), getHeight(), transform,
						x(), y(), LGraphics.TOP | LGraphics.LEFT);
			}
			tmp.recycle();
			tmp = null;
			return;
		}
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public String getSpriteName() {
		return spriteName;
	}

	public void setSpriteName(String spriteName) {
		this.spriteName = spriteName;
	}

	public int getFilterType() {
		return filterType;
	}

	public void setFilterType(int filterType) {
		this.filterType = filterType;
	}

	public float getAlpha() {
		return this.animation.getAlpha();
	}

	public void setAlpha(float alpha) {
		this.animation.setAlpha(alpha);
	}

	public int getTransform() {
		return transform;
	}

	public void setTransform(int transform) {
		this.transform = transform;
	}

	public Bitmap getBitmap() {
		SpriteImage simg = animation.getSpriteImage();
		if (simg != null) {
			return simg.getBitmap();
		}
		return null;
	}

	public Object getTag() {
		return tag;
	}

	public void setTag(Object tag) {
		this.tag = tag;
	}

	public void dispose() {
		if (image == null) {
			image.dispose();
			image = null;
		}
		if (animation == null) {
			animation.dispose();
			animation = null;
		}
	}
}
