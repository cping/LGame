namespace Loon.Core.Graphics.OpenGL
{
    using Microsoft.Xna.Framework;

    public class GLBatch : LRelease
    {

        private readonly int maxVertices;

        private int primitiveType;

        private XNA_GL batch;

        private int numVertices;

        private bool closed;

        public GLBatch()
        {
            batch = new XNA_GL();
        }

        public GLBatch(int maxVertices)
        {
            this.batch = new XNA_GL(maxVertices);
            this.maxVertices = maxVertices;
        }

        public void Begin(int p)
        {
            this.primitiveType = p;
            this.numVertices = 0;
            batch.GLBegin(primitiveType);
        }

        public void Color(LColor c)
        {
            batch.GLColor(c);
        }

        public void Color(Color c)
        {
            batch.GLColor(c);
        }

        public void Color(float r,float g,float b,float a)
        {
            batch.GLColor(r,g,b,a);
        }

        public void Vertex(float x, float y)
        {
            Vertex(x, y, 0);
        }

        public void Vertex(float x, float y, float z)
        {
            batch.GLVertex3f(x, y, z);
            numVertices++;
        }

        public int GetNumVertices()
        {
            return numVertices;
        }

        public int GetMaxVertices()
        {
            return maxVertices;
        }

        public void End()
        {
            batch.GLEnd();
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
