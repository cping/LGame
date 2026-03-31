/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
package loon.action.map;

import loon.LRelease;
import loon.LSystem;
import loon.action.map.battle.BattleType.RangeType;
import loon.canvas.LColor;
import loon.geom.PointI;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.utils.ISOUtils;
import loon.utils.IntMap;
import loon.utils.MathUtils;
import loon.utils.ObjectMap;
import loon.utils.ObjectMap.Entry;
import loon.utils.timer.Duration;
import loon.utils.SortedList;
import loon.utils.TArray;

/**
 * 斜视地图高亮区域及瓦片高亮特效生成用工具类
 */
public class TileIsoHighlighter implements LRelease {

	// 单个瓦片特效
	public static class TileEffect {

		private final LColor animColor = new LColor();
		public final int x, y;
		private final EffectType effectType;
		public final int priority;
		private float alpha = 0f;
		private boolean fadingIn = true;
		private boolean shouldFadeOut = false;
		private boolean breathingEffect = true;
		private float[] px = new float[4];
		private float[] py = new float[4];
		private LColor flickerBorderColor = LColor.darkYellow;

		public TileEffect(int x, int y, EffectType type) {
			this.x = x;
			this.y = y;
			this.effectType = type;
			this.priority = type.priority;
		}

		public void update(float deltaTime) {
			if (fadingIn) {
				alpha += deltaTime * 2f;
				if (alpha >= 0.35f) {
					alpha = 0.35f;
					fadingIn = false;
				}
			}
			if (shouldFadeOut) {
				alpha -= deltaTime * 2f;
				if (alpha <= 0f) {
					alpha = 0f;
				}
			}
		}

		public void setFade(boolean f) {
			fadingIn = f;
		}

		public boolean isFade() {
			return fadingIn;
		}

		public void setBreathingEffect(boolean b) {
			breathingEffect = b;
		}

		public boolean isBreathingEffect() {
			return breathingEffect;
		}

		public void setFlickerBorderColor(LColor color) {
			this.flickerBorderColor = color;
		}

		public LColor getFlickerBorderColor() {
			return flickerBorderColor;
		}

		public void fadeOut() {
			shouldFadeOut = true;
		}

		public void stopFadeOut() {
			shouldFadeOut = false;
		}

		public void render(GLEx g, float drawX, float drawY, float tileWidth, float tileHeight, float elapsedTime) {
			final int oldColor = g.color();
			float scale = 1.0f;
			float brightness = 1.0f;

			switch (effectType.name) {
			case "move":
				scale = 1.0f + 0.05f * MathUtils.sin(elapsedTime * 2f);
				brightness = 0.9f + 0.1f * MathUtils.sin(elapsedTime * 1.5f);
				break;
			case "attack":
				scale = 1.0f + 0.08f * MathUtils.sin(elapsedTime * 4f);
				brightness = 0.8f + 0.2f * MathUtils.sin(elapsedTime * 3f);
				break;
			case "heal":
				scale = 1.0f + 0.03f * MathUtils.sin(elapsedTime * 2.5f);
				brightness = 1.0f;
				break;
			case "special":
				scale = 1.0f + 0.1f * MathUtils.sin(elapsedTime * 5f);
				brightness = 1.0f + 0.2f * MathUtils.sin(elapsedTime * 4f);
				break;
			case "defense":
				scale = 1.0f + 0.06f * MathUtils.sin(elapsedTime * 3f);
				brightness = 0.85f + 0.15f * MathUtils.sin(elapsedTime * 2f);
				break;
			case "support":
				scale = 1.0f + 0.04f * MathUtils.sin(elapsedTime * 2.5f);
				brightness = 0.9f + 0.1f * MathUtils.sin(elapsedTime * 2f);
				break;
			case "danger":
				scale = 1.0f + 0.07f * MathUtils.sin(elapsedTime * 4.5f);
				brightness = 0.7f + 0.3f * MathUtils.sin(elapsedTime * 5f);
				break;
			case "target":
				scale = 1.0f + 0.09f * MathUtils.sin(elapsedTime * 6f);
				brightness = 0.8f + 0.2f * MathUtils.sin(elapsedTime * 10f);
				break;
			}

			if (!breathingEffect) {
				scale = 1.0f;
			}

			float halfW = (tileWidth / 2f) * scale;
			float halfH = (tileHeight / 4f) * scale;

			px[0] = drawX + tileWidth / 2f;
			py[0] = drawY + halfH;
			px[1] = drawX + tileWidth / 2f + halfW;
			py[1] = drawY + tileHeight / 2f;
			px[2] = drawX + tileWidth / 2f;
			py[2] = drawY + tileHeight - halfH;
			px[3] = drawX + tileWidth / 2f - halfW;
			py[3] = drawY + tileHeight / 2f;

			animColor.setColor(effectType.color.r * brightness, effectType.color.g * brightness,
					effectType.color.b * brightness, alpha);

			g.setColor(animColor);
			g.fillPolygon(px, py, 4);

			if (effectType.flickerBorder) {
				g.setColor((MathUtils.sin(elapsedTime * 3f) > 0) ? flickerBorderColor : animColor.darker());
			} else {
				g.setColor(animColor.darker());
			}
			g.drawPolygon(px, py, 4);

			if (effectType.rippleEffect) {
				float rippleAlpha = 0.2f + 0.2f * MathUtils.sin(elapsedTime * 3f);
				animColor.setColor(effectType.color.r, effectType.color.g, effectType.color.b, rippleAlpha);
				g.setColor(animColor);
				g.drawPolygon(px, py, 4);
			}

			g.setColor(oldColor);
		}

	}

