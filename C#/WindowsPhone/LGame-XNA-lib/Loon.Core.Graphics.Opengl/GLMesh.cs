using Loon.Java;
namespace Loon.Core.Graphics.Opengl
{
    public enum VertexDataType
    {
        VertexArray, VertexBufferObject, VertexBufferObjectSubData
    }

    public class GLMesh
    {

        internal readonly bool isVertexArray;

        public GLMesh(bool isStatic, int maxVertices, int maxIndices,
                GLAttributes attributes)
        {
            isVertexArray = true;
        }

        public GLMesh(bool isStatic, int maxVertices, int maxIndices,
                params Loon.Core.Graphics.Opengl.GLAttributes.VertexAttribute[] attributes)
        {
            isVertexArray = true;
        }

        public GLMesh(VertexDataType type, bool isStatic, int maxVertices,
                int maxIndices, params Loon.Core.Graphics.Opengl.GLAttributes.VertexAttribute[] attributes)
        {
            isVertexArray = true;
        }

        public void SetVertices(float[] vertices)
        {

        }

        public void SetVertices(float[] vertices, int offset, int count)
        {

        }

        public void GetVertices(float[] vertices)
        {

        }

        public void GetVertices(int srcOffset, float[] vertices)
        {

        }

        public void GetVertices(int srcOffset, int count, float[] vertices)
        {

        }

        public void GetVertices(int srcOffset, int count, float[] vertices, int destOffset)
        {
        }

        public void SetIndices(short[] indices)
        {

        }

        public void SetIndices(short[] indices, int offset, int count)
        {

        }

        public void GetIndices(short[] buffer)
        {

        }

        public int GetNumIndices()
        {
            return 0;
        }

        public int GetNumVertices()
        {
            return 0;
        }

        public int GetMaxVertices()
        {
            return 0;
        }

        public int GetMaxIndices()
        {
            return 0;
        }

        public int GetVertexSize()
        {
            return 0;
        }

        public void SetAutoBind(bool autoBind)
        {

        }

        public void Bind()
        {

        }

        public void Unbind()
        {

        }

        public void Render(int primitiveType)
        {

        }

        public void Render(int primitiveType, int offset, int count)
        {

        }

        public void Dispose()
        {

        }

        public Loon.Core.Graphics.Opengl.GLAttributes.VertexAttribute GetVertexAttribute(int usage)
        {
            return null;
        }

        public GLAttributes getVertexAttributes()
        {
            return null;
        }

        public ByteBuffer GetVerticesBuffer()
        {
            return null;
        }

        public ByteBuffer GetIndicesBuffer()
        {
            return null;
        }

    }

}
