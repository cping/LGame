namespace Loon.Core.Graphics.OpenGL.Particle
{
    public interface ParticleEmitter
    {
         void Update(ParticleSystem system, long delta);

         bool Completed();

         void WrapUp();

         void UpdateParticle(Particle particle, long delta);

         bool IsEnabled();

         void SetEnabled(bool enabled);

         bool UseAdditive();

         LTexture GetImage();

         bool IsOriented();

         bool UsePoints(ParticleSystem system);

         void ResetState();
    }
}
