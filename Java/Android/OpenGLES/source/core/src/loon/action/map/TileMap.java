package loon.action.map;

import java.io.IOException;
import java.util.ArrayList;

import loon.action.sprite.Animation;
import loon.action.sprite.ISprite;
import loon.action.sprite.SpriteBatch;
import loon.core.LObject;
import loon.core.LRelease;
import loon.core.LSystem;
import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
import loon.core.graphics.LImage;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTexturePack;
import loon.core.graphics.opengl.LTexture.Format;
import loon.utils.MathUtils;


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
public class TileMap extends LObject implements ISprite, LRelease {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2419037580406911982L;

	public static interface DrawListener {

		public void update(long elapsedTime);

		public void draw(GLEx g, float x, float y);

	}

	public static class Tile {

		int id;

		int imgId;

		public Attribute attribute;

		boolean isAnimation;

		public Animation animation;

	}

	private int firstTileX;

	private int firstTileY;

	private int lastTileX;

	private int lastTileY;

	public DrawListener listener;

	private LTexturePack imgPack;

	private ArrayList<TileMap.Tile> arrays = new ArrayList<TileMap.Tile>(10);

	private ArrayList<Animation> animations = new ArrayList<Animation>();

	private final int maxWidth, maxHeight;

	private final Field2D field;

	private float lastOffsetX, lastOffsetY;

	private Vector2f offset;

	private Format format;

	private boolean active, dirty;

	private boolean visible;

	private boolean playAnimation;

	public TileMap(String fileName, int tileWidth, int tileHeight)
			throws IOException {
		this(fileName, tileWidth, tileHeight, LSystem.screenRect.width,
				LSystem.screenRect.height, Format.LINEAR);
	}

	public TileMap(String fileName, int tileWidth, int tileHeight, int mWidth,
			int mHeight, Format format) throws IOException {
		this(TileMapConfig.loadAthwartArray(fileName), tileWidth, tileHeight,
				mWidth, mHeight, format);
	}

	public TileMap(int[][] maps, int tileWidth, int tileHeight, int mWidth,
			int mHeight, Format format) {
		this(new Field2D(maps, tileWidth, tileHeight), mWidth, mHeight, format);
	}

	public TileMap(Field2D field) {
		this(field, LSystem.screenRect.width, LSystem.screenRect.height,
				Format.LINEAR);
	}

	public TileMap(Field2D field, Format format) {
		this(field, LSystem.screenRect.width, LSystem.screenRect.height, format);
	}

	public TileMap(Field2D field, int mWidth, int mHeight, Format format) {
		this.field = field;
		this.maxWidth = mWidth;
		this.maxHeight = mHeight;
		this.offset = new Vector2f(0, 0);
		this.imgPack = new LTexturePack();
		this.format = format;
		this.lastOffsetX = -1;
		this.lastOffsetY = -1;
		this.active = true;
		this.dirty = true;
		this.visible = true;
		imgPack.setFormat(format);
	}

	public static TileMap loadCharsMap(String resName, int tileWidth,
			int tileHeight) {
		return new TileMap(TileMapConfig.loadCharsField(resName, tileWidth,
				tileHeight));
	}

	public void setImagePack(String file) {
		if (imgPack != null) {
			imgPack.dispose();
			imgPack = null;
		}
		this.active = false;
		this.dirty = true;
		imgPack = new LTexturePack(file);
		imgPack.packed(format);
	}

	public void removeTile(int id) {
		for (Tile tile : arrays) {
			if (tile.id == id) {
				if (tile.isAnimation) {
					animations.remove(tile.animation);
				}
				arrays.remove(tile);
			}
		}
		if (animations.size() == 0) {
			playAnimation = false;
		}
	}

	public int putAnimationTile(int id, Animation animation, Attribute attribute) {
		if (active) {
			TileMap.Tile tile = new TileMap.Tile();
			tile.id = id;
			tile.imgId = -1;
			tile.attribute = attribute;
			if (animation != null && animation.getTotalFrames() > 0) {
				tile.isAnimation = true;
				tile.animation = animation;
				playAnimation = true;
			}
			animations.add(animation);
			arrays.add(tile);
			dirty = true;
			return tile.imgId;
		} else {
			throw new RuntimeException(
					"Map is no longer active, you can not add new tiles !");
		}
	}

