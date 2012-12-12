using Microsoft.Xna.Framework.Graphics;
using System.Collections.Generic;
using Microsoft.Xna.Framework;
namespace Loon.Core.Graphics.Opengl
{
    public class GLBase : LRelease
    {

        private static short[] Def_FanIndices;

        private static short[] Def_QuadIndices;

        public short[] FanIndices;

        public short[] QuadIndices;

        public VertexPositionColorTexture[] Vertices;

        public int GetVerticesSize()
        {
            return Vertices.Length;
        }

        public int GetFanIndicesSize()
        {
            return FanIndices.Length;
        }

        public int GetQuadIndicesSize()
        {
            return QuadIndices.Length;
        }

        public GLBase(int maxSize)
        {
            this.Vertices = new VertexPositionColorTexture[maxSize];
            int lines = maxSize / 2;
            int indices = lines * 6;
            if (Def_QuadIndices == null || Def_QuadIndices.Length < indices)
            {
                this.QuadIndices = new short[indices];
                for (int j = 0; j < lines; j++)
                {
                    this.QuadIndices[j * 6] = (short)(j * 4);
                    this.QuadIndices[(j * 6) + 1] = (short)((j * 4) + 1);
                    this.QuadIndices[(j * 6) + 2] = (short)((j * 4) + 2);
                    this.QuadIndices[(j * 6) + 3] = (short)(j * 4);
                    this.QuadIndices[(j * 6) + 4] = (short)((j * 4) + 2);
                    this.QuadIndices[(j * 6) + 5] = (short)((j * 4) + 3);
                }
                GLBase.Def_QuadIndices = QuadIndices;
            }
            else
            {
                this.QuadIndices = GLBase.Def_QuadIndices;
            }
            indices = maxSize * 3;
            if (Def_FanIndices == null || Def_FanIndices.Length < indices)
            {
                this.FanIndices = new short[indices];
                for (int k = 0; k < maxSize; k++)
                {
                    this.FanIndices[k * 3] = 0;
                    this.FanIndices[(k * 3) + 1] = (short)(k + 1);
                    this.FanIndices[(k * 3) + 2] = (short)(k + 2);
                }
                GLBase.Def_FanIndices = FanIndices;
            }
            else
            {
                this.FanIndices = GLBase.Def_FanIndices;
            }
        }

        public void Transform(int primitiveType, int numVertices, List<Vector3> vertexsBuffer, List<Vector2> texCoordsBuffer, List<Color> colorsBuffer, bool hasCols, bool hasClear, Color defaultColor)
        {
            GL.GLVertices(primitiveType, numVertices, Vertices, vertexsBuffer, texCoordsBuffer, colorsBuffer, hasCols, hasClear, defaultColor);
        }

        public void Send(int primitiveType, int count)
        {
            GLEx.GL.Submit(primitiveType, count, Vertices, QuadIndices, FanIndices);
        }

        public void Clear(int count)
        {
            System.Array.Clear(Vertices, 0, count);
        }

        public void Dispose()
        {

        }

    }
}
