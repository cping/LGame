package loon.live2d.framework;

import loon.live2d.ALive2DModel;

public interface IPlatformManager {
	
	public byte[] loadBytes(String path);
	
	public String loadString(String path);
	
	public ALive2DModel loadLive2DModel(String path);
	
	public void loadTexture(ALive2DModel model, int no, String path);
	
	public void log(String txt);
	
}
