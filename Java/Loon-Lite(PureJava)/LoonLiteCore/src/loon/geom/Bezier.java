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
package loon.geom;

import loon.utils.MathUtils;
import loon.utils.StringKeyValue;

public class Bezier {

	public static Vector2f pointQuadraticCurve(float t, float ax, float ay, float bx, float by, float cx, float cy) {
		t = MathUtils.clamp(t, 0f, 1f);
		if (t == 0) {
			return new Vector2f(ax, ay);
		}
		if (t == 1) {
			return new Vector2f(cx, cy);
		}

		final float u = 1 - t;
		final float tu = t * u;
		final float tt = t * t;
		final float uu = u * u;

		return new Vector2f(pos(uu, tu, tt, ax, bx, cx), pos(uu, tu, tt, ay, by, cy));
	}

	public final static float pos(float uu, float tu, float tt, float a, float b, float c) {
		return a * uu + b * 2 * tu + c * tt;
	}

	public final static float bezierByTime(Bezier c, float t) {
		float percent = cardano(c, t);
		float p1y = c.controlPoint1.y;
		float p2y = c.controlPoint2.y;
		return ((1f - percent) * (p1y + (p2y - p1y) * percent) * 3f + percent * percent) * percent;
	}

	public final static float cubic(float a, float b, float c, float d, float t) {
		float t1 = 1f - t;
		return ((t1 * t1 * t1) * a + 3f * t * (t1 * t1) * b + 3f * (t * t) * (t1) * c + (t * t * t) * d);
	}

	public final static float quad(float a, float b, float c, float t) {
		float t1 = 1f - t;
		return (t1 * t1) * a + 2f * (t1) * t * b + (t * t) * c;
	}

	public static Vector2f cardano(XY p0, XY p1, XY p2, XY p3, float tension, float t) {
		if (tension < 0f) {
			tension = 0f;
		}
		if (tension > 1f) {
			tension = 1f;
		}
		float t2 = t * t;
		float t3 = t2 * t;

		float s = (1 - tension) / 2;

		float b1 = s * ((-t3 + (2 * t2)) - t);
		float b2 = s * (-t3 + t2) + (2 * t3 - 3 * t2 + 1);
		float b3 = s * (t3 - 2 * t2 + t) + (-2 * t3 + 3 * t2);
		float b4 = s * (t3 - t2);

		float x = (p0.getX() * b1 + p1.getX() * b2 + p2.getX() * b3 + p3.getX() * b4);
		float y = (p0.getY() * b1 + p1.getY() * b2 + p2.getY() * b3 + p3.getY() * b4);

		return new Vector2f(x, y);
	}

	public final static float cardano(Bezier curve, float x) {
		float pa = x - 0f;
		float pb = x - curve.controlPoint1.x;
		float pc = x - curve.controlPoint2.x;
		float pd = x - 1f;
		float pa3 = pa * 3f;
		float pb3 = pb * 3f;
		float pc3 = pc * 3f;
		float d = (-pa + pb3 - pc3 + pd);
		float rd = 1 / d;
		float r3 = 1 / 3f;
		float a = (pa3 - 6 * pb + pc3) * rd;
		float a3 = a * r3;
		float b = (-pa3 + pb3) * rd;
		float c = pa * rd;
		float p = (3f * b - a * a) * r3;
		float p3 = p * r3;
		float q = (2f * a * a * a - 9 * a * b + 27 * c) / 27f;
		float q2 = q / 2f;
		float discriminant = q2 * q2 + p3 * p3 * p3;
		float u1;
		float v1;
		float x1;
		float x2;
		float x3;
		if (discriminant < 0) {
			float mp3 = -p * r3;
			float mp33 = mp3 * mp3 * mp3;
			float r = MathUtils.sqrt(mp33);
			float t = -q / (2f * r);
			float cosphi = t < -1 ? -1 : t > 1 ? 1 : t;
			float phi = MathUtils.acos(cosphi);
			float crtr = MathUtils.crt(r);
			float t1 = 2 * crtr;
			x1 = t1 * MathUtils.cos(phi * r3) - a3;
			x2 = t1 * MathUtils.cos((phi + MathUtils.TAU) * r3) - a3;
			x3 = t1 * MathUtils.cos((phi + 2f * MathUtils.TAU) * r3) - a3;
			if (x1 >= 0 && x1 <= 1) {
				if (x2 >= 0 && x2 <= 1) {
					if (x3 >= 0 && x3 <= 1) {
						return MathUtils.max(x1, x2, x3);
					} else {
						return MathUtils.max(x1, x2);
					}
				} else if (x3 >= 0 && x3 <= 1) {
					return MathUtils.max(x1, x3);
				} else {
					return x1;
				}
			} else if (x2 >= 0 && x2 <= 1) {
				if (x3 >= 0 && x3 <= 1) {
					return MathUtils.max(x2, x3);
				} else {
					return x2;
				}
			} else {
				return x3;
			}
		} else if (discriminant == 0) {
			u1 = q2 < 0 ? MathUtils.crt(-q2) : -MathUtils.crt(q2);
			x1 = 2 * u1 - a3;
			x2 = -u1 - a3;

			if (x1 >= 0 && x1 <= 1) {
				if (x2 >= 0 && x2 <= 1) {
					return MathUtils.max(x1, x2);
				} else {
					return x1;
				}
			} else {
				return x2;
			}
		} else {
			float sd = MathUtils.sqrt(discriminant);
			u1 = MathUtils.crt(-q2 + sd);
			v1 = MathUtils.crt(q2 + sd);
			x1 = u1 - v1 - a3;
			return x1;
		}
	}

