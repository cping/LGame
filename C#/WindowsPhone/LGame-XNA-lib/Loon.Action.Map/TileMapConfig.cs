namespace Loon.Action.Map
{

    using System.IO;
    using System.Collections.Generic;
    using Loon.Core.Resource;
    using Loon.Utils;
    using Loon.Core.Graphics;
    using Loon.Core;
    using System;

    public class TileMapConfig
    {

        private int[][] backMap;

        public int[][] GetBackMap()
        {
            return backMap;
        }

        public void SetBackMap(int[][] backMap_0)
        {
            this.backMap = backMap_0;
        }

        public static Field2D LoadCharsField(string resName, int tileWidth,
                int tileHeight)
        {
            Field2D field = new Field2D(LoadCharsMap(resName), tileWidth,
                    tileHeight);
            return field;
        }

        public static int[][] LoadCharsMap(string resName)
        {
            int[][] map = null;
            try
            {
                TextReader br = new StreamReader(Resources.OpenStream(resName), System.Text.Encoding.GetEncoding(LSystem.encoding));
                string line = br.ReadLine();
                int width = Int32.Parse(line);
                line = br.ReadLine();
                int height = Int32.Parse(line);
                map = (int[][])CollectionUtils.XNA_CreateJaggedArray(typeof(int), width, height);
                for (int i = 0; i < width; i++)
                {
                    line = br.ReadLine();
                    for (int j = 0; j < height; j++)
                    {
                        map[i][j] = line[j];
                    }
                }
            }
            catch (Exception e)
            {
                Console.Error.WriteLine(e.StackTrace);
            }
            return map;
        }

        public static IList<int[]> LoadList(string fileName)
        {
            Stream ins0 = Resources.OpenStream(fileName);
            TextReader reader = new StreamReader(ins0);
            IList<int[]> records = new List<int[]>(
                    CollectionUtils.INITIAL_CAPACITY);
            string result = null;
            try
            {
                while ((result = reader.ReadLine()) != null)
                {
                    if (!"".Equals(result))
                    {
                        string[] stringArray = StringUtils.Split(result, ",");
                        int size = stringArray.Length;
                        int[] intArray = new int[size];
                        for (int i = 0; i < size; i++)
                        {
                            intArray[i] = Int32.Parse(stringArray[i]);
                        }
                        records.Add(intArray);
                    }
                }
            }
            finally
            {
                if (reader != null)
                {
                    try
                    {
                        reader.Close();
                        reader = null;
                    }
                    catch (IOException ex)
                    {
                        Loon.Utils.Debugging.Log.Exception(ex);
                    }
                }
            }
            return records;
        }

        public static int[][] ReversalXandY(int[][] array)
        {
            int col = array[0].Length;
            int row = array.Length;
            int[][] result = (int[][])CollectionUtils.XNA_CreateJaggedArray(typeof(int), col, row);
            for (int y = 0; y < col; y++)
            {
                for (int x = 0; x < row; x++)
                {
                    result[x][y] = array[y][x];
                }
            }
            return result;
        }

        public static int[][] LoadAthwartArray(string fileName)
        {
            IList<int[]> list = LoadList(fileName);
            int col = list.Count;
            int[][] result = new int[col][];
            for (int i = 0; i < col; i++)
            {
                result[i] = (int[])list[i];
            }
            return result;
        }
        public static int[][] LoadJustArray(string fileName)
        {
            IList<int[]> list = LoadList(fileName);
            int col = list.Count;
            int[][] mapArray = new int[col][];
            for (int i = 0; i < col; i++)
            {
                mapArray[i] = (int[])list[i];
            }
            int row = ((mapArray[(col > 0) ? col - 1 : 0]).Length);
            int[][] result = (int[][])CollectionUtils.XNA_CreateJaggedArray(typeof(int), row, col);
            for (int y = 0; y < col; y++)
            {
                for (int x = 0; x < row; x++)
                {
                    result[x][y] = mapArray[y][x];
                }
            }
            return result;
        }
	

    }
}
