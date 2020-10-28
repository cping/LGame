using loon.utils;

namespace loon.geom
{
    public class RectBox : Shape, BoxSize, XYZW
    {

        public int width;

        public int height;

        private Matrix4 _matrix;

        public RectBox()
        {
            SetBounds(0, 0, 0, 0);
        }

        public RectBox(int width, int height)
        {
            SetBounds(0, 0, width, height);
        }

        public RectBox(int x, int y, int width, int height)
        {
            SetBounds(x, y, width, height);
        }

        public RectBox(float x, float y, float width, float height)
        {
            SetBounds(x, y, width, height);
        }

        public RectBox(double x, double y, double width, double height)
        {
            SetBounds(x, y, width, height);
        }

        public RectBox(RectBox rect)
        {
            SetBounds(rect.x, rect.y, rect.width, rect.height);
        }

        public RectBox Offset(Vector2f offset)
        {
            x += offset.x;
            y += offset.y;
            return this;
        }

        public RectBox Offset(int offsetX, int offsetY)
        {
            x += offsetX;
            y += offsetY;
            return this;
        }

        public RectBox SetBoundsFromCenter(float centerX, float centerY, float cornerX, float cornerY)
        {
            float halfW = MathUtils.Abs(cornerX - centerX);
            float halfH = MathUtils.Abs(cornerY - centerY);
            SetBounds(centerX - halfW, centerY - halfH, halfW * 2.0, halfH * 2.0);
            return this;
        }

        public RectBox SetBounds(RectBox rect)
        {
            SetBounds(rect.x, rect.y, rect.width, rect.height);
            return this;
        }

        public RectBox SetBounds(double x, double y, double width, double height)
        {
            SetBounds((float)x, (float)y, (float)width, (float)height);
            return this;
        }

        public RectBox SetBounds(float x, float y, float width, float height)
        {
            this.x = x;
            this.y = y;
            this.width = (int)width;
            this.height = (int)height;
            this.minX = x;
            this.minY = y;
            this.maxX = x + width;
            this.maxY = y + height;
            this.pointsDirty = true;
            this.CheckPoints();
            return this;
        }

        public override Shape Transform(Matrix3 transform)
        {
            throw new System.NotImplementedException();
        }

        protected override void CreatePoints()
        {
            throw new System.NotImplementedException();
        }

        public float GetWidth()
        {
            throw new System.NotImplementedException();
        }

        public float GetHeight()
        {
            throw new System.NotImplementedException();
        }

        public void SetWidth(float w)
        {
            throw new System.NotImplementedException();
        }

        public void SetHeight(float h)
        {
            throw new System.NotImplementedException();
        }

        public float GetW()
        {
            throw new System.NotImplementedException();
        }

        public float GetZ()
        {
            throw new System.NotImplementedException();
        }
    }
}
