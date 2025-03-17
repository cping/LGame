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
package loon.component;

import java.util.Comparator;

import loon.LRelease;
import loon.LSystem;
import loon.LTexture;
import loon.LTextures;
import loon.action.map.items.IItem;
import loon.action.map.items.Inventory;
import loon.action.map.items.Item;
import loon.action.map.items.ItemInfo;
import loon.canvas.Canvas;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.component.skin.InventorySkin;
import loon.component.skin.SkinManager;
import loon.font.IFont;
import loon.font.Text;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.utils.MathUtils;

/**
 * 游戏背包用组件类
 */
public class LInventory extends LLayer {

	public static class ItemUI extends Item<ItemInfo> {

		protected boolean _saved;

		protected LInventory _inventory;

		protected Actor _actor;

		ItemUI(LInventory inv, String name, ItemInfo item, float x, float y, float w, float h) {
			super(name, x, y, w, h, item);
			this._inventory = inv;
		}

		protected void setInventoryUI(LInventory ui) {
			this._inventory = ui;
		}

		protected void updateActorSize(Actor actor) {
			if (actor.isThereparent()) {
				actor.setLocation((_itemArea.getX() + _inventory._offsetGridActorX),
						(_itemArea.getY() + _inventory._offsetGridActorY));
				actor.setSize((_itemArea.getWidth() - _inventory._offsetGridActorX * 2f),
						(_itemArea.getHeight() - _inventory._offsetGridActorY * 2f));
			}
		}

		@Override
		public ItemUI setArea(float x, float y, float w, float h) {
			super.setArea(x, y, w, h);
			resetActor();
			return this;
		}

		protected void removeActor() {
			if (_actor != null) {
				_inventory.removeObject(_actor);
				bind(null);
			}
		}

		protected void free() {
			final LRelease release = new LRelease() {

				@Override
				public void close() {
					setItem(null);
					setName(LSystem.UNKNOWN);
					removeActor();
				}
			};
			if (_actor != null && _inventory._actorFadeTime > 0f && _actor.isActionCompleted()) {
				this._saved = false;
				this._actor.selfAction().fadeOut(_inventory._actorFadeTime).start().dispose(release);
			} else {
				release.close();
			}
		}

		public void resetActor() {
			if (_actor != null) {
				updateActorSize(_actor);
			}
		}

		public ItemUI bind(LTexture tex, float x, float y, float w, float h) {
			if (_actor == null) {
				_actor = new Actor(tex, x, y, w, h);
			} else {
				_actor.setImage(tex);
			}
			bind(_actor);
			return this;
		}

		public ItemUI bind(Actor act) {
			if (act == null) {
				if (_actor != null) {
					_actor.setTag(null);
					_actor = null;
				}
				this._item = new ItemInfo();
				this._name = LSystem.UNKNOWN;
				this._saved = false;
				return this;
			}
			if (!_saved) {
				Object o = act.getTag();
				if (o != null && o instanceof ItemUI) {
					final ItemUI item = ((ItemUI) o);
					final ItemInfo tmpInfo = _item.cpy();
					final String tmpName = _name;
					this._item = item._item;
					this._name = item._name;
					item._item = tmpInfo;
					item._name = tmpName;
				}
			}
			_actor = act;
			_actor.setTag(this);
			_image = _actor.getImage();
			updateActorSize(_actor);
			if (!_inventory.containsObject(act)) {
				_inventory.addObject(act);
				if (_inventory._actorFadeTime > 0f && _actor.isActionCompleted()) {
					_actor.setAlpha(0f);
					_actor.selfAction().fadeIn(_inventory._actorFadeTime).start();
				}
			}
			_saved = true;
			return this;
		}

		public ItemUI swap(Actor actor) {
			if (actor == null) {
				return this;
			}
			if (actor.getTag() != null && actor.getTag() instanceof ItemUI) {
				return swap((ItemUI) actor.getTag());
			}
			return this;
		}

