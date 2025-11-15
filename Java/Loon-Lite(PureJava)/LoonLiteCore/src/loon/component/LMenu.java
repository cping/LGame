/**
 * 
 * Copyright 2014
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
 * @version 0.4.1
 */
package loon.component;

import loon.LRelease;
import loon.LSysException;
import loon.LSystem;
import loon.LTexture;
import loon.LTextures;
import loon.canvas.LColor;
import loon.component.skin.MenuSkin;
import loon.component.skin.SkinManager;
import loon.events.QueryEvent;
import loon.events.SysTouch;
import loon.events.Updateable;
import loon.font.FontSet;
import loon.font.IFont;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.TArray;
import loon.utils.timer.Duration;

/**
 * LGame菜单栏，用户可以隐藏大量按钮到其中，直到选中菜单时才动态展露，而非选中时则恢复隐藏.(此组件允许用户自行替换UI，
 * 若setSupportScroll(true)则支持滚动)
 * 
 * Example:
 * 
 * <pre>
 * LMenu panel = new LMenu(LMenu.MOVE_LEFT, "Menu");
 * panel.add("ABC");
 * panel.add("EFG");
 * panel.add("ABC");
 * </pre>
 */
public class LMenu extends LComponent implements FontSet<LMenu> {

	public static interface MenuItemClick {
		public void onClick(MenuItem item);
	}

	private static class ClickMenu implements Updateable {

		private MenuItemClick click;

		private MenuItem item;

		ClickMenu(MenuItemClick c, MenuItem i) {
			this.click = c;
			this.item = i;
		}

		@Override
		public void action(Object a) {
			if (click != null && item != null) {
				try {
					click.onClick(item);
				} catch (Throwable thr) {
					LSystem.error("LMenu click() exception", thr);
				}
			}
		}

	}

	public static class MenuItem implements LRelease {

		protected LTexture _texture;
		protected LMenu _parent;
		protected String _label;

		protected String _varName;
		private float _x;
		private float _y;
		public int index;
		public float yslot;
		public float xslot;
		public float itemWidth;
		public float itemHeight;
		public float offsetX;
		public float offsetY;
		public float labelOffsetX;
		public float labelOffsetY;

		private RectBox _itemrect;

		protected boolean _keep = false;

		private boolean _visible = true;
		private boolean _clicked = false;
		private boolean _localpos = false, _localsize = false;

		private IFont _font;
		private MenuItemClick _itemclick;

		protected MenuItem(LMenu parent, LTexture tex, String label, MenuItemClick click) {
			this(SkinManager.get().getMenuSkin().getFont(), parent, tex, false, label, click);
		}

		protected MenuItem(IFont font, LMenu parent, LTexture tex, String label, MenuItemClick click) {
			this(font, parent, tex, false, label, click);
		}

		protected MenuItem(IFont font, LMenu parent, LTexture tex, boolean keep, String label, MenuItemClick click) {
			this._varName = label;
			this._parent = parent;
			if (parent != null) {
				parent.add(this);
				this.index = parent._items.size;
			}
			this._label = label;
			this._keep = keep;
			this._itemclick = click;
			this._font = font;
			this.setTexture(tex);
		}

		protected MenuItem(LMenu parent, LTexture tex, boolean keep, String label, float x, float y,
				MenuItemClick click) {
			this(SkinManager.get().getMenuSkin().getFont(), parent, tex, true, label, x, y, 0, 0, click);
		}

		protected MenuItem(IFont font, LMenu parent, LTexture tex, boolean keep, String label, float x, float y,
				MenuItemClick click) {
			this(font, parent, tex, true, label, x, y, 0, 0, click);
		}

