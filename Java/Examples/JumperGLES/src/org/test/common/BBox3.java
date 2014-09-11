package org.test.common;

import loon.core.RefObject;
import loon.core.geom.Vector3f;

public class BBox3
{
	public Vector3f[] vcorners;
	public Vector3f vmax;
	public Vector3f vmin;

	public BBox3()
	{
		this.vmin = new Vector3f();
		this.vmax = new Vector3f();
		this.vcorners = new Vector3f[8];
	}

	public BBox3(BBox3 bb)
	{
		this.vmin = new Vector3f();
		this.vmax = new Vector3f();
		this.vcorners = new Vector3f[8];
		this.vmin = bb.vmin;
		this.vmax = bb.vmax;
		this.UpdateCorners();
	}

	public BBox3(Vector3f _vmin, Vector3f _vmax)
	{
		this.vmin = new Vector3f();
		this.vmax = new Vector3f();
		this.vcorners = new Vector3f[8];
		this.vmin = _vmin;
		this.vmax = _vmax;
		this.UpdateCorners();
	}

	public BBox3(Vector3f[] varray, int num)
	{
		this.vmin = new Vector3f();
		this.vmax = new Vector3f();
		this.vcorners = new Vector3f[8];
		this.vmin = varray[0];
		this.vmax = varray[0];
		for (int i = 0; i < num; i++)
		{
			if (varray[i].x < this.vmin.x)
			{
				this.vmin.x = varray[i].x;
			}
			else if (varray[i].x > this.vmax.x)
			{
				this.vmax.x = varray[i].x;
			}
			if (varray[i].y < this.vmin.y)
			{
				this.vmin.y = varray[i].y;
			}
			else if (varray[i].y > this.vmax.y)
			{
				this.vmax.y = varray[i].y;
			}
			if (varray[i].z < this.vmin.z)
			{
				this.vmin.z = varray[i].z;
			}
			else if (varray[i].z > this.vmax.z)
			{
				this.vmax.z = varray[i].z;
			}
		}
		this.UpdateCorners();
	}

	public final void begin_grow()
	{
		this.vmin.x = 1000000f;
		this.vmin.y = 1000000f;
		this.vmin.z = 1000000f;
		this.vmax.x = -1000000f;
		this.vmax.y = -1000000f;
		this.vmax.z = -1000000f;
	}

	public final boolean contains(BBox3 bb)
	{
		return (this.is_point_in(bb.vmin) || (this.is_point_in(new Vector3f(bb.vmin.x, bb.vmin.y, bb.vmax.z)) || (this.is_point_in(new Vector3f(bb.vmax.x, bb.vmin.y, bb.vmax.z)) || (this.is_point_in(new Vector3f(bb.vmax.x, bb.vmin.y, bb.vmin.z)) || (this.is_point_in(bb.vmax) || (this.is_point_in(new Vector3f(bb.vmin.x, bb.vmax.y, bb.vmin.z)) || (this.is_point_in(new Vector3f(bb.vmin.x, bb.vmax.y, bb.vmax.z)) || this.is_point_in(new Vector3f(bb.vmax.x, bb.vmax.y, bb.vmin.z)))))))));
	}

	public final Vector3f get_center()
	{
		float num = this.vmax.x - this.vmin.x;
		float num2 = this.vmax.y - this.vmin.y;
		float num3 = this.vmax.z - this.vmin.z;
		return new Vector3f(this.vmin.x + (num * 0.5f), this.vmin.y + (num2 * 0.5f), this.vmin.z + (num3 * 0.5f));
	}

	public final Vector3f[] GetAllCorners()
	{
		return this.vcorners;
	}

