namespace Loon.Core.Graphics.Opengl
{
    using Loon.Java;
    using Loon.Utils;

    public sealed class GLType
    {

        public readonly static GLType Point = new GLType(GL10.GL_POINTS);

        public readonly static GLType Line = new GLType(GL10.GL_LINE_STRIP);

        public readonly static GLType Filled = new GLType(GL10.GL_TRIANGLES);

        internal int glType;

        internal GLType(int glType)
        {
            this.glType = glType;
        }

    }


    public class GLRenderer
    {

        private GLBatch _renderer;

        private LColor _color = new LColor(1f, 1f, 1f, 1f);

        private GLType _currType = null;

        public GLRenderer()
            : this(9000)
        {

        }

        public GLRenderer(int maxVertices)
        {
            _renderer = new GLBatch(maxVertices);
        }

        public void Begin(GLType type)
        {
            if (_currType != null)
            {
                throw new RuntimeException(
                        "Call End() before beginning a new shape batch !");
            }
            _currType = type;
            _renderer.Begin(_currType.glType);
        }

        public void SetColor(LColor Color)
        {
            this._color.SetColor(Color);
        }

        public void SetColor(float r, float g, float b, float a)
        {
            this._color.SetColor(r, g, b, a);
        }

        public void Point(float x, float y)
        {
            Point(x, y, 1);
        }

        public void Point(float x, float y, float z)
        {
            if (_currType != GLType.Point)
            {
                throw new RuntimeException("Must call Begin(GLType.Point)");
            }
            CheckDirty();
            CheckFlush(1);
            _renderer.Color(_color);
            _renderer.Vertex(x, y, z);
        }

        public void Line(float x, float y, float z, float x2, float y2, float z2)
        {
            if (_currType != GLType.Line)
            {
                throw new RuntimeException("Must call Begin(GLType.Line)");
            }
            CheckDirty();
            CheckFlush(2);
            _renderer.Color(_color);
            _renderer.Vertex(x, y, z);
            _renderer.Color(_color);
            _renderer.Vertex(x2, y2, z2);
        }

        public void Line(float x, float y, float x2, float y2)
        {
            if (_currType != GLType.Line)
            {
                throw new RuntimeException("Must call Begin(GLType.Line)");
            }
            CheckDirty();
            CheckFlush(2);
            _renderer.Color(_color);
            _renderer.Vertex(x, y, 0);
            _renderer.Color(_color);
            _renderer.Vertex(x2, y2, 0);
        }

        public void Curve(float x1, float y1, float cx1, float cy1, float cx2,
                float cy2, float x2, float y2, int segments)
        {
            if (_currType != GLType.Line)
            {
                throw new RuntimeException("Must call Begin(GLType.Line)");
            }
            CheckDirty();
            CheckFlush(segments * 2 + 2);
            float subdiv_step = 1f / segments;
            float subdiv_step2 = subdiv_step * subdiv_step;
            float subdiv_step3 = subdiv_step * subdiv_step * subdiv_step;

            float pre1 = 3 * subdiv_step;
            float pre2 = 3 * subdiv_step2;
            float pre4 = 6 * subdiv_step2;
            float pre5 = 6 * subdiv_step3;

            float tmp1x = x1 - cx1 * 2 + cx2;
            float tmp1y = y1 - cy1 * 2 + cy2;

            float tmp2x = (cx1 - cx2) * 3 - x1 + x2;
            float tmp2y = (cy1 - cy2) * 3 - y1 + y2;

            float fx = x1;
            float fy = y1;

            float dfx = (cx1 - x1) * pre1 + tmp1x * pre2 + tmp2x * subdiv_step3;
            float dfy = (cy1 - y1) * pre1 + tmp1y * pre2 + tmp2y * subdiv_step3;

            float ddfx = tmp1x * pre4 + tmp2x * pre5;
            float ddfy = tmp1y * pre4 + tmp2y * pre5;

            float dddfx = tmp2x * pre5;
            float dddfy = tmp2y * pre5;

            for (; segments-- > 0; )
            {
                _renderer.Color(_color);
                _renderer.Vertex(fx, fy, 0);
                fx += dfx;
                fy += dfy;
                dfx += ddfx;
                dfy += ddfy;
                ddfx += dddfx;
                ddfy += dddfy;
                _renderer.Color(_color);
                _renderer.Vertex(fx, fy, 0);
            }
            _renderer.Color(_color);
            _renderer.Vertex(fx, fy, 0);
            _renderer.Color(_color);
            _renderer.Vertex(x2, y2, 0);
        }

