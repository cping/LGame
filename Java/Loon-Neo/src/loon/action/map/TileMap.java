package loon.action.map;

import java.io.IOException;

import loon.LObject;
import loon.LProcess;
import loon.LSystem;
import loon.LTexture;
import loon.LTexture.Format;
import loon.action.ActionBind;
import loon.action.sprite.Animation;
import loon.action.sprite.ISprite;
import loon.action.sprite.MoveControl;
import loon.action.sprite.SpriteBatch;
import loon.action.sprite.Sprites;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.geom.Affine2f;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.opengl.LTexturePack;
import loon.opengl.LTexturePackClip;
import loon.utils.MathUtils;
import loon.utils.TArray;

/**
 * 一个简单的二维数组地图构造以及显示类.复杂地图请使用tmx包
 */
public class TileMap extends LObject<ISprite> implements ISprite {

	private Sprites sprites;

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

	private TArray<TileMap.Tile> arrays = new TArray<TileMap.Tile>(10);

	private TArray<Animation> animations = new TArray<Animation>();

	private final int maxWidth, maxHeight;

	private final Field2D field;

	private int lastOffsetX, lastOffsetY;

	private ActionBind follow;

	private Vector2f offset;

	private Format format;

	private boolean active, dirty;

	private boolean visible, roll;

	private boolean playAnimation;

	private LColor baseColor = LColor.white;

	private float scaleX = 1f, scaleY = 1f;

	public TileMap(String fileName, int tileWidth, int tileHeight) throws IOException {
		this(fileName, tileWidth, tileHeight, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight(), Format.LINEAR);
	}

	public TileMap(String fileName, int tileWidth, int tileHeight, int mWidth, int mHeight) throws IOException {
		this(fileName, tileWidth, tileHeight, mWidth, mHeight, Format.LINEAR);
	}

	public TileMap(String fileName, int tileWidth, int tileHeight, int mWidth, int mHeight, Format format)
			throws IOException {
		this(TileMapConfig.loadAthwartArray(fileName), tileWidth, tileHeight, mWidth, mHeight, format);
	}

	public TileMap(int[][] maps, int tileWidth, int tileHeight, int mWidth, int mHeight, Format format) {
		this(new Field2D(maps, tileWidth, tileHeight), mWidth, mHeight, format);
	}

	public TileMap(int[][] maps, int tileWidth, int tileHeight, int mWidth, int mHeight) {
		this(maps, tileWidth, tileHeight, mWidth, mHeight, Format.LINEAR);
	}

