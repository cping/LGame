/**
 * 
 * Copyright 2014
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
 * @version 0.4.1
 * 
 */
package loon.component;

import loon.LObject;
import loon.LSysException;
import loon.LTexture;
import loon.PlayerUtils;
import loon.Screen;
import loon.action.ActionBind;
import loon.action.ActionTween;
import loon.action.collision.CollisionHelper;
import loon.action.map.Field2D;
import loon.canvas.LColor;
import loon.component.layout.Margin;
import loon.events.ClickListener;
import loon.events.EventActionT;
import loon.events.QueryEvent;
import loon.events.Touched;
import loon.font.FontSet;
import loon.font.IFont;
import loon.geom.RectBox;
import loon.geom.Sized;
import loon.geom.Vector2f;
import loon.geom.XY;
import loon.utils.CollectionUtils;
import loon.utils.MathUtils;
import loon.utils.ObjectMap;
import loon.utils.TArray;
import loon.utils.Easing.EasingMode;

/**
 * UI组件的群组化操作控制器，可以同时改变一组组件的参数或动画事件
 */
public class UIControls {

	public static float getChildrenHeight(LContainer c) {
		if (c == null) {
			return 0f;
		}
		float totalHeight = 0;
		LComponent[] list = c._childs;
		for (LComponent element : list) {
			totalHeight += element.getHeight();
		}
		return totalHeight;
	}

	public static float getChildrenWidth(LContainer c) {
		if (c == null) {
			return 0f;
		}
		float totalWidth = 0;
		LComponent[] list = c._childs;
		for (LComponent element : list) {
			totalWidth += element.getWidth();
		}
		return totalWidth;
	}

	public static float getMaxChildHeight(LContainer c) {
		if (c == null) {
			return 0f;
		}
		float maxHeight = 0;
		LComponent[] list = c._childs;
		for (LComponent element : list) {
			maxHeight = MathUtils.max(maxHeight, element.getHeight());
		}
		return maxHeight;
	}

	public static float getMaxChildWidth(LContainer c) {
		if (c == null) {
			return 0f;
		}
		float maxWidth = 0;
		LComponent[] list = c._childs;
		for (LComponent element : list) {
			maxWidth = MathUtils.max(maxWidth, element.getWidth());
		}
		return maxWidth;
	}

	private ObjectMap<ActionBind, ActionTween> tweens = new ObjectMap<ActionBind, ActionTween>(
			CollectionUtils.INITIAL_CAPACITY);

	private Margin _margin;

	private TArray<LComponent> _comps;

	public UIControls(LComponent... comps) {
		this();
		if (comps != null) {
			add(comps);
		}
	}

	public UIControls(TArray<LComponent> comps) {
		this();
		if (comps != null) {
			add(comps);
		}
	}

	public UIControls() {
		this._comps = new TArray<LComponent>();
	}

	public UIControls set(Screen screen) {
		if (screen == null) {
			return this;
		}
		return set(screen.getDesktop());
	}

	public UIControls set(Desktop desktop) {
		if (desktop == null) {
			return this;
		}
		synchronized (desktop) {
			set(desktop.getComponentsArray());
		}
		return this;
	}

	public UIControls set(TArray<LComponent> comps) {
		if (comps == null || comps.size == 0 || comps.equals(this._comps)) {
			return this;
		}
		synchronized (UIControls.class) {
			clear();
			this._comps = comps;
		}
		return this;
	}

	public UIControls clear() {
		if (this._comps.size == 0) {
			return this;
		}
		this._comps.clear();
		return this;
	}

	public UIControls clear(Screen screen) {
		if (screen == null) {
			return this;
		}
		return clear(screen.getDesktop());
	}

	public UIControls clear(Desktop desktop) {
		if (desktop == null) {
			return this;
		}
		synchronized (desktop) {
			desktop.clear(this._comps);
			clear();
		}
		return this;
	}

	public LComponent random() {
		if (_comps.size == 0) {
			return null;
		}
		return _comps.get(MathUtils.random(_comps.size - 1));
	}

	public int size() {
		return _comps.size;
	}

	public boolean isEmpty() {
		return _comps.isEmpty();
	}

	public boolean isNotEmpty() {
		return _comps.isNotEmpty();
	}

	public int indexOf(QueryEvent<LComponent> q) {
		if (q == null) {
			return -1;
		}
		int i = 0;
		for (LComponent s : _comps) {
			if (q.hit(s)) {
				return i;
			}
			i++;
		}
		return -1;
	}

	public int indexOf(LComponent v) {
		if (v == null) {
			return -1;
		}
		int i = 0;
		for (LComponent s : _comps) {
			if (s != null && (s == v || s.equals(v))) {
				return i;
			}
			i++;
		}
		return -1;
	}

	public LComponent find(QueryEvent<LComponent> q) {
		for (LComponent s : _comps) {
			if (q.hit(s)) {
				return s;
			}
		}
		return null;
	}