	public final void grow(BBox3 bb)
	{
		if (bb.vmin.x < this.vmin.x)
		{
			this.vmin.x = bb.vmin.x;
		}
		if (bb.vmin.y < this.vmin.y)
		{
			this.vmin.y = bb.vmin.y;
		}
		if (bb.vmin.z < this.vmin.z)
		{
			this.vmin.z = bb.vmin.z;
		}
		if (bb.vmax.x > this.vmax.x)
		{
			this.vmax.x = bb.vmax.x;
		}
		if (bb.vmax.y > this.vmax.y)
		{
			this.vmax.y = bb.vmax.y;
		}
		if (bb.vmax.z > this.vmax.z)
		{
			this.vmax.z = bb.vmax.z;
		}
		this.UpdateCorners();
	}

	public final void grow(Vector3f v)
	{
		if (v.x < this.vmin.x)
		{
			this.vmin.x = v.x;
		}
		if (v.x > this.vmax.x)
		{
			this.vmax.x = v.x;
		}
		if (v.y < this.vmin.y)
		{
			this.vmin.y = v.y;
		}
		if (v.y > this.vmax.y)
		{
			this.vmax.y = v.y;
		}
		if (v.z < this.vmin.z)
		{
			this.vmin.z = v.z;
		}
		if (v.z > this.vmax.z)
		{
			this.vmax.z = v.z;
		}
		this.UpdateCorners();
	}

	public final void grow(Vector3f[] varray, int num)
	{
		for (int i = 0; i < num; i++)
		{
			this.grow(varray[i]);
		}
		this.UpdateCorners();
	}

	public final int intersect(BBox3 box)
	{
		int num = 0xffff;
		int num2 = 0;
		int num3 = this.line_test(this.vmin.x, this.vmax.x, box.vmin.x, box.vmax.x);
		num &= num3;
		num2 |= num3;
		int num4 = this.line_test(this.vmin.y, this.vmax.y, box.vmin.y, box.vmax.y);
		num &= num4;
		num2 |= num4;
		int num5 = this.line_test(this.vmin.z, this.vmax.z, box.vmin.z, box.vmax.z);
		num &= num5;
		if ((num2 | num5) != 0)
		{
			if (num != 0)
			{
				return num;
			}
			if (((num3 != 0) && (num4 != 0)) && (num5 != 0))
			{
				return 8;
			}
		}
		return 0;
	}

	public final boolean intersect(Line3 line)
	{
		Vector3f vector = line.start();
		Vector3f vector2 = line.end();
		if (((((vector.x >= this.vmin.x) && (vector.y >= this.vmin.y)) && ((vector.z >= this.vmin.z) && (vector.x <= this.vmax.x))) && ((vector.y <= this.vmax.y) && (vector.z <= this.vmax.z))) || ((((vector2.x >= this.vmin.x) && (vector2.y >= this.vmin.y)) && ((vector2.z >= this.vmin.z) && (vector2.x <= this.vmax.x))) && ((vector2.y <= this.vmax.y) && (vector2.z <= this.vmax.z))))
		{
			return true;
		}
		Vector3f outVec = new Vector3f();
		for (int i = 0; i < 6; i++)
		{
			switch (i)
			{
				case 0:
					RefObject<Vector3f> tempRef_outVec = new RefObject<Vector3f>(outVec);
					boolean tempVar = !this.isect_const_x(this.vmin.x, line, tempRef_outVec) || !this.pip_const_x(outVec);
						outVec = tempRef_outVec.argvalue;
					if (tempVar)
					{
						break;
					}
					return true;

				case 1:
					RefObject<Vector3f> tempRef_outVec2 = new RefObject<Vector3f>(outVec);
					boolean tempVar2 = !this.isect_const_x(this.vmax.x, line, tempRef_outVec2) || !this.pip_const_x(outVec);
						outVec = tempRef_outVec2.argvalue;
					if (tempVar2)
					{
						break;
					}
					return true;

				case 2:
					RefObject<Vector3f> tempRef_outVec3 = new RefObject<Vector3f>(outVec);
					boolean tempVar3 = !this.isect_const_y(this.vmin.y, line, tempRef_outVec3) || !this.pip_const_y(outVec);
						outVec = tempRef_outVec3.argvalue;
					if (tempVar3)
					{
						break;
					}
					return true;

				case 3:
					RefObject<Vector3f> tempRef_outVec4 = new RefObject<Vector3f>(outVec);
					boolean tempVar4 = !this.isect_const_y(this.vmax.y, line, tempRef_outVec4) || !this.pip_const_y(outVec);
						outVec = tempRef_outVec4.argvalue;
					if (tempVar4)
					{
						break;
					}
					return true;

				case 4:
					RefObject<Vector3f> tempRef_outVec5 = new RefObject<Vector3f>(outVec);
					boolean tempVar5 = !this.isect_const_z(this.vmin.z, line, tempRef_outVec5) || !this.pip_const_z(outVec);
						outVec = tempRef_outVec5.argvalue;
					if (tempVar5)
					{
						break;
					}
					return true;

				case 5:
					RefObject<Vector3f> tempRef_outVec6 = new RefObject<Vector3f>(outVec);
					boolean tempVar6 = !this.isect_const_z(this.vmax.z, line, tempRef_outVec6) || !this.pip_const_z(outVec);
						outVec = tempRef_outVec6.argvalue;
					if (tempVar6)
					{
						break;
					}
					return true;
			}
		}
		return false;
	}

