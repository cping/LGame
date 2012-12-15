using System.Collections.Generic;
using Loon.Core.Geom;
using Loon.Utils;
using System;
using System.Text;
namespace Loon.Action.Sprite.Node
{

	public class DefinitionObject
	{

		private List<string> elementNames;

		public DefinitionObject parentDefinitionObject = null;

		public string fileName;

		public virtual void ChildDefinitionObject(DefinitionObject childObject, string str)
		{
		}

		public virtual void ChildDefinitionObjectDidFinishParsing(DefinitionObject childObject)
		{
		}

		public virtual void ChildDefinitionObjectDidInit(DefinitionObject childObject)
		{
		}

		public virtual void DefinitionObjectDidFinishParsing()
		{
		}

		public virtual void DefinitionObjectDidInit()
		{
			this.elementNames = new List<string>();
		}

		public virtual void DefinitionObjectDidReceiveString(string value)
		{
		}

		public DefinitionObject InitWithParentObject(DefinitionObject parentObject)
		{
			this.parentDefinitionObject = parentObject;
			return this;
		}

		public static Vector2f StrToVector2(string str)
		{
			string[] result = StringUtils.Split(str, ",");
			string name = result[0];
			string value = result[1];
			return new Vector2f(Convert.ToSingle(name), Convert.ToSingle(value));
		}

		public virtual void UndefinedElementDidFinish(string elementName)
		{
			string result = this.elementNames[this.elementNames.Count - 1];
			if (result.Equals(elementName))
			{
				this.elementNames.Remove(result);
			}
		}

		public virtual void UndefinedElementDidReceiveString(string str)
		{
		}

		public virtual void UndefinedElementDidStart(string elementName)
		{
			this.elementNames.Add(elementName);
		}

		private bool goto_flag = false;

		protected internal virtual List<string> GetResult(string v)
		{
			StringBuilder buffer = new StringBuilder();
			List<string> result = new List<string>(20);
			char[] chars = v.ToCharArray();
			int size = chars.Length;
			for (int i = 0; i < size; i++)
			{
				char ch = chars[i];
				if (ch == '/')
				{
					goto_flag = true;
				}
				else if ((ch == '\n' | ch == ';'))
				{
					string mess = buffer.ToString();
					int len = mess.Length;
					if (len > 0)
					{
						result.Add(mess);
						buffer.Remove(0, len);
					}
					goto_flag = false;
				}
				else if (!goto_flag && ch != 0x9)
				{
					buffer.Append(ch);
				}
			}
			if (buffer.Length > 0)
			{
				result.Add(buffer.ToString());
			}
			buffer = null;
			return result;
		}
	}

}