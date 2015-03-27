package loon.media;

public class SoundOpenAlSource {

	private int		sourceId;
	private SoundOpenAlBuffer	buffer;

	public SoundOpenAlSource(SoundOpenAlBuffer buffer) {
		this.buffer = buffer;
		this.sourceId = OpenAlBridge.addSource(buffer.getId());
	}

	public void setPosition(float x, float y, float z) {
		OpenAlBridge.setPosition(sourceId, x, y, z);
	}
	
	public void setPitch(float pitch) {
		OpenAlBridge.setPitch(sourceId, pitch);
	}
	
	public void setGain(float gain) {
		OpenAlBridge.setGain(sourceId, gain);
	}
	
	public void setRolloffFactor(float rollOff) {
		OpenAlBridge.setRolloffFactor(sourceId, rollOff);
	}
	
	public void play(boolean loop) {
		OpenAlBridge.play(sourceId, loop);
	}
	
	public void stop() {
		OpenAlBridge.stop(sourceId);
	}

	public void release() {
		OpenAlBridge.releaseSource(sourceId);
	}

	public String toString() {
		return "source " + sourceId + " playing " + buffer;
	}

}
