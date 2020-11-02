using loon.utils;

namespace loon.geom
{
    public class Plane : XY
    {

        public enum Side
        {
            FRONT, BACK, ON_PLANE
        }

        public Vector3f normal;
        public float d;

        public Plane() : this(Vector3f.ZERO(), 0)
        {

        }

        public Plane(Vector3f normal, float d)
        {
            this.normal = new Vector3f(normal);
            this.d = d;
        }

        public Plane(float a, float b, float c, float d)
        {
            this.normal = new Vector3f(a, b, c);
            this.d = d;

            float length = normal.Length();
            normal.ScaleSelf(1 / length);
            this.d /= length;
        }

        public Plane(Plane plane) : this(plane.normal, plane.d)
        {

        }

        public static Vector3f Intersection(Plane p1, Plane p2, Plane p3,
                Vector3f dest)
        {
            if (dest == null)
            {
                dest = new Vector3f();
            }

            float c23x, c23y, c23z;
            float c31x, c31y, c31z;
            float c12x, c12y, c12z;

            c23x = p2.normal.y * p3.normal.z - p2.normal.z * p3.normal.y;
            c23y = p2.normal.z * p3.normal.x - p2.normal.x * p3.normal.z;
            c23z = p2.normal.x * p3.normal.y - p2.normal.y * p3.normal.x;

            c31x = p3.normal.y * p1.normal.z - p3.normal.z * p1.normal.y;
            c31y = p3.normal.z * p1.normal.x - p3.normal.x * p1.normal.z;
            c31z = p3.normal.x * p1.normal.y - p3.normal.y * p1.normal.x;

            c12x = p1.normal.y * p2.normal.z - p1.normal.z * p2.normal.y;
            c12y = p1.normal.z * p2.normal.x - p1.normal.x * p2.normal.z;
            c12z = p1.normal.x * p2.normal.y - p1.normal.y * p2.normal.x;

            float dot = p1.normal.Dot(c23x, c23y, c23z);
            dest.x = (-c23x * p1.d - c31x * p2.d - c12x * p3.d) / dot;
            dest.y = (-c23y * p1.d - c31y * p2.d - c12y * p3.d) / dot;
            dest.z = (-c23z * p1.d - c31z * p2.d - c12z * p3.d) / dot;

            return dest;
        }

        public Side TestPoint(Vector3f point)
        {
            return TestPoint(point.x, point.y, point.z);
        }

        public Side TestPoint(float x, float y, float z)
        {
            float test = normal.Dot(x, y, z) + d;

            if (test == 0)
                return Side.ON_PLANE;

            if (test > 0)
                return Side.FRONT;

            return Side.BACK;
        }

        public Plane Set(Vector3f normal, float d)
        {
            this.normal.Set(normal);
            this.d = d;

            return this;
        }

        public Plane Set(float a, float b, float c, float d)
        {
            this.normal.Set(a, b, c);
            this.d = d;

            float length = normal.Length();
            normal.ScaleSelf(1 / length);
            this.d /= length;

            return this;
        }

        public Plane Set(Plane plane)
        {
            this.normal.Set(plane.normal);
            this.d = plane.d;

            return this;
        }


        public override int GetHashCode()
        {
            int result = normal.GetHashCode();
            result = 31 * result + (d != +0.0f ? (int)NumberUtils.FloatToIntBits(d) : 0);
            return result;
        }


        public override bool Equals(object o)
        {
            if (this == o)
            {
                return true;
            }
            if (o == null || GetType() != o.GetType())
            {
                return false;
            }

            Plane plane = (Plane)o;
            return NumberUtils.Compare(plane.d, d) == 0
                    && normal.Equals(plane.normal);
        }

        public float GetX()
        {
            return normal.x;
        }

        public float GetY()
        {
            return normal.y;
        }


        public override string ToString()
        {
            StringKeyValue builder = new StringKeyValue("Plane");
            builder.Kv("normal", normal)
            .Comma()
            .Kv("dot", d);
            return builder.ToString();
        }
    }
}
