namespace Loon.Core.Graphics.Component
{
    using System.Collections.Generic;
    using Loon.Core.Geom;
    using Loon.Utils;

    public class Speed
    {

        private static Vector2f gravity = new Vector2f(0.0f, 0.6f);

        private float dx = 0.0f;

        private float dy = 0.0f;

        private float direction = 0;

        private float length;

        public Speed()
        {
        }

        public Speed(float direction, float length)
        {
            this.Set(direction, length);
        }

        public static Vector2f GetVelocity(Vector2f velocity, List<Vector2f> forces)
        {
            foreach (Vector2f v in forces)
            {
                velocity.Add(v);
            }
            return velocity;
        }

        public static Vector2f ElasticForce(Vector2f displacement,
                float forceConstant)
        {
            float forceX = -forceConstant * displacement.GetX();
            float forceY = -forceConstant * displacement.GetY();
            Vector2f theForce = new Vector2f(forceX, forceY);
            return theForce;
        }

        public static Vector2f GetVelocity(Vector2f velocity, Vector2f force)
        {
            velocity.Add(force);
            return velocity;
        }

        public static Vector2f GetVelocity(Vector2f velocity, Vector2f force,
                float mass)
        {
            Vector2f acceleration = new Vector2f(force.GetX() / mass, force.GetY()
                    / mass);
            velocity.Add(acceleration);
            return velocity;
        }

        public static void SetGravity(int g)
        {
            gravity.SetY(g);
        }

        public static Vector2f Gravity()
        {
            return gravity;
        }

        public void Set(float direction, float length)
        {
            this.length = length;
            this.direction = direction;
            this.dx = (float)(length * MathUtils.Cos(MathUtils
                    .ToRadians(direction)));
            this.dy = (float)(length * MathUtils.Sin(MathUtils
                    .ToRadians(direction)));
        }

        public void SetDirection(float direction)
        {
            this.direction = direction;
            this.dx = (float)(this.length * MathUtils.Cos(MathUtils
                    .ToRadians(direction)));
            this.dy = (float)(this.length * MathUtils.Sin(MathUtils
                    .ToRadians(direction)));
        }

        public void Add(Speed other)
        {
            this.dx += other.dx;
            this.dy += other.dy;
            this.direction = (int)MathUtils.ToDegrees(MathUtils.Atan2(this.dy,
                    this.dx));
            this.length = (float)MathUtils.Sqrt(this.dx * this.dx + this.dy
                    * this.dy);
        }

        public float GetX()
        {
            return this.dx;
        }

        public float GetY()
        {
            return this.dy;
        }

        public float GetDirection()
        {
            return this.direction;
        }

        public float GetLength()
        {
            return this.length;
        }

        public Speed Copy()
        {
            Speed copy = new Speed();
            copy.dx = this.dx;
            copy.dy = this.dy;
            copy.direction = this.direction;
            copy.length = this.length;
            return copy;
        }
    }

}