        public void Triangle(float x1, float y1, float x2, float y2, float x3,
                float y3)
        {
            if (_currType != GLType.Filled && _currType != GLType.Line)
            {
                throw new RuntimeException(
                        "Must call Begin(GLType.Filled) or Begin(GLType.Line)");
            }
            CheckDirty();
            CheckFlush(6);
            if (_currType == GLType.Line)
            {
                _renderer.Color(_color);
                _renderer.Vertex(x1, y1, 0);
                _renderer.Color(_color);
                _renderer.Vertex(x2, y2, 0);

                _renderer.Color(_color);
                _renderer.Vertex(x2, y2, 0);
                _renderer.Color(_color);
                _renderer.Vertex(x3, y3, 0);

                _renderer.Color(_color);
                _renderer.Vertex(x3, y3, 0);
                _renderer.Color(_color);
                _renderer.Vertex(x1, y1, 0);
            }
            else
            {
                _renderer.Color(_color);
                _renderer.Vertex(x1, y1, 0);
                _renderer.Color(_color);
                _renderer.Vertex(x2, y2, 0);
                _renderer.Color(_color);
                _renderer.Vertex(x3, y3, 0);
            }
        }

        public void Rect(float x, float y, float width, float height)
        {
            if (_currType != GLType.Filled && _currType != GLType.Line)
            {
                throw new RuntimeException(
                        "Must call Begin(GLType.Filled) or Begin(GLType.Line)");
            }

            CheckDirty();
            CheckFlush(8);

            if (_currType == GLType.Line)
            {
                _renderer.Color(_color);
                _renderer.Vertex(x, y, 0);
                _renderer.Color(_color);
                _renderer.Vertex(x + width, y, 0);

                _renderer.Color(_color);
                _renderer.Vertex(x + width, y, 0);
                _renderer.Color(_color);
                _renderer.Vertex(x + width, y + height, 0);

                _renderer.Color(_color);
                _renderer.Vertex(x + width, y + height, 0);
                _renderer.Color(_color);
                _renderer.Vertex(x, y + height, 0);

                _renderer.Color(_color);
                _renderer.Vertex(x, y + height, 0);
                _renderer.Color(_color);
                _renderer.Vertex(x, y, 0);
            }
            else
            {
                _renderer.Color(_color);
                _renderer.Vertex(x, y, 0);
                _renderer.Color(_color);
                _renderer.Vertex(x + width, y, 0);
                _renderer.Color(_color);
                _renderer.Vertex(x + width, y + height, 0);

                _renderer.Color(_color);
                _renderer.Vertex(x + width, y + height, 0);
                _renderer.Color(_color);
                _renderer.Vertex(x, y + height, 0);
                _renderer.Color(_color);
                _renderer.Vertex(x, y, 0);
            }
        }

        public void Rect(float x, float y, float width, float height, LColor col1,
                LColor col2, LColor col3, LColor col4)
        {
            if (_currType != GLType.Filled && _currType != GLType.Line)
            {
                throw new RuntimeException(
                        "Must call Begin(GLType.Filled) or Begin(GLType.Line)");
            }
            CheckDirty();
            CheckFlush(8);

            if (_currType == GLType.Line)
            {
                _renderer.Color(col1.r, col1.g, col1.b, col1.a);
                _renderer.Vertex(x, y, 0);
                _renderer.Color(col2.r, col2.g, col2.b, col2.a);
                _renderer.Vertex(x + width, y, 0);

                _renderer.Color(col2.r, col2.g, col2.b, col2.a);
                _renderer.Vertex(x + width, y, 0);
                _renderer.Color(col3.r, col3.g, col3.b, col3.a);
                _renderer.Vertex(x + width, y + height, 0);

                _renderer.Color(col3.r, col3.g, col3.b, col3.a);
                _renderer.Vertex(x + width, y + height, 0);
                _renderer.Color(col4.r, col4.g, col4.b, col4.a);
                _renderer.Vertex(x, y + height, 0);

                _renderer.Color(col4.r, col4.g, col4.b, col4.a);
                _renderer.Vertex(x, y + height, 0);
                _renderer.Color(col1.r, col1.g, col1.b, col1.a);
                _renderer.Vertex(x, y, 0);
            }
            else
            {
                _renderer.Color(col1.r, col1.g, col1.b, col1.a);
                _renderer.Vertex(x, y, 0);
                _renderer.Color(col2.r, col2.g, col2.b, col2.a);
                _renderer.Vertex(x + width, y, 0);
                _renderer.Color(col3.r, col3.g, col3.b, col3.a);
                _renderer.Vertex(x + width, y + height, 0);

                _renderer.Color(col3.r, col3.g, col3.b, col3.a);
                _renderer.Vertex(x + width, y + height, 0);
                _renderer.Color(col4.r, col4.g, col4.b, col4.a);
                _renderer.Vertex(x, y + height, 0);
                _renderer.Color(col1.r, col1.g, col1.b, col1.a);
                _renderer.Vertex(x, y, 0);
            }
        }

