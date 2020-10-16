using java.lang;

namespace loon.font
{
	public class TextWrap
	{
		public readonly static TextWrap MANUAL = new TextWrap(Float.MAX_VALUE_JAVA);

		public readonly float width;

		public readonly float indent;

		public TextWrap(float width) : this(width, 0)
		{
	
		}

		public TextWrap(float width, float indent)
		{
			this.width = width;
			this.indent = indent;
		}
		
		public override int GetHashCode()
		{
			return (int)width ^ (int)indent;
		}


		public override bool Equals(object other)
		{
			if (other is TextWrap) {
				TextWrap ow = (TextWrap)other;
				return width == ow.width && indent == ow.indent;
			} else
			{
				return false;
			}
		}
	}

}