		public ItemUI swap(ItemUI item) {
			if (item == this) {
				return this;
			}
			if (item._actor == _actor) {
				return this;
			}
			final LTexture srcImg = item._image;
			final Actor srcActor = item._actor;
			final boolean srcSaved = item._saved;
			final RectBox srcArea = item._itemArea.cpy();
			final String srcName = item._name;
			final ItemInfo srcItem = item._item.cpy();

			final LTexture dstImg = _image;
			final Actor dstActor = _actor;
			final boolean dstSaved = _saved;
			final RectBox dstArea = _itemArea.cpy();
			final String dstName = _name;
			final ItemInfo dstItem = _item.cpy();

			item._image = srcImg;
			item._saved = srcSaved;
			item._itemArea = srcArea;
			item._name = srcName;
			item._item = dstItem;

			if (srcActor != null) {
				srcActor.setLocation((dstArea.getX() + _inventory._offsetGridActorX),
						(dstArea.getY() + _inventory._offsetGridActorY));
				srcActor.setSize((dstArea.getWidth() - _inventory._offsetGridActorX * 2f),
						(dstArea.getHeight() - _inventory._offsetGridActorY * 2f));
			}
			this._image = dstImg;
			this._saved = dstSaved;
			this._itemArea = dstArea;
			this._name = dstName;
			this._item = srcItem;

			if (dstActor != null) {
				dstActor.setLocation((srcArea.getX() + _inventory._offsetGridActorX),
						(srcArea.getY() + _inventory._offsetGridActorY));
				dstActor.setSize((srcArea.getWidth() - _inventory._offsetGridActorX * 2f),
						(srcArea.getHeight() - _inventory._offsetGridActorY * 2f));
			}
			if (srcActor != null) {
				bind(srcActor);
			} else {
				bind(null);
			}
			if (dstActor != null) {
				item.bind(dstActor);
			} else {
				item.bind(null);
			}
			return this;
		}

		public Actor getActor() {
			return _actor;
		}

	}

	public LInventory swap(ItemUI a, ItemUI b) {
		_inventory.swap(a, b);
		return this;
	}

	private LColor _gridColor;

	private boolean _initialization;

	private boolean _isCircleGrid;

	private boolean _isDisplayBar;

	private boolean _isAllowShowTip;

	private int _currentRowTableSize;

	private int _currentColTableSize;

	private float _offsetGridActorX;

	private float _offsetGridActorY;

	private float _gridPaddingLeft, _gridPaddingTop;

	private float _gridPaddingRight, _gridPaddingBottom;

	private float _gridPaddingX, _gridPaddingY;

	private float _gridTileWidth;

	private float _gridTileHeight;

	private float _actorFadeTime;

	private boolean _displayDrawGrid;

	private boolean _dirty;

	private boolean _isMobile;

	private LColor _tipFontColor;

	private IFont _tipFont;

	private Text _tipText;

	private LTexture _cacheGridTexture;

	private LTexture _tipTexture;

	private LTexture _barTexture;

	private Inventory _inventory;

	private Vector2f _titleSize;

	private boolean _tipSelected;

	private ItemUI _tipItem;

	public LInventory(float x, float y, float w, float h) {
		this(SkinManager.get().getMessageSkin().getFont(), x, y, w, h, false);
	}

	public LInventory(IFont font, float x, float y, float w, float h) {
		this(font, x, y, w, h, false);
	}

	public LInventory(float x, float y, float w, float h, boolean limit) {
		this(SkinManager.get().getMessageSkin().getFont(), x, y, w, h, limit);
	}

	public LInventory(IFont font, float x, float y, float w, float h, boolean limit) {
		this(font, (LTexture) null, (LTexture) null, LColor.gray, x, y, w, h, limit);
	}

	public LInventory(IFont font, LColor grid, float x, float y, float w, float h, boolean limit) {
		this(font, (LTexture) null, (LTexture) null, grid, x, y, w, h, limit);
	}

	public LInventory(IFont font, LTexture bg, LTexture bar, float x, float y, float w, float h, boolean limit) {
		this(font, bg, bar, LColor.gray, x, y, w, h, limit);
	}

