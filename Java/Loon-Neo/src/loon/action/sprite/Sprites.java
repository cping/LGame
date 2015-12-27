/**
 * Copyright 2008 - 2010
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
package loon.action.sprite;

import java.io.Serializable;

import loon.LObject;
import loon.LRelease;
import loon.LSystem;
import loon.Screen;
import loon.geom.PointI;
import loon.geom.RectBox;
import loon.opengl.GLEx;
import loon.utils.CollectionUtils;
import loon.utils.LayerSorter;
import loon.utils.MathUtils;

public class Sprites implements Serializable, LRelease {

	public static interface SpriteListener {

		public void update(ISprite spr);

	}

	private static final long serialVersionUID = 7460335325994101982L;

	private int viewX;

	private int viewY;

	private boolean isViewWindowSet = false, visible = true;

	private SpriteListener sprListerner;

	private final static LayerSorter<ISprite> spriteSorter = new LayerSorter<ISprite>(
			false);

	private int capacity = 1000;

	private ISprite[] sprites;

	private int size;

	private int width, height;

	private Screen _screen;

	public Sprites(Screen screen, int w, int h) {
		this._screen = screen;
		this.visible = true;
		this.width = w;
		this.height = h;
		this.sprites = new ISprite[capacity];
	}

	public Sprites(Screen screen) {
		this(screen, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	/**
	 * 设定指定对象到图层最前
	 * 
	 * @param sprite
	 */
	public void sendToFront(ISprite sprite) {
		if (this.size <= 1 || this.sprites[0] == sprite) {
			return;
		}
		if (sprites[0] == sprite) {
			return;
		}
		for (int i = 0; i < this.size; i++) {
			if (this.sprites[i] == sprite) {
				this.sprites = CollectionUtils.cut(this.sprites, i);
				this.sprites = CollectionUtils.expand(this.sprites, 1, false);
				this.sprites[0] = sprite;
				this.sortSprites();
				break;
			}
		}
	}

	/**
	 * 设定指定对象到图层最后
	 * 
	 * @param sprite
	 */
	public void sendToBack(ISprite sprite) {
		if (this.size <= 1 || this.sprites[this.size - 1] == sprite) {
			return;
		}
		if (sprites[this.size - 1] == sprite) {
			return;
		}
		for (int i = 0; i < this.size; i++) {
			if (this.sprites[i] == sprite) {
				this.sprites = CollectionUtils.cut(this.sprites, i);
				this.sprites = CollectionUtils.expand(this.sprites, 1, true);
				this.sprites[this.size - 1] = sprite;
				this.sortSprites();
				break;
			}
		}
	}

	/**
	 * 按所在层级排序
	 * 
	 */
	public void sortSprites() {
		spriteSorter.sort(this.sprites);
	}

	/**
	 * 扩充当前集合容量
	 * 
	 * @param capacity
	 */
	private void expandCapacity(int capacity) {
		if (sprites.length < capacity) {
			ISprite[] bagArray = new ISprite[capacity];
			System.arraycopy(sprites, 0, bagArray, 0, size);
			sprites = bagArray;
		}
	}

	/**
	 * 压缩当前集合容量
	 * 
	 * @param capacity
	 */
	private void compressCapacity(int capacity) {
		if (capacity + this.size < sprites.length) {
			ISprite[] newArray = new ISprite[this.size + 2];
			System.arraycopy(sprites, 0, newArray, 0, this.size);
			sprites = newArray;
		}
	}

	/**
	 * 查找指定位置的精灵对象
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public ISprite find(int x, int y) {
		ISprite[] snapshot = sprites;
		for (int i = snapshot.length - 1; i >= 0; i--) {
			ISprite child = snapshot[i];
			RectBox rect = child.getCollisionBox();
			if (rect != null && rect.contains(x, y)) {
				return child;
			}
		}
		return null;
	}

	/**
	 * 查找指定名称的精灵对象
	 * 
	 * @param name
	 * @return
	 */
	public ISprite find(String name) {
		ISprite[] snapshot = sprites;
		for (int i = snapshot.length - 1; i >= 0; i--) {
			ISprite child = snapshot[i];
			if (child instanceof LObject) {
				String childName = ((LObject) child).getName();
				if (name.equalsIgnoreCase(childName)) {
					return child;
				}
			}
		}
		return null;
	}

	/**
	 * 在指定索引处插入一个精灵
	 * 
	 * @param index
	 * @param sprite
	 * @return
	 */
	public boolean add(int index, ISprite sprite) {
		if (sprite == null) {
			return false;
		}
		if (index > this.size) {
			index = this.size;
		}
		if (index == this.size) {
			this.add(sprite);
		} else {
			System.arraycopy(this.sprites, index, this.sprites, index + 1,
					this.size - index);
			this.sprites[index] = sprite;
			if (++this.size >= this.sprites.length) {
				expandCapacity((size + 1) * 2);
			}
		}
		return sprites[index] != null;
	}

	public ISprite getSprite(int index) {
		if (index < 0 || index > size || index >= sprites.length) {
			return null;
		}
		return sprites[index];
	}

	/**
	 * 返回位于顶部的精灵
	 * 
	 * @return
	 */
	public ISprite getTopSprite() {
		if (size > 0) {
			return sprites[0];
		}
		return null;
	}

	/**
	 * 返回位于底部的精灵
	 * 
	 * @return
	 */
	public ISprite getBottomSprite() {
		if (size > 0) {
			return sprites[size - 1];
		}
		return null;
	}

	/**
	 * 顺序添加精灵
	 * 
	 * @param sprite
	 * @return
	 */
	public boolean add(ISprite sprite) {
		if (contains(sprite)) {
			return false;
		}

		if (this.size == this.sprites.length) {
			expandCapacity((size + 1) * 2);
		}
		return (sprites[size++] = sprite) != null;
	}

	/**
	 * 顺序添加精灵
	 * 
	 * @param sprite
	 * @return
	 */
	public void append(ISprite sprite) {
		add(sprite);
	}

	/**
	 * 检查指定精灵是否存在
	 * 
	 * @param sprite
	 * @return
	 */
	public boolean contains(ISprite sprite) {
		if (sprite == null) {
			return false;
		}
		if (sprites == null) {
			return false;
		}
		for (int i = 0; i < size; i++) {
			if (sprites[i] != null && sprite.equals(sprites[i])) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 删除指定索引处精灵
	 * 
	 * @param index
	 * @return
	 */
	public ISprite remove(int index) {
		ISprite removed = this.sprites[index];
		int size = this.size - index - 1;
		if (size > 0) {
			System.arraycopy(this.sprites, index + 1, this.sprites, index, size);
		}
		this.sprites[--this.size] = null;
		if (size == 0) {
			sprites = new ISprite[0];
		}
		return removed;
	}

	/**
	 * 清空所有精灵
	 * 
	 */
	public void removeAll() {
		clear();
		this.sprites = new ISprite[0];
	}

	/**
	 * 删除指定精灵
	 * 
	 * @param sprite
	 * @return
	 */
	public boolean remove(ISprite sprite) {
		if (sprite == null) {
			return false;
		}
		if (sprites == null) {
			return false;
		}
		boolean removed = false;
		for (int i = size; i > 0; i--) {
			if ((sprite == sprites[i - 1]) || (sprite.equals(sprites[i - 1]))) {
				removed = true;
				size--;
				sprites[i - 1] = sprites[size];
				sprites[size] = null;
				if (size == 0) {
					sprites = new ISprite[0];
				} else {
					compressCapacity(2);
				}
				return removed;
			}
		}
		return removed;
	}

	/**
	 * 删除指定名称的精灵
	 * 
	 * @param name
	 * @return
	 */
	public boolean removeName(String name) {
		if (name == null) {
			return false;
		}
		if (sprites == null) {
			return false;
		}
		boolean removed = false;
		for (int i = size; i > 0; i--) {
			if ((name.equals(sprites[i - 1].getName()))) {
				removed = true;
				size--;
				sprites[i - 1] = sprites[size];
				sprites[size] = null;
				if (size == 0) {
					sprites = new ISprite[0];
				} else {
					compressCapacity(2);
				}
				return removed;
			}
		}
		return removed;
	}

	/**
	 * 删除指定范围内精灵
	 * 
	 * @param startIndex
	 * @param endIndex
	 */
	public void remove(int startIndex, int endIndex) {
		int numMoved = this.size - endIndex;
		System.arraycopy(this.sprites, endIndex, this.sprites, startIndex,
				numMoved);
		int newSize = this.size - (endIndex - startIndex);
		while (this.size != newSize) {
			this.sprites[--this.size] = null;
		}
		if (size == 0) {
			sprites = new ISprite[0];
		}
	}

	public PointI getMinPos() {
		PointI p = new PointI(0, 0);
		for (int i = 0; i < size; i++) {
			ISprite sprite = sprites[i];
			p.x = MathUtils.min(p.x, sprite.x());
			p.y = MathUtils.min(p.y, sprite.y());
		}
		return p;
	}

	public PointI getMaxPos() {
		PointI p = new PointI(0, 0);
		for (int i = 0; i < size; i++) {
			ISprite sprite = sprites[i];
			p.x = MathUtils.max(p.x, sprite.x());
			p.y = MathUtils.max(p.y, sprite.y());
		}
		return p;
	}

	/**
	 * 清空当前精灵集合
	 * 
	 */
	public void clear() {
		for (int i = 0; i < sprites.length; i++) {
			sprites[i] = null;
		}
		size = 0;
	}

	/**
	 * 刷新事务
	 * 
	 * @param elapsedTime
	 */
	public void update(long elapsedTime) {

		boolean listerner = (sprListerner != null);
		for (int i = size - 1; i >= 0; i--) {

			ISprite child = sprites[i];
			if (child.isVisible()) {
				child.update(elapsedTime);
				if (listerner) {
					sprListerner.update(child);
				}
			}
		}
	}

	/**
	 * 创建UI图像
	 * 
	 * @param g
	 */
	public void createUI(final GLEx g) {
		createUI(g, 0, 0);
	}

	/**
	 * 创建UI图像
	 * 
	 * @param g
	 */
	public void createUI(final GLEx g, final int x, final int y) {
		if (!visible) {
			return;
		}
		float minX, minY, maxX, maxY;
		if (this.isViewWindowSet) {
			g.setClip(x, y, this.width, this.height);
			minX = this.viewX;
			maxX = minX + this.width;
			minY = this.viewY;
			maxY = minY + this.height;
		} else {
			minX = x;
			maxX = x + this.width;
			minY = y;
			maxY = y + this.height;
		}
		g.translate(x - this.viewX, y - this.viewY);
		for (int i = 0; i < this.size; i++) {

			ISprite spr = sprites[i];
			if (spr.isVisible()) {

				int layerX = spr.x();
				int layerY = spr.y();

				float layerWidth = spr.getWidth();
				float layerHeight = spr.getHeight();

				if (layerX + layerWidth < minX || layerX > maxX
						|| layerY + layerHeight < minY || layerY > maxY) {
					continue;
				}

				spr.createUI(g);
			}
		}
		g.translate(-(x - this.viewX), -(y - this.viewY));
		if (this.isViewWindowSet) {
			g.clearClip();
		}
	}

	/**
	 * 设定精灵集合在屏幕中的位置与大小
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public void setViewWindow(int x, int y, int width, int height) {
		this.isViewWindowSet = true;
		this.viewX = x;
		this.viewY = y;
		this.width = width;
		this.height = height;
	}

	/**
	 * 设定精灵集合在屏幕中的位置
	 * 
	 * @param x
	 * @param y
	 */
	public void setLocation(int x, int y) {
		this.isViewWindowSet = true;
		this.viewX = x;
		this.viewY = y;
	}

	public ISprite[] getSprites() {
		return this.sprites;
	}

	public int size() {
		return this.size;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public SpriteListener getSprListerner() {
		return sprListerner;
	}

	public void setSprListerner(SpriteListener sprListerner) {
		this.sprListerner = sprListerner;
	}

	public Screen getScreen() {
		return _screen;
	}

	@Override
	public void close() {
		this.visible = false;
		for (ISprite spr : sprites) {
			if (spr != null) {
				spr.close();
				spr = null;
			}
		}
		clear();
	}

}
