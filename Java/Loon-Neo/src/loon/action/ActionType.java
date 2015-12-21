package loon.action;

public class ActionType {

	public static final int POSITION = 1;
	public static final int SCALE = 2;
	public static final int VISIBILITY = 3;
	public static final int ROTATION = 4;
	public static final int ALPHA = 5;

	/**
	 * 获得当前ActionBind对象的格式化数据(返回值为返回的浮点数组长度)
	 * 
	 * @param target
	 * @param tweenType
	 * @param returnValues
	 * @return
	 */
	public final static int getValues(ActionBind target, int tweenType,
			float[] returnValues) {
		switch (tweenType) {
		case POSITION:
			returnValues[0] = target.getX();
			returnValues[1] = target.getY();
			return 2;
		case SCALE:
			returnValues[0] = target.getScaleX();
			returnValues[1] = target.getScaleY();
			return 2;
		case VISIBILITY:
			returnValues[0] = target.isVisible() ? 1f : 0f;
			return 1;
		case ROTATION:
			returnValues[0] = target.getRotation();
			return 1;
		case ALPHA:
			returnValues[0] = target.getAlpha();
			return 1;
		default:
			return -1;
		}
	}

	/**
	 * 注入当前ActionBind已经格式化的数据
	 * 
	 * @param target
	 * @param tweenType
	 * @param newValues
	 */
	public final static void setValues(ActionBind target, int tweenType,
			float[] newValues) {
		switch (tweenType) {
		case POSITION:
			target.setLocation(newValues[0], newValues[1]);
			break;
		case SCALE:
			target.setScale(newValues[0], newValues[1]);
			break;
		case VISIBILITY:
			target.setVisible(newValues[0] > 0);
			break;
		case ROTATION:
			target.setRotation(newValues[0]);
			break;
		case ALPHA:
			target.setAlpha(newValues[0]);
			break;
		default:
			break;
		}
	}

}
