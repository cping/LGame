using Loon.Core;
using Loon.Core.Graphics;
using Loon.Core.Graphics.Opengl;
using Microsoft.Xna.Framework.Graphics;
using System;
using Loon.Core.Geom;
using Loon.Utils;
using Microsoft.Xna.Framework;
namespace Loon.Action.Sprite
{

    public class BlendState
    {
        public static readonly BlendState NonPremultiplied = new BlendState(0x0);
        public static readonly BlendState Additive = new BlendState(0x1);
        public static readonly BlendState AlphaBlend = new BlendState(0x2);
        public static readonly BlendState Opaque = new BlendState(0x3);

        protected internal int _code = 0;

        private BlendState(int c)
        {
            this._code = c;
        }

        public override int GetHashCode()
        {
            return _code;
        }

        public static implicit operator BlendState(Microsoft.Xna.Framework.Graphics.BlendState blend)
        {
            if (blend == Microsoft.Xna.Framework.Graphics.BlendState.NonPremultiplied)
            {
                return BlendState.NonPremultiplied;
            }
            if (blend == Microsoft.Xna.Framework.Graphics.BlendState.Additive)
            {
                return BlendState.Additive;
            }
            else if (blend == Microsoft.Xna.Framework.Graphics.BlendState.AlphaBlend)
            {
                return BlendState.AlphaBlend;
            }
            else if (blend == Microsoft.Xna.Framework.Graphics.BlendState.Opaque)
            {
                return BlendState.Opaque;
            }
            return BlendState.NonPremultiplied;
        }

    }

    public class SpriteEffects
    {

        public static readonly SpriteEffects None = new SpriteEffects(0x0);
        public static readonly SpriteEffects FlipHorizontally = new SpriteEffects(0x1);
        public static readonly SpriteEffects FlipVertically = new SpriteEffects(0x2);

        protected internal int _code = 0;

        private SpriteEffects(int c)
        {
            this._code = c;
        }

        public override int GetHashCode()
        {
            return _code;
        }

        public static implicit operator SpriteEffects(Microsoft.Xna.Framework.Graphics.SpriteEffects eff)
        {
            switch (eff)
            {
                case Microsoft.Xna.Framework.Graphics.SpriteEffects.FlipHorizontally:
                    return FlipHorizontally;
                case Microsoft.Xna.Framework.Graphics.SpriteEffects.FlipVertically:
                    return FlipVertically;
                default:
                    return None;
            }
        }
    }

    public class SpriteBatch : LRelease
    {

        public static int VERTEX_SIZE = 2 + 1 + 2;

        public static int SPRITE_SIZE = 4 * VERTEX_SIZE;

        private LTexture lastTexture = null;

        private int idx = 0;

        private VertexPositionColorTexture[] vertices;

        private short[] indices;

        private bool drawing = false, lockSubmit = false;

        public int renderCalls = 0;

        public int totalRenderCalls = 0;

        public int maxSpritesInBatch = 0;

        private float alpha = 1f;

        private float invTexWidth;

        private float invTexHeight;


        private BlendState lastBlendState = BlendState.NonPremultiplied;

        public SpriteBatch()
            : this(3000)
        {

        }

        public SpriteBatch(int size)
            : this(size, 1)
        {

        }

        public SpriteBatch(int size, int buffers)
        {
            this.vertices = new VertexPositionColorTexture[size * SPRITE_SIZE];
            int len = size * 6;
            this.indices = new short[len];
            short j = 0;
            for (int i = 0; i < len; i += 6, j += 4)
            {
                indices[i + 0] = (short)(j + 0);
                indices[i + 1] = (short)(j + 1);
                indices[i + 2] = (short)(j + 2);
                indices[i + 3] = (short)(j + 2);
                indices[i + 4] = (short)(j + 3);
                indices[i + 5] = (short)(j + 0);
            }
        }

        public void HalfAlpha()
        {
            m_color.PackedValue = 2147483647;
            alpha = 0.5f;
        }

        public void ResetColor()
        {
            m_color.PackedValue = 4294967295;
            alpha = 1f;
        }

        public void DrawSpriteBounds(SpriteRegion sprite, LColor color)
        {
            VertexPositionColorTexture[] vertices = sprite.getVertices();

            float x1 = vertices[0].Position.X;
            float y1 = vertices[0].Position.Y;

            float x2 = vertices[1].Position.X;
            float y2 = vertices[1].Position.Y;

            float x3 = vertices[2].Position.X;
            float y3 = vertices[2].Position.Y;

            float x4 = vertices[3].Position.X;
            float y4 = vertices[3].Position.Y;

            Submit();
            GLEx gl = GLEx.Self;
            LColor old = gl.GetColor();
            gl.SetColor(color);
            gl.DrawLine(x1, y1, x2, y2);
            gl.DrawLine(x2, y2, x3, y3);
            gl.DrawLine(x3, y3, x4, y4);
            gl.DrawLine(x4, y4, x1, y1);
            gl.SetColor(old);
        }

        public void Draw(Shape shape)
        {
            Submit();
            GLEx gl = GLEx.Self;
            LColor old = gl.GetColor();
            gl.SetColor(m_color);
            gl.Draw(shape);
            gl.SetColor(old);
        }

        public void Fill(Shape shape)
        {
            Submit();
            GLEx gl = GLEx.Self;
            LColor old = gl.GetColor();
            gl.SetColor(m_color);
            gl.Fill(shape);
            gl.SetColor(old);
        }

        public void FillPolygon(float[] xPoints, float[] yPoints, int nPoints)
        {
            Submit();
            GLEx gl = GLEx.Self;
            LColor old = gl.GetColor();
            gl.SetColor(m_color);
            gl.FillPolygon(xPoints, yPoints, nPoints);
            gl.SetColor(old);
        }

        public void DrawPolygon(float[] xPoints, float[] yPoints, int nPoints)
        {
            Submit();
            GLEx gl = GLEx.Self;
            LColor old = gl.GetColor();
            gl.SetColor(m_color);
            gl.DrawPolygon(xPoints, yPoints, nPoints);
            gl.SetColor(old);
        }

        public void DrawOval(float x1, float y1, float width, float height)
        {
            this.DrawArc(x1, y1, width, height, 32, 0, 360);
        }

        public void FillOval(float x1, float y1, float width, float height)
        {
            this.FillArc(x1, y1, width, height, 32, 0, 360);
        }

        public void DrawArc(RectBox rect, int segments, float start, float end)
        {
            DrawArc(rect.x, rect.y, rect.width, rect.height, segments, start, end);
        }

        public void DrawArc(float x1, float y1, float width, float height,
                int segments, float start, float end)
        {
            Submit();
            GLEx gl = GLEx.Self;
            LColor old = gl.GetColor();
            gl.SetColor(m_color);
            gl.DrawArc(x1, y1, width, height, segments, start, end);
            gl.SetColor(old);
        }

        public void FillArc(float x1, float y1, float width, float height,
                float start, float end)
        {
            Submit();
            GLEx gl = GLEx.Self;
            LColor old = gl.GetColor();
            gl.SetColor(m_color);
            gl.FillArc(x1, y1, width, height, start, end);
            gl.SetColor(old);
        }