	public final boolean intersect_closest_point_get(Line3 line, RefObject<Vector3f> outVec)
	{
		Vector3f vector2 = new Vector3f();
		float num2 = Tools.MAX_FLOAT;
		boolean flag2 = false;
		for (int i = 0; i < 6; i++)
		{
			boolean flag = false;
			switch (i)
			{
				case 0:
					RefObject<Vector3f> tempRef_vector2 = new RefObject<Vector3f>(vector2);
					boolean tempVar = this.isect_const_x(this.vmin.x, line, tempRef_vector2) && this.pip_const_x(vector2);
						vector2 = tempRef_vector2.argvalue;
					if (tempVar)
					{
						flag = true;
					}
					break;

				case 1:
					RefObject<Vector3f> tempRef_vector22 = new RefObject<Vector3f>(vector2);
					boolean tempVar2 = this.isect_const_x(this.vmax.x, line, tempRef_vector22) && this.pip_const_x(vector2);
						vector2 = tempRef_vector22.argvalue;
					if (tempVar2)
					{
						flag = true;
					}
					break;

				case 2:
					RefObject<Vector3f> tempRef_vector23 = new RefObject<Vector3f>(vector2);
					boolean tempVar3 = this.isect_const_y(this.vmin.y, line, tempRef_vector23) && this.pip_const_y(vector2);
						vector2 = tempRef_vector23.argvalue;
					if (tempVar3)
					{
						flag = true;
					}
					break;

				case 3:
					RefObject<Vector3f> tempRef_vector24 = new RefObject<Vector3f>(vector2);
					boolean tempVar4 = this.isect_const_y(this.vmax.y, line, tempRef_vector24) && this.pip_const_y(vector2);
						vector2 = tempRef_vector24.argvalue;
					if (tempVar4)
					{
						flag = true;
					}
					break;

				case 4:
					RefObject<Vector3f> tempRef_vector25 = new RefObject<Vector3f>(vector2);
					boolean tempVar5 = this.isect_const_z(this.vmin.z, line, tempRef_vector25) && this.pip_const_z(vector2);
						vector2 = tempRef_vector25.argvalue;
					if (tempVar5)
					{
						flag = true;
					}
					break;

				case 5:
					RefObject<Vector3f> tempRef_vector26 = new RefObject<Vector3f>(vector2);
					boolean tempVar6 = this.isect_const_z(this.vmax.z, line, tempRef_vector26) && this.pip_const_z(vector2);
						vector2 = tempRef_vector26.argvalue;
					if (tempVar6)
					{
						flag = true;
					}
					break;
			}
			if (flag)
			{
				float num = (vector2.sub(line.b)).len();
				if (num < num2)
				{
					num2 = num;
					outVec.argvalue = vector2;
				}
				flag2 = true;
			}
		}
		return flag2;
	}

