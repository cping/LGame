namespace Loon.Core.Resource
{
    using System.IO;
    using System.Collections.Generic;
    using System.Text;
    using Loon.Utils;

    public class CSVReader
    {

        private string delimiter = ",";

        private char escape = '\"';

        private string nowLine = null;

        private StreamReader reader;

        public CSVReader(string fileName)
        {
            reader = CSVReader.Reader(fileName, LSystem.encoding);
        }

        public CSVReader(string fileName, string charsetName)
        {
            reader = CSVReader.Reader(fileName, charsetName);
        }

        public static StreamReader Reader(string fileName, string charsetName)
        {
            try
            {
                StreamReader reader = new StreamReader(Resources.OpenStream(fileName), System.Text.Encoding.GetEncoding(charsetName));
                return reader;
            }
            catch (IOException ex)
            {
                Loon.Utils.Debug.Log.Exception(ex);
                return null;
            }
        }

        public CSVReader(StreamReader ins0)
        {
            reader = ins0;
        }

        public CSVReader(StreamReader ins0, string d)
        {
            reader = ins0;
            delimiter = d;
        }

        public CSVReader(StreamReader ins0, char d)
        {
            reader = ins0;
            delimiter += d;
        }

        public string GetDelimiter()
        {
            return delimiter;
        }

        public virtual bool Ready()
        {
            return reader.Peek() > -1;
        }

        public string ReadLine()
        {
            return reader.ReadLine();
        }

        public string[] ReadLineAsArray()
        {
            List<string> v = ReadLineAsList();
            if (v == null)
            {
                return null;
            }
            string[] items = new string[v.Count];
            for (int i = 0; i < v.Count; i++)
            {
                items[i] = (string)v[i];
            }
            return items;
        }

        public List<string> ReadLineAsList()
        {
            string line = ReadLine();
            if (line == null)
            {
                return null;
            }
            return GetCSVItems(line);
        }

        public string GetNowLine()
        {
            return nowLine;
        }

        private List<string> GetCSVItems(string line)
        {
            List<string> v = new List<string>();
            int startIdx = 0;
            int searchIdx = -1;
            StringBuilder sbLine = new StringBuilder(line);
            while ((searchIdx = StringUtils.IndexOf(sbLine.ToString(), delimiter, startIdx)) != -1)
            {
                string buf = null;

                if (sbLine[startIdx] != escape)
                {
                    buf = sbLine.ToString(startIdx, searchIdx - startIdx);
                    startIdx = searchIdx + 1;
                }
                else
                {

                    int escapeIdx = -1;
                    searchIdx = startIdx;
                    bool findDelimiter = false;

                    while ((escapeIdx = sbLine.ToString().IndexOf(escape,
                            searchIdx + 1)) != -1
                            && sbLine.Length > escapeIdx + 1)
                    {
                        char nextChar = sbLine[escapeIdx + 1];
                        if (delimiter.IndexOf(nextChar) != -1)
                        {
                            buf = sbLine.ToString(startIdx + 1, escapeIdx - startIdx + 1);
                            startIdx = escapeIdx + 2;
                            findDelimiter = true;
                            break;
                        }
                        if (nextChar == escape)
                        {
                            sbLine.Remove(escapeIdx, 1);
                            escapeIdx--;
                        }
                        searchIdx = escapeIdx + 1;
                    }

                    if (!findDelimiter)
                    {
                        break;
                    }
                }

                CollectionUtils.Add(v, buf.Trim());
            }

            if (startIdx < sbLine.Length)
            {
                int lastIdx = sbLine.Length - 1;
                if (sbLine[startIdx] == escape
                        && sbLine[lastIdx] == escape)
                {
                    sbLine.Remove(lastIdx, 1);
                    sbLine.Remove(startIdx, 1);
                }
                CollectionUtils.Add(v, sbLine.ToString(startIdx, sbLine.Length - startIdx).Trim());
            }
            else if (startIdx == sbLine.Length)
            {
                CollectionUtils.Add(v, "");
            }

            return v;
        }

        public void Close()
        {
            if (reader != null)
            {
                reader.Close();
                reader = null;
            }
        }

    }

}