	public LInventory(InventorySkin skin, float x, float y, float w, float h, boolean limit) {
		this(skin.getFont(), skin.getFontColor(), skin.getBackgroundTexture(), skin.getBarTexture(),
				skin.getGridColor(), x, y, w, h, limit);
	}

	public LInventory(IFont font, LTexture bg, LTexture bar, LColor gridColor, float x, float y, float w, float h,
			boolean limit) {
		this(font, LColor.white, bg, bar, gridColor, x, y, w, h, limit);
	}

	/**
	 * 构建一个游戏用背包
	 * 
	 * @param font
	 * @param fontColor
	 * @param bg
	 * @param bar
	 * @param gridColor
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param limit
	 */
	public LInventory(IFont font, LColor fontColor, LTexture bg, LTexture bar, LColor gridColor, float x, float y,
			float w, float h, boolean limit) {
		super(MathUtils.ifloor(x), MathUtils.ifloor(y), MathUtils.ifloor(w), MathUtils.ifloor(h), limit);
		this._inventory = new Inventory();
		this._titleSize = new Vector2f(w, h);
		this._offsetGridActorX = 2f;
		this._offsetGridActorY = 2f;
		this._actorFadeTime = 10f;
		if (gridColor != null) {
			this._gridColor = gridColor.lighter();
		}
		this._displayDrawGrid = _isDisplayBar = _isAllowShowTip = true;
		this._isCircleGrid = false;
		this._isMobile = LSystem.isMobile() || LSystem.isEmulateTouch();
		this._tipFont = font;
		this._tipFontColor = fontColor;
		this._barTexture = (bar == null ? SkinManager.get().getWindowSkin().getBarTexture() : bar);
		setTipBackground((LTexture) null);
		setBackground(bg == null ? SkinManager.get().getWindowSkin().getBackgroundTexture() : bg, w, h);
		setLayer(1000);
		setActorDrag(true);
		setDragLocked(false);
		setElastic(false);
	}

	public LInventory topBottom(float top, float bottom, int row, int col) {
		return leftTopRightBottom(_offsetGridActorX, top, _offsetGridActorY, bottom, row, col);
	}

	public LInventory rightBottom(float right, float bottom, int row, int col) {
		return leftTopRightBottom(_offsetGridActorX, _offsetGridActorY, right, bottom, row, col);
	}

	public LInventory leftTop(float left, float top, int row, int col) {
		return leftTopRightBottom(left, top, _offsetGridActorX, _offsetGridActorY, row, col);
	}

	public LInventory leftTopRightBottom(float left, float top, float right, float bottom, int row, int col) {
		return update(left, top, right, bottom, row, col, _offsetGridActorX * 2f, _offsetGridActorX * 2f);
	}

	public LInventory update() {
		return update(_gridPaddingLeft, _gridPaddingTop, _gridPaddingRight, _gridPaddingBottom, _currentRowTableSize,
				_currentColTableSize, _gridPaddingX, _gridPaddingY);
	}

