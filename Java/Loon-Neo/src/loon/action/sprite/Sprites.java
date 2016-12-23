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
 * @version 0.5
 */
package loon.action.sprite;

import java.io.Serializable;

import loon.LObject.State;
import loon.LRelease;
import loon.LSystem;
import loon.Screen;
import loon.geom.PointI;
import loon.geom.RectBox;
import loon.opengl.GLEx;
import loon.utils.CollectionUtils;
import loon.utils.LayerSorter;
import loon.utils.MathUtils;
import loon.utils.TArray;

/**
 * 精灵精灵总父类，用来注册，控制，以及渲染所有精灵精灵（所有默认【不支持】触屏的精灵，被置于此。不过，
 * 当LNode系列精灵和SpriteBatchScreen合用时，也支持触屏.）
 * 
 */
public class Sprites implements Serializable, LRelease {

	private final static TArray<Sprites> SPRITES_CACHE = new TArray<Sprites>(8);

	public final static int allSpritesCount() {
		int size = 0;
		for (int i = 0, len = SPRITES_CACHE.size; i < len; i++) {
			size += SPRITES_CACHE.get(i).size();
		}
		return size;
	}

	public static interface SpriteListener {

		public void update(ISprite spr);

	}

	private static final long serialVersionUID = 7460335325994101982L;

	protected ISprite[] _sprites;

	private int viewX;

	private int viewY;

	private boolean _isViewWindowSet = false, _visible = true;

	private SpriteListener sprListerner;

	private final static LayerSorter<ISprite> spriteSorter = new LayerSorter<ISprite>(
			false);

	private int capacity = 1000;

	private int _size;

	private int _width, _height;

	private Screen _screen;

	public Sprites(Screen screen, float width, float height) {
		this(screen, (int) width, (int) height);
	}

