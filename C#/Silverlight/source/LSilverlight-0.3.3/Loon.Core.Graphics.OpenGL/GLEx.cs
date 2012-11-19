#region LGame License
/**
 * Copyright 2008 - 2012
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.3.3
 */
#endregion
namespace Loon.Core.Graphics.OpenGL
{
    using Microsoft.Xna.Framework.Graphics;
    using Microsoft.Xna.Framework;
    using Microsoft.Xna.Framework.Content;
    using Loon.Core.Geom;
    using Loon.Core.Graphics;
    using Loon.Utils;
    using Loon.Core.Graphics.Device;
    using Loon.Java;
    using Loon.Utils.Debug;

    /// <summary>
    /// GLEx专用摄影机，用以矫正GLEx与位置相关的2D显示结果
    /// </summary>
    public class GLExCamera
    {

        private GraphicsDevice device;

        private float rotation;

        private Vector2 position;

        private Vector2 scale;

        private Vector2 offset;

        private Rectangle visibleArea;

        internal Microsoft.Xna.Framework.Matrix viewMatrix;

        public Rectangle VisibleArea
        {
            get { return visibleArea; }
        }

        public int ViewingWidth
        {
            get { return visibleArea.Width; }
            set { visibleArea.Width = value; }
        }

        public int ViewingHeight
        {
            get { return visibleArea.Height; }
            set { visibleArea.Height = value; }
        }

        public Vector2 ScreenPosition
        {
            get { return new Vector2(device.Viewport.Width / 2, device.Viewport.Height / 2); }
        }

        public GLExCamera(GraphicsDevice d)
        {
            this.device = d;
            this.visibleArea = new Rectangle(0, 0, device.Viewport.Width, device.Viewport.Height);
            this.position = Vector2.Zero;
            this.scale = Vector2.One;
            this.rotation = 0.0f;
            this.offset = Vector2.Zero;
        }

        public Vector2 Offset
        {
            get { return offset; }
            set
            {
                offset = value;
                visibleArea.X = (int)(position.X + offset.X - visibleArea.Width / 2);
                visibleArea.Y = (int)(position.Y + offset.Y - visibleArea.Height / 2);
            }
        }

        public float Rotation
        {
            get { return this.rotation; }
            set { this.rotation = value; }
        }

        public Vector2 Scale
        {
            get { return this.scale; }
            set
            {
                this.scale = value;
            }
        }

        public void SetScale(float x, float y)
        {
            scale.X = x;
            scale.Y = y;
        }

        public Vector2 Position
        {
            get { return this.position; }
            set { this.position = value; }
        }

        public void SetTranslate(float x, float y)
        {
            position.X += x;
            position.Y += y;
        }

        public Microsoft.Xna.Framework.Matrix Old
        {
            get
            {
                this.position = Vector2.Zero;
                this.scale = Vector2.One;
                this.rotation = 0.0f;
                return Result;
            }
        }

        public Microsoft.Xna.Framework.Matrix Result
        {
            get
            {
                Vector3 matrixRotOrigin = new Vector3(Position + Offset, 0);
                Vector3 matrixScreenPos = new Vector3(ScreenPosition, 0.0f);

                Microsoft.Xna.Framework.Matrix result = Microsoft.Xna.Framework.Matrix.CreateTranslation(-matrixRotOrigin)
                    * Microsoft.Xna.Framework.Matrix.CreateScale(Scale.X, Scale.Y, 1.0f)
                    * Microsoft.Xna.Framework.Matrix.CreateRotationZ(rotation);


                return result;
            }
        }


    }

    public class GLEx : LTrans, LRelease
    {

        private Vector2 ZERO = Vector2.Zero;

        private Microsoft.Xna.Framework.Matrix viewStore;

        private Rectangle texDst = new Rectangle();

        private Rectangle texSrc = new Rectangle();

        public static int ToPowerOfTwo(int value)
        {
            if (value == 0)
            {
                return 1;
            }
            if ((value & value - 1) == 0)
            {
                return value;
            }
            value |= value >> 1;
            value |= value >> 2;
            value |= value >> 4;
            value |= value >> 8;
            value |= value >> 16;
            return value + 1;
        }

        internal static GLExCamera cemera;

        internal static GraphicsDevice device;

        public static GraphicsDevice Device
        {
            get
            {
                return device;
            }
        }

        private static GLEx self;

        public static GLEx Self
        {
            get
            {
                return self;
            }
        }

        public Microsoft.Xna.Framework.Matrix View
        {
            get
            {
                return cemera.viewMatrix;
            }
        }

        private Rectangle rectangle;

        private RectBox xnaClip;

        private Vector2 postion;

        private SpriteBatch innterBatch;

        private LFont font;

        private XNA_GL shape;

        private Color color;

        private bool isClose, isDirty;

        private bool useGLBegin, useFont;

        private readonly RasterizerState clipRasterizerState;

        public static Texture2D WhitePixel { get; private set; }

        public static GL gl;

        public static GL10 gl10;

        public GLEx(GraphicsDevice device)
            : this(device, null)
        {
        }

        public GLEx(GraphicsDevice device, LFont f)
        {
            GLEx.device = device;
            GLEx.cemera = new GLExCamera(device);
            clipRasterizerState = new RasterizerState() { ScissorTestEnable = true };
            device.RasterizerState = RasterizerState.CullNone;
            device.DepthStencilState = DepthStencilState.None;
            cemera.viewMatrix = cemera.Result;
            if (f == null)
            {
                SetFont(LFont.GetDefaultFont());
            }
            else
            {
                SetFont(f);
            }
            this.xnaClip = new RectBox(LSystem.screenRect);
            this.font = LFont.GetDefaultFont();
            this.innterBatch = new SpriteBatch(device);
            this.rectangle = new Rectangle(0, 0, 0, 0);
            this.postion = new Vector2(0, 0);
            this.color = Color.White;

            WhitePixel = new Texture2D(device, 1, 1, true, SurfaceFormat.Color);
            Color[] pixels = { Color.White };
            WhitePixel.SetData(pixels);
            GLEx.self = this;

            GLEx.gl = new GL();
            GLEx.gl10 = new GL10();

            shape = XNA_GL.LoadGLShape;
        }

        public SpriteBatch GetSpriteBatch()
        {
            return innterBatch;
        }

        public void Scale(float x, float y)
        {
            cemera.SetScale(x, y);
            cemera.viewMatrix = cemera.Result;
            isDirty = true;
        }

        public float GetScaleX()
        {
            return cemera.Scale.X;
        }

        public float GetScaleY()
        {
            return cemera.Scale.Y;
        }

        public void SetRotation(float r)
        {
            cemera.Rotation = MathHelper.ToRadians(r);
            cemera.viewMatrix = cemera.Result;
            isDirty = true;
        }

        public float GetRotation()
        {
            return cemera.Rotation;
        }

        private void UPos(float x, float y)
        {
            postion.X = x;
            postion.Y = y;
        }

