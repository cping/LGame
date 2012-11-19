namespace Loon.Action.Map
{

    using Loon.Core.Graphics.OpenGL;
    using Loon.Core.Geom;
    using Loon.Core;
    using Loon.Action.Sprite;
    using System.Collections.Generic;
    using System;
    using Loon.Core.Graphics;
    using Loon.Utils;
    using Loon.Java;

    public class TileMap : LObject, ISprite, LRelease
    {

        public interface DrawListener
        {

            void Update(long elapsedTime);

            void Draw(GLEx g, int x, int y);

        }

        public class Tile
        {

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

        private int lastOffsetX, lastOffsetY;

        private Vector2f offset;

        private bool active, dirty;

        private bool visible;

        private bool playAnimation;



        public TileMap(string fileName, int tileWidth, int tileHeight, int mWidth,
                int mHeight)
            : this(TileMapConfig.LoadAthwartArray(fileName), tileWidth, tileHeight,
                mWidth, mHeight)
        {

        }

        public TileMap(int[][] maps, int tileWidth, int tileHeight, int mWidth,
                int mHeight)
            : this(new Field2D(maps, tileWidth, tileHeight), tileWidth, tileHeight,
                mWidth, mHeight)
        {

        }

        public TileMap(Field2D field, int tileWidth, int tileHeight, int mWidth,
                int mHeight)
        {
            this.field = field;
            this.maxWidth = mWidth;
            this.maxHeight = mHeight;
            this.offset = new Vector2f(0, 0);
            this.imgPack = new LTexturePack();
            this.lastOffsetX = -1;
            this.lastOffsetY = -1;
            this.active = true;
            this.dirty = true;
            this.visible = true;
        }

        public void SetImagePack(string file)
        {
            if (imgPack != null)
            {
                imgPack.Dispose();
                imgPack = null;
            }
            this.active = false;
            this.dirty = true;
            imgPack = new LTexturePack(file);
            imgPack.Packed();
        }

        private SpriteBatch batchAnimation;

        public void RemoveTile(int id)
        {
            foreach (Tile tile in arrays)
            {
                if (tile.id == id)
                {
                    if (tile.isAnimation)
                    {
                        animations.Remove(tile.animation);
                    }
                    arrays.Remove(tile);
                }
            }
            if (animations.Count == 0)
            {
                playAnimation = false;
            }
        }

        public void PutAnimationTile(int id, Animation animation,
                Attribute attribute)
        {
            if (active)
            {
                TileMap.Tile tile = new TileMap.Tile();
                tile.id = id;
                tile.imgId = -1;
                tile.attribute = attribute;
                if (animation != null && animation.GetTotalFrames() > 0)
                {
                    tile.isAnimation = true;
                    tile.animation = animation;
                    playAnimation = true;
                    if (batchAnimation == null)
                    {
                        batchAnimation = new SpriteBatch();
                    }
                }
                animations.Add(animation);
                arrays.Add(tile);
                dirty = true;
            }
            else
            {
                throw new Exception(
                        "Map is no longer active, you can not Add new tiles !");
            }
        }

        public void PutAnimationTile(int id, string res, int w, int h, int timer)
        {
            PutAnimationTile(id, Animation.GetDefaultAnimation(res, w, h, timer),
                    null);
        }

        public void PutAnimationTile(int id, Animation animation)
        {
            PutAnimationTile(id, animation, null);
        }

        public void PutTile(int id, LPixmap img, Attribute attribute)
        {
            if (active)
            {
                TileMap.Tile tile = new TileMap.Tile();
                tile.id = id;
                tile.imgId = imgPack.PutImage(img);
                tile.attribute = attribute;
                arrays.Add(tile);
                dirty = true;
            }
            else
            {
                throw new Exception(
                        "Map is no longer active, you can not Add new tiles !");
            }
        }

        public void PutTile(int id, LPixmap img)
        {
            PutTile(id, img, null);
        }

        public void PutTile(int id, LTexture img, Attribute attribute)
        {
            if (active)
            {
                TileMap.Tile tile = new TileMap.Tile();
                tile.id = id;
                tile.imgId = imgPack.PutImage(img);
                tile.attribute = attribute;
                arrays.Add(tile);
                dirty = true;
            }
            else
            {
                throw new Exception(
                        "Map is no longer active, you can not Add new tiles !");
            }
        }

        public void PutTile(int id, LTexture img)
        {
            PutTile(id, img, null);
        }

        public void PutTile(int id, string res, Attribute attribute)
        {
            if (active)
            {
                TileMap.Tile tile = new TileMap.Tile();
                tile.id = id;
                tile.imgId = imgPack.PutImage(res);
                tile.attribute = attribute;
                arrays.Add(tile);
                dirty = true;
            }
            else
            {
                throw new Exception(
                        "Map is no longer active, you can not Add new tiles !");
            }
        }

        public void PutTile(int id, string res)
        {
            PutTile(id, res, null);
        }

        public void PutTile(int id, int imgId, Attribute attribute)
        {
            if (active)
            {
                TileMap.Tile tile = new TileMap.Tile();
                tile.id = id;
                tile.imgId = imgId;
                tile.attribute = attribute;
                arrays.Add(tile);
                dirty = true;
            }
            else
            {
                throw new Exception(
                        "Map is no longer active, you can not Add new tiles !");
            }
        }

        public void PutTile(int id, int imgId)
        {
            PutTile(id, imgId, null);
        }

        public TileMap.Tile GetTile(int id)
        {
            foreach (Tile tile in arrays)
            {
                if (tile.id == id)
                {
                    return tile;
                }
            }
            return null;
        }

        public int[][] GetMap()
        {
            return field.GetMap();
        }

        public bool IsActive()
        {
            return active;
        }

        public void Completed()
        {
            if (imgPack != null)
            {
                imgPack.Packed();
                active = false;
            }
        }

        public int GetTileID(int x, int y)
        {
            if (x >= 0 && x < field.GetWidth() && y >= 0 && y < field.GetHeight())
            {
                return field.GetType(y, x);
            }
            else
            {
                return -1;
            }
        }

        public void SetTileID(int x, int y, int id)
        {
            if (x >= 0 && x < field.GetWidth() && y >= 0 && y < field.GetHeight())
            {
                field.SetType(y, x, id);
            }
        }

        public void Draw(GLEx g)
        {
            Draw(g, null, offset.X(), offset.Y());
        }

        public void Draw(GLEx g, SpriteBatch batch, float offsetX, float offsetY)
        {
            if (arrays.Count == 0)
            {
                throw new RuntimeException("Not to add any tiles !");
            }
            bool useBatch = (batch != null);
            imgPack.GLBegin();
            firstTileX = field.PixelsToTilesWidth(-offsetX);
            firstTileY = field.PixelsToTilesHeight(-offsetY);
            lastTileX = firstTileX + field.PixelsToTilesWidth(maxWidth) + 1;
            lastTileX = MathUtils.Min(lastTileX, field.GetWidth());
            lastTileY = firstTileY + field.PixelsToTilesHeight(maxHeight) + 1;
            lastTileY = MathUtils.Min(lastTileY, field.GetHeight());
            int[][] maps = field.GetMap();
            for (int i = firstTileX; i < lastTileX; i++)
            {
                for (int j = firstTileY; j < lastTileY; j++)
                {
                    if (i > -1 && j > -1 && i < field.GetWidth()
                            && j < field.GetHeight())
                    {
                        int id = maps[j][i];
                        foreach (Tile tile in arrays)
                        {
                            if (playAnimation)
                            {
                                if (tile.id == id)
                                {
                                    if (tile.isAnimation)
                                    {
                                        if (useBatch)
                                        {
                                            batch.Draw(
                                                     tile.animation.GetSpriteImage(),
                                                     field.TilesToWidthPixels(i)
                                                             + offsetX,
                                                     field.TilesToHeightPixels(j)
                                                             + offsetY,
                                                     field.GetTileWidth(),
                                                     field.GetTileHeight());
                                        }
                                        else
                                        {
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
                                    else
                                    {
                                        imgPack.Draw(tile.imgId,
                                                field.TilesToWidthPixels(i)
                                                        + offsetX,
                                                field.TilesToHeightPixels(j)
                                                        + offsetY,
                                                field.GetTileWidth(),
                                                field.GetTileHeight());
                                    }
                                }
                            }
                            else if (tile.id == id)
                            {
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
            lastOffsetX = (int)offsetX;
            lastOffsetY = (int)offsetY;
            dirty = false;

            if (listener != null)
            {
                listener.Draw(g, lastOffsetX, lastOffsetY);
            }
        }


        public int[] GetLimit()
        {
            return field.GetLimit();
        }

        public void SetLimit(int[] limit)
        {
            field.SetLimit(limit);
        }

        public bool IsHit(int px, int py)
        {
            return field.IsHit(px, py);
        }

        public bool IsHit(Vector2f v)
        {
            return field.IsHit(v);
        }

        public Vector2f GetTileCollision(LObject o, float newX, float newY)
        {
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

            for (int x = fromTileX; x <= toTileX; x++)
            {
                for (int y = fromTileY; y <= toTileY; y++)
                {
                    if ((x < 0) || (x >= this.GetWidth()))
                    {
                        return new Vector2f(x, y);
                    }
                    if ((y < 0) || (y >= this.GetHeight()))
                    {
                        return new Vector2f(x, y);
                    }

                    if (this.IsHit(x, y))
                    {
                        return new Vector2f(x, y);
                    }
                }
            }

            return null;
        }

        public int GetTileIDFromPixels(Vector2f v)
        {
            return GetTileIDFromPixels(v.x, v.y);
        }

        public int GetTileIDFromPixels(float sx, float sy)
        {
            float x = (sx + offset.GetX());
            float y = (sy + offset.GetY());
            Vector2f tileCoordinates = PixelsToTiles(x, y);
            return GetTileID(MathUtils.Round(tileCoordinates.GetX()),
                    MathUtils.Round(tileCoordinates.GetY()));
        }

        public Vector2f PixelsToTiles(float x, float y)
        {
            float xprime = x / field.GetTileWidth() - 1;
            float yprime = y / field.GetTileHeight() - 1;
            return new Vector2f(xprime, yprime);
        }

        public Field2D GetField()
        {
            return field;
        }


        public int TilesToPixelsX(float x)
        {
            return field.TilesToWidthPixels(x);
        }

        public int TilesToPixelsY(float y)
        {
            return field.TilesToHeightPixels(y);
        }

        public int PixelsToTilesWidth(float x)
        {
            return field.PixelsToTilesWidth(x);
        }

        public int PixelsToTilesHeight(float y)
        {
            return field.PixelsToTilesHeight(y);
        }


        public int GetTileWidth()
        {
            return field.GetTileWidth();
        }

        public int GetTileHeight()
        {
            return field.GetTileHeight();
        }

        public override int GetHeight()
        {
            return field.GetHeight() * field.GetTileWidth();
        }

        public override int GetWidth()
        {
            return field.GetWidth() * field.GetTileHeight();
        }

        public int GetRow()
        {
            return field.GetWidth();
        }

        public int GetCol()
        {
            return field.GetHeight();
        }

        /// <summary>
        /// 转换坐标为像素坐标
        /// </summary>
        ///
        /// <param name="x"></param>
        /// <param name="y"></param>
        /// <returns></returns>
        public Vector2f TilesToPixels(float x, float y)
        {
            float xprime = x * field.GetTileWidth() - offset.GetX();
            float yprime = y * field.GetTileHeight() - offset.GetY();
            return new Vector2f(xprime, yprime);
        }

        /// <summary>
        /// 设置瓦片位置
        /// </summary>
        ///
        /// <param name="x"></param>
        /// <param name="y"></param>
        public void SetOffset(float x, float y)
        {
            this.offset.Set(x, y);
        }

        /// <summary>
        /// 设定偏移量
        /// </summary>
        ///
        /// <param name="offset"></param>
        public void SetOffset(Vector2f offset)
        {
            this.offset.Set(offset);
        }

        /// <summary>
        /// 获得瓦片位置
        /// </summary>
        ///
        /// <returns></returns>
        public Vector2f GetOffset()
        {
            return offset;
        }

        public DrawListener GetListener()
        {
            return listener;
        }

        public void SetListener(DrawListener listener)
        {
            this.listener = listener;
        }

        public bool IsDirty()
        {
            return dirty;
        }

        public void SetDirty(bool dirty)
        {
            this.dirty = dirty;
        }

        public void SetVisible(bool v)
        {
            this.visible = v;
        }

        public bool IsVisible()
        {
            return visible;
        }

        public void CreateUI(GLEx g)
        {
            if (!visible)
            {
                return;
            }
            if (GetX() != 0 || GetY() != 0)
            {
                g.Translate(GetX(), GetY());
            }
            Draw(g);
            if (GetX() != 0 || GetY() != 0)
            {
                g.Translate(-GetX(), -GetY());
            }
        }

        public RectBox GetCollisionBox()
        {
            return GetRect(X() + offset.x, Y() + offset.y, field.GetTileWidth()
                    * field.GetWidth(), field.GetTileHeight() * field.GetHeight());
        }

        public LTexture GetBitmap()
        {
            return imgPack.GetTexture();
        }

        public override void Update(long elapsedTime)
        {
            if (playAnimation && animations.Count > 0)
            {
                foreach (Animation a in animations)
                {
                    a.Update(elapsedTime);
                }
            }
            if (listener != null)
            {
                listener.Update(elapsedTime);
            }
        }

        public void StartAnimation()
        {
            playAnimation = true;
            if (batchAnimation == null)
            {
                batchAnimation = new SpriteBatch();
            }
        }

        public void StopAnimation()
        {
            playAnimation = false;
        }

        public void Dispose()
        {
            visible = false;
            playAnimation = false;
            animations.Clear();
            if (imgPack != null)
            {
                imgPack.Dispose();
            }
            if (batchAnimation != null)
            {
                batchAnimation.Dispose();
            }
        }

    }
}
