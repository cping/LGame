package loon.action.sprite;

import loon.action.ActionBind;
import loon.action.ActionTween;
import loon.action.map.Field2D;
import loon.canvas.LColor;
import loon.stage.PlayerUtils;
import loon.utils.CollectionUtils;
import loon.utils.MathUtils;
import loon.utils.ObjectMap;
import loon.utils.TArray;

public class SpriteControls {

	public static float getChildrenHeight(Sprites s) {
		float totalHeight = 0;
		ISprite[] list = s.getSprites();
		for (int i = 0; i < list.length; i++) {
			totalHeight += list[i].getHeight();
		}
		return totalHeight;
	}

	public static float getChildrenWidth(Sprites s) {
		float totalWidth = 0;
		ISprite[] list = s.getSprites();
		for (int i = 0; i < list.length; i++) {
			totalWidth += list[i].getWidth();
		}
		return totalWidth;
	}

	public static float getMaxChildHeight(Sprites s) {
		int maxHeight = 0;
		ISprite[] list = s.getSprites();
		for (int i = 0; i < list.length; i++) {
			maxHeight = MathUtils.max(maxHeight, (int) list[i].getHeight());
		}
		return maxHeight;
	}

	public static int getMaxChildWidth(Sprites s) {
		int maxWidth = 0;
		ISprite[] list = s.getSprites();
		for (int i = 0; i < list.length; i++) {
			maxWidth = MathUtils.max(maxWidth, (int) list[i].getWidth());
		}
		return maxWidth;
	}

	private ObjectMap<ActionBind, ActionTween> tweens = new ObjectMap<ActionBind, ActionTween>(
			CollectionUtils.INITIAL_CAPACITY);

	private final TArray<Sprite> _sprs;

	public SpriteControls(Sprite... comps) {
		this();
		add(comps);
	}

	public SpriteControls(TArray<Sprite> comps) {
		this();
		add(comps);
	}
	
	public SpriteControls() {
		this._sprs = new TArray<Sprite>();
	}

	public SpriteControls add(Sprite comp) {
		if (comp == null) {
			throw new IllegalArgumentException("Sprite cannot be null.");
		}
		_sprs.add(comp);
		return this;
	}

	public SpriteControls add(TArray<Sprite> comps) {
		if (comps == null) {
			throw new IllegalArgumentException("Sprites cannot be null.");
		}
		_sprs.addAll(comps);
		return this;
	}

	public SpriteControls remove(Sprite comp) {
		if (comp == null) {
			throw new IllegalArgumentException("Sprite cannot be null.");
		}
		_sprs.remove(comp);
		return this;
	}

	public SpriteControls add(Sprite... comps) {
		if (comps == null) {
			throw new IllegalArgumentException("Sprites cannot be null.");
		}
		for (int i = 0, n = comps.length; i < n; i++) {
			add(comps[i]);
		}
		return this;
	}

	public SpriteControls remove(Sprite... comps) {
		if (comps == null) {
			throw new IllegalArgumentException("Sprites cannot be null.");
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
			Sprite comp = _sprs.get(i);
			if (comp != null) {
				comp.setSize(w, h);
			}
		}
		return this;
	}

	public SpriteControls setFilterColor(LColor filterColor) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			Sprite comp = _sprs.get(i);
			if (comp != null) {
				comp.setFilterColor(filterColor);
			}
		}
		return this;
	}

	public SpriteControls setRunning(boolean running) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			Sprite comp = _sprs.get(i);
			if (comp != null) {
				comp.setRunning(running);
			}
		}
		return this;
	}

	public SpriteControls setAlpha(float alpha) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			Sprite comp = _sprs.get(i);
			if (comp != null) {
				comp.setAlpha(alpha);
			}
		}
		return this;
	}

	public SpriteControls setScale(float s) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			Sprite comp = _sprs.get(i);
			if (comp != null) {
				comp.setScale(s);
			}
		}
		return this;
	}

	public SpriteControls setScale(float sx, float sy) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			Sprite comp = _sprs.get(i);
			if (comp != null) {
				comp.setScale(sx, sy);
			}
		}
		return this;
	}

	public SpriteControls setX(float x) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			Sprite comp = _sprs.get(i);
			if (comp != null) {
				comp.setX(x);
			}
		}
		return this;
	}

	public SpriteControls setY(float y) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			Sprite comp = _sprs.get(i);
			if (comp != null) {
				comp.setY(y);
			}
		}
		return this;
	}

	public SpriteControls setWidth(int w) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			Sprite comp = _sprs.get(i);
			if (comp != null) {
				comp.setWidth(w);
			}
		}
		return this;
	}

	public SpriteControls setHeight(int h) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			Sprite comp = _sprs.get(i);
			if (comp != null) {
				comp.setHeight(h);
			}
		}
		return this;
	}

	public SpriteControls setRotation(float r) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			Sprite comp = _sprs.get(i);
			if (comp != null) {
				comp.setRotation(r);
			}
		}
		return this;
	}

	public SpriteControls setLocation(float dx, float dy) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			Sprite comp = _sprs.get(i);
			if (comp != null) {
				comp.setLocation(dx, dy);
			}
		}
		return this;
	}

	public SpriteControls setLayer(int z) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			Sprite comp = _sprs.get(i);
			if (comp != null) {
				comp.setLayer(z);
			}
		}
		return this;
	}

	public SpriteControls setVisible(boolean v) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			Sprite comp = _sprs.get(i);
			if (comp != null) {
				comp.setVisible(v);
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
			Sprite comp = _sprs.get(i);
			if (comp != null) {
				ActionTween tween = tweens.get(comp);
				if (comp.getAlpha() >= 255) {
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

	public SpriteControls fadeIn(float speed) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			Sprite comp = _sprs.get(i);
			if (comp != null) {
				ActionTween tween = tweens.get(comp);
				if (comp.getAlpha() <= 0) {
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

	public SpriteControls moveBy(float endX, float endY, int speed) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			Sprite comp = _sprs.get(i);
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

	public SpriteControls moveBy(float endX, float endY) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			Sprite comp = _sprs.get(i);
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

	public SpriteControls moveTo(float endX, float endY, int speed) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			Sprite comp = _sprs.get(i);
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

	public SpriteControls moveTo(float endX, float endY, boolean flag, int speed) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			Sprite comp = _sprs.get(i);
			if (comp != null) {
				ActionTween tween = tweens.get(comp);
				if (tween == null) {
					tween = PlayerUtils.set(comp).moveTo(endX, endY, flag,
							speed);
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

	public SpriteControls moveTo(Field2D map, float endX, float endY, boolean flag,
			int speed) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			Sprite comp = _sprs.get(i);
			if (comp != null) {
				ActionTween tween = tweens.get(comp);
				if (tween == null) {
					tween = PlayerUtils.set(comp).moveTo(map, endX, endY, flag,
							speed);
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

	public SpriteControls delay(float d) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			Sprite comp = _sprs.get(i);
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

	public SpriteControls rotateTo(float angle) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			Sprite comp = _sprs.get(i);
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

	public SpriteControls rotateTo(float angle, float speed) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			Sprite comp = _sprs.get(i);
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

	public SpriteControls scaleTo(float sx, float sy) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			Sprite comp = _sprs.get(i);
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

	public SpriteControls scaleTo(float sx, float sy, float speed) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			Sprite comp = _sprs.get(i);
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

	public SpriteControls showTo(boolean v) {
		for (int i = 0, n = _sprs.size; i < n; i++) {
			Sprite comp = _sprs.get(i);
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


}