	public int putAnimationTile(int id, String res, int w, int h, int timer) {
		return putAnimationTile(id,
				Animation.getDefaultAnimation(res, w, h, timer), null);
	}

	public int putAnimationTile(int id, Animation animation) {
		return putAnimationTile(id, animation, null);
	}

	public int putTile(int id, LImage img, Attribute attribute) {
		if (active) {
			TileMap.Tile tile = new TileMap.Tile();
			tile.id = id;
			tile.imgId = imgPack.putImage(img);
			tile.attribute = attribute;
			arrays.add(tile);
			dirty = true;
			return tile.imgId;
		} else {
			throw new RuntimeException(
					"Map is no longer active, you can not add new tiles !");
		}
	}

	public int putTile(int id, LImage img) {
		return putTile(id, img, null);
	}

	public int putTile(int id, LTexture img, Attribute attribute) {
		if (active) {
			TileMap.Tile tile = new TileMap.Tile();
			tile.id = id;
			tile.imgId = imgPack.putImage(img);
			tile.attribute = attribute;
			arrays.add(tile);
			dirty = true;
			return tile.imgId;
		} else {
			throw new RuntimeException(
					"Map is no longer active, you can not add new tiles !");
		}
	}

	public int putTile(int id, LTexture img) {
		return putTile(id, img, null);
	}

	public int putTile(int id, String res, Attribute attribute) {
		if (active) {
			TileMap.Tile tile = new TileMap.Tile();
			tile.id = id;
			tile.imgId = imgPack.putImage(res);
			tile.attribute = attribute;
			arrays.add(tile);
			dirty = true;
			return tile.imgId;
		} else {
			throw new RuntimeException(
					"Map is no longer active, you can not add new tiles !");
		}
	}

	public int putTile(int id, String res) {
		return putTile(id, res, null);
	}

	public void putTile(int id, int imgId, Attribute attribute) {
		if (active) {
			TileMap.Tile tile = new TileMap.Tile();
			tile.id = id;
			tile.imgId = imgId;
			tile.attribute = attribute;
			arrays.add(tile);
			dirty = true;
		} else {
			throw new RuntimeException(
					"Map is no longer active, you can not add new tiles !");
		}
	}

	public void putTile(int id, int imgId) {
		putTile(id, imgId, null);
	}

	public TileMap.Tile getTile(int id) {
		for (Tile tile : arrays) {
			if (tile.id == id) {
				return tile;
			}
		}
		return null;
	}

	public int[][] getMap() {
		return field.getMap();
	}

	public boolean isActive() {
		return active;
	}

	public void completed() {
		if (imgPack != null) {
			imgPack.packed(format);
			active = false;
		}
	}

	public Format getFormat() {
		return format;
	}

	public int getTileID(int x, int y) {
		if (x >= 0 && x < field.getWidth() && y >= 0 && y < field.getHeight()) {
			return field.getType(y, x);
		} else {
			return -1;
		}
	}

	public void setTileID(int x, int y, int id) {
		if (x >= 0 && x < field.getWidth() && y >= 0 && y < field.getHeight()) {
			field.setType(y, x, id);
		}
	}

	public void draw(GLEx g) {
		draw(g, null, offset.x(), offset.y());
	}