        public void FillArc(float x1, float y1, float width, float height,
                int segments, float start, float end)
        {
            Submit();
            GLEx gl = GLEx.Self;
            LColor old = gl.GetColor();
            gl.SetColor(m_color);
            gl.FillArc(x1, y1, width, height, start, end);
            gl.SetColor(old);
        }

        public void DrawRoundRect(float x, float y, float width,
                float height, int radius)
        {
            Submit();
            GLEx gl = GLEx.Self;
            LColor old = gl.GetColor();
            gl.SetColor(m_color);
            gl.DrawRoundRect(x, y, width, height, radius);
            gl.SetColor(old);
        }

        public void DrawRoundRect(float x, float y, float width,
                float height, int radius, int segs)
        {
            Submit();
            GLEx gl = GLEx.Self;
            LColor old = gl.GetColor();
            gl.SetColor(m_color);
            gl.DrawRoundRect(x, y, width, height, radius, segs);
            gl.SetColor(old);
        }

        public void FillRoundRect(float x, float y, float width,
                float height, int cornerRadius)
        {
            FillRoundRect(x, y, width, height, cornerRadius, 40);
        }

        public void FillRoundRect(float x, float y, float width,
                float height, int radius, int segs)
        {
            Submit();
            GLEx gl = GLEx.Self;
            LColor old = gl.GetColor();
            gl.SetColor(m_color);
            gl.FillRoundRect(x, y, width, height, radius, segs);
            gl.SetColor(old);
        }

        public void FillRect(float x, float y, float width, float height)
        {
            Submit();
            GLEx gl = GLEx.Self;
            LColor old = gl.GetColor();
            gl.SetColor(m_color);
            gl.FillRect(x, y, width, height);
            gl.SetColor(old);
        }

        public void DrawRect(float x, float y, float width, float height)
        {
            Submit();
            GLEx gl = GLEx.Self;
            LColor old = gl.GetColor();
            gl.SetColor(m_color);
            gl.DrawRect(x, y, width, height);
            gl.SetColor(old);
        }

        public void DrawPoint(int x, int y, LColor c)
        {
            Submit();
            GLEx gl = GLEx.Self;
            LColor old = gl.GetColor();
            gl.SetColor(c);
            gl.DrawPoint(x, y);
            gl.SetColor(old);
        }

        public void DrawPoints(float[] x, float[] y, LColor c)
        {
            Submit();
            GLEx gl = GLEx.Self;
            LColor old = gl.GetColor();
            gl.SetColor(c);
            gl.DrawPoints(x, y, y.Length);
            gl.SetColor(old);
        }

        public void DrawPoints(float[] x, float[] y)
        {
            DrawPoints(x, y, m_color);
        }

        public void DrawPoint(int x, int y)
        {
            Submit();
            GLEx gl = GLEx.Self;
            LColor old = gl.GetColor();
            gl.SetColor(m_color);
            gl.DrawPoint(x, y);
            gl.SetColor(old);
        }

        public void drawLine(float x1, float y1, float x2, float y2)
        {
            Submit();
            GLEx gl = GLEx.Self;
            LColor old = gl.GetColor();
            gl.SetColor(m_color);
            gl.DrawLine(x1, y1, x2, y2);
            gl.SetColor(old);
        }


        public void Begin()
        {
            lock (typeof(SpriteBatch))
            {

                renderCalls = 0;
                idx = 0;
                lastTexture = null;
                drawing = true;
            }
        }

        public void End()
        {
            CheckDrawing();
            if (idx > 0)
            {
                Submit();
            }
            lastTexture = null;
            idx = 0;
            drawing = false;
        }

        private void CheckTexture(LTexture texture)
        {
            CheckDrawing();
            if (!texture.isLoaded)
            {
                texture.LoadTexture();
            }
            LTexture tex2d = texture.GetParent();
            if (tex2d != null)
            {
                if (tex2d != lastTexture)
                {
                    Submit();
                    lastTexture = tex2d;
                }
                else if (idx == vertices.Length)
                {
                    Submit();
                }
                invTexWidth = (1f / texture.Width) * texture.widthRatio;
                invTexHeight = (1f / texture.Height) * texture.heightRatio;
            }
            else if (texture != lastTexture)
            {
                Submit();
                lastTexture = texture;
                invTexWidth = (1f / texture.Width) * texture.widthRatio;
                invTexHeight = (1f / texture.Height) * texture.heightRatio;
            }
            else if (idx == vertices.Length)
            {
                Submit();
            }
        }

        public void Draw(LTexture texture, float x, float y, float rotation)
        {
            Draw(texture, x, y, texture.Width / 2, texture.Height / 2,
                    texture.Width, texture.Height, 1f, 1f, rotation, 0,
                    0, texture.Width, texture.Height, false, false);
        }

        public void Draw(LTexture texture, float x, float y, float width,
                float height, float rotation)
        {
            if (rotation == 0 && texture.Width == width
                    && texture.Height == height)
            {
                Draw(texture, x, y, width, height);
            }
            else
            {
                Draw(texture, x, y, width / 2, height / 2, width, height, 1f, 1f,
                        rotation, 0, 0, texture.Width, texture.Height,
                        false, false);
            }
        }

        public void Draw(LTexture texture, float x, float y, float rotation,
                float srcX, float srcY, float srcWidth, float srcHeight)
        {
            Draw(texture, x, y, srcWidth / 2, srcHeight / 2, texture.Width,
                    texture.Height, 1f, 1f, rotation, srcX, srcY, srcWidth,
                    srcHeight, false, false);
        }

        public void Draw(LTexture texture, Vector2f pos, Vector2f origin,
                float width, float height, float scale, float rotation,
                RectBox src, bool flipX, bool flipY)
        {
            Draw(texture, pos.x, pos.y, origin.x, origin.y, width, height, scale,
                    scale, rotation, src.x, src.y, src.width, src.height, flipX,
                    flipY, false);
        }

        public void Draw(LTexture texture, Vector2f pos, Vector2f origin,
                float scale, float rotation, RectBox src, bool flipX,
                bool flipY)
        {
            Draw(texture, pos.x, pos.y, origin.x, origin.y, src.width, src.height,
                    scale, scale, rotation, src.x, src.y, src.width, src.height,
                    flipX, flipY, false);
        }

        public void Draw(LTexture texture, Vector2f pos, Vector2f origin,
                float scale, RectBox src, bool flipX, bool flipY)
        {
            Draw(texture, pos.x, pos.y, origin.x, origin.y, src.width, src.height,
                    scale, scale, 0, src.x, src.y, src.width, src.height, flipX,
                    flipY, false);
        }

        public void Draw(LTexture texture, Vector2f pos, Vector2f origin,
                RectBox src, bool flipX, bool flipY)
        {
            Draw(texture, pos.x, pos.y, origin.x, origin.y, src.width, src.height,
                    1f, 1f, 0, src.x, src.y, src.width, src.height, flipX, flipY,
                    false);
        }