        public void Oval(float x, float y, float radius)
        {
            Oval(x, y, radius, (int)(6 * (float)JavaRuntime.Java_Cbrt(radius)));
        }

        public void Oval(float x, float y, float radius, int segments)
        {
            if (segments <= 0)
            {
                throw new System.ArgumentException("segments must be >= 0.");
            }
            if (_currType != GLType.Filled && _currType != GLType.Line)
            {
                throw new RuntimeException(
                        "Must call Begin(GLType.Filled) or Begin(GLType.Line)");
            }
            CheckDirty();
            CheckFlush(segments * 2 + 2);
            float angle = 2 * 3.1415926f / segments;
            float cos = MathUtils.Cos(angle);
            float sin = MathUtils.Sin(angle);
            float cx = radius, cy = 0;
            if (_currType == GLType.Line)
            {
                for (int i = 0; i < segments; i++)
                {
                    _renderer.Color(_color);
                    _renderer.Vertex(x + cx, y + cy, 0);
                    float temp = cx;
                    cx = cos * cx - sin * cy;
                    cy = sin * temp + cos * cy;
                    _renderer.Color(_color);
                    _renderer.Vertex(x + cx, y + cy, 0);
                }
                _renderer.Color(_color);
                _renderer.Vertex(x + cx, y + cy, 0);
            }
            else
            {
                segments--;
                for (int i = 0; i < segments; i++)
                {
                    _renderer.Color(_color);
                    _renderer.Vertex(x, y, 0);
                    _renderer.Color(_color);
                    _renderer.Vertex(x + cx, y + cy, 0);
                    float temp = cx;
                    cx = cos * cx - sin * cy;
                    cy = sin * temp + cos * cy;
                    _renderer.Color(_color);
                    _renderer.Vertex(x + cx, y + cy, 0);
                }
                _renderer.Color(_color);
                _renderer.Vertex(x, y, 0);
                _renderer.Color(_color);
                _renderer.Vertex(x + cx, y + cy, 0);
            }
            cx = radius;
            cy = 0;
            _renderer.Color(_color);
            _renderer.Vertex(x + cx, y + cy, 0);
        }

        public void Polygon(float[] vertices)
        {
            if (_currType != GLType.Line)
            {
                throw new RuntimeException("Must call Begin(GLType.Line)");
            }
            if (vertices.Length < 6)
            {
                throw new System.ArgumentException(
                        "Polygons must contain at least 3 points.");
            }
            if (vertices.Length % 2 != 0)
            {
                throw new System.ArgumentException(
                        "Polygons must have a pair number of vertices.");
            }
            int numFloats = vertices.Length;

            CheckDirty();
            CheckFlush(numFloats);

            float firstX = vertices[0];
            float firstY = vertices[1];

            for (int i = 0; i < numFloats; i += 2)
            {
                float x1 = vertices[i];
                float y1 = vertices[i + 1];

                float x2;
                float y2;

                if (i + 2 >= numFloats)
                {
                    x2 = firstX;
                    y2 = firstY;
                }
                else
                {
                    x2 = vertices[i + 2];
                    y2 = vertices[i + 3];
                }

                _renderer.Color(_color);
                _renderer.Vertex(x1, y1, 0);
                _renderer.Color(_color);
                _renderer.Vertex(x2, y2, 0);
            }
        }

        private void CheckDirty()
        {
            GLType type = _currType;
            End();
            Begin(type);
        }

        private void CheckFlush(int newVertices)
        {
            if (_renderer.GetMaxVertices() - _renderer.GetNumVertices() >= newVertices)
            {
                return;
            }
            GLType type = _currType;
            End();
            Begin(type);
        }

        public void End()
        {
            if (_renderer != null)
            {
                _renderer.End();
                _currType = null;
            }
        }

        public void Flush()
        {
            GLType type = _currType;
            End();
            Begin(type);
        }

        public GLType GetCurrentType()
        {
            return _currType;
        }

        public void Dispose()
        {
            if (_renderer != null)
            {
                _renderer.Dispose();
            }
        }

    }
}
