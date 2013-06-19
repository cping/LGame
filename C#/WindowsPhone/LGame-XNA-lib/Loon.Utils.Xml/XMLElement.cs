using Loon.Core;
using System.Collections.Generic;
using System;
using System.Text;
using Loon.Java.Collections;
using Loon.Net;
namespace Loon.Utils.Xml {
	
	public class XMLElement : LRelease {
	
		private string name;
	
		private Dictionary<string, XMLAttribute> attributes;
	
		private List<object> contents;
	
		private XMLElement parent;
	
		internal XMLElement(string name_0) {
            this.attributes = new Dictionary<string, XMLAttribute>();
            this.contents = new List<object>();
			this.name = name_0;
		}
	
		public byte[] ReadContentBinHex() {
			byte[] buffer = new byte[0x1000];
			ReadBinHex(buffer, 0, 0x1000);
			return buffer;
		}
	
		public int ReadBinHex(byte[] buffer, int offset, int length) {
			if (offset < 0) {
				throw new ArgumentException(
						"Offset must be non-negative integer.");
			} else if (length < 0) {
				throw new ArgumentException(
						"Length must be non-negative integer.");
			} else if (buffer.Length < offset + length) {
				throw new ArgumentException(
						"buffer length is smaller than the sum of offset and length.");
			}
			if (length == 0) {
				return 0;
			}
			char[] chars = new char[length * 2];
			int charsLength = ReadValueChunk(chars, 0, length * 2);
			return Base64Coder.FromBinHexString(chars, offset, charsLength, buffer);
		}
	
		private int ReadValueChunk(char[] buffer, int offset, int length) {
			StringBuilder textCache = new StringBuilder(length);
			for (IIterator e = Elements(); e.HasNext();) {
				textCache.Append(e.Next().ToString());
			}
			int min = textCache.Length;
			if (min > length) {
				min = length;
			}
			string str = textCache.ToString(0,min-0);
			System.Array.Copy((Array)(str.ToCharArray()),offset,(Array)(buffer),0,length);
			if (min < length) {
				return min + ReadValueChunk(buffer, offset + min, length - min);
			} else {
				return min;
			}
		}

        public XMLAttribute GetAttribute(string n)
        {
            if (!this.attributes.ContainsKey(n))
                throw new System.Exception("Unknown attribute name '" + n
                        + "' in element '" + this.name + "' !");
            return attributes[n];
        }

        public string GetAttribute(string n, string v)
        {
            if (!this.attributes.ContainsKey(n))
            {
                return v;
            }
            return attributes[n].GetValue();
        }

        public int GetIntAttribute(string n, int v)
        {
            if (!this.attributes.ContainsKey(n))
            {
                return v;
            }
            return attributes[n].GetIntValue();
        }

        public float GetFloatAttribute(string n, float v)
        {
            if (!this.attributes.ContainsKey(n))
            {
                return v;
            }
            return attributes[n].GetFloatValue();
        }

        public double GetDoubleAttribute(string n, double v)
        {
            if (!this.attributes.ContainsKey(n))
            {
                return v;
            }
            return attributes[n].GetDoubleValue(); ;
        }

        public bool GetBoolAttribute(string n, bool v)
        {
            if (!this.attributes.ContainsKey(n))
            {
                return v;
            }
            return attributes[n].GetBoolValue();
        }

        public IDictionary<string, XMLAttribute> GetAttributes()
        {
            return this.attributes;
        }

        public bool HasAttribute(string n)
        {
            return this.attributes.ContainsKey(n);
        }

        public IIterator Elements()
        {
            return new IteratorAdapter(this.contents.GetEnumerator());
        }

        public List<XMLElement> List()
        {
            List<XMLElement> lists = new List<XMLElement>(contents.Count);
            for (IIterator e = Elements(); e.HasNext(); )
            {
                Object o = e.Next();
                if (!(o is XMLElement))
                {
                    continue;
                }
                lists.Add((XMLElement)o);
            }
            return lists;
        }

