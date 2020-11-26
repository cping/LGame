using java.lang;

namespace loon.utils
{
    public class StringKeyValue
    {
        private readonly int capacity;

        private string key;

        private string value;

        private readonly Array<string> flags;

        private StrBuilder _buffer;

        private bool _dirty;

        private bool _init_buffer;

        public StringKeyValue(string key) : this(128, key, null)
        {

        }

        public StringKeyValue(int size, string key) : this(size, key, null)
        {

        }

        public StringKeyValue(int size, string k, string val)
        {
            this.capacity = size;
            this.key = k;
            this.value = val;
            this.flags = new Array<string>();
        }

        private void InitBuild()
        {
            if (!_init_buffer && _buffer == null)
            {
                _buffer = new StrBuilder(capacity);
                _init_buffer = true;
            }
        }

        public string GetKey()
        {
            return key;
        }

        public void SetKey(string newKey)
        {
            this.key = newKey;
        }

        public int GetCapacity()
        {
            return capacity;
        }

        public StringKeyValue AddValue(bool ch)
        {
            InitBuild();
            _buffer.Append(ch);
            _dirty = true;
            return this;
        }

        public StringKeyValue AddValue(long ch)
        {
            InitBuild();
            _buffer.Append(ch);
            _dirty = true;
            return this;
        }

        public StringKeyValue AddValue(int ch)
        {
            InitBuild();
            _buffer.Append(ch);
            _dirty = true;
            return this;
        }

        public StringKeyValue AddValue(float ch)
        {
            InitBuild();
            _buffer.Append(ch);
            _dirty = true;
            return this;
        }

        public StringKeyValue AddValue(CharSequence ch)
        {
            if (ch == null)
            {
                return this;
            }
            InitBuild();
            _buffer.Append(ch);
            _dirty = true;
            return this;
        }
        public StringKeyValue AddValue(string s)
        {
            if (s == null)
            {
                return this;
            }
            InitBuild();
            _buffer.Append(s);
            _dirty = true;
            return this;
        }

        public StringKeyValue Tab()
        {
            return AddValue("	");
        }

        public StringKeyValue Space()
        {
            return AddValue(" ");
        }

        public StringKeyValue NewLine()
        {
            return AddValue(LSystem.LS);
        }

        public StringKeyValue PushBrace()
        {
            return AddValue("{");
        }

        public StringKeyValue PopBrace()
        {
            return AddValue("}");
        }

        public StringKeyValue PushParen()
        {
            return AddValue("(");
        }

        public StringKeyValue PopParen()
        {
            return AddValue(")");
        }

        public StringKeyValue PushBracket()
        {
            return AddValue("[");
        }

        public StringKeyValue PopBracket()
        {
            return AddValue("]");
        }

        public StringKeyValue Quot()
        {
            return AddValue("\"");
        }

        public StringKeyValue Comma()
        {
            return AddValue(",");
        }

        public StringKeyValue SComma()
        {
            return AddValue(" , ");
        }

        public StringKeyValue Kv(CharSequence key, object[] values)
        {
            if (key == null && values == null)
            {
                return this;
            }
            int size = values.Length;
            StrBuilder sbr = new StrBuilder(size + 32);
            sbr.Append('{');
            for (int i = 0; i < size; i++)
            {
                sbr.Append(values[i]);
                if (i < size - 1)
                {
                    sbr.Append(',');
                }
            }
            sbr.Append('}');
            return Kv(key, sbr.ToString());
        }

        public StringKeyValue Kv(CharSequence key, object value)
        {
            if (key == null && value == null)
            {
                return this;
            }
            if (key != null && value == null)
            {
                return AddValue(key).AddValue("=").AddValue(LSystem.UNKNOWN);
            }
            else if (key != null && value != null)
            {
                return AddValue(key).AddValue("=").AddValue(value.ToString());
            }
            return this;
        }

