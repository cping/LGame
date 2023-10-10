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
import loon.LSysException;
import loon.LSystem;
import loon.LTexture;
import loon.PlayerUtils;
import loon.Screen;
import loon.Visible;
import loon.action.ActionBind;
import loon.action.ActionListener;
import loon.action.ActionTween;
import loon.action.collision.CollisionObject;
import loon.action.map.Field2D;
import loon.canvas.LColor;
import loon.component.layout.LayoutConstraints;
import loon.component.layout.LayoutManager;
import loon.component.layout.LayoutPort;
import loon.events.ClickListener;
import loon.events.GameKey;
import loon.events.ResizeListener;
import loon.events.SysInput;
import loon.events.SysKey;
import loon.events.SysTouch;
import loon.events.Touched;
import loon.events.TouchedClick;
import loon.geom.Affine2f;
import loon.geom.BoxSize;
import loon.geom.Dimension;
import loon.geom.PointF;
import loon.geom.PointI;
import loon.geom.RectBox;
import loon.geom.Shape;
import loon.geom.Vector2f;
import loon.geom.XY;
import loon.opengl.GLEx;
import loon.opengl.LTextureFree;
import loon.opengl.TextureUtils;
import loon.utils.Flip;
import loon.utils.MathUtils;
import loon.utils.StringUtils;

/**
 * Loon桌面组件的核心,所有UI类组件基于此类产生
 */
