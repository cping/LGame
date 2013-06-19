using Loon.Core.Event;
using Loon.Core;
using System;
using System.Collections.Generic;
using Loon.Utils;
namespace Loon.Media
{
    public class Audio
    {
        private List<XNASound> playing = null ;

        public Audio()
        {
            playing = new List<XNASound>(CollectionUtils.INITIAL_CAPACITY);
        }

        public SongPlayerSound CreateSong(string path)
        {
            SongPlayerSound player = new SongPlayerSound();
            try
            {
                SongPlayer song = MediaSound.NewSongPlayer(path);
                player.OnLoaded(song);
            }
            catch(Exception ex)
            {
                player.OnLoadError(ex);
            }
            return player;
        }

        public AudioEffectSound CreateAudio(string path)
        {
            AudioEffectSound player = new AudioEffectSound();
            try
            {
                AudioEffect effect = MediaSound.NewAudioEffect(path);
                player.OnLoaded(effect);
            }
            catch (Exception ex)
            {
                player.OnLoadError(ex);
            }
            return player;
        }

        public void OnPause()
        {
            foreach (XNASound sound in playing)
            {
                sound.OnPause();
            }
        }

        public void OnResume()
        {
            List<XNASound> wasPlaying = new List<XNASound>(
                    playing);
            playing.Clear();
            foreach (XNASound sound in wasPlaying)
            {
                sound.OnResume();
            }
        }

        public void OnDestroy()
        {
            foreach (XNASound sound in playing)
            {
                sound.Release();
            }
            playing.Clear();

        }

        void OnPlaying(XNASound sound)
        {
            CollectionUtils.Add(playing, sound);
        }

        void OnStopped(XNASound sound)
        {
            CollectionUtils.Remove(playing, sound);
        }
    }


    public class SongPlayerSound : SoundImpl
    {

        protected override bool PlayingImpl()
        {
            return ((SongPlayer)impl).IsPlaying();
        }

        protected override bool PlayImpl()
        {
            ((SongPlayer)impl).PlaySong();
            return ((SongPlayer)impl).IsPlaying();
        }

        protected override void StopImpl()
        {
            ((SongPlayer)impl).StopSong();
        }

        protected override void SetLoopingImpl(bool looping)
        {
            this.looping = looping;
            ((SongPlayer)impl).Loop(looping);
        }

        protected override void SetVolumeImpl(float volume)
        {
            ((SongPlayer)impl).SongVolume(volume);
        }

        protected override void ReleaseImpl()
        {
            ((SongPlayer)impl).Dispose();
        }
	};

    public class AudioEffectSound : SoundImpl
    {

        protected override bool PlayingImpl()
        {
            return ((AudioEffect)impl).IsPlaying();
        }

        protected override bool PlayImpl()
        {
            ((AudioEffect)impl).PlayAudioEffect();
            return ((AudioEffect)impl).IsPlaying();
        }

        protected override void StopImpl()
        {
            ((AudioEffect)impl).StopAudioEffect();
        }

        protected override void SetLoopingImpl(bool looping)
        {
            this.looping = looping;
            ((AudioEffect)impl).Loop(looping);
        }

        protected override void SetVolumeImpl(float volume)
        {
            ((AudioEffect)impl).AudioEffectVolume(volume);
        }

        protected override void ReleaseImpl()
        {
            ((AudioEffect)impl).Dispose();
        }
    };

    
}
