using System.Collections.Generic;
using Loon.Utils;
using Loon.Core.Geom;
using Loon.Core.Graphics.OpenGL;
using System;
using Loon.Core.Graphics;
using Microsoft.Xna.Framework;
namespace Loon.Action.Sprite
{
    public class SpriteRegion : LTextureRegion
    {

        public class Animation
        {

            SpriteRegion[] keyFrames;

            public float frameDuration;

            public float animationDuration;

            public Animation(float frameDuration, List<SpriteRegion> keyFrames)
            {
                this.frameDuration = frameDuration;
                this.animationDuration = frameDuration * keyFrames.Count;
                this.keyFrames = new SpriteRegion[keyFrames.Count];
                for (int i = 0, n = keyFrames.Count; i < n; i++)
                {
                    this.keyFrames[i] = keyFrames[i];
                }
            }

            public Animation(float frameDuration, params SpriteRegion[] keyFrames)
            {
                this.frameDuration = frameDuration;
                this.keyFrames = keyFrames;
                this.animationDuration = frameDuration * keyFrames.Length;
            }

            public SpriteRegion GetKeyFrame(float stateTime, bool looping)
            {
                int frameNumber = (int)(stateTime / frameDuration);
                if (!looping)
                {
                    frameNumber = MathUtils.Min(keyFrames.Length - 1, frameNumber);
                }
                else
                {
                    frameNumber = frameNumber % keyFrames.Length;
                }
                return keyFrames[frameNumber];
            }

            public bool IsAnimationFinished(float stateTime)
            {
                int frameNumber = (int)(stateTime / frameDuration);
                return keyFrames.Length - 1 < frameNumber;
            }
        }

        private float[] vertices = new float[SpriteBatch.SPRITE_SIZE];
        private Color color = new Color(1f, 1f, 1f, 1f);
        private float x, y;
        internal float width, height;
        private float originX, originY;
        private float rotation;
        private float scaleX = 1, scaleY = 1;
        private bool dirty = true;
        private RectBox bounds = new RectBox();

        public SpriteRegion()
        {
            SetColor(1f, 1f, 1f, 1f);
        }

        public SpriteRegion(string file)
            : this(LTextures.LoadTexture(file))
        {

        }

        public SpriteRegion(LTexture texture)
            : this(texture, 0, 0, texture.GetWidth(), texture.GetHeight())
        {

        }

        public SpriteRegion(LTexture texture, int srcWidth, int srcHeight)
            : this(texture, 0, 0, srcWidth, srcHeight)
        {

        }

        public SpriteRegion(string file, int srcX, int srcY, int srcWidth,
                int srcHeight)
            : this(LTextures.LoadTexture(file), srcX, srcY, srcWidth, srcHeight)
        {

        }

        public SpriteRegion(LTexture texture, int srcX, int srcY, int srcWidth,
                int srcHeight)
        {
            if (texture == null)
            {
                throw new ArgumentException("texture cannot be null.");
            }
            this.texture = texture;
            SetRegion(srcX, srcY, srcWidth, srcHeight);
            SetColor(1f, 1f, 1f, 1f);
            SetSize(Math.Abs(srcWidth), Math.Abs(srcHeight));
            SetOrigin(width / 2, height / 2);
        }

        public SpriteRegion(LTextureRegion region)
        {
            SetRegion(region);
            SetColor(1f, 1f, 1f, 1f);
            SetSize(Math.Abs(region.GetRegionWidth()),
                    Math.Abs(region.GetRegionHeight()));
            SetOrigin(width / 2, height / 2);
        }

        public SpriteRegion(LTextureRegion region, int srcX, int srcY,
                int srcWidth, int srcHeight)
        {
            SetRegion(region, srcX, srcY, srcWidth, srcHeight);
            SetColor(1f, 1f, 1f, 1f);
            SetSize(Math.Abs(srcWidth), Math.Abs(srcHeight));
            SetOrigin(width / 2, height / 2);
        }

        public SpriteRegion(SpriteRegion sprite)
        {
            Set(sprite);
        }

        public void Set(SpriteRegion sprite)
        {
            if (sprite == null)
            {
                throw new ArgumentException("sprite cannot be null.");
            }
            texture = sprite.texture;
            xOff = sprite.xOff;
            yOff = sprite.yOff;
            widthRatio = sprite.widthRatio;
            heightRatio = sprite.heightRatio;
            x = sprite.x;
            y = sprite.y;
            width = sprite.width;
            height = sprite.height;
            originX = sprite.originX;
            originY = sprite.originY;
            rotation = sprite.rotation;
            scaleX = sprite.scaleX;
            scaleY = sprite.scaleY;
            SetColor(sprite.color);
            dirty = sprite.dirty;
        }

        public void SetBounds(float x, float y, float width, float height)
        {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            if (dirty)
            {
                return;
            }
            float x2 = x + width;
            float y2 = y + height;

            if (rotation != 0 || scaleX != 1 || scaleY != 1)
            {
                dirty = true;
            }
        }