		protected MenuItem(IFont font, LMenu parent, LTexture tex, boolean keep, String label, float x, float y,
				float w, float h, MenuItemClick click) {
			this._x = x;
			this._y = y;
			this.itemWidth = w;
			this.itemHeight = h;
			if (w < 1 && h < 1) {
				this._localsize = true;
			} else {
				this._localsize = false;
			}
			if (x < 1 && y < 1) {
				this._localpos = true;
			} else {
				this._localpos = false;
			}
			this._parent = parent;
			if (_parent != null) {
				_parent.add(this);
				this.index = _parent._items.size;
			}
			this._varName = label;
			this._label = label;
			this._keep = keep;
			this._itemclick = click;
			this._font = font;
			this.setTexture(tex);
		}

		public MenuItem setTexture(String path) {
			return setTexture(LTextures.loadTexture(path));
		}

		public MenuItem setTexture(LTexture tex2d) {
			if (tex2d == null) {
				this._keep = false;
				return this;
			}
			this._texture = tex2d;
			this._parent.freeRes().add(tex2d);
			return this;
		}

		public boolean isVisible() {
			return this._visible;
		}

		public MenuItem setVisible(boolean v) {
			this._visible = v;
			return this;
		}

		public MenuItem offset(float x, float y) {
			this.offsetX = x;
			this.offsetY = y;
			return this;
		}

		public MenuItem setOffsetX(float x) {
			this.offsetX = x;
			return this;
		}

		public MenuItem setOffsetY(float y) {
			this.offsetY = y;
			return this;
		}

		public MenuItem setFont(IFont font) {
			this._font = font;
			return this;
		}

		public IFont getFont() {
			return this._font;
		}

		public void draw(GLEx g, Vector2f pos, boolean isDown, boolean isDrag, boolean checked) {
			if (!_visible) {
				return;
			}
			final IFont font = _font == null ? g.getFont() : _font;
			if (this._parent != null) {
				if (!_localpos) {
					this._x = (this._parent._cellWidth * this.xslot + this._parent._cellWidth / this.itemWidth
							+ this.xslot * this._parent._paddingx) - this._parent._item_left_offset;
					this._y = (this._parent._cellHeight * this.yslot + this._parent._cellHeight / this.itemHeight
							+ this.yslot * this._parent._paddingy);

					if (_x > Float.MAX_VALUE) {
						_x = 0;
					} else if (_x < Float.MIN_VALUE) {
						_x = 0;
					}
					if (_y > Float.MAX_VALUE) {
						_y = 0;
					} else if (_y < Float.MIN_VALUE) {
						_y = 0;
					}
				}
				if (_parent._moveType == LMenu.MOVE_RIGHT) {
					float posX = _parent.getScreenRight() - _parent._main_panel_size;
					this._x = posX + _x;
				}
				if (!_localsize) {
					if (!this._keep || _texture == null) {
						this.itemWidth = this._parent._cellWidth;
						this.itemHeight = this._parent._cellHeight;
					} else {
						this.itemWidth = this._texture.getWidth();
						this.itemHeight = this._texture.getHeight();
					}
				}
				if (pos != null && bounds().contains(pos.x, pos.y) && checked) {
					if ((isDown && !isDrag) && checked && (!this._clicked)) {
						ClickMenu menu = new ClickMenu(this._itemclick, this);
						LSystem.load(menu);
						this._clicked = true;
					}
				}
				if (!checked) {
					this._clicked = false;
				}
				if (_texture != null) {
					g.draw(this._texture, this._x + _parent._leftOffsetMoveMenu + offsetX,
							this._y + this._parent._paddingy + this._parent.scroll + offsetY, this.itemWidth,
							this.itemHeight, this._clicked ? LColor.gray : _parent._component_baseColor);
				}
				if (this._label != null) {
					font.drawString(g, _label,
							(this._x + _parent._leftOffsetMoveMenu + (itemWidth / 2 - font.stringWidth(_label) / 2))
									+ offsetX + labelOffsetX,
							(this._y + this._parent._paddingy + this._parent.scroll - font.getAscent() - 2) + offsetY
									+ labelOffsetY,
							this._clicked ? LColor.gray : _parent._fontColor);
				}

			} else {
				if (_parent != null) {
					if (bounds().contains(_parent.getTouchX(), _parent.getTouchY()) && checked) {
						if ((isDown && !isDrag) && checked && (!this._clicked)) {
							ClickMenu menu = new ClickMenu(this._itemclick, this);
							LSystem.load(menu);
							this._clicked = true;
						}
					}
				}
				if (!checked) {
					this._clicked = false;
				}
				if (_texture != null) {
					g.draw(this._texture, this._x + offsetX, this._y + offsetY, this.itemWidth, this.itemHeight);
				}
				if (this._label != null) {
					font.drawString(g, this._label,
							(this._x + (itemWidth / 2 - font.stringWidth(_label) / 2 - font.getAscent())) + offsetX
									+ labelOffsetX,
							(this._y - 2) + offsetY + labelOffsetY);
				}
			}
		}

