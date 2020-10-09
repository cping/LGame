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
import loon.event.GameKey;
import loon.event.QueryEvent;
import loon.event.ResizeListener;
import loon.event.SysInput;
import loon.event.SysTouch;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.utils.StringUtils;
import loon.utils.TArray;
import loon.utils.reply.Callback;

/**
 * 桌面组件总父类，用来注册，控制，以及渲染所有桌面组件（所有默认支持触屏的组件，被置于此）
 * 
 */
public class Desktop implements Visible, LRelease {

	// 输入设备监听
	protected final Screen input;

	private ResizeListener<Desktop> _resizeListener;

	private LContainer contentPane;

	private LComponent modal;

	private LComponent hoverComponent;

	private LComponent selectedComponent;

	private LComponent[] clickComponent;

	private LToolTip tooltip;

	private boolean dvisible;

	private boolean dclosed;

	private final String desktop_name;

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
		this(null, screen, (int) width, (int) height);
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
	 * @param input
	 * @param width
	 * @param height
	 */
	public Desktop(String name, Screen screen, int width, int height) {
		this.clickComponent = new LComponent[1];
		this.desktop_name = StringUtils.isEmpty(name) ? "Desktop" + LSystem.getDesktopSize() : name;
		this.dvisible = true;
		this.contentPane = new LPanel(0, 0, (int) width, (int) height);
		this.contentPane.desktopContainer = true;
		this.input = screen;
		this.tooltip = new LToolTip();
		this.contentPane.add(this.tooltip);
		this.contentPane.setDesktop(this);
		this.setDesktop(this.contentPane);
		LSystem.pushDesktopPool(this);
	}

	public int size() {
		return contentPane.getComponentCount();
	}

	public Desktop addAt(LComponent comp, float x, float y) {
		if (comp != null) {
			comp.setLocation(x, y);
			add(comp);
		}
		return this;
	}

	public LSpriteUI addSprite(ISprite sprite) {
		LSpriteUI ui = new LSpriteUI(sprite);
		add(ui);
		return ui;
	}

	public LSpriteUI addSpriteAt(ISprite sprite, float x, float y) {
		LSpriteUI ui = new LSpriteUI(sprite);
		addAt(ui, x, y);
		return ui;
	}

	public LComponent addPadding(LComponent comp, float offX, float offY) {
		if (dclosed) {
			return comp;
		}
		return contentPane.addPadding(comp, offX, offY);
	}

	public LComponent addCol(LComponent comp) {
		if (dclosed) {
			return comp;
		}
		return contentPane.addCol(comp);
	}

	public LComponent addCol(LComponent comp, float offY) {
		if (dclosed) {
			return comp;
		}
		return contentPane.addCol(comp, offY);
	}

	public LComponent addRow(LComponent comp) {
		if (dclosed) {
			return comp;
		}
		return contentPane.addRow(comp);
	}

	public LComponent addRow(LComponent comp, float offX) {
		if (dclosed) {
			return comp;
		}
		return contentPane.addRow(comp, offX);
	}

	public LComponent add(LComponent comp) {
		if (dclosed) {
			return comp;
		}
		if (comp == null) {
			return comp;
		}
		comp.setDesktop(this);
		if (comp.isFull) {
			this.input.setRepaintMode(Screen.SCREEN_NOT_REPAINT);
		}
		this.contentPane.add(comp);
		this.processTouchMotionEvent();
		return comp;
	}

	public int remove(LComponent comp) {
		if (comp == null) {
			return -1;
		}
		int removed = this.removeComponent(this.contentPane, comp);
		if (removed != -1) {
			this.processTouchMotionEvent();
		}
		return removed;
	}

	public boolean removeTag(Object tag) {
		boolean removed = this.removeComponentTag(this.contentPane, tag);
		if (removed) {
			this.processTouchMotionEvent();
		}
		return removed;
	}

	public boolean removeUIName(String uiName) {
		boolean removed = this.removeComponentUIName(this.contentPane, uiName);
		if (removed) {
			this.processTouchMotionEvent();
		}
		return removed;
	}

	public boolean removeName(String name) {
		boolean removed = this.removeComponentName(this.contentPane, name);
		if (removed) {
			this.processTouchMotionEvent();
		}
		return removed;
	}

