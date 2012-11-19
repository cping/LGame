namespace Loon.Action
{

    using Loon.Utils;

    public class CircleTo : ActionEvent
    {

        private int x;

        private int y;

        private int cx;

        private int cy;

        private int radius;

        private int velocity;

        private float dt;

        public CircleTo(int radius_0, int velocity_1)
        {
            this.radius = radius_0;
            this.velocity = velocity_1;
        }

        public override bool IsComplete()
        {
            return isComplete;
        }

        public override void OnLoad()
        {
            this.cx = (int)original.GetX();
            this.cy = (int)original.GetY();
            this.x = (cx + radius);
            this.y = cy;
        }

        public override void Update(long elapsedTime)
        {
            dt += MathUtils.Max((elapsedTime / 1000), 0.05f);
            this.x = (int)(this.cx + this.radius
                    * MathUtils.Cos(MathUtils.ToRadians(this.velocity * dt)));
            this.y = (int)(this.cy + this.radius
                    * MathUtils.Sin(MathUtils.ToRadians(this.velocity * dt)));
            lock (original)
            {
                original.SetLocation(x + offsetX, y + offsetY);
            }
        }

    }
}
