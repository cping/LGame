/****************************************************************************
 * OpenAl
 ****************************************************************************/
package loon.media;

public class OpenAlBridge {
	
	public static final int SUCCESS = 1;
	public static final int ERROR = 0;

	static native int init();

	static native int close();

	static native int addBuffer(String filename);

	static native int releaseBuffer(int bufferId);

	static native int addSource(int bufferId);

	static native int releaseSource(int sourceId);

	static native void setPosition(int sourceId, float x, float y, float z);

	static native void setPitch(int sourceId, float pitch);

	static native void setGain(int sourceId, float gain);

	static native void setRolloffFactor(int sourceId, float rollOff);

	static native int play(int sourceId, boolean loop);

	static native int stop(int sourceId);

	static native int setListenerPos(float x, float y, float z);

	static native int setListenerOrientation(float xAt, float yAt, float zAt);

	public static String str(int retVal) {
		if (retVal == SUCCESS) {
			return "SUCCESS";
		} else if (retVal == ERROR) {
			return "ERROR";
		}
		return "UNKNOWN";
	}


}
