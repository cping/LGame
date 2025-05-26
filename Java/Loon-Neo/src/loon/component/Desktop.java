/**
 * 
 * Copyright 2008 - 2009
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
 * @version 0.1
 */
package loon.component;

import loon.LRelease;
import loon.LSysException;
import loon.LSystem;
import loon.Screen;
import loon.Visible;
import loon.action.sprite.ISprite;
import loon.component.layout.LayoutConstraints;
import loon.component.layout.LayoutManager;
import loon.component.layout.LayoutPort;
import loon.component.layout.Margin;
import loon.events.GameKey;
import loon.events.QueryEvent;
import loon.events.ResizeListener;
import loon.events.SysInput;
import loon.events.SysTouch;
import loon.geom.DirtyRectList;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.opengl.ShaderMask;
import loon.opengl.light.Light2D;
import loon.opengl.light.Light2D.LightType;
import loon.utils.IArray;
import loon.utils.MathUtils;
import loon.utils.StringUtils;
import loon.utils.TArray;
import loon.utils.reply.Callback;

/**
 * 桌面组件总父类，用来注册，控制，以及渲染所有桌面组件（所有默认支持触屏的组件，被置于此）
 * 
 */
public class Desktop implements Visible, IArray, LRelease {

	// 是否在整个桌面组件中使用光源
	private boolean _useLight = false;

	private Light2D _light;

	// 是否使用shadermask改变画面显示效果
	private boolean _useShaderMask = false;

	private ShaderMask _shaderMask;

	private final DirtyRectList _dirtyList = new DirtyRectList();

	private final Vector2f _touchPoint = new Vector2f();

	// 输入设备监听
	protected SysInput _sysInput;

	// 当前运行屏幕
	protected Screen _curScreen;

	private ResizeListener<Desktop> _resizeListener;

	private LContainer _contentPane;

	private LComponent _modal;

	private LComponent _hoverComponent;

	private LComponent _selectedComponent;

	private LComponent _clickedComponent;

	private LComponent[] _clickComponents;

	private LToolTip _tooltip;

	private Vector2f _touchOffset = new Vector2f();

	private boolean _clicked;

	private boolean _visible;

	private boolean _closed;

	private final String _desktop_name;

	/**
	 * 空桌面控制
	 */
	public Desktop() {
		this(null, null, 1, 1);
	}

