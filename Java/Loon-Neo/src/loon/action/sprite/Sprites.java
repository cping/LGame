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

import loon.LObject.State;
import loon.LRelease;
import loon.LSystem;
import loon.Screen;
import loon.Visible;
import loon.action.ActionBind;
import loon.action.ActionControl;
import loon.event.QueryEvent;
import loon.geom.PointI;
import loon.geom.RectBox;
import loon.opengl.GLEx;
import loon.utils.CollectionUtils;
import loon.utils.IArray;
import loon.utils.LayerSorter;
import loon.utils.MathUtils;
import loon.utils.StringUtils;
import loon.utils.TArray;

/**
 * 精灵精灵总父类，用来注册，控制，以及渲染所有精灵精灵（所有默认【不支持】触屏的精灵，被置于此。不过，
 * 当LNode系列精灵和SpriteBatchScreen合用时，也支持触屏.）
 * 
 */
public class Sprites implements IArray, Visible, LRelease {

	public static interface SpriteListener {

		public void update(ISprite spr);

	}

	protected ISprite[] _sprites;

	private int viewX;

	private int viewY;

	private int viewWidth;

	private int viewHeight;

	private boolean _isViewWindowSet = false, _visible = true, _closed = false;

	private SpriteListener sprListerner;

	private final static LayerSorter<ISprite> spriteSorter = new LayerSorter<ISprite>(false);

	private int _size;

	private int _width, _height;

	private float _newLineHeight = -1f;

	private Screen _screen;

	private final String _sprites_name;

	public Sprites(Screen screen, int w, int h) {
		this(null, screen, w, h);
	}

	public Sprites(Screen screen, float width, float height) {
		this(null, screen, (int) width, (int) height);
	}

