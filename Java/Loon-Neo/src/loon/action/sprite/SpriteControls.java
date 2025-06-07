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
import loon.Screen;
import loon.action.ActionBind;
import loon.action.ActionTween;
import loon.action.collision.CollisionHelper;
import loon.action.map.Field2D;
import loon.canvas.LColor;
import loon.component.layout.Margin;
import loon.events.EventActionT;
import loon.events.EventDispatcher;
import loon.events.QueryEvent;
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
 * Sprite组件的群组化操作控制器，可以同时改变一组精灵的参数或动画事件
 */
public class SpriteControls {

	public static float getChildrenHeight(Sprites s) {
		if (s == null) {
			return 0f;
		}
		float totalHeight = 0;
		final ISprite[] list = s._sprites;
		final int size = list.length;
		for (int i = size - 1; i > -1; --i) {
			totalHeight += list[i].getHeight();
		}
		return totalHeight;
	}

	public static float getChildrenWidth(Sprites s) {
		if (s == null) {
			return 0f;
		}
		float totalWidth = 0;
		final ISprite[] list = s._sprites;
		final int size = list.length;
		for (int i = size - 1; i > -1; --i) {
			totalWidth += list[i].getWidth();
		}
		return totalWidth;
	}

	public static float getMaxChildHeight(Sprites s) {
		if (s == null) {
			return 0f;
		}
		float maxHeight = 0;
		final ISprite[] list = s._sprites;
		final int size = list.length;
		for (int i = size - 1; i > -1; --i) {
			maxHeight = MathUtils.max(maxHeight, list[i].getHeight());
		}
		return maxHeight;
	}

	public static float getMaxChildWidth(Sprites s) {
		if (s == null) {
			return 0f;
		}
		float maxWidth = 0;
		final ISprite[] list = s._sprites;
		final int size = list.length;
		for (int i = size - 1; i > -1; --i) {
			maxWidth = MathUtils.max(maxWidth, list[i].getWidth());
		}
		return maxWidth;
	}

	private ObjectMap<ISprite, ActionTween> tweens = new ObjectMap<ISprite, ActionTween>(
			CollectionUtils.INITIAL_CAPACITY);

	private Margin _margin;

	private TArray<ISprite> _sprs;

	public SpriteControls(ISprite... sprs) {
		this();
		if (sprs != null) {
			add(sprs);
		}
	}

	public SpriteControls(TArray<ISprite> sprs) {
		this();
		if (sprs != null) {
			add(sprs);
		}
	}

	public SpriteControls() {
		this._sprs = new TArray<ISprite>();
	}

	public SpriteControls set(Screen screen) {
		if (screen == null) {
			return this;
		}
		return set(screen.getSprites());
	}

	public SpriteControls set(Sprites sprites) {
		if (sprites == null) {
			return this;
		}
		synchronized (sprites) {
			set(sprites.getSpritesArray());
		}
		return this;
	}

	public SpriteControls set(TArray<ISprite> sprites) {
		if (sprites == null || sprites.size == 0 || sprites.equals(this._sprs)) {
			return this;
		}
		synchronized (SpriteControls.class) {
			clear();
			this._sprs = sprites;
		}
		return this;
	}

	public SpriteControls clear() {
		if (this._sprs.size == 0) {
			return this;
		}
		this._sprs.clear();
		return this;
	}

	public SpriteControls clear(Screen screen) {
		if (screen == null) {
			return this;
		}
		return clear(screen.getSprites());
	}

	public SpriteControls clear(Sprites sprites) {
		if (sprites == null) {
			return this;
		}
		synchronized (sprites) {
			sprites.clear(this._sprs);
			clear();
		}
		return this;
	}

	public ISprite random() {
		if (_sprs.size == 0) {
			return null;
		}
		return _sprs.get(MathUtils.random(_sprs.size - 1));
	}

	public int size() {
		return _sprs.size;
	}

	public boolean isEmpty() {
		return _sprs.isEmpty();
	}

	public boolean isNotEmpty() {
		return _sprs.isNotEmpty();
	}