	public final boolean is_completely_in(BBox3 bb)
	{
		if (!this.is_point_in(bb.vmin))
		{
			return false;
		}
		if (!this.is_point_in(new Vector3f(bb.vmin.x, bb.vmin.y, bb.vmax.z)))
		{
			return false;
		}
		if (!this.is_point_in(new Vector3f(bb.vmax.x, bb.vmin.y, bb.vmax.z)))
		{
			return false;
		}
		if (!this.is_point_in(new Vector3f(bb.vmax.x, bb.vmin.y, bb.vmin.z)))
		{
			return false;
		}
		if (!this.is_point_in(bb.vmax))
		{
			return false;
		}
		if (!this.is_point_in(new Vector3f(bb.vmin.x, bb.vmax.y, bb.vmin.z)))
		{
			return false;
		}
		if (!this.is_point_in(new Vector3f(bb.vmin.x, bb.vmax.y, bb.vmax.z)))
		{
			return false;
		}
		if (!this.is_point_in(new Vector3f(bb.vmax.x, bb.vmax.y, bb.vmin.z)))
		{
			return false;
		}
		return true;
	}

	public final boolean is_point_in(Vector3f p)
	{
		return ((((p.x >= this.vmin.x) && (p.x <= this.vmax.x)) && ((p.y >= this.vmin.y) && (p.y <= this.vmax.y))) && ((p.z >= this.vmin.z) && (p.z <= this.vmax.z)));
	}

	public final boolean isect_const_x(float x, Line3 l, RefObject<Vector3f> outVec)
	{
		if (l.m.x != 0f)
		{
			float t = (x - l.b.x) / l.m.x;
			if ((t >= 0f) && (t <= 1f))
			{
				outVec.argvalue = l.ipol(t);
				return true;
			}
		}
		return false;
	}

	public final boolean isect_const_y(float y, Line3 l, RefObject<Vector3f> outVec)
	{
		if (l.m.y != 0f)
		{
			float t = (y - l.b.y) / l.m.y;
			if ((t >= 0f) && (t <= 1f))
			{
				outVec.argvalue = l.ipol(t);
				return true;
			}
		}
		return false;
	}

	public final boolean isect_const_z(float z, Line3 l, RefObject<Vector3f> outVec)
	{
		if (l.m.z != 0f)
		{
			float t = (z - l.b.z) / l.m.z;
			if ((t >= 0f) && (t <= 1f))
			{
				outVec.argvalue = l.ipol(t);
				return true;
			}
		}
		return false;
	}

	public final int line_test(float v0, float v1, float w0, float w1)
	{
		if ((v1 < w0) || (v0 > w1))
		{
			return 0;
		}
		if ((v0 == w0) && (v1 == w1))
		{
			return 1;
		}
		if ((v0 >= w0) && (v1 <= w1))
		{
			return 2;
		}
		if ((v0 <= w0) && (v1 >= w1))
		{
			return 4;
		}
		return 8;
	}

	public final boolean pip_const_x(Vector3f p)
	{
		return (((p.y >= this.vmin.y) && (p.y <= this.vmax.y)) && ((p.z >= this.vmin.z) && (p.z <= this.vmax.z)));
	}

	public final boolean pip_const_y(Vector3f p)
	{
		return (((p.x >= this.vmin.x) && (p.x <= this.vmax.x)) && ((p.z >= this.vmin.z) && (p.z <= this.vmax.z)));
	}

	public final boolean pip_const_z(Vector3f p)
	{
		return (((p.x >= this.vmin.x) && (p.x <= this.vmax.x)) && ((p.y >= this.vmin.y) && (p.y <= this.vmax.y)));
	}

