using Loon.Core.Graphics.Opengl;
using System.Collections.Generic;
using Loon.Utils;
using Loon.Core.Graphics;
using Loon.Core.Geom;
using System;
using Microsoft.Xna.Framework.Graphics;
namespace Loon.Action.Sprite
{
    public class SpriteRegion : LTextureRegion
    {

        public class Animation
        {

            LTextureRegion[] keyFrames;

            public float frameDuration;

            public float animationDuration;

            public Animation(float frameDuration, List<LTextureRegion> keyFrames)
            {
                this.frameDuration = frameDuration;
                this.animationDuration = frameDuration * keyFrames.Count;
                this.keyFrames = new LTextureRegion[keyFrames.Count];
                for (int i = 0, n = keyFrames.Count; i < n; i++)
                {
                    this.keyFrames[i] = keyFrames[i];
                }
            }

            public Animation(float frameDuration, params LTextureRegion[] keyFrames)
            {
                this.frameDuration = frameDuration;
                this.keyFrames = keyFrames;
                this.animationDuration = frameDuration * keyFrames.Length;
            }

            public LTextureRegion getKeyFrame(float stateTime, bool looping)
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

            public bool isAnimationFinished(float stateTime)
            {
                int frameNumber = (int)(stateTime / frameDuration);
                return keyFrames.Length - 1 < frameNumber;
            }
        }

        private VertexPositionColorTexture[] vertices = new VertexPositionColorTexture[4];
        private LColor color = new LColor(1f, 1f, 1f, 1f);
        private float x, y;
        float width, height;
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

        public SpriteRegion(string file, Loon.Core.Graphics.Opengl.LTexture.Format format)
            : this(LTextures.LoadTexture(file, format))
        {

        }