	public int indexOf(QueryEvent<ISprite> q) {
		if (q == null) {
			return -1;
		}
		int i = 0;
		for (ISprite s : _sprs) {
			if (q.hit(s)) {
				return i;
			}
			i++;
		}
		return -1;
	}

	public int indexOf(ISprite v) {
		if (v == null) {
			return -1;
		}
		int i = 0;
		for (ISprite s : _sprs) {
			if (s != null && (s == v || s.equals(v))) {
				return i;
			}
			i++;
		}
		return -1;
	}

	public ISprite find(QueryEvent<ISprite> q) {
		if (q == null) {
			return null;
		}
		for (ISprite s : _sprs) {
			if (q.hit(s)) {
				return s;
			}
		}
		return null;
	}

	public SpriteControls fill(Screen screen) {
		if (screen == null) {
			return this;
		}
		return set(screen.getSprites());
	}

	public SpriteControls fill(Sprites sprites) {
		if (sprites == null) {
			return this;
		}
		synchronized (sprites) {
			set(sprites.getSpritesArray());
		}
		return this;
	}

	public SpriteControls fill(TArray<ISprite> sprites) {
		if (sprites == null || sprites.size == 0 || sprites.equals(this._sprs)) {
			return this;
		}
		this._sprs.fill(sprites);
		return this;
	}

	public TArray<ISprite> list() {
		return this._sprs;
	}

	public TArray<ISprite> intersects(float x, float y) {
		TArray<ISprite> sprites = new TArray<ISprite>();
		for (ISprite child : this._sprs) {
			if (child != null) {
				if (child.getRectBox().inPoint(x, y)) {
					sprites.add(child);
				}
			}
		}
		return sprites;
	}

	public ISprite intersectsOnly(float x, float y) {
		for (ISprite child : this._sprs) {
			if (child != null) {
				if (child.getRectBox().inPoint(x, y)) {
					return child;
				}
			}
		}
		return null;
	}

	public TArray<ISprite> intersects(RectBox rect) {
		TArray<ISprite> sprites = new TArray<ISprite>();
		for (ISprite child : this._sprs) {
			if (child != null) {
				if (rect.intersects(child.getX(), child.getY(), child.getWidth(), child.getHeight())) {
					sprites.add(child);
				}
			}
		}
		return sprites;
	}

	public TArray<ISprite> intersects(float x, float y, float width, float height) {
		TArray<ISprite> sprites = new TArray<ISprite>();
		for (ISprite child : this._sprs) {
			if (child != null) {
				if (CollisionHelper.intersects(x, y, width, height, child.getX(), child.getY(), child.getWidth(),
						child.getHeight())) {
					sprites.add(child);
				}
			}
		}
		return sprites;
	}

	public TArray<ISprite> contains(RectBox rect) {
		TArray<ISprite> sprites = new TArray<ISprite>();
		for (ISprite child : this._sprs) {
			if (child != null) {
				if (rect.contains(child.getX(), child.getY(), child.getWidth(), child.getHeight())) {
					sprites.add(child);
				}
			}
		}
		return sprites;
	}

	public TArray<ISprite> contains(float x, float y, float width, float height) {
		TArray<ISprite> sprites = new TArray<ISprite>();
		for (ISprite child : this._sprs) {
			if (child != null) {
				if (CollisionHelper.contains(x, y, width, height, child.getX(), child.getY(), child.getWidth(),
						child.getHeight())) {
					sprites.add(child);
				}
			}
		}
		return sprites;
	}

	public boolean allIn(Screen screen) {
		return allIn(screen, true);
	}

	public boolean allIn(Screen screen, boolean canView) {
		return getCountIn(screen, canView) >= _sprs.size;
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
		final int size = _sprs.size;
		for (int i = size - 1; i > -1; --i) {
			ISprite spr = _sprs.get(i);
			if (spr != null && (screen.intersects(spr, canView) || screen.contains(spr, canView))) {
				count++;
			}
		}
		return count;
	}

	public SpriteControls add(ISprite spr) {
		if (spr == null) {
			throw new LSysException("ISprite cannot be null.");
		}
		_sprs.add(spr);
		return this;
	}

