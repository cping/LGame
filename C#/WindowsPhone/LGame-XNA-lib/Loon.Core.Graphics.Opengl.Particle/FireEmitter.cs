using Loon.Utils;
namespace Loon.Core.Graphics.Opengl.Particle
{

    public class FireEmitter : ParticleEmitter
    {

        private int x;

        private int y;

        private int interval = 50;

        private long timer;

        private float size = 40;

        public FireEmitter()
        {
        }

        public FireEmitter(int x, int y)
        {
            this.x = x;
            this.y = y;
        }

        public FireEmitter(int x, int y, float size)
        {
            this.x = x;
            this.y = y;
            this.size = size;
        }

        public void Update(ParticleSystem system, long delta)
        {
            timer -= delta;
            if (timer <= 0)
            {
                timer = interval;
                Particle p = system.GetNewParticle(this, 1000);
                p.SetColor(1, 1, 1, 0.5f);
                p.SetPosition(x, y);
                p.SetSize(size);
                float vx = (-0.02f + (MathUtils.Random() * 0.04f));
                float vy = (-(MathUtils.Random() * 0.15f));
                p.SetVelocity(vx, vy, 1.1f);
            }
        }

        public void UpdateParticle(Particle particle, long delta)
        {
            if (particle.GetLife() > 600)
            {
                particle.AdjustSize(0.07f * delta);
            }
            else
            {
                particle.AdjustSize(-0.04f * delta * (size / 40.0f));
            }
            float c = 0.002f * delta;
            particle.AdjustColor(0, -c / 2, -c * 2, -c / 4);
        }

        public bool IsEnabled()
        {
            return true;
        }

        public void SetEnabled(bool enabled)
        {
        }

        public bool Completed()
        {
            return false;
        }

        public bool UseAdditive()
        {
            return false;
        }

        public LTexture GetImage()
        {
            return null;
        }

        public bool UsePoints(ParticleSystem system)
        {
            return false;
        }

        public bool IsOriented()
        {
            return false;
        }

        public void WrapUp()
        {
        }

        public void ResetState()
        {
        }
    }
}
