namespace Loon.Action.Map
{

    using System;
    using System.IO;
    using System.Collections.Generic;
    using Loon.Utils;
    using Loon.Core;
    using Loon.Core.Geom;
    using Loon.Utils.Debug;
 
    public class Field2D : Config
    {

        private static readonly float angular = MathUtils.Cos(MathUtils.PI / 4);

        public static int GetDirection(float angle)
        {
            float tup = MathUtils.Sin(angle) * 0 + MathUtils.Cos(angle) * -1;
            float tright = MathUtils.Sin(angle) * 1 + MathUtils.Cos(angle) * 0;
            float tleft = MathUtils.Sin(angle) * -1 + MathUtils.Cos(angle) * 0;
            float tdown = MathUtils.Sin(angle) * 0 + MathUtils.Cos(angle) * 1;
            if (tup > angular)
            {
                return TUP;
            }
            if (tright > angular)
            {
                return TRIGHT;
            }
            if (tleft > angular)
            {
                return TLEFT;
            }
            if (tdown > angular)
            {
                return TDOWN;
            }
            return EMPTY;
        }
	
        private static Vector2f vector2;

        static private readonly Dictionary<Vector2f, Int32> directions = new Dictionary<Vector2f, Int32>(
                9);

        static private readonly Dictionary<Int32, Vector2f> directionValues = new Dictionary<Int32, Vector2f>(
                9);

        private List<Vector2f> result;

        private int[][] data;

        private int[] limit;

        private int tileWidth, tileHeight;

        private int width, height;

        public Field2D(Field2D field)
        {
            Copy(field);
        }

        public Field2D(String fileName, int w, int h)
        {
            try
            {
                Set(Loon.Action.Map.TileMapConfig.LoadAthwartArray(fileName), w, h);
            }
            catch (IOException ex)
            {
                Log.Exception(ex);
            }
        }

        public Field2D(int[][] data_0)
            : this(data_0, 0, 0)
        {

        }

        public Field2D(int[][] data_0, int w, int h)
        {
            this.Set(data_0, w, h);
        }

        public void Copy(Field2D field)
        {
            this.Set(CollectionUtils.CopyOf(field.data), field.tileWidth,
                    field.tileHeight);
        }

        public void Set(int[][] data_0, int w, int h)
        {
            this.SetMap(data_0);
            this.SetTileWidth(w);
            this.SetTileHeight(h);
            this.width = data_0[0].Length;
            this.height = data_0.Length;
        }

        public void SetSize(int w, int h)
        {
            this.width = w;
            this.height = h;
        }

        public int GetWidth()
        {
            return width;
        }

        public int GetHeight()
        {
            return height;
        }

        public int PixelsToTilesWidth(float x)
        {
            return MathUtils.Floor(x / tileWidth);
        }

        public int PixelsToTilesWidth(int x)
        {
            return MathUtils.Floor(x / tileWidth);
        }

        public int PixelsToTilesHeight(float y)
        {
            return MathUtils.Floor(y / tileHeight);
        }

        public int PixelsToTilesHeight(int y)
        {
            return MathUtils.Floor(y / tileHeight);
        }

        public int TilesToWidthPixels(int tiles)
        {
            return tiles * tileWidth;
        }

        public int TilesToHeightPixels(int tiles)
        {
            return tiles * tileHeight;
        }

        public int TilesToWidthPixels(float tiles)
        {
            return (int)(tiles * tileWidth);
        }

        public int TilesToHeightPixels(float tiles)
        {
            return (int)(tiles * tileHeight);
        }

        public int GetTileHeight()
        {
            return tileHeight;
        }

        public void SetTileHeight(int tileHeight_0)
        {
            this.tileHeight = tileHeight_0;
        }

        public int GetTileWidth()
        {
            return tileWidth;
        }

        public void SetTileWidth(int tileWidth_0)
        {
            this.tileWidth = tileWidth_0;
        }

        public int[] GetLimit()
        {
            return limit;
        }

        public void SetLimit(int[] limit_0)
        {
            this.limit = limit_0;
        }

        public int GetType(int x, int y)
        {
            try
            {
                return data[x][y];
            }
            catch
            {
                return -1;
            }
        }

        public void SetType(int x, int y, int tile)
        {
            try
            {
                this.data[x][y] = tile;
            }
            catch
            {
            }
        }

        public int[][] GetMap()
        {
            return data;
        }

        public void SetMap(int[][] data_0)
        {
            this.data = data_0;
        }

        public bool IsHit(Vector2f point)
        {
            int type = Get(data, point);
            if (type == -1)
            {
                return false;
            }
            if (limit != null)
            {
                for (int i = 0; i < limit.Length; i++)
                {
                    if (limit[i] == type)
                    {
                        return false;
                    }
                }
            }
            return true;
        }

