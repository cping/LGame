/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package loon.action.sprite.node;

import loon.utils.MathUtils;

public class Easing {

	private static final int TYPE_IN = 0;

	private static final int TYPE_OUT = 1;

	private static final int TYPE_IN_OUT = 2;

	private static final int FUNCTION_LINEAR = 0;

	private static final int FUNCTION_QUADRADIC = 1;

	private static final int FUNCTION_CUBIC = 2;

	private static final int FUNCTION_QUARTIC = 3;

	private static final int FUNCTION_QUINTIC = 4;

	private static final int FUNCTION_BACK = 5;

	private static final int FUNCTION_ELASTIC = 6;

	public static final Easing NONE = new Easing(TYPE_IN, FUNCTION_LINEAR);

	public static final Easing REGULAR_IN = new Easing(TYPE_IN,
			FUNCTION_QUADRADIC);

	public static final Easing REGULAR_OUT = new Easing(TYPE_OUT,
			FUNCTION_QUADRADIC);

	public static final Easing REGULAR_IN_OUT = new Easing(TYPE_IN_OUT,
			FUNCTION_QUADRADIC);

	public static final Easing STRONG_IN = new Easing(TYPE_IN, FUNCTION_QUINTIC);

	public static final Easing STRONG_OUT = new Easing(TYPE_OUT,
			FUNCTION_QUINTIC);

	public static final Easing STRONG_IN_OUT = new Easing(TYPE_IN_OUT,
			FUNCTION_QUINTIC);

	public static final Easing BACK_IN = new Easing(TYPE_IN, FUNCTION_BACK);

	public static final Easing BACK_OUT = new Easing(TYPE_OUT, FUNCTION_BACK);

	public static final Easing BACK_IN_OUT = new Easing(TYPE_IN_OUT,
			FUNCTION_BACK);

	public static final Easing ELASTIC_IN = new Easing(TYPE_IN,
			FUNCTION_ELASTIC);

	public static final Easing ELASTIC_OUT = new Easing(TYPE_OUT,
			FUNCTION_ELASTIC);

	public static final Easing ELASTIC_IN_OUT = new Easing(TYPE_IN_OUT,
			FUNCTION_ELASTIC);

	private final int type;

	private final int function;

	private final float strength;

	protected Easing() {
		this(TYPE_IN, FUNCTION_LINEAR);
	}

	protected Easing(int type) {
		this(type, FUNCTION_LINEAR);
	}

	private Easing(int type, int function) {
		this(type, function, 1);
	}

	private Easing(int type, int function, float stength) {
		this.type = type;
		this.function = function;
		this.strength = stength;
	}

	public Easing(Easing easing, float strength) {
		this(easing.type, easing.function, strength);
	}

	public final float ease(float time, float duration) {
		if (time <= 0 || duration <= 0) {
			return 0;
		} else if (time >= duration) {
			return duration;
		}

		final float t = time / duration;

		float easedT;

		switch (type) {

		default:
			easedT = t;
			break;

		case TYPE_IN:
			easedT = ease(t);
			break;

		case TYPE_OUT:
			easedT = 1 - ease(1 - t);
			break;

		case TYPE_IN_OUT:
			if (t < 0.5) {
				easedT = ease(2 * t) / 2;
			} else {
				easedT = 1 - ease(2 - 2 * t) / 2;
			}
			break;
		}
		if (strength != 1) {
			easedT = strength * easedT + (1 - strength) * t;
		}
		return (easedT * duration);
	}

	protected float ease(float t) {

		float t2;
		float t3;

		switch (function) {

		default:
		case FUNCTION_LINEAR:
			return t;

		case FUNCTION_QUADRADIC:
			return t * t;

		case FUNCTION_CUBIC:
			return t * t * t;

		case FUNCTION_QUARTIC:
			t2 = t * t;
			return t2 * t2;

		case FUNCTION_QUINTIC:
			t2 = t * t;
			return t2 * t2 * t;

		case FUNCTION_BACK:
			t2 = t * t;
			t3 = t2 * t;
			return t3 + t2 - t;

		case FUNCTION_ELASTIC:
			t2 = t * t;
			t3 = t2 * t;

			float scale = t2 * (2 * t3 + t2 - 4 * t + 2);
			float wave = -MathUtils.sin(t * 3.5f * MathUtils.PI);

			return scale * wave;
		}
	}
}