	public LInventory update(float left, float top, float right, float bottom, int row, int col, float spaceSizeX,
			float spaceSizeY) {
		if (row == this._currentRowTableSize && col == this._currentColTableSize && spaceSizeX == this._gridPaddingX
				&& spaceSizeY == this._gridPaddingY && left == this._gridPaddingLeft && top == this._gridPaddingTop
				&& right == this._gridPaddingRight && bottom == this._gridPaddingBottom) {
			return this;
		}
		this._currentRowTableSize = row;
		this._currentColTableSize = col;
		this._gridPaddingLeft = left;
		this._gridPaddingTop = top;
		this._gridPaddingRight = right;
		this._gridPaddingBottom = bottom;
		this._gridPaddingX = spaceSizeX;
		this._gridPaddingY = spaceSizeY;
		this._gridTileWidth = getWidth() / _currentRowTableSize;
		this._gridTileHeight = getHeight() / _currentColTableSize;
		final int tileWidth = MathUtils
				.ifloor(_gridTileWidth - ((_gridPaddingLeft + _gridPaddingRight) / _currentRowTableSize));
		final int tileHeight = MathUtils
				.ifloor(_gridTileHeight - ((_gridPaddingTop + _gridPaddingBottom) / _currentColTableSize));
		this._titleSize.set(tileWidth, tileHeight);
		final float xLeft = MathUtils.min(_gridPaddingLeft, (_gridPaddingLeft + _gridPaddingRight))
				+ _gridPaddingX / 2f;
		final float xTop = MathUtils.min(_gridPaddingTop, (_gridPaddingTop + _gridPaddingBottom)) + _gridPaddingY / 2f;
		if (this._initialization) {
			this._inventory.clear();
		}
		final int size = _inventory.getItemCount();
		int idx = 0;
		if (size == 0) {
			for (int y = 0; y < col; y++) {
				for (int x = 0; x < row; x++) {
					ItemUI item = new ItemUI(this, LSystem.UNKNOWN + idx, new ItemInfo(), xLeft + (x * tileWidth),
							xTop + (y * tileHeight), tileWidth - spaceSizeX, tileHeight - spaceSizeY);
					_inventory.addItem(item);
					idx++;
				}
			}
		} else {
			ItemUI item = null;
			for (int y = 0; y < col; y++) {
				for (int x = 0; x < row; x++) {
					if (idx < size) {
						item = (ItemUI) _inventory.getItem(idx);
						item.setArea(xLeft + (x * tileWidth), xTop + (y * tileHeight), tileWidth - spaceSizeX,
								tileHeight - spaceSizeY);
					} else {
						item = new ItemUI(this, LSystem.UNKNOWN + idx, new ItemInfo(), xLeft + (x * tileWidth),
								xTop + (y * tileHeight), tileWidth - spaceSizeX, tileHeight - spaceSizeY);
						_inventory.addItem(item);
					}
					idx++;
				}
			}
		}
		this._initialization = true;
		this._dirty = true;
		return this;
	}

	public float getPaddingX() {
		return _gridPaddingX;
	}

	public float getPaddingY() {
		return _gridPaddingY;
	}

	public float getPaddingLeft() {
		return _gridPaddingLeft;
	}

	public float getPaddingTop() {
		return _gridPaddingTop;
	}

	public float getPaddingRight() {
		return _gridPaddingRight;
	}

	public float getPaddingBottom() {
		return _gridPaddingBottom;
	}

	public int getColumns() {
		return _currentColTableSize;
	}

	public int getRows() {
		return _currentRowTableSize;
	}

	public boolean isDirty() {
		return this._dirty;
	}

	public LInventory putItem(String path) {
		return putItem(LTextures.loadTexture(path), new ItemInfo());
	}

	public LInventory putItem(String path, ItemInfo info) {
		return putItem(LTextures.loadTexture(path), info);
	}

	public LInventory putItem(LTexture tex) {
		return putItem(tex, new ItemInfo());
	}

	public LInventory putItem(LTexture tex, ItemInfo info) {
		if (_initialization) {
			final int size = _inventory.getItemCount();
			for (int i = 0; i < size; i++) {
				ItemUI item = (ItemUI) _inventory.getItem(i);
				if (item != null && !item._saved) {
					RectBox rect = item.getArea();
					item.bind(tex, rect.x + _offsetGridActorX, rect.y + _offsetGridActorY,
							rect.width - _offsetGridActorX * 2f, rect.height - _offsetGridActorY * 2f);
					item.setName(info.getName());
					item.setItem(info);
					return this;
				}
			}
		} else {
			if (info == null) {
				info = new ItemInfo();
			}
			ItemUI item = new ItemUI(this, info.getName(), info, 0f, 0f, 0f, 0f);
			item.bind(tex, 0f, 0f, 32f, 32f);
			_inventory.addItem(item);
		}
		return this;
	}

	public ItemUI removeItemIndex(int idx) {
		ItemUI item = getItem(idx);
		if (item != null && item._saved) {
			item.free();
		}
		return item;
	}

	public boolean removeItem(float x, float y) {
		ItemUI item = getItem(x, y);
		if (item != null && item._saved) {
			item.free();
		}
		return item != null;
	}

