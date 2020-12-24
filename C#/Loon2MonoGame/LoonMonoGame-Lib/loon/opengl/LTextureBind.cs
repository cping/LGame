using loon.utils;

namespace loon.opengl
{
	public class LTextureBind : GLBase
	{

		public readonly GL20 gl;
		protected internal int curTexId = -1;
		protected internal int lastTexId = -1;

		public virtual int CurrentTextureID
		{
			get
			{
				return this.curTexId;
			}
		}

		public virtual LTexture Texture
		{
			set
			{
				//JAVA TO C# CONVERTER WARNING: The original Java variable was marked 'final':
				//ORIGINAL LINE: final int id = value.getID();
				int id = value.ID;
				if (!value.IsLoaded())
				{
					value.LoadTexture();
				}
				if (curTexId != -1 && curTexId != id)
				{
					Flush();
				}
				this.lastTexId = this.curTexId;
				this.curTexId = id;
			}
		}

		public override void End()
		{
			base.End();
			lastTexId = -1;
			curTexId = -1;
		}

		protected internal LTextureBind(GL20 gl)
		{
			this.gl = gl;
		}

		protected internal virtual void BindTexture()
		{
			GLUtils.BindTexture(gl, curTexId);
		}

		public override void Init()
		{

		}

	}

}
