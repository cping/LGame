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
import loon.LObject;
import loon.LRelease;
import loon.LSysException;
import loon.LSystem;
import loon.LTexture;
import loon.Screen;
import loon.Visible;
import loon.ZIndex;
import loon.action.ActionBind;
import loon.action.ActionControl;
import loon.action.PlaceActions;
import loon.action.collision.CollisionAction;
import loon.action.map.Side;
import loon.component.layout.Margin;
import loon.events.QueryEvent;
import loon.events.ResizeListener;
import loon.events.SysInput;
import loon.geom.Circle;
import loon.geom.DirtyRectList;
import loon.geom.Ellipse;
import loon.geom.Line;
import loon.geom.PointI;
import loon.geom.RectBox;
import loon.geom.Triangle2f;
import loon.geom.XY;
import loon.opengl.GLEx;
import loon.opengl.ShaderMask;
import loon.opengl.light.Light2D;
import loon.opengl.light.Light2D.LightType;
import loon.utils.CollectionUtils;
import loon.utils.IArray;
import loon.utils.IntArray;
import loon.utils.LayerSorter;
import loon.utils.MathUtils;
import loon.utils.ObjectSet;
import loon.utils.StringUtils;
import loon.utils.TArray;
import loon.utils.reply.Callback;

/**
 * 精灵精灵总父类，用来注册，控制，以及渲染所有精灵精灵（所有默认【不支持】触屏的精灵，被置于此。不过，
 * 当LNode系列精灵和SpriteBatchScreen合用时，也支持触屏.）
 * 
 */
public class Sprites extends PlaceActions implements Visible, ZIndex, IArray, LRelease {

	public static interface Created<T> {
		T make();
	}

	public static interface SpriteListener {

		public Sprites update(ISprite spr);

	}

	// 是否在整个桌面组件中使用光源
	private boolean _useLight = false;

	private Light2D _light;

	// 是否使用shadermask改变画面显示效果
	private boolean _useShaderMask = false;

	private ShaderMask _shaderMask;

	private final DirtyRectList _dirtyList = new DirtyRectList();

	private ISpritesShadow _spriteShadow;

	private Margin _margin;

	private ObjectSet<String> _collisionIgnoreStrings;
	
	private IntArray _collisionIgnoreTypes;
	
	private int _indexLayer;

	private boolean _createShadow;

	private boolean _sortableChildren;

	private boolean _isViewWindowSet = false, _limitViewWindows = false, _visible = true, _closed = false;

	private final static LayerSorter<ISprite> spriteLayerSorter = new LayerSorter<ISprite>();

	private final static SpriteSorter<ISprite> spriteXYSorter = new SpriteSorter<ISprite>();

	private int _currentPosHash = 1;

	private int _lastPosHash = 1;

	private int _viewX;

	private int _viewY;

	private int _viewWidth;

	private int _viewHeight;

	private int _size = 0;

	private int _sizeExpandCount = 1;

	private int _width = 0, _height = 0;

	private float _newLineHeight = -1f;

	private float _scrollX = 0f;

	private float _scrollY = 0f;

	private final String _sprites_name;

	private boolean _autoSortLayer;

	private boolean _checkAllCollision;

	private boolean _checkViewCollision;

	private SpriteListener _sprListerner = null;

	private ResizeListener<Sprites> _resizeListener;

	private CollisionAction<ISprite> _collisionActionListener;

	private RectBox _collViewSize;

	private TArray<ISprite> _collisionObjects;

	private Screen _screen;

	private SysInput _input;

	protected ISprite[] _sprites;

	public Sprites(Screen screen, int w, int h) {
		this(null, screen, w, h);
	}

	public Sprites(Screen screen, float width, float height) {
		this(null, screen, MathUtils.iceil(width), MathUtils.iceil(height));
	}