        public StringKeyValue Kv(string key, object[] values)
        {
            if (key == null && values == null)
            {
                return this;
            }
            int size = values.Length;
            StrBuilder sbr = new StrBuilder(size + 32);
            sbr.Append('{');
            for (int i = 0; i < size; i++)
            {
                sbr.Append(values[i]);
                if (i < size - 1)
                {
                    sbr.Append(',');
                }
            }
            sbr.Append('}');
            return Kv(key, sbr.ToString());
        }

        public StringKeyValue Kv(string key, object value)
        {
            if (key == null && value == null)
            {
                return this;
            }
            if (key != null && value == null)
            {
                return AddValue(key).AddValue("=").AddValue(LSystem.UNKNOWN);
            }
            else if (key != null && value != null)
            {
                return AddValue(key).AddValue("=").AddValue(value.ToString());
            }
            return this;
        }

        public StringKeyValue Text(CharSequence mes)
        {
            return AddValue(mes);
        }

        public StringKeyValue Text(string mes)
        {
            return AddValue(mes);
        }

        public string RemoveFirstTag()
        {
            return flags.RemoveFirst();
        }

        public string RemoveLastTag()
        {
            return flags.RemoveLast();
        }

        public StringKeyValue AddTag(CharSequence tag)
        {
            flags.Add(tag.ToString());
            return this;
        }

        public StringKeyValue AddTag(string tag)
        {
            flags.Add(tag);
            return this;
        }

        public StringKeyValue RemoveTag(CharSequence tag)
        {
            flags.Remove(tag.ToString());
            return this;
        }
        public StringKeyValue RemoveTag(string tag)
        {
            flags.Remove(tag);
            return this;
        }

        public StringKeyValue PushTag(CharSequence tag)
        {
            string t = tag.ToString();
            flags.Add(t);
            return AddValue("<" + t + ">");
        }
        public StringKeyValue PushTag(string tag)
        {
            flags.Add(tag);
            return AddValue("<" + tag + ">");
        }

        public StringKeyValue PopTag(CharSequence tag)
        {
            string tmp = flags.Pop();
            return AddValue("</" + ((tag == null || tag.Length() == 0 || " ".Equals(tag.ToString())) ? tmp : tag.ToString()) + ">");
        }
        public StringKeyValue PopTag(string tag)
        {
            string tmp = flags.Pop();
            return AddValue("</" + ((tag == null || tag.Length() == 0 || " ".Equals(tag)) ? tmp : tag) + ">");
        }

        public StringKeyValue PopTag()
        {
            if (flags.Size() > 0)
            {
                return AddValue("</" + flags.Pop() + ">");
            }
            return this;
        }

        public StringKeyValue PopTagAll()
        {
            for (; flags.HashNext();)
            {
                AddValue("</" + flags.Next() + ">");
            }
            flags.Clear();
            return this;
        }

        public StringKeyValue RemoveValue()
        {
            return RemoveValue(0, _buffer.Length());
        }

        public StringKeyValue RemoveValue(int start, int end)
        {
            InitBuild();
            _buffer.Delete(start, end);
            _dirty = true;
            return this;
        }

        public string GetValue()
        {
            if (_dirty && _buffer != null)
            {
                value = _buffer.ToString();
                _dirty = false;
            }
            return value;
        }

        public StringKeyValue Clear()
        {
            if (_buffer != null && _buffer.Length() > 0)
            {
                _buffer.Delete(0, _buffer.Length());
                _dirty = true;
            }
            return this;
        }

        public int Length()
        {
            return (_buffer != null && _buffer.Length() > 0) ? _buffer.Length() : 0;
        }

        public char CharAt(int i)
        {
            return (char)((_buffer != null && _buffer.Length() < i) ? _buffer.CharAt(i) : -1);
        }

        public override string ToString()
        {
            return GetKey() + " [" + GetValue() + "]";
        }
    }

}