        public void Draw(LTexture texture, Vector2f pos, RectBox src,
                bool flipX, bool flipY)
        {
            Draw(texture, pos.x, pos.y, src.width / 2, src.height / 2, src.width,
                    src.height, 1f, 1f, 0, src.x, src.y, src.width, src.height,
                    flipX, flipY, false);
        }

        public void Draw(LTexture texture, float x, float y, float originX,
                float originY, float width, float height, float scaleX,
                float scaleY, float rotation, float srcX, float srcY,
                float srcWidth, float srcHeight, bool flipX, bool flipY)
        {
            Draw(texture, x, y, originX, originY, width, height, scaleX, scaleY,
                    rotation, srcX, srcY, srcWidth, srcHeight, flipX, flipY, false);
        }

        public void Draw(LTexture texture, float x, float y, float originX,
                float originY, float scaleX, float scaleY, float rotation,
                float srcX, float srcY, float srcWidth, float srcHeight,
                bool flipX, bool flipY)
        {
            Draw(texture, x, y, originX, originY, srcWidth, srcHeight, scaleX,
                    scaleY, rotation, srcX, srcY, srcWidth, srcHeight, flipX,
                    flipY, false);
        }

        public void Draw(LTexture texture, Vector2f position, RectBox src,
                LColor c, float rotation, Vector2f origin, Vector2f scale,
                SpriteEffects effects)
        {
            uint old = GetIntColor();

            SetColor(c);

            bool flipX = false;
            bool flipY = false;
            switch (effects._code)
            {
                case 1:
                    flipX = true;
                    break;
                case 2:
                    flipY = true;
                    break;
                default:
                    break;
            }
            if (src != null)
            {
                Draw(texture, position.x, position.y, origin.x, origin.y,
                        src.width, src.height, scale.x, scale.y, rotation, src.x,
                        src.y, src.width, src.height, flipX, flipY, true);
            }
            else
            {
                Draw(texture, position.x, position.y, origin.x, origin.y,
                        texture.Width, texture.Height, scale.x, scale.y,
                        rotation, 0, 0, texture.Width, texture.Height,
                        flipX, flipY, true);
            }
            SetColor(old);
        }

        public void Draw(LTexture texture, Vector2f position, RectBox src,
                LColor c, float rotation, float sx, float sy, float scale,
                SpriteEffects effects)
        {

            if (src == null && rotation == 0 && scale == 1f && sx == 0 && sy == 0)
            {
                Draw(texture, position, c);
                return;
            }

            uint old = GetIntColor();
            SetColor(c);
            bool flipX = false;
            bool flipY = false;
            switch (effects._code)
            {
                case 1:
                    flipX = true;
                    break;
                case 2:
                    flipY = true;
                    break;
                default:
                    break;
            }
            if (src != null)
            {
                Draw(texture, position.x, position.y, sx, sy, src.width,
                        src.height, scale, scale, rotation, src.x, src.y,
                        src.width, src.height, flipX, flipY, true);
            }
            else
            {
                Draw(texture, position.x, position.y, sx, sy, texture.Width,
                        texture.Height, scale, scale, rotation, 0, 0,
                        texture.Width, texture.Height, flipX, flipY, true);
            }
            SetColor(old);
        }

        public void Draw(LTexture texture, Vector2f position, RectBox src,
                LColor c, float rotation, Vector2f origin, float scale,
                SpriteEffects effects)
        {
            uint old = GetIntColor();
            SetColor(c);
            bool flipX = false;
            bool flipY = false;
            switch (effects._code)
            {
                case 1:
                    flipX = true;
                    break;
                case 2:
                    flipY = true;
                    break;
                default:
                    break;
            }
            if (src != null)
            {
                Draw(texture, position.x, position.y, origin.x, origin.y,
                        src.width, src.height, scale, scale, rotation, src.x,
                        src.y, src.width, src.height, flipX, flipY, true);
            }
            else
            {
                Draw(texture, position.x, position.y, origin.x, origin.y,
                        texture.Width, texture.Height, scale, scale,
                        rotation, 0, 0, texture.Width, texture.Height,
                        flipX, flipY, true);
            }
            SetColor(old);
        }

        public void Draw(LTexture texture, float px, float py, float srcX,
                float srcY, float srcWidth, float srcHeight, LColor c,
                float rotation, float originX, float originY, float scale,
                SpriteEffects effects)
        {

            if (effects == SpriteEffects.None && rotation == 0f && originX == 0f
                    && originY == 0f && scale == 1f)
            {
                Draw(texture, px, py, srcX, srcY, srcWidth, srcHeight, c);
                return;
            }

            uint old = GetIntColor();

            SetColor(c);

            bool flipX = false;
            bool flipY = false;
            switch (effects._code)
            {
                case 1:
                    flipX = true;
                    break;
                case 2:
                    flipY = true;
                    break;
                default:
                    break;
            }
            Draw(texture, px, py, originX, originY, srcWidth, srcHeight, scale,
                    scale, rotation, srcX, srcY, srcWidth, srcHeight, flipX, flipY,
                    true);
            SetColor(old);
        }

        public void Draw(LTexture texture, float px, float py, RectBox src,
                LColor c, float rotation, Vector2f origin, float scale,
                SpriteEffects effects)
        {
            Draw(texture, px, py, src, c, rotation, origin.x, origin.y, scale,
                    effects);
        }

        public void Draw(LTexture texture, float px, float py, RectBox src,
                LColor c, float rotation, float ox, float oy, float scale,
                SpriteEffects effects)
        {
            Draw(texture, px, py, src, c, rotation, ox, oy, scale, scale, effects);
        }

        public void Draw(LTexture texture, float px, float py, RectBox src,
                LColor c, float rotation, float ox, float oy, float scaleX,
                float scaleY, SpriteEffects effects)
        {
            uint old = GetIntColor();

            SetColor(c);

            bool flipX = false;
            bool flipY = false;
            switch (effects._code)
            {
                case 1:
                    flipX = true;
                    break;
                case 2:
                    flipY = true;
                    break;
                default:
                    break;
            }
            if (src != null)
            {
                Draw(texture, px, py, ox, oy, src.width, src.height, scaleX,
                        scaleY, rotation, src.x, src.y, src.width, src.height,
                        flipX, flipY, true);
            }
            else
            {
                Draw(texture, px, py, ox, oy, texture.Width,
                        texture.Height, scaleX, scaleY, rotation, 0, 0,
                        texture.Width, texture.Height, flipX, flipY, true);
            }
            SetColor(old);
        }

        public void Draw(LTexture texture, Vector2f position, LColor c,
                float rotation, Vector2f origin, Vector2f scale,
                SpriteEffects effects)
        {
            uint old = GetIntColor();

            SetColor(c);

            bool flipX = false;
            bool flipY = false;
            switch (effects._code)
            {
                case 1:
                    flipX = true;
                    break;
                case 2:
                    flipY = true;
                    break;
                default:
                    break;
            }

            Draw(texture, position.x, position.y, origin.x, origin.y,
                    texture.Width, texture.Height, scale.x, scale.y,
                    rotation, 0, 0, texture.Width, texture.Height, flipX,
                    flipY, true);

            SetColor(old);
        }

