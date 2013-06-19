namespace Loon.Action.Map.Tmx
{

    using System;
    using System.Collections.Generic;
    using SharpZipLib;
    using Loon.Core.Graphics;
    using Loon.Core;
    using Loon.Utils.Xml;
    using Loon.Net;
    using Loon.Java;
    using Loon.Core.Graphics.Opengl;
    using Loon.Utils;

    public class TMXLayer : LLight, LRelease
    {

        private class MapTileSet
        {

            internal Loon.Core.Graphics.Opengl.LTextureBatch.GLCache cache;

            internal LTexture texture;

        }

        private TMXLayer.MapTileSet mapTileSet;

        private Dictionary<Int32, MapTileSet> lazyMaps = new Dictionary<Int32, MapTileSet>(
                10);

        private int keyHashCode = 1;

        private int cx = 0, cy = 0;

        private TMXTileSet tmxTileSet;

        private readonly TMXTiledMap tmx;

        public int index;

        public string name;

        public int[,,] data;

        public int width;

        public int height;

        public TMXProperty props;
	
       
        /// <summary>
        /// 根据TMX地图描述创建一个新层
        /// </summary>
        ///
        /// <param name="map"></param>
        /// <param name="element"></param>
        /// <exception cref="System.Exception"></exception>
        public TMXLayer(TMXTiledMap map, XMLElement element)
        {

            this.tmx = map;
            this.name = element.GetAttribute("name", "");
            this.width = element.GetIntAttribute("width", 0);
            this.height = element.GetIntAttribute("height", 0);
            this.data = new int[width, height, 3];
            this.MaxLightSize(width, height);

            // 获得当前图层属性
            XMLElement propsElement = element.GetChildrenByName("properties");

            if (propsElement != null)
            {
                props = new TMXProperty();
                List<XMLElement> properties = propsElement.List("property");
                for (int i = 0; i < properties.Count; i++)
                {
                    XMLElement propElement = properties[i];
                    string name_0 = propElement.GetAttribute("name", null);
                    string value_ren = propElement.GetAttribute("value", null);
                    props.SetProperty(name_0, value_ren);
                }
            }

            XMLElement dataNode = element.GetChildrenByName("data");
            string encoding = dataNode.GetAttribute("encoding", null);
            string compression = dataNode.GetAttribute("compression", null);

            // 进行base64的压缩解码
            if ("base64".Equals(encoding) && "gzip".Equals(compression))
            {
                try
                {

                    byte[] sdec = Base64Coder.DecodeBase64(dataNode.GetContents().Trim().ToCharArray());

                    ByteArrayInputStream mask0 = new ByteArrayInputStream(sdec);

                    GZipInputStream dis = new GZipInputStream(mask0);

                    for (int y = 0; y < height; y++)
                    {
                        for (int x = 0; x < width; x++)
                        {
                            int tileId = 0;

                            tileId |= dis.ReadByte();
                            tileId |= dis.ReadByte() << 8;
                            tileId |= dis.ReadByte() << 16;
                            tileId |= dis.ReadByte() << 24;

                            if (tileId == 0)
                            {
                                data[x,y,0] = -1;
                                data[x,y,1] = 0;
                                data[x,y,2] = 0;
                            }
                            else
                            {

                                TMXTileSet set = map.FindTileSet(tileId);

                                if (set != null)
                                {
                               
                                    data[x,y,0] = set.index;
                                    data[x,y,1] = tileId - set.firstGID;
                                }
                                data[x,y,2] = tileId;
                            }

                        }
                    }
                }
                catch (Exception e)
                {
                    Loon.Utils.Debugging.Log.Exception(e);
                    throw new Exception("Unable to decode base64 !");
                }
            }
            else
            {
                throw new Exception("Unsupport tiled map type " + encoding
                        + "," + compression + " only gzip base64 Support !");
            }
        }


        /// <summary>
        /// 获得指定位置的瓦片ID
        /// </summary>
        ///
        /// <param name="x"></param>
        /// <param name="y"></param>
        /// <returns></returns>
        public int GetTileID(int x, int y)
        {
            return data[x,y,2];
        }

        /// <summary>
        /// 设置指定位置的瓦片ID
        /// </summary>
        ///
        /// <param name="x"></param>
        /// <param name="y"></param>
        /// <param name="tile"></param>
        public void SetTileID(int x, int y, int tile)
        {
            if (tile == 0)
            {
                data[x,y,0] = -1;
                data[x,y,1] = 0;
                data[x,y,2] = 0;
            }
            else
            {
                TMXTileSet set = tmx.FindTileSet(tile);
                data[x,y,0] = set.index;
                data[x,y,1] = tile - set.firstGID;
                data[x,y,2] = tile;
            }
        }

        public void ClearCache()
        {
            if (lazyMaps != null)
            {
                lazyMaps.Clear();
            }
        }

        /// <summary>
        /// 渲染当前层画面到LGraphics之上
        /// </summary>
        ///
        /// <param name="g"></param>
        /// <param name="x"></param>
        /// <param name="y"></param>
        /// <param name="sx"></param>
        /// <param name="sy"></param>
        /// <param name="width"></param>
        /// <param name="ty"></param>
        /// <param name="isLine"></param>
        /// <param name="mapTileWidth"></param>
        /// <param name="mapTileHeight"></param>
        public void Draw(GLEx g, int x, int y, int sx, int sy, int width,
                int height, bool isLine, int mapTileWidth, int mapTileHeight)
        {

            if (width == 0 || height == 0)
            {
                return;
            }

            if (lightingOn)
            {
                GLUtils.SetShadeModelSmooth(GLEx.GL);
            }

            this.tmxTileSet = null;
            this.mapTileSet = null;

            for (int tileset = 0; tileset < tmx.GetTileSetCount(); tileset++)
            {

                keyHashCode = 1;
                keyHashCode = LSystem.Unite(keyHashCode, tileset);
                keyHashCode = LSystem.Unite(keyHashCode, sx);
                keyHashCode = LSystem.Unite(keyHashCode, sy);
                keyHashCode = LSystem.Unite(keyHashCode, width);
                keyHashCode = LSystem.Unite(keyHashCode, height);
                keyHashCode = LSystem.Unite(keyHashCode, mapTileWidth);
                keyHashCode = LSystem.Unite(keyHashCode, mapTileHeight);
                keyHashCode = LSystem.Unite(keyHashCode, lightingOn);

                mapTileSet = (MapTileSet)CollectionUtils.Get(lazyMaps, keyHashCode);

                if (!isLightDirty && mapTileSet != null)
                {

                    mapTileSet.cache.x = x;
                    mapTileSet.cache.y = y;

                    LTextureBatch.Commit(mapTileSet.texture, mapTileSet.cache);

                    if (isLine)
                    {
                        tmx.Draw(g, x, y, sx, sy, width, height, index);
                    }
                    if (lightingOn)
                    {
                        GLUtils.SetShadeModelSmooth(GLEx.GL);
                    }

                    return;
                }

                for (int ty = 0; ty < height; ty++)
                {
                    for (int tx = 0; tx < width; tx++)
                    {

                        if ((sx + tx < 0) || (sy + ty < 0))
                        {
                            continue;
                        }
                        if ((sx + tx >= this.width) || (sy + ty >= this.height))
                        {
                            continue;
                        }

                        if (data[sx + tx,sy + ty,0] == tileset)
                        {
                            if (tmxTileSet == null)
                            {
                                tmxTileSet = tmx.GetTileSet(tileset);
                                tmxTileSet.tiles.GLBegin();

                            }

                            int sheetX = tmxTileSet
                                    .GetTileX(data[sx + tx,sy + ty,1]);
                            int sheetY = tmxTileSet
                                    .GetTileY(data[sx + tx,sy + ty,1]);

                            int tileOffsetY = tmxTileSet.tileHeight - mapTileHeight;

                            cx = tx * mapTileWidth;
                            cy = ty * mapTileHeight - tileOffsetY;

                            if (lightingOn)
                            {
                                SetLightColor(cx / mapTileWidth, cy / mapTileHeight);
                            }

                            tmxTileSet.tiles
                                    .Draw(g, cx, cy, sheetX, sheetY, colors);
                        }

                    }
                }

                if (tmxTileSet != null)
                {

                    tmxTileSet.tiles.GLEnd();

                    if (mapTileSet == null)
                    {
                        mapTileSet = new TMXLayer.MapTileSet();
                    }
                    else
                    {
                        mapTileSet.texture = null;
                        mapTileSet.cache.Dispose();
                        mapTileSet.cache = null;
                    }

                    mapTileSet.texture = tmxTileSet.tiles.GetTarget();
                    mapTileSet.cache = tmxTileSet.tiles.NewCache();
                    mapTileSet.cache.x = x;
                    mapTileSet.cache.y = y;

                    CollectionUtils.Put(lazyMaps, keyHashCode, mapTileSet);

                    if (lightingOn)
                    {
                        GLUtils.SetShadeModelFlat(GLEx.GL);
                    }

                    if (isLine)
                    {
                        tmx.Draw(g, x, y, sx, sy, width, height, index);
                    }

                    isLightDirty = false;
                    tmxTileSet = null;
                }
            }

        }

        public virtual void Dispose()
        {
            if (lazyMaps != null)
            {
                lazyMaps.Clear();
            }
        }
    }
}