		public void setVarName(String varName) {
			this._varName = varName;
		}

		public String getVarName() {
			return this._varName;
		}

		public boolean isClicked() {
			return _clicked;
		}

		public RectBox bounds() {
			if (_parent._moveType == LMenu.MOVE_LEFT) {
				if (_itemrect == null) {
					_itemrect = new RectBox(this._x + _parent._leftOffsetMoveMenu + offsetX,
							this._y + this._parent._paddingy + this._parent.scroll + offsetY, this.itemWidth,
							this.itemHeight);
				} else {
					_itemrect.setBounds(this._x + _parent._leftOffsetMoveMenu + offsetX,
							this._y + this._parent._paddingy + this._parent.scroll + offsetY, this.itemWidth,
							this.itemHeight);
				}
			} else if (_parent._moveType == LMenu.MOVE_RIGHT) {
				if (_itemrect == null) {
					_itemrect = new RectBox(this._x + _parent._leftOffsetMoveMenu + offsetX,
							this._y + this._parent._paddingy + this._parent.scroll + offsetY, this.itemWidth,
							this.itemHeight);
				} else {
					_itemrect.setBounds(this._x + _parent._leftOffsetMoveMenu + offsetX,
							this._y + this._parent._paddingy + this._parent.scroll + offsetY, this.itemWidth,
							this.itemHeight);
				}
			}
			return _itemrect;
		}

		public LMenu getParent() {
			return this._parent;
		}

		public String getLabel() {
			return this._label;
		}

		public float getX() {
			return this._x;
		}

		public float getY() {
			return this._y;
		}

		public float getWidth() {
			return this.itemWidth;
		}

		public float getHeight() {
			return this.itemHeight;
		}

		@Override
		public void close() {
			if (_texture != null) {
				_texture.close();
				_texture = null;
			}
		}

		public boolean isClosed() {
			return _texture == null || _texture.isClosed();
		}
	}

	private boolean _mouseSelect = false;

	private boolean _tabOpening;

	private boolean _panelOpening;

	private boolean _clickedMenu;

	protected float _leftOffsetMoveMenu;

	private IFont _font;
	private float _width;
	private float _main_panel_size;
	private float _tabY;
	private LColor _fontColor = LColor.white;

	private LTexture _mainpanel;
	private LTexture _tab;
	private boolean _active, _supportScroll;

	private RectBox _mainRec;
	private RectBox _tabRec;

	public int xslot;
	public int yslot;
	public float scroll;
	public float maxscroll;
	public float scrollspeed = 25f;

	private LMenu _selected;

	private float _cellWidth = LSystem.LAYER_TILE_SIZE;
	private float _cellHeight = LSystem.LAYER_TILE_SIZE;
	private float _paddingx = 2f;
	private float _paddingy = 50f;
	private float _alphaMenu = 0.7f;
	private float _menuSpeed = 1.5f;
	private float _offsetMenuTextX;
	private float _offsetMenuTextY;