        public SpriteRegion(LTexture texture)
            : this(texture, 0, 0, texture.Width, texture.Height)
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
                throw new Exception("texture cannot be null.");
            }
            this.texture = texture;
            SetRegion(srcX, srcY, srcWidth, srcHeight);
            SetColor(1f, 1f, 1f, 1f);
            setSize(Math.Abs(srcWidth), Math.Abs(srcHeight));
            setOrigin(width / 2, height / 2);
        }

        public SpriteRegion(LTextureRegion region)
        {
            SetRegion(region);
            SetColor(1f, 1f, 1f, 1f);
            setSize(Math.Abs(region.GetRegionWidth()),
                    Math.Abs(region.GetRegionHeight()));
            setOrigin(width / 2, height / 2);
        }

        public SpriteRegion(LTextureRegion region, int srcX, int srcY,
                int srcWidth, int srcHeight)
        {
            SetRegion(region, srcX, srcY, srcWidth, srcHeight);
            SetColor(1f, 1f, 1f, 1f);
            setSize(Math.Abs(srcWidth), Math.Abs(srcHeight));
            setOrigin(width / 2, height / 2);
        }

        public SpriteRegion(SpriteRegion sprite)
        {
            set(sprite);
        }

        public void set(SpriteRegion sprite)
        {
            if (sprite == null)
            {
                throw new Exception("sprite cannot be null.");
            }
            Array.Copy(sprite.vertices, 0, vertices, 0, 4);
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
            color.SetColor(sprite.color);
            dirty = sprite.dirty;
        }

        public void setBounds(float x, float y, float width, float height)
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
            VertexPositionColorTexture[] vertices = this.vertices;
            vertices[0].Position.X = x;
            vertices[0].Position.Y = y;

            vertices[1].Position.X = x;
            vertices[1].Position.Y = y2;

            vertices[2].Position.X = x2;
            vertices[2].Position.Y = y2;

            vertices[3].Position.X = x2;
            vertices[3].Position.Y = y;

            if (rotation != 0 || scaleX != 1 || scaleY != 1)
            {
                dirty = true;
            }
        }

        public void setSize(float width, float height)
        {
            this.width = width;
            this.height = height;

            if (dirty)
            {
                return;
            }

            float x2 = x + width;
            float y2 = y + height;
            VertexPositionColorTexture[] vertices = this.vertices;
            vertices[0].Position.X = x;
            vertices[0].Position.Y = y;

            vertices[1].Position.X = x;
            vertices[1].Position.Y = y2;

            vertices[2].Position.X = x2;
            vertices[2].Position.Y = y2;

            vertices[3].Position.X = x2;
            vertices[3].Position.Y = y;

            if (rotation != 0 || scaleX != 1 || scaleY != 1)
            {
                dirty = true;
            }
        }

        public void setPosition(float x, float y)
        {
            translate(x - this.x, y - this.y);
        }

        public void setX(float x)
        {
            translateX(x - this.x);
        }

        public void setY(float y)
        {
            translateY(y - this.y);
        }

        public void translateX(float xAmount)
        {
            this.x += xAmount;

            if (dirty)
            {
                return;
            }

            VertexPositionColorTexture[] vertices = this.vertices;
            vertices[0].Position.X += xAmount;
            vertices[1].Position.X += xAmount;
            vertices[2].Position.X += xAmount;
            vertices[3].Position.X += xAmount;
        }

        public void translateY(float yAmount)
        {
            y += yAmount;

            if (dirty)
            {
                return;
            }

            VertexPositionColorTexture[] vertices = this.vertices;
            vertices[0].Position.Y += yAmount;
            vertices[1].Position.Y += yAmount;
            vertices[2].Position.Y += yAmount;
            vertices[3].Position.Y += yAmount;
        }

        public void translate(float xAmount, float yAmount)
        {
            x += xAmount;
            y += yAmount;

            if (dirty)
            {
                return;
            }

            VertexPositionColorTexture[] vertices = this.vertices;
            vertices[0].Position.X += xAmount;
            vertices[0].Position.Y += yAmount;

            vertices[1].Position.X += xAmount;
            vertices[1].Position.Y += yAmount;

            vertices[2].Position.X += xAmount;
            vertices[2].Position.Y += yAmount;

            vertices[3].Position.X += xAmount;
            vertices[3].Position.Y += yAmount;
        }


        public void SetColor(LColor tint)
        {
            VertexPositionColorTexture[] vertices = this.vertices;
            vertices[0].Color = tint;
            vertices[1].Color = tint;
            vertices[2].Color = tint;
            vertices[3].Color = tint;
        }

        public void SetColor(float r, float g, float b, float a)
        {
            LColor tint = new LColor(r, g, b, a);
            VertexPositionColorTexture[] vertices = this.vertices;
            vertices[0].Color = tint;
            vertices[1].Color = tint;
            vertices[2].Color = tint;
            vertices[3].Color = tint;
        }

        public void setOrigin(float originX, float originY)
        {
            this.originX = originX;
            this.originY = originY;
            dirty = true;
        }

        public void setRotation(float degrees)
        {
            this.rotation = degrees;
            dirty = true;
        }

        public void rotate(float degrees)
        {
            rotation += degrees;
            dirty = true;
        }


        public void rotate90(bool clockwise)
        {
            VertexPositionColorTexture[] vertices = this.vertices;

            if (clockwise)
            {
                float temp = vertices[0].TextureCoordinate.Y;
                vertices[0].TextureCoordinate.Y = vertices[3].TextureCoordinate.Y;
                vertices[3].TextureCoordinate.Y = vertices[2].TextureCoordinate.Y;
                vertices[2].TextureCoordinate.Y = vertices[1].TextureCoordinate.Y;
                vertices[1].TextureCoordinate.Y = temp;

                temp = vertices[0].TextureCoordinate.X;
                vertices[0].TextureCoordinate.X = vertices[3].TextureCoordinate.X;
                vertices[3].TextureCoordinate.X = vertices[2].TextureCoordinate.X;
                vertices[2].TextureCoordinate.X = vertices[1].TextureCoordinate.X;
                vertices[1].TextureCoordinate.X = temp;
            }
            else
            {
                float temp = vertices[0].TextureCoordinate.Y;
                vertices[0].TextureCoordinate.Y = vertices[1].TextureCoordinate.Y;
                vertices[1].TextureCoordinate.Y = vertices[2].TextureCoordinate.Y;
                vertices[2].TextureCoordinate.Y = vertices[3].TextureCoordinate.Y;
                vertices[3].TextureCoordinate.Y = temp;

                temp = vertices[0].TextureCoordinate.X;
                vertices[0].TextureCoordinate.X = vertices[1].TextureCoordinate.X;
                vertices[1].TextureCoordinate.X = vertices[2].TextureCoordinate.X;
                vertices[2].TextureCoordinate.X = vertices[3].TextureCoordinate.X;
                vertices[3].TextureCoordinate.X = temp;
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

        public void scale(float amount)
        {
            this.scaleX += amount;
            this.scaleY += amount;
            dirty = true;
        }

        public VertexPositionColorTexture[] getVertices()
        {
            if (dirty)
            {
                dirty = false;

                VertexPositionColorTexture[] i_vertices = this.vertices;
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
                    i_vertices[0].Position.X = x1;
                    i_vertices[0].Position.Y = y1;

                    float x2 = localXCos - localY2Sin + worldOriginX;
                    float y2 = localY2Cos + localXSin + worldOriginY;
                    i_vertices[1].Position.X = x2;
                    i_vertices[1].Position.Y = y2;

                    float x3 = localX2Cos - localY2Sin + worldOriginX;
                    float y3 = localY2Cos + localX2Sin + worldOriginY;
                    i_vertices[2].Position.X = x3;
                    i_vertices[2].Position.Y = y3;

                    i_vertices[3].Position.X = x1 + (x3 - x2);
                    i_vertices[3].Position.Y = y3 - (y2 - y1);
                }
                else
                {
                    float x1 = localX + worldOriginX;
                    float y1 = localY + worldOriginY;
                    float x2 = localX2 + worldOriginX;
                    float y2 = localY2 + worldOriginY;

                    i_vertices[0].Position.X = x1;
                    i_vertices[0].Position.Y = y1;

                    i_vertices[1].Position.X = x1;
                    i_vertices[1].Position.Y = y2;

                    i_vertices[2].Position.X = x2;
                    i_vertices[2].Position.Y = y2;

                    i_vertices[3].Position.X = x2;
                    i_vertices[3].Position.Y = y1;
                }
            }
            return this.vertices;
        }

        public RectBox getBoundingRectangle()
        {
            VertexPositionColorTexture[] vertices = getVertices();

            float minx = vertices[0].Position.X;
            float miny = vertices[0].Position.Y;
            float maxx = vertices[0].Position.X;
            float maxy = vertices[0].Position.Y;

            minx = minx > vertices[1].Position.X ? vertices[1].Position.X : minx;
            minx = minx > vertices[2].Position.X ? vertices[2].Position.X : minx;
            minx = minx > vertices[3].Position.X ? vertices[3].Position.Y : minx;

            maxx = maxx < vertices[1].Position.X ? vertices[1].Position.X : maxx;
            maxx = maxx < vertices[2].Position.X ? vertices[2].Position.X : maxx;
            maxx = maxx < vertices[3].Position.X ? vertices[3].Position.X : maxx;

            miny = miny > vertices[1].Position.Y ? vertices[1].Position.Y : miny;
            miny = miny > vertices[2].Position.Y ? vertices[2].Position.Y : miny;
            miny = miny > vertices[3].Position.Y ? vertices[3].Position.Y : miny;

            maxy = maxy < vertices[1].Position.Y ? vertices[1].Position.Y : maxy;
            maxy = maxy < vertices[2].Position.Y ? vertices[2].Position.Y : maxy;
            maxy = maxy < vertices[3].Position.Y ? vertices[3].Position.Y : maxy;

            bounds.x = minx;
            bounds.y = miny;
            bounds.width = (int)(maxx - minx);
            bounds.height = (int)(maxy - miny);

            return bounds;
        }

        public void Draw(SpriteBatch batch)
        {
            batch.Draw(texture, getVertices(), 0, 4);
        }

        public void Draw(SpriteBatch batch, float alpha)
        {
            LColor color = GetColor();
            float oldAlpha = color.a;
            color.a *= alpha;
            SetColor(color);
            Draw(batch);
            color.a = oldAlpha;
            SetColor(color);
        }

        public float getX()
        {
            return x;
        }

        public float getY()
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

        public float getOriginX()
        {
            return originX;
        }

        public float getOriginY()
        {
            return originY;
        }

        public float getRotation()
        {
            return rotation;
        }

        public float getScaleX()
        {
            return scaleX;
        }

        public float getScaleY()
        {
            return scaleY;
        }

        public LColor GetColor()
        {
            return new LColor(color);
        }

        public override void SetRegion(float u, float v, float u2, float v2)
        {
            this.xOff = u;
            this.yOff = v;
            this.widthRatio = u2;
            this.heightRatio = v2;

            VertexPositionColorTexture[] vertices = this.vertices;
            vertices[0].TextureCoordinate.X = u;
            vertices[0].TextureCoordinate.Y = v;

            vertices[1].TextureCoordinate.X = u;
            vertices[1].TextureCoordinate.Y = v2;

            vertices[2].TextureCoordinate.X = u2;
            vertices[2].TextureCoordinate.Y = v2;

            vertices[3].TextureCoordinate.X = u2;
            vertices[3].TextureCoordinate.Y = v;
        }

        public override void Flip(bool x, bool y)
        {
            base.Flip(x, y);
            VertexPositionColorTexture[] vertices = this.vertices;
            if (x)
            {
                float temp = vertices[0].TextureCoordinate.X;
                vertices[0].TextureCoordinate.X = vertices[2].TextureCoordinate.X;
                vertices[2].TextureCoordinate.X = temp;
                temp = vertices[1].TextureCoordinate.X;
                vertices[1].TextureCoordinate.X = vertices[3].TextureCoordinate.X;
                vertices[3].TextureCoordinate.X = temp;
            }
            if (y)
            {
                float temp = vertices[0].TextureCoordinate.Y;
                vertices[0].TextureCoordinate.Y = vertices[2].TextureCoordinate.Y;
                vertices[2].TextureCoordinate.Y = temp;
                temp = vertices[1].TextureCoordinate.Y;
                vertices[1].TextureCoordinate.Y = vertices[3].TextureCoordinate.Y;
                vertices[3].TextureCoordinate.Y = temp;
            }
        }

        public override void Scroll(float xAmount, float yAmount)
        {
            VertexPositionColorTexture[] vertices = this.vertices;
            if (xAmount != 0)
            {
                float u = (vertices[0].TextureCoordinate.X + xAmount) % 1;
                float u2 = u + width / texture.Width;
                this.xOff = u;
                this.widthRatio = u2;
                vertices[0].TextureCoordinate.X = u;
                vertices[1].TextureCoordinate.X = u;
                vertices[2].TextureCoordinate.X = u2;
                vertices[3].TextureCoordinate.X = u2;
            }
            if (yAmount != 0)
            {
                float v = (vertices[0].TextureCoordinate.Y + yAmount) % 1;
                float v2 = v + height / texture.Height;
                this.yOff = v;
                this.heightRatio = v2;
                vertices[0].TextureCoordinate.Y = v2;
                vertices[1].TextureCoordinate.Y = v;
                vertices[2].TextureCoordinate.Y = v;
                vertices[3].TextureCoordinate.Y = v2;
            }
        }
    }
}
