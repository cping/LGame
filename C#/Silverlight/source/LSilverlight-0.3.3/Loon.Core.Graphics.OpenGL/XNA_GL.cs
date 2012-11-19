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
using System;
using System.Collections.Generic;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework;
using Loon.Utils;
using Loon.Core.Geom;

namespace Loon.Core.Graphics.OpenGL
{
    /// <summary>
    /// 以XNA模拟GL接口实现（此类特意不对外部开放）
    /// </summary>
    internal sealed class XNA_GL : GL, LRelease
    {
        private static XNA_GL _XnaToGL;

        private SpriteBatch batch;

        public static XNA_GL LoadGLShape
        {
            get
            {
                if (_XnaToGL == null)
                {
                    _XnaToGL = new XNA_GL(3000);
                }
                _XnaToGL.Load();
                return _XnaToGL;
            }
        }

        public void Load()
        {
            this.LineWidth = 1;
            this.pDevice = GLEx.device;
            this.pCamera = GLEx.cemera;

            effect = new BasicEffect(pDevice);
            effect.VertexColorEnabled = true;

            xnaLine = new XNALine();
            xnaRect = new XNARectangle(pDevice);
            xnaPolygon = new XNAPolygon(pDevice);

            maxPrimitiveCount = pDevice.GraphicsProfile == GraphicsProfile.Reach ? 65535 : 1048575;

            vertsPos = new VertexPositionColor[maxPrimitiveCount];

            effect.Projection = Microsoft.Xna.Framework.Matrix.CreateOrthographicOffCenter(0f, LSystem.screenRect.width, LSystem.screenRect.height, 0f, -1.0f, 1.0f);
        }

        private XNALine xnaLine;

        private XNARectangle xnaRect;

        private XNAPolygon xnaPolygon;

        private BasicEffect effect;

        private GraphicsDevice pDevice;

        private GLExCamera pCamera;

        private List<Vector3> vectors;

        private List<Color> colors;

        private bool hasCols;

        private int glType = GL_QUADS;

        private int maxPrimitiveCount;

        private VertexPositionColor[] vertsPos;

        private int indexPos;

#if XBOX || WINDOWS_PHONE
        public const int segments = 16;
#else
        public const int segments = 32;
#endif
        internal XNA_GL(int maxVertices)
        {
            this.vectors = new List<Vector3>(maxVertices * 3);
            this.colors = new List<Color>(maxVertices * 4);
        }

        internal XNA_GL()
            : this(3000)
        {
        }

        public int LineWidth
        {
            set;
            get;
        }

        public void GLColor(Color c)
        {
            colors.Add(c);
            this.hasCols = true;
        }

        public void GLColor(LColor c)
        {
            colors.Add(c.Color);
            this.hasCols = true;
        }

        public void GLColor(float r,float g,float b,float a)
        {
            colors.Add(new LColor(r,g,b,a).Color);
            this.hasCols = true;
        }

        public void GLVertex3f(float x, float y, float z)
        {
            vectors.Add(new Vector3(x, y, z));
        }

        public void GLVertex2f(float x, float y)
        {
            GLVertex3f(x, y, 0);
        }

        public void DrawRect(SpriteBatch batch, Vector2 topLeft, Vector2 bottomRight, Texture2D pixel, Color c)
        {
            if (LineWidth == 0)
            {
                vectors.Add(new Vector3(topLeft.X, topLeft.Y, 0f));
                vectors.Add(new Vector3(bottomRight.X, topLeft.Y, 0f));
                vectors.Add(new Vector3(bottomRight.X, bottomRight.Y, 0f));
                vectors.Add(new Vector3(topLeft.X, bottomRight.Y, 0f));
                vectors.Add(new Vector3(topLeft.X, topLeft.Y, 0f));
                RenderShape(batch, pixel, c);
            }
            else
            {
                xnaPolygon.ClearPoints();
                xnaRect.Position = topLeft;
                xnaRect.Width = bottomRight.X;
                xnaRect.Height = bottomRight.Y;
                xnaRect.Stroke = c;
                xnaRect.StrokeWidth = LineWidth;
                xnaRect.Draw(batch);
            }
        }