        public void Draw(LTexture texture, Vector2f position, LColor c,
                float rotation, float originX, float originY, float scale,
                SpriteEffects effects)
        {
            uint old = GetIntColor();

            SetColor(c);

            bool flipX = false;
            bool flipY = false;
            switch (effects._code)
            {
                case 1:
                    flipX = true;
                    break;
                case 2:
                    flipY = true;
                    break;
                default:
                    break;
            }

            Draw(texture, position.x, position.y, originX, originY,
                    texture.Width, texture.Height, scale, scale,
                    rotation, 0, 0, texture.Width, texture.Height, flipX,
                    flipY, true);

            SetColor(old);
        }

        public void Draw(LTexture texture, float posX, float posY, float srcX,
                float srcY, float srcWidth, float srcHeight, LColor c,
                float rotation, float originX, float originY, float scaleX,
                float scaleY, SpriteEffects effects)
        {
            uint old = GetIntColor();

            SetColor(c);

            bool flipX = false;
            bool flipY = false;
            switch (effects._code)
            {
                case 1:
                    flipX = true;
                    break;
                case 2:
                    flipY = true;
                    break;
                default:
                    break;
            }
            Draw(texture, posX, posY, originX, originY, srcWidth, srcHeight,
                    scaleX, scaleY, rotation, srcX, srcY, srcWidth, srcHeight,
                    flipX, flipY, true);
            SetColor(old);
        }

        public void Draw(LTexture texture, Vector2f position, float srcX,
                float srcY, float srcWidth, float srcHeight, LColor c,
                float rotation, Vector2f origin, Vector2f scale,
                SpriteEffects effects)
        {
            uint old = GetIntColor();

            SetColor(c);

            bool flipX = false;
            bool flipY = false;
            switch (effects._code)
            {
                case 1:
                    flipX = true;
                    break;
                case 2:
                    flipY = true;
                    break;
                default:
                    break;
            }
            Draw(texture, position.x, position.y, origin.x, origin.y, srcWidth,
                    srcHeight, scale.x, scale.y, rotation, srcX, srcY, srcWidth,
                    srcHeight, flipX, flipY, true);
            SetColor(old);
        }

        public void Draw(LTexture texture, RectBox dst, RectBox src, LColor c,
                float rotation, Vector2f origin, SpriteEffects effects)
        {
            uint old = GetIntColor();

            SetColor(c);

            bool flipX = false;
            bool flipY = false;
            switch (effects._code)
            {
                case 1:
                    flipX = true;
                    break;
                case 2:
                    flipY = true;
                    break;
                default:
                    break;
            }
            if (src != null)
            {
                Draw(texture, dst.x, dst.y, origin.x, origin.y, dst.width,
                        dst.height, 1f, 1f, rotation, src.x, src.y, src.width,
                        src.height, flipX, flipY, true);
            }
            else
            {
                Draw(texture, dst.x, dst.y, origin.x, origin.y, dst.width,
                        dst.height, 1f, 1f, rotation, 0, 0, texture.Width,
                        texture.Height, flipX, flipY, true);
            }
            SetColor(old);
        }

        public void Draw(LTexture texture, float dstX, float dstY, float dstWidth,
                float dstHeight, float srcX, float srcY, float srcWidth,
                float srcHeight, LColor c, float rotation, float originX,
                float originY, SpriteEffects effects)
        {
            if (effects == SpriteEffects.None && rotation == 0 && originX == 0
                    && originY == 0)
            {
                Draw(texture, dstX, dstY, dstWidth, dstHeight, srcX, srcY,
                        srcWidth, srcHeight, c);
                return;
            }
            uint old = GetIntColor();

            SetColor(c);

            bool flipX = false;
            bool flipY = false;
            switch (effects._code)
            {
                case 1:
                    flipX = true;
                    break;
                case 2:
                    flipY = true;
                    break;
                default:
                    break;
            }
            Draw(texture, dstX, dstY, originX, originY, dstWidth, dstHeight, 1f,
                    1f, rotation, srcX, srcY, srcWidth, srcHeight, flipX, flipY,
                    true);
            SetColor(old);
        }

        public void Draw(LTexture texture, float x, float y, float originX,
                float originY, float width, float height, float scaleX,
                float scaleY, float rotation, float srcX, float srcY,
                float srcWidth, float srcHeight, bool flipX, bool flipY,
                bool off)
        {
            CheckTexture(texture);

            float worldOriginX = x + originX;
            float worldOriginY = y + originY;
            if (off)
            {
                worldOriginX = x;
                worldOriginY = y;
            }
            float fx = -originX;
            float fy = -originY;
            float fx2 = width - originX;
            float fy2 = height - originY;

            if (scaleX != 1 || scaleY != 1)
            {
                fx *= scaleX;
                fy *= scaleY;
                fx2 *= scaleX;
                fy2 *= scaleY;
            }

            float p1x = fx;
            float p1y = fy;
            float p2x = fx;
            float p2y = fy2;
            float p3x = fx2;
            float p3y = fy2;
            float p4x = fx2;
            float p4y = fy;

            float x1;
            float y1;
            float x2;
            float y2;
            float x3;
            float y3;
            float x4;
            float y4;

            if (rotation != 0)
            {
                float cos = MathUtils.CosDeg(rotation);
                float sin = MathUtils.SinDeg(rotation);

                x1 = cos * p1x - sin * p1y;
                y1 = sin * p1x + cos * p1y;

                x2 = cos * p2x - sin * p2y;
                y2 = sin * p2x + cos * p2y;

                x3 = cos * p3x - sin * p3y;
                y3 = sin * p3x + cos * p3y;

                x4 = x1 + (x3 - x2);
                y4 = y3 - (y2 - y1);
            }
            else
            {
                x1 = p1x;
                y1 = p1y;

                x2 = p2x;
                y2 = p2y;

                x3 = p3x;
                y3 = p3y;

                x4 = p4x;
                y4 = p4y;
            }

            x1 += worldOriginX;
            y1 += worldOriginY;
            x2 += worldOriginX;
            y2 += worldOriginY;
            x3 += worldOriginX;
            y3 += worldOriginY;
            x4 += worldOriginX;
            y4 += worldOriginY;

            float u = srcX * invTexWidth + texture.xOff;
            float v = srcY * invTexHeight + texture.yOff;
            float u2 = (srcX + srcWidth) * invTexWidth;
            float v2 = (srcY + srcHeight) * invTexHeight;

            if (flipX)
            {
                float tmp = u;
                u = u2;
                u2 = tmp;
            }

            if (flipY)
            {
                float tmp = v;
                v = v2;
                v2 = tmp;
            }

            vertices[idx].Position.X = x1;
            vertices[idx].Position.Y = y1;
            vertices[idx].Color = m_color;
            vertices[idx].TextureCoordinate.X = u;
            vertices[idx].TextureCoordinate.Y = v;

            vertices[idx + 1].Position.X = x2;
            vertices[idx + 1].Position.Y = y2;
            vertices[idx + 1].Color = m_color;
            vertices[idx + 1].TextureCoordinate.X = u;
            vertices[idx + 1].TextureCoordinate.Y = v2;

            vertices[idx + 2].Position.X = x3;
            vertices[idx + 2].Position.Y = y3;
            vertices[idx + 2].Color = m_color;
            vertices[idx + 2].TextureCoordinate.X = u2;
            vertices[idx + 2].TextureCoordinate.Y = v2;

            vertices[idx + 3].Position.X = x4;
            vertices[idx + 3].Position.Y = y4;
            vertices[idx + 3].Color = m_color;
            vertices[idx + 3].TextureCoordinate.X = u2;
            vertices[idx + 3].TextureCoordinate.Y = v;

            idx += 4;
        }

