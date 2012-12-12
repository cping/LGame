using Loon.Core;
using Loon.Action.Sprite;
using Loon.Core.Graphics.Opengl;
using System.Collections.Generic;
using Loon.Core.Geom;
using Loon.Utils;
using System;
using Loon.Core.Graphics;
namespace Loon.Action.Map {
	
	public class TileMap : LObject, ISprite, LRelease {
	
		public interface DrawListener {
	
			void Update(long elapsedTime);
	
			void Draw(GLEx g, float x, float y);
	
		}
	
		public class Tile {
	
			internal int id;
	
			internal int imgId;
	
			public Attribute attribute;
	
			internal bool isAnimation;
	
			public Animation animation;
	
		}
	
		private int firstTileX;
	
		private int firstTileY;
	
		private int lastTileX;
	
		private int lastTileY;
	
		public DrawListener listener;
	
		private LTexturePack imgPack;

        private List<TileMap.Tile> arrays = new List<TileMap.Tile>(10);

        private List<Animation> animations = new List<Animation>();
	
		private readonly int maxWidth, maxHeight;
	
		private readonly Field2D field;
	
		private float lastOffsetX, lastOffsetY;
	
		private Vector2f offset;
	
		private Loon.Core.Graphics.Opengl.LTexture.Format format;
	
		private bool active, dirty;
	
		private bool visible;
	
		private bool playAnimation;
	
		public TileMap(string fileName, int tileWidth, int tileHeight) :this(fileName, tileWidth, tileHeight, LSystem.screenRect.width,
					LSystem.screenRect.height, Loon.Core.Graphics.Opengl.LTexture.Format.LINEAR){
		
		}
	
		public TileMap(string fileName, int tileWidth, int tileHeight, int mWidth,
				int mHeight, Loon.Core.Graphics.Opengl.LTexture.Format format):	this(TileMapConfig.LoadAthwartArray(fileName), tileWidth, tileHeight,
					mWidth, mHeight, format) {
		
		}
	
		public TileMap(int[][] maps, int tileWidth, int tileHeight, int mWidth,
				int mHeight, Loon.Core.Graphics.Opengl.LTexture.Format format):this(new Field2D(maps, tileWidth, tileHeight), mWidth, mHeight, format) {
			
		}
	
		public TileMap(Field2D field):this(field, LSystem.screenRect.width, LSystem.screenRect.height,
					Loon.Core.Graphics.Opengl.LTexture.Format.LINEAR) {
			
		}
	
		public TileMap(Field2D field, Loon.Core.Graphics.Opengl.LTexture.Format format):this(field, LSystem.screenRect.width, LSystem.screenRect.height, format) {
			
		}
	
		public TileMap(Field2D field, int mWidth, int mHeight, Loon.Core.Graphics.Opengl.LTexture.Format format) {
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
			imgPack.SetFormat(format);
		}
	
		public static TileMap LoadCharsMap(string resName, int tileWidth,
				int tileHeight) {
			return new TileMap(TileMapConfig.LoadCharsField(resName, tileWidth,
					tileHeight));
		}
	
		public void SetImagePack(string file) {
			if (imgPack != null) {
				imgPack.Dispose();
				imgPack = null;
			}
			this.active = false;
			this.dirty = true;
			imgPack = new LTexturePack(file);
			imgPack.Packed(format);
		}
	
		public void RemoveTile(int id) {
			foreach (Tile tile  in  arrays) {
				if (tile.id == id) {
					if (tile.isAnimation) {
						CollectionUtils.Remove(animations,tile.animation);
					}
                    CollectionUtils.Remove(arrays,tile);
				}
			}
			if (animations.Count == 0) {
				playAnimation = false;
			}
		}
	
		public int PutAnimationTile(int id, Animation animation, Attribute attribute) {
			if (active) {
				TileMap.Tile tile = new TileMap.Tile();
				tile.id = id;
				tile.imgId = -1;
				tile.attribute = attribute;
				if (animation != null && animation.GetTotalFrames() > 0) {
					tile.isAnimation = true;
					tile.animation = animation;
					playAnimation = true;
				}
				CollectionUtils.Add(animations,animation);
                CollectionUtils.Add(arrays,tile);
				dirty = true;
				return tile.imgId;
			} else {
				throw new Exception(
						"Map is no longer active, you can not add new tiles !");
			}
		}
	
