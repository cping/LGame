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
package loon.action.sprite;

import java.util.Comparator;
import java.util.Iterator;

import loon.LSystem;
import loon.LTexture;
import loon.action.map.Direction;
import loon.action.map.battle.BattleType.ObjectState;
import loon.canvas.LColor;
import loon.opengl.GLEx;
import loon.utils.IntArray;
import loon.utils.IntMap;
import loon.utils.MathUtils;
import loon.utils.ObjectMap;
import loon.utils.TArray;
import loon.utils.timer.Duration;

/**
 * AnimationManager的专属渲染器
 */
public class AnimationRenderer extends Entity {

	public static class RenderData {
		AnimationManager animManager;
		float x, y;
		float scale;

		private final ObjectMap<String, float[]> animationSizes = new ObjectMap<String, float[]>();

		RenderData(AnimationManager animManager, float x, float y, float scale) {
			this.animManager = animManager;
			this.x = x;
			this.y = y;
			this.scale = scale;
		}

		public float getWidth() {
			LTexture frame = animManager.getCurrentFrame();
			return frame != null ? frame.getWidth() * scale : 0;
		}

		public float getHeight() {
			LTexture frame = animManager.getCurrentFrame();
			return frame != null ? frame.getHeight() * scale : 0;
		}

		public AnimationManager playAnimation(int layerIndex, ObjectState state, Direction dir) {
			return animManager.play(layerIndex, state, dir);
		}

		public void setAnimationSize(String animationName, float width, float height) {
			animationSizes.put(animationName, new float[] { width, height });
		}
	}

	private final static Comparator<RenderData> sortRenderEntry = new Comparator<RenderData>() {

		@Override
		public int compare(RenderData o1, RenderData o2) {
			return MathUtils.compare(o1.y, o2.y);
		}
	};

	private final IntMap<TArray<RenderData>> layers = new IntMap<TArray<RenderData>>();

	private IntArray cachedSortedLayers = new IntArray();
	private boolean layersDirty = true;

	public AnimationRenderer(float x, float y, float w, float h) {
		super();
		this.setRepaint(true);
		this.setLocation(x, y);
		this.setSize(w, h);
	}

	public void addCharacter(AnimationManager animManager, float x, float y) {
		this.addCharacter(animManager, x, y, 1f, 0);
	}

	public void addCharacter(AnimationManager animManager, float x, float y, float scale, int layerIndex) {
		TArray<RenderData> list = layers.get(layerIndex);
		if (list == null) {
			list = new TArray<RenderData>();
			layers.put(layerIndex, list);
		}
		list.add(new RenderData(animManager, x, y, scale));
		list.sort(sortRenderEntry);
		layersDirty = true;
	}

	public void removeCharacter(AnimationManager animManager) {
		for (Iterator<TArray<RenderData>> it = layers.iterator(); it.hasNext();) {
			TArray<RenderData> entries = it.next();
			for (int i = 0; i < entries.size(); i++) {
				RenderData data = entries.get(i);
				if (data.animManager == animManager) {
					entries.remove(data);
					i--;
					layersDirty = true;
				}
			}
		}
	}

	public void playCharacterAnimation(AnimationManager animManager, int layerIndex, ObjectState state, Direction dir) {
		for (Iterator<TArray<RenderData>> it = layers.iterator(); it.hasNext();) {
			TArray<RenderData> entries = it.next();
			for (RenderData entry : entries) {
				if (entry.animManager == animManager) {
					entry.playAnimation(layerIndex, state, dir);
					return;
				}
			}
		}
	}

	public void setCharacterAnimationSize(AnimationManager animManager, String animationName, float width,
			float height) {
		for (Iterator<TArray<RenderData>> it = layers.iterator(); it.hasNext();) {
			TArray<RenderData> entries = it.next();
			for (RenderData entry : entries) {
				if (entry.animManager == animManager) {
					entry.setAnimationSize(animationName, width, height);
					return;
				}
			}
		}
	}

	@Override
	public AnimationRenderer resume() {
		super.resume();
		for (Iterator<TArray<RenderData>> it = layers.iterator(); it.hasNext();) {
			TArray<RenderData> entries = it.next();
			for (RenderData entry : entries) {
				if (entry.animManager != null) {
					entry.animManager.resume();
				}
			}
		}
		return this;
	}

	@Override
	public AnimationRenderer pause() {
		super.pause();
		for (Iterator<TArray<RenderData>> it = layers.iterator(); it.hasNext();) {
			TArray<RenderData> entries = it.next();
			for (RenderData entry : entries) {
				if (entry.animManager != null) {
					entry.animManager.pause();
				}
			}
		}
		return this;
	}

