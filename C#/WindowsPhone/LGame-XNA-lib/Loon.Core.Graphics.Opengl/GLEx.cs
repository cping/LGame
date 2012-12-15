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
namespace Loon.Core.Graphics.Opengl
{
    using Microsoft.Xna.Framework.Graphics;
    using Microsoft.Xna.Framework;
    using Microsoft.Xna.Framework.Content;
    using Loon.Core.Geom;
    using Loon.Core.Graphics;
    using Loon.Utils;
    using Loon.Core.Graphics.Device;
    using Loon.Java;
    using System;

    public enum Direction
    {
        TRANS_NONE, TRANS_MIRROR, TRANS_FILP, TRANS_MF
    }

    public class GLEx : LTrans, LRelease
    {


        public static bool IsPowerOfTwo(int value)
        {
            return value != 0 && (value & value - 1) == 0;
        }

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


        private static GLEx self;

        public static GLEx Self
        {
            get
            {
                return self;
            }
        }

  

        private GLBatch glBatch;

        private LTextureBatch texBatch;

        private int currentBlendMode;

        private float lastAlpha = 1.0F, lineWidth = 1, sx = 1, sy = 1;

        private bool isClose, isTex2DEnabled, isARRAYEnable, isAntialias, isPushed;

        private bool preTex2dMode;

        private static bool vboOn, vboSupported;

        public static int lazyTextureID;

        bool onAlpha;

        private LColor color = new LColor(LColor.white);

        private LFont font = LFont.GetDefaultFont();

        private RectBox xnaClip;

        public static Texture2D WhitePixel { get; private set; }

        private static GL _gl;

        public static GL10 GL10
        {
            get
            {
                return null;
            }
        }

        public static GL11 GL11
        {
            get
            {
                return null;
            }
        }

        public static GL GL
        {
            get
            {
                return _gl;
            }
        }

        internal void Load(GL g)
        {
            GLEx._gl = g;
        }

        public void Update()
        {
            if (isClose)
            {
                return;
            }
            // 刷新原始设置
            GLUtils.Reset(_gl);
            // 清空背景为黑色
            GLUtils.SetClearColor(_gl, LColor.black);
            // 设定插值模式为FASTEST(最快,质量有损)
            GLUtils.SetHintFastest(_gl);
            // 着色模式设为FLAT
            GLUtils.SetShadeModelFlat(_gl);
            // 禁用光照效果
            GLUtils.DisableLightning(_gl);
            // 禁用色彩抖动
            GLUtils.DisableDither(_gl);
            // 禁用深度测试
            GLUtils.DisableDepthTest(_gl);
            // 禁用双面剪切
            GLUtils.DisableCulling(_gl);
            // 禁用顶点数据
            GLUtils.DisableVertexArray(_gl);
            // 禁用纹理坐标
            GLUtils.DisableTexCoordArray(_gl);
            // 禁用纹理色彩
            GLUtils.DisableTexColorArray(_gl);
            // 禁用纹理贴图
            GLUtils.DisableTextures(_gl);
            // 设定画布渲染模式为默认
            //this.SetBlendMode(GL.MODE_NORMAL);
            // 设为2D界面模式(转为2D屏幕坐标系)
            Set2DStateOn();
        }

        public void Set2DStateOn()
        {
            if (!preTex2dMode)
            {
                _gl.GLDisable(GL10.GL_DEPTH_TEST);
                _gl.GLMatrixMode(GL10.GL_MODELVIEW);
                _gl.GLLoadIdentity();
                preTex2dMode = true;
            }
        }

        public void Set2DStateOff()
        {
            if (preTex2dMode)
            {
                _gl.GLEnable(GL.GL_DEPTH_TEST);
                _gl.GLMatrixMode(GL.GL_MODELVIEW);
                _gl.GLLoadIdentity();
                preTex2dMode = false;
            }
        }

        private SpriteBatch xnaBatch;

        private System.Collections.Generic.Dictionary<Int32, XNARectangle> lazyXnaRect = new System.Collections.Generic.Dictionary<int, XNARectangle>(CollectionUtils.INITIAL_CAPACITY);

        private System.Collections.Generic.Dictionary<Int32, XNALine> lazyXnaLine = new System.Collections.Generic.Dictionary<int, XNALine>(CollectionUtils.INITIAL_CAPACITY);

        private System.Collections.Generic.Dictionary<Int32, XNAPolygon> lazyXnaPolygon = new System.Collections.Generic.Dictionary<int, XNAPolygon>(CollectionUtils.INITIAL_CAPACITY);

        private readonly RectBox _trueScreenMax = LSystem.screenRect; 

        public GLEx(GL gl)
        {
            GLEx._gl = gl;

            this.glBatch = new GLBatch(512);
       
            this.texBatch = new LTextureBatch(glBatch);
            this.texBatch.ClearBatch = false;

            this.xnaClip = new RectBox(_trueScreenMax);

            WhitePixel = new Texture2D(GL.device, 1, 1, true, SurfaceFormat.Color);
            Color[] pixels = { Color.White };
            WhitePixel.SetData(pixels);
            GLEx.self = this;
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
            if (isClose)
            {
                return;
            }

            while (end < start)
            {
                end += 360;
            }
            float cx = x1 + (width / 2.0f);
            float cy = y1 + (height / 2.0f);
            GLBegin(GL.GL_LINE_STRIP);
            int step = 360 / segments;
            for (float a = start; a < (end + step); a += step)
            {
                float ang = a;
                if (ang > end)
                {
                    ang = end;
                }
                float x = (cx + (MathUtils.Cos(MathUtils.ToRadians(ang)) * width / 2.0f));
                float y = (cy + (MathUtils.Sin(MathUtils.ToRadians(ang)) * height / 2.0f));
                GLVertex2f(x, y);
            }
            GLEnd();
        }

        public void FillArc(float x1, float y1, float width, float height,
                float start, float end)
        {
            FillArc(x1, y1, width, height, 40, start, end);
        }

