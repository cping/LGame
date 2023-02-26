/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
 * @version 0.5
 */
package loon.action.sprite;

import loon.LObject;
import loon.LSysException;
import loon.PlayerUtils;
import loon.action.ActionBind;
import loon.action.ActionTween;
import loon.action.map.Field2D;
import loon.canvas.LColor;
import loon.component.layout.Margin;
import loon.events.EventDispatcher;
import loon.font.FontSet;
import loon.font.IFont;
import loon.utils.CollectionUtils;
import loon.utils.MathUtils;
import loon.utils.ObjectMap;
import loon.utils.TArray;
import loon.utils.Easing.EasingMode;

public class SpriteControls {

	public static float getChildrenHeight(Sprites s) {
		float totalHeight = 0;
		ISprite[] list = s._sprites;
		for (int i = 0; i < list.length; i++) {
			totalHeight += list[i].getHeight();
		}
		return totalHeight;
	}

	public static float getChildrenWidth(Sprites s) {
		float totalWidth = 0;
		ISprite[] list = s._sprites;
		for (int i = 0; i < list.length; i++) {
			totalWidth += list[i].getWidth();
		}
		return totalWidth;
	}

	public static float getMaxChildHeight(Sprites s) {
		int maxHeight = 0;
		ISprite[] list = s._sprites;
		for (int i = 0; i < list.length; i++) {
			maxHeight = MathUtils.max(maxHeight, (int) list[i].getHeight());
		}
		return maxHeight;
	}

	public static int getMaxChildWidth(Sprites s) {
		int maxWidth = 0;
		ISprite[] list = s._sprites;
		for (int i = 0; i < list.length; i++) {
			maxWidth = MathUtils.max(maxWidth, (int) list[i].getWidth());
		}
		return maxWidth;
	}

	private ObjectMap<ISprite, ActionTween> tweens = new ObjectMap<ISprite, ActionTween>(
			CollectionUtils.INITIAL_CAPACITY);

	private Margin _margin;

	private final TArray<ISprite> _sprs;

	public SpriteControls(ISprite... comps) {
		this();
		add(comps);
	}

	public SpriteControls(TArray<ISprite> comps) {
		this();
		add(comps);
	}

	public SpriteControls() {
		this._sprs = new TArray<ISprite>();
	}

	public SpriteControls add(ISprite spr) {
		if (spr == null) {
			throw new LSysException("ISprite cannot be null.");
		}
		_sprs.add(spr);
		return this;
	}

	public SpriteControls add(TArray<ISprite> comps) {
		if (comps == null) {
			throw new LSysException("Sprites cannot be null.");
		}
		_sprs.addAll(comps);
		return this;
	}

	public SpriteControls remove(ISprite spr) {
		if (spr == null) {
			throw new LSysException("ISprite cannot be null.");
		}
		_sprs.remove(spr);
		return this;
	}

	public SpriteControls add(ISprite... comps) {
		if (comps == null) {
			throw new LSysException("Sprites cannot be null.");
		}
		for (int i = 0, n = comps.length; i < n; i++) {
			add(comps[i]);
		}
		return this;
	}

	public SpriteControls remove(ISprite... comps) {
		if (comps == null) {
			throw new LSysException("Sprites cannot be null.");
		}
		for (int i = 0, n = comps.length; i < n; i++) {
			remove(comps[i]);
		}
		return this;
	}

	public SpriteControls removeAll() {
		_sprs.clear();
		return this;
	}

