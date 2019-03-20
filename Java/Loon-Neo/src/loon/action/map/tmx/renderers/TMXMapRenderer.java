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
package loon.action.map.tmx.renderers;

import loon.LObject;
import loon.LSystem;
import loon.LTexture;
import loon.LTextures;
import loon.PlayerUtils;
import loon.Screen;
import loon.action.ActionTween;
import loon.action.map.Field2D;
import loon.action.map.tmx.TMXImageLayer;
import loon.action.map.tmx.TMXMap;
import loon.action.map.tmx.TMXMapLayer;
import loon.action.map.tmx.TMXTileLayer;
import loon.action.map.tmx.TMXTileSet;
import loon.action.map.tmx.tiles.TMXAnimationFrame;
import loon.action.map.tmx.tiles.TMXTile;
import loon.action.sprite.ISprite;
import loon.action.sprite.Sprites;
import loon.canvas.LColor;
import loon.geom.RectBox;
import loon.opengl.GLEx;
import loon.utils.ObjectMap;
import loon.utils.TimeUtils;

public abstract class TMXMapRenderer extends LObject<ISprite> implements ISprite {

	protected Sprites sprites = null;

	protected int lastHashCode = 1;

	protected static class TileAnimator {

		private TMXTile tile;

		private int currentFrameIndex;
		private float elapsedDuration;

		public TileAnimator(TMXTile tile) {
			this.tile = tile;
			elapsedDuration = 0;
			currentFrameIndex = 0;
		}

		public void update(long delta) {
			elapsedDuration += TimeUtils.convert(delta, TimeUtils.getDefaultTimeUnit(), TimeUtils.Unit.MILLIS);

			if (elapsedDuration >= tile.getFrames().get(currentFrameIndex).getDuration()) {
				currentFrameIndex = (currentFrameIndex + 1) % tile.getFrames().size;
				elapsedDuration = 0;
			}
		}

		public TMXAnimationFrame getCurrentFrame() {
			return tile.getFrames().get(currentFrameIndex);
		}
	}

	protected abstract void renderTileLayer(GLEx gl, TMXTileLayer tileLayer);

	protected abstract void renderImageLayer(GLEx gl, TMXImageLayer imageLayer);

	protected TMXMap map;
	protected ObjectMap<String, LTexture> textureMap;
	protected ObjectMap<TMXTile, TileAnimator> tileAnimators;

	protected boolean visible;
	protected float scaleX = 1f;
	protected float scaleY = 1f;

	protected LColor baseColor = new LColor(LColor.white);

	public TMXMapRenderer(TMXMap map) {
		this.textureMap = new ObjectMap<String, LTexture>();
		this.tileAnimators = new ObjectMap<TMXTile, TileAnimator>();
		this.visible = true;
		this.map = map;

		for (TMXTileSet tileSet : map.getTileSets()) {
			String path = tileSet.getImage().getSource();
			if (!textureMap.containsKey(path)) {
				textureMap.put(path, LTextures.loadTexture(path));
			}
			for (TMXTile tile : tileSet.getTiles()) {
				if (tile.isAnimated()) {
					TileAnimator animator = new TileAnimator(tile);
					tileAnimators.put(tile, animator);
				}
			}
		}

		for (TMXImageLayer imageLayer : map.getImageLayers()) {
			String path = imageLayer.getImage().getSource();
			if (!textureMap.containsKey(path)) {
				textureMap.put(path, LTextures.loadTexture(path));
			}
		}
	}

	public static TMXMapRenderer create(TMXMap map) {
		switch (map.getOrientation()) {
		case ISOMETRIC:
			return new TMXIsometricMapRenderer(map);
		case ORTHOGONAL:
			return new TMXOrthogonalMapRenderer(map);
		default:
			break;
		}
		throw LSystem
				.runThrow("A TmxMapRenderer has not yet been implemented for " + map.getOrientation() + " orientation");
	}

	public void update(long delta) {
		for (TileAnimator animator : tileAnimators.values()) {
			animator.update(delta);
		}
	}

	protected void renderBackgroundColor(GLEx gl) {
		gl.fillRect(_location.x, _location.y, map.getWidth() * map.getTileWidth(),
				map.getHeight() * map.getTileHeight(), map.getBackgroundColor());
	}

	public void setLocationToTilePosX(int x) {
		setX(x / map.getTileWidth());
	}

	public void setLocationToTilePosY(int y) {
		setY(y / map.getTileHeight());
	}

	public void setColor(LColor c) {
		baseColor.setColor(c);
	}

	public LColor getColor() {
		return baseColor;
	}

	@Override
	public float getWidth() {
		return map.getWidth() * map.getTileWidth();
	}

	@Override
	public float getHeight() {
		return map.getHeight() * map.getTileHeight();
	}

