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
package loon.core.graphics.component;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.RectBox;
import loon.core.graphics.LComponent;
import loon.core.graphics.LFont;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.core.input.LInputFactory.Touch;
import loon.utils.collection.Array;

/**
 * LGame菜单栏，用户可以隐藏大量按钮到其中，直到选中菜单时才动态展露，而非选中时则恢复隐藏.(此组件允许用户自行替换UI)
 */
public class LMenu extends LComponent {

	private LFont font;
	private float width;
	private float maxwidth;
	private float taby;

	private LTexture mainpanel;
	private LTexture tab;
	boolean active;

	public int xslot;
	public int yslot;
	public float scroll;
	public float maxscroll;
	public float scrollspeed = 25f;
	static LMenu selected;
	private float cellWidth = 32f;
	private float cellHeight = 32f;
	float paddingx = 2f;
	float paddingy = 50f;
	int rows = 1;

	public final static int MOVE_LEFT = 0;

	public final static int MOVE_RIGHT = 1;

	private int item_left_offset = 8;

	private int item_top_offset = 0;

	public Array<MenuItem> item = new Array<MenuItem>();

	private SpriteBatch batch;

	private int tabWidth, tabHeight;

	int type = MOVE_LEFT;

	String label;

	public LMenu(String label) {
		this(LFont.getDefaultFont(), label, 100, 50);
	}

	public LMenu(LFont font, String label, int width, int height) {
		this(font, label, width, height, DefUI.getDefaultTextures(3), DefUI
				.getDefaultTextures(2));
	}

	public LMenu(LFont font, String label, int width, int height, LTexture tab,
			LTexture main) {
		super(0, 0, width, height);
		this.label = label;
		this.font = font;
		this.batch = new SpriteBatch();
		this.mainpanel = main;
		this.tab = tab;
		this.tabWidth = width;
		this.tabHeight = height;
		this.maxwidth = getScreenWidth() / 4;
		this.maxwidth += this.cellWidth + this.paddingx;
	}

	public RectBox tagbounds(int type) {
		if (type == MOVE_LEFT) {

			return new RectBox(this.width - 2, getTaby(), tabWidth, tabHeight);
		} else if (type == MOVE_RIGHT) {
			return new RectBox(getScreenWidth() - 2 - tabWidth + this.width,
					getTaby(), tabWidth, tabHeight);
		}
		return null;
	}

	public RectBox panelbounds(int type) {
		if (type == MOVE_LEFT) {
			return new RectBox(-1, 0, this.width, getScreenHeight());
		} else if (type == MOVE_RIGHT) {
			return new RectBox(getScreenWidth() - this.width - 2, 0,
					this.width, getScreenHeight());
		}
		return null;
	}

	public float getTaby() {
		return this.taby;
	}

	public void setTaby(float taby) {
		this.taby = taby;
	}

	public MenuItem add(String label) {
		return add(label, null);
	}

	public MenuItem add(String label, MenuItemClick click) {
		return add(new LMenu.MenuItem(this, DefUI.getDefaultTextures(3), label,
				click));
	}

	public MenuItem add(String label, String file, MenuItemClick click) {
		return add(new LMenu.MenuItem(this, LTextures.loadTexture(file), label, click));
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
		return add(new LMenu.MenuItem(this, texture, false, label, x, y, w, h,
				click));
	}

	public MenuItem add(MenuItem item) {
		if (this.xslot > this.maxwidth / (this.cellWidth + this.paddingx * 2)) {
			this.xslot = 1;
			this.yslot += 1;
			this.rows += 1;
		}
		item.xslot = this.xslot;
		item.yslot = this.yslot;
		this.item.add(item);
		this.xslot += 1;
		return item;
	}

	public static interface MenuItemClick {
		public void onClick(MenuItem item);
	};

	public class MenuItem {
		LTexture texture;
		LMenu parent;
		int index;
		String label;
		boolean keep = false;
		public float x;
		public float y;
		public float yslot;
		public float xslot;
		public float itemWidth;
		public float itemHeight;
		private boolean clicked;
		private boolean localpos = false, localsize = false;

		private MenuItemClick _itemclick;

		MenuItem(LMenu parent, LTexture tex, String label, MenuItemClick click) {
			this.texture = tex;
			this.parent = parent;
			if (parent != null) {
				parent.add(this);
				this.index = parent.item.size();
			}
			this.label = label;
			this._itemclick = click;
		}

		MenuItem(LMenu parent, LTexture tex, boolean keep, String label,
				MenuItemClick click) {
			this.texture = tex;
			this.parent = parent;
			if (parent != null) {
				parent.add(this);
				this.index = parent.item.size();
			}
			this.label = label;
			this.keep = keep;
			this._itemclick = click;
		}

		MenuItem(LMenu parent, LTexture tex, boolean keep, String label,
				float x, float y, MenuItemClick click) {
			this.x = x;
			this.y = y;
			this.texture = tex;
			this.localpos = true;
			this.parent = parent;
			if (parent != null) {
				parent.add(this);
				this.index = parent.item.size();
			}
			this.label = label;
			this.keep = keep;
			this._itemclick = click;
		}

		MenuItem(LMenu parent, LTexture tex, boolean keep, String label,
				float x, float y, float w, float h, MenuItemClick click) {
			this.x = x;
			this.y = y;
			this.localsize = true;
			this.itemWidth = w;
			this.itemHeight = h;
			this.texture = tex;
			this.localpos = true;
			this.parent = parent;
			if (parent != null) {
				parent.add(this);
				this.index = parent.item.size();
			}
			this.label = label;
			this.keep = keep;
			this._itemclick = click;
		}