	public final static int MOVE_LEFT = 0;

	public final static int MOVE_RIGHT = 1;

	private int _item_left_offset = 10;

	private int _item_top_offset = 0;

	private int _tabWidth, _tabHeight;

	private int _moveType = MOVE_RIGHT;

	private int _rows = 1;

	private TArray<MenuItem> _items = new TArray<MenuItem>(10);

	private String _tablabel;

	private boolean _itemClicked;

	private boolean _defUI;

	public LMenu(int move_type, String label) {
		this(move_type, SkinManager.get().getMenuSkin().getFont(), label, 100, 50);
	}

	public LMenu(int move_type, String label, int w, int h) {
		this(move_type, SkinManager.get().getMenuSkin().getFont(), label, w, h);
	}

	public LMenu(int move_type, IFont font, String label, int width, int height) {
		this(move_type, font, label, width, height, SkinManager.get().getMenuSkin().getTabTexture(),
				SkinManager.get().getMenuSkin().getMainTexture(), 0, 0, true);
	}

	public LMenu(int move_type, IFont font, String label, int width, int height, String tabfile, String mainfile) {
		this(move_type, font, label, width, height, LSystem.loadTexture(tabfile), LSystem.loadTexture(mainfile), 0, 0,
				false);
	}

	public LMenu(int move_type, IFont font, String label, int width, int height, String tabfile, String mainfile,
			int taby, int mainsize) {
		this(move_type, font, label, width, height, LSystem.loadTexture(tabfile), LSystem.loadTexture(mainfile), taby,
				mainsize, false);
	}

	public LMenu(int move_type, IFont font, String label, int width, int height, LTexture tab, LTexture main,
			int taby) {
		this(move_type, font, label, width, height, tab, main, taby, 0, false);
	}

	public LMenu(int move_type, IFont font, String label, int width, int height, LTexture tab, LTexture main, int taby,
			int mainsize) {
		this(move_type, font, label, width, height, tab, main, taby, mainsize, false);
	}

	public LMenu(int move_type, IFont font, String label, int width, int height, LTexture tab, LTexture main, int taby,
			int mainsize, boolean defUI) {
		this(move_type, font, label, width, height, tab, main, taby, mainsize, defUI,
				SkinManager.get().getMenuSkin().getFontColor());
	}

	public LMenu(MenuSkin skin, int move_type, String label, int width, int height, int taby, int mainsize,
			boolean defUI) {
		this(move_type, skin.getFont(), label, width, height, skin.getTabTexture(), skin.getMainTexture(), taby,
				mainsize, defUI, skin.getFontColor());
	}

	public LMenu(int move_type, IFont font, String label, int width, int height, LTexture tab, LTexture main, int taby,
			int mainsize, boolean defUI, LColor color) {
		super(0, 0, width, height);
		this._fontColor = color;
		this._moveType = move_type;
		this._tablabel = label;
		this._font = font;
		this._tabY = taby;
		this._tab = tab;
		this._mainpanel = main;
		this._tabWidth = width;
		this._tabHeight = height;
		if (mainsize > 0) {
			this._main_panel_size = mainsize;
		} else {
			this._main_panel_size = getScreenWidth() / 4;
		}
		this._main_panel_size += this._cellWidth + this._paddingx;
		this._component_baseColor = LColor.white.cpy();
		this._defUI = defUI;
		if (_moveType > MOVE_RIGHT) {
			throw new LSysException("Type:" + _moveType + ", The Menu display mode is not supported !");
		}
		setLocation(MathUtils.ifloor(getScreenX()), getScreenY());
	}

