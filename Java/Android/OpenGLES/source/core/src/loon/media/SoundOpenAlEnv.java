package loon.media;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SoundOpenAlEnv {

	private List<SoundOpenAlBuffer> buffers;
	private List<SoundOpenAlSource> sources;

	private boolean released;

	private static boolean supportNative = false;

	private static SoundOpenAlEnv instance;

	static {
		try {
			System.loadLibrary("openal");
			System.loadLibrary("openalsupport");
			supportNative = true;
		} catch (Error e) {
			supportNative = false;
		}
	}

	public final static boolean isSupportNative() {
		return supportNative;
	}

	private SoundOpenAlEnv() {
		this.buffers = new ArrayList<SoundOpenAlBuffer>();
		this.sources = new ArrayList<SoundOpenAlSource>();
		if (supportNative) {
			OpenAlBridge.init();
		}
	}

	public static SoundOpenAlEnv getInstance() {
		if (instance == null) {
			instance = new SoundOpenAlEnv();
		}
		return instance;
	}

	public SoundOpenAlBuffer addBuffer(String name) throws IOException {
		SoundOpenAlBuffer buffer = SoundOpenAlBuffer.createFrom(name);
		this.buffers.add(buffer);
		return buffer;
	}

	public SoundOpenAlBuffer findBufferByName(String name) {
		for (SoundOpenAlBuffer buffer : buffers) {
			if (name.equals(buffer.getName())) {
				return buffer;
			}
		}
		return null;
	}

	public SoundOpenAlSource addSource(SoundOpenAlBuffer buffer) {
		SoundOpenAlSource source = new SoundOpenAlSource(buffer);
		this.sources.add(source);
		return source;
	}

	public void setListenerPos(float x, float y, float z) {
		OpenAlBridge.setListenerPos(x, y, z);
	}

	public void setListenerOrientation(double heading) {
		double zv = -Math.cos(Math.toRadians(heading));
		double xv = Math.sin(Math.toRadians(heading));
		this.setListenerOrientation((float) xv, 0, (float) zv);
	}

	public void setListenerOrientation(float xv, float yv, float zv) {
		OpenAlBridge.setListenerOrientation(xv, yv, zv);
	}

	public void playAllSources(boolean loop) {
		for (SoundOpenAlSource source : sources) {
			source.play(loop);
		}
	}

	public void stopAllSources() {
		for (SoundOpenAlSource source : sources) {
			source.stop();
		}
	}

	public synchronized void release() {
		if (!released) {
			for (SoundOpenAlSource source : sources) {
				source.stop();
			}
			for (SoundOpenAlSource source : sources) {
				source.release();
			}
			for (SoundOpenAlBuffer buffer : buffers) {
				buffer.release();
			}
			OpenAlBridge.close();
			released = true;
		}
	}

}
