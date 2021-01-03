using java.lang;

namespace loon.utils.parse
{
	public class StrTokenizer
	{

		private string strings;

		private string delimiters;

		private bool returnDelimiters;

		private int position;

		public StrTokenizer(string str): this(str, " \t\n\r\f", false)
		{
			
		}

		public StrTokenizer(string str, string delimiters): this(str, delimiters, false)
		{
			
		}

		public StrTokenizer(string str, string delimiters, bool returnDelimiters)
		{
			if (str == null)
			{
				throw new LSysException("string == null");
			}
			this.strings = str;
			this.delimiters = delimiters;
			this.returnDelimiters = returnDelimiters;
			this.position = 0;
		}

		public void Reset()
		{
			this.position = 0;
		}

		public int CountTokens()
		{
			int count = 0;
			bool inToken = false;
			for (int i = position, length = strings.Length; i < length; i++)
			{
				if (delimiters.IndexOf(strings.CharAt(i), 0) >= 0)
				{
					if (returnDelimiters)
					{
						count++;
					}
					if (inToken)
					{
						count++;
						inToken = false;
					}
				}
				else
				{
					inToken = true;
				}
			}
			if (inToken)
			{
				count++;
			}
			return count;
		}

		public bool HasMoreElements()
		{
			return HasMoreTokens();
		}

		public bool HasMoreTokens()
		{
			if (delimiters == null)
			{
				throw new NullPointerException("delimiters == null");
			}
			int length = strings.Length;
			if (position < length)
			{
				if (returnDelimiters)
				{
					return true;
				}
				for (int i = position; i < length; i++)
				{
					if (delimiters.IndexOf(strings.CharAt(i), 0) == -1)
					{
						return true;
					}
				}
			}
			return false;
		}

		public string NextToken()
		{
			if (delimiters == null)
			{
				throw new LSysException("delimiters == null");
			}
			int i = position;
			int length = strings.Length;

			if (i < length)
			{
				if (returnDelimiters)
				{
					if (delimiters.IndexOf(strings.CharAt(position), 0) >= 0)
						return strings.CharAt(position++).ToString();
					for (position++; position < length; position++)
						if (delimiters.IndexOf(strings.CharAt(position), 0) >= 0)
							return strings.JavaSubstring(i, position);
					return strings.JavaSubstring(i);
				}

				while (i < length && delimiters.IndexOf(strings.CharAt(i), 0) >= 0)
				{
					i++;
				}
				position = i;
				if (i < length)
				{
					for (position++; position < length; position++)
					{
						if (delimiters.IndexOf(strings.CharAt(position), 0) >= 0)
						{
							return strings.JavaSubstring(i, position);
						}
					}
					return strings.JavaSubstring(i);
				}
			}
			return null;
		}

		public string NextToken(string delims)
		{
			this.delimiters = delims;
			return NextToken();
		}

	}
}
