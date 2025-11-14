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
import loon.action.ActionBindData;
import loon.action.ActionControl;
import loon.action.ActionListener;
import loon.action.ActionTween;
import loon.action.Flip;
import loon.action.collision.CollisionObject;
import loon.action.collision.Gravity;
import loon.action.map.Field2D;
import loon.canvas.LColor;
import loon.component.layout.LayoutConstraints;
import loon.component.layout.LayoutManager;
import loon.component.layout.LayoutPort;
import loon.events.ClickListener;
import loon.events.EventAction;
import loon.events.EventActionN;
import loon.events.GameKey;
import loon.events.QueryEvent;
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
import loon.geom.XYZW;
import loon.opengl.GLEx;
import loon.opengl.LTextureFree;
import loon.opengl.TextureUtils;
import loon.utils.HelperUtils;
import loon.utils.MathUtils;
import loon.utils.StringUtils;
import loon.utils.timer.Duration;
import loon.utils.timer.StopwatchTimer;

/**
 * Loon桌面组件的核心,所有UI类组件基于此类产生
 */
public abstract class LComponent extends LObject<LContainer>
		implements Flip<LComponent>, CollisionObject, Visible, ActionBind, XY, BoxSize, LRelease {

	private class HideComponent implements ActionListener {

		private LComponent _component;

		public HideComponent(LComponent c) {
			this._component = c;
		}

		@Override
		public void start(ActionBind o) {

		}

		@Override
		public void process(ActionBind o) {

		}

		@Override
		public void stop(ActionBind o) {
			if (_component == null) {
				return;
			}
			if (_component.getParent() != null) {
				_component.getParent().remove((LComponent) o);
			}
			if (_component.getScreen() != null) {
				_component.getScreen().remove((LComponent) o);
			}
			if (_component._desktop != null) {
				_component._desktop.remove((LComponent) o);
			}
			_component.close();
		}

	}

	private Origin _origin = Origin.CENTER;

	private Vector2f _offset = new Vector2f();

	private ResizeListener<LComponent> _resizeListener;

	private EventAction _loopActionListener;

	private EventActionN _validateEvent;

	protected LTexture[] _imageUI = null;

	protected Shape _otherShape = null;

	protected float _fixedWidthOffset = 0f;

	protected float _fixedHeightOffset = 0f;

	protected boolean _component_paused = false;

	protected boolean _component_elastic = false;

	protected boolean _component_autoDestroy = true;

	protected boolean _component_resizabled = true;

	protected boolean isFull = false;

	protected boolean isSelectDraw = false;

	protected boolean isAllowSelectOfSelf = true;

	// 渲染状态
	public boolean customRendering = false;

	// 居中位置，组件坐标与大小
	private int _cam_x, _cam_y;

	protected float _width, _height;
	// 水平设置
	protected boolean _flipX = false, _flipY = false;
	// 缩放比例
	protected float _scaleX = 1f, _scaleY = 1f;
	// 屏幕位置
	protected int _screenX, _screenY;
	// 组件触摸后的移动值
	protected float _touchDownMovedValue = 2f;

	private LTextureFree _freeTextures;

	private final Vector2f _touchPoint = new Vector2f();

	private Vector2f _touchOffset = new Vector2f();

	private PointF _compPosition = new PointF();

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
	protected boolean _component_focusable = false;

	// 是否已选中
	protected boolean _component_selected = false;

	// 按下后是否移动组件
	protected boolean _component_downmoved = false;

	protected LColor _component_baseColor = null;

	protected LColor _colorTemp = new LColor();

	protected Desktop _desktop;

	protected boolean _isLimitMove = false, _drawBackground = true;

	protected boolean _keyPressedDown;

	protected LTexture _background;

	protected LayoutConstraints _rootConstraints = null;

	protected SysInput _input;

	// 点击事件监听
	protected ClickListener _clickListener;

	private TouchedClick _touchListener;

	// 充当卓面容器
	protected boolean desktopContainer;

	// 默认锁定当前组件(否则可以拖动)
	protected boolean _dragLocked = true;

	// 组件内部变量, 用于锁定当前组件的触屏（鼠标）与键盘事件
	private boolean _touchLocked = false, _keyLocked = false;

	// 计算按下与松开时间用的秒表
	private StopwatchTimer _downUpTimer = new StopwatchTimer();

	protected LRelease _disposed;

	public LComponent(Vector2f position, Vector2f size) {
		this(position.x(), position.y(), size.x(), size.y());
	}

	public LComponent(XY position, XY size) {
		this(MathUtils.ifloor(position.getX()), MathUtils.ifloor(position.getY()), MathUtils.ifloor(size.getX()),
				MathUtils.ifloor(size.getY()));
	}

	public LComponent(XYZW rect) {
		this(MathUtils.ifloor(rect.getX()), MathUtils.ifloor(rect.getY()), MathUtils.ifloor(rect.getZ()),
				MathUtils.ifloor(rect.getW()));
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
		this._component_resizabled = true;
		this.isAllowSelectOfSelf = true;
	}

	/**
	 * 刷新基本参数
	 * 
	 * @return
	 */
	public LComponent reset() {
		this.isAllowSelectOfSelf = true;
		this.isFull = false;
		this.isSelectDraw = false;
		this.customRendering = false;
		this._fixedWidthOffset = 0f;
		this._fixedHeightOffset = 0f;
		this._component_paused = false;
		this._component_elastic = false;
		this._component_autoDestroy = true;
		this._destroyed = false;
		this._flipX = _flipY = false;
		this._scaleX = _scaleY = 1f;
		this._cam_x = _cam_y = 0;
		this._screenX = _screenY = 0;
		this._touchDownMovedValue = 2f;
		this._downClick = false;
		this._pivotX = _pivotY = -1;
		this._component_visible = true;
		this._component_enabled = true;
		this._component_resizabled = true;
		this._component_focusable = false;
		this._component_selected = false;
		this._component_downmoved = false;
		this._downUpTimer.reset();
		this._origin = Origin.CENTER;
		return this;
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
		x += getScreenLeft();
		y += getScreenTop();
		int tempX = x;
		int tempY = y;
		int tempWidth = (width() - getScreenWidth());
		int tempHeight = (height() - getScreenHeight());

		final int limitX = tempX + tempWidth;
		final int limitY = tempY + tempHeight;

		if (_width >= getScreenWidth()) {
			if (limitX > tempWidth) {
				tempX = MathUtils.ifloor(getScreenWidth() - _width);
			} else if (limitX < 1) {
				tempX = x();
			}
		} else {
			return;
		}
		if (_height >= getScreenHeight()) {
			if (limitY > tempHeight) {
				tempY = MathUtils.ifloor(getScreenHeight() - _height);
			} else if (limitY < 1) {
				tempY = y();
			}
		} else {
			return;
		}
		this._cam_x = tempX;
		this._cam_y = tempY;
		this.setLocation(_cam_x, _cam_y);
	}

	@Override
	public void setLayer(final int z) {
		if (_objectSuper != null && getLayer() != z) {
			_objectSuper.setDirtyChildren(true);
		}
		super.setLayer(z);
	}

	protected boolean isNotMoveInScreen(int x, int y) {
		if (!this._isLimitMove) {
			return false;
		}
		x += getScreenLeft();
		y += getScreenTop();
		int width = (width() - getScreenWidth());
		int height = (height() - getScreenHeight());
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

	public LComponent pause() {
		if (!this._component_paused) {
			ActionControl.get().paused(this._component_paused = true, this);
		}
		return this;
	}

	public LComponent resume() {
		if (this._component_paused) {
			ActionControl.get().paused(this._component_paused = false, this);
		}
		return this;
	}

	public boolean isPaused() {
		return _component_paused;
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
	public void update(final long elapsedTime) {
		if (_destroyed) {
			return;
		}
		if (_objectSuper != null) {
			validatePosition();
		}
		if (_component_paused) {
			return;
		}
		process(elapsedTime);
		if (_loopActionListener != null) {
			HelperUtils.callEventAction(_loopActionListener, this);
		}
	}

	public LComponent loop(final EventAction la) {
		this._loopActionListener = la;
		return this;
	}

	public void process(final long elapsedTime) {
	}

	public abstract void createUI(final GLEx g, final int x, final int y);

	protected void preUI(final GLEx g) {
	}

	protected void postUI(final GLEx g) {
	}

	/**
	 * 渲染当前组件画面于指定绘图器之上
	 * 
	 * @param g
	 */
	public void createUI(final GLEx g) {
		if (_destroyed) {
			return;
		}
		if (!this._component_visible) {
			return;
		}
		if (_objectAlpha < 0.01f) {
			return;
		}
		final int blend = g.getBlendMode();
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
			final int width = MathUtils.floor(this._width);
			final int height = MathUtils.floor(this._height);
			if (this._component_elastic) {
				g.setClip(newX, newY, width, height);
			}
			if (update) {
				g.saveTx();
				final Affine2f tx = g.tx();
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
			g.setBlendMode(_GL_BLEND);
			preUI(g);
			if (_drawBackground && _background != null) {
				g.draw(_background, newX, newY, width, height, _component_baseColor);
			}
			if (this.customRendering) {
				this.createCustomUI(g, newX, newY, width, height);
			} else {
				this.createUI(g, newX, newY);
			}
			postUI(g);
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
			g.setBlendMode(blend);
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
	protected void createCustomUI(final GLEx g, final int x, final int y, final int w, final int h) {
	}

	protected void createCustomUI(final int w, final int h) {
	}

	public boolean contains(final float x, final float y) {
		return contains(x, y, 0, 0);
	}

	public boolean contains(final float x, final float y, final float width, final float height) {
		return (this._component_visible && getCollisionBox().contains(x, y, width, height));
	}

	public boolean intersects(final float x1, final float y1) {
		return intersects(x1, y1, 1f, 1f);
	}

	public boolean intersects(final float x1, final float y1, final float width, final float height) {
		return (this._component_visible) && getCollisionBox().intersects(x1, y1, width, height);
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
	public boolean intersects(final CollisionObject obj) {
		if (obj == null) {
			return false;
		}
		return intersects(obj.getRectBox());
	}

	@Override
	public boolean intersects(final Shape rect) {
		if (rect == null) {
			return false;
		}
		return getCollisionBox().intersects(rect);
	}

	@Override
	public boolean contains(final CollisionObject obj) {
		if (obj == null) {
			return false;
		}
		return getCollisionBox().contains(obj.getRectBox());
	}

	@Override
	public boolean contains(final Shape rect) {
		if (rect == null) {
			return false;
		}
		return getCollisionBox().contains(rect);
	}

	@Override
	public boolean collided(final Shape rect) {
		if (rect == null) {
			return false;
		}
		return getCollisionBox().collided(rect);
	}

	public boolean intersects(final LComponent comp) {
		return (this._component_visible) && (comp != null && comp.isVisible())
				&& getCollisionBox().intersects(comp.getCollisionBox());
	}

	public boolean contains(final LComponent comp) {
		return (this._component_visible) && (comp != null && comp.isVisible())
				&& getCollisionBox().contains(comp.getCollisionBox());
	}

	@Override
	public boolean isVisible() {
		return this._component_visible;
	}

	@Override
	public void setVisible(final boolean v) {
		if (this._component_visible == v) {
			return;
		}
		this._component_visible = v;
		if (this._component_visible) {
			fireShowComponent();
		} else {
			fireHideComponent();
		}
		if (_desktop != null) {
			this._desktop.setComponentStat(this, this._component_visible);
		}
	}

	protected void fireShowComponent() {

	}

	protected void fireHideComponent() {

	}

	public boolean isShowing() {
		boolean showing = true;
		LComponent comp = this;
		for (; comp != null;) {
			if (comp.isVisible() == false) {
				showing = false;
				break;
			}
			comp = comp.getParent();
		}
		return showing;
	}

	public boolean isEnabled() {
		return (this._objectSuper == null) ? this._component_enabled
				: (this._component_enabled && this._objectSuper.isEnabled());
	}

	public LComponent setEnabled(final boolean b) {
		if (this._component_enabled == b) {
			return this;
		}
		this._component_enabled = b;
		if (this._component_enabled) {
			this.onEnable();
		} else {
			this.onDisable();
		}
		if (_desktop != null) {
			this._desktop.setComponentStat(this, this._component_enabled);
		}
		return this;
	}

	public LComponent setActiveX(final boolean a) {
		this._component_paused = !a;
		this.setVisible(a);
		this.setEnabled(a);
		return this;
	}

	public boolean isActiveX() {
		return (this._component_enabled && this._component_visible && !this._component_paused);
	}

	public boolean isSelected() {
		return this._component_selected;
	}

	final LComponent setSelected(final boolean b) {
		this._component_selected = b;
		if (_component_selected) {
			processInFocus();
		} else {
			processOutFocus();
		}
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

	public LComponent getSelectedComponent() {
		final Desktop desk = getDesktop();
		return desk == null ? null : desk.getSelectedComponent();
	}

	public LComponent getClickedComponent() {
		final Desktop desk = getDesktop();
		return desk == null ? null : desk.getClickedComponent();
	}

	public boolean isDesktopClicked() {
		final Desktop desk = getDesktop();
		return desk == null ? false : desk.isCurrentClicked(this);
	}

	public boolean isDesktopFocusable() {
		final Desktop desk = getDesktop();
		return desk == null ? false : desk.isCurrentFocusable(this);
	}

	public boolean isFocusable() {
		return this._component_focusable;
	}

	public LComponent setFocusable(final boolean b) {
		this._component_focusable = b;
		return this;
	}

	public LComponent focusIn() {
		Desktop desktop = getDesktop();
		if (desktop != null) {
			desktop.selectComponent(this);
		}
		return this;
	}

	public LComponent focusOut() {
		Desktop desktop = getDesktop();
		if (desktop != null) {
			desktop.selectComponent(null);
		}
		return this;
	}

	public LContainer getContainer() {
		return getSuper();
	}

	protected final void setContainer(final LContainer container) {
		this.changeContainer(container);
		this.validatePosition();
	}

	protected final void changeContainer(final LContainer container) {
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
	public void setX(final Integer x) {
		if (this.getX() != x || x == 0) {
			super.setX(x);
			this.validatePosition();
		}
	}

	@Override
	public void setX(final float x) {
		if (this.getX() != x || x == 0) {
			super.setX(x);
			this.validatePosition();
		}
	}

	@Override
	public void setY(final Integer y) {
		if (this.getY() != y || y == 0) {
			super.setY(y);
			this.validatePosition();
		}
	}

	@Override
	public void setY(final float y) {
		if (this.getY() != y || y == 0) {
			super.setY(y);
			this.validatePosition();
		}
	}

	@Override
	public void setLocation(final Vector2f location) {
		setLocation(location.x, location.y);
	}

	@Override
	public void setLocation(final float dx, final float dy) {
		if (this.getX() != dx || this.getY() != dy || dx == 0 || dy == 0) {
			super.setLocation(dx, dy);
			this.validatePosition();
		}
	}

	@Override
	public void move(final float dx, final float dy) {
		if (dx != 0 || dy != 0) {
			final float moved = 128f;
			if (dx > -moved && dx < moved && dy > -moved && dy < moved) {
				super.move(dx, dy);
				this.validatePosition();
			}
		}
	}

	public LComponent setBounds(final float dx, final float dy, final float width, final float height) {
		this.setLocation(dx, dy);
		this.setSize(width, height);
		return this;
	}

	public LComponent fill(LComponent c) {
		if (c == null) {
			return this;
		}
		return setBounds(c.getX(), c.getY(), c.getWidth(), c.getHeight());
	}

	public boolean isResizabled() {
		return this._component_resizabled;
	}

	public LComponent setResizabled(final boolean r) {
		this._component_resizabled = r;
		return this;
	}

	@Override
	public LComponent setSize(final float w, final float h) {
		if (!MathUtils.equal(this._width, w) || !MathUtils.equal(this._height, h)) {
			this._width = MathUtils.max(1f, w);
			this._height = MathUtils.max(1f, h);
			this.validateResize();
		}
		return this;
	}

	public LComponent sizeBy(final float s) {
		return sizeBy(s, s);
	}

	public LComponent sizeBy(final float w, final float h) {
		if (!MathUtils.equal(this._width, w) || !MathUtils.equal(this._height, h)) {
			this._width += MathUtils.max(1f, w);
			this._height += MathUtils.max(1f, h);
			this.validateResize();
		}
		return this;
	}

	public LComponent setValidateEvent(final EventActionN n) {
		this._validateEvent = n;
		return this;
	}

	public EventActionN getValidateEvent() {
		return this._validateEvent;
	}

	public void validateNow() {
		this.validatePosition();
	}

	protected void validatePosition() {
		if (_objectSuper != null) {
			this._screenX = _objectLocation.x() + this._objectSuper.getScreenX();
			this._screenY = _objectLocation.y() + this._objectSuper.getScreenY();
		} else {
			this._screenX = _objectLocation.x();
			this._screenY = _objectLocation.y();
		}
		if (_validateEvent != null) {
			this._validateEvent.update();
		}
	}

	public int getScreenX() {
		return this._screenX;
	}

	public int getScreenY() {
		return this._screenY;
	}

	public int getScreenWidth() {
		final Screen screen = getScreen();
		return screen == null ? LSystem.viewSize.getWidth() : MathUtils.ifloor(screen.getScreenWidth());
	}

	public int getScreenHeight() {
		final Screen screen = getScreen();
		return screen == null ? LSystem.viewSize.getHeight() : MathUtils.ifloor(screen.getScreenHeight());
	}

	public int getScreenLeft() {
		final Screen screen = getScreen();
		return screen == null ? 0 : MathUtils.ifloor(screen.getScalePixelX());
	}

	public int getScreenTop() {
		final Screen screen = getScreen();
		return screen == null ? 0 : MathUtils.ifloor(screen.getScalePixelY());
	}

	public int getScreenRight() {
		final Screen screen = getScreen();
		return screen == null ? 0 : MathUtils.ifloor(screen.getScalePixelX() + screen.getScreenWidth());
	}

	public int getScreenBottom() {
		final Screen screen = getScreen();
		return screen == null ? 0 : MathUtils.ifloor(screen.getScalePixelY() + screen.getScreenHeight());
	}

	public float getDesktopX() {
		final Desktop desk = getDesktop();
		return desk == null ? this._screenX : desk.getX();
	}

	public float getDesktopY() {
		final Desktop desk = getDesktop();
		return desk == null ? this._screenY : desk.getY();
	}

	public float getDesktopWidth() {
		final Desktop desk = getDesktop();
		return desk == null ? this.getScreenWidth() : desk.getWidth();
	}

	public float getDesktopHeight() {
		final Desktop desk = getDesktop();
		return desk == null ? this.getScreenHeight() : desk.getHeight();
	}

	public float getDesktopLeft() {
		return getDesktopX();
	}

	public float getDesktopRight() {
		final Desktop desk = getDesktop();
		return desk == null ? this.getScreenX() + this.getScreenWidth() : desk.getX() + desk.getWidth();
	}

	public float getDesktopTop() {
		return getDesktopY();
	}

	public float getDesktopBottom() {
		final Desktop desk = getDesktop();
		return desk == null ? this.getScreenY() + this.getScreenHeight() : desk.getY() + desk.getHeight();
	}

	public int getScreenComponentLeft() {
		return MathUtils.ifloor(getScreenLeft() + _screenX);
	}

	public int getScreenComponentTop() {
		return MathUtils.ifloor(getScreenTop() + _screenY);
	}

	public int getScreenComponentRight() {
		return MathUtils.ifloor(getScreenRight() + getWidth());
	}

	public int getScreenComponentBottom() {
		return MathUtils.ifloor(getScreenBottom() + getHeight());
	}

	public LComponent size(final float s) {
		return size(s, s);
	}

	public LComponent size(final float w, final float h) {
		setWidth(w);
		setHeight(h);
		return this;
	}

	public LComponent width(final float w) {
		setWidth(w);
		return this;
	}

	public LComponent height(final float w) {
		setHeight(w);
		return this;
	}

	@Override
	public void setHeight(final float height) {
		if (!MathUtils.equal(height, this._height)) {
			this._height = MathUtils.max(1f, height);
			this.validateResize();
		}
	}

	@Override
	public void setWidth(final float width) {
		if (!MathUtils.equal(width, this._width)) {
			this._width = MathUtils.max(1f, width);
			this.validateResize();
		}
	}

	@Override
	public float getWidth() {
		return (this._width * _scaleX) - _fixedWidthOffset;
	}

	@Override
	public float getHeight() {
		return (this._height * _scaleY) - _fixedHeightOffset;
	}

	public float getFixedWidthOffset() {
		return _fixedWidthOffset;
	}

	public LComponent setFixedWidthOffset(final float fixedWidthOffset) {
		this._fixedWidthOffset = fixedWidthOffset;
		return this;
	}

	public float getFixedHeightOffset() {
		return _fixedHeightOffset;
	}

	public LComponent setFixedHeightOffset(final float fixedHeightOffset) {
		this._fixedHeightOffset = fixedHeightOffset;
		return this;
	}

	public RectBox getInScreenCollisionBox(final float offsetX, final float offsetY) {
		validatePosition();
		final Screen screen = getScreen();
		float screenX = 0f;
		float screenY = 0f;
		if (screen != null) {
			screenX = screen.getScalePixelX();
			screenY = screen.getScalePixelY();
		}
		final float newX = getScalePixelX() + screenX;
		final float newY = getScalePixelY() + screenY;
		final float newW = getWidth() - newX;
		final float newH = getHeight() - newY;
		return setRect(MathUtils.getBounds(newX + offsetX, newY + offsetY, newW - offsetX * 2f, newH - offsetY * 2f,
				_objectRotation, _objectRect));
	}

	public boolean containsInScreen(final XY xy) {
		return containsInScreen(xy.getX(), xy.getY());
	}

	public boolean containsInScreen(final float x, final float y) {
		return containsInScreen(x, y, 0f, 0f);
	}

	public boolean containsInScreen(final RectBox rect) {
		return containsInScreen(rect.x, rect.y, rect.width, rect.height);
	}

	public boolean containsInScreen(final CollisionObject obj) {
		if (obj == null) {
			return false;
		}
		return containsInScreen(obj.getRectBox());
	}

	public boolean containsInScreen(final float x, final float y, final float width, final float height) {
		return getInScreenCollisionBox(width, height).contains(x, y, width, height);
	}

	public boolean containsInScreen(final ActionBind obj) {
		if (obj == null) {
			return false;
		}
		return containsInScreen(obj.getRectBox());
	}

	public boolean intersectsInScreen(final XY xy) {
		return intersectsInScreen(xy.getX(), xy.getY());
	}

	public boolean intersectsInScreen(final float x, final float y) {
		return intersectsInScreen(x, y, 1f, 1f);
	}

	public boolean intersectsInScreen(final RectBox rect) {
		return intersectsInScreen(rect.x, rect.y, rect.width, rect.height);
	}

	public boolean intersectsInScreen(final CollisionObject obj) {
		if (obj == null) {
			return false;
		}
		return intersectsInScreen(obj.getRectBox());
	}

	public boolean intersectsInScreen(final ActionBind obj) {
		if (obj == null) {
			return false;
		}
		return intersectsInScreen(obj.getRectBox());
	}

	public boolean intersectsInScreen(float x, float y, float width, float height) {
		return getInScreenCollisionBox(width, height).intersects(x, y, width, height);
	}

	public RectBox getCollisionBox() {
		validatePosition();
		return setRect(MathUtils.getBounds(getScalePixelX(), getScalePixelY(), getWidth(), getHeight(), _objectRotation,
				_objectRect));
	}

	public float getScalePixelX() {
		if (_pivotX != -1f) {
			return getDrawScrollX() + _pivotX;
		}
		return ((_scaleX == 1f) ? getDrawScrollX() : (getDrawScrollX() + _origin.ox(getWidth())));
	}

	public float getScalePixelY() {
		if (_pivotY != -1f) {
			return getDrawScrollY() + _pivotY;
		}
		return ((_scaleY == 1f) ? getDrawScrollY() : (getDrawScrollY() + _origin.oy(getHeight())));
	}

	public LComponent getToolTipParent() {
		return this.tooltipParent;
	}

	public void setToolTipParent(final LComponent tipParent) {
		this.tooltipParent = tipParent;
	}

	public String getToolTipText() {
		return this.tooltip;
	}

	public LComponent setToolTipText(final String text) {
		if (StringUtils.isEmpty(text)) {
			return this;
		}
		this.tooltip = text;
		return this;
	}

	public boolean isTooltip() {
		return !StringUtils.isNullOrEmpty(this.tooltip);
	}

	protected void checkDownPosition() {
		if (_component_downmoved) {
			_compPosition.set(getX(), getY());
			move_down(_touchDownMovedValue);
		}
	}

	protected void checkUpPosition() {
		if (_component_downmoved) {
			setLocation(_compPosition);
		}
	}

	public void doClick() {
		if (_clickListener != null) {
			try {
				_clickListener.DoClick(this);
			} catch (Throwable cause) {
				LSystem.error("Component doClick() exception", cause);
			}
		}
	}

	public void downClick() {
		if (_clickListener != null) {
			try {
				_clickListener.DownClick(this, getUITouchX(), getUITouchY());
			} catch (Throwable cause) {
				LSystem.error("Component downClick() exception", cause);
			}
		}
	}

	public void dragClick() {
		if (_clickListener != null) {
			try {
				_clickListener.DragClick(this, getUITouchX(), getUITouchY());
			} catch (Throwable cause) {
				LSystem.error("Component dragClick() exception", cause);
			}
		}
	}

	public void upClick() {
		if (_clickListener != null) {
			try {
				_clickListener.UpClick(this, getUITouchX(), getUITouchY());
			} catch (Throwable cause) {
				LSystem.error("Component upClick() exception", cause);
			}
		}
	}

	public void downClick(final int x, final int y) {
		if (_clickListener != null) {
			_clickListener.DownClick(this, x, y);
		}
	}

	public void upClick(final int x, final int y) {
		if (_clickListener != null) {
			_clickListener.UpClick(this, x, y);
		}
	}

	public void dragClick(final int x, final int y) {
		if (_clickListener != null) {
			_clickListener.DragClick(this, x, y);
		}
	}

	protected void processTouchDragged() {
		if (!_dragLocked) {
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
		if (!this._downClick) {
			checkDownPosition();
			this._downUpTimer.start();
		}
		this._downClick = true;
	}

	protected void processTouchReleased() {
		if (this._downClick) {
			this._downUpTimer.stop();
			try {
				this.upClick();
			} catch (Throwable e) {
				LSystem.error("Component upClick() exception", e);
			}
			checkUpPosition();
			this._downClick = false;
		}
	}

	protected void processTouchMoved() {
	}

	protected void processTouchEntered() {
	}

	protected void processTouchExited() {
		_downClick = false;
		_downUpTimer.reset();
	}

	// 键盘操作
	protected void processKeyPressed() {
		if (this.isSelected()) {
			this.downKey();
		}
	}

	protected void processKeyReleased() {
		if (this.isSelected()) {
			this.upKey();
		}
	}

	protected void processResize() {
	}

	protected void processScale() {
	}

	// 获得焦点
	protected void processInFocus() {

	}

	// 离开焦点
	protected void processOutFocus() {

	}

	protected void keyPressed(final GameKey key) {
	}

	protected void keyReleased(final GameKey key) {
	}

	protected void onAttached() {
	}

	protected void onDetached() {
	}

	protected void onEnable() {
	}

	protected void onDisable() {
	}

	public void downKey() {
	}

	public void upKey() {
	}

	void keyPressed() {
		this.checkFocusKey();
		this.processKeyPressed();
		this._keyPressedDown = true;
	}

	void keyReleased() {
		if (this._keyPressedDown) {
			this.processKeyReleased();
			this._keyPressedDown = false;
		}
	}

	protected boolean isKeyPressedDown() {
		return _keyPressedDown;
	}

	/**
	 * 检测键盘事件焦点
	 * 
	 */
	protected void checkFocusKey() {
		if (this._input != null && this._input.getKeyPressed() == SysKey.ENTER) {
			this.transferFocus();
		} else {
			this.transferFocusBackward();
		}
	}

	protected boolean isTouchDownClick() {
		return _downClick;
	}

	public long getDownUpStartTimer() {
		return _downUpTimer.getStartTime();
	}

	public long getDownUpEndTimer() {
		return _downUpTimer.getEndTime();
	}

	public long getDownUpTimer() {
		return _downUpTimer.getDuration();
	}

	public long getDownUpLastTimer() {
		return _downUpTimer.getLastDuration();
	}

	public float getDownUpStartTimerSeconds() {
		return Duration.toS(getDownUpStartTimer());
	}

	public float getDownUpEndTimerSeconds() {
		return Duration.toS(getDownUpEndTimer());
	}

	public float getDownUpTimerSeconds() {
		return Duration.toS(getDownUpTimer());
	}

	public float getDownUpLastTimerSeconds() {
		return Duration.toS(getDownUpLastTimer());
	}

	public boolean isLongPressed() {
		return isLongPressed(LSystem.LONG_PRESSED_TIME);
	}

	public boolean isLongPressed(final float seconds) {
		if (_downUpTimer.completed()) {
			return false;
		}
		if (!_downClick) {
			final long timer = getDownUpTimer();
			if (timer >= seconds * LSystem.SECOND) {
				return true;
			}
		} else {
			long endTimer = 0;
			if (!_downUpTimer.completed()) {
				endTimer = _downUpTimer.getTimestamp();
			}
			endTimer = endTimer - getDownUpStartTimer();
			if (endTimer >= seconds * LSystem.SECOND) {
				return true;
			}
		}
		return false;
	}

	public LTexture[] getImageUI() {
		return this._imageUI;
	}

	public void setImageUI(final LTexture[] imageUI, final boolean processUI) {
		if (imageUI != null) {
			this._width = imageUI[0].getWidth();
			this._height = imageUI[0].getHeight();
		}
		this._imageUI = imageUI;
	}

	public void setImageUI(final int index, final LTexture imageUI) {
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

	public LComponent setBackground(final String fileName) {
		return this.setBackground(LSystem.loadTexture(fileName));
	}

	public LComponent setBackground(final LColor color) {
		return setBackground(TextureUtils.createTexture(1, 1, color), false);
	}

	public LComponent setBackgroundString(final String color) {
		return setBackground(new LColor(color));
	}

	public LComponent onlyBackground(final LTexture b) {
		this._drawBackground = false;
		this.setBackground(b);
		return this;
	}

	public LComponent setBackground(final LTexture b) {
		return setBackground(b, true);
	}

	public LComponent setBackground(final LTexture b, final boolean updateSize) {
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

	public LComponent setBackground(final LTexture b, final float w, final float h) {
		return setBackground(b, w, h, true);
	}

	public LComponent setBackground(final LTexture b, final float w, final float h, final boolean updateSize) {
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
		return _cam_x == 0 ? x() : _cam_x;
	}

	public int getCamY() {
		return _cam_y == 0 ? y() : _cam_y;
	}

	public LComponent clip(final boolean b) {
		return setElastic(b);
	}

	public boolean isClip() {
		return isElastic();
	}

	public boolean isElastic() {
		return this._component_elastic;
	}

	public LComponent setElastic(final boolean b) {
		if (getWidth() > LSystem.LAYER_TILE_SIZE || getHeight() > LSystem.LAYER_TILE_SIZE) {
			this._component_elastic = b;
		} else {
			this._component_elastic = false;
		}
		return this;
	}

	public boolean isAutoDestroy() {
		return _component_autoDestroy;
	}

	public LComponent setAutoDestroy(final boolean autoDestroy) {
		this._component_autoDestroy = autoDestroy;
		return this;
	}

	public boolean isActive() {
		return this._component_enabled;
	}

	public LComponent setActive(final boolean e) {
		this._component_enabled = e;
		return this;
	}

	@Override
	public Field2D getField2D() {
		return null;
	}

	public boolean isDrawSelect() {
		return this.isSelectDraw;
	}

	public LComponent setDrawSelect(final boolean select) {
		this.isSelectDraw = select;
		return this;
	}

	public LComponent debug(final boolean select) {
		return setDrawSelect(select);
	}

	public LComponent setScale(final float s) {
		this.setScale(s, s);
		return this;
	}

	@Override
	public void setScale(final float sx, final float sy) {
		if (!MathUtils.equal(this._scaleX, sx) || !MathUtils.equal(this._scaleY, sy)) {
			this._scaleX = sx;
			this._scaleY = sy;
			this.processScale();
		}
	}

	public LComponent scaleBy(final float s) {
		return scaleBy(s, s);
	}

	public LComponent scaleBy(final float sx, final float sy) {
		if (!MathUtils.equal(this._scaleX, sx) || !MathUtils.equal(this._scaleY, sy)) {
			this._scaleX += sx;
			this._scaleY += sy;
			this.processScale();
		}
		return this;
	}

	@Override
	public float getScaleX() {
		return this._scaleX;
	}

	@Override
	public float getScaleY() {
		return this._scaleY;
	}

	public float[] getUVs() {
		final float left = _flipX ? 1f : 0f;
		final float right = _flipX ? 0f : 1f;
		final float top = _flipY ? 1f : 0f;
		final float bottom = _flipY ? 0f : 1f;
		return new float[] { left, top, right, top, right, bottom, left, bottom };
	}

	@Override
	public boolean isBounded() {
		return true;
	}

	@Override
	public boolean inContains(final float x, final float y, final float w, final float h) {
		if (_objectSuper != null) {
			return _objectSuper.contains(x, y, w, h);
		}
		return getCollisionBox().contains(x, y, w, h);
	}

	private float toPixelScaleX(final float x) {
		return MathUtils.iceil(x / _scaleX);
	}

	private float toPixelScaleY(final float y) {
		return MathUtils.iceil(y / _scaleY);
	}

	protected float getInternalTouchDX() {
		return _input == null ? SysTouch.getDX() : _input.getTouchDX();
	}

	protected float getInternalTouchDY() {
		return _input == null ? SysTouch.getDY() : _input.getTouchDY();
	}

	protected float getInternalTouchX() {
		return _input == null ? SysTouch.getX() : _input.getTouchX();
	}

	protected float getInternalTouchY() {
		return _input == null ? SysTouch.getY() : _input.getTouchY();
	}

	public float getTouchDX() {
		return toPixelScaleX(getInternalTouchDX());
	}

	public float getTouchDY() {
		return toPixelScaleY(getInternalTouchDY());
	}

	public float getTouchX() {
		return toPixelScaleX(getInternalTouchX());
	}

	public float getTouchY() {
		return toPixelScaleY(getInternalTouchY());
	}

	public Vector2f getUITouch(final float x, final float y) {
		return getUITouch(x, y, null);
	}

	public Vector2f getUITouch(final float x, final float y, Vector2f pointResult) {
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
			pointResult.x = toPixelScaleX(newX) + _touchOffset.x;
			pointResult.y = toPixelScaleY(newY) + _touchOffset.y;
			return pointResult;
		}
		final float oldWidth = _width;
		final float oldHeight = _height;
		final float newWidth = getWidth();
		final float newHeight = getHeight();
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
		pointResult.addSelf(_touchOffset);
		return pointResult;
	}

	public Vector2f getUITouchXY() {
		float newX = 0f;
		float newY = 0f;
		float touchX = _input == null ? SysTouch.getX() : _input.getTouchX();
		float touchY = _input == null ? SysTouch.getY() : _input.getTouchY();
		if (getRotation() == 0) {
			if (_objectSuper == null) {
				newX = toPixelScaleX(touchX - getX());
				newY = toPixelScaleY(touchY - getY());
			} else {
				if (_objectSuper.isContainer() && (_objectSuper instanceof LScrollContainer)) {
					LScrollContainer scroll = (LScrollContainer) _objectSuper;
					newX = toPixelScaleX(touchX + scroll.getBoxScrollX() - _objectSuper.getX() - getX());
					newY = toPixelScaleY(touchY + scroll.getBoxScrollY() - _objectSuper.getY() - getY());
				} else {
					newX = toPixelScaleX(touchX - _objectSuper.getX() - getX());
					newY = toPixelScaleY(touchY - _objectSuper.getY() - getY());
				}
			}
			_touchPoint.set(newX, newY).addSelf(_touchOffset);
		} else {
			if (_objectSuper.isContainer() && (_objectSuper instanceof LScrollContainer)) {
				LScrollContainer scroll = (LScrollContainer) _objectSuper;
				newX = touchX + scroll.getBoxScrollX();
				newY = touchY + scroll.getBoxScrollY();
			} else {
				newX = touchX;
				newY = touchY;
			}
			return getUITouch(newX, newY, _touchPoint);
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
		if (_component_resizabled) {
			if (this._resizeListener != null) {
				this._resizeListener.onResize(this);
			}
			this.processResize();
		}
		return this;
	}

	@Override
	public RectBox getCollisionArea() {
		return getCollisionBox();
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

	public LComponent setPivotX(final float pX) {
		_pivotX = pX;
		return this;
	}

	public LComponent setPivotY(final float pY) {
		_pivotY = pY;
		return this;
	}

	public float getPivotX() {
		return _pivotX;
	}

	public float getPivotY() {
		return _pivotY;
	}

	public LComponent setPivot(final float pX, final float pY) {
		setPivotX(pX);
		setPivotY(pY);
		return this;
	}

	public void resetPivot() {
		setPivot(-1f, -1f);
	}

	public LComponent resetAnchor() {
		resetPivot();
		return this;
	}

	public LComponent setAnchor(final float scale) {
		return setAnchor(scale, scale);
	}

	public LComponent setAnchor(final float sx, final float sy) {
		setPivot(_width * sx, _height * sy);
		return this;
	}

	public Vector2f localCenter() {
		return new Vector2f(getCenterX() - this._pivotX * this.getWidth(),
				getCenterY() - this._pivotY * this.getHeight());
	}

	public LComponent coord(final float x, final float y) {
		setLocation(x, y);
		return this;
	}

	public LComponent show() {
		if (_component_visible && MathUtils.equal(_objectAlpha, 1f)) {
			return this;
		}
		this.setVisible(true);
		if (!getScreen().contains(this)) {
			getScreen().add(this);
		}
		return this;
	}

	public LComponent hide() {
		if (!_component_visible && MathUtils.equal(_objectAlpha, 0f)) {
			return this;
		}
		this.setVisible(false);
		if (getScreen().contains(this)) {
			getScreen().remove(this);
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
	public LComponent in(final float speed) {
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
	public LComponent out(final float speed) {
		this.selfAction().fadeOut(speed).start().setActionListener(new HideComponent(this));
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
		RectBox bounds = _objectSuper.getCollisionBox();
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
		RectBox bounds = _objectSuper.getCollisionBox();
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

	public boolean isTouchInUI(final float rotation) {
		return checkPointInRect(getUITouchXY(), getRectBox(), rotation);
	}

	protected boolean checkPointInRect(final Vector2f pos, final RectBox rect, final float rotation) {
		if (pos == null || rect == null) {
			return false;
		}
		final float radians = rotation * MathUtils.DEG_TO_RAD;
		final float centerX = rect.x + rect.getWidth() / 2f;
		final float centerY = rect.y + rect.getHeight() / 2f;
		final float deltaX = pos.x - centerX;
		final float deltaY = pos.y - centerY;
		final float rotatedX = centerX + (deltaX * MathUtils.cos(radians) - deltaY * MathUtils.sin(radians));
		final float rotatedY = centerY + (deltaX * MathUtils.sin(radians) + deltaY * MathUtils.cos(radians));
		return rotatedX >= rect.x && rotatedY >= rect.y && rotatedX <= (rect.x + rect.width)
				&& rotatedY <= (rect.y + rect.height);
	}

	public Vector2f getAbsolutePosition() {
		Vector2f screenPos = new Vector2f(getPosition());
		for (LComponent p = this.getParent(); p != null; p = p.getParent()) {
			screenPos.addSelf(p.getPosition());
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

	public boolean isOutScreen() {
		return getScalePixelX() < getScreenLeft() || getScalePixelX() + getWidth() > getScreenRight()
				|| getScalePixelY() < getScreenTop() || getScalePixelY() + getHeight() > getScreenBottom();
	}

	public LComponent moveInScreen() {
		if (getScreen() != null) {
			if (getScalePixelX() < getScreenLeft()) {
				setX(getScreenLeft());
			} else if (getScalePixelX() + getWidth() > getScreenRight()) {
				setX(getScreenRight() - getWidth());
			}
			if (getScalePixelY() < getScreenTop()) {
				setY(getScreenTop());
			} else if (getScalePixelY() + getHeight() > getScreenBottom()) {
				setY(getScreenBottom() - getHeight());
			}
		}
		return this;
	}

	@Override
	public LComponent setFlipX(final boolean x) {
		this._flipX = x;
		return this;
	}

	@Override
	public LComponent setFlipY(final boolean y) {
		this._flipY = y;
		return this;
	}

	@Override
	public LComponent setFlipXY(final boolean x, final boolean y) {
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

	public boolean isPointInUI(final Vector2f v) {
		return isPointInUI(v.x, v.y);
	}

	public boolean isPointInUI(final PointI p) {
		return isPointInUI(p.x, p.y);
	}

	public boolean isPointInUI(final PointF p) {
		return isPointInUI(p.x, p.y);
	}

	public boolean isPointInUI(final float x, final float y) {
		return getCollisionBox().contains(x, y);
	}

	public boolean isPointInUI() {
		return isPointInUI(getTouchX(), getTouchY());
	}

	public boolean isKeyDown(final int key) {
		if (_input == null) {
			return SysKey.isKeyPressed(key);
		}
		return _input.isKeyPressed(key) || SysKey.isKeyPressed(key);
	}

	public boolean isKeyUp(final int key) {
		if (_input == null) {
			return SysKey.isKeyReleased(key);
		}
		return _input.isKeyReleased(key) || SysKey.isKeyReleased(key);
	}

	public boolean isKeyDown(final String key) {
		if (_input == null) {
			return SysKey.isKeyPressed(key);
		}
		return _input.isKeyPressed(key) || SysKey.isKeyPressed(key);
	}

	public boolean isKeyUp(final String key) {
		if (_input == null) {
			return SysKey.isKeyReleased(key);
		}
		return _input.isKeyReleased(key) || SysKey.isKeyReleased(key);
	}

	public boolean isClickDown() {
		if (_input == null) {
			return SysTouch.isDown();
		}
		return _input.getTouchPressed() == SysTouch.TOUCH_DOWN;
	}

	public boolean isClickUp() {
		if (_input == null) {
			return SysTouch.isUp();
		}
		return _input.getTouchReleased() == SysTouch.TOUCH_UP;
	}

	public boolean isClickDrag() {
		if (_input == null) {
			return SysTouch.isDrag();
		}
		return _input.getTouchPressed() == SysTouch.TOUCH_DRAG && (_downClick && _input.isDragMoved());
	}

	public boolean isTouchResponseEvent(final float x, final float y) {
		return isVisible() && isEnabled() && !isLocked() && contains(x, y);
	}

	public boolean isTouchNotResponseEvent(final float x, final float y) {
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

	public boolean hasActions() {
		return ActionControl.get().containsKey(this);
	}

	public LComponent clearActions() {
		ActionControl.get().removeAllActions(this);
		return this;
	}

	public LComponent setDesktop(final Desktop d) {
		if (this._desktop == d) {
			return this;
		}
		this._desktop = d;
		if (d != null) {
			this._input = d._sysInput;
		}
		return this;
	}

	public Desktop getDesktop() {
		return this._desktop;
	}

	public LComponent setInput(final SysInput i) {
		this._input = i;
		return this;
	}

	public SysInput screenInput() {
		return this._input;
	}

	public Screen getScreen() {
		return (_desktop == null || _desktop._curScreen == null) ? LSystem.getProcess().getScreen()
				: _desktop._curScreen;
	}

	public LContainer getParent(final QueryEvent<LComponent> test) {
		LContainer p = getParent();
		while (p != null && !test.hit(p)) {
			p = p.getParent();
		}
		return p;
	}

	public LContainer getParentBefore(final QueryEvent<LComponent> test) {
		LContainer p = getParent();
		LContainer prev = null;
		while (p != null && !test.hit(p)) {
			prev = p;
			p = prev.getParent();
		}
		return prev;
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

	public LTextureFree freeRes() {
		if (_freeTextures == null) {
			_freeTextures = new LTextureFree();
		}
		return _freeTextures;
	}

	public Gravity getGravity() {
		return new Gravity(getUIName(), this);
	}

	@Override
	public RectBox getBoundingRect() {
		return getCollisionBox();
	}

	@Override
	public boolean containsPoint(final float x, final float y) {
		return getCollisionBox().contains(x, y, 1, 1);
	}

	public boolean isDescendantOf(final LComponent o) {
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

	public LComponent getTopLevelAncestor() {
		LComponent parent = this;
		for (; parent.getParent() != null;) {
			parent = parent.getParent();
		}
		return parent;
	}

	public Vector2f getOffset() {
		return _offset;
	}

	public LComponent setOffset(final float x, final float y) {
		this._offset.set(x, y);
		return this;
	}

	public LComponent setOffset(final Vector2f offset) {
		if (offset == null) {
			return this;
		}
		this._offset = offset;
		return this;
	}

	public float getOffsetX() {
		return _offset.x;
	}

	public LComponent setOffsetX(final float offsetX) {
		this._offset.setX(offsetX);
		return this;
	}

	public float getOffsetY() {
		return _offset.y;
	}

	public LComponent setOffsetY(final float offsetY) {
		this._offset.setY(offsetY);
		return this;
	}

	public ResizeListener<LComponent> getResizeListener() {
		return _resizeListener;
	}

	public LComponent setResizeListener(final ResizeListener<LComponent> listener) {
		this._resizeListener = listener;
		return this;
	}

	public LComponent softCenterOn(final float x, final float y) {
		final RectBox rect = getDesktop() == null ? LSystem.viewSize.getRect() : getDesktop().getBoundingBox();
		final LComponent comp = this.getSuper();
		if (x != 0) {
			float dx = (x - rect.getWidth() / 2 / this.getScaleX() - this.getX()) / 3;
			if (comp != null) {
				RectBox boundingBox = comp.getCollisionBox();
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
				RectBox boundingBox = comp.getCollisionBox();
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

	public boolean isTouchableListener() {
		return _touchListener != null;
	}

	public boolean isClickedListener() {
		return _clickListener != null;
	}

	public boolean isLocked() {
		return _dragLocked;
	}

	public LComponent setLocked(final boolean locked) {
		this._dragLocked = locked;
		return this;
	}

	public boolean isDragLocked() {
		return isLocked();
	}

	public LComponent setDragLocked(final boolean locked) {
		return setLocked(locked);
	}

	public LComponent dragLocked() {
		return setDragLocked(true);
	}

	public LComponent dragUnlocked() {
		return setDragLocked(false);
	}

	public LComponent setTouchLocked(final boolean locked) {
		this._touchLocked = locked;
		return this;
	}

	public boolean isTouchLocked() {
		return this._touchLocked;
	}

	public LComponent setKeyLocked(final boolean locked) {
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
		if (_clickListener != null) {
			_touchListener.addClickListener(_clickListener);
		}
		this._clickListener = _touchListener;
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

	public LComponent setTouchedEnabled(final boolean e) {
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

	public LComponent addClickListener(final ClickListener c) {
		this._clickListener = c;
		makeTouched();
		return this;
	}

	public LComponent S(final ClickListener c) {
		return SetClick(c);
	}

	public LComponent SetClick(final ClickListener c) {
		this._clickListener = c;
		return this;
	}

	public ClickListener getClick() {
		return _clickListener;
	}

	public LComponent clearListener() {
		this._clickListener = null;
		this._touchListener = null;
		this._resizeListener = null;
		this._loopActionListener = null;
		return this;
	}

	public ActionBindData getActionData() {
		return new ActionBindData((ActionBind) this);
	}

	public boolean isAllowTouch() {
		return !_touchLocked && _component_enabled;
	}

	public boolean isAllowKey() {
		return !_keyLocked && _component_enabled;
	}

	protected LComponent setAllowSelectOfSelf(final boolean a) {
		this.isAllowSelectOfSelf = a;
		return this;
	}

	public Vector2f getTouchOffset() {
		return _touchOffset;
	}

	public LComponent setTouchOffset(final Vector2f offset) {
		if (offset != null) {
			this._touchOffset = offset;
		}
		return this;
	}

	public LComponent setTouchOffset(final float x, final float y) {
		return setTouchOffset(Vector2f.at(x, y));
	}

	public boolean isTouchDownMoved() {
		return _component_downmoved;
	}

	public LComponent setTouchDownMoved(final boolean m) {
		this._component_downmoved = m;
		return this;
	}

	public float getTouchDownMovedValue() {
		return _touchDownMovedValue;
	}

	public LComponent setTouchDownMovedValue(final float m) {
		this._touchDownMovedValue = m;
		return this;
	}

	public LComponent setCustomShape(final Shape s) {
		this._otherShape = s;
		return this;
	}

	public Shape getCustomShape() {
		return this._otherShape;
	}

	public LComponent removeParent() {
		final LComponent comp = this.getSuper();
		if (comp != null && (comp instanceof LContainer)) {
			((LContainer) comp).remove(this);
			setParent(null);
		}
		return this;
	}

	public LComponent removeFromScreen() {
		if (_desktop != null) {
			_desktop.remove(this);
			return this;
		}
		getScreen().remove(this);
		return this;
	}

	public LComponent buildToScreen() {
		if (_desktop != null) {
			_desktop.add(this);
			return this;
		}
		getScreen().add(this);
		return this;
	}

	public LComponent dispose(final LRelease r) {
		_disposed = r;
		return this;
	}

	public LComponent freeImages() {
		if (!_component_autoDestroy) {
			return this;
		}
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
		return this;
	}

	@Override
	protected void _onDestroy() {
		if (!_component_autoDestroy) {
			return;
		}
		if (_disposed != null && _disposed != this) {
			_disposed.close();
		}
		this._component_visible = false;
		this._component_paused = false;
		this._component_selected = false;
		this._component_downmoved = false;
		this._component_resizabled = false;
		if (_desktop != null) {
			this._desktop.setComponentStat(this, false);
		}
		if (this._objectSuper != null) {
			this._objectSuper.remove(this);
		}
		this._objectSuper = null;
		this._input = null;
		this.freeImages();
		this.clearListener();
		removeActionEvents(this);
		destory();
	}

	public abstract void destory();

}