        public void DrawLine(SpriteBatch batch, Vector2 start, Vector2 end, Texture2D pixel, Color c)
        {
            if (LineWidth == 0)
            {
                vectors.Add(new Vector3(start.X, start.Y, 0f));
                vectors.Add(new Vector3(end.X, end.Y, 0f));
                RenderShape(batch, pixel, c);
            }
            else
            {
                xnaPolygon.ClearPoints();
                xnaLine.Start = start;
                xnaLine.End = end;
                xnaLine.Stroke = c;
                xnaLine.StrokeWidth = LineWidth;
                xnaLine.Draw(batch);
            }
        }

        private void RenderShape(SpriteBatch spriteBatch, Texture2D pixel, Color c)
        {
            if (vectors.Count < 2)
            {
                return;
            }
            Vector2 zoom = new Vector2();
            for (int i = 1; i < vectors.Count; i++)
            {
                Vector3 vector1 = vectors[i - 1];
                Vector3 vector2 = vectors[i];
                float distance = Vector3.Distance(vector1, vector2);
                float angle = MathUtils.Atan2(vector2.Y - vector1.Y, vector2.X - vector1.X);
                zoom.X = distance;
                zoom.Y = 1;
                spriteBatch.Draw(pixel,
                   new Vector2(vector1.X, vector1.Y),
                   null,
                   c,
                   angle,
                   Vector2.Zero,
                   zoom,
                   SpriteEffects.None,
                   0);
            }
            vectors.Clear();
        }


        public void GLBegin(int type)
        {
            this.glType = type;
            this.hasCols = false;
        }

        private void QUADS(List<Vector3> vertices, int count, Color c)
        {
            if (count<= 2)
            {
                LINE(vertices, count, c);
                return;
            }
            int index = 0;
            if (this.hasCols)
            {
                for (int i = 1; i < count - 1; i++)
                {
                    index = indexPos * 3;
                    vertsPos[index].Position = vertices[0];
                    vertsPos[index].Color = colors[0];
                    vertsPos[index + 1].Position = vertices[i];
                    vertsPos[index + 1].Color = colors[i];
                    vertsPos[index + 2].Position = vertices[i + 1];
                    vertsPos[index + 2].Color = colors[i + 1];
                    indexPos++;
                }
            }
            else
            {
                for (int i = 1; i < count - 1; i++)
                {
                    index = indexPos * 3;
                    vertsPos[index].Position = vertices[0];
                    vertsPos[index].Color = c;
                    vertsPos[index + 1].Position = vertices[i];
                    vertsPos[index + 1].Color = c;
                    vertsPos[index + 2].Position = vertices[i + 1];
                    vertsPos[index + 2].Color = c;

                    indexPos++;
                }
            }
        }

        private void LINE(List<Vector3> vertices, int count, Color color)
        {
              int index = 0;
              if (this.hasCols)
              {
                  index = indexPos * 2;
                  for (int i = 0; i < count - 1; i++)
                  {
                      vertsPos[index].Position = vertices[i];
                      vertsPos[index].Color = colors[i];
                      vertsPos[index + 1].Position = vertices[i + 1];
                      vertsPos[index + 1].Color = colors[i + 1];
                      indexPos++;
                  }

                  vertsPos[index].Position = vertices[count - 1];
                  vertsPos[index].Color = colors[count - 1];
                  vertsPos[index + 1].Position = vertices[0];
                  vertsPos[index + 1].Color = colors[0];
                  indexPos++;
              }
              else
              {
                  index = indexPos * 2;
                  for (int i = 0; i < count - 1; i++)
                  {
                      vertsPos[index].Position = vertices[i];
                      vertsPos[index].Color = color;
                      vertsPos[index + 1].Position = vertices[i + 1];
                      vertsPos[index + 1].Color = color;
                      indexPos++;
                  }

                  vertsPos[index].Position = vertices[count - 1];
                  vertsPos[index].Color = color;
                  vertsPos[index + 1].Position = vertices[0];
                  vertsPos[index + 1].Color = color;
                  indexPos++;
              }
        }