        public void SetSize(float width, float height)
        {
            this.width = width;
            this.height = height;

            if (dirty)
            {
                return;
            }


            if (rotation != 0 || scaleX != 1 || scaleY != 1)
            {
                dirty = true;
            }
        }

        public void SetPosition(float x, float y)
        {
            Translate(x - this.x, y - this.y);
        }

        public void SetX(float x)
        {
            TranslateX(x - this.x);
        }

        public void SetY(float y)
        {
            TranslateY(y - this.y);
        }

        public void TranslateX(float xAmount)
        {
            this.x += xAmount;

            if (dirty)
            {
                return;
            }

            float[] vertices = this.vertices;
            vertices[0] += xAmount;
            vertices[5] += xAmount;
            vertices[10] += xAmount;
            vertices[15] += xAmount;
        }

        public void TranslateY(float yAmount)
        {
            y += yAmount;

            if (dirty)
            {
                return;
            }

            float[] vertices = this.vertices;
            vertices[1] += yAmount;
            vertices[6] += yAmount;
            vertices[11] += yAmount;
            vertices[16] += yAmount;
        }

        public void Translate(float xAmount, float yAmount)
        {
            x += xAmount;
            y += yAmount;

            if (dirty)
            {
                return;
            }

            float[] vertices = this.vertices;
            vertices[0] += xAmount;
            vertices[1] += yAmount;

            vertices[5] += xAmount;
            vertices[6] += yAmount;

            vertices[10] += xAmount;
            vertices[11] += yAmount;

            vertices[15] += xAmount;
            vertices[16] += yAmount;
        }

        public void SetColor(Color tint)
        {
            this.color.PackedValue = tint.PackedValue;
        }

        public void SetColor(float r, float g, float b, float a)
        {
            color.R = (byte)(r * 255);
            color.G = (byte)(g * 255);
            color.B = (byte)(b * 255);
            color.A = (byte)(a * 255);
        }

        public void SetOrigin(float originX, float originY)
        {
            this.originX = originX;
            this.originY = originY;
            dirty = true;
        }

        public void SetRotation(float degrees)
        {
            this.rotation = degrees;
            dirty = true;
        }

        public void Rotate(float degrees)
        {
            rotation += degrees;
            dirty = true;
        }

        public void Rotate90(bool clockwise)
        {
            float[] vertices = this.vertices;

            if (clockwise)
            {
                float temp = vertices[4];
                vertices[4] = vertices[19];
                vertices[19] = vertices[14];
                vertices[14] = vertices[9];
                vertices[9] = temp;

                temp = vertices[3];
                vertices[3] = vertices[18];
                vertices[18] = vertices[13];
                vertices[13] = vertices[8];
                vertices[8] = temp;
            }
            else
            {
                float temp = vertices[4];
                vertices[4] = vertices[9];
                vertices[9] = vertices[14];
                vertices[14] = vertices[19];
                vertices[19] = temp;

                temp = vertices[3];
                vertices[3] = vertices[8];
                vertices[8] = vertices[13];
                vertices[13] = vertices[18];
                vertices[18] = temp;
            }
        }

        public void SetScale(float scaleXY)
        {
            this.scaleX = scaleXY;
            this.scaleY = scaleXY;
            dirty = true;
        }

        public void SetScale(float scaleX, float scaleY)
        {
            this.scaleX = scaleX;
            this.scaleY = scaleY;
            dirty = true;
        }

        public void Scale(float amount)
        {
            this.scaleX += amount;
            this.scaleY += amount;
            dirty = true;
        }

        public float[] GetVertices()
        {
            if (dirty)
            {
                dirty = false;

                float[] vertices = this.vertices;
                float localX = -originX;
                float localY = -originY;
                float localX2 = localX + width;
                float localY2 = localY + height;
                float worldOriginX = this.x - localX;
                float worldOriginY = this.y - localY;
                if (scaleX != 1 || scaleY != 1)
                {
                    localX *= scaleX;
                    localY *= scaleY;
                    localX2 *= scaleX;
                    localY2 *= scaleY;
                }
                if (rotation != 0)
                {
                    float cos = MathUtils.CosDeg(rotation);
                    float sin = MathUtils.SinDeg(rotation);
                    float localXCos = localX * cos;
                    float localXSin = localX * sin;
                    float localYCos = localY * cos;
                    float localYSin = localY * sin;
                    float localX2Cos = localX2 * cos;
                    float localX2Sin = localX2 * sin;
                    float localY2Cos = localY2 * cos;
                    float localY2Sin = localY2 * sin;

                    float x1 = localXCos - localYSin + worldOriginX;
                    float y1 = localYCos + localXSin + worldOriginY;
                    vertices[0] = x1;
                    vertices[1] = y1;

                    float x2 = localXCos - localY2Sin + worldOriginX;
                    float y2 = localY2Cos + localXSin + worldOriginY;
                    vertices[5] = x2;
                    vertices[6] = y2;

                    float x3 = localX2Cos - localY2Sin + worldOriginX;
                    float y3 = localY2Cos + localX2Sin + worldOriginY;
                    vertices[10] = x3;
                    vertices[11] = y3;

                    vertices[15] = x1 + (x3 - x2);
                    vertices[16] = y3 - (y2 - y1);
                }
                else
                {
                    float x1 = localX + worldOriginX;
                    float y1 = localY + worldOriginY;
                    float x2 = localX2 + worldOriginX;
                    float y2 = localY2 + worldOriginY;

                    vertices[0] = x1;
                    vertices[1] = y1;

                    vertices[5] = x1;
                    vertices[6] = y2;

                    vertices[10] = x2;
                    vertices[11] = y2;

                    vertices[15] = x2;
                    vertices[16] = y1;
                }
            }
            return this.vertices;
        }

