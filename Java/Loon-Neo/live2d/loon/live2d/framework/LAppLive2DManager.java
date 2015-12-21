package loon.live2d.framework;

import loon.live2d.Live2D;
import loon.live2d.util.UtSystem;

public class LAppLive2DManager {

	private LAppModel model;

	private String modelPath;

	private long time;
	private float timePerBeat;
	private long prevTime;

	public LAppLive2DManager() {
		Live2D.init();
		Live2DFramework.setPlatformManager(new PlatformManager());

		model = new LAppModel();
		modelPath = null;

		time = 0;
		prevTime = 0;
	}

	public LAppModel loadModel(String path) {

		releaseModel();
		modelPath = path;
		model.load(modelPath);
		model.feedIn();

		return model;
	}

	public void releaseModel() {
		model.release();
	}

	public void danceSetBPM(float bpm) {
		if (bpm == -1) {
			timePerBeat = 1000;
		} else {
			timePerBeat = bpm * 1000 / 60;
		}
	}

	public void danceResetBPM(float bpm) {

		timePerBeat = 1000;

	}

	public void update() {

		UtSystem.updateUserTimeMSec();
		long currTime = UtSystem.getUserTimeMSec();

		if (timePerBeat != 0) {
			float deltaTime = (currTime - prevTime); 
			float ratio = deltaTime / 1000;
			long warpedTime = (long) (ratio * timePerBeat);
			time += warpedTime;
			prevTime = currTime;
			UtSystem.setUserTimeMSec(time);
		}

	}

	public LAppModel getModel() {
		return model;
	}

	public void danceStop() {
		model.danceStop();
	}

	public void danceStart() {
		model.danceStart();
	}

	public boolean tapEvent(float x, float y) {

		model.switchDance();

		return true;
	}

	public void setAccel(float x, float y, float z) {
		model.setAccel(x, y, z);
	}

	public void setDrag(float x, float y) {
		model.setDrag(x, y);
	}

}