	public void draw(GLEx g, SpriteBatch batch, float offsetX, float offsetY) {
		final boolean useBatch = (batch != null);
		if (!dirty && lastOffsetX == offsetX && lastOffsetY == offsetY) {
			imgPack.glCache();
			if (playAnimation) {
				int[][] maps = field.getMap();
				for (int i = firstTileX; i < lastTileX; i++) {
					for (int j = firstTileY; j < lastTileY; j++) {
						if (i > -1 && j > -1 && i < field.width
								&& j < field.height) {
							int id = maps[j][i];
							for (Tile tile : arrays) {
								if (tile.isAnimation && tile.id == id) {
									if (useBatch) {
										batch.draw(
												tile.animation.getSpriteImage(),
												field.tilesToWidthPixels(i)
														+ offsetX,
												field.tilesToHeightPixels(j)
														+ offsetY,
												field.getTileWidth(),
												field.getTileHeight());
									} else {
										g.drawTexture(
												tile.animation.getSpriteImage(),
												field.tilesToWidthPixels(i)
														+ offsetX,
												field.tilesToHeightPixels(j)
														+ offsetY,
												field.getTileWidth(),
												field.getTileHeight());
									}
								}
							}
						}
					}
				}
			}
		} else {
			if (arrays.size() == 0) {
				throw new RuntimeException("Not to add any tiles !");
			}
			imgPack.glBegin();
			firstTileX = field.pixelsToTilesWidth(-offsetX);
			firstTileY = field.pixelsToTilesHeight(-offsetY);
			lastTileX = firstTileX + field.pixelsToTilesWidth(maxWidth) + 1;
			lastTileX = MathUtils.min(lastTileX, field.width);
			lastTileY = firstTileY + field.pixelsToTilesHeight(maxHeight) + 1;
			lastTileY = MathUtils.min(lastTileY, field.height);
			int[][] maps = field.getMap();
			for (int i = firstTileX; i < lastTileX; i++) {
				for (int j = firstTileY; j < lastTileY; j++) {
					if (i > -1 && j > -1 && i < field.width
							&& j < field.height) {
						int id = maps[j][i];
						for (Tile tile : arrays) {
							if (playAnimation) {
								if (tile.id == id) {
									if (tile.isAnimation) {
										if (useBatch) {
											batch.draw(
													tile.animation
															.getSpriteImage(),
													field.tilesToWidthPixels(i)
															+ offsetX,
													field.tilesToHeightPixels(j)
															+ offsetY, field
															.getTileWidth(),
													field.getTileHeight());
										} else {
											g.drawTexture(
													tile.animation
															.getSpriteImage(),
													field.tilesToWidthPixels(i)
															+ offsetX,
													field.tilesToHeightPixels(j)
															+ offsetY, field
															.getTileWidth(),
													field.getTileHeight());
										}
									} else {
										imgPack.draw(tile.imgId,
												field.tilesToWidthPixels(i)
														+ offsetX,
												field.tilesToHeightPixels(j)
														+ offsetY,
												field.getTileWidth(),
												field.getTileHeight());
									}
								}
							} else if (tile.id == id) {
								imgPack.draw(tile.imgId,
										field.tilesToWidthPixels(i) + offsetX,
										field.tilesToHeightPixels(j) + offsetY,
										field.getTileWidth(),
										field.getTileHeight());
							}

						}
					}
				}
			}
			imgPack.glEnd();
			lastOffsetX = offsetX;
			lastOffsetY = offsetY;
			dirty = false;
		}
		if (listener != null) {
			listener.draw(g, offsetX, offsetY);
		}
	}

	public int[] getLimit() {
		return field.getLimit();
	}

	public void setLimit(int[] limit) {
		field.setLimit(limit);
	}

	public boolean isHit(int px, int py) {
		return field.isHit(px, py);
	}

	public boolean isHit(Vector2f v) {
		return field.isHit(v);
	}

	public Vector2f getTileCollision(LObject o, float newX, float newY) {
		newX = MathUtils.ceil(newX);
		newY = MathUtils.ceil(newY);

		float fromX = MathUtils.min(o.getX(), newX);
		float fromY = MathUtils.min(o.getY(), newY);
		float toX = MathUtils.max(o.getX(), newX);
		float toY = MathUtils.max(o.getY(), newY);

		int fromTileX = field.pixelsToTilesWidth(fromX);
		int fromTileY = field.pixelsToTilesHeight(fromY);
		int toTileX = field.pixelsToTilesWidth(toX + o.getWidth() - 1f);
		int toTileY = field.pixelsToTilesHeight(toY + o.getHeight() - 1f);

		for (int x = fromTileX; x <= toTileX; x++) {
			for (int y = fromTileY; y <= toTileY; y++) {
				if ((x < 0) || (x >= field.getWidth())) {
					return new Vector2f(x, y);
				}
				if ((y < 0) || (y >= field.getHeight())) {
					return new Vector2f(x, y);
				}
				if (!this.isHit(x, y)) {
					return new Vector2f(x, y);
				}
			}
		}

		return null;
	}