        private void URect(float x, float y, float w, float h)
        {
            rectangle.X = (int)x;
            rectangle.Y = (int)y;
            rectangle.Width = (int)w;
            rectangle.Height = (int)h;
        }

        public void SetAntiAlias(bool anti)
        {

        }

        public void SetAlpha(byte alpha)
        {
            color.A = alpha;
        }

        public void SetAlpha(float alpha)
        {
            if (alpha < 0)
            {
                alpha = 0;
            }
            else if (alpha > 255)
            {
                alpha = 255;
            }
            color.A = (byte)(255 * alpha);
        }

        public float GetAlpha()
        {
            return this.color.A / 255;
        }

        public void GLPushMatrix()
        {
            this.viewStore = cemera.viewMatrix;
        }

        public void GLPopMatrix()
        {
            if (viewStore != null)
            {
                cemera.viewMatrix = viewStore;
            }
        }

        public void Reset(bool c)
        {
            color.PackedValue = 4294967295;
            if (c)
            {
                device.Clear(Color.Black);
            }
        }


        public void RestoreMatrix()
        {
            if (isDirty)
            {
                cemera.viewMatrix = cemera.Old;
                isDirty = false;
            }
            if (font != null)
            {
                LFont resetFont = LFont.GetDefaultFont();
                if (resetFont != null)
                {
                    font = resetFont;
                }
            }
            ResetLineWidth();
            ClearClip();
        }

        public void Clear()
        {
            device.Clear(Color.Black);
        }

        public void DrawClear(Color c)
        {
            device.Clear(c);
        }

        public void DrawClear(LColor c)
        {
            device.Clear(c.Color);
        }

        public void SetLineWidth(int l)
        {
            shape.LineWidth = l;
        }

        public int GetLineWidth()
        {
            return shape.LineWidth;
        }

        public void ResetLineWidth()
        {
            shape.LineWidth = 0;
        }

        public bool UseFont
        {
            get
            {
                return useFont;
            }
        }

        public void SetFont(LFont f)
        {
            this.font = f;
            if (font != null)
            {
                useFont = true;
            }
            else
            {
                useFont = false;
            }
        }

        public LFont GetFont()
        {
            return this.font;
        }

        /// <summary>
        /// 矫正显示坐标到指定位置
        /// </summary>
        /// <param name="x"></param>
        /// <param name="y"></param>
        public void Translate(float x, float y)
        {
            cemera.SetTranslate(-x, -y);
            cemera.viewMatrix = cemera.Result;
            isDirty = true;
            xnaClip.x = MathUtils.Min(xnaClip.x + GetTranslateX(), LSystem.screenRect.width);
            xnaClip.y = MathUtils.Min(xnaClip.y + GetTranslateY(), LSystem.screenRect.height);
            xnaClip.width = (int)MathUtils.Min(xnaClip.width + GetTranslateX(), LSystem.screenRect.width
                    - GetTranslateX());
            xnaClip.height = (int)MathUtils.Min(xnaClip.height + GetTranslateY(), LSystem.screenRect.height
                    - GetTranslateY());
        }

        public RectBox GetClipBounds()
        {
            if (isClose)
            {
                return null;
            }

            return xnaClip;
        }

        private bool isClipRect;

        public void ClipRect(int x, int y, int width, int height)
        {
            if (isClose)
            {
                return;
            }
            if (!isClipRect)
            {
                device.RasterizerState = clipRasterizerState;
                isClipRect = true;
            }
            try
            {
                xnaClip = xnaClip.GetIntersection(new RectBox((x + GetTranslateX()) * LSystem.scaleWidth, (y
                       + GetTranslateY()) * LSystem.scaleHeight, width * LSystem.scaleWidth, height * LSystem.scaleHeight));
                device.ScissorRectangle = xnaClip.GetRectangle2D();
            }
            catch (System.Exception)
            {
            }

        }

        public void SetClip(int x, int y, int width, int height)
        {
            if (isClose)
            {
                return;
            }
            if (!isClipRect)
            {
                device.RasterizerState = clipRasterizerState;
                isClipRect = true;
            }
            try
            {
                xnaClip.SetBounds(MathUtils.Max(x + GetTranslateX(), 0) * LSystem.scaleWidth, MathUtils.Max(y
                        + GetTranslateY(), 0) * LSystem.scaleHeight, MathUtils.Min(width, width - GetTranslateX()) * LSystem.scaleWidth,
                        MathUtils.Min(height, height - GetTranslateY()) * LSystem.scaleHeight);
                device.ScissorRectangle = xnaClip.GetRectangle2D();
            }
            catch (System.Exception)
            {
            }
        }

        public void ClearClip()
        {
            if (isClipRect)
            {
                xnaClip.SetBounds(LSystem.screenRect.x, LSystem.screenRect.y, LSystem.screenRect.width * LSystem.scaleWidth, LSystem.screenRect.height * LSystem.scaleHeight);
                device.ScissorRectangle = xnaClip.GetRectangle2D();
                device.RasterizerState = RasterizerState.CullNone;
                isClipRect = false;
            }
        }

        public RectBox GetClip()
        {
            return GetClipBounds();
        }

        public int GetClipWidth()
        {
            return xnaClip.width;
        }

        public int GetClipHeight()
        {
            return xnaClip.height;
        }

        public float GetTranslateX()
        {
            return cemera.Position.X * -1;
        }

        public float GetTranslateY()
        {
            return cemera.Position.Y * -1;
        }

        void InBegin(Color c)
        {
            InBegin(c, false, true);
        }

        void InBegin(Color c, bool alpha)
        {
            InBegin(c, false, alpha);
        }

        void InBegin(Color c, bool non, bool alpha)
        {
            if (non)
            {
                innterBatch.Begin(SpriteSortMode.Deferred, BlendState.NonPremultiplied, null, null, device.RasterizerState, null, cemera.viewMatrix);
            }
            else
            {
                if (alpha && c.A != 255)
                {
                    innterBatch.Begin(SpriteSortMode.Deferred, BlendState.Additive, null, null, device.RasterizerState, null, cemera.viewMatrix);
                }
                else
                {
                    innterBatch.Begin(SpriteSortMode.Deferred, BlendState.AlphaBlend, null, null, device.RasterizerState, null, cemera.viewMatrix);
                }
            }
        }

        void InBegin(Color c, LTexture tex2d)
        {
            InBegin(c, tex2d, true);
        }

        void InBegin(Color c, LTexture tex2d, bool alpha)
        {

            if (tex2d.isOpaque)
            {
                if (alpha && c.A != 255)
                {
                    innterBatch.Begin(SpriteSortMode.Deferred, BlendState.Additive, null, null, device.RasterizerState, null, cemera.viewMatrix);
                }
                else
                {
                    innterBatch.Begin(SpriteSortMode.Deferred, BlendState.Opaque, null, null, device.RasterizerState, null, cemera.viewMatrix);
                }
            }
            else
            {
                if (tex2d.isExt)
                {
                    innterBatch.Begin(SpriteSortMode.Deferred, BlendState.NonPremultiplied, null, null, device.RasterizerState, null, GLEx.cemera.viewMatrix);
                }
                else
                {
                    innterBatch.Begin(SpriteSortMode.Deferred, BlendState.AlphaBlend, null, null, device.RasterizerState, null, GLEx.cemera.viewMatrix);
                }
            }

        }