        public RectBox GetBoundingRectangle()
        {
            float[] vertices = GetVertices();

            float minx = vertices[0];
            float miny = vertices[1];
            float maxx = vertices[0];
            float maxy = vertices[1];

            minx = (minx > vertices[5]) ? vertices[5] : minx;
            minx = (minx > vertices[10]) ? vertices[10] : minx;
            minx = (minx > vertices[15]) ? vertices[15] : minx;

            maxx = (maxx < vertices[5]) ? vertices[5] : maxx;
            maxx = (maxx < vertices[10]) ? vertices[10] : maxx;
            maxx = (maxx < vertices[15]) ? vertices[15] : maxx;

            miny = (miny > vertices[6]) ? vertices[6] : miny;
            miny = (miny > vertices[11]) ? vertices[11] : miny;
            miny = (miny > vertices[16]) ? vertices[16] : miny;

            maxy = (maxy < vertices[6]) ? vertices[6] : maxy;
            maxy = (maxy < vertices[11]) ? vertices[11] : maxy;
            maxy = (maxy < vertices[16]) ? vertices[16] : maxy;

            bounds.x = minx;
            bounds.y = miny;
            bounds.width = (int)(maxx - minx);
            bounds.height = (int)(maxy - miny);

            return bounds;
        }

        public void Draw(SpriteBatch batch)
        {
            batch.Draw(this, x, y);
        }

        private Color old = new Color();

        public void Draw(SpriteBatch batch, float alpha)
        {
            old.PackedValue = color.PackedValue;
            float oldAlpha = color.A;
            old.A = (byte)(255 * alpha);
            SetColor(color);
            Draw(batch);
            color.A = (byte)(255 * oldAlpha);
            SetColor(color);
        }

        public float GetX()
        {
            return x;
        }

        public float GetY()
        {
            return y;
        }

        public float GetWidth()
        {
            return width;
        }

        public float GetHeight()
        {
            return height;
        }

        public float GetOriginX()
        {
            return originX;
        }

        public float GetOriginY()
        {
            return originY;
        }

        public float GetRotation()
        {
            return rotation;
        }

        public float GetScaleX()
        {
            return scaleX;
        }

        public float GetScaleY()
        {
            return scaleY;
        }

        public Color GetColor()
        {
            return color;
        }

        public override void SetRegion(float u, float v, float u2, float v2)
        {
            this.xOff = u;
            this.yOff = v;
            this.widthRatio = u2;
            this.heightRatio = v2;

            float[] vertices = this.vertices;
            vertices[3] = u;
            vertices[4] = v;

            vertices[8] = u;
            vertices[9] = v2;

            vertices[13] = u2;
            vertices[14] = v2;

            vertices[18] = u2;
            vertices[19] = v;
        }

        public override void Flip(bool x, bool y)
        {
            base.Flip(x, y);
            float[] vertices = this.vertices;
            if (x)
            {
                float temp = vertices[3];
                vertices[3] = vertices[13];
                vertices[13] = temp;
                temp = vertices[8];
                vertices[8] = vertices[18];
                vertices[18] = temp;
            }
            if (y)
            {
                float temp = vertices[4];
                vertices[4] = vertices[14];
                vertices[14] = temp;
                temp = vertices[9];
                vertices[9] = vertices[19];
                vertices[19] = temp;
            }
        }

        public override void Scroll(float xAmount, float yAmount)
        {
            float[] vertices = this.vertices;
            if (xAmount != 0)
            {
                float u = (vertices[3] + xAmount) % 1;
                float u2 = u + width / texture.GetWidth();
                this.xOff = u;
                this.widthRatio = u2;
                vertices[3] = u;
                vertices[8] = u;
                vertices[13] = u2;
                vertices[18] = u2;
            }
            if (yAmount != 0)
            {
                float v = (vertices[9] + yAmount) % 1;
                float v2 = v + height / texture.GetHeight();
                this.yOff = v;
                this.heightRatio = v2;
                vertices[4] = v2;
                vertices[9] = v;
                vertices[14] = v;
                vertices[19] = v2;
            }
        }
    }

}