		public void draw(SpriteBatch batch) {

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
						x = Float.MIN_VALUE;
					} else if (x < Float.MIN_VALUE) {
						x = 0;
					}
					if (y > Float.MAX_VALUE) {
						y = Float.MIN_VALUE;
					} else if (y < Float.MIN_VALUE) {
						y = 0;
					}
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
				if (bounds().contains(input.getTouchX(), input.getTouchY())
						&& Touch.isDown()) {
					batch.setColor(0.5f, 0.5f, 0.5f, 1.0f);
					if (input.isTouchPressed(Touch.LEFT) && (!this.clicked)) {
						if (this._itemclick != null) {
							this._itemclick.onClick(this);
						}
						this.clicked = true;
					}
				} else {
					batch.setColor(1f, 1f, 1f, 1f);
				}
				if (!input.isTouchPressed(Touch.LEFT)) {
					this.clicked = false;
				}
				if (texture != null) {
					batch.draw(this.texture, this.x + 3f, this.y
							+ this.parent.paddingy + this.parent.scroll,
							this.itemWidth, this.itemHeight);
				}
				if (this.label != null) {
					batch.drawString(
							label,
							this.x
									+ 3f
									+ (itemWidth / 2 - font.stringWidth(label) / 2),
							this.y + this.parent.paddingy + this.parent.scroll
									- 2);
				}

				batch.setColor(1f, 1f, 1f, 1f);

			} else {
				if (bounds().contains(input.getTouchX(), input.getTouchY())
						&& Touch.isDown()) {
					batch.setColor(0.5F, 0.5F, 0.5F, 1.0F);
					if ((input.isTouchPressed(Touch.LEFT)) && (!this.clicked)) {
						if (this._itemclick != null) {
							this._itemclick.onClick(this);
						}
						this.clicked = true;
					}
				} else {
					batch.setColor(1f, 1f, 1f, 1f);
				}
				if (!input.isTouchPressed(Touch.LEFT)) {
					this.clicked = false;
				}
				if (texture != null) {
					batch.draw(this.texture, this.x, this.y, this.itemWidth,
							this.itemHeight);
				}
				if (this.label != null) {
					batch.drawString(
							this.label,
							this.x
									+ (itemWidth / 2 - font.stringWidth(label) / 2),
							this.y - 2);
				}

				batch.setColor(1f, 1f, 1f, 1f);

			}
		}

		public RectBox bounds() {
			return new RectBox(this.x + 3f, this.y + this.parent.paddingy
					+ this.parent.scroll, this.itemWidth, this.itemHeight);
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

	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component,
			LTexture[] buttonImage) {
		if (batch != null) {
			LFont oldfont = g.getFont();
			int oldcolor = g.getColorARGB();
			batch.begin();
			batch.setAlpha(0.7f);
			switch (type) {
			case MOVE_LEFT:
				if ((selected == this) || (selected == null)) {
					batch.draw(this.tab, this.width - 2, getTaby(), tabWidth,
							tabHeight);
					if (label != null) {
						batch.drawString(
								this.label,
								this.width
										- 2
										+ (tabWidth / 2 - font
												.stringWidth(label) / 2),
								getTaby()
										+ (tabHeight / 2 + font.getHeight() / 2)
										- 5);
					}
				}
				if ((this.active) || (this.width > 0)) {
					batch.draw(mainpanel, 1f, 0, this.width, getScreenHeight());
					if (this.width == this.maxwidth) {
						for (int i = 0; i < this.item.size(); i++) {
							this.item.get(i).draw(batch);
						}
					}
				}

				break;

			case MOVE_RIGHT:
				// developing...

				break;
			}
			batch.resetColor();
			batch.end();
			g.setColor(oldcolor);
			g.setFont(oldfont);
		}

	}

	private boolean mouseSelect = false;

	public void update(long elapsedTime) {
		if (!visible) {
			return;
		}
		super.update(elapsedTime);

		if (!isFocusable()) {
			return;
		}

		switch (type) {
		case MOVE_LEFT:
			if (!this.active) {
				if (tagbounds(type).contains(input.getTouchX(),
						input.getTouchY())
						&& (input.isTouchPressed(Touch.LEFT)
								|| input.isTouchReleased(Touch.LEFT) || input
									.isMoving()) && (selected == null)) {
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

					if (((tagbounds(type).contains(input.getTouchX(),
							input.getTouchY())) || (panelbounds(type).contains(
							input.getTouchX(), input.getTouchY())))) {
						if (this.width < this.maxwidth)
							this.width += 0.3F * (this.maxwidth - this.width)
									* (elapsedTime / 100f);
						else {
							this.width = this.maxwidth;
						}
						if (this.width > this.maxwidth - 2) {
							this.width = this.maxwidth;
						}
						if (input.isMoving()) {
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
						mouseSelect = false;
					}
				}
			}

			break;

		case MOVE_RIGHT:

			// developing...
			break;
		}

	}

	public boolean isMouseSelect() {
		return mouseSelect;
	}

	public float getPanelWidth() {
		return maxwidth;
	}

	public void setPanelWidth(float w) {
		this.maxwidth = w;
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

	public static LMenu getSelected() {
		return selected;
	}

	public static void setSelected(LMenu selected) {
		LMenu.selected = selected;
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

	@Override
	public String getUIName() {
		return "Menu";
	}

	public void dispose() {
		super.dispose();
		if (batch != null) {
			batch.dispose();
		}
	}

}
