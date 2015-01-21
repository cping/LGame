package loon.core.graphics.opengl.math;

import java.io.Serializable;

import loon.utils.MathUtils;
import loon.utils.NumberUtils;

public class Location2 implements Serializable, Location<Location2> {


	/**
	 * 
	 */
	private static final long serialVersionUID = -8621005200964961218L;
	public final static Location2 X = new Location2(1, 0);
	public final static Location2 Y = new Location2(0, 1);
	public final static Location2 Zero = new Location2(0, 0);

	public float x;

	public float y;

	public Location2() {
	}

	public Location2(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Location2(Location2 v) {
		set(v);
	}

	@Override
	public Location2 cpy() {
		return new Location2(this);
	}

	public static float len(float x, float y) {
		return (float) Math.sqrt(x * x + y * y);
	}

	@Override
	public float len() {
		return (float) Math.sqrt(x * x + y * y);
	}

	public static float len2(float x, float y) {
		return x * x + y * y;
	}

	@Override
	public float len2() {
		return x * x + y * y;
	}

	@Override
	public Location2 set(Location2 v) {
		x = v.x;
		y = v.y;
		return this;
	}

	public Location2 set(float x, float y) {
		this.x = x;
		this.y = y;
		return this;
	}

	@Override
	public Location2 sub(Location2 v) {
		x -= v.x;
		y -= v.y;
		return this;
	}

	public Location2 sub(float x, float y) {
		this.x -= x;
		this.y -= y;
		return this;
	}

	@Override
	public Location2 nor() {
		float len = len();
		if (len != 0) {
			x /= len;
			y /= len;
		}
		return this;
	}

	@Override
	public Location2 add(Location2 v) {
		x += v.x;
		y += v.y;
		return this;
	}

	public Location2 add(float x, float y) {
		this.x += x;
		this.y += y;
		return this;
	}

	public static float dot(float x1, float y1, float x2, float y2) {
		return x1 * x2 + y1 * y2;
	}

	@Override
	public float dot(Location2 v) {
		return x * v.x + y * v.y;
	}

	public float dot(float ox, float oy) {
		return x * ox + y * oy;
	}

	@Override
	public Location2 scl(float scalar) {
		x *= scalar;
		y *= scalar;
		return this;
	}

	public Location2 scl(float x, float y) {
		this.x *= x;
		this.y *= y;
		return this;
	}

	@Override
	public Location2 scl(Location2 v) {
		this.x *= v.x;
		this.y *= v.y;
		return this;
	}

	@Override
	public Location2 mulAdd(Location2 vec, float scalar) {
		this.x += vec.x * scalar;
		this.y += vec.y * scalar;
		return this;
	}

	@Override
	public Location2 mulAdd(Location2 vec, Location2 mulVec) {
		this.x += vec.x * mulVec.x;
		this.y += vec.y * mulVec.y;
		return this;
	}

	public static float dst(float x1, float y1, float x2, float y2) {
		final float x_d = x2 - x1;
		final float y_d = y2 - y1;
		return (float) Math.sqrt(x_d * x_d + y_d * y_d);
	}

	@Override
	public float dst(Location2 v) {
		final float x_d = v.x - x;
		final float y_d = v.y - y;
		return (float) Math.sqrt(x_d * x_d + y_d * y_d);
	}

	public float dst(float x, float y) {
		final float x_d = x - this.x;
		final float y_d = y - this.y;
		return (float) Math.sqrt(x_d * x_d + y_d * y_d);
	}

	public static float dst2(float x1, float y1, float x2, float y2) {
		final float x_d = x2 - x1;
		final float y_d = y2 - y1;
		return x_d * x_d + y_d * y_d;
	}

	@Override
	public float dst2(Location2 v) {
		final float x_d = v.x - x;
		final float y_d = v.y - y;
		return x_d * x_d + y_d * y_d;
	}

	public float dst2(float x, float y) {
		final float x_d = x - this.x;
		final float y_d = y - this.y;
		return x_d * x_d + y_d * y_d;
	}

	@Override
	public Location2 limit(float limit) {
		return limit2(limit * limit);
	}

	@Override
	public Location2 limit2(float limit2) {
		float len2 = len2();
		if (len2 > limit2) {
			return scl((float) Math.sqrt(limit2 / len2));
		}
		return this;
	}

	@Override
	public Location2 clamp(float min, float max) {
		final float len2 = len2();
		if (len2 == 0f)
			return this;
		float max2 = max * max;
		if (len2 > max2)
			return scl((float) Math.sqrt(max2 / len2));
		float min2 = min * min;
		if (len2 < min2)
			return scl((float) Math.sqrt(min2 / len2));
		return this;
	}

	@Override
	public Location2 setLength(float len) {
		return setLength2(len * len);
	}

	@Override
	public Location2 setLength2(float len2) {
		float oldLen2 = len2();
		return (oldLen2 == 0 || oldLen2 == len2) ? this : scl((float) Math
				.sqrt(len2 / oldLen2));
	}

	@Override
	public String toString() {
		return "[" + x + ":" + y + "]";
	}

	public Location2 mul(Transform3 mat) {
		float x = this.x * mat.val[0] + this.y * mat.val[3] + mat.val[6];
		float y = this.x * mat.val[1] + this.y * mat.val[4] + mat.val[7];
		this.x = x;
		this.y = y;
		return this;
	}

	public float crs(Location2 v) {
		return this.x * v.y - this.y * v.x;
	}

	public float crs(float x, float y) {
		return this.x * y - this.y * x;
	}

	public float angle() {
		float angle = (float) Math.atan2(y, x) * MathUtils.RAD_TO_DEG;
		if (angle < 0){
			angle += 360;
		}
		return angle;
	}

	public float angle(Location2 reference) {
		return (float) Math.atan2(crs(reference), dot(reference))
				* MathUtils.RAD_TO_DEG;
	}

	public float angleRad() {
		return (float) Math.atan2(y, x);
	}

	public float angleRad(Location2 reference) {
		return (float) Math.atan2(crs(reference), dot(reference));
	}

	public Location2 setAngle(float degrees) {
		return setAngleRad(degrees * MathUtils.DEG_TO_RAD);
	}

	public Location2 setAngleRad(float radians) {
		this.set(len(), 0f);
		this.rotateRad(radians);

		return this;
	}

	public Location2 rotate(float degrees) {
		return rotateRad(degrees * MathUtils.DEG_TO_RAD);
	}

	public Location2 rotateRad(float radians) {
		float cos = (float) Math.cos(radians);
		float sin = (float) Math.sin(radians);

		float newX = this.x * cos - this.y * sin;
		float newY = this.x * sin + this.y * cos;

		this.x = newX;
		this.y = newY;

		return this;
	}

	public Location2 rotate90(int dir) {
		float x = this.x;
		if (dir >= 0) {
			this.x = -y;
			y = x;
		} else {
			this.x = y;
			y = -x;
		}
		return this;
	}

	@Override
	public Location2 lerp(Location2 target, float alpha) {
		final float invAlpha = 1.0f - alpha;
		this.x = (x * invAlpha) + (target.x * alpha);
		this.y = (y * invAlpha) + (target.y * alpha);
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + NumberUtils.floatToIntBits(x);
		result = prime * result + NumberUtils.floatToIntBits(y);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Location2 other = (Location2) obj;
		if (NumberUtils.floatToIntBits(x) != NumberUtils
				.floatToIntBits(other.x))
			return false;
		if (NumberUtils.floatToIntBits(y) != NumberUtils
				.floatToIntBits(other.y))
			return false;
		return true;
	}

	@Override
	public boolean epsilonEquals(Location2 other, float epsilon) {
		if (other == null)
			return false;
		if (Math.abs(other.x - x) > epsilon)
			return false;
		if (Math.abs(other.y - y) > epsilon)
			return false;
		return true;
	}


	public boolean epsilonEquals(float x, float y, float epsilon) {
		if (Math.abs(x - this.x) > epsilon)
			return false;
		if (Math.abs(y - this.y) > epsilon)
			return false;
		return true;
	}

	@Override
	public boolean isUnit() {
		return isUnit(0.000000001f);
	}

	@Override
	public boolean isUnit(final float margin) {
		return Math.abs(len2() - 1f) < margin;
	}

	@Override
	public boolean isZero() {
		return x == 0 && y == 0;
	}

	@Override
	public boolean isZero(final float margin) {
		return len2() < margin;
	}

	@Override
	public boolean isOnLine(Location2 other) {
		return MathUtils.isZero(x * other.y - y * other.x);
	}

	@Override
	public boolean isOnLine(Location2 other, float epsilon) {
		return MathUtils.isZero(x * other.y - y * other.x, epsilon);
	}

	@Override
	public boolean isCollinear(Location2 other, float epsilon) {
		return isOnLine(other, epsilon) && dot(other) > 0f;
	}

	@Override
	public boolean isCollinear(Location2 other) {
		return isOnLine(other) && dot(other) > 0f;
	}

	@Override
	public boolean isCollinearOpposite(Location2 other, float epsilon) {
		return isOnLine(other, epsilon) && dot(other) < 0f;
	}

	@Override
	public boolean isCollinearOpposite(Location2 other) {
		return isOnLine(other) && dot(other) < 0f;
	}

	@Override
	public boolean isPerpendicular(Location2 vector) {
		return MathUtils.isZero(dot(vector));
	}

	@Override
	public boolean isPerpendicular(Location2 vector, float epsilon) {
		return MathUtils.isZero(dot(vector), epsilon);
	}

	@Override
	public boolean hasSameDirection(Location2 vector) {
		return dot(vector) > 0;
	}

	@Override
	public boolean hasOppositeDirection(Location2 vector) {
		return dot(vector) < 0;
	}

	@Override
	public Location2 setZero() {
		this.x = 0;
		this.y = 0;
		return this;
	}
}