        public void Draw(LTexture texture, float x, float y, float width,
                float height, float rotation, LColor c)
        {
            uint old = GetIntColor();

            SetColor(c);

            Draw(texture, x, y, width, height, rotation);
            SetColor(old);
        }

        public void DrawFlipX(LTexture texture, float x, float y)
        {
            Draw(texture, x, y, texture.Width, texture.Height, 0, 0,
                    texture.Width, texture.Height, true, false);
        }

        public void DrawFlipY(LTexture texture, float x, float y)
        {
            Draw(texture, x, y, texture.Width, texture.Height, 0, 0,
                    texture.Width, texture.Height, false, true);
        }

        public void DrawFlipX(LTexture texture, float x, float y, float width,
                float height)
        {
            Draw(texture, x, y, width, height, 0, 0, texture.Width,
                    texture.Height, true, false);
        }

        public void DrawFlipY(LTexture texture, float x, float y, float width,
                float height)
        {
            Draw(texture, x, y, width, height, 0, 0, texture.Width,
                    texture.Height, false, true);
        }

        public void DrawFlipX(LTexture texture, float x, float y, float rotation)
        {
            Draw(texture, x, y, texture.Width / 2, texture.Height / 2,
                    texture.Width, texture.Height, 1f, 1f, rotation, 0,
                    0, texture.Width, texture.Height, true, false);
        }

        public void DrawFlipY(LTexture texture, float x, float y, float rotation)
        {
            Draw(texture, x, y, texture.Width / 2, texture.Height / 2,
                    texture.Width, texture.Height, 1f, 1f, rotation, 0,
                    0, texture.Width, texture.Height, false, true);
        }

        public void DrawFlipX(LTexture texture, float x, float y, float width,
                float height, float rotation)
        {
            Draw(texture, x, y, width / 2, height / 2, width, height, 1f, 1f,
                    rotation, 0, 0, texture.Width, texture.Height, true,
                    false);
        }

        public void DrawFlipY(LTexture texture, float x, float y, float width,
                float height, float rotation)
        {
            Draw(texture, x, y, width / 2, height / 2, width, height, 1f, 1f,
                    rotation, 0, 0, texture.Width, texture.Height, false,
                    true);
        }

        public void Draw(LTexture texture, RectBox dstBox, RectBox srcBox, LColor c)
        {
            uint old = GetIntColor();

            SetColor(c);

            Draw(texture, dstBox.x, dstBox.y, dstBox.width, dstBox.height,
                    srcBox.x, srcBox.y, srcBox.width, srcBox.height, false, false);
            SetColor(old);
        }

        public void Draw(LTexture texture, float x, float y, float width,
                float height, float srcX, float srcY, float srcWidth,
                float srcHeight)
        {
            Draw(texture, x, y, width, height, srcX, srcY, srcWidth, srcHeight,
                    false, false);
        }

        public void Draw(LTexture texture, float x, float y, float width,
                float height, float srcX, float srcY, float srcWidth,
                float srcHeight, LColor c)
        {
            uint old = GetIntColor();

            SetColor(c);

            Draw(texture, x, y, width, height, srcX, srcY, srcWidth, srcHeight,
                    false, false);
            SetColor(old);
        }

        public void DrawEmbedded(LTexture texture, float x, float y, float width,
                float height, float srcX, float srcY, float srcWidth,
                float srcHeight, LColor c)
        {
            Draw(texture, x, y, width - x, height - y, srcX, srcY, srcWidth - srcX,
                    srcHeight - srcY, c);
        }

        public void Draw(LTexture texture, float x, float y, float width,
                float height, float srcX, float srcY, float srcWidth,
                float srcHeight, bool flipX, bool flipY)
        {

            CheckTexture(texture);

            float u = srcX * invTexWidth + texture.xOff;
            float v = srcY * invTexHeight + texture.yOff;
            float u2 = (srcX + srcWidth) * invTexWidth;
            float v2 = (srcY + srcHeight) * invTexHeight;
            float fx2 = x + width;
            float fy2 = y + height;

            if (flipX)
            {
                float tmp = u;
                u = u2;
                u2 = tmp;
            }

            if (flipY)
            {
                float tmp = v;
                v = v2;
                v2 = tmp;
            }

            vertices[idx].Position.X = x;
            vertices[idx].Position.Y = y;
            vertices[idx].Color = m_color;
            vertices[idx].TextureCoordinate.X = u;
            vertices[idx].TextureCoordinate.Y = v;

            vertices[idx + 1].Position.X = x;
            vertices[idx + 1].Position.Y = fy2;
            vertices[idx + 1].Color = m_color;
            vertices[idx + 1].TextureCoordinate.X = u;
            vertices[idx + 1].TextureCoordinate.Y = v2;

            vertices[idx + 2].Position.X = fx2;
            vertices[idx + 2].Position.Y = fy2;
            vertices[idx + 2].Color = m_color;
            vertices[idx + 2].TextureCoordinate.X = u2;
            vertices[idx + 2].TextureCoordinate.Y = v2;

            vertices[idx + 3].Position.X = fx2;
            vertices[idx + 3].Position.Y = y;
            vertices[idx + 3].Color = m_color;
            vertices[idx + 3].TextureCoordinate.X = u2;
            vertices[idx + 3].TextureCoordinate.Y = v;

            idx += 4;
        }

        public void Draw(LTexture texture, Vector2f pos, RectBox srcBox, LColor c)
        {
            uint old = GetIntColor();

            SetColor(c);

            if (srcBox == null)
            {
                Draw(texture, pos.x, pos.y, 0, 0, texture.Width,
                        texture.Height);
            }
            else
            {
                Draw(texture, pos.x, pos.y, srcBox.x, srcBox.y, srcBox.width,
                        srcBox.height);
            }
            SetColor(old);
        }

        public void Draw(LTexture texture, float x, float y, float srcX,
                float srcY, float srcWidth, float srcHeight, LColor c)
        {
            uint old = GetIntColor();
            SetColor(c);
            Draw(texture, x, y, srcX, srcY, srcWidth, srcHeight);
            SetColor(old);
        }

        public void SetColor(LColor color)
        {
            m_color.SetColor(color);
        }

        public void SetColor(int r, int g, int b, int a)
        {
            m_color.SetColor(r, g, b, a);
        }

        public void SetColor(float r, float g, float b, float a)
        {
            m_color.SetColor(r, g, b, a);
        }

        public void SetColor(int r, int g, int b)
        {
            m_color.SetColor(r, g, b, alpha * 255);
        }

