package org.loon.framework.javase.game.core.graphics;

import java.awt.Image;
import java.awt.Shape;
import java.awt.event.KeyEvent;

import org.loon.framework.javase.game.core.LInput;
import org.loon.framework.javase.game.core.LObject;
import org.loon.framework.javase.game.core.LRelease;
import org.loon.framework.javase.game.core.LSystem;
import org.loon.framework.javase.game.core.geom.RectBox;
import org.loon.framework.javase.game.core.geom.Vector2D;
import org.loon.framework.javase.game.core.graphics.device.LGraphics;

/**
 * 
 * Copyright 2008 - 2011
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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1.1
 */

public abstract class LComponent extends LObject implements LRelease {

	protected Shape oldClip;

	// 容器
	private LContainer parent;

	protected boolean elastic;

	private LImage[] imageUI;

	protected boolean isFull;

	// 渲染状态
	public boolean customRendering;

	// 透明度
	protected float alpha = 1.0f;

	// 居中位置，组件坐标与大小
	private int cam_x, cam_y, width, height;

	// 屏幕位置
	protected int screenX, screenY;

	// 操作提示
	protected String tooltip;

	// 组件标记
	protected boolean visible = true;

	protected boolean enabled = true;

	// 是否为焦点
	protected boolean focusable = true;

	// 是否已选中
	protected boolean selected = false;

	protected Desktop desktop = Desktop.EMPTY_DESKTOP;

	private RectBox screenRect;

	protected LInput input;

	protected boolean isLimitMove;

	protected LImage background;

	/**
	 * 构造可用组件
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public LComponent(int x, int y, int width, int height) {
		this.setLocation(x, y);
		this.width = width;
		this.height = height;
		this.screenRect = LSystem.screenRect;
		if (this.width == 0) {
			this.width = 10;
		}
		if (this.height == 0) {
			this.height = 10;
		}
	}

	public int getScreenWidth() {
		return screenRect.width;
	}

	public int getScreenHeight() {
		return screenRect.height;
	}

	/**
	 * 让当前组件向指定的中心点位置居中
	 * 
	 * @param x
	 * @param y
	 */
	public void moveCamera(int x, int y) {
		if (!this.isLimitMove) {
			setLocation(x, y);
			return;
		}
		int tempX = x;
		int tempY = y;
		int tempWidth = getWidth() - screenRect.width;
		int tempHeight = getHeight() - screenRect.height;

		int limitX = tempX + tempWidth;
		int limitY = tempY + tempHeight;

		if (width >= screenRect.width) {
			if (limitX > tempWidth) {
				tempX = screenRect.width - width;
			} else if (limitX < 1) {
				tempX = x();
			}
		} else {
			return;
		}
		if (height >= screenRect.height) {
			if (limitY > tempHeight) {
				tempY = screenRect.height - height;
			} else if (limitY < 1) {
				tempY = y();
			}
		} else {
			return;
		}
		this.cam_x = tempX;
		this.cam_y = tempY;
		this.setLocation(cam_x, cam_y);
	}

