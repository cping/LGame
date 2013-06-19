package loon.core.graphics;

import loon.action.ActionBind;
import loon.action.map.Field2D;
import loon.core.LObject;
import loon.core.LRelease;
import loon.core.LSystem;
import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
import loon.core.graphics.component.ClickListener;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.TextureUtils;
import loon.core.graphics.opengl.LTexture.Format;
import loon.core.input.LInput;
import loon.core.input.LInputFactory.Key;

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
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.1.1
 */

public abstract class LComponent extends LObject implements ActionBind, LRelease {

	public static interface CallListener {

		public void act(long elapsedTime);

	}

	public ClickListener Click;

	public void SetClick(ClickListener c) {
		Click = c;
	}

	public ClickListener GetClick() {
		return Click;
	}

	public CallListener Call;

	public void SetCall(CallListener u) {
		Call = u;
	}

	public CallListener GetCall() {
		return Call;
	}

	// 容器
	private LContainer parent;

	private LTexture[] imageUI;

	protected boolean elastic;

	protected boolean autoDestroy;

	protected boolean isClose;

	protected boolean isFull;

	// 渲染状态
	public boolean customRendering;

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

	protected LTexture background;

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
	@Override
	public boolean isContainer() {
		return false;
	}

	/**
	 * 更新组件状态
	 * 
	 */
	@Override
	public void update(long elapsedTime) {
		if (isClose) {
			return;
		}
		if (parent != null) {
			validatePosition();
		}
		if (Call != null) {
			Call.act(elapsedTime);
		}
	}

	public abstract void createUI(GLEx g, int x, int y, LComponent component,
			LTexture[] buttonImage);

