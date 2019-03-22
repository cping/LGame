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
import loon.LSystem;
import loon.LTexture;
import loon.LTextures;
import loon.canvas.LColor;
import loon.component.skin.MenuSkin;
import loon.component.skin.SkinManager;
import loon.event.SysTouch;
import loon.event.Updateable;
import loon.font.FontSet;
import loon.font.IFont;
import loon.font.LFont;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.opengl.LSTRDictionary;
import loon.utils.TArray;

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

	private class ClickMenu implements Updateable {

		private MenuItemClick click;

		private MenuItem item;

		ClickMenu(MenuItemClick c, MenuItem i) {
			this.click = c;
			this.item = i;
		}

		@Override
		public void action(Object a) {
			if (click != null && item != null) {
				click.onClick(item);
			}
		}

	}

	public class MenuItem implements LRelease {
		LTexture texture;
		LMenu parent;
		int index;
		String label;
		public float x;
		public float y;
		public float yslot;
		public float xslot;
		public float itemWidth;
		public float itemHeight;
		public float offsetX;
		public float offsetY;

		boolean keep = false;
		private boolean visible = true;
		private boolean clicked = false;
		private boolean localpos = false, localsize = false;

		private IFont _font;
		private MenuItemClick _itemclick;

		MenuItem(LMenu parent, LTexture tex, String label, MenuItemClick click) {
			this(SkinManager.get().getMenuSkin().getFont(), parent, tex, false,
					label, click);
		}

		MenuItem(IFont font, LMenu parent, LTexture tex, String label,
				MenuItemClick click) {
			this(font, parent, tex, false, label, click);
		}

		MenuItem(IFont font, LMenu parent, LTexture tex, boolean keep,
				String label, MenuItemClick click) {
			this.texture = tex;
			this.parent = parent;
			if (parent != null) {
				parent.add(this);
				this.index = parent.items.size;
			}
			this.label = label;
			this.keep = keep;
			this._itemclick = click;
			this._font = font;
			if (tex == null) {
				this.keep = false;
			}
		}

		MenuItem(LMenu parent, LTexture tex, boolean keep, String label,
				float x, float y, MenuItemClick click) {
			this(SkinManager.get().getMenuSkin().getFont(), parent, tex, true,
					label, x, y, 0, 0, click);
		}

		MenuItem(IFont font, LMenu parent, LTexture tex, boolean keep,
				String label, float x, float y, MenuItemClick click) {
			this(font, parent, tex, true, label, x, y, 0, 0, click);
		}

		MenuItem(IFont font, LMenu parent, LTexture tex, boolean keep,
				String label, float x, float y, float w, float h,
				MenuItemClick click) {
			this.x = x;
			this.y = y;
			this.itemWidth = w;
			this.itemHeight = h;
			this.texture = tex;
			if (w < 1 && h < 1) {
				this.localsize = true;
			} else {
				this.localsize = false;
			}
			this.texture = tex;
			if (x < 1 && y < 1) {
				this.localpos = true;
			} else {
				this.localpos = false;
			}
			this.parent = parent;
			if (parent != null) {
				parent.add(this);
				this.index = parent.items.size;
			}
			this.label = label;
			this.keep = keep;
			this._itemclick = click;
			this._font = font;
			if (tex == null) {
				this.keep = false;
			}
		}

		public boolean isVisible() {
			return this.visible;
		}

		public void setVisible(boolean v) {
			this.visible = v;
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

		public void draw(GLEx g) {
			if (!visible) {
				return;
			}
			int color = g.color();
			IFont font = g.getFont();
			try {
				boolean check = (SysTouch.isDown() || SysTouch.isDrag());
				Vector2f pos = SysTouch.getLocation();
				if (this.parent != null) {
					if (!localpos) {
						this.x = (this.parent.cellWidth * this.xslot
								+ this.parent.cellWidth / this.itemWidth + this.xslot
								* this.parent.paddingx)
								- this.parent.item_left_offset;
						this.y = (this.parent.cellHeight * this.yslot
								+ this.parent.cellHeight / this.itemHeight + this.yslot
								* this.parent.paddingy);

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
					if (parent.type == LMenu.MOVE_RIGHT) {
						float posX = parent.getScreenWidth()
								- parent.main_panel_size;
						this.x = posX + x;
					}
					if (!localsize) {
						if (!this.keep || texture == null) {
							this.itemWidth = this.parent.cellWidth;
							this.itemHeight = this.parent.cellHeight;
						} else {
							this.itemWidth = this.texture.getWidth();
							this.itemHeight = this.texture.getHeight();
						}
					}
					if (bounds().contains(pos.x, pos.y) && check) {
						g.setTint(0.5f, 0.5f, 0.5f, 1.0f);
						if (check && (!this.clicked)) {
							ClickMenu menu = new ClickMenu(this._itemclick,
									this);
							LSystem.load(menu);
							this.clicked = true;
						}
					}
					if (!check) {
						this.clicked = false;
					}
					if (texture != null) {
						g.draw(this.texture, this.x + 3f, this.y
								+ this.parent.paddingy + this.parent.scroll,
								this.itemWidth, this.itemHeight,
								parent.baseColor);
					}
					if (this.label != null) {
						font.drawString(
								g,
								label,
								(this.x + 3f + (itemWidth / 2 - font
										.stringWidth(label) / 2)) + offsetX,
								(this.y + this.parent.paddingy
										+ this.parent.scroll - font.getAscent() - 2)
										+ offsetY, parent.fontColor);
					}

				} else {
					if (bounds().contains(pos.x, pos.y) && (check)) {
						g.setTint(0.5f, 0.5f, 0.5f, 1.0f);
						if (check && (!this.clicked)) {
							ClickMenu menu = new ClickMenu(this._itemclick,
									this);
							LSystem.load(menu);
							this.clicked = true;
						}
					}
					if (!check) {
						this.clicked = false;
					}
					if (texture != null) {
						g.draw(this.texture, this.x, this.y, this.itemWidth,
								this.itemHeight, parent.baseColor);
					}
					if (this.label != null) {
						font.drawString(
								g,
								this.label,
								(this.x + (itemWidth / 2
										- font.stringWidth(label) / 2 - font
											.getAscent())) + offsetX,
								(this.y - 2) + offsetY, parent.fontColor);
					}

				}
			} finally {
				g.setColor(color);
				g.setFont(font);
			}
		}

		private RectBox itemrect;

		public RectBox bounds() {
			if (parent.type == LMenu.MOVE_LEFT) {
				if (itemrect == null) {
					itemrect = new RectBox(this.x + 3f, this.y
							+ this.parent.paddingy + this.parent.scroll,
							this.itemWidth, this.itemHeight);
				} else {
					itemrect.setBounds(this.x + 3f, this.y
							+ this.parent.paddingy + this.parent.scroll,
							this.itemWidth, this.itemHeight);
				}
			} else if (parent.type == LMenu.MOVE_RIGHT) {

				if (itemrect == null) {
					itemrect = new RectBox(this.x + 3f, this.y
							+ this.parent.paddingy + this.parent.scroll,
							this.itemWidth, this.itemHeight);
				} else {
					itemrect.setBounds(this.x + 3f, this.y
							+ this.parent.paddingy + this.parent.scroll,
							this.itemWidth, this.itemHeight);
				}
			}
			return itemrect;
		}

		public LMenu getParent() {
			return this.parent;
		}

		public String getLabel() {
			return this.label;
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
			if (texture != null) {
				texture.close();
				texture = null;
			}
		}
	}

	private IFont font;
	private float width;
	private float main_panel_size;
	private float tabY;
	private LColor fontColor = LColor.white;

	private LTexture mainpanel;
	private LTexture tab;
	private boolean active, supportScroll;

	public int xslot;
	public int yslot;
	public float scroll;
	public float maxscroll;
	public float scrollspeed = 25f;
	private LMenu selected;
	private float cellWidth = 32f;
	private float cellHeight = 32f;
	private float paddingx = 2f;
	private float paddingy = 50f;
	private float alphaMenu = 0.7f;
	private int rows = 1;

	public final static int MOVE_LEFT = 0;

	public final static int MOVE_RIGHT = 1;

	private int item_left_offset = 10;

	private int item_top_offset = 0;

	public TArray<MenuItem> items = new TArray<MenuItem>(10);

	private int tabWidth, tabHeight;

	private int type = MOVE_RIGHT;

	private String label;

	private boolean _defUI;

	public LMenu(int move_type, String label) {
		this(move_type, SkinManager.get().getMenuSkin().getFont(), label, 100,
				50);
	}

	public LMenu(int move_type, String label, int w, int h) {
		this(move_type, SkinManager.get().getMenuSkin().getFont(), label, w, h);
	}

	public LMenu(int move_type, IFont font, String label, int width, int height) {
		this(move_type, font, label, width, height, SkinManager.get()
				.getMenuSkin().getTabTexture(), SkinManager.get().getMenuSkin()
				.getMainTexture(), 0, 0, true);
	}

	public LMenu(int move_type, IFont font, String label, int width,
			int height, String tabfile, String mainfile) {
		this(move_type, font, label, width, height, LTextures
				.loadTexture(tabfile), LTextures.loadTexture(mainfile), 0, 0,
				false);
	}

	public LMenu(int move_type, IFont font, String label, int width,
			int height, String tabfile, String mainfile, int taby, int mainsize) {
		this(move_type, font, label, width, height, LTextures
				.loadTexture(tabfile), LTextures.loadTexture(mainfile), taby,
				mainsize, false);
	}

	public LMenu(int move_type, IFont font, String label, int width,
			int height, LTexture tab, LTexture main, int taby) {
		this(move_type, font, label, width, height, tab, main, taby, 0, false);
	}

	public LMenu(int move_type, IFont font, String label, int width,
			int height, LTexture tab, LTexture main, int taby, int mainsize) {
		this(move_type, font, label, width, height, tab, main, taby, mainsize,
				false);
	}

	public LMenu(int move_type, IFont font, String label, int width,
			int height, LTexture tab, LTexture main, int taby, int mainsize,
			boolean defUI) {
		this(move_type, font, label, width, height, tab, main, taby, mainsize,
				defUI, SkinManager.get().getMenuSkin().getFontColor());
	}

	public LMenu(MenuSkin skin, int move_type, String label, int width,
			int height, int taby, int mainsize, boolean defUI) {
		this(move_type, skin.getFont(), label, width, height, skin
				.getTabTexture(), skin.getMainTexture(), taby, mainsize, defUI,
				skin.getFontColor());
	}

	public LMenu(int move_type, IFont font, String label, int width,
			int height, LTexture tab, LTexture main, int taby, int mainsize,
			boolean defUI, LColor color) {
		super(0, 0, width, height);
		this.fontColor = color;
		this.type = move_type;
		this.label = label;
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
		this._defUI = defUI;
		if (type > MOVE_RIGHT) {
			throw LSystem.runThrow("Type:" + type
					+ ", The Menu display mode is not supported !");
		}
	}

	private RectBox tabRec;

	private RectBox tagbounds(int type) {
		if (type == MOVE_LEFT) {
			if (tabRec == null) {
				tabRec = new RectBox(this.width, getTaby(), tabWidth, tabHeight);
			} else {
				tabRec.setBounds(this.width, getTaby(), tabWidth, tabHeight);
			}
		} else if (type == MOVE_RIGHT) {
			float posX = this.getScreenWidth() - this.width - this.tabWidth;
			if (tabRec == null) {
				tabRec = new RectBox(posX, getTaby(), tabWidth, tabHeight);
			} else {
				tabRec.setBounds(posX, getTaby(), tabWidth, tabHeight);
			}
		}
		return tabRec;
	}

	private RectBox mianRec;

	private RectBox panelbounds(int type) {
		if (type == MOVE_LEFT) {
			if (mianRec == null) {
				mianRec = new RectBox(0, 0, this.width, getScreenHeight());
			} else {
				mianRec.setBounds(0, 0, this.width, getScreenHeight());
			}
		} else if (type == MOVE_RIGHT) {
			float posX = this.getScreenWidth() - this.width;
			if (mianRec == null) {
				mianRec = new RectBox(posX, 0, this.width, getScreenHeight());
			} else {
				mianRec.setBounds(posX, 0, this.width, getScreenHeight());
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
		return add(new LMenu.MenuItem(this, SkinManager.get().getMenuSkin()
				.getTabTexture(), label, click));
	}

	public MenuItem add(String label, String file, MenuItemClick click) {
		return add(new LMenu.MenuItem(this, LTextures.loadTexture(file), label,
				click));
	}

	public MenuItem add(String label, LTexture texture, MenuItemClick click) {
		return add(new LMenu.MenuItem(this, texture, label, click));
	}

	public MenuItem add(String label, LTexture texture, float x, float y,
			MenuItemClick click) {
		return add(new LMenu.MenuItem(this, texture, false, label, x, y, click));
	}

	public MenuItem add(String label, LTexture texture, float x, float y,
			float w, float h, MenuItemClick click) {
		return add(new LMenu.MenuItem(font, this, texture, false, label, x, y,
				w, h, click));
	}

	public MenuItem add(MenuItem item) {
		if (this.xslot > this.main_panel_size
				/ (this.cellWidth + this.paddingx * 2)) {
			this.xslot = 1;
			this.yslot += 1;
			this.rows += 1;
		}
		item.xslot = this.xslot;
		item.yslot = this.yslot;
		this.items.add(item);
		this.xslot += 1;
		if (item._font != null && item._font instanceof LFont) {
			LSTRDictionary.get().bind((LFont) item._font, item.label);
		}
		return item;
	}

	@Override
	public synchronized void createUI(GLEx g, int x, int y,
			LComponent component, LTexture[] buttonImage) {
		float alpha = g.alpha();
		try {
			g.setAlpha(alphaMenu);
			switch (type) {
			case MOVE_LEFT:
				if ((selected == this) || (selected == null)) {
					g.draw(this.tab, this.width, getTaby(), tabWidth,
							tabHeight, baseColor);
					if (label != null) {
						g.setAlpha(1f);
						font.drawString(
								g,
								this.label,
								this.width
										+ (tabWidth / 2 - font
												.stringWidth(label) / 2),
								getTaby()
										+ (tabHeight / 2 - font.getHeight() / 2)
										- 5, fontColor);
						g.setAlpha(alphaMenu);
					}
				}
				if ((this.active) || (this.width > 0)) {
					g.draw(mainpanel, 0, 0, this.width, getScreenHeight(),
							baseColor);
					if (this.width == this.main_panel_size) {
						for (int i = 0; i < this.items.size; i++) {
							this.items.get(i).draw(g);
						}
					}
				}

				break;

			case MOVE_RIGHT:
				if ((selected == this) || (selected == null)) {
					float posX = this.getScreenWidth() - this.width
							- this.tabWidth;
					g.draw(this.tab, posX, getTaby(), tabWidth, tabHeight,
							baseColor);
					if (label != null) {
						g.setAlpha(1f);
						font.drawString(
								g,
								this.label,
								posX
										+ (tabWidth / 2 - font
												.stringWidth(label) / 2),
								getTaby()
										+ (tabHeight / 2 - font.getHeight() / 2)
										- 5, fontColor);
						g.setAlpha(this.alphaMenu);
					}
				}
				if ((this.active) || (this.width > 0)) {
					float posX = this.getScreenWidth() - this.width;
					g.draw(mainpanel, posX, 0, this.width, getScreenHeight(),
							baseColor);
					if (this.width == this.main_panel_size) {
						for (int i = 0; i < this.items.size; i++) {
							this.items.get(i).draw(g);
						}
					}
				}

				break;
			}
		} finally {
			g.setAlpha(alpha);
		}
	}

	private boolean mouseSelect = false;

	public void update(long elapsedTime) {
		if (!isVisible()) {
			return;
		}
		super.update(elapsedTime);

		if (!this.active) {
			if (tagbounds(type).contains(SysTouch.getX(), SysTouch.getY())
					&& ((SysTouch.isDown() || SysTouch.isDrag()))
					&& (selected == null)) {
				this.active = true;
				this.mouseSelect = true;
				if (selected == null) {
					selected = this;
				}
			}
			if (this.width > 0) {
				this.width -= 0.3f * this.width * (elapsedTime / 100f);
			}

			if (this.width <= 2) {
				this.width = 0;
			}

		} else {
			this.mouseSelect = true;
			if (selected == this) {
				this.maxscroll = ((this.paddingy + this.cellHeight) * this.rows);
				if (this.scroll > this.maxscroll) {
					this.scroll = this.maxscroll;
				}
				if (this.scroll < -this.maxscroll) {
					this.scroll = (-this.maxscroll);
				}

				if (((tagbounds(type)
						.contains(SysTouch.getX(), SysTouch.getY())) || (panelbounds(type)
						.contains(SysTouch.getX(), SysTouch.getY())))) {
					if (this.width < this.main_panel_size)
						this.width += 0.3F
								* (this.main_panel_size - this.width)
								* (elapsedTime / 100f);
					else {
						this.width = this.main_panel_size;
					}
					if (this.width > this.main_panel_size - 2) {
						this.width = this.main_panel_size;
					}
					if (input.isMoving() && supportScroll) {
						if (input.getTouchDY() > 5) {
							this.scroll -= this.scrollspeed
									* (elapsedTime / 100f);
						} else if (input.getTouchDY() < -5) {
							this.scroll += this.scrollspeed
									* (elapsedTime / 100f);
						}
					}
				} else {

					if (selected == this) {
						selected = null;
					}
					this.active = false;
					this.mouseSelect = false;
				}
			}
		}
	}

	public boolean isMouseSelect() {
		return mouseSelect;
	}

	public float getPanelWidth() {
		return main_panel_size;
	}

	public void setPanelWidth(float w) {
		this.main_panel_size = w;
	}

	public int getItemLeftOffset() {
		return item_left_offset;
	}

	public void setItemLeftOffset(int left) {
		this.item_left_offset = left;
	}

	public int getItemTopOffset() {
		return item_top_offset;
	}

	public void setItemTopOffset(int top) {
		this.item_top_offset = top;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public float getScroll() {
		return scroll;
	}

	public void setScroll(float scroll) {
		this.scroll = scroll;
	}

	public float getScrollspeed() {
		return scrollspeed;
	}

	public void setScrollspeed(float scrollspeed) {
		this.scrollspeed = scrollspeed;
	}

	public LMenu getSelected() {
		return selected;
	}

	public float getCellWidth() {
		return cellWidth;
	}

	public void setCellWidth(float cellWidth) {
		this.cellWidth = cellWidth;
	}

	public float getCellHeight() {
		return cellHeight;
	}

	public void setCellHeight(float cellHeight) {
		this.cellHeight = cellHeight;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getXslot() {
		return xslot;
	}

	public void setXslot(int xslot) {
		this.xslot = xslot;
	}

	public int getYslot() {
		return yslot;
	}

	public void setYslot(int yslot) {
		this.yslot = yslot;
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

	public void setPaddingx(float paddingx) {
		this.paddingx = paddingx;
	}

	public float getPaddingy() {
		return paddingy;
	}

	public void setPaddingy(float paddingy) {
		this.paddingy = paddingy;
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

	public int getType() {
		return type;
	}

	public boolean isSupportScroll() {
		return supportScroll;
	}

	public void setSupportScroll(boolean s) {
		this.supportScroll = s;
	}

	public void setType(int t) {
		this.type = t;
	}

	public boolean isdefUI() {
		return _defUI;
	}

	public float getAlphaMenu() {
		return alphaMenu;
	}

	public void setAlphaMenu(float alphaMenu) {
		this.alphaMenu = alphaMenu;
	}

	@Override
	public String getUIName() {
		return "Menu";
	}

	@Override
	public LMenu setFont(IFont font) {
		this.font = font;
		return this;
	}

	@Override
	public IFont getFont() {
		return this.font;
	}

	public LColor getFontColor() {
		return fontColor;
	}

	public void setFontColor(LColor fontColor) {
		this.fontColor = fontColor;
	}

	@Override
	public void close() {
		super.close();
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
