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
package loon.action.map.tmx;

import loon.canvas.LColor;
import loon.geom.RectBox;
import loon.geom.Sized;
import loon.utils.MathUtils;

public class TMXMapLayer implements Sized {

	public enum TmxLayerType {
		TILE, OBJECT, IMAGE
	}

	private LColor tempColor = new LColor();

	protected TMXMap parent;
	protected String name;

	protected LColor tintColor;

	protected int id;

	protected float offsetX;
	protected float offsetY;

	protected float parallaxX;
	protected float parallaxY;

	private float renderOffsetX;
	private float renderOffsetY;

	protected int width;
	protected int height;
	protected int zOrder;
	protected int parseOrder;

	protected int widthInPixels;
	protected int heightInPixels;

	protected float opacity;
	protected boolean visible;
	protected boolean renderOffsetDirty;

	protected float repeatX;
	protected float repeatY;

	protected TmxLayerType type;

	protected TMXProperties properties;

	protected RectBox layerRect;

	public TMXMapLayer(TMXMap parent, String name, float offsetX, float offsetY, int width, int height, float opacity,
			boolean visible, TmxLayerType type) {
		this(parent, 0, name, offsetX, offsetY, width, height, opacity, visible, type);
	}

	public TMXMapLayer(TMXMap parent, int id, String name, float offsetX, float offsetY, int width, int height,
			float opacity, boolean visible, TmxLayerType type) {

		this.parent = parent;
		this.id = id;
		this.name = name;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.width = width;
		this.height = height;
		this.opacity = opacity;
		this.visible = visible;
		this.type = type;
		this.updateRenderOffset();
		if (this.parent != null) {
			widthInPixels = this.parent.getTileWidth() * width;
			heightInPixels = this.parent.getTileHeight() * height;
		}
		properties = new TMXProperties();

	}

	public int getTileWidth() {
		return parent != null ? parent.getTileWidth() : 0;
	}

	public int getTileHeight() {
		return parent != null ? parent.getTileHeight() : 0;
	}

	public TMXMapLayer updateRenderOffset() {
		renderOffsetDirty = true;
		return this;
	}

	public boolean isDirty() {
		return renderOffsetDirty;
	}

	public TMXMap getMap() {
		return parent;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	@Override
	public float getX() {
		return getOffsetX();
	}

	@Override
	public float getY() {
		return getOffsetY();
	}

	public float getOffsetX() {
		return offsetX;
	}

	public float getOffsetY() {
		return offsetY;
	}

	public TMXMapLayer setOffsetX(float offsetX) {
		this.offsetX = offsetX;
		updateRenderOffset();
		return this;
	}

	public TMXMapLayer setOffsetY(float offsetY) {
		this.offsetY = offsetY;
		updateRenderOffset();
		return this;
	}

	public float getParallaxX() {
		return parallaxX;
	}

	public TMXMapLayer setParallaxX(float x) {
		this.parallaxX = x;
		return this;
	}

	public float getParallaxY() {
		return parallaxY;
	}

	public TMXMapLayer setParallaxY(float y) {
		this.parallaxY = y;
		return this;
	}

	public float getRepeatX() {
		return repeatX;
	}

	public float getRepeatY() {
		return repeatY;
	}

	public boolean isRepeatX() {
		return !MathUtils.isEqual(repeatX, 0f);
	}

	public boolean isRepeatY() {
		return !MathUtils.isEqual(repeatY, 0f);
	}

	public boolean isRepeat() {
		return isRepeatX() || isRepeatY();
	}

	public boolean isNotRepeat() {
		return !isRepeat();
	}

	public float getRenderOffsetX() {
		if (renderOffsetDirty) {
			calcRenderOffsets();
		}
		return renderOffsetX;
	}

	public float getRenderOffsetY() {
		if (renderOffsetDirty) {
			calcRenderOffsets();
		}
		return renderOffsetY;
	}

	protected void calcRenderOffsets() {
		if (parent != null) {
			parent.calcRenderOffsets();
			renderOffsetX = parent.getRenderOffsetX() + offsetX;
			renderOffsetY = parent.getRenderOffsetY() + offsetY;
		} else {
			renderOffsetX = offsetX;
			renderOffsetY = offsetY;
		}
		renderOffsetDirty = false;
	}

	public RectBox getLayerRect() {
		calcRenderOffsets();
		if (layerRect == null) {
			layerRect = new RectBox(offsetX, offsetY, widthInPixels, heightInPixels);
		} else {
			layerRect.setBounds(offsetX, offsetY, widthInPixels, heightInPixels);
		}
		return layerRect;
	}

	public int getDefaultRepeatIntX() {
		if (parent == null) {
			return 0;
		}
		return MathUtils.ceil((parent.getWidthInPixels() / widthInPixels) + 4);
	}

	public int getDefaultRepeatIntY() {
		if (parent == null) {
			return 0;
		}
		return MathUtils.ceil((parent.getHeightInPixels() / heightInPixels) + 4);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getWidthInPixels() {
		return widthInPixels;
	}

	public int getHeightInPixels() {
		return heightInPixels;
	}

	public int getZOrder() {
		return zOrder;
	}

	public TMXMapLayer setZOrder(int z) {
		this.zOrder = z;
		return this;
	}

	public int getParseOrder() {
		return parseOrder;
	}

	public float getOpacity() {
		return opacity;
	}

	public boolean isVisible() {
		return visible;
	}

	public LColor getTileLayerColor(LColor baseColor) {
		if (tempColor.equals(baseColor) && (opacity == 1f) && (tintColor == null || tintColor.equals(LColor.white))) {
			return baseColor;
		}
		float alpha = opacity;
		if (alpha <= 0f) {
			alpha = 0f;
		}
		if (alpha > 1f) {
			alpha = 1f;
		}
		tempColor.setColor(baseColor);
		if (tintColor == null) {
			return tempColor.mulSelfAlpha(alpha);
		}
		return tempColor.mulSelfAlpha(alpha).mul(tintColor);
	}

	public LColor getTintColor() {
		return tintColor;
	}

	public TMXMapLayer setTintColor(LColor c) {
		this.tintColor = c;
		return this;
	}

	public TmxLayerType getTypeCode() {
		return type;
	}

	public TMXProperties getProperties() {
		return properties;
	}

	@Override
	public int getZ() {
		return getZOrder();
	}

	@Override
	public float left() {
		return getX();
	}

	@Override
	public float top() {
		return getY();
	}

	@Override
	public float right() {
		return getWidth();
	}

	@Override
	public float bottom() {
		return getHeight();
	}

}