        public void SetColor(float r, float g, float b)
        {
            m_color.SetColor(r, g, b, alpha);
        }

        public void SetColor(uint v)
        {
            m_color.PackedValue = v;
        }

        public void SetAlpha(float alpha)
        {
            this.alpha = alpha;
            m_color.a = alpha;
        }

        public float GetAlpha()
        {
            return alpha;
        }

        public LColor GetColor()
        {
            return new LColor(m_color);
        }

        public uint GetIntColor()
        {
            return m_color.PackedValue;
        }

        private LColor m_color = LColor.white;

        public void Draw(LTexture texture, float x, float y, float srcX,
                float srcY, float srcWidth, float srcHeight)
        {


            CheckTexture(texture);

            float u = srcX * invTexWidth + texture.xOff;
            float v = srcY * invTexHeight + texture.yOff;
            float u2 = (srcX + srcWidth) * invTexWidth;
            float v2 = (srcY + srcHeight) * invTexHeight;
            float fx2 = x + srcWidth;
            float fy2 = y + srcHeight;

            vertices[idx].Position.X = x;
            vertices[idx].Position.Y = y;
            vertices[idx].Color = m_color;
            vertices[idx].TextureCoordinate.X = u;
            vertices[idx].TextureCoordinate.Y = v;

            vertices[idx + 1].Position.X = x;
            vertices[idx + 1].Position.Y = fy2;
            vertices[idx + 1].Color = m_color;
            vertices[idx + 1].TextureCoordinate.X = u;
            vertices[idx + 1].TextureCoordinate.Y = v2;

            vertices[idx + 2].Position.X = fx2;
            vertices[idx + 2].Position.Y = fy2;
            vertices[idx + 2].Color = m_color;
            vertices[idx + 2].TextureCoordinate.X = u2;
            vertices[idx + 2].TextureCoordinate.Y = v2;

            vertices[idx + 3].Position.X = fx2;
            vertices[idx + 3].Position.X = y;
            vertices[idx + 3].Color = m_color;
            vertices[idx + 3].TextureCoordinate.X = u2;
            vertices[idx + 3].TextureCoordinate.Y = v;

            idx += 4;

        }

        public void Draw(LTexture texture, float x, float y)
        {
            if (texture == null)
            {
                return;
            }
            Draw(texture, x, y, texture.Width, texture.Height);
        }

        public void Draw(LTexture texture, float x, float y, LColor c)
        {
            uint old = GetIntColor();
            SetColor(c);
            Draw(texture, x, y, texture.Width, texture.Height);
            SetColor(old);
        }

        public void Draw(LTexture texture, RectBox rect, LColor c)
        {
            uint old = GetIntColor();

            SetColor(c);

            Draw(texture, rect.x, rect.y, rect.width, rect.height);
            SetColor(old);
        }

        public void Draw(LTexture texture, Vector2f pos, LColor c)
        {
            uint old = GetIntColor();

            SetColor(c);

            Draw(texture, pos.x, pos.y, texture.Width, texture.Height);
            SetColor(old);
        }

        public void Draw(LTexture texture, float x, float y, float width,
                float height)
        {
            if (texture == null)
            {
                return;
            }

            CheckTexture(texture);

            float fx2 = x + width;
            float fy2 = y + height;
            float u = texture.xOff;
            float v = texture.yOff;
            float u2 = texture.widthRatio;
            float v2 = texture.heightRatio;

            vertices[idx].Position.X = x;
            vertices[idx].Position.Y = y;
            vertices[idx].Color = m_color;
            vertices[idx].TextureCoordinate.X = u;
            vertices[idx].TextureCoordinate.Y = v;

            vertices[idx + 1].Position.X = x;
            vertices[idx + 1].Position.Y = fy2;
            vertices[idx + 1].Color = m_color;
            vertices[idx + 1].TextureCoordinate.X = u;
            vertices[idx + 1].TextureCoordinate.Y = v2;

            vertices[idx + 2].Position.X = fx2;
            vertices[idx + 2].Position.Y = fy2;
            vertices[idx + 2].Color = m_color;
            vertices[idx + 2].TextureCoordinate.X = u2;
            vertices[idx + 2].TextureCoordinate.Y = v2;

            vertices[idx + 3].Position.X = fx2;
            vertices[idx + 3].Position.Y = y;
            vertices[idx + 3].Color = m_color;
            vertices[idx + 3].TextureCoordinate.X = u2;
            vertices[idx + 3].TextureCoordinate.Y = v;

            idx += 4;
        }


        public void Draw(LTexture texture, VertexPositionColorTexture[] spriteVertices, int offset,
            int length)
        {

            CheckTexture(texture);

            int remainingVertices = vertices.Length - idx;
            if (remainingVertices == 0)
            {
                Submit();
                remainingVertices = vertices.Length;
            }
            int vertexCount = MathUtils.Min(remainingVertices, length - offset);
            Array.Copy(spriteVertices, offset, vertices, idx, vertexCount);
            offset += vertexCount;
            idx += vertexCount;

            while (offset < length)
            {
                Submit();
                vertexCount = MathUtils.Min(vertices.Length, length - offset);
                Array.Copy(spriteVertices, offset, vertices, 0, vertexCount);
                offset += vertexCount;
                idx += vertexCount;
            }
        }

        public void Draw(LTextureRegion region, float x, float y, float rotation)
        {
            Draw(region, x, y, region.GetRegionWidth(), region.GetRegionHeight(),
                    rotation);
        }

        public void Draw(LTextureRegion region, float x, float y, float width,
                float height, float rotation)
        {
            Draw(region, x, y, region.GetRegionWidth() / 2,
                    region.GetRegionHeight() / 2, width, height, 1f, 1f, rotation);
        }

        public void Draw(LTextureRegion region, float x, float y)
        {
            Draw(region, x, y, region.GetRegionWidth(), region.GetRegionHeight());
        }

        public void Draw(LTextureRegion region, float x, float y, float width,
                float height)
        {
            CheckTexture(region.GetTexture());

            float fx2 = x + width;
            float fy2 = y + height;
            float u = region.xOff;
            float v = region.yOff;
            float u2 = region.widthRatio;
            float v2 = region.heightRatio;

            vertices[idx].Position.X = x;
            vertices[idx].Position.Y = y;
            vertices[idx].Color = m_color;
            vertices[idx].TextureCoordinate.X = u;
            vertices[idx].TextureCoordinate.Y = v;

            vertices[idx + 1].Position.X = x;
            vertices[idx + 1].Position.Y = fy2;
            vertices[idx + 1].Color = m_color;
            vertices[idx + 1].TextureCoordinate.X = u;
            vertices[idx + 1].TextureCoordinate.Y = v2;

            vertices[idx + 2].Position.X = fx2;
            vertices[idx + 2].Position.Y = fy2;
            vertices[idx + 2].Color = m_color;
            vertices[idx + 2].TextureCoordinate.X = u2;
            vertices[idx + 2].TextureCoordinate.Y = v2;

            vertices[idx + 3].Position.X = fx2;
            vertices[idx + 3].Position.Y = y;
            vertices[idx + 3].Color = m_color;
            vertices[idx + 3].TextureCoordinate.X = u2;
            vertices[idx + 3].TextureCoordinate.Y = v;

            idx += 4;
        }

