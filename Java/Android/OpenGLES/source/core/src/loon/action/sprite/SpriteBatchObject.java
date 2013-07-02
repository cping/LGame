/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package loon.action.sprite;

import loon.action.ActionBind;
import loon.action.map.Attribute;
import loon.action.map.Config;
import loon.action.map.Field2D;
import loon.action.map.TileMap;
import loon.core.LObject;
import loon.core.LRelease;
import loon.core.geom.RectBox;
import loon.core.graphics.LColor;

public abstract class SpriteBatchObject extends LObject implements Config,
		LRelease, ActionBind {

	float scaleX = 1, scaleY = 1;

	public void setScale(final float s) {
		this.setScale(s, s);
	}

	@Override
	public void setScale(final float sx, final float sy) {
		if (this.scaleX == sx && this.scaleY == sy) {
			return;
		}
		this.scaleX = sx;
		this.scaleY = sy;
	}

	@Override
	public float getScaleX() {
		return this.scaleX;
	}

	@Override
	public float getScaleY() {
		return this.scaleY;
	}

	protected Attribute attribute;

	protected Animation animation;

	protected TileMap tiles;

	protected RectBox rectBox;

	protected float dstWidth, dstHeight;

	protected boolean mirror;

	private LColor filterColor = new LColor(1f, 1f, 1f, 1f);

	public SpriteBatchObject(float x, float y, float dw, float dh,
			Animation animation, TileMap map) {
		this.setLocation(x, y);
		this.tiles = map;
		this.animation = animation;
		this.dstWidth = dw;
		this.dstHeight = dh;
		if (dw < 1 && dh < 1) {
			this.rectBox = new RectBox(x, y, animation.getSpriteImage()
					.getWidth(), animation.getSpriteImage().getHeight());
		} else {
			this.rectBox = new RectBox(x, y, dw, dh);
		}
	}
	
	public SpriteBatchObject(float x, float y, 
			Animation animation, TileMap map) {
		this.setLocation(x, y);
		this.tiles = map;
		this.animation = animation;
		this.dstWidth = animation.getSpriteImage().getWidth();
		this.dstHeight = animation.getSpriteImage().getHeight();
		if (dstWidth < 1 && dstHeight < 1) {
			this.rectBox = new RectBox(x, y, animation.getSpriteImage()
					.getWidth(), animation.getSpriteImage().getHeight());
		} else {
			this.rectBox = new RectBox(x, y, dstWidth, dstHeight);
		}
	}
	
	public void draw(SpriteBatch batch, float offsetX, float offsetY) {
		if (alpha != 1f) {
			batch.setAlpha(alpha);
		}
		if (!filterColor.equals(1f, 1f, 1f, 1f)) {
			batch.setColor(filterColor);
		}
		if (scaleX == 1 && scaleY == 1) {
			if (mirror) {
				if (getRotation() != 0) {
					if (dstWidth < 1 && dstHeight < 1) {
						batch.drawFlipX(animation.getSpriteImage(), getX()
								+ offsetX, getY() + offsetY, getRotation());
					} else {
						batch.drawFlipX(animation.getSpriteImage(), getX()
								+ offsetX, getY() + offsetY, dstWidth,
								dstHeight, getRotation());
					}
				} else {
					if (dstWidth < 1 && dstHeight < 1) {
						batch.drawFlipX(animation.getSpriteImage(), getX()
								+ offsetX, getY() + offsetY);
					} else {
						batch.drawFlipX(animation.getSpriteImage(), getX()
								+ offsetX, getY() + offsetY, dstWidth,
								dstHeight);
					}
				}
			} else {
				if (getRotation() != 0) {
					if (dstWidth < 1 && dstHeight < 1) {
						batch.draw(animation.getSpriteImage(),
								getX() + offsetX, getY() + offsetY,
								getRotation());
					} else {
						batch.draw(animation.getSpriteImage(),
								getX() + offsetX, getY() + offsetY, dstWidth,
								dstHeight, getRotation());
					}
				} else {
					if (dstWidth < 1 && dstHeight < 1) {
						batch.draw(animation.getSpriteImage(),
								getX() + offsetX, getY() + offsetY);
					} else {
						batch.draw(animation.getSpriteImage(),
								getX() + offsetX, getY() + offsetY, dstWidth,
								dstHeight);
					}
				}
			}
		} else {
			final float width = animation.getSpriteImage().getWidth();
			final float height = animation.getSpriteImage().getHeight();
			if (mirror) {
				if (getRotation() != 0) {
					if (dstWidth < 1 && dstHeight < 1) {
						batch.drawFlipX(animation.getSpriteImage(), getX()
								+ offsetX, getY() + offsetY, width * scaleX,
								height * scaleY, getRotation());
					} else {
						batch.drawFlipX(animation.getSpriteImage(), getX()
								+ offsetX, getY() + offsetY, dstWidth * scaleX,
								dstHeight * scaleY, getRotation());
					}
				} else {
					if (dstWidth < 1 && dstHeight < 1) {
						batch.drawFlipX(animation.getSpriteImage(), getX()
								+ offsetX, width * scaleX, height * scaleY,
								getY() + offsetY);
					} else {
						batch.drawFlipX(animation.getSpriteImage(), getX()
								+ offsetX, getY() + offsetY, dstWidth * scaleX,
								dstHeight * scaleY);
					}
				}
			} else {
				if (getRotation() != 0) {
					if (dstWidth < 1 && dstHeight < 1) {
						batch.draw(animation.getSpriteImage(),
								getX() + offsetX, getY() + offsetY, width
										* scaleX, height * scaleY,
								getRotation());
					} else {
						batch.draw(animation.getSpriteImage(),
								getX() + offsetX, getY() + offsetY, dstWidth
										* scaleX, dstHeight * scaleY,
								getRotation());
					}
				} else {
					if (dstWidth < 1 && dstHeight < 1) {
						batch.draw(animation.getSpriteImage(),
								getX() + offsetX, getY() + offsetY, width
										* scaleX, height * scaleY);
					} else {
						batch.draw(animation.getSpriteImage(),
								getX() + offsetX, getY() + offsetY, dstWidth
										* scaleX, dstHeight * scaleY);
					}
				}
			}
		}
		if (alpha != 1f || !filterColor.equals(1f, 1f, 1f, 1f)) {
			batch.resetColor();
		}
	}

	public TileMap getTileMap() {
		return tiles;
	}

	@Override
	public Field2D getField2D() {
		return tiles.getField();
	}

	public void setFilterColor(LColor f) {
		this.filterColor.setColor(f);
	}

	public LColor getFilterColor() {
		return this.filterColor;
	}

	public void setSize(int width, int height) {
		this.dstWidth = width;
		this.dstHeight = height;
	}

	public boolean isCollision(SpriteBatchObject o) {
		RectBox src = getCollisionArea();
		RectBox dst = o.getCollisionArea();
		if (src.intersects(dst)) {
			return true;
		}
		return false;
	}

	@Override
	public int getWidth() {
		return (int) ((dstWidth > 1 ? (int) dstWidth : animation
				.getSpriteImage().getWidth()) * scaleX);
	}

	@Override
	public int getHeight() {
		return (int) ((dstHeight > 1 ? (int) dstHeight : animation
				.getSpriteImage().getHeight()) * scaleY);
	}

	public Attribute getAttribute() {
		return attribute;
	}

	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}

	@Override
	public void dispose() {
		if (animation != null) {
			animation.dispose();
		}
	}

	public Animation getAnimation() {
		return animation;
	}

	public void setAnimation(Animation a) {
		this.animation = a;
	}

	public void setIndex(int index) {
		if (animation instanceof AnimationStorage) {
			((AnimationStorage) animation).playIndex(index);
		}
	}

	public boolean isMirror() {
		return mirror;
	}

	public void setMirror(boolean mirror) {
		this.mirror = mirror;
	}

	@Override
	public boolean isBounded() {
		return false;
	}

	@Override
	public boolean isContainer() {
		return false;
	}

	@Override
	public boolean inContains(int x, int y, int w, int h) {
		return false;
	}

	@Override
	public RectBox getRectBox() {
		return getCollisionArea();
	}

	@Override
	public int getContainerWidth() {
		return 0;
	}

	@Override
	public int getContainerHeight() {
		return 0;
	}
}
