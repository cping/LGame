namespace Loon.Media
{
    using System.Collections.Generic;
    using Loon.Utils.Collection;

    //和Android版一样，只能加载指定文件夹下文件，差异在于WP中文件夹是Content。
    public class AssetsSoundManager
    {

        private static AssetsSoundManager assetsSoundManager;

        private ArrayMap sounds = new ArrayMap(50);

        private int clipCount = 0;

        private bool paused;

        private AudioEffect asound;

        public static AssetsSoundManager GetInstance()
        {
            if (assetsSoundManager == null)
            {
                return (assetsSoundManager = new AssetsSoundManager());
            }
            return assetsSoundManager;
        }

        private AssetsSoundManager()
        {
        }

        public void PlaySound(string name, int vol)
        {
            if (paused)
            {
                return;
            }
            if (sounds.ContainsKey(name))
            {
                AudioEffect ass = ((AudioEffect)sounds.Get(name));
                ass.AudioEffectVolume(vol);
                ass.PlayAudioEffect();
            }
            else
            {
                if (clipCount > 50)
                {
                    int idx = sounds.Size() - 1;
                    string k = (string)sounds.GetKey(idx);
                    AudioEffect clip = (AudioEffect)sounds.Remove(k);
                    clip.StopAudioEffect();
                    clip = null;
                    clipCount--;
                }
                asound = new AudioEffect(name);
                asound.AudioEffectVolume(vol);
                asound.PlayAudioEffect();
                sounds.Put(name, asound);
                clipCount++;
            }
        }

        public void StopSound(int index)
        {
            AudioEffect sound = (AudioEffect)sounds.Get(index);
            if (sound != null)
            {
                sound.StopAudioEffect();
            }
        }

        public void PlaySound(string name, bool loop)
        {
            if (paused)
            {
                return;
            }
            if (sounds.ContainsKey(name))
            {
                AudioEffect ass = ((AudioEffect)sounds.Get(name));
                ass.Loop(loop);
                ass.PlayAudioEffect();
            }
            else
            {
                if (clipCount > 50)
                {
                    int idx = sounds.Size() - 1;
                    string k = (string)sounds.GetKey(idx);
                    AudioEffect clip = (AudioEffect)sounds.Remove(k);
                    clip.StopAudioEffect();
                    clip = null;
                    clipCount--;
                }
                asound = new AudioEffect(name);
                asound.Loop(loop);
                asound.PlayAudioEffect();
                sounds.Put(name, asound);
                clipCount++;
            }
        }

        public void StopSoundAll()
        {
            if (sounds != null)
            {
                List<ArrayMap.Entry> list = sounds.ToList();
                for (int i = 0; i < list.Count; i++)
                {
                    ArrayMap.Entry sound = list[i];
                    if (sound != null)
                    {
                        AudioEffect ass = (AudioEffect)sound.GetValue();
                        if (ass != null)
                        {
                            ass.StopAudioEffect();
                        }
                    }
                }
            }
        }

        public void ResetSound()
        {
            if (asound != null)
            {
                asound.ResumeAudioEffect();
            }
        }

        public void StopSound()
        {
            if (asound != null)
            {
                asound.StopAudioEffect();
            }
        }

        public void Release()
        {
            if (asound != null)
            {
                asound.StopAudioEffect();
            }
        }

        public void SetSoundVolume(int vol)
        {
            if (asound != null)
            {
                asound.AudioEffectVolume(vol);
            }
        }

        public void Pause(bool pause)
        {
            if (asound != null && pause)
            {
                asound.PauseAudioEffect();
            }
            else if (asound != null)
            {
                asound.ResumeAudioEffect();
            }
            this.paused = pause;
        }
    }
}
