using System;
using System.Text;
namespace Loon.Core.Graphics.Opengl
{
    public sealed class GLAttributes
    {

        public class VertexAttribute
        {

            public int usage;

            public int numComponents;

            public int offset;

            public string alias;

            public VertexAttribute(int usage, int numComponents, string alias)
            {
                this.usage = usage;
                this.numComponents = numComponents;
                this.alias = alias;
            }

            public override int GetHashCode()
            {
                return base.GetHashCode();
            }

            public override bool Equals(object obj)
            {
                if (!(obj is VertexAttribute))
                {
                    return false;
                }
                VertexAttribute other = (VertexAttribute)obj;
                return this.usage == other.usage
                        && this.numComponents == other.numComponents
                        && this.alias.Equals(other.alias);
            }
        }

        public class Usage
        {
            public const int Position = 0;
            public const int Color = 1;
            public const int ColorPacked = 5;
            public const int Normal = 2;
            public const int TextureCoordinates = 3;
            public const int Generic = 4;
        }

        internal VertexAttribute[] attributes;

        public int vertexSize;

        public GLAttributes(params VertexAttribute[] attributes)
        {
            if (attributes.Length == 0)
            {
                throw new ArgumentException("attributes must be >= 1");
            }
            VertexAttribute[] list = new VertexAttribute[attributes.Length];
            for (int i = 0; i < attributes.Length; i++)
                list[i] = attributes[i];

            this.attributes = list;

            CheckValidity();
            vertexSize = CalculateOffsets();
        }

        public int GetOffset(int usage)
        {
            VertexAttribute vertexAttribute = FindByUsage(usage);
            if (vertexAttribute == null)
                return 0;
            return vertexAttribute.offset / 4;
        }

        public VertexAttribute FindByUsage(int usage)
        {
            int len = Size();
            for (int i = 0; i < len; i++)
            {
                if (Get(i).usage == usage)
                    return Get(i);
            }
            return null;
        }

        private int CalculateOffsets()
        {
            int count = 0;
            for (int i = 0; i < attributes.Length; i++)
            {
                VertexAttribute attribute = attributes[i];
                attribute.offset = count;
                if (attribute.usage == Usage.ColorPacked)
                    count += 4;
                else
                    count += 4 * attribute.numComponents;
            }

            return count;
        }

        private void CheckValidity()
        {
            bool pos = false;
            bool cols = false;
            bool nors = false;

            for (int i = 0; i < attributes.Length; i++)
            {
                VertexAttribute attribute = attributes[i];
                if (attribute.usage == Usage.Position)
                {
                    if (pos)
                        throw new ArgumentException(
                                "two position attributes were specified");
                    pos = true;
                }

                if (attribute.usage == Usage.Normal)
                {
                    if (nors)
                        throw new ArgumentException(
                                "two normal attributes were specified");
                }

                if (attribute.usage == Usage.Color
                        || attribute.usage == Usage.ColorPacked)
                {
                    if (attribute.numComponents != 4)
                        throw new ArgumentException(
                                "color attribute must have 4 components");

                    if (cols)
                        throw new ArgumentException(
                                "two color attributes were specified");
                    cols = true;
                }
            }

            if (pos == false)
                throw new ArgumentException(
                        "no position attribute was specified");
        }

        public int Size()
        {
            return attributes.Length;
        }

        public VertexAttribute Get(int index)
        {
            return attributes[index];
        }

        public override string ToString()
        {
            StringBuilder builder = new StringBuilder();
            builder.Append("[");
            for (int i = 0; i < attributes.Length; i++)
            {
                builder.Append("(");
                builder.Append(attributes[i].alias);
                builder.Append(", ");
                builder.Append(attributes[i].usage);
                builder.Append(", ");
                builder.Append(attributes[i].numComponents);
                builder.Append(", ");
                builder.Append(attributes[i].offset);
                builder.Append(")");
                builder.Append("\n");
            }
            builder.Append("]");
            return builder.ToString();
        }

        public override int GetHashCode()
        {
            return base.GetHashCode();
        }

        public override bool Equals(object obj)
        {
            if (!(obj is GLAttributes))
            {
                return false;
            }
            GLAttributes other = (GLAttributes)obj;
            if (this.attributes.Length != other.Size())
            {
                return false;
            }
            for (int i = 0; i < attributes.Length; i++)
            {
                if (!attributes[i].Equals(other.attributes[i]))
                {
                    return false;
                }
            }
            return true;
        }
    }
}
