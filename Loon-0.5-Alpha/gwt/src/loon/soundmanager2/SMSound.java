package loon.soundmanager2;

import com.google.gwt.core.client.JavaScriptObject;

public class SMSound {
	
	public interface SMSoundCallback {
		public void onfinish ();
	}
	
	public static final int STOPPED = 0;
	public static final int PLAYING = 1;
	
	private JavaScriptObject jsSound;
	
	protected SMSound (JavaScriptObject jsSound) {
		this.jsSound = jsSound;
	}

	public native final void destruct () /*-{
		this.@loon.soundmanager2.SMSound::jsSound.destruct();
	}-*/;

	
	public native final int getPosition () /*-{
		return this.@loon.soundmanager2.SMSound::jsSound.position;
	}-*/;
	

	public native final void setPosition (int position) /*-{
		this.@loon.soundmanager2.SMSound::jsSound.setPosition(position);
	}-*/;
	
	
	public native final void pause () /*-{
		this.@loon.soundmanager2.SMSound::jsSound.pause();
	}-*/;
	
	public native final void play (SMSoundOptions options) /*-{
		this.@loon.soundmanager2.SMSound::jsSound.play(
			{
				volume: options.@loon.soundmanager2.SMSoundOptions::volume,
				pan: options.@loon.soundmanager2.SMSoundOptions::pan,
				loops: options.@loon.soundmanager2.SMSoundOptions::loops,
				from: options.@loon.soundmanager2.SMSoundOptions::from,
				onfinish: function() {
					var callback = options.@loon.soundmanager2.SMSoundOptions::callback;
					if(callback != null) {
						callback.@loon.soundmanager2.SMSound.SMSoundCallback::onfinish()();
					}
				}
			}
		);
	}-*/;
	
	public native final void play () /*-{
		this.@loon.soundmanager2.SMSound::jsSound.play();
	}-*/;

	public native final void resume () /*-{
		this.@loon.soundmanager2.SMSound::jsSound.resume();
	}-*/;

	public native final void stop () /*-{
		this.@loon.soundmanager2.SMSound::jsSound.stop();
	}-*/;

	public native final void setVolume (int volume) /*-{
		this.@loon.soundmanager2.SMSound::jsSound.setVolume(volume);
	}-*/;

	public native final int getVolume () /*-{
		return this.@loon.soundmanager2.SMSound::jsSound.volume;
	}-*/;

	public native final void setPan (int pan) /*-{
		this.@loon.soundmanager2.SMSound::jsSound.setPan(pan);
	}-*/;
	
	
	public native final int getPan () /*-{
		return this.@loon.soundmanager2.SMSound::jsSound.pan;
	}-*/;
	
	public native final int getPlayState () /*-{
		return this.@loon.soundmanager2.SMSound::jsSound.playState;
	}-*/;

	public native final boolean getPaused () /*-{
		return this.@loon.soundmanager2.SMSound::jsSound.paused;
	}-*/;
	
	public native final int getLoops () /*-{
		return this.@loon.soundmanager2.SMSound::jsSound.loops;
	}-*/;
}