        public int Max
        {
            get
            {
                return maxPrimitiveCount;
            }
        }

        public void GLEnd()
        {
            GLEnd(Color.White);
        }

        public void GLEnd(Color c)
        {
            BlendState oldState = pDevice.BlendState;
            BlendState state;
            if (c.A == 255)
            {
                state = BlendState.AlphaBlend;
            }
            else
            {
                state = BlendState.Additive;
            }
            pDevice.BlendState = state;
            //effect.Alpha = (float)c.A / 255;
            // effect.View = pCamera.viewMatrix;
            /*  
             * foreach (EffectPass pass in effect.CurrentTechnique.Passes)
              {
                  pass.Apply();
              }
             */
            effect.CurrentTechnique.Passes[0].Apply();
            if (vectors.Count > 0)
            {
                switch (glType)
                {
                    case GL_TRIANGLE_FAN:
                        QUADS(this.vectors, vectors.Count, c);
                        pDevice.DrawUserPrimitives(PrimitiveType.TriangleList, vertsPos, 0, indexPos);
                        break;
                    case GL_LINES:
                        LINE(this.vectors, vectors.Count, c);
                        pDevice.DrawUserPrimitives(PrimitiveType.LineList, vertsPos, 0, indexPos);
                        break;
                }
            }
            indexPos = 0;
            vectors.Clear();
            if (hasCols)
            {
                colors.Clear();
            }
            pDevice.BlendState = oldState;
        }


        public void Draw(Shape shape, Color c)
        {
            if (shape == null)
            {
                return;
            }
            if (glType == GL.GL_LINE_STRIP || shape is Path)
            {

                xnaPolygon.ClearPoints();
                float[] points = shape.GetPoints();
                if (points == null)
                {
                    return;
                }
                if (points.Length == 0)
                {
                    return;
                }
                float x = points[0];
                float y = points[1];
                if (batch == null)
                {
                    batch = new SpriteBatch(GLEx.Device);
                }
                batch.Begin(SpriteSortMode.Deferred, c.A == 255 ? BlendState.AlphaBlend : BlendState.Additive, null, null, GLEx.Device.RasterizerState, null, GLEx.cemera.viewMatrix);
                for (int i = 0; i < points.Length; i += 2)
                {
                    DrawLine(batch, new Vector2(x, y), new Vector2(points[i], points[i + 1]), GLEx.WhitePixel, c);
                    x = points[i];
                    y = points[i + 1];
                }
                batch.End();
            }
            else
            {

                if (LineWidth == 0)
                {
                    float[] points = shape.GetPoints();
                    if (points.Length == 0)
                    {
                        return;
                    }
                    GLBegin(GL_LINES);
                    for (int i = 0; i < points.Length; i += 2)
                    {
                        GLVertex2f(points[i], points[i + 1]);
                    }
                    if (shape.Closed())
                    {
                        GLVertex2f(points[0], points[1]);
                    }
                    GLEnd(c);
                }
                else
                {

                    xnaPolygon.ClearPoints();
                    float[] points = shape.GetPoints();
                    if (points == null)
                    {
                        return;
                    }
                    if (points.Length == 0)
                    {
                        return;
                    }

                    for (int i = 0; i < points.Length; i += 2)
                    {
                        xnaPolygon.AddPoint(new Vector2(points[i], points[i + 1]));
                    }
                    if (shape.Closed())
                    {
                        xnaPolygon.AddPoint(new Vector2(points[0], points[1]));
                    }
                    xnaPolygon.Stroke = c;
                    xnaPolygon.StrokeWidth = LineWidth;
                    if (batch == null)
                    {
                        batch = new SpriteBatch(GLEx.Device);
                    }
                    batch.Begin(SpriteSortMode.Deferred, c.A == 255 ? BlendState.AlphaBlend : BlendState.Additive, null, null, GLEx.Device.RasterizerState, null, GLEx.cemera.viewMatrix);
                    xnaPolygon.Draw(batch);
                    batch.End();

                }
            }

        }

