namespace Loon.Action.Map
{

    using System.IO;
    using System.Collections.Generic;
    using Loon.Core.Resource;
    using Loon.Utils;
    using Loon.Core.Graphics;

    public class TileMapConfig
    {

        private int[][] backMap;

        public int[][] GetBackMap()
        {
            return backMap;
        }

        public void SetBackMap(int[][] m)
        {
            this.backMap = m;
        }

        public static IList<int[]> LoadList(string fileName)
        {
            Stream ins = Resources.OpenStream(fileName);
            StreamReader reader = new StreamReader(ins);
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
                            intArray[i] = int.Parse(stringArray[i]);
                        }
                        records.Add(intArray);
                    }
                }
            }
            finally
            {
                if (reader != null)
                {
                    reader.Close();
                    reader = null;
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
            int row = (((int[])mapArray[(col > 0) ? col - 1 : 0]).Length);
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