	public boolean removeNotTag(Object tag) {
		boolean removed = this.removeComponentNotTag(this.contentPane, tag);
		if (removed) {
			this.processTouchMotionEvent();
		}
		return removed;
	}

	public boolean removeNotUIName(String uiName) {
		boolean removed = this.removeComponentNotUIName(this.contentPane, uiName);
		if (removed) {
			this.processTouchMotionEvent();
		}
		return removed;
	}

	public boolean removeNotName(String name) {
		boolean removed = this.removeComponentNotName(this.contentPane, name);
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
		LComponent[] components = container._childs;
		int i = 0;
		while (removed == -1 && i < components.length - 1) {
			if (components[i].isContainer()) {
				removed = this.removeComponent((LContainer) components[i], comp);
			}
			i++;
		}

		return removed;
	}

	private boolean isClicked;

	/**
	 * 刷新当前桌面
	 * 
	 */
	public void update(long elapsedTime) {
		if (!this.dvisible) {
			return;
		}
		if (!this.contentPane.isVisible()) {
			return;
		}
		this.processEvents();
		try {
			// 刷新桌面中子容器组件
			this.contentPane.update(elapsedTime);
		} catch (Throwable cause) {
			LSystem.error("Desktop update() exception", cause);
		}
	}

	public LayoutConstraints getRootConstraints() {
		if (contentPane != null) {
			return contentPane.getRootConstraints();
		}
		return null;
	}

	public LayoutPort getLayoutPort() {
		if (contentPane != null) {
			return contentPane.getLayoutPort();
		}
		return null;
	}

	public LayoutPort getLayoutPort(final RectBox newBox, final LayoutConstraints newBoxConstraints) {
		if (contentPane != null) {
			return contentPane.getLayoutPort(newBox, newBoxConstraints);
		}
		return null;
	}

	public LayoutPort getLayoutPort(final LayoutPort src) {
		if (contentPane != null) {
			return contentPane.getLayoutPort(src);
		}
		return null;
	}

	public void layoutElements(final LayoutManager manager, final LComponent... comps) {
		if (contentPane != null) {
			contentPane.layoutElements(manager, comps);
		}
	}

	public void layoutElements(final LayoutManager manager, final LayoutPort... ports) {
		if (contentPane != null) {
			contentPane.layoutElements(manager, ports);
		}
	}

	public void packLayout(final LayoutManager manager) {
		if (contentPane != null) {
			contentPane.packLayout(manager);
		}
	}

	public void packLayout(final LayoutManager manager, final float spacex, final float spacey, final float spaceWidth,
			final float spaceHeight) {
		if (contentPane != null) {
			contentPane.packLayout(manager, spacex, spacey, spaceWidth, spaceHeight);
		}
	}

	public void setAutoDestory(final boolean a) {
		if (contentPane != null) {
			contentPane.setAutoDestroy(a);
		}
	}

	public boolean isAutoDestory() {
		if (contentPane != null) {
			return contentPane.isAutoDestroy();
		}
		return false;
	}

	public void doClick(int x, int y) {
		if (!this.contentPane.isVisible()) {
			return;
		}
		LComponent[] components = contentPane._childs;
		for (int i = 0; i < components.length; i++) {
			LComponent component = components[i];
			if (component != null && component.intersects(x, y)) {
				component.update(0);
				component.processTouchPressed();
			}
		}
		isClicked = true;
	}

	public void doClicked(int x, int y) {
		if (!this.contentPane.isVisible()) {
			return;
		}
		LComponent[] components = contentPane._childs;
		for (int i = 0; i < components.length; i++) {
			LComponent component = components[i];
			if (component != null && component.intersects(x, y)) {
				component.update(0);
				component.processTouchReleased();
				component.processTouchClicked();
			}
		}
		isClicked = true;
	}

	public void createUI(GLEx g) {
		if (!dvisible) {
			return;
		}
		try {
			g.saveTx();
			this.contentPane.createUI(g);
		} finally {
			g.restoreTx();
		}
	}

	public void keyPressed(GameKey key) {
		if (this.contentPane != null && this.contentPane != this.selectedComponent) {
			this.contentPane.keyPressed(key);
		} else if (this.selectedComponent != null && !this.selectedComponent._keyLocked) {
			this.selectedComponent.keyPressed(key);
		}
	}