	/**
	 * 渲染当前组件画面于指定绘图器之上
	 * 
	 * @param g
	 */
	public void createUI(GLEx g) {
		if (isClose) {
			return;
		}
		if (!this.visible) {
			return;
		}
		final int width = this.getWidth();
		final int height = this.getHeight();

		if (rotation != 0) {
			float centerX = this.screenX + width / 2;
			float centerY = this.screenY + height / 2;
			g.rotate(centerX, centerY, rotation);
		} else if (!(scaleX == 1f && scaleY == 1f)) {
			g.scale(scaleX, scaleY);
		} else if (this.elastic) {
			g.setClip(this.screenX, this.screenY, width, height);
		}
		// 变更透明度
		if (alpha > 0.1 && alpha < 1.0) {
			g.setAlpha(alpha);
			if (background != null) {
				g.drawTexture(background, this.screenX, this.screenY, width,
						height);
			}
			if (this.customRendering) {
				this.createCustomUI(g, this.screenX, this.screenY, width,
						height);
			} else {
				this.createUI(g, this.screenX, this.screenY, this, this.imageUI);
			}
			g.setAlpha(1F);
			// 不变更
		} else {
			if (background != null) {
				g.drawTexture(background, this.screenX, this.screenY, width,
						height);
			}
			if (this.customRendering) {
				this.createCustomUI(g, this.screenX, this.screenY, width,
						height);
			} else {
				this.createUI(g, this.screenX, this.screenY, this, this.imageUI);
			}
		}
		if (rotation != 0 || !(scaleX == 1f && scaleY == 1f)) {
			g.restore();
		} else if (this.elastic) {
			g.clearClip();
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
	protected void createCustomUI(GLEx g, int x, int y, int w, int h) {
	}

	public boolean contains(int x, int y) {
		return contains(x, y, 0, 0);
	}

	public boolean contains(int x, int y, int width, int height) {
		return (this.visible)
				&& (x >= this.screenX
						&& y >= this.screenY
						&& ((x + width) <= (this.screenX + this.width * scaleX)) && ((y + height) <= (this.screenY + this.height
						* scaleY)));
	}

	public boolean intersects(int x1, int y1) {
		return (this.visible)
				&& (x1 >= this.screenX
						&& x1 <= this.screenX + this.width * scaleX
						&& y1 >= this.screenY && y1 <= this.screenY
						+ this.height * scaleY);
	}

	public boolean intersects(LComponent comp) {
		return (this.visible)
				&& (comp.isVisible())
				&& (this.screenX + this.width * scaleX >= comp.screenX
						&& this.screenX <= comp.screenX + comp.width
						&& this.screenY + this.height * scaleY >= comp.screenY && this.screenY <= comp.screenY
						+ comp.height);
	}

	@Override
	public void dispose() {
		this.isClose = true;
		this.desktop.setComponentStat(this, false);
		if (this.parent != null) {
			this.parent.remove(this);
		}
		this.desktop = Desktop.EMPTY_DESKTOP;
		this.input = null;
		this.parent = null;
		if (imageUI != null) {
			for (int i = 0; i < imageUI.length; i++) {
				imageUI[i].destroy();
				imageUI[i] = null;
			}
			this.imageUI = null;
		}
		if (background != null) {
			this.background.destroy();
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

	public void setBounds(float dx, float dy, int width, int height) {
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

	@Override
	public void setX(Integer x) {
		if (this.getX() != x || x == 0) {
			super.setX(x);
			this.validatePosition();
		}
	}

	@Override
	public void setX(float x) {
		if (this.getX() != x || x == 0) {
			super.setX(x);
			this.validatePosition();
		}
	}

	@Override
	public void setY(Integer y) {
		if (this.getY() != y || y == 0) {
			super.setY(y);
			this.validatePosition();
		}
	}

	@Override
	public void setY(float y) {
		if (this.getY() != y || y == 0) {
			super.setY(y);
			this.validatePosition();
		}
	}

	@Override
	public void setLocation(Vector2f location) {
		setLocation(location.x, location.y);
	}

	@Override
	public void setLocation(float dx, float dy) {
		if (this.getX() != dx || this.getY() != dy || dx == 0 || dy == 0) {
			super.setLocation(dx, dy);
			this.validatePosition();
		}
	}

	@Override
	public void move(float dx, float dy) {
		if (dx != 0 || dy != 0) {
			if (dx > -100 && dx < 100 && dy > -100 && dy < 100) {
				super.move(dx, dy);
				this.validatePosition();
			}
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

	@Override
	public int getWidth() {
		return (int) (this.width * scaleX);
	}

	@Override
	public int getHeight() {
		return (int) (this.height * scaleY);
	}

	public RectBox getCollisionBox() {
		if (rect == null) {
			rect = new RectBox(screenX, screenY, width * scaleX, height
					* scaleY);
		} else {
			rect.setBounds(screenX, screenY, width * scaleX, height * scaleY);
		}
		return rect;
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
		if (this.input.getKeyPressed() == Key.ENTER) {

			this.transferFocus();

		} else {
			this.transferFocusBackward();
		}
	}

	public LTexture[] getImageUI() {
		return this.imageUI;
	}

	public void setImageUI(LTexture[] imageUI, boolean processUI) {
		if (imageUI != null) {
			this.width = imageUI[0].getWidth();
			this.height = imageUI[0].getHeight();
		}

		this.imageUI = imageUI;
	}

	public void setImageUI(int index, LTexture imageUI) {
		if (imageUI != null) {
			this.width = imageUI.getWidth();
			this.height = imageUI.getHeight();
		}
		this.imageUI[index] = imageUI;
	}

	public abstract String getUIName();

	public LTexture getBackground() {
		return background;
	}

	public void clearBackground() {
		this.setBackground(new LTexture(1, 1, true, Format.SPEED));
	}

	public void setBackground(String fileName) {
		this.setBackground(new LTexture(fileName, Format.SPEED,
				android.graphics.Bitmap.Config.RGB_565));
	}

	public void setBackground(LColor color) {
		setBackground(TextureUtils
				.createTexture(getWidth(), getHeight(), color));
	}

	public void setBackground(LTexture background) {
		if (background == null) {
			return;
		}
		LTexture oldImage = this.background;
		if (oldImage != background && oldImage != null) {
			oldImage.destroy();
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
		return cam_y == 0 ? y() : cam_y;
	}

	protected void createCustomUI(int w, int h) {
	}

	public boolean isClose() {
		return isClose;
	}

	public boolean isAutoDestroy() {
		return autoDestroy;
	}

	public void setAutoDestroy(boolean autoDestroy) {
		this.autoDestroy = autoDestroy;
	}

	@Override
	public Field2D getField2D() {
		return null;
	}

	float scaleX = 1, scaleY = 1;

	public void setScale(final float s) {
		this.setScale(s, s);
	}

	@Override
	public void setScale(final float sx, final float sy) {
		if (this.scaleX == sx && this.scaleY == sy) {
			return;
		}
		this.scaleX = sx;
		this.scaleY = sy;
	}

	@Override
	public float getScaleX() {
		return this.scaleX;
	}

	@Override
	public float getScaleY() {
		return this.scaleY;
	}

	@Override
	public boolean isBounded() {
		return true;
	}

	@Override
	public boolean inContains(int x, int y, int w, int h) {
		if (parent != null) {
			return parent.contains(x, y, w, h);
		}
		return false;
	}

	@Override
	public RectBox getRectBox() {
		return getCollisionBox();
	}

	@Override
	public int getContainerWidth() {
		return parent.getWidth();
	}

	@Override
	public int getContainerHeight() {
		return parent.getHeight();
	}

}
