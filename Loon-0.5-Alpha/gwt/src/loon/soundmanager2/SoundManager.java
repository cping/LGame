package loon.soundmanager2;

public class SoundManager {
	
	public interface SoundManagerCallback {
		public void onready ();
		public void ontimeout (String status, String errorType);
	}

	public static final native SoundManager getInstance () /*-{
		return $wnd.soundManager;
	}-*/;

	public static native String getVersion () /*-{
		return $wnd.soundManager.version;
	}-*/;

	public static native String getUrl () /*-{
		return $wnd.soundManager.url;
	}-*/;

	public static native void setUrl (String url) /*-{
		$wnd.soundManager.url = url;
	}-*/;

	public static native void setDebugMode (boolean debug) /*-{
		$wnd.soundManager.debugMode = debug;
	}-*/;

	public static native boolean getDebugMode () /*-{
		return $wnd.soundManager.debugMode;
	}-*/;

	public static native void setFlashVersion (int version) /*-{
		$wnd.soundManager.flashVersion = version;
	}-*/;

	public static native int getFlashVersion () /*-{
		return $wnd.soundManager.flashVersion;
	}-*/;

	public static native SMSound createSound (String url) /*-{
		var jsSound = $wnd.soundManager.createSound({url: url});
		return @loon.soundmanager2.SMSound::new(Lcom/google/gwt/core/client/JavaScriptObject;)(jsSound);
	}-*/;

	public static native void reboot () /*-{
		$wnd.soundManager.reboot();
	}-*/;

	public static native boolean ok () /*-{
		return $wnd.soundManager.ok();
	}-*/;

	public static native void init (String moduleBaseURL, int flashVersion, boolean preferFlash, SoundManagerCallback callback) /*-{
		$wnd.soundManager = new $wnd.SoundManager();
		$wnd.soundManager.setup({
			url: moduleBaseURL,
			flashVersion: flashVersion,
			preferFlash: preferFlash,
			onready: function() { 
				callback.@loon.soundmanager2.SoundManager.SoundManagerCallback::onready()();
			},
			ontimeout: function(status) {
				callback.@loon.soundmanager2.SoundManager.SoundManagerCallback::ontimeout(Ljava/lang/String;Ljava/lang/String;)(status.success, (typeof status.error === 'undefined') ? '' : status.error.type);
			}
					
		});
		$wnd.soundManager.beginDelayedInit();
	}-*/;

}