	public UIControls fill(Screen screen) {
		if (screen == null) {
			return this;
		}
		return set(screen.getDesktop());
	}

	public UIControls fill(Desktop desktop) {
		if (desktop == null) {
			return this;
		}
		synchronized (desktop) {
			set(desktop.getComponentsArray());
		}
		return this;
	}

	public UIControls fill(TArray<LComponent> comps) {
		if (comps == null || comps.size == 0 || comps.equals(this._comps)) {
			return this;
		}
		this._comps.fill(comps);
		return this;
	}

	public TArray<LComponent> list() {
		return this._comps;
	}

	public TArray<LComponent> intersects(float x, float y) {
		TArray<LComponent> comps = new TArray<LComponent>();
		for (LComponent child : this._comps) {
			if (child != null) {
				if (child.getRectBox().inPoint(x, y)) {
					comps.add(child);
				}
			}
		}
		return comps;
	}

	public LComponent intersectsOnly(float x, float y) {
		for (LComponent child : this._comps) {
			if (child != null) {
				if (child.getRectBox().inPoint(x, y)) {
					return child;
				}
			}
		}
		return null;
	}

	public TArray<LComponent> intersects(RectBox rect) {
		TArray<LComponent> comps = new TArray<LComponent>();
		for (LComponent child : this._comps) {
			if (child != null) {
				if (rect.intersects(child.getX(), child.getY(), child.getWidth(), child.getHeight())) {
					comps.add(child);
				}
			}
		}
		return comps;
	}

	public TArray<LComponent> intersects(float x, float y, float width, float height) {
		TArray<LComponent> comps = new TArray<LComponent>();
		for (LComponent child : this._comps) {
			if (child != null) {
				if (CollisionHelper.intersects(x, y, width, height, child.getX(), child.getY(), child.getWidth(),
						child.getHeight())) {
					comps.add(child);
				}
			}
		}
		return comps;
	}

	public TArray<LComponent> contains(RectBox rect) {
		TArray<LComponent> comps = new TArray<LComponent>();
		for (LComponent child : this._comps) {
			if (child != null) {
				if (rect.contains(child.getX(), child.getY(), child.getWidth(), child.getHeight())) {
					comps.add(child);
				}
			}
		}
		return comps;
	}

	public TArray<LComponent> contains(float x, float y, float width, float height) {
		TArray<LComponent> comps = new TArray<LComponent>();
		for (LComponent child : this._comps) {
			if (child != null) {
				if (CollisionHelper.contains(x, y, width, height, child.getX(), child.getY(), child.getWidth(),
						child.getHeight())) {
					comps.add(child);
				}
			}
		}
		return comps;
	}

	public boolean allIn(Screen screen) {
		return allIn(screen, true);
	}

	public boolean allIn(Screen screen, boolean canView) {
		return getCountIn(screen, canView) >= _comps.size;
	}

	public boolean allNotIn(Screen screen) {
		return allNotIn(screen, true);
	}

	public boolean allNotIn(Screen screen, boolean canView) {
		return getCountIn(screen, canView) == 0;
	}

	public int getCountIn(Screen screen) {
		return getCountIn(screen, true);
	}