        public void Fill(Shape shape, Color c)
        {
            float[] points = shape.GetPoints();
            if (points.Length == 0)
            {
                return;
            }
            GLBegin(GL_QUADS);
            for (int i = 0; i < points.Length; i += 2)
            {
                GLVertex2f(points[i], points[i + 1]);
            }
            if (shape.Closed())
            {
                GLVertex2f(points[0], points[1]);
            }
            GLEnd(c);
        }

        public void DrawSixStart(Color color, float x, float y, float r)
        {
            DrawTriangle(color, x, y, r);
            DrawRTriangle(color, x, y, r);
        }

        public void DrawTriangle(Color color, float x, float y, float r)
        {
            float x1 = x;
            float y1 = y - r;
            float x2 = x + (r * MathUtils.Cos(MathUtils.PI / 6.0f));
            float y2 = y + (r * MathUtils.Sin(MathUtils.PI / 6.0f));
            float x3 = x - (r * MathUtils.Cos(MathUtils.PI / 6.0f));
            float y3 = y + (r * MathUtils.Sin(MathUtils.PI / 6.0f));
            float[] xpos = new float[3];
            xpos[0] = x1;
            xpos[1] = x2;
            xpos[2] = x3;
            float[] ypos = new float[3];
            ypos[0] = y1;
            ypos[1] = y2;
            ypos[2] = y3;
            FillPolygon(xpos, ypos, 3, color);
        }

        public void DrawRTriangle(Color color, float x, float y, float r)
        {
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
            FillPolygon(xpos, ypos, 3, color);
        }

        public void DrawTriangle(float x1, float y1, float x2,
                 float y2, float x3, float y3, Color c)
        {
            if (LineWidth == 0)
            {
                GLBegin(GL.GL_LINE_LOOP);
                GLVertex2f(x1, y1);
                GLVertex2f(x2, y2);
                GLVertex2f(x3, y3);
                GLEnd(c);
            }
            else
            {
                xnaPolygon.ClearPoints();
                if (batch == null)
                {
                    batch = new SpriteBatch(GLEx.Device);
                }
                batch.Begin(SpriteSortMode.Deferred, c.A == 255 ? BlendState.AlphaBlend : BlendState.Additive, null, null, GLEx.Device.RasterizerState, null, GLEx.cemera.viewMatrix);
                xnaPolygon.AddPoint(new Vector2(x1, y1));
                xnaPolygon.AddPoint(new Vector2(x2, y2));
                xnaPolygon.AddPoint(new Vector2(x3, y3));
                xnaPolygon.Stroke = c;
                xnaPolygon.StrokeWidth = LineWidth;
                xnaPolygon.Draw(batch);
                batch.End();
            }
        }

        public void FillTriangle(float x1, float y1, float x2,
                 float y2, float x3, float y3, Color c)
        {
            GLBegin(GL.GL_TRIANGLES);
            GLVertex2f(x1, y1);
            GLVertex2f(x2, y2);
            GLVertex2f(x3, y3);
            GLEnd(c);
        }

        public void FillTriangle(Triangle2f[] ts, Color c)
        {
            FillTriangle(ts, 0, 0, c);
        }

        public void FillTriangle(Triangle2f[] ts, int x, int y, Color c)
        {
            if (ts == null)
            {
                return;
            }
            int size = ts.Length;
            for (int i = 0; i < size; i++)
            {
                FillTriangle(ts[i], x, y, c);
            }
        }

        public void FillTriangle(Triangle2f t, Color c)
        {
            FillTriangle(t, 0, 0, c);
        }

        public void FillTriangle(Triangle2f t, float x, float y, Color c)
        {
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
            FillPolygon(xpos, ypos, 3, c);
        }

        public void DrawTriangle(Triangle2f[] ts, Color c)
        {
            DrawTriangle(ts, 0, 0, c);
        }

        public void DrawTriangle(Triangle2f[] ts, int x, int y, Color c)
        {
            if (ts == null)
            {
                return;
            }
            int size = ts.Length;
            for (int i = 0; i < size; i++)
            {
                DrawTriangle(ts[i], x, y, c);
            }
        }

        public void DrawTriangle(Triangle2f t, Color c)
        {
            DrawTriangle(t, 0, 0, c);
        }

