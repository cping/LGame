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
import loon.LTextures;
import loon.PlayerUtils;
import loon.Screen;
import loon.action.ActionBind;
import loon.action.ActionListener;
import loon.action.ActionTween;
import loon.action.map.Field2D;
import loon.canvas.LColor;
import loon.component.layout.BoxSize;
import loon.component.layout.LayoutConstraints;
import loon.component.layout.LayoutManager;
import loon.component.layout.LayoutPort;
import loon.event.ClickListener;
import loon.event.GameKey;
import loon.event.SysInput;
import loon.event.SysKey;
import loon.event.SysTouch;
import loon.event.Touched;
import loon.event.TouchedClick;
import loon.geom.Affine2f;
import loon.geom.PointF;
import loon.geom.PointI;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.geom.XY;
import loon.opengl.GLEx;
import loon.opengl.TextureUtils;
import loon.utils.Flip;
import loon.utils.MathUtils;

public abstract class LComponent extends LObject<LContainer> implements
		Flip<LComponent>, ActionBind, XY, BoxSize, LRelease {

	// 默认锁定当前组件(否则可以拖动)
	protected boolean locked = true;

	public boolean isLocked() {
		return locked;
	}

	public LComponent setLocked(boolean locked) {
		this.locked = locked;
		return this;
	}

	public boolean isDragLocked() {
		return isLocked();
	}

	public LComponent setDragLocked(boolean locked) {
		return setLocked(locked);
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

	public LComponent touchedClear() {
		if (_touchListener != null) {
			_touchListener.clear();
		}
		_touchListener = null;
		return this;
	}

	public boolean isTouchedEnabled() {
		if (_touchListener != null) {
			return _touchListener.isEnabled();
		}
		return false;
	}

	public void setTouchedEnabled(boolean e) {
		if (_touchListener != null) {
			_touchListener.setEnabled(e);
		}
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

	protected float _width, _height;
	// 水平设置
	protected boolean _flipX = false, _flipY = false;
	// 缩放比例
	protected float _scaleX = 1f, _scaleY = 1f;
	// 屏幕位置
	protected int _screenX, _screenY;

	private boolean _downClick = false;
	// 中心点
	protected float pivotX = -1, pivotY = -1;

	// 操作提示组件
	protected LComponent tooltipParent;
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

	protected boolean _isLimitMove = false, _drawBackground = true;

	protected LTexture _background;

	protected LayoutConstraints _rootConstraints = null;

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
		return (desktop == null || desktop.input == null) ? LSystem
				.getProcess().getScreen() : desktop.input;
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
		if (!this._isLimitMove) {
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
		if (!this._isLimitMove) {
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
		if (_alpha < 0.01f) {
			return;
		}
		synchronized (this) {
			int blend = g.getBlendMode();
			boolean update = _rotation != 0
					|| !(_scaleX == 1f && _scaleY == 1f) || _flipX || _flipY;
			try {
				g.saveBrush();
				g.setAlpha(_alpha);
				final int width = (int) this.getWidth();
				final int height = (int) this.getHeight();
				if (this.elastic) {
					g.setClip(this._screenX, this._screenY, width, height);
				}
				if (update) {
					g.saveTx();
					Affine2f tx = g.tx();
					final float centerX = pivotX == -1 ? this._screenX
							+ _origin.ox(width) : this._screenX + pivotX;
					final float centerY = pivotY == -1 ? this._screenY
							+ _origin.oy(height) : this._screenY + pivotY;
					if (_rotation != 0) {
						tx.translate(centerX, centerY);
						tx.preRotate(_rotation);
						tx.translate(-centerX, -centerY);
					}
					if (_flipX || _flipY) {
						if (_flipX && _flipY) {
							Affine2f.transform(tx, centerX, centerY,
									Affine2f.TRANS_ROT180);
						} else if (_flipX) {
							Affine2f.transform(tx, centerX, centerY,
									Affine2f.TRANS_MIRROR);
						} else if (_flipY) {
							Affine2f.transform(tx, centerX, centerY,
									Affine2f.TRANS_MIRROR_ROT180);
						}
					}
					if (!(_scaleX == 1f && _scaleY == 1f)) {
						tx.translate(centerX, centerY);
						tx.preScale(_scaleX, _scaleY);
						tx.translate(-centerX, -centerY);
					}
				}
				g.setBlendMode(_blend);
				if (_drawBackground && _background != null) {
					g.draw(_background, this._screenX, this._screenY, width,
							height, baseColor);
				}
				if (this.customRendering) {
					this.createCustomUI(g, this._screenX, this._screenY, width,
							height);
				} else {
					this.createUI(g, this._screenX, this._screenY, this,
							this._imageUI);
				}
				if (isDrawSelect()) {
					int tmp = g.color();
					g.setColor(baseColor);
					g.drawRect(this._screenX, this._screenY, width - 1f,
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
				g.restoreBrush();
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
				&& (x >= this._screenX
						&& y >= this._screenY
						&& ((x + width) <= (this._screenX + this._width
								* _scaleX)) && ((y + height) <= (this._screenY + this._height
						* _scaleY)));
	}

	public boolean intersects(float x1, float y1) {
		return (this.visible)
				&& (x1 >= this._screenX
						&& x1 <= this._screenX + this._width * _scaleX
						&& y1 >= this._screenY && y1 <= this._screenY
						+ this._height * _scaleY);
	}

	public boolean intersects(LComponent comp) {
		return (this.visible)
				&& (comp.isVisible())
				&& (this._screenX + this._width * _scaleX >= comp._screenX
						&& this._screenX <= comp._screenX + comp._width
						&& this._screenY + this._height * _scaleY >= comp._screenY && this._screenY <= comp._screenY
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
		if (desktop != null) {
			this.desktop.setComponentStat(this, this.visible);
		}
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
			this._screenX = _location.x() + this._super.getScreenX();
			this._screenY = _location.y() + this._super.getScreenY();
		} else {
			this._screenX = _location.x();
			this._screenY = _location.y();
		}
	}

	public int getScreenX() {
		return this._screenX;
	}

	public int getScreenY() {
		return this._screenY;
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
		return (this._width * _scaleX);
	}

	@Override
	public float getHeight() {
		return (this._height * _scaleY);
	}

	public int width() {
		return (int) getWidth();
	}

	public int height() {
		return (int) getHeight();
	}

	public RectBox getCollisionBox() {
		validatePosition();
		if (_rect != null) {
			_rect.setBounds(MathUtils.getBounds(_screenX, _screenY, getWidth()
					* _scaleX, getHeight() * _scaleY, _rotation, _rect));
		} else {
			_rect = MathUtils.getBounds(_screenX, _screenY, getWidth()
					* _scaleX, getHeight() * _scaleY, _rotation, _rect);
		}
		return _rect;
	}

	public LComponent getToolTipParent() {
		return this.tooltipParent;
	}

	public void setToolTipParent(LComponent tipParent) {
		this.tooltipParent = tipParent;
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
		this._downClick = true;
	}

	protected void processTouchReleased() {
		if (this._downClick) {
			this.upClick();
			this._downClick = false;
		}
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

	protected void keyPressed(GameKey key) {
	}

	protected void keyReleased(GameKey key) {
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
		this.setBackground(LTextures.loadTexture(fileName));
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
		this._background = b;
		this._background.setDisabledTexture(true);
		if (_drawBackground) {
			this._width = b.getWidth() > 1 ? b.getWidth() : this._width;
			this._height = b.getHeight() > 1 ? b.getHeight() : this._height;
			if (this._width <= 0) {
				this._width = 1;
			}
			if (this._height <= 0) {
				this._height = 1;
			}
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
		if (this._scaleX == sx && this._scaleY == sy) {
			return;
		}
		this._scaleX = sx;
		this._scaleY = sy;
	}

	@Override
	public float getScaleX() {
		return this._scaleX;
	}

	@Override
	public float getScaleY() {
		return this._scaleY;
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

	public float getTouchDX() {
		return input == null ? SysTouch.getDX() : input.getTouchDX();
	}

	public float getTouchDY() {
		return input == null ? SysTouch.getDY() : input.getTouchDY();
	}

	public float getTouchX() {
		return input == null ? SysTouch.getX() : input.getTouchX();
	}

	public float getTouchY() {
		return input == null ? SysTouch.getY() : input.getTouchY();
	}

	public float getUITouchX() {
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

	public float getUITouchY() {
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

	public LComponent show() {
		visible = true;
		if (!getScreen().contains(this)) {
			getScreen().add(this);
		}
		return this;
	}

	public LComponent hide() {
		visible = false;
		if (getScreen().contains(this)) {
			getScreen().remove(this);
		}
		return this;
	}

	public boolean toggleVisible() {
		if (visible) {
			hide();
		} else {
			show();
		}
		return visible;
	}

	public LComponent in() {
		this.setAlpha(0f);
		this.selfAction().fadeIn(30).start();
		return this;
	}

	public LComponent out() {
		this.selfAction().fadeOut(30).start()
				.setActionListener(new ActionListener() {

					@Override
					public void stop(ActionBind o) {
						if (getParent() != null) {
							getParent().remove((LComponent) o);
						}
						if (getScreen() != null) {
							getScreen().remove((LComponent) o);
						}
						close();
					}

					@Override
					public void start(ActionBind o) {

					}

					@Override
					public void process(ActionBind o) {

					}
				});
		return this;
	}

	public LayoutConstraints getRootConstraints() {
		if (_rootConstraints == null) {
			_rootConstraints = new LayoutConstraints();
		}
		return _rootConstraints;
	}

	public LayoutPort getLayoutPort() {
		return new LayoutPort(this, getRootConstraints());
	}

	public LayoutPort getLayoutPort(final RectBox newBox,
			final LayoutConstraints newBoxConstraints) {
		return new LayoutPort(newBox, newBoxConstraints);
	}

	public LayoutPort getLayoutPort(final LayoutPort src) {
		return new LayoutPort(src);
	}

	public void layoutElements(final LayoutManager manager,
			final LayoutPort... ports) {
		if (manager != null) {
			manager.layoutElements(getLayoutPort(), ports);
		}
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
	public LComponent setFlipX(boolean x) {
		this._flipX = x;
		return this;
	}

	@Override
	public LComponent setFlipY(boolean y) {
		this._flipY = y;
		return this;
	}

	@Override
	public LComponent setFlipXY(boolean x, boolean y) {
		setFlipX(x);
		setFlipY(y);
		return this;
	}

	@Override
	public boolean isFlipX() {
		return _flipX;
	}

	@Override
	public boolean isFlipY() {
		return _flipY;
	}

	public boolean isPointInUI(Vector2f v) {
		return isPointInUI(v.x, v.y);
	}

	public boolean isPointInUI(PointI p) {
		return isPointInUI(p.x, p.y);
	}

	public boolean isPointInUI(PointF p) {
		return isPointInUI(p.x, p.y);
	}

	public boolean isPointInUI(float x, float y) {
		return getRectBox().contains(x, y);
	}

	public boolean isPointInUI() {
		return isPointInUI(getTouchX(), getTouchY());
	}

	public boolean isClickDown() {
		return input.getTouchPressed() == SysTouch.TOUCH_DOWN
				|| SysTouch.isDown();
	}

	public boolean isClickUp() {
		return input.getTouchReleased() == SysTouch.TOUCH_UP || SysTouch.isUp();
	}

	public boolean isClickDrag() {
		return input.getTouchPressed() == SysTouch.TOUCH_DRAG
				|| SysTouch.isDrag();
	}

	public ActionTween selfAction() {
		return PlayerUtils.set(this);
	}

	public boolean isActionCompleted() {
		return PlayerUtils.isActionCompleted(this);
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

	@Override
	public String toString() {
		return getName() + " pos=" + _location + " size=" + " [ "
				+ getRectBox().toString() + "]";
	}
}