	public int getCountIn(Screen screen, boolean canView) {
		int count = 0;
		for (int i = _comps.size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null && (screen.intersects(comp, canView) || screen.contains(comp, canView))) {
				count++;
			}
		}
		return count;
	}

	public UIControls add(LComponent comp) {
		if (comp == null) {
			throw new LSysException("LComponent cannot be null.");
		}
		_comps.add(comp);
		return this;
	}

	public UIControls add(LComponent... comps) {
		if (comps == null) {
			throw new LSysException("LComponents cannot be null.");
		}
		for (int i = 0, n = comps.length; i < n; i++) {
			LComponent comp = comps[i];
			if (comp != null) {
				add(comp);
			}
		}
		return this;
	}

	public UIControls add(TArray<LComponent> comps) {
		if (comps == null) {
			throw new LSysException("LComponents cannot be null.");
		}
		_comps.addAll(comps);
		return this;
	}

	public UIControls remove(Screen screen, LComponent comp) {
		if (screen == null) {
			throw new LSysException("Screen cannot be null.");
		}
		return remove(screen.getDesktop(), comp);
	}

	public UIControls remove(Desktop desktop, LComponent comp) {
		if (desktop == null) {
			throw new LSysException("Desktop cannot be null.");
		}
		desktop.remove(comp);
		return remove(comp);
	}

	public UIControls remove(LComponent comp) {
		if (comp == null) {
			throw new LSysException("LComponent cannot be null.");
		}
		_comps.remove(comp);
		return this;
	}

	public UIControls remove(LComponent... comps) {
		if (comps == null) {
			throw new LSysException("LComponents cannot be null.");
		}
		for (int i = comps.length - 1; i > -1; --i) {
			LComponent comp = comps[i];
			if (comp != null) {
				remove(comp);
			}
		}
		return this;
	}

	public UIControls removeAll() {
		if (this._comps.size == 0) {
			return this;
		}
		_comps.clear();
		return this;
	}

	public UIControls setSize(int w, int h) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.setSize(w, h);
			}
		}
		return this;
	}

	public UIControls setFocusable(boolean focus) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.setFocusable(focus);
			}
		}
		return this;
	}

	public UIControls setEnabled(boolean e) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.setEnabled(e);
			}
		}
		return this;
	}

	/**
	 * 附带一提，此处Set大写是为了显示作用比较特殊，以及建议使用一个ClickListener，监听多个组件，所以"S"
	 * 
	 * @param click
	 */
	public UIControls SetClick(ClickListener click) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.SetClick(click);
			}
		}
		return this;
	}

	public UIControls S(ClickListener click) {
		return SetClick(click);
	}

	public UIControls alpha(float a) {
		return setAlpha(a);
	}

	public UIControls setAlpha(float a) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.setAlpha(a);
			}
		}
		return this;
	}

	public UIControls setTouchLocked(boolean locked) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.setTouchLocked(locked);
			}
		}
		return this;
	}

	public UIControls setKeyLocked(boolean locked) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.setKeyLocked(locked);
			}
		}
		return this;
	}

	public UIControls setTouch(ClickListener listener) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.SetClick(listener);
			}
		}
		return this;
	}

	public UIControls addTouch(ClickListener listener) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.addClickListener(listener);
			}
		}
		return this;
	}

	public UIControls up(Touched touched) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.up(touched);
			}
		}
		return this;
	}

	public UIControls down(Touched touched) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.down(touched);
			}
		}
		return this;
	}

	public UIControls all(Touched touched) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.all(touched);
			}
		}
		return this;
	}

	public UIControls downClick() {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.processTouchPressed();
			}
		}
		return this;
	}

	public UIControls upClick() {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.processTouchReleased();
			}
		}
		return this;
	}

	public UIControls dragClick() {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.processTouchDragged();
			}
		}
		return this;
	}

	public UIControls allClick() {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.processTouchClicked();
			}
		}
		return this;
	}

	public UIControls keyDown() {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.processKeyPressed();
			}
		}
		return this;
	}

	public UIControls keyUp() {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.processKeyReleased();
			}
		}
		return this;
	}

	public UIControls in() {
		return in(30);
	}

	public UIControls in(float speed) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.in(speed);
			}
		}
		return this;
	}

	public UIControls out() {
		return out(30);
	}

	public UIControls out(float speed) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.out(speed);
			}
		}
		return this;
	}

	public boolean isTransparent() {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				return (comp.getAlpha() != 1f);
			}
		}
		return false;
	}

	public UIControls setScale(float s) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.setScale(s);
			}
		}
		return this;
	}

	public UIControls setScale(float sx, float sy) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.setScale(sx, sy);
			}
		}
		return this;
	}

	public UIControls setX(float x) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.setX(x);
			}
		}
		return this;
	}

	public UIControls setY(float y) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.setY(y);
			}
		}
		return this;
	}

	public UIControls location(float x, float y) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.setLocation(x, y);
			}
		}
		return this;
	}

	public UIControls offset(float x, float y) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.setLocation(comp.getX() + x, comp.getY() + y);
			}
		}
		return this;
	}

	public UIControls setWidth(int w) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.setWidth(w);
			}
		}
		return this;
	}

	public UIControls setHeight(int h) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.setHeight(h);
			}
		}
		return this;
	}

	public UIControls setColor(LColor c) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.setColor(c);
			}
		}
		return this;
	}

	public UIControls setFont(IFont font) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null && (comp instanceof FontSet<?>)) {
				((FontSet<?>) comp).setFont(font);
			}
		}
		return this;
	}

	public UIControls setRotation(float r) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.setRotation(r);
			}
		}
		return this;
	}

	public UIControls setLocation(float dx, float dy) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.setLocation(dx, dy);
			}
		}
		return this;
	}

	public UIControls setLayer(int z) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.setLayer(z);
			}
		}
		return this;
	}

	public UIControls setBackground(LColor color) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.setBackground(color);
			}
		}
		return this;
	}

	public UIControls setBackground(LTexture tex) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.setBackground(tex);
			}
		}
		return this;
	}

	public UIControls setBackground(String imgPath) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.setBackground(imgPath);
			}
		}
		return this;
	}

	public UIControls setVisible(boolean v) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.setVisible(v);
			}
		}
		return this;
	}

	public UIControls setTicked(boolean c) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			Object o = _comps.get(i);
			if (o != null && o instanceof LCheckBox) {
				LCheckBox box = (LCheckBox) o;
				box.setTicked(c);
			}
		}
		return this;
	}

	public UIControls setPercentage(float p) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			Object o = _comps.get(i);
			if (o != null) {
				if (o instanceof LProgress) {
					LProgress progress = (LProgress) o;
					progress.setPercentage(p);
				} else if (o instanceof LSlider) {
					LSlider progress = (LSlider) o;
					progress.setPercentage(p);
				}
			}
		}
		return this;
	}

	public UIControls transferFocus() {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.transferFocus();
			}
		}
		return this;
	}

	public UIControls transferFocusBackward() {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.transferFocusBackward();
			}
		}
		return this;
	}

	public UIControls requestFocus() {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.requestFocus();
			}
		}
		return this;
	}

	public UIControls callEvents(EventActionT<LComponent> e) {
		if (e == null) {
			return this;
		}
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null && e != null) {
				e.update(comp);
			}
		}
		return this;
	}

	public UIControls clearTweens() {
		for (ActionTween tween : tweens.values()) {
			tween.free();
		}
		tweens.clear();
		return this;
	}

	public UIControls startTweens() {
		for (ActionTween tween : tweens.values()) {
			tween.start();
		}
		return this;
	}

	public UIControls killTweens() {
		for (ActionTween tween : tweens.values()) {
			tween.kill();
		}
		return this;
	}

	public UIControls pauseTweens() {
		for (ActionTween tween : tweens.values()) {
			tween.pause();
		}
		return this;
	}

	public UIControls resumeTweens() {
		for (ActionTween tween : tweens.values()) {
			tween.resume();
		}
		return this;
	}

	public UIControls fadeOut(float speed) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				ActionTween tween = tweens.get(comp);
				if (comp.getAlpha() <= 0) {
					if (tween == null) {
						tween = PlayerUtils.set(comp).fadeIn(speed);
					} else {
						tween.fadeIn(speed);
					}
				} else {
					if (tween == null) {
						tween = PlayerUtils.set(comp).fadeOut(speed);
					} else {
						tween.fadeOut(speed);
					}
					if (!tweens.containsKey(comp)) {
						tweens.put(comp, tween);
					}
				}
			}
		}
		return this;
	}

	public UIControls fadeIn(float speed) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				ActionTween tween = tweens.get(comp);
				if (comp.getAlpha() >= 255) {
					if (tween == null) {
						tween = PlayerUtils.set(comp).fadeOut(speed);
					} else {
						tween.fadeOut(speed);
					}
				} else {
					if (tween == null) {
						tween = PlayerUtils.set(comp).fadeIn(speed);
					} else {
						tween.fadeIn(speed);
					}
				}
				if (!tweens.containsKey(comp)) {
					tweens.put(comp, tween);
				}
			}
		}
		return this;
	}

	public UIControls moveBy(float endX, float endY, int speed) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				ActionTween tween = tweens.get(comp);
				if (tween == null) {
					tween = PlayerUtils.set(comp).moveBy(endX, endY, speed);
				} else {
					tween.moveBy(endX, endY, speed);
				}
				if (!tweens.containsKey(comp)) {
					tweens.put(comp, tween);
				}
			}
		}
		return this;
	}

	public UIControls moveBy(float endX, float endY) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				ActionTween tween = tweens.get(comp);
				if (tween == null) {
					tween = PlayerUtils.set(comp).moveBy(endX, endY);
				} else {
					tween.moveBy(endX, endY);
				}
				if (!tweens.containsKey(comp)) {
					tweens.put(comp, tween);
				}
			}
		}
		return this;
	}

	public UIControls moveTo(float endX, float endY, int speed) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				ActionTween tween = tweens.get(comp);
				if (tween == null) {
					tween = PlayerUtils.set(comp).moveTo(endX, endY, speed);
				} else {
					tween.moveTo(endX, endY, speed);
				}
				if (!tweens.containsKey(comp)) {
					tweens.put(comp, tween);
				}

			}
		}
		return this;
	}

	public UIControls moveTo(float endX, float endY, boolean flag, int speed) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				ActionTween tween = tweens.get(comp);
				if (tween == null) {
					tween = PlayerUtils.set(comp).moveTo(endX, endY, flag, speed);
				} else {
					tween.moveTo(endX, endY, flag, speed);
				}
				if (!tweens.containsKey(comp)) {
					tweens.put(comp, tween);
				}

			}
		}
		return this;
	}

	public UIControls moveTo(Field2D map, float endX, float endY, boolean flag, int speed) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				ActionTween tween = tweens.get(comp);
				if (tween == null) {
					tween = PlayerUtils.set(comp).moveTo(map, endX, endY, flag, speed);
				} else {
					tween.moveTo(map, endX, endY, flag, speed);
				}
				if (!tweens.containsKey(comp)) {
					tweens.put(comp, tween);
				}

			}
		}
		return this;
	}

	public UIControls flipXTo() {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				ActionTween tween = tweens.get(comp);
				if (tween == null) {
					tween = PlayerUtils.set(comp).flipX();
				} else {
					tween.flipX();
				}
				if (!tweens.containsKey(comp)) {
					tweens.put(comp, tween);
				}

			}
		}
		return this;
	}

	public UIControls flipYTo() {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				ActionTween tween = tweens.get(comp);
				if (tween == null) {
					tween = PlayerUtils.set(comp).flipY();
				} else {
					tween.flipY();
				}
				if (!tweens.containsKey(comp)) {
					tweens.put(comp, tween);
				}

			}
		}
		return this;
	}

	/**
	 * 将控制器中的组件随机放置于Screen显示范围外的左侧
	 * 
	 * @param screen
	 * @return
	 */
	public UIControls outsideLeftRandOn(Screen screen) {
		if (screen == null) {
			return this;
		}
		int count = 0;
		final int size = _comps.size;
		final int maxCount = size * size * 4;
		for (int i = 0, n = size; i < n; i++) {
			LComponent comp = _comps.get(i);
			if (comp != null && (comp instanceof LObject<?>)) {
				LObject<?> o = ((LObject<?>) comp);
				screen.outsideLeftRandOn(o, MathUtils.random(0f, screen.getWidth()), 0f);
				for (int j = size - 1; j > -1; --j) {
					LComponent dst = _comps.get(j);
					if (dst != null && dst != comp) {
						RectBox rect = comp.getCollisionBox();
						if (rect.collided(dst.getCollisionBox()) || rect.contains(dst.getCollisionBox())) {
							if (count > maxCount) {
								comp.setLocation(comp.getX() - comp.getWidth() * 2f, comp.getY());
								count = 0;
							} else {
								if (i > 0) {
									i--;
								}
								count++;
								continue;
							}
						}
					}
				}
			}
		}
		return this;
	}

	/**
	 * 将控制器中的组件随机放置于Screen显示范围外的右侧
	 * 
	 * @param screen
	 * @return
	 */
	public UIControls outsideRightRandOn(Screen screen) {
		if (screen == null) {
			return this;
		}
		int count = 0;
		final int size = _comps.size;
		final int maxCount = size * size * 4;
		for (int i = 0, n = size; i < n; i++) {
			LComponent comp = _comps.get(i);
			if (comp != null && (comp instanceof LObject<?>)) {
				LObject<?> o = ((LObject<?>) comp);
				screen.outsideRightRandOn(o, MathUtils.random(0f, screen.getWidth()), 0f);
				for (int j = size - 1; j > -1; --j) {
					LComponent dst = _comps.get(j);
					if (dst != null && dst != comp) {
						RectBox rect = comp.getCollisionBox();
						if (rect.collided(dst.getCollisionBox()) || rect.contains(dst.getCollisionBox())) {
							if (count > maxCount) {
								comp.setLocation(comp.getX() + comp.getWidth() * 2f, comp.getY());
								count = 0;
							} else {
								if (i > 0) {
									i--;
								}
								count++;
								continue;
							}
						}
					}
				}
			}
		}
		return this;
	}

	/**
	 * 将控制器中的组件随机放置于Screen显示范围外的上侧
	 * 
	 * @param screen
	 * @return
	 */
	public UIControls outsideTopRandOn(Screen screen) {
		if (screen == null) {
			return this;
		}
		int count = 0;
		final int size = _comps.size;
		final int maxCount = size * size * 4;
		for (int i = 0, n = size; i < n; i++) {
			LComponent comp = _comps.get(i);
			if (comp != null && (comp instanceof LObject<?>)) {
				LObject<?> o = ((LObject<?>) comp);
				screen.outsideTopRandOn(o, 0f, MathUtils.random(0f, screen.getHeight()));
				for (int j = size - 1; j > -1; --j) {
					LComponent dst = _comps.get(j);
					if (dst != null && dst != comp) {
						RectBox rect = comp.getCollisionBox();
						if (rect.collided(dst.getCollisionBox()) || rect.contains(dst.getCollisionBox())) {
							if (count > maxCount) {
								comp.setLocation(comp.getX(), comp.getY() - comp.getHeight() * 2f);
								count = 0;
							} else {
								if (i > 0) {
									i--;
								}
								count++;
								continue;
							}
						}
					}
				}
			}
		}
		return this;
	}

	/**
	 * 将控制器中的组件随机放置于Screen显示范围外的下侧
	 * 
	 * @param screen
	 * @return
	 */
	public UIControls outsideBottomRandOn(Screen screen) {
		if (screen == null) {
			return this;
		}
		int count = 0;
		final int size = _comps.size;
		final int maxCount = size * size * 4;
		for (int i = 0, n = size; i < n; i++) {
			LComponent comp = _comps.get(i);
			if (comp != null && (comp instanceof LObject<?>)) {
				LObject<?> o = ((LObject<?>) comp);
				screen.outsideBottomRandOn(o, 0f, MathUtils.random(0f, screen.getHeight()));
				for (int j = size - 1; j > -1; --j) {
					LComponent dst = _comps.get(j);
					if (dst != null && dst != comp) {
						RectBox rect = comp.getCollisionBox();
						if (rect.collided(dst.getCollisionBox()) || rect.contains(dst.getCollisionBox())) {
							if (count > maxCount) {
								comp.setLocation(comp.getX(), comp.getY() + comp.getHeight() * 2f);
								count = 0;
							} else {
								if (i > 0) {
									i--;
								}
								count++;
								continue;
							}
						}
					}
				}
			}
		}
		return this;
	}

	public UIControls move(float x, float y) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null && (comp instanceof LObject<?>)) {
				LObject<?> o = ((LObject<?>) comp);
				o.move(x, y);
			}
		}
		return this;
	}

	public UIControls move_left(float v) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null && (comp instanceof LObject<?>)) {
				LObject<?> o = ((LObject<?>) comp);
				o.move_left(v);
			}
		}
		return this;
	}

	public UIControls move_right(float v) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null && (comp instanceof LObject<?>)) {
				LObject<?> o = ((LObject<?>) comp);
				o.move_right(v);
			}
		}
		return this;
	}

	public UIControls move_up(float v) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null && (comp instanceof LObject<?>)) {
				LObject<?> o = ((LObject<?>) comp);
				o.move_up(v);
			}
		}
		return this;
	}

	public UIControls move_down(float v) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null && (comp instanceof LObject<?>)) {
				LObject<?> o = ((LObject<?>) comp);
				o.move_down(v);
			}
		}
		return this;
	}

	public UIControls move_45D_left(float v) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null && (comp instanceof LObject<?>)) {
				LObject<?> o = ((LObject<?>) comp);
				o.move_45D_left(v);
			}
		}
		return this;
	}

	public UIControls move_45D_right(float v) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null && (comp instanceof LObject<?>)) {
				LObject<?> o = ((LObject<?>) comp);
				o.move_45D_right(v);
			}
		}
		return this;
	}

	public UIControls move_45D_up(float v) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null && (comp instanceof LObject<?>)) {
				LObject<?> o = ((LObject<?>) comp);
				o.move_45D_up(v);
			}
		}
		return this;
	}

	public UIControls move_45D_down(float v) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null && (comp instanceof LObject<?>)) {
				LObject<?> o = ((LObject<?>) comp);
				o.move_45D_down(v);
			}
		}
		return this;
	}

	public UIControls delay(float d) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				ActionTween tween = tweens.get(comp);
				if (tween == null) {
					tween = PlayerUtils.set(comp).delay(d);
				} else {
					tween.delay(d);
				}
				if (!tweens.containsKey(comp)) {
					tweens.put(comp, tween);
				}

			}
		}
		return this;
	}

	public UIControls rotateTo(float angle) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				ActionTween tween = tweens.get(comp);
				if (tween == null) {
					tween = PlayerUtils.set(comp).rotateTo(angle);
				} else {
					tween.rotateTo(angle);
				}
				if (!tweens.containsKey(comp)) {
					tweens.put(comp, tween);
				}

			}
		}
		return this;
	}

	public UIControls rotateTo(float angle, float speed) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				ActionTween tween = tweens.get(comp);
				if (tween == null) {
					tween = PlayerUtils.set(comp).rotateTo(angle, speed);
				} else {
					tween.rotateTo(angle, speed);
				}
				if (!tweens.containsKey(comp)) {
					tweens.put(comp, tween);
				}

			}
		}
		return this;
	}

	public UIControls scaleTo(float sx, float sy) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				ActionTween tween = tweens.get(comp);
				if (tween == null) {
					tween = PlayerUtils.set(comp).scaleTo(sx, sy);
				} else {
					tween.scaleTo(sx, sy);
				}
				if (!tweens.containsKey(comp)) {
					tweens.put(comp, tween);
				}

			}
		}
		return this;
	}

	public UIControls scaleTo(float sx, float sy, float speed) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				ActionTween tween = tweens.get(comp);
				if (tween == null) {
					tween = PlayerUtils.set(comp).scaleTo(sx, sy, speed);
				} else {
					tween.scaleTo(sx, sy, speed);
				}
				if (!tweens.containsKey(comp)) {
					tweens.put(comp, tween);
				}

			}
		}
		return this;
	}

	public UIControls showTo(boolean v) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				ActionTween tween = tweens.get(comp);
				if (tween == null) {
					tween = PlayerUtils.set(comp).showTo(v);
				} else {
					tween.showTo(v);
				}
				if (!tweens.containsKey(comp)) {
					tweens.put(comp, tween);
				}

			}
		}
		return this;
	}

	public UIControls colorTo(LColor end) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				ActionTween tween = tweens.get(comp);
				if (tween == null) {
					tween = PlayerUtils.set(comp).colorTo(end);
				} else {
					tween.colorTo(end);
				}
				if (!tweens.containsKey(comp)) {
					tweens.put(comp, tween);
				}

			}
		}
		return this;
	}

	public UIControls shakeTo(float shakeX, float shakeY) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				ActionTween tween = tweens.get(comp);
				if (tween == null) {
					tween = PlayerUtils.set(comp).shakeTo(shakeX, shakeY);
				} else {
					tween.shakeTo(shakeX, shakeY);
				}
				if (!tweens.containsKey(comp)) {
					tweens.put(comp, tween);
				}

			}
		}
		return this;
	}

	public UIControls followTo(ActionBind bind, float follow, float speed) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null && (comp instanceof ActionBind)) {
				ActionTween tween = tweens.get(comp);
				if (tween == null) {
					tween = PlayerUtils.set(comp).followTo(bind, follow, speed);
				} else {
					tween.followTo(bind, follow, speed);
				}
				if (!tweens.containsKey(comp)) {
					tweens.put(comp, tween);
				}

			}
		}
		return this;
	}

	public UIControls flashTo(float duration) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				ActionTween tween = tweens.get(comp);
				if (tween == null) {
					tween = PlayerUtils.set(comp).flashTo(duration);
				} else {
					tween.flashTo(duration);
				}
				if (!tweens.containsKey(comp)) {
					tweens.put(comp, tween);
				}

			}
		}
		return this;
	}

	public UIControls transferTo(float startPos, float endPos, float duration, EasingMode mode, boolean controlX,
			boolean controlY) {
		final int size = this._comps.size;
		for (int i = size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				ActionTween tween = tweens.get(comp);
				if (tween == null) {
					tween = PlayerUtils.set(comp).transferTo(startPos, endPos, duration, mode, controlX, controlY);
				} else {
					tween.transferTo(startPos, endPos, duration, mode, controlX, controlY);
				}
				if (!tweens.containsKey(comp)) {
					tweens.put(comp, tween);
				}

			}
		}
		return this;
	}

	public boolean isTweenFinished() {
		int size = 0;
		for (ActionTween tween : tweens.values()) {
			if (tween.isFinished()) {
				size++;
			}
		}
		return size == tweens.size;
	}

	public Margin margin(float size, boolean vertical, float left, float top, float right, float bottom) {
		if (_margin == null) {
			_margin = new Margin(size, vertical);
		} else {
			_margin.setSize(size);
			_margin.setVertical(vertical);
		}
		_margin.setMargin(left, top, right, bottom);
		_margin.clear();
		for (int i = _comps.size - 1; i > -1; --i) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				_margin.addChild(comp);
			}
		}
		return _margin;
	}

	public Vector2f getMoveTarget(TArray<XY> targets, LComponent comp, Sized mapSize, float scrollWidth,
			float scrollHeight, float orthogonalWidth, float orthogonalHeight, float paddingX, float paddingY) {
		if (comp == null) {
			return null;
		}
		LComponent parentSprite = comp.getParent() == null ? comp : comp.getParent();

		Vector2f parentLocal = Vector2f.at(parentSprite.getX() + parentSprite.getOffsetX(),
				parentSprite.getY() + parentSprite.getOffsetY());

		return getMoveTarget(targets, comp, parentLocal, mapSize, scrollWidth, scrollHeight, orthogonalWidth,
				orthogonalHeight, paddingX, paddingY);
	}

	public Vector2f getMoveTarget(XY pos, LComponent comp, Sized mapSize, float scrollWidth, float scrollHeight,
			float orthogonalWidth, float orthogonalHeight) {
		if (comp == null) {
			return null;
		}
		LComponent parentSprite = comp.getParent() == null ? comp : comp.getParent();

		Vector2f parentLocal = Vector2f.at(parentSprite.getX() + parentSprite.getOffsetX(),
				parentSprite.getY() + parentSprite.getOffsetY());

		return getMoveTarget(pos, comp, parentLocal, mapSize, scrollWidth, scrollHeight, orthogonalWidth,
				orthogonalHeight);
	}

	public Vector2f getMoveTarget(XY pos, LComponent comp, Vector2f parentLocal, Sized mapSize, float scrollWidth,
			float scrollHeight, float orthogonalWidth, float orthogonalHeight) {
		if (comp == null) {
			return null;
		}
		return getMoveTarget(new TArray<XY>(pos), comp, parentLocal, mapSize, scrollWidth, scrollHeight,
				orthogonalWidth, orthogonalHeight, 0f, 0f);
	}

	public Vector2f getMoveTarget(TArray<XY> targets, LComponent comp, Vector2f parentLocal, Sized mapSize,
			float scrollWidth, float scrollHeight, float orthogonalWidth, float orthogonalHeight) {
		if (comp == null) {
			return null;
		}
		return getMoveTarget(targets, comp, parentLocal, mapSize, scrollWidth, scrollHeight, orthogonalWidth,
				orthogonalHeight, 0f, 0f);
	}

	public Vector2f getMoveTarget(TArray<XY> targets, LComponent comp, Vector2f parentLocal, Sized mapSize,
			float scrollWidth, float scrollHeight, float orthogonalWidth, float orthogonalHeight, float paddingX,
			float paddingY) {

		if (comp == null) {
			return null;
		}

		Vector2f center = Vector2f.at(comp.getX() + comp.getOffsetX(), comp.getY() + comp.getOffsetY());

		if (targets.size > 0) {

			XY first = targets.first();

			float minX = first.getX();
			float maxX = first.getX();

			float minY = first.getY();
			float maxY = first.getY();

			if (mapSize != null) {
				minX = MathUtils.max(minX, mapSize.left());
				maxX = MathUtils.min(maxX, mapSize.left() + mapSize.right());

				minY = MathUtils.max(minY, mapSize.top() - mapSize.bottom());
				maxY = MathUtils.min(maxY, mapSize.top());
			}

			for (int i = 1; i < targets.size; i++) {

				XY pos = targets.get(i);
				Vector2f position = Vector2f.at(pos.getX(), pos.getY());

				if (mapSize != null) {
					position.x = MathUtils.max(position.x, mapSize.left());
					position.x = MathUtils.min(position.x, mapSize.left() + mapSize.right());

					position.y = MathUtils.max(position.y, mapSize.top() - mapSize.bottom());
					position.y = MathUtils.min(position.y, mapSize.top());
				}

				if (position.x < minX) {
					minX = position.x;
				}
				if (position.x > maxX) {
					maxX = position.x;
				}

				if (position.y < minY) {
					minY = position.y;
				}
				if (position.y > maxY) {
					maxY = position.y;
				}
			}

			center.x = (minX + maxX) / 2f;
			center.y = (minY + maxY) / 2f;
		}

		Vector2f target = Vector2f.ZERO();

		float widthHalf = scrollWidth / 2f;
		float heightHalf = scrollHeight / 2f;

		float left = parentLocal.x - widthHalf;
		float right = parentLocal.x + widthHalf;
		float top = parentLocal.y + heightHalf;
		float bottom = parentLocal.y - heightHalf;

		if (center.x < left) {
			target.x = center.x + widthHalf;
		} else if (center.x > right) {
			target.x = center.x - widthHalf;
		} else {
			target.x = parentLocal.x;
		}

		if (center.y < bottom) {
			target.y = center.y + heightHalf;
		} else if (center.y > top) {
			target.y = center.y - heightHalf;
		} else {
			target.y = parentLocal.y;
		}

		if (mapSize != null) {

			float effectivePaddingX = paddingX;
			float effectivePaddingY = paddingY;

			float mapLeft = mapSize.left() + effectivePaddingX;
			float mapRight = mapSize.left() + mapSize.right() - effectivePaddingX;

			float mapBottom = mapSize.top() - mapSize.bottom() + effectivePaddingY;
			float mapTop = mapSize.top() - effectivePaddingY;

			if (orthogonalWidth > mapSize.right()) {
				target.x = mapLeft + mapSize.right() / 2;
			} else {
				target.x = MathUtils.max(target.x, mapLeft + orthogonalWidth / 2);
				target.x = MathUtils.min(target.x, mapRight - orthogonalWidth / 2);
			}

			if (orthogonalHeight > mapSize.bottom()) {
				target.y = mapBottom + mapSize.bottom() / 2;
			} else {
				target.y = MathUtils.max(target.y, mapBottom + orthogonalHeight / 2);
				target.y = MathUtils.min(target.y, mapTop - orthogonalHeight / 2);
			}
		}
		return target;
	}

	public TArray<Vector2f> moveTargets(XY pos, Vector2f parentLocal, Sized mapSize, float scrollWidth,
			float scrollHeight, float orthogonalWidth, float orthogonalHeight) {
		return moveTargets(pos, parentLocal, mapSize, scrollWidth, scrollHeight, orthogonalWidth, orthogonalHeight, 0f,
				0f);
	}

	public TArray<Vector2f> moveTargets(XY pos, Vector2f parentLocal, Sized mapSize, float scrollWidth,
			float scrollHeight, float orthogonalWidth, float orthogonalHeight, float paddingX, float paddingY) {
		return moveTargets(new TArray<XY>(pos), parentLocal, mapSize, scrollWidth, scrollHeight, orthogonalWidth,
				orthogonalHeight, paddingX, paddingY);
	}

	public TArray<Vector2f> moveTargets(TArray<XY> targets, Vector2f parentLocal, Sized mapSize, float scrollWidth,
			float scrollHeight, float orthogonalWidth, float orthogonalHeight, float paddingX, float paddingY) {
		TArray<Vector2f> moveList = new TArray<Vector2f>();
		for (int i = 0, n = _comps.size; i < n; i++) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				moveList.add(getMoveTarget(targets, comp, parentLocal, mapSize, scrollWidth, scrollHeight,
						orthogonalWidth, orthogonalHeight, paddingX, paddingY));
			}
		}
		return moveList;
	}
}
