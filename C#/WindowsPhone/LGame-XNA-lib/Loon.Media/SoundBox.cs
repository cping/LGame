using Loon.Utils;
using Loon.Core.Graphics;
using Loon.Core;
namespace Loon.Media
{

    public abstract class SoundBox
    {

        private System.Collections.Generic.Dictionary<string, Sound> sounds = new System.Collections.Generic.Dictionary<string, Sound>(
                CollectionUtils.INITIAL_CAPACITY);

        public void PlaySound(string path)
        {
            PlaySound(path, false);
        }

        public void PlaySound(string path, bool loop)
        {
          
            Sound sound = (Sound)CollectionUtils.Get(sounds, path);
            if (sound == null)
            {
                sound = Assets.GetSound(path);
                CollectionUtils.Put(sounds, path, sound);
            }
            else
            {
                sound.Stop();
            }
            sound.SetLooping(loop);
            sound.Play();
        }

        public void Volume(string path, float volume)
        {
            Sound sound = (Sound)CollectionUtils.Get(sounds, path);
            if (sound != null)
            {
                sound.SetVolume(volume);
            }
        }

        public void StopSound(string path)
        {
            Sound sound = (Sound)CollectionUtils.Get(sounds, path);
            if (sound != null)
            {
                sound.Stop();
            }
        }

        public void StopSound()
        {
            foreach (Sound s in sounds.Values)
            {
                if (s != null)
                {
                    s.Stop();
                }
            }
        }

        public void Release()
        {
            foreach (Sound s in sounds.Values)
            {
                if (s != null)
                {
                    s.Release();
                }
            }
            sounds.Clear();
        }
    }

}
