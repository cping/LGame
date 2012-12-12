using System;
using System.Collections.Generic;
using System.IO;
using Loon.Core;
using Loon.Action.Sprite;
using Loon.Utils.Xml;
using Loon.Core.Resource;
using Loon.Core.Graphics;
using Loon.Core.Graphics.Opengl;
using Loon.Utils;

namespace Loon.Action.Map.Tmx
{
    public class TMXTileSet : LRelease
    {
        // »ù´¡µØÍ¼
        private readonly TMXTiledMap map;

        // ÍßÆ¬Ë÷Òý
        public int index;

        public string name;

        public int firstGID;

        public int lastGID = Int32.MaxValue;

        public int tileWidth;

        public int tileHeight;

        public SpriteSheet tiles;

        public int tilesAcross;

        public int tilesDown;

        private Dictionary<Int32, TMXProperty> props = new Dictionary<Int32, TMXProperty>();

        protected internal int tileSpacing = 0;

        protected internal int tileMargin = 0;

        public TMXTileSet(TMXTiledMap map, XMLElement element, bool loadImage)
        {
            this.map = map;
            this.name = element.GetAttribute("name", null);
            this.firstGID = element.GetIntAttribute("firstgid", 0);
            string source = element.GetAttribute("source", "");
            if (!"".Equals(source))
            {
                try
                {
                    Stream ins0 = Resources.OpenStream(map.GetTilesLocation()
                            + "/" + source);
                    XMLDocument doc = XMLParser.Parse(ins0);
                    XMLElement docElement = doc.GetRoot();
                    element = docElement;
                }
                catch (Exception e)
                {
                    Loon.Utils.Debug.Log.Exception(e);
                    throw new Exception(this.map.tilesLocation + "/"
                            + source);
                }
            }
            string tileWidthString = element.GetAttribute("tilewidth", "");
            string tileHeightString = element.GetAttribute("tileheight", "");
            if (tileWidthString.Length == 0 || tileHeightString.Length == 0)
            {
                throw new Exception(
                        "tileWidthString.length == 0 || tileHeightString.length == 0");
            }
            tileWidth = Int32.Parse(tileWidthString);
            tileHeight = Int32.Parse(tileHeightString);

            string sv = element.GetAttribute("spacing", "");
            if ((sv != null) && (!"".Equals(sv)))
            {
                tileSpacing = Int32.Parse(sv);
            }

            string mv = element.GetAttribute("margin", "");
            if ((mv != null) && (!"".Equals(mv)))
            {
                tileMargin = Int32.Parse(mv);
            }

            List<XMLElement> list = element.List("image");
            XMLElement imageNode = list[0];
            string fileName = imageNode.GetAttribute("source", null);

            LColor trans = null;
            string t = imageNode.GetAttribute("trans", null);
            if ((t != null) && (t.Length > 0))
            {
                trans = new LColor(((uint)Convert.ToInt32(t, 16)));
            }

            if (loadImage)
            {
                string path = map.GetTilesLocation() + "/" + fileName;
                LTexture image;
                if (trans != null)
                {
                    image = TextureUtils.FilterColor(path, trans);
                }
                else
                {
                    image = LTextures.LoadTexture(path);
                }
                SetTileSetImage(image);
            }

            List<XMLElement> elements = element.List("tile");
            for (int i = 0; i < elements.Count; i++)
            {
                XMLElement tileElement = elements[i];

                int id = tileElement.GetIntAttribute("id", 0);
                id += firstGID;
                TMXProperty tileProps = new TMXProperty();

                XMLElement propsElement = (XMLElement)tileElement
                        .GetChildrenByName("properties");
                List<XMLElement> properties = propsElement.List("property");
                for (int p = 0; p < properties.Count; p++)
                {
                    XMLElement propElement = properties[p];
                    string name_1 = propElement.GetAttribute("name", null);
                    string value_ren = propElement.GetAttribute("value", null);
                    tileProps.SetProperty(name_1, value_ren);
                }
                CollectionUtils.Put(props, id, tileProps);
            }
        }

        public int GetTileWidth()
        {
            return tileWidth;
        }

        public int GetTileHeight()
        {
            return tileHeight;
        }

        public int GetTileSpacing()
        {
            return tileSpacing;
        }

        public int GetTileMargin()
        {
            return tileMargin;
        }

        public void SetTileSetImage(LTexture image)
        {
            image.MaxBatchSize = 4096;
            tiles = new SpriteSheet(image, tileWidth, tileHeight, tileSpacing,
                    tileMargin);
            tilesAcross = tiles.GetHorizontalCount();
            tilesDown = tiles.GetVerticalCount();

            if (tilesAcross <= 0)
            {
                tilesAcross = 1;
            }
            if (tilesDown <= 0)
            {
                tilesDown = 1;
            }

            lastGID = (tilesAcross * tilesDown) + firstGID - 1;
        }

        public TMXProperty GetProperties(int globalID)
        {
            return (TMXProperty)CollectionUtils.Get(props, ((int)(globalID)));
        }

        public int GetTileX(int id)
        {
            return id % tilesAcross;
        }

        public int GetTileY(int id)
        {
            return id / tilesAcross;
        }

        public void SetLimit(int limit)
        {
            lastGID = limit;
        }

        public bool Contains(int gid)
        {
            return (gid >= firstGID) && (gid <= lastGID);
        }

        public virtual void Dispose()
        {
            if (tiles != null)
            {
                tiles.Dispose();
                tiles = null;
            }
        }
    }
}
