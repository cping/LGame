using loon.utils;

namespace loon.geom
{
    public class Bezier
    {

        public Vector2f endPosition = new Vector2f();

        public Vector2f controlPoint1 = new Vector2f();

        public Vector2f controlPoint2 = new Vector2f();

        public Bezier() : this(0f, 0f, 0f, 0f, 0f, 0f)
        {

        }

        public Bezier(float cp1x, float cp1y, float cp2x, float cp2y, float endx, float endy) : this(Vector2f.At(cp1x, cp1y), Vector2f.At(cp2x, cp2y), Vector2f.At(endx, endy))
        {

        }

        public Bezier(Vector2f controlPos1, Vector2f controlPos2, Vector2f endPos)
        {
            controlPoint1.Set(controlPos1);
            controlPoint2.Set(controlPos2);
            endPosition.Set(endPos);
        }

        public Vector2f GetEndPosition()
        {
            return endPosition;
        }

        public Bezier SetEndPosition(float x, float y)
        {
            return SetEndPosition(Vector2f.At(x, y));
        }

        public Bezier SetEndPosition(Vector2f endPosition)
        {
            this.endPosition = endPosition;
            return this;
        }

        public Vector2f GetControlPoint1()
        {
            return controlPoint1;
        }

        public Bezier SetControlPoint1(float x, float y)
        {
            return SetControlPoint1(Vector2f.At(x, y));
        }

        public Bezier SetControlPoint1(Vector2f controlPoint1)
        {
            this.controlPoint1 = controlPoint1;
            return this;
        }

        public Vector2f GetControlPoint2()
        {
            return controlPoint2;
        }

        public Bezier SetControlPoint2(float x, float y)
        {
            return SetControlPoint2(Vector2f.At(x, y));
        }

        public Bezier SetControlPoint2(Vector2f controlPoint2)
        {
            this.controlPoint2 = controlPoint2;
            return this;
        }

        public Bezier Cpy()
        {
            return new Bezier(controlPoint1, controlPoint2, endPosition);
        }


        public override string ToString()
        {
            StringKeyValue builder = new StringKeyValue("Bezier");
            builder.Kv("controlPoint1", controlPoint1)
            .Comma()
            .Kv("controlPoint2", controlPoint2)
            .Comma()
            .Kv("endPosition", endPosition);
            return builder.ToString();
        }
    }
}
