using Loon.Java;
using Loon.Utils;
using System;
namespace Loon.Core.Graphics.OpenGL
{
    public class LTextureRegion: LRelease
    {

        internal int dstX, dstY,dstWidth,dstHeight;

        internal bool flipX, flipY;

        public LTexture texture;

        private float x1, y1, x2, y2;

        public float xOff
        {
            set
            {
                if (texture != null)
                {
                    dstX = (int)(value * texture.texWidth);
                }
                x1 = value;
            }
            get
            {
                return x1;
            }
        }

        public float yOff
        {
            set
            {
                if (texture != null)
                {
                    dstY = (int)(value * texture.texHeight);
                }
                y1 = value;
            }
            get
            {
                return y1;
            }
        }

        public float widthRatio
        {
            set
            {
                if (texture != null)
                {
                    dstWidth = (int)((texture.texWidth * value)) - dstX;
                }
                x2 = value;
            }
            get
            {
                return x2;
            }
        }

        public float heightRatio
        {
            set
            {
                if (texture != null)
                {
                    dstHeight = (int)((texture.texHeight * value)) - dstY;
                }
                y2 = value;
            }
            get
            {
                return y2;
            }
        }

        public LTextureRegion()
        {
            this.widthRatio = 1f;
            this.heightRatio = 1f;
        }

        public LTextureRegion(string file):this(LTextures.LoadTexture(file))
        {
            
        }

        public LTextureRegion(string file, int x, int y, int width, int height):this(LTextures.LoadTexture(file), x, y, width, height)
        {
            
        }

        public LTextureRegion(LTexture texture)
        {
            if (texture == null)
            {
                throw new ArgumentException("texture cannot be null.");
            }
            this.texture = texture;
            this.widthRatio = 1f;
            this.heightRatio = 1f;
            SetRegion(0, 0, texture.texWidth, texture.texHeight);
        }

        public LTextureRegion(LTexture texture, int width, int height)
        {
            if (texture == null)
            {
                throw new ArgumentException("texture cannot be null.");
            }
            this.texture = texture;
            this.widthRatio = 1f;
            this.heightRatio = 1f;
            SetRegion(0, 0, width, height);
        }

        public LTextureRegion(LTexture texture, int x, int y, int width, int height)
        {
            if (texture == null)
            {
                throw new ArgumentException("texture cannot be null.");
            }
            this.texture = texture;
            this.widthRatio = 1f;
            this.heightRatio = 1f;
            SetRegion(x, y, width, height);
        }

        public LTextureRegion(LTextureRegion region)
        {
            SetRegion(region);
        }

        public LTextureRegion(LTextureRegion region, int x, int y, int width,
                int height)
        {
            SetRegion(region, x, y, width, height);
        }

        public void SetRegion(LTexture texture)
        {
            this.texture = texture;
            this.widthRatio = 1f;
            this.heightRatio = 1f;
            SetRegion(0, 0, texture.GetWidth(), texture.GetHeight());
        }

        public void SetRegion(int x, int y, int width, int height)
        {
            float invTexWidth = (1f / texture.GetWidth()) * texture.widthRatio;
            float invTexHeight = (1f / texture.GetHeight()) * texture.heightRatio;
            SetRegion(x * invTexWidth + texture.xOff, y * invTexHeight
                    + texture.yOff, (x + width) * invTexWidth, (y + height)
                    * invTexHeight);
        }

        public virtual void SetRegion(float xOff, float yOff, float widthRatio,
                float heightRatio)
        {
            this.xOff = xOff;
            this.yOff = yOff;
            this.widthRatio = widthRatio;
            this.heightRatio = heightRatio;
        }

        public void SetRegion(LTextureRegion region)
        {
            texture = region.texture;
            SetRegion(region.xOff, region.yOff, region.widthRatio,
                    region.heightRatio);
        }

        public void SetRegion(LTextureRegion region, int x, int y, int width,
                int height)
        {
            texture = region.texture;
            SetRegion(region.GetRegionX() + x, region.GetRegionY() + y, width,
                    height);
        }

        public LTexture GetTexture()
        {
            return texture;
        }

        public void SetTexture(LTexture texture)
        {
            this.texture = texture;
        }

        public int GetRegionX()
        {
            return (int)(xOff * texture.texWidth);
        }

        public int GetRegionY()
        {
            return (int)(yOff * texture.texHeight);
        }

        public int GetRegionWidth()
        {
            int result = MathUtils.Round((widthRatio - xOff)
                    * texture.GetTextureWidth());
            return (result > 0) ? result : -result;
        }

        public int GetRegionHeight()
        {
            int result = MathUtils.Round((heightRatio - yOff)
                    * texture.GetTextureHeight());
            return (result > 0) ? result : -result;
        }

        public virtual void Flip(bool x, bool y)
        {
            if (x)
            {
                float temp = xOff;
                xOff = widthRatio;
                widthRatio = temp;
                this.flipX = true;
            }
            if (y)
            {
                float temp = yOff;
                yOff = heightRatio;
                heightRatio = temp;
                this.flipY = true;
            }
        }

        public virtual void Scroll(float xAmount, float yAmount)
        {
            if (xAmount != 0)
            {
                float width = (widthRatio - xOff) * texture.GetTextureWidth();
                xOff = (xOff + xAmount) % 1;
                widthRatio = xOff + width / texture.GetWidth();
            }
            if (yAmount != 0)
            {
                float height = (heightRatio - yOff) * texture.GetTextureHeight();
                yOff = (yOff + yAmount) % 1;
                heightRatio = yOff + height / texture.GetTextureHeight();
            }
        }

        public LTextureRegion[][] Split(int tileWidth, int tileHeight)
        {
            int x = GetRegionX();
            int y = GetRegionY();
            int width = GetRegionWidth();
            int height = GetRegionHeight();

            if (width < 0)
            {
                x = x - width;
                width = -width;
            }

            if (height < 0)
            {
                y = y - height;
                height = -height;
            }

            int rows = height / tileHeight;
            int cols = width / tileWidth;

            int startX = x;
            LTextureRegion[][] tiles = (LTextureRegion[][])CollectionUtils.XNA_CreateJaggedArray(typeof(LTextureRegion), rows, cols);
            for (int row = 0; row < rows; row++, y += tileHeight)
            {
                x = startX;
                for (int col = 0; col < cols; col++, x += tileWidth)
                {
                    tiles[row][col] = new LTextureRegion(texture, x, y, tileWidth,
                            tileHeight);
                }
            }

            return tiles;
        }

        public static LTextureRegion[][] Split(LTexture texture, int tileWidth,
                int tileHeight)
        {
            LTextureRegion region = new LTextureRegion(texture);
            return region.Split(tileWidth, tileHeight);
        }

        public void Dispose()
        {
            if (texture != null)
            {
                texture.Destroy();
            }
        }
    }
}