	public static class EffectType {

		public final static EffectType MOVE = new EffectType("move", LColor.blue, 1, 2f, 1.5f, false, false);
		public final static EffectType ATTACK = new EffectType("attack", LColor.red, 3, 4f, 3f, true, false);
		public final static EffectType HEAL = new EffectType("heal", LColor.green, 2, 2.5f, 2f, false, false);
		public final static EffectType SPECIAL = new EffectType("special", LColor.purple, 4, 5f, 4f, true, true);
		public final static EffectType DEFENSE = new EffectType("defense", LColor.cyan, 2, 2f, 1.5f, false, false);
		public final static EffectType SUPPORT = new EffectType("support", LColor.orange, 3, 3f, 2f, false, true);
		public final static EffectType DANGER = new EffectType("danger", LColor.darkRed, 4, 4f, 3f, true, false);
		public final static EffectType TARGET = new EffectType("target", LColor.yellow, 5, 6f, 5f, true, false);
		public final String name;
		public final LColor color;
		public final int priority;
		public final float scaleSpeed;
		public final float brightnessSpeed;
		public final boolean flickerBorder;
		public final boolean rippleEffect;

		public EffectType(String name, LColor color, int priority, float scaleSpeed, float brightnessSpeed,
				boolean flickerBorder, boolean rippleEffect) {
			this.name = name;
			this.color = color;
			this.priority = priority;
			this.scaleSpeed = scaleSpeed;
			this.brightnessSpeed = brightnessSpeed;
			this.flickerBorder = flickerBorder;
			this.rippleEffect = rippleEffect;
		}

	}

	private final IntMap<TileEffect> _effectMap = new IntMap<TileEffect>();

	private float _elapsedTime = 0f;

	private float _speed = 1f;

	private boolean _visible = true;

	public TileIsoHighlighter() {
		this(1f);
	}

	public TileIsoHighlighter(float speed) {
		_visible = true;
		_speed = speed;
	}

	public boolean isVisible() {
		return _visible;
	}

	public TileIsoHighlighter setVisible(boolean v) {
		_visible = v;
		return this;
	}

	public void update(long elapsedTime) {
		update(MathUtils.max(Duration.toS(elapsedTime), LSystem.MIN_SECONE_SPEED_FIXED));
	}

	public void update(float deltaTime) {
		if (!_visible) {
			return;
		}
		float v = deltaTime * _speed;
		_elapsedTime += v;
		for (TileEffect eff : _effectMap) {
			eff.update(v);
		}
	}

