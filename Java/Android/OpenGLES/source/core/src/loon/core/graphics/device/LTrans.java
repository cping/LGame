package loon.core.graphics.device;

/**
 * Copyright 2008 - 2010
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
 * @version 0.1
 */
public interface LTrans {

	public static final int TRANS_NONE = 0;

	public static final int TRANS_ROT90 = 5;

	public static final int TRANS_ROT180 = 3;

	public static final int TRANS_ROT270 = 6;

	public static final int TRANS_MIRROR = 2;

	public static final int TRANS_MIRROR_ROT90 = 7;

	public static final int TRANS_MIRROR_ROT180 = 1;

	public static final int TRANS_MIRROR_ROT270 = 4;

	final static public float ANGLE_90 = (float) (Math.PI / 2);

	final static public float ANGLE_270 = (float) (Math.PI * 3 / 2);

	public static final int HCENTER = 1;

	public static final int VCENTER = 2;

	public static final int LEFT = 4;

	public static final int RIGHT = 8;

	public static final int TOP = 16;

	public static final int BOTTOM = 32;

	public static final int BASELINE = 64;

	public static final int SOLID = 0;

	public static final int DOTTED = 1;
}