        public XMLElement GetFirstChild()
        {
            for (IIterator e = Elements(); e.HasNext(); )
            {
                object o = e.Next();
                if (!(o is XMLElement))
                {
                    continue;
                }
                return (XMLElement)o;
            }
            return null;
        }

        public XMLElement GetChildrenByName(string n)
        {
            for (IIterator e = Elements(); e.HasNext(); )
            {
                object o = e.Next();
                if ((!(o is XMLElement))
                        || (!((XMLElement)o).GetName().Equals(n)))
                {
                    continue;
                }
                return (XMLElement)o;
            }
            return null;
        }

        public List<XMLElement> Find(string n)
        {
            List<XMLElement> v = new List<XMLElement>();
            for (IIterator e = Elements(); e.HasNext(); )
            {
                object o = e.Next();
                if ((!(o is XMLElement)))
                {
                    continue;
                }
                XMLElement ele = (XMLElement)o;
                if (!ele.Equals(ele.GetName()))
                {
                    IIterator it = ele.Elements(n);
                    for (; it.HasNext(); )
                    {
                        XMLElement child = (XMLElement)it.Next();
                        child.parent = ele;
                        CollectionUtils.Add(v, child);
                    }
                    continue;
                }
                else if (ele.Equals(ele.GetName()))
                {
                    CollectionUtils.Add(v, (XMLElement)o);
                    continue;
                }
            }
            return v;
        }

        public List<XMLElement> List(string n)
        {
            List<XMLElement> v = new List<XMLElement>();
            for (IIterator e = Elements(); e.HasNext(); )
            {
                object o = e.Next();
                if ((!(o is XMLElement))
                        || (!((XMLElement)o).GetName().Equals(n)))
                {
                    continue;
                }
                CollectionUtils.Add(v, (XMLElement)o);
            }
            return v;
        }

        public IIterator Elements(string n)
        {
            List<object> v = new List<object>();
            for (IIterator e = Elements(); e.HasNext(); )
            {
                object o = e.Next();
                if ((!(o is XMLElement))
                        || (!((XMLElement)o).GetName().Equals(n)))
                {
                    continue;
                }
                CollectionUtils.Add(v, o);
            }
            return new IteratorAdapter(v.GetEnumerator());
        }

        public void AddAllTo(List<XMLElement> list)
        {
            for (IIterator e = Elements(); e.HasNext(); )
            {
                object o = e.Next();
                if ((!(o is XMLElement))
                        || (!((XMLElement)o).GetName().Equals(name)))
                {
                    continue;
                }
                CollectionUtils.Add(list, (XMLElement)o);
            }
        }

        public string GetName()
        {
            return this.name;
        }

        public XMLElement GetParent()
        {
            return this.parent;
        }

        public string GetContents()
        {
            System.Text.StringBuilder sbr = new System.Text.StringBuilder(1024);
            for (IIterator e = Elements(); e.HasNext(); )
            {
                sbr.Append(e.Next().ToString());
            }
            return sbr.ToString();
        }

        public override string ToString()
        {
            ICollection<string> set = this.attributes.Keys;
            string str1 = "<" + this.name;
            for (IIterator it = new IteratorAdapter(set.GetEnumerator()); it.HasNext(); )
            {
                string str2 = (string)it.Next();
                str1 = str1 + " " + str2 + " = \"" + GetAttribute(str2).GetValue()
                        + "\"";
            }
            str1 = str1 + ">";
            str1 = str1 + GetContents();
            str1 = str1 + "</" + this.name + ">";
            return str1;
        }

        internal XMLAttribute AddAttribute(string n, string value_ren)
        {
            XMLAttribute attribute = new XMLAttribute(n, value_ren);
            attributes.Add(n, attribute);
            return attribute;
        }

        internal void AddContents(object o)
        {
            contents.Add(o);
        }

        public virtual void Dispose()
        {
            if (attributes != null)
            {
                attributes.Clear();
                attributes = null;
            }
            if (contents != null)
            {
                contents.Clear();
                contents = null;
            }
        }
	
	
	}
}
