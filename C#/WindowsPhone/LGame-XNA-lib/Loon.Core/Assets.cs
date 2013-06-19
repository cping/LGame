using System.Collections.Generic;
using Loon.Utils;
using Loon.Java;
using Loon.Media;
using Loon.Core.Resource;
using System;
using System.Text;
using System.IO;
namespace Loon.Core
{

    public class Assets
    {

        private static Audio _audio;
 
        private static string pathPrefix = "assets/";

        protected static string NormalizePath(string path)
        {
            int pathLen;
            do
            {
                pathLen = path.Length;
                path = path.Replace("[^/]+/\\.\\./", "");
            } while (path.Length != pathLen);
            return path;
        }

        public static void SetPathPrefix(string prefix)
        {
            if (prefix.StartsWith("/") || prefix.EndsWith("/"))
            {
                throw new ArgumentException(
                        "Prefix must not start or end with '/'.");
            }
            pathPrefix = (prefix.Length == 0) ? prefix : (prefix + "/");
        }

        public static string GetPathPrefix()
        {
            return pathPrefix;
        }

        public static void OnResume()
        {
            if (_audio == null)
            {
                _audio = new Audio();
            }
            _audio.OnResume();
        }

        public static void OnPause()
        {
            if (_audio == null)
            {
                _audio = new Audio();
            }
            _audio.OnPause();
        }

        public static void OnDestroy()
        {
            if (_audio == null)
            {
                _audio = new Audio();
            }
            _audio.OnDestroy();
        }

        public static Sound GetSound(string path)
        {
            if (_audio == null)
            {
                _audio = new Audio();
            }
            return _audio.CreateAudio(NormalizePath(pathPrefix + path));
        }

        public static Sound GetMusic(string path)
        {
            if (_audio == null)
            {
                _audio = new Audio();
            }
            return _audio.CreateSong(NormalizePath(pathPrefix + path));
        }

        public static InputStream GetStream(string resName)
        {
            return Resources.OpenResource(NormalizePath(pathPrefix + resName));
        }

        public static string GetText(string path)
        {
            StringBuilder sbr = new StringBuilder(1000);
            try
            {
                StreamReader reader = new StreamReader(GetStream(path), System.Text.Encoding.UTF8);
                string record = null;
                for (; (record = reader.ReadLine()) != null; )
                {
                    sbr.Append(record);
                }
                reader.Close();
            }
            catch (Exception ex)
            {
                Loon.Utils.Debugging.Log.Exception(ex);
            }
            return sbr.ToString();
        }

        private static List<Asset> _assetList = new List<Asset>(
                CollectionUtils.INITIAL_CAPACITY);

        private static int _loadedIndex = 0;

        public static void Reset()
        {
            _loadedIndex = 0;
        }

        public static string GetCurrentAssetName()
        {
            if (_loadedIndex < _assetList.Count)
            {
                return _assetList[_loadedIndex].AssetName;
            }
            return "LoadComplete";
        }

        public static int GetPercentLoaded()
        {
            return ((100 * _loadedIndex) / _assetList.Count);
        }

        public static bool HasLoaded()
        {
            return (_loadedIndex >= _assetList.Count);
        }

        public static bool LoadAllAssets()
        {
            while (!LoadOneAsset())
            {
                try
                {
                    Thread.Sleep(1);
                }
                catch (Exception)
                {

                }
            }
            return true;
        }

        public static bool LoadOneAsset()
        {
            if (HasLoaded())
            {
                return true;
            }
            Asset asset = _assetList[_loadedIndex];
            if (asset != null)
            {
                asset.Load();
            }
            _loadedIndex++;
            return false;
        }

        public static void PrepareAsset(Asset asset)
        {
            if (asset != null)
            {
                CollectionUtils.Add(_assetList, asset);
            }
        }
    }
}