	@Override
	protected void repaint(GLEx g, float offsetX, float offsetY) {
		if (_destroyed) {
			return;
		}
		float drawX = drawX(offsetX);
		float drawY = drawY(offsetY);
		IntArray sortedLayers = getSortedLayers();
		for (int i = 0; i < sortedLayers.size(); i++) {
			int layerIndex = sortedLayers.get(i);
			TArray<RenderData> entries = layers.get(layerIndex);
			if (entries == null) {
				continue;
			}
			for (int j = 0; j < entries.size(); j++) {
				RenderData entry = entries.get(j);
				LTexture frame = entry.animManager.getCurrentFrame();
				if (frame != null) {
					g.draw(frame, entry.x + drawX, entry.y + drawY, MathUtils.min(getWidth(), frame.getWidth()),
							MathUtils.min(getHeight(), frame.getHeight()), _baseColor, 0f, entry.scale, entry.scale,
							false, false);
				}
			}
		}
	}

	public void draw(GLEx g, float x, float y, LColor color, boolean flipX, boolean flipY) {
		draw(g, x, y, 0f, 0f, color, flipX, flipY, 0f);
	}

	public void draw(GLEx g, float x, float y, float w, float h, LColor color, boolean flipX, boolean flipY,
			float rotation) {
		if (_destroyed) {
			return;
		}
		IntArray sortedLayers = getSortedLayers();
		for (int i = 0; i < sortedLayers.size(); i++) {
			int layerIndex = sortedLayers.get(i);
			TArray<RenderData> entries = layers.get(layerIndex);
			if (entries == null) {
				continue;
			}
			for (int j = 0; j < entries.size(); j++) {
				RenderData entry = entries.get(j);
				LTexture frame = entry.animManager.getCurrentFrame();
				if (frame != null) {
					g.draw(frame, entry.x + x, entry.y + y, w >= 0 ? w : frame.getWidth(),
							h >= 0 ? h : frame.getHeight(), color, rotation, entry.scale, entry.scale, flipX, flipY);
				}
			}
		}
	}

	@Override
	public void onProcess(long elapsedTime) {
		this.update(MathUtils.max(Duration.toS(elapsedTime), LSystem.MIN_SECONE_SPEED_FIXED));
	}

	public void update(float deltaTime) {
		for (Iterator<TArray<RenderData>> it = layers.iterator(); it.hasNext();) {
			TArray<RenderData> entries = it.next();
			for (int i = 0; i < entries.size(); i++) {
				RenderData entry = entries.get(i);
				if (entry != null) {
					entry.animManager.update(deltaTime);
				}
			}
			entries.sort(sortRenderEntry);
		}
	}

	private boolean checkCollision(RenderData a, RenderData b) {
		LTexture fa = a.animManager.getCurrentFrame();
		LTexture fb = b.animManager.getCurrentFrame();
		if (fa == null || fb == null) {
			return false;
		}
		float ax2 = a.x + a.getWidth();
		float ay2 = a.y + a.getHeight();
		float bx2 = b.x + b.getWidth();
		float by2 = b.y + b.getHeight();
		return !(ax2 < b.x || bx2 < a.x || ay2 < b.y || by2 < a.y);
	}

	public void resolveCollisions() {
		for (Iterator<TArray<RenderData>> it = layers.iterator(); it.hasNext();) {
			TArray<RenderData> entries = it.next();
			for (int i = 0; i < entries.size(); i++) {
				for (int j = i + 1; j < entries.size(); j++) {
					RenderData a = entries.get(i);
					RenderData b = entries.get(j);
					if (checkCollision(a, b)) {
						float overlapX = (a.x + a.getWidth()) - b.x;
						float overlapY = (a.y + a.getHeight()) - b.y;
						if (overlapX < overlapY) {
							a.x -= overlapX / 2f;
							b.x += overlapX / 2f;
						} else {
							a.y -= overlapY / 2f;
							b.y += overlapY / 2f;
						}
					}
				}
			}
		}
	}

	private IntArray getSortedLayers() {
		if (layersDirty) {
			if (cachedSortedLayers == null) {
				cachedSortedLayers = new IntArray(layers.keys());
			} else {
				cachedSortedLayers.clear();
				cachedSortedLayers.addAll(layers.keys());
			}
			cachedSortedLayers.sort();
			layersDirty = false;
		}
		return cachedSortedLayers;
	}

	@Override
	protected void _onDestroy() {
		super._onDestroy();
		for (Iterator<TArray<RenderData>> it = layers.iterator(); it.hasNext();) {
			TArray<RenderData> entries = it.next();
			for (RenderData entry : entries) {
				entry.animManager.close();
			}
		}
		layers.clear();
	}
}