	private RectBox tagbounds(int mt) {
		if (mt == MOVE_LEFT) {
			if (_tabRec == null) {
				_tabRec = new RectBox(this._width, getTaby(), _tabWidth, _tabHeight);
			} else {
				_tabRec.setBounds(this._width, getTaby(), _tabWidth, _tabHeight);
			}
		} else if (_moveType == MOVE_RIGHT) {
			float posX = this.getScreenRight() - this._width - this._tabWidth;
			if (_tabRec == null) {
				_tabRec = new RectBox(posX, getTaby(), _tabWidth, _tabHeight);
			} else {
				_tabRec.setBounds(posX, getTaby(), _tabWidth, _tabHeight);
			}
		}
		return _tabRec;
	}

	private RectBox panelbounds(int mt) {
		if (mt == MOVE_LEFT) {
			if (_mainRec == null) {
				_mainRec = new RectBox(getDesktopLeft(), getDesktopTop(), this._width, getScreenHeight());
			} else {
				_mainRec.setBounds(getDesktopLeft(), getDesktopTop(), this._width, getScreenHeight());
			}
		} else if (_moveType == MOVE_RIGHT) {
			float posX = this.getScreenRight() - this._width;
			if (_mainRec == null) {
				_mainRec = new RectBox(posX, getDesktopTop(), this._width, getScreenHeight());
			} else {
				_mainRec.setBounds(posX, getDesktopTop(), this._width, getScreenHeight());
			}
		}
		return _mainRec;
	}

	public float getTaby() {
		return this._tabY;
	}

	public void setTaby(float tabY) {
		this._tabY = tabY;
	}

	public MenuItem add(String label) {
		return add(label, null);
	}

	public MenuItem add(String label, MenuItemClick click) {
		return add(new LMenu.MenuItem(this, SkinManager.get().getMenuSkin().getTabTexture(), label, click));
	}

	public MenuItem add(String label, String file, MenuItemClick click) {
		return add(new LMenu.MenuItem(this, LSystem.loadTexture(file), label, click));
	}

	public MenuItem add(String label, LTexture texture, MenuItemClick click) {
		return add(new LMenu.MenuItem(this, texture, label, click));
	}

	public MenuItem add(String label, LTexture texture, float x, float y, MenuItemClick click) {
		return add(new LMenu.MenuItem(this, texture, false, label, x, y, click));
	}

	public MenuItem add(String label, LTexture texture, float x, float y, float w, float h, MenuItemClick click) {
		return add(new LMenu.MenuItem(_font, this, texture, false, label, x, y, w, h, click));
	}

	public MenuItem add(MenuItem item) {
		if (this.xslot > this._main_panel_size / (this._cellWidth + this._paddingx * 2)) {
			this.xslot = 1;
			this.yslot += 1;
			this._rows += 1;
		}
		item.xslot = this.xslot;
		item.yslot = this.yslot;
		this._items.add(item);
		this.xslot += 1;
		return item;
	}

	public MenuItem getItem(QueryEvent<MenuItem> item) {
		return _items.find(item);
	}

	public MenuItem getItem(String name) {
		if (name == null) {
			return null;
		}
		for (int i = _items.size - 1; i > -1; i--) {
			MenuItem item = _items.get(i);
			if (item != null && name.equals(item._varName)) {
				return item;
			}
		}
		return null;
	}

	public TArray<MenuItem> getItems(String name) {
		if (name == null) {
			return null;
		}
		final TArray<MenuItem> items = new TArray<MenuItem>();
		for (MenuItem item : items) {
			if (item != null && name.equals(item._varName)) {
				items.add(item);
			}
		}
		return items;
	}