	public int getTileIDFromPixels(Vector2f v) {
		return getTileIDFromPixels(v.x, v.y);
	}

	public int getTileIDFromPixels(float sx, float sy) {
		float x = (sx + offset.getX());
		float y = (sy + offset.getY());
		Vector2f tileCoordinates = pixelsToTiles(x, y);
		return getTileID(MathUtils.round(tileCoordinates.getX()),
				MathUtils.round(tileCoordinates.getY()));
	}

	public Vector2f pixelsToTiles(float x, float y) {
		float xprime = x / field.getTileWidth() - 1;
		float yprime = y / field.getTileHeight() - 1;
		return new Vector2f(xprime, yprime);
	}

	public Field2D getField() {
		return field;
	}

	public int tilesToPixelsX(float x) {
		return field.tilesToWidthPixels(x);
	}

	public int tilesToPixelsY(float y) {
		return field.tilesToHeightPixels(y);
	}

	public int pixelsToTilesWidth(float x) {
		return field.pixelsToTilesWidth(x);
	}

	public int pixelsToTilesHeight(float y) {
		return field.pixelsToTilesHeight(y);
	}

	/**
	 * 转换坐标为像素坐标
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public Vector2f tilesToPixels(float x, float y) {
		float xprime = x * field.getTileWidth() - offset.getX();
		float yprime = y * field.getTileHeight() - offset.getY();
		return new Vector2f(xprime, yprime);
	}

	/**
	 * 设置瓦片位置
	 * 
	 * @param x
	 * @param y
	 */
	public void setOffset(float x, float y) {
		this.offset.set(x, y);
	}

	/**
	 * 设定偏移量
	 * 
	 * @param offset
	 */
	public void setOffset(Vector2f offset) {
		this.offset.set(offset);
	}

	/**
	 * 获得瓦片位置
	 * 
	 * @return
	 */
	public Vector2f getOffset() {
		return offset;
	}

	public int getTileWidth() {
		return field.getTileWidth();
	}

	public int getTileHeight() {
		return field.getTileHeight();
	}

	@Override
	public int getHeight() {
		return field.getHeight() * field.getTileWidth();
	}

	@Override
	public int getWidth() {
		return field.getWidth() * field.getTileHeight();
	}

	public int getRow() {
		return field.getWidth();
	}

	public int getCol() {
		return field.getHeight();
	}

	public DrawListener getListener() {
		return listener;
	}

	public void setListener(DrawListener listener) {
		this.listener = listener;
	}

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	@Override
	public void setVisible(boolean v) {
		this.visible = v;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void createUI(GLEx g) {
		if (!visible) {
			return;
		}
		if (getX() != 0 || getY() != 0) {
			g.translate(getX(), getY());
		}
		draw(g);
		if (getX() != 0 || getY() != 0) {
			g.translate(-getX(), -getY());
		}
	}

	@Override
	public RectBox getCollisionBox() {
		return getRect(x() + offset.x, y() + offset.y, field.getTileWidth()
				* field.getWidth(), field.getTileHeight() * field.getHeight());
	}

	@Override
	public LTexture getBitmap() {
		return imgPack.getTexture();
	}

	@Override
	public void update(long elapsedTime) {
		if (playAnimation && animations.size() > 0) {
			for (Animation a : animations) {
				a.update(elapsedTime);
			}
		}
		if (listener != null) {
			listener.update(elapsedTime);
		}
	}

	public void startAnimation() {
		playAnimation = true;
	}

	public void stopAnimation() {
		playAnimation = false;
	}

	@Override
	public void dispose() {
		visible = false;
		playAnimation = false;
		animations.clear();
		if (imgPack != null) {
			imgPack.dispose();
		}
	}

}
