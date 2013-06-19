
using System.Collections.Generic;
using Loon.Core;
using System;
using Loon.Utils;
namespace Loon.Media
{
 public abstract class SoundImpl : Sound {

     protected IList<Callback<object>> callbacks;
	protected Exception error;
	protected bool playing, looping;
	protected float volume = 1;
    protected object impl;

    public virtual void OnLoaded(object impl)
    {
		this.impl = impl;
        callbacks = CallbackList<object>.DispatchSuccessClear(callbacks, this);
		SetVolumeImpl(volume);
		SetLoopingImpl(looping);
		if (playing) {
			PlayImpl();
		}
	}

    public virtual void OnLoadError(Exception error)
    {
		this.error = error;
        callbacks = CallbackList<object>.DispatchSuccessClear(callbacks, error);
	}


    public virtual bool Prepare()
    {
		return (impl != null) ? PrepareImpl() : false;
	}


    public virtual bool IsPlaying()
    {
		return (impl != null) ? PlayingImpl() : playing;
	}


    public virtual bool Play()
    {
		this.playing = true;
		if (impl != null) {
			return PlayImpl();
		} else {
			return false;
		}
	}


    public virtual void Stop()
    {
		this.playing = false;
		if (impl != null) {
			StopImpl();
		}
	}


    public virtual void SetLooping(bool looping)
    {
		this.looping = looping;
		if (impl != null) {
			SetLoopingImpl(looping);
		}
	}


    public virtual float Volume()
    {
		return volume;
	}


    public virtual void SetVolume(float volume)
    {
		this.volume = MathUtils.Clamp(volume, 0, 1);
		if (impl != null) {
			SetVolumeImpl(this.volume);
		}
	}


    public virtual void Release()
    {
		if (impl != null) {
			ReleaseImpl();
            impl = null;
		}
	}


    public virtual void AddCallback(Callback<object> callback)
    {
		if (impl != null) {
			callback.OnSuccess(this);
		} else if (error != null) {
			callback.OnFailure(error);
		} else {
            callbacks = CallbackList<object>.CreateAdd(callbacks, callback);
		}
	}

	protected virtual bool PrepareImpl() {
		return false;
	}

    protected virtual bool PlayingImpl()
    {
		return playing;
	}

	protected abstract bool PlayImpl();

	protected abstract void StopImpl();

	protected abstract void SetLoopingImpl(bool looping);

	protected abstract void SetVolumeImpl(float volume);

	protected abstract void ReleaseImpl();
}

}