	public void keyReleased(GameKey key) {
		if (this.contentPane != null && this.contentPane != this.selectedComponent) {
			this.contentPane.keyReleased(key);
		} else if (this.selectedComponent != null && !this.selectedComponent._keyLocked) {
			this.selectedComponent.keyReleased(key);
		}
	}

	/**
	 * 事件监听
	 * 
	 */
	public void processEvents() {
		processTouchs();
		processKeys();
	}

	/**
	 * 触发触屏(鼠标)事件
	 */
	public void processTouchs() {
		// 鼠标滑动
		this.processTouchMotionEvent();
		// 鼠标事件
		if (this.hoverComponent != null && !this.hoverComponent._touchLocked && this.hoverComponent.isEnabled()) {
			this.processTouchEvent();
		}
	}

	/**
	 * 触发键盘事件
	 */
	public void processKeys() {
		// 键盘事件
		if (this.selectedComponent != null && !this.selectedComponent._keyLocked
				&& this.selectedComponent.isEnabled()) {
			this.processKeyEvent();
		}
	}

	/**
	 * 鼠标运动事件
	 * 
	 */
	private void processTouchMotionEvent() {
		if (this.hoverComponent != null && !this.hoverComponent._touchLocked && this.hoverComponent.isEnabled()
				&& this.input.isMoving()) {
			if (this.input.getTouchDX() != 0 || this.input.getTouchDY() != 0 || SysTouch.getDX() != 0
					|| SysTouch.getDY() != 0) {
				this.hoverComponent.processTouchDragged();
				if (LSystem.isMobile() || LSystem.base().setting.emulateTouch) {
					if (tooltip != null) {
						this.tooltip.setToolTipComponent(hoverComponent);
						this.tooltip.reshow = 0;
						this.tooltip.initial = 0;
						this.tooltip.showTip();
					}
				}
			}
		} else {
			int touchX = input == null ? SysTouch.x() : this.input.getTouchX();
			int touchY = input == null ? SysTouch.y() : this.input.getTouchY();
			int touchDx = (int) (input == null ? SysTouch.getDX() : this.input.getTouchDX());
			int touchDy = (int) (input == null ? SysTouch.getDY() : this.input.getTouchDY());
			// 获得当前窗体下鼠标坐标
			LComponent comp = null;
			if (SysTouch.getButton() != -1) {
				comp = this.findComponent(touchX, touchY);
			}
			if (comp != null && !comp._touchLocked) {
				if (touchDx != 0 || touchDy != 0 || SysTouch.getDX() != 0 || SysTouch.getDY() != 0) {
					comp.processTouchMoved();
					if (tooltip != null) {
						if (!this.tooltip.dismissing && comp.isPointInUI()) {
							// 刷新提示
							this.tooltip.dismiss = 0;
							this.tooltip.dismissing = true;
						}
					}
				}
				if (this.hoverComponent == null) {
					if (tooltip != null) {
						this.tooltip.setToolTipComponent(comp);
					}
					if (SysTouch.getButton() != -1) {
						comp.processTouchEntered();
					}
				} else if (comp != this.hoverComponent && !this.hoverComponent._touchLocked) {
					if (tooltip != null) {
						this.tooltip.setToolTipComponent(comp);
					}
					this.hoverComponent.processTouchExited();
					if (SysTouch.getButton() != -1) {
						comp.processTouchEntered();
					}
				}
			} else {
				// 如果没有对应的悬停提示数据
				if (tooltip != null) {
					this.tooltip.setToolTipComponent(null);
				}
				if (this.hoverComponent != null && !this.hoverComponent._touchLocked) {
					this.hoverComponent.processTouchExited();
				}
			}
			this.hoverComponent = comp;
		}
	}

	public LToolTip getToolTip() {
		return this.tooltip;
	}

	/**
	 * 设置全局通用的提示组件
	 * 
	 * @param tip
	 */
	public void setToolTip(LToolTip tip) {
		this.contentPane.replace(this.tooltip, tip);
		this.tooltip = tip;
	}

