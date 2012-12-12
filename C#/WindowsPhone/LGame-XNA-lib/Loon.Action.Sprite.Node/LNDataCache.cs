using Loon.Core;
using Loon.Utils;
namespace Loon.Action.Sprite.Node {
	
	public class LNDataCache : LRelease {
	
		private static bool initCache;
	
		private static System.Collections.Generic.Dictionary<string, DefImage> imageDictionary;

        private static System.Collections.Generic.Dictionary<string, DefAnimation> animationDictionary;

        public static DefAnimation AnimationByKey(string key)
        {
            if (!initCache)
            {
                InitDataDefinitions();
            }
            return (DefAnimation)CollectionUtils.Get(animationDictionary, key);
        }
	
		public static LNFrameStruct GetFrameStruct(string key) {
			if (!initCache) {
				InitDataDefinitions();
			}
            DefImage img = ImageByKey(key);
			if (img != null) {
				return LNFrameStruct.InitWithImage(img);
			}
			return null;
		}
	
		public static DefImage ImageByKey(string key) {
			if (!initCache) {
				InitDataDefinitions();
			}
			return (DefImage)CollectionUtils.Get(imageDictionary,key.ToLower());
		}
	
		public static void InitDataDefinitions() {
			if (!initCache) {
                imageDictionary = new System.Collections.Generic.Dictionary<string, DefImage>();
                animationDictionary = new System.Collections.Generic.Dictionary<string, DefAnimation>();
				initCache = true;
			}
		}

        public static void SetAnimation(DefAnimation anim, string key)
        {
            if (!initCache)
            {
                InitDataDefinitions();
            }
            CollectionUtils.Put(animationDictionary, key, anim);
        }
	
		public static void SetImage(DefImage img, string key) {
			if (!initCache) {
				InitDataDefinitions();
			}
			CollectionUtils.Put(imageDictionary,key.ToLower(), img);
		}
	
		public void Dispose() {
			initCache = false;
			if (imageDictionary != null) {
				imageDictionary.Clear();
				imageDictionary = null;
			}
			if (animationDictionary != null) {
				animationDictionary.Clear();
				animationDictionary = null;
			}
		}
	}
}