	protected boolean isNotMoveInScreen(int x, int y) {
		if (!this.isLimitMove) {
			return false;
		}
		int width = getWidth() - screenRect.width;
		int height = getHeight() - screenRect.height;
		int limitX = x + width;
		int limitY = y + height;
		if (getWidth() >= screenRect.width) {
			if (limitX >= width - 1) {
				return true;
			} else if (limitX <= 1) {
				return true;
			}
		} else {
			if (!screenRect.contains(x, y, getWidth(), getHeight())) {
				return true;
			}
		}
		if (getHeight() >= screenRect.height) {
			if (limitY >= height - 1) {
				return true;
			} else if (limitY <= 1) {
				return true;
			}
		} else {
			if (!screenRect.contains(x, y, getWidth(), getHeight())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 返回当前组件对象是否为容器
	 * 
	 * @return
	 */
	public boolean isContainer() {
		return false;
	}

	/**
	 * 更新组件状态
	 * 
	 */
	public void update(long timer) {
		if (parent != null) {
			validatePosition();
		}
	}

	public abstract void createUI(LGraphics g, int x, int y,
			LComponent component, LImage[] buttonImage);

	/**
	 * 渲染当前组件画面于指定绘图器之上
	 * 
	 * @param g
	 */
	public void createUI(LGraphics g) {
		if (!this.visible) {
			return;
		}
		try {
			if (this.elastic) {
				this.oldClip = g.getClip();
				g.clipRect(this.getScreenX(), this.getScreenY(), this
						.getWidth(), this.getHeight());
			}
			// 变更透明度
			if (alpha > 0.1 && alpha < 1.0) {
				g.setAlpha(alpha);
				if (background != null) {
					g.drawImage(background, this.screenX, this.screenY,
							this.width, this.height);
				}
				if (this.customRendering) {
					this.createCustomUI(g, this.screenX, this.screenY,
							this.width, this.height);
				} else {
					this.createUI(g, this.screenX, this.screenY, this,
							this.imageUI);

				}
				g.setAlpha(1.0F);
				// 不变更
			} else {
				if (background != null) {
					g.drawImage(background, this.screenX, this.screenY,
							this.width, this.height);
				}
				if (this.customRendering) {
					this.createCustomUI(g, this.screenX, this.screenY,
							this.width, this.height);
				} else {
					this.createUI(g, this.screenX, this.screenY, this,
							this.imageUI);

				}
			}
			if (this.elastic) {
				g.setClip(this.oldClip);
			}
		} catch (Exception ex) {

		}
	}

	/**
	 * 自定义UI
	 * 
	 * @param g
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	protected void createCustomUI(LGraphics g, int x, int y, int w, int h) {
	}

	public boolean contains(int x, int y) {
		return contains(x, y, 0, 0);
	}

	public boolean contains(int x, int y, int width, int height) {
		return (this.visible)
				&& (x >= this.screenX && y >= this.screenY
						&& ((x + width) <= (this.screenX + this.width)) && ((y + height) <= (this.screenY + this.height)));
	}

	public boolean intersects(int x1, int y1) {
		return (this.visible)
				&& (x1 >= this.screenX && x1 <= this.screenX + this.width
						&& y1 >= this.screenY && y1 <= this.screenY
						+ this.height);
	}

	public boolean intersects(LComponent comp) {
		return (this.visible)
				&& (comp.isVisible())
				&& (this.screenX + this.width >= comp.screenX
						&& this.screenX <= comp.screenX + comp.width
						&& this.screenY + this.height >= comp.screenY && this.screenY <= comp.screenY
						+ comp.height);
	}

	public void dispose() {
		this.desktop.setComponentStat(this, false);
		if (this.parent != null) {
			this.parent.remove(this);
		}
		this.desktop = Desktop.EMPTY_DESKTOP;
		this.input = null;
		this.parent = null;
		if (imageUI != null) {
			for (int i = 0; i < imageUI.length; i++) {
				imageUI[i].dispose();
			}
			this.imageUI = null;
		}
		if (background != null) {
			this.background.dispose();
			this.background = null;
		}
		this.selected = false;
		this.visible = false;
	}

	public boolean isVisible() {
		return this.visible;
	}

	public void setVisible(boolean visible) {
		if (this.visible == visible) {
			return;
		}
		this.visible = visible;
		this.desktop.setComponentStat(this, this.visible);
	}

	public boolean isEnabled() {
		return (this.parent == null) ? this.enabled
				: (this.enabled && this.parent.isEnabled());
	}

	public void setEnabled(boolean b) {
		if (this.enabled == b) {
			return;
		}
		this.enabled = b;
		this.desktop.setComponentStat(this, this.enabled);
	}

	public boolean isSelected() {
		return this.selected;
	}

	final void setSelected(boolean b) {
		this.selected = b;
	}

	public boolean requestFocus() {
		return this.desktop.selectComponent(this);
	}

	public void transferFocus() {
		if (this.isSelected() && this.parent != null) {
			this.parent.transferFocus(this);
		}
	}

	public void transferFocusBackward() {
		if (this.isSelected() && this.parent != null) {
			this.parent.transferFocusBackward(this);
		}
	}

	public boolean isFocusable() {
		return this.focusable;
	}

	public void setFocusable(boolean b) {
		this.focusable = b;
	}

	public LContainer getContainer() {
		return this.parent;
	}

	final void setContainer(LContainer container) {
		this.parent = container;

		this.validatePosition();
	}

	final void setDesktop(Desktop desktop) {
		if (this.desktop == desktop) {
			return;
		}

		this.desktop = desktop;
		this.input = desktop.input;
	}

	public void setBounds(int dx, int dy, int width, int height) {
		setLocation(dx, dy);
		if (this.width != width || this.height != height) {
			this.width = width;
			this.height = height;
			if (width == 0) {
				width = 1;
			}
			if (height == 0) {
				height = 1;
			}
			this.validateSize();
		}
	}

	public void setX(Integer x) {
		if (this.getX() != x || x == 0) {
			super.setX(x);
			this.validatePosition();
		}
	}

	public void setX(double x) {
		if (this.getX() != x || x == 0) {
			super.setX(x);
			this.validatePosition();
		}
	}

	public void setY(Integer y) {
		if (this.getY() != y || y == 0) {
			super.setY(y);
			this.validatePosition();
		}
	}

	public void setY(double y) {
		if (this.getY() != y || y == 0) {
			super.setY(y);
			this.validatePosition();
		}
	}

	public void setLocation(Vector2D location) {
		setLocation(location.x, location.y);
	}

	public void setLocation(double dx, double dy) {
		if (this.getX() != dx || this.getY() != dy || dx == 0 || dy == 0) {
			super.setLocation(dx, dy);
			this.validatePosition();
		}
	}

	public void move(double dx, double dy) {
		if (dx != 0 || dy != 0) {
			super.move(dx, dy);
			this.validatePosition();
		}
	}

	public void setSize(int w, int h) {
		if (this.width != w || this.height != h) {
			this.width = w;
			this.height = h;
			if (this.width == 0) {
				this.width = 1;
			}
			if (this.height == 0) {
				this.height = 1;
			}
			this.validateSize();
		}
	}

	protected void validateSize() {
	}

	public void validatePosition() {
		if (parent != null) {
			this.screenX = location.x() + this.parent.getScreenX();
			this.screenY = location.y() + this.parent.getScreenY();
		} else {
			this.screenX = location.x();
			this.screenY = location.y();
		}
	}

	public int getScreenX() {
		return this.screenX;
	}

	public int getScreenY() {
		return this.screenY;
	}

	protected void setHeight(int height) {
		this.height = height;
	}

	protected void setWidth(int width) {
		this.width = width;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public RectBox getCollisionBox() {
		return new RectBox(screenX, screenY, width, height);
	}

	public String getToolTipText() {
		return this.tooltip;
	}

	public void setToolTipText(String text) {
		this.tooltip = text;
	}

	// 鼠标操作
	protected void processTouchPressed() {

	}

	protected void processTouchReleased() {
	}

	protected void processTouchClicked() {
	}

	protected void processTouchMoved() {
	}

	protected void processTouchDragged() {
	}

	protected void processTouchEntered() {
	}

	protected void processTouchExited() {
	}

	// 键盘操作
	protected void processKeyPressed() {

	}

	protected void processKeyReleased() {
	}

	void keyPressed() {
		this.checkFocusKey();
		this.processKeyPressed();
	}

	/**
	 * 检测键盘事件焦点
	 * 
	 */
	protected void checkFocusKey() {
		if (this.input.getKeyPressed() == KeyEvent.VK_ENTER) {
			this.transferFocus();
		} else {
			this.transferFocusBackward();
		}
	}

	public float getAlpha() {
		return alpha;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	public LImage[] getImageUI() {
		return this.imageUI;
	}

	public void setImageUI(LImage[] imageUI, boolean processUI) {
		if (imageUI != null) {
			this.width = imageUI[0].getWidth();
			this.height = imageUI[0].getHeight();
		}

		this.imageUI = imageUI;
	}

	public void setImageUI(int index, LImage imageUI) {
		if (imageUI != null) {
			this.width = imageUI.getWidth();
			this.height = imageUI.getHeight();
		}
		this.imageUI[index] = imageUI;
	}

	public abstract String getUIName();

	public LImage getBackground() {
		return background;
	}

	public void clearBackground() {
		this.setBackground(new LImage(1, 1, true));
	}

	public void setBackground(String fileName) {
		this.setBackground(LImage.createImage(fileName));
	}

	public void setBackground(Image img) {
		this.setBackground(new LImage(img));
	}

	public void setBackground(LColor color) {
		LImage image = LImage.createImage(getWidth(), getHeight(), false);
		LGraphics g = image.getLGraphics();
		g.setColor(color);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.dispose();
		setBackground(image);
	}

	public void setBackground(LImage background) {
		if (background == null) {
			return;
		}
		LImage oldImage = this.background;
		if (oldImage != background && oldImage != null) {
			oldImage.dispose();
			oldImage = null;
		}
		this.background = background;
		this.setAlpha(1.0F);
		this.width = background.getWidth();
		this.height = background.getHeight();
		if (this.width <= 0) {
			this.width = 1;
		}
		if (this.height <= 0) {
			this.height = 1;
		}
	}

	public int getCamX() {
		return cam_x == 0 ? x() : cam_x;
	}

	public int getCamY() {
		return cam_y == 0 ? x() : cam_y;
	}

	protected void createCustomUI(int w, int h) {
	}

}