	/**
	 * 鼠标按下事件
	 * 
	 */
	private void processTouchEvent() {
		int pressed = this.input.getTouchPressed(), released = this.input.getTouchReleased();
		if (pressed > SysInput.NO_BUTTON) {
			if (!LSystem.isMobile() && !LSystem.base().setting.emulateTouch) {
				if (tooltip != null) {
					this.tooltip.setToolTipComponent(null);
				}
			}
			if (tooltip != null) {
				this.tooltip.reshow = 0;
				this.tooltip.initial = 0;
			}
			if (!isClicked && this.hoverComponent != null && !this.hoverComponent._touchLocked) {
				this.hoverComponent.processTouchPressed();
			}
			this.clickComponent[0] = this.hoverComponent;
			if (this.hoverComponent != null && !this.hoverComponent._touchLocked && this.hoverComponent.isFocusable()) {
				if ((pressed == SysTouch.TOUCH_DOWN || pressed == SysTouch.TOUCH_UP)
						&& this.hoverComponent != this.selectedComponent) {
					this.selectComponent(this.hoverComponent);
				}
			}
		}
		if (released > SysInput.NO_BUTTON) {
			if (!isClicked && this.hoverComponent != null && !this.hoverComponent._touchLocked) {
				this.hoverComponent.processTouchReleased();
				// 当释放鼠标时，点击事件生效
				if (this.clickComponent[0] == this.hoverComponent && this.hoverComponent != null
						&& !this.hoverComponent._touchLocked) {
					this.hoverComponent.processTouchClicked();
				}
			}
		}
		this.isClicked = false;
	}