	public TileMap(int[][] maps, int tileWidth, int tileHeight) {
		this(maps, tileWidth, tileHeight, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public TileMap(Field2D field) {
		this(field, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight(), Format.LINEAR);
	}

	public TileMap(Field2D field, Format format) {
		this(field, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight(), format);
	}

	public TileMap(Field2D field, int mWidth, int mHeight, Format format) {
		this.field = field;
		if (field != null && mWidth == -1 && mHeight == -1) {
			this.maxWidth = field.getViewWidth();
			this.maxHeight = field.getViewHeight();
		} else {
			this.maxWidth = mWidth;
			this.maxHeight = mHeight;
		}
		if (field == null) {
			this.offset = new Vector2f(0, 0);
		} else {
			this.offset = field.getOffset();
		}
		this.imgPack = new LTexturePack();
		this.format = format;
		this.lastOffsetX = -1;
		this.lastOffsetY = -1;
		this.active = true;
		this.dirty = true;
		this.visible = true;
		this.sprites = new Sprites(LSystem.getProcess().getScreen(), maxWidth, maxHeight);
		this.imgPack.setFormat(format);
	}

	public static TileMap loadCharsMap(String resName, int tileWidth, int tileHeight) {
		return new TileMap(TileMapConfig.loadCharsField(resName, tileWidth, tileHeight));
	}

	public void setImagePack(String fileName, LTexturePackClip[] clips) {
		setImagePack(fileName, new TArray<LTexturePackClip>(clips));
	}

	public void setImagePack(String fileName, TArray<LTexturePackClip> clips) {
		if (imgPack != null) {
			imgPack.close();
			imgPack = null;
		}
		this.active = false;
		this.dirty = true;
		imgPack = new LTexturePack(fileName, clips);
		imgPack.packed(format);
	}

	public void setImagePack(String file) {
		if (imgPack != null) {
			imgPack.close();
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
		if (animations.size == 0) {
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
			throw new RuntimeException("Map is no longer active, you can not add new tiles !");
		}
	}

	public int putAnimationTile(int id, String res, int w, int h, int timer) {
		return putAnimationTile(id, Animation.getDefaultAnimation(res, w, h, timer), null);
	}

	public int putAnimationTile(int id, Animation animation) {
		return putAnimationTile(id, animation, null);
	}

	public int putTile(int id, Image img, Attribute attribute) {
		if (active) {
			TileMap.Tile tile = new TileMap.Tile();
			tile.id = id;
			tile.imgId = imgPack.putImage(img);
			tile.attribute = attribute;
			arrays.add(tile);
			dirty = true;
			return tile.imgId;
		} else {
			throw new RuntimeException("Map is no longer active, you can not add new tiles !");
		}
	}

	public int putTile(int id, Image img) {
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
			throw new RuntimeException("Map is no longer active, you can not add new tiles !");
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
			throw new RuntimeException("Map is no longer active, you can not add new tiles !");
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
			throw new RuntimeException("Map is no longer active, you can not add new tiles !");
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

	public void pack() {
		completed();
	}

	public void completed() {
		if (imgPack != null) {
			imgPack.packed(format);
			active = true;
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

	public void add(ISprite sprite) {
		sprites.add(sprite);
	}

	public void addAt(ISprite sprite, float x, float y) {
		sprites.addAt(sprite, x, y);
	}

	public void remove(int idx) {
		sprites.remove(idx);
	}

	public void remove(ISprite sprite) {
		sprites.remove(sprite);
	}

	public void remove(int start, int end) {
		sprites.remove(start, end);
	}

	public void draw(GLEx g) {
		if (this.roll) {
			this.offset = this.toRollPosition(this.offset);
		}
		draw(g, null, x() + offset.x(), y() + offset.y());
	}

	public void draw(GLEx g, SpriteBatch batch, int offsetX, int offsetY) {
		final boolean useBatch = (batch != null);
		if (!dirty && lastOffsetX == offsetX && lastOffsetY == offsetY) {
			imgPack.postCache();
			if (playAnimation) {
				int[][] maps = field.getMap();
				for (int i = firstTileX; i < lastTileX; i++) {
					for (int j = firstTileY; j < lastTileY; j++) {
						if (i > -1 && j > -1 && i < field.getWidth() && j < field.getHeight()) {
							int id = maps[j][i];
							for (Tile tile : arrays) {
								if (tile.isAnimation && tile.id == id) {
									if (useBatch) {
										LColor tmp = batch.getColor();
										batch.setColor(baseColor);
										batch.draw(tile.animation.getSpriteImage(),
												field.tilesToWidthPixels(i) + offsetX,
												field.tilesToHeightPixels(j) + offsetY, field.getTileWidth(),
												field.getTileHeight());
										batch.setColor(tmp);
									} else {
										g.draw(tile.animation.getSpriteImage(), field.tilesToWidthPixels(i) + offsetX,
												field.tilesToHeightPixels(j) + offsetY, field.getTileWidth(),
												field.getTileHeight(), baseColor);
									}
								}
							}
						}
					}
				}
			}
		} else {
			if (arrays.size == 0) {
				throw new RuntimeException("Not to add any tiles !");
			}

			imgPack.glBegin();

			firstTileX = field.pixelsToTilesWidth(-offsetX);
			firstTileY = field.pixelsToTilesHeight(-offsetY);

			lastTileX = firstTileX + field.pixelsToTilesWidth(maxWidth) + 1;
			lastTileX = MathUtils.min(lastTileX, field.getWidth());
			lastTileY = firstTileY + field.pixelsToTilesHeight(maxHeight) + 1;
			lastTileY = MathUtils.min(lastTileY, field.getHeight());
			int[][] maps = field.getMap();
			for (int i = firstTileX; i < lastTileX; i++) {
				for (int j = firstTileY; j < lastTileY; j++) {
					if (i > -1 && j > -1 && i < field.getWidth() && j < field.getHeight()) {
						int id = maps[j][i];
						for (Tile tile : arrays) {
							if (playAnimation) {
								if (tile.id == id) {
									if (tile.isAnimation) {
										if (useBatch) {
											LColor tmp = batch.getColor();
											batch.setColor(baseColor);
											batch.draw(tile.animation.getSpriteImage(),
													field.tilesToWidthPixels(i) + offsetX,
													field.tilesToHeightPixels(j) + offsetY, field.getTileWidth(),
													field.getTileHeight());
											batch.setColor(tmp);
										} else {
											g.draw(tile.animation.getSpriteImage(),
													field.tilesToWidthPixels(i) + offsetX,
													field.tilesToHeightPixels(j) + offsetY, field.getTileWidth(),
													field.getTileHeight(), baseColor);
										}
									} else {
										imgPack.draw(tile.imgId, field.tilesToWidthPixels(i) + offsetX,
												field.tilesToHeightPixels(j) + offsetY, field.getTileWidth(),
												field.getTileHeight(), baseColor);
									}
								}
							} else if (tile.id == id) {
								imgPack.draw(tile.imgId, field.tilesToWidthPixels(i) + offsetX,
										field.tilesToHeightPixels(j) + offsetY, field.getTileWidth(),
										field.getTileHeight(), baseColor);
							}

						}
					}
				}
			}
			imgPack.glEnd();
			imgPack.saveCache();
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
		return isHit(v.x(), v.y());
	}

	public boolean isPixelHit(int px, int py) {
		return isPixelHit(px, py, 0, 0);
	}

	public boolean isPixelHit(int px, int py, int movePx, int movePy) {
		return isHit(field.pixelsToTilesWidth(field.offsetXPixel(px)) + movePx,
				field.pixelsToTilesHeight(field.offsetYPixel(py)) + movePy);
	}

	public boolean isPixelTUp(int px, int py) {
		return isPixelHit(px, py, 0, -1);
	}

	public boolean isPixelTRight(int px, int py) {
		return isPixelHit(px, py, 1, 0);
	}

	public boolean isPixelTLeft(int px, int py) {
		return isPixelHit(px, py, -1, 0);
	}

	public boolean isPixelTDown(int px, int py) {
		return isPixelHit(px, py, 0, 1);
	}

	public Vector2f getTileCollision(LObject<?> o, float newX, float newY) {
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
		return getTileID(MathUtils.round(tileCoordinates.getX()), MathUtils.round(tileCoordinates.getY()));
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

	public float getOffsetX() {
		return offset.x;
	}

	public float getOffsetY() {
		return offset.y;
	}

	public int getTileWidth() {
		return field.getTileWidth();
	}

	public int getTileHeight() {
		return field.getTileHeight();
	}

	public float getHeight() {
		return field.getHeight() * field.getTileWidth();
	}

	public float getWidth() {
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

	public void setVisible(boolean v) {
		this.visible = v;
	}

	public boolean isVisible() {
		return visible;
	}

	@Override
	public void createUI(GLEx g, float offsetX, float offsetY) {
		if (!visible) {
			return;
		}
		boolean update = (_rotation != 0) || !(scaleX == 1f && scaleY == 1f);
		int blend = g.getBlendMode();
		int tmp = g.color();
		try {
			g.setBlendMode(_blend);
			g.setAlpha(_alpha);
			if (this.roll) {
				this.offset = toRollPosition(this.offset);
			}
			float newX = this._location.x + offsetX + offset.getX();
			float newY = this._location.y + offsetY + offset.getY();
			if (update) {
				g.saveTx();
				Affine2f tx = g.tx();
				if (_rotation != 0) {
					final float rotationCenterX = newX + getWidth() / 2f;
					final float rotationCenterY = newY + getHeight() / 2f;
					tx.translate(rotationCenterX, rotationCenterY);
					tx.preRotate(_rotation);
					tx.translate(-rotationCenterX, -rotationCenterY);
				}
				if ((scaleX != 1) || (scaleY != 1)) {
					final float scaleCenterX = newX + getWidth() / 2f;
					final float scaleCenterY = newY + getHeight() / 2f;
					tx.translate(scaleCenterX, scaleCenterY);
					tx.preScale(scaleX, scaleY);
					tx.translate(-scaleCenterX, -scaleCenterY);
				}
			}
			followActionObject();
			int moveX = (int) newX;
			int moveY = (int) newY;
			draw(g, null, moveX, moveY);
			sprites.paintPos(g, moveX, moveY);
		} catch (Exception ex) {
			LSystem.base().log().error("Array2D TileMap error !", ex);
		} finally {
			if (update) {
				g.restoreTx();
			}
			g.setBlendMode(blend);
			g.setColor(tmp);
		}
	}

	public void createUI(GLEx g) {
		createUI(g, 0, 0);
	}

	public RectBox getCollisionBox() {
		return getRect(x() + offset.x, y() + offset.y, field.getTileWidth() * field.getWidth(),
				field.getTileHeight() * field.getHeight());
	}

	@Override
	public LTexture getBitmap() {
		return imgPack.getTexture();
	}

	public Sprites getSprites() {
		return sprites;
	}

	public void update(long elapsedTime) {
		if (playAnimation && animations.size > 0) {
			for (Animation a : animations) {
				a.update(elapsedTime);
			}
		}
		sprites.update(elapsedTime);
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

	public void followActionObject() {
		if (follow != null) {
			LProcess process = LSystem.getProcess();
			float offsetX = process.getWidth() / 2 - follow.getX();
			offsetX = MathUtils.min(offsetX, 0);
			offsetX = MathUtils.max(offsetX, process.getWidth() - getWidth());
			float offsetY = process.getHeight() / 2 - follow.getY();
			offsetY = MathUtils.min(offsetY, 0);
			offsetY = MathUtils.max(offsetY, process.getHeight() - getHeight());
			setOffset(offsetX, offsetY);
			field.setOffset(offset);
		}
	}

	public LColor getColor() {
		return new LColor(baseColor);
	}

	@Override
	public void setColor(LColor c) {
		if (c != null && !c.equals(baseColor)) {
			this.baseColor = c;
			this.dirty = true;
		}
	}

	@Override
	public Field2D getField2D() {
		return field;
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
		return field.getRect().contains(x, y, w, h);
	}

	@Override
	public RectBox getRectBox() {
		return field.getRect();
	}

	public ActionBind getFollow() {
		return follow;
	}

	public TileMap setFollow(ActionBind follow) {
		this.follow = follow;
		return this;
	}

	public TileMap followAction(ActionBind follow) {
		return setFollow(follow);
	}

	public float offsetXPixel(float x) {
		return x - offset.x;
	}

	public float offsetYPixel(float y) {
		return y - offset.y;
	}

	public boolean inMap(int x, int y) {
		return ((((x >= 0) && (x < maxWidth)) && (y >= 0)) && (y < maxHeight));
	}

	public MoveControl followControl(ActionBind bind) {
		followAction(bind);
		return new MoveControl(bind, this.field);
	}

	public Vector2f toRollPosition(Vector2f pos) {
		pos.x = pos.x % ((float) (field.getViewWidth()));
		pos.y = pos.y % ((float) (field.getViewHeight()));
		if (pos.x < 0f) {
			pos.x += field.getViewWidth();
		}
		if (pos.x < 0f) {
			pos.y += field.getViewHeight();
		}
		return pos;
	}

	public boolean isRoll() {
		return roll;
	}

	public void setRoll(boolean roll) {
		this.roll = roll;
	}

	@Override
	public void close() {
		visible = false;
		playAnimation = false;
		roll = false;
		animations.clear();
		if (imgPack != null) {
			imgPack.close();
		}
		sprites.close();
		setState(State.DISPOSED);
	}

}
