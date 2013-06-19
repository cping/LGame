using Loon.Core;
using System;
namespace Loon.Media
{
    public  class Error : Silence
    {
        private Exception error;

        public Error(Exception error)
        {
            this.error = error;
        }


        public override void AddCallback(Callback<object> callback)
        {
            callback.OnFailure(error);
        }
    }
    public  class Silence : Sound
    {

        public virtual bool Prepare()
        {
            return false;
        }


        public virtual bool Play()
        {
            return false;
        }


        public virtual void Stop()
        {
        }


        public virtual void SetLooping(bool looping)
        {
        }


        public virtual float Volume()
        {
            return 0;
        }


        public virtual void SetVolume(float volume)
        {
        }


        public virtual bool IsPlaying()
        {
            return false;
        }


        public virtual void Release()
        {
        }


        public virtual void AddCallback(Callback<object> callback)
        {
            callback.OnSuccess(this);
        }
    }
    public interface Sound
    {

        bool Prepare();

        bool Play();

        void Stop();

        void SetLooping(bool looping);

        float Volume();

        void SetVolume(float volume);

        bool IsPlaying();

        void Release();

        void AddCallback(Callback<object> callback);
    }

}