	public SpriteControls add(TArray<ISprite> sprites) {
		if (sprites == null) {
			throw new LSysException("Sprites cannot be null.");
		}
		_sprs.addAll(sprites);
		return this;
	}

	public SpriteControls add(ISprite... sprites) {
		if (sprites == null) {
			throw new LSysException("Sprites cannot be null.");
		}
		for (int i = 0, n = sprites.length; i < n; i++) {
			ISprite sprite = sprites[i];
			if (sprite != null) {
				add(sprite);
			}
		}
		return this;
	}

	public SpriteControls remove(Screen screen, ISprite spr) {
		if (screen == null) {
			throw new LSysException("Screen cannot be null.");
		}
		return remove(screen.getSprites(), spr);
	}

	public SpriteControls remove(Sprites sprites, ISprite spr) {
		if (sprites == null) {
			throw new LSysException("Sprites cannot be null.");
		}
		sprites.remove(spr);
		return remove(spr);
	}

	public SpriteControls remove(ISprite spr) {
		if (spr == null) {
			throw new LSysException("ISprite cannot be null.");
		}
		_sprs.remove(spr);
		return this;
	}

	public SpriteControls remove(ISprite... sprites) {
		if (sprites == null) {
			throw new LSysException("Sprites cannot be null.");
		}
		final int size = _sprs.size;
		for (int i = size - 1; i > -1; --i) {
			ISprite sprite = sprites[i];
			if (sprite != null) {
				remove(sprite);
			}
		}
		return this;
	}

	public SpriteControls removeAll() {
		if (this._sprs.size == 0) {
			return this;
		}
		_sprs.clear();
		return this;
	}

