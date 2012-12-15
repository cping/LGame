using System;
using System.Collections.Generic;
using System.IO;
using Loon.Core;
using Loon.Core.Geom;
using Loon.Core.Resource;
using Loon.Core.Graphics.Opengl;
using Loon.Core.Input;
using Loon.Utils.Xml;
using Loon.Utils;

namespace Loon.Action.Map.Tmx
{
    public class TMXTiledMap : LRelease
    {

        protected internal int width;

        protected internal int height;

        protected internal int tileWidth;

        protected internal int tileHeight;

        private RectBox screenRect;

        protected internal string tilesLocation;

        protected internal TMXProperty props;

        protected internal List<TMXTileSet> tileSets = new List<TMXTileSet>();

        protected internal List<TMXLayer> layers = new List<TMXLayer>();

        protected internal List<TMXTileGroup> objectGroups = new List<TMXTileGroup>();

        private bool loadTileSets = true;

        private int defWidth, defHeight;

        public TMXTiledMap(string fileName):this(fileName, true)
        {
            
        }

        public TMXTiledMap(string fileName, bool loadTileSets_0)
        {
            this.loadTileSets = loadTileSets_0;
            fileName = fileName.Replace('\\', '/');
            string res = null;
            if (fileName.IndexOf("/") != -1)
            {
                res = fileName.Substring(0, (fileName.LastIndexOf("/")) - (0));
            }
            else
            {
                res = fileName;
            }
            try
            {
                this.Load(Resources.OpenStream(fileName), res);
            }
            catch (Exception e)
            {
                Console.Error.WriteLine(e.StackTrace);
            }
        }

        public TMXTiledMap(string fileName, string tileSetsLocation)
        {
            try
            {
                Load(Resources.OpenStream(fileName), tileSetsLocation);
            }
            catch (IOException e)
            {
                Console.Error.WriteLine(e.StackTrace);
            }
        }

        public TMXTiledMap(Stream ins0)
        {
            Load(ins0, "");
        }

        public TMXTiledMap(Stream ins0, string tileSetsLocation)
        {
            Load(ins0, tileSetsLocation);
        }

        public string GetTilesLocation()
        {
            return tilesLocation;
        }

        public int GetLayerIndex(string name)
        {
            for (int i = 0; i < layers.Count; i++)
            {
                TMXLayer layer = layers[i];
                if (layer.name.Equals(name))
                {
                    return i;
                }
            }
            return -1;
        }

        public LTexture GetTileImage(int x, int y, int layerIndex)
        {
            TMXLayer layer = layers[layerIndex];

            int tileSetIndex = layer.data[x,y,0];
            if ((tileSetIndex >= 0) && (tileSetIndex < tileSets.Count))
            {
                TMXTileSet tileSet = tileSets[tileSetIndex];

                int sheetX = tileSet.GetTileX(layer.data[x,y,1]);
                int sheetY = tileSet.GetTileY(layer.data[x,y,1]);

                return tileSet.tiles.GetSubImage(sheetX, sheetY);
            }

            return null;
        }

        public int GetWidth()
        {
            return width;
        }

        public int GetHeight()
        {
            return height;
        }

        public int GetTileHeight()
        {
            return tileHeight;
        }

        public int GetTileWidth()
        {
            return tileWidth;
        }

        public TMXLayer GetLayer(int id)
        {
            return layers[id];
        }

        public int GetTileId(int x, int y, int layerIndex)
        {
            TMXLayer layer = layers[layerIndex];
            return layer.GetTileID(x, y);
        }

        public void SetTileId(int x, int y, int layerIndex, int tileid)
        {
            TMXLayer layer = layers[layerIndex];
            layer.SetTileID(x, y, tileid);
        }

        public string GetMapProperty(string propertyName, string def)
        {
            if (props == null)
                return def;
            return props.GetProperty(propertyName, def);
        }

        public string GetLayerProperty(int layerIndex, string propertyName,
                string def)
        {
            TMXLayer layer = layers[layerIndex];
            if (layer == null || layer.props == null)
                return def;
            return layer.props.GetProperty(propertyName, def);
        }

        public string GetTileProperty(int tileID, string propertyName, string def)
        {
            if (tileID == 0)
            {
                return def;
            }

            TMXTileSet set = FindTileSet(tileID);

            TMXProperty props_0 = set.GetProperties(tileID);
            if (props_0 == null)
            {
                return def;
            }
            return props_0.GetProperty(propertyName, def);
        }

        public void Draw(GLEx g, LTouch e)
        {
            int x = e.X() / tileWidth;
            int y = e.Y() / tileHeight;
            Draw(g, 0, 0, x, y, width - defWidth, height - defHeight, false);
        }

