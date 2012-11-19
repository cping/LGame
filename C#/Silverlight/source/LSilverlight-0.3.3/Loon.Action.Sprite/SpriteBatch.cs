namespace Loon.Action.Sprite
{
    using Loon.Core.Graphics.OpenGL;
    using Microsoft.Xna.Framework.Graphics;
    using Microsoft.Xna.Framework;
    using Loon.Core;
    using Loon.Utils;
    using Loon.Utils.Debug;
    using System;
    using Loon.Core.Graphics;
    using Loon.Java;
    using Loon.Core.Geom;
    using System.Collections.Generic;

    public class SpriteBatch : LRelease
    {
        private Microsoft.Xna.Framework.Graphics.SpriteBatch batch;

        private Vector2 origin;

        private Color color = new Color(1f, 1f, 1f, 1f);

        private float alpha = 1f;

        private LTexture lastTexture = null;

        private int idx = 0;

        private bool isClose;

        private Rectangle texDst, texSrc;

        private bool isInit, drawing;

        private int mode = -1;
        
	    public static int VERTEX_SIZE = 2 + 1 + 2;

	    public static int SPRITE_SIZE = 4 * VERTEX_SIZE;

        public SpriteBatch(int size)
            : this()
        {

        }

        public SpriteBatch()
        {
            texDst = new Rectangle();
            texSrc = new Rectangle();
            origin = new Vector2();
        }

        private void Load(LTexture texture)
        {
            lastTexture = texture;
            int type = -1;
            if (texture.isExt)
            {
                type = 0;
            }
            else if (color.A != 255)
            {
                type = 1;
            }
            else
            {
                type = 2;

            }
            if (mode != type)
            {
                if (idx > 0)
                {
                    batch.End();
                    idx = 0;
                }
                switch (type)
                {
                    case 0:
                        batch.Begin(SpriteSortMode.Deferred, BlendState.NonPremultiplied, null, null, GLEx.device.RasterizerState, null, GLEx.cemera.viewMatrix);
                        break;
                    case 1:
                        batch.Begin(SpriteSortMode.Deferred, BlendState.Additive, null, null, GLEx.device.RasterizerState, null, GLEx.cemera.viewMatrix);
                        break;
                    case 2:
                        batch.Begin(SpriteSortMode.Deferred, BlendState.AlphaBlend, null, null, GLEx.device.RasterizerState, null, GLEx.cemera.viewMatrix);
                        break;
                }
                mode = type;
            }
        }

        public void HalfAlpha()
        {
            color.R = 255;
            color.G = 255;
            color.B = 255;
            color.A = 127;
            alpha = 0.5f;
        }

        public void ResetColor()
        {
            color.R = 255;
            color.G = 255;
            color.B = 255;
            color.A = 255;
            alpha = 1f;
        }

        private Dictionary<Int32, XNARectangle> rectLazy = new Dictionary<Int32, XNARectangle>(
                100);

        private Dictionary<Int32, XNARectangle> fillRectLazy = new Dictionary<Int32, XNARectangle>(
                100);

        private Dictionary<Int32, XNALine> lineLazy = new Dictionary<Int32, XNALine>(
                1000);

        private Dictionary<Int32, XNAEllipse> circleLazy = new Dictionary<Int32, XNAEllipse>(
                1000);

        private Dictionary<Int32, XNAPolyline> polyLazy = new Dictionary<Int32, XNAPolyline>(
                1000);

        private int lineWidth = 1;

        private void SubmitDraw()
        {
            if (idx == 0)
            {
                if (alpha == 1)
                {
                    batch.Begin(SpriteSortMode.Deferred, BlendState.AlphaBlend, null, null, GLEx.device.RasterizerState, null, GLEx.cemera.viewMatrix);
                    mode = 2;
                }
                else
                {
                    batch.Begin(SpriteSortMode.Deferred, BlendState.Additive, null, null, GLEx.device.RasterizerState, null, GLEx.cemera.viewMatrix);
                    mode = 1;
                }
            }
        }

        public void DrawRoundRect(float x, float y, float width,
                float height, int radius)
        {
            DrawRoundRect(x, y, width, height, radius, 40);
        }

        public void DrawRoundRect(float x, float y, float width,
                float height, int radius, int segs)
        {
            if (radius < 0)
            {
                throw new InvalidOperationException("radius > 0");
            }
            if (radius == 0)
            {
                DrawRect(x, y, width, height);
                return;
            }
            int mr = (int)MathUtils.Min(width, height) / 2;
            if (radius > mr)
            {
                radius = mr;
            }
            DrawLine(x + radius, y, x + width - radius, y);
            DrawLine(x, y + radius, x, y + height - radius);
            DrawLine(x + width, y + radius, x + width, y + height - radius);
            DrawLine(x + radius, y + height, x + width - radius, y + height);
            float d = radius * 2;
            DrawArc(x + width - d, y + height - d, d, d, segs, 0, 90);
            DrawArc(x, y + height - d, d, d, segs, 90, 180);
            DrawArc(x + width - d, y, d, d, segs, 270, 360);
            DrawArc(x, y, d, d, segs, 180, 270);
        }

        public void FillRoundRect(float x, float y, float width,
                float height, int cornerRadius)
        {
            FillRoundRect(x, y, width, height, cornerRadius, 40);
        }

        public void FillRoundRect(float x, float y, float width,
                float height, int radius, int segs)
        {
            if (radius < 0)
            {
                throw new InvalidOperationException("radius > 0");
            }
            if (radius == 0)
            {
                FillRect(x, y, width, height);
                return;
            }
            int mr = (int)MathUtils.Min(width, height) / 2;
            if (radius > mr)
            {
                radius = mr;
            }
            float d = radius * 2;
            FillRect(x + radius, y, width - d, radius);
            FillRect(x, y + radius, radius, height - d);
            FillRect(x + width - radius, y + radius, radius, height - d);
            FillRect(x + radius, y + height - radius, width - d, radius);
            FillRect(x + radius, y + radius, width - d, height - d);
            FillArc(x + width - d, y + height - d, d, d, segs, 0, 90);
            FillArc(x, y + height - d, d, d, segs, 90, 180);
            FillArc(x + width - d, y, d, d, segs, 270, 360);
            FillArc(x, y, d, d, segs, 180, 270);
        }

        public void Draw(Shape shape)
        {
            if (shape == null)
            {
                return;
            }
            Submit();
            GLEx.Self.Draw(shape);
        }

        public void Fill(Shape shape)
        {
            if (shape == null)
            {
                return;
            }
            Submit();
            GLEx.Self.Fill(shape);
        }

        public void FillPolygon(float[] xPoints, float[] yPoints, int nPoints)
        {
            Submit();
            GLEx.Self.FillPolygon(xPoints, yPoints, nPoints);
        }

        public void DrawPolygon(float[] xPoints, float[] yPoints, int nPoints)
        {
            Submit();
            GLEx.Self.DrawPolygon(xPoints, yPoints, nPoints);
        }

        public void FillArc(float x1, float y1, float width, float height,
             int segments, float start, float end)
        {
            Submit();
            GLEx.Self.FillArc(x1, y1, width, height, start, end);
        }

        public void FillArc(float x1, float y1, float width, float height,
             float start, float end)
        {
            FillArc(x1, y1, width, height, 32, start, end);
        }

        public void FillArc(float x1, float y1, float width, float height)
        {
            FillArc(x1, y1, width, height, 0, 360);
        }

        public void FillOval(float x1, float y1, float width, float height)
        {
            FillArc(x1, y1, width, height);
        }

        public void DrawArc(float x1, float y1, float width, float height,
             int segments, float start, float end)
        {
            while (end < start)
            {
                end += 360;
            }
            float cx = x1 + (width / 2.0f);
            float cy = y1 + (height / 2.0f);
            int step = 360 / segments;

            int hashCode = 1;

            hashCode = LSystem.Unite(hashCode, x1);
            hashCode = LSystem.Unite(hashCode, y1);
            hashCode = LSystem.Unite(hashCode, width);
            hashCode = LSystem.Unite(hashCode, height);
            hashCode = LSystem.Unite(hashCode, segments);
            hashCode = LSystem.Unite(hashCode, start);
            hashCode = LSystem.Unite(hashCode, end);
            hashCode = LSystem.Unite(hashCode, color.PackedValue);
            SubmitDraw();
            XNAPolygon poly = (XNAPolygon)CollectionUtils.Get(polyLazy, hashCode);
            if (poly == null)
            {
                poly = new XNAPolygon(GLEx.device);
                poly.Stroke = color;
                poly.StrokeWidth = 1;
                for (float a = start; a < (end + step); a += step)
                {
                    float ang = a;
                    if (ang > end)
                    {
                        ang = end;
                    }
                    float x = (cx + (MathUtils.Cos(MathUtils.ToRadians(ang)) * width / 2.0f));
                    float y = (cy + (MathUtils.Sin(MathUtils.ToRadians(ang)) * height / 2.0f));
                    poly.AddPoint(new Vector2(x, y));
                }
                CollectionUtils.Put(polyLazy, hashCode, poly);
            }
            poly.Draw(batch);
            idx++;
        }

        public void DrawArc(float x1, float y1, float width, float height,
             float start, float end)
        {
            DrawArc(x1, y1, width, height, 32, start, end);
        }

        public void DrawArc(float x1, float y1, float width, float height)
        {
            DrawArc(x1, y1, width, height, 0, 360);
        }

        public void DrawOval(float x1, float y1, float width, float height)
        {
            DrawArc(x1, y1, width, height);
        }

        public void FillRect(float x, float y, float width, float height)
        {
            int hashCode = 1;
            hashCode = LSystem.Unite(hashCode, x);
            hashCode = LSystem.Unite(hashCode, y);
            hashCode = LSystem.Unite(hashCode, width);
            hashCode = LSystem.Unite(hashCode, height);
            hashCode = LSystem.Unite(hashCode, color.PackedValue);
            XNARectangle rect = (XNARectangle)CollectionUtils.Get(fillRectLazy, hashCode);
            SubmitDraw();
            if (rect == null)
            {
                rect = new XNARectangle(GLEx.device);
                rect.Position = new Vector2(x, y);
                rect.StrokeWidth = lineWidth;
                rect.Width = width;
                rect.Height = height;
                rect.Stroke = color;
                XNALinearGradient gradient = new XNALinearGradient(GLEx.device);
                gradient.AddStop(color, 0);
                rect.Fill = gradient;
                CollectionUtils.Put(fillRectLazy, hashCode, rect);
            }
            rect.Draw(batch);
            idx++;
        }

        public void DrawRect(float x, float y, float width, float height)
        {
            int hashCode = 1;
            hashCode = LSystem.Unite(hashCode, x);
            hashCode = LSystem.Unite(hashCode, y);
            hashCode = LSystem.Unite(hashCode, width);
            hashCode = LSystem.Unite(hashCode, height);
            hashCode = LSystem.Unite(hashCode, color.PackedValue);
            XNARectangle rect = (XNARectangle)CollectionUtils.Get(rectLazy, hashCode);
            SubmitDraw();
            if (rect == null)
            {
                rect = new XNARectangle(GLEx.device);
                rect.Position = new Vector2(x, y);
                rect.StrokeWidth = lineWidth;
                rect.Width = width;
                rect.Height = height;
                rect.Stroke = color;
                CollectionUtils.Put(rectLazy, hashCode, rect);
            }
            rect.Draw(batch);
            idx++;
        }

        public void DrawPoint(int x, int y, LColor c)
        {
            Color old = color;
            SetColor(c);
            DrawLine(x, y, x + 1, y + 1);
            SetColor(old);
        }

        public void DrawPoints(int[] x, int[] y, LColor c)
        {
            int size = y.Length;
            for (int i = 0; i < size; i++)
            {
                DrawPoint(x[i], y[i], c);
            }
        }

        public void DrawPoints(int[] x, int[] y)
        {
            int size = y.Length;
            for (int i = 0; i < size; i++)
            {
                DrawPoint(x[i], y[i]);
            }
        }

        public void DrawPoint(int x, int y)
        {
            DrawLine(x, y, x + 1, y + 1);
        }

        public void DrawLine(float x1, float y1, float x2, float y2)
        {
            int hashCode = 1;
            hashCode = LSystem.Unite(hashCode, x1);
            hashCode = LSystem.Unite(hashCode, y1);
            hashCode = LSystem.Unite(hashCode, x2);
            hashCode = LSystem.Unite(hashCode, y2);
            hashCode = LSystem.Unite(hashCode, color.PackedValue);
            XNALine line = (XNALine)CollectionUtils.Get(lineLazy, hashCode);
            SubmitDraw();
            if (line == null)
            {
                line = new XNALine();
                line.StrokeWidth = lineWidth;
                line.Stroke = color;
                line.Start = new Vector2(x1, y1);
                line.End = new Vector2(x2, y2);
                CollectionUtils.Put(lineLazy, hashCode, line);
            }
            line.Draw(batch);
            idx++;
        }

        private void CheckDrawing()
        {
            if (!drawing)
            {
                throw new InvalidOperationException("Not implemented begin !");
            }
        }

        public void Begin()
        {
            if (drawing)
            {
                throw new InvalidOperationException("Not implemented end !");
            }
            if (!isInit)
            {
                batch = new Microsoft.Xna.Framework.Graphics.SpriteBatch(GLEx.device);
                isInit = true;
            }
            lastTexture = null;
            mode = -1;
            idx = 0;
            drawing = true;
        }

        public void End()
        {
            CheckDrawing();
            if (idx > 0)
            {
                batch.End();
            }
            idx = 0;
            lastTexture = null;
            mode = -1;
            drawing = false;
        }

        private void Submit()
        {
            if (idx > 0)
            {
                batch.End();
            }
            idx = 0;
        }

        public void Flush()
        {
            Submit();
        }

        public void SetColor(Color c)
        {
            color = c;
        }

        public void SetColor(LColor c)
        {
            color.R = c.R;
            color.G = c.G;
            color.B = c.B;
            color.A = (byte)(alpha != 1f ? alpha * 255 : c.A);
        }

        public void SetColor(float r, float g, float b, float a)
        {
            color = new Color(r, g, b, alpha != 1f ? alpha : a);
        }

        public void SetColor(float r, float g, float b)
        {
            color = new Color(r, g, b, alpha != 1f ? alpha : 1f);
        }

        public void SetColor(uint v)
        {
            color.PackedValue = v;
        }

        public void SetAlpha(float alpha)
        {
            this.alpha = alpha;
            color.A = (byte)(255 * alpha);
        }

        public float GetAlpha()
        {
            return alpha;
        }

        public Color GetColor()
        {
            return color;
        }

        public LColor GetLColor()
        {
            return new LColor(color);
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

        public void DrawString(string s, Vector2f position)
        {
            DrawString(s, position.x, position.y, color);
        }

        public void DrawString(string s, Vector2f position, Color color)
        {
            DrawString(s, position.x, position.y, color);
        }

        public void DrawString(string s, float x, float y)
        {
            DrawString(s, x, y, color);
        }

        public void DrawString(string s, float x, float y, Color color)
        {
            DrawString(s, x, y, 0, color);
        }

        public void DrawString(string s, float x, float y, float rotation)
        {
            DrawString(s, x, y, rotation, color);
        }

        public void DrawString(string s, float x, float y, float rotation,
                LColor c)
        {
            DrawString(s, x, y, rotation, c.Color);
        }

        public void DrawString(string text, float x, float y, float rotation, Color c)
        {
            if (font == null)
            {
                throw new RuntimeException("Did not set any Font !");
            }
            else
            {
                Vector2 pos = new Vector2(x, y);
                if (rotation != 0)
                {
                    float centerX = font.StringWidth(text) / 2;
                    float centerY = font.StringHeight(text) / 2;
                    Vector2 origin = new Vector2(centerX, centerY);
                    batch.DrawString(
                    font.Font,
                    text,
                    pos,
                    c,
                    rotation,
                    origin,
                    1f,
                    SpriteEffects.None,
                    0);
                }
                else
                {
                    batch.DrawString(font.Font, text, pos, c);
                }
            }
        }

        public void Draw(LTexture texture, float x, float y)
        {
            Draw(texture, x, y, 0);
        }

        public void Draw(LTexture texture, float x, float y, float width, float height)
        {
            if (isClose)
            {
                return;
            }
            if (texture == null)
            {
                return;
            }

            if (!texture.isLoaded)
            {
                texture.LoadTexture();
            }
            if (texture.IsChild)
            {
                float xOff = texture.xOff;
                float yOff = texture.yOff;
                float widthRatio = (1f * texture.widthRatio);
                float heightRatio = (1f * texture.heightRatio);
                int newX = (int)(xOff * texture.texWidth);
                int newY = (int)(yOff * texture.texHeight);
                int newWidth = (int)((texture.texWidth * widthRatio)) - newX;
                int newHeight = (int)((texture.texHeight * heightRatio)) - newY;

                texDst.X = (int)x;
                texDst.Y = (int)y;
                texDst.Width = (int)width;
                texDst.Height = (int)height;
                texSrc.X = newX;
                texSrc.Y = newY;
                texSrc.Width = newWidth;
                texSrc.Height = newHeight;
            }
            else
            {
                texDst.X = (int)x;
                texDst.Y = (int)y;
                texDst.Width = (int)width;
                texDst.Height = (int)height;
                texSrc.X = 0;
                texSrc.Y = 0;
                texSrc.Width = texture.Width;
                texSrc.Height = texture.Height;
            }

            Load(texture);

            batch.Draw(texture, texDst, texSrc, color);

            idx++;
        }

        public void Draw(LTexture texture, float x, float y, float rotation)
        {
            if (isClose)
            {
                return;
            }
            if (texture == null)
            {
                return;
            }

            if (!texture.isLoaded)
            {
                texture.LoadTexture();
            }
            if (texture.IsChild)
            {
                float xOff = texture.xOff;
                float yOff = texture.yOff;
                float widthRatio = (1f * texture.widthRatio);
                float heightRatio = (1f * texture.heightRatio);
                int newX = (int)(xOff * texture.texWidth);
                int newY = (int)(yOff * texture.texHeight);
                int newWidth = (int)((texture.texWidth * widthRatio)) - newX;
                int newHeight = (int)((texture.texHeight * heightRatio)) - newY;

                texDst.X = (int)x;
                texDst.Y = (int)y;
                texDst.Width = texture.width;
                texDst.Height = texture.height;
                texSrc.X = newX;
                texSrc.Y = newY;
                texSrc.Width = newWidth;
                texSrc.Height = newHeight;
            }
            else
            {
                texDst.X = (int)x;
                texDst.Y = (int)y;
                texDst.Width = texture.Width;
                texDst.Height = texture.Height;
                texSrc.X = 0;
                texSrc.Y = 0;
                texSrc.Width = texture.Width;
                texSrc.Height = texture.Height;
            }

            Load(texture);

            if (rotation != 0)
            {
                float centerX = texSrc.Width / 2;
                float centerY = texSrc.Height / 2;
                texDst.X += (int)texture.Width / 2;
                texDst.Y += (int)texture.Height / 2;
                origin.X = centerX;
                origin.Y = centerY;
                batch.Draw(texture, texDst, texSrc, color, MathUtils.ToRadians(rotation), origin, SpriteEffects.None, 0);
            }
            else
            {
                batch.Draw(texture, texDst, texSrc, color);
            }
            idx++;
        }

        public void DrawFlipX(LTexture texture, float x, float y)
        {
            Draw(texture, x, y, texture.width, texture.height, 0, 0,
                    texture.width, texture.height, true, false);
        }

        public void DrawFlipY(LTexture texture, float x, float y)
        {
            Draw(texture, x, y, texture.width, texture.height, 0, 0,
                    texture.width, texture.height, false, true);
        }

        public void DrawFlipX(LTexture texture, float x, float y, float width,
                float height)
        {
            Draw(texture, x, y, width, height, 0, 0, texture.width,
                    texture.height, true, false);
        }

        public void DrawFlipY(LTexture texture, float x, float y, float width,
                float height)
        {
            Draw(texture, x, y, width, height, 0, 0, texture.width,
                    texture.height, false, true);
        }

        public void DrawFlipX(LTexture texture, float x, float y, float rotation)
        {
            Draw(texture, x, y, texture.width / 2, texture.height / 2,
                    texture.width, texture.height, 1f, 1f, rotation, 0,
                    0, texture.width, texture.height, true, false);
        }

        public void DrawFlipY(LTexture texture, float x, float y, float rotation)
        {
            Draw(texture, x, y, texture.width / 2, texture.height / 2,
                    texture.width, texture.hashCode, 1f, 1f, rotation, 0,
                    0, texture.width, texture.height, false, true);
        }

        public void DrawFlipX(LTexture texture, float x, float y, float width,
                float height, float rotation)
        {
            Draw(texture, x, y, width / 2, height / 2, width, height, 1f, 1f,
                    rotation, 0, 0, texture.width, texture.height, true,
                    false);
        }

        public void DrawFlipY(LTexture texture, float x, float y, float width,
                float height, float rotation)
        {
            Draw(texture, x, y, width / 2, height / 2, width, height, 1f, 1f,
                    rotation, 0, 0, texture.width, texture.height, false,
                    true);
        }

        public void Draw(LTexture texture, float x, float y, float srcX,
                float srcY, float srcWidth, float srcHeight)
        {
            Draw(texture, x, y, texture.width, texture.height, srcX, srcY, srcWidth, srcHeight, false, false);
        }

        public void Draw(LTexture texture, float x, float y, float width,
            float height, float srcX, float srcY, float srcWidth,
            float srcHeight, bool flipX, bool flipY)
        {
            if (isClose)
            {
                return;
            }
            if (texture == null)
            {
                return;
            }
            if (!texture.isLoaded)
            {
                texture.LoadTexture();
            }
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

                texDst.X = (int)x;
                texDst.Y = (int)y;
                texDst.Width = (int)(width);
                texDst.Height = (int)(height);
                texSrc.X = newX;
                texSrc.Y = newY;
                texSrc.Width = newWidth;
                texSrc.Height = newHeight;
            }
            else
            {
                texDst.X = (int)x;
                texDst.Y = (int)y;
                texDst.Width = (int)(width);
                texDst.Height = (int)(height);
                texSrc.X = (int)srcX;
                texSrc.Y = (int)srcY;
                texSrc.Width = (int)(srcWidth - srcX);
                texSrc.Height = (int)(srcHeight - srcY);
            }

            Load(texture);

            SpriteEffects e = SpriteEffects.None;

            if (flipX && !flipY)
            {
                e = SpriteEffects.FlipHorizontally;
            }
            else
                if (flipY && !flipX)
                {
                    e = SpriteEffects.FlipVertically;
                }
                else
                    if (flipX && flipY)
                    {
                        e = SpriteEffects.FlipHorizontally | SpriteEffects.FlipVertically;
                    }


            batch.Draw(texture.Texture, texDst, texSrc, color, 0, Vector2.Zero, e, 0);

            idx++;
        }


        public void Draw(LTexture texture, float x, float y, float width,
                float height, float rotation)
        {
            Draw(texture, x, y, width / 2, height / 2, width, height, 1f, 1f,
                    rotation, 0, 0, texture.width, texture.height, false,
                    false);
        }

        public void Draw(LTexture texture, float x, float y, float rotation,
                float srcX, float srcY, float srcWidth, float srcHeight)
        {
            Draw(texture, x, y, srcWidth / 2, srcHeight / 2, texture.width,
                    texture.height, 1f, 1f, rotation, srcX, srcY, srcWidth,
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
                float originY, float scaleX,
                float scaleY, float rotation, float srcX, float srcY,
                float srcWidth, float srcHeight, bool flipX, bool flipY)
        {
            Draw(texture, x, y, originX, originY, srcWidth, srcHeight, scaleX, scaleY,
                    rotation, srcX, srcY, srcWidth, srcHeight, flipX, flipY, false);
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
            float originY, float width, float height, float scaleX,
            float scaleY, float rotation, float srcX, float srcY,
            float srcWidth, float srcHeight, bool flipX, bool flipY,
            bool off)
        {
            if (isClose)
            {
                return;
            }
            if (texture == null)
            {
                return;
            }
            if (!texture.isLoaded)
            {
                texture.LoadTexture();
            }
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

                texDst.X = (int)x;
                texDst.Y = (int)y;
                texDst.Width = (int)(width * scaleX);
                texDst.Height = (int)(height * scaleY);
                texSrc.X = newX;
                texSrc.Y = newY;
                texSrc.Width = newWidth;
                texSrc.Height = newHeight;
            }
            else
            {
                texDst.X = (int)x;
                texDst.Y = (int)y;
                texDst.Width = (int)(width * scaleX);
                texDst.Height = (int)(height * scaleY);
                texSrc.X = (int)srcX;
                texSrc.Y = (int)srcY;
                texSrc.Width = (int)(srcWidth - srcX);
                texSrc.Height = (int)(srcHeight - srcY);
            }

            Load(texture);

            SpriteEffects e = SpriteEffects.None;

            if (flipX && !flipY)
            {
                e = SpriteEffects.FlipHorizontally;
            }
            else
                if (flipY && !flipX)
                {
                    e = SpriteEffects.FlipVertically;
                }
                else
                    if (flipX && flipY)
                    {
                        e = SpriteEffects.FlipHorizontally | SpriteEffects.FlipVertically;
                    }

            if (rotation != 0)
            {
                float centerX = texSrc.Width / 2;
                float centerY = texSrc.Height / 2;
                texDst.X += (int)texDst.Width / 2;
                texDst.Y += (int)texDst.Height / 2;
                origin.X = centerX;
                origin.Y = centerY;
                batch.Draw(texture.Texture, texDst, texSrc, color, MathUtils.ToRadians(rotation), origin, e, 0);
            }
            else if (originX != 0 || originX != 0)
            {
                texDst.X += (int)originX;
                texDst.Y += (int)originY;
                origin.X = originX;
                origin.Y = originY;
                batch.Draw(texture.Texture, texDst, texSrc, color, 0, origin, e, 0);
            }
            else
            {
                batch.Draw(texture.Texture, texDst, texSrc, color, 0, Vector2.Zero, e, 0);
            }
            idx++;
        }

        public void Draw(LTextureRegion region, float x, float y)
        {
            Draw(region, x, y, 0);
        }

        public void Draw(LTextureRegion region, float x, float y, float rotation)
        {
            if (isClose)
            {
                return;
            }
            if (region == null)
            {
                return;
            }
            LTexture texture = region.texture;
            if (texture == null)
            {
                return;
            }

            if (!texture.isLoaded)
            {
                texture.LoadTexture();
            }

            float xOff = region.xOff;
            float yOff = region.yOff;
            float widthRatio = region.widthRatio;
            float heightRatio = region.heightRatio;

            texDst.X = (int)x;
            texDst.Y = (int)y;
            texDst.Width = region.GetRegionWidth();
            texDst.Height = region.GetRegionHeight();
            texSrc.X = region.dstX;
            texSrc.Y = region.dstY;
            texSrc.Width = region.dstWidth;
            texSrc.Height = region.dstHeight;

            Load(texture);

            if (rotation != 0)
            {
                float centerX = texSrc.Width / 2;
                float centerY = texSrc.Height / 2;
                texDst.X += (int)texture.Width / 2;
                texDst.Y += (int)texture.Height / 2;
                origin.X = centerX;
                origin.Y = centerY;
                batch.Draw(texture, texDst, texSrc, color, MathUtils.ToRadians(rotation), origin, SpriteEffects.None, 0);
            }
            else
            {
                batch.Draw(texture, texDst, texSrc, color);
            }
            idx++;
        }

        public void Draw(LTextureRegion region, float x, float y, float width,
                float height, float rotation)
        {
            Draw(region, x, y, region.GetRegionWidth() / 2,
                    region.GetRegionHeight() / 2, width, height, 1f, 1f, rotation);
        }

        public void Draw(LTextureRegion region, float x, float y, float originX,
            float originY, float width, float height, float scaleX,
            float scaleY, float rotation)
        {
            Draw(region, x, y, originX, originY, width, height, scaleX, scaleY, rotation);
        }

        public void Draw(LTextureRegion region, float x, float y, float originX,
            float originY, float width, float height, float scaleX,
            float scaleY, float rotation, bool clockwise)
        {
            if (isClose)
            {
                return;
            }
            if (region == null)
            {
                return;
            }
            LTexture texture = region.texture;
            if (texture == null)
            {
                return;
            }

            if (!texture.isLoaded)
            {
                texture.LoadTexture();
            }

            float xOff = region.xOff;
            float yOff = region.yOff;
            float widthRatio = region.widthRatio;
            float heightRatio = region.heightRatio;

            texDst.X = (int)x;
            texDst.Y = (int)y;
            texDst.Width = (int)(width * scaleX);
            texDst.Height = (int)(height * scaleY);
            texSrc.X = region.dstX;
            texSrc.Y = region.dstY;
            texSrc.Width = region.dstWidth;
            texSrc.Height = region.dstHeight;

            Load(texture);

            if (rotation != 0)
            {
                float centerX = texSrc.Width / 2;
                float centerY = texSrc.Height / 2;
                texDst.X += (int)texDst.Width / 2;
                texDst.Y += (int)texDst.Height / 2;
                origin.X = centerX;
                origin.Y = centerY;
                batch.Draw(texture.Texture, texDst, texSrc, color, MathUtils.ToRadians(rotation), origin, SpriteEffects.None, 0);
            }
            else if (originX != 0 || originX != 0)
            {
                texDst.X += (int)originX;
                texDst.Y += (int)originY;
                origin.X = originX;
                origin.Y = originY;
                batch.Draw(texture.Texture, texDst, texSrc, color, 0, origin, SpriteEffects.None, 0);
            }
            else
            {
                batch.Draw(texture.Texture, texDst, texSrc, color, 0, Vector2.Zero, SpriteEffects.None, 0);
            }
            idx++;
        }

        private bool blendingDisabled = false;

        private int blendSrcFunc = 0;

        private int blendDstFunc = 0;

        public void DisableBlending()
        {
            Submit();
            blendingDisabled = true;
        }

        public void EnableBlending()
        {
            Submit();
            blendingDisabled = false;
        }

        public void SetBlendFunction(int srcFunc, int dstFunc)
        {
            Submit();
            blendSrcFunc = srcFunc;
            blendDstFunc = dstFunc;
        }

        public bool IsBlendingEnabled()
        {
            return !blendingDisabled;
        }

        public void Dispose()
        {
            if (batch != null)
            {
                batch.Dispose();
            }
            isClose = true;
            rectLazy.Clear();
            fillRectLazy.Clear();
            lineLazy.Clear();
            circleLazy.Clear();
            polyLazy.Clear();
        }
    }
}
