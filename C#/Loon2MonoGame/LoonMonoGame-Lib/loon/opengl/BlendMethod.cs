namespace loon.opengl
{
	public class BlendMethod
	{

		public const int MODE_NORMAL = 1;

		public const int MODE_ALPHA_MAP = 2;

		public const int MODE_ALPHA_BLEND = 3;

		public const int MODE_COLOR_MULTIPLY = 4;

		public const int MODE_ADD = 5;

		public const int MODE_SCREEN = 6;

		public const int MODE_ALPHA = 7;

		public const int MODE_SPEED = 8;

		public const int MODE_ALPHA_ONE = 9;

		public const int MODE_NONE = 10;

		public const int MODE_MASK = 11;

		public const int MODE_LIGHT = 12;

		public const int MODE_ALPHA_ADD = 13;

		public const int MODE_MULTIPLY = 14;

		protected internal int _GL_BLEND;

		public BlendMethod(int b)
		{
			this._GL_BLEND = b;
		}

		public BlendMethod() : this(BlendMethod.MODE_NORMAL)
		{
		}

		public virtual void BlendNormal()
		{
			_GL_BLEND = BlendMethod.MODE_NORMAL;
		}

		public virtual void BlendSpeed()
		{
			_GL_BLEND = BlendMethod.MODE_SPEED;
		}

		public virtual void BlendAdd()
		{
			_GL_BLEND = BlendMethod.MODE_ALPHA_ADD;
		}

		public virtual void BlendMultiply()
		{
			_GL_BLEND = BlendMethod.MODE_MULTIPLY;
		}

		public virtual void BlendLight()
		{
			_GL_BLEND = BlendMethod.MODE_LIGHT;
		}

		public virtual void BlendMask()
		{
			_GL_BLEND = BlendMethod.MODE_MASK;
		}

		public virtual int GetBlend()
		{
				return _GL_BLEND;
		}

		public virtual BlendMethod SetBlend(int b)
		{
			this._GL_BLEND = b;
			return this;
		}

	}
}
