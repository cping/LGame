using System.Collections.Generic;
using Loon.Utils;
namespace Loon.Core
{

    public class AssetManager
    {

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