		public int PutAnimationTile(int id, string res, int w, int h, int timer) {
			return PutAnimationTile(id,
					Animation.GetDefaultAnimation(res, w, h, timer), null);
		}
	
		public int PutAnimationTile(int id, Animation animation) {
			return PutAnimationTile(id, animation, null);
		}
	
		public int PutTile(int id, LImage img, Attribute attribute) {
			if (active) {
				TileMap.Tile tile = new TileMap.Tile();
				tile.id = id;
				tile.imgId = imgPack.PutImage(img);
				tile.attribute = attribute;
				CollectionUtils.Add(arrays,tile);
				dirty = true;
				return tile.imgId;
			} else {
				throw new Exception(
						"Map is no longer active, you can not add new tiles !");
			}
		}
	
		public int PutTile(int id, LImage img) {
			return PutTile(id, img, null);
		}

        public int PutTile(int id, LTexture img, Attribute attribute)
        {
            if (active)
            {
                TileMap.Tile tile = new TileMap.Tile();
                tile.id = id;
                tile.imgId = imgPack.PutImage(img);
                tile.attribute = attribute;
                CollectionUtils.Add(arrays, tile);
                dirty = true;
                return tile.imgId;
            }
            else
            {
                throw new Exception(
                        "Map is no longer active, you can not add new tiles !");
            }
        }
	
		public int PutTile(int id, LTexture img) {
			return PutTile(id, img, null);
		}
	
		public int PutTile(int id, string res, Attribute attribute) {
			if (active) {
				TileMap.Tile tile = new TileMap.Tile();
				tile.id = id;
				tile.imgId = imgPack.PutImage(res);
				tile.attribute = attribute;
				CollectionUtils.Add(arrays,tile);
				dirty = true;
				return tile.imgId;
			} else {
				throw new Exception(
						"Map is no longer active, you can not add new tiles !");
			}
		}
	
		public int PutTile(int id, string res) {
			return PutTile(id, res, null);
		}

        public void PutTile(int id, int imgId, Attribute attribute)
        {
            if (active)
            {
                TileMap.Tile tile = new TileMap.Tile();
                tile.id = id;
                tile.imgId = imgId;
                tile.attribute = attribute;
                CollectionUtils.Add(arrays, tile);
                dirty = true;
            }
            else
            {
                throw new Exception(
                        "Map is no longer active, you can not add new tiles !");
            }
        }
	
		public void PutTile(int id, int imgId) {
			PutTile(id, imgId, null);
		}
	
		public TileMap.Tile GetTile(int id) {
			foreach (Tile tile  in  arrays) {
				if (tile.id == id) {
					return tile;
				}
			}
			return null;
		}
	
		public int[][] GetMap() {
			return field.GetMap();
		}
	
		public bool IsActive() {
			return active;
		}
	
		public void Completed() {
			if (imgPack != null) {
				imgPack.Packed(format);
				active = false;
			}
		}
	
		public Loon.Core.Graphics.Opengl.LTexture.Format GetFormat() {
			return format;
		}
	
		public int GetTileID(int x, int y) {
			if (x >= 0 && x < field.GetWidth() && y >= 0 && y < field.GetHeight()) {
				return field.GetType(y, x);
			} else {
				return -1;
			}
		}
	
		public void SetTileID(int x, int y, int id) {
			if (x >= 0 && x < field.GetWidth() && y >= 0 && y < field.GetHeight()) {
				field.SetType(y, x, id);
			}
		}
	
		public void Draw(GLEx g) {
			Draw(g, null, offset.X(), offset.Y());
		}
	