	public SpriteControls setSize(int w, int h) {
		final int size = _sprs.size;
		for (int i = size - 1; i > -1; --i) {
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
		final int size = _sprs.size;
		for (int i = size - 1; i > -1; --i) {
			ISprite spr = _sprs.get(i);
			if (spr != null) {
				spr.setColor(c);
			}
		}
		return this;
	}

	public SpriteControls setRunning(boolean running) {
		final int size = _sprs.size;
		for (int i = size - 1; i > -1; --i) {
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
		final int size = _sprs.size;
		for (int i = size - 1; i > -1; --i) {
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
		final int size = _sprs.size;
		for (int i = size - 1; i > -1; --i) {
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
		final int size = _sprs.size;
		for (int i = size - 1; i > -1; --i) {
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
		final int size = _sprs.size;
		for (int i = size - 1; i > -1; --i) {
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
		final int size = _sprs.size;
		for (int i = size - 1; i > -1; --i) {
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
		final int size = _sprs.size;
		for (int i = size - 1; i > -1; --i) {
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
		final int size = _sprs.size;
		for (int i = size - 1; i > -1; --i) {
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
		final int size = _sprs.size;
		for (int i = size - 1; i > -1; --i) {
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
		final int size = _sprs.size;
		for (int i = size - 1; i > -1; --i) {
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
		final int size = _sprs.size;
		for (int i = size - 1; i > -1; --i) {
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
		final int size = _sprs.size;
		for (int i = size - 1; i > -1; --i) {
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
		final int size = _sprs.size;
		for (int i = size - 1; i > -1; --i) {
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
		final int size = _sprs.size;
		for (int i = size - 1; i > -1; --i) {
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
		final int size = _sprs.size;
		for (int i = size - 1; i > -1; --i) {
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

	public SpriteControls callEvents(EventActionT<ISprite> e) {
		if (e == null) {
			return this;
		}
		for (int i = _sprs.size - 1; i > -1; --i) {
			ISprite spr = _sprs.get(i);
			if (spr != null && e != null) {
				e.update(spr);
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
		final int size = _sprs.size;
		for (int i = size - 1; i > -1; --i) {
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
		final int size = _sprs.size;
		for (int i = size - 1; i > -1; --i) {
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
		final int size = _sprs.size;
		for (int i = size - 1; i > -1; --i) {
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
		final int size = _sprs.size;
		for (int i = size - 1; i > -1; --i) {
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
		final int size = _sprs.size;
		for (int i = size - 1; i > -1; --i) {
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
		final int size = _sprs.size;
		for (int i = size - 1; i > -1; --i) {
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
		final int size = _sprs.size;
		for (int i = size - 1; i > -1; --i) {
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

	/**
	 * 将控制器中的精灵随机放置于Screen显示范围外的左侧
	 * 
	 * @param screen
	 * @return
	 */
	public SpriteControls outsideLeftRandOn(Screen screen) {
		if (screen == null) {
			return this;
		}
		int count = 0;
		final int size = _sprs.size;
		final int maxCount = size * size * 4;
		for (int i = 0, n = size; i < n; i++) {
			ISprite spr = _sprs.get(i);
			if (spr != null && (spr instanceof LObject<?>)) {
				LObject<?> o = ((LObject<?>) spr);
				screen.outsideLeftRandOn(o, MathUtils.random(0f, screen.getWidth()), 0f);
				for (int j = size - 1; j > -1; --j) {
					ISprite dst = _sprs.get(j);
					if (dst != null && dst != spr) {
						RectBox rect = spr.getCollisionBox();
						if (rect.collided(dst.getCollisionBox()) || rect.contains(dst.getCollisionBox())) {
							if (count > maxCount) {
								spr.setLocation(spr.getX() - spr.getWidth() * 2f, spr.getY());
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
	 * 将控制器中的精灵随机放置于Screen显示范围外的右侧
	 * 
	 * @param screen
	 * @return
	 */
	public SpriteControls outsideRightRandOn(Screen screen) {
		if (screen == null) {
			return this;
		}
		int count = 0;
		final int size = _sprs.size;
		final int maxCount = size * size * 4;
		for (int i = 0, n = size; i < n; i++) {
			ISprite spr = _sprs.get(i);
			if (spr != null && (spr instanceof LObject<?>)) {
				LObject<?> o = ((LObject<?>) spr);
				screen.outsideRightRandOn(o, MathUtils.random(0f, screen.getWidth()), 0f);
				for (int j = size - 1; j > -1; --j) {
					ISprite dst = _sprs.get(j);
					if (dst != null && dst != spr) {
						RectBox rect = spr.getCollisionBox();
						if (rect.collided(dst.getCollisionBox()) || rect.contains(dst.getCollisionBox())) {
							if (count > maxCount) {
								spr.setLocation(spr.getX() + spr.getWidth() * 2f, spr.getY());
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
	 * 将控制器中的精灵随机放置于Screen显示范围外的上侧
	 * 
	 * @param screen
	 * @return
	 */
	public SpriteControls outsideTopRandOn(Screen screen) {
		if (screen == null) {
			return this;
		}
		int count = 0;
		final int size = _sprs.size;
		final int maxCount = size * size * 4;
		for (int i = 0, n = size; i < n; i++) {
			ISprite spr = _sprs.get(i);
			if (spr != null && (spr instanceof LObject<?>)) {
				LObject<?> o = ((LObject<?>) spr);
				screen.outsideTopRandOn(o, 0f, MathUtils.random(0f, screen.getHeight()));
				for (int j = size - 1; j > -1; --j) {
					ISprite dst = _sprs.get(j);
					if (dst != null && dst != spr) {
						RectBox rect = spr.getCollisionBox();
						if (rect.collided(dst.getCollisionBox()) || rect.contains(dst.getCollisionBox())) {
							if (count > maxCount) {
								spr.setLocation(spr.getX(), spr.getY() - spr.getHeight() * 2f);
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
	 * 将控制器中的精灵随机放置于Screen显示范围外的下侧
	 * 
	 * @param screen
	 * @return
	 */
	public SpriteControls outsideBottomRandOn(Screen screen) {
		if (screen == null) {
			return this;
		}
		int count = 0;
		final int size = _sprs.size;
		final int maxCount = size * size * 4;
		for (int i = 0, n = size; i < n; i++) {
			ISprite spr = _sprs.get(i);
			if (spr != null && (spr instanceof LObject<?>)) {
				LObject<?> o = ((LObject<?>) spr);
				screen.outsideBottomRandOn(o, 0f, MathUtils.random(0f, screen.getHeight()));
				for (int j = size - 1; j > -1; --j) {
					ISprite dst = _sprs.get(j);
					if (dst != null && dst != spr) {
						RectBox rect = spr.getCollisionBox();
						if (rect.collided(dst.getCollisionBox()) || rect.contains(dst.getCollisionBox())) {
							if (count > maxCount) {
								spr.setLocation(spr.getX(), spr.getY() + spr.getHeight() * 2f);
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

	public SpriteControls move(float x, float y) {
		final int size = _sprs.size;
		for (int i = size - 1; i > -1; --i) {
			ISprite spr = _sprs.get(i);
			if (spr != null && (spr instanceof LObject<?>)) {
				LObject<?> o = ((LObject<?>) spr);
				o.move(x, y);
			}
		}
		return this;
	}

	public SpriteControls move_left(float v) {
		final int size = _sprs.size;
		for (int i = size - 1; i > -1; --i) {
			ISprite spr = _sprs.get(i);
			if (spr != null && (spr instanceof LObject<?>)) {
				LObject<?> o = ((LObject<?>) spr);
				o.move_left(v);
			}
		}
		return this;
	}

	public SpriteControls move_right(float v) {
		final int size = _sprs.size;
		for (int i = size - 1; i > -1; --i) {
			ISprite spr = _sprs.get(i);
			if (spr != null && (spr instanceof LObject<?>)) {
				LObject<?> o = ((LObject<?>) spr);
				o.move_right(v);
			}
		}
		return this;
	}

	public SpriteControls move_up(float v) {
		final int size = _sprs.size;
		for (int i = size - 1; i > -1; --i) {
			ISprite spr = _sprs.get(i);
			if (spr != null && (spr instanceof LObject<?>)) {
				LObject<?> o = ((LObject<?>) spr);
				o.move_up(v);
			}
		}
		return this;
	}

	public SpriteControls move_down(float v) {
		final int size = _sprs.size;
		for (int i = size - 1; i > -1; --i) {
			ISprite spr = _sprs.get(i);
			if (spr != null && (spr instanceof LObject<?>)) {
				LObject<?> o = ((LObject<?>) spr);
				o.move_down(v);
			}
		}
		return this;
	}

	public SpriteControls move_45D_left(float v) {
		final int size = _sprs.size;
		for (int i = size - 1; i > -1; --i) {
			ISprite spr = _sprs.get(i);
			if (spr != null && (spr instanceof LObject<?>)) {
				LObject<?> o = ((LObject<?>) spr);
				o.move_45D_left(v);
			}
		}
		return this;
	}

	public SpriteControls move_45D_right(float v) {
		final int size = _sprs.size;
		for (int i = size - 1; i > -1; --i) {
			ISprite spr = _sprs.get(i);
			if (spr != null && (spr instanceof LObject<?>)) {
				LObject<?> o = ((LObject<?>) spr);
				o.move_45D_right(v);
			}
		}
		return this;
	}

	public SpriteControls move_45D_up(float v) {
		final int size = _sprs.size;
		for (int i = size - 1; i > -1; --i) {
			ISprite spr = _sprs.get(i);
			if (spr != null && (spr instanceof LObject<?>)) {
				LObject<?> o = ((LObject<?>) spr);
				o.move_45D_up(v);
			}
		}
		return this;
	}

	public SpriteControls move_45D_down(float v) {
		final int size = _sprs.size;
		for (int i = size - 1; i > -1; --i) {
			ISprite spr = _sprs.get(i);
			if (spr != null && (spr instanceof LObject<?>)) {
				LObject<?> o = ((LObject<?>) spr);
				o.move_45D_down(v);
			}
		}
		return this;
	}

	public SpriteControls delay(float d) {
		final int size = _sprs.size;
		for (int i = size - 1; i > -1; --i) {
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
		final int size = _sprs.size;
		for (int i = size - 1; i > -1; --i) {
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
		final int size = _sprs.size;
		for (int i = size - 1; i > -1; --i) {
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
		final int size = _sprs.size;
		for (int i = size - 1; i > -1; --i) {
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
		final int size = _sprs.size;
		for (int i = size - 1; i > -1; --i) {
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
		final int size = _sprs.size;
		for (int i = size - 1; i > -1; --i) {
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
		final int size = _sprs.size;
		for (int i = size - 1; i > -1; --i) {
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
		final int size = _sprs.size;
		for (int i = size - 1; i > -1; --i) {
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
		final int size = _sprs.size;
		for (int i = size - 1; i > -1; --i) {
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
		final int size = _sprs.size;
		for (int i = size - 1; i > -1; --i) {
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
		final int size = _sprs.size;
		for (int i = size - 1; i > -1; --i) {
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
		final int len = _sprs.size;
		for (int i = len - 1; i > -1; --i) {
			ISprite spr = _sprs.get(i);
			if (spr != null) {
				_margin.addChild(spr);
			}
		}
		return _margin;
	}

	public Vector2f getMoveTarget(TArray<XY> targets, ISprite spr, Sized mapSize, float scrollWidth, float scrollHeight,
			float orthogonalWidth, float orthogonalHeight, float paddingX, float paddingY) {
		if (spr == null) {
			return null;
		}
		ISprite parentSprite = spr.getParent() == null ? spr : spr.getParent();

		Vector2f parentLocal = Vector2f.at(parentSprite.getX() + parentSprite.getOffsetX(),
				parentSprite.getY() + parentSprite.getOffsetY());

		return getMoveTarget(targets, spr, parentLocal, mapSize, scrollWidth, scrollHeight, orthogonalWidth,
				orthogonalHeight, paddingX, paddingY);
	}

	public Vector2f getMoveTarget(XY pos, ISprite spr, Sized mapSize, float scrollWidth, float scrollHeight,
			float orthogonalWidth, float orthogonalHeight) {
		if (spr == null) {
			return null;
		}
		ISprite parentSprite = spr.getParent() == null ? spr : spr.getParent();

		Vector2f parentLocal = Vector2f.at(parentSprite.getX() + parentSprite.getOffsetX(),
				parentSprite.getY() + parentSprite.getOffsetY());

		return getMoveTarget(pos, spr, parentLocal, mapSize, scrollWidth, scrollHeight, orthogonalWidth,
				orthogonalHeight);
	}

	public Vector2f getMoveTarget(XY pos, ISprite spr, Vector2f parentLocal, Sized mapSize, float scrollWidth,
			float scrollHeight, float orthogonalWidth, float orthogonalHeight) {
		if (spr == null) {
			return null;
		}
		return getMoveTarget(new TArray<XY>(pos), spr, parentLocal, mapSize, scrollWidth, scrollHeight, orthogonalWidth,
				orthogonalHeight, 0f, 0f);
	}

	public Vector2f getMoveTarget(TArray<XY> targets, ISprite spr, Vector2f parentLocal, Sized mapSize,
			float scrollWidth, float scrollHeight, float orthogonalWidth, float orthogonalHeight) {
		if (spr == null) {
			return null;
		}
		return getMoveTarget(targets, spr, parentLocal, mapSize, scrollWidth, scrollHeight, orthogonalWidth,
				orthogonalHeight, 0f, 0f);
	}

	public Vector2f getMoveTarget(TArray<XY> targets, ISprite spr, Vector2f parentLocal, Sized mapSize,
			float scrollWidth, float scrollHeight, float orthogonalWidth, float orthogonalHeight, float paddingX,
			float paddingY) {

		if (spr == null) {
			return null;
		}

		Vector2f center = Vector2f.at(spr.getX() + spr.getOffsetX(), spr.getY() + spr.getOffsetY());

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
		for (int i = 0, n = _sprs.size; i < n; i++) {
			ISprite spr = _sprs.get(i);
			if (spr != null) {
				moveList.add(getMoveTarget(targets, spr, parentLocal, mapSize, scrollWidth, scrollHeight,
						orthogonalWidth, orthogonalHeight, paddingX, paddingY));
			}
		}
		return moveList;
	}
}
