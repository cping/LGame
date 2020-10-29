using System;
using java.lang;

namespace loon.utils
{
    public class CharParser
{

		protected int poistion;

		protected string context;

		public void SetText(string c)
		{
			this.context = c;
		}

		public string GetText()
		{
			return this.context;
		}

		public char NextChar()
		{
			if (Eof())
			{
				return CharUtils.ToChar(-1);
			}
			return context.CharAt(poistion);
		}

		public bool StartsWith(string prefix)
		{
			return context.Substring(poistion, context.Length()).StartsWith(prefix);
		}

		public bool Eof()
		{
			return (poistion >= context.Length());
		}

		protected void ConsumeWhitespace()
		{
			while (!Eof() && CharUtils.IsWhitespace(NextChar()))
			{
				ConsumeChar();
			}
		}

		public char ConsumeChar()
		{
			if (poistion < context.Length())
			{
				char consumedChar = context.CharAt(poistion);
				poistion++;
				return consumedChar;
			}
			else
			{
				return CharUtils.ToChar(-1);
			}
		}

	}
}
