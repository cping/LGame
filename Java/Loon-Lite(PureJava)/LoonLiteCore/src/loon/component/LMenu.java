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
		private float x;
		private float y;
		public int index;
		public float yslot;
		public float xslot;
		public float itemWidth;
		public float itemHeight;
		public float offsetX;
		public float offsetY;
		public float labelOffsetX;
		public float labelOffsetY;

		private RectBox itemrect;

		protected boolean _keep = false;

		private boolean visible = true;
		private boolean clicked = false;
		private boolean localpos = false, localsize = false;

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
				this.index = parent.items.size;
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
			this.x = x;
			this.y = y;
			this.itemWidth = w;
			this.itemHeight = h;
			if (w < 1 && h < 1) {
				this.localsize = true;
			} else {
				this.localsize = false;
			}
			if (x < 1 && y < 1) {
				this.localpos = true;
			} else {
				this.localpos = false;
			}
			this._parent = parent;
			if (_parent != null) {
				_parent.add(this);
				this.index = _parent.items.size;
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
			return this.visible;
		}

		public MenuItem setVisible(boolean v) {
			this.visible = v;
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
			if (!visible) {
				return;
			}
			final IFont font = _font == null ? g.getFont() : _font;
			if (this._parent != null) {
				if (!localpos) {
					this.x = (this._parent.cellWidth * this.xslot + this._parent.cellWidth / this.itemWidth
							+ this.xslot * this._parent.paddingx) - this._parent.item_left_offset;
					this.y = (this._parent.cellHeight * this.yslot + this._parent.cellHeight / this.itemHeight
							+ this.yslot * this._parent.paddingy);

					if (x > Float.MAX_VALUE) {
						x = 0;
					} else if (x < Float.MIN_VALUE) {
						x = 0;
					}
					if (y > Float.MAX_VALUE) {
						y = 0;
					} else if (y < Float.MIN_VALUE) {
						y = 0;
					}
				}
				if (_parent._moveType == LMenu.MOVE_RIGHT) {
					float posX = _parent.getScreenRight() - _parent.main_panel_size;
					this.x = posX + x;
				}
				if (!localsize) {
					if (!this._keep || _texture == null) {
						this.itemWidth = this._parent.cellWidth;
						this.itemHeight = this._parent.cellHeight;
					} else {
						this.itemWidth = this._texture.getWidth();
						this.itemHeight = this._texture.getHeight();
					}
				}
				if (pos != null && bounds().contains(pos.x, pos.y) && checked) {
					if ((isDown && !isDrag) && checked && (!this.clicked)) {
						ClickMenu menu = new ClickMenu(this._itemclick, this);
						LSystem.load(menu);
						this.clicked = true;
					}
				}
				if (!checked) {
					this.clicked = false;
				}
				if (_texture != null) {
					g.draw(this._texture, this.x + _parent._leftOffsetMoveMenu + offsetX,
							this.y + this._parent.paddingy + this._parent.scroll + offsetY, this.itemWidth,
							this.itemHeight, this.clicked ? LColor.gray : _parent._component_baseColor);
				}
				if (this._label != null) {
					font.drawString(g, _label,
							(this.x + _parent._leftOffsetMoveMenu + (itemWidth / 2 - font.stringWidth(_label) / 2))
									+ offsetX + labelOffsetX,
							(this.y + this._parent.paddingy + this._parent.scroll - font.getAscent() - 2) + offsetY
									+ labelOffsetY,
							this.clicked ? LColor.gray : _parent.fontColor);
				}

			} else {
				if (_parent != null) {
					if (bounds().contains(_parent.getTouchX(), _parent.getTouchY()) && checked) {
						if ((isDown && !isDrag) && checked && (!this.clicked)) {
							ClickMenu menu = new ClickMenu(this._itemclick, this);
							LSystem.load(menu);
							this.clicked = true;
						}
					}
				}
				if (!checked) {
					this.clicked = false;
				}
				if (_texture != null) {
					g.draw(this._texture, this.x + offsetX, this.y + offsetY, this.itemWidth, this.itemHeight);
				}
				if (this._label != null) {
					font.drawString(g, this._label,
							(this.x + (itemWidth / 2 - font.stringWidth(_label) / 2 - font.getAscent())) + offsetX
									+ labelOffsetX,
							(this.y - 2) + offsetY + labelOffsetY);
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
			return clicked;
		}

		public RectBox bounds() {
			if (_parent._moveType == LMenu.MOVE_LEFT) {
				if (itemrect == null) {
					itemrect = new RectBox(this.x + _parent._leftOffsetMoveMenu + offsetX,
							this.y + this._parent.paddingy + this._parent.scroll + offsetY, this.itemWidth,
							this.itemHeight);
				} else {
					itemrect.setBounds(this.x + _parent._leftOffsetMoveMenu + offsetX,
							this.y + this._parent.paddingy + this._parent.scroll + offsetY, this.itemWidth,
							this.itemHeight);
				}
			} else if (_parent._moveType == LMenu.MOVE_RIGHT) {
				if (itemrect == null) {
					itemrect = new RectBox(this.x + _parent._leftOffsetMoveMenu + offsetX,
							this.y + this._parent.paddingy + this._parent.scroll + offsetY, this.itemWidth,
							this.itemHeight);
				} else {
					itemrect.setBounds(this.x + _parent._leftOffsetMoveMenu + offsetX,
							this.y + this._parent.paddingy + this._parent.scroll + offsetY, this.itemWidth,
							this.itemHeight);
				}
			}
			return itemrect;
		}

		public LMenu getParent() {
			return this._parent;
		}

		public String getLabel() {
			return this._label;
		}

		public float getX() {
			return this.x;
		}

		public float getY() {
			return this.y;
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

	private IFont font;
	private float width;
	private float main_panel_size;
	private float tabY;
	private LColor fontColor = LColor.white;

	private LTexture mainpanel;
	private LTexture tab;
	private boolean active, supportScroll;

	private RectBox mianRec;
	private RectBox tabRec;

	public int xslot;
	public int yslot;
	public float scroll;
	public float maxscroll;
	public float scrollspeed = 25f;
	private LMenu selected;
	private float cellWidth = LSystem.LAYER_TILE_SIZE;
	private float cellHeight = LSystem.LAYER_TILE_SIZE;
	private float paddingx = 2f;
	private float paddingy = 50f;
	private float alphaMenu = 0.7f;
	private int rows = 1;

	public final static int MOVE_LEFT = 0;

	public final static int MOVE_RIGHT = 1;

	private float _menuSpeed = 1.5f;

	private float _offsetMenuTextX;

	private float _offsetMenuTextY;

	private int item_left_offset = 10;

	private int item_top_offset = 0;

	private TArray<MenuItem> items = new TArray<MenuItem>(10);

	private int tabWidth, tabHeight;

	private int _moveType = MOVE_RIGHT;

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
		this.fontColor = color;
		this._moveType = move_type;
		this._tablabel = label;
		this.font = font;
		this.tabY = taby;
		this.tab = tab;
		this.mainpanel = main;
		this.tabWidth = width;
		this.tabHeight = height;
		if (mainsize > 0) {
			this.main_panel_size = mainsize;
		} else {
			this.main_panel_size = getScreenWidth() / 4;
		}
		this.main_panel_size += this.cellWidth + this.paddingx;
		this._component_baseColor = LColor.white.cpy();
		this._defUI = defUI;
		if (_moveType > MOVE_RIGHT) {
			throw new LSysException("Type:" + _moveType + ", The Menu display mode is not supported !");
		}
		setLocation(MathUtils.ifloor(getScreenX()), getScreenY());
	}

	private RectBox tagbounds(int mt) {
		if (mt == MOVE_LEFT) {
			if (tabRec == null) {
				tabRec = new RectBox(this.width, getTaby(), tabWidth, tabHeight);
			} else {
				tabRec.setBounds(this.width, getTaby(), tabWidth, tabHeight);
			}
		} else if (_moveType == MOVE_RIGHT) {
			float posX = this.getScreenRight() - this.width - this.tabWidth;
			if (tabRec == null) {
				tabRec = new RectBox(posX, getTaby(), tabWidth, tabHeight);
			} else {
				tabRec.setBounds(posX, getTaby(), tabWidth, tabHeight);
			}
		}
		return tabRec;
	}

	private RectBox panelbounds(int mt) {
		if (mt == MOVE_LEFT) {
			if (mianRec == null) {
				mianRec = new RectBox(getDesktopLeft(), getDesktopTop(), this.width, getScreenHeight());
			} else {
				mianRec.setBounds(getDesktopLeft(), getDesktopTop(), this.width, getScreenHeight());
			}
		} else if (_moveType == MOVE_RIGHT) {
			float posX = this.getScreenRight() - this.width;
			if (mianRec == null) {
				mianRec = new RectBox(posX, getDesktopTop(), this.width, getScreenHeight());
			} else {
				mianRec.setBounds(posX, getDesktopTop(), this.width, getScreenHeight());
			}
		}
		return mianRec;
	}

	public float getTaby() {
		return this.tabY;
	}

	public void setTaby(float tabY) {
		this.tabY = tabY;
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
		return add(new LMenu.MenuItem(font, this, texture, false, label, x, y, w, h, click));
	}

	public MenuItem add(MenuItem item) {
		if (this.xslot > this.main_panel_size / (this.cellWidth + this.paddingx * 2)) {
			this.xslot = 1;
			this.yslot += 1;
			this.rows += 1;
		}
		item.xslot = this.xslot;
		item.yslot = this.yslot;
		this.items.add(item);
		this.xslot += 1;
		return item;
	}

	public MenuItem getItem(QueryEvent<MenuItem> item) {
		return items.find(item);
	}

	public MenuItem getItem(String name) {
		if (name == null) {
			return null;
		}
		for (int i = items.size - 1; i > -1; i--) {
			MenuItem item = items.get(i);
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
		for (int i = items.size - 1; i > -1; i--) {
			MenuItem item = items.get(i);
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
				if ((selected == this) || (selected == null)) {
					g.draw(this.tab, this.width, getTaby(), tabWidth, tabHeight,
							_component_baseColor.setAlpha(alphaMenu));
					if (_tablabel != null) {
						font.drawString(g, this._tablabel,
								this.width + (tabWidth / 2f - font.stringWidth(_tablabel) / 2f) + _offsetMenuTextX,
								getTaby() + getScreenY() + (tabHeight - font.getHeight()) / 2f + _offsetMenuTextY,
								fontColor);
					}
				}
				if ((this.active) || (this.width > 0)) {
					g.draw(mainpanel, getScreenX(), getScreenY(), this.width, getScreenHeight(), _component_baseColor);
					if (MathUtils.equal(this.width, this.main_panel_size)) {
						final TArray<MenuItem> list = this.items;
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
				if ((selected == this) || (selected == null)) {
					float posX = this.getScreenRight() - this.width - this.tabWidth;
					g.draw(this.tab, posX, getTaby() + getScreenY(), tabWidth, tabHeight,
							_component_baseColor.setAlpha(alphaMenu));
					if (_tablabel != null) {
						font.drawString(g, this._tablabel,
								posX + (tabWidth / 2 - font.stringWidth(_tablabel) / 2) + _offsetMenuTextX,
								getTaby() + (tabHeight - font.getHeight()) / 2f + _offsetMenuTextY, fontColor);
					}
				}
				if ((this.active) || (this.width > 0)) {
					float posX = this.getScreenRight() - this.width;
					g.draw(mainpanel, posX, getScreenY(), this.width, getScreenHeight(), _component_baseColor);
					if (MathUtils.equal(this.width, this.main_panel_size)) {
						final TArray<MenuItem> list = this.items;
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
		if (!this.active) {
			if (_tabOpening && (selected == null)) {
				this.active = true;
				this._mouseSelect = true;
				if (selected == null) {
					selected = this;
				}
			}
			if (this.width > 0) {
				this.width -= 0.3f * this.width * delta;
			}
			if (this.width <= 0) {
				this.width = 0;
			}
		} else {
			this._mouseSelect = true;
			if (selected == this) {
				final float menuCellHeight = this.paddingy + this.cellHeight;
				this.maxscroll = (menuCellHeight * this.rows);
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
					if (this.width < this.main_panel_size)
						this.width += 0.3F * (this.main_panel_size - this.width) * delta;
					else {
						this.width = this.main_panel_size;
					}
					if (this.width > this.main_panel_size - 2) {
						this.width = this.main_panel_size;
					}
					if (_input.isMoving() && supportScroll) {
						if (_input.getTouchDY() > 5) {
							this.scroll -= this.scrollspeed * delta;
						} else if (_input.getTouchDY() < -5) {
							this.scroll += this.scrollspeed * delta;
						}
					}
				} else {
					if (selected == this) {
						selected = null;
					}
					this.active = false;
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
		return main_panel_size;
	}

	public LMenu setPanelWidth(float w) {
		this.main_panel_size = w;
		return this;
	}

	public int getItemLeftOffset() {
		return item_left_offset;
	}

	public LMenu setItemLeftOffset(int left) {
		this.item_left_offset = left;
		return this;
	}

	public int getItemTopOffset() {
		return item_top_offset;
	}

	public LMenu setItemTopOffset(int top) {
		this.item_top_offset = top;
		return this;
	}

	public boolean isActive() {
		return active;
	}

	public LMenu setActive(boolean active) {
		this.active = active;
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
		return selected;
	}

	public float getCellWidth() {
		return cellWidth;
	}

	public LMenu setCellWidth(float cellWidth) {
		this.cellWidth = cellWidth;
		return this;
	}

	public float getCellHeight() {
		return cellHeight;
	}

	public LMenu setCellHeight(float cellHeight) {
		this.cellHeight = cellHeight;
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
		return paddingx;
	}

	public LMenu setPaddingx(float p) {
		this.paddingx = p;
		return this;
	}

	public float getPaddingy() {
		return paddingy;
	}

	public LMenu setPaddingy(float p) {
		this.paddingy = p;
		return this;
	}

	public int getRows() {
		return rows;
	}

	public int getTabWidth() {
		return tabWidth;
	}

	public int getTabHeight() {
		return tabHeight;
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
		return supportScroll;
	}

	public LMenu setSupportScroll(boolean s) {
		this.supportScroll = s;
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
		return alphaMenu;
	}

	public LMenu setAlphaMenu(float a) {
		this.alphaMenu = a;
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
		this.font = f;
		return this;
	}

	@Override
	public IFont getFont() {
		return this.font;
	}

	public LColor getFontColor() {
		return fontColor.cpy();
	}

	public LMenu setFontColor(LColor f) {
		this.fontColor = f;
		return this;
	}

	@Override
	public String getUIName() {
		return "Menu";
	}

	@Override
	public void destory() {
		for (int i = 0, size = items.size; i < size; i++) {
			MenuItem item = items.get(i);
			if (item != null) {
				item.close();
				item = null;
			}
		}
		items.clear();
		if (!_defUI) {
			if (tab != null) {
				tab.close();
			}
			if (mainpanel != null) {
				mainpanel.close();
			}
		}
	}

}
