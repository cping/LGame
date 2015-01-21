package loon.core.graphics.opengl.math;

public interface Location<T extends Location<T>> {

	T cpy();

	float len();

	float len2();

	T limit(float limit);

	T limit2(float limit2);

	T setLength(float len);

	T setLength2(float len2);

	T clamp(float min, float max);

	T set(T v);

	T sub(T v);

	T nor();

	T add(T v);

	float dot(T v);

	T scl(float scalar);

	T scl(T v);

	float dst(T v);

	float dst2(T v);

	T lerp(T target, float alpha);

	boolean isUnit();

	boolean isUnit(final float margin);

	boolean isZero();

	boolean isZero(final float margin);

	boolean isOnLine(T other, float epsilon);

	boolean isOnLine(T other);

	boolean isCollinear(T other, float epsilon);

	boolean isCollinear(T other);

	boolean isCollinearOpposite(T other, float epsilon);

	boolean isCollinearOpposite(T other);

	boolean isPerpendicular(T other);

	boolean isPerpendicular(T other, float epsilon);

	boolean hasSameDirection(T other);

	boolean hasOppositeDirection(T other);

	boolean epsilonEquals(T other, float epsilon);

	T mulAdd(T v, float scalar);

	T mulAdd(T v, T mulVec);

	T setZero();
}
