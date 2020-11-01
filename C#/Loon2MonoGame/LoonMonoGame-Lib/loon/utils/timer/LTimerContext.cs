namespace loon.utils.timer
{
	public class LTimerContext
	{
		public long timeSinceLastUpdate;

		public long tick;

		public float alpha;

		public LTimerContext()
		{
			this.timeSinceLastUpdate = 0;
		}

		public float GetMilliseconds()
		{
			return MathUtils.Max(timeSinceLastUpdate / 1000f, LSystem.MIN_SECONE_SPEED_FIXED);
		}

		public long GetTimeSinceLastUpdate()
		{
			return timeSinceLastUpdate;
		}

		public float GetAlpha()
		{
			return alpha;
		}

		public override string ToString()
		{
			StringKeyValue builder = new StringKeyValue("LTimerContext");
			builder.Kv("timeSinceLastUpdate", timeSinceLastUpdate).Comma().Kv("tick", tick).Comma().Kv("alpha", alpha);
			return builder.ToString();
		}

	}

}
