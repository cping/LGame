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

import loon.LObject;
import loon.LRelease;
import loon.LTexture;
import loon.action.ActionBind;
import loon.action.map.Attribute;
import loon.action.map.Config;
import loon.action.map.Field2D;
import loon.action.map.TileMap;
import loon.canvas.LColor;
import loon.geom.RectBox;
import loon.opengl.GLEx;

public abstract class ActionObject extends LObject<ISprite> implements Config, LRelease,
		ActionBind, ISprite {

	boolean visible = true;

	float scaleX = 1, scaleY = 1;

	public void setScale(final float s) {
		this.setScale(s, s);
	}

	public void setScale(final float sx, final float sy) {
		if (this.scaleX == sx && this.scaleY == sy) {
			return;
		}
		this.scaleX = sx;
		this.scaleY = sy;
	}

	public float getScaleX() {
		return this.scaleX;
	}

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

	public ActionObject(float x, float y, float dw, float dh,
			Animation animation, TileMap map) {
		this.setLocation(x, y);
		this.tiles = map;
		this.animation = animation;
		this.dstWidth = dw;
		this.dstHeight = dh;
		if (dw < 1 && dh < 1) {
			this.rectBox = new RectBox(x, y,
					animation.getSpriteImage().width(), animation
							.getSpriteImage().height());
		} else {
			this.rectBox = new RectBox(x, y, dw, dh);
		}
	}

	public ActionObject(float x, float y, Animation animation, TileMap map) {
		this.setLocation(x, y);
		this.tiles = map;
		this.animation = animation;
		this.dstWidth = animation.getSpriteImage().width();
		this.dstHeight = animation.getSpriteImage().height();
		if (dstWidth < 1 && dstHeight < 1) {
			this.rectBox = new RectBox(x, y,
					animation.getSpriteImage().width(), animation
							.getSpriteImage().height());
		} else {
			this.rectBox = new RectBox(x, y, dstWidth, dstHeight);
		}
	}

	public void draw(SpriteBatch batch, float offsetX, float offsetY) {
		if (!visible) {
			return;
		}
		float tmp = batch.color();
		float alpha = batch.alpha();
		try {
			batch.setAlpha(_alpha);
			batch.setColor(filterColor);
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
							batch.draw(animation.getSpriteImage(), getX()
									+ offsetX, getY() + offsetY, getRotation());
						} else {
							batch.draw(animation.getSpriteImage(), getX()
									+ offsetX, getY() + offsetY, dstWidth,
									dstHeight, getRotation());
						}
					} else {
						if (dstWidth < 1 && dstHeight < 1) {
							batch.draw(animation.getSpriteImage(), getX()
									+ offsetX, getY() + offsetY);
						} else {
							batch.draw(animation.getSpriteImage(), getX()
									+ offsetX, getY() + offsetY, dstWidth,
									dstHeight);
						}
					}
				}
			} else {
				final float width = animation.getSpriteImage().width();
				final float height = animation.getSpriteImage().height();
				if (mirror) {
					if (getRotation() != 0) {
						if (dstWidth < 1 && dstHeight < 1) {
							batch.drawFlipX(animation.getSpriteImage(), getX()
									+ offsetX, getY() + offsetY, width, height,
									scaleX, scaleY, getRotation());
						} else {
							batch.drawFlipX(animation.getSpriteImage(), getX()
									+ offsetX, getY() + offsetY, dstWidth,
									dstHeight, scaleX, scaleY, getRotation());
						}
					} else {
						if (dstWidth < 1 && dstHeight < 1) {
							batch.drawScaleFlipX(animation.getSpriteImage(),
									getX() + offsetX, width, height, getY()
											+ offsetY, scaleX, scaleY);
						} else {
							batch.drawScaleFlipX(animation.getSpriteImage(),
									getX() + offsetX, getY() + offsetY,
									dstWidth, dstHeight, scaleX, scaleY);
						}
					}
				} else {
					if (getRotation() != 0) {
						if (dstWidth < 1 && dstHeight < 1) {
							batch.drawScale(animation.getSpriteImage(), getX()
									+ offsetX, getY() + offsetY, width, height,
									scaleX, scaleY, getRotation());
						} else {
							batch.drawScale(animation.getSpriteImage(), getX()
									+ offsetX, getY() + offsetY, dstWidth,
									dstHeight, scaleX, scaleY, getRotation());
						}
					} else {
						if (dstWidth < 1 && dstHeight < 1) {
							batch.drawScale(animation.getSpriteImage(), getX()
									+ offsetX, getY() + offsetY, width, height,
									scaleX, scaleY);
						} else {
							batch.drawScale(animation.getSpriteImage(), getX()
									+ offsetX, getY() + offsetY, dstWidth,
									dstHeight, scaleX, scaleY);
						}
					}
				}
			}
		} finally {
			batch.setColor(tmp);
			batch.setAlpha(alpha);
		}
	}

	public void draw(GLEx gl, float offsetX, float offsetY) {
		if (!visible) {
			return;
		}
		int tmp = gl.color();
		float alpha = gl.alpha();
		try {
			gl.setAlpha(_alpha);
			if (scaleX == 1 && scaleY == 1) {
				if (mirror) {
					if (getRotation() != 0) {
						if (dstWidth < 1 && dstHeight < 1) {
							gl.drawMirror(animation.getSpriteImage(), getX()
									+ offsetX, getY() + offsetY, filterColor,
									getRotation());
						} else {
							gl.drawMirror(animation.getSpriteImage(), getX()
									+ offsetX, getY() + offsetY, dstWidth,
									dstHeight, filterColor, getRotation());
						}
					} else {
						if (dstWidth < 1 && dstHeight < 1) {
							gl.drawMirror(animation.getSpriteImage(), getX()
									+ offsetX, getY() + offsetY, filterColor, 0);
						} else {
							gl.drawMirror(animation.getSpriteImage(), getX()
									+ offsetX, getY() + offsetY, dstWidth,
									dstHeight, filterColor, 0);
						}
					}
				} else {
					if (getRotation() != 0) {
						if (dstWidth < 1 && dstHeight < 1) {
							gl.draw(animation.getSpriteImage(), getX()
									+ offsetX, getY() + offsetY, filterColor,
									getRotation());
						} else {
							gl.draw(animation.getSpriteImage(), getX()
									+ offsetX, getY() + offsetY, dstWidth,
									dstHeight, filterColor, getRotation());
						}
					} else {
						if (dstWidth < 1 && dstHeight < 1) {
							gl.draw(animation.getSpriteImage(), getX()
									+ offsetX, getY() + offsetY, filterColor);
						} else {
							gl.draw(animation.getSpriteImage(), getX()
									+ offsetX, getY() + offsetY, dstWidth,
									dstHeight, filterColor);
						}
					}
				}
			} else {
				final float width = animation.getSpriteImage().width();
				final float height = animation.getSpriteImage().height();
				if (mirror) {
					if (getRotation() != 0) {
						if (dstWidth < 1 && dstHeight < 1) {
							gl.drawMirrorScale(animation.getSpriteImage(),
									getX() + offsetX, getY() + offsetY, width,
									height, filterColor, scaleX, scaleY,
									getRotation());
						} else {
							gl.drawMirrorScale(animation.getSpriteImage(),
									getX() + offsetX, getY() + offsetY,
									dstWidth, dstHeight, filterColor, scaleX,
									scaleY, getRotation());
						}
					} else {
						if (dstWidth < 1 && dstHeight < 1) {
							gl.drawMirrorScale(animation.getSpriteImage(),
									getX() + offsetX, width, height, getY()
											+ offsetY, filterColor, scaleX,
									scaleY);
						} else {
							gl.drawMirrorScale(animation.getSpriteImage(),
									getX() + offsetX, getY() + offsetY,
									dstWidth, dstHeight, filterColor, scaleX,
									scaleY);
						}
					}
				} else {
					if (getRotation() != 0) {
						if (dstWidth < 1 && dstHeight < 1) {
							gl.drawScale(animation.getSpriteImage(), getX()
									+ offsetX, getY() + offsetY, width, height,
									filterColor, scaleX, scaleY, getRotation());
						} else {
							gl.drawScale(animation.getSpriteImage(), getX()
									+ offsetX, getY() + offsetY, dstWidth,
									dstHeight, filterColor, scaleX, scaleY,
									getRotation());
						}
					} else {
						if (dstWidth < 1 && dstHeight < 1) {
							gl.drawScale(animation.getSpriteImage(), getX()
									+ offsetX, getY() + offsetY, width, height,
									filterColor, scaleX, scaleY);
						} else {
							gl.drawScale(animation.getSpriteImage(), getX()
									+ offsetX, getY() + offsetY, dstWidth,
									dstHeight, filterColor, scaleX, scaleY);
						}
					}
				}
			}
		} finally {
			gl.setColor(tmp);
			gl.setAlpha(alpha);
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

	public void setSize(float width, float height) {
		this.dstWidth = width;
		this.dstHeight = height;
	}

	public void setWidth(float w){
		this.dstWidth = w;
	}
	
	public void setHeight(float h){
		this.dstHeight = h;
	}
	public boolean isCollision(ActionObject o) {
		RectBox src = getCollisionArea();
		RectBox dst = o.getCollisionArea();
		if (src.intersects(dst)) {
			return true;
		}
		return false;
	}

	@Override
	public float getWidth() {
		return (int) ((dstWidth > 1 ? (int) dstWidth : animation
				.getSpriteImage().width()) * scaleX);
	}

	@Override
	public float getHeight() {
		return (int) ((dstHeight > 1 ? (int) dstHeight : animation
				.getSpriteImage().height()) * scaleY);
	}

	public Attribute getAttribute() {
		return attribute;
	}

	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
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
	public boolean inContains(float x, float y, float w, float h) {
		return getCollisionArea().contains(x, y, w, h);
	}

	@Override
	public RectBox getRectBox() {
		return getCollisionArea();
	}

	@Override
	public void setVisible(boolean v) {
		this.visible = v;
	}

	@Override
	public boolean isVisible() {
		return this.visible;
	}

	@Override
	public void createUI(GLEx g) {
		draw(g, 0, 0);
	}

	@Override
	public void createUI(GLEx g, float offsetX, float offsetY) {
		draw(g, offsetX, offsetY);
	}

	@Override
	public RectBox getCollisionBox() {
		return getCollisionArea();
	}

	@Override
	public LTexture getBitmap() {
		return animation.getSpriteImage();
	}

	public void close() {
		if (animation != null) {
			animation.close();
		}
	}

}