		public void Draw(GLEx g, SpriteBatch batch, float offsetX, float offsetY) {
			bool useBatch = (batch != null);
			if (!dirty && lastOffsetX == offsetX && lastOffsetY == offsetY) {
				imgPack.GLCache();
				if (playAnimation) {
					int[][] maps = field.GetMap();
					for (int i = firstTileX; i < lastTileX; i++) {
						for (int j = firstTileY; j < lastTileY; j++) {
							if (i > -1 && j > -1 && i < field.width
									&& j < field.height) {
								int id = maps[j][i];
								foreach (Tile tile  in  arrays) {
									if (tile.isAnimation && tile.id == id) {
										if (useBatch) {
											batch.Draw(
													tile.animation.GetSpriteImage(),
													field.TilesToWidthPixels(i)
															+ offsetX,
													field.TilesToHeightPixels(j)
															+ offsetY,
													field.GetTileWidth(),
													field.GetTileHeight());
										} else {
											g.DrawTexture(
													tile.animation.GetSpriteImage(),
													field.TilesToWidthPixels(i)
															+ offsetX,
													field.TilesToHeightPixels(j)
															+ offsetY,
													field.GetTileWidth(),
													field.GetTileHeight());
										}
									}
								}
							}
						}
					}
				}
			} else {
				if (arrays.Count == 0) {
					throw new Exception("Not to add any tiles !");
				}
				imgPack.GLBegin();
				firstTileX = field.PixelsToTilesWidth(-offsetX);
				firstTileY = field.PixelsToTilesHeight(-offsetY);
				lastTileX = firstTileX + field.PixelsToTilesWidth(maxWidth) + 1;
				lastTileX = MathUtils.Min(lastTileX, field.width);
				lastTileY = firstTileY + field.PixelsToTilesHeight(maxHeight) + 1;
				lastTileY = MathUtils.Min(lastTileY, field.height);
				int[][] maps = field.GetMap();
				for (int i = firstTileX; i < lastTileX; i++) {
					for (int j = firstTileY; j < lastTileY; j++) {
						if (i > -1 && j > -1 && i < field.width && j < field.height) {
							int id = maps[j][i];
							foreach (Tile tile  in  arrays) {
								if (playAnimation) {
									if (tile.id == id) {
										if (tile.isAnimation) {
											if (useBatch) {
												batch.Draw(
														tile.animation
																.GetSpriteImage(),
														field.TilesToWidthPixels(i)
																+ offsetX,
														field.TilesToHeightPixels(j)
																+ offsetY, field
																.GetTileWidth(),
														field.GetTileHeight());
											} else {
												g.DrawTexture(
														tile.animation
																.GetSpriteImage(),
														field.TilesToWidthPixels(i)
																+ offsetX,
														field.TilesToHeightPixels(j)
																+ offsetY, field
																.GetTileWidth(),
														field.GetTileHeight());
											}
										} else {
											imgPack.Draw(tile.imgId,
													field.TilesToWidthPixels(i)
															+ offsetX,
													field.TilesToHeightPixels(j)
															+ offsetY,
													field.GetTileWidth(),
													field.GetTileHeight());
										}
									}
								} else if (tile.id == id) {
									imgPack.Draw(tile.imgId,
											field.TilesToWidthPixels(i) + offsetX,
											field.TilesToHeightPixels(j) + offsetY,
											field.GetTileWidth(),
											field.GetTileHeight());
								}
	
							}
						}
					}
				}
				imgPack.GLEnd();
				lastOffsetX = offsetX;
				lastOffsetY = offsetY;
				dirty = false;
			}
			if (listener != null) {
				listener.Draw(g, offsetX, offsetY);
			}
		}
	
		public int[] GetLimit() {
			return field.GetLimit();
		}
	
		public void SetLimit(int[] limit) {
			field.SetLimit(limit);
		}
	
		public bool IsHit(int px, int py) {
			return field.IsHit(px, py);
		}
	
		public bool IsHit(Vector2f v) {
			return field.IsHit(v);
		}
	
		public Vector2f GetTileCollision(LObject o, float newX, float newY) {
			newX = MathUtils.Ceil(newX);
			newY = MathUtils.Ceil(newY);
	
			float fromX = MathUtils.Min(o.GetX(), newX);
			float fromY = MathUtils.Min(o.GetY(), newY);
			float toX = MathUtils.Max(o.GetX(), newX);
			float toY = MathUtils.Max(o.GetY(), newY);
	
			int fromTileX = field.PixelsToTilesWidth(fromX);
			int fromTileY = field.PixelsToTilesHeight(fromY);
			int toTileX = field.PixelsToTilesWidth(toX + o.GetWidth() - 1f);
			int toTileY = field.PixelsToTilesHeight(toY + o.GetHeight() - 1f);
	
			for (int x = fromTileX; x <= toTileX; x++) {
				for (int y = fromTileY; y <= toTileY; y++) {
					if ((x < 0) || (x >= field.GetWidth())) {
						return new Vector2f(x, y);
					}
					if ((y < 0) || (y >= field.GetHeight())) {
						return new Vector2f(x, y);
					}
					if (!this.IsHit(x, y)) {
						return new Vector2f(x, y);
					}
				}
			}
	
			return null;
		}
	
