using loon.utils;

namespace loon.canvas
{
    public class LColorPool : LRelease
    {

        private static LColorPool colorPool;

        public static void FreeStatic()
        {
            colorPool = null;
        }

        public static LColorPool Get()
        {
            lock (typeof(LColorPool))
            {
                if (colorPool == null)
                {
                    colorPool = new LColorPool();
                }
            }
            return colorPool;
        }

        private static readonly LColor alphaColor = new LColor(0f, 0f, 0f, 0f);

        private IntMap<LColor> colorMap = new IntMap<LColor>();

        private bool closed;

        public LColor GetColor(float r, float g, float b, float a)
        {
            if (a <= 0.1f)
            {
                return alphaColor;
            }
            int hashCode = 1;
            hashCode = LSystem.Unite(hashCode, r);
            hashCode = LSystem.Unite(hashCode, g);
            hashCode = LSystem.Unite(hashCode, b);
            hashCode = LSystem.Unite(hashCode, a);
            LColor color = colorMap.Get(hashCode);
            if (color == null)
            {
                color = new LColor(r, g, b, a);
                colorMap.Put(hashCode, color);
            }
            return color;
        }

        public LColor GetColor(uint c)
        {
            LColor color = colorMap.Get(c);
            if (color == null)
            {
                color = new LColor(c);
                colorMap.Put(c, color);
            }
            return color;
        }

        public LColor GetColor(float r, float g, float b)
        {
            return GetColor(r, g, b, 1f);
        }

        public LColor GetColor(int r, int g, int b, int a)
        {
            if (a <= 10)
            {
                return alphaColor;
            }
            int hashCode = 1;
            hashCode = LSystem.Unite(hashCode, r);
            hashCode = LSystem.Unite(hashCode, g);
            hashCode = LSystem.Unite(hashCode, b);
            hashCode = LSystem.Unite(hashCode, a);
            LColor color = colorMap.Get(hashCode);
            if (color == null)
            {
                color = new LColor(r, g, b, a);
                colorMap.Put(hashCode, color);
            }
            return color;
        }

        public LColor GetColor(int r, int g, int b)
        {
            return GetColor(r, g, b, 1f);
        }


        public void Close()
        {
            if (colorMap != null)
            {
                colorMap.Clear();
            }
            closed = true;
        }

        public bool IsClosed()
        {
            return closed;
        }
    }
}