	public final void set(Vector3f[] varray, int num)
	{
		this.vmin = varray[0];
		this.vmax = varray[0];
		for (int i = 0; i < num; i++)
		{
			if (varray[i].x < this.vmin.x)
			{
				this.vmin.x = varray[i].x;
			}
			else if (varray[i].x > this.vmax.x)
			{
				this.vmax.x = varray[i].x;
			}
			if (varray[i].y < this.vmin.y)
			{
				this.vmin.y = varray[i].y;
			}
			else if (varray[i].y > this.vmax.y)
			{
				this.vmax.y = varray[i].y;
			}
			if (varray[i].z < this.vmin.z)
			{
				this.vmin.z = varray[i].z;
			}
			else if (varray[i].z > this.vmax.z)
			{
				this.vmax.z = varray[i].z;
			}
		}
		this.UpdateCorners();
	}

	public final void set(Vector3f _vmin, Vector3f _vmax)
	{
		this.vmin = _vmin;
		this.vmax = _vmax;
		this.UpdateCorners();
	}

	private void UpdateCorners()
	{
		this.vcorners[0] = this.vmin;
		this.vcorners[1].x = this.vmin.x;
		this.vcorners[1].y = this.vmax.y;
		this.vcorners[1].z = this.vmin.z;
		this.vcorners[2].x = this.vmax.x;
		this.vcorners[2].y = this.vmax.y;
		this.vcorners[2].z = this.vmin.z;
		this.vcorners[3].x = this.vmax.x;
		this.vcorners[3].y = this.vmin.y;
		this.vcorners[3].z = this.vmin.z;
		this.vcorners[4] = this.vmax;
		this.vcorners[5].x = this.vmin.x;
		this.vcorners[5].y = this.vmax.y;
		this.vcorners[5].z = this.vmax.z;
		this.vcorners[6].x = this.vmin.x;
		this.vcorners[6].y = this.vmin.y;
		this.vcorners[6].z = this.vmax.z;
		this.vcorners[7].x = this.vmax.x;
		this.vcorners[7].y = this.vmin.y;
		this.vcorners[7].z = this.vmax.z;
	}

	public enum EClip
	{
		CLIP_BOTTOM(4),
		CLIP_FAR(0x20),
		CLIP_LEFT(1),
		CLIP_NEAR(0x10),
		CLIP_RIGHT(2),
		CLIP_TOP(8);

		private int intValue;
		private static java.util.HashMap<Integer, EClip> mappings;
		private static java.util.HashMap<Integer, EClip> getMappings()
		{
			if (mappings == null)
			{
				synchronized (EClip.class)
				{
					if (mappings == null)
					{
						mappings = new java.util.HashMap<Integer, EClip>();
					}
				}
			}
			return mappings;
		}

		private EClip(int value)
		{
			intValue = value;
			EClip.getMappings().put(value, this);
		}

		public int getValue()
		{
			return intValue;
		}

		public static EClip forValue(int value)
		{
			return getMappings().get(value);
		}
	}

	public enum EIntersctResult
	{
		CLIPS(8),
		CONTAINS(4),
		ISCONTAINED(2),
		ISEQUAL(1),
		OUTSIDE(0);

		private int intValue;
		private static java.util.HashMap<Integer, EIntersctResult> mappings;
		private static java.util.HashMap<Integer, EIntersctResult> getMappings()
		{
			if (mappings == null)
			{
				synchronized (EIntersctResult.class)
				{
					if (mappings == null)
					{
						mappings = new java.util.HashMap<Integer, EIntersctResult>();
					}
				}
			}
			return mappings;
		}

		private EIntersctResult(int value)
		{
			intValue = value;
			EIntersctResult.getMappings().put(value, this);
		}

		public int getValue()
		{
			return intValue;
		}

		public static EIntersctResult forValue(int value)
		{
			return getMappings().get(value);
		}
	}
}