	/**
	 * 构造一个可用桌面
	 * 
	 * @param screen
	 */
	public Desktop(Screen screen) {
		this(null, screen, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	/**
	 * 构造一个可用桌面
	 * 
	 * @param screen
	 * @param width
	 * @param height
	 */
	public Desktop(Screen screen, float width, float height) {
		this(null, screen, MathUtils.iceil(width), MathUtils.iceil(height));
	}

	/**
	 * 构造一个可用桌面
	 * 
	 * @param screen
	 * @param width
	 * @param height
	 */
	public Desktop(Screen screen, int width, int height) {
		this(null, screen, width, height);
	}

	/**
	 * 构造一个可用桌面
	 * 
	 * @param name
	 * @param screen
	 * @param width
	 * @param height
	 */
	public Desktop(String name, Screen screen, int width, int height) {
		this._clickComponents = new LComponent[1];
		this._desktop_name = StringUtils.isEmpty(name) ? "Desktop" + LSystem.getDesktopSize() : name;
		this._visible = true;
		this._contentPane = new LPanel(0, 0, width, height);
		this._contentPane.desktopContainer = true;
		this._tooltip = new LToolTip();
		this._contentPane.add(this._tooltip);
		this._contentPane.setDesktop(this);
		this.setScreenInput(screen);
		this.setDesktop(this._contentPane);
		LSystem.pushDesktopPool(this);
	}

	public Desktop setScreenInput(Screen screen) {
		this._sysInput = screen;
		this._curScreen = screen;
		return this;
	}

	public Desktop setInput(SysInput i) {
		this._sysInput = i;
		return this;
	}

	@Override
	public int size() {
		return _contentPane.getComponentCount();
	}

	public Desktop addAt(LComponent comp, float x, float y) {
		if (comp != null) {
			comp.setLocation(x, y);
			add(comp);
		}
		return this;
	}

	public LSpriteUI addSprite(ISprite sprite) {
		final LSpriteUI ui = new LSpriteUI(sprite);
		add(ui);
		return ui;
	}

	public LSpriteUI addSpriteAt(ISprite sprite, float x, float y) {
		final LSpriteUI ui = new LSpriteUI(sprite);
		addAt(ui, x, y);
		return ui;
	}

	public LComponent addPadding(LComponent comp, float offX, float offY) {
		if (_closed) {
			return comp;
		}
		return _contentPane.addPadding(comp, offX, offY);
	}

	public LComponent addCol(LComponent comp) {
		if (_closed) {
			return comp;
		}
		return _contentPane.addCol(comp);
	}

	public LComponent addCol(LComponent comp, float offY) {
		if (_closed) {
			return comp;
		}
		return _contentPane.addCol(comp, offY);
	}

	public LComponent addRow(LComponent comp) {
		if (_closed) {
			return comp;
		}
		return _contentPane.addRow(comp);
	}

	public LComponent addRow(LComponent comp, float offX) {
		if (_closed) {
			return comp;
		}
		return _contentPane.addRow(comp, offX);
	}

	public LComponent add(LComponent comp) {
		if (_closed) {
			return comp;
		}
		if (comp == null) {
			return comp;
		}
		comp.setDesktop(this);
		if (comp.isFull) {
			this._sysInput.setRepaintMode(Screen.SCREEN_NOT_REPAINT);
		}
		this._contentPane.add(comp);
		this.processTouchMotionEvent();
		return comp;
	}

	public int remove(LComponent comp) {
		if (comp == null) {
			return -1;
		}
		final int removed = this.removeComponent(this._contentPane, comp);
		if (removed != -1) {
			this.processTouchMotionEvent();
		}
		return removed;
	}

	public boolean removeTag(Object tag) {
		final boolean removed = this.removeComponentTag(this._contentPane, tag);
		if (removed) {
			this.processTouchMotionEvent();
		}
		return removed;
	}

	public boolean removeUIName(String uiName) {
		final boolean removed = this.removeComponentUIName(this._contentPane, uiName);
		if (removed) {
			this.processTouchMotionEvent();
		}
		return removed;
	}

	public boolean removeName(String name) {
		final boolean removed = this.removeComponentName(this._contentPane, name);
		if (removed) {
			this.processTouchMotionEvent();
		}
		return removed;
	}

	public boolean removeNotTag(Object tag) {
		final boolean removed = this.removeComponentNotTag(this._contentPane, tag);
		if (removed) {
			this.processTouchMotionEvent();
		}
		return removed;
	}

	public boolean removeNotUIName(String uiName) {
		final boolean removed = this.removeComponentNotUIName(this._contentPane, uiName);
		if (removed) {
			this.processTouchMotionEvent();
		}
		return removed;
	}

	public boolean removeNotName(String name) {
		final boolean removed = this.removeComponentNotName(this._contentPane, name);
		if (removed) {
			this.processTouchMotionEvent();
		}
		return removed;
	}

	private boolean removeComponentUIName(LContainer container, String name) {
		return container.removeUIName(name);
	}

	private boolean removeComponentName(LContainer container, String name) {
		return container.removeName(name);
	}

	private boolean removeComponentTag(LContainer container, Object tag) {
		return container.removeTag(tag);
	}

	private boolean removeComponentNotName(LContainer container, String name) {
		return container.removeNotName(name);
	}

	private boolean removeComponentNotUIName(LContainer container, String name) {
		return container.removeNotUIName(name);
	}

	private boolean removeComponentNotTag(LContainer container, Object tag) {
		return container.removeNotTag(tag);
	}

	private int removeComponent(LContainer container, LComponent comp) {
		int removed = container.remove(comp);
		final LComponent[] components = container._childs;
		int i = 0;
		while (removed == -1 && i < components.length - 1) {
			if (components[i].isContainer()) {
				removed = this.removeComponent((LContainer) components[i], comp);
			}
			i++;
		}

		return removed;
	}

	/**
	 * 刷新当前桌面
	 * 
	 */
	public void update(long elapsedTime) {
		if (!this._visible) {
			return;
		}
		if (!this._contentPane.isVisible()) {
			return;
		}
		this.processEvents();
		try {
			// 刷新桌面中子容器组件
			this._contentPane.update(elapsedTime);
		} catch (Throwable cause) {
			LSystem.error("Desktop update() exception", cause);
		}
	}

	public LayoutConstraints getRootConstraints() {
		if (_contentPane != null) {
			return _contentPane.getRootConstraints();
		}
		return null;
	}

	public LayoutPort getLayoutPort() {
		if (_contentPane != null) {
			return _contentPane.getLayoutPort();
		}
		return null;
	}

	public LayoutPort getLayoutPort(final RectBox newBox, final LayoutConstraints newBoxConstraints) {
		if (_contentPane != null) {
			return _contentPane.getLayoutPort(newBox, newBoxConstraints);
		}
		return null;
	}

	public LayoutPort getLayoutPort(final LayoutPort src) {
		if (_contentPane != null) {
			return _contentPane.getLayoutPort(src);
		}
		return null;
	}

	public Desktop layoutElements(final LayoutManager manager, final LComponent... comps) {
		if (_contentPane != null) {
			_contentPane.layoutElements(manager, comps);
		}
		return this;
	}

	public Desktop layoutElements(final LayoutManager manager, final LayoutPort... ports) {
		if (_contentPane != null) {
			_contentPane.layoutElements(manager, ports);
		}
		return this;
	}

	public Desktop packLayout(final LayoutManager manager) {
		if (_contentPane != null) {
			_contentPane.packLayout(manager);
		}
		return this;
	}

	public Desktop packLayout(final LayoutManager manager, LComponent[] comps) {
		if (_contentPane != null) {
			_contentPane.packLayout(manager, comps, 0, 0, 0, 0, false);
		}
		return this;
	}

	public Desktop packLayout(final LayoutManager manager, LComponent[] comps, final float spacex, final float spacey,
			final float spaceWidth, final float spaceHeight) {
		if (_contentPane != null) {
			_contentPane.packLayout(manager, comps, spacex, spacey, spaceWidth, spaceHeight, false);
		}
		return this;
	}

	public Desktop packLayout(final LayoutManager manager, final float spacex, final float spacey,
			final float spaceWidth, final float spaceHeight) {
		if (_contentPane != null) {
			_contentPane.packLayout(manager, spacex, spacey, spaceWidth, spaceHeight);
		}
		return this;
	}

	public Desktop packLayout(final LayoutManager manager, final TArray<LComponent> comps, final float spacex,
			final float spacey, final float spaceWidth, final float spaceHeight) {
		if (_contentPane != null) {
			_contentPane.packLayout(manager, comps, spacex, spacey, spaceWidth, spaceHeight);
		}
		return this;
	}

	public Desktop packLayout(final LayoutManager manager, final LComponent[] comps, final float spacex,
			final float spacey, final float spaceWidth, final float spaceHeight, final boolean reversed) {
		if (_contentPane != null) {
			_contentPane.packLayout(manager, comps, spacex, spacey, spaceWidth, spaceHeight, reversed);
		}
		return this;
	}

	public Desktop setAutoDestory(final boolean a) {
		if (_contentPane != null) {
			_contentPane.setAutoDestroy(a);
		}
		return this;
	}

	public boolean isAutoDestory() {
		if (_contentPane != null) {
			return _contentPane.isAutoDestroy();
		}
		return false;
	}

	public Desktop doClick(int x, int y) {
		if (!this._contentPane.isVisible()) {
			return this;
		}
		final LComponent[] components = _contentPane._childs;
		for (int i = 0; i < components.length; i++) {
			LComponent component = components[i];
			if (component != null && component.intersects(x, y)) {
				component.update(0);
				component.processTouchPressed();
			}
		}
		_clicked = true;
		return this;
	}

	public Desktop doClicked(int x, int y) {
		if (!this._contentPane.isVisible()) {
			return this;
		}
		LComponent[] components = _contentPane._childs;
		for (int i = 0; i < components.length; i++) {
			LComponent component = components[i];
			if (component != null && component.intersects(x, y)) {
				component.update(0);
				component.processTouchReleased();
				component.processTouchClicked();
			}
		}
		_clicked = true;
		return this;
	}

	public Light2D createGlobalLight(LightType lt) {
		if (lt == null) {
			this._useLight = false;
			return null;
		}
		if (this._light == null) {
			this._light = new Light2D(lt);
		} else {
			this._light.updateLightType(lt);
		}
		this._useLight = true;
		return this._light;
	}

	public boolean isGlobalLight() {
		return this._useLight;
	}

	public Light2D getGlobalLight() {
		return _light;
	}

	public boolean isShaderMask() {
		return this._useShaderMask;
	}

	public ShaderMask getShaderMask() {
		return this._shaderMask;
	}

	public Desktop setShaderMask(ShaderMask mask) {
		this._shaderMask = mask;
		this._useShaderMask = (this._shaderMask != null);
		return this;
	}

	public void createUI(GLEx g) {
		if (!_visible) {
			return;
		}
		try {
			g.saveTx();
			if (_useLight && !_light.isClosed()) {
				_light.setAutoTouchTimer(_sysInput.getTouchX(), _sysInput.getTouchY(), _sysInput.getCurrentTimer());
				final ShaderMask lightMask = _light.getMask();
				lightMask.pushBatch(g);
				this._contentPane.createUI(g);
				lightMask.popBatch(g);
			} else {
				if (_useShaderMask) {
					_shaderMask.pushBatch(g);
				}
				this._contentPane.createUI(g);
				if (_useShaderMask) {
					_shaderMask.popBatch(g);
				}
			}
		} finally {
			g.restoreTx();
		}
	}

	public Desktop setClip(boolean clip) {
		_contentPane.setElastic(clip);
		return this;
	}

	public boolean isClip() {
		return _contentPane.isElastic();
	}

	public Desktop keyPressed(GameKey key) {
		if (this._contentPane != null && this._contentPane != this._selectedComponent) {
			this._contentPane.keyPressed(key);
		} else if (this._selectedComponent != null && this._selectedComponent.isAllowKey()) {
			this._selectedComponent.keyPressed(key);
		}
		return this;
	}

	public Desktop keyReleased(GameKey key) {
		if (this._contentPane != null && this._contentPane != this._selectedComponent) {
			this._contentPane.keyReleased(key);
		} else if (this._selectedComponent != null && this._selectedComponent.isAllowKey()) {
			this._selectedComponent.keyReleased(key);
		}
		return this;
	}

	public boolean isCurrentClicked(LComponent comp) {
		return comp == null ? false : _clickedComponent == comp;
	}

	public boolean isCurrentFocusable(LComponent comp) {
		return isCurrentClicked(comp) && (comp != null && comp.isFocusable());
	}

	/**
	 * 事件监听
	 * 
	 */
	public Desktop processEvents() {
		processTouchs();
		processKeys();
		return this;
	}

	/**
	 * 触发触屏(鼠标)事件
	 */
	public Desktop processTouchs() {
		// 鼠标滑动
		this.processTouchMotionEvent();

		// 鼠标事件
		if (this._hoverComponent != null && _hoverComponent.isAllowTouch()) {
			this.processTouchEvent();
		}
		return this;
	}

	/**
	 * 触发键盘事件
	 */
	public Desktop processKeys() {
		// 键盘事件
		if (this._selectedComponent != null && this._selectedComponent.isAllowKey()) {
			this.processKeyEvent();
		}
		return this;
	}

	/**
	 * 强行显示提示
	 */
	protected void showTooltip(LComponent comp) {
		if (comp.isTooltip() && _tooltip != null && !_tooltip.isRunning()) {
			this._tooltip.setLockedFadeIn(true);
			this._tooltip.setToolTipComponent(comp);
			this._tooltip.showTip();
		}
	}

	public Vector2f getUITouch(float x, float y, boolean cpy) {
		return getUITouch(x, y, _touchPoint, cpy);
	}

	public Vector2f getUITouch(float x, float y, Vector2f pointResult, boolean cpy) {
		if (pointResult == null) {
			pointResult = new Vector2f(x, y);
		} else {
			pointResult.set(x, y);
		}
		pointResult.addSelf(_touchOffset);
		return cpy ? pointResult.cpy() : pointResult;
	}

	/**
	 * 鼠标运动事件
	 * 
	 */
	private void processTouchMotionEvent() {
		if (this._hoverComponent != null && _hoverComponent.isAllowTouch() && this._sysInput.isMoving()) {
			if (this._sysInput.getTouchDX() != 0 || this._sysInput.getTouchDY() != 0 || SysTouch.getDX() != 0
					|| SysTouch.getDY() != 0) {
				this._hoverComponent.validatePosition();
				this._hoverComponent.processTouchDragged();
			}
		} else {
			final int typeButton = SysTouch.getButton();
			int touchX = _sysInput == null ? SysTouch.x() : this._sysInput.getTouchX();
			int touchY = _sysInput == null ? SysTouch.y() : this._sysInput.getTouchY();
			int touchDx = (int) (_sysInput == null ? SysTouch.getDX() : this._sysInput.getTouchDX());
			int touchDy = (int) (_sysInput == null ? SysTouch.getDY() : this._sysInput.getTouchDY());
			if (_sysInput != null) {
				final Vector2f touch = getUITouch(touchX, touchY, false);
				touchX = touch.x();
				touchY = touch.y();
				final Vector2f touchDrag = getUITouch(touchDx, touchDy, false);
				touchDx = touchDrag.x();
				touchDy = touchDrag.y();
			}
			// 获得当前窗体下鼠标坐标
			LComponent comp = null;
			if (typeButton != -1) {
				comp = this.findComponent(touchX, touchY);
			}
			if (comp != null && comp.isAllowTouch()) {
				comp.validatePosition();
				if (touchDx != 0 || touchDy != 0 || SysTouch.getDX() != 0 || SysTouch.getDY() != 0) {
					comp.processTouchMoved();
					if (comp.isTooltip() && _tooltip != null) {
						if (!this._tooltip._dismissing && comp.isPointInUI(touchX, touchY)) {
							// 刷新提示
							this._tooltip._dismiss = 0;
							this._tooltip._dismissing = true;
						}
					}
				}
				if (this._hoverComponent == null) {
					if (comp.isTooltip() && _tooltip != null) {
						this._tooltip.setToolTipComponent(comp);
					}
					if (typeButton != -1) {
						comp.processTouchEntered();
					}
				} else if (comp != this._hoverComponent && this._hoverComponent.isAllowTouch()) {
					if (comp.isTooltip() && _tooltip != null) {
						this._tooltip.setToolTipComponent(comp);
					}
					this._hoverComponent.processTouchExited();
					if (typeButton != -1) {
						comp.processTouchEntered();
					}
				}
			} else {
				// 如果没有对应的悬停提示数据
				if (_tooltip != null) {
					this._tooltip.setToolTipComponent(null);
				}
				if (this._hoverComponent != null && this._hoverComponent.isAllowTouch()) {
					this._hoverComponent.validatePosition();
					this._hoverComponent.processTouchExited();
				}
			}
			this._hoverComponent = comp;
		}
	}

	public LToolTip getToolTip() {
		return this._tooltip;
	}

	/**
	 * 设置全局通用的提示组件
	 * 
	 * @param tip
	 */
	public Desktop setToolTip(LToolTip tip) {
		this._contentPane.replace(this._tooltip, tip);
		this._tooltip = tip;
		return this;
	}

	/**
	 * 鼠标按下事件
	 * 
	 */
	private void processTouchEvent() {
		final int pressed = this._sysInput.getTouchPressed(), released = this._sysInput.getTouchReleased();
		if (pressed > Screen.NO_BUTTON) {
			final boolean mobile = LSystem.isMobile() || LSystem.isEmulateTouch();
			if (!mobile) {
				if (_tooltip != null) {
					this._tooltip.setToolTipComponent(null);
				}
			}
			if (_tooltip != null) {
				this._tooltip._reshow = 0;
				this._tooltip._initialFlag = 0;
			}
			if (!_clicked && this._hoverComponent != null && this._hoverComponent.isAllowTouch()) {
				this._hoverComponent.validatePosition();
				this._hoverComponent.processTouchPressed();
			}
			this._clickComponents[0] = this._hoverComponent;
			if (this._hoverComponent != null && this._hoverComponent.isAllowTouch()
					&& this._hoverComponent.isAllowSelectOfSelf) {
				final boolean down = pressed == SysTouch.TOUCH_DOWN;
				final boolean up = pressed == SysTouch.TOUCH_UP;
				if ((down || up) && this._hoverComponent != this._selectedComponent) {
					this.setClickComponent(this._hoverComponent, down, up);
					this.selectComponent(this._hoverComponent);
				}
			}
			// 如果手机环境长按
			if (mobile && this._hoverComponent != null && this._hoverComponent.isLongPressed()
					&& this._hoverComponent.isAllowTouch()) {
				showTooltip(this._hoverComponent);
			}
		}
		if (released > Screen.NO_BUTTON) {
			if (!_clicked && this._hoverComponent != null && this._hoverComponent.isAllowTouch()) {
				this._hoverComponent.validatePosition();
				this._hoverComponent.processTouchReleased();
				// 当释放鼠标时，点击事件生效
				if (this._clickComponents[0] == this._hoverComponent && this._hoverComponent != null
						&& this._hoverComponent.isAllowTouch()) {
					this._hoverComponent.processTouchClicked();
				}
			}
		}
		this._clicked = false;
	}

	/**
	 * 触发键盘事件
	 * 
	 */
	private void processKeyEvent() {
		if (this._selectedComponent != null && this._selectedComponent.isAllowKey()
				&& this._sysInput.getKeyPressed() != Screen.NO_KEY) {
			this._selectedComponent.validatePosition();
			this._selectedComponent.keyPressed();
		}
		if (this._selectedComponent != null && this._selectedComponent.isAllowKey()
				&& this._sysInput.getKeyReleased() != Screen.NO_KEY && this._selectedComponent != null) {
			this._selectedComponent.validatePosition();
			this._selectedComponent.keyReleased();
		}
	}

	/**
	 * 查找指定坐标点成员
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private LComponent findComponent(int x, int y) {
		if (this._modal != null && !this._modal.isContainer()) {
			return null;
		}
		// 返回子容器
		final LContainer panel = (this._modal == null) ? this._contentPane : ((LContainer) this._modal);
		final LComponent comp = panel.findComponent(x, y);
		return comp;
	}

	/**
	 * 清除容器焦点
	 */
	public Desktop clearFocus() {
		this.deselectComponent(true);
		return this;
	}

	void deselectComponent() {
		this.deselectComponent(false);
	}

	void deselectComponent(boolean clearFocus) {
		if (this._selectedComponent == null) {
			return;
		}
		this._selectedComponent.setSelected(false);
		if (clearFocus) {
			this._selectedComponent.setFocusable(false);
		}
		this._selectedComponent = null;
	}

	/**
	 * 获得当前点击的具体组件(selectComponent包含触屏拖拽和鼠标划过对象,_clickedComponent只为点击对象)
	 * 
	 * @param comp
	 */
	void setClickComponent(LComponent comp, boolean down, boolean up) {
		this._clickedComponent = comp;
		if (this._clickedComponent != null) {
			if (down) {
				this._clickedComponent.setFocusable(true);
			} else if (up) {
				this._clickedComponent.setFocusable(false);
			}
		}
	}

	/**
	 * 查找指定容器
	 * 
	 * @param comp
	 * @return
	 */
	boolean selectComponent(LComponent comp) {
		if (comp == null) {
			return false;
		}

		if (!comp.isVisible() || !comp.isEnabled() || !comp.isAllowSelectOfSelf) {
			return false;
		}

		// 清除最后部分
		this.deselectComponent();

		// 设定选中状态
		comp.setSelected(true);
		comp.setFocusable(true);

		this._selectedComponent = comp;

		return true;
	}

	void setDesktop(LComponent comp) {
		if (comp == null) {
			return;
		}
		if (comp.isContainer()) {
			LComponent[] child = ((LContainer) comp)._childs;
			for (int i = 0; i < child.length; i++) {
				this.setDesktop(child[i]);
			}
		}
		comp.setDesktop(this);
	}

	void setComponentStat(LComponent comp, boolean active) {
		if (comp == null) {
			return;
		}

		if (!active) {
			if (this._hoverComponent == comp) {
				this.processTouchMotionEvent();
			}

			if (this._selectedComponent == comp) {
				this.deselectComponent();
			}

			this._clickComponents[0] = null;

			if (this._modal == comp) {
				this._modal = null;
			}

		} else {
			this.processTouchMotionEvent();
		}

		if (comp.isContainer()) {
			LComponent[] components = ((LContainer) comp)._childs;
			int size = ((LContainer) comp).getComponentCount();
			for (int i = 0; i < size; i++) {
				this.setComponentStat(components[i], active);
			}
		}
	}

	void clearComponentsStat(final LComponent[] components) {
		if (components == null) {
			return;
		}

		boolean checkTouchMotion = false;
		for (int i = 0; i < components.length; i++) {
			LComponent comp = components[i];
			if (this._hoverComponent == comp) {
				checkTouchMotion = true;
			}
			if (this._selectedComponent == comp) {
				this.deselectComponent();
			}
			this._clickComponents[0] = null;
		}

		if (checkTouchMotion) {
			this.processTouchMotionEvent();
		}
	}

	public Desktop validateUI() {
		return this.validateContainer(this._contentPane);
	}

	final Desktop validateContainer(LContainer container) {
		final LComponent[] components = container._childs;
		final int size = container.getComponentCount();
		for (int i = 0; i < size; i++) {
			LComponent comp = components[i];
			if (comp != null && comp.isContainer() && comp instanceof LContainer) {
				return this.validateContainer((LContainer) components[i]);
			}
		}
		return this;
	}

	public LComponent[] getComponents() {
		return _contentPane.getComponents();
	}

	public LComponent getTopComponent() {
		final LComponent[] components = _contentPane._childs;
		final int size = components.length;
		if (size > 1) {
			return components[1];
		}
		return null;
	}

	public LComponent getBottomComponent() {
		final LComponent[] components = _contentPane._childs;
		final int size = components.length;
		if (size > 0) {
			return components[size - 1];
		}
		return null;
	}

	public LLayer getTopLayer() {
		final LComponent[] components = _contentPane._childs;
		final int size = components.length;
		for (int i = 0; i < size; i++) {
			LComponent comp = components[i];
			if (comp instanceof LLayer) {
				return (LLayer) comp;
			}
		}
		return null;
	}

	public LLayer getBottomLayer() {
		final LComponent[] components = _contentPane._childs;
		final int size = components.length;
		for (int i = size; i > 0; i--) {
			LComponent comp = components[i - 1];
			if (comp instanceof LLayer) {
				return (LLayer) comp;
			}
		}
		return null;
	}

	public UIControls createUIControls() {
		UIControls controls = null;
		if (_contentPane != null) {
			controls = _contentPane.createUIControls();
		} else {
			controls = new UIControls();
		}
		return controls;
	}

	public UIControls controls() {
		return createUIControls();
	}

	public UIControls findUINamesToUIControls(String... uiName) {
		UIControls controls = null;
		if (_contentPane != null && _contentPane._childs != null) {
			TArray<LComponent> comps = _contentPane.findUINames(uiName);
			controls = new UIControls(comps);
		} else {
			controls = new UIControls();
		}
		return controls;
	}

	public UIControls findNotUINamesToUIControls(String... uiName) {
		UIControls controls = null;
		if (_contentPane != null && _contentPane._childs != null) {
			TArray<LComponent> comps = _contentPane.findNotUINames(uiName);
			controls = new UIControls(comps);
		} else {
			controls = new UIControls();
		}
		return controls;
	}

	public UIControls findNamesToUIControls(String... name) {
		UIControls controls = null;
		if (_contentPane != null && _contentPane._childs != null) {
			TArray<LComponent> comps = _contentPane.findNames(name);
			controls = new UIControls(comps);
		} else {
			controls = new UIControls();
		}
		return controls;
	}

	public UIControls findNameContainsToUIControls(String... name) {
		UIControls controls = null;
		if (_contentPane != null && _contentPane._childs != null) {
			TArray<LComponent> comps = _contentPane.findNameContains(name);
			controls = new UIControls(comps);
		} else {
			controls = new UIControls();
		}
		return controls;
	}

	public UIControls findNotNamesToUIControls(String... name) {
		UIControls controls = null;
		if (_contentPane != null && _contentPane._childs != null) {
			TArray<LComponent> comps = _contentPane.findNotNames(name);
			controls = new UIControls(comps);
		} else {
			controls = new UIControls();
		}
		return controls;
	}

	public UIControls findTagsToUIControls(Object... o) {
		UIControls controls = null;
		if (_contentPane != null && _contentPane._childs != null) {
			TArray<LComponent> comps = _contentPane.findTags(o);
			controls = new UIControls(comps);
		} else {
			controls = new UIControls();
		}
		return controls;
	}

	public UIControls findNotTagsToUIControls(Object... o) {
		UIControls controls = null;
		if (_contentPane != null && _contentPane._childs != null) {
			TArray<LComponent> comps = _contentPane.findNotTags(o);
			controls = new UIControls(comps);
		} else {
			controls = new UIControls();
		}
		return controls;
	}

	public LComponent findComponentUI(String name) {
		return this._contentPane.findComponentUI(name);
	}

	public float getWidth() {
		return this._contentPane.getWidth();
	}

	public float getHeight() {
		return this._contentPane.getHeight();
	}

	public Desktop setSize(int w, int h) {
		this._contentPane.setSize(w, h);
		return this;
	}

	public Desktop hide() {
		setVisible(false);
		return this;
	}

	public Desktop show() {
		setVisible(true);
		return this;
	}

	public LContainer getContentPane() {
		return this._contentPane;
	}

	public Desktop setContentPane(LContainer pane) {
		pane.setBounds(0, 0, this.getWidth(), this.getHeight());
		this._contentPane = pane;
		this.setDesktop(this._contentPane);
		return this;
	}

	public LComponent getHoverComponent() {
		return this._hoverComponent;
	}

	public LComponent getSelectedComponent() {
		return this._selectedComponent;
	}

	public LComponent getClickedComponent() {
		return this._clickedComponent;
	}

	public LComponent getModal() {
		return this._modal;
	}

	public Desktop setModal(LComponent comp) {
		if (comp != null && !comp.isVisible()) {
			throw new LSysException("Can't set invisible component as _modal component!");
		}
		this._modal = comp;
		return this;
	}

	public boolean contains(LComponent comp) {
		return _contentPane.contains(comp);
	}

	public Desktop setSortableChildren(boolean v) {
		_contentPane.setSortableChildren(v);
		return this;
	}

	public boolean isSortableChildren() {
		return _contentPane.isSortableChildren();
	}

	public LComponent get() {
		return this._contentPane.get();
	}

	public Desktop removeAll() {
		clear();
		return this;
	}

	@Override
	public void clear() {
		if (_contentPane != null) {
			_contentPane.clear();
		}
	}

	public Desktop sortDesktop() {
		if (_contentPane != null) {
			_contentPane.sortComponents();
		}
		return this;
	}

	public Desktop scrollBy(float x, float y) {
		if (_contentPane != null) {
			_contentPane.scrollBy(x, y);
		}
		return this;
	}

	public Desktop scrollTo(float x, float y) {
		if (_contentPane != null) {
			_contentPane.scrollTo(x, y);
		}
		return this;
	}

	public float scrollX() {
		if (_contentPane != null) {
			return _contentPane.scrollX();
		}
		return 0f;
	}

	public float scrollY() {
		if (_contentPane != null) {
			return _contentPane.scrollY();
		}
		return 0f;
	}

	public Desktop scrollX(float x) {
		if (_contentPane != null) {
			_contentPane.scrollX(x);
		}
		return this;
	}

	public Desktop scrollY(float y) {
		if (_contentPane != null) {
			_contentPane.scrollY(y);
		}
		return this;
	}

	public Desktop freeComponent() {
		this._clicked = false;
		this._hoverComponent = null;
		this._selectedComponent = null;
		this._clickedComponent = null;
		this._clickComponents[0] = null;
		return this;
	}

	public Desktop resize() {
		freeComponent();
		if (_resizeListener != null) {
			_resizeListener.onResize(this);
		}
		if (_contentPane != null) {
			_contentPane.processResize();
		}
		return this;
	}

	public Desktop forChildren(Callback<LComponent> callback) {
		if (_contentPane != null) {
			_contentPane.forChildren(callback);
		}
		return this;
	}

	/**
	 * 删除符合指定条件的组件并返回操作的集合
	 * 
	 * @param query
	 * @return
	 */
	public TArray<LComponent> remove(QueryEvent<LComponent> query) {
		return _contentPane.remove(query);
	}

	/**
	 * 查找符合指定条件的组件并返回操作的集合
	 * 
	 * @param query
	 * @return
	 */
	public TArray<LComponent> find(QueryEvent<LComponent> query) {
		return _contentPane.find(query);
	}

	/**
	 * 删除指定条件的组件并返回操作的集合
	 * 
	 * @param query
	 * @return
	 */
	public TArray<LComponent> delete(QueryEvent<LComponent> query) {
		return _contentPane.delete(query);
	}

	/**
	 * 查找符合指定条件的组件并返回操作的集合
	 * 
	 * @param query
	 * @return
	 */
	public TArray<LComponent> select(QueryEvent<LComponent> query) {
		return _contentPane.select(query);
	}

	public Margin margin(boolean vertical, float left, float top, float right, float bottom) {
		float size = vertical ? getHeight() : getWidth();
		if (_closed) {
			return new Margin(size, vertical);
		}
		return _contentPane.margin(vertical, left, top, right, bottom);
	}

	public boolean isVisibleInParents() {
		return isVisibleInParents(_selectedComponent);
	}

	public boolean isVisibleInParents(LComponent comp) {
		if (comp == null) {
			return false;
		}
		TArray<LComponent> parentList = new TArray<LComponent>();
		for (LComponent parent = comp.getParent(); parent != null; parent = parent.getParent()) {
			parentList.add(parent);
		}
		if (parentList.size() > 0) {
			Vector2f pos = new Vector2f(0, 0);
			Vector2f rect = new Vector2f(0, 0);
			Vector2f absolutePosition = comp.getAbsolutePosition();

			Vector2f cSize = comp.getSize();
			Vector2f cPos = comp.getPosition();

			float lx = absolutePosition.x;
			float rx = absolutePosition.x + cSize.x;
			float ty = absolutePosition.y;
			float by = absolutePosition.y + cSize.y;

			if (cPos.x > comp.getParent().getSize().x || cPos.x + cSize.x < 0 || cPos.y > comp.getParent().getSize().y
					|| cPos.y + cSize.y < 0) {
				return false;
			}
			if (parentList.size() != 1) {
				for (int i = parentList.size() - 1; i >= 1; i--) {
					LComponent parent = parentList.get(i);
					pos.addSelf(parent.getPosition());
					rect.set(pos).addSelf(parent.getSize());
					if (lx > rect.x || rx < pos.x || ty > rect.y || by < pos.y) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public SysInput getInput() {
		return _sysInput;
	}

	public Screen getScreen() {
		return _curScreen;
	}

	public float getScreenX() {
		return _curScreen == null ? 0 : _curScreen.getX();
	}

	public float getScreenY() {
		return _curScreen == null ? 0 : _curScreen.getY();
	}

	public float getX() {
		return _contentPane == null ? 0 : _contentPane.getX();
	}

	public float getY() {
		return _contentPane == null ? 0 : _contentPane.getY();
	}

	public float getStageX() {
		return (getX() - getScreenX()) / _contentPane.getScaleX();
	}

	public float getStageY() {
		return (getX() - getScreenX()) / _contentPane.getScaleY();
	}

	private void addRect(TArray<RectBox> rects, RectBox rect) {
		if (rect.width > 1 && rect.height > 1) {
			if (!rects.contains(rect)) {
				rects.add(rect);
			}
		}
	}

	private void addAllRect(TArray<RectBox> rects, LComponent comp) {
		if (comp.isContainer()) {
			LContainer c = (LContainer) comp;
			LComponent[] childs = c._childs;
			if (childs != null) {
				for (int i = childs.length - 1; i > -1; i--) {
					LComponent cc = childs[i];
					if (cc != null) {
						addRect(rects, cc.getCollisionBox().add(cc.getCollisionBox()));
					}
				}
			}
		} else {
			addRect(rects, comp.getCollisionBox());
		}
	}

	public DirtyRectList getDirtyList() {
		final TArray<RectBox> rects = new TArray<RectBox>();
		LComponent[] childs = _contentPane._childs;
		if (childs != null) {
			for (int i = childs.length - 1; i > -1; i--) {
				LComponent comp = childs[i];
				if (comp != null) {
					addAllRect(rects, comp);
				}
			}
		}
		_dirtyList.clear();
		for (RectBox rect : rects) {
			if (rect.width > 1 && rect.height > 1) {
				_dirtyList.add(rect);
			}
		}
		return _dirtyList;
	}

	public String getName() {
		return _desktop_name;
	}

	public ResizeListener<Desktop> getResizeListener() {
		return _resizeListener;
	}

	public Desktop setResizeListener(ResizeListener<Desktop> listener) {
		this._resizeListener = listener;
		return this;
	}

	public RectBox getBoundingBox() {
		return this._contentPane == null ? _curScreen.getRectBox().cpy() : this._contentPane.getRectBox().cpy();
	}

	public Vector2f getTouchOffset() {
		return _touchOffset;
	}

	public Desktop setTouchOffset(Vector2f offset) {
		if (offset != null) {
			this._touchOffset = offset;
		}
		return this;
	}

	public Desktop setTouchOffset(float x, float y) {
		return setTouchOffset(Vector2f.at(x, y));
	}

	@Override
	public boolean isEmpty() {
		return _contentPane.isEmpty();
	}

	@Override
	public boolean isNotEmpty() {
		return _contentPane.isNotEmpty();
	}

	@Override
	public boolean isVisible() {
		return _visible;
	}

	@Override
	public void setVisible(boolean v) {
		this._visible = v;
	}

	@Override
	public String toString() {
		return super.toString() + " " + "[name=" + _desktop_name + ", total=" + size() + ", content=" + _contentPane
				+ "]";
	}

	public boolean isClosed() {
		return _closed;
	}

	@Override
	public void close() {
		this._visible = false;
		if (_contentPane != null) {
			_contentPane.close();
		}
		this._closed = true;
		this._resizeListener = null;
		if (_light != null) {
			this._light.close();
			this._light = null;
		}
		this._useLight = false;
		if (_shaderMask != null) {
			_useShaderMask = false;
			_shaderMask.close();
			_shaderMask = null;
		}
		LSystem.popDesktopPool(this);
	}

}