	public SpriteControls setSize(int w, int h) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			ISprite spr = _sprs.get(i);
			if (spr != null) {
				if (spr instanceof Sprite) {
					((Sprite) spr).setSize(w, h);
				} else if (spr instanceof Entity) {
					((Entity) spr).setSize(w, h);
				} else if (spr instanceof ActionObject) {
					((ActionObject) spr).setSize(w, h);
				}
			}
		}
		return this;
	}

	public SpriteControls setColor(LColor c) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			ISprite spr = _sprs.get(i);
			if (spr != null) {
				spr.setColor(c);
			}
		}
		return this;
	}

	public SpriteControls setRunning(boolean running) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			ISprite spr = _sprs.get(i);
			if (spr != null) {
				if (spr instanceof Sprite) {
					((Sprite) spr).setRunning(running);
				}
			}
		}
		return this;
	}

	public SpriteControls alpha(float a) {
		return setAlpha(a);
	}

	public SpriteControls setAlpha(float a) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			ISprite spr = _sprs.get(i);
			if (spr != null) {
				if (spr instanceof Sprite) {
					((Sprite) spr).setAlpha(a);
				} else if (spr instanceof Entity) {
					((Entity) spr).setAlpha(a);
				} else if (spr instanceof ActionObject) {
					((ActionObject) spr).setAlpha(a);
				} else if (spr instanceof EventDispatcher) {
					((EventDispatcher) spr).setAlpha(a);
				} else if (spr instanceof LObject<?>) {
					((LObject<?>) spr).setAlpha(a);
				} else {
					spr.setAlpha(a);
				}

			}
		}
		return this;
	}

	public SpriteControls setScale(float s) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			ISprite spr = _sprs.get(i);
			if (spr != null) {
				if (spr instanceof Sprite) {
					((Sprite) spr).setScale(s);
				} else if (spr instanceof Entity) {
					((Entity) spr).setScale(s);
				} else if (spr instanceof ActionObject) {
					((ActionObject) spr).setScale(s);
				} else if (spr instanceof MovieClip) {
					((MovieClip) spr).setScale(s, s);
				} else {
					spr.setScale(s, s);
				}
			}
		}
		return this;
	}

	public SpriteControls setScale(float sx, float sy) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			ISprite spr = _sprs.get(i);
			if (spr != null) {
				if (spr instanceof Sprite) {
					((Sprite) spr).setScale(sx, sy);
				} else if (spr instanceof Entity) {
					((Entity) spr).setScale(sx, sy);
				} else if (spr instanceof ActionObject) {
					((ActionObject) spr).setScale(sx, sy);
				} else if (spr instanceof MovieClip) {
					((MovieClip) spr).setScale(sx, sy);
				} else {
					spr.setScale(sx, sy);
				}
			}
		}
		return this;
	}

	public SpriteControls setX(float x) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			ISprite spr = _sprs.get(i);
			if (spr != null) {
				if (spr instanceof Sprite) {
					((Sprite) spr).setX(x);
				} else if (spr instanceof Entity) {
					((Entity) spr).setX(x);
				} else if (spr instanceof ActionObject) {
					((ActionObject) spr).setX(x);
				} else if (spr instanceof EventDispatcher) {
					((EventDispatcher) spr).setX(x);
				} else if (spr instanceof LObject<?>) {
					((LObject<?>) spr).setX(x);
				} else {
					spr.setX(x);
				}
			}
		}
		return this;
	}

	public SpriteControls setY(float y) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			ISprite spr = _sprs.get(i);
			if (spr != null) {
				if (spr instanceof Sprite) {
					((Sprite) spr).setY(y);
				} else if (spr instanceof Entity) {
					((Entity) spr).setY(y);
				} else if (spr instanceof ActionObject) {
					((ActionObject) spr).setY(y);
				} else if (spr instanceof EventDispatcher) {
					((EventDispatcher) spr).setY(y);
				} else if (spr instanceof LObject<?>) {
					((LObject<?>) spr).setY(y);
				} else {
					spr.setY(y);
				}
			}
		}
		return this;
	}

	public SpriteControls location(float x, float y) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			ISprite spr = _sprs.get(i);
			if (spr != null) {
				if (spr instanceof Sprite) {
					((Sprite) spr).setLocation(x, y);
				} else if (spr instanceof Entity) {
					((Entity) spr).setLocation(x, y);
				} else if (spr instanceof ActionObject) {
					((ActionObject) spr).setLocation(x, y);
				} else if (spr instanceof EventDispatcher) {
					((EventDispatcher) spr).setLocation(x, y);
				} else if (spr instanceof LObject<?>) {
					((LObject<?>) spr).setLocation(x, y);
				} else {
					spr.setLocation(x, y);
				}
			}
		}
		return this;
	}

	public SpriteControls offset(float x, float y) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			ISprite spr = _sprs.get(i);
			if (spr != null) {
				float oldX = spr.getX();
				float oldY = spr.getY();
				if (spr instanceof Sprite) {
					((Sprite) spr).setLocation(oldX + x, oldY + y);
				} else if (spr instanceof Entity) {
					((Entity) spr).setLocation(oldX + x, oldY + y);
				} else if (spr instanceof ActionObject) {
					((ActionObject) spr).setLocation(oldX + x, oldY + y);
				} else if (spr instanceof EventDispatcher) {
					((EventDispatcher) spr).setLocation(oldX + x, oldY + y);
				} else if (spr instanceof LObject<?>) {
					((LObject<?>) spr).setLocation(oldX + x, oldY + y);
				} else {
					spr.setLocation(oldX + x, oldY + y);
				}
			}
		}
		return this;
	}

	public SpriteControls setWidth(int w) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			ISprite spr = _sprs.get(i);
			if (spr != null) {
				if (spr instanceof Sprite) {
					((Sprite) spr).setWidth(w);
				} else if (spr instanceof Entity) {
					((Entity) spr).setWidth(w);
				} else if (spr instanceof ActionObject) {
					((ActionObject) spr).setWidth(w);
				} else if (spr instanceof MovieClip) {
					((MovieClip) spr).setWidth(w);
				}
			}
		}
		return this;
	}

	public SpriteControls setHeight(int h) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			ISprite spr = _sprs.get(i);
			if (spr != null) {
				if (spr instanceof Sprite) {
					((Sprite) spr).setHeight(h);
				} else if (spr instanceof Entity) {
					((Entity) spr).setHeight(h);
				} else if (spr instanceof ActionObject) {
					((ActionObject) spr).setHeight(h);
				} else if (spr instanceof MovieClip) {
					((MovieClip) spr).setHeight(h);
				}
			}
		}
		return this;
	}

	public SpriteControls setFont(IFont font) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			ISprite spr = _sprs.get(i);
			if (spr != null) {
				if (spr instanceof FontSet<?>) {
					((FontSet<?>) spr).setFont(font);
				}
			}
		}
		return this;
	}

	public SpriteControls setRotation(float r) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			ISprite spr = _sprs.get(i);
			if (spr != null) {
				if (spr instanceof Sprite) {
					((Sprite) spr).setRotation(r);
				} else if (spr instanceof Entity) {
					((Entity) spr).setRotation(r);
				} else if (spr instanceof ActionObject) {
					((ActionObject) spr).setRotation(r);
				} else if (spr instanceof EventDispatcher) {
					((EventDispatcher) spr).setRotation(r);
				} else if (spr instanceof LObject<?>) {
					((LObject<?>) spr).setRotation(r);
				}
			}
		}
		return this;
	}

	public SpriteControls setLocation(float dx, float dy) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			ISprite spr = _sprs.get(i);
			if (spr != null) {
				if (spr instanceof Sprite) {
					((Sprite) spr).setLocation(dx, dy);
				} else if (spr instanceof Entity) {
					((Entity) spr).setLocation(dx, dy);
				} else if (spr instanceof ActionObject) {
					((ActionObject) spr).setLocation(dx, dy);
				} else if (spr instanceof EventDispatcher) {
					((EventDispatcher) spr).setLocation(dx, dy);
				} else if (spr instanceof LObject<?>) {
					((LObject<?>) spr).setLocation(dx, dy);
				}
			}
		}
		return this;
	}

	public SpriteControls setLayer(int z) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			ISprite spr = _sprs.get(i);
			if (spr != null) {
				if (spr instanceof Sprite) {
					((Sprite) spr).setLayer(z);
				} else if (spr instanceof Entity) {
					((Entity) spr).setLayer(z);
				} else if (spr instanceof ActionObject) {
					((ActionObject) spr).setLayer(z);
				} else if (spr instanceof EventDispatcher) {
					((EventDispatcher) spr).setLayer(z);
				} else if (spr instanceof LObject<?>) {
					((LObject<?>) spr).setLayer(z);
				}
			}
		}
		return this;
	}

	public SpriteControls setVisible(boolean v) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			ISprite spr = _sprs.get(i);
			if (spr != null) {
				if (spr instanceof Sprite) {
					((Sprite) spr).setVisible(v);
				} else if (spr instanceof Entity) {
					((Entity) spr).setVisible(v);
				} else if (spr instanceof ActionObject) {
					((ActionObject) spr).setVisible(v);
				}
			}
		}
		return this;
	}

	public SpriteControls clearTweens() {
		for (ActionTween tween : tweens.values()) {
			tween.free();
		}
		tweens.clear();
		return this;
	}

	public SpriteControls startTweens() {
		for (ActionTween tween : tweens.values()) {
			tween.start();
		}
		return this;
	}

	public SpriteControls killTweens() {
		for (ActionTween tween : tweens.values()) {
			tween.kill();
		}
		return this;
	}

	public SpriteControls pauseTweens() {
		for (ActionTween tween : tweens.values()) {
			tween.pause();
		}
		return this;
	}

	public SpriteControls resumeTweens() {
		for (ActionTween tween : tweens.values()) {
			tween.resume();
		}
		return this;
	}

	public SpriteControls fadeOut(float speed) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			ISprite spr = _sprs.get(i);
			if (spr != null && (spr instanceof ActionBind)) {
				ActionTween tween = tweens.get(spr);
				if (spr.getAlpha() >= 255) {
					if (tween == null) {
						tween = PlayerUtils.set((ActionBind) spr).fadeIn(speed);
					} else {
						tween.fadeIn(speed);
					}
				} else {
					if (tween == null) {
						tween = PlayerUtils.set((ActionBind) spr).fadeOut(speed);
					} else {
						tween.fadeOut(speed);
					}
					if (!tweens.containsKey(spr)) {
						tweens.put(spr, tween);
					}
				}
			}
		}
		return this;
	}

	public SpriteControls fadeIn(float speed) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			ISprite spr = _sprs.get(i);
			if (spr != null && (spr instanceof ActionBind)) {
				ActionTween tween = tweens.get(spr);
				if (spr.getAlpha() <= 0) {
					if (tween == null) {
						tween = PlayerUtils.set((ActionBind) spr).fadeOut(speed);
					} else {
						tween.fadeOut(speed);
					}
				} else {
					if (tween == null) {
						tween = PlayerUtils.set((ActionBind) spr).fadeIn(speed);
					} else {
						tween.fadeIn(speed);
					}
				}
				if (!tweens.containsKey(spr)) {
					tweens.put(spr, tween);
				}
			}
		}
		return this;
	}

	public SpriteControls moveBy(float endX, float endY, int speed) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			ISprite spr = _sprs.get(i);
			if (spr != null && (spr instanceof ActionBind)) {
				ActionTween tween = tweens.get(spr);
				if (tween == null) {
					tween = PlayerUtils.set((ActionBind) spr).moveBy(endX, endY, speed);
				} else {
					tween.moveBy(endX, endY, speed);
				}
				if (!tweens.containsKey(spr)) {
					tweens.put(spr, tween);
				}
			}
		}
		return this;
	}

	public SpriteControls moveBy(float endX, float endY) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			ISprite spr = _sprs.get(i);
			if (spr != null && (spr instanceof ActionBind)) {
				ActionTween tween = tweens.get(spr);
				if (tween == null) {
					tween = PlayerUtils.set((ActionBind) spr).moveBy(endX, endY);
				} else {
					tween.moveBy(endX, endY);
				}
				if (!tweens.containsKey(spr)) {
					tweens.put(spr, tween);
				}
			}
		}
		return this;
	}

	public SpriteControls moveTo(float endX, float endY, int speed) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			ISprite spr = _sprs.get(i);
			if (spr != null && (spr instanceof ActionBind)) {
				ActionTween tween = tweens.get(spr);
				if (tween == null) {
					tween = PlayerUtils.set((ActionBind) spr).moveTo(endX, endY, speed);
				} else {
					tween.moveTo(endX, endY, speed);
				}
				if (!tweens.containsKey(spr)) {
					tweens.put(spr, tween);
				}

			}
		}
		return this;
	}

	public SpriteControls moveTo(float endX, float endY, boolean flag, int speed) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			ISprite spr = _sprs.get(i);
			if (spr != null && (spr instanceof ActionBind)) {
				ActionTween tween = tweens.get(spr);
				if (tween == null) {
					tween = PlayerUtils.set((ActionBind) spr).moveTo(endX, endY, flag, speed);
				} else {
					tween.moveTo(endX, endY, flag, speed);
				}
				if (!tweens.containsKey(spr)) {
					tweens.put(spr, tween);
				}

			}
		}
		return this;
	}

	public SpriteControls moveTo(Field2D map, float endX, float endY, boolean flag, int speed) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			ISprite spr = _sprs.get(i);
			if (spr != null && (spr instanceof ActionBind)) {
				ActionTween tween = tweens.get(spr);
				if (tween == null) {
					tween = PlayerUtils.set((ActionBind) spr).moveTo(map, endX, endY, flag, speed);
				} else {
					tween.moveTo(map, endX, endY, flag, speed);
				}
				if (!tweens.containsKey(spr)) {
					tweens.put(spr, tween);
				}

			}
		}
		return this;
	}

	public SpriteControls delay(float d) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			ISprite spr = _sprs.get(i);
			if (spr != null && (spr instanceof ActionBind)) {
				ActionTween tween = tweens.get(spr);
				if (tween == null) {
					tween = PlayerUtils.set((ActionBind) spr).delay(d);
				} else {
					tween.delay(d);
				}
				if (!tweens.containsKey(spr)) {
					tweens.put(spr, tween);
				}

			}
		}
		return this;
	}

	public SpriteControls rotateTo(float angle) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			ISprite spr = _sprs.get(i);
			if (spr != null && (spr instanceof ActionBind)) {
				ActionTween tween = tweens.get(spr);
				if (tween == null) {
					tween = PlayerUtils.set((ActionBind) spr).rotateTo(angle);
				} else {
					tween.rotateTo(angle);
				}
				if (!tweens.containsKey(spr)) {
					tweens.put(spr, tween);
				}

			}
		}
		return this;
	}

	public SpriteControls rotateTo(float angle, float speed) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			ISprite spr = _sprs.get(i);
			if (spr != null && (spr instanceof ActionBind)) {
				ActionTween tween = tweens.get(spr);
				if (tween == null) {
					tween = PlayerUtils.set((ActionBind) spr).rotateTo(angle, speed);
				} else {
					tween.rotateTo(angle, speed);
				}
				if (!tweens.containsKey(spr)) {
					tweens.put(spr, tween);
				}

			}
		}
		return this;
	}

	public SpriteControls scaleTo(float sx, float sy) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			ISprite spr = _sprs.get(i);
			if (spr != null && (spr instanceof ActionBind)) {
				ActionTween tween = tweens.get(spr);
				if (tween == null) {
					tween = PlayerUtils.set((ActionBind) spr).scaleTo(sx, sy);
				} else {
					tween.scaleTo(sx, sy);
				}
				if (!tweens.containsKey(spr)) {
					tweens.put(spr, tween);
				}

			}
		}
		return this;
	}

	public SpriteControls scaleTo(float sx, float sy, float speed) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			ISprite spr = _sprs.get(i);
			if (spr != null && (spr instanceof ActionBind)) {
				ActionTween tween = tweens.get(spr);
				if (tween == null) {
					tween = PlayerUtils.set((ActionBind) spr).scaleTo(sx, sy, speed);
				} else {
					tween.scaleTo(sx, sy, speed);
				}
				if (!tweens.containsKey(spr)) {
					tweens.put(spr, tween);
				}

			}
		}
		return this;
	}

	public SpriteControls showTo(boolean v) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			ISprite spr = _sprs.get(i);
			if (spr != null && (spr instanceof ActionBind)) {
				ActionTween tween = tweens.get(spr);
				if (tween == null) {
					tween = PlayerUtils.set((ActionBind) spr).showTo(v);
				} else {
					tween.showTo(v);
				}
				if (!tweens.containsKey(spr)) {
					tweens.put(spr, tween);
				}

			}
		}
		return this;
	}

	public SpriteControls colorTo(LColor end) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			ISprite spr = _sprs.get(i);
			if (spr != null && (spr instanceof ActionBind)) {
				ActionTween tween = tweens.get(spr);
				if (tween == null) {
					tween = PlayerUtils.set((ActionBind) spr).colorTo(end);
				} else {
					tween.colorTo(end);
				}
				if (!tweens.containsKey(spr)) {
					tweens.put(spr, tween);
				}

			}
		}
		return this;
	}

	public SpriteControls shakeTo(float shakeX, float shakeY) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			ISprite spr = _sprs.get(i);
			if (spr != null && (spr instanceof ActionBind)) {
				ActionTween tween = tweens.get(spr);
				if (tween == null) {
					tween = PlayerUtils.set((ActionBind) spr).shakeTo(shakeX, shakeY);
				} else {
					tween.shakeTo(shakeX, shakeY);
				}
				if (!tweens.containsKey(spr)) {
					tweens.put(spr, tween);
				}

			}
		}
		return this;
	}

	public SpriteControls followTo(ActionBind bind, float follow, float speed) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			ISprite spr = _sprs.get(i);
			if (spr != null && (spr instanceof ActionBind)) {
				ActionTween tween = tweens.get(spr);
				if (tween == null) {
					tween = PlayerUtils.set((ActionBind) spr).followTo(bind, follow, speed);
				} else {
					tween.followTo(bind, follow, speed);
				}
				if (!tweens.containsKey(spr)) {
					tweens.put(spr, tween);
				}

			}
		}
		return this;
	}

	public SpriteControls flashTo(float duration) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			ISprite spr = _sprs.get(i);
			if (spr != null && (spr instanceof ActionBind)) {
				ActionTween tween = tweens.get(spr);
				if (tween == null) {
					tween = PlayerUtils.set((ActionBind) spr).flashTo(duration);
				} else {
					tween.flashTo(duration);
				}
				if (!tweens.containsKey(spr)) {
					tweens.put(spr, tween);
				}

			}
		}
		return this;
	}

	public SpriteControls transferTo(float startPos, float endPos, float duration, EasingMode mode, boolean controlX,
			boolean controlY) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			ISprite spr = _sprs.get(i);
			if (spr != null && (spr instanceof ActionBind)) {
				ActionTween tween = tweens.get(spr);
				if (tween == null) {
					tween = PlayerUtils.set((ActionBind) spr).transferTo(startPos, endPos, duration, mode, controlX,
							controlY);
				} else {
					tween.transferTo(startPos, endPos, duration, mode, controlX, controlY);
				}
				if (!tweens.containsKey(spr)) {
					tweens.put(spr, tween);
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
		for (int i = 0; i < _sprs.size; i++) {
			ISprite spr = _sprs.get(i);
			if (spr != null) {
				_margin.addChild(spr);
			}
		}
		return _margin;
	}
}
