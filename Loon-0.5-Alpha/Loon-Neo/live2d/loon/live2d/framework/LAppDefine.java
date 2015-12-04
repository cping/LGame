package loon.live2d.framework;

public class LAppDefine {

	public static boolean DEBUG_LOG = false;
	public static boolean DEBUG_DRAW_HIT_AREA = false;

	public static final float VIEW_MAX_SCALE = 2f;
	public static final float VIEW_MIN_SCALE = 0.8f;

	public static final float VIEW_LOGICAL_LEFT = -1;
	public static final float VIEW_LOGICAL_RIGHT = 1;

	public static final float VIEW_LOGICAL_MAX_LEFT = -2;
	public static final float VIEW_LOGICAL_MAX_RIGHT = 2;
	public static final float VIEW_LOGICAL_MAX_BOTTOM = -2;
	public static final float VIEW_LOGICAL_MAX_TOP = 2;

	public static final String MOTION_GROUP_IDLE = "idle";
	public static final String MOTION_GROUP_TAP_BODY = "tap_body";
	public static final String MOTION_GROUP_FLICK_HEAD = "flick_head";
	public static final String MOTION_GROUP_PINCH_IN = "pinch_in";
	public static final String MOTION_GROUP_PINCH_OUT = "pinch_out";
	public static final String MOTION_GROUP_SHAKE = "shake";
	public static final String MOTION_GROUP_DANCE = "dance";

	static final String HIT_AREA_HEAD = "head";
	static final String HIT_AREA_BODY = "body";

	public static final int PRIORITY_NONE = 0;
	public static final int PRIORITY_IDLE = 1;
	public static final int PRIORITY_NORMAL = 2;
	public static final int PRIORITY_FORCE = 3;
}