		public int GetTileIDFromPixels(Vector2f v) {
			return GetTileIDFromPixels(v.x, v.y);
		}
	
		public int GetTileIDFromPixels(float sx, float sy) {
			float x = (sx + offset.GetX());
			float y = (sy + offset.GetY());
			Vector2f tileCoordinates = pixelsToTiles(x, y);
			return GetTileID(MathUtils.Round(tileCoordinates.GetX()),
					MathUtils.Round(tileCoordinates.GetY()));
		}
	
		public Vector2f pixelsToTiles(float x, float y) {
			float xprime = x / field.GetTileWidth() - 1;
			float yprime = y / field.GetTileHeight() - 1;
			return new Vector2f(xprime, yprime);
		}
	
		public Field2D GetField() {
			return field;
		}
	
		public int TilesToPixelsX(float x) {
			return field.TilesToWidthPixels(x);
		}
	
		public int TilesToPixelsY(float y) {
			return field.TilesToHeightPixels(y);
		}
	
		public int PixelsToTilesWidth(float x) {
			return field.PixelsToTilesWidth(x);
		}
	
		public int PixelsToTilesHeight(float y) {
			return field.PixelsToTilesHeight(y);
		}
	
		public Vector2f TilesToPixels(float x, float y) {
			float xprime = x * field.GetTileWidth() - offset.GetX();
			float yprime = y * field.GetTileHeight() - offset.GetY();
			return new Vector2f(xprime, yprime);
		}
	
		public void SetOffset(float x, float y) {
			this.offset.Set(x, y);
		}
	
		public void SetOffset(Vector2f offset) {
			this.offset.Set(offset);
		}
	
		public Vector2f GetOffset() {
			return offset;
		}
	
		public int GetTileWidth() {
			return field.GetTileWidth();
		}
	
		public int GetTileHeight() {
			return field.GetTileHeight();
		}
	
		public override int GetHeight() {
			return field.GetHeight() * field.GetTileWidth();
		}
	
		public override int GetWidth() {
			return field.GetWidth() * field.GetTileHeight();
		}
	
		public int GetRow() {
			return field.GetWidth();
		}
	
		public int GetCol() {
			return field.GetHeight();
		}
	
		public DrawListener GetListener() {
			return listener;
		}
	
		public void SetListener(DrawListener listener) {
			this.listener = listener;
		}
	
		public bool IsDirty() {
			return dirty;
		}
	
		public void SetDirty(bool dirty) {
			this.dirty = dirty;
		}
	
		public void SetVisible(bool v) {
			this.visible = v;
		}
	
		public bool IsVisible() {
			return visible;
		}
	
		public void CreateUI(GLEx g) {
			if (!visible) {
				return;
			}
			if (GetX() != 0 || GetY() != 0) {
				g.Translate(GetX(), GetY());
			}
			Draw(g);
			if (GetX() != 0 || GetY() != 0) {
				g.Translate(-GetX(), -GetY());
			}
		}
	
		public RectBox GetCollisionBox() {
			return GetRect(X() + offset.x, Y() + offset.y, field.GetTileWidth()
					* field.GetWidth(), field.GetTileHeight() * field.GetHeight());
		}
	
		public LTexture GetBitmap() {
			return imgPack.GetTexture();
		}
	
		public override void Update(long elapsedTime) {
			if (playAnimation && animations.Count > 0) {
				foreach (Animation a  in  animations) {
					a.Update(elapsedTime);
				}
			}
			if (listener != null) {
				listener.Update(elapsedTime);
			}
		}
	
		public void StartAnimation() {
			playAnimation = true;
		}
	
		public void StopAnimation() {
			playAnimation = false;
		}
	
		public virtual void Dispose() {
			visible = false;
			playAnimation = false;
			animations.Clear();
			if (imgPack != null) {
				imgPack.Dispose();
			}
		}
	
	}
}
