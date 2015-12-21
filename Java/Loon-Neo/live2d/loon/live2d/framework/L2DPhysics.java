package loon.live2d.framework;

import loon.BaseIO;
import loon.live2d.ALive2DModel;
import loon.live2d.physics.PhysicsHair;
import loon.live2d.util.Json;
import loon.live2d.util.UtSystem;
import loon.live2d.util.Json.Value;
import loon.utils.TArray;

public class L2DPhysics {
	private TArray<PhysicsHair> physicsList;
	private long startTimeMSec;

	public L2DPhysics() {
		physicsList = new TArray<PhysicsHair>();
		startTimeMSec = UtSystem.getUserTimeMSec();
	}

	public void updateParam(ALive2DModel model) {
		long timeMSec = UtSystem.getUserTimeMSec() - startTimeMSec;
		for (int i = 0; i < physicsList.size; i++) {
			physicsList.get(i).update(model, timeMSec);
		}
	}

	public static L2DPhysics load(String path) throws Exception {
		byte[] buf = BaseIO.loadBytes(path);
		return load(buf);
	}

	public static L2DPhysics load(byte[] buf) throws Exception {

		L2DPhysics ret = new L2DPhysics();

		Value json = Json.parseFromBytes(buf);

		Value params = json.get("physics_hair");
		int paramNum = params.getVector(null).size;

		for (int i = 0; i < paramNum; i++) {
			Value param = params.get(i);

			PhysicsHair physics = new PhysicsHair();

			Value setup = param.get("setup");

			float length = setup.get("length").toFloat();

			float resist = setup.get("regist").toFloat();

			float mass = setup.get("mass").toFloat();
			physics.setup(length, resist, mass);

			Value srcList = param.get("src");
			int srcNum = srcList.getVector(null).size;
			for (int j = 0; j < srcNum; j++) {
				Value src = srcList.get(j);
				String id = src.get("id").toString();// param ID
				PhysicsHair.Src type = PhysicsHair.Src.SRC_TO_X;
				String typeStr = src.get("ptype").toString();
				if (typeStr.equals("x")) {
					type = PhysicsHair.Src.SRC_TO_X;
				} else if (typeStr.equals("y")) {
					type = PhysicsHair.Src.SRC_TO_Y;
				} else if (typeStr.equals("angle")) {
					type = PhysicsHair.Src.SRC_TO_G_ANGLE;
				}

				float scale = src.get("scale").toFloat();
				float weight = src.get("weight").toFloat();
				physics.addSrcParam(type, id, scale, weight);
			}

			Value targetList = param.get("targets");
			int targetNum = targetList.getVector(null).size;
			for (int j = 0; j < targetNum; j++) {
				Value target = targetList.get(j);
				String id = target.get("id").toString();
				PhysicsHair.Target type = PhysicsHair.Target.TARGET_FROM_ANGLE;
				String typeStr = target.get("ptype").toString();
				if (typeStr.equals("angle")) {
					type = PhysicsHair.Target.TARGET_FROM_ANGLE;
				} else if (typeStr.equals("angle_v")) {
					type = PhysicsHair.Target.TARGET_FROM_ANGLE_V;
				}

				float scale = target.get("scale").toFloat();
				float weight = target.get("weight").toFloat();
				physics.addTargetParam(type, id, scale, weight);

			}

			ret.physicsList.add(physics);
		}

		return ret;
	}
}
