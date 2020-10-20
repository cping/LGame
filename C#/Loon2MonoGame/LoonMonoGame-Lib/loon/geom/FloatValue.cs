using java.lang;

namespace loon.geom
{
	public class FloatValue
	{
		private float value = 0f;

		public FloatValue() : this(0f)
		{

		}

		public FloatValue(float v)
		{
			this.Set(v);
		}

		public FloatValue Set(float v)
		{
			this.value = v;
			return this;
		}

		public float Get()
		{
			return Result();
		}

		public float Result()
		{
			return value;
		}

		public override string ToString()
		{
			return JavaSystem.Str(value);
		}
	}
}
