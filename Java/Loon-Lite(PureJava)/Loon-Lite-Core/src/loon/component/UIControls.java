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
 *          新增类，用以同时处理多个组件对象到同一状态
 */
package loon.component;

import loon.LSysException;
import loon.LTexture;
import loon.PlayerUtils;
import loon.action.ActionBind;
import loon.action.ActionTween;
import loon.action.map.Field2D;
import loon.canvas.LColor;
import loon.event.ClickListener;
import loon.event.Touched;
import loon.font.FontSet;
import loon.font.IFont;
import loon.utils.CollectionUtils;
import loon.utils.MathUtils;
import loon.utils.ObjectMap;
import loon.utils.TArray;
import loon.utils.Easing.EasingMode;

/**
 * 组件的群组化操作控制器，可以同时改变一组组件的参数或动画事件
 */
public class UIControls {

	public static float getChildrenHeight(LContainer c) {
		float totalHeight = 0;
		LComponent[] list = c._childs;
		for (int i = 0; i < list.length; i++) {
			totalHeight += list[i].getHeight();
		}
		return totalHeight;
	}

	public static float getChildrenWidth(LContainer c) {
		float totalWidth = 0;
		LComponent[] list = c._childs;
		for (int i = 0; i < list.length; i++) {
			totalWidth += list[i].getWidth();
		}
		return totalWidth;
	}

	public static float getMaxChildHeight(LContainer c) {
		int maxHeight = 0;
		LComponent[] list = c._childs;
		for (int i = 0; i < list.length; i++) {
			maxHeight = MathUtils.max(maxHeight, (int) list[i].getHeight());
		}
		return maxHeight;
	}

	public static int getMaxChildWidth(LContainer c) {
		int maxWidth = 0;
		LComponent[] list = c._childs;
		for (int i = 0; i < list.length; i++) {
			maxWidth = MathUtils.max(maxWidth, (int) list[i].getWidth());
		}
		return maxWidth;
	}

	private ObjectMap<ActionBind, ActionTween> tweens = new ObjectMap<ActionBind, ActionTween>(
			CollectionUtils.INITIAL_CAPACITY);

	private final TArray<LComponent> _comps;

	public UIControls(LComponent... comps) {
		this();
		add(comps);
	}

	public UIControls(TArray<LComponent> comps) {
		this();
		add(comps);
	}

	public UIControls() {
		this._comps = new TArray<LComponent>();
	}

	public UIControls add(LComponent comp) {
		if (comp == null) {
			throw new LSysException("LComponent cannot be null.");
		}
		_comps.add(comp);
		return this;
	}

	public UIControls add(LComponent... comps) {
		return add(new TArray<LComponent>(comps));
	}

	public UIControls add(TArray<LComponent> comps) {
		if (comps == null) {
			throw new LSysException("LComponents cannot be null.");
		}
		_comps.addAll(comps);
		return this;
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
		for (int i = 0, n = comps.length; i < n; i++) {
			remove(comps[i]);
		}
		return this;
	}

	public UIControls removeAll() {
		_comps.clear();
		return this;
	}