        void InEnd()
        {
            innterBatch.End();
        }

        public void GLBegin()
        {
            shape.GLBegin(GL.GL_QUADS);
        }

        public void GLBegin(int type)
        {
            shape.GLBegin(type);
        }

        public void GLVertex3f(float x, float y, float z)
        {
            shape.GLVertex3f(x, y, z);
        }

        public void GLVertex2f(float x, float y)
        {
            shape.GLVertex2f(x, y);
        }

        public void GLColor(float r, float g, float b, float a)
        {
            shape.GLColor(r, g, b, a);
        }

        public void GLColor(LColor c)
        {
            shape.GLColor(c);
        }

        public void GLColor(float r, float g, float b)
        {
            GLColor(r, g, b, 1f);
        }

        public void GLEnd()
        {
            shape.GLEnd(color);
        }

        public void ResetColor()
        {
            color.R = 255;
            color.G = 255;
            color.B = 255;
            color.A = 255;
        }

        public void SetColor(int r, int g, int b)
        {
            color.R = (byte)r;
            color.G = (byte)g;
            color.B = (byte)b;
        }

        public void SetColor(int r, int g, int b, int a)
        {
            color.R = (byte)r;
            color.G = (byte)g;
            color.B = (byte)b;
            color.A = (byte)a;
        }

        public void SetColor(byte r, byte g, byte b)
        {
            color.R = r;
            color.G = g;
            color.B = b;
        }

        public void SetColor(byte r, byte g, byte b, byte a)
        {
            color.R = r;
            color.G = g;
            color.B = b;
            color.A = a;
        }

        public void SetColor(LColor c)
        {
            if (color.A == 255)
            {
                color.PackedValue = c.Color.PackedValue;
            }
            else
            {
                color.R = c.Color.R;
                color.G = c.Color.G;
                color.B = c.Color.B;
            }
        }

        public void SetColor(uint c)
        {
            if (color.A == 255)
            {
                color.PackedValue = c;
            }
            else
            {
                color.R = LColor.GetRed(c);
                color.G = LColor.GetGreen(c);
                color.B = LColor.GetBlue(c);
            }
        }

        public void SetColor(Color c)
        {
            if (c == null)
            {
                this.color = Color.White;
            }
            else
            {
                if (color.A == 255)
                {
                    color.PackedValue = c.PackedValue;
                }
                else
                {
                    color.R = c.R;
                    color.G = c.G;
                    color.B = c.B;
                }
            }
        }

        public LColor GetLColor()
        {
            return new LColor(color);
        }

        public Color GetColor()
        {
            return new Color(color.R, color.G, color.B, color.A);
        }

        public void Restore()
        {
            this.RestoreMatrix();
        }

        public void Rotate(float rx, float ry, float angle)
        {
            cemera.SetTranslate(rx, ry);
            cemera.Rotation = angle;
        }

        public void Rotate(float angle)
        {
            cemera.Rotation = angle;
        }

        public void SetBackground(LColor color)
        {
            DrawClear(color);
        }

        public void SetBackground(Color color)
        {
            DrawClear(color);
        }

        public void DrawString(string text, float x, float y)
        {
            DrawString(text, x, y, color);
        }

        public void DrawString(string text, float x, float y, LColor c)
        {
            DrawString(text, x, y, c.Color);
        }

        public void DrawString(string text, float x, float y, Color c)
        {
            if (font == null)
            {
                throw new RuntimeException("Did not set any Font !");
            }
            else
            {
                UPos(x, y);
                SpriteFont spriteFont = font.Font;
                if (spriteFont != null)
                {
                    InBegin(c, false, false);
                    innterBatch.DrawString(spriteFont, text, postion, c);
                    InEnd();
                }
            }
        }

        public void DrawString(string text, float x, float y, float rotation, LColor c)
        {
            DrawString(text, x, y, rotation, c.Color);
        }

        public void DrawString(string text, float x, float y, float rotation, Color c)
        {
            if (font == null)
            {
                throw new RuntimeException("Did not set any Font !");
            }
            else
            {
                UPos(x, y);
                SpriteFont spriteFont = font.Font;
                if (spriteFont != null)
                {
                    InBegin(c, false, false);
                    if (rotation != 0)
                    {
                        float centerX = font.StringWidth(text) / 2;
                        float centerY = font.StringHeight(text) / 2;
                        Vector2 origin = new Vector2(centerX, centerY);
                        innterBatch.DrawString(
                        spriteFont,
                        text,
                        postion,
                        c,
                        rotation,
                        origin,
                        1f,
                        SpriteEffects.None,
                        0);
                    }
                    else
                    {
                        innterBatch.DrawString(spriteFont, text, postion, c);
                    }
                    InEnd();
                }
            }
        }

        public void DrawChar(char chars, float x, float y)
        {
            DrawChar(chars, x, y, 0);
        }

        public void DrawChar(char chars, float x, float y, float rotation)
        {
            DrawChar(chars, x, y, rotation, color);
        }

        public void DrawChar(char chars, float x, float y, float rotation, Color c)
        {
            DrawString("" + chars, x, y, rotation, c);
        }

        public void DrawChar(char chars, float x, float y, float rotation, LColor c)
        {
            DrawString("" + chars, x, y, rotation, c);
        }

        public void DrawChar(byte[] message, int offset, int length, int x, int y)
        {
            if (isClose)
            {
                return;
            }
            DrawString(StringUtils.Substring(StringUtils.NewString(message), offset, length), x, y);
        }

        public void DrawChars(char[] message, int offset, int length, int x, int y)
        {
            if (isClose)
            {
                return;
            }
            DrawString(StringUtils.Substring(StringUtils.NewString(message), offset, length), x, y);
        }

        public void DrawString(string message, int x, int y, int anchor)
        {
            if (isClose)
            {
                return;
            }
            if (font == null)
            {
                throw new RuntimeException("Did not set any Font !");
            }
            else
            {
                float newx = x;
                float newy = y;
                if (anchor == 0)
                {
                    anchor = TOP | LEFT;
                }
                if ((anchor & TOP) != 0)
                {
                    newy -= font.Ascent;
                }
                else if ((anchor & BOTTOM) != 0)
                {
                    newy -= font.Ascent;
                }
                if ((anchor & HCENTER) != 0)
                {
                    newx -= font.StringWidth(message) / 2;
                }
                else if ((anchor & RIGHT) != 0)
                {
                    newx -= font.StringWidth(message);
                }
                DrawString(message, newx, newy);
            }
        }

