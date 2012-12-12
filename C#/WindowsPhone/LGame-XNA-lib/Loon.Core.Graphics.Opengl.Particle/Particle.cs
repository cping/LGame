using Loon.Utils;
namespace Loon.Core.Graphics.Opengl.Particle
{
    public class Particle
    {

        public const int INHERIT_POINTS = 1;

        public const int USE_POINTS = 2;

        public const int USE_QUADS = 3;

        protected internal float x;

        protected internal float y;

        protected internal float velx;

        protected internal float vely;

        protected internal float size = 10;

        protected internal LColor color = LColor.white;

        protected internal float life;

        protected internal float originalLife;

        private ParticleSystem engine;

        private ParticleEmitter emitter;

        protected internal LTexture image;

        protected internal int type;

        protected internal int usePoints = INHERIT_POINTS;

        protected internal bool oriented = false;

        protected internal float scaleY = 1.0f;

        public Particle(ParticleSystem engine)
        {
            this.engine = engine;
        }

        public float GetX()
        {
            return x;
        }

        public float GetY()
        {
            return y;
        }

        public void Move(float x, float y)
        {
            this.x += x;
            this.y += y;
        }

        public float GetSize()
        {
            return size;
        }

        public LColor GetColor()
        {
            return color;
        }

        public void SetImage(LTexture image)
        {
            this.image = image;
        }

        public float GetOriginalLife()
        {
            return originalLife;
        }

        public float GetLife()
        {
            return life;
        }

        public bool InUse()
        {
            return life > 0;
        }

        public void Render()
        {
            if ((engine.UsePoints() && (usePoints == INHERIT_POINTS))
                    || (usePoints == USE_POINTS))
            {
                GLEx gl = GLEx.Self;
                gl.GLBegin(GL.GL_POINTS);
                gl.GLColor(color);
                gl.GLVertex2f(x, y);
                gl.GLEnd();
            }
            else
            {
                float angle = 0;
                if (oriented)
                {
                    angle = MathUtils.Atan2(y, x) * 180 / MathUtils.PI;
                }
                image.Draw((x - (size / 2)), (y - (size / 2)), size, size, angle,
                        color);
            }
        }

        public void Update(long delta)
        {
            emitter.UpdateParticle(this, delta);
            life -= delta;
            if (life > 0)
            {
                x += delta * velx;
                y += delta * vely;
            }
            else
            {
                engine.Release(this);
            }
        }

        public void Init(ParticleEmitter emitter, float life)
        {
            x = 0;
            this.emitter = emitter;
            y = 0;
            velx = 0;
            vely = 0;
            size = 10;
            type = 0;
            this.originalLife = this.life = life;
            oriented = false;
            scaleY = 1.0f;
        }

        public void SetType(int type)
        {
            this.type = type;
        }

        public void SetUsePoint(int usePoints)
        {
            this.usePoints = usePoints;
        }

        public int GetIType()
        {
            return type;
        }

        public void SetSize(float size)
        {
            this.size = size;
        }

        public void AdjustSize(float delta)
        {
            size += delta;
            size = MathUtils.Max(0, size);
        }

        public void SetLife(float life)
        {
            this.life = life;
        }

        public void AdjustLife(float delta)
        {
            life += delta;
        }

        public void Kill()
        {
            life = 1;
        }

        public void SetColor(float r, float g, float b, float a)
        {
            if (color == LColor.white)
            {
                color = new LColor(r, g, b, a);
            }
            else
            {
                color.r = r;
                color.g = g;
                color.b = b;
                color.a = a;
            }
        }

        public void SetPosition(float x, float y)
        {
            this.x = x;
            this.y = y;
        }

        public void SetVelocity(float dirx, float diry, float speed)
        {
            this.velx = dirx * speed;
            this.vely = diry * speed;
        }

        public void SetSpeed(float speed)
        {
            float currentSpeed = MathUtils.Sqrt((velx * velx) + (vely * vely));
            velx *= speed;
            vely *= speed;
            velx /= currentSpeed;
            vely /= currentSpeed;
        }

        public void SetVelocity(float velx, float vely)
        {
            SetVelocity(velx, vely, 1);
        }

        public void AdjustPosition(float dx, float dy)
        {
            x += dx;
            y += dy;
        }

        public void AdjustColor(float r, float g, float b, float a)
        {
            if (color == null)
            {
                color = new LColor(1, 1, 1, 1f);
            }
            color.r += r;
            color.g += g;
            color.b += b;
            color.a += a;
        }

        public void AdjustColor(int r, int g, int b, int a)
        {
            if (color == null)
            {
                color = new LColor(1, 1, 1, 1f);
            }

            color.r += (r / 255.0f);
            color.g += (g / 255.0f);
            color.b += (b / 255.0f);
            color.a += (a / 255.0f);
        }

        public void AdjustVelocity(float dx, float dy)
        {
            velx += dx;
            vely += dy;
        }

        public ParticleEmitter GetEmitter()
        {
            return emitter;
        }

        public bool IsOriented()
        {
            return oriented;
        }

        public void SetOriented(bool oriented)
        {
            this.oriented = oriented;
        }

        public float GetScaleY()
        {
            return scaleY;
        }

        public void SetScaleY(float scaleY)
        {
            this.scaleY = scaleY;
        }
    }
}