	public void updateAllEffect(boolean fadeOut, boolean fadeIn, boolean breath, LColor borderColor) {
		for (TileEffect eff : _effectMap) {
			if (eff != null) {
				eff.shouldFadeOut = fadeOut;
				eff.fadingIn = fadeIn;
				eff.breathingEffect = breath;
				eff.flickerBorderColor = borderColor;
			}
		}
	}

	public TileEffect getEffect(int x, int y) {
		int hashCode = 1;
		hashCode = LSystem.unite(hashCode, x);
		hashCode = LSystem.unite(hashCode, y);
		return _effectMap.get(hashCode);
	}

	public void addEffect(int x, int y, EffectType type) {
		int hashCode = 1;
		hashCode = LSystem.unite(hashCode, x);
		hashCode = LSystem.unite(hashCode, y);
		TileEffect newEff = new TileEffect(x, y, type);
		if (_effectMap.containsKey(hashCode)) {
			TileEffect oldEff = _effectMap.get(hashCode);
			if (newEff.priority > oldEff.priority) {
				_effectMap.put(hashCode, newEff);
			} else if (newEff.priority == oldEff.priority) {
				LColor mixed = new LColor((oldEff.effectType.color.r + newEff.effectType.color.r) / 2,
						(oldEff.effectType.color.g + newEff.effectType.color.g) / 2,
						(oldEff.effectType.color.b + newEff.effectType.color.b) / 2,
						MathUtils.max(oldEff.alpha, newEff.alpha));
				_effectMap.put(hashCode,
						new TileEffect(x, y,
								new EffectType("mixed", mixed, newEff.priority, newEff.effectType.scaleSpeed,
										newEff.effectType.brightnessSpeed, newEff.effectType.flickerBorder,
										newEff.effectType.rippleEffect)));
			}
		} else {
			_effectMap.put(hashCode, newEff);
		}
	}

	public void addEffects(EffectType e, Vector2f... coords) {
		for (Vector2f pos : coords) {
			addEffect(pos.x(), pos.y(), e);
		}
	}

	public void addEffects(ObjectMap<TileIsoHighlighter.EffectType, TArray<Vector2f>> typeToCoords) {
		for (ObjectMap.Entries<TileIsoHighlighter.EffectType, TArray<Vector2f>> entry = typeToCoords.entries(); entry
				.hasNext();) {
			Entry<TileIsoHighlighter.EffectType, TArray<Vector2f>> es = entry.next();
			if (es != null) {
				for (Vector2f pos : es.getValue()) {
					addEffect(pos.x(), pos.y(), es.getKey());
				}
			}
		}
	}

	public void generateMoveRange(int startX, int startY, int movePower, Field2D field) {
		generateMoveRange(startX, startY, movePower, field, true);
	}

	public void generateMoveRange(int startX, int startY, int movePower, Field2D field, boolean allDir) {
		clearEffect();
		SortedList<PointI> queue = new SortedList<PointI>();
		ObjectMap<PointI, Integer> costMap = new ObjectMap<PointI, Integer>();
		PointI start = new PointI(startX, startY);
		queue.add(start);
		costMap.put(start, 0);
		int[][] dirs = null;
		if (allDir) {
			dirs = new int[][] { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 }, { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 } };
		} else {
			dirs = new int[][] { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
		}
		while (!queue.isEmpty()) {
			PointI current = queue.poll();
			int currentCost = costMap.get(current);
			addEffect(current.x, current.y, EffectType.MOVE);
			for (int[] d : dirs) {
				int nx = current.x + d[0];
				int ny = current.y + d[1];
				if (!field.contains(nx, ny)) {
					continue;
				}
				int terrainCost = field.getCost(nx, ny);
				int newCost = currentCost + terrainCost;
				PointI next = new PointI(nx, ny);
				if (newCost <= movePower && (!costMap.containsKey(next) || newCost < costMap.get(next))) {
					costMap.put(next, newCost);
					queue.add(next);
				}
			}
		}
	}

