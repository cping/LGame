using Microsoft.Xna.Framework.Content;
using System;
using Microsoft.Xna.Framework.Audio;
using Loon.Core;
using Loon.Utils;
namespace Loon.Media
{
    public class XNAMediaPlayer
    {
        public static IServiceProvider services;
        private SoundEffectInstance sound;
        private SoundEffect soundEffect;

        public XNAMediaPlayer(string resName)
        {
            try
            {
                if (resName.IndexOf("/") == -1 && (FileUtils.GetExtension(resName).Length == 0))
                {
                    resName = "Content/" + resName;
                }
                this.soundEffect = LSystem.screenActivity.GameRes.Load<SoundEffect>(resName);
                this.sound = this.soundEffect.CreateInstance();
            }
            catch (Exception)
            {
            }
        }

        public int GetCurrentPosition()
        {
            return 0;
        }

        public bool IsPlaying()
        {
            if (this.sound == null)
            {
                return false;
            }
            return (this.sound.State == SoundState.Playing);
        }

        public void Pause()
        {
            if (this.sound != null)
            {
                this.sound.Pause();
            }
        }

        public void Release()
        {
            if (this.sound != null)
            {
                this.sound.Dispose();
                this.sound = null;
            }
            if (this.soundEffect != null)
            {
                this.soundEffect.Dispose();
                this.soundEffect = null;
            }
        }

        public void SeekTo(int pos)
        {
        }

        public void SetLooping(bool loop)
        {
            if (this.sound != null)
            {
                this.sound.IsLooped = loop;
            }
        }

        public void SetVolume(float leftVolume, float rightVolume)
        {
            if (this.sound != null)
            {
                this.sound.Volume = (leftVolume + rightVolume) * 0.5f;
            }
        }

        public void Start()
        {
            if (this.sound != null)
            {
                this.sound.Play();
            }
        }

        public void Stop()
        {
            if (this.sound != null)
            {
                this.sound.Stop();
            }
        }
    }
}
