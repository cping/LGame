namespace loon.utils.timer
{
	public abstract class FloatTimerEvent
	{

		private float _delay;
		private float _acc;

		private bool _repeat;
		private bool _done;
		private bool _stopped;

		public FloatTimerEvent(float delay):this(delay, false)
		{
			
		}

		public FloatTimerEvent(float delay, bool repeat)
		{
			this._delay = delay;
			this._repeat = repeat;
			this._acc = 0f;
		}

		public void Update(LTimerContext context)
		{
			Update(context.timeSinceLastUpdate);
		}

		public void Update(long elapsedTime)
		{
			this.Update(MathUtils.Max(elapsedTime / 1000f, LSystem.MIN_SECONE_SPEED_FIXED));
		}

		public void Update(float delta)
		{
			if ((!this._done) && (!this._stopped))
			{
				this._acc += delta;
				if (this._acc >= this._delay)
				{
					this._acc -= this._delay;
					if (this._repeat)
					{
						Reset();
					}
					else
					{
						this._done = true;
					}
					Execute();
				}
			}
		}

		public FloatTimerEvent Reset()
		{
			this._stopped = false;
			this._done = false;
			this._acc = 0f;
			return this;
		}

		public bool IsCompleted()
		{
			return this._done;
		}

		public bool IsRunning()
		{
			return (!this._done) && (this._acc < this._delay) && (!this._stopped);
		}

		public FloatTimerEvent Stop()
		{
			this._stopped = true;
			return this;
		}

		public FloatTimerEvent SetDelay(int delay)
		{
			this._delay = delay;
			return this;
		}

		public abstract void Execute();

		public float GetPercentage()
		{
			return this._acc / this._delay;
		}

		public float GetRemaining()
		{
			return this._delay - this._acc;
		}

		public float GetPercentageRemaining()
		{
			if (this._done)
				return 100f;
			if (this._stopped)
			{
				return 0f;
			}
			return 1f - (this._delay - this._acc) / this._delay;
		}

		public float GetDelay()
		{
			return this._delay;
		}

		public override string ToString()
		{
			StringKeyValue builder = new StringKeyValue("FloatTimerEvent");
			builder.Kv("delay", _delay).Comma().Kv("repeat", _repeat).Comma().Kv("acc", _acc).Comma().Kv("done", _done).Comma()
					.Kv("stopped", _stopped);
			return builder.ToString();
		}
	}
}