        public bool IsHit(int px, int py)
        {
            int type = Get(data, px, py);
            if (type == -1)
            {
                return false;
            }

            if (limit != null)
            {
                for (int i = 0; i < limit.Length; i++)
                {
                    if (limit[i] == type)
                    {
                        return false;
                    }
                }
            }
            return true;
        }

        public static int GetDirection(int x, int y)
        {
            return GetDirection(x, y, Config.EMPTY);
        }

        public static int GetDirection(int x, int y, int value_ren)
        {
            if (vector2 == null)
            {
                vector2 = new Vector2f(x, y);
            }
            else
            {
                vector2.Set(x, y);
            }
            object result = (object)CollectionUtils.Get(directions, vector2);
            if (result != null)
            {
                return (Int32)result;
            }
            else
            {
                return value_ren;
            }
        }

        public static Vector2f GetDirection(Int32 type)
        {
            return directionValues[type];
        }

        private static void InsertArrays(int[][] arrays, int index, int px, int py)
        {
            arrays[index][0] = px;
            arrays[index][1] = py;
        }

        public int[][] Neighbors(int px, int py, bool flag)
        {
            int[][] pos = (int[][])CollectionUtils.XNA_CreateJaggedArray(typeof(int), 8, 2);
            InsertArrays(pos, 0, px, py - 1);
            InsertArrays(pos, 0, px + 1, py);
            InsertArrays(pos, 0, px, py + 1);
            InsertArrays(pos, 0, px - 1, py);
            if (flag)
            {
                InsertArrays(pos, 0, px - 1, py - 1);
                InsertArrays(pos, 0, px + 1, py - 1);
                InsertArrays(pos, 0, px + 1, py + 1);
                InsertArrays(pos, 0, px - 1, py + 1);
            }
            return pos;
        }

        public List<Vector2f> Neighbors(Vector2f pos, bool flag)
        {
            if (result == null)
            {
                result = new List<Vector2f>(8);
            }
            else
            {
                CollectionUtils.Clear(result);
            }
            int x = pos.X();
            int y = pos.Y();
            CollectionUtils.Add(result, new Vector2f(x, y - 1));
            CollectionUtils.Add(result, new Vector2f(x + 1, y));
            CollectionUtils.Add(result, new Vector2f(x, y + 1));
            CollectionUtils.Add(result, new Vector2f(x - 1, y));
            if (flag)
            {
                CollectionUtils.Add(result, new Vector2f(x - 1, y - 1));
                CollectionUtils.Add(result, new Vector2f(x + 1, y - 1));
                CollectionUtils.Add(result, new Vector2f(x + 1, y + 1));
                CollectionUtils.Add(result, new Vector2f(x - 1, y + 1));
            }
            return result;
        }

        private int Get(int[][] data, int px, int py)
        {
            try
            {
                if (px >= 0 && px < width && py >= 0 && py < height)
                {
                    return data[py][px];
                }
                else
                {
                    return -1;
                }
            }
            catch (Exception)
            {
                return -1;
            }
        }

        private int Get(int[][] data, Vector2f point)
        {
            try
            {
                if (point.X() >= 0 && point.X() < width && point.Y() >= 0
                        && point.Y() < height)
                {
                    return data[point.Y()][point.X()];
                }
                else
                {
                    return -1;
                }
            }
            catch (Exception)
            {
                return -1;
            }
        }

        static Field2D()
        {
            CollectionUtils.Put(directions, new Vector2f(0, 0), Config.EMPTY);
            CollectionUtils.Put(directions, new Vector2f(1, -1), Config.UP);
            CollectionUtils.Put(directions, new Vector2f(-1, -1), Config.LEFT);
            CollectionUtils.Put(directions, new Vector2f(1, 1), Config.RIGHT);
            CollectionUtils.Put(directions, new Vector2f(-1, 1), Config.DOWN);
            CollectionUtils.Put(directions, new Vector2f(0, -1), Config.TUP);
            CollectionUtils.Put(directions, new Vector2f(-1, 0), Config.TLEFT);
            CollectionUtils.Put(directions, new Vector2f(1, 0), Config.TRIGHT);
            CollectionUtils.Put(directions, new Vector2f(0, 1), Config.TDOWN);
            CollectionUtils.Put(directionValues, Config.EMPTY, new Vector2f(0, 0));
            CollectionUtils.Put(directionValues, Config.UP, new Vector2f(1, -1));
            CollectionUtils.Put(directionValues, Config.LEFT, new Vector2f(-1, -1));
            CollectionUtils.Put(directionValues, Config.RIGHT, new Vector2f(1, 1));
            CollectionUtils.Put(directionValues, Config.DOWN, new Vector2f(-1, 1));
            CollectionUtils.Put(directionValues, Config.TUP, new Vector2f(0, -1));
            CollectionUtils.Put(directionValues, Config.TLEFT, new Vector2f(-1, 0));
            CollectionUtils.Put(directionValues, Config.TRIGHT, new Vector2f(1, 0));
            CollectionUtils.Put(directionValues, Config.TDOWN, new Vector2f(0, 1));
        }

    }
}
