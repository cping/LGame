package loon.live2d.framework;

import loon.LSystem;
import loon.Sound;
import loon.live2d.motion.AMotion;
import loon.live2d.util.ModelSetting;
import loon.live2d.util.ModelSettingJson;
import loon.live2d.util.UtSystem;
import loon.opengl.GLEx;
import loon.utils.ListMap;
import loon.utils.MathUtils;

public class LAppModel extends L2DBaseModel {

	private String modelName = "unkown";

	private ModelSetting modelSetting = null;

	public LAppModel() {
		super();
	}

	public String getModelName() {
		return this.modelName;
	}

	public void release() {
		if (live2DModel == null) {
			return;
		}
		live2DModel.deleteTextures();
	}

	private int danceNumber;
	private boolean isDance = false;

	public void toggleDance() {
		isDance = !isDance;
	}

	public void danceStart() {
		isDance = true;

		mainMotionManager.setReservePriority(0);

		int max = modelSetting.getMotionNum(LAppDefine.MOTION_GROUP_DANCE);
		danceNumber = (int) (MathUtils.random() * max);

		startMotion(LAppDefine.MOTION_GROUP_DANCE, danceNumber,
				LAppDefine.PRIORITY_IDLE);
	}

	public void danceStop() {
		isDance = false;

		mainMotionManager.setReservePriority(0);
		startRandomMotion(LAppDefine.MOTION_GROUP_IDLE,
				LAppDefine.PRIORITY_IDLE);
	}

	static Object lock = new Object();

	public void startMotion(String name, int no, int priority) {
		String motionName = modelSetting.getMotionFile(name, no);

		if (motionName == null || motionName.equals("")) {
			return;
		}

		AMotion motion;

		synchronized (lock) {
			if (priority == LAppDefine.PRIORITY_FORCE) {
				mainMotionManager.setReservePriority(priority);
			} else if (!mainMotionManager.reserveMotion(priority)) {
				return;
			}

			String motionPath = modelHomeDir + motionName;
			motion = loadMotion(null, motionPath);

			if (motion == null) {
				mainMotionManager.setReservePriority(0);
				return;
			}
		}

		motion.setFadeIn(modelSetting.getMotionFadeIn(name, no));
		motion.setFadeOut(modelSetting.getMotionFadeOut(name, no));

		if (modelSetting.getMotionSound(name, no) == null) {
			synchronized (lock) {
				mainMotionManager.startMotionPrio(motion, priority);
			}
		} else {
			String soundName = modelSetting.getMotionSound(name, no);
			String soundPath = modelHomeDir + soundName;
			// playsound
			// startVoiceMotion( motion,player,priority);
			Sound sound = LSystem.base().assets().getSound(soundPath);
			sound.play();
		}
	}

	public void switchDance() {

		mainMotionManager.setReservePriority(0);

		int max = modelSetting.getMotionNum(LAppDefine.MOTION_GROUP_DANCE);
		if (danceNumber < max - 1) {
			danceNumber++;
		} else {
			danceNumber = 0;
		}
	}

	public void update() {
		if (live2DModel == null) {
			return;
		}

		long timeMSec = UtSystem.getUserTimeMSec() - startTimeMSec;
		double timeSec = timeMSec / 1000.0;
		double t = timeSec * 2 * MathUtils.PI;

		synchronized (lock) {
			if (mainMotionManager.isFinished()) {
				if (isDance) {
					startMotion(LAppDefine.MOTION_GROUP_DANCE, danceNumber,
							LAppDefine.PRIORITY_IDLE);
				} else {
					startRandomMotion(LAppDefine.MOTION_GROUP_IDLE,
							LAppDefine.PRIORITY_IDLE);
				}
			}

			// -----------------------------------------------------------------
			live2DModel.loadParam();

			boolean update = mainMotionManager.updateParam(live2DModel);
			eyeBlink.updateParam(live2DModel);
			live2DModel.saveParam();
			if (update) {

			}
			// -----------------------------------------------------------------
		}

		if (expressionManager != null)
			expressionManager.updateParam(live2DModel);

		live2DModel.addToParamFloat(L2DStandardID.PARAM_ANGLE_X, dragX * 30, 1);
		live2DModel.addToParamFloat(L2DStandardID.PARAM_ANGLE_Y, dragY * 30, 1);
		live2DModel.addToParamFloat(L2DStandardID.PARAM_ANGLE_Z,
				(dragX * dragY) * -30, 1);

		live2DModel.addToParamFloat(L2DStandardID.PARAM_BODY_ANGLE_X,
				dragX * 10, 1);

		live2DModel.addToParamFloat(L2DStandardID.PARAM_EYE_BALL_X, dragX, 1);
		live2DModel.addToParamFloat(L2DStandardID.PARAM_EYE_BALL_Y, dragY, 1);

		live2DModel.addToParamFloat(L2DStandardID.PARAM_ANGLE_X,
				(float) (15 * MathUtils.sin(t / 6.5345)), 0.5f);
		live2DModel.addToParamFloat(L2DStandardID.PARAM_ANGLE_Y,
				(float) (8 * MathUtils.sin(t / 3.5345)), 0.5f);
		live2DModel.addToParamFloat(L2DStandardID.PARAM_ANGLE_Z,
				(float) (10 * MathUtils.sin(t / 5.5345)), 0.5f);
		live2DModel.addToParamFloat(L2DStandardID.PARAM_BODY_ANGLE_X,
				(float) (4 * MathUtils.sin(t / 15.5345)), 0.5f);
		live2DModel.setParamFloat(L2DStandardID.PARAM_BREATH,
				(float) (0.5f + 0.5f * MathUtils.sin(t / 3.2345)), 1);

		live2DModel.addToParamFloat(L2DStandardID.PARAM_ANGLE_Z, 90 * accelX,
				0.5f);

		if (physics != null)
			physics.updateParam(live2DModel);

		if (lipSync) {
			live2DModel.setParamFloat(L2DStandardID.PARAM_MOUTH_OPEN_Y,
					lipSyncValue, 0.8f);
		}

		if (pose != null)
			pose.updateParam(live2DModel);

		live2DModel.update();
	}

