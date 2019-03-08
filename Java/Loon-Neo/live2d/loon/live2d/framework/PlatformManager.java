package loon.live2d.framework;

import loon.BaseIO;
import loon.LSystem;
import loon.LTexture;
import loon.LTextures;
import loon.live2d.ALive2DModel;
import loon.live2d.Live2DModelImpl;

public class PlatformManager implements IPlatformManager {

	public byte[] loadBytes(String path) {
		return BaseIO.loadBytes(path);
	}

	public String loadString(String path) {
		return BaseIO.loadText(path);
	}

	public void loadTexture(ALive2DModel model, int no, String path) {
		LTexture texture = LTextures.loadTexture(path);
		((Live2DModelImpl) model).setTexture(no, texture);
	}

	public ALive2DModel loadLive2DModel(String path) {
		ALive2DModel model = Live2DModelImpl.loadModel(path);
		return model;
	}

	@Override
	public void log(String txt) {
		LSystem.debug(txt);
	}
}
