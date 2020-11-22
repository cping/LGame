namespace loon.geom
{
	public class StrValue
	{

		private string value;

		public StrValue(): this("")
		{
			
		}

		public StrValue(string v)
		{
			this.Set(v);
		}

		public StrValue Set(string v)
		{
			this.value = v;
			return this;
		}

		public string Get()
		{
			return Result();
		}

		public string Result()
		{
			return value;
		}

		public override string ToString()
		{
			return value;
		}

	}
}