	public void reset() {
	}

	public void startRandomMotion(String name, int priority) {
		int max = modelSetting.getMotionNum(name);
		int no = (int) (MathUtils.random() * max);

		startMotion(name, no, priority);
	}

	private float width, height;

	String modelHomeDir;

	public void load(String modelSettingPath) {
		updating = true;
		initialized = false;
		modelHomeDir = modelSettingPath.substring(0,
				modelSettingPath.lastIndexOf("/") + 1);

		this.modelSetting = new ModelSettingJson(modelSettingPath);

		if (modelSetting.getModelName() != null) {
			modelName = modelSetting.getModelName();
		}

		loadModelData(modelHomeDir + modelSetting.getModelFile());
		String[] texPaths = modelSetting.getTextureFiles();
		for (int i = 0; i < texPaths.length; i++) {
			loadTexture(i, modelHomeDir + texPaths[i]);
		}

		String[] expressionNames = modelSetting.getExpressionNames();
		String[] expressionPaths = modelSetting.getExpressionFiles();

		for (int i = 0; i < expressionPaths.length; i++) {
			loadExpression(expressionNames[i], modelHomeDir
					+ expressionPaths[i]);
		}
		loadPose(modelHomeDir + modelSetting.getPoseFile());

		ListMap<String, Float> layout = new ListMap<String, Float>();
		if (modelSetting.getLayout(layout)) {
			if (layout.get("width") != null) {
				modelMatrix.setWidth(width = layout.get("width"));

			}
			if (layout.get("height") != null) {
				modelMatrix.setHeight(height = layout.get("height"));
			}
			if (layout.get("x") != null) {
				modelMatrix.setX(layout.get("x"));

			}
			if (layout.get("y") != null) {
				modelMatrix.setY(layout.get("y"));
			}
			if (layout.get("center_x") != null) {
				modelMatrix.centerX(layout.get("center_x"));
			}
			if (layout.get("center_y") != null) {
				modelMatrix.centerY(layout.get("center_y"));
			}
			if (layout.get("top") != null) {
				modelMatrix.top(layout.get("top"));

			}
			if (layout.get("bottom") != null) {
				modelMatrix.bottom(layout.get("bottom"));
			}
			if (layout.get("left") != null) {
				modelMatrix.left(layout.get("left"));
			}
			if (layout.get("right") != null) {
				modelMatrix.right(layout.get("right"));
			}
			if (width <= 0) {
				width = 2f;
			}
			if (height <= 0) {
				height = 2f;
			}
		}

		for (int i = 0; i < modelSetting.getInitParamNum(); i++) {
			String id = modelSetting.getInitParamID(i);
			float value = modelSetting.getInitParamValue(i);
			live2DModel.setParamFloat(id, value);
		}

		for (int i = 0; i < modelSetting.getInitPartsVisibleNum(); i++) {
			String id = modelSetting.getInitPartsVisibleID(i);
			float value = modelSetting.getInitPartsVisibleValue(i);
			live2DModel.setPartsOpacity(id, value);
		}

		eyeBlink = new L2DEyeBlink();

		updating = false;
		initialized = true;

	}

	public float getWidth() {
		return width;
	}

	public float getHieght() {
		return height;
	}

	public void setPosition(float x, float y) {
		float nx = width / 2 - x / LSystem.viewSize.width * width / 2;
		float ny = height / 2 - y / LSystem.viewSize.height * height / 2;
		this.modelMatrix.setPosition(-nx, ny);
	}

	public void setExpression(String name) {
		if (!expressions.containsKey(name)) {
			return;
		}
		AMotion motion = expressions.get(name);
		expressionManager.startMotion(motion, false);
	}

	public void setRandomExpression() {
		int no = (int) (MathUtils.random() * expressions.size);
		String[] keys = expressions.keys;
		setExpression(keys[no]);
	}

	public void draw(GLEx gl) {
		alpha += accAlpha;
		if (alpha < 0) {
			alpha = 0;
			accAlpha = 0;
		} else if (alpha > 1) {
			alpha = 1;
			accAlpha = 0;
		}
		if (alpha < 0.001) {
			return;
		}
		if (alpha < 0.999) {
			live2DModel.draw(modelMatrix, gl);
		} else {
			live2DModel.draw(modelMatrix, gl);
		}
	}

	public boolean hitTest(String id, float testX, float testY) {
		if (alpha < 1) {
			return false;
		}
		if (modelSetting == null) {
			return false;
		}
		int len = modelSetting.getHitAreasNum();
		for (int i = 0; i < len; i++) {
			if (id.equals(modelSetting.getHitAreaName(i))) {
				return hitTestSimple(modelSetting.getHitAreaID(i), testX, testY);
			}
		}
		return false;
	}

	public void feedIn() {
		alpha = 0;
		accAlpha = 0.1f;
	}

}
