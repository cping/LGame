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
	protected float itemsOffsetX;
	protected float itemsOffsetY;
	protected float lineHeight;
	protected int numberOfMenus;
	protected BoxItem[] menuItems;

	protected static final LColor FOCUSED_COLOR = new LColor(LColor.yellow);
	protected String wideFlag;
	protected MenuMode mode;

	public static enum MenuMode {
		NORMAL, OTHER;
	}

	public static enum TextAlignment {
		LEFT, CENTER, RIGHT;
	}

	public static class BoxItem implements FontSet<BoxItem> {

		private IFont font;
		private String text;

		private boolean isEnable;
		private TextAlignment align;
		private float x1;
		private float x2;
		private float y1;
		private float y2;

		public BoxItem(IFont font, String text, boolean isEnable,
				TextAlignment align) {
			this.font = font;
			this.text = text;
			this.isEnable = isEnable;
			this.align = align;
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
			if ((x >= this.x1) && (x <= this.x2) && (y >= this.y1)
					&& (y <= this.y2)) {
				return true;
			}
			return false;
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
			this.font  = font;
			return this;
		}

		@Override
		public IFont getFont() {
			return this.font;
		}
	}

	public BaseBox(IFont font, int w, int h, int numberOfMenus) {
		super(font);
		this.numberOfMenus = numberOfMenus;
		this.menuItems = new BoxItem[numberOfMenus];
		init(w, h);
	}

	public BaseBox(IFont font, int w, int h, String[] menuItems) {
		super(font);
		this.numberOfMenus = menuItems.length;
		this.menuItems = new BoxItem[menuItems.length];
		for (int i = 0; i < menuItems.length; i++) {
			this.menuItems[i] = new BoxItem(font, menuItems[i]);
		}
		init(w, h);
	}

	public BaseBox(IFont font, int w, int h) {
		super(font);
		this.numberOfMenus = 1;
		this.menuItems = new BoxItem[] { new BoxItem(font, "") };
		init(w, h);
	}

	@Override
	protected void init(int w, int h) {
		super.init(w, h);
		this.itemsOffsetX = (this.font.stringWidth("H") * 0.5f);
		this.mode = MenuMode.NORMAL;
		this.wideFlag = "n";
		this._boxWidth = (MathUtils.round(this.itemsOffsetX * 2f) + this.font
				.stringWidth("HHHH"));
		this.dirty();
	}

	@Override
	public void dirty() {
		if (this.mode == MenuMode.NORMAL) {
			int lh = this.font.getHeight();
			this.lineHeight = (lh * 1.134146f);
			this._boxHeight = (lh / 2 + MathUtils.round(this.lineHeight
					* this.numberOfMenus));
			this.itemsOffsetY = (lh / 4f);
			this._borderW = 3f;
		}
	}

	public void setMenuMode(MenuMode mode) {
		this.mode = mode;
	}

	public String getMenuMode() {
		return this.mode.name();
	}

	@Override
	public void setLocation(float x, float y) {
		super.setLocation(x, y);
		setMenuCoordinates();
	}

	public void setEnabled(int menuIndex, boolean enable) {
		this.menuItems[menuIndex].setEnable(enable);
	}

	public void draw(GLEx g, int focusedIndex, LColor c) {
		draw(g, this._boxX, this._boxY, focusedIndex, c);
	}

	protected void draw(GLEx g, float x, float y, int focusedIndex, LColor c) {
		if ((focusedIndex >= this.menuItems.length) || (focusedIndex < -1)) {
			return;
		}
		drawBorder(g, x, y, c);
		for (int i = 0; i < this.menuItems.length; i++) {
			LColor color;
			if (!this.menuItems[i].isEnable()) {
				color = LColor.gray;
			} else {
				if (focusedIndex == i) {
					color = FOCUSED_COLOR;
				} else {
					color = this.fontColor;
				}
			}
			this.menuItems[i].draw(g, color);
		}
	}

	@Override
	protected void drawBorder(GLEx g, float x, float y, LColor c) {
		this._boxX = x;
		this._boxY = y;
		super.drawBorder(g, this._boxX, this._boxY, c);
		g.setColor(LColor.green);
		for (int i = 0; i < this.menuItems.length; i++) {
			float x1 = this.menuItems[i].getX1();
			float x2 = this.menuItems[i].getX2();
			float y1 = this.menuItems[i].getY1();
			float y2 = this.menuItems[i].getY2();
			g.drawRect(x1, y1, x2 - x1, y2 - y1);
		}
	}

	public void setMenuCoordinates() {
		int lh = getLineHeight();
		for (int i = 0; i < this.menuItems.length; i++) {
			switch (this.menuItems[i].getAlign()) {
			case LEFT:
				this.menuItems[i].setMenuCoordinates(this._boxX
						+ this.itemsOffsetX, this._boxY + this.itemsOffsetY
						+ lh * i);
				break;
			case CENTER:
				int itemW = getMenuItemWidth(i);
				this.menuItems[i].setMenuCoordinates(this._boxX
						+ (getWidth() / 2 - itemW / 2), this._boxY
						+ this.itemsOffsetY + lh * i);
				break;
			case RIGHT:
				this.menuItems[i].setMenuCoordinates(this._boxX
						+ this.itemsOffsetX, this._boxY + this.itemsOffsetY
						+ lh * i);
				break;
			}
		}
		if (this.mode == MenuMode.NORMAL) {
			int max = 0;
			for (BoxItem item : this.menuItems) {
				max = MathUtils.max(max, item.getItemWidth());
			}
			if (max >= 110)
				this.wideFlag = "w";
			else {
				this.wideFlag = "n";
			}
		}
	}

	public int getMenuFocus(float x, float y) {
		for (int i = 0; i < this.menuItems.length; i++) {
			if (this.menuItems[i].isFocused(x, y)) {
				return i;
			}
		}
		return -1;
	}

	public void setItemsOffsetX(float itemsOffsetX) {
		this.itemsOffsetX = itemsOffsetX;
	}

	public int getItemsOffsetX() {
		return MathUtils.round(this.itemsOffsetX);
	}

	public int getItemsOffsetY() {
		return MathUtils.round(this.itemsOffsetY);
	}

	public int getLineHeight() {
		return MathUtils.round(this.lineHeight);
	}

	public String getMenuText(int menuIndex) {
		return this.menuItems[menuIndex].getText();
	}

	public int getMenuItemWidth(int menuIndex) {
		return this.menuItems[menuIndex].getItemWidth();
	}

	public int getMenuItemHeight() {
		return this.menuItems[0].getItemHeight();
	}

	public int getItemCount() {
		return this.menuItems.length;
	}

	public BoxItem[] getAllMenuItems() {
		return this.menuItems;
	}

	public BoxItem getMenuItem(int index) {
		return this.menuItems[index];
	}

	public void setBoxWidth(float boxWidth) {
		this._boxWidth = MathUtils.round(boxWidth);
		dirty();
	}

	public void setMenuItem(int menuIndex, BoxItem menuItem) {
		if ((menuIndex < 0) || (menuIndex >= this.menuItems.length)) {
			return;
		}
		this.menuItems[menuIndex] = menuItem;
		dirty();
	}

	public void addMenuItem(BoxItem menuItem) {
		if ((this.menuItems.length == 1)
				&& (StringUtils.isEmpty(this.menuItems[0].getText()))) {
			setMenuItem(0, menuItem);
		} else {
			addMenuItem(this.menuItems.length, menuItem);
		}
	}

	public void addMenuItem(int menuIndex, BoxItem menuItem)
			throws IllegalArgumentException {
		if ((menuIndex < 0) || (menuIndex > this.menuItems.length)) {
			return;
		}
		BoxItem[] arr = new BoxItem[this.menuItems.length + 1];
		int i = 0;
		for (int j = 0; i < arr.length; i++) {
			if (menuIndex == i) {
				arr[i] = menuItem;
				j++;
			} else {
				arr[i] = this.menuItems[(i - j)];
			}
		}
		this.menuItems = arr;
		this.numberOfMenus = this.menuItems.length;
		dirty();
	}

	public void setAllMenuItem(String[] menuItems) {
		this.menuItems = new BoxItem[menuItems.length];
		for (int i = 0; i < menuItems.length; i++) {
			this.menuItems[i] = new BoxItem(this.font, menuItems[i]);
		}
		this.numberOfMenus = menuItems.length;
		dirty();
	}

	public void setAllMenuItem(BoxItem[] menuItems) {
		this.menuItems = menuItems;
		this.numberOfMenus = menuItems.length;
		dirty();
	}

	public void setBoxTexture(LTexture windowImage) {
		this._textureBox = windowImage;
		this._boxWidth = this._textureBox.getWidth();
		this._boxHeight = this._textureBox.getHeight();
	}

}