        public void DrawStyleString(string message, float x, float y, uint color,
                uint color1)
        {
            if (isClose)
            {
                return;
            }
            if (font == null)
            {
                throw new RuntimeException("Did not set any Font !");
            }
            else
            {
                SetColor(color);
                DrawString(message, x + 1, y);
                DrawString(message, x - 1, y);
                DrawString(message, x, y + 1);
                DrawString(message, x, y - 1);
                SetColor(color1);
                DrawString(message, x, y);
            }
        }

        public void DrawStyleString(string message, float x, float y, LColor c1,
                LColor c2)
        {
            if (isClose)
            {
                return;
            }
            if (font == null)
            {
                throw new RuntimeException("Did not set any Font !");
            }
            else
            {
                SetColor(c1);
                DrawString(message, x + 1, y);
                DrawString(message, x - 1, y);
                DrawString(message, x, y + 1);
                DrawString(message, x, y - 1);
                SetColor(c2);
                DrawString(message, x, y);
            }
        }

        public void DrawPixmap(LPixmap pix, float x, float y)
        {
            DrawPixmap(pix, x, y, color);
        }

        public void DrawPixmap(LPixmap pix, float x, float y, LColor c)
        {
            DrawPixmap(pix, x, y, c.Color);
        }

        public void DrawPixmap(LPixmap pix, float x, float y, Color c)
        {
            if (pix == null)
            {
                return;
            }
            UPos(x, y);
            InBegin(c);
            innterBatch.Draw(pix.Get(), postion, pix.Color);
            InEnd();
        }

        public void DrawTexture(Texture2D tex2d, float x, float y)
        {
            DrawTexture(tex2d, x, y, color);
        }

        public void DrawTexture(Texture2D tex2d, float x, float y, LColor c)
        {
            DrawTexture(tex2d, x, y, c.Color);
        }

        public void DrawTexture(Texture2D tex2d, float x, float y, Color c)
        {
            UPos(x, y);
            InBegin(c);
            this.innterBatch.Draw(tex2d, postion, color);
            InEnd();
        }

        public void Draw(
            Texture2D texture2D,
            Vector2 position,
            Rectangle? sourceRectangle,
            Color color,
            float rotation,
            Vector2 origin,
            Vector2 scale,
            SpriteEffects effects,
            float layerDepth)
        {
            innterBatch.Draw(
                texture2D,
                position,
                sourceRectangle,
                color,
                rotation,
                origin,
                scale,
                effects,
                0);
        }

        public void Draw(
            Texture2D texture2D,
            Vector2 position,
            Rectangle? sourceRectangle,
            Color color,
            float rotation,
            Vector2 origin,
            float scale,
            SpriteEffects effects,
            float layerDepth)
        {
            innterBatch.Draw(
                texture2D,
                position,
                sourceRectangle,
                color,
                rotation,
                origin,
                scale,
                effects,
                0);
        }

        public void Draw(
            Texture2D texture2D,
            Rectangle destinationRectangle,
            Rectangle? sourceRectangle,
            Color color,
            float rotation,
            Vector2 origin,
            SpriteEffects effects,
            float layerDepth)
        {
            destinationRectangle.X = MathUtils.Round(destinationRectangle.X);
            destinationRectangle.Y = MathUtils.Round(destinationRectangle.Y);
            destinationRectangle.Width = MathUtils.Round(destinationRectangle.Width);
            destinationRectangle.Height = MathUtils.Round(destinationRectangle.Height);
            innterBatch.Draw(
                 texture2D,
                 destinationRectangle,
                 sourceRectangle,
                 color,
                 rotation,
                 origin,
                 effects,
                 0);
        }

        public void DrawString(
            SpriteFont font,
            string text,
            Vector2 position,
            Color color,
            float rotation,
            Vector2 origin,
            float scale,
            SpriteEffects spriteEffects,
            float layerDepth)
        {
            innterBatch.DrawString(
                font,
                text,
                position,
                color,
                rotation,
                origin,
                scale,
                spriteEffects,
                0);
        }

        public void DrawString(
            SpriteFont font,
            string text,
            Vector2 position,
            Color color,
            float rotation,
            Vector2 origin,
            Vector2 scale,
            SpriteEffects spriteEffects,
            float layerDepth)
        {
            innterBatch.DrawString(
                font,
                text,
                position,
                color,
                rotation,
                origin,
                scale,
                spriteEffects,
                0);
        }

        public void DrawRegion(LTexture texture, int x_src, int y_src, int width,
        int height, int transform, int x_dst, int y_dst, int anchor)
        {
            DrawRegion(texture, x_src, y_src, width, height, transform, x_dst,
                    y_dst, anchor, color);
        }

        public void DrawRegion(LTexture texture, int x_src, int y_src, int width,
            int height, int transform, int x_dst, int y_dst, int anchor,
            LColor c)
        {
            DrawRegion(texture, x_src, y_src, width, height, transform, x_dst, y_dst, anchor,
             c.Color);
        }

        public void DrawRegion(LTexture texture, int x_src, int y_src, int width,
            int height, int transform, int x_dst, int y_dst, int anchor,
            Color c)
        {
            if (isClose || texture == null || texture.isClose)
            {
                return;
            }
            if (x_src + width > texture.Width
                    || y_src + height > texture.Height || width < 0
                    || height < 0 || x_src < 0 || y_src < 0)
            {
                throw new System.ArgumentException("Area out of texture");
            }
            int dW = width, dH = height;

            float rotate = 0;

            SpriteEffects dir = SpriteEffects.None;

            switch (transform)
            {
                case TRANS_NONE:
                    {
                        break;
                    }
                case TRANS_ROT90:
                    {
                        rotate = 90;
                        dW = height;
                        dH = width;
                        break;
                    }
                case TRANS_ROT180:
                    {
                        rotate = 180;
                        break;
                    }
                case TRANS_ROT270:
                    {
                        rotate = 270;
                        dW = height;
                        dH = width;
                        break;
                    }
                case TRANS_MIRROR:
                    {
                        dir = SpriteEffects.FlipHorizontally;
                        break;
                    }
                case TRANS_MIRROR_ROT90:
                    {
                        dir = SpriteEffects.FlipHorizontally;
                        rotate = -90;
                        dW = height;
                        dH = width;
                        break;
                    }
                case TRANS_MIRROR_ROT180:
                    {
                        dir = SpriteEffects.FlipHorizontally;
                        rotate = -180;
                        break;
                    }
                case TRANS_MIRROR_ROT270:
                    {
                        dir = SpriteEffects.FlipHorizontally;
                        rotate = -270;
                        dW = height;
                        dH = width;
                        break;
                    }
                default:
                    throw new System.ArgumentException("Bad transform");
            }

            bool badAnchor = false;

            if (anchor == 0)
            {
                anchor = TOP | LEFT;
            }

            if ((anchor & 0x7f) != anchor || (anchor & BASELINE) != 0)
            {
                badAnchor = true;
            }

            if ((anchor & TOP) != 0)
            {
                if ((anchor & (VCENTER | BOTTOM)) != 0)
                {
                    badAnchor = true;
                }
            }
            else if ((anchor & BOTTOM) != 0)
            {
                if ((anchor & VCENTER) != 0)
                {
                    badAnchor = true;
                }
                else
                {
                    y_dst -= dH - 1;
                }
            }
            else if ((anchor & VCENTER) != 0)
            {
                y_dst -= (int)((uint)(dH - 1) >> 1);
            }
            else
            {
                badAnchor = true;
            }

            if ((anchor & LEFT) != 0)
            {
                if ((anchor & (HCENTER | RIGHT)) != 0)
                {
                    badAnchor = true;
                }
            }
            else if ((anchor & RIGHT) != 0)
            {
                if ((anchor & HCENTER) != 0)
                {
                    badAnchor = true;
                }
                else
                {
                    x_dst -= dW - 1;
                }
            }
            else if ((anchor & HCENTER) != 0)
            {
                x_dst -= (int)((uint)(dW - 1) >> 1);
            }
            else
            {
                badAnchor = true;
            }
            if (badAnchor)
            {
                throw new System.ArgumentException("Bad Anchor");
            }

            DrawTexture(texture, x_dst, y_dst, width, height, x_src, y_src, x_src
                    + width, y_src + height, rotate, c, dir);
        }