	public ItemUI popItem() {
		ItemUI item = null;
		for (int i = _inventory.getItemCount() - 1; i > -1; i--) {
			item = (ItemUI) _inventory.getItem(i);
			if (item != null && item._saved) {
				break;
			}
		}
		if (item != null) {
			item.free();
		}
		return item;
	}

	public int getItemToIndex(ItemUI im) {
		return _inventory.getItemToIndex(im);
	}

	public ItemUI getItem(int idx) {
		return (ItemUI) _inventory.getItem(idx);
	}

	public ItemUI getItem(float x, float y) {
		return (ItemUI) _inventory.getItem(x, y);
	}

	public LInventory setItem(LTexture tex, ItemInfo info, float x, float y) {
		if (_initialization) {
			final int size = _inventory.getItemCount();
			for (int i = 0; i < size; i++) {
				ItemUI item = (ItemUI) _inventory.getItem(i);
				if (item != null) {
					RectBox rect = item.getArea();
					if (rect.contains(x, y)) {
						item.setItem(info);
						item.bind(tex, rect.x + _offsetGridActorX, rect.y + _offsetGridActorY,
								rect.width - _offsetGridActorX * 2f, rect.height - _offsetGridActorY * 2f);
					}
					return this;
				}
			}
		}
		return this;
	}

	public LInventory setItem(LTexture tex, ItemInfo info, int idx) {
		if (_initialization) {
			ItemUI item = (ItemUI) _inventory.getItem(idx);
			if (item != null) {
				RectBox rect = item.getArea();
				item.setItem(info);
				if (rect != null) {
					item.bind(tex, rect.x + _offsetGridActorX, rect.y + _offsetGridActorY,
							rect.width - _offsetGridActorX * 2f, rect.height - _offsetGridActorY * 2f);
				} else {
					item.bind(tex, 0f, 0f, 32f, 32f);
				}
			}
		}
		return this;
	}

	public LInventory setTipBackground(String path) {
		return setTipBackground(LTextures.loadTexture(path));
	}

	public LInventory setTipBackground(LTexture tex) {
		_tipTexture = (tex == null ? this._tipTexture = SkinManager.get().getMessageSkin().getBackgroundTexture()
				: tex);
		return this;
	}

	protected LInventory setTipText(String message) {
		return setTipText(_tipFont, message);
	}

	protected LInventory setTipText(IFont font, String message) {
		if (_tipText == null) {
			_tipText = new Text(font, message);
		} else {
			_tipText.setText(font, message);
		}
		return this;
	}

	protected void checkTouchTip() {
		if (!_isAllowShowTip) {
			return;
		}
		final Vector2f pos = getUITouchXY();
		setTipItem(getItem(pos.x, pos.y));
	}

	public LInventory setTipItem(int idx) {
		return setTipItem(getItem(idx));
	}

	public LInventory setTipItem(ItemUI item) {
		if (item == null) {
			freeTipSelected();
			return this;
		}
		_tipItem = item;
		if (_tipItem != null && _tipItem._saved) {
			final String name = _tipItem.getItem().getName();
			final String des = _tipItem.getItem().getDescription();
			final String context = name + LSystem.LF + des;
			setTipText(context);
			_tipSelected = true;
		} else {
			_tipSelected = false;
		}
		return this;
	}

	public int getItemCount() {
		return this._inventory.getItemCount();
	}

	public float getGold() {
		return this._inventory.getGold();
	}

	public LInventory addGold(float i) {
		this._inventory.addGold(i);
		return this;
	}

	public LInventory subGold(float i) {
		this._inventory.subGold(i);
		return this;
	}

	public LInventory mulGold(float i) {
		this._inventory.mulGold(i);
		return this;
	}

	public LInventory divGold(float i) {
		this._inventory.divGold(i);
		return this;
	}

	public LInventory setGold(float i) {
		this._inventory.setGold(i);
		return this;
	}

	public LInventory merge(LInventory inv) {
		this._inventory.merge(inv._inventory);
		return this;
	}