	/**
	 * 触发键盘事件
	 * 
	 */
	private void processKeyEvent() {
		if (this.selectedComponent != null && !this.selectedComponent._keyLocked
				&& this.input.getKeyPressed() != SysInput.NO_KEY) {
			this.selectedComponent.keyPressed();
		}
		if (this.selectedComponent != null && !this.selectedComponent._keyLocked
				&& this.input.getKeyReleased() != SysInput.NO_KEY && this.selectedComponent != null) {
			this.selectedComponent.processKeyReleased();
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
		if (this.modal != null && !this.modal.isContainer()) {
			return null;
		}
		// 返回子容器
		LContainer panel = (this.modal == null) ? this.contentPane : ((LContainer) this.modal);
		LComponent comp = panel.findComponent(x, y);
		return comp;
	}

	/**
	 * 清除容器焦点
	 */
	public void clearFocus() {
		this.deselectComponent();
	}

	void deselectComponent() {
		if (this.selectedComponent == null) {
			return;
		}
		this.selectedComponent.setSelected(false);
		this.selectedComponent = null;
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
		if (!comp.isVisible() || !comp.isEnabled() || !comp.isFocusable()) {
			return false;
		}

		// 清除最后部分
		this.deselectComponent();

		// 设定选中状态
		comp.setSelected(true);
		this.selectedComponent = comp;

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
			if (this.hoverComponent == comp) {
				this.processTouchMotionEvent();
			}

			if (this.selectedComponent == comp) {
				this.deselectComponent();
			}

			this.clickComponent[0] = null;

			if (this.modal == comp) {
				this.modal = null;
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

	void clearComponentsStat(LComponent[] components) {
		if (components == null) {
			return;
		}

		boolean checkTouchMotion = false;
		for (int i = 0; i < components.length; i++) {
			LComponent comp = components[i];
			if (this.hoverComponent == comp) {
				checkTouchMotion = true;
			}
			if (this.selectedComponent == comp) {
				this.deselectComponent();
			}
			this.clickComponent[0] = null;
		}

		if (checkTouchMotion) {
			this.processTouchMotionEvent();
		}
	}

	public void validateUI() {
		this.validateContainer(this.contentPane);
	}

	final void validateContainer(LContainer container) {
		LComponent[] components = container._childs;
		int size = container.getComponentCount();
		for (int i = 0; i < size; i++) {
			if (components[i].isContainer()) {
				this.validateContainer((LContainer) components[i]);
			}
		}
	}

	public LComponent[] getComponents() {
		return contentPane.getComponents();
	}

	public LComponent getTopComponent() {
		LComponent[] components = contentPane._childs;
		int size = components.length;
		if (size > 1) {
			return components[1];
		}
		return null;
	}

	public LComponent getBottomComponent() {
		LComponent[] components = contentPane._childs;
		int size = components.length;
		if (size > 0) {
			return components[size - 1];
		}
		return null;
	}

	public LLayer getTopLayer() {
		LComponent[] components = contentPane._childs;
		int size = components.length;
		for (int i = 0; i < size; i++) {
			LComponent comp = components[i];
			if (comp instanceof LLayer) {
				return (LLayer) comp;
			}
		}
		return null;
	}

	public LLayer getBottomLayer() {
		LComponent[] components = contentPane._childs;
		int size = components.length;
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
		if (contentPane != null) {
			controls = contentPane.createUIControls();
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
		if (contentPane != null && contentPane._childs != null) {
			TArray<LComponent> comps = contentPane.findUINames(uiName);
			controls = new UIControls(comps);
		} else {
			controls = new UIControls();
		}
		return controls;
	}

	public UIControls findNotUINamesToUIControls(String... uiName) {
		UIControls controls = null;
		if (contentPane != null && contentPane._childs != null) {
			TArray<LComponent> comps = contentPane.findNotUINames(uiName);
			controls = new UIControls(comps);
		} else {
			controls = new UIControls();
		}
		return controls;
	}

	public UIControls findNamesToUIControls(String... name) {
		UIControls controls = null;
		if (contentPane != null && contentPane._childs != null) {
			TArray<LComponent> comps = contentPane.findNames(name);
			controls = new UIControls(comps);
		} else {
			controls = new UIControls();
		}
		return controls;
	}

	public UIControls findNameContainsToUIControls(String... name) {
		UIControls controls = null;
		if (contentPane != null && contentPane._childs != null) {
			TArray<LComponent> comps = contentPane.findNameContains(name);
			controls = new UIControls(comps);
		} else {
			controls = new UIControls();
		}
		return controls;
	}

	public UIControls findNotNamesToUIControls(String... name) {
		UIControls controls = null;
		if (contentPane != null && contentPane._childs != null) {
			TArray<LComponent> comps = contentPane.findNotNames(name);
			controls = new UIControls(comps);
		} else {
			controls = new UIControls();
		}
		return controls;
	}

	public UIControls findTagsToUIControls(Object... o) {
		UIControls controls = null;
		if (contentPane != null && contentPane._childs != null) {
			TArray<LComponent> comps = contentPane.findTags(o);
			controls = new UIControls(comps);
		} else {
			controls = new UIControls();
		}
		return controls;
	}

	public UIControls findNotTagsToUIControls(Object... o) {
		UIControls controls = null;
		if (contentPane != null && contentPane._childs != null) {
			TArray<LComponent> comps = contentPane.findNotTags(o);
			controls = new UIControls(comps);
		} else {
			controls = new UIControls();
		}
		return controls;
	}

	public float getWidth() {
		return this.contentPane.getWidth();
	}

	public float getHeight() {
		return this.contentPane.getHeight();
	}

	public void setSize(int w, int h) {
		this.contentPane.setSize(w, h);
	}

	public LContainer getContentPane() {
		return this.contentPane;
	}

	public void setContentPane(LContainer pane) {
		pane.setBounds(0, 0, (int) this.getWidth(), (int) this.getHeight());
		this.contentPane = pane;
		this.setDesktop(this.contentPane);
	}

	public LComponent getHoverComponent() {
		return this.hoverComponent;
	}

	public LComponent getSelectedComponent() {
		return this.selectedComponent;
	}

	public LComponent getModal() {
		return this.modal;
	}

	public void setModal(LComponent comp) {
		if (comp != null && !comp.isVisible()) {
			throw new LSysException("Can't set invisible component as modal component!");
		}
		this.modal = comp;
	}

	public boolean contains(LComponent comp) {
		return contentPane.contains(comp);
	}

	public Desktop setSortableChildren(boolean v) {
		contentPane.setSortableChildren(v);
		return this;
	}

	public boolean isSortableChildren() {
		return contentPane.isSortableChildren();
	}

	public LComponent get() {
		return this.contentPane.get();
	}

	public Desktop removeAll() {
		return clear();
	}

	public Desktop clear() {
		if (contentPane != null) {
			contentPane.clear();
		}
		return this;
	}

	public Desktop sortDesktop() {
		if (contentPane != null) {
			contentPane.sortComponents();
		}
		return this;
	}

	public Desktop scrollBy(float x, float y) {
		if (contentPane != null) {
			contentPane.scrollBy(x, y);
		}
		return this;
	}

	public Desktop scrollTo(float x, float y) {
		if (contentPane != null) {
			contentPane.scrollTo(x, y);
		}
		return this;
	}

	public float scrollX() {
		if (contentPane != null) {
			return contentPane.scrollX();
		}
		return 0f;
	}

	public float scrollY() {
		if (contentPane != null) {
			return contentPane.scrollY();
		}
		return 0f;
	}

	public Desktop scrollX(float x) {
		if (contentPane != null) {
			contentPane.scrollX(x);
		}
		return this;
	}

	public Desktop scrollY(float y) {
		if (contentPane != null) {
			contentPane.scrollY(y);
		}
		return this;
	}

	public Desktop freeComponent() {
		this.isClicked = false;
		this.hoverComponent = null;
		this.selectedComponent = null;
		this.clickComponent[0] = null;
		return this;
	}

	public Desktop resize() {
		freeComponent();
		if (_resizeListener != null) {
			_resizeListener.onResize(this);
		}
		if (contentPane != null) {
			contentPane.processResize();
		}
		return this;
	}

	public Desktop forChildren(Callback<LComponent> callback) {
		if (contentPane != null) {
			contentPane.forChildren(callback);
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
		return contentPane.remove(query);
	}

	/**
	 * 查找符合指定条件的组件并返回操作的集合
	 * 
	 * @param query
	 * @return
	 */
	public TArray<LComponent> find(QueryEvent<LComponent> query) {
		return contentPane.find(query);
	}

	/**
	 * 删除指定条件的组件并返回操作的集合
	 * 
	 * @param query
	 * @return
	 */
	public <T extends LComponent> TArray<T> delete(QueryEvent<T> query) {
		return contentPane.delete(query);
	}

	/**
	 * 查找符合指定条件的组件并返回操作的集合
	 * 
	 * @param query
	 * @return
	 */
	public <T extends LComponent> TArray<T> select(QueryEvent<T> query) {
		return contentPane.select(query);
	}

	public Margin margin(boolean vertical, float left, float top, float right, float bottom) {
		float size = vertical ? getHeight() : getWidth();
		if (dclosed) {
			return new Margin(size, vertical);
		}
		return contentPane.margin(vertical, left, top, right, bottom);
	}

	public boolean isVisibleInParents() {
		return isVisibleInParents(selectedComponent);
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

	public Screen getScreen() {
		return input;
	}

	public float getScreenX() {
		return input == null ? 0 : input.getX();
	}

	public float getScreenY() {
		return input == null ? 0 : input.getY();
	}

	public float getX() {
		return contentPane == null ? 0 : contentPane.getX();
	}

	public float getY() {
		return contentPane == null ? 0 : contentPane.getY();
	}

	public float getStageX() {
		return (getX() - getScreenX()) / contentPane.getScaleX();
	}

	public float getStageY() {
		return (getX() - getScreenX()) / contentPane.getScaleY();
	}

	public Screen getInput() {
		return input;
	}

	public String getName() {
		return desktop_name;
	}

	public ResizeListener<Desktop> getResizeListener() {
		return _resizeListener;
	}

	public Desktop setResizeListener(ResizeListener<Desktop> listener) {
		this._resizeListener = listener;
		return this;
	}

	@Override
	public boolean isVisible() {
		return dvisible;
	}

	@Override
	public void setVisible(boolean v) {
		this.dvisible = v;
	}

	@Override
	public String toString() {
		return super.toString() + " " + "[name=" + desktop_name + ", total=" + size() + ", content=" + contentPane
				+ "]";
	}

	public boolean isClosed() {
		return dclosed;
	}

	@Override
	public void close() {
		this.dvisible = false;
		if (contentPane != null) {
			contentPane.close();
		}
		this.dclosed = true;
		this._resizeListener = null;
		LSystem.popDesktopPool(this);
	}

}
