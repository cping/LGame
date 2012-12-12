namespace Loon.Core.Graphics.Component
{
    public abstract class ActorSpeed : Actor
    {

        private Speed speed = new Speed();

        private float x1;

        private float y1;

        public ActorSpeed()
        {
        }

        public ActorSpeed(Speed speed)
        {
            this.speed = speed;
        }

        public void Move()
        {
            this.x1 += this.speed.GetX();
            this.y1 += this.speed.GetY();
            if (this.x1 >= GetLLayer().GetWidth())
            {
                this.x1 = 0.0f;
            }
            if (this.x1 < 0.0f)
            {
                this.x1 = (GetLLayer().GetWidth() - 1);
            }
            if (this.y1 >= GetLLayer().GetHeight())
            {
                this.y1 = 0.0f;
            }
            if (this.y1 < 0.0f)
            {
                this.y1 = (GetLLayer().GetHeight() - 1);
            }
            SetLocation(this.x1, this.y1);
        }

        public override void SetLocation(float x, float y)
        {
            this.x1 = x;
            this.y1 = y;
            base.SetLocation(x, y);
        }

        public override void SetLocation(int x, int y)
        {
            this.x1 = x;
            this.y1 = y;
            base.SetLocation(x, y);
        }

        public void IncreaseSpeed(Speed s)
        {
            this.speed.Add(s);
        }

        public Speed GetSpeed()
        {
            return this.speed;
        }
    }
}