	public LInventory sort(Comparator<IItem> comp) {
		this._inventory.sort(comp);
		return this;
	}

	public LInventory clearInventory() {
		this._inventory.clear();
		return this;
	}

	public boolean containsItem(float x, float y) {
		return getItem(x, y) != null;
	}

	public boolean isInitialized() {
		return this._initialization;
	}

	protected LTexture createGridCache() {
		if (_dirty) {
			if (_cacheGridTexture != null) {
				_cacheGridTexture.cancalSubmit();
				_cacheGridTexture.close(true);
				_cacheGridTexture = null;
			}
			Canvas g = Image.createCanvas(getWidth(), getHeight());
			final int tint = g.getStrokeColor();
			g.setColor(_gridColor);
			for (int i = _inventory.getItemCount() - 1; i > -1; i--) {
				RectBox rect = _inventory.getItem(i).getArea();
				if (rect != null) {
					if (_displayDrawGrid) {
						if (_isCircleGrid) {
							g.drawOval(rect.x, rect.y, rect.width, rect.height);
						} else {
							g.strokeRect(rect.x, rect.y, rect.width, rect.height);
						}
					}
				}
			}
			g.setStrokeColor(tint);
			_cacheGridTexture = g.toTexture();
			_dirty = false;
			g = null;
		}
		return _cacheGridTexture;
	}

	@Override
	public void createCustomUI(GLEx g, int x, int y, int w, int h) {
		if (!_component_visible) {
			return;
		}
		if (_isDisplayBar) {
			if (_gridPaddingLeft > _gridPaddingX && _barTexture != null) {
				g.draw(_barTexture, x, y, _gridPaddingLeft, h);
			}
			if (_gridPaddingRight > _gridPaddingX && _barTexture != null) {
				g.draw(_barTexture, x + getWidth() - _gridPaddingRight - _gridPaddingX, y,
						_gridPaddingRight + _gridPaddingX, h);
			}
			if (_gridPaddingTop > _gridPaddingY && _barTexture != null) {
				g.draw(_barTexture, x, y, w, _gridPaddingTop);
			}
			if (_gridPaddingBottom > _gridPaddingY && _barTexture != null) {
				g.draw(_barTexture, x, y + getHeight() - _gridPaddingBottom - _gridPaddingY, w,
						_gridPaddingBottom + _gridPaddingY);
			}
		}
		if (_displayDrawGrid) {
			createGridCache();
			if (_cacheGridTexture != null) {
				g.draw(_cacheGridTexture, x, y);
			}
		}
		super.createCustomUI(g, x, y, w, h);
		drawTip(g, x, y);
	}

	protected void drawTip(GLEx g, float x, float y) {
		if (!_isAllowShowTip) {
			return;
		}
		if (_tipSelected && _tipItem != null && _tipText != null) {
			final RectBox rect = _tipItem.getArea();
			final IFont font = _tipText.getFont();
			final float fontSize = font.getSize();
			final float texW = _tipText.getWidth();
			final float texH = _tipText.getHeight();
			final float width = texW + fontSize;
			final float height = texH + fontSize;
			final float posX = x + rect.x + (rect.getWidth() + width - _gridTileWidth) / 2f - fontSize;
			final float posY = y + rect.y + (rect.getHeight() + height - _gridTileHeight) / 2f;
			g.draw(_tipTexture, posX - (width - texW + fontSize) / 2f, posY, width, height);
			_tipText.paintString(g, posX - (width - texW) / 2f, posY + (height - texH) / 2f, _tipFontColor);
		}
	}

	public LInventory freeTipSelected() {
		_tipSelected = false;
		_tipItem = null;
		return this;
	}

	@Override
	public void processTouchMoved() {
		super.processTouchMoved();
		if (!_isMobile) {
			checkTouchTip();
		}
	}

	@Override
	public void downClick(int dx, int dy) {
		super.downClick(dx, dy);
		freeTipSelected();
		if (_isMobile && isLongPressed()) {
			checkTouchTip();
		}
	}

