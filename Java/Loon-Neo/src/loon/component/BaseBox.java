/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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

import loon.LSysException;
import loon.LSystem;
import loon.LTexture;
import loon.canvas.LColor;
import loon.font.FontSet;
import loon.font.IFont;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.StringUtils;

public class BaseBox extends AbstractBox {

	public static final TextAlignment ALIGN_LEFT = TextAlignment.LEFT;
	public static final TextAlignment ALIGN_CENTER = TextAlignment.CENTER;
	public static final TextAlignment ALIGN_RIGHT = TextAlignment.RIGHT;

	protected LColor _focusedColor = LColor.yellow;

	protected float _itemsOffsetX;
	protected float _itemsOffsetY;
	protected float _lineHeight;
	protected int _numberOfMenus;
	protected BoxItem[] _menuItems;

	protected String _wideFlag;
	protected MenuMode _mode;

	public static enum MenuMode {
		NORMAL, OTHER;
	}

	public static enum TextAlignment {
		LEFT, CENTER, RIGHT;
	}

	public static class BoxItem implements FontSet<BoxItem> {

		private IFont font;
		private LColor fontColor;
		private String text;

		private boolean isEnable;
		private TextAlignment align;
		private float x1;
		private float x2;
		private float y1;
		private float y2;

		public BoxItem(IFont font, String text, boolean isEnable, TextAlignment align) {
			this(font, text, isEnable, align, LColor.white);
		}

		public BoxItem(IFont font, String text, boolean isEnable, TextAlignment align, LColor color) {
			this.font = font;
			this.text = text;
			this.isEnable = isEnable;
			this.align = align;
			this.fontColor = color;
		}

		public BoxItem(IFont font, String text) {
			this(font, text, true, TextAlignment.LEFT);
		}

		public BoxItem(IFont font, String text, TextAlignment align) {
			this(font, text, true, align);
		}

		public String getText() {
			return this.text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public boolean isEnable() {
			return this.isEnable;
		}

		public void setEnable(boolean isEnable) {
			this.isEnable = isEnable;
		}

		public TextAlignment getAlign() {
			return this.align;
		}

		public void setAlign(TextAlignment align) {
			this.align = align;
		}

		public int getItemWidth() {
			return this.font.stringWidth(this.text);
		}

		public int getItemHeight() {
			return this.font.getHeight();
		}

		public void setMenuCoordinates(float x, float y) {
			this.x1 = x;
			this.x2 = (x + this.font.stringWidth(this.text));
			this.y1 = y;
			this.y2 = (y + 31.0F);
		}

		public float getX1() {
			return this.x1;
		}

		public float getX2() {
			return this.x2;
		}

		public float getY1() {
			return this.y1;
		}

		public float getY2() {
			return this.y2;
		}

		public boolean isFocused(float x, float y) {
			if ((x >= this.x1) && (x <= this.x2) && (y >= this.y1) && (y <= this.y2)) {
				return true;
			}
			return false;
		}

		public void draw(GLEx g) {
			this.font.drawString(g, this.text, this.x1, this.y1, this.fontColor);
		}

		public void draw(GLEx g, LColor color) {
			this.font.drawString(g, this.text, this.x1, this.y1, color);
		}

		@Override
		public String toString() {
			return this.text;
		}

		@Override
		public BoxItem setFont(IFont font) {
			this.font = font;
			return this;
		}

		@Override
		public IFont getFont() {
			return this.font;
		}

		@Override
		public BoxItem setFontColor(LColor color) {
			this.fontColor = color;
			return this;
		}

		@Override
		public LColor getFontColor() {
			return fontColor.cpy();
		}
	}

	public BaseBox(IFont font, int w, int h, int numberOfMenus) {
		super(font);
		this._numberOfMenus = numberOfMenus;
		this._menuItems = new BoxItem[numberOfMenus];
		init(w, h);
	}

	public BaseBox(IFont font, int w, int h, String[] menuItems) {
		super(font);
		this._numberOfMenus = menuItems.length;
		this._menuItems = new BoxItem[menuItems.length];
		for (int i = 0; i < menuItems.length; i++) {
			this._menuItems[i] = new BoxItem(font, menuItems[i]);
		}
		init(w, h);
	}

	public BaseBox(IFont font, int w, int h) {
		super(font);
		this._numberOfMenus = 1;
		this._menuItems = new BoxItem[] { new BoxItem(font, LSystem.EMPTY) };
		init(w, h);
	}

	@Override
	protected void init(int w, int h) {
		super.init(w, h);
		this._itemsOffsetX = (this.font.stringWidth("H") * 0.5f);
		this._mode = MenuMode.NORMAL;
		this._wideFlag = "n";
		this._boxWidth = (MathUtils.round(this._itemsOffsetX * 2f) + this.font.stringWidth("HHHH"));
		this.dirty();
	}

	@Override
	public void dirty() {
		if (this._mode == MenuMode.NORMAL) {
			int lh = this.font.getHeight();
			this._lineHeight = (lh * 1.134146f);
			this._itemsOffsetY = (lh / 4f);
			this._boxHeight = (lh / 2 + MathUtils.round(this._lineHeight * this._numberOfMenus));
			this._borderW = 3f;
		}
	}

	public void setMenuMode(MenuMode mode) {
		this._mode = mode;
	}

	public String getMenuMode() {
		return this._mode.name();
	}

	@Override
	public void setLocation(float x, float y) {
		super.setLocation(x, y);
		setMenuCoordinates();
	}

	public LColor getFocusedColor() {
		return _focusedColor;
	}

	public void setFocusedColor(LColor c) {
		_focusedColor = c;
	}

