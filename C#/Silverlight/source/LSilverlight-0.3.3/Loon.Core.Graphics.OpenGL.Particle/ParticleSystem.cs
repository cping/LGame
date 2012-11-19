namespace Loon.Core.Graphics.OpenGL.Particle
{
    using System.Collections.Generic;
    using Loon.Utils;
    using Loon.Utils.Debug;
    using System;

    public class ParticleSystem
    {

        public const int BLEND_ADDITIVE = 1;

        public const int BLEND_COMBINE = 2;

        private const int DEFAULT_PARTICLES = 100;

        private List<ParticleEmitter> removeMe = new List<ParticleEmitter>();

        public static void SetRelativePath(string path)
        {
            ConfigEmitter.SetRelativePath(path);
        }

        public class ParticlePool
        {
            public Particle[] particles;
            public List<Particle> available;

            public ParticlePool(ParticleSystem system, int maxParticles)
            {
                particles = new Particle[maxParticles];
                available = new List<Particle>();

                for (int i = 0; i < particles.Length; i++)
                {
                    particles[i] = CreateParticle(system);
                }

                Reset(system);
            }

            public void Reset(ParticleSystem system)
            {
                available.Clear();

                for (int i = 0; i < particles.Length; i++)
                {
                    available.Add(particles[i]);
                }
            }
        }

        protected internal Dictionary<ParticleEmitter, ParticlePool> particlesByEmitter = new Dictionary<ParticleEmitter, ParticlePool>();

        protected internal int maxParticlesPerEmitter;

        protected internal List<ParticleEmitter> emitters = new List<ParticleEmitter>();

        protected internal Particle dummy;

        private int blendingMode = BLEND_COMBINE;

        private int pCount;

        private bool usePoints;

        private float x;

        private float y;

        private bool removeCompletedEmitters = true;

        private LTexture sprite;

        private bool visible = true;

        private string defaultImageName;

        private LColor mask;

        public ParticleSystem(LTexture defaultSprite)
            : this(defaultSprite, DEFAULT_PARTICLES)
        {

        }

        public ParticleSystem(string defaultSpriteRef)
            : this(defaultSpriteRef, DEFAULT_PARTICLES)
        {

        }

        public void Reset()
        {
            IEnumerator<ParticlePool> pools = particlesByEmitter.Values.GetEnumerator();
            while (pools.MoveNext())
            {
                ParticlePool pool = pools.Current;
                pool.Reset(this);
            }

            for (int i = 0; i < emitters.Count; i++)
            {
                ParticleEmitter emitter = emitters[i];
                emitter.ResetState();
            }
        }

        public bool IsVisible()
        {
            return visible;
        }

        public void SetVisible(bool visible)
        {
            this.visible = visible;
        }

        public void SetRemoveCompletedEmitters(bool remove)
        {
            removeCompletedEmitters = remove;
        }

        public void SetUsePoints(bool usePoints)
        {
            this.usePoints = usePoints;
        }

        public bool UsePoints()
        {
            return usePoints;
        }

        public ParticleSystem(string defaultSpriteRef, int maxParticles)
            : this(defaultSpriteRef, maxParticles, null)
        {

        }

        public ParticleSystem(string defaultSpriteRef, int maxParticles, LColor mask)
        {
            this.maxParticlesPerEmitter = maxParticles;
            this.mask = mask;

            SetDefaultImageName(defaultSpriteRef);
            dummy = CreateParticle(this);
        }

        public ParticleSystem(LTexture defaultSprite, int maxParticles)
        {
            this.maxParticlesPerEmitter = maxParticles;

            sprite = defaultSprite;
            dummy = CreateParticle(this);
        }

        public void SetDefaultImageName(string refs)
        {
            defaultImageName = refs;
            sprite = null;
        }

        public int GetBlendingMode()
        {
            return blendingMode;
        }

        protected internal static Particle CreateParticle(ParticleSystem system)
        {
            return new Particle(system);
        }

        public void SetBlendingMode(int mode)
        {
            this.blendingMode = mode;
        }

        public int GetEmitterCount()
        {
            return emitters.Count;
        }

        public ParticleEmitter GetEmitter(int index)
        {
            return (ParticleEmitter)emitters[index];
        }

        public void AddEmitter(ParticleEmitter emitter)
        {
            emitters.Add(emitter);

            ParticlePool pool = new ParticlePool(this, maxParticlesPerEmitter);
            CollectionUtils.Put(particlesByEmitter, emitter, pool);
        }

        public void RemoveEmitter(ParticleEmitter emitter)
        {
            emitters.Remove(emitter);
            particlesByEmitter.Remove(emitter);
        }

        public void RemoveAllEmitters()
        {
            for (int i = 0; i < emitters.Count; i++)
            {
                RemoveEmitter(emitters[i]);
                i--;
            }
        }

        public float GetPositionX()
        {
            return x;
        }

        public float GetPositionY()
        {
            return y;
        }

        public void SetPosition(float x, float y)
        {
            this.x = x;
            this.y = y;
        }

        public void Render(GLEx g)
        {
            Render(g, x, y);
        }

        public void Render(GLEx g, float x, float y)
        {

            if (!visible)
            {
                return;
            }

            if ((sprite == null) && (defaultImageName != null))
            {
                LoadSystemParticleImage();
            }


            g.Translate(x, y);

            if (blendingMode == BLEND_ADDITIVE)
            {
                //GLEx.self.setBlendMode(GL.MODE_ALPHA_ONE);
            }
            if (UsePoints())
            {
                //GLEx.gl10.glEnable(GL.GL_POINT_SMOOTH);
                //g.glTex2DDisable();
            }

            for (int emitterIdx = 0; emitterIdx < emitters.Count; emitterIdx++)
            {

                ParticleEmitter emitter = emitters[emitterIdx];

                if (!emitter.IsEnabled())
                {
                    continue;
                }

                if (emitter.UseAdditive())
                {
                    //g.setBlendMode(GL.MODE_ALPHA_ONE);
                }

                ParticlePool pool = particlesByEmitter[emitter];
                LTexture image = emitter.GetImage();
                if (image == null)
                {
                    image = this.sprite;
                }

                if (!emitter.IsOriented() && !emitter.UsePoints(this))
                {
                    image.GLBegin();
                }

                for (int i = 0; i < pool.particles.Length; i++)
                {
                    if (pool.particles[i].InUse())
                    {
                        pool.particles[i].Render();
                    }
                }

                if (!emitter.IsOriented() && !emitter.UsePoints(this))
                {
                    image.GLEnd();
                }

                if (emitter.UseAdditive())
                {
                    //g.setBlendMode(GL.MODE_NORMAL);
                }
            }

            if (UsePoints())
            {
                //GLEx.gl10.glDisable(GL.GL_POINT_SMOOTH);
            }
            if (blendingMode == BLEND_ADDITIVE)
            {
                //g.setBlendMode(GL.MODE_NORMAL);
            }

            g.ResetColor();
            g.Translate(-x, -y);

        }

        private void LoadSystemParticleImage()
        {

            try
            {
                if (mask != null)
                {
                    sprite = TextureUtils.FilterColor(defaultImageName, mask);
                }
                else
                {
                    sprite = new LTexture(defaultImageName);
                }
            }
            catch (Exception e)
            {
                Log.Exception(e);
                defaultImageName = null;
            }

        }

        public void Update(long delta)
        {
            if ((sprite == null) && (defaultImageName != null))
            {
                LoadSystemParticleImage();
            }

            removeMe.Clear();
            List<ParticleEmitter> emitters = new List<ParticleEmitter>(
                    this.emitters);
            for (int i = 0; i < emitters.Count; i++)
            {
                ParticleEmitter emitter = emitters[i];
                if (emitter.IsEnabled())
                {
                    emitter.Update(this, delta);
                    if (removeCompletedEmitters)
                    {
                        if (emitter.Completed())
                        {
                            removeMe.Add(emitter);
                            particlesByEmitter.Remove(emitter);
                        }
                    }
                }
            }
            CollectionUtils.RemoveAll(emitters, removeMe);

            pCount = 0;

            if (particlesByEmitter.Count != 0)
            {
                IEnumerator<ParticleEmitter> it = particlesByEmitter.Keys
                        .GetEnumerator();
                while (it.MoveNext())
                {
                    ParticleEmitter emitter = it.Current;
                    if (emitter.IsEnabled())
                    {
                        ParticlePool pool = particlesByEmitter[emitter];
                        for (int i = 0; i < pool.particles.Length; i++)
                        {
                            if (pool.particles[i].life > 0)
                            {
                                pool.particles[i].Update(delta);
                                pCount++;
                            }
                        }
                    }
                }
            }
        }

        public int GetParticleCount()
        {
            return pCount;
        }

        public Particle GetNewParticle(ParticleEmitter emitter, float life)
        {
            ParticlePool pool = particlesByEmitter[emitter];
            List<Particle> available = pool.available;
            if (available.Count > 0)
            {
                Particle p = (Particle)CollectionUtils.RemoveAt(available,available.Count - 1);
                p.Init(emitter, life);
                p.SetImage(sprite);

                return p;
            }

            Log.DebugWrite("Ran out of particles (increase the limit)!");
            return dummy;
        }

        public void Release(Particle particle)
        {
            if (particle != dummy)
            {
                ParticlePool pool = (ParticlePool)CollectionUtils.Get(particlesByEmitter, particle
                        .GetEmitter());
                pool.available.Add(particle);
            }
        }

        public void ReleaseAll(ParticleEmitter emitter)
        {
            if (particlesByEmitter.Count != 0)
            {
                IEnumerator<ParticlePool> it = particlesByEmitter.Values.GetEnumerator();
                while (it.MoveNext())
                {
                    ParticlePool pool = it.Current;
                    for (int i = 0; i < pool.particles.Length; i++)
                    {
                        if (pool.particles[i].InUse())
                        {
                            if (pool.particles[i].GetEmitter() == emitter)
                            {
                                pool.particles[i].SetLife(-1);
                                Release(pool.particles[i]);
                            }
                        }
                    }
                }
            }
        }

        public void MoveAll(ParticleEmitter emitter, float x, float y)
        {
            ParticlePool pool = particlesByEmitter[emitter];
            for (int i = 0; i < pool.particles.Length; i++)
            {
                if (pool.particles[i].InUse())
                {
                    pool.particles[i].Move(x, y);
                }
            }
        }
    }
}