        public void Draw(GLEx g, int tx, int ty)
        {
            Draw(g, 0, 0, tx, ty);
        }

        public void Draw(GLEx g, int x, int y, int tx, int ty)
        {
            Draw(g, x, y, tx, ty, defWidth, defHeight, false);
        }

        public void Draw(GLEx g, int x, int y, int layer)
        {
            Draw(g, x, y, 0, 0, GetWidth(), GetHeight(), layer, false);
        }

        public void Draw(GLEx g, int x, int y, int sx, int sy, int width, int height)
        {
            Draw(g, x, y, sx, sy, width, height, false);
        }

        public void Draw(GLEx g, int x, int y, int sx, int sy, int width,
                int height, int index, bool lineByLine)
        {
            TMXLayer layer = layers[index];
            layer.Draw(g, x, y, sx, sy, width, height, lineByLine, tileWidth,
                    tileHeight);
        }

        public void Draw(GLEx g, int x, int y, int sx, int sy, int width,
                int height, bool lineByLine)
        {
            for (int i = 0; i < layers.Count; i++)
            {
                TMXLayer layer = layers[i];
                layer.Draw(g, x, y, sx, sy, width, height, lineByLine, tileWidth,
                        tileHeight);
            }
        }
	
        public int GetLayerCount()
        {
            return layers.Count;
        }

        private void Load(Stream ins, string tileSetsLocation)
        {

            screenRect = LSystem.screenRect;

            tilesLocation = tileSetsLocation;

            try
            {
                XMLDocument doc = XMLParser.Parse(ins);
                XMLElement docElement = doc.GetRoot();

                string orient = docElement.GetAttribute("orientation", "");
                if (!"orthogonal".Equals(orient))
                {
                    throw new Exception(
                            "Only orthogonal maps supported, found " + orient);
                }

                width = docElement.GetIntAttribute("width", 0);
                height = docElement.GetIntAttribute("height", 0);
                tileWidth = docElement.GetIntAttribute("tilewidth", 0);
                tileHeight = docElement.GetIntAttribute("tileheight", 0);

                XMLElement propsElement = docElement
                        .GetChildrenByName("properties");
                if (propsElement != null)
                {
                    props = new TMXProperty();
                    List<XMLElement> property = propsElement.List("property");
                    for (int i = 0; i < property.Count; i++)
                    {
                        XMLElement propElement = property[i];
                        string name = propElement.GetAttribute("name", null);
                        string value_ren = propElement.GetAttribute("value", null);
                        props.SetProperty(name, value_ren);
                    }
                }

                if (loadTileSets)
                {
                    TMXTileSet tileSet = null;
                    TMXTileSet lastSet = null;

                    List<XMLElement> setNodes = docElement.List("tileset");
                    for (int i_0 = 0; i_0 < setNodes.Count; i_0++)
                    {
                        XMLElement current = setNodes[i_0];

                        tileSet = new TMXTileSet(this, current, true);
                        tileSet.index = i_0;

                        if (lastSet != null)
                        {
                            lastSet.SetLimit(tileSet.firstGID - 1);
                        }
                        lastSet = tileSet;

                        CollectionUtils.Add(tileSets, tileSet);
                    }
                }

                List<XMLElement> layerNodes = docElement.List("layer");
                for (int i_1 = 0; i_1 < layerNodes.Count; i_1++)
                {
                    XMLElement current_2 = layerNodes[i_1];
                    TMXLayer layer = new TMXLayer(this, current_2);
                    layer.index = i_1;

                    CollectionUtils.Add(layers, layer);
                }

                List<XMLElement> objectGroupNodes = docElement
                        .List("objectgroup");

                for (int i_3 = 0; i_3 < objectGroupNodes.Count; i_3++)
                {
                    XMLElement current_4 = objectGroupNodes[i_3];
                    TMXTileGroup objectGroup = new TMXTileGroup(current_4);
                    objectGroup.index = i_3;

                    CollectionUtils.Add(objectGroups, objectGroup);
                }

                defWidth = (int)(screenRect.GetWidth() / tileWidth);
                defHeight = (int)(screenRect.GetHeight() / tileHeight);

            }
            catch (Exception ex)
            {
                Console.Error.WriteLine(ex.StackTrace);
                throw new Exception("Failed to parse map", ex);
            }
        }

        public int GetScreenWidth()
        {
            return defWidth;
        }

        public int GetScreenHeight()
        {
            return defHeight;
        }

        public int GetTileSetCount()
        {
            return tileSets.Count;
        }

