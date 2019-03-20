/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.5
 */
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