	public void generateRange(int centerX, int centerY, RangeType rangeType, int size, Field2D field,
			EffectType effect) {
		generateRange(centerX, centerY, rangeType, size, field, effect, null, true);
	}

	public void generateRange(int centerX, int centerY, RangeType rangeType, int size, Field2D field, EffectType effect,
			TArray<PointI> paths, boolean allDir) {
		clearEffect();

		final int[][] dirs4 = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
		final int[][] dirs8 = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 }, { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 } };
		final int[][] dirs = allDir ? dirs8 : dirs4;

		switch (rangeType) {
		default:
		case SINGLE:
		case SELF:
			addEffect(centerX, centerY, effect);
			break;
		case ADJACENT:
			for (int[] d : dirs) {
				int nx = centerX + d[0], ny = centerY + d[1];
				if (field.contains(nx, ny)) {
					addEffect(nx, ny, effect);
				}
			}
			break;
		case CROSS:
			for (int i = 1; i <= size; i++) {
				for (int[] d : dirs) {
					int nx = centerX + d[0] * i;
					int ny = centerY + d[1] * i;
					if (field.contains(nx, ny)) {
						addEffect(nx, ny, effect);
					}
				}
			}
			break;
		case DIAMOND:
			for (int i = 1; i <= size; i++) {
				int[][] diagDirs = { { i, i }, { i, -i }, { -i, i }, { -i, -i } };
				for (int[] d : diagDirs) {
					int nx = centerX + d[0], ny = centerY + d[1];
					if (field.contains(nx, ny)) {
						addEffect(nx, ny, effect);
					}
				}
			}
			break;
		case CIRCLE:
			for (int dx = -size; dx <= size; dx++) {
				for (int dy = -size; dy <= size; dy++) {
					if (dx * dx + dy * dy <= size * size) {
						int nx = centerX + dx, ny = centerY + dy;
						if (field.contains(nx, ny)) {
							addEffect(nx, ny, effect);
						}
					}
				}
			}
			break;
		case AOE:
			for (int nx = 0; nx < field.getWidth(); nx++) {
				for (int ny = 0; ny < field.getHeight(); ny++) {
					addEffect(nx, ny, effect);
				}
			}
		case LINE:
			for (int i = 1; i <= size; i++) {
				int nx = centerX + i, ny = centerY;
				if (field.contains(nx, ny)) {
					addEffect(nx, ny, effect);
				}
			}
			break;
		case LINE_AOE:
			for (int i = 1; i <= size; i++) {
				for (int dy = -1; dy <= 1; dy++) {
					int nx = centerX + i, ny = centerY + dy;
					if (field.contains(nx, ny)) {
						addEffect(nx, ny, effect);
					}
				}
			}
			break;
		case SQUARE:
		case AREA:
			for (int dx = -size; dx <= size; dx++) {
				for (int dy = -size; dy <= size; dy++) {
					int nx = centerX + dx, ny = centerY + dy;
					if (field.contains(nx, ny)) {
						addEffect(nx, ny, effect);
					}
				}
			}
			break;

		case GLOBAL:
			for (int x = 0; x < field.getWidth(); x++) {
				for (int y = 0; y < field.getHeight(); y++) {
					addEffect(x, y, effect);
				}
			}
			break;

		case ROW:
			for (int x = 0; x < field.getWidth(); x++) {
				addEffect(x, centerY, effect);
			}
			break;

		case COLUMN:
			for (int y = 0; y < field.getHeight(); y++) {
				addEffect(centerX, y, effect);
			}
			break;

		case RING:
			for (int dx = -size; dx <= size; dx++) {
				for (int dy = -size; dy <= size; dy++) {
					int dist2 = dx * dx + dy * dy;
					if (dist2 <= size * size && dist2 >= (size - 1) * (size - 1)) {
						int nx = centerX + dx, ny = centerY + dy;
						if (field.contains(nx, ny)) {
							addEffect(nx, ny, effect);
						}
					}
				}
			}
			break;

		case SECTOR:
			for (int dx = 0; dx <= size; dx++) {
				for (int dy = -dx; dy <= dx; dy++) {
					int nx = centerX + dx, ny = centerY + dy;
					if (field.contains(nx, ny)) {
						addEffect(nx, ny, effect);
					}
				}
			}
			break;

		case PLUS:
			for (int i = -size; i <= size; i++) {
				addEffect(centerX + i, centerY, effect);
				addEffect(centerX, centerY + i, effect);
			}
			break;

		case CHECKER:
			for (int dx = -size; dx <= size; dx++) {
				for (int dy = -size; dy <= size; dy++) {
					if ((dx + dy) % 2 == 0) {
						int nx = centerX + dx, ny = centerY + dy;
						if (field.contains(nx, ny)) {
							addEffect(nx, ny, effect);
						}
					}
				}
			}
			break;

		case RANDOM:
			for (int i = 0; i < size; i++) {
				int nx = centerX + MathUtils.nextInt(size * 2 + 1) - size;
				int ny = centerY + MathUtils.nextInt(size * 2 + 1) - size;
				if (field.contains(nx, ny)) {
					addEffect(nx, ny, effect);
				}
			}
			break;

		case PATH:
			if (paths != null) {
				for (PointI p : paths) {
					addEffect(p.x, p.y, effect);
				}
			}
			break;
		}
	}

	public void generateRange(EffectType type, int startX, int startY, int minRange, int maxRange, Field2D field) {
		for (int dx = -maxRange; dx <= maxRange; dx++) {
			for (int dy = -maxRange; dy <= maxRange; dy++) {
				int dist = MathUtils.abs(dx) + MathUtils.abs(dy);
				if (dist >= minRange && dist <= maxRange) {
					int nx = startX + dx;
					int ny = startY + dy;
					if (field.contains(nx, ny)) {
						addEffect(nx, ny, type);
					}
				}
			}
		}
	}

	public void generateRadius(EffectType type, int startX, int startY, int radius, Field2D field) {
		for (int dx = -radius; dx <= radius; dx++) {
			for (int dy = -radius; dy <= radius; dy++) {
				int dist = MathUtils.abs(dx) + MathUtils.abs(dy);
				if (dist <= radius) {
					int nx = startX + dx;
					int ny = startY + dy;
					if (field.contains(nx, ny)) {
						addEffect(nx, ny, type);
					}
				}
			}
		}
	}

	public float getSpeed() {
		return _speed;
	}

	public TileIsoHighlighter setSpeed(float v) {
		_speed = v;
		return this;
	}

	public void clearEffect() {
		_effectMap.clear();
		_elapsedTime = 0f;
	}

	public void renderRangeArea(GLEx g, int startX, int startY, int endX, int endY, float tileWidth, float tileHeight,
			float elapsedTime) {
		if (!_visible) {
			return;
		}
		for (int i = startX; i < endX; i++) {
			for (int j = startY; j < endY; j++) {
				float drawX = ISOUtils.getTileToScreenX(i, j, tileWidth, tileHeight);
				float drawY = ISOUtils.getTileToScreenY(i, j, tileWidth, tileHeight);
				int hashCode = 1;
				hashCode = LSystem.unite(hashCode, i);
				hashCode = LSystem.unite(hashCode, j);
				TileEffect eff = _effectMap.get(hashCode);
				if (eff != null) {
					eff.render(g, drawX, drawY, tileWidth, tileHeight, elapsedTime);
				}
			}
		}
	}

	public void renderTileHighlight(GLEx g, int x, int y, float drawX, float drawY, float tileWidth, float tileHeight) {
		if (!_visible) {
			return;
		}
		int hashCode = 1;
		hashCode = LSystem.unite(hashCode, x);
		hashCode = LSystem.unite(hashCode, y);
		TileEffect eff = _effectMap.get(hashCode);
		if (eff != null) {
			eff.render(g, drawX, drawY, tileWidth, tileHeight, _elapsedTime);
		}
	}

	@Override
	public void close() {
		_effectMap.clear();
		_visible = false;
	}
}