	public Sprites(Screen screen) {
		this(null, screen, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public Sprites(String name, Screen screen, float w, float h) {
		this(name, screen, MathUtils.iceil(w), MathUtils.iceil(h));
	}

	public Sprites(String name, Screen screen, int w, int h) {
		this._sortableChildren = this._visible = true;
		this._sprites = new ISprite[CollectionUtils.INITIAL_CAPACITY];
		this._sprites_name = StringUtils.isEmpty(name) ? "Sprites" + LSystem.getSpritesSize() : name;
		this._size = 0;
		this._sizeExpandCount = 1;
		this._currentPosHash = _lastPosHash = 1;
		this._newLineHeight = -1f;
		this.setScreen(screen);
		this.setSize(w, h);
		LSystem.pushSpritesPool(this);
	}

	/**
	 * 设定当前精灵管理器对应的屏幕
	 * 
	 * @param screen
	 * @return
	 */
	public Sprites setScreen(Screen screen) {
		this._screen = screen;
		this.setInput(screen);
		return this;
	}

	/**
	 * 设定当前精灵管理器对应的操作输入器
	 * 
	 * @param input
	 * @return
	 */
	public Sprites setInput(SysInput input) {
		this._input = input;
		return this;
	}

	/**
	 * 获得当前屏幕对应的操作输入器
	 * 
	 * @return
	 */
	public SysInput screenInput() {
		return this._input;
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
			if (this._width <= 0) {
				this._width = 1;
			}
			if (this._height <= 0) {
				this._height = 1;
			}
			if (this._viewWidth < this._width) {
				this._viewWidth = this._width;
			}
			if (this._viewHeight < this._height) {
				this._viewHeight = this._height;
			}
			this.resize(w, h, true);
		}
		return this;
	}

	/**
	 * 设定指定对象到图层最前
	 * 
	 * @param sprite
	 */
	public Sprites sendToFront(ISprite sprite) {
		if (_closed) {
			return this;
		}
		if (this._size <= 1 || this._sprites[0] == sprite) {
			return this;
		}
		if (_sprites[0] == sprite) {
			return this;
		}
		for (int i = 0; i < this._size; i++) {
			if (this._sprites[i] == sprite) {
				this._sprites = CollectionUtils.cut(this._sprites, i);
				this._sprites = CollectionUtils.expand(this._sprites, _sizeExpandCount, false);
				this._sprites[0] = sprite;
				if (_sortableChildren) {
					this.sortSprites();
				}
				break;
			}
		}
		return this;
	}

	/**
	 * 设定指定对象到图层最后
	 * 
	 * @param sprite
	 */
	public Sprites sendToBack(ISprite sprite) {
		if (_closed) {
			return this;
		}
		if (this._size <= 1 || this._sprites[this._size - 1] == sprite) {
			return this;
		}
		if (_sprites[this._size - 1] == sprite) {
			return this;
		}
		for (int i = 0; i < this._size; i++) {
			if (this._sprites[i] == sprite) {
				this._sprites = CollectionUtils.cut(this._sprites, i);
				this._sprites = CollectionUtils.expand(this._sprites, _sizeExpandCount, true);
				this._sprites[this._size - 1] = sprite;
				if (_sortableChildren) {
					this.sortSprites();
				}
				break;
			}
		}
		return this;
	}

	/**
	 * 按所在层级排序
	 * 
	 */
	public Sprites sortSprites() {
		if (this._closed) {
			return this;
		}
		if (this._size <= 1) {
			return this;
		}
		if (this._sprites.length != this._size) {
			final ISprite[] sprs = CollectionUtils.copyOf(this._sprites, this._size);
			spriteLayerSorter.sort(sprs);
			this._sprites = sprs;
		} else {
			spriteLayerSorter.sort(this._sprites);
		}
		return this;
	}

	public Sprites setSortableChildren(boolean v) {
		this._sortableChildren = v;
		return this;
	}

	public boolean isSortableChildren() {
		return this._sortableChildren;
	}

	/**
	 * 扩充当前集合容量
	 * 
	 * @param capacity
	 */
	private void expandCapacity(int capacity) {
		if (_sprites.length < capacity) {
			final ISprite[] bagArray = new ISprite[capacity];
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
			final ISprite[] newArray = new ISprite[this._size + capacity];
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
		final ISprite[] snapshot = this._sprites;
		final int size = this._size;
		for (int i = size - 1; i >= 0; i--) {
			ISprite child = snapshot[i];
			if (child != null) {
				RectBox rect = child.getCollisionBox();
				if (rect != null && rect.contains(x, y)) {
					return child;
				}
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
		final ISprite[] snapshot = this._sprites;
		final int size = this._size;
		for (int i = size - 1; i >= 0; i--) {
			ISprite child = snapshot[i];
			if (child != null) {
				String childName = child.getName();
				if (name.equals(childName)) {
					return child;
				}
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
		float maxX = 0;
		float maxY = 0;

		ISprite tag = null;
		final int size = this._size;
		final ISprite[] childs = this._sprites;

		if (size == 1) {
			ISprite cp = childs[0];
			if (cp != null && cp.getY() >= _newLineHeight) {
				maxX = cp.getX();
				maxY = cp.getY();
				tag = cp;
			}
		} else {
			for (int i = 0; i < size; i++) {
				ISprite c = childs[i];
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
		if (tag == null && size > 0) {
			tag = childs[size - 1];
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

	private void updateViewSize(ISprite sprite) {
		if (sprite == null) {
			return;
		}
		if (sprite.getWidth() > getWidth()) {
			if (_screen == null) {
				setViewWindow(_viewX, _viewY, MathUtils.iceil(MathUtils.max(sprite.getWidth(), LSystem.viewSize.width)),
						_height);
			} else {
				setViewWindow(_viewX, _viewY, MathUtils.iceil(MathUtils.max(sprite.getWidth(), _screen.getWidth())),
						_height);
			}
		}
		if (sprite.getHeight() > getHeight()) {
			if (_screen == null) {
				setViewWindow(_viewX, _viewY, _width,
						MathUtils.iceil(MathUtils.max(sprite.getHeight(), LSystem.viewSize.height)));
			} else {
				setViewWindow(_viewX, _viewY, _width,
						MathUtils.iceil(MathUtils.max(sprite.getHeight(), _screen.getHeight())));

			}
		}
	}

	/**
	 * 设定指定索引为指定精灵,并替换位置和层级
	 * 
	 * @param idx
	 * @param sprite
	 * @return
	 */
	public boolean set(int idx, ISprite sprite) {
		if (_closed) {
			return false;
		}
		if (sprite == null) {
			return false;
		}
		if (idx > this._size) {
			idx = this._size;
		}
		if (idx == this._size) {
			return this.add(sprite);
		} else if (idx > -1 && idx < this._size) {
			updateViewSize(sprite);
			final ISprite[] childs = this._sprites;
			ISprite dstSpr = childs[idx];
			if (dstSpr != null) {
				sprite.setLocation(dstSpr.getX(), dstSpr.getY());
				sprite.setLayer(dstSpr.getLayer());
				dstSpr.setState(State.REMOVED);
				if (dstSpr instanceof IEntity) {
					((IEntity) dstSpr).onDetached();
				}
			}
			childs[idx] = sprite;
			sprite.setSprites(this);
			if (!contains(sprite)) {
				this._sprites = childs;
				sprite.setState(State.ADDED);
				if (sprite instanceof IEntity) {
					((IEntity) sprite).onAttached();
				}
			}
			if (_sortableChildren) {
				sortSprites();
			}
			return true;
		}
		return false;
	}

	/**
	 * 在指定索引新增一个精灵,其余精灵顺序排后
	 * 
	 * @param idx
	 * @param sprite
	 * @return
	 */
	public boolean addAt(int idx, ISprite sprite) {
		if (_closed) {
			return false;
		}
		if (sprite == null) {
			return false;
		}
		if (contains(sprite)) {
			return set(idx, sprite);
		}
		if (idx > this._size) {
			idx = this._size;
		}
		if (idx == this._size) {
			return this.add(sprite);
		} else {
			updateViewSize(sprite);
			ISprite[] childs = this._sprites;
			final ISprite[] oldStartChilds = CollectionUtils.copyOf(childs, idx + 1);
			final ISprite[] oldEndChilds = CollectionUtils.copyOf(childs, idx, this._size);
			oldStartChilds[idx] = sprite;
			childs = CollectionUtils.concat(oldStartChilds, oldEndChilds);
			this._sprites = childs;
			if (++this._size >= this._sprites.length) {
				expandCapacity((_size + 1) * 2);
			}
			sprite.setSprites(this);
			if (_sortableChildren) {
				sortSprites();
			}
			sprite.setState(State.ADDED);
			if (sprite instanceof IEntity) {
				((IEntity) sprite).onAttached();
			}
		}
		boolean result = _sprites[idx] != null;
		return result;
	}

	public Sprites addAt(ISprite child, float x, float y) {
		if (_closed) {
			return this;
		}
		if (child != null) {
			child.setLocation(x, y);
			add(child);
		}
		return this;
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

	public ISprite getRandomSprite() {
		return getRandomSprite(0, _size);
	}

	public ISprite getRandomSprite(int min, int max) {
		if (_closed) {
			return null;
		}
		min = MathUtils.max(0, min);
		max = MathUtils.min(max, _size);
		return _sprites[MathUtils.nextInt(min, max)];
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
		updateViewSize(sprite);
		sprite.setSprites(this);
		if (this._size == this._sprites.length) {
			expandCapacity((_size + 1) * 2);
		}
		final boolean result = (_sprites[_size++] = sprite) != null;
		if (_sortableChildren) {
			sortSprites();
		}
		sprite.setState(State.ADDED);
		if (sprite instanceof IEntity) {
			((IEntity) sprite).onAttached();
		}
		return result;
	}

	/**
	 * 顺序添加精灵
	 * 
	 * @param sprite
	 * @return
	 */
	public Sprites append(ISprite sprite) {
		add(sprite);
		return this;
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
		final int len = tags.length;
		final ISprite[] childs = this._sprites;
		for (int j = 0; j < len; j++) {
			final Object tag = tags[j];
			if (tag != null) {
				for (int i = size - 1; i > -1; i--) {
					ISprite child = childs[i];
					if (child != null && (child.getTag() == tag || tag.equals(child.getTag()))) {
						list.add(child);
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
		final int len = tags.length;
		final ISprite[] childs = this._sprites;
		for (int j = 0; j < len; j++) {
			final Object tag = tags[j];
			if (tag != null) {
				for (int i = size - 1; i > -1; i--) {
					ISprite child = childs[i];
					if (child != null && !tag.equals(child.getTag())) {
						list.add(child);
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
		final int len = names.length;
		final ISprite[] childs = this._sprites;
		for (int j = 0; j < len; j++) {
			final String name = names[j];
			if (name != null) {
				for (int i = size - 1; i > -1; i--) {
					ISprite child = childs[i];
					if (child != null && name.equals(child.getName())) {
						list.add(child);
					}
				}
			}
		}
		return list;
	}

	public TArray<ISprite> findNameContains(String... names) {
		if (_closed) {
			return null;
		}
		final TArray<ISprite> list = new TArray<ISprite>();
		final int size = this._size;
		final int len = names.length;
		final ISprite[] childs = this._sprites;
		for (int j = 0; j < len; j++) {
			final String name = names[j];
			if (name != null) {
				for (int i = size - 1; i > -1; i--) {
					ISprite child = childs[i];
					if (child != null) {
						String childName = child.getName();
						if (childName != null && childName.contains(name)) {
							list.add(child);
						}
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
		final TArray<ISprite> list = new TArray<ISprite>();
		final int size = this._size;
		final int len = names.length;
		final ISprite[] childs = this._sprites;
		for (int j = 0; j < len; j++) {
			final String name = names[j];
			if (name != null) {
				for (int i = size - 1; i > -1; i--) {
					ISprite child = childs[i];
					if (child != null && name.equals(child.getName())) {
						list.add(child);
					}
				}
			}
		}
		return list;
	}

	/**
	 * 查找符合的Flag整型对象
	 * 
	 * @param flag
	 * @return
	 */
	public TArray<ISprite> findFlagTypes(int flag) {
		if (_closed) {
			return null;
		}
		final TArray<ISprite> list = new TArray<ISprite>();
		final int size = this._size;
		final ISprite[] childs = this._sprites;
		for (int i = size - 1; i > -1; i--) {
			ISprite child = childs[i];
			if (child != null && child.getFlagType() == flag) {
				list.add(child);
			}
		}
		return list;
	}

	/**
	 * 查找符合的Flag字符对象
	 * 
	 * @param flag
	 * @return
	 */
	public TArray<ISprite> findFlagObjects(String flag) {
		if (_closed) {
			return null;
		}
		final TArray<ISprite> list = new TArray<ISprite>();
		if (flag == null) {
			return list;
		}
		final int size = this._size;
		final ISprite[] childs = this._sprites;
		for (int i = size - 1; i > -1; i--) {
			ISprite child = childs[i];
			if (child != null && flag.equals(child.getObjectFlag())) {
				list.add(child);
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
		final int size = this._size;
		final ISprite[] childs = _sprites;
		for (int i = size - 1; i > -1; i--) {
			final ISprite sp = childs[i];
			boolean exist = (sp != null);
			if (exist && (sprite == sp || sprite.equals(sp))) {
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
		if (_closed) {
			return null;
		}
		if (_sprites == null) {
			return null;
		}
		final TArray<ISprite> sprites = new TArray<ISprite>();
		final int size = this._size;
		final ISprite[] childs = _sprites;
		for (int i = size - 1; i > -1; i--) {
			final ISprite sp = childs[i];
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
			return null;
		}
		return contains(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
	}

	/**
	 * 交换两个精灵位置
	 * 
	 * @param first
	 * @param second
	 * @return
	 */
	public Sprites swapSprite(int first, int second) {
		if (_closed) {
			return this;
		}
		if (first == second) {
			return this;
		}
		if (first >= _size) {
			throw new LSysException("first can't be >= size: " + first + " >= " + _size);
		}
		if (second >= _size) {
			throw new LSysException("second can't be >= size: " + second + " >= " + _size);
		}
		final ISprite[] sprs = this._sprites;
		final ISprite firstValue = sprs[first];
		sprs[first] = sprs[second];
		sprs[second] = firstValue;
		return this;
	}

	/**
	 * 交换两个精灵位置
	 * 
	 * @param first
	 * @param second
	 * @return
	 */
	public Sprites swapSprite(ISprite first, ISprite second) {
		if (_closed) {
			return this;
		}
		if ((first == null && second == null) || (first == second)) {
			return this;
		}
		int fi = -1;
		int bi = -1;
		final int size = this._size;
		final ISprite[] sprs = this._sprites;
		for (int i = 0; i < size; i++) {
			final ISprite spr = sprs[i];
			if (spr == first) {
				fi = i;
			}
			if (spr == second) {
				bi = i;
			}
			if (fi != -1 && bi != -1) {
				break;
			}
		}
		if (fi != -1 && bi != -1) {
			return swapSprite(fi, bi);
		}
		return this;
	}

	public Sprites swapSprite(String first, String second) {
		if (_closed) {
			return this;
		}
		if ((first == null || second == null) || (first.equals(second))) {
			return this;
		}
		int fi = -1;
		int bi = -1;
		final int size = this._size;
		final ISprite[] sprs = this._sprites;
		for (int i = 0; i < size; i++) {
			final ISprite spr = sprs[i];
			if (spr != null && spr.getName().equals(first)) {
				fi = i;
			}
			if (spr != null && spr.getName().equals(second)) {
				bi = i;
			}
			if (fi != -1 && bi != -1) {
				break;
			}
		}
		if (fi != -1 && bi != -1) {
			return swapSprite(fi, bi);
		}
		return this;
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
		if (_closed) {
			return null;
		}
		if (_sprites == null) {
			return null;
		}
		final TArray<ISprite> sprites = new TArray<ISprite>();
		final int size = this._size;
		final ISprite[] sprs = this._sprites;
		for (int i = 0; i < size; i++) {
			ISprite sp = sprs[i];
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
			return null;
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
		if (_size == 0) {
			return null;
		}
		ISprite removed = this._sprites[index];
		if (removed != null) {
			removed.setState(State.REMOVED);
			if (removed instanceof IEntity) {
				((IEntity) removed).onDetached();
			}
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
	public Sprites removeAll() {
		if (_closed) {
			return this;
		}
		if (_size != 0) {
			clear();
			this._sprites = new ISprite[0];
		}
		return this;
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
		if (_size == 0) {
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
				if (spr instanceof IEntity) {
					((IEntity) spr).onDetached();
				}
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
	 * 删除alpha大于或小于指定范围的精灵
	 * 
	 * @param spr
	 * @param more
	 * @param limit
	 * @return
	 */
	public boolean removeWhenAlpha(ISprite spr, boolean more, float limit) {
		if (_size == 0) {
			return false;
		}
		if (spr != null && (more ? spr.getAlpha() >= limit : spr.getAlpha() <= limit)) {
			return remove(spr);
		}
		return false;
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
		if (_size == 0) {
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
				if (spr instanceof IEntity) {
					((IEntity) spr).onDetached();
				}
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
	public Sprites remove(int startIndex, int endIndex) {
		if (_closed) {
			return this;
		}
		if (_size == 0) {
			return this;
		}
		if (endIndex - startIndex > 0) {
			final int size = this._size;
			final ISprite[] childs = this._sprites;
			for (int i = startIndex; i < endIndex && i < size; i++) {
				ISprite spr = childs[i];
				if (spr != null) {
					spr.setState(State.REMOVED);
					if (spr instanceof IEntity) {
						((IEntity) spr).onDetached();
					}
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
		return this;
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

	public void onTriggerCollisions() {
		if (_checkAllCollision) {
			checkAllCollisionObjects();
		} else if (_checkViewCollision) {
			checkViewCollisionObjects();
		}
	}

	public Sprites collidable() {
		return setCheckAllCollision(true);
	}

	public Sprites collidableView() {
		return setCheckViewCollision(true);
	}

	public boolean isCheckAllCollision() {
		return _checkAllCollision;
	}

	public Sprites setCheckAllCollision(boolean c) {
		this._checkAllCollision = c;
		return this;
	}

	protected void checkAllCollisionObjects() {
		if (!_checkAllCollision) {
			return;
		}
		final ISprite[] sprs = this._sprites;
		final int size = sprs.length;
		for (int i = size - 1; i > -1; i--) {
			final ISprite src = sprs[i];
			for (int j = 0; j < size; j++) {
				if (i != j) {
					final ISprite dst = sprs[j];
					if (src != null && dst != null && src != dst && dst.isVisible() && src.isVisible()) {
						final RectBox srcCollision = src.getCollisionBox();
						final RectBox dstCollision = dst.getCollisionBox();
						if (srcCollision.collided(dstCollision)) {
							onTriggerCollision(src, dst);
						}
					}
				}
			}
		}
	}

	public boolean isCheckViewCollision() {
		return _checkViewCollision;
	}

	public Sprites setCheckViewCollision(boolean c) {
		this._checkViewCollision = c;
		return this;
	}

	protected void checkViewCollisionObjects() {
		this.checkViewCollisionObjects(0f, 0f);
	}

	protected void checkViewCollisionObjects(float x, float y) {
		if (!_checkViewCollision) {
			return;
		}
		float minX, minY, maxX, maxY;
		if (this._isViewWindowSet) {
			minX = x + this._viewX;
			maxX = minX + this._viewWidth;
			minY = y + this._viewY;
			maxY = minY + this._viewHeight;
		} else {
			minX = x;
			maxX = x + this._width;
			minY = y;
			maxY = y + this._height;
		}
		if (_collViewSize == null) {
			_collViewSize = new RectBox(minX, minY, maxX, maxY);
		} else {
			_collViewSize.setBounds(minX, minY, maxX, maxY);
		}
		if (_collisionObjects == null) {
			_collisionObjects = new TArray<ISprite>();
		} else {
			_collisionObjects.clear();
		}
		final ISprite[] sprs = this._sprites;
		int size = sprs.length;
		for (int i = size - 1; i > -1; i--) {
			final ISprite spr = sprs[i];
			if (spr != null && spr.isVisible()) {
				if (_collViewSize.collided(spr.getCollisionBox())) {
					_collisionObjects.add(spr);
				}
			}
		}
		size = _collisionObjects.size;
		for (int i = size - 1; i > -1; i--) {
			final ISprite src = _collisionObjects.get(i);
			for (int j = 0; j < size; j++) {
				if (i != j) {
					final ISprite dst = _collisionObjects.get(j);
					if (src != null && dst != null && src != dst && dst.isVisible() && src.isVisible()) {
						final RectBox srcCollision = src.getCollisionBox();
						final RectBox dstCollision = dst.getCollisionBox();
						if (srcCollision.collided(dstCollision)) {
							onTriggerCollision(src, dst);
						}
					}
				}
			}
		}
	}

	private void onTriggerCollision(final ISprite spr, final ISprite dst) {
		if (spr == null || dst == null) {
			return;
		}
		if (checkCollisionSkip(spr, dst)) {
			return;
		}
		final int dir = Side.getCollisionSide(spr.getCollisionBox(), dst.getCollisionBox());
		spr.onCollision(dst, dir);
		if (_collisionActionListener != null) {
			_collisionActionListener.onCollision(spr, dst, dir);
		}
	}

	private boolean checkCollisionSkip(final ISprite spr, final ISprite dst) {
		if (_collisionIgnoreTypes != null) {
			if (_collisionIgnoreTypes.contains(spr.getFlagType())
					|| _collisionIgnoreTypes.contains(dst.getFlagType())) {
				return true;
			}
		}
		if (_collisionIgnoreStrings != null) {
			if (_collisionIgnoreStrings.contains(spr.getObjectFlag())
					|| _collisionIgnoreStrings.contains(dst.getObjectFlag())) {
				return true;
			}
		}
		return false;
	}

	public Sprites addCollisionIgnoreType(int t) {
		if (_collisionIgnoreTypes == null) {
			_collisionIgnoreTypes = new IntArray();
		}
		if (!_collisionIgnoreTypes.contains(t)) {
			_collisionIgnoreTypes.add(t);
		}
		return this;
	}

	public boolean removeCollisionIgnoreType(int t) {
		if (_collisionIgnoreTypes == null) {
			_collisionIgnoreTypes = new IntArray();
		}
		return _collisionIgnoreTypes.removeValue(t);
	}

	public Sprites addCollisionIgnoreString(String t) {
		if (_collisionIgnoreStrings == null) {
			_collisionIgnoreStrings = new ObjectSet<String>();
		}
		_collisionIgnoreStrings.add(t);
		return this;
	}

	public boolean removeCollisionIgnoreString(String t) {
		if (_collisionIgnoreStrings == null) {
			_collisionIgnoreStrings = new ObjectSet<String>();
		}
		return _collisionIgnoreStrings.remove(t);
	}

	public Sprites triggerCollision(CollisionAction<ISprite> c) {
		return triggerAllCollision(c);
	}

	public Sprites triggerAllCollision(CollisionAction<ISprite> c) {
		setCheckAllCollision(c != null);
		setCollisionAction(c);
		return this;
	}

	public Sprites viewCollision(CollisionAction<ISprite> c) {
		return triggerViewCollision(c);
	}

	public Sprites triggerViewCollision(CollisionAction<ISprite> c) {
		setCheckViewCollision(c != null);
		setCollisionAction(c);
		return this;
	}

	public Sprites setCollisionAction(CollisionAction<ISprite> c) {
		this._collisionActionListener = c;
		return this;
	}

	public CollisionAction<ISprite> getCollisionAction() {
		return _collisionActionListener;
	}

	public boolean checkAdd(ISprite spr, QueryEvent<ISprite> e) {
		if (e == null) {
			return false;
		}
		if (e.hit(spr)) {
			return add(spr);
		}
		return false;
	}

	public boolean checkRemove(ISprite spr, QueryEvent<ISprite> e) {
		if (e == null) {
			return false;
		}
		if (e.hit(spr)) {
			return remove(spr);
		}
		return false;
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
		final boolean listerner = (_sprListerner != null);
		final ISprite[] childs = _sprites;
		final int size = this._size;
		for (int i = size - 1; i > -1; i--) {
			final ISprite child = childs[i];
			if (child != null) {
				try {
					child.update(elapsedTime);
					if (listerner) {
						_sprListerner.update(child);
					}
					if (_autoSortLayer) {
						_currentPosHash = LSystem.unite(_currentPosHash, child.getX());
						_currentPosHash = LSystem.unite(_currentPosHash, child.getY());
						_currentPosHash = LSystem.unite(_currentPosHash, child.getOffsetX());
						_currentPosHash = LSystem.unite(_currentPosHash, child.getOffsetY());
					}
				} catch (Throwable cause) {
					LSystem.error("Sprites update() exception", cause);
				}
			}
		}
		if (_autoSortLayer) {
			if (size <= 1) {
				return;
			}
			if (_currentPosHash != _lastPosHash) {
				if (childs.length != size) {
					final ISprite[] sprs = CollectionUtils.copyOf(childs, size);
					spriteXYSorter.sort(sprs);
					this._sprites = sprs;
				} else {
					spriteXYSorter.sort(childs);
					this._sprites = childs;
				}
				_lastPosHash = _currentPosHash;
			}
		}
		onTriggerCollisions();
	}

	public Light2D createGlobalLight(LightType lt) {
		if (lt == null) {
			this._useLight = false;
			return null;
		}
		if (this._light == null) {
			this._light = new Light2D(lt);
		} else {
			this._light.updateLightType(lt);
		}
		this._useLight = true;
		return this._light;
	}

	public boolean isGlobalLight() {
		return this._useLight;
	}

	public Light2D getGlobalLight() {
		return _light;
	}

	public boolean isShaderMask() {
		return this._useShaderMask;
	}

	public ShaderMask getShaderMask() {
		return this._shaderMask;
	}

	public Sprites setShaderMask(ShaderMask mask) {
		this._shaderMask = mask;
		this._useShaderMask = (this._shaderMask != null);
		return this;
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
		final ISprite[] childs = _sprites;
		final int size = this._size;
		if (_useLight && !_light.isClosed()) {
			_light.setAutoTouchTimer(_screen.getTouchX(), _screen.getTouchY(), _screen.getCurrentTimer());
			final ShaderMask lightMask = _light.getMask();
			lightMask.pushBatch(g);
			for (int i = 0; i < size; i++) {
				final ISprite spr = childs[i];
				if (spr != null && spr.isVisible()) {
					if (_limitViewWindows) {
						spriteX = minX + spr.getX();
						spriteY = minY + spr.getY();
						spriteWidth = spr.getWidth();
						spriteHeight = spr.getHeight();
						if (spriteX + spriteWidth < minX || spriteX > maxX || spriteY + spriteHeight < minY
								|| spriteY > maxY) {
							continue;
						}
					}
					if (_createShadow && spr.showShadow()) {
						_spriteShadow.drawShadow(g, spr, 0f, 0f);
					}
					spr.createUI(g);
				}
			}
			lightMask.popBatch(g);
		} else {
			if (_useShaderMask) {
				_shaderMask.pushBatch(g);
			}
			for (int i = 0; i < size; i++) {
				final ISprite spr = childs[i];
				if (spr != null && spr.isVisible()) {
					if (_limitViewWindows) {
						spriteX = minX + spr.getX();
						spriteY = minY + spr.getY();
						spriteWidth = spr.getWidth();
						spriteHeight = spr.getHeight();
						if (spriteX + spriteWidth < minX || spriteX > maxX || spriteY + spriteHeight < minY
								|| spriteY > maxY) {
							continue;
						}
					}
					if (_createShadow && spr.showShadow()) {
						_spriteShadow.drawShadow(g, spr, 0f, 0f);
					}
					spr.createUI(g);
				}
			}
			if (_useShaderMask) {
				_shaderMask.popBatch(g);
			}
		}
	}

	public void paintPos(final GLEx g, final float offsetX, final float offsetY) {
		if (!_visible || _closed) {
			return;
		}
		final ISprite[] childs = _sprites;
		final int size = this._size;
		if (_useLight && !_light.isClosed()) {
			_light.setAutoTouchTimer(_screen.getTouchX(), _screen.getTouchY(), _screen.getCurrentTimer());
			final ShaderMask lightMask = _light.getMask();
			lightMask.pushBatch(g);
			for (int i = 0; i < size; i++) {
				final ISprite spr = childs[i];
				if (spr != null && spr.isVisible()) {
					if (_createShadow && spr.showShadow()) {
						_spriteShadow.drawShadow(g, spr, offsetX, offsetY);
					}
					spr.createUI(g, offsetX, offsetY);
				}
			}
			lightMask.popBatch(g);
		} else {
			if (_useShaderMask) {
				_shaderMask.pushBatch(g);
			}
			for (int i = 0; i < size; i++) {
				final ISprite spr = childs[i];
				if (spr != null && spr.isVisible()) {
					if (_createShadow && spr.showShadow()) {
						_spriteShadow.drawShadow(g, spr, offsetX, offsetY);
					}
					spr.createUI(g, offsetX, offsetY);
				}
			}
			if (_useShaderMask) {
				_shaderMask.popBatch(g);
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
		if (!_visible || _closed) {
			return;
		}

		final float newScrollX = _scrollX;
		final float newScrollY = _scrollY;

		final int drawWidth = _width;
		final int drawHeight = _height;

		final float startX = MathUtils.scroll(newScrollX, drawWidth);
		final float startY = MathUtils.scroll(newScrollY, drawHeight);

		final boolean update = (startX != 0f || startY != 0f);

		if (update) {
			g.translate(startX, startY);
		}
		float minX, minY, maxX, maxY;
		if (this._isViewWindowSet) {
			minX = x + this._viewX;
			maxX = minX + this._viewWidth;
			minY = y + this._viewY;
			maxY = minY + this._viewHeight;
		} else {
			minX = x;
			maxX = x + this._width;
			minY = y;
			maxY = y + this._height;
		}

		final boolean offset = (minX != 0 || minY != 0);
		if (offset) {
			g.translate(minX, minY);
		}

		final ISprite[] childs = _sprites;
		final int size = this._size;

		if (_useLight && !_light.isClosed()) {
			_light.setAutoTouchTimer(_screen.getTouchX(), _screen.getTouchY(), _screen.getCurrentTimer());
			final ShaderMask lightMask = _light.getMask();
			lightMask.pushBatch(g);
			for (int i = 0; i < size; i++) {
				final ISprite spr = childs[i];
				if (spr != null && spr.isVisible()) {
					if (_limitViewWindows) {
						int layerX = spr.x();
						int layerY = spr.y();
						float layerWidth = spr.getWidth() + 1;
						float layerHeight = spr.getHeight() + 1;
						if (layerX + layerWidth < minX || layerX > maxX || layerY + layerHeight < minY
								|| layerY > maxY) {
							continue;
						}
					}
					if (_createShadow && spr.showShadow()) {
						_spriteShadow.drawShadow(g, spr, 0f, 0f);
					}
					spr.createUI(g);
				}
			}
			lightMask.popBatch(g);
		} else {
			if (_useShaderMask) {
				_shaderMask.pushBatch(g);
			}
			for (int i = 0; i < size; i++) {
				final ISprite spr = childs[i];
				if (spr != null && spr.isVisible()) {
					if (_limitViewWindows) {
						int layerX = spr.x();
						int layerY = spr.y();
						float layerWidth = spr.getWidth() + 1;
						float layerHeight = spr.getHeight() + 1;
						if (layerX + layerWidth < minX || layerX > maxX || layerY + layerHeight < minY
								|| layerY > maxY) {
							continue;
						}
					}
					if (_createShadow && spr.showShadow()) {
						_spriteShadow.drawShadow(g, spr, 0f, 0f);
					}
					spr.createUI(g);
				}
			}
			if (_useShaderMask) {
				_shaderMask.popBatch(g);
			}
		}
		if (offset) {
			g.translate(-minX, -minY);
		}
		if (update) {
			g.translate(-startX, -startY);
		}
	}

	public Sprites addEntityGroup(int count) {
		for (int i = 0; i < count; i++) {
			add(new Entity());
		}
		return this;
	}

	public Sprites addEntityGroup(LTexture tex, int count) {
		for (int i = 0; i < count; i++) {
			add(new Entity(tex));
		}
		return this;
	}

	public Sprites addEntityGroup(String path, int count) {
		for (int i = 0; i < count; i++) {
			add(new Entity(path));
		}
		return this;
	}

	public Sprites addEntityGroup(Created<? extends IEntity> s, int count) {
		if (s == null) {
			return this;
		}
		for (int i = 0; i < count; i++) {
			add(s.make());
		}
		return this;
	}

	public Sprites addSpriteGroup(int count) {
		for (int i = 0; i < count; i++) {
			add(new Sprite());
		}
		return this;
	}

	public Sprites addSpriteGroup(LTexture tex, int count) {
		for (int i = 0; i < count; i++) {
			add(new Sprite(tex));
		}
		return this;
	}

	public Sprites addSpriteGroup(String path, int count) {
		for (int i = 0; i < count; i++) {
			add(new Sprite(path));
		}
		return this;
	}

	public Sprites addSpriteGroup(Created<? extends ISprite> s, int count) {
		if (s == null) {
			return this;
		}
		for (int i = 0; i < count; i++) {
			add(s.make());
		}
		return this;
	}

	public ISprite addSprite(final String name, final TArray<TComponent<ISprite>> comps) {
		Sprite newSprite = new Sprite();
		newSprite.setName(name);
		for (int i = 0; i < comps.size; i++) {
			TComponent<ISprite> t = comps.get(i);
			if (t != null) {
				newSprite.addComponent(t);
			}
		}
		add(newSprite);
		return newSprite;
	}

	public ISprite addSprite(final String name, final String imagePath, final TArray<TComponent<ISprite>> comps) {
		Sprite newSprite = new Sprite(imagePath);
		newSprite.setName(name);
		for (int i = 0; i < comps.size; i++) {
			TComponent<ISprite> t = comps.get(i);
			if (t != null) {
				newSprite.addComponent(t);
			}
		}
		add(newSprite);
		return newSprite;
	}

	public ISprite addSprite(final String name, final LTexture tex, final TArray<TComponent<ISprite>> comps) {
		Sprite newSprite = new Sprite(tex);
		newSprite.setName(name);
		for (int i = 0; i < comps.size; i++) {
			TComponent<ISprite> t = comps.get(i);
			if (t != null) {
				newSprite.addComponent(t);
			}
		}
		add(newSprite);
		return newSprite;
	}

	@SuppressWarnings("unchecked")
	public ISprite addSprite(final String name, final TComponent<ISprite>... comps) {
		Sprite newSprite = new Sprite();
		newSprite.setName(name);
		for (int i = 0; i < comps.length; i++) {
			TComponent<ISprite> t = comps[i];
			if (t != null) {
				newSprite.addComponent(t);
			}
		}
		add(newSprite);
		return newSprite;
	}

	@SuppressWarnings("unchecked")
	public ISprite addSprite(final String name, final String imagePath, final TComponent<ISprite>... comps) {
		Sprite newSprite = new Sprite(imagePath);
		newSprite.setName(name);
		for (int i = 0; i < comps.length; i++) {
			TComponent<ISprite> t = comps[i];
			if (t != null) {
				newSprite.addComponent(t);
			}
		}
		add(newSprite);
		return newSprite;
	}

	@SuppressWarnings("unchecked")
	public ISprite addSprite(final String name, final LTexture tex, final TComponent<ISprite>... comps) {
		Sprite newSprite = new Sprite(tex);
		newSprite.setName(name);
		for (int i = 0; i < comps.length; i++) {
			TComponent<ISprite> t = comps[i];
			if (t != null) {
				newSprite.addComponent(t);
			}
		}
		add(newSprite);
		return newSprite;
	}

	public IEntity addEntity(final String name, final TArray<TComponent<IEntity>> comps) {
		Entity newEntity = new Entity();
		newEntity.setName(name);
		for (int i = 0; i < comps.size; i++) {
			TComponent<IEntity> t = comps.get(i);
			if (t != null) {
				newEntity.addComponent(t);
			}
		}
		add(newEntity);
		return newEntity;
	}

	public IEntity addEntity(final String name, final String imagePath, final TArray<TComponent<IEntity>> comps) {
		Entity newEntity = new Entity(imagePath);
		newEntity.setName(name);
		for (int i = 0; i < comps.size; i++) {
			TComponent<IEntity> t = comps.get(i);
			if (t != null) {
				newEntity.addComponent(t);
			}
		}
		add(newEntity);
		return newEntity;
	}

	public IEntity addEntity(final String name, final LTexture tex, final TArray<TComponent<IEntity>> comps) {
		Entity newEntity = new Entity(tex);
		newEntity.setName(name);
		for (int i = 0; i < comps.size; i++) {
			TComponent<IEntity> t = comps.get(i);
			if (t != null) {
				newEntity.addComponent(t);
			}
		}
		add(newEntity);
		return newEntity;
	}

	@SuppressWarnings("unchecked")
	public IEntity addEntity(final String name, final TComponent<IEntity>... comps) {
		Entity newEntity = new Entity();
		newEntity.setName(name);
		for (int i = 0; i < comps.length; i++) {
			TComponent<IEntity> t = comps[i];
			if (t != null) {
				newEntity.addComponent(t);
			}
		}
		add(newEntity);
		return newEntity;
	}

	@SuppressWarnings("unchecked")
	public IEntity addEntity(final String name, final String imagePath, final TComponent<IEntity>... comps) {
		Entity newEntity = new Entity(imagePath);
		newEntity.setName(name);
		for (int i = 0; i < comps.length; i++) {
			TComponent<IEntity> t = comps[i];
			if (t != null) {
				newEntity.addComponent(t);
			}
		}
		add(newEntity);
		return newEntity;
	}

	@SuppressWarnings("unchecked")
	public IEntity addEntity(final String name, final LTexture tex, final TComponent<IEntity>... comps) {
		Entity newEntity = new Entity(tex);
		newEntity.setName(name);
		for (int i = 0; i < comps.length; i++) {
			TComponent<IEntity> t = comps[i];
			if (t != null) {
				newEntity.addComponent(t);
			}
		}
		add(newEntity);
		return newEntity;
	}

	public float getX() {
		return _viewX;
	}

	public float getY() {
		return _viewY;
	}

	public float getStageX() {
		return (getX() - getScreenX());
	}

	public float getStageY() {
		return (getX() - getScreenX());
	}

	/**
	 * 设定精灵集合在屏幕中的位置与大小
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public Sprites setViewWindow(int x, int y, int width, int height) {
		this._isViewWindowSet = true;
		this._viewX = x;
		this._viewY = y;
		this._viewWidth = width;
		this._viewHeight = height;
		return this;
	}

	/**
	 * 设定精灵集合在屏幕中的位置
	 * 
	 * @param x
	 * @param y
	 */
	public Sprites setLocation(int x, int y) {
		this._isViewWindowSet = true;
		this._viewX = x;
		this._viewY = y;
		if (this._viewWidth <= 0) {
			this._viewWidth = getWidth();
		}
		if (this._viewHeight <= 0) {
			this._viewHeight = getHeight();
		}
		return this;
	}

	/**
	 * 创建对应当前精灵集合的精灵控制器
	 * 
	 * @return
	 */
	public SpriteControls createSpriteControls() {
		if (_closed) {
			return new SpriteControls();
		}
		SpriteControls controls = null;
		if (_sprites != null && _sprites.length > 0 && _size > 0) {
			controls = new SpriteControls(_sprites);
		} else {
			controls = new SpriteControls();
		}
		return controls;
	}

	public SpriteControls controls() {
		return createSpriteControls();
	}

	public SpriteControls createSpriteControls(TArray<ISprite> sprites) {
		if (sprites == null || sprites.size == 0) {
			return createSpriteControls();
		}
		return new SpriteControls(sprites);
	}

	public SpriteControls controls(TArray<ISprite> sprites) {
		return createSpriteControls(sprites);
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

	public SpriteControls findNameContainsToSpriteControls(String... names) {
		if (_closed) {
			return new SpriteControls();
		}
		SpriteControls controls = null;
		if (_sprites != null) {
			TArray<ISprite> sps = findNameContains(names);
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

	/**
	 * 获得当前精灵集合中的全部精灵
	 * 
	 * @return
	 */
	public ISprite[] getSprites() {
		if (_closed) {
			return null;
		}
		if (_sprites == null) {
			return null;
		}
		return CollectionUtils.copyOf(this._sprites, this._size);
	}

	/**
	 * 获得当前精灵集合中的全部精灵
	 * 
	 * @return
	 */
	public TArray<ISprite> getSpritesArray() {
		if (_closed) {
			return null;
		}
		if (_sprites == null) {
			return null;
		}
		final TArray<ISprite> result = new TArray<ISprite>();
		int size = _sprites.length;
		for (int i = size - 1; i > -1; i--) {
			ISprite spr = _sprites[i];
			if (spr != null) {
				result.add(spr);
			}
		}
		return result;
	}

	/**
	 * 删除符合指定条件的精灵并返回操作的集合
	 * 
	 * @param query
	 * @return
	 */
	public TArray<ISprite> remove(QueryEvent<ISprite> query) {
		if (_closed || query == null) {
			return new TArray<ISprite>();
		}
		final TArray<ISprite> result = new TArray<ISprite>();
		final int size = _size;
		final ISprite[] childs = _sprites;
		for (int i = size - 1; i > -1; i--) {
			final ISprite sprite = childs[i];
			if (sprite != null && query.hit(sprite)) {
				result.add(sprite);
				remove(i);
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
		if (_closed || query == null) {
			return new TArray<ISprite>();
		}
		final TArray<ISprite> result = new TArray<ISprite>();
		final int size = _size;
		final ISprite[] childs = _sprites;
		for (int i = size - 1; i > -1; i--) {
			final ISprite sprite = childs[i];
			if (sprite != null && query.hit(sprite)) {
				result.add(sprite);
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
	public TArray<ISprite> delete(QueryEvent<ISprite> query) {
		if (_closed || query == null) {
			return new TArray<ISprite>();
		}
		final TArray<ISprite> result = new TArray<ISprite>();
		final int size = _size;
		final ISprite[] childs = _sprites;
		for (int i = size - 1; i > -1; i--) {
			ISprite sprite = childs[i];
			if (sprite != null && query.hit(sprite)) {
				result.add(sprite);
				remove(i);
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
	public TArray<ISprite> select(QueryEvent<ISprite> query) {
		if (_closed || query == null) {
			return new TArray<ISprite>();
		}
		final TArray<ISprite> result = new TArray<ISprite>();
		final int size = _size;
		final ISprite[] childs = _sprites;
		for (int i = size - 1; i > -1; i--) {
			ISprite sprite = childs[i];
			if (sprite != null && query.hit(sprite)) {
				result.add(sprite);
			}
		}
		return result;
	}

	private void addRect(TArray<RectBox> rects, RectBox child) {
		if (_closed) {
			return;
		}
		if (child.width > 1 && child.height > 1) {
			if (!rects.contains(child)) {
				rects.add(child);
			}
		}
	}

	private void addAllRect(TArray<RectBox> rects, ISprite spr) {
		if (_closed) {
			return;
		}
		if (spr instanceof Entity) {
			if (spr.isContainer()) {
				final Entity ns = (Entity) spr;
				final TArray<IEntity> childs = ns._childrens;
				final int size = childs.size;
				for (int i = size - 1; i > -1; i--) {
					IEntity cc = childs.get(i);
					if (cc != null) {
						final RectBox rect1 = ns.getCollisionBox();
						final RectBox rect2 = cc.getCollisionBox();
						if (rect1 != null && !rect1.equals(rect2)) {
							addRect(rects, rect1.add(rect2));
						}
					}
				}
			} else {
				addRect(rects, spr.getCollisionBox());
			}
		} else if (spr instanceof Sprite) {
			if (spr.isContainer()) {
				final Sprite ns = (Sprite) spr;
				final TArray<ISprite> childs = ns._childrens;
				final int size = childs.size;
				for (int i = size - 1; i > -1; i--) {
					ISprite cc = childs.get(i);
					if (cc != null) {
						final RectBox rect1 = ns.getCollisionBox();
						final RectBox rect2 = cc.getCollisionBox();
						if (rect1 != null && !rect1.equals(rect2)) {
							addRect(rects, rect1.add(rect2));
						}
					}
				}
			} else {
				addRect(rects, spr.getCollisionBox());
			}
		} else {
			addRect(rects, spr.getCollisionBox());
		}
	}

	public DirtyRectList getDirtyList() {
		final TArray<RectBox> rects = new TArray<RectBox>();
		final ISprite[] childs = _sprites;
		if (childs != null) {
			final int size = _size;
			for (int i = size - 1; i > -1; i--) {
				ISprite spr = childs[i];
				if (spr != null) {
					addAllRect(rects, spr);
				}
			}
		}
		_dirtyList.clear();
		final int len = rects.size;
		for (int j = 0; j < len; j++) {
			final RectBox rect = rects.get(j);
			if (rect.width > 1 && rect.height > 1) {
				_dirtyList.add(rect);
			}
		}
		return _dirtyList;
	}

	public Sprites setSpritesShadow(ISpritesShadow s) {
		this._spriteShadow = s;
		if (_spriteShadow != null) {
			_createShadow = true;
		} else {
			_createShadow = false;
		}
		return this;
	}

	public ISpritesShadow shadow() {
		return _spriteShadow;
	}

	public boolean getSpritesShadow() {
		return _createShadow;
	}

	public int getMaxX() {
		int maxX = 0;
		final int size = this._size;
		final ISprite[] childs = this._sprites;
		for (int i = size - 1; i > -1; i--) {
			ISprite comp = childs[i];
			if (comp != null) {
				int curX = comp.x();
				if (curX > maxX) {
					maxX = curX;
				}
			}
		}
		return maxX;
	}

	public int getMaxY() {
		int maxY = 0;
		final int size = this._size;
		final ISprite[] childs = this._sprites;
		for (int i = size - 1; i > -1; i--) {
			ISprite comp = childs[i];
			if (comp != null) {
				int curY = comp.y();
				if (curY > maxY) {
					maxY = curY;
				}
			}
		}
		return maxY;
	}

	public int getMaxZ() {
		int maxZ = 0;
		final int size = this._size;
		final ISprite[] childs = this._sprites;
		for (int i = size - 1; i > -1; i--) {
			ISprite comp = childs[i];
			if (comp != null) {
				int curZ = comp.getZ();
				if (curZ > maxZ) {
					maxZ = curZ;
				}
			}
		}
		return maxZ;
	}

	@Override
	public int size() {
		return this._size;
	}

	public RectBox getBoundingBox() {
		if (_isViewWindowSet) {
			return new RectBox(this._viewX, this._viewY, this._viewWidth, this._viewHeight);
		}
		return new RectBox(this._viewX, this._viewY, this._width, this._height);
	}

	public int getHeight() {
		return _height;
	}

	public int getWidth() {
		return _width;
	}

	public Sprites hide() {
		setVisible(false);
		return this;
	}

	public Sprites show() {
		setVisible(true);
		return this;
	}

	@Override
	public boolean isVisible() {
		return _visible;
	}

	@Override
	public void setVisible(boolean v) {
		this._visible = v;
	}

	public SpriteListener getSprListerner() {
		return _sprListerner;
	}

	public Sprites setSprListerner(SpriteListener sprListerner) {
		this._sprListerner = sprListerner;
		return this;
	}

	public Screen getScreen() {
		return _screen;
	}

	public float getScreenX() {
		return _screen == null ? 0 : _screen.getX();
	}

	public float getScreenY() {
		return _screen == null ? 0 : _screen.getY();
	}

	public Sprites scrollBy(float x, float y) {
		this._scrollX += x;
		this._scrollY += y;
		return this;
	}

	public Sprites scrollTo(float x, float y) {
		this._scrollX = x;
		this._scrollY = y;
		return this;
	}

	public float scrollX() {
		return this._scrollX;
	}

	public float scrollY() {
		return this._scrollY;
	}

	public Sprites scrollX(float x) {
		this._scrollX = x;
		return this;
	}

	public Sprites scrollY(float y) {
		this._scrollY = y;
		return this;
	}

	public Margin margin(boolean vertical, float left, float top, float right, float bottom) {
		float size = vertical ? getHeight() : getWidth();
		if (_closed) {
			return new Margin(size, vertical);
		}
		if (_margin == null) {
			_margin = new Margin(size, vertical);
		} else {
			_margin.setSize(size);
			_margin.setVertical(vertical);
		}
		_margin.setMargin(left, top, right, bottom);
		_margin.clear();
		final int len = this._size;
		final ISprite[] childs = this._sprites;
		for (int i = 0; i < len; i++) {
			ISprite spr = childs[i];
			if (spr != null) {
				_margin.addChild(spr);
			}
		}
		return _margin;
	}

	/**
	 * 遍历Sprites中所有精灵对象并反馈给Callback
	 * 
	 * @param callback
	 */
	public Sprites forChildren(Callback<ISprite> callback) {
		if (callback == null) {
			return this;
		}
		final ISprite[] childs = _sprites;
		final int size = _size;
		for (int i = size - 1; i > -1; i--) {
			final ISprite child = childs[i];
			if (child != null) {
				callback.onSuccess(child);
			}
		}
		return this;
	}

	public Sprites resize(float width, float height, boolean forceResize) {
		if (_closed) {
			return this;
		}
		if (_resizeListener != null) {
			_resizeListener.onResize(this);
		}
		if (forceResize || (!MathUtils.equal(this._width, width) && !MathUtils.equal(this._height, height))) {
			this._width = (int) width;
			this._height = (int) height;
			final ISprite[] childs = _sprites;
			final int size = _size;
			for (int i = size - 1; i > -1; i--) {
				final ISprite child = childs[i];
				if (child != null) {
					child.onResize();
				}
			}
		}
		return this;
	}

	public ResizeListener<Sprites> getResizeListener() {
		return _resizeListener;
	}

	public Sprites setResizeListener(ResizeListener<Sprites> listener) {
		this._resizeListener = listener;
		return this;
	}

	public boolean isLimitViewWindows() {
		return _limitViewWindows;
	}

	public Sprites setLimitViewWindows(boolean limit) {
		this._limitViewWindows = limit;
		return this;
	}

	public Sprites rect(RectBox rect) {
		return rect(rect, 0);
	}

	public Sprites rect(RectBox rect, int shift) {
		rect(this, rect, shift);
		return this;
	}

	public Sprites triangle(Triangle2f t) {
		return triangle(t, 1);
	}

	public Sprites triangle(Triangle2f t, int stepRate) {
		triangle(this, t, stepRate);
		return this;
	}

	public Sprites circle(Circle circle) {
		return circle(circle, -1f, -1f);
	}

	public Sprites circle(Circle circle, float startAngle, float endAngle) {
		circle(this, circle, startAngle, endAngle);
		return this;
	}

	public Sprites ellipse(Ellipse e) {
		return ellipse(e, -1f, -1f);
	}

	public Sprites ellipse(Ellipse e, float startAngle, float endAngle) {
		ellipse(this, e, startAngle, endAngle);
		return this;
	}

	public Sprites line(Line e) {
		line(this, e);
		return this;
	}

	public Sprites rotateAround(XY point, float angle) {
		rotateAround(this, point, angle);
		return this;
	}

	public Sprites rotateAroundDistance(XY point, float angle, float distance) {
		rotateAroundDistance(this, point, angle, distance);
		return this;
	}

	public String getName() {
		return this._sprites_name;
	}

	@Override
	public boolean isEmpty() {
		return _size == 0 || _sprites == null;
	}

	@Override
	public boolean isNotEmpty() {
		return !isEmpty();
	}

	public Sprites setAutoYLayer(boolean y) {
		spriteXYSorter.setSortY(y);
		return this;
	}

	public boolean isAutoSortXYLayer() {
		return _autoSortLayer;
	}

	public Sprites setAutoSortXYLayer(boolean sort) {
		this._autoSortLayer = sort;
		return this;
	}

	public Sprites centerOn(final LObject<?> object) {
		if (object == null) {
			return this;
		}
		LObject.centerOn(object, getX(), getY(), getWidth(), getHeight());
		return this;
	}

	public Sprites centerTopOn(final LObject<?> object) {
		if (object == null) {
			return this;
		}
		LObject.centerTopOn(object, getX(), getY(), getWidth(), getHeight());
		return this;
	}

	public Sprites centerBottomOn(final LObject<?> object) {
		if (object == null) {
			return this;
		}
		LObject.centerBottomOn(object, getX(), getY(), getWidth(), getHeight());
		return this;
	}

	public Sprites topOn(final LObject<?> object) {
		if (object == null) {
			return this;
		}
		LObject.topOn(object, getX(), getY(), getWidth(), getHeight());
		return this;
	}

	public Sprites topLeftOn(final LObject<?> object) {
		if (object == null) {
			return this;
		}
		LObject.topLeftOn(object, getX(), getY(), getWidth(), getHeight());
		return this;
	}

	public Sprites topRightOn(final LObject<?> object) {
		if (object == null) {
			return this;
		}
		LObject.topRightOn(object, getX(), getY(), getWidth(), getHeight());
		return this;
	}

	public Sprites leftOn(final LObject<?> object) {
		if (object == null) {
			return this;
		}
		LObject.leftOn(object, getX(), getY(), getWidth(), getHeight());
		return this;
	}

	public Sprites rightOn(final LObject<?> object) {
		if (object == null) {
			return this;
		}
		LObject.rightOn(object, getX(), getY(), getWidth(), getHeight());
		return this;
	}

	public Sprites bottomOn(final LObject<?> object) {
		if (object == null) {
			return this;
		}
		LObject.bottomOn(object, getX(), getY(), getWidth(), getHeight());
		return this;
	}

	public Sprites bottomLeftOn(final LObject<?> object) {
		if (object == null) {
			return this;
		}
		LObject.bottomLeftOn(object, getX(), getY(), getWidth(), getHeight());
		return this;
	}

	public Sprites bottomRightOn(final LObject<?> object) {
		if (object == null) {
			return this;
		}
		LObject.bottomRightOn(object, getX(), getY(), getWidth(), getHeight());
		return this;
	}

	public Sprites centerOn(final LObject<?> object, final float offsetX, final float offsetY) {
		if (object == null) {
			return this;
		}
		LObject.centerOn(object, getX(), getY(), getWidth(), getHeight());
		object.setLocation(object.getX() + offsetX, object.getY() + offsetY);
		return this;
	}

	public Sprites centerTopOn(final LObject<?> object, final float offsetX, final float offsetY) {
		if (object == null) {
			return this;
		}
		LObject.centerTopOn(object, getX(), getY(), getWidth(), getHeight());
		object.setLocation(object.getX() + offsetX, object.getY() + offsetY);
		return this;
	}

	public Sprites centerBottomOn(final LObject<?> object, final float offsetX, final float offsetY) {
		if (object == null) {
			return this;
		}
		centerBottomOn(object);
		object.setLocation(object.getX() + offsetX, object.getY() + offsetY);
		return this;
	}

	public Sprites topOn(final LObject<?> object, final float offsetX, final float offsetY) {
		if (object == null) {
			return this;
		}
		topOn(object);
		object.setLocation(object.getX() + offsetX, object.getY() + offsetY);
		return this;
	}

	public Sprites topLeftOn(final LObject<?> object, final float offsetX, final float offsetY) {
		if (object == null) {
			return this;
		}
		topLeftOn(object);
		object.setLocation(object.getX() + offsetX, object.getY() + offsetY);
		return this;
	}

	public Sprites topRightOn(final LObject<?> object, final float offsetX, final float offsetY) {
		if (object == null) {
			return this;
		}
		topRightOn(object);
		object.setLocation(object.getX() + offsetX, object.getY() + offsetY);
		return this;
	}

	public Sprites leftOn(final LObject<?> object, final float offsetX, final float offsetY) {
		if (object == null) {
			return this;
		}
		leftOn(object);
		object.setLocation(object.getX() + offsetX, object.getY() + offsetY);
		return this;
	}

	public Sprites rightOn(final LObject<?> object, final float offsetX, final float offsetY) {
		if (object == null) {
			return this;
		}
		rightOn(object);
		object.setLocation(object.getX() + offsetX, object.getY() + offsetY);
		return this;
	}

	public Sprites bottomOn(final LObject<?> object, final float offsetX, final float offsetY) {
		if (object == null) {
			return this;
		}
		bottomOn(object);
		object.setLocation(object.getX() + offsetX, object.getY() + offsetY);
		return this;
	}

	public Sprites bottomLeftOn(final LObject<?> object, final float offsetX, final float offsetY) {
		if (object == null) {
			return this;
		}
		bottomLeftOn(object);
		object.setLocation(object.getX() + offsetX, object.getY() + offsetY);
		return this;
	}

	public Sprites bottomRightOn(final LObject<?> object, final float offsetX, final float offsetY) {
		if (object == null) {
			return this;
		}
		bottomRightOn(object);
		object.setLocation(object.getX() + offsetX, object.getY() + offsetY);
		return this;
	}

	@Override
	public String toString() {
		return super.toString() + " " + "[name=" + _sprites_name + ", total=" + size() + "]";
	}

	/**
	 * 清空当前精灵集合
	 * 
	 */
	@Override
	public void clear() {
		if (_closed) {
			return;
		}
		if (_sprites == null) {
			return;
		}
		for (int i = 0; i < _sprites.length; i++) {
			ISprite removed = _sprites[i];
			if (removed != null) {
				removed.setState(State.REMOVED);
				if (removed instanceof IEntity) {
					((IEntity) removed).onDetached();
				}
				// 删除精灵同时，删除缓动动画
				if (removed instanceof ActionBind) {
					ActionControl.get().removeAllActions((ActionBind) removed);
				}
			}
			_sprites[i] = null;
		}
		_size = 0;
		return;
	}

	/**
	 * 删除指定集合中的精灵
	 * 
	 * @param removes
	 * @return
	 */
	public TArray<Boolean> clear(TArray<ISprite> removes) {
		if (_closed || removes == null) {
			return new TArray<Boolean>();
		}
		TArray<Boolean> result = new TArray<Boolean>();
		final int size = removes.size;
		for (int i = size - 1; i > -1; i--) {
			final ISprite sprite = removes.get(i);
			if (sprite != null) {
				result.add(remove(sprite));
			}
		}
		return result;
	}

	/**
	 * 删除指定集合中的精灵
	 * 
	 * @param removes
	 * @return
	 */
	public TArray<Boolean> clear(ISprite... removes) {
		if (_closed || removes == null) {
			return new TArray<Boolean>();
		}
		TArray<Boolean> result = new TArray<Boolean>();
		final int size = removes.length;
		for (int i = size - 1; i > -1; i--) {
			final ISprite sprite = removes[i];
			if (sprite != null) {
				result.add(remove(sprite));
			}
		}
		return result;
	}

	public Sprites setLayerTop() {
		return this.setLayer(Integer.MAX_VALUE);
	}

	public Sprites setLayerBottom() {
		return this.setLayer(Integer.MIN_VALUE);
	}

	public Sprites setLayer(int z) {
		this._indexLayer = z;
		return this;
	}

	public Sprites setZ(int z) {
		return this.setZOrder(z);
	}

	public Sprites setZOrder(int z) {
		return setLayer(-z);
	}

	public int getZOrder() {
		return MathUtils.abs(getLayer());
	}

	public int getZ() {
		return getZOrder();
	}

	@Override
	public int getLayer() {
		return _indexLayer;
	}

	public Sprites clearListerner() {
		this._sprListerner = null;
		this._resizeListener = null;
		this._collisionActionListener = null;
		if (this._collisionIgnoreStrings != null) {
			this._collisionIgnoreStrings.clear();
			this._collisionIgnoreStrings = null;
		}
		if (this._collisionIgnoreTypes != null) {
			this._collisionIgnoreTypes.clear();
			this._collisionIgnoreTypes = null;
		}
		return this;
	}

	public boolean isClosed() {
		return _closed;
	}

	@Override
	public void close() {
		if (_closed) {
			return;
		}
		this._visible = this._createShadow = false;
		this._autoSortLayer = this._checkViewCollision = false;
		this._checkAllCollision = false;
		if (_spriteShadow != null) {
			_spriteShadow.close();
		}
		this._newLineHeight = 0;
		final ISprite[] childs = _sprites;
		if (childs != null) {
			final int size = _size;
			for (int i = size - 1; i > -1; i--) {
				final ISprite child = childs[i];
				if (child != null) {
					child.close();
				}
			}
		}
		clear();
		if (_light != null) {
			_light.close();
			_light = null;
		}
		this._useLight = false;
		if (_shaderMask != null) {
			_useShaderMask = false;
			_shaderMask.close();
			_shaderMask = null;
		}
		this._size = 0;
		this._closed = true;
		this._sprites = null;
		this._collViewSize = null;
		this.clearListerner();
		LSystem.popSpritesPool(this);
	}

}
