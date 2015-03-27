package loon.media;

import java.io.IOException;

import loon.LSystem;

public class SoundOpenAlBuffer {

	private String name;
	private int bufferId;

	private SoundOpenAlBuffer(String name, String path) {
		this.name = name;
		this.bufferId = OpenAlBridge.addBuffer(path);
	}

	public static SoundOpenAlBuffer createFrom(String name) throws IOException {
		String path = LSystem.getResourcePath(name);
		return new SoundOpenAlBuffer(name, path);
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return bufferId;
	}

	public void release() {
		OpenAlBridge.releaseBuffer(bufferId);
	}

	public String toString() {
		return name + " " + bufferId;
	}
}
