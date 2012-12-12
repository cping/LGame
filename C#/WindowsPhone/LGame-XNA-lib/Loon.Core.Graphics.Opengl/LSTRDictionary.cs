using System.Text;
using System.Collections.Generic;
using Loon.Utils;
namespace Loon.Core.Graphics.Opengl
{
    public class LSTRDictionary
    {

        private static readonly System.Collections.Generic.Dictionary<string, LFont> cacheList = new System.Collections.Generic.Dictionary<string, LFont>(
                20);

        private static readonly System.Collections.Generic.Dictionary<LFont, Dict> fontList = new System.Collections.Generic.Dictionary<LFont, Dict>(
                20);

        private static System.Collections.Generic.Dictionary<string, LSTRFont> lazyEnglish = new System.Collections.Generic.Dictionary<string, LSTRFont>(
                10);

        public static readonly string added = "0123456789";

        public const char split = '$';

        private static StringBuilder lazyKey;

        public class Dict : LRelease
        {

            internal List<char> dicts;

            internal LSTRFont font;

            static internal Dict NewDict()
            {
                return new Dict();
            }

            internal Dict()
            {
                dicts = new List<char>(512);
            }

            public void Dispose()
            {
                if (font != null)
                {
                    font.Dispose();
                    font = null;
                }
                if (dicts != null)
                {
                    dicts.Clear();
                    dicts = null;
                }
            }

        }

        public static void ClearStringLazy()
        {
            lock (cacheList)
            {
                if (cacheList != null)
                {
                    cacheList.Clear();
                }
            }
            lock (fontList)
            {
                foreach (Dict d in fontList.Values)
                {
                    if (d != null)
                    {
                        d.Dispose();
                    }
                }
                fontList.Clear();
            }
        }

        private static readonly int size = LSystem.DEFAULT_MAX_CACHE_SIZE * 5;

        public static Dict Bind(LFont font, string mes)
        {
            string message = mes + added;
            if (cacheList.Count > size)
            {
                ClearStringLazy();
            }
            lock (fontList)
            {
                LFont cFont = (LFont)CollectionUtils.Get(cacheList,message);
                Dict pDict = (Dict)CollectionUtils.Get(fontList,font);
                if (cFont == null || pDict == null)
                {
                    if (pDict == null)
                    {
                        pDict = Dict.NewDict();
                       CollectionUtils.Put(fontList,font, pDict);
                    }
                    lock (pDict)
                    {
                        CollectionUtils.Put(cacheList,message, font);
                        List<char> charas = pDict.dicts;
                        int oldSize = charas.Count;
                        char[] chars = message.ToCharArray();
                        for (int i = 0; i < chars.Length; i++)
                        {
                            if (!charas.Contains(chars[i]))
                            {
                                charas.Add(chars[i]);
                            }
                        }
                        int newSize = charas.Count;
                        if (oldSize != newSize)
                        {
                            if (pDict.font != null)
                            {
                                pDict.font.Dispose();
                                pDict.font = null;
                            }
                            StringBuilder sbr = new StringBuilder(newSize);
                            for (int i = 0; i < newSize; i++)
                            {
                                sbr.Append(charas[i]);
                            }
                            pDict.font = new LSTRFont(font, sbr.ToString());
                        }
                    }
                }
                return pDict;
            }
        }

        public static void DrawString(LFont font, string message, float x,
                float y, float angle, LColor c)
        {
            Dict pDict = Bind(font, message);
            if (pDict.font != null)
            {
                lock (pDict.font)
                {
              
                    pDict.font.DrawString(message, x, y, angle, c);
                }
            }
        }

        public static void DrawString(LFont font, string message, float x,
                float y, float sx, float sy, float ax, float ay, float angle,
                LColor c)
        {
            Dict pDict = Bind(font, message);
            if (pDict.font != null)
            {
                lock (pDict.font)
                {
                    pDict.font.DrawString(message, x, y, sx, sy, ax, ay, angle, c);
                }
            }
        }

        public static string MakeStringLazyKey(LFont font, string text)
        {
            int hashCode = 0;
            hashCode = LSystem.Unite(hashCode, font.GetSize());
            hashCode = LSystem.Unite(hashCode, font.GetStyle());
            if (lazyKey == null)
            {
                lazyKey = new StringBuilder();
                lazyKey.Append(font.GetFontName().ToLower());
                lazyKey.Append(hashCode);
                lazyKey.Append(split);
                lazyKey.Append(text);
            }
            else
            {
                lazyKey.Clear();
                lazyKey.Append(font.GetFontName().ToLower());
                lazyKey.Append(hashCode);
                lazyKey.Append(split);
                lazyKey.Append(text);
            }
            return lazyKey.ToString();
        }

        private static string MakeLazyWestKey(LFont font)
        {
            if (lazyKey == null)
            {
                lazyKey = new StringBuilder();
                lazyKey.Append(font.GetFontName().ToLower());
                lazyKey.Append(font.GetStyle());
                lazyKey.Append(font.GetSize());
            }
            else
            {
                lazyKey.Clear();
                lazyKey.Append(font.GetFontName().ToLower());
                lazyKey.Append(font.GetStyle());
                lazyKey.Append(font.GetSize());
            }
            return lazyKey.ToString();
        }

        public static void ClearEnglishLazy()
        {
            lock (lazyEnglish)
            {
                foreach (LSTRFont str in lazyEnglish.Values)
                {
                    if (str != null)
                    {
                        str.Dispose();
                    }
                }
            }
        }

        public static LSTRFont GetGLFont(LFont f)
        {
            if (lazyEnglish.Count > LSystem.DEFAULT_MAX_CACHE_SIZE)
            {
                ClearEnglishLazy();
            }
            string key = MakeLazyWestKey(f);
            LSTRFont font = (LSTRFont)CollectionUtils.Get(lazyEnglish, key);
            if (font == null)
            {
                font = new LSTRFont(f, true);
                CollectionUtils.Put(lazyEnglish, key, font);
            }
            return font;
        }

        public static void Dispose()
        {
            ClearEnglishLazy();
            ClearStringLazy();
        }
    }
}