        public void DrawTriangle(Triangle2f t, float x, float y, Color c)
        {
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
            DrawPolygon(xpos, ypos, 3, c);
        }

        public void FillPolygon(float[] xPoints, float[] yPoints,
            int nPoints, Color c)
        {
            GLBegin(GL.GL_POLYGON);
            for (int i = 0; i < nPoints; i++)
            {
                GLVertex2f(xPoints[i], yPoints[i]);
            }
            GLEnd(c);
        }

        public void DrawPolygon(float[] xPoints, float[] yPoints,
            int nPoints, Color c)
        {
            if (LineWidth == 0)
            {
                GLBegin(GL.GL_LINE_STRIP);
                for (int i = 0; i < nPoints; i++)
                {
                    GLVertex2f(xPoints[i], yPoints[i]);
                }
                GLEnd(c);
            }
            else
            {
                xnaPolygon.ClearPoints();
                if (batch == null)
                {
                    batch = new SpriteBatch(GLEx.Device);
                }
                batch.Begin(SpriteSortMode.Deferred, c.A == 255 ? BlendState.AlphaBlend : BlendState.Additive, null, null, GLEx.Device.RasterizerState, null, GLEx.cemera.viewMatrix);
                int size = xPoints.Length;
                for (int j = 0; j < size; j += 2)
                {
                    xnaPolygon.AddPoint(new Vector2(xPoints[j], yPoints[j]));
                }
                xnaPolygon.Stroke = c;
                xnaPolygon.StrokeWidth = LineWidth;
                xnaPolygon.Draw(batch);
                batch.End();
            }
        }

        public void FillOval(float x1, float y1, float width, float height, Color c)
        {
            this.FillArc(x1, y1, width, height, segments, 0, 360, c);
        }

        public void FillArc(float x1, float y1, float width, float height,
            float start, float end, Color c)
        {
            FillArc(x1, y1, width, height, segments, start, end, c);
        }

        public void FillArc(float x1, float y1, float width, float height,
             int segments, float start, float end, Color c)
        {
            while (end < start)
            {
                end += 360;
            }
            float cx = x1 + (width / 2.0f);
            float cy = y1 + (height / 2.0f);
            int step = 360 / segments;
            GLBegin(GL_QUADS);
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
            GLEnd(c);
        }

        public void DrawOval(float x1, float y1, float width, float height, Color c)
        {
            this.DrawOval(x1, y1, width, height, segments, 0, 360, c);
        }

        public void DrawOval(float x1, float y1, float width, float height,
            float start, float end, Color c)
        {
            DrawOval(x1, y1, width, height, segments, start, end, c);
        }

        private Dictionary<Int32, XNAPolyline> polyLazy = new Dictionary<Int32, XNAPolyline>(
                1000);

        public void DrawOval(float x1, float y1, float width, float height,
             int segments, float start, float end, Color c)
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
            hashCode = LSystem.Unite(hashCode, c.PackedValue);
            XNAPolygon poly = (XNAPolygon)CollectionUtils.Get(polyLazy, hashCode);
            if (poly == null)
            {
                poly = new XNAPolygon(GLEx.device);
                poly.Stroke = c;
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
            if (batch == null)
            {
                batch = new SpriteBatch(GLEx.device);
            }
            batch.Begin(SpriteSortMode.Deferred, c.A == 255 ? BlendState.AlphaBlend : BlendState.Additive, null, null, GLEx.Device.RasterizerState, null, GLEx.cemera.viewMatrix);
            poly.Draw(batch);
            batch.End();
        }

        public void Dispose()
        {
            if (vectors != null)
            {
                vectors.Clear();
            }
            if (batch != null)
            {
                batch.Dispose();
                batch = null;
            }
            if (_XnaToGL != null)
            {
                _XnaToGL = null;
            }
            if (polyLazy != null)
            {
                foreach (XNAPolyline line in polyLazy.Values)
                {
                    if (line != null)
                    {
                        line.Dispose();
                    }
                }
                polyLazy.Clear();
                polyLazy = null;
            }
        }
    }
}