	@Override
	public void dragClick(int dx, int dy) {
		super.dragClick(dx, dy);
		final boolean draged = _input == null ? false : (_input.getTouchDX() == 0 && _input.getTouchDY() == 0);
		if (_isMobile && !_tipSelected && draged) {
			checkTouchTip();
		}
	}

	@Override
	public void upClick(int dx, int dy) {
		super.upClick(dx, dy);
		final Actor act = getClickActor();
		if (act != null) {
			final ItemUI itemDst = getItem(dx, dy);
			final Object o = act.getTag();
			if (itemDst != null) {
				if (o != itemDst) {
					if (!itemDst._saved || o == null) {
						itemDst.bind(act);
					} else {
						itemDst.swap(act);
					}
				}
			}
		}
		freeTipSelected();
	}

	public float getGridTileWidth() {
		return _gridTileWidth;
	}

	public float getGridTileHeight() {
		return _gridTileHeight;
	}

	public boolean isDisplayDrawGrid() {
		return _displayDrawGrid;
	}

	public LInventory setDisplayDrawGrid(boolean d) {
		this._displayDrawGrid = d;
		this._dirty = true;
		return this;
	}

	public boolean isCircleGrid() {
		return _isCircleGrid;
	}

	public LInventory setCircleGrid(boolean d) {
		this._isCircleGrid = d;
		this._dirty = true;
		return this;
	}

	public LTexture getBarImage() {
		return _barTexture;
	}

	public LInventory setBarImage(LTexture bar) {
		this._barTexture = bar;
		return this;
	}

	public boolean isAllowShowTip() {
		return _isAllowShowTip;
	}

	public LInventory setAllowShowTip(boolean a) {
		this._isAllowShowTip = a;
		return this;
	}

	public boolean isDisplayBar() {
		return _isDisplayBar;
	}

	public LInventory setDisplayBar(boolean d) {
		this._isDisplayBar = d;
		return this;
	}

	public float getOffsetGridActorX() {
		return _offsetGridActorX;
	}

	public LInventory setOffsetGridActorX(float x) {
		this._offsetGridActorX = x;
		this._dirty = true;
		return this;
	}

	public float getOffsetGridActorY() {
		return _offsetGridActorY;
	}

	public LInventory setOffsetGridActorY(float y) {
		this._offsetGridActorY = y;
		this._dirty = true;
		return this;
	}

	public float getItemFadeAlphaTime() {
		return _actorFadeTime;
	}

	public LInventory setItemFadeAlphaTime(float a) {
		this._actorFadeTime = a;
		return this;
	}

	public ItemUI getTipItem() {
		return _tipItem;
	}

	public LColor getTipFontColor() {
		return _tipFontColor.cpy();
	}

	public LInventory setTipFontColor(LColor t) {
		this._tipFontColor = new LColor(t);
		return this;
	}

	public Vector2f getTitleSize() {
		return _titleSize.cpy();
	}

	public boolean isTipSelected() {
		return _tipSelected;
	}

	/**
	 * 变化当前背包为指定高度的可滚动容器(大于背包大小则背包最大值的2/3)
	 * 
	 * @param x
	 * @param y
	 * @param h
	 * @return
	 */
	public LScrollContainer toVerticalScroll(float x, float y, float h) {
		LScrollContainer scroll = LScrollContainer.createVerticalScrollContainer(this, x, y, h);
		scroll.add(this);
		return scroll;
	}

	/**
	 * 变化当前背包为指定宽度的可滚动容器(大于背包大小则背包最大值的2/3)
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @return
	 */
	public LScrollContainer toHorizontalScroll(float x, float y, float w) {
		LScrollContainer scroll = LScrollContainer.createHorizontalScrollContainer(this, x, y, w);
		scroll.add(this);
		return scroll;
	}

	@Override
	protected void _onDestroy() {
		super._onDestroy();
		if (_cacheGridTexture != null) {
			_cacheGridTexture.close();
			_cacheGridTexture = null;
		}
		if (_barTexture != null) {
			_barTexture.close();
			_barTexture = null;
		}
	}

}
