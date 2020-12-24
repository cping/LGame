namespace loon.opengl
{
	public abstract class GLBase : LRelease
	{

		public bool begun;

		public abstract void Init();

		public virtual bool Running()
		{
			return begun;
		}

		public virtual void Begin(float fbufWidth, float fbufHeight, bool flip)
		{
			if (begun)
			{
				throw new LSysException(this.GetType().Name + " mismatched begin()");
			}
			begun = true;
		}

		public virtual void Flush()
		{

		}

		public virtual void End()
		{
			try
			{
				Flush();
			}
			catch (System.Exception ex)
			{
				LSystem.Error("GL error end()", ex);
			}
			finally
			{
				begun = false;
			}

		}

		public virtual void Close()
		{
			if (begun)
			{
				LSystem.Error(this.GetType().Name + " close() without end()");
			}
		}
	}
}