	public void setEnabled(int menuIndex, boolean enable) {
		this._menuItems[menuIndex].setEnable(enable);
	}

	public void draw(GLEx g, int focusedIndex, LColor c) {
		draw(g, this._boxX, this._boxY, focusedIndex, c);
	}

	protected void draw(GLEx g, float x, float y, int focusedIndex, LColor c) {
		if ((focusedIndex >= this._menuItems.length) || (focusedIndex < -1)) {
			return;
		}
		drawBorder(g, x, y, c);
		for (int i = 0; i < this._menuItems.length; i++) {
			LColor color;
			if (!this._menuItems[i].isEnable()) {
				color = LColor.gray;
			} else {
				if (focusedIndex == i) {
					color = _focusedColor;
				} else {
					color = this.fontColor;
				}
			}
			this._menuItems[i].draw(g, color);
		}
	}

	@Override
	protected void drawBorder(GLEx g, float x, float y, LColor c) {
		this._boxX = x;
		this._boxY = y;
		super.drawBorder(g, this._boxX, this._boxY, c);
		g.setColor(LColor.green);
		for (int i = 0; i < this._menuItems.length; i++) {
			float x1 = this._menuItems[i].getX1();
			float x2 = this._menuItems[i].getX2();
			float y1 = this._menuItems[i].getY1();
			float y2 = this._menuItems[i].getY2();
			g.drawRect(x1, y1, x2 - x1, y2 - y1);
		}
	}

	public void setMenuCoordinates() {
		int lh = getLineHeight();
		for (int i = 0; i < this._menuItems.length; i++) {
			switch (this._menuItems[i].getAlign()) {
			case LEFT:
				this._menuItems[i].setMenuCoordinates(this._boxX + this._itemsOffsetX,
						this._boxY + this._itemsOffsetY + lh * i);
				break;
			case CENTER:
				int itemW = getMenuItemWidth(i);
				this._menuItems[i].setMenuCoordinates(this._boxX + (getWidth() / 2 - itemW / 2),
						this._boxY + this._itemsOffsetY + lh * i);
				break;
			case RIGHT:
				this._menuItems[i].setMenuCoordinates(this._boxX + this._itemsOffsetX,
						this._boxY + this._itemsOffsetY + lh * i);
				break;
			}
		}
		if (this._mode == MenuMode.NORMAL) {
			int max = 0;
			for (BoxItem item : this._menuItems) {
				max = MathUtils.max(max, item.getItemWidth());
			}
			if (max >= 110)
				this._wideFlag = "w";
			else {
				this._wideFlag = "n";
			}
		}
	}

	public int getMenuFocus(float x, float y) {
		for (int i = 0; i < this._menuItems.length; i++) {
			if (this._menuItems[i].isFocused(x, y)) {
				return i;
			}
		}
		return -1;
	}

	public void setItemsOffsetX(float itemsOffsetX) {
		this._itemsOffsetX = itemsOffsetX;
	}

	public int getItemsOffsetX() {
		return MathUtils.round(this._itemsOffsetX);
	}

	public int getItemsOffsetY() {
		return MathUtils.round(this._itemsOffsetY);
	}

	public int getLineHeight() {
		return MathUtils.round(this._lineHeight);
	}

	public String getMenuText(int menuIndex) {
		return this._menuItems[menuIndex].getText();
	}

	public int getMenuItemWidth(int menuIndex) {
		return this._menuItems[menuIndex].getItemWidth();
	}

	public int getMenuItemHeight() {
		return this._menuItems[0].getItemHeight();
	}

	public int getItemCount() {
		return this._menuItems.length;
	}

	public BoxItem[] getAllMenuItems() {
		return this._menuItems;
	}

	public BoxItem getMenuItem(int index) {
		return this._menuItems[index];
	}

	public void setBoxWidth(float boxWidth) {
		this._boxWidth = MathUtils.round(boxWidth);
		dirty();
	}

	public void setMenuItem(int menuIndex, BoxItem menuItem) {
		if ((menuIndex < 0) || (menuIndex >= this._menuItems.length)) {
			return;
		}
		this._menuItems[menuIndex] = menuItem;
		dirty();
	}

	public void addMenuItem(BoxItem menuItem) {
		if ((this._menuItems.length == 1) && (StringUtils.isEmpty(this._menuItems[0].getText()))) {
			setMenuItem(0, menuItem);
		} else {
			addMenuItem(this._menuItems.length, menuItem);
		}
	}

	public void addMenuItem(int menuIndex, BoxItem menuItem) throws LSysException {
		if ((menuIndex < 0) || (menuIndex > this._menuItems.length)) {
			return;
		}
		BoxItem[] arr = new BoxItem[this._menuItems.length + 1];
		int i = 0;
		for (int j = 0; i < arr.length; i++) {
			if (menuIndex == i) {
				arr[i] = menuItem;
				j++;
			} else {
				arr[i] = this._menuItems[(i - j)];
			}
		}
		this._menuItems = arr;
		this._numberOfMenus = this._menuItems.length;
		dirty();
	}

	public void setAllMenuItem(String[] menuItems) {
		this._menuItems = new BoxItem[menuItems.length];
		for (int i = 0; i < menuItems.length; i++) {
			this._menuItems[i] = new BoxItem(this.font, menuItems[i]);
		}
		this._numberOfMenus = menuItems.length;
		dirty();
	}

	public void setAllMenuItem(BoxItem[] menuItems) {
		this._menuItems = menuItems;
		this._numberOfMenus = menuItems.length;
		dirty();
	}

	public void setBoxTexture(LTexture windowImage) {
		this._textureBox = windowImage;
		this._boxWidth = this._textureBox.getWidth();
		this._boxHeight = this._textureBox.getHeight();
	}

}