public abstract class LComponent extends LObject<LContainer>
		implements Flip<LComponent>, CollisionObject, Visible, ActionBind, XY, BoxSize, LRelease {

	public static interface CallListener {

		public void act(long elapsedTime);

	}

	private Origin _origin = Origin.CENTER;

	private Vector2f _offset = new Vector2f();

	private ResizeListener<LComponent> _resizeListener;

	protected LTexture[] _imageUI = null;

	protected float _fixedWidthOffset = 0f;

	protected float _fixedHeightOffset = 0f;

	protected boolean _component_elastic = false;

	protected boolean _component_autoDestroy = true;

	protected boolean _component_isClose = false;

	protected boolean isFull = false;

	protected boolean isSelectDraw = false;
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

	private LTextureFree _freeTextures;

	private final Vector2f _touchPoint = new Vector2f();

	private boolean _downClick = false;
	// 中心点
	protected float _pivotX = -1, _pivotY = -1;

	// 操作提示组件
	protected LComponent tooltipParent;
	// 操作提示
	protected String tooltip;

	// 组件标记
	protected boolean _component_visible = true;

	protected boolean _component_enabled = true;

	// 是否为焦点
	protected boolean _component_focusable = true;

	// 是否已选中
	protected boolean _component_selected = false;

	protected LColor _component_baseColor = null;

	protected Desktop _desktop;

	protected boolean _isLimitMove = false, _drawBackground = true;

	protected LTexture _background;

	protected LayoutConstraints _rootConstraints = null;

	protected SysInput input;

	// 点击事件监听
	protected ClickListener _click;

	// 循环事件监听
	protected CallListener _call;

	private TouchedClick _touchListener;

	// 充当卓面容器
	protected boolean desktopContainer;

	// 默认锁定当前组件(否则可以拖动)
	protected boolean locked = true;

	// 组件内部变量, 用于锁定当前组件的触屏（鼠标）与键盘事件
	private boolean _touchLocked = false, _keyLocked = false;

	public LComponent(Vector2f position, Vector2f size) {
		this(position.x(), position.y(), size.x(), size.y());
	}

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
		this._width = MathUtils.max(1f, width);
		this._height = MathUtils.max(1f, height);
	}

	public int getScreenWidth() {
		return getScreen().getWidth();
	}

	public int getScreenHeight() {
		return getScreen().getHeight();
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
		int tempWidth = (int) (getWidth() - getScreenWidth());
		int tempHeight = (int) (getHeight() - getScreenHeight());

		int limitX = tempX + tempWidth;
		int limitY = tempY + tempHeight;

		if (_width >= getScreenWidth()) {
			if (limitX > tempWidth) {
				tempX = (int) (getScreenWidth() - _width);
			} else if (limitX < 1) {
				tempX = x();
			}
		} else {
			return;
		}
		if (_height >= getScreenHeight()) {
			if (limitY > tempHeight) {
				tempY = (int) (getScreenHeight() - _height);
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
		int width = (int) (getWidth() - getScreenWidth());
		int height = (int) (getHeight() - getScreenHeight());
		int limitX = x + width;
		int limitY = y + height;
		if (getWidth() >= getScreenWidth()) {
			if (limitX >= width - 1) {
				return true;
			} else if (limitX <= 1) {
				return true;
			}
		} else {
			if (!getScreen().contains(x, y, getWidth(), getHeight())) {
				return true;
			}
		}
		if (getHeight() >= getScreenHeight()) {
			if (limitY >= height - 1) {
				return true;
			} else if (limitY <= 1) {
				return true;
			}
		} else {
			if (!getScreen().contains(x, y, getWidth(), getHeight())) {
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
		if (_component_isClose) {
			return;
		}
		if (_objectSuper != null) {
			validatePosition();
		}
		if (_call != null) {
			_call.act(elapsedTime);
		}
		process(elapsedTime);
	}

	public void process(long elapsedTime) {

	}

	public abstract void createUI(GLEx g, int x, int y);

	/**
	 * 渲染当前组件画面于指定绘图器之上
	 * 
	 * @param g
	 */
	public void createUI(GLEx g) {
		if (_component_isClose) {
			return;
		}
		if (!this._component_visible) {
			return;
		}
		if (_objectAlpha < 0.01f) {
			return;
		}
		final boolean update = _objectRotation != 0 || !(_scaleX == 1f && _scaleY == 1f) || _flipX || _flipY;
		try {
			g.saveBrush();
			float screenAlpha = 1f;
			if (getScreen() != null) {
				screenAlpha = getScreen().getAlpha();
			}
			g.setAlpha(_objectAlpha * screenAlpha);
			final int newX = MathUtils.floor(this._screenX + _offset.x);
			final int newY = MathUtils.floor(this._screenY + _offset.y);
			final int width = MathUtils.floor(this.getWidth());
			final int height = MathUtils.floor(this.getHeight());
			if (this._component_elastic) {
				g.setClip(newX, newY, width, height);
			}
			if (update) {
				g.saveTx();
				Affine2f tx = g.tx();
				final float centerX = (_pivotX == -1 ? newX + _origin.ox(width) : newX + _pivotX);
				final float centerY = (_pivotY == -1 ? newY + _origin.oy(height) : newY + _pivotY);
				if (_objectRotation != 0) {
					tx.translate(centerX, centerY);
					tx.preRotate(_objectRotation);
					tx.translate(-centerX, -centerY);
				}
				if (_flipX || _flipY) {
					if (_flipX && _flipY) {
						Affine2f.transform(tx, centerX, centerY, Affine2f.TRANS_ROT180);
					} else if (_flipX) {
						Affine2f.transform(tx, centerX, centerY, Affine2f.TRANS_MIRROR);
					} else if (_flipY) {
						Affine2f.transform(tx, centerX, centerY, Affine2f.TRANS_MIRROR_ROT180);
					}
				}
				if (!(_scaleX == 1f && _scaleY == 1f)) {
					tx.translate(centerX, centerY);
					tx.preScale(_scaleX, _scaleY);
					tx.translate(-centerX, -centerY);
				}
			}
			if (_drawBackground && _background != null) {
				g.draw(_background, newX, newY, width, height, _component_baseColor);
			}
			if (this.customRendering) {
				this.createCustomUI(g, newX, newY, width, height);
			} else {
				this.createUI(g, newX, newY);
			}
			if (isDrawSelect()) {
				g.drawRect(newX, newY, width - 1f, height - 1f, _component_baseColor);
			}
		} finally {
			if (update) {
				g.restoreTx();
			}
			if (this._component_elastic) {
				g.clearClip();
			}
			g.restoreBrush();
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

	protected void createCustomUI(int w, int h) {
	}

	public boolean contains(float x, float y) {
		return contains(x, y, 0, 0);
	}

	public boolean contains(float x, float y, float width, float height) {
		return (this._component_visible && (x >= this.getDrawScrollX() && y >= this.getDrawScrollY()
				&& ((x + width) <= (this.getDrawScrollX() + this._width * _scaleX))
				&& ((y + height) <= (this.getDrawScrollY() + this._height * _scaleY))));
	}

	public boolean intersects(float x1, float y1) {
		return (this._component_visible)
				&& (x1 >= this.getDrawScrollX() && x1 <= this.getDrawScrollX() + this._width * _scaleX
						&& y1 >= this.getDrawScrollY() && y1 <= this.getDrawScrollY() + this._height * _scaleY);
	}

	protected float getDrawScrollX() {
		if (_objectSuper != null) {
			return this._screenX - _objectSuper._component_scrollX;
		}
		return this._screenX;
	}

	protected float getDrawScrollY() {
		if (_objectSuper != null) {
			return this._screenY - _objectSuper._component_scrollY;
		}
		return this._screenY;
	}

	@Override
	public boolean intersects(CollisionObject obj) {
		return intersects(obj.getRectBox());
	}

	@Override
	public boolean intersects(Shape s) {
		return getCollisionBox().intersects(s);
	}

	@Override
	public boolean contains(Shape s) {
		return getCollisionBox().contains(s);
	}
	
	@Override
	public boolean collided(Shape s) {
		return getCollisionBox().collided(s);
	}

	public boolean intersects(LComponent comp) {
		return (this._component_visible) && (comp.isVisible())
				&& (this.getDrawScrollX() + this._width * _scaleX >= comp.getDrawScrollX()
						&& this.getDrawScrollX() <= comp.getDrawScrollX() + comp._width
						&& this.getDrawScrollY() + this._height * _scaleY >= comp.getDrawScrollY()
						&& this.getDrawScrollY() <= comp.getDrawScrollY() + comp._height);
	}

	@Override
	public boolean isVisible() {
		return this._component_visible;
	}

	@Override
	public void setVisible(boolean v) {
		if (this._component_visible == v) {
			return;
		}
		this._component_visible = v;
		if (_desktop != null) {
			this._desktop.setComponentStat(this, this._component_visible);
		}
	}

	public boolean isEnabled() {
		return (this._objectSuper == null) ? this._component_enabled
				: (this._component_enabled && this._objectSuper.isEnabled());
	}

	public LComponent setEnabled(boolean b) {
		if (this._component_enabled == b) {
			return this;
		}
		this._component_enabled = b;
		if (_desktop != null) {
			this._desktop.setComponentStat(this, this._component_enabled);
		}
		return this;
	}

	public boolean isSelected() {
		return this._component_selected;
	}

	final LComponent setSelected(boolean b) {
		this._component_selected = b;
		return this;
	}

	public boolean requestFocus() {
		if (_desktop != null) {
			return this._desktop.selectComponent(this);
		}
		return false;
	}

	public LComponent transferFocus() {
		if (this.isSelected() && this._objectSuper != null) {
			this._objectSuper.transferFocus(this);
		}
		return this;
	}

	public LComponent transferFocusBackward() {
		if (this.isSelected() && this._objectSuper != null) {
			this._objectSuper.transferFocusBackward(this);
		}
		return this;
	}

	public boolean isFocusable() {
		return this._component_focusable;
	}

	public LComponent setFocusable(boolean b) {
		this._component_focusable = b;
		return this;
	}

	public LContainer getContainer() {
		return getSuper();
	}

	protected final void setContainer(LContainer container) {
		this.changeContainer(container);
		this.validatePosition();
	}

	protected final void changeContainer(LContainer container) {
		LContainer comp = getSuper();
		if (comp == this) {
			return;
		}
		if (comp != null) {
			comp.remove(container);
		}
		this.setSuper(container);
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
			final float moved = 128f;
			if (dx > -moved && dx < moved && dy > -moved && dy < moved) {
				super.move(dx, dy);
				this.validatePosition();
			}
		}
	}

	public LComponent setBounds(float dx, float dy, int width, int height) {
		this.setLocation(dx, dy);
		this.setSize(width, height);
		return this;
	}

	@Override
	public LComponent setSize(float w, float h) {
		if (this._width != w || this._height != h) {
			this._width = MathUtils.max(1f, w);
			this._height = MathUtils.max(1f, h);
			this.validateResize();
		}
		return this;
	}

	public void validatePosition() {
		if (_objectSuper != null) {
			this._screenX = _objectLocation.x() + this._objectSuper.getScreenX();
			this._screenY = _objectLocation.y() + this._objectSuper.getScreenY();
		} else {
			this._screenX = _objectLocation.x();
			this._screenY = _objectLocation.y();
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
		if (height != this._height) {
			this.validateResize();
		}
		this._height = MathUtils.max(1f, height);
	}

	@Override
	public void setWidth(float width) {
		if (width != this._width) {
			this.validateResize();
		}
		this._width = MathUtils.max(1f, width);
	}

	@Override
	public float getWidth() {
		return (this._width * _scaleX) - _fixedWidthOffset;
	}

	@Override
	public float getHeight() {
		return (this._height * _scaleY) - _fixedHeightOffset;
	}

	public int width() {
		return (int) getWidth();
	}

	public int height() {
		return (int) getHeight();
	}

	public float getFixedWidthOffset() {
		return _fixedWidthOffset;
	}

	public LComponent setFixedWidthOffset(float fixedWidthOffset) {
		this._fixedWidthOffset = fixedWidthOffset;
		return this;
	}

	public float getFixedHeightOffset() {
		return _fixedHeightOffset;
	}

	public LComponent setFixedHeightOffset(float fixedHeightOffset) {
		this._fixedHeightOffset = fixedHeightOffset;
		return this;
	}

	public RectBox getCollisionBox() {
		validatePosition();
		return setRect(MathUtils.getBounds(getDrawScrollX(), getDrawScrollY(), getWidth() * _scaleX,
				getHeight() * _scaleY, _objectRotation, _objectRect));
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

	public LComponent setToolTipText(String text) {
		if (StringUtils.isEmpty(text)) {
			return this;
		}
		this.tooltip = text;
		return this;
	}

	public void doClick() {
		if (_click != null) {
			try {
				_click.DoClick(this);
			} catch (Throwable cause) {
				LSystem.error("Component doClick() exception", cause);
			}
		}
	}

	public void downClick() {
		if (_click != null) {
			try {
				_click.DownClick(this, getUITouchX(), getUITouchY());
			} catch (Throwable cause) {
				LSystem.error("Component downClick() exception", cause);
			}
		}
	}

	public void dragClick() {
		if (_click != null) {
			try {
				_click.DragClick(this, getUITouchX(), getUITouchY());
			} catch (Throwable cause) {
				LSystem.error("Component dragClick() exception", cause);
			}
		}
	}

	public void upClick() {
		if (_click != null) {
			try {
				_click.UpClick(this, getUITouchX(), getUITouchY());
			} catch (Throwable cause) {
				LSystem.error("Component upClick() exception", cause);
			}
		}
	}

	protected void processTouchDragged() {
		if (!locked) {
			if (getContainer() != null) {
				getContainer().sendToFront(this);
			}
			if (isContainer()) {
				this.move(getUITouchX() - getWidth() / 2, getUITouchY());
			} else {
				this.move(getUITouchX(), getUITouchY());
			}
		}
		try {
			this.dragClick();
		} catch (Throwable e) {
			LSystem.error("Component dragClick() exception", e);
		}
	}

	protected void processTouchClicked() {
		try {
			this.doClick();
		} catch (Throwable e) {
			LSystem.error("Component doClick() exception", e);
		}
	}

	protected void processTouchPressed() {
		try {
			this.downClick();
		} catch (Throwable e) {
			LSystem.error("Component downClick() exception", e);
		}
		this._downClick = true;
	}

	protected void processTouchReleased() {
		if (this._downClick) {
			try {
				this.upClick();
			} catch (Throwable e) {
				LSystem.error("Component upClick() exception", e);
			}
			this._downClick = false;
		}
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

	protected void processResize() {
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
			this._width = imageUI[0].getWidth();
			this._height = imageUI[0].getHeight();
		}
		this._imageUI = imageUI;
	}

	public void setImageUI(int index, LTexture imageUI) {
		if (imageUI != null) {
			this._width = imageUI.getWidth();
			this._height = imageUI.getHeight();
		}
		this._imageUI[index] = imageUI;
	}

	public abstract String getUIName();

	public LTexture getBackground() {
		return _background;
	}

	public LComponent clearBackground() {
		return this.setBackground(LSystem.base().graphics().finalColorTex(), false);
	}

	public LComponent setBackground(String fileName) {
		return this.setBackground(LSystem.loadTexture(fileName));
	}

	public LComponent setBackground(LColor color) {
		return setBackground(TextureUtils.createTexture(1, 1, color), false);
	}

	public LComponent setBackgroundString(String color) {
		return setBackground(new LColor(color));
	}

	public LComponent onlyBackground(LTexture b) {
		this._drawBackground = false;
		this.setBackground(b);
		return this;
	}

	public LComponent setBackground(LTexture b) {
		return setBackground(b, true);
	}

	public LComponent setBackground(LTexture b, boolean updateSize) {
		if (b == null) {
			return this;
		}
		if (b == this._background) {
			return this;
		}
		if (!_drawBackground) {
			return setBackground(b, this._width, this._height, updateSize);
		} else {
			return setBackground(b, b.getWidth(), b.getHeight(), updateSize);
		}
	}

	public LComponent setBackground(LTexture b, float w, float h) {
		return setBackground(b, w, h, true);
	}

	public LComponent setBackground(LTexture b, float w, float h, boolean updateSize) {
		if (b == null) {
			return this;
		}
		if (b == this._background) {
			return this;
		}
		this._background = b;
		if (updateSize) {
			this.setSize(w, h);
		}
		freeRes().add(_background);
		return this;
	}

	public int getCamX() {
		return cam_x == 0 ? x() : cam_x;
	}

	public int getCamY() {
		return cam_y == 0 ? y() : cam_y;
	}

	public LComponent clip(boolean b) {
		return setElastic(b);
	}

	public boolean isClip() {
		return isElastic();
	}

	public boolean isElastic() {
		return this._component_elastic;
	}

	public LComponent setElastic(boolean b) {
		if (getWidth() > 32 || getHeight() > 32) {
			this._component_elastic = b;
		} else {
			this._component_elastic = false;
		}
		return this;
	}

	public boolean isAutoDestroy() {
		return _component_autoDestroy;
	}

	public void setAutoDestroy(boolean autoDestroy) {
		this._component_autoDestroy = autoDestroy;
	}

	@Override
	public Field2D getField2D() {
		return null;
	}

	public boolean isDrawSelect() {
		return this.isSelectDraw;
	}

	public LComponent setDrawSelect(boolean select) {
		this.isSelectDraw = select;
		return this;
	}

	public LComponent debug(boolean select) {
		return setDrawSelect(select);
	}

	public LComponent setScale(final float s) {
		this.setScale(s, s);
		return this;
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
		if (_objectSuper != null) {
			return _objectSuper.contains(x, y, w, h);
		}
		return getRectBox().contains(x, y, w, h);
	}

	private float toPixelScaleX(float x) {
		return MathUtils.iceil(x / _scaleX);
	}

	private float toPixelScaleY(float y) {
		return MathUtils.iceil(y / _scaleY);
	}

	public float getTouchDX() {
		return toPixelScaleX(input == null ? SysTouch.getDX() : input.getTouchDX());
	}

	public float getTouchDY() {
		return toPixelScaleY(input == null ? SysTouch.getDY() : input.getTouchDY());
	}

	public float getTouchX() {
		return toPixelScaleX(input == null ? SysTouch.getX() : input.getTouchX());
	}

	public float getTouchY() {
		return toPixelScaleY(input == null ? SysTouch.getY() : input.getTouchY());
	}

	public Vector2f getUITouch(float x, float y) {
		return getUITouch(x, y, null);
	}

	public Vector2f getUITouch(float x, float y, Vector2f pointResult) {
		if (!(x == -1 && y == -1 && pointResult != null)) {
			if (pointResult == null) {
				pointResult = new Vector2f(x, y);
			} else {
				pointResult.set(x, y);
			}
		}
		float newX = 0f;
		float newY = 0f;
		LComponent parent = getParent();
		if (parent != null) {
			newX = pointResult.x - parent.getX() - getX();
			newY = pointResult.y - parent.getX() - getY();
		} else {
			newX = pointResult.x - getX();
			newY = pointResult.y - getY();
		}
		final float angle = getRotation();
		if (angle == 0 || angle == 360) {
			pointResult.x = toPixelScaleX(newX);
			pointResult.y = toPixelScaleY(newY);
			return pointResult;
		}
		float oldWidth = _width;
		float oldHeight = _height;
		float newWidth = getWidth();
		float newHeight = getHeight();
		float offX = oldWidth / 2f - newWidth / 2f;
		float offY = oldHeight / 2f - newHeight / 2f;
		float posX = (newX - offX);
		float posY = (newY - offY);
		if (angle == 90) {
			offX = oldHeight / 2f - newWidth / 2f;
			offY = oldWidth / 2f - newHeight / 2f;
			posX = (newX - offY);
			posY = (newY - offX);
			pointResult.set(posX / getScaleX(), posY / getScaleY()).rotateSelf(90);
			pointResult.set(-pointResult.x, MathUtils.abs(pointResult.y - this._height));
		} else if (angle == -90) {
			offX = oldHeight / 2f - newWidth / 2f;
			offY = oldWidth / 2f - newHeight / 2f;
			posX = (newX - offY);
			posY = (newY - offX);
			pointResult.set(posX / getScaleX(), posY / getScaleY()).rotateSelf(-90);
			pointResult.set(-(pointResult.x - this._width), MathUtils.abs(pointResult.y));
		} else if (angle == -180 || angle == 180) {
			pointResult.set(posX / getScaleX(), posY / getScaleY()).rotateSelf(getRotation()).addSelf(_width, _height);
		} else {
			float rad = MathUtils.toRadians(angle);
			float sin = MathUtils.sin(rad);
			float cos = MathUtils.cos(rad);
			float dx = offX / getScaleX();
			float dy = offY / getScaleY();
			float dx2 = cos * dx - sin * dy;
			float dy2 = sin * dx + cos * dy;
			pointResult.x = _width - (newX - dx2);
			pointResult.y = _height - (newY - dy2);
		}
		return pointResult;
	}

	public Vector2f getUITouchXY() {
		if (getRotation() == 0) {
			float newX = 0f;
			float newY = 0f;
			if (_objectSuper == null) {
				newX = toPixelScaleX(SysTouch.getX() - getX());
				newY = toPixelScaleY(SysTouch.getY() - getY());
			} else {
				if (_objectSuper.isContainer() && (_objectSuper instanceof LScrollContainer)) {
					LScrollContainer scroll = (LScrollContainer) _objectSuper;
					newX = toPixelScaleX(SysTouch.getX() + scroll.getBoxScrollX() - _objectSuper.getX() - getX());
					newY = toPixelScaleY(SysTouch.getY() + scroll.getBoxScrollY() - _objectSuper.getY() - getY());
				} else {
					newX = toPixelScaleX(SysTouch.getX() - _objectSuper.getX() - getX());
					newY = toPixelScaleY(SysTouch.getY() - _objectSuper.getY() - getY());
				}
			}
			_touchPoint.set(newX, newY);
		} else {
			if (_objectSuper.isContainer() && (_objectSuper instanceof LScrollContainer)) {
				LScrollContainer scroll = (LScrollContainer) _objectSuper;
				return getUITouch(SysTouch.getX() + scroll.getBoxScrollX(), SysTouch.getY() + scroll.getBoxScrollY(),
						_touchPoint);
			} else {
				return getUITouch(SysTouch.getX(), SysTouch.getY(), _touchPoint);
			}
		}
		return _touchPoint;
	}

	public float getUITouchX() {
		return getUITouchXY().x;
	}

	public float getUITouchY() {
		return getUITouchXY().y;
	}

	protected LComponent validateResize() {
		if (this._resizeListener != null) {
			this._resizeListener.onResize(this);
		}
		this.processResize();
		return this;
	}

	@Override
	public RectBox getRectBox() {
		return getCollisionBox();
	}

	@Override
	public float getContainerX() {
		return this._objectSuper == null ? super.getContainerX() : this._objectSuper.getScreenX();
	}

	@Override
	public float getContainerY() {
		return this._objectSuper == null ? super.getContainerY() : this._objectSuper.getScreenY();
	}

	@Override
	public float getContainerWidth() {
		if (_objectSuper == null) {
			return getScreenWidth();
		}
		return _objectSuper.getWidth();
	}

	@Override
	public float getContainerHeight() {
		if (_objectSuper == null) {
			return getScreenHeight();
		}
		return _objectSuper.getHeight();
	}

	public LComponent setPivotX(float pX) {
		_pivotX = pX;
		return this;
	}

	public LComponent setPivotY(float pY) {
		_pivotY = pY;
		return this;
	}

	public float getPivotX() {
		return _pivotX;
	}

	public float getPivotY() {
		return _pivotY;
	}

	public LComponent setPivot(float pX, float pY) {
		setPivotX(pX);
		setPivotY(pY);
		return this;
	}

	public LComponent setAnchor(final float scale) {
		return setAnchor(scale, scale);
	}

	public LComponent setAnchor(final float sx, final float sy) {
		setPivot(_width * sx, _height * sy);
		return this;
	}

	public LComponent coord(float x, float y) {
		setLocation(x, y);
		return this;
	}

	public LComponent show() {
		_component_visible = true;
		if (!getScreen().contains(this)) {
			getScreen().add(this);
		}
		return this;
	}

	public LComponent hide() {
		_component_visible = false;
		if (getScreen().contains(this)) {
			getScreen().remove(this);
		}
		if (_desktop != null) {
			_desktop.remove(this);
		}
		return this;
	}

	public boolean toggleVisible() {
		if (_component_visible) {
			hide();
		} else {
			show();
		}
		return _component_visible;
	}

	public LComponent cancelFocus() {
		return setFocusable(false);
	}

	public LComponent enabled() {
		this.setEnabled(true);
		return this;
	}

	public LComponent disabled() {
		this.setEnabled(false);
		return this;
	}

	/**
	 * 淡入当前组件
	 * 
	 * @return
	 */
	public LComponent in() {
		return in(30);
	}

	/**
	 * 淡入当前组件
	 * 
	 * @param speed
	 * @return
	 */
	public LComponent in(float speed) {
		this.setAlpha(0f);
		this.selfAction().fadeIn(speed).start();
		return this;
	}

	/**
	 * 淡出当前组件(并且淡出后自动注销)
	 * 
	 * @return
	 */
	public LComponent out() {
		return out(30);
	}

	/**
	 * 淡出当前组件(并且淡出后自动注销)
	 * 
	 * @param speed
	 * @return
	 */
	public LComponent out(float speed) {
		this.selfAction().fadeOut(speed).start().setActionListener(new ActionListener() {

			@Override
			public void stop(ActionBind o) {
				if (getParent() != null) {
					getParent().remove((LComponent) o);
				}
				if (getScreen() != null) {
					getScreen().remove((LComponent) o);
				}
				if (_desktop != null) {
					_desktop.remove((LComponent) o);
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

	public LayoutPort getLayoutPort(final RectBox newBox, final LayoutConstraints newBoxConstraints) {
		return new LayoutPort(newBox, newBoxConstraints);
	}

	public LayoutPort getLayoutPort(final LayoutPort src) {
		return new LayoutPort(src);
	}

	public void layoutElements(final LayoutManager manager, final LayoutPort... ports) {
		if (manager != null) {
			manager.layoutElements(getLayoutPort(), ports);
		}
	}

	public float clampX(float x) {
		if (_objectSuper == null) {
			return x;
		}
		RectBox bounds = _objectSuper.getRectBox();
		float dw = _objectSuper.getWidth();
		float bx = bounds.x + ((dw - this.getWidth()) / 2);
		float bw = MathUtils.max(bx, bx + bounds.width - dw);
		if (x < bx) {
			x = bx;
		} else if (x > bw) {
			x = bw;
		}
		return x;
	}

	public float clampY(float y) {
		if (_objectSuper == null) {
			return y;
		}
		RectBox bounds = _objectSuper.getRectBox();
		float dh = _objectSuper.getHeight();
		float by = bounds.y + ((dh - this.getHeight()) / 2);
		float bh = MathUtils.max(by, by + bounds.height - dh);
		if (y < by) {
			y = by;
		} else if (y > bh) {
			y = bh;
		}
		return y;
	}

	public Vector2f getAbsolutePosition() {
		Vector2f screenPos = new Vector2f(getPosition());
		for (LComponent p = this.getParent(); p != null; p = p.getParent()) {
			screenPos.add(p.getPosition());
		}
		return screenPos;
	}

	public Vector2f getSize() {
		return new Vector2f(getWidth(), getHeight());
	}

	@Override
	public void setColor(LColor c) {
		this._component_baseColor = new LColor(c);
	}

	@Override
	public LColor getColor() {
		return new LColor(this._component_baseColor);
	}

	public Origin getOrigin() {
		return _origin;
	}

	public LComponent setOrigin(Origin o) {
		this._origin = o;
		return this;
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
		return input.getTouchPressed() == SysTouch.TOUCH_DOWN || SysTouch.isDown();
	}

	public boolean isClickUp() {
		return input.getTouchReleased() == SysTouch.TOUCH_UP || SysTouch.isUp();
	}

	public boolean isClickDrag() {
		return input.getTouchPressed() == SysTouch.TOUCH_DRAG || SysTouch.isDrag();
	}

	public boolean isTouchResponseEvent(float x, float y) {
		return isVisible() && isEnabled() && !isLocked() && contains(x, y);
	}

	public boolean isTouchNotResponseEvent(float x, float y) {
		return !isVisible() || !isEnabled() || isLocked() || !contains(x, y);
	}

	public Dimension getDimension() {
		return new Dimension(this._width * this._scaleX, this._height * this._scaleY);
	}

	@Override
	public ActionTween selfAction() {
		return PlayerUtils.set(this);
	}

	@Override
	public boolean isActionCompleted() {
		return PlayerUtils.isActionCompleted(this);
	}

	public LComponent setDesktop(Desktop d) {
		if (this._desktop == d) {
			return this;
		}
		this._desktop = d;
		this.input = d.input;
		return this;
	}

	public Desktop getDesktop() {
		return this._desktop;
	}

	public Screen getScreen() {
		return (_desktop == null || _desktop.input == null) ? LSystem.getProcess().getScreen() : _desktop.input;
	}

	@Override
	public float getCenterX() {
		return getX() + getWidth() / 2f;
	}

	@Override
	public float getCenterY() {
		return getY() + getHeight() / 2f;
	}

	public boolean isDesktopContainer() {
		return desktopContainer;
	}

	public boolean isClosed() {
		return _component_isClose;
	}

	public LTextureFree freeRes() {
		if (_freeTextures == null) {
			_freeTextures = new LTextureFree();
		}
		return _freeTextures;
	}

	@Override
	public RectBox getBoundingRect() {
		return getCollisionBox();
	}

	@Override
	public boolean containsPoint(float x, float y) {
		return getCollisionBox().contains(x, y, 1, 1);
	}

	public boolean isDescendantOf(LComponent o) {
		if (o == null) {
			throw new LSysException("Component cannot be null");
		}
		LComponent parent = this;
		for (;;) {
			if (parent == null) {
				return false;
			}
			if (parent == o) {
				return true;
			}
			parent = parent.getParent();
		}
	}

	public boolean isAscendantOf(LComponent o) {
		if (o == null) {
			throw new LSysException("Component cannot be null");
		}
		for (;;) {
			if (o == null) {
				return false;
			}
			if (o == this) {
				return true;
			}
			o = o.getParent();
		}
	}

	public Vector2f getOffset() {
		return _offset;
	}

	public LComponent setOffset(float x, float y) {
		this._offset.set(x, y);
		return this;
	}

	public LComponent setOffset(Vector2f offset) {
		this._offset = offset;
		return this;
	}

	public float getOffsetX() {
		return _offset.x;
	}

	public LComponent setOffsetX(float offsetX) {
		this._offset.setX(offsetX);
		return this;
	}

	public float getOffsetY() {
		return _offset.y;
	}

	public LComponent setOffsetY(float offsetY) {
		this._offset.setY(offsetY);
		return this;
	}

	public ResizeListener<LComponent> getResizeListener() {
		return _resizeListener;
	}

	public LComponent setResizeListener(ResizeListener<LComponent> listener) {
		this._resizeListener = listener;
		return this;
	}

	public LComponent softCenterOn(float x, float y) {
		final RectBox rect = getDesktop() == null ? LSystem.viewSize.getRect() : getDesktop().getBoundingBox();
		final LComponent comp = this.getSuper();
		if (x != 0) {
			float dx = (x - rect.getWidth() / 2 / this.getScaleX() - this.getX()) / 3;
			if (comp != null) {
				RectBox boundingBox = comp.getRectBox();
				if (this.getX() + dx < boundingBox.getMinX()) {
					setX(boundingBox.getMinX() / this.getScaleX());
				} else if (this.getX() + dx > (boundingBox.getMaxX() - rect.getWidth()) / this.getScaleX()) {
					setX(MathUtils.max(boundingBox.getMaxX() - rect.getWidth(), boundingBox.getMinX())
							/ this.getScaleX());
				} else {
					this.setX(this.getX() + dx);
				}
			} else {
				this.setX(this.getX() + dx);
			}
		}
		if (y != 0) {
			float dy = (y - rect.getHeight() / 2 / this.getScaleY() - this.getY()) / 3;
			if (comp != null) {
				RectBox boundingBox = comp.getRectBox();
				if (this.getY() + dy < boundingBox.getMinY()) {
					this.setY(boundingBox.getMinY() / this.getScaleY());
				} else if (this.getY() + dy > (boundingBox.getMaxY() - rect.getHeight()) / this.getScaleY()) {
					this.setY(MathUtils.max(boundingBox.getMaxY() - rect.getHeight(), boundingBox.getMinY())
							/ this.getScaleY());
				} else {
					this.setY(this.getY() + dy);
				}
			} else {
				this.setY(this.getY() + dy);
			}
		}
		return this;
	}

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

	public LComponent dragLocked() {
		return setDragLocked(true);
	}

	public LComponent dragUnlocked() {
		return setDragLocked(false);
	}

	public LComponent setTouchLocked(boolean locked) {
		this._touchLocked = locked;
		return this;
	}

	public boolean isTouchLocked() {
		return this._touchLocked;
	}

	public LComponent setKeyLocked(boolean locked) {
		this._keyLocked = locked;
		return this;
	}

	public boolean isKeyLocked() {
		return this._keyLocked;
	}

	private final TouchedClick makeTouched() {
		if (_touchListener == null) {
			_touchListener = new TouchedClick();
		}
		if (_click != null) {
			_touchListener.addClickListener(_click);
		}
		this._click = _touchListener;
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

	public LComponent setTouchedEnabled(boolean e) {
		if (_touchListener != null) {
			_touchListener.setEnabled(e);
		}
		return this;
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
		this._click = c;
		makeTouched();
		return this;
	}

	public LComponent S(ClickListener c) {
		return SetClick(c);
	}

	public LComponent SetClick(ClickListener c) {
		this._click = c;
		return this;
	}

	public ClickListener getClick() {
		return _click;
	}

	public LComponent SC(CallListener u) {
		SetCall(u);
		return this;
	}

	public LComponent SetCall(CallListener u) {
		this._call = u;
		return this;
	}

	public CallListener getCall() {
		return _call;
	}

	public LComponent clearListener() {
		this._call = null;
		this._click = null;
		this._touchListener = null;
		return this;
	}

	public boolean isAllowTouch() {
		return !_touchLocked && _component_enabled;
	}

	public boolean isAllowKey() {
		return !_keyLocked && _component_enabled;
	}

	@Override
	public void close() {
		if (!_component_autoDestroy) {
			return;
		}
		if (_component_isClose) {
			return;
		}
		this._component_visible = false;
		this._component_isClose = true;
		if (_desktop != null) {
			this._desktop.setComponentStat(this, false);
		}
		if (this._objectSuper != null) {
			this._objectSuper.remove(this);
		}
		this._objectSuper = null;
		if (_imageUI != null) {
			final int size = _imageUI.length;
			for (int i = 0; i < size; i++) {
				_imageUI[i].close();
				_imageUI[i] = null;
			}
			this._imageUI = null;
		}
		if (_background != null) {
			this._background.close();
			this._background = null;
		}
		if (_freeTextures != null) {
			this._freeTextures.close();
			this._freeTextures = null;
		}
		this._component_selected = false;
		this._component_visible = false;
		this._touchListener = null;
		this._resizeListener = null;
		this._click = null;
		this.input = null;
		setState(State.DISPOSED);
		removeActionEvents(this);
		destory();
	}

	public abstract void destory();

}