	public Sprites(Screen screen) {
		this(null, screen, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public Sprites(String name, Screen screen, float w, float h) {
		this(name, screen, (int) w, (int) h);
	}

	public Sprites(String name, Screen screen, int w, int h) {
		this._screen = screen;
		this._visible = true;
		this._sprites = new ISprite[CollectionUtils.INITIAL_CAPACITY];
		this._sprites_name = StringUtils.isEmpty(name) ? "Sprites" + LSystem.getSpritesSize() : name;
		this.setSize(w, h);
		LSystem.pushSpritesPool(this);
	}

	/**
	 * 设定Sprites大小
	 * 
	 * @param w
	 * @param h
	 * @return
	 */
	public Sprites setSize(int w, int h) {
		if (this._width != w || this._height != h) {
			this._width = w;
			this._height = h;
			if (this._width == 0) {
				this._width = 1;
			}
			if (this._height == 0) {
				this._height = 1;
			}
			if (viewWidth < this._width) {
				viewWidth = this._width;
			}
			if (viewHeight < this._height) {
				viewHeight = this._height;
			}
		}
		return this;
	}

	/**
	 * 设定指定对象到图层最前
	 * 
	 * @param sprite
	 */
	public void sendToFront(ISprite sprite) {
		if (_closed) {
			return;
		}
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
		if (_closed) {
			return;
		}
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
		if (_closed) {
			return;
		}
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
			ISprite[] newArray = new ISprite[this._size + capacity];
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
		if (_closed) {
			return null;
		}
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
		if (_closed) {
			return null;
		}
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
	 * 按照上一个精灵的x,y位置,另起一行添加精灵,并偏移指定位置
	 * 
	 * @param spr
	 * @param offX
	 * @param offY
	 * @return
	 */
	public ISprite addPadding(ISprite spr, float offX, float offY) {
		return addPadding(spr, offX, offY, 2);
	}

	/**
	 * 按照上一个精灵的y轴,另起一行添加精灵
	 * 
	 * @param spr
	 * @return
	 */
	public ISprite addCol(ISprite spr) {
		return addPadding(spr, 0, 0, 1);
	}

	/**
	 * 按照上一个精灵的y轴,另起一行添加精灵,并让y轴偏移指定位置
	 * 
	 * @param spr
	 * @param offY
	 * @return
	 */
	public ISprite addCol(ISprite spr, float offY) {
		return addPadding(spr, 0, offY, 1);
	}

	/**
	 * 按照上一个精灵的x轴,另起一行添加精灵
	 * 
	 * @param spr
	 * @return
	 */
	public ISprite addRow(ISprite spr) {
		return addPadding(spr, 0, 0, 0);
	}

	/**
	 * 按照上一个精灵的x轴,另起一行添加精灵,并将x轴偏移指定位置
	 * 
	 * @param spr
	 * @param offX
	 * @return
	 */
	public ISprite addRow(ISprite spr, float offX) {
		return addPadding(spr, offX, 0, 0);
	}

	/**
	 * 按照上一个精灵的x,y位置,另起一行添加精灵,并偏移指定位置
	 * 
	 * @param spr
	 * @param offX
	 * @param offY
	 * @param code
	 * @return
	 */
	public ISprite addPadding(ISprite spr, float offX, float offY, int code) {
		if (_closed) {
			return spr;
		}
		if (spr == null) {
			return null;
		}
		if (this == spr) {
			return spr;
		}

		float maxX = 0;
		float maxY = 0;

		ISprite tag = null;

		if (_size == 1) {
			ISprite cp = _sprites[0];
			if (cp != null && cp.getY() >= _newLineHeight) {
				maxX = cp.getX();
				maxY = cp.getY();
				tag = cp;
			}
		} else {
			for (int i = 0; i < _size; i++) {
				ISprite c = _sprites[i];
				if (c != null && c != spr && c.getY() >= _newLineHeight) {
					float oldMaxX = maxX;
					float oldMaxY = maxY;
					maxX = MathUtils.max(maxX, c.getX());
					maxY = MathUtils.max(maxY, c.getY());
					if (oldMaxX != maxX || oldMaxY != maxY) {
						tag = c;
					}
				}
			}

		}
		if (tag == null && _size > 0) {
			tag = _sprites[_size - 1];
		}

		if (tag != null && tag != spr) {
			switch (code) {
			case 0:
				spr.setLocation(maxX + tag.getWidth() + offX, maxY + offY);
				break;
			case 1:
				spr.setLocation(0 + offX, maxY + tag.getHeight() + offY);
				break;
			default:
				spr.setLocation(maxX + tag.getWidth() + offX, maxY + tag.getHeight() + offY);
				break;
			}

		} else {
			switch (code) {
			case 0:
				spr.setLocation(maxX + offX, maxY + offY);
				break;
			case 1:
				spr.setLocation(0 + offX, maxY + offY);
				break;
			default:
				spr.setLocation(maxX + offX, maxY + offY);
				break;
			}
		}

		add(spr);
		_newLineHeight = spr.getY();
		return spr;
	}

	/**
	 * 在指定索引处插入一个精灵
	 * 
	 * @param index
	 * @param sprite
	 * @return
	 */
	public boolean add(int index, ISprite sprite) {
		if (_closed) {
			return false;
		}
		if (sprite == null) {
			return false;
		}
		if (index > this._size) {
			index = this._size;
		}
		if (index == this._size) {
			return this.add(sprite);
		} else {
			if (sprite.getWidth() > getWidth()) {
				setViewWindow(viewX, viewY, (int) MathUtils.max(sprite.getWidth(), LSystem.viewSize.width), _height);
			}
			if (sprite.getHeight() > getHeight()) {
				setViewWindow(viewX, viewY, _width, (int) MathUtils.max(sprite.getWidth(), LSystem.viewSize.width));
			}
			System.arraycopy(this._sprites, index, this._sprites, index + 1, this._size - index);
			this._sprites[index] = sprite;
			if (++this._size >= this._sprites.length) {
				expandCapacity((_size + 1) * 2);
			}
			sortSprites();
			sprite.setState(State.ADDED);
			sprite.setSprites(this);
		}
		boolean result = _sprites[index] != null;
		return result;
	}

	public void addAt(ISprite child, float x, float y) {
		if (_closed) {
			return;
		}
		if (child != null) {
			child.setLocation(x, y);
			add(child);
		}
	}

	public ISprite getSprite(int index) {
		if (_closed) {
			return null;
		}
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
		if (_closed) {
			return null;
		}
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
		if (_closed) {
			return null;
		}
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
		if (_closed) {
			return false;
		}
		if (sprite == null) {
			return false;
		}
		if (contains(sprite)) {
			return false;
		}
		sprite.setSprites(this);
		if (sprite.getWidth() > getWidth()) {
			setViewWindow(viewX, viewY, (int) MathUtils.max(sprite.getWidth(), LSystem.viewSize.width), _height);
		}
		if (sprite.getHeight() > getHeight()) {
			setViewWindow(viewX, viewY, _width, (int) MathUtils.max(sprite.getHeight(), LSystem.viewSize.height));
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
		if (_closed) {
			return null;
		}
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
		if (_closed) {
			return null;
		}
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
		if (_closed) {
			return null;
		}
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
		if (_closed) {
			return null;
		}
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
		if (_closed) {
			return false;
		}
		if (sprite == null) {
			return false;
		}
		if (_sprites == null) {
			return false;
		}
		for (int i = 0; i < _size; i++) {
			ISprite sp = _sprites[i];
			boolean exist = (sp != null);
			if (exist && sprite.equals(sp)) {
				return true;
			}
			if (exist && sp instanceof Entity) {
				Entity superEntity = (Entity) sp;
				for (int j = 0; j < superEntity.getChildCount(); j++) {
					boolean superExist = (superEntity.getChildByIndex(j) != null);
					if (superExist && sp.equals(superEntity.getChildByIndex(j))) {
						return true;
					}
				}
			} else if (exist && sp instanceof Sprite) {
				Sprite superSprite = (Sprite) sp;
				for (int j = 0; j < superSprite.size(); j++) {
					boolean superExist = (superSprite.getChildByIndex(j) != null);
					if (superExist && sp.equals(superSprite.getChildByIndex(j))) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 返回指定位置内的所有精灵
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @return
	 */
	public TArray<ISprite> contains(float x, float y, float w, float h) {
		TArray<ISprite> sprites = new TArray<ISprite>();
		if (_closed) {
			return sprites;
		}
		if (_sprites == null) {
			return sprites;
		}
		for (int i = 0; i < _size; i++) {
			ISprite sp = _sprites[i];
			if (sp != null) {
				if (sp.inContains(x, y, w, h)) {
					sprites.add(sp);
				}
			}
		}
		return sprites;
	}

	/**
	 * 返回包含指定位置的所有精灵
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public TArray<ISprite> contains(float x, float y) {
		return contains(x, y, 1f, 1f);
	}

	/**
	 * 返回包含指定精灵位置的所有精灵
	 * 
	 * @param sprite
	 * @return
	 */
	public TArray<ISprite> containsSprite(ISprite sprite) {
		if (sprite == null) {
			return new TArray<ISprite>(0);
		}
		return contains(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
	}

	/**
	 * 返回指定位置内的所有精灵
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @return
	 */
	public TArray<ISprite> intersects(float x, float y, float w, float h) {
		TArray<ISprite> sprites = new TArray<ISprite>();
		if (_closed) {
			return sprites;
		}
		if (_sprites == null) {
			return sprites;
		}
		for (int i = 0; i < _size; i++) {
			ISprite sp = _sprites[i];
			if (sp != null) {
				if (sp.getCollisionBox().intersects(x, y, w, h)) {
					sprites.add(sp);
				}
			}
		}
		return sprites;
	}

	/**
	 * 返回与指定位置相交的所有精灵
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public TArray<ISprite> intersects(float x, float y) {
		return intersects(x, y, 1f, 1f);
	}

	/**
	 * 返回与指定精灵位置相交的所有精灵
	 * 
	 * @param sprite
	 * @return
	 */
	public TArray<ISprite> intersectsSprite(ISprite sprite) {
		if (sprite == null) {
			return new TArray<ISprite>(0);
		}
		return intersects(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
	}

	/**
	 * 删除指定索引处精灵
	 * 
	 * @param index
	 * @return
	 */
	public ISprite remove(int index) {
		if (_closed) {
			return null;
		}
		ISprite removed = this._sprites[index];
		if (removed != null) {
			removed.setState(State.REMOVED);
			// 删除精灵同时，删除缓动动画
			if (removed instanceof ActionBind) {
				ActionControl.get().removeAllActions((ActionBind) removed);
			}
		}
		int size = this._size - index - 1;
		if (size > 0) {
			System.arraycopy(this._sprites, index + 1, this._sprites, index, size);
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
		if (_closed) {
			return;
		}
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
		if (_closed) {
			return false;
		}
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
				// 删除精灵同时，删除缓动动画
				if (spr instanceof ActionBind) {
					ActionControl.get().removeAllActions((ActionBind) spr);
				}
				removed = true;
				_size--;
				_sprites[i - 1] = _sprites[_size];
				_sprites[_size] = null;
				if (_size == 0) {
					_sprites = new ISprite[0];
				} else {
					compressCapacity(CollectionUtils.INITIAL_CAPACITY);
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
		if (_closed) {
			return false;
		}
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
				// 删除精灵同时，删除缓动动画
				if (spr instanceof ActionBind) {
					ActionControl.get().removeAllActions((ActionBind) spr);
				}
				removed = true;
				_size--;
				_sprites[i - 1] = _sprites[_size];
				_sprites[_size] = null;
				if (_size == 0) {
					_sprites = new ISprite[0];
				} else {
					compressCapacity(CollectionUtils.INITIAL_CAPACITY);
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
		if (_closed) {
			return;
		}
		if (endIndex - startIndex > 0) {
			for (int i = startIndex; i < endIndex && i < _sprites.length; i++) {
				ISprite spr = _sprites[i];
				if (spr != null) {
					spr.setState(State.REMOVED);
					// 删除精灵同时，删除缓动动画
					if (spr instanceof ActionBind) {
						ActionControl.get().removeAllActions((ActionBind) spr);
					}
				}
			}
		}
		int numMoved = this._size - endIndex;
		System.arraycopy(this._sprites, endIndex, this._sprites, startIndex, numMoved);
		int newSize = this._size - (endIndex - startIndex);
		while (this._size != newSize) {
			this._sprites[--this._size] = null;
		}
		if (_size == 0) {
			_sprites = new ISprite[0];
		}
	}

	public PointI getMinPos() {
		if (_closed) {
			return new PointI(0, 0);
		}
		PointI p = new PointI(0, 0);
		for (int i = 0; i < _size; i++) {
			ISprite sprite = _sprites[i];
			p.x = MathUtils.min(p.x, sprite.x());
			p.y = MathUtils.min(p.y, sprite.y());
		}
		return p;
	}

	public PointI getMaxPos() {
		if (_closed) {
			return new PointI(0, 0);
		}
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
		if (_closed) {
			return;
		}
		for (int i = 0; i < _sprites.length; i++) {
			ISprite removed = _sprites[i];
			if (removed != null) {
				removed.setState(State.REMOVED);
				// 删除精灵同时，删除缓动动画
				if (removed instanceof ActionBind) {
					ActionControl.get().removeAllActions((ActionBind) removed);
				}
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
		if (!_visible || _closed) {
			return;
		}
		boolean listerner = (sprListerner != null);
		for (int i = _size - 1; i > -1; i--) {
			ISprite child = _sprites[i];
			if (child.isVisible()) {
				try {
					child.update(elapsedTime);
					if (listerner) {
						sprListerner.update(child);
					}
				} catch (Throwable cause) {
					LSystem.error("Sprites update() exception", cause);
				}
			}
		}
	}

	/**
	 * 单纯渲染精灵
	 * 
	 * @param g
	 */
	public void paint(final GLEx g, final float minX, final float minY, final float maxX, final float maxY) {
		if (!_visible || _closed) {
			return;
		}
		float spriteX;
		float spriteY;
		float spriteWidth;
		float spriteHeight;
		for (int i = 0; i < this._size; i++) {
			ISprite spr = this._sprites[i];
			if (spr != null && spr.isVisible()) {
				spriteX = minX + spr.getX();
				spriteY = minY + spr.getY();
				spriteWidth = spr.getWidth();
				spriteHeight = spr.getHeight();
				if (spriteX + spriteWidth < minX || spriteX > maxX || spriteY + spriteHeight < minY || spriteY > maxY) {
					continue;
				}
				spr.createUI(g);
			}
		}
	}

	public void paintPos(final GLEx g, final float offsetX, final float offsetY) {
		if (_closed) {
			return;
		}
		if (!_visible) {
			return;
		}
		for (int i = 0; i < this._size; i++) {
			ISprite spr = this._sprites[i];
			if (spr != null && spr.isVisible()) {
				spr.createUI(g, offsetX, offsetY);
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
		if (_closed) {
			return;
		}
		if (!_visible) {
			return;
		}
		float minX, minY, maxX, maxY;
		if (this._isViewWindowSet) {
			minX = x + this.viewX;
			maxX = minX + this.viewWidth;
			minY = y + this.viewY;
			maxY = minY + this.viewHeight;
		} else {
			minX = x;
			maxX = x + this._width;
			minY = y;
			maxY = y + this._height;
		}
		boolean offset = (minX != 0 || minY != 0);
		if (offset) {
			g.translate(minX, minY);
		}
		for (int i = 0; i < this._size; i++) {
			ISprite spr = this._sprites[i];
			if (spr != null && spr.isVisible()) {
				int layerX = spr.x();
				int layerY = spr.y();
				float layerWidth = spr.getWidth() + 1;
				float layerHeight = spr.getHeight() + 1;
				if (layerX + layerWidth < minX || layerX > maxX || layerY + layerHeight < minY || layerY > maxY) {
					continue;
				}
				spr.createUI(g);
			}
		}
		if (offset) {
			g.translate(-minX, -minY);
		}
	}

	public float getX() {
		return viewX;
	}

	public float getY() {
		return viewY;
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
		this.viewWidth = width;
		this.viewHeight = height;
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

	public SpriteControls createSpriteControls() {
		if (_closed) {
			return new SpriteControls();
		}
		SpriteControls controls = null;
		if (_sprites != null) {
			controls = new SpriteControls(_sprites);
		} else {
			controls = new SpriteControls();
		}
		return controls;
	}

	public SpriteControls findNamesToSpriteControls(String... names) {
		if (_closed) {
			return new SpriteControls();
		}
		SpriteControls controls = null;
		if (_sprites != null) {
			TArray<ISprite> sps = findNames(names);
			controls = new SpriteControls(sps);
		} else {
			controls = new SpriteControls();
		}
		return controls;
	}

	public SpriteControls findNotNamesToSpriteControls(String... names) {
		if (_closed) {
			return new SpriteControls();
		}
		SpriteControls controls = null;
		if (_sprites != null) {
			TArray<ISprite> sps = findNotNames(names);
			controls = new SpriteControls(sps);
		} else {
			controls = new SpriteControls();
		}
		return controls;
	}

	public SpriteControls findTagsToSpriteControls(Object... o) {
		if (_closed) {
			return new SpriteControls();
		}
		SpriteControls controls = null;
		if (_sprites != null) {
			TArray<ISprite> sps = findTags(o);
			controls = new SpriteControls(sps);
		} else {
			controls = new SpriteControls();
		}
		return controls;
	}

	public SpriteControls findNotTagsToSpriteControls(Object... o) {
		if (_closed) {
			return new SpriteControls();
		}
		SpriteControls controls = null;
		if (_sprites != null) {
			TArray<ISprite> sps = findNotTags(o);
			controls = new SpriteControls(sps);
		} else {
			controls = new SpriteControls();
		}
		return controls;
	}

	public ISprite[] getSprites() {
		if (_sprites == null) {
			return null;
		}
		return CollectionUtils.copyOf(this._sprites, this._size);
	}

	/**
	 * 删除符合指定条件的精灵并返回操作的集合
	 * 
	 * @param query
	 * @return
	 */
	public TArray<ISprite> remove(QueryEvent<ISprite> query) {
		TArray<ISprite> result = new TArray<ISprite>();
		for (int i = _sprites.length - 1; i > -1; i--) {
			ISprite sprite = _sprites[i];
			if (sprite != null) {
				if (query.hit(sprite)) {
					result.add(sprite);
					remove(i);
				}
			}
		}
		return result;
	}

	/**
	 * 查找符合指定条件的精灵并返回操作的集合
	 * 
	 * @param query
	 * @return
	 */
	public TArray<ISprite> find(QueryEvent<ISprite> query) {
		TArray<ISprite> result = new TArray<ISprite>();
		for (int i = _sprites.length - 1; i > -1; i--) {
			ISprite sprite = _sprites[i];
			if (sprite != null) {
				if (query.hit(sprite)) {
					result.add(sprite);
				}
			}
		}
		return result;
	}

	/**
	 * 删除指定条件的精灵并返回操作的集合
	 * 
	 * @param query
	 * @return
	 */
	public <T extends ISprite> TArray<T> delete(QueryEvent<T> query) {
		TArray<T> result = new TArray<T>();
		for (int i = _sprites.length - 1; i > -1; i--) {
			ISprite sprite = _sprites[i];
			if (sprite != null) {
				@SuppressWarnings("unchecked")
				T v = (T) sprite;
				if (query.hit(v)) {
					result.add(v);
					remove(i);
				}
			}
		}
		return result;
	}

	/**
	 * 查找符合指定条件的精灵并返回操作的集合
	 * 
	 * @param query
	 * @return
	 */
	public <T extends ISprite> TArray<T> select(QueryEvent<T> query) {
		TArray<T> result = new TArray<T>();
		for (int i = _sprites.length - 1; i > -1; i--) {
			ISprite sprite = _sprites[i];
			if (sprite != null) {
				@SuppressWarnings("unchecked")
				T v = (T) sprite;
				if (query.hit(v)) {
					result.add(v);
				}
			}
		}
		return result;
	}

	@Override
	public int size() {
		return this._size;
	}

	public int getHeight() {
		return _height;
	}

	public int getWidth() {
		return _width;
	}

	@Override
	public boolean isVisible() {
		return _visible;
	}

	@Override
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

	public String getName() {
		return _sprites_name;
	}

	@Override
	public boolean isEmpty() {
		return _size == 0 || _sprites == null;
	}

	@Override
	public String toString() {
		return super.toString() + " " + "[name=" + _sprites_name + ", total=" + size() + "]";
	}

	public boolean isClosed() {
		return _closed;
	}

	@Override
	public void close() {
		if (_closed) {
			return;
		}
		this._visible = false;
		this._newLineHeight = 0;
		for (ISprite spr : _sprites) {
			if (spr != null) {
				spr.close();
			}
		}
		clear();
		this._sprites = null;
		this._closed = true;
		LSystem.popSpritesPool(this);
	}
}