        public void Draw(LTextureRegion region, float x, float y, float originX,
                float originY, float width, float height, float scaleX,
                float scaleY, float rotation)
        {

            CheckTexture(region.GetTexture());

            float worldOriginX = x + originX;
            float worldOriginY = y + originY;
            float fx = -originX;
            float fy = -originY;
            float fx2 = width - originX;
            float fy2 = height - originY;

            if (scaleX != 1 || scaleY != 1)
            {
                fx *= scaleX;
                fy *= scaleY;
                fx2 *= scaleX;
                fy2 *= scaleY;
            }

            float p1x = fx;
            float p1y = fy;
            float p2x = fx;
            float p2y = fy2;
            float p3x = fx2;
            float p3y = fy2;
            float p4x = fx2;
            float p4y = fy;

            float x1;
            float y1;
            float x2;
            float y2;
            float x3;
            float y3;
            float x4;
            float y4;

            if (rotation != 0)
            {
                float cos = MathUtils.CosDeg(rotation);
                float sin = MathUtils.SinDeg(rotation);

                x1 = cos * p1x - sin * p1y;
                y1 = sin * p1x + cos * p1y;

                x2 = cos * p2x - sin * p2y;
                y2 = sin * p2x + cos * p2y;

                x3 = cos * p3x - sin * p3y;
                y3 = sin * p3x + cos * p3y;

                x4 = x1 + (x3 - x2);
                y4 = y3 - (y2 - y1);
            }
            else
            {
                x1 = p1x;
                y1 = p1y;

                x2 = p2x;
                y2 = p2y;

                x3 = p3x;
                y3 = p3y;

                x4 = p4x;
                y4 = p4y;
            }

            x1 += worldOriginX;
            y1 += worldOriginY;
            x2 += worldOriginX;
            y2 += worldOriginY;
            x3 += worldOriginX;
            y3 += worldOriginY;
            x4 += worldOriginX;
            y4 += worldOriginY;

            float u = region.xOff;
            float v = region.yOff;
            float u2 = region.widthRatio;
            float v2 = region.heightRatio;

            vertices[idx++].Position.X = x1;
            vertices[idx++].Position.Y = y1;
            vertices[idx++].Color = m_color;
            vertices[idx++].TextureCoordinate.X = u;
            vertices[idx++].TextureCoordinate.Y = v;

            vertices[idx++].Position.X = x2;
            vertices[idx++].Position.Y = y2;
            vertices[idx++].Color = m_color;
            vertices[idx++].TextureCoordinate.X = u;
            vertices[idx++].TextureCoordinate.Y = v2;

            vertices[idx++].Position.X = x3;
            vertices[idx++].Position.Y = y3;
            vertices[idx++].Color = m_color;
            vertices[idx++].TextureCoordinate.X = u2;
            vertices[idx++].TextureCoordinate.Y = v2;

            vertices[idx++].Position.X = x4;
            vertices[idx++].Position.Y = y4;
            vertices[idx++].Color = m_color;
            vertices[idx++].TextureCoordinate.X = u2;
            vertices[idx++].TextureCoordinate.Y = v;

            idx += 4;
        }

        public void Draw(LTextureRegion region, float x, float y, float originX,
                float originY, float width, float height, float scaleX,
                float scaleY, float rotation, bool clockwise)
        {

            CheckTexture(region.GetTexture());

            float worldOriginX = x + originX;
            float worldOriginY = y + originY;
            float fx = -originX;
            float fy = -originY;
            float fx2 = width - originX;
            float fy2 = height - originY;

            if (scaleX != 1 || scaleY != 1)
            {
                fx *= scaleX;
                fy *= scaleY;
                fx2 *= scaleX;
                fy2 *= scaleY;
            }

            float p1x = fx;
            float p1y = fy;
            float p2x = fx;
            float p2y = fy2;
            float p3x = fx2;
            float p3y = fy2;
            float p4x = fx2;
            float p4y = fy;

            float x1;
            float y1;
            float x2;
            float y2;
            float x3;
            float y3;
            float x4;
            float y4;

            if (rotation != 0)
            {
                float cos = MathUtils.CosDeg(rotation);
                float sin = MathUtils.SinDeg(rotation);

                x1 = cos * p1x - sin * p1y;
                y1 = sin * p1x + cos * p1y;

                x2 = cos * p2x - sin * p2y;
                y2 = sin * p2x + cos * p2y;

                x3 = cos * p3x - sin * p3y;
                y3 = sin * p3x + cos * p3y;

                x4 = x1 + (x3 - x2);
                y4 = y3 - (y2 - y1);
            }
            else
            {
                x1 = p1x;
                y1 = p1y;

                x2 = p2x;
                y2 = p2y;

                x3 = p3x;
                y3 = p3y;

                x4 = p4x;
                y4 = p4y;
            }

            x1 += worldOriginX;
            y1 += worldOriginY;
            x2 += worldOriginX;
            y2 += worldOriginY;
            x3 += worldOriginX;
            y3 += worldOriginY;
            x4 += worldOriginX;
            y4 += worldOriginY;

            float u1, v1, u2, v2, u3, v3, u4, v4;
            if (clockwise)
            {
                u1 = region.widthRatio;
                v1 = region.heightRatio;
                u2 = region.xOff;
                v2 = region.heightRatio;
                u3 = region.xOff;
                v3 = region.yOff;
                u4 = region.widthRatio;
                v4 = region.yOff;
            }
            else
            {
                u1 = region.xOff;
                v1 = region.yOff;
                u2 = region.widthRatio;
                v2 = region.yOff;
                u3 = region.widthRatio;
                v3 = region.heightRatio;
                u4 = region.xOff;
                v4 = region.heightRatio;
            }

            vertices[idx].Position.X = x1;
            vertices[idx].Position.Y = y1;
            vertices[idx].Color = m_color;
            vertices[idx].TextureCoordinate.X = u1;
            vertices[idx].TextureCoordinate.Y = v1;

            vertices[idx + 1].Position.X = x2;
            vertices[idx + 1].Position.Y = y2;
            vertices[idx + 1].Color = m_color;
            vertices[idx + 1].TextureCoordinate.X = u2;
            vertices[idx + 1].TextureCoordinate.Y = v2;

            vertices[idx + 2].Position.X = x3;
            vertices[idx + 2].Position.Y = y3;
            vertices[idx + 2].Color = m_color;
            vertices[idx + 2].TextureCoordinate.X = u3;
            vertices[idx + 2].TextureCoordinate.Y = v3;

            vertices[idx + 3].Position.X = x4;
            vertices[idx + 3].Position.Y = y4;
            vertices[idx + 3].Color = m_color;
            vertices[idx + 3].TextureCoordinate.X = u4;
            vertices[idx + 3].TextureCoordinate.Y = v4;

            idx += 4;
        }


        private void CheckDrawing()
        {
            if (!drawing)
            {
                throw new Exception("Not implemented begin !");
            }
        }

