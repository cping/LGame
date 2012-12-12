using System;
using System.Collections.Generic;
using Loon.Utils;
namespace Loon.Core.Graphics {
	
	public class LColorPool : LRelease {
	
		private static LColorPool pool;
	
		public static LColorPool _() {
			 lock (typeof(LColorPool)) {
						if (pool == null) {
							pool = new LColorPool();
						}
					}
			return pool;
		}
	
		private readonly LColor AlphaColor = new LColor(0f, 0f, 0f, 0f);

        private Dictionary<Int32, LColor> ColorMap = new Dictionary<Int32, LColor>();

        public LColor GetColor(float r, float g, float b, float a)
        {
            if (a <= 0.1f)
            {
                return AlphaColor;
            }
            int hashCode = 1;
            hashCode = LSystem.Unite(hashCode, r);
            hashCode = LSystem.Unite(hashCode, g);
            hashCode = LSystem.Unite(hashCode, b);
            hashCode = LSystem.Unite(hashCode, a);
            LColor color = (LColor)CollectionUtils.Get(ColorMap, hashCode);
            if (color == null)
            {
                color = new LColor(r, g, b, a);
                CollectionUtils.Put(ColorMap, hashCode, color);
            }
            return color;
        }
	
		public LColor GetColor(uint c) {
            LColor color = (LColor)CollectionUtils.Get(ColorMap, c);
			if (color == null) {
				color = new LColor(c);
                CollectionUtils.Put(ColorMap, c, color);
			}
			return color;
		}
	
		public LColor GetColor(float r, float g, float b) {
			return GetColor(r, g, b, 1f);
		}
	
		public LColor GetColor(int r, int g, int b, int a) {
			if (a <= 10) {
				return AlphaColor;
			}
			int hashCode = 1;
			hashCode = LSystem.Unite(hashCode, r);
			hashCode = LSystem.Unite(hashCode, g);
			hashCode = LSystem.Unite(hashCode, b);
			hashCode = LSystem.Unite(hashCode, a);
            LColor color = (LColor)CollectionUtils.Get(ColorMap, hashCode);
			if (color == null) {
				color = new LColor(r, g, b, a);
                CollectionUtils.Put(ColorMap, hashCode, color);
			}
			return color;
		}
	
		public LColor GetColor(int r, int g, int b) {
			return GetColor(r, g, b, 1f);
		}
	
		public void Dispose() {
			if (ColorMap != null) {
				ColorMap.Clear();
			}
		}
	}
}
