namespace Loon.Media
{
    using Microsoft.Xna.Framework.Audio;
    using Microsoft.Xna.Framework.Media;
    using Loon.Utils;
    using Loon.Core;
    using System;

    public class SoundPlayer
    {
        public static IServiceProvider services;
        private SoundEffectInstance sound;
        private SoundEffect soundEffect;

        public SoundPlayer(string resName)
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

    public class SongPlayer : LRelease
    {
        public Song song = null;

        public float Volume = 0.0f;
        public float Pitch = 0.0f;
        public float Pan = 0.0f;

        private bool isLoaded;

        private string fileName;

        private bool _loop;

        public SongPlayer(string resName)
        {
            if (resName.IndexOf("/") == -1 && (FileUtils.GetExtension(resName).Length == 0))
            {
                resName = "Content/" + resName;
            }
            this.fileName = resName;
        }

        public bool IsLoaded()
        {
            return isLoaded;
        }

        internal void LoadSong(string resName)
        {
            try
            {
                if (resName.IndexOf("/") == -1 && (FileUtils.GetExtension(resName).Length == 0))
                {
                    resName = "Content/" + resName;
                }
                song = LSystem.screenActivity.GameRes.Load<Song>(resName);
                isLoaded = true;
            }
            catch (Exception ex)
            {
                Loon.Utils.Debugging.Log.Exception(ex);
            }
        }

        private void VaildLoad()
        {
            if (song == null || !isLoaded)
            {
                LoadSong(fileName);
            }
        }

        public void Loop(bool loop)
        {
            this._loop = loop;
        }

        public bool IsPlaying()
        {
            return MediaPlayer.State == MediaState.Playing;
        }

        public void PlaySong()
        {
            VaildLoad();
            if (MediaPlayer.State != MediaState.Playing)
            {
                MediaPlayer.Play(song);
                MediaPlayer.IsRepeating = _loop;
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

        public AudioEffect(string resName)
        {
            this.fileName = resName;
        }

        public bool IsLoaded()
        {
            return isLoaded;
        }

        internal void LoadAudioEffect(string resName)
        {
            string ext = FileUtils.GetExtension(resName);
            if (resName.IndexOf("/") == -1 && (ext.Length == 0))
            {
                resName = "Content/" + resName;
            }
            soundEffect = null;
            if (ext.Length == 0)
            {
                soundEffect = LSystem.screenActivity.GameRes.Load<SoundEffect>(resName);
            }
            else
            {
                soundEffect = SoundEffect.FromStream(Loon.Core.Resource.Resources.OpenStream(resName));
            }
            soundEffectInstance = soundEffect.CreateInstance();
            soundEffectInstance.IsLooped = _loop;
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
                if (soundEffectInstance.IsLooped != _loop)
                {
                    soundEffectInstance.IsLooped = _loop;
                }
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

        public bool IsPlaying()
        {
           return soundEffectInstance.State == SoundState.Playing;
        }

        private bool _loop;

        public void Loop(bool loop)
        {
            VaildLoad();
            this._loop = loop;
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

        public static SongPlayer NewSongPlayer(string path)
        {
            return new SongPlayer(path);
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
