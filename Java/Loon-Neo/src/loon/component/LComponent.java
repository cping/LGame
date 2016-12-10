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
package loon.component;

import loon.Director.Origin;
import loon.LObject;
import loon.LRelease;
import loon.LSystem;
import loon.LTexture;
import loon.Screen;
import loon.action.ActionBind;
import loon.action.map.Field2D;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.component.layout.BoxSize;
import loon.component.layout.LayoutConstraints;
import loon.component.layout.LayoutPort;
import loon.event.ClickListener;
import loon.event.SysInput;
import loon.event.SysKey;
import loon.event.SysTouch;
import loon.event.Touched;
import loon.event.TouchedClick;
import loon.event.Updateable;
import loon.geom.Affine2f;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.geom.XY;
import loon.opengl.GLEx;
import loon.opengl.TextureUtils;

public abstract class LComponent extends LObject<LContainer> implements
		ActionBind, XY, BoxSize, LRelease {

	// 默认锁定当前组件(否则可以拖动)
	protected boolean locked = true;

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	// 组件内部变量, 用于锁定当前组件的触屏（鼠标）与键盘事件
	protected boolean _touchLocked = false, _keyLocked = false;

	public void setTouchLocked(boolean locked) {
		this._touchLocked = locked;
	}

	public boolean isTouchLocked() {
		return this._touchLocked;
	}

	public void setKeyLocked(boolean locked) {
		this._keyLocked = locked;
	}

	public boolean isKeyLocked() {
		return this._keyLocked;
	}

	public static interface CallListener {

		public void act(long elapsedTime);

	}

	// 点击事件监听
	public ClickListener Click;

	private TouchedClick _touchListener;

	private final TouchedClick makeTouched() {
		if (_touchListener == null) {
			_touchListener = new TouchedClick();
		}
		if (Click != null) {
			_touchListener.addClickListener(Click);
		}
		this.Click = _touchListener;
		return _touchListener;
	}

	public LComponent all(Touched t) {
		makeTouched().setAllTouch(t);
		return this;
	}

	public LComponent down(Touched t) {
		makeTouched().setDownTouch(t);
		return this;
	}

	public LComponent up(Touched t) {
		makeTouched().setUpTouch(t);
		return this;
	}

	public LComponent drag(Touched t) {
		makeTouched().setDragTouch(t);
		return this;
	}

	public LComponent A(ClickListener c) {
		return addClickListener(c);
	}

	public LComponent addClickListener(ClickListener c) {
		this.Click = c;
		makeTouched();
		return this;
	}

	public LComponent S(ClickListener c) {
		return SetClick(c);
	}

	public LComponent SetClick(ClickListener c) {
		this.Click = c;
		return this;
	}

	public ClickListener getClick() {
		return Click;
	}

	// 循环事件监听
	public CallListener Call;

	public LComponent SC(CallListener u) {
		SetCall(u);
		return this;
	}

	public LComponent SetCall(CallListener u) {
		this.Call = u;
		return this;
	}

	public CallListener getCall() {
		return Call;
	}

	private Origin _origin = Origin.CENTER;

	private LTexture[] _imageUI = null;

	protected boolean elastic = false;

	protected boolean autoDestroy = true;

	protected boolean isClose = false;

	protected boolean isFull = false;

	private boolean isSelectDraw = false;
	// 渲染状态
	public boolean customRendering = false;

	// 居中位置，组件坐标与大小
	private int cam_x, cam_y;

	private float _width, _height;
	// 缩放比例
	private float scaleX = 1f, scaleY = 1f;
	// 屏幕位置
	protected int screenX, screenY;

	// 中心点
	protected float pivotX = -1, pivotY = -1;

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

	protected SysInput input;

	protected boolean isLimitMove;

	protected LTexture _background;

	protected LayoutConstraints constraints = null;

	protected LColor baseColor = null;

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
		this._width = width;
		this._height = height;
		this.screenRect = LSystem.viewSize.getRect();
		if (this._width == 0) {
			this._width = 10;
		}
		if (this._height == 0) {
			this._height = 10;
		}

	}

	public Screen getScreen() {
		return desktop.input;
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
		int tempWidth = (int) (getWidth() - screenRect.width);
		int tempHeight = (int) (getHeight() - screenRect.height);

		int limitX = tempX + tempWidth;
		int limitY = tempY + tempHeight;

		if (_width >= screenRect.width) {
			if (limitX > tempWidth) {
				tempX = (int) (screenRect.width - _width);
			} else if (limitX < 1) {
				tempX = x();
			}
		} else {
			return;
		}
		if (_height >= screenRect.height) {
			if (limitY > tempHeight) {
				tempY = (int) (screenRect.height - _height);
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
		int width = (int) (getWidth() - screenRect.width);
		int height = (int) (getHeight() - screenRect.height);
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
	public void update(long elapsedTime) {
		if (isClose) {
			return;
		}
		if (_super != null) {
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
		if (_alpha < 0.01) {
			return;
		}
		synchronized (this) {
			int blend = g.getBlendMode();
			boolean update = _rotation != 0 || !(scaleX == 1f && scaleY == 1f);
			try {
				final int width = (int) this.getWidth();
				final int height = (int) this.getHeight();
				if (this.elastic) {
					g.setClip(this.screenX, this.screenY, width, height);
				}
				if (update) {
					g.saveTx();
					if (_rotation != 0) {
						float centerX = pivotX == -1 ? this.screenX
								+ _origin.ox(width) : this.screenX + pivotX;
						float centerY = pivotY == -1 ? this.screenY
								+ _origin.oy(height) : this.screenY + pivotY;
						g.rotate(centerX, centerY, _rotation);
					}
					if (!(scaleX == 1f && scaleY == 1f)) {
						Affine2f transform = g.tx();
						float centerX = pivotX == -1 ? this.screenX
								+ _origin.ox(width) : this.screenX + pivotX;
						float centerY = pivotY == -1 ? this.screenY
								+ _origin.oy(height) : this.screenY + pivotY;
						transform.translate(centerX, centerY);
						transform.preScale(scaleX, scaleY);
						transform.translate(-centerX, -centerY);
					}
				}
				g.setBlendMode(_blend);
				// 变更透明度
				if (_alpha > 0.1 && _alpha < 1.0) {
					float tmp = g.alpha();
					g.setAlpha(_alpha);
					if (_background != null) {
						g.draw(_background, this.screenX, this.screenY, width,
								height, baseColor);
					}
					if (this.customRendering) {
						this.createCustomUI(g, this.screenX, this.screenY,
								width, height);
					} else {
						this.createUI(g, this.screenX, this.screenY, this,
								this._imageUI);
					}
					g.setAlpha(tmp);
					// 不变更
				} else {
					if (_background != null) {
						g.draw(_background, this.screenX, this.screenY, width,
								height, baseColor);
					}
					if (this.customRendering) {
						this.createCustomUI(g, this.screenX, this.screenY,
								width, height);
					} else {
						this.createUI(g, this.screenX, this.screenY, this,
								this._imageUI);
					}
				}
				if (isDrawSelect()) {
					int tmp = g.color();
					g.setColor(baseColor);
					g.drawRect(this.screenX, this.screenY, width - 1f,
							height - 1f);
					g.setColor(tmp);
				}
			} finally {
				if (update) {
					g.restoreTx();
				}
				if (this.elastic) {
					g.clearClip();
				}
				g.setBlendMode(blend);
			}
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

	public boolean contains(float x, float y) {
		return contains(x, y, 0, 0);
	}

	public boolean contains(float x, float y, float width, float height) {
		return (this.visible)
				&& (x >= this.screenX
						&& y >= this.screenY
						&& ((x + width) <= (this.screenX + this._width * scaleX)) && ((y + height) <= (this.screenY + this._height
						* scaleY)));
	}

	public boolean intersects(float x1, float y1) {
		return (this.visible)
				&& (x1 >= this.screenX
						&& x1 <= this.screenX + this._width * scaleX
						&& y1 >= this.screenY && y1 <= this.screenY
						+ this._height * scaleY);
	}

	public boolean intersects(LComponent comp) {
		return (this.visible)
				&& (comp.isVisible())
				&& (this.screenX + this._width * scaleX >= comp.screenX
						&& this.screenX <= comp.screenX + comp._width
						&& this.screenY + this._height * scaleY >= comp.screenY && this.screenY <= comp.screenY
						+ comp._height);
	}

	@Override
	public boolean isVisible() {
		return this.visible;
	}

	@Override
	public void setVisible(boolean v) {
		if (this.visible == v) {
			return;
		}
		this.visible = v;
		this.desktop.setComponentStat(this, this.visible);
	}

	public boolean isEnabled() {
		return (this._super == null) ? this.enabled
				: (this.enabled && this._super.isEnabled());
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
		if (this.isSelected() && this._super != null) {
			this._super.transferFocus(this);
		}
	}

	public void transferFocusBackward() {
		if (this.isSelected() && this._super != null) {
			this._super.transferFocusBackward(this);
		}
	}

	public boolean isFocusable() {
		return this.focusable;
	}

	public void setFocusable(boolean b) {
		this.focusable = b;
	}

	public LContainer getContainer() {
		return this._super;
	}

	final void setContainer(LContainer container) {
		this._super = container;

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
		if (this._width != width || this._height != height) {
			this._width = width;
			this._height = height;
			if (_width == 0) {
				_width = 1;
			}
			if (_height == 0) {
				_height = 1;
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
	public void setLocation(Vector2f _location) {
		setLocation(_location.x, _location.y);
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
		if (this._width != w || this._height != h) {
			this._width = w;
			this._height = h;
			if (this._width == 0) {
				this._width = 1;
			}
			if (this._height == 0) {
				this._height = 1;
			}
			this.validateSize();
		}
	}

	protected void validateSize() {
	}

	public void validatePosition() {
		if (_super != null) {
			this.screenX = _location.x() + this._super.getScreenX();
			this.screenY = _location.y() + this._super.getScreenY();
		} else {
			this.screenX = _location.x();
			this.screenY = _location.y();
		}
	}

	public int getScreenX() {
		return this.screenX;
	}

	public int getScreenY() {
		return this.screenY;
	}

	@Override
	public void setHeight(float height) {
		this._height = height;
	}

	@Override
	public void setWidth(float width) {
		this._width = width;
	}

	@Override
	public float getWidth() {
		return (this._width * scaleX);
	}

	@Override
	public float getHeight() {
		return (this._height * scaleY);
	}

	public int width() {
		return (int) getWidth();
	}

	public int height() {
		return (int) getHeight();
	}

	public RectBox getCollisionBox() {
		if (_rect == null) {
			_rect = new RectBox(screenX, screenY, _width * scaleX, _height
					* scaleY);
		} else {
			_rect.setBounds(screenX, screenY, _width * scaleX, _height * scaleY);
		}
		return _rect;
	}

	public String getToolTipText() {
		return this.tooltip;
	}

	public void setToolTipText(String text) {
		this.tooltip = text;
	}

	public void doClick() {
		if (Click != null) {
			Click.DoClick(this);
		}
	}

	public void downClick() {
		if (Click != null) {
			Click.DownClick(this, input.getTouchX(), input.getTouchY());
		}
	}

	public void dragClick() {
		if (Click != null) {
			Click.DragClick(this, input.getTouchX(), input.getTouchY());
		}
	}

	public void upClick() {
		if (Click != null) {
			Click.UpClick(this, input.getTouchX(), input.getTouchY());
		}
	}

	protected void processTouchClicked() {
		this.doClick();
	}

	protected void processTouchPressed() {
		this.downClick();
	}

	protected void processTouchReleased() {
		this.upClick();
	}

	protected void processTouchDragged() {
		if (!locked) {
			if (getContainer() != null) {
				getContainer().sendToFront(this);
			}
			this.move(this.input.getTouchDX(), this.input.getTouchDY());
		}
		this.dragClick();
	}

	protected void processTouchMoved() {
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
		if (this.input.getKeyPressed() == SysKey.ENTER) {

			this.transferFocus();

		} else {
			this.transferFocusBackward();
		}
	}

	public LTexture[] getImageUI() {
		return this._imageUI;
	}

	public void setImageUI(LTexture[] imageUI, boolean processUI) {
		if (imageUI != null) {
			this._width = (int) imageUI[0].width();
			this._height = (int) imageUI[0].height();
		}

		this._imageUI = imageUI;
	}

	public void setImageUI(int index, LTexture imageUI) {
		if (imageUI != null) {
			this._width = (int) imageUI.width();
			this._height = (int) imageUI.height();
		}
		this._imageUI[index] = imageUI;
	}

	public abstract String getUIName();

	public LTexture getBackground() {
		return _background;
	}

	public void clearBackground() {
		this.setBackground(LSystem.base().graphics().finalColorTex());
	}

	public void setBackground(String fileName) {
		this.setBackground(Image.createImage(fileName).texture());
	}

	public void setBackground(LColor color) {
		setBackground(TextureUtils.createTexture(1, 1, color));
	}

	public void setBackgroundString(String color) {
		setBackground(new LColor(color));
	}

	public void setBackground(LTexture b) {
		if (b == null) {
			return;
		}
		if (b == this._background) {
			return;
		}
		final LTexture oldImage = this._background;
		if (oldImage != null && oldImage != b) {
			if (!b.equals(LSystem.base().graphics().finalColorTex())) {
				Updateable update = new Updateable() {

					@Override
					public void action(Object a) {
						if (oldImage != null) {
							LTexture parent = LTexture.firstFather(oldImage);
							parent.closeChildAll();
							parent.close();
						}

					}
				};
				LSystem.unload(update);
			}
		}
		this._background = b;
		this.setAlpha(1.0F);
		this._width = b.getWidth() > 1 ? b.getWidth() : this._width;
		this._height = b.getHeight() > 1 ? b.getHeight() : this._height;
		if (this._width <= 0) {
			this._width = 1;
		}
		if (this._height <= 0) {
			this._height = 1;
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

	public boolean isDrawSelect() {
		return this.isSelectDraw;
	}

	public void setDrawSelect(boolean select) {
		this.isSelectDraw = select;
	}

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
	public boolean inContains(float x, float y, float w, float h) {
		if (_super != null) {
			return _super.contains(x, y, w, h);
		}
		return false;
	}

	public float getTouchX() {
		if (_super == null) {
			return SysTouch.getX() - getX();
		} else {
			if (_super instanceof LScrollContainer) {
				return SysTouch.getX()
						+ ((LScrollContainer) _super).getScrollX()
						- _super.getX() - getX();
			} else {
				return SysTouch.getX() - _super.getX() - getX();
			}
		}
	}

	public float getTouchY() {
		if (_super == null) {
			return SysTouch.getY() - getY();
		} else {
			if (_super instanceof LScrollContainer) {
				return SysTouch.getY()
						+ ((LScrollContainer) _super).getScrollY()
						- _super.getY() - getY();
			} else {
				return SysTouch.getY() - _super.getY() - getY();
			}
		}
	}

	public RectBox getRectBox() {
		return getCollisionBox();
	}

	@Override
	public float getContainerWidth() {
		if (_super == null) {
			return screenRect.getWidth();
		}
		return _super.getWidth();
	}

	@Override
	public float getContainerHeight() {
		if (_super == null) {
			return screenRect.getHeight();
		}
		return _super.getHeight();
	}

	public void setPivotX(float pX) {
		pivotX = pX;
	}

	public void setPivotY(float pY) {
		pivotY = pY;
	}

	public float getPivotX() {
		return pivotX;
	}

	public float getPivotY() {
		return pivotY;
	}

	public void setPivot(float pX, float pY) {
		setPivotX(pX);
		setPivotY(pY);
	}

	public void show() {
		visible = true;
	}

	public void hide() {
		visible = false;
	}

	public boolean toggleVisible() {
		if (visible) {
			hide();
		} else {
			show();
		}
		return visible;
	}

	public LayoutConstraints getConstraints() {
		if (constraints == null) {
			constraints = new LayoutConstraints();
		}
		return constraints;
	}

	public LayoutPort getLayoutPort() {
		return new LayoutPort(this, getConstraints());
	}

	public LayoutPort getLayoutPort(final RectBox newBox,
			final LayoutConstraints newBoxConstraints) {
		return new LayoutPort(newBox, newBoxConstraints);
	}

	public LayoutPort getLayoutPort(final LayoutPort src) {
		return new LayoutPort(src);
	}

	@Override
	public void setColor(LColor c) {
		this.baseColor = c;
	}

	@Override
	public LColor getColor() {
		return new LColor(this.baseColor);
	}

	public Origin getOrigin() {
		return _origin;
	}

	public void setOrigin(Origin o) {
		this._origin = o;
	}

	@Override
	public String toString() {
		return getName() + " pos=" + _location + " size=" + "(" + getWidth()
				+ "," + getHeight() + ")";
	}

	@Override
	public void close() {
		if (!autoDestroy) {
			return;
		}
		if (isClose) {
			return;
		}
		this.isClose = true;
		this.desktop.setComponentStat(this, false);
		if (this._super != null) {
			this._super.remove(this);
		}
		this.desktop = Desktop.EMPTY_DESKTOP;
		this.input = null;
		this._super = null;
		if (_imageUI != null) {
			for (int i = 0, size = _imageUI.length; i < size; i++) {
				_imageUI[i].close();
				_imageUI[i] = null;
			}
			this._imageUI = null;
		}
		if (_background != null) {
			this._background.close();
			this._background = null;
		}
		this.selected = false;
		this.visible = false;
		setState(State.DISPOSED);
		removeActionEvents(this);
	}

}