        public void DrawSixStart(LColor color, float x, float y, float r)
        {
            DrawSixStart(color.Color, x, y, r);
        }

        public void DrawSixStart(Color color, float x, float y, float r)
        {
            shape.DrawSixStart(color, x, y, r);
        }

        public void DrawTriangle(LColor color, float x, float y, float r)
        {
            DrawTriangle(color.Color, x, y, r);
        }

        public void DrawTriangle(Color color, float x, float y, float r)
        {
            shape.DrawTriangle(color, x, y, r);
        }

        public void DrawRTriangle(LColor color, float x, float y, float r)
        {
            DrawRTriangle(color.Color, x, y, r);
        }

        public void DrawRTriangle(Color color, float x, float y, float r)
        {
            shape.DrawRTriangle(color, x, y, r);
        }

        public void DrawTriangle(float x1, float y1, float x2,
                 float y2, float x3, float y3, LColor c)
        {
            DrawTriangle(x1, y1, x2, y2, x3, y3, c.Color);
        }

        public void DrawTriangle(float x1, float y1, float x2,
                 float y2, float x3, float y3, Color c)
        {
            shape.DrawTriangle(x1, y1, x2, y2, x3, y3, c);
        }

        public void DrawTriangle(Triangle2f[] ts, int x, int y, LColor c)
        {
            DrawTriangle(ts, x, y, c.Color);
        }

        public void DrawTriangle(Triangle2f[] ts, int x, int y, Color c)
        {
            shape.DrawTriangle(ts, x, y, c);
        }

        public void DrawTriangle(Triangle2f[] ts, LColor c)
        {
            FillTriangle(ts, c.Color);
        }

        public void DrawTriangle(Triangle2f[] ts, Color c)
        {
            shape.FillTriangle(ts, c);
        }

        public void DrawTriangle(Triangle2f t, LColor c)
        {
            DrawTriangle(t, c.Color);
        }

        public void DrawTriangle(Triangle2f t, Color c)
        {
            shape.DrawTriangle(t, c);
        }

        public void DrawTriangle(Triangle2f t, float x, float y, LColor c)
        {
            DrawTriangle(t, x, y, c.Color);
        }

        public void DrawTriangle(Triangle2f t, float x, float y, Color c)
        {
            shape.DrawTriangle(t, x, y, c);
        }

        public void FillTriangle(float x1, float y1, float x2,
                 float y2, float x3, float y3, LColor c)
        {
            FillTriangle(x1, y1, x2, y2, x3, y3, c.Color);
        }

        public void FillTriangle(float x1, float y1, float x2,
                 float y2, float x3, float y3, Color c)
        {
            shape.FillTriangle(x1, y1, x2, y2, x3, y3, c);
        }

        public void FillTriangle(Triangle2f[] ts, LColor c)
        {
            FillTriangle(ts, 0, 0, c.Color);
        }

        public void FillTriangle(Triangle2f[] ts, Color c)
        {
            shape.FillTriangle(ts, 0, 0, c);
        }

        public void FillTriangle(Triangle2f[] ts, int x, int y, LColor c)
        {
            FillTriangle(ts, x, y, c.Color);
        }

        public void FillTriangle(Triangle2f[] ts, int x, int y, Color c)
        {
            shape.FillTriangle(ts, x, y, c);
        }

        public void FillTriangle(Triangle2f t, LColor c)
        {
            FillTriangle(t, c.Color);
        }

        public void FillTriangle(Triangle2f t, Color c)
        {
            shape.FillTriangle(t, c);
        }

        public void FillTriangle(Triangle2f t, float x, float y, LColor c)
        {
            FillTriangle(t, x, y, c.Color);
        }

        public void FillTriangle(Triangle2f t, float x, float y, Color c)
        {
            shape.FillTriangle(t, x, y, c);
        }

        public void FillPolygon(float[] xPoints, float[] yPoints,
            int nPoints)
        {
            shape.FillPolygon(xPoints, yPoints, nPoints, color);
        }

        public void FillPolygon(float[] xPoints, float[] yPoints,
            int nPoints, LColor c)
        {
            FillPolygon(xPoints, yPoints, nPoints, c.Color);
        }

        public void FillPolygon(float[] xPoints, float[] yPoints,
            int nPoints, Color c)
        {
            shape.FillPolygon(xPoints, yPoints, nPoints, c);
        }

        public void DrawPolygon(float[] xPoints, float[] yPoints,
            int nPoints)
        {
            shape.DrawPolygon(xPoints, yPoints, nPoints, color);
        }

        public void DrawPolygon(float[] xPoints, float[] yPoints,
            int nPoints, LColor c)
        {
            DrawPolygon(xPoints, yPoints, nPoints, c.Color);
        }

        public void DrawPolygon(float[] xPoints, float[] yPoints,
            int nPoints, Color c)
        {
            shape.DrawPolygon(xPoints, yPoints, nPoints, c);
        }

        public void DrawTexture(LTexture texture, float x, float y)
        {
            if (texture == null)
            {
                return;
            }
            DrawTexture(texture, x, y, color);
        }

        public void DrawTexture(LTexture texture, float x, float y, LColor c)
        {
            if (texture == null)
            {
                return;
            }
            DrawTexture(texture, x, y, texture.Width, texture.Height, 0, 0, texture.Width, texture.Height, 0, c.Color);
        }

        public void DrawTexture(LTexture texture, float x, float y, Color c)
        {
            if (texture == null)
            {
                return;
            }
            DrawTexture(texture, x, y, texture.Width, texture.Height, 0, 0, texture.Width, texture.Height, 0, c);
        }

