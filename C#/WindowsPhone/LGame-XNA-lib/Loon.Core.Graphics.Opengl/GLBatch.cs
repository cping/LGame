using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework;
using System.Collections.Generic;

namespace Loon.Core.Graphics.Opengl
{
    public class GLBatch : LRelease
    {

        private List<Vector3> vertexsBuffer;

        private List<Vector2> texCoordsBuffer;

        private List<Color> colorsBuffer;

         bool hasCols;

        private bool closed;

        private readonly int m_maxVertices;

        private int numVertices;

        private int primitiveType;

        private LColor m_color = new LColor(1f, 1f, 1f, 1f);

        private GLBase glbase;

        internal GLBase Base
        {
            get
            {
                return glbase;
            }
        }

        public GLBatch(int maxVertices)
        {
            this.m_maxVertices = maxVertices;
            this.glbase = new GLBase(maxVertices);
            this.vertexsBuffer = new List<Vector3>(3 * maxVertices);
            this.colorsBuffer = new List<Color>(4 * maxVertices);
            this.texCoordsBuffer = new List<Vector2>(2 * maxVertices);
        }

        public void Begin(int p)
        {
            lock (typeof(GLBatch))
            {
                GLEx.GL.DisableTextures();
                this.primitiveType = p;
                this.numVertices = 0;
                this.hasCols = false;
            }
        }

        public void End()
        {
            End(LColor.white);
        }

        internal void End(LColor col)
        {
            glbase.Transform(this.primitiveType, this.numVertices, this.vertexsBuffer, this.texCoordsBuffer, this.colorsBuffer, this.hasCols, true, col);
            glbase.Send(this.primitiveType, this.numVertices);
            glbase.Clear(m_maxVertices);
            GLEx.GL.EnableTextures();
        }

        public void Color(LColor c)
        {
            colorsBuffer.Add(c);
            m_color.SetColor(c.r, c.g, c.b, c.a);
            hasCols = true;
        }

        public void Color(float r, float g, float b, float a)
        {
            colorsBuffer.Add(new Color(r, g, b, a));
            m_color.SetColor(r, g, b, a);
            hasCols = true;
        }

        public void TexCoord(float u, float v)
        {
            texCoordsBuffer.Add(new Vector2(u, v));
        }

        public void Vertex(float x, float y)
        {
            Vertex(x, y, 0);
        }

        public void Vertex(float x, float y, float z)
        {
            if (primitiveType == GL.GL_POINTS || primitiveType == GL.GL_LINES)
            {
                vertexsBuffer.Add(new Vector3(x, y, z));
                vertexsBuffer.Add(new Vector3(x + 1, y + 1, z));
                numVertices += 2;
            }
            else
            {
                vertexsBuffer.Add(new Vector3(x, y, z));
                numVertices++;
            }
        }

        public bool IsColor()
        {
            return hasCols;
        }

        public LColor GetColor()
        {
            return m_color;
        }

        public int GetNumVertices()
        {
            return numVertices;
        }

        public int GetMaxVertices()
        {
            return m_maxVertices;
        }

        public bool IsClose()
        {
            return closed;
        }

        public void Dispose()
        {
            closed = true;
        }

    }
}