	public UIControls setSize(int w, int h) {
		for (int i = 0, n = _comps.size; i < n; i++) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.setSize(w, h);
			}
		}
		return this;
	}

	public UIControls setFocusable(boolean focus) {
		for (int i = 0, n = _comps.size; i < n; i++) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.setFocusable(focus);
			}
		}
		return this;
	}

	public UIControls setEnabled(boolean e) {
		for (int i = 0, n = _comps.size; i < n; i++) {
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
		for (int i = 0, n = _comps.size; i < n; i++) {
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
		for (int i = 0, n = _comps.size; i < n; i++) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.setAlpha(a);
			}
		}
		return this;
	}

	public UIControls setTouchLocked(boolean locked) {
		for (int i = 0, n = _comps.size; i < n; i++) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.setTouchLocked(locked);
			}
		}
		return this;
	}

	public UIControls setKeyLocked(boolean locked) {
		for (int i = 0, n = _comps.size; i < n; i++) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.setKeyLocked(locked);
			}
		}
		return this;
	}

	public UIControls setTouch(ClickListener listener) {
		for (int i = 0, n = _comps.size; i < n; i++) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.SetClick(listener);
			}
		}
		return this;
	}

	public UIControls addTouch(ClickListener listener) {
		for (int i = 0, n = _comps.size; i < n; i++) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.addClickListener(listener);
			}
		}
		return this;
	}

	public UIControls up(Touched touched) {
		for (int i = 0, n = _comps.size; i < n; i++) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.up(touched);
			}
		}
		return this;
	}

	public UIControls down(Touched touched) {
		for (int i = 0, n = _comps.size; i < n; i++) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.down(touched);
			}
		}
		return this;
	}

	public UIControls all(Touched touched) {
		for (int i = 0, n = _comps.size; i < n; i++) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.all(touched);
			}
		}
		return this;
	}

	public UIControls downClick() {
		for (int i = 0, n = _comps.size; i < n; i++) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.processTouchPressed();
			}
		}
		return this;
	}

	public UIControls upClick() {
		for (int i = 0, n = _comps.size; i < n; i++) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.processTouchReleased();
			}
		}
		return this;
	}

	public UIControls dragClick() {
		for (int i = 0, n = _comps.size; i < n; i++) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.processTouchDragged();
			}
		}
		return this;
	}

	public UIControls allClick() {
		for (int i = 0, n = _comps.size; i < n; i++) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.processTouchClicked();
			}
		}
		return this;
	}

	public UIControls keyDown() {
		for (int i = 0, n = _comps.size; i < n; i++) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.processKeyPressed();
			}
		}
		return this;
	}

	public UIControls keyUp() {
		for (int i = 0, n = _comps.size; i < n; i++) {
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
		for (int i = 0, n = _comps.size; i < n; i++) {
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
		for (int i = 0, n = _comps.size; i < n; i++) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.out(speed);
			}
		}
		return this;
	}

	public boolean isTransparent() {
		for (int i = 0, n = _comps.size; i < n; i++) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				return (comp.getAlpha() != 1f);
			}
		}
		return false;
	}

	public UIControls setScale(float s) {
		for (int i = 0, n = _comps.size; i < n; i++) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.setScale(s);
			}
		}
		return this;
	}

	public UIControls setScale(float sx, float sy) {
		for (int i = 0, n = _comps.size; i < n; i++) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.setScale(sx, sy);
			}
		}
		return this;
	}

	public UIControls setX(float x) {
		for (int i = 0, n = _comps.size; i < n; i++) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.setX(x);
			}
		}
		return this;
	}

	public UIControls setY(float y) {
		for (int i = 0, n = _comps.size; i < n; i++) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.setY(y);
			}
		}
		return this;
	}

	public UIControls setWidth(int w) {
		for (int i = 0, n = _comps.size; i < n; i++) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.setWidth(w);
			}
		}
		return this;
	}

	public UIControls setHeight(int h) {
		for (int i = 0, n = _comps.size; i < n; i++) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.setHeight(h);
			}
		}
		return this;
	}

	public UIControls setColor(LColor c) {
		for (int i = 0, n = _comps.size; i < n; i++) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.setColor(c);
			}
		}
		return this;
	}

	public UIControls setFont(IFont font) {
		for (int i = 0, n = _comps.size; i < n; i++) {
			LComponent comp = _comps.get(i);
			if (comp != null && (comp instanceof FontSet<?>)) {
				((FontSet<?>) comp).setFont(font);
			}
		}
		return this;
	}

	public UIControls setRotation(float r) {
		for (int i = 0, n = _comps.size; i < n; i++) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.setRotation(r);
			}
		}
		return this;
	}

	public UIControls setLocation(float dx, float dy) {
		for (int i = 0, n = _comps.size; i < n; i++) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.setLocation(dx, dy);
			}
		}
		return this;
	}

	public UIControls setLayer(int z) {
		for (int i = 0, n = _comps.size; i < n; i++) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.setLayer(z);
			}
		}
		return this;
	}

	public UIControls setBackground(LColor color) {
		for (int i = 0, n = _comps.size; i < n; i++) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.setBackground(color);
			}
		}
		return this;
	}

	public UIControls setBackground(LTexture tex) {
		for (int i = 0, n = _comps.size; i < n; i++) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.setBackground(tex);
			}
		}
		return this;
	}

	public UIControls setBackground(String imgPath) {
		for (int i = 0, n = _comps.size; i < n; i++) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.setBackground(imgPath);
			}
		}
		return this;
	}

	public UIControls setVisible(boolean v) {
		for (int i = 0, n = _comps.size; i < n; i++) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.setVisible(v);
			}
		}
		return this;
	}

	public UIControls setTicked(boolean c) {
		for (int i = 0, n = _comps.size; i < n; i++) {
			Object o = _comps.get(i);
			if (o != null && o instanceof LCheckBox) {
				LCheckBox box = (LCheckBox) o;
				box.setTicked(c);
			}
		}
		return this;
	}

	public UIControls setPercentage(float p) {
		for (int i = 0, n = _comps.size; i < n; i++) {
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
		for (int i = 0, n = _comps.size; i < n; i++) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.transferFocus();
			}
		}
		return this;
	}

	public UIControls transferFocusBackward() {
		for (int i = 0, n = _comps.size; i < n; i++) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.transferFocusBackward();
			}
		}
		return this;
	}

	public UIControls requestFocus() {
		for (int i = 0, n = _comps.size; i < n; i++) {
			LComponent comp = _comps.get(i);
			if (comp != null) {
				comp.requestFocus();
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
		for (int i = 0, n = _comps.size; i < n; i++) {
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
		for (int i = 0, n = _comps.size; i < n; i++) {
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
		for (int i = 0, n = _comps.size; i < n; i++) {
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
		for (int i = 0, n = _comps.size; i < n; i++) {
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
		for (int i = 0, n = _comps.size; i < n; i++) {
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
		for (int i = 0, n = _comps.size; i < n; i++) {
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
		for (int i = 0, n = _comps.size; i < n; i++) {
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

	public UIControls delay(float d) {
		for (int i = 0, n = _comps.size; i < n; i++) {
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
		for (int i = 0, n = _comps.size; i < n; i++) {
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
		for (int i = 0, n = _comps.size; i < n; i++) {
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
		for (int i = 0, n = _comps.size; i < n; i++) {
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
		for (int i = 0, n = _comps.size; i < n; i++) {
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
		for (int i = 0, n = _comps.size; i < n; i++) {
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
		for (int i = 0, n = _comps.size; i < n; i++) {
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
		for (int i = 0, n = _comps.size; i < n; i++) {
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

	public UIControls flashTo(float duration) {
		for (int i = 0, n = _comps.size; i < n; i++) {
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
		for (int i = 0, n = _comps.size; i < n; i++) {
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
}
