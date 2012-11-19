using System;
using System.Collections.Generic;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework;
using Loon.Utils;

namespace Loon.Core.Graphics.OpenGL
{
    public class LTextureBatch : LRelease
    {

        private Rectangle dst = new Rectangle();

        private Rectangle src = new Rectangle();

        internal bool useBegin;

        internal SpriteBatch batch;

        private LTexture texture;

        public LTexture Texture
        {
            get
            {
                return texture;
            }
        }

        public int Width
        {
            get
            {
                return texture.Width;
            }
        }

        public int Height
        {
            get
            {
                return texture.Height;
            }
        }

        public LTextureBatch(LTexture tex2d)
        {
            this.texture = tex2d;
            this.batch = new SpriteBatch(GLEx.Device);
        }

        public void GLBegin(BlendState state)
        {
            texture.LoadTexture();
            batch.Begin(SpriteSortMode.Deferred, state, null, null, null, null, GLEx.cemera.viewMatrix);
            useBegin = true;
        }

        public void GLBegin()
        {
            texture.LoadTexture();
            if (texture.isOpaque)
            {
                batch.Begin(SpriteSortMode.Deferred, BlendState.Opaque, null, null, null, null, GLEx.cemera.viewMatrix);
            }
            else
            {
                if (texture.isExt)
                {
                    batch.Begin(SpriteSortMode.Deferred, BlendState.NonPremultiplied, null, null, null, null, GLEx.cemera.viewMatrix);
                }
                else
                {
                    batch.Begin(SpriteSortMode.Deferred, BlendState.AlphaBlend, null, null, null, null, GLEx.cemera.viewMatrix);
                }
            }
            useBegin = true;
        }

        public void GLEnd()
        {
            batch.End();
            useBegin = false;
        }

        public void Draw(float x, float y, LColor c)
        {
            Draw(x, y, texture.Width, texture.Height, 0, c);
        }

        public void Draw(float x, float y, float rotation, LColor c)
        {
            Draw(x, y, texture.Width, texture.Height, rotation, c);
        }

        public void Draw(float x, float y, float width, float height, LColor c)
        {
            Draw(x, y, width, height, 0f, c);
        }

        public void Draw(float x, float y, float width, float height, float rotation, LColor c)
        {
            Draw(x, y, width, height, 0f, 0f, texture.Width, texture.Height, rotation, c);
        }

        public void Draw(float x, float y, float width,
            float height, float srcX, float srcY, float srcWidth,
            float srcHeight, float rotation, LColor c)
        {
            Draw(x, y, width, height, srcX, srcY, srcWidth, srcHeight, rotation, c.Color);
        }

        public void Draw(float x, float y, float width,
            float height, float srcX, float srcY, float srcWidth,
            float srcHeight, float rotation, Color c)
        {
            if (texture.IsChild)
            {
                float xOff = ((srcX / texture.width) * texture.widthRatio) + texture.xOff;
                float yOff = ((srcY / texture.height) * texture.heightRatio) + texture.yOff;
                float widthRatio = ((srcWidth / texture.width) * texture.widthRatio);
                float heightRatio = ((srcHeight / texture.height) * texture.heightRatio);
                int newX = (int)(xOff * texture.texWidth);
                int newY = (int)(yOff * texture.texHeight);
                int newWidth = (int)((texture.texWidth * widthRatio)) - newX;
                int newHeight = (int)((texture.texHeight * heightRatio)) - newY;
                dst.X = (int)x;
                dst.Y = (int)y;
                dst.Width = (int)width;
                dst.Height = (int)height;
                src.X = newX;
                src.Y = newY;
                src.Width = newWidth;
                src.Height = newHeight;
            }
            else
            {
                dst.X = (int)x;
                dst.Y = (int)y;
                dst.Width = (int)width;
                dst.Height = (int)height;
                src.X = (int)srcX;
                src.Y = (int)srcY;
                src.Width = (int)(srcWidth - srcX);
                src.Height = (int)(srcHeight - srcY);
            }
            if (c == null)
            {
                c = Color.White;
            }
            if (rotation != 0)
            {
                float centerX = src.Width / 2;
                float centerY = src.Height / 2;
                dst.X += (int)width / 2;
                dst.Y += (int)height / 2;
                Vector2 postion = new Vector2(centerX, centerY);
                batch.Draw(texture.Texture, dst, src, c, MathUtils.ToRadians(rotation), postion, SpriteEffects.None, 0);
            }
            else
            {
                batch.Draw(texture.Texture, dst, src, c);
            }
        }

        public void Dispose()
        {
            this.useBegin = false;
        }

    }
}
