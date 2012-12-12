namespace Loon.Media
{
    using Microsoft.Xna.Framework.Audio;
    using Microsoft.Xna.Framework.Media;
    using Loon.Utils;
    using Loon.Core;

    public class AudioPlayer : LRelease
    {
        public Song song = null;

        public float Volume = 0.0f;
        public float Pitch = 0.0f;
        public float Pan = 0.0f;

        private bool isLoaded;

        private string fileName;

        public AudioPlayer(string file)
        {
            this.fileName = file;
        }

        public bool IsLoaded()
        {
            return isLoaded;
        }

        internal void LoadSong(string resName)
        {
            if (resName.IndexOf("/") == -1 && (FileUtils.GetExtension(resName).Length == 0))
            {
                resName = "Content/" + resName;
            }
            song = LSystem.screenActivity.GameRes.Load<Song>(resName);
            isLoaded = true;
        }

        private void VaildLoad()
        {
            if (song == null || !isLoaded)
            {
                LoadSong(fileName);
            }
        }

        public void PlaySong()
        {
            VaildLoad();
            if (MediaPlayer.State != MediaState.Playing)
            {
                MediaPlayer.Play(song);
            }
        }

        public void PauseSong()
        {
            VaildLoad();
            if (MediaPlayer.State == MediaState.Playing)
            {
                MediaPlayer.Pause();
            }
        }

        public void ResumeSong()
        {
            VaildLoad();
            if (MediaPlayer.State == MediaState.Paused)
            {
                MediaPlayer.Resume();
            }
        }

        public void StopSong()
        {
            VaildLoad();
            if (MediaPlayer.State == MediaState.Playing || MediaPlayer.State == MediaState.Paused)
            {
                MediaPlayer.Stop();
            }
        }

        public void SongVolume(float volume)
        {
            volume = MathUtils.Clamp(volume, 0.0f, 1.0f);
            Volume = volume * MediaSound.MasterVolume;
        }

        public void Dispose()
        {
            if (song != null)
            {
                try
                {
                    song.Dispose();
                    song = null;
                }
                catch (System.Exception)
                {
                }
            }
        }
    }

    public class AudioEffect : LRelease
    {
        protected SoundEffect soundEffect = null;
        protected SoundEffectInstance soundEffectInstance = null;

        public float Volume = 0.0f;
        public float Pitch = 0.0f;
        public float Pan = 0.0f;

        private bool isLoaded;

        private string fileName;

        public AudioEffect(string file)
        {
            this.fileName = file;
        }

        public bool IsLoaded()
        {
            return isLoaded;
        }

        internal void LoadAudioEffect(string resName)
        {
            if (resName.IndexOf("/") == -1 && (FileUtils.GetExtension(resName).Length == 0))
            {
                resName = "Content/" + resName;
            }
            soundEffect = LSystem.screenActivity.GameRes.Load<SoundEffect>(resName);
            soundEffectInstance = soundEffect.CreateInstance();
            isLoaded = true;
        }

        private void VaildLoad()
        {
            if (soundEffect == null || !isLoaded)
            {
                LoadAudioEffect(fileName);
            }
        }

        public void PlayAudioEffect()
        {
            VaildLoad();
            if (soundEffectInstance.State != SoundState.Playing)
            {
                soundEffectInstance.Play();
            }
        }

        public void PauseAudioEffect()
        {
            VaildLoad();
            if (soundEffectInstance.State == SoundState.Playing)
            {
                soundEffectInstance.Pause();
            }
        }

        public void ResumeAudioEffect()
        {
            VaildLoad();
            if (soundEffectInstance.State == SoundState.Paused)
            {
                soundEffectInstance.Resume();
            }
        }

        public void Loop(bool loop)
        {
            VaildLoad();
            soundEffectInstance.IsLooped = loop;
        }

        public void StopAudioEffect()
        {
            VaildLoad();
            if (soundEffectInstance.State == SoundState.Playing || soundEffectInstance.State == SoundState.Paused)
            {
                soundEffectInstance.Stop();
            }
        }

        public void AudioEffectVolume(float volume)
        {
            volume = MathUtils.Clamp(volume, 0.0f, 1.0f);
            Volume = volume * MediaSound.MasterVolume;
        }

        public void PanAudioEffect(float pan)
        {
            pan = MathUtils.Clamp(pan, 0.0f, 1.0f);
            soundEffectInstance.Pan = pan;
        }

        public void PitchAudioEffect(float pitch)
        {
            pitch = MathUtils.Clamp(pitch, 0.0f, 1.0f);
            soundEffectInstance.Pitch = pitch;
        }

        public void Dispose()
        {
            try
            {
                if (soundEffectInstance != null)
                {
                    soundEffectInstance.Dispose();
                    soundEffectInstance = null;
                }
                if (soundEffect != null)
                {
                    soundEffect.Dispose();
                    soundEffect = null;
                }
            }
            catch (System.Exception)
            {
            }
        }
    }

    public static class MediaSound
    {

        private static readonly System.Collections.Generic.List<SoundEffectInstance> soundEffectInstances = new System.Collections.Generic.List<SoundEffectInstance>(500);

        private static int updateCount;

        private static float volume = 1.0f;

        public static AudioEffect NewAudioEffect(string path)
        {
            return new AudioEffect(path);
        }

        public static AudioPlayer NewAudioPlay(string path)
        {
            return new AudioPlayer(path);
        }

        public static float MasterVolume
        {
            get { return volume; }
            set
            {
                volume = MathUtils.Clamp(value, 0.0f, 1.0f);
            }
        }

        public static SoundEffectInstance Add(SoundEffectInstance soundEffectInstance)
        {
            soundEffectInstances.Add(soundEffectInstance);

            return soundEffectInstance;
        }

        public static void Update()
        {
            if (updateCount == 1024)
            {
                foreach (SoundEffectInstance s in soundEffectInstances)
                {
                    if (s.State != SoundState.Playing)
                    {
                        soundEffectInstances.Remove(s);
                    }
                }
                updateCount = 0;
            }
            updateCount++;
        }

        public static void Play(SoundEffect soundEffect)
        {
            if (soundEffect != null)
            {
                soundEffect.Play();
            }
        }

        public static void Stop(SoundEffect soundEffect)
        {
            if (soundEffect != null)
            {
                soundEffect.Dispose();
            }
        }
    }
}