        public void DrawTexture(LTexture texture, float x, float y, float width, float height)
        {
            if (texture == null)
            {
                return;
            }
            DrawTexture(texture, x, y, width, height, color);
        }

        public void DrawTexture(LTexture texture, float x, float y, float width, float height, LColor c)
        {
            if (texture == null)
            {
                return;
            }
            DrawTexture(texture, x, y, width, height, 0, c.Color);
        }

        public void DrawTexture(LTexture texture, float x, float y, float width, float height, Color c)
        {
            if (texture == null)
            {
                return;
            }
            DrawTexture(texture, x, y, width, height, 0, c);
        }

        public void DrawTexture(LTexture texture, float x, float y, float rotation, LColor c)
        {
            if (texture == null)
            {
                return;
            }
            DrawTexture(texture, x, y, texture.Width, texture.Height, rotation, c.Color);
        }

        public void DrawTexture(LTexture texture, float x, float y, float rotation, Color c)
        {
            if (texture == null)
            {
                return;
            }
            DrawTexture(texture, x, y, texture.Width, texture.Height, rotation, c);
        }

        public void DrawTexture(LTexture texture, float x, float y, float rotation)
        {
            DrawTexture(texture, x, y, rotation, color);
        }

        public void DrawTexture(LTexture texture, float x, float y, float width,
           float height, float rotation, LColor c)
        {
            if (texture == null)
            {
                return;
            }
            DrawTexture(texture, x, y, width, height, 0, 0, texture.Width, texture.Height, rotation, c.Color);
        }

        public void DrawTexture(LTexture texture, float x, float y, float width,
           float height, float rotation, Color c)
        {
            if (texture == null)
            {
                return;
            }
            DrawTexture(texture, x, y, width, height, 0, 0, texture.Width, texture.Height, rotation, c);
        }

        public void DrawTexture(LTexture texture, float x, float y, float width,
          float height, float rotation)
        {
            if (texture == null)
            {
                return;
            }
            DrawTexture(texture, x, y, width, height, 0, 0, texture.Width, texture.Height, rotation, color);
        }

        public void DrawTexture(LTexture texture, float x, float y, float width,
            float height, float srcX, float srcY, float srcWidth,
            float srcHeight)
        {
            DrawTexture(texture, x, y, width, height, srcX, srcY, srcWidth, srcHeight, 0);
        }

        public void DrawTexture(LTexture texture, float x, float y, float width,
            float height, float srcX, float srcY, float srcWidth,
            float srcHeight, float rotation)
        {
            DrawTexture(texture, x, y, width, height, srcX, srcY, srcWidth, srcHeight, rotation, color, SpriteEffects.None);
        }

        public void DrawTexture(LTexture texture, float x, float y, float width,
            float height, float srcX, float srcY, float srcWidth,
            float srcHeight, float rotation, SpriteEffects effect)
        {
            DrawTexture(texture, x, y, width, height, srcX, srcY, srcWidth, srcHeight, rotation, color, effect);
        }

        public void DrawTexture(LTexture texture, float x, float y, float width,
            float height, float srcX, float srcY, float srcWidth,
            float srcHeight, float rotation, LColor c)
        {
            if (c != null)
            {
                DrawTexture(texture, x, y, width, height, srcX, srcY, srcWidth, srcHeight, rotation, c.Color, SpriteEffects.None);
            }
            else
            {
                DrawTexture(texture, x, y, width, height, srcX, srcY, srcWidth, srcHeight, rotation, color, SpriteEffects.None);
            }
        }

        public void DrawTexture(LTexture texture, float x, float y, float width,
            float height, float srcX, float srcY, float srcWidth,
            float srcHeight, float rotation, Color c)
        {
            DrawTexture(texture, x, y, width, height, srcX, srcY, srcWidth, srcHeight, rotation, c, SpriteEffects.None);
        }

        public void DrawTexture(LTexture texture, float x, float y, float width,
            float height, float srcX, float srcY, float srcWidth,
            float srcHeight, float rotation, LColor c, SpriteEffects effect)
        {
            DrawTexture(texture, x, y, width, height, srcX, srcY, srcWidth, srcHeight, rotation, c.Color, effect);
        }

        public void DrawFlipTexture(LTexture texture, float x, float y,
                LColor color)
        {
            if (texture == null)
            {
                return;
            }
            DrawTexture(texture, x, y, texture.width, texture.height, 0, 0,
            texture.width, texture.height, 0, color,
            SpriteEffects.FlipHorizontally);
        }

        public void DrawMirrorTexture(LTexture texture, float x, float y,
                LColor color)
        {
            if (texture == null)
            {
                return;
            }
            DrawTexture(texture, x, y, texture.width, texture.height, 0, 0,
            texture.width, texture.height, 0, color,
            SpriteEffects.FlipVertically);
        }