        public TMXTileSet GetTileSet(int index)
        {
            return tileSets[index];
        }

        public TMXTileSet GetTileSetByGID(int gid)
        {
            for (int i = 0; i < tileSets.Count; i++)
            {
                TMXTileSet set = tileSets[i];
                if (set.Contains(gid))
                {
                    return set;
                }
            }

            return null;
        }

        public TMXTileSet FindTileSet(int gid)
        {
            for (int i = 0; i < tileSets.Count; i++)
            {
                TMXTileSet set = tileSets[i];

                if (set.Contains(gid))
                {
                    return set;
                }
            }

            return null;
        }

        protected internal void Draw(GLEx g, int x, int y, int sx, int sy, int width,
                int height, int layer)
        {
        }

        public int GetObjectGroupCount()
        {
            return objectGroups.Count;
        }

        public int GetObjectCount(int groupID)
        {
            if (groupID >= 0 && groupID < objectGroups.Count)
            {
                TMXTileGroup grp = objectGroups[groupID];
                return grp.objects.Count;
            }
            return -1;
        }

        public string GetObjectName(int groupID, int objectID)
        {
            if (groupID >= 0 && groupID < objectGroups.Count)
            {
                TMXTileGroup grp = objectGroups[groupID];
                if (objectID >= 0 && objectID < grp.objects.Count)
                {
                    TMXTile obj0 = grp.objects[objectID];
                    return obj0.name;
                }
            }
            return null;
        }

        public string GetObjectType(int groupID, int objectID)
        {
            if (groupID >= 0 && groupID < objectGroups.Count)
            {
                TMXTileGroup grp = objectGroups[groupID];
                if (objectID >= 0 && objectID < grp.objects.Count)
                {
                    TMXTile obj0 = grp.objects[objectID];
                    return obj0.type;
                }
            }
            return null;
        }

        public int GetObjectX(int groupID, int objectID)
        {
            if (groupID >= 0 && groupID < objectGroups.Count)
            {
                TMXTileGroup grp = objectGroups[groupID];
                if (objectID >= 0 && objectID < grp.objects.Count)
                {
                    TMXTile obj0 = grp.objects[objectID];
                    return obj0.x;
                }
            }
            return -1;
        }

        public int GetObjectY(int groupID, int objectID)
        {
            if (groupID >= 0 && groupID < objectGroups.Count)
            {
                TMXTileGroup grp = objectGroups[groupID];
                if (objectID >= 0 && objectID < grp.objects.Count)
                {
                    TMXTile obj0 = grp.objects[objectID];
                    return obj0.y;
                }
            }
            return -1;
        }

        public int GetObjectWidth(int groupID, int objectID)
        {
            if (groupID >= 0 && groupID < objectGroups.Count)
            {
                TMXTileGroup grp = objectGroups[groupID];
                if (objectID >= 0 && objectID < grp.objects.Count)
                {
                    TMXTile obj0 = grp.objects[objectID];
                    return obj0.width;
                }
            }
            return -1;
        }

        public int GetObjectHeight(int groupID, int objectID)
        {
            if (groupID >= 0 && groupID < objectGroups.Count)
            {
                TMXTileGroup grp = objectGroups[groupID];
                if (objectID >= 0 && objectID < grp.objects.Count)
                {
                    TMXTile obj0 = grp.objects[objectID];
                    return obj0.height;
                }
            }
            return -1;
        }

        public string GetObjectImage(int groupID, int objectID)
        {
            if (groupID >= 0 && groupID < objectGroups.Count)
            {
                TMXTileGroup grp = objectGroups[groupID];
                if (objectID >= 0 && objectID < grp.objects.Count)
                {
                    TMXTile obj0 = grp.objects[objectID];

                    if (obj0 == null)
                    {
                        return null;
                    }

                    return obj0.image;
                }
            }

            return null;
        }

        public string GetObjectProperty(int groupID, int objectID,
                string propertyName, string def)
        {
            if (groupID >= 0 && groupID < objectGroups.Count)
            {
                TMXTileGroup grp = objectGroups[groupID];
                if (objectID >= 0 && objectID < grp.objects.Count)
                {
                    TMXTile obj0 = grp.objects[objectID];

                    if (obj0 == null)
                    {
                        return def;
                    }
                    if (obj0.props == null)
                    {
                        return def;
                    }

                    return obj0.props.GetProperty(propertyName, def);
                }
            }
            return def;
        }

        public virtual void Dispose()
        {
            if (tileSets != null)
            {
                foreach (TMXTileSet tmx in tileSets)
                {
                    if (tmx != null)
                    {
                        tmx.Dispose();
                    }
                }
            }
        }

    }
}