	public static float bezierInterpolate(float p0, float p1, float p2, float p3, float t) {
		float u = 1f - t;
		float coeff0 = u * u * u;
		float coeff1 = 3 * u * u * t;
		float coeff2 = 3 * u * t * t;
		float coeff3 = t * t * t;
		return coeff0 * p0 + coeff1 * p1 + coeff2 * p2 + coeff3 * p3;
	}

	public static float mix2(float p, float a, float b) {
		return a * (1f - p) + (b * p);
	}

	public static float mix3(float p, float a, float b, float c) {
		return mix2(p, mix2(p, a, b), mix2(p, b, c));
	}

	public static float mix4(float p, float a, float b, float c, float d) {
		return mix2(p, mix3(p, a, b, c), mix3(p, b, c, d));
	}

	public static float mix5(float p, float a, float b, float c, float d, float e) {
		return mix2(p, mix4(p, a, b, c, d), mix4(p, b, c, d, e));
	}

	public static float bezier(float c1, float c2, float c3, float c4, float t) {
		final float t1 = 1f - t;
		return t1 * (t1 * (c1 + (c2 * 3 - c1) * t) + c3 * 3 * t * t) + c4 * t * t * t;
	}

	public static PointF bezier2(float p, XY a, XY b) {
		return new PointF(mix2(p, a.getX(), b.getX()), mix2(p, a.getY(), b.getY()));
	}

	public static PointF bezier3(float p, XY a, XY b, XY c) {
		return new PointF(mix3(p, a.getX(), b.getX(), c.getX()), mix3(p, a.getY(), b.getY(), c.getY()));
	}

	public static PointF bezier4(float p, XY a, XY b, XY c, XY d) {
		return new PointF(mix4(p, a.getX(), b.getX(), c.getX(), d.getX()),
				mix4(p, a.getY(), b.getY(), c.getY(), d.getY()));
	}

	public static PointF bezier5(float p, XY a, XY b, XY c, XY d, XY e) {
		return new PointF(mix5(p, a.getX(), b.getX(), c.getX(), d.getX(), e.getX()),
				mix5(p, a.getY(), b.getY(), c.getY(), d.getY(), e.getY()));
	}

	public Vector2f endPosition = new Vector2f();

	public Vector2f controlPoint1 = new Vector2f();

	public Vector2f controlPoint2 = new Vector2f();

	public Bezier() {
		this(0f, 0f, 0f, 0f, 0f, 0f);
	}

	public Bezier(float cp1x, float cp1y, float cp2x, float cp2y, float endx, float endy) {
		this(Vector2f.at(cp1x, cp1y), Vector2f.at(cp2x, cp2y), Vector2f.at(endx, endy));
	}

	public Bezier(Vector2f controlPos1, Vector2f controlPos2, Vector2f endPos) {
		controlPoint1.set(controlPos1);
		controlPoint2.set(controlPos2);
		endPosition.set(endPos);
	}

	public Vector2f getEndPosition() {
		return endPosition;
	}

	public Bezier setEndPosition(float x, float y) {
		return setEndPosition(Vector2f.at(x, y));
	}

	public Bezier setEndPosition(Vector2f endPosition) {
		this.endPosition = endPosition;
		return this;
	}

	public Vector2f getControlPoint1() {
		return controlPoint1;
	}

	public Bezier setControlPoint1(float x, float y) {
		return setControlPoint1(Vector2f.at(x, y));
	}

	public Bezier setControlPoint1(Vector2f controlPoint1) {
		this.controlPoint1 = controlPoint1;
		return this;
	}

	public Vector2f getControlPoint2() {
		return controlPoint2;
	}

	public Bezier setControlPoint2(float x, float y) {
		return setControlPoint2(Vector2f.at(x, y));
	}

	public Bezier setControlPoint2(Vector2f controlPoint2) {
		this.controlPoint2 = controlPoint2;
		return this;
	}

	public Bezier cpy() {
		return new Bezier(controlPoint1, controlPoint2, endPosition);
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue("Bezier");
		builder.kv("controlPoint1", controlPoint1).comma().kv("controlPoint2", controlPoint2).comma().kv("endPosition",
				endPosition);
		return builder.toString();
	}
}