        public void FillArc(float x1, float y1, float width, float height,
                int segments, float start, float end)
        {
            if (isClose)
            {
                return;
            }
            while (end < start)
            {
                end += 360;
            }
            float cx = x1 + (width / 2.0f);
            float cy = y1 + (height / 2.0f);

            GLBegin(GL.GL_TRIANGLES);
            int step = 360 / segments;

            GLVertex2f(cx, cy);

            for (float a = start; a < (end + step); a += step)
            {
                float ang = a;
                if (ang > end)
                {
                    ang = end;
                }

                float x = (cx + (MathUtils.Cos(MathUtils.ToRadians(ang)) * width / 2.0f));
                float y = (cy + (MathUtils.Sin(MathUtils.ToRadians(ang)) * height / 2.0f));

                GLVertex2f(x, y);
            }
            GLEnd();
            if (isAntialias)
            {
                GLBegin(GL.GL_TRIANGLE_FAN);
                GLVertex2f(cx, cy);
                if (end != 360)
                {
                    end -= 10;
                }
                for (float j = start; j < (end + step); j += step)
                {
                    float ang = j;
                    if (ang > end)
                    {
                        ang = end;
                    }

                    float x = (cx + (MathUtils.Cos(MathUtils.ToRadians(ang + 10))
                            * width / 2.0f));
                    float y = (cy + (MathUtils.Sin(MathUtils.ToRadians(ang + 10))
                            * height / 2.0f));

                    GLVertex2f(x, y);
                }
                GLEnd();
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
            if (isClose)
            {
                return;
            }
            if (radius < 0)
            {
                throw new Exception("radius > 0");
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
            if (isClose)
            {
                return;
            }
            if (radius < 0)
            {
                throw new Exception("radius > 0");
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

        public void DrawLine(float x1, float y1, float x2, float y2)
        {
            if (isClose)
            {
                return;
            }
            if (lineWidth <= 1)
            {
                _DrawLine(x1, y1, x2, y2, true);
            }
            else
            {
                int hashCode = 1;
                hashCode = LSystem.Unite(hashCode, x1);
                hashCode = LSystem.Unite(hashCode, y1);
                hashCode = LSystem.Unite(hashCode, x2);
                hashCode = LSystem.Unite(hashCode, y2);
                hashCode = LSystem.Unite(hashCode, lineWidth);
                XNALine xnaLine = (XNALine)CollectionUtils.Get(lazyXnaLine, hashCode);
                if (xnaLine == null)
                {
                    xnaLine = new XNALine();
                    xnaLine.Start = new Vector2(x1, y1);
                    xnaLine.End = new Vector2(x2, y2);
                    xnaLine.StrokeWidth = lineWidth;
                    CollectionUtils.Put(lazyXnaLine, hashCode, xnaLine);
                }
                xnaLine.Stroke = color;
                XnaBatchBegin(color);
                xnaLine.Draw(xnaBatch);
                XnaBatchEnd();
            }
        }

        private void _DrawLine(float x1, float y1, float x2, float y2, bool use)
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
            if (use)
            {
                GLBegin(GL.GL_LINES);
            }
            GLVertex2f(x1, y1);
            GLVertex2f(x2, y2);
            if (use)
            {
                GLEnd();
            }
        }

        private float[] temp_xs = new float[4];

        private float[] temp_ys = new float[4];

        public void DrawRect(float x1, float y1, float x2,
         float y2)
        {
            if (lineWidth <= 1)
            {
                SetRect(x1, y1, x2, y2, false);
            }
            else
            {
                int hashCode = 1;
                hashCode = LSystem.Unite(hashCode, x1);
                hashCode = LSystem.Unite(hashCode, y1);
                hashCode = LSystem.Unite(hashCode, x2);
                hashCode = LSystem.Unite(hashCode, y2);
                hashCode = LSystem.Unite(hashCode, lineWidth);
                XNARectangle xnaRect = (XNARectangle)CollectionUtils.Get(lazyXnaRect, hashCode);
                if (xnaRect == null)
                {
                    xnaRect = new XNARectangle(GL.device);
                    xnaRect.Position = new Vector2(x1, y1);
                    xnaRect.Width = x2;
                    xnaRect.Height = y2;
                    xnaRect.StrokeWidth = lineWidth;
                    CollectionUtils.Put(lazyXnaRect, hashCode, xnaRect);
                }
                xnaRect.Stroke = color;
                XnaBatchBegin(color);
                xnaRect.Draw(xnaBatch);
                XnaBatchEnd();
            }
        }

        public void DrawSpriteFont(XNAFont font, string mes, float x, float y)
        {
            SpriteBatch batch = GLEx.Self.XNASpriteBatch();
            batch.Begin(SpriteSortMode.Immediate, BlendState.AlphaBlend, null, null, GL.device.RasterizerState, null, _gl.GLView);
            batch.DrawString(font.SpriteFont, mes, new Vector2(x, y), color);
            batch.End();
        }

        public void DrawSpriteFont(XNAFont font, string mes, float x, float y, float scaleX, float scaleY)
        {
            SpriteBatch batch = GLEx.Self.XNASpriteBatch();
            batch.Begin(SpriteSortMode.Immediate, BlendState.AlphaBlend, null, null, GL.device.RasterizerState, null, _gl.GLView);
            batch.DrawString(font.SpriteFont, mes, new Vector2(x, y), color, 0f, Vector2.Zero, new Vector2(scaleX, scaleY), SpriteEffects.None, 0);
            batch.End();
        }

        public void DrawSpriteFont(XNAFont font, string mes, float x, float y,float rotation)
        {
            SpriteBatch batch = GLEx.Self.XNASpriteBatch();
            batch.Begin(SpriteSortMode.Immediate, BlendState.AlphaBlend, null, null, GL.device.RasterizerState, null, _gl.GLView);
            batch.DrawString(font.SpriteFont, mes, new Vector2(x, y), color, rotation, Vector2.Zero, 1f, SpriteEffects.None, 0);
            batch.End();
        }

        private void XnaBatchEnd()
        {
            if (xnaBatch != null)
            {
                xnaBatch.End();
            }
        }

        public SpriteBatch XNASpriteBatch()
        {
            if (xnaBatch == null)
            {
                xnaBatch = new SpriteBatch(GL.device);
            }
            return xnaBatch;
        }

        private void XnaBetchNonBegin()
        {
            XNASpriteBatch().Begin(SpriteSortMode.Immediate, BlendState.NonPremultiplied, null, null, GL.device.RasterizerState, null, _gl.GLView);
        }

        private void XnaBatchBegin(LColor c)
        {
            XNASpriteBatch();
            if (onAlpha && c.A != 255)
            {
                xnaBatch.Begin(SpriteSortMode.Immediate, BlendState.Additive, null, null, GL.device.RasterizerState, null, _gl.GLView);
            }
            else
            {
                xnaBatch.Begin(SpriteSortMode.Immediate, BlendState.AlphaBlend, null, null, GL.device.RasterizerState, null, _gl.GLView);
            }
        }

        public void DrawTexture2D(Texture2D tex2d, float x, float y)
        {
            XnaBatchBegin(color);
            xnaBatch.Draw(tex2d, new Vector2(x, y), color);
            XnaBatchEnd();
        }

        public void DrawTexture2D(Texture2D tex2d, float x, float y, float w, float h)
        {
            XnaBatchBegin(color);
            xnaBatch.Draw(tex2d, new Rectangle((int)x, (int)y, (int)w, (int)h), color);
            XnaBatchEnd();
        }

        private Vector2 pos_0 = new Vector2();

        private Vector2 pos_1 = new Vector2();

        public void FillRect(float x1, float y1, float x2,
                 float y2)
        {
            pos_0.X = x1;
            pos_0.Y = y1;
            pos_1.X = x2;
            pos_1.Y = y2;
            XnaBetchNonBegin();
            xnaBatch.Draw(WhitePixel, pos_0, null, this.color, 0f, Vector2.Zero, pos_1, SpriteEffects.None, 0f);
            XnaBatchEnd();
            //缩放像素效率较高 
            //SetRect(x1, y1, x2, y2, true);
        }

        public void Fill(Shape shape)
        {
            if (isClose)
            {
                return;
            }
            if (shape == null)
            {
                return;
            }
            Triangle tris = shape.GetTriangles();
            if (tris.GetTriangleCount() == 0)
            {
                return;
            }
            GLBegin(GL.GL_TRIANGLES);
            for (int i = 0; i < tris.GetTriangleCount(); i++)
            {
                for (int p = 0; p < 3; p++)
                {
                    float[] pt = tris.GetTrianglePoint(i, p);
                    GLVertex2f(pt[0], pt[1]);
                }
            }
            GLEnd();
        }

        public void DrawPoint(float x, float y)
        {
            if (isClose)
            {
                return;
            }
            XnaBatchBegin(color);
            xnaBatch.Draw(WhitePixel, new Vector2(x, y), color);
            XnaBatchEnd();
        }

        public void DrawPoints(float[] x, float[] y, int size)
        {
            if (isClose)
            {
                return;
            }
            XnaBatchBegin(color);
            for (int i = 0; i < size; i++)
            {
                xnaBatch.Draw(WhitePixel, new Vector2(x[i], y[i]), color);
            }
            XnaBatchEnd();
        }

        public void Draw(Shape shape)
        {
            if (isClose)
            {
                return;
            }
            if (lineWidth <= 1)
            {
                float[] points = shape.GetPoints();
                if (points.Length == 0)
                {
                    return;
                }
                GLBegin(GL.GL_LINE_STRIP);
                for (int i = 0; i < points.Length; i += 2)
                {
                    GLVertex2f(points[i], points[i + 1]);
                }
                if (shape.Closed())
                {
                    GLVertex2f(points[0], points[1]);
                }
                GLEnd();
            }
            else
            {
                float[] points = shape.GetPoints();
                if (points == null)
                {
                    return;
                }
                if (points.Length == 0)
                {
                    return;
                }
                if (shape is Path)
                {
                    float x = points[0];
                    float y = points[1];

                    for (int i = 0; i < points.Length; i += 2)
                    {
                        DrawLine(x, y, points[i], points[i + 1]);
                        x = points[i];
                        y = points[i + 1];
                    }
                }
                else
                {
                    int hashCode = 1;
                    for (int i = 0; i < points.Length; i++)
                    {
                        hashCode = LSystem.Unite(hashCode, points[i]);
                    }
                    XNAPolygon xnaPolygon = (XNAPolygon)CollectionUtils.Get(lazyXnaPolygon, hashCode);
                    if (xnaPolygon == null)
                    {
                        xnaPolygon = new XNAPolygon(GL.device);
                        xnaPolygon.StrokeWidth = lineWidth;
                        for (int i = 0; i < points.Length; i += 2)
                        {
                            xnaPolygon.AddPoint(new Vector2(points[i], points[i + 1]));
                        }
                        if (shape.Closed())
                        {
                            xnaPolygon.AddPoint(new Vector2(points[0], points[1]));
                        }
                        CollectionUtils.Put(lazyXnaPolygon, hashCode, xnaPolygon);
                    }
                    xnaPolygon.Stroke = color;
                    XnaBatchBegin(color);
                    xnaPolygon.Draw(xnaBatch);
                    XnaBatchEnd();
                }
            }
        }

        public void SetRect(float x, float y, float width, float height,
                bool Fill)
        {
            if (isClose)
            {
                return;
            }
            temp_xs[0] = x;
            temp_xs[1] = x + width;
            temp_xs[2] = x + width;
            temp_xs[3] = x;

            temp_ys[0] = y;
            temp_ys[1] = y;
            temp_ys[2] = y + height;
            temp_ys[3] = y + height;

            if (Fill)
            {
                FillPolygon(temp_xs, temp_ys, 4);
            }
            else
            {
                DrawPolygon(temp_xs, temp_ys, 4);
            }
        }

        public void DrawPolygon(float[] xPoints, float[] yPoints, int nPoints)
        {
            if (isClose)
            {
                return;
            }
            _DrawPolygon(xPoints, yPoints, nPoints, true);
        }

        private void _DrawPolygon(float[] xPoints, float[] yPoints, int nPoints,
                bool use)
        {
            if (use)
            {
                GLBegin(GL.GL_LINE_LOOP);
            }
            for (int i = 0; i < nPoints; i++)
            {
                GLVertex2f(xPoints[i], yPoints[i]);
            }
            if (use)
            {
                GLEnd();
            }
        }

        public void FillPolygon(float[] xPoints, float[] yPoints, int nPoints)
        {
            if (isClose)
            {
                return;
            }
            _FillPolygon(xPoints, yPoints, nPoints, true);
        }

        private void _FillPolygon(float[] xPoints, float[] yPoints,
                int nPoints, bool use)
        {
            if (use)
            {
                GLBegin(GL.GL_TRIANGLES);
            }
            {
                for (int i = 0; i < nPoints; i++)
                {
                    GLVertex2f(xPoints[i], yPoints[i]);
                }
            }
            if (use)
            {
                GLEnd();
            }
        }

        private bool useBegin;

        public void GLBegin(int mode)
        {
            if (isClose)
            {
                return;
            }

            glBatch.Begin(mode);
            this.useBegin = true;
        }

        public void PutPixel4ES(float x, float y, float r, float g, float b, float a)
        {
            if (isClose || !useBegin)
            {
                return;
            }
            if (a <= 0 || (r == 0 && g == 0 && b == 0 && a == 0))
            {
                return;
            }
            if ((x < 0 || y < 0) || (x > LSystem.screenRect.width || y > LSystem.screenRect.height))
            {
                return;
            }
            this.GLVertex2f(x, y);
            this.GLColor(r, g, b, a);
        }

        public void PutPixel4ES(float x, float y, LColor c)
        {
            PutPixel4ES(x, y, c.r, c.g, c.b, c.a);
        }

        public void PutPixel3ES(float x, float y, float r, float g, float b)
        {
            PutPixel4ES(x, y, r, g, b, 1);
        }

        public void GLTexCoord2f(float fcol, float frow)
        {
            if (isClose || !useBegin)
            {
                return;
            }
            glBatch.TexCoord(fcol, frow);
        }

        public void GLVertex2f(float x, float y)
        {
            if (isClose || !useBegin)
            {
                return;
            }
            GLVertex3f(x, y, 0);
        }

        public void GLVertex3f(float x, float y, float z)
        {
            if (isClose || !useBegin)
            {
                return;
            }
            glBatch.Vertex(x, y, z);
        }

        public void GLColor(float r, float g, float b, float a)
        {
            if (isClose || !useBegin)
            {
                return;
            }
            glBatch.Color(r, g, b, a);
        }

        public void GLColor(LColor c)
        {
            if (isClose)
            {
                return;
            }
            glBatch.Color(c);
        }

        public void GLColor(float r, float g, float b)
        {
            GLColor(r, g, b, 1f);
        }

        public void GLLine(float x1, float y1, float x2, float y2)
        {
            _DrawLine(x1, y1, x2, y2, false);
        }

        public void GLDrawRect(float x, float y, float width, float height)
        {
            GLRect(x, y, width, height, false);
        }

        public void GLFillRect(float x, float y, float width, float height)
        {
            GLRect(x, y, width, height, true);
        }

        private void GLRect(float x, float y, float width, float height,
                bool Fill)
        {
            float[] xs = new float[4];
            float[] ys = new float[4];
            xs[0] = x;
            xs[1] = x + width;
            xs[2] = x + width;
            xs[3] = x;
            ys[0] = y;
            ys[1] = y;
            ys[2] = y + height;
            ys[3] = y + height;
            if (Fill)
            {
                GLFillPoly(xs, ys, 4);
            }
            else
            {
                GLDrawPoly(xs, ys, 4);
            }
        }

        public void GLDrawPoly(float[] xPoints, float[] yPoints, int nPoints)
        {
            _DrawPolygon(xPoints, yPoints, nPoints, false);
        }

        public void GLFillPoly(float[] xPoints, float[] yPoints, int nPoints)
        {
            _FillPolygon(xPoints, yPoints, nPoints, false);
        }

        public void GLTex2DDisable()
        {
            if (isClose)
            {
                return;
            }
            if (isTex2DEnabled)
            {
                _gl.GLDisable(GL.GL_TEXTURE_2D);
                isTex2DEnabled = false;
            }
        }

        public void GLTex2DEnable()
        {
            if (isClose)
            {
                return;
            }
            if (!isTex2DEnabled)
            {
                _gl.GLEnable(GL.GL_TEXTURE_2D);
                isTex2DEnabled = true;
            }
        }

        public void GLTex2DARRAYEnable()
        {
            if (isClose)
            {
                return;
            }
            if (!isARRAYEnable)
            {
                _gl.GLEnableClientState(GL10.GL_VERTEX_ARRAY);
                _gl.GLEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
                isARRAYEnable = true;
            }
        }

        public void GLTex2DARRAYDisable()
        {
            if (isClose)
            {
                return;
            }
            if (isARRAYEnable)
            {
                _gl.GLDisableClientState(GL.GL_VERTEX_ARRAY);
                _gl.GLDisableClientState(GL.GL_TEXTURE_COORD_ARRAY);
                isARRAYEnable = false;
            }
        }

        public void SetBlendMode(int mode)
        {
            if (isClose)
            {
                return;
            }
            if (currentBlendMode == mode)
            {
                return;
            }
            this.currentBlendMode = mode;
            if (currentBlendMode == GL10.MODE_NORMAL)
            {
                GLUtils.EnableBlend(_gl);
                _gl.GLColorMask(true, true, true, true);
                _gl.GLBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
                return;
            }
            else if (currentBlendMode == GL10.MODE_ALPHA_MAP)
            {
                GLUtils.DisableBlend(_gl);
                _gl.GLColorMask(false, false, false, true);
                return;
            }
            else if (currentBlendMode == GL10.MODE_ALPHA_BLEND)
            {
                GLUtils.EnableBlend(_gl);
                _gl.GLColorMask(true, true, true, false);
                _gl.GLBlendFunc(GL10.GL_DST_ALPHA, GL10.GL_ONE_MINUS_DST_ALPHA);
                return;
            }
            else if (currentBlendMode == GL10.MODE_COLOR_MULTIPLY)
            {
                GLUtils.EnableBlend(_gl);
                _gl.GLColorMask(true, true, true, true);
                _gl.GLBlendFunc(GL10.GL_ONE_MINUS_SRC_COLOR, GL10.GL_SRC_COLOR);
                return;
            }
            else if (currentBlendMode == GL10.MODE_ADD)
            {
                GLUtils.EnableBlend(_gl);
                _gl.GLColorMask(true, true, true, true);
                _gl.GLBlendFunc(GL10.GL_ONE, GL10.GL_ONE);
                return;
            }
            else if (currentBlendMode == GL10.MODE_SPEED)
            {
                GLUtils.EnableBlend(_gl);
                _gl.GLColorMask(true, true, true, false);
                _gl.GLBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
                return;
            }
            else if (currentBlendMode == GL.MODE_SCREEN)
            {
                GLUtils.EnableBlend(_gl);
                _gl.GLColorMask(true, true, true, true);
                _gl.GLBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_COLOR);
                return;
            }
            else if (currentBlendMode == GL10.MODE_ALPHA_ONE)
            {
                GLUtils.EnableBlend(_gl);
                _gl.GLColorMask(true, true, true, true);
                _gl.GLBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE);
                return;
            }
            else if (currentBlendMode == GL10.MODE_ALPHA)
            {
                GLUtils.EnableBlend(_gl);
                _gl.GLColorMask(true, true, true, false);
                _gl.GLBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
                return;
            }
            else if (currentBlendMode == GL10.MODE_NONE)
            {
                GLUtils.DisableBlend(_gl);
                _gl.GLColorMask(true, true, true, false);
                return;
            }
        }

        public static bool CheckVBO()
        {
            return false;
        }

        public static bool IsVbo()
        {
            return false;
        }

        public static void SetVbo(bool vbo)
        {
            GLEx.vboOn = vbo;
        }

        public static bool IsVboSupported()
        {
            return false;
        }

        public static void SetVBOSupported(bool vboSupported)
        {
            GLEx.vboSupported = vboSupported;
        }

        public void GLPushMatrix()
        {
            if (isClose)
            {
                return;
            }
            _gl.GLPushMatrix();
        }

        public void GLPopMatrix()
        {
            if (isClose)
            {
                return;
            }
            _gl.GLPopMatrix();
        }

        public void Bind(int id)
        {
            if (lazyTextureID != id)
            {
                _gl.GLBindTexture(GL10.GL_TEXTURE_2D, id);
                lazyTextureID = id;
            }
        }

        public void DrawClear()
        {
            if (isClose)
            {
                return;
            }
            DrawClear(LColor.black);
        }

        public void DrawClear(LColor color)
        {
            if (isClose)
            {
                return;
            }
            GL.device.Clear(color);
        }

        public void SaveMatrices()
        {
            if (isClose)
            {
                return;
            }
            _gl.GLMatrixMode(GL10.GL_PROJECTION);
            _gl.GLPushMatrix();
            _gl.GLMatrixMode(GL10.GL_MODELVIEW);
            _gl.GLPushMatrix();
        }

        public void RestoreMatrices()
        {
            if (isClose)
            {
                return;
            }
            _gl.GLMatrixMode(GL10.GL_PROJECTION);
            _gl.GLPopMatrix();
            _gl.GLMatrixMode(GL10.GL_MODELVIEW);
            _gl.GLPopMatrix();
        }

        public void SavePrj()
        {
            if (isClose)
            {
                return;
            }
            _gl.GLMatrixMode(GL.GL_PROJECTION);
            _gl.GLPushMatrix();
        }

        public void RestorePrj()
        {
            if (isClose)
            {
                return;
            }
            _gl.GLMatrixMode(GL.GL_PROJECTION);
            _gl.GLPopMatrix();
        }

        public void SetMatrixMode(Loon.Core.Geom.Matrix mx)
        {
            if (isClose)
            {
                return;
            }
            _gl.GLMatrixMode(GL10.GL_MODELVIEW);
            _gl.GLLoadMatrixf(mx.Get(), 0);
        }

        public void Scale(float x, float y)
        {
            if (x != sx || y != sy)
            {
                this.sx = sx * x;
                this.sy = sy * y;
                _gl.GLScale(x, y, 0);
            }
        }

        public float GetScaleX()
        {
            return sx;
        }

        public float GetScaleY()
        {
            return sy;
        }

        public void GLEnd()
        {
            if (isClose || !useBegin)
            {
                useBegin = false;
                return;
            }
            glBatch.End(color);
            useBegin = false;
        }

        public void SetRotation(float r)
        {
            _gl.GLRotate(r);
        }

        public float GetRotation()
        {
            return _gl.GetRotation();
        }

        public void SetAlpha(byte alpha)
        {
            color.A = alpha;
        }

        public void SetAlphaValue(int alpha)
        {
            if (isClose)
            {
                return;
            }
            SetAlpha((float)alpha / 255);
        }

        public void Test()
        {
            lastAlpha = 1;
        }

        public bool IsAlpha()
        {
            return onAlpha;
        }

        public void GL_REPLACE()
        {

        }

        public void GL_MODULATE()
        {

        }

        public void SetAlpha(float alpha)
        {
            if (color.a == alpha)
            {
                return;
            }
            if (alpha > 0.95f)
            {
                GL_REPLACE();
                color.a = 1f;
                onAlpha = false;
            }
            else
            {
                GL_MODULATE();
                color.a = alpha;
                onAlpha = true;
            }
            lastAlpha = color.a;
        }

        public float GetAlpha()
        {
            return color.a;
        }

        public void SetColorValue(int r, int g, int b, int a)
        {
            float red = (float)r / 255.0f;
            float green = (float)g / 255.0f;
            float blue = (float)b / 255.0f;
            float alpha = (float)a / 255.0f;
            SetColor(red, green, blue, alpha);
        }

        public void ResetColor()
        {
            if (isClose)
            {
                return;
            }
            lastAlpha = 1f;
            onAlpha = false;
            color.R = 255;
            color.G = 255;
            color.B = 255;
            color.A = 255;
        }

        public void SetColorRGB(LColor c)
        {
            if (isClose)
            {
                return;
            }
            GL_MODULATE();
            color.SetFloatColor(c.r, c.g, c.b, lastAlpha);
        }

        public void SetColorARGB(LColor c)
        {
            if (isClose)
            {
                return;
            }
            GL_MODULATE();
            color.SetColor((byte)c.R, (byte)c.G, (byte)c.B, (byte)(c.A != 0 ? c.A * lastAlpha : 255));
        }

        public void SetColor(int pixel)
        {
            int[] rgbs = LColor.GetRGBs(pixel);
            SetColorValue(rgbs[0], rgbs[1], rgbs[2], (int)(lastAlpha * 255));
        }

        public void SetColor(LColor c)
        {
            SetColorARGB(c);
        }

        public void SetColor(float r, float g, float b,
                 float a)
        {
            if (isClose)
            {
                return;
            }
            GL_MODULATE();
            color.SetFloatColor(r, g, b, a);
        }

        public void SetColor(int r, int g, int b,
                 int a)
        {
            if (isClose)
            {
                return;
            }
            float red = r / 255f;
            float green = g / 255f;
            float blue = b / 255f;
            float alpha = a / 255f;
            GL_MODULATE();
            color.SetFloatColor(red, green, blue, alpha);
        }

        public void SetColor(float r, float g, float b)
        {
            SetColor(r, g, b, lastAlpha);
        }

        public void SetColor(int r, int g, int b)
        {
            SetColor(r, g, b, (int)(lastAlpha * 255));
        }

        public LColor GetColor()
        {
            return new LColor(color);
        }

        public uint GetColorRGB()
        {
            return LColor.GetRGB(color.R, color.G, color.B);
        }

        public uint GetColorARGB()
        {
            return LColor.GetARGB(color.R, color.G, color.B, color.A);
        }

        public void SetAntiAlias(bool flag)
        {
            if (isClose)
            {
                return;
            }
            if (flag)
            {
                _gl.GLEnable(GL10.GL_LINE_SMOOTH);
            }
            else
            {
                _gl.GLDisable(GL10.GL_LINE_SMOOTH);
            }
            this.isAntialias = flag;
        }

        public bool IsAntialias()
        {
            return isAntialias;
        }

        public void DrawSixStart(LColor color, float x, float y, float r)
        {
            if (isClose)
            {
                return;
            }
            SetColor(color);
            DrawTriangle(color, x, y, r);
            DrawRTriangle(color, x, y, r);
            ResetColor();
        }

        /**
         * 绘制正三角
         * 
         * @param color
         * @param x
         * @param y
         * @param r
         */
        public void DrawTriangle(LColor color, float x, float y, float r)
        {
            if (isClose)
            {
                return;
            }
            float x1 = x;
            float y1 = y - r;
            float x2 = x - (r * MathUtils.Cos(MathUtils.PI / 6));
            float y2 = y + (r * MathUtils.Sin(MathUtils.PI / 6));
            float x3 = x + (r * MathUtils.Cos(MathUtils.PI / 6));
            float y3 = y + (r * MathUtils.Sin(MathUtils.PI / 6));
            float[] xpos = new float[3];
            xpos[0] = x1;
            xpos[1] = x2;
            xpos[2] = x3;
            float[] ypos = new float[3];
            ypos[0] = y1;
            ypos[1] = y2;
            ypos[2] = y3;
            SetColor(color);
            FillPolygon(xpos, ypos, 3);
            ResetColor();
        }

        /**
         * 绘制倒三角
         * 
         * @param color
         * @param x
         * @param y
         * @param r
         */
        public void DrawRTriangle(LColor color, float x, float y, float r)
        {
            if (isClose)
            {
                return;
            }
            float x1 = x;
            float y1 = y + r;
            float x2 = x - (r * MathUtils.Cos(MathUtils.PI / 6.0f));
            float y2 = y - (r * MathUtils.Sin(MathUtils.PI / 6.0f));
            float x3 = x + (r * MathUtils.Cos(MathUtils.PI / 6.0f));
            float y3 = y - (r * MathUtils.Sin(MathUtils.PI / 6.0f));
            float[] xpos = new float[3];
            xpos[0] = x1;
            xpos[1] = x2;
            xpos[2] = x3;
            float[] ypos = new float[3];
            ypos[0] = y1;
            ypos[1] = y2;
            ypos[2] = y3;
            SetColor(color);
            FillPolygon(xpos, ypos, 3);
            ResetColor();
        }

        /**
         * 绘制三角形
         * 
         * @param x1
         * @param y1
         * @param x2
         * @param y2
         * @param x3
         * @param y3
         */
        public void DrawTriangle(float x1, float y1, float x2,
                 float y2, float x3, float y3)
        {
            if (isClose)
            {
                return;
            }
            GLBegin(GL.GL_LINE_LOOP);
            GLVertex2f(x1, y1);
            GLVertex2f(x2, y2);
            GLVertex2f(x3, y3);
            GLEnd();
        }

        /**
         * 填充三角形
         * 
         * @param x1
         * @param y1
         * @param x2
         * @param y2
         * @param x3
         * @param y3
         */
        public void FillTriangle(float x1, float y1, float x2,
                 float y2, float x3, float y3)
        {
            if (isClose)
            {
                return;
            }

            GLBegin(GL.GL_TRIANGLES);
            GLVertex2f(x1, y1);
            GLVertex2f(x2, y2);
            GLVertex2f(x3, y3);
            GLEnd();
        }

        /**
         * 绘制并填充一组三角
         * 
         * @param ts
         */
        public void FillTriangle(Triangle2f[] ts)
        {
            FillTriangle(ts, 0, 0);
        }

        /**
         * 绘制并填充一组三角
         * 
         * @param ts
         * @param x
         * @param y
         */
        public void FillTriangle(Triangle2f[] ts, int x, int y)
        {
            if (isClose)
            {
                return;
            }
            if (ts == null)
            {
                return;
            }
            int size = ts.Length;
            for (int i = 0; i < size; i++)
            {
                FillTriangle(ts[i], x, y);
            }
        }

        /**
         * 绘制并填充一组三角
         * 
         * @param t
         */
        public void FillTriangle(Triangle2f t)
        {
            FillTriangle(t, 0, 0);
        }

        /**
         * 绘制并填充一组三角
         * 
         * @param t
         * @param x
         * @param y
         */
        public void FillTriangle(Triangle2f t, float x, float y)
        {
            if (isClose)
            {
                return;
            }
            if (t == null)
            {
                return;
            }
            float[] xpos = new float[3];
            float[] ypos = new float[3];
            xpos[0] = x + t.xpoints[0];
            xpos[1] = x + t.xpoints[1];
            xpos[2] = x + t.xpoints[2];
            ypos[0] = y + t.ypoints[0];
            ypos[1] = y + t.ypoints[1];
            ypos[2] = y + t.ypoints[2];
            FillPolygon(xpos, ypos, 3);
        }

        /**
         * 绘制一组三角
         * 
         * @param ts
         */
        public void DrawTriangle(Triangle2f[] ts)
        {
            DrawTriangle(ts, 0, 0);
        }

        /**
         * 绘制一组三角
         * 
         * @param ts
         * @param x
         * @param y
         */
        public void DrawTriangle(Triangle2f[] ts, int x, int y)
        {
            if (isClose)
            {
                return;
            }
            if (ts == null)
            {
                return;
            }
            int size = ts.Length;
            for (int i = 0; i < size; i++)
            {
                DrawTriangle(ts[i], x, y);
            }
        }

        /**
         * 绘制三角
         * 
         * @param t
         */
        public void DrawTriangle(Triangle2f t)
        {
            DrawTriangle(t, 0, 0);
        }

        /**
         * 绘制三角
         * 
         * @param t
         * @param x
         * @param y
         */
        public void DrawTriangle(Triangle2f t, int x, int y)
        {
            if (isClose)
            {
                return;
            }
            if (t == null)
            {
                return;
            }
            float[] xpos = new float[3];
            float[] ypos = new float[3];
            xpos[0] = x + t.xpoints[0];
            xpos[1] = x + t.xpoints[1];
            xpos[2] = x + t.xpoints[2];
            ypos[0] = y + t.ypoints[0];
            ypos[1] = y + t.ypoints[1];
            ypos[2] = y + t.ypoints[2];
            DrawPolygon(xpos, ypos, 3);
        }

        /**
         * Java method: 
        public void Reset(bool clear)
        {
            if (isClose)
            {
                return;
            }
            Bind(0);
            if (isTex2DEnabled)
            {
                _gl.glDisable(GL.GL_TEXTURE_2D);
                isTex2DEnabled = false;
            }
            if (clear)
            {
                _gl.glClearColor(0, 0, 0, 1f);
                _gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
            }
        }
        **/

        public void Reset(bool c)
        {
            color.PackedValue = 4294967295;
            if (c)
            {
                GL.device.Clear(Color.Black);
            }
        }

        public void Clear()
        {
            GL.device.Clear(Color.Black);
        }

        public void SetLineWidth(int l)
        {
            this.lineWidth = l;
        }

        public float GetLineWidth()
        {
            return lineWidth;
        }

        public void ResetLineWidth()
        {
            lineWidth = 1;
        }

        public LFont GetFont()
        {
            return font;
        }

        public void SetFont(LFont f)
        {
            this.font = f;
        }

        /// <summary>
        /// 矫正显示坐标到指定位置
        /// </summary>
        /// <param name="x"></param>
        /// <param name="y"></param>
        public void Translate(int x, int y)
        {
            _gl.GLTranslate(x, y, 0);
            xnaClip.x -= x;
            xnaClip.width -= x;
            xnaClip.y -= y;
            xnaClip.height -= y;
        }

        public void Translate(float x, float y)
        {
            _gl.GLTranslate(x, y, 0);
            xnaClip.x -= x;
            xnaClip.width -= (int)x;
            xnaClip.y -= y;
            xnaClip.height -= (int)y;
        }

        internal static void DeleteBuffer(int bufferID)
        {
            if (!vboOn)
            {
                return;
            }

        }

        internal static void DeleteTexture(int textureID)
        {

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
            SetClip(x, y, width, height);
        }

        private Rectangle _clipRect = new Rectangle();

        private void ConvertClip(RectBox clip)
        {
            _clipRect.X = (int)(clip.x * LSystem.scaleWidth);
            _clipRect.Y = (int)(clip.y * LSystem.scaleHeight);
            _clipRect.Width = (int)(clip.width * LSystem.scaleWidth);
            _clipRect.Height = (int)(clip.height * LSystem.scaleHeight);
            GL.device.ScissorRectangle = _clipRect;
        }

        public void SetClip(int x, int y, int width, int height)
        {
            if (isClose)
            {
                return;
            }
            if (!isClipRect)
            {
                Vector2 pos = _gl.GLCamera.Position;
                xnaClip.SetBounds(MathUtils.Max(x + pos.X, 0), MathUtils.Max(y
                           + pos.Y, 0), (MathUtils.Min(LSystem.screenRect.width, width) - pos.X),
                           (MathUtils.Min(LSystem.screenRect.height, height) - pos.X));
                if (_trueScreenMax.Contains(xnaClip))
                {
                    GL.device.RasterizerState = _gl.rstateScissor;
                    ConvertClip(xnaClip);
                    isClipRect = true;
                }
            }
        }
    
        public void ClearClip()
        {
            if (isClipRect)
            {
                xnaClip.SetBounds(_trueScreenMax);
                ConvertClip(_trueScreenMax);
                GL.device.RasterizerState = RasterizerState.CullNone;
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

        public void DrawString(string str, Vector2f position)
        {
            DrawString(str, position.x, position.y, color);
        }

        public void DrawString(string str, Vector2f position,
                LColor color)
        {
            DrawString(str, position.x, position.y, color);
        }

        public void DrawString(string str, float x, float y)
        {
            DrawString(str, x, y, color);
        }

        public void DrawString(string str, float x, float y,
                LColor color)
        {
            if (isClose)
            {
                return;
            }
            DrawString(str, x, y, 0, color);
        }

        public void DrawString(string str, float x, float y,
                float rotation)
        {
            if (isClose)
            {
                return;
            }
            DrawString(str, x, y, rotation, color);
        }

        public void DrawString(string str, float x, float y,
                float rotation, LColor c)
        {
            if (isClose || c == null)
            {
                return;
            }
            if (str == null || str.Length == 0)
            {
                return;
            }
            LSTRDictionary.DrawString(font, str, x, y - font.GetAscent(), rotation, c);
        }

        public void DrawChar(char chars, float x, float y)
        {
            DrawChar(chars, x, y, 0);
        }

        public void DrawChar(char chars, float x, float y, float rotation)
        {
            DrawChar(chars, x, y, rotation, color);
        }

        public void DrawChar(char chars, float x, float y, float rotation, LColor c)
        {
            DrawString(Convert.ToString(chars), x, y, rotation, c);
        }

        public void DrawBytes(byte[] message, int offset, int length, int x, int y)
        {
            if (isClose)
            {
                return;
            }
            DrawString(System.Text.Encoding.UTF8.GetString(message, offset, length), x, y);
        }

        public void DrawChars(char[] message, int offset, int length, int x, int y)
        {
            if (isClose)
            {
                return;
            }
            DrawString(new string(message, offset, length), x, y);
        }

        public void DrawString(string message, int x, int y, int anchor)
        {
            if (isClose)
            {
                return;
            }
            int newx = x;
            int newy = y;
            if (anchor == 0)
            {
                anchor = LGraphics.TOP | LGraphics.LEFT;
            }
            if ((anchor & LGraphics.TOP) != 0)
            {
                newy -= font.GetAscent();
            }
            else if ((anchor & LGraphics.BOTTOM) != 0)
            {
                newy -= font.GetAscent();
            }
            if ((anchor & LGraphics.HCENTER) != 0)
            {
                newx -= font.StringWidth(message) / 2;
            }
            else if ((anchor & LGraphics.RIGHT) != 0)
            {
                newx -= font.StringWidth(message);
            }
            DrawString(message, newx, newy);
        }



        public void DrawStyleString(string message, float x, float y, uint color,
                uint color1)
        {
            if (isClose)
            {
                return;
            }
            SetColor(color);
            DrawString(message, x + 1, y);
            DrawString(message, x - 1, y);
            DrawString(message, x, y + 1);
            DrawString(message, x, y - 1);
            SetColor(color1);
            DrawString(message, x, y);
        }

        public void DrawStyleString(string message, float x, float y, LColor c1,
                LColor c2)
        {
            if (isClose)
            {
                return;
            }
            SetColorRGB(c1);
            DrawString(message, x + 1, y);
            DrawString(message, x - 1, y);
            DrawString(message, x, y + 1);
            DrawString(message, x, y - 1);
            SetColorRGB(c2);
            DrawString(message, x, y);
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
            color.A = (byte)(a * lastAlpha);
        }

        public void SetColor(uint c)
        {
            if (lastAlpha == 1f)
            {
                color.PackedValue = c;
            }
            else
            {
                color.R = LColor.GetRed(c);
                color.G = LColor.GetGreen(c);
                color.B = LColor.GetBlue(c);
                color.A = (byte)(LColor.GetAlpha(c) * lastAlpha);
            }
        }

        public LColor GetLColor()
        {
            return new LColor(color);
        }

        public void Restore()
        {
            if (isClose)
            {
                return;
            }
            this.lastAlpha = 1;
            this.sx = 1;
            this.sy = 1;
            if (!isPushed)
            {
                _gl.GLPopMatrix();
                isPushed = false;
            }
            ResetFont();
            ResetLineWidth();
            ClearClip();
        }

        public void Rotate(float rx, float ry, float angle)
        {
            _gl.GLRotatef(angle,rx, ry, 0f);
        }

        public void Rotate(float angle)
        {
            _gl.GLRotate(angle);
        }

        public SpriteBatch NewSpriteBatch()
        {
            return new SpriteBatch(GL.device);
        }

        public bool UseGLBegin()
        {
            return useBegin;
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
            return new XNALinearGradient(GL.device);
        }

        public XNAPolyline NewXNAPolyline()
        {
            return new XNAPolyline(GL.device);
        }

        public XNAPolygon NewXNAPolygon()
        {
            return new XNAPolygon(GL.device);
        }

        public XNACircle NewXNACircle()
        {
            return new XNACircle(GL.device);
        }

        public XNAEllipse NewXNAEllipse()
        {
            return new XNAEllipse(GL.device);
        }

        public XNAPath NewPath()
        {
            return new XNAPath(GL.device);
        }

        public XNARadialGradient NewXNARadialGradient()
        {
            return new XNARadialGradient(GL.device);
        }

        public XNARectangle NewXNARectangle()
        {
            return new XNARectangle(GL.device);
        }

        public void DrawTexture(LTexture texture, float x, float y)
        {
            if (isClose || texture == null || texture.isClose)
            {
                return;
            }
            if (!texture.isLoaded)
            {
                texture.LoadTexture();
            }
            DrawTexture(texture, x, y, texture.width, texture.height, 0, 0,
                    texture.width, texture.height, color, 0, null, Direction.TRANS_NONE);
        }

        public void DrawFlipTexture(LTexture texture, float x, float y,
                LColor color)
        {
            if (isClose || texture == null || texture.isClose)
            {
                return;
            }
            if (!texture.isLoaded)
            {
                texture.LoadTexture();
            }
            DrawTexture(texture, x, y, texture.width, texture.height, 0, 0,
                    texture.width, texture.height, color, 0, null,
                    Direction.TRANS_FILP);
        }

        public void DrawMirrorTexture(LTexture texture, float x, float y,
                LColor color)
        {
            if (isClose || texture == null || texture.isClose)
            {
                return;
            }
            if (!texture.isLoaded)
            {
                texture.LoadTexture();
            }
            DrawTexture(texture, x, y, texture.width, texture.height, 0, 0,
                    texture.width, texture.height, color, 0, null,
                    Direction.TRANS_MIRROR);
        }

        public void DrawTexture(LTexture texture, float x, float y,
                LColor color)
        {
            if (isClose || texture == null || texture.isClose)
            {
                return;
            }
            if (!texture.isLoaded)
            {
                texture.LoadTexture();
            }
            DrawTexture(texture, x, y, texture.width, texture.height, 0, 0,
                    texture.width, texture.height, color, 0, null, Direction.TRANS_NONE);
        }

        public void DrawJavaTexture(LTexture texture, float dx1, float dy1,
                float dx2, float dy2, float sx1, float sy1, float sx2, float sy2)
        {
            if (isClose || texture == null || texture.isClose)
            {
                return;
            }
            DrawTexture(texture, dx1, dy1, dx2 - dx1, dy2 - dy1, sx1, sy1, sx2,
                    sy2, color, 0, null, Direction.TRANS_NONE);
        }

        public void DrawTexture(LTexture texture, float x, float y,
                float rotation)
        {
            if (isClose || texture == null || texture.isClose)
            {
                return;
            }
            if (!texture.isLoaded)
            {
                texture.LoadTexture();
            }
            DrawTexture(texture, x, y, texture.width, texture.height, 0, 0,
                    texture.width, texture.height, color, rotation, null, Direction.TRANS_NONE);
        }

        /**
         * 渲染纹理为指定状态
         * 
         * @param texture
         * @param x
         * @param y
         * @param dir
         */
        public void DrawTexture(LTexture texture, float x, float y,
                Direction dir)
        {
            if (isClose || texture == null || texture.isClose)
            {
                return;
            }
            if (!texture.isLoaded)
            {
                texture.LoadTexture();
            }
            DrawTexture(texture, x, y, texture.width, texture.height, 0, 0,
                    texture.width, texture.height, color, 0, null, dir);
        }

        /**
         * 渲染纹理为指定状态
         * 
         * @param texture
         * @param x
         * @param y
         * @param color
         * @param rotation
         */
        public void DrawTexture(LTexture texture, float x, float y,
                LColor color, float rotation)
        {
            if (isClose || texture == null || texture.isClose)
            {
                return;
            }
            if (!texture.isLoaded)
            {
                texture.LoadTexture();
            }
            DrawTexture(texture, x, y, texture.width, texture.height, 0, 0,
                    texture.width, texture.height, color, rotation, null, Direction.TRANS_NONE);
        }

        /**
         * 渲染纹理为指定状态
         * 
         * @param texture
         * @param x
         * @param y
         * @param color
         * @param dir
         */
        public void DrawTexture(LTexture texture, float x, float y,
                LColor color, Direction dir)
        {
            if (isClose || texture == null || texture.isClose)
            {
                return;
            }
            if (!texture.isLoaded)
            {
                texture.LoadTexture();
            }
            DrawTexture(texture, x, y, texture.width, texture.height, 0, 0,
                    texture.width, texture.height, color, 0, null, dir);
        }

        /**
         * 渲染纹理为指定状态
         * 
         * @param texture
         * @param x
         * @param y
         * @param color
         * @param rotation
         * @param origin
         * @param dir
         */
        public void DrawTexture(LTexture texture, float x, float y,
                LColor color, float rotation, Vector2f origin, Direction dir)
        {
            if (isClose || texture == null || texture.isClose)
            {
                return;
            }
            if (!texture.isLoaded)
            {
                texture.LoadTexture();
            }
            DrawTexture(texture, x, y, texture.width, texture.height, 0, 0,
                    texture.width, texture.height, color, rotation, origin, dir);
        }

        /**
         * 渲染纹理为指定状态
         * 
         * @param texture
         * @param x
         * @param y
         * @param color
         * @param rotation
         * @param origin
         * @param scale
         * @param dir
         */
        public void DrawTexture(LTexture texture, float x, float y,
                LColor color, float rotation, Vector2f origin, float scale,
                Direction dir)
        {
            if (isClose || texture == null || texture.isClose)
            {
                return;
            }
            if (!texture.isLoaded)
            {
                texture.LoadTexture();
            }
            DrawTexture(texture, x, y, texture.width * scale, texture.height
                    * scale, 0, 0, texture.width, texture.height, color, rotation,
                    origin, dir);
        }

        /**
         * 渲染纹理为指定状态
         * 
         * @param texture
         * @param x
         * @param y
         * @param width
         * @param height
         */
        public void DrawTexture(LTexture texture, float x, float y,
                float width, float height)
        {
            if (isClose || texture == null || texture.isClose)
            {
                return;
            }
            if (!texture.isLoaded)
            {
                texture.LoadTexture();
            }
            DrawTexture(texture, x, y, width, height, 0, 0, texture.width,
                    texture.height, color, 0, null, Direction.TRANS_NONE);
        }

        /**
         * 渲染纹理为指定状态
         * 
         * @param texture
         * @param x
         * @param y
         * @param width
         * @param height
         * @param color
         */
        public void DrawTexture(LTexture texture, float x, float y,
                float width, float height, LColor color)
        {
            if (isClose || texture == null || texture.isClose)
            {
                return;
            }
            if (!texture.isLoaded)
            {
                texture.LoadTexture();
            }
            DrawTexture(texture, x, y, width, height, 0, 0, texture.width,
                    texture.height, color, 0, null, Direction.TRANS_NONE);
        }

        /**
         * 渲染纹理为指定状态
         * 
         * @param texture
         * @param x
         * @param y
         * @param width
         * @param height
         * @param rotation
         */
        public void DrawTexture(LTexture texture, float x, float y,
                float width, float height, float rotation)
        {
            if (isClose || texture == null || texture.isClose)
            {
                return;
            }
            if (!texture.isLoaded)
            {
                texture.LoadTexture();
            }
            DrawTexture(texture, x, y, width, height, 0, 0, texture.width,
                    texture.height, color, rotation, null, Direction.TRANS_NONE);
        }

        /**
         * 渲染纹理到指定位置
         * 
         * @param texture
         * @param x
         * @param y
         * @param rotation
         * @param d
         */
        public void DrawTexture(LTexture texture, float x, float y,
                float rotation, Direction d)
        {
            if (isClose || texture == null || texture.isClose)
            {
                return;
            }
            if (!texture.isLoaded)
            {
                texture.LoadTexture();
            }
            DrawTexture(texture, x, y, texture.width, texture.height, 0, 0,
                    texture.width, texture.height, color, rotation, null, d);
        }

        /**
         * 渲染纹理为指定状态
         * 
         * @param texture
         * @param x
         * @param y
         * @param width
         * @param height
         * @param dir
         */
        public void DrawTexture(LTexture texture, float x, float y,
                float width, float height, Direction dir)
        {
            if (isClose || texture == null || texture.isClose)
            {
                return;
            }
            DrawTexture(texture, x, y, width, height, 0, 0, texture.width,
                    texture.height, color, 0, null, dir);
        }

        /**
         * 渲染纹理为指定状态
         * 
         * @param texture
         * @param x
         * @param y
         * @param width
         * @param height
         * @param color
         * @param rotation
         */
        public void DrawTexture(LTexture texture, float x, float y,
                float width, float height, float rotation, LColor color)
        {
            if (isClose || texture == null || texture.isClose)
            {
                return;
            }
            DrawTexture(texture, x, y, width, height, 0, 0, texture.width,
                    texture.height, color, rotation, null, Direction.TRANS_NONE);
        }

        /**
         * 渲染纹理为指定状态
         * 
         * @param texture
         * @param x
         * @param y
         * @param width
         * @param height
         * @param color
         * @param dir
         */
        public void DrawTexture(LTexture texture, float x, float y,
                float width, float height, LColor color, Direction dir)
        {
            if (isClose || texture == null || texture.isClose)
            {
                return;
            }
            DrawTexture(texture, x, y, width, height, 0, 0, texture.width,
                    texture.height, color, 0, null, dir);
        }

        /**
         * 渲染纹理为指定状态
         * 
         * @param texture
         * @param x
         * @param y
         * @param width
         * @param height
         * @param color
         * @param rotation
         * @param origin
         * @param dir
         */
        public void DrawTexture(LTexture texture, float x, float y,
                float width, float height, LColor color, float rotation,
                Vector2f origin, Direction dir)
        {
            if (isClose || texture == null || texture.isClose)
            {
                return;
            }
            DrawTexture(texture, x, y, width, height, 0, 0, texture.width,
                    texture.height, color, rotation, origin, dir);
        }

        /**
         * 渲染纹理为指定状态
         * 
         * @param texture
         * @param dx1
         * @param dy1
         * @param dx2
         * @param dy2
         * @param sx1
         * @param sy1
         * @param sx2
         * @param sy2
         */
        public void DrawTexture(LTexture texture, float dx1, float dy1,
                float dx2, float dy2, float sx1, float sy1, float sx2, float sy2)
        {
            if (isClose || texture == null || texture.isClose)
            {
                return;
            }
            DrawTexture(texture, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, color,
                    0, null, Direction.TRANS_NONE);

        }

        /**
         * 渲染纹理为指定状态
         * 
         * @param texture
         * @param dx1
         * @param dy1
         * @param dx2
         * @param dy2
         * @param sx1
         * @param sy1
         * @param sx2
         * @param sy2
         * @param color
         */
        public void LTexture(LTexture texture, float dx1, float dy1,
                float dx2, float dy2, float sx1, float sy1, float sx2, float sy2,
                LColor color)
        {
            if (isClose || texture == null || texture.isClose)
            {
                return;
            }
            DrawTexture(texture, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, color,
                    0, null, Direction.TRANS_NONE);
        }

        /**
         * 渲染纹理为指定状态
         * 
         * @param texture
         * @param dx1
         * @param dy1
         * @param dx2
         * @param dy2
         * @param sx1
         * @param sy1
         * @param sx2
         * @param sy2
         * @param rotation
         * @param color
         */
        public void DrawTexture(LTexture texture, float dx1, float dy1,
                float dx2, float dy2, float sx1, float sy1, float sx2, float sy2,
                float rotation, LColor color)
        {
            if (isClose || texture == null || texture.isClose)
            {
                return;
            }
            DrawTexture(texture, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, color,
                    rotation, null, Direction.TRANS_NONE);
        }

        /**
         * 渲染纹理为指定状态
         * 
         * @param texture
         * @param x_src
         * @param y_src
         * @param width
         * @param height
         * @param transform
         * @param x_dst
         * @param y_dst
         * @param anchor
         */
        public void DrawRegion(LTexture texture, int x_src, int y_src, int width,
                int height, int transform, int x_dst, int y_dst, int anchor)
        {
            DrawRegion(texture, x_src, y_src, width, height, transform, x_dst,
                    y_dst, anchor, color);
        }

        /**
         * 渲染纹理为指定状态
         * 
         * @param texture
         * @param x_src
         * @param y_src
         * @param width
         * @param height
         * @param transform
         * @param x_dst
         * @param y_dst
         * @param anchor
         * @param c
         */
        public void DrawRegion(LTexture texture, int x_src, int y_src, int width,
                int height, int transform, int x_dst, int y_dst, int anchor,
                LColor c)
        {
            if (isClose || texture == null || texture.isClose)
            {
                return;
            }
            if (!texture.isLoaded)
            {
                texture.LoadTexture();
            }
            if (x_src + width > texture.GetWidth()
                    || y_src + height > texture.GetHeight() || width < 0
                    || height < 0 || x_src < 0 || y_src < 0)
            {
                throw new Exception("Area out of texture");
            }
            int dW = width, dH = height;

            float rotate = 0;
            Direction dir = Direction.TRANS_NONE;

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
                        dir = Direction.TRANS_MIRROR;
                        break;
                    }
                case TRANS_MIRROR_ROT90:
                    {
                        dir = Direction.TRANS_MIRROR;
                        rotate = -90;
                        dW = height;
                        dH = width;
                        break;
                    }
                case TRANS_MIRROR_ROT180:
                    {
                        dir = Direction.TRANS_MIRROR;
                        rotate = -180;
                        break;
                    }
                case TRANS_MIRROR_ROT270:
                    {
                        dir = Direction.TRANS_MIRROR;
                        rotate = -270;
                        dW = height;
                        dH = width;
                        break;
                    }
                default:
                    throw new Exception("Bad transform");
            }

            bool badAnchor = false;

            if (anchor == 0)
            {
                anchor = LGraphics.TOP | LGraphics.LEFT;
            }

            if ((anchor & 0x7f) != anchor || (anchor & LGraphics.BASELINE) != 0)
            {
                badAnchor = true;
            }

            if ((anchor & LGraphics.TOP) != 0)
            {
                if ((anchor & (LGraphics.VCENTER | LGraphics.BOTTOM)) != 0)
                {
                    badAnchor = true;
                }
            }
            else if ((anchor & LGraphics.BOTTOM) != 0)
            {
                if ((anchor & LGraphics.VCENTER) != 0)
                {
                    badAnchor = true;
                }
                else
                {
                    y_dst -= dH - 1;
                }
            }
            else if ((anchor & LGraphics.VCENTER) != 0)
            {
                y_dst -= (dH - 1) >> 1;
            }
            else
            {
                badAnchor = true;
            }

            if ((anchor & LGraphics.LEFT) != 0)
            {
                if ((anchor & (LGraphics.HCENTER | LGraphics.RIGHT)) != 0)
                {
                    badAnchor = true;
                }
            }
            else if ((anchor & LGraphics.RIGHT) != 0)
            {
                if ((anchor & LGraphics.HCENTER) != 0)
                {
                    badAnchor = true;
                }
                else
                {
                    x_dst -= dW - 1;
                }
            }
            else if ((anchor & LGraphics.HCENTER) != 0)
            {
                x_dst -= (dW - 1) >> 1;
            }
            else
            {
                badAnchor = true;
            }
            if (badAnchor)
            {
                throw new Exception("Bad Anchor");
            }

            DrawTexture(texture, x_dst, y_dst, width, height, x_src, y_src, x_src
                    + width, y_src + height, c, rotate, null, dir);
        }

        /**
         * 渲染纹理为指定状态
         * 
         * @param texture
         * @param destRect
         */
        public void DrawTexture(LTexture texture, RectBox destRect)
        {
            if (isClose || texture == null || texture.isClose)
            {
                return;
            }
            if (!texture.isLoaded)
            {
                texture.LoadTexture();
            }
            DrawTexture(texture, destRect.x, destRect.y, destRect.width,
                    destRect.height, 0, 0, texture.width, texture.height, color, 0,
                    null, Direction.TRANS_NONE);
        }

        /**
         * 渲染纹理为指定状态
         * 
         * @param texture
         * @param destRect
         * @param color
         */
        public void DrawTexture(LTexture texture, RectBox destRect,
                LColor color)
        {
            if (isClose || texture == null || texture.isClose)
            {
                return;
            }
            if (!texture.isLoaded)
            {
                texture.LoadTexture();
            }
            DrawTexture(texture, destRect.x, destRect.y, destRect.width,
                    destRect.height, 0, 0, texture.width, texture.height, color, 0,
                    null, Direction.TRANS_NONE);
        }

        /**
         * 渲染纹理为指定状态
         * 
         * @param texture
         * @param destRect
         * @param srcRect
         */
        public void DrawTexture(LTexture texture, RectBox destRect,
                RectBox srcRect)
        {
            if (isClose || texture == null || texture.isClose)
            {
                return;
            }
            if (!texture.isLoaded)
            {
                texture.LoadTexture();
            }
            DrawTexture(texture, destRect.x, destRect.y, destRect.width,
                    destRect.height, srcRect.x, srcRect.y, srcRect.width,
                    srcRect.height, color, 0, null, Direction.TRANS_NONE);
        }

        /**
         * 渲染纹理为指定状态
         * 
         * @param texture
         * @param destRect
         * @param srcRect
         * @param color
         */
        public void DrawTexture(LTexture texture, RectBox destRect,
                RectBox srcRect, LColor color)
        {
            if (isClose || texture == null || texture.isClose)
            {
                return;
            }
            if (!texture.isLoaded)
            {
                texture.LoadTexture();
            }
            DrawTexture(texture, destRect.x, destRect.y, destRect.width,
                    destRect.height, srcRect.x, srcRect.y, srcRect.width,
                    srcRect.height, color, 0, null, Direction.TRANS_NONE);
        }

        public void DrawTexture(LTexture texture, RectBox destRect,
                RectBox srcRect, float rotation)
        {
            if (isClose || texture == null || texture.isClose)
            {
                return;
            }
            if (!texture.isLoaded)
            {
                texture.LoadTexture();
            }
            DrawTexture(texture, destRect.x, destRect.y, destRect.width,
                    destRect.height, srcRect.x, srcRect.y, srcRect.width,
                    srcRect.height, color, rotation, null, Direction.TRANS_NONE);
        }

        public void DrawTexture(LTexture texture, Vector2f position)
        {
            if (isClose || texture == null || texture.isClose)
            {
                return;
            }
            if (!texture.isLoaded)
            {
                texture.LoadTexture();
            }
            DrawTexture(texture, position.x, position.y, texture.width,
                    texture.height, 0, 0, texture.width, texture.height, color, 0,
                    null, Direction.TRANS_NONE);
        }

        public void DrawTexture(LTexture texture, Vector2f position,
                LColor color)
        {
            if (isClose || texture == null || texture.isClose)
            {
                return;
            }
            if (!texture.isLoaded)
            {
                texture.LoadTexture();
            }
            DrawTexture(texture, position.x, position.y, texture.width,
                    texture.height, 0, 0, texture.width, texture.height, color, 0,
                    null, Direction.TRANS_NONE);
        }

        public void DrawTexture(LTexture texture, float x, float y,
                float width, float height, float srcX, float srcY, float srcWidth,
                float srcHeight, LColor c, float rotation)
        {
            if (!texture.isLoaded)
            {
                texture.LoadTexture();
            }
            DrawTexture(texture, x, y, width, height, srcX, srcY, srcWidth,
                    srcHeight, c, rotation, null, Direction.TRANS_NONE);
        }

        private void DrawTexture(LTexture texture, float x, float y,
                float width, float height, float srcX, float srcY, float srcWidth,
                float srcHeight, LColor c, float rotation, Vector2f origin,
                Direction dir)
        {

            if (isClose)
            {
                return;
            }
            if (!texture.isVisible)
            {
                return;
            }
            if (!texture.isLoaded)
            {
                texture.LoadTexture();
            }
            lastTextre = texture;
            texBatch.SetTexture(texture);
            texBatch.GLBegin();
            lastTextre = texture;
            if (c != null)
            {
                texBatch.SetImageColor(c);
            }
            else
            {
                if (lastAlpha != 1f)
                {
                    byte old = color.A;
                    color.A = (byte)(old * lastAlpha);
                    texBatch.SetImageColor(color);
                    color.A = old;
                }
                else
                {
                    texBatch.SetImageColor(color);
                }
            }
            bool flipX = false;
            bool flipY = false;
            if (Direction.TRANS_MIRROR == dir)
            {
                flipX = true;
            }
            else if (Direction.TRANS_FILP == dir)
            {
                flipY = true;
            }
            else if (Direction.TRANS_MF == dir)
            {
                flipX = true;
                flipY = true;
            }
            if (origin != null)
            {
                texBatch.Draw(x, y, origin.x, origin.y, width, height, 1f, 1f,
                          rotation, srcX, srcY, srcWidth, srcHeight, flipX, flipY);
            }
            else if (rotation == 0 && !flipX && !flipY)
            {
                texBatch.Draw(x, y, width, height, srcX, srcY, srcWidth, srcHeight);
            }
            else if (rotation == 0)
            {
                texBatch.Draw(x, y, width,
                         height, srcX, srcY, srcWidth,
                         srcHeight, flipX, flipY);
            }
            else
            {
                texBatch.Draw(x, y, width / 2, height / 2, width, height, 1f, 1f,
                  rotation, srcX, srcY, srcWidth, srcHeight, flipX, flipY);
            }
            if (texBatch.isColor)
            {
                texBatch.SetImageColor(LColor.white);
            }
            texBatch.GLEnd();
        }

        private LTexture lastTextre;

        public LTexture GetLastTexture()
        {
            return lastTextre;
        }

        public void Save()
        {
            if (isClose)
            {
                return;
            }
            if (!isPushed)
            {
                _gl.GLPushMatrix();
                isPushed = true;
            }
        }

        public void ResetFont()
        {
            this.font = LFont.GetDefaultFont();
            this.ResetColor();
        }

        public float GetTranslateX()
        {
            return _gl.GLCamera.Position.X;
        }

        public float GetTranslateY()
        {
            return _gl.GLCamera.Position.Y;
        }

        public static bool IsPixelFlinger()
        {
            return true;
        }

        public bool IsClose()
        {
            return isClose;
        }

        public void Dispose()
        {
            this.isClose = true;
            this.useBegin = false;
            if (WhitePixel != null)
            {
                WhitePixel.Dispose();
                WhitePixel = null;
            }
        }
    }
}