	public Sprites(Screen screen, int w, int h) {
		this._screen = screen;
		this._visible = true;
		this._width = w;
		this._height = h;
		this._sprites = new ISprite[capacity];
		SPRITES_CACHE.add(this);
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
		if (this._size <= 1 || this._sprites[0] == sprite) {
			return;
		}
		if (_sprites[0] == sprite) {
			return;
		}
		for (int i = 0; i < this._size; i++) {
			if (this._sprites[i] == sprite) {
				this._sprites = CollectionUtils.cut(this._sprites, i);
				this._sprites = CollectionUtils.expand(this._sprites, 1, false);
				this._sprites[0] = sprite;
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
		if (this._size <= 1 || this._sprites[this._size - 1] == sprite) {
			return;
		}
		if (_sprites[this._size - 1] == sprite) {
			return;
		}
		for (int i = 0; i < this._size; i++) {
			if (this._sprites[i] == sprite) {
				this._sprites = CollectionUtils.cut(this._sprites, i);
				this._sprites = CollectionUtils.expand(this._sprites, 1, true);
				this._sprites[this._size - 1] = sprite;
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
		spriteSorter.sort(this._sprites);
	}

	/**
	 * 扩充当前集合容量
	 * 
	 * @param capacity
	 */
	private void expandCapacity(int capacity) {
		if (_sprites.length < capacity) {
			ISprite[] bagArray = new ISprite[capacity];
			System.arraycopy(_sprites, 0, bagArray, 0, _size);
			_sprites = bagArray;
		}
	}

	/**
	 * 压缩当前集合容量
	 * 
	 * @param capacity
	 */
	private void compressCapacity(int capacity) {
		if (capacity + this._size < _sprites.length) {
			ISprite[] newArray = new ISprite[this._size + 2];
			System.arraycopy(_sprites, 0, newArray, 0, this._size);
			_sprites = newArray;
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
		ISprite[] snapshot = _sprites;
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
		ISprite[] snapshot = _sprites;
		for (int i = snapshot.length - 1; i >= 0; i--) {
			ISprite child = snapshot[i];
			String childName = child.getName();
			if (name.equals(childName)) {
				return child;
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
		if (index > this._size) {
			index = this._size;
		}
		if (index == this._size) {
			return this.add(sprite);
		} else {
			System.arraycopy(this._sprites, index, this._sprites, index + 1,
					this._size - index);
			this._sprites[index] = sprite;
			if (++this._size >= this._sprites.length) {
				expandCapacity((_size + 1) * 2);
			}
			sortSprites();
			sprite.setState(State.ADDED);
		}
		boolean result = _sprites[index] != null;
		return result;
	}

	public void addAt(ISprite child, float x, float y) {
		if (child != null) {
			child.setLocation(x, y);
			add(child);
		}
	}

	public ISprite getSprite(int index) {
		if (index < 0 || index > _size || index >= _sprites.length) {
			return null;
		}
		return _sprites[index];
	}

	/**
	 * 返回位于顶部的精灵
	 * 
	 * @return
	 */
	public ISprite getTopSprite() {
		if (_size > 0) {
			return _sprites[0];
		}
		return null;
	}

	/**
	 * 返回位于底部的精灵
	 * 
	 * @return
	 */
	public ISprite getBottomSprite() {
		if (_size > 0) {
			return _sprites[_size - 1];
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

		if (this._size == this._sprites.length) {
			expandCapacity((_size + 1) * 2);
		}
		boolean result = (_sprites[_size++] = sprite) != null;
		sortSprites();
		sprite.setState(State.ADDED);
		return result;
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
	 * 返回一组拥有指定标签的精灵
	 * 
	 * @param tags
	 * @return
	 */
	public TArray<ISprite> findTags(Object... tags) {
		TArray<ISprite> list = new TArray<ISprite>();
		final int size = this._size;
		for (Object tag : tags) {
			for (int i = size - 1; i > -1; i--) {
				if (this._sprites[i] instanceof ISprite) {
					ISprite sp = (ISprite) this._sprites[i];
					if (sp.getTag() == tag || tag.equals(sp.getTag())) {
						list.add(sp);
					}
				}
			}
		}
		return list;
	}

	/**
	 * 返回一组没有指定标签的精灵
	 * 
	 * @param tags
	 * @return
	 */
	public TArray<ISprite> findNotTags(Object... tags) {
		TArray<ISprite> list = new TArray<ISprite>();
		final int size = this._size;
		for (Object tag : tags) {
			for (int i = size - 1; i > -1; i--) {
				if (this._sprites[i] instanceof ISprite) {
					ISprite sp = (ISprite) this._sprites[i];
					if (!tag.equals(sp.getTag())) {
						list.add(sp);
					}
				}
			}
		}
		return list;
	}

	/**
	 * 返回一组指定名的精灵
	 * 
	 * @param names
	 * @return
	 */
	public TArray<ISprite> findNames(String... names) {
		TArray<ISprite> list = new TArray<ISprite>();
		final int size = this._size;
		for (String name : names) {
			for (int i = size - 1; i > -1; i--) {
				if (this._sprites[i] instanceof ISprite) {
					ISprite sp = (ISprite) this._sprites[i];
					if (name.equals(sp.getName())) {
						list.add(sp);
					}
				}
			}
		}
		return list;
	}

	/**
	 * 返回一组没有指定名的精灵
	 * 
	 * @param names
	 * @return
	 */
	public TArray<ISprite> findNotNames(String... names) {
		TArray<ISprite> list = new TArray<ISprite>();
		final int size = this._size;
		for (String name : names) {
			for (int i = size - 1; i > -1; i--) {
				if (this._sprites[i] instanceof ISprite) {
					ISprite sp = (ISprite) this._sprites[i];
					if (!name.equals(sp.getName())) {
						list.add(sp);
					}
				}
			}
		}
		return list;
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
		if (_sprites == null) {
			return false;
		}
		for (int i = 0; i < _size; i++) {
			if (_sprites[i] != null && sprite.equals(_sprites[i])) {
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
		ISprite removed = this._sprites[index];
		if (removed != null) {
			removed.setState(State.REMOVED);
		}
		int size = this._size - index - 1;
		if (size > 0) {
			System.arraycopy(this._sprites, index + 1, this._sprites, index,
					size);
		}
		this._sprites[--this._size] = null;
		if (size == 0) {
			_sprites = new ISprite[0];
		}
		return removed;
	}

	/**
	 * 清空所有精灵
	 * 
	 */
	public void removeAll() {
		clear();
		this._sprites = new ISprite[0];
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
		if (_sprites == null) {
			return false;
		}
		boolean removed = false;
		for (int i = _size; i > 0; i--) {
			ISprite spr = _sprites[i - 1];
			if ((sprite == spr) || (sprite.equals(spr))) {
				spr.setState(State.REMOVED);
				removed = true;
				_size--;
				_sprites[i - 1] = _sprites[_size];
				_sprites[_size] = null;
				if (_size == 0) {
					_sprites = new ISprite[0];
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
		if (_sprites == null) {
			return false;
		}
		boolean removed = false;
		for (int i = _size; i > 0; i--) {
			ISprite spr = _sprites[i - 1];
			if ((name.equals(spr.getName()))) {
				spr.setState(State.REMOVED);
				removed = true;
				_size--;
				_sprites[i - 1] = _sprites[_size];
				_sprites[_size] = null;
				if (_size == 0) {
					_sprites = new ISprite[0];
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
		if (endIndex - startIndex > 0) {
			for (int i = startIndex; i < endIndex && i < _sprites.length; i++) {
				ISprite spr = _sprites[i];
				if (spr != null) {
					spr.setState(State.REMOVED);
				}
			}
		}
		int numMoved = this._size - endIndex;
		System.arraycopy(this._sprites, endIndex, this._sprites, startIndex,
				numMoved);
		int newSize = this._size - (endIndex - startIndex);
		while (this._size != newSize) {
			this._sprites[--this._size] = null;
		}
		if (_size == 0) {
			_sprites = new ISprite[0];
		}
	}

	public PointI getMinPos() {
		PointI p = new PointI(0, 0);
		for (int i = 0; i < _size; i++) {
			ISprite sprite = _sprites[i];
			p.x = MathUtils.min(p.x, sprite.x());
			p.y = MathUtils.min(p.y, sprite.y());
		}
		return p;
	}

	public PointI getMaxPos() {
		PointI p = new PointI(0, 0);
		for (int i = 0; i < _size; i++) {
			ISprite sprite = _sprites[i];
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
		for (int i = 0; i < _sprites.length; i++) {
			if (_sprites[i] != null) {
				_sprites[i].setState(State.REMOVED);
			}
			_sprites[i] = null;
		}
		_size = 0;
	}

	/**
	 * 刷新事务
	 * 
	 * @param elapsedTime
	 */
	public void update(long elapsedTime) {

		boolean listerner = (sprListerner != null);
		for (int i = _size - 1; i >= 0; i--) {

			ISprite child = _sprites[i];
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
		if (!_visible) {
			return;
		}
		float minX, minY, maxX, maxY;
		if (this._isViewWindowSet) {
			g.setClip(x, y, this._width, this._height);
			minX = this.viewX;
			maxX = minX + this._width;
			minY = this.viewY;
			maxY = minY + this._height;
		} else {
			minX = x;
			maxX = x + this._width;
			minY = y;
			maxY = y + this._height;
		}
		g.translate(x - this.viewX, y - this.viewY);
		for (int i = 0; i < this._size; i++) {
			ISprite spr = this._sprites[i];
			if (spr != null && spr.isVisible()) {
				int layerX = spr.x();
				int layerY = spr.y();

				float layerWidth = spr.getWidth() + 1;
				float layerHeight = spr.getHeight() + 1;

				if (layerX + layerWidth < minX || layerX > maxX
						|| layerY + layerHeight < minY || layerY > maxY) {
					continue;
				}

				spr.createUI(g);

			}
		}
		g.translate(-(x - this.viewX), -(y - this.viewY));
		if (this._isViewWindowSet) {
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
		this._isViewWindowSet = true;
		this.viewX = x;
		this.viewY = y;
		this._width = width;
		this._height = height;
	}

	/**
	 * 设定精灵集合在屏幕中的位置
	 * 
	 * @param x
	 * @param y
	 */
	public void setLocation(int x, int y) {
		this._isViewWindowSet = true;
		this.viewX = x;
		this.viewY = y;
	}

	public SpriteControls findNamesToSpriteControls(String... names) {
		SpriteControls controls = null;
		if (_sprites != null) {
			TArray<ISprite> sps = findNames(names);
			controls = new SpriteControls(sps);
		}
		return controls;
	}

	public SpriteControls findNotUINamesToUIControls(String... names) {
		SpriteControls controls = null;
		if (_sprites != null) {
			TArray<ISprite> sps = findNotNames(names);
			controls = new SpriteControls(sps);
		}
		return controls;
	}

	public SpriteControls findTagsToSpriteControls(Object... o) {
		SpriteControls controls = null;
		if (_sprites != null) {
			TArray<ISprite> sps = findTags(o);
			controls = new SpriteControls(sps);
		}
		return controls;
	}

	public SpriteControls findNotTagsToUIControls(Object... o) {
		SpriteControls controls = null;
		if (_sprites != null) {
			TArray<ISprite> sps = findNotTags(o);
			controls = new SpriteControls(sps);
		}
		return controls;
	}

	public ISprite[] getSprites() {
		return CollectionUtils.copyOf(this._sprites, this._size);
	}

	public int size() {
		return this._size;
	}

	public int getHeight() {
		return _height;
	}

	public int getWidth() {
		return _width;
	}

	public boolean isVisible() {
		return _visible;
	}

	public void setVisible(boolean visible) {
		this._visible = visible;
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
		this._visible = false;
		for (ISprite spr : _sprites) {
			if (spr != null) {
				spr.close();
			}
		}
		clear();
		SPRITES_CACHE.remove(this);
	}

}
