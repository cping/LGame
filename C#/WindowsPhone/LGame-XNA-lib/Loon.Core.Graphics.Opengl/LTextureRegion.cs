using System;
using Loon.Utils;
namespace Loon.Core.Graphics.Opengl
{
    public class LTextureRegion : LRelease
    {

        public LTexture texture;

        public float xOff, yOff;

        public float widthRatio = 1f, heightRatio = 1f;

        public LTextureRegion()
        {
        }

        public LTextureRegion(string file)
            : this(file, Loon.Core.Graphics.Opengl.LTexture.Format.LINEAR)
        {

        }

        public LTextureRegion(string file, Loon.Core.Graphics.Opengl.LTexture.Format f)
            : this(LTextures.LoadTexture(file, f))
        {

        }

        public LTextureRegion(string file, int x, int y, int width, int height)
            : this(LTextures.LoadTexture(file), x, y, width, height)
        {

        }

        public LTextureRegion(LTexture texture)
        {
            if (texture == null)
            {
                throw new Exception("texture cannot be null.");
            }
            this.texture = texture;
            SetRegion(0, 0, texture.texWidth, texture.texHeight);
        }

        public LTextureRegion(LTexture texture, int width, int height)
        {
            if (texture == null)
            {
                throw new Exception("texture cannot be null.");
            }
            this.texture = texture;
            SetRegion(0, 0, width, height);
        }

        public LTextureRegion(LTexture texture, int x, int y, int width, int height)
        {
            if (texture == null)
            {
                throw new Exception("texture cannot be null.");
            }
            this.texture = texture;
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

        public virtual void SetRegion(LTexture texture)
        {
            this.texture = texture;
            SetRegion(0, 0, texture.Width, texture.Height);
        }

        public virtual void SetRegion(int x, int y, int width, int height)
        {
            float invTexWidth = (1f / texture.Width) * texture.widthRatio;
            float invTexHeight = (1f / texture.Height) * texture.heightRatio;
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

        public virtual void SetRegion(LTextureRegion region)
        {
            texture = region.texture;
            SetRegion(region.xOff, region.yOff, region.widthRatio,
                    region.heightRatio);
        }

        public virtual void SetRegion(LTextureRegion region, int x, int y, int width,
                int height)
        {
            texture = region.texture;
            SetRegion(region.GetRegionX() + x, region.GetRegionY() + y, width,
                    height);
        }

        public virtual LTexture GetTexture()
        {
            return texture;
        }

        public virtual void SetTexture(LTexture texture)
        {
            this.texture = texture;
        }

        public virtual int GetRegionX()
        {
            return (int)(xOff * texture.texWidth);
        }

        public virtual int GetRegionY()
        {
            return (int)(yOff * texture.texHeight);
        }

        public virtual int GetRegionWidth()
        {
            int result = MathUtils.Round((widthRatio - xOff)
                    * texture.GetTextureWidth());
            return result > 0 ? result : -result;
        }

        public virtual int GetRegionHeight()
        {
            int result = MathUtils.Round((heightRatio - yOff)
                    * texture.GetTextureHeight());
            return result > 0 ? result : -result;
        }

        public virtual void Flip(bool x, bool y)
        {
            if (x)
            {
                float temp = xOff;
                xOff = widthRatio;
                widthRatio = temp;
            }
            if (y)
            {
                float temp = yOff;
                yOff = heightRatio;
                heightRatio = temp;
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

        public virtual LTextureRegion[][] Split(int tileWidth, int tileHeight)
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

        public virtual void Dispose()
        {
            if (texture != null)
            {
                texture.Destroy();
            }
        }
    }
}