        public void Flush()
        {
            Submit();
        }

        public BlendState GetBlendState()
        {
            return lastBlendState;
        }

        public void SetBlendState(BlendState state)
        {
            if (state != lastBlendState)
            {
                this.lastBlendState = state;
                switch (lastBlendState._code)
                {
                    case 0:
                        GL.XNA_BlendState = Microsoft.Xna.Framework.Graphics.BlendState.NonPremultiplied;
                        break;
                    case 1:
                        GL.XNA_BlendState = Microsoft.Xna.Framework.Graphics.BlendState.Additive;
                        break;
                    case 2:
                        GL.XNA_BlendState = Microsoft.Xna.Framework.Graphics.BlendState.AlphaBlend;
                        break;
                    case 3:
                        GL.XNA_BlendState = Microsoft.Xna.Framework.Graphics.BlendState.Opaque;
                        break;

                }
            }
        }

        public void Flush(BlendState state)
        {
            Submit(state,true);
        }

        private void Submit()
        {
            Submit(lastBlendState,false);
        }

        private void Submit(BlendState state,bool only)
        {
            if (idx == 0)
            {
                return;
            }
            lock (typeof(SpriteBatch))
            {
                renderCalls++;
                totalRenderCalls++;
                int spritesInBatch = idx / 20;
                if (spritesInBatch > maxSpritesInBatch)
                {
                    maxSpritesInBatch = spritesInBatch;
                }
                SetBlendState(state);
                if (only)
                {
                    GLEx.GL.GLOnlyBind(lastTexture);
                }
                else
                {
                    GLEx.GL.GLBind(lastTexture);
                }
                GLEx.GL.Submit(GL10.GL_TRIANGLE_FAN, idx, vertices, indices, null);
                System.Array.Clear(vertices, 0, idx);
                idx = 0;
            }
        }

        public bool IsLockSubmit()
        {
            return lockSubmit;
        }

        public void SetLockSubmit(bool lockSubmit)
        {
            this.lockSubmit = lockSubmit;
        }

        private LFont font = LFont.GetDefaultFont();

        public LFont GetFont()
        {
            return font;
        }

        public void SetFont(LFont font)
        {
            this.font = font;
        }

        public void DrawString(LFont spriteFont, string text, float px, float py,
                LColor color, float rotation, float originx, float originy,
                float scale)
        {
            LFont old = font;
            if (spriteFont != null)
            {
                SetFont(spriteFont);
            }
            int height = (int)((spriteFont.GetHeight() - 2));
            if (rotation == 0f)
            {
                DrawString(text, px - (originx * scale), (py + height)
                        - (originy * scale), scale, scale, originx, originy,
                        rotation, color);
            }
            else
            {
                DrawString(text, px, (py + height), scale, scale, originx, originy,
                        rotation, color);
            }
            SetFont(old);
        }

        public void DrawString(LFont spriteFont, string text, Vector2f position,
                LColor color, float rotation, Vector2f origin, float scale)
        {
            LFont old = font;
            if (spriteFont != null)
            {
                SetFont(spriteFont);
            }
            int heigh = (int)((spriteFont.GetHeight() - 2));
            if (rotation == 0f)
            {
                DrawString(text, position.x - (origin.x * scale),
                        (position.y + heigh) - (origin.y * scale), scale, scale,
                        origin.x, origin.y, rotation, color);
            }
            else
            {
                DrawString(text, position.x, (position.y + heigh), scale, scale,
                        origin.x, origin.y, rotation, color);
            }
            SetFont(old);
        }

        public void DrawString(LFont spriteFont, string text, Vector2f position,
                LColor color)
        {
            LFont old = font;
            if (spriteFont != null)
            {
                SetFont(spriteFont);
            }
            int heigh = (int)(spriteFont.GetHeight() - 2);
            DrawString(text, position.x, (position.y + heigh), 1f, 1f, 0f, 0f, 0f,
                    color);
            SetFont(old);
        }

        public void DrawString(LFont spriteFont, string text, float x, float y,
                LColor color)
        {
            LFont old = font;
            if (spriteFont != null)
            {
                SetFont(spriteFont);
            }
            int heigh = (int)(spriteFont.GetHeight() - 2);
            DrawString(text, x, (y + heigh), 1f, 1f, 0f, 0f, 0f, color);
            SetFont(old);
        }

        public void DrawString(LFont spriteFont, string text, Vector2f position,
                LColor color, float rotation, Vector2f origin, Vector2f scale)
        {
            LFont old = font;
            if (spriteFont != null)
            {
                SetFont(spriteFont);
            }
            int heigh = (int)((spriteFont.GetHeight() - 2));
            if (rotation == 0f)
            {
                DrawString(text, position.x - (origin.x * scale.x),
                        (position.y + heigh) - (origin.y * scale.y), scale.x,
                        scale.y, origin.x, origin.y, rotation, color);
            }
            else
            {
                DrawString(text, position.x, (position.y + heigh), scale.x,
                        scale.y, origin.x, origin.y, rotation, color);
            }
            SetFont(old);
        }


        public void DrawString(string mes, float x, float y, float scaleX,
                float scaleY, float ax, float ay, float rotation, LColor c)
        {

            if (!drawing)
            {
                throw new Exception("Not implemented begin !");
            }
            if (c == null)
            {
                return;
            }
            if (mes == null || mes.Length == 0)
            {
                return;
            }
            if (!lockSubmit)
            {
                Submit();
            }
            LSTRDictionary.DrawString(font, mes, x, y - font.GetAscent(), scaleX, scaleX, ax,
                   ay, rotation, c);
        }

        public void DrawString(string mes, Vector2f position)
        {
            DrawString(mes, position.x, position.y, m_color);
        }

        public void DrawString(string mes, Vector2f position, LColor color)
        {
            DrawString(mes, position.x, position.y, color);
        }

        public void DrawString(string mes, float x, float y)
        {
            DrawString(mes, x, y, m_color);
        }

        public void DrawString(string mes, float x, float y, LColor color)
        {

            DrawString(mes, x, y, 0, color);
        }

        public void DrawString(string mes, float x, float y, float rotation)
        {
            DrawString(mes, x, y, rotation, m_color);
        }

        public void DrawString(string mes, float x, float y, float rotation,
                LColor c)
        {
            DrawString(mes, x, y, 1f, 1f, 0, 0, rotation, c);
        }

        public void DrawString(string mes, float x, float y, float sx, float sy,
                Vector2f origin, float rotation, LColor c)
        {
            DrawString(mes, x, y, sx, sy, origin.x, origin.y, rotation, c);
        }

        public void DrawString(string mes, float x, float y, Vector2f origin,
                float rotation, LColor c)
        {
            DrawString(mes, x, y, 1f, 1f, origin.x, origin.y, rotation, c);
        }

        public void DrawString(string mes, float x, float y, Vector2f origin,
                LColor c)
        {
            DrawString(mes, x, y, 1f, 1f, origin.x, origin.y, 0, c);
        }


        public void Dispose()
        {
            this.vertices = null;
        }
    }
}