        public void DrawTexture(LTexture texture, float x, float y, float width,
            float height, float srcX, float srcY, float srcWidth,
            float srcHeight, float rotation, Color c, SpriteEffects effect)
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
                texSrc.X = (int)srcX;
                texSrc.Y = (int)srcY;
                texSrc.Width = (int)(srcWidth - srcX);
                texSrc.Height = (int)(srcHeight - srcY);
            }
            InBegin(c, texture);
            if (rotation != 0)
            {
                float centerX = texSrc.Width / 2;
                float centerY = texSrc.Height / 2;
                texDst.X += (int)width / 2;
                texDst.Y += (int)height / 2;
                UPos(centerX, centerY);
                innterBatch.Draw(texture.Texture, texDst, texSrc, c, MathUtils.ToRadians(rotation), postion, SpriteEffects.None, 0);
            }
            else
            {
                innterBatch.Draw(texture.Texture, texDst, texSrc, c, 0, ZERO, effect, 0);
            }
            InEnd();
        }

        public void FillOval(float x, float y, float width, float height)
        {
            shape.FillOval(x, y, width, height, color);
        }

        public void FillOval(float x, float y, float width, float height, LColor c)
        {
            FillOval(x, y, width, height, c.Color);
        }

        public void FillOval(float x, float y, float width, float height, Color c)
        {
            shape.FillOval(x, y, width, height, c);
        }

        public void FillArc(float x1, float y1, float width, float height,
            float start, float end)
        {
            FillArc(x1, y1, width, height, start, end, color);
        }

        public void FillArc(float x1, float y1, float width, float height,
            float start, float end, LColor c)
        {
            FillArc(x1, y1, width, height, start, end, c.Color);
        }

        public void FillArc(float x1, float y1, float width, float height,
            float start, float end, Color c)
        {
            shape.FillArc(x1, y1, width, height, start, end, c);
        }

        public void Draw(Shape s)
        {
            shape.Draw(s, color);
        }

        public void Draw(Shape s, LColor c)
        {
            Draw(s, c.Color);
        }

        public void Draw(Shape s, Color c)
        {
            shape.Draw(s, c);
        }

        public void Fill(Shape s)
        {
            shape.Fill(s, color);
        }

        public void Fill(Shape s, LColor c)
        {
            Fill(s, c.Color);
        }

        public void Fill(Shape s, Color c)
        {
            shape.Fill(s, c);
        }

        public void GLLine(float x1, float y1, float x2, float y2)
        {
            GLLine(x1, y1, x2, y2, color);
        }

        public void GLLine(float x1, float y1, float x2, float y2, LColor c)
        {
            GLLine(x1, y1, x2, y2, c.Color);
        }

        public void GLLine(float x1, float y1, float x2, float y2, Color c)
        {
            if (x1 > x2)
            {
                x1++;
            }
            else
            {
                x2++;
            }
            if (y1 > y2)
            {
                y1++;
            }
            else
            {
                y2++;
            }
            shape.DrawLine(innterBatch, new Vector2(x1, y1), new Vector2(x2, y2), WhitePixel, c);
        }

        public void DrawLine(float x1, float y1, float x2, float y2, LColor c)
        {
            DrawLine(x1, y1, x2, y2, c.Color);
        }

        public void DrawLine(float x1, float y1, float x2, float y2, Color c)
        {
            if (x1 > x2)
            {
                x1++;
            }
            else
            {
                x2++;
            }
            if (y1 > y2)
            {
                y1++;
            }
            else
            {
                y2++;
            }
            InBegin(c);
            shape.DrawLine(innterBatch, new Vector2(x1, y1), new Vector2(x2, y2), WhitePixel, c);
            InEnd();
        }

        public void DrawLine(float x1, float y1, float x2, float y2)
        {
            DrawLine(x1, y1, x2, y2, color);
        }

        public void DrawRect(float x, float y, float w, float h, LColor c)
        {
            DrawRect(x, y, w, h, c.Color);
        }

        public void DrawRect(float x, float y, float w, float h, Color c)
        {
            InBegin(c);
            shape.DrawRect(innterBatch, new Vector2(x, y), new Vector2(w, h), WhitePixel, c);
            InEnd();
        }

        public void DrawRect(float x, float y, float w, float h)
        {
            DrawRect(x, y, w, h, color);
        }

        public void DrawOval(float x1, float y1, float width, float height, LColor c)
        {
            DrawOval(x1, y1, width, height, c.Color);
        }

        public void DrawOval(float x1, float y1, float width, float height)
        {
            shape.DrawOval(x1, y1, width, height, 0, 360, color);
        }

        public void DrawOval(float x1, float y1, float width, float height, Color c)
        {
            shape.DrawOval(x1, y1, width, height, 0, 360, c);
        }

        public void DrawOval(float x1, float y1, float width, float height,
            float start, float end, Color c)
        {
            shape.DrawOval(x1, y1, width, height, start, end, c);
        }

        public void FillRect(float x, float y, float w, float h, LColor c)
        {
            FillRect(x, y, w, h, c.Color);
        }

        public void FillRect(float x, float y, float w, float h, Color c)
        {
            URect(x, y, w, h);
            InBegin(c, true, false);
            innterBatch.Draw(WhitePixel, rectangle, c);
            InEnd();
        }

        public void FillRect(float x, float y, float w, float h)
        {
            FillRect(x, y, w, h, color);
        }

        public void DrawPoint(float x, float y)
        {
            DrawPoint(x, y, color);
        }

        public void DrawPoint(float x, float y, LColor c)
        {
            DrawPoint(x, y, c.Color);
        }

        public void DrawPoint(float x, float y, Color c)
        {
            UPos(x, y);
            InBegin(c, false, false);
            innterBatch.Draw(WhitePixel, postion, c);
            InEnd();
        }

        public void DrawPoints(float[] xs, float[] ys)
        {
            DrawPoints(xs, ys, color);
        }

        public void DrawPoints(float[] xs, float[] ys, LColor c)
        {
            DrawPoints(xs, ys, c.Color);
        }

        public void DrawPoints(float[] xs, float[] ys, Color c)
        {
            DrawPoints(xs, ys, xs.Length, c);
        }

        public void DrawPoints(float[] xs, float[] ys, int size)
        {
            DrawPoints(xs, ys, size, color);
        }

        public void DrawPoints(float[] xs, float[] ys, int size, Color c)
        {
            InBegin(c, false, false);
            for (int i = 0; i < size; i++)
            {
                UPos(xs[i], ys[i]);
                innterBatch.Draw(WhitePixel, postion, c);
            }
            InEnd();
        }

        public void CopyArea(int sx, int sy, int width, int height, int dx, int dy, int anchor)
        {
            if (width <= 0 || height <= 0)
            {
                return;
            }
            bool badAnchor = false;
            if ((anchor & 0x7f) != anchor || (anchor & BASELINE) != 0)
            {
                badAnchor = true;
            }
            if ((anchor & TOP) != 0)
            {
                if ((anchor & (VCENTER | BOTTOM)) != 0)
                {
                    badAnchor = true;
                }
            }
            else if ((anchor & BOTTOM) != 0)
            {
                if ((anchor & VCENTER) != 0)
                {
                    badAnchor = true;
                }
                else
                {
                    dy -= height - 1;
                }
            }
            else if ((anchor & VCENTER) != 0)
            {
                dy -= (int)((uint)(height - 1) >> 1);
            }
            else
            {
                badAnchor = true;
            }
            if ((anchor & LEFT) != 0)
            {
                if ((anchor & (HCENTER | RIGHT)) != 0)
                {
                    badAnchor = true;
                }
            }
            else if ((anchor & RIGHT) != 0)
            {
                if ((anchor & HCENTER) != 0)
                {
                    badAnchor = true;
                }
                else
                {
                    dx -= width;
                }
            }
            else if ((anchor & HCENTER) != 0)
            {
                dy -= (int)((uint)(height - 1) >> 1);
            }
            else
            {
                badAnchor = true;
            }
            if (badAnchor)
            {
                throw new System.ArgumentException("Bad Anchor !");
            }
            CopyArea(sx, sy, width, height, dx - sx, dy - sy);
        }

        public void CopyArea(int x, int y, int width, int height, int dx, int dy)
        {
            CopyArea(null, x, y, width, height, dx, dy);
        }

        public void CopyArea(LTexture texture, int x, int y, int width, int height,
                int dx, int dy)
        {
            if (isClose)
            {
                return;
            }
            if (x < 0)
            {
                width += x;
                x = 0;
            }
            if (y < 0)
            {
                height += y;
                y = 0;
            }
            if (texture != null)
            {
                if (x + width > texture.Width)
                {
                    width = texture.Height - x;
                }
                if (y + height > texture.Height)
                {
                    height = texture.Height - y;
                }
                LTexture tex2d = texture.GetSubTexture(x, y, width, height);
                DrawTexture(tex2d, x + dx, y + dy);
                tex2d = null;
            }
            else
            {
                if (x + width > GetWidth())
                {
                    width = GetWidth() - x;
                }
                if (y + height > GetHeight())
                {
                    height = GetHeight() - y;
                }
                LTexture tex2d = ScreenUtils.ToScreenCaptureTexture().GetSubTexture(x, y, width,
                        height);
                DrawTexture(tex2d, x + dx, y + dy);
                if (tex2d != null)
                {
                    tex2d.Destroy();
                    tex2d = null;
                }
            }
        }

        public SpriteBatch NewSpriteBatch()
        {
            return new SpriteBatch(device);
        }

        private SpriteBatch batchSprite;

        private bool initBatch, initAlphaBlend;

        private Color batchAlpha = new Color(1f, 1f, 1f, 1f);


        // ----- 批处理纹理渲染开始 ------//
        public void BeginBatch()
        {
            if (!initBatch)
            {
                batchSprite = new SpriteBatch(device);
                initBatch = true;
            }
            useGLBegin = true;
            if (color.A != 255)
            {
                batchSprite.Begin(SpriteSortMode.Deferred, BlendState.Additive, null, null, device.RasterizerState, null, cemera.viewMatrix);
            }
            else
            {
                if (initAlphaBlend)
                {
                    batchSprite.Begin(SpriteSortMode.Deferred, BlendState.AlphaBlend, null, null, device.RasterizerState, null, cemera.viewMatrix);
                }
                else
                {
                    batchSprite.Begin(SpriteSortMode.Deferred, BlendState.NonPremultiplied, null, null, device.RasterizerState, null, cemera.viewMatrix);
                }

            }
        }

        public void SetBatchColor(LColor c)
        {
            batchAlpha.PackedValue = c.PackedValue;
        }

        public LColor GetBatchColor()
        {
            return new LColor(batchAlpha);
        }

        public void SetBatchAlpha(float a)
        {
            batchAlpha.A = (byte)(255 * a);
        }

        public float GetBatchAlpha()
        {
            return batchAlpha.A / 255;
        }

        public void ResetBatchColor()
        {
            batchAlpha.PackedValue = 4294967295;
        }

        public void BeginBatch(BlendState blend)
        {
            useGLBegin = true;

            batchSprite.Begin(SpriteSortMode.Deferred, blend, null, null, device.RasterizerState, null, cemera.viewMatrix);
        }

        public void DrawBatch(LTexture texture, float x, float y)
        {
            DrawBatch(texture, x, y, batchAlpha);
        }

        public void DrawBatch(LTexture texture, float x, float y, LColor c)
        {
            DrawBatch(texture, x, y, texture.Width, texture.Height, 0, c.Color);
        }

        public void DrawBatch(LTexture texture, float x, float y, Color c)
        {
            DrawBatch(texture, x, y, texture.Width, texture.Height, 0, c);
        }

        public void DrawBatch(LTexture texture, float x, float y, float width,
                float height)
        {
            DrawBatch(texture, x, y, width, height, 0, 0, texture.Width, texture.Height, 0, batchAlpha);
        }

        public void DrawBatch(LTexture texture, float x, float y, float width,
                float height, float rotation)
        {
            DrawBatch(texture, x, y, width, height, 0, 0, texture.Width, texture.Height, rotation, batchAlpha);
        }

        public void DrawBatch(LTexture texture, float x, float y, float width,
                float height, float rotation, LColor c)
        {
            DrawBatch(texture, x, y, width, height, 0, 0, texture.Width, texture.Height, rotation, c.Color);
        }

        public void DrawBatch(LTexture texture, float x, float y, float width,
                float height, float rotation, Color c)
        {
            DrawBatch(texture, x, y, width, height, 0, 0, texture.Width, texture.Height, rotation, c);
        }

        public void DrawBatch(LTexture texture, float x, float y, float width,
                float height, float srcX, float srcY, float srcWidth,
                float srcHeight, float rotation, LColor c)
        {
            DrawBatch(texture, x, y, width, height, srcX, srcY, srcWidth, srcHeight, rotation, c.Color);
        }

        public void DrawBatch(LTexture texture, float x, float y, float width,
                float height, float srcX, float srcY, float srcWidth,
                float srcHeight, float rotation, Color c)
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
            initAlphaBlend = !texture.isExt;
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
                texSrc.X = (int)srcX;
                texSrc.Y = (int)srcY;
                texSrc.Width = (int)(srcWidth - srcX);
                texSrc.Height = (int)(srcHeight - srcY);
            }
            if (c == null)
            {
                c = Color.White;
            }
            if (rotation != 0)
            {
                float centerX = texSrc.Width / 2;
                float centerY = texSrc.Height / 2;
                texDst.X += (int)width / 2;
                texDst.Y += (int)height / 2;
                UPos(centerX, centerY);
                batchSprite.Draw(texture.Texture, texDst, texSrc, c, MathUtils.ToRadians(rotation), postion, SpriteEffects.None, 0);
            }
            else
            {
                batchSprite.Draw(texture.Texture, texDst, texSrc, c);
            }
        }

        public void EndBatch()
        {
            if (useGLBegin)
            {
                batchSprite.End();
            }
            useGLBegin = false;
            batchAlpha.PackedValue = 4294967295;
        }

        // ----- 批处理纹理渲染结束 ------//

        public bool UseGLBegin()
        {
            return useGLBegin;
        }

        public int GetWidth()
        {
            return xnaClip.width;
        }

        public int GetHeight()
        {
            return xnaClip.height;
        }

        public XNALine NewXNALine()
        {
            return new XNALine();
        }

        public XNALinearGradient NewXNALinearGradient()
        {
            return new XNALinearGradient(device);
        }

        public XNAPolyline NewXNAPolyline()
        {
            return new XNAPolyline(device);
        }

        public XNAPolygon NewXNAPolygon()
        {
            return new XNAPolygon(device);
        }

        public XNACircle NewXNACircle()
        {
            return new XNACircle(device);
        }

        public XNAEllipse NewXNAEllipse()
        {
            return new XNAEllipse(device);
        }

        public XNAPath NewPath()
        {
            return new XNAPath(device);
        }

        public XNARadialGradient NewXNARadialGradient()
        {
            return new XNARadialGradient(device);
        }

        public XNARectangle NewXNARectangle()
        {
            return new XNARectangle(device);
        }

        public void Dispose()
        {
            this.initBatch = false;
            this.isClose = true;
            this.isDirty = false;
            this.useFont = false;
            this.useGLBegin = false;
            if (innterBatch != null)
            {
                innterBatch.Dispose();
                innterBatch = null;
            }
            if (batchSprite != null)
            {
                batchSprite.Dispose();
                batchSprite = null;
            }
            if (shape != null)
            {
                shape.Dispose();
                shape = null;
            }
            if (font != null)
            {
                font.Dispose();
                font = null;
            }
            GLEx.device = null;
            GLEx.cemera = null;
            if (WhitePixel != null)
            {
                WhitePixel.Dispose();
                WhitePixel = null;
            }
        }
    }
}