	public void renderImageLayers(GLEx gl, int... layerIDs) {
		if (layerIDs == null || layerIDs.length == 0) {
			for (TMXImageLayer imageLayer : map.getImageLayers())
				renderImageLayer(gl, imageLayer);
		} else {
			for (int layerIndex : layerIDs) {
				if (layerIndex < map.getNumImageLayers()) {
					renderImageLayer(gl, map.getImageLayer(layerIndex));
				}
			}
		}
	}

	public void renderTileLayers(GLEx gl, int... layerIDs) {
		if (layerIDs == null || layerIDs.length == 0) {
			for (TMXTileLayer tileLayer : map.getTileLayers()) {
				renderTileLayer(gl, tileLayer);
			}
		} else {
			for (int layerIndex : layerIDs) {
				if (layerIndex > map.getNumTileLayers()) {
					renderTileLayer(gl, map.getTileLayer(layerIndex));
				}
			}
		}
	}

	public TMXMap getMap() {
		return map;
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void createUI(GLEx g, float offsetX, float offsetY) {
		float tmp = g.alpha();
		float tmpAlpha = baseColor.a;
		int color = g.color();
		g.setAlpha(_alpha);
		baseColor.a = _alpha;
		g.setColor(baseColor);
		renderBackgroundColor(g);
		float ox = getX();
		float oy = getY();
		setLocation(ox + offsetX, oy + offsetY);
		for (TMXMapLayer mapLayer : map.getLayers()) {
			if (mapLayer instanceof TMXTileLayer) {
				renderTileLayer(g, (TMXTileLayer) mapLayer);
			}
			if (mapLayer instanceof TMXImageLayer) {
				renderImageLayer(g, (TMXImageLayer) mapLayer);
			}
		}
		setLocation(ox, oy);
		baseColor.a = tmpAlpha;
		g.setColor(color);
		g.setAlpha(tmp);
	}

	@Override
	public void createUI(GLEx g) {
		float tmp = g.alpha();
		float tmpAlpha = baseColor.a;
		int color = g.color();
		g.setAlpha(_alpha);
		baseColor.a = _alpha;
		g.setColor(baseColor);
		renderBackgroundColor(g);
		for (TMXMapLayer mapLayer : map.getLayers()) {
			if (mapLayer instanceof TMXTileLayer) {
				renderTileLayer(g, (TMXTileLayer) mapLayer);
			}
			if (mapLayer instanceof TMXImageLayer) {
				renderImageLayer(g, (TMXImageLayer) mapLayer);
			}
		}
		baseColor.a = tmpAlpha;
		g.setColor(color);
		g.setAlpha(tmp);
	}

	@Override
	public RectBox getCollisionBox() {
		return getCollisionArea();
	}

	@Override
	public LTexture getBitmap() {
		return null;
	}

	@Override
	public Field2D getField2D() {
		return null;
	}

	@Override
	public float getScaleX() {
		return scaleX;
	}

	@Override
	public float getScaleY() {
		return scaleY;
	}

	@Override
	public void setScale(float sx, float sy) {
		this.scaleX = sx;
		this.scaleY = sy;
	}

	@Override
	public boolean isBounded() {
		return false;
	}

	@Override
	public boolean isContainer() {
		return true;
	}

	@Override
	public boolean inContains(float x, float y, float w, float h) {
		return getCollisionArea().contains(x, y, w, h);
	}

	@Override
	public RectBox getRectBox() {
		return getCollisionArea();
	}

	@Override
	public int hashCode() {
		int result = map.getTileSets().size;
		result = LSystem.unite(result, _location.x);
		result = LSystem.unite(result, _location.y);
		result = LSystem.unite(result, map.getTileHeight());
		result = LSystem.unite(result, map.getTileWidth());
		result = LSystem.unite(result, map.getTileHeight());
		result = LSystem.unite(result, scaleX);
		result = LSystem.unite(result, scaleY);
		result = LSystem.unite(result, _rotation);
		return result;
	}

	@Override
	public ActionTween selfAction() {
		return PlayerUtils.set(this);
	}

	@Override
	public boolean isActionCompleted() {
		return PlayerUtils.isActionCompleted(this);
	}

	@Override
	public void setSprites(Sprites ss) {
		if (this.sprites == ss) {
			return;
		}
		this.sprites = ss;
	}

	@Override
	public Sprites getSprites() {
		return this.sprites;
	}

	@Override
	public Screen getScreen() {
		if (this.sprites == null) {
			return LSystem.getProcess().getScreen();
		}
		return this.sprites.getScreen() == null ? LSystem.getProcess().getScreen() : this.sprites.getScreen();
	}

	@Override
	public void close() {
		visible = false;
		if (textureMap != null) {
			textureMap.clear();
		}
		if (tileAnimators != null) {
			tileAnimators.clear();
		}
		for (LTexture texture : textureMap.values()) {
			texture.close();
		}
		lastHashCode = 1;
		setState(State.DISPOSED);
	}
}
