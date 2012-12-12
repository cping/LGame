using Microsoft.Xna.Framework.Graphics;
namespace Loon.Core.Graphics.Opengl
{
    public abstract class LTextureData
    {
        protected internal bool isExt;

        public static bool ALL_ALPHA = false;

        internal int width, height;

        internal int texWidth, texHeight;

        internal bool hasAlpha, multipyAlpha = ALL_ALPHA;

        internal string fileName;

        internal Texture2D buffer;

        public abstract LTextureData Copy();

        public abstract void CreateTexture();

        public int GetHeight()
        {
            return height;
        }

        public int GetTexHeight()
        {
            return texHeight;
        }

        public int GetTexWidth()
        {
            return texWidth;
        }

        public bool HasAlpha()
        {
            return hasAlpha;
        }

        public int GetWidth()
        {
            return width;
        }

        public string GetFileName()
        {
            return fileName;
        }

        public bool IsMultipyAlpha()
        {
            return multipyAlpha;
        }

        public void SetMultipyAlpha(bool multipyAlpha)
        {
            this.multipyAlpha = multipyAlpha;
        }
    }
}