	public boolean isItemClicked() {
		for (int i = _items.size - 1; i > -1; i--) {
			MenuItem item = _items.get(i);
			if (item != null && item.isClicked()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void createUI(GLEx g, int x, int y) {
		final float oldAlpha = g.alpha();
		try {
			final boolean down = SysTouch.isDown();
			final boolean drag = SysTouch.isDrag();
			final boolean checked = (down || drag);
			switch (_moveType) {
			case MOVE_LEFT:
				if ((_selected == this) || (_selected == null)) {
					g.draw(this._tab, this._width, getTaby(), _tabWidth, _tabHeight,
							_component_baseColor.setAlpha(_alphaMenu));
					if (_tablabel != null) {
						_font.drawString(g, this._tablabel,
								this._width + (_tabWidth / 2f - _font.stringWidth(_tablabel) / 2f) + _offsetMenuTextX,
								getTaby() + getScreenY() + (_tabHeight - _font.getHeight()) / 2f + _offsetMenuTextY,
								_fontColor);
					}
				}
				if ((this._active) || (this._width > 0)) {
					g.draw(_mainpanel, getScreenX(), getScreenY(), this._width, getScreenHeight(),
							_component_baseColor);
					if (MathUtils.equal(this._width, this._main_panel_size)) {
						final TArray<MenuItem> list = this._items;
						final int size = list.size;
						for (int i = 0; i < size; i++) {
							MenuItem item = list.get(i);
							if (item != null) {
								item.draw(g, checked ? getUITouchXY() : null, down, drag, checked);
							}
						}
					}
				}
				break;
			case MOVE_RIGHT:
				if ((_selected == this) || (_selected == null)) {
					float posX = this.getScreenRight() - this._width - this._tabWidth;
					g.draw(this._tab, posX, getTaby() + getScreenY(), _tabWidth, _tabHeight,
							_component_baseColor.setAlpha(_alphaMenu));
					if (_tablabel != null) {
						_font.drawString(g, this._tablabel,
								posX + (_tabWidth / 2 - _font.stringWidth(_tablabel) / 2) + _offsetMenuTextX,
								getTaby() + (_tabHeight - _font.getHeight()) / 2f + _offsetMenuTextY, _fontColor);
					}
				}
				if ((this._active) || (this._width > 0)) {
					float posX = this.getScreenRight() - this._width;
					g.draw(_mainpanel, posX, getScreenY(), this._width, getScreenHeight(), _component_baseColor);
					if (MathUtils.equal(this._width, this._main_panel_size)) {
						final TArray<MenuItem> list = this._items;
						final int size = list.size;
						for (int i = 0; i < size; i++) {
							MenuItem item = list.get(i);
							if (item != null) {
								item.draw(g, checked ? getUITouchXY() : null, down, drag, checked);
							}
						}
					}
				}
				break;
			}
			checkTouchExitMenu();
		} finally {
			g.setAlpha(oldAlpha);
		}
	}

	@Override
	public void update(long elapsedTime) {
		if (!isVisible()) {
			return;
		}
		checkTouchDownMenuTabPanel();
		super.update(elapsedTime);
		final float delta = (Duration.toS(elapsedTime)) * (LSystem.getFPS() / _menuSpeed);
		if (!this._active) {
			if (_tabOpening && (_selected == null)) {
				this._active = true;
				this._mouseSelect = true;
				if (_selected == null) {
					_selected = this;
				}
			}
			if (this._width > 0) {
				this._width -= 0.3f * this._width * delta;
			}
			if (this._width <= 0) {
				this._width = 0;
			}
		} else {
			this._mouseSelect = true;
			if (_selected == this) {
				final float menuCellHeight = this._paddingy + this._cellHeight;
				this.maxscroll = (menuCellHeight * this._rows);
				if (this.scroll > this.maxscroll) {
					this.scroll = this.maxscroll;
				}
				if (this.scroll < -this.maxscroll) {
					this.scroll = -this.maxscroll;
				}
				final float halfmenuHeight = MathUtils.abs(getScreenTop() - menuCellHeight / 2f);
				if (this.scroll > 0f && this.scroll > halfmenuHeight) {
					this.scroll = halfmenuHeight;
				} else if (this.scroll < 0f) {
					final float upScroll = MathUtils.abs(scroll);
					final float upMaxScroll = MathUtils.abs((maxscroll - this.getDesktopBottom()) + menuCellHeight);
					if (upScroll > upMaxScroll) {
						this.scroll = -upMaxScroll;
					}
				}
				if (isMenuOpening()) {
					if (this._width < this._main_panel_size)
						this._width += 0.3F * (this._main_panel_size - this._width) * delta;
					else {
						this._width = this._main_panel_size;
					}
					if (this._width > this._main_panel_size - 2) {
						this._width = this._main_panel_size;
					}
					if (_input.isMoving() && _supportScroll) {
						if (_input.getTouchDY() > 5) {
							this.scroll -= this.scrollspeed * delta;
						} else if (_input.getTouchDY() < -5) {
							this.scroll += this.scrollspeed * delta;
						}
					}
				} else {
					if (_selected == this) {
						_selected = null;
					}
					this._active = false;
					this._mouseSelect = false;
				}
			}
		}
	}

	public boolean touchTab() {
		final Vector2f pos = getUITouchXY();
		return touchTab(pos.x, pos.y);
	}

	public boolean touchPanel() {
		final Vector2f pos = getUITouchXY();
		return touchPanel(pos.x, pos.y);
	}

	public boolean touchTab(float x, float y) {
		return tagbounds(_moveType).contains(x, y);
	}

	public boolean touchPanel(float x, float y) {
		return panelbounds(_moveType).contains(x, y);
	}

	public boolean isMenuOpening() {
		return _tabOpening || _panelOpening;
	}

	public boolean isMenuClosed() {
		return !(_tabOpening && _panelOpening);
	}

	public boolean isDoClickItem() {
		return !SysTouch.isDrag() && !_input.isMoving() && (_tabOpening || _panelOpening);
	}

	public boolean isNotInMenuTouchClick() {
		final Vector2f pos = getUITouchXY();
		return isClickDown() && !isDesktopClicked() && !isSelected() && !getCollisionBox().contains(pos.x, pos.y);
	}

	public boolean isDoClickMenuTab() {
		return !isMenuOpening() && isClickDown() && isTouchDownClick();
	}

	public boolean isMouseSelect() {
		return _mouseSelect;
	}

	public boolean isClickedMenu() {
		return _clickedMenu;
	}

	protected void checkTouchDownMenuTabPanel() {
		if (_clickedMenu) {
			return;
		}
		if (this._moveType == MOVE_LEFT && isDoClickMenuTab()) {
			_clickedMenu = true;
		} else if (!isMenuOpening() && isClickDown()) {
			_clickedMenu = true;
		}
		if (_clickedMenu) {
			final Vector2f pos = getUITouchXY();
			_tabOpening = touchTab(pos.x, pos.y);
			_panelOpening = touchPanel(pos.x, pos.y);
		}
	}

	protected void checkTouchExitMenu() {
		if (isDoClickItem()) {
			_panelOpening = touchPanel();
			if (!_panelOpening) {
				_itemClicked = isItemClicked();
				if (!_itemClicked && isNotInMenuTouchClick()) {
					_tabOpening = _panelOpening = false;
				}
			}
		}
		_clickedMenu = false;
	}

	public float getPanelWidth() {
		return _main_panel_size;
	}

	public LMenu setPanelWidth(float w) {
		this._main_panel_size = w;
		return this;
	}

	public int getItemLeftOffset() {
		return _item_left_offset;
	}

	public LMenu setItemLeftOffset(int left) {
		this._item_left_offset = left;
		return this;
	}

	public int getItemTopOffset() {
		return _item_top_offset;
	}

	public LMenu setItemTopOffset(int top) {
		this._item_top_offset = top;
		return this;
	}

	@Override
	public boolean isActive() {
		return _active && super.isActive();
	}

	@Override
	public LMenu setActive(boolean active) {
		super.setActive(active);
		this._active = active;
		return this;
	}

	public float getScroll() {
		return scroll;
	}

	public LMenu setScroll(float scroll) {
		this.scroll = scroll;
		return this;
	}

	public float getScrollspeed() {
		return scrollspeed;
	}

	public LMenu setScrollspeed(float scrollspeed) {
		this.scrollspeed = scrollspeed;
		return this;
	}

	public LMenu getSelected() {
		return _selected;
	}

	public float getCellWidth() {
		return _cellWidth;
	}

	public LMenu setCellWidth(float cellWidth) {
		this._cellWidth = cellWidth;
		return this;
	}

	public float getCellHeight() {
		return _cellHeight;
	}

	public LMenu setCellHeight(float cellHeight) {
		this._cellHeight = cellHeight;
		return this;
	}

	public String getLabel() {
		return _tablabel;
	}

	public LMenu setLabel(String label) {
		this._tablabel = label;
		return this;
	}

	public int getXslot() {
		return xslot;
	}

	public LMenu setXslot(int xslot) {
		this.xslot = xslot;
		return this;
	}

	public int getYslot() {
		return yslot;
	}

	public LMenu setYslot(int yslot) {
		this.yslot = yslot;
		return this;
	}

	public float getMaxscroll() {
		return maxscroll;
	}

	public void setMaxscroll(float maxscroll) {
		this.maxscroll = maxscroll;
	}

	public float getPaddingx() {
		return _paddingx;
	}

	public LMenu setPaddingx(float p) {
		this._paddingx = p;
		return this;
	}

	public float getPaddingy() {
		return _paddingy;
	}

	public LMenu setPaddingy(float p) {
		this._paddingy = p;
		return this;
	}

	public int getRows() {
		return _rows;
	}

	public int getTabWidth() {
		return _tabWidth;
	}

	public int getTabHeight() {
		return _tabHeight;
	}

	public float menuSpeed() {
		return _menuSpeed;
	}

	public LMenu setSpeed(float s) {
		this._menuSpeed = MathUtils.max(0, s);
		return this;
	}

	public int getTypeCode() {
		return _moveType;
	}

	public boolean isSupportScroll() {
		return _supportScroll;
	}

	public LMenu setSupportScroll(boolean s) {
		this._supportScroll = s;
		return this;
	}

	public LMenu setTypeCode(int t) {
		this._moveType = t;
		return this;
	}

	public boolean isDefUI() {
		return _defUI;
	}

	public float getAlphaMenu() {
		return _alphaMenu;
	}

	public LMenu setAlphaMenu(float a) {
		this._alphaMenu = a;
		return this;
	}

	public float getOffsetMenuTextX() {
		return _offsetMenuTextX;
	}

	public float getOffsetMenuTextY() {
		return _offsetMenuTextY;
	}

	public LMenu setOffsetMenuTextX(float x) {
		this._offsetMenuTextX = x;
		return this;
	}

	public LMenu setOffsetMenuTextY(float y) {
		this._offsetMenuTextX = y;
		return this;
	}

	public float getLeftOffsetMoveMenu() {
		return _leftOffsetMoveMenu;
	}

	public LMenu setLeftOffsetMoveMenu(float left) {
		this._leftOffsetMoveMenu = left;
		return this;
	}

	@Override
	public LMenu setFont(IFont f) {
		this._font = f;
		return this;
	}

	@Override
	public IFont getFont() {
		return this._font;
	}

	@Override
	public LColor getFontColor() {
		return _fontColor.cpy();
	}

	@Override
	public LMenu setFontColor(LColor f) {
		this._fontColor = f;
		return this;
	}

	@Override
	public String getUIName() {
		return "Menu";
	}

	@Override
	public void destory() {
		for (int i = 0, size = _items.size; i < size; i++) {
			MenuItem item = _items.get(i);
			if (item != null) {
				item.close();
				item = null;
			}
		}
		_items.clear();
		if (!_defUI) {
			if (_tab != null) {
				_tab.close();
			}
			if (_mainpanel != null) {
				_mainpanel.close();
			}
		}
	}